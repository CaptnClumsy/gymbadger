package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class TeamNotFoundException extends Exception {
	protected TeamNotFoundException(final String message) {
        super(message);
    }
}

