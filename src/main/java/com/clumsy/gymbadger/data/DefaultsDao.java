package com.clumsy.gymbadger.data;

import java.util.List;

import lombok.Data;

@Data
public class DefaultsDao {

	private UserDao user;
	private Integer zoom;
	private Double lat;
	private Double lng;
	private Boolean cluster;
	
	private List<AnnouncementDao> announcements;

	public DefaultsDao() {
	}

	public DefaultsDao(Integer zoom, Double lat, Double lng, Boolean cluster) {
		this.zoom = zoom;
		this.lat = lat;
		this.lng = lng;
		this.cluster = cluster;
	}

}
