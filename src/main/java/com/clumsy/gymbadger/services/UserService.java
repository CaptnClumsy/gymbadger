package com.clumsy.gymbadger.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.UserRepo;

@Service
public class UserService {

	private final UserRepo userRepo;
	
	@Autowired
	UserService(final UserRepo userRepo) {
		this.userRepo = userRepo;
	}
	
	@Transactional(readOnly = true)
	public UserEntity getDefaultAccount() throws UserNotFoundException {
		UserEntity user = userRepo.findOne(0L);
		if (user == null) {
			throw new UserNotFoundException("Default account does not exist");
		}
		return user;
	}

	public UserEntity getCurrentUser() throws UserNotFoundException {
		// TODO: Add some security and get current user from the session
		return getDefaultAccount();
	}
}
