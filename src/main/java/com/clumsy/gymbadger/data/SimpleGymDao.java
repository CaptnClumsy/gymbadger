package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class SimpleGymDao {
	private Long id;
	private String name;
	private String shortName;
	
	public SimpleGymDao(final Long id, final String name, final String shortName) {
		this.id=id;
		this.name=name;
		this.shortName=shortName;
	}
}
