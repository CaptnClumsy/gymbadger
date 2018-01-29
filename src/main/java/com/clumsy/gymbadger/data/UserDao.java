package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class UserDao {
	
	private Long id;
	private String name;
	private String displayName;
	private boolean admin;
	
	public UserDao(final Long id, final String name, final String displayName, final boolean admin) {
		this.id=id;
		this.name=name;
		this.displayName=displayName;
		this.admin=admin;
	}

}
