package com.clumsy.gymbader.imaging;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.clumsy.gymbadger.data.SimpleGymDao;

import lombok.Data;

@Data
public class ImageRecognitionResult {
	private Long gymId;
	private List<String> gymNames;
	private Rectangle bounds;
	
	public ImageRecognitionResult(final Long gymId, final List<SimpleGymDao> gymNames, final Rectangle bounds) {
		this.gymId=gymId;
		this.gymNames=new ArrayList<String>();
		for (SimpleGymDao dao: gymNames) {
			this.gymNames.add(dao.getName());
		}
		this.bounds=bounds;
	}
}
