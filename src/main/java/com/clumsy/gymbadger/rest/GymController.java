package com.clumsy.gymbadger.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.ChartScopeDao;
import com.clumsy.gymbadger.data.ChartTimeInterval;
import com.clumsy.gymbadger.data.FavouritesDao;
import com.clumsy.gymbadger.data.GymHistoryDao;
import com.clumsy.gymbadger.data.GymSummaryDao;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.services.AccessControlException;
import com.clumsy.gymbadger.services.AreaNotFoundException;
import com.clumsy.gymbadger.services.ExportException;
import com.clumsy.gymbadger.services.ExportService;
import com.clumsy.gymbadger.services.GymHistoryNotFoundException;
import com.clumsy.gymbadger.services.GymNotFoundException;
import com.clumsy.gymbadger.services.GymPropsNotFoundException;
import com.clumsy.gymbadger.services.GymService;
import com.clumsy.gymbadger.services.PokemonNotFoundException;
import com.clumsy.gymbadger.services.ReportService;
import com.clumsy.gymbadger.services.UnknownHistoryTypeException;
import com.clumsy.gymbadger.services.UserNotFoundException;
import com.clumsy.gymbadger.services.UserService;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
 
@RestController
@RequestMapping("/api/gyms")
public class GymController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private GymService gymService;

	@Autowired
	private ExportService exportService;

	@Autowired
	private ReportService reportService;
	
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<GymSummaryDao> getGyms(Principal principal) {
		try {
			final UserEntity user = userService.getCurrentUser(principal);
			final List<GymSummaryDao> gyms = gymService.getAllGymSummaries(user.getId());
			return gyms;
		} catch (GymNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }
    
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public GymSummaryDao addGym(@RequestBody GymSummaryDao newGym, Principal principal) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
		try {
			final UserEntity user = userService.getCurrentUser(principal);
			return gymService.newGym(user, newGym.getName(), newGym.getLat(), newGym.getLng(), newGym.getArea().getId(), newGym.getPark());
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (AccessControlException e) {
			throw new ForbiddenException(e);
		} catch (AreaNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public GymSummaryDao updateGym(@PathVariable("id") Long gymId, @RequestBody GymSummaryDao newGym, Principal principal) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
		try {
			final UserEntity user = userService.getCurrentUser(principal);
			return gymService.updateGym(user, gymId, newGym.getPark(), newGym.getStatus(), newGym.getLastRaid(),
			    newGym.getPokemonId(), newGym.getCaught());
		} catch (GymNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (PokemonNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }
    
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> exportGyms(@RequestParam(value = "gyms", required = false) List<Long> gyms,
    		@RequestParam(value = "areas", required = false) List<Long> areas,
    		@RequestParam(value = "sort", required = false) String sort, 
    		Principal principal) {
    	try {
    		// Get the CSV file content as a byte array
			final UserEntity user = userService.getCurrentUser(principal);
			// Export all gyms in the selected areas or specifically listed gyms
			final byte[] content = exportService.exportToCsv(user, gyms, areas, sort);
			// Get current date/time for the output filename
			String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(Calendar.getInstance().getTime());
			// Setup the response headers
			HttpHeaders responseHeaders = new HttpHeaders();
	        responseHeaders.add("content-disposition", "attachment; filename=" + "export-" + timeStamp + ".csv");
	        responseHeaders.add("Content-Type","text/csv");
	        // Return the data
			return new ResponseEntity<ByteArrayResource>(new ByteArrayResource(content), responseHeaders, HttpStatus.OK);
		} catch (GymNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (ExportException e) {
			throw new ExportFailedException(e);
		}
    }

    @RequestMapping(value = "/{id}/history", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<GymHistoryDao> getGymHistory(@PathVariable("id") Long gymId, Principal principal) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
		try {
			final UserEntity user = userService.getCurrentUser(principal);
    	    return gymService.getGymHistory(gymId, user);
		} catch (GymNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }
    
    @RequestMapping(value = "/{id}/history/{historyid}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public GymHistoryDao updateGymHistory(@PathVariable("id") Long gymId, @PathVariable("historyid") Long historyId,
    		@RequestBody GymHistoryDao dao, Principal principal) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
		try {
			final UserEntity user = userService.getCurrentUser(principal);
    	    return gymService.updateGymHistory(gymId, user, dao);
		} catch (GymNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (GymHistoryNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (PokemonNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (UnknownHistoryTypeException e) {
			throw new ObjectNotFoundException(e);
		} catch (AccessControlException e) {
			throw new ForbiddenException(e);
		} catch (GymPropsNotFoundException e) {
			throw new ForbiddenException(e);
		}
    }

    @RequestMapping(value = "/{id}/history/{historyid}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public GymHistoryDao deleteGymHistory(@PathVariable("id") Long gymId, @PathVariable("historyid") Long historyId, Principal principal) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
		try {
			final UserEntity user = userService.getCurrentUser(principal);
    	    return gymService.deleteGymHistory(gymId, user, historyId);
		} catch (GymNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (GymHistoryNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (UnknownHistoryTypeException e) {
			throw new ObjectNotFoundException(e);
		} catch (AccessControlException e) {
			throw new ForbiddenException(e);
		} catch (GymPropsNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }
    
    @RequestMapping(value = "/favourites", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public FavouritesDao favouriteGyms(@RequestParam(value = "scope", required = false) String scope,
    		@RequestParam(value = "start", required = false) Long start, Principal principal) {
    	try {
    		// Build the scope object
    		ChartScopeDao scopeDao = new ChartScopeDao();
    		if (scope!=null && !scope.isEmpty()) {
    		    scopeDao.setInterval(ChartTimeInterval.fromStringIgnoreCase(scope));
    		}
    		if (start!=null) {
    			final Date startDate = new Date(start);
    			scopeDao.setStart(startDate);
    		}
    		// Get the data for the report and return it
			final UserEntity user = userService.getCurrentUser(principal);
			return reportService.getFavouriteGyms(user, scopeDao);
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }
}