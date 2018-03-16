package com.clumsy.gymbadger.data;

import java.util.List;

import lombok.Data;

@Data
public class BadgeUploadResultDao {

	private GymBadgeStatus status;
	private List<SimpleGymDao> gyms;
	
	public BadgeUploadResultDao(final GymBadgeStatus status) {
		this.status=status;
	}
}
