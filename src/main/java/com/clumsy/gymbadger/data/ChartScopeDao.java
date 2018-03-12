package com.clumsy.gymbadger.data;

import java.util.Date;

import lombok.Data;

@Data
public class ChartScopeDao {
	private ChartTimeInterval interval;
	private Date start;
}
