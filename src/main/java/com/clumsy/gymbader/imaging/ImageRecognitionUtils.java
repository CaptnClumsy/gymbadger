package com.clumsy.gymbader.imaging;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.clumsy.gymbadger.data.SimpleGymDao;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Block;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Page;
import com.google.cloud.vision.v1.Paragraph;
import com.google.cloud.vision.v1.Symbol;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.cloud.vision.v1.Vertex;
import com.google.cloud.vision.v1.Word;
import com.google.protobuf.ByteString;

public class ImageRecognitionUtils {

	public static List<ImageRecognitionResult> getGyms(final List<String> inputFiles, final List<SimpleGymDao> gymList) throws ImageProcessingException, InvalidBadgeListException { 
		// Setup the requests to send to google vision
		final List<AnnotateImageRequest> requests = new ArrayList<AnnotateImageRequest>();
		try {
			for (String inFile : inputFiles) {
				final ByteString imgBytes = ByteString.readFrom(new FileInputStream(inFile));
				final Image img = Image.newBuilder().setContent(imgBytes).build();
				final Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
				final AnnotateImageRequest request =
						AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
				requests.add(request);
			}
		} catch (IOException e) {
			throw new ImageProcessingException("Unable to read temporary file on server: "+e);
		}
		
		try {
			// Send data to google vision and get response
			final ImageAnnotatorClient client = ImageAnnotatorClient.create();
			final BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
		    final List<AnnotateImageResponse> responses = response.getResponsesList();
		    client.close();
		    // Process the response for each image
		    final List<ImageRecognitionResult> matchingGyms = new ArrayList<ImageRecognitionResult>();
		    for (AnnotateImageResponse res : responses) {
		        if (res.hasError()) {
			        System.err.println("Error: "+res.getError().getMessage());
			        continue;
			    }
		        // Walk the elements of this images page and find the blocks of text
		        boolean isImageValid = false;
		        TextAnnotation annotation = res.getFullTextAnnotation();
		        for (Page page: annotation.getPagesList()) {
		            for (Block block : page.getBlocksList()) {
		                String blockText = "";
		                for (Paragraph para : block.getParagraphsList()) {
		                    String paraText = "";
		                    for (Word word: para.getWordsList()) {
		                        String wordText = "";
		                        for (Symbol symbol: word.getSymbolsList()) {
		                            wordText = wordText + symbol.getText();
		                        }
		                        if (!wordText.equals("'") && paraText.length()!=0) {
		                        	paraText = paraText + " " + wordText;
		                        } else {
		                        	paraText = paraText + wordText;
		                        }
		                    }
		                    blockText = blockText + paraText;
		                }
		                if (blockText.equalsIgnoreCase("GYM BADGES")) {
		                	isImageValid = true;
		                }
		                final List<SimpleGymDao> matching = GymMatcher.getBestMatches(blockText, gymList);
		                if (matching!=null && matching.size()!=0) {
		                	// Create a rectangle representing the text block bounds
		                	BoundingPoly box = block.getBoundingBox();
		                	List<Vertex> vertices = box.getVerticesList();
		                	Rectangle bounds = new Rectangle(vertices.get(0).getX(), vertices.get(0).getY(),
		                			vertices.get(1).getX()-vertices.get(0).getX(),
		                			vertices.get(2).getY()-vertices.get(1).getY());
		                	// Add this gym to the list
		                	matchingGyms.add(new ImageRecognitionResult(matching, bounds));
		                }
		            }
		        }
		        if (!isImageValid && matchingGyms.isEmpty()) {
		        	throw new InvalidBadgeListException();
		        }
		    } 
		    return matchingGyms;
		 } catch (Exception e) {
			 throw new ImageProcessingException("Unable to analyze image: "+e);
		}
	}
	
}
