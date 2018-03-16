package com.clumsy.gymbadger.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbader.imaging.ImageProcessingException;
import com.clumsy.gymbader.imaging.ImageRecognitionResult;
import com.clumsy.gymbader.imaging.ImageRecognitionUtils;
import com.clumsy.gymbader.imaging.ImageUtils;
import com.clumsy.gymbader.imaging.InvalidBadgeListException;
import com.clumsy.gymbadger.data.BadgeUploadResultDao;
import com.clumsy.gymbadger.data.GymBadgeStatus;
import com.clumsy.gymbadger.data.SimpleGymDao;
import com.clumsy.gymbadger.data.UploadDao;
import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.repos.GymRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UploadService {

	@Autowired
	private WorkspaceService workspaceService;

	@Autowired
	private GymRepo gymRepo;
	
	public void add(UploadDao dao, String originalFilename, InputStream inputStream) throws WorkspaceException {
		final Path filePath = Paths.get(dao.getDirectory(), originalFilename);
		try {
			java.nio.file.Files.copy(
			    inputStream, 
				filePath, 
				StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			log.error("Failed to copy file to temp area: " + e.getMessage());
			throw new WorkspaceException("Failed to copy screenshot to server temporary folder");
		} finally {	 
	        IOUtils.closeQuietly(inputStream);
		}
		dao.getFiles().add(filePath.toString());
	}

	public UploadDao begin(Long id) throws WorkspaceException {
		UploadDao dao = new UploadDao();
		dao.setUserId(id);
		dao.setDirectory(workspaceService.getWorkspace(id));
		dao.setFiles(new ArrayList<String>());
		return dao;
	}

	@Transactional(readOnly = true)
	public List<BadgeUploadResultDao> process(UploadDao dao) throws WorkspaceException {
		if (dao==null || dao.getFiles()==null) {
			throw new IllegalArgumentException("Upload DAO is empty");
		}
		// Prepare images for processing
		final List<String> tmpFiles = new ArrayList<String>();
		for (int i=0; i<dao.getFiles().size(); i++) {
			final String filePath = dao.getFiles().get(i);
			try {
				Path tmpFile = Files.createTempFile(Paths.get(dao.getDirectory()), null, "png");
				ImageUtils.convertForOCR(filePath, tmpFile.toString());
				tmpFiles.add(tmpFile.toString());
			} catch (ImageProcessingException e) {
				throw new WorkspaceException("Error converting gym badge screenshot for image processing: "+e);
			} catch (IOException e) {
				throw new WorkspaceException("Unable to create temporary file for image processing: "+e);
			}
		}
		// Query all gym short names
		final List<GymEntity> gyms = gymRepo.findAllGyms();
		final List<SimpleGymDao> gymShortNames = new ArrayList<SimpleGymDao>();
		for (GymEntity gym : gyms) {
			if (gym.getShortName()!=null && gym.getShortName().length()!=0) {
				gymShortNames.add(new SimpleGymDao(gym.getId(), gym.getShortName()));
			} else {
				gymShortNames.add(new SimpleGymDao(gym.getId(), gym.getName()));
			}
		}
		// Analyse the images and display results
		final List<BadgeUploadResultDao> results = new ArrayList<BadgeUploadResultDao>();
		try {		
			List<ImageRecognitionResult> matchingGyms = ImageRecognitionUtils.getGyms(tmpFiles, gymShortNames);
			//ImageUtils.drawBounds(inFile, outFile, matchingGyms);
			for (ImageRecognitionResult result : matchingGyms) {
				log.debug("FOUND GYM AT: "+result.getBounds().toString());
				if (result.getGymNames()!=null && result.getGymNames().size()>0) {
					// Need to find the badge colour from the screenshot somehow
					final BadgeUploadResultDao badgeResult = new BadgeUploadResultDao(GymBadgeStatus.NONE);
					final List<SimpleGymDao> gymDaos = new ArrayList<SimpleGymDao>();
					for (int i=0; i<result.getGymNames().size(); i++) {
						String gym = result.getGymNames().get(i);
						gymDaos.add(new SimpleGymDao(result.gym));
						if (i==0) {
							log.debug("* " + gym);
						} else {
							log.debug("  " + gym);
						}
					}
					badgeResult.setGyms(gymDaos);
					results.add(badgeResult);
				}
			}
		} catch (ImageProcessingException e) {
		    throw new WorkspaceException("Unable to perform image recognition: "+e);
		} catch (InvalidBadgeListException e) {
			throw new WorkspaceException("Invalid badge list: "+e);
		}
		return results;
	}

	public void end(final Long userId) {
		workspaceService.release(userId);
	}
}
