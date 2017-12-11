package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class ErrorDao {

    private String title;
	private String message;
	
	public ErrorDao(final String title, final String message) {
		this.title=title;
		this.message=message;
	}

	
}
