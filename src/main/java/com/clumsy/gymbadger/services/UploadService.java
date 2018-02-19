package com.clumsy.gymbadger.services;

import java.io.FileInputStream;
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
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Block;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Page;
import com.google.cloud.vision.v1.Paragraph;
import com.google.cloud.vision.v1.Symbol;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.cloud.vision.v1.Word;
import com.google.protobuf.ByteString;

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
		dao.getFiles().add(filePath.toString());
	}

	public UploadDao begin(Long id) throws WorkspaceException {
		UploadDao dao = new UploadDao();
		dao.setUserId(id);
		dao.setGyms(new ArrayList<BadgeUploadGymDao>());
		dao.setDirectory(workspaceService.getWorkspace(id));
		dao.setFiles(new ArrayList<String>());
		return dao;
	}

	public List<BadgeUploadGymDao> process(UploadDao dao) throws WorkspaceException {
		if (dao==null || dao.getFiles()==null) {
			throw new IllegalArgumentException("Upload DAO is empty");
		}
		final List<AnnotateImageRequest> requests = new ArrayList<>();
		try {
			for (int i=0; i<dao.getFiles().size(); i++) {
				final String filePath = dao.getFiles().get(i);
				final ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
				final Image img = Image.newBuilder().setContent(imgBytes).build();
				final Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
				final AnnotateImageRequest request =
				    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
				requests.add(request);
			}
		} catch (IOException e) {
			throw new WorkspaceException("Unable to read temporary file on server: "+e);
		}
		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
		    final BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
		    final List<AnnotateImageResponse> responses = response.getResponsesList();
		    client.close();
		    for (AnnotateImageResponse res : responses) {
		        if (res.hasError()) {
			        log.error("Error: "+res.getError().getMessage());
			        continue;
			    }
		        TextAnnotation annotation = res.getFullTextAnnotation();
		        for (Page page: annotation.getPagesList()) {
		            String pageText = "";
		            for (Block block : page.getBlocksList()) {
		                String blockText = "";
		                for (Paragraph para : block.getParagraphsList()) {
		                    String paraText = "";
		                    for (Word word: para.getWordsList()) {
		                        String wordText = "";
		                        for (Symbol symbol: word.getSymbolsList()) {
		                            wordText = wordText + symbol.getText();
		                        }
		                        paraText = paraText + " " + wordText;
		                    }
		                    // Output Example using Paragraph:
		                    log.warn("Paragraph: " + paraText);
		                    //log.warn("Bounds: " + para.getBoundingBox() + "\n");
		                    blockText = blockText + paraText;
		                }
		                pageText = pageText + blockText;
		            }
		        }
		        //log.warn(annotation.getText());
		    }
		 } catch (IOException e) {
			 throw new WorkspaceException("Unable to process temporary file on server: "+e);
		 } catch (Exception e) {
			 throw new WorkspaceException("Unable to analyze image: "+e);
		}
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
