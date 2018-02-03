package com.clumsy.gymbadger.data;

import java.util.List;

import lombok.Data;

@Data
public class UploadDao {
	private Long userId;
	private String directory;
	private List<BadgeUploadGymDao> gyms;
}
