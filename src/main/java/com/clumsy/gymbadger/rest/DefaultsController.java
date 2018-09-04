package com.clumsy.gymbadger.rest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.DefaultsDao;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.services.DefaultsService;
import com.clumsy.gymbadger.services.UserNotFoundException;
import com.clumsy.gymbadger.services.UserService;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
 
@RestController
@RequestMapping("/api/defaults")
public class DefaultsController {
 
	@Autowired
	private DefaultsService defaultsService;
	
	@Autowired
	private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DefaultsDao getDefaults(Principal principal) {
		try {
			final UserEntity user = userService.getCurrentUser(principal);
			return defaultsService.getDefaults(user);
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public DefaultsDao updateDefaults(Principal principal, @RequestBody DefaultsDao updatedDefaults) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
    	try {
    		final UserEntity user = userService.getCurrentUser(principal);
    		if (!updatedDefaults.getUser().getId().equals(user.getId())) {
    			throw new ForbiddenException("You can only update your own default settings");
    		}
    		final DefaultsDao savedDefaults = defaultsService.updateDefaults(user, updatedDefaults);
    		return savedDefaults;
    	} catch (UserNotFoundException e) {
    		throw new ObjectNotFoundException("Current user not found");
    	}
    }
}