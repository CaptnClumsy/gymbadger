package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class LeaderDao {
	private Integer rank;
	private String name;
	private Integer badges;

	public LeaderDao(final Integer rank, final String name, final Integer badges) {
		this.rank=rank;
		this.name=name;
		this.badges=badges;
	}
}
