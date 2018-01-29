package com.clumsy.gymbadger.services;

@SuppressWarnings("serial")
public class PokemonNotFoundException extends Exception {
	protected PokemonNotFoundException(final String message) {
        super(message);
    }
}
