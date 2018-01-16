package com.clumsy.gymbadger.services;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.LeaderDao;
import com.clumsy.gymbadger.data.LeadersDao;
import com.clumsy.gymbadger.data.UserDao;
import com.clumsy.gymbadger.entities.LeaderEntity;
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

	@Transactional(readOnly = true)
	public LeadersDao getLeaderboard(final UserEntity user) {
		List<LeaderEntity> leaderEntities = userRepo.findLeaders();
		LeadersDao leaders = new LeadersDao();
		leaders.setShare(user.getShareData());
		int rank = 1;
		for (LeaderEntity leaderEntity : leaderEntities) {
			LeaderDao leader = new LeaderDao(rank++, leaderEntity.getdisplayName(), leaderEntity.getBadges());
			leaders.add(leader);
		}
		return leaders;
	}

	@Transactional
	public UserDao setLeaderboard(UserEntity user, Boolean share) {
		if (user.getShareData()==share) {
			return new UserDao(user.getId(), user.getName(), user.getDisplayName());
		}
		user.setShareData(share);
		UserEntity savedUser = userRepo.save(user);
		return new UserDao(savedUser.getId(), savedUser.getName(), savedUser.getDisplayName());
	}
}
