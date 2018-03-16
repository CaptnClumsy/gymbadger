package com.clumsy.gymbader.imaging;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageUtils {

	private static BufferedImage convertToGreyScale(final String inFile) throws ImageProcessingException {
	    // Load the original image
		BufferedImage originalImg = null;
		try {
			originalImg = ImageIO.read(new File(inFile));
		} catch (IOException e) {
			throw new ImageProcessingException("Failed to read image file: "+e.getMessage());
		}
		// Convert image to greyscale?
		BufferedImage image = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(),  
			BufferedImage.TYPE_BYTE_GRAY);  
		Graphics2D g = image.createGraphics();  
		g.drawImage(originalImg, 0, 0, null);
		// draw dividing lines to help OCR
		g.setStroke(new BasicStroke(2f));
        g.setColor(Color.BLACK);
        g.draw(new Line2D.Double(originalImg.getWidth()/3, 0, originalImg.getWidth()/3, originalImg.getHeight()));
        g.draw(new Line2D.Double((originalImg.getWidth()/3)*2, 0, (originalImg.getWidth()/3)*2, originalImg.getHeight()));
		g.dispose();
		return image;
	}
	
	public static void convertForOCR(final String inFile, final String outFile) throws ImageProcessingException {
		BufferedImage image = convertToGreyScale(inFile);
		try {
		    File outputfile = new File(outFile);
		    ImageIO.write(image, "png", outputfile);
		} catch (IOException ex) {
			throw new ImageProcessingException("Failed to write image file: "+ex.getMessage());
	    }
	}
	
	public static void drawBounds(final String inFile, final String outFile, List<ImageRecognitionResult> res) throws ImageProcessingException {
	    // Load the original image
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(inFile));
		} catch (IOException e) {
			throw new ImageProcessingException("Failed to read image file: "+e.getMessage());
		}
		Graphics2D g = image.createGraphics();  
		g.drawImage(image, 0, 0, null);
		g.setStroke(new BasicStroke(2f));
        g.setColor(Color.BLACK);
		// draw the bounds
		for (ImageRecognitionResult r : res) {
			if (r.getBounds()==null) {
				continue;
			}
			g.drawRect(r.getBounds().x, r.getBounds().y, r.getBounds().width, r.getBounds().height);
		}
		g.dispose();
		try {
		    File outputfile = new File(outFile);
		    ImageIO.write(image, "png", outputfile);
		} catch (IOException ex) {
			throw new ImageProcessingException("Failed to write image file: "+ex.getMessage());
	    }
	}
		    
}
