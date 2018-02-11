package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class UnknownHistoryTypeException extends Exception {
	protected UnknownHistoryTypeException(final String message) {
        super(message);
    }
}