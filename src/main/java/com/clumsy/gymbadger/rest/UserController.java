package com.clumsy.gymbadger.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.UserDao;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
 
@RestController
@RequestMapping("/api/users")
public class UserController {
 
	@Autowired
	private UserRepo userRepo;
	
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public UserDao getUser(@PathVariable("id") Long id) {
        final UserEntity user = userRepo.findOne(id);
        if (user == null) {
        	throw new ObjectNotFoundException();
        }
        return new UserDao(user.getId(), user.getName());
    }
}