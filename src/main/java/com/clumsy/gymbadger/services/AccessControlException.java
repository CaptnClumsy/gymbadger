package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class AccessControlException extends Exception {
	protected AccessControlException(final String message) {
        super(message);
    }
}
