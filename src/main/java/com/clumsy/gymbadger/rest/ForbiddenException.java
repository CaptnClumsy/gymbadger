package com.clumsy.gymbadger.rest;

@SuppressWarnings("serial")
public class ForbiddenException extends RuntimeException {
	
	public ForbiddenException(Exception e) {
		super(e.getMessage());
	}
	
	public ForbiddenException(String message) {
		super(message);
	}

}