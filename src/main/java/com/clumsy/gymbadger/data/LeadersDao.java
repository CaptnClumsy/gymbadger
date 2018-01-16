package com.clumsy.gymbadger.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LeadersDao {
	private Boolean share;
	private List<LeaderDao> leaders;
	
	public void add(final LeaderDao leader) {
		if (leaders==null) {
			leaders = new ArrayList<LeaderDao>();
		}
		leaders.add(leader);
	}
}
