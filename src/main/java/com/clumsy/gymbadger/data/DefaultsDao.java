package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class DefaultsDao {

	private Integer zoom;
	private Double lat;
	private Double lng;
	
	public DefaultsDao(Integer zoom, Double lat, Double lng) {
		this.zoom = zoom;
		this.lat = lat;
		this.lng = lng;
	}

}
