package com.clumsy.gymbadger.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.GymSummaryDao;
import com.clumsy.gymbadger.entities.GymBadgeStatus;
import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.entities.GymPropsEntity;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.GymPropsRepo;
import com.clumsy.gymbadger.repos.GymRepo;
import com.google.common.collect.Lists;


@Service
public class GymService {

	private final GymRepo gymRepo;
	private final GymPropsRepo gymPropsRepo;
	
	@Autowired
	GymService(final GymRepo gymRepo, final GymPropsRepo gymPropsRepo) {
		this.gymRepo = gymRepo;
		this.gymPropsRepo = gymPropsRepo;
	}
	
	@Transactional(readOnly = true)
	public List<GymEntity> getAllGyms(final Long userId) throws GymNotFoundException {
		final List<GymEntity> gyms = gymRepo.findAll();
		if (gyms == null) {
			throw new GymNotFoundException("Unable to query gym list");
		}
		return gyms;
	}
	
	@Transactional(readOnly = true)
	public List<GymPropsEntity> getAllGymProps(final Long userId) throws GymPropsNotFoundException {
		final List<GymPropsEntity> props = gymPropsRepo.findAll();
		if (props == null) {
			throw new GymPropsNotFoundException("Unable to query per-user gym properties");
		}
		return props;
	}
	
	@Transactional(readOnly = true)
	public GymEntity getGym(final Long gymId) throws GymNotFoundException {
		final GymEntity gym = gymRepo.findOne(gymId);
		if (gym == null) {
			throw new GymNotFoundException("Unable to query gym");
		}
		return gym;
	}
	
	@Transactional(readOnly = true)
	public GymPropsEntity getGymProps(final Long userId, final Long gymId) throws GymPropsNotFoundException {
		final GymPropsEntity props = gymPropsRepo.findOneByUserIdAndGymId(userId, gymId);
		if (props == null) {
			throw new GymPropsNotFoundException("Unable to query per-user gym properties");
		}
		return props;
	}

	public GymSummaryDao getGymSummary(final Long userId, final Long gymId) throws GymPropsNotFoundException, GymNotFoundException {
		final GymEntity gym = getGym(gymId);
		final GymPropsEntity props = getGymProps(userId, gymId);
		final GymSummaryDao dao = GymSummaryDao.fromGymEntity(gym);
		if (props != null) {
			dao.setStatus(props.getBadgeStatus());
		}
		return dao;
	}

	@Transactional(readOnly = true)
	public List<GymSummaryDao> getGymSummaries(final Long userId) throws GymNotFoundException {
		// Get any user specific properties for the gyms and put them in a hash map
		try {
			final List<GymPropsEntity> props = getAllGymProps(userId);
			final Map<Long, GymPropsEntity> gymPropsMap = props.stream().collect(Collectors.toMap(GymPropsEntity::getGymId, x-> x));
			
			// Get all the gyms
			final List<GymEntity> gyms = getAllGyms(userId);
			
			// Fill in the user-specific data
			return Lists.newArrayList(Lists.transform(gyms, gym -> {
				final GymPropsEntity gymProps = gymPropsMap.get(gym.getId());
				final GymSummaryDao dao = GymSummaryDao.fromGymEntity(gym);
				if (gymProps != null) {
					dao.setStatus(gymProps.getBadgeStatus());
				} 
				return dao;
	        }));
		} catch (GymPropsNotFoundException e) {
			// This is expected if the user has never used the system before
			// So just return all the gyms with default user data
			final List<GymEntity> gyms = getAllGyms(userId);
			return Lists.newArrayList(Lists.transform(gyms, gym -> {
	            return GymSummaryDao.fromGymEntity(gym);
	        }));
		}

	}

	public GymSummaryDao updateGym(final UserEntity user, final Long gymId,
			final GymBadgeStatus status, final Boolean isPark) throws GymNotFoundException {
		final GymEntity gym = getGym(gymId);
		if (gym==null) {
			throw new GymNotFoundException("Unable to query gym for update");
		}
		// Admin users can update the park status of a gym
		if (user.getAdmin()) {
			if (gym.getPark() != isPark) {
				gym.setPark(isPark);
				gymRepo.save(gym);
			}
		}
		// Try to find users current properties for this gym
		GymPropsEntity props = null;
		try {
			
			props = getGymProps(user.getId(), gymId);
		} catch (GymPropsNotFoundException e) {
			// Expected if user has never edited this gym before
			// Insert new user/gym property record
			props = new GymPropsEntity();
			props.setUserId(user.getId());
			props.setGymId(gymId);
		}
		// Now update the per-user properties of the gym
		props.setBadgeStatus(status);
		gymPropsRepo.save(props);
		// Create a summary object with the gym details and per-user gym details
		final GymSummaryDao dao = GymSummaryDao.fromGymEntity(gym);
		dao.setStatus(props.getBadgeStatus());
		return dao;
	}
	
}
