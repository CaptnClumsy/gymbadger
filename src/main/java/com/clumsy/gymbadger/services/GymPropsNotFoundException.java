package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class GymPropsNotFoundException extends Exception {
	protected GymPropsNotFoundException(final String message) {
        super(message);
    }
}