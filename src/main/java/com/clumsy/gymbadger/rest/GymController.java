package com.clumsy.gymbadger.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.GymSummaryDao;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.services.GymNotFoundException;
import com.clumsy.gymbadger.services.GymService;
import com.clumsy.gymbadger.services.UserNotFoundException;
import com.clumsy.gymbadger.services.UserService;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
 
@RestController
@RequestMapping("/api/gyms")
public class GymController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private GymService gymService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<GymSummaryDao> getGyms(Principal principal) {
		try {
			final UserEntity user = userService.getCurrentUser(principal);
			final List<GymSummaryDao> gyms = gymService.getGymSummaries(user.getId());
			return gyms;
		} catch (GymNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (UserNotFoundException e) {
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
		}
    }
}