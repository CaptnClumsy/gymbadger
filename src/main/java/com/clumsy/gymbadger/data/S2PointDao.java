package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class S2PointDao {

	private double lat;
	private double lng;

	public S2PointDao(double lat, double lng) {
		this.lat=lat;
		this.lng = lng;
	}
	
}
