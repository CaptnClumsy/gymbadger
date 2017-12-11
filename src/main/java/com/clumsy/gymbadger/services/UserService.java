package com.clumsy.gymbadger.services;

import java.security.Principal;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.UserRepo;

@Service
public class UserService {

	private final UserRepo userRepo;
	private DefaultsService defaultsService;
	
	@Autowired
	UserService(final UserRepo userRepo, final DefaultsService defaultsService) {
		this.userRepo = userRepo;
		this.defaultsService = defaultsService;
	}
	
	@Transactional(readOnly = true)
	public UserEntity getDefaultAccount() throws UserNotFoundException {
		UserEntity user = userRepo.findOne(0L);
		if (user == null) {
			throw new UserNotFoundException("Default account does not exist");
		}
		return user;
	}

	@Transactional
	public UserEntity getCurrentUser(final Principal principal) throws UserNotFoundException {
		if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
			return getDefaultAccount();
		}
		UserEntity user = userRepo.findOneByName(principal.getName());
		if (user == null) {
			// Automatically register new users
			UserEntity newUser = new UserEntity();
			newUser.setName(principal.getName());
			newUser.setAdmin(false);
			if (principal instanceof OAuth2Authentication) {
	        	OAuth2Authentication auth = (OAuth2Authentication)principal;
	        	@SuppressWarnings("unchecked")
				LinkedHashMap<String,String> details = (LinkedHashMap<String, String>) auth.getUserAuthentication().getDetails();
	        	newUser.setDisplayName(details.get("name"));
	        } else {
	        	newUser.setDisplayName("none");
	        }
			UserEntity savedUser = userRepo.save(newUser);
			defaultsService.insertDefaults(savedUser.getId());
			return savedUser;
		}
		return user;
	}
}
