package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class GymHistoryNotFoundException extends Exception {
	protected GymHistoryNotFoundException(final String message) {
        super(message);
    }
}

