package com.clumsy.gymbadger.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.DefaultsDao;
import com.clumsy.gymbadger.entities.DefaultsEntity;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.DefaultsRepo;
import com.clumsy.gymbadger.services.UserNotFoundException;
import com.clumsy.gymbadger.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
 
@RestController
@RequestMapping("/api/defaults")
public class DefaultsController {
 
	@Autowired
	private DefaultsRepo defaultsRepo;
	
	@Autowired
	private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DefaultsDao getDefaults() {
		try {
			final UserEntity user = userService.getCurrentUser();
			final DefaultsEntity defaults = defaultsRepo.findOne(user.getId());
			return new DefaultsDao(defaults.getZoom(), defaults.getLatitude(), defaults.getLongitude());
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException();
		}
    }
}