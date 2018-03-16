package com.clumsy.gymbader.imaging;

import java.awt.Rectangle;
import java.util.List;

import lombok.Data;

@Data
public class ImageRecognitionResult {
	private Long gymId;
	private List<String> gymNames;
	private Rectangle bounds;
	
	public ImageRecognitionResult(final Long gymId, final List<String> gymNames, final Rectangle bounds) {
		this.gymId=gymId;
		this.gymNames=gymNames;
		this.bounds=bounds;
	}
}
