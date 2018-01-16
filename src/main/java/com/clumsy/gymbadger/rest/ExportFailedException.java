package com.clumsy.gymbadger.rest;

@SuppressWarnings("serial")
public class ExportFailedException extends RuntimeException {

	public ExportFailedException(Exception e) {
		super(e.getMessage());
	}
	
	public ExportFailedException(String message) {
		super(message);
	}
}
