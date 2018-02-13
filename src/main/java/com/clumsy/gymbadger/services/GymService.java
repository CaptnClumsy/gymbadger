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
		return GymSummaryDao.fromGymEntity(gym, props);
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
				return GymSummaryDao.fromGymEntity(gym, gymProps);
	        }));
		} catch (GymPropsNotFoundException e) {
			// This is expected if the user has never used the system before
			// So just return the gyms with default user data
			return Lists.newArrayList(Lists.transform(gyms, gym -> {
	            return GymSummaryDao.fromGymEntity(gym, null);
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
			if (status!=null && !status.is(props.getBadgeStatus())) {
				hasBadgeChanged = true;
			}
			if (props.getLastRaid() == null) {
				// No last recorded raid and now one is specified
				if (lastRaid!=null || caught!=null || pokemonId!=null) {
				    hasRaidChanged=true;
			    }
			} else {
				// Last raid was recorded but date or pokemon or caught flag has changed
				if (lastRaid!=null && (props.getLastRaid()==null || !lastRaid.equals(props.getLastRaid().getLastRaid()))) {
					hasRaidChanged=true;
				}
				if (pokemonId!=null && (props.getLastRaid().getPokemon()==null ||
						pokemonId!=props.getLastRaid().getPokemon().getId())) {
					hasRaidChanged=true;
				}
				if (caught!=props.getLastRaid().getCaught()) {
					hasRaidChanged = true;
				}
			}
		} catch (GymPropsNotFoundException e) {
			// Expected if user has never edited this gym before
			// Insert new user/gym property record
			props = new GymPropsEntity();
			props.setUserId(user.getId());
			props.setGymId(gymId);
			if (status!=null && !status.is(GymBadgeStatus.NONE)) {
				hasBadgeChanged = true;
			}
			if (lastRaid!=null || pokemonId!=null || caught!=null) {
				hasRaidChanged=true;
			}
		}
		// Write history records
		if (hasRaidChanged) {
			PokemonEntity pokemon = null;
			if (pokemonId!=null) {
				pokemon = pokemonRepo.findOne(pokemonId);
				if (pokemon==null) {
					throw new PokemonNotFoundException("Pokemon "+pokemonId+" not found");
				}
			}
			final UserRaidHistoryEntity raidHistory = writeGymRaidHistory(user.getId(), gymId,
				lastRaid, pokemon, caught);
			if (props.getLastRaid()!=null) {
				if (raidHistory.getLastRaid().after(props.getLastRaid().getLastRaid())) {
					props.setLastRaid(raidHistory);
				}
			} else {
				props.setLastRaid(raidHistory);
			}
			
		}
		if (hasBadgeChanged) {
			writeGymBadgeHistory(user.getId(), gymId, status);
		}
		// Now update the per-user properties of the gym
		props.setBadgeStatus(status);
		gymPropsRepo.save(props);
		// Create a summary object with the gym details and per-user gym details
		final GymSummaryDao dao = GymSummaryDao.fromGymEntity(gym, props);
		return dao;
	}

	private UserRaidHistoryEntity writeGymRaidHistory(final Long userId, final Long gymId,
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
		gymHistoryRepo.save(history);
		return savedRaidHistory;
	}
	
	private UserGymHistoryEntity writeGymBadgeHistory(final Long userId, final Long gymId, final GymBadgeStatus status) {
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
		return GymSummaryDao.fromGymEntity(savedGym, null);
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

	@Transactional
	public GymHistoryDao updateGymHistory(final Long gymId, final UserEntity user, final GymHistoryDao dao)
		throws GymHistoryNotFoundException, GymNotFoundException,
		PokemonNotFoundException, UnknownHistoryTypeException, AccessControlException {
		final GymEntity gym = gymRepo.findOne(gymId);
		if (gym==null) {
			throw new GymNotFoundException("Gym not found");
		}
		final UserGymHistoryEntity history = gymHistoryRepo.findOneById(dao.getId());
		if (history==null) {
			throw new GymHistoryNotFoundException("History entry not found");
		}
		if (!history.getUserId().equals(user.getId())) {
			throw new AccessControlException("Current user cannot update history for another user");
		}
		if (history.getType().equals(HistoryType.BADGE)) {
			final UserBadgeHistoryEntity badgeHistory = badgeHistoryRepo.findOne(history.getHistoryId());
			if (badgeHistory==null) {
				throw new GymHistoryNotFoundException("Badge history entry not found");
			}
			badgeHistory.setBadgeStatus(dao.getStatus());
			final UserBadgeHistoryEntity savedBadgeHistory = badgeHistoryRepo.save(badgeHistory);
			return GymHistoryDao.fromEntities(history, savedBadgeHistory);
		} else if (history.getType().equals(HistoryType.RAID)) {
			final UserRaidHistoryEntity raidHistory = raidHistoryRepo.findOne(history.getHistoryId());
			if (raidHistory==null) {
				throw new GymHistoryNotFoundException("Raid history entry not found");
			}
			raidHistory.setLastRaid(dao.getDateTime());
			if (dao.getPokemon()!=null) {
				PokemonEntity pokemon = pokemonRepo.findOne(dao.getPokemon().getId());
				if (pokemon==null) {
					throw new PokemonNotFoundException("Pokemon not found");
				}
				raidHistory.setPokemon(pokemon);
			} else {
				raidHistory.setPokemon(null);
			}
			raidHistory.setCaught(dao.getCaught());
			final UserRaidHistoryEntity savedRaidHistory = raidHistoryRepo.save(raidHistory);
			return GymHistoryDao.fromEntities(history, savedRaidHistory);
		}
		throw new UnknownHistoryTypeException("Unknown history type "+history.getType());
	}
	
	@Transactional
	public GymHistoryDao deleteGymHistory(final Long gymId, final UserEntity user, final Long historyId)
		throws GymHistoryNotFoundException, GymNotFoundException,
		UnknownHistoryTypeException, AccessControlException, GymPropsNotFoundException {
		final GymEntity gym = gymRepo.findOne(gymId);
		if (gym==null) {
			throw new GymNotFoundException("Gym not found");
		}
		final UserGymHistoryEntity history = gymHistoryRepo.findOneById(historyId);
		if (history==null) {
			throw new GymHistoryNotFoundException("History entry not found");
		}
		if (!history.getUserId().equals(user.getId())) {
			throw new AccessControlException("Current user cannot delete history for another user");
		}
		gymHistoryRepo.delete(history);
		if (history.getType().equals(HistoryType.BADGE)) {
			final UserBadgeHistoryEntity badgeHistory = badgeHistoryRepo.findOne(history.getHistoryId());
			if (badgeHistory==null) {
				throw new GymHistoryNotFoundException("Badge history entry not found");
			}
			// Update the latest badge status
			final GymPropsEntity props = gymPropsRepo.findOneByUserIdAndGymId(history.getUserId(), history.getGymId());
			if (props==null) {
				throw new GymPropsNotFoundException("No properites found for this gym and user");
			}
			// Delete the badge history
			badgeHistoryRepo.delete(badgeHistory);
			Long newLatest = badgeHistoryRepo.findLatestBadge(history.getUserId(), history.getGymId());
			// Set latest badge status to the new one
			if (newLatest!=null) {
				final UserBadgeHistoryEntity newLatestBadge = badgeHistoryRepo.findOne(newLatest);
				if (newLatestBadge==null) {
					throw new GymHistoryNotFoundException("Latest badge history entry not found");
				}
			    props.setBadgeStatus(newLatestBadge.getBadgeStatus());
				gymPropsRepo.save(props);
				return GymHistoryDao.fromEntities(history, newLatestBadge);
			}
			writeGymBadgeHistory(history.getUserId(), history.getGymId(), GymBadgeStatus.NONE);
			props.setBadgeStatus(GymBadgeStatus.NONE);
			gymPropsRepo.save(props);
			return GymHistoryDao.fromEntities(history, GymBadgeStatus.NONE);
		} else if (history.getType().equals(HistoryType.RAID)) {
			final UserRaidHistoryEntity raidHistory = raidHistoryRepo.findOne(history.getHistoryId());
			if (raidHistory==null) {
				throw new GymHistoryNotFoundException("Raid history entry not found");
			}
			// Set latestRaid to null in user_gym_props
			final GymPropsEntity props = gymPropsRepo.findOneByUserIdAndGymId(history.getUserId(), history.getGymId());
			if (props==null) {
				throw new GymPropsNotFoundException("No properites found for this gym and user");
			}
			props.setLastRaid(null);
			final GymPropsEntity savedProps = gymPropsRepo.save(props);
			// Delete the raid data
			raidHistoryRepo.delete(raidHistory);
			Long newLatest = raidHistoryRepo.findLatestRaid(history.getUserId(), history.getGymId());
			// Set latestRaid to the new one
			if (newLatest!=null) {
				final UserRaidHistoryEntity newLatestRaid = raidHistoryRepo.findOne(newLatest);
				if (newLatestRaid==null) {
					throw new GymHistoryNotFoundException("Latest raid history entry not found");
				}
				savedProps.setLastRaid(newLatestRaid);
				gymPropsRepo.save(savedProps);
				return GymHistoryDao.fromEntities(history, newLatestRaid);
			}
			// No other raids exist so return an empty dao
			return new GymHistoryDao();
		}
		throw new UnknownHistoryTypeException("Unknown history type "+history.getType());
	}
}
