package com.clumsy.gymbadger.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.ChartScopeDao;
import com.clumsy.gymbadger.data.ChartTimeInterval;
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
		final FavouritesDao dao = new FavouritesDao();
		final List<FavouriteDao> favourites = new ArrayList<FavouriteDao>();
		List<Object> results = null;
		if (scope==null || scope.getInterval().is(ChartTimeInterval.ALL)) {
			dao.setSince(null);
		    results = raidHistoryRepo.countAllGymRaids(user.getId());
		} else {
			Date since = null;
			if (scope.getInterval().is(ChartTimeInterval.CUSTOM)) {
			    since = scope.getStart();
			} else if (scope.getInterval().is(ChartTimeInterval.DAY)) {
				since = new Date();
			} else if (scope.getInterval().is(ChartTimeInterval.WEEK)) {
				// find nearest Sunday
				Calendar now = Calendar.getInstance();
				int weekday = now.get(Calendar.DAY_OF_WEEK);
				if (weekday != Calendar.SUNDAY)
				{
				    int days = (weekday-1);
				    now.add(Calendar.DAY_OF_YEAR, -days);   
				}
				since = now.getTime();
			} else if (scope.getInterval().is(ChartTimeInterval.MONTH)) {
				Calendar now = Calendar.getInstance();
				now.set(Calendar.DAY_OF_MONTH, 1);
				since = now.getTime();
			} else if (scope.getInterval().is(ChartTimeInterval.YEAR)) {
				Calendar now = Calendar.getInstance();
				now.set(Calendar.DAY_OF_MONTH, 1);
				now.set(Calendar.MONTH, Calendar.JANUARY);
				since = now.getTime();
			}
			// Set to midnight
			Calendar cal = Calendar.getInstance();
			cal.setTime(since);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			// Query raids since that time
			results = raidHistoryRepo.countGymRaidsSince(user.getId(), cal.getTime());
			dao.setSince(cal.getTime());
 		}
		final Iterator<Object> it = results.iterator();
		Long total = 0L;
    	while (it.hasNext()) {
    		Object[] arr = (Object[])it.next();
    		FavouriteDao favourite = new FavouriteDao();
			favourite.setId(((GymEntity)arr[0]).getId());
			favourite.setName(((GymEntity)arr[0]).getName());
			favourite.setCount((Long)arr[1]);
			total+=favourite.getCount();
			favourites.add(favourite);
    	}
		dao.setTotal(total);
		dao.setFavourites(favourites);
		return dao;
	}

}
