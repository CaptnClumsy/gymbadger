package com.clumsy.gymbadger.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.ChartScopeDao;
import com.clumsy.gymbadger.data.FavouriteDao;
import com.clumsy.gymbadger.data.FavouritesDao;
import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.UserRaidHistoryRepo;

@Service
public class ReportService {

	private final UserRaidHistoryRepo raidHistoryRepo;
	
	@Autowired
	ReportService(final UserRaidHistoryRepo raidHistoryRepo) {
		this.raidHistoryRepo = raidHistoryRepo;
	}

	@Transactional(readOnly = true)
	public FavouritesDao getFavouriteGyms(final UserEntity user, final ChartScopeDao scope) {
		List<FavouriteDao> favourites = new ArrayList<FavouriteDao>();
		List<Object> results = raidHistoryRepo.findRaidGymsByDay(user.getId());
		final Iterator<Object> it = results.iterator();
    	while (it.hasNext()) {
    		Object[] arr = (Object[])it.next();
    		FavouriteDao favourite = new FavouriteDao();
			favourite.setId(((GymEntity)arr[0]).getId());
			favourite.setName(((GymEntity)arr[0]).getName());
			favourite.setCount((Long)arr[1]);
			favourites.add(favourite);
    	}
		FavouritesDao dao = new FavouritesDao();
		dao.setFavourites(favourites);
		return dao;
	}

}
