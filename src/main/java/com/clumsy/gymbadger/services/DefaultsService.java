package com.clumsy.gymbadger.services;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clumsy.gymbadger.data.AnnouncementDao;
import com.clumsy.gymbadger.data.DefaultsDao;
import com.clumsy.gymbadger.data.UserDao;
import com.clumsy.gymbadger.entities.AnnouncementEntity;
import com.clumsy.gymbadger.entities.DefaultsEntity;
import com.clumsy.gymbadger.entities.UserAnnouncementEntity;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.AnnouncementRepo;
import com.clumsy.gymbadger.repos.DefaultsRepo;
import com.clumsy.gymbadger.repos.UserAnnouncementRepo;

@Service
public class DefaultsService {

	private static final Long DEFAULT_USERID = 0L;

	private static final Integer DEFAULT_ZOOM = 15;
	private static final Double DEFAULT_LATITUDE = 51.7519741;
	private static final Double DEFAULT_LONGITUDE = -0.3370427;

	private final DefaultsRepo defaultsRepo;
	private final UserAnnouncementRepo userAnnouncementRepo;
	private final AnnouncementRepo announcementRepo;

	@Autowired
	DefaultsService(final DefaultsRepo defaultsRepo, final UserAnnouncementRepo userAnnouncementRepo,
			final AnnouncementRepo announcementRepo) {
		this.defaultsRepo = defaultsRepo;
		this.userAnnouncementRepo = userAnnouncementRepo;
		this.announcementRepo = announcementRepo;
	}

	@Transactional(readOnly = true) 
	public DefaultsDao getDefaults(final UserEntity user) {
		DefaultsEntity defaults = defaultsRepo.findOneByUserid(user.getId());
		DefaultsDao dao = new DefaultsDao(defaults.getZoom(), defaults.getLatitude(), defaults.getLongitude());
		dao.setUser(UserDao.fromEntity(user));
		List<UserAnnouncementEntity> announcementEntities = userAnnouncementRepo.findAllByUserId(user.getId());
		if (announcementEntities!=null) {
			List<AnnouncementDao> announcements = new ArrayList<AnnouncementDao>();
			for (UserAnnouncementEntity entity : announcementEntities) {
				announcements.add(AnnouncementDao.fromEntity(entity));
			}
			dao.setAnnouncements(announcements);
		}
		
		return dao;
	}
	@Transactional
	public DefaultsEntity insertDefaults(final Long userId) {
		// If there are any system wide announcements this new user needs to see them
		List<AnnouncementEntity> announcementEntities = announcementRepo.findAllByUserId(DEFAULT_USERID);
		if (announcementEntities!=null) {
			for (AnnouncementEntity entity : announcementEntities) {
				UserAnnouncementEntity newAnnounce = new UserAnnouncementEntity();
				newAnnounce.setUserId(userId);
				newAnnounce.setAnnouncement(entity);
				userAnnouncementRepo.save(newAnnounce);
			}
		}
		// Insert the default values
		DefaultsEntity defaults = new DefaultsEntity();
		defaults.setUserid(userId);
		defaults.setZoom(DEFAULT_ZOOM);
		defaults.setLatitude(DEFAULT_LATITUDE);
		defaults.setLongitude(DEFAULT_LONGITUDE);
		return defaultsRepo.save(defaults);
	}
}
