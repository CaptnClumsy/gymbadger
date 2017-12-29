package com.clumsy.gymbadger.app;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.clumsy.gymbadger.data.ErrorDao;
import com.clumsy.gymbadger.rest.ForbiddenException;
import com.clumsy.gymbadger.rest.NotLoggedInException;
import com.clumsy.gymbadger.rest.ObjectNotFoundException;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { ObjectNotFoundException.class })
    protected ResponseEntity<Object> handleNotExists(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorDao("Error", ex.getMessage()), 
          new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
    @ExceptionHandler(value = { NotLoggedInException.class })
    protected ResponseEntity<Object> handleNotLoggedIn(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorDao("Error", "Not logged in"), 
          new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
    
    @ExceptionHandler(value = { ForbiddenException.class })
    protected ResponseEntity<Object> handleForbidden(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorDao("Error", "Forbidden"), 
          new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
}
