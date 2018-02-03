package com.clumsy.gymbadger.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clumsy.gymbadger.data.BadgeUploadGymDao;
import com.clumsy.gymbadger.data.GymBadgeStatus;
import com.clumsy.gymbadger.data.UploadDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UploadService {

	@Autowired
	private WorkspaceService workspaceService;

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
	}

	public UploadDao begin(Long id) throws WorkspaceException {
		UploadDao dao = new UploadDao();
		dao.setUserId(id);
		dao.setGyms(new ArrayList<BadgeUploadGymDao>());
		dao.setDirectory(workspaceService.getWorkspace(id));
		return dao;
	}

	public List<BadgeUploadGymDao> process(UploadDao dao) {
		// TODO: Return some fake data for now 
		dao.getGyms().add(new BadgeUploadGymDao(1L, "The Mermaid", GymBadgeStatus.GOLD));
		dao.getGyms().add(new BadgeUploadGymDao(2L, "Bleak House", GymBadgeStatus.GOLD));
		dao.getGyms().add(new BadgeUploadGymDao(3L, "The Castle Inn", GymBadgeStatus.BRONZE));
		dao.getGyms().add(new BadgeUploadGymDao(4L, "Abbey Theatre", GymBadgeStatus.BASIC));
		return dao.getGyms();
	}
	
	public void end(final Long userId) {
		workspaceService.release(userId);
	}
}
