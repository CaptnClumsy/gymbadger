package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class SimpleGymDao {
	private Long id;
	private String name;
	
	public SimpleGymDao(final Long id, final String name) {
		this.id=id;
		this.name=name;
	}
}
