package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class CommentNotFoundException extends Exception {
	protected CommentNotFoundException(final String message) {
        super(message);
    }
}
