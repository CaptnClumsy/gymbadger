package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class ExportException extends Exception {

	protected ExportException(final String message) {
        super(message);
    }
	
	protected ExportException(Exception e) {
		super(e.getMessage());
	}
}