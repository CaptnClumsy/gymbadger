package com.clumsy.gymbadger.data;

import com.clumsy.gymbadger.entities.UserEntity;

import lombok.Data;

@Data
public class UserDao {
	
	private Long id;
	private String name;
	private String displayName;
	private boolean admin;
	private Team team;

	public UserDao() {
	}
	
	public UserDao(final Long id, final String name, final String displayName,
		final boolean admin, final Team team) {
		this.id=id;
		this.name=name;
		this.displayName=displayName;
		this.admin=admin;
		this.team=team;
	}

	public static UserDao fromEntity(UserEntity user) {
		return new UserDao(user.getId(), user.getName(), user.getDisplayName(),
			user.getAdmin(), user.getTeam());
	}

}
