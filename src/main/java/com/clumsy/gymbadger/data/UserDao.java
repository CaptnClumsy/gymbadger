package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class UserDao {
	
	private Long id;
	private String name;
	private String displayName;
	
	public UserDao(final Long id, final String name, final String displayName) {
		this.id=id;
		this.name=name;
		this.displayName=displayName;
	}

}
