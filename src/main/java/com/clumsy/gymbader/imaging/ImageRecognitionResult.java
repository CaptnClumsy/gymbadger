package com.clumsy.gymbader.imaging;

import java.awt.Rectangle;
import java.util.List;

import com.clumsy.gymbadger.data.SimpleGymDao;

import lombok.Data;

@Data
public class ImageRecognitionResult {
	private List<SimpleGymDao> gyms;
	private Rectangle bounds;
	
	public ImageRecognitionResult(final List<SimpleGymDao> gyms, final Rectangle bounds) {
		this.gyms=gyms;
		this.bounds=bounds;
	}
}
