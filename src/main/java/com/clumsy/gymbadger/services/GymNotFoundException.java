package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class GymNotFoundException extends Exception {
	protected GymNotFoundException(final String message) {
        super(message);
    }
}
