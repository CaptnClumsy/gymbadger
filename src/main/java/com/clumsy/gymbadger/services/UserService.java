package com.clumsy.gymbadger.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.AnnouncementDao;
import com.clumsy.gymbadger.data.AnnouncementType;
import com.clumsy.gymbadger.data.LeaderDao;
import com.clumsy.gymbadger.data.LeadersDao;
import com.clumsy.gymbadger.data.UserDao;
import com.clumsy.gymbadger.entities.AnnouncementEntity;
import com.clumsy.gymbadger.entities.LeaderEntity;
import com.clumsy.gymbadger.entities.UserAnnouncementEntity;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.AnnouncementRepo;
import com.clumsy.gymbadger.repos.UserAnnouncementRepo;
import com.clumsy.gymbadger.repos.UserRepo;

@Service
public class UserService {

	private static final Long DEFAULT_USERID = 0L;

	private final UserRepo userRepo;
	private final AnnouncementRepo announcementRepo;
	private final UserAnnouncementRepo userAnnouncementRepo;
	private DefaultsService defaultsService;
	
	@Autowired
	UserService(final UserRepo userRepo, final AnnouncementRepo announcementRepo, 
			final UserAnnouncementRepo userAnnouncementRepo, final DefaultsService defaultsService) {
		this.userRepo = userRepo;
		this.announcementRepo = announcementRepo;
		this.userAnnouncementRepo = userAnnouncementRepo;
		this.defaultsService = defaultsService;
	}
	
	@Transactional(readOnly = true)
	public UserEntity getDefaultAccount() throws UserNotFoundException {
		UserEntity user = userRepo.findOne(DEFAULT_USERID);
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
			newUser.setShareData(false);
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
	public UserDao setLeaderboard(final UserEntity user, final Boolean share) {
		if (user.getShareData()==share) {
			return new UserDao(user.getId(), user.getName(), user.getDisplayName(), user.getAdmin());
		}
		user.setShareData(share);
		UserEntity savedUser = userRepo.save(user);
		return new UserDao(savedUser.getId(), savedUser.getName(), savedUser.getDisplayName(), user.getAdmin());
	}

	@Transactional(readOnly = true)
	public List<AnnouncementDao> getAnnouncements(final UserEntity user) {
		List<AnnouncementDao> list = new ArrayList<AnnouncementDao>();
		List<UserAnnouncementEntity> announcementEntities = userAnnouncementRepo.findAllByUserId(user.getId());
		for (UserAnnouncementEntity entity : announcementEntities) {
			AnnouncementDao dao = AnnouncementDao.fromEntity(entity);
			list.add(dao);
		}
		return list;
	}
	
	@Transactional
	public AnnouncementDao postAnnouncement(final UserEntity user, final AnnouncementType type, 
			final String message, final boolean everyone) throws AccessControlException, UserNotFoundException {
		if (!user.getAdmin()) {
			throw new AccessControlException("Only admin users can make announcements");
		}
		AnnouncementEntity announcement = new AnnouncementEntity();
	    announcement.setMessage(message);
	    announcement.setType(type);
	    if (everyone) {
	    	announcement.setUser(getDefaultAccount());
	    } else {
	        announcement.setUser(user);
	    }
	    AnnouncementEntity newAnnouncement = announcementRepo.save(announcement);
		if (!everyone) {
			UserAnnouncementEntity userAnnounce = new UserAnnouncementEntity();
			userAnnounce.setAnnouncement(newAnnouncement);
			userAnnounce.setUserId(user.getId());
			UserAnnouncementEntity savedUserAnnounce = userAnnouncementRepo.save(userAnnounce);
			return AnnouncementDao.fromEntity(savedUserAnnounce);
		}
		List<UserEntity> allUsers = userRepo.findAll();
		UserAnnouncementEntity defaultUserAnnouncement = null;
		for (UserEntity thisUser : allUsers) {
			UserAnnouncementEntity userAnnounce = new UserAnnouncementEntity();
			userAnnounce.setAnnouncement(newAnnouncement);
			userAnnounce.setUserId(thisUser.getId());
			UserAnnouncementEntity savedUserAnnounce = userAnnouncementRepo.save(userAnnounce);
			if (thisUser.getId()==DEFAULT_USERID) {
				defaultUserAnnouncement = savedUserAnnounce;
			}
		}
		if (defaultUserAnnouncement==null) {
			throw new UserNotFoundException("Default user account not found.");
		}
		return AnnouncementDao.fromEntity(defaultUserAnnouncement);
	}
	
	@Transactional
	public void deleteUserAnnouncement(final UserEntity user) throws AnnouncementNotFoundException {
		List<UserAnnouncementEntity> userAnnounce = userAnnouncementRepo.findAllByUserId(user.getId());
		if (userAnnounce==null) {
			throw new AnnouncementNotFoundException("Announcement not found.");
		}
		for (UserAnnouncementEntity entity : userAnnounce) {
		    userAnnouncementRepo.delete(entity.getId());
		}
	}
	
	@Transactional
	public void deleteAnnouncement(final UserEntity user, final Long id) throws AccessControlException, AnnouncementNotFoundException {
		if (!user.getAdmin()) {
			throw new AccessControlException("Only admin users can delete announcements");
		}
		AnnouncementEntity announcement = announcementRepo.findOne(id);
		if (id==null) {
			throw new AnnouncementNotFoundException("Announcement "+id+" not found.");
		}
		List<UserAnnouncementEntity> userAnnouncements = userAnnouncementRepo.findAllByAnnouncementId(id);
		userAnnouncementRepo.deleteInBatch(userAnnouncements);
		announcementRepo.delete(announcement);
	}
}
