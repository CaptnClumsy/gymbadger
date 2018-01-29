package com.clumsy.gymbadger.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.GymBadgeStatus;
import com.clumsy.gymbadger.data.GymHistoryDao;
import com.clumsy.gymbadger.data.GymSummaryDao;
import com.clumsy.gymbadger.data.HistoryType;
import com.clumsy.gymbadger.entities.AreaEntity;
import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.entities.GymPropsEntity;
import com.clumsy.gymbadger.entities.PokemonEntity;
import com.clumsy.gymbadger.entities.UserBadgeHistoryEntity;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.entities.UserGymHistoryEntity;
import com.clumsy.gymbadger.entities.UserRaidHistoryEntity;
import com.clumsy.gymbadger.repos.AreaRepo;
import com.clumsy.gymbadger.repos.BossRepo;
import com.clumsy.gymbadger.repos.GymPropsRepo;
import com.clumsy.gymbadger.repos.GymRepo;
import com.clumsy.gymbadger.repos.UserBadgeHistoryRepo;
import com.clumsy.gymbadger.repos.UserGymHistoryRepo;
import com.clumsy.gymbadger.repos.UserRaidHistoryRepo;
import com.google.common.collect.Lists;


@Service
public class GymService {

	private final GymRepo gymRepo;
	private final GymPropsRepo gymPropsRepo;
	private final AreaRepo areaRepo;
	private final UserGymHistoryRepo gymHistoryRepo;
	private final UserBadgeHistoryRepo badgeHistoryRepo;
	private final UserRaidHistoryRepo raidHistoryRepo;
	private final BossRepo pokemonRepo;

	@Autowired
	GymService(final GymRepo gymRepo, final GymPropsRepo gymPropsRepo,
		final AreaRepo areaRepo, final UserGymHistoryRepo gymHistoryRepo,
		final UserBadgeHistoryRepo badgeHistoryRepo, final UserRaidHistoryRepo raidHistoryRepo,
		final BossRepo pokemonRepo) {
		this.gymRepo = gymRepo;
		this.gymPropsRepo = gymPropsRepo;
		this.areaRepo = areaRepo;
		this.gymHistoryRepo = gymHistoryRepo;
		this.badgeHistoryRepo = badgeHistoryRepo;
		this.raidHistoryRepo = raidHistoryRepo;
		this.pokemonRepo = pokemonRepo;
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
			final Long pokemonId, final Boolean caught) throws GymNotFoundException, PokemonNotFoundException {
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
		boolean hasBadgeChanged = false;
		boolean hasRaidChanged = false;
		try {
			props = getGymProps(user.getId(), gymId);
			if (lastRaid!=props.getLastRaid() || pokemonId != props.getPokemonId() ||
				caught!=props.getCaught()) {
				hasRaidChanged = true;
			}
			if (status!=props.getBadgeStatus()) {
				hasBadgeChanged = true;
			}
		} catch (GymPropsNotFoundException e) {
			// Expected if user has never edited this gym before
			// Insert new user/gym property record
			props = new GymPropsEntity();
			props.setUserId(user.getId());
			props.setGymId(gymId);
			if (status!=null) {
				hasBadgeChanged = true;
			}
			if (lastRaid!=null || pokemonId!=null || caught!=null) {
				hasRaidChanged=true;
			}
		}
		// Now update the per-user properties of the gym
		props.setLastRaid(lastRaid);
		props.setPokemonId(pokemonId);
		props.setCaught(caught);
		props.setBadgeStatus(status);
		gymPropsRepo.save(props);
		// And write history records
		if (hasRaidChanged) {
			PokemonEntity pokemon = null;
			if (pokemonId!=null) {
				pokemon = pokemonRepo.findOne(pokemonId);
				if (pokemon==null) {
					throw new PokemonNotFoundException("Pokemon "+pokemonId+" not found");
				}
			}
			writeGymRaidHistory(new Date(), user.getId(), gymId,
				lastRaid, pokemon, caught);	
		}
		if (hasBadgeChanged) {
			writeGymBadgeHistory(new Date(), user.getId(), gymId, status);
		}
		// Create a summary object with the gym details and per-user gym details
		final GymSummaryDao dao = GymSummaryDao.fromGymEntity(gym);
		dao.setStatus(props.getBadgeStatus());
		dao.setLastRaid(props.getLastRaid());
		dao.setPokemonId(props.getPokemonId());
		dao.setCaught(props.getCaught());
		return dao;
	}

	private UserGymHistoryEntity writeGymRaidHistory(final Date date, final Long userId, final Long gymId,
			final Date lastRaid, final PokemonEntity pokemon, final Boolean caught) {
		// Write raid history
		UserRaidHistoryEntity raidHistory = new UserRaidHistoryEntity();
		raidHistory.setLastRaid(lastRaid);
		raidHistory.setPokemon(pokemon);
		raidHistory.setCaught(caught);
		UserRaidHistoryEntity savedRaidHistory = raidHistoryRepo.save(raidHistory);
		// Add to the history table
		UserGymHistoryEntity history = new UserGymHistoryEntity();
		history.setType(HistoryType.RAID);
		history.setDateTime(new Date());
		history.setUserId(userId);
		history.setGymId(gymId);
		history.setHistoryId(savedRaidHistory.getId());
		return gymHistoryRepo.save(history);
	}
	
	private UserGymHistoryEntity writeGymBadgeHistory(final Date date, final Long userId, final Long gymId,
			final GymBadgeStatus status) {
		// Write the badge history
		UserBadgeHistoryEntity badgeHistory = new UserBadgeHistoryEntity();
		badgeHistory.setBadgeStatus(status);
		UserBadgeHistoryEntity savedBadgeHistory = badgeHistoryRepo.save(badgeHistory);
		// Add to the history table
		UserGymHistoryEntity history = new UserGymHistoryEntity();
		history.setType(HistoryType.BADGE);
		history.setDateTime(new Date());
		history.setUserId(userId);
		history.setGymId(gymId);
		history.setHistoryId(savedBadgeHistory.getId());
		return gymHistoryRepo.save(history);
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
	
	@Transactional(readOnly = true)
	public List<GymHistoryDao> getGymHistory(final Long gymId, final UserEntity user) throws GymNotFoundException {
		final GymEntity gym = getGym(gymId);
		if (gym==null) {
			throw new GymNotFoundException("Unable to query gym for update");
		}
		List<Object> raidHistory = gymHistoryRepo.findAllRaidHistory(user.getId(), gymId);
		List<Object> badgeHistory = gymHistoryRepo.findAllBadgeHistory(user.getId(), gymId);
		if ((badgeHistory==null || badgeHistory.size()==0) && 
			(raidHistory==null || raidHistory.size()==0)) {
			return Collections.emptyList();
		}
		if (badgeHistory==null) {
			badgeHistory = Collections.emptyList();
		}
		if (raidHistory==null) {
			raidHistory = Collections.emptyList();
		}
		// Merge the lists together
		final List<Object> mergedList = new ArrayList<Object>(raidHistory.size()+badgeHistory.size());
		mergedList.addAll(raidHistory);
        mergedList.addAll(badgeHistory);
        // Convert them to dao objects
        final ArrayList<GymHistoryDao> daoList = new ArrayList<GymHistoryDao>(raidHistory.size()+badgeHistory.size());
        final Iterator<Object> it = mergedList.iterator();
    	while (it.hasNext()) {
    		Object[] arr = (Object[])it.next();
    		final GymHistoryDao dao = GymHistoryDao.fromEntities(arr[0],arr[1]);
    		daoList.add(dao);
    	}
    	// Sort them
        Collections.sort(daoList);
        
		return daoList;
	}
}
