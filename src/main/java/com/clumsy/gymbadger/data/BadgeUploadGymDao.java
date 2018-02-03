package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class BadgeUploadGymDao {
	private Long id;
	private String name;
	private GymBadgeStatus status;
	
	public BadgeUploadGymDao(final Long id, final String name, final GymBadgeStatus status) {
		this.id=id;
		this.name=name;
		this.status=status;
	}
}
