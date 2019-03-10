package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class RegionNotFoundException extends Exception {
	protected RegionNotFoundException(final String message) {
        super(message);
    }
}
