package com.clumsy.gymbadger.services;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clumsy.gymbadger.entities.DefaultsEntity;
import com.clumsy.gymbadger.repos.DefaultsRepo;

@Service
public class DefaultsService {

	private static final Integer DEFAULT_ZOOM = 15;
	private static final Double DEFAULT_LATITUDE = 51.7519741;
	private static final Double DEFAULT_LONGITUDE = -0.3370427;

	private final DefaultsRepo defaultsRepo;

	@Autowired
	DefaultsService(final DefaultsRepo defaultsRepo) {
		this.defaultsRepo = defaultsRepo;
	}

	@Transactional(readOnly = true) 
	public DefaultsEntity getDefaults(Long userId) {
		return defaultsRepo.findOneByUserid(userId);
		
	}
	@Transactional
	public DefaultsEntity insertDefaults(final Long userId) {
		DefaultsEntity defaults = new DefaultsEntity();
		defaults.setUserid(userId);
		defaults.setZoom(DEFAULT_ZOOM);
		defaults.setLatitude(DEFAULT_LATITUDE);
		defaults.setLongitude(DEFAULT_LONGITUDE);
		return defaultsRepo.save(defaults);
	}
}
