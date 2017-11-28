package com.clumsy.gymbadger.data;

import com.clumsy.gymbadger.entities.AreaEntity;
import com.clumsy.gymbadger.entities.GymBadgeStatus;
import com.clumsy.gymbadger.entities.GymEntity;

import lombok.Data;

@Data
public class GymSummaryDao {

	private Long id;
	private String name;

	private Double lat;
	private Double lng;

	private GymBadgeStatus status;
	
	private Boolean park;
	
	private AreaEntity area;

	public GymSummaryDao() {
	}

	public GymSummaryDao(final Long id, final String name, final Double lat, final Double lng, final Boolean park, final AreaEntity area) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.park = park;
		this.area = area;
		this.status = GymBadgeStatus.NONE;
	}

	public static GymSummaryDao fromGymEntity(final GymEntity gym) {
		return new GymSummaryDao(gym.getId(), gym.getName(), gym.getLatitude(), gym.getLongitude(), gym.getPark(), gym.getArea());
	}
}
