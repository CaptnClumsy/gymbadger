package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class UserNotFoundException extends Exception {
	protected UserNotFoundException(final String message) {
        super(message);
    }
}
