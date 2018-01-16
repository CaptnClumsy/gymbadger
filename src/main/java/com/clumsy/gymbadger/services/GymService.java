package com.clumsy.gymbadger.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.GymSummaryDao;
import com.clumsy.gymbadger.entities.AreaEntity;
import com.clumsy.gymbadger.entities.GymBadgeStatus;
import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.entities.GymPropsEntity;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.AreaRepo;
import com.clumsy.gymbadger.repos.GymPropsRepo;
import com.clumsy.gymbadger.repos.GymRepo;
import com.google.common.collect.Lists;


@Service
public class GymService {

	private final GymRepo gymRepo;
	private final GymPropsRepo gymPropsRepo;
	private final AreaRepo areaRepo;
	
	@Autowired
	GymService(final GymRepo gymRepo, final GymPropsRepo gymPropsRepo, final AreaRepo areaRepo) {
		this.gymRepo = gymRepo;
		this.gymPropsRepo = gymPropsRepo;
		this.areaRepo = areaRepo;
	}
	
	@Transactional(readOnly = true)
	public List<GymEntity> getAllGyms() throws GymNotFoundException {
		final List<GymEntity> gyms = gymRepo.findAllGyms();
		if (gyms == null) {
			throw new GymNotFoundException("Unable to query gym list");
		}
		return gyms;
	}
	
	@Transactional(readOnly = true)
	public List<GymEntity> getGymsById(final List<Long> ids) throws GymNotFoundException {
		final List<GymEntity> gyms = gymRepo.findGyms(ids);
		if (gyms == null) {
			throw new GymNotFoundException("Unable to query gym list by id");
		}
		return gyms;
	}
	
	@Transactional(readOnly = true)
	public List<GymEntity> getGymsByArea(final List<Long> areas) throws GymNotFoundException {
		final List<GymEntity> gyms = gymRepo.findGymsByArea(areas);
		if (gyms == null) {
			throw new GymNotFoundException("Unable to query gym list by area");
		}
		return gyms;
	}
	
	@Transactional(readOnly = true)
	public List<GymPropsEntity> getAllGymProps(final Long userId) throws GymPropsNotFoundException {
		final List<GymPropsEntity> props = gymPropsRepo.findAllByUserId(userId);
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
			dao.setLastRaid(props.getLastRaid());
			dao.setPokemonId(props.getPokemonId());
			dao.setCaught(props.getCaught());
		}
		return dao;
	}

	public List<GymSummaryDao> getAllGymSummaries(final Long userId) throws GymNotFoundException {
		return getGymSummariesInternal(userId, null, null);
	}
	
	public List<GymSummaryDao> getGymSummariesByArea(final Long userId, final List<Long> areas) throws GymNotFoundException {
		return getGymSummariesInternal(userId, null, areas);
	}
	
	public List<GymSummaryDao> getGymSummariesById(final Long userId, final List<Long> ids) throws GymNotFoundException {
		return getGymSummariesInternal(userId, ids, null);
	}
	
	@Transactional(readOnly = true)
	private List<GymSummaryDao> getGymSummariesInternal(final Long userId, final List<Long> ids, final List<Long> areas) throws GymNotFoundException {
		// Get the list of gyms
		List<GymEntity> gyms = null;
		if (ids != null && ids.size() != 0) {
			// Only the listed gyms
		    gyms = getGymsById(ids);
		} else if (areas != null && areas.size() != 0) {
			// Only get gyms for the listed areas
			gyms = getGymsByArea(areas);
		} else {
			// Get all the gyms
			gyms = getAllGyms();
		}
		
		// Get any user specific properties for the gyms and put them in a hash map
		try {
			final List<GymPropsEntity> props = getAllGymProps(userId);
			
			// Put all the user specific properties for the gyms into a hash map
			final Map<Long, GymPropsEntity> gymPropsMap = props.stream().collect(Collectors.toMap(GymPropsEntity::getGymId, x-> x));
			
			// Fill in the user-specific data
			return Lists.newArrayList(Lists.transform(gyms, gym -> {
				final GymPropsEntity gymProps = gymPropsMap.get(gym.getId());
				final GymSummaryDao dao = GymSummaryDao.fromGymEntity(gym);
				if (gymProps != null) {
					dao.setStatus(gymProps.getBadgeStatus());
					dao.setLastRaid(gymProps.getLastRaid());
					dao.setPokemonId(gymProps.getPokemonId());
					dao.setCaught(gymProps.getCaught());
				} 
				return dao;
	        }));
		} catch (GymPropsNotFoundException e) {
			// This is expected if the user has never used the system before
			// So just return the gyms with default user data
			return Lists.newArrayList(Lists.transform(gyms, gym -> {
	            return GymSummaryDao.fromGymEntity(gym);
	        }));
		}

	}

	@Transactional
	public GymSummaryDao updateGym(final UserEntity user, final Long gymId, final Boolean isPark,
			final GymBadgeStatus status, final Date lastRaid,
			final Long pokemonId, final Boolean caught) throws GymNotFoundException {
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
		props.setLastRaid(lastRaid);
		props.setPokemonId(pokemonId);
		props.setCaught(caught);
		gymPropsRepo.save(props);
		// Create a summary object with the gym details and per-user gym details
		final GymSummaryDao dao = GymSummaryDao.fromGymEntity(gym);
		dao.setStatus(props.getBadgeStatus());
		dao.setLastRaid(props.getLastRaid());
		dao.setPokemonId(props.getPokemonId());
		dao.setCaught(props.getCaught());
		return dao;
	}

	@Transactional
	public GymSummaryDao newGym(final UserEntity user, final String name, final Double lat, final Double lng,
			final Long areaId, final Boolean park) throws AccessControlException, AreaNotFoundException {
		if (!user.getAdmin()) {
			throw new AccessControlException("Only admin users can create new gyms");
		}
		final AreaEntity area = areaRepo.findOne(areaId);
		if (area == null) {
			throw new AreaNotFoundException("Specified area does not exist");
		}
		final GymEntity newGym = new GymEntity();
		newGym.setName(name);
		newGym.setLatitude(lat);
		newGym.setLongitude(lng);
		newGym.setArea(area);
		newGym.setPark(park);
		final GymEntity savedGym = gymRepo.save(newGym);
		return GymSummaryDao.fromGymEntity(savedGym);
	}
	
	@Transactional
	public void deleteGym(final UserEntity user, final Long gymId) throws AccessControlException, GymNotFoundException {
		if (!user.getAdmin()) {
			throw new AccessControlException("Only admin users can delete gyms");
		}
		final GymEntity gym = gymRepo.findOne(gymId);
		if (gym == null) {
			throw new GymNotFoundException("Specified gym does not exist");
		}
		gymRepo.delete(gym);
	}
	
}
