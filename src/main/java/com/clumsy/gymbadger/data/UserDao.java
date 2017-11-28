package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class UserDao {
	
	private Long id;
	private String name;
	
	public UserDao(final Long id, final String name) {
		this.id=id;
		this.name=name;
	}

}
