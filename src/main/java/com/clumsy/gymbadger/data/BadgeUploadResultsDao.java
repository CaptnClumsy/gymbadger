package com.clumsy.gymbadger.data;

import java.util.List;

import lombok.Data;

@Data
public class BadgeUploadResultsDao {
	private List<BadgeUploadGymDao> gyms;
}
