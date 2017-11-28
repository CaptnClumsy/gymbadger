package com.clumsy.gymbadger.rest;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such object")
public class ObjectNotFoundException extends RuntimeException {

}
