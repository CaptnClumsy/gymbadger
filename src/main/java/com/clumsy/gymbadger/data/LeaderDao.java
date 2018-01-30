package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class LeaderDao implements Comparable<LeaderDao> {
	private Integer rank;
	private String name;
	private Integer badges;

	public LeaderDao(final Integer rank, final String name, final Integer badges) {
		this.rank=rank;
		this.name=name;
		this.badges=badges;
	}

	@Override
	public int compareTo(LeaderDao o) {
	    if (o.getBadges() > badges) {
	        return +1;
		} else if (o.getBadges() < badges) {
		    return -1;
		} 
		return 0;
	}
}
