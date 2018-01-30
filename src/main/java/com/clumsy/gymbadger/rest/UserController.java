package com.clumsy.gymbadger.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.AnnouncementDao;
import com.clumsy.gymbadger.data.AnnouncementType;
import com.clumsy.gymbadger.data.LeadersDao;
import com.clumsy.gymbadger.data.UserDao;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.repos.UserRepo;
import com.clumsy.gymbadger.services.AccessControlException;
import com.clumsy.gymbadger.services.AnnouncementNotFoundException;
import com.clumsy.gymbadger.services.UserNotFoundException;
import com.clumsy.gymbadger.services.UserService;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
 
@RestController
@RequestMapping("/api/users")
public class UserController {
 
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/currentUser", method = RequestMethod.GET)
    public UserDao getCurrentUser(Principal principal) {
		try {
			UserEntity user = userService.getCurrentUser(principal);
			return new UserDao(user.getId(), user.getName(), user.getDisplayName(), user.getAdmin());
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException("Current user not found");
		}
	}

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public UserDao getUser(@PathVariable("id") Long id) {
        final UserEntity user = userRepo.findOne(id);
        if (user == null) {
        	throw new ObjectNotFoundException("User "+id+" not found");
        }
        return new UserDao(user.getId(), user.getName(), user.getDisplayName(), user.getAdmin());
    }
    
    @RequestMapping(value = "/leaderboard", method = RequestMethod.GET)
    public LeadersDao getLeaderboard(Principal principal) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
    	try {
    	    UserEntity user = userService.getCurrentUser(principal);
		    return userService.getGoldLeaderboard(user);
    	} catch (UserNotFoundException e) {
    		throw new ObjectNotFoundException("Current user not found");
    	}
	}
    
    @RequestMapping(value = "/leaderboard/totals", method = RequestMethod.GET)
    public LeadersDao getTotalsLeaderboard(Principal principal) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
    	try {
    	    UserEntity user = userService.getCurrentUser(principal);
		    return userService.getTotalLeaderboard(user);
    	} catch (UserNotFoundException e) {
    		throw new ObjectNotFoundException("Current user not found");
    	}
	}
    
    @RequestMapping(value = "/leaderboard", method = RequestMethod.PUT)
    public UserDao setLeaderboard(Principal principal, @RequestBody Boolean share) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
    	try {
    	    UserEntity user = userService.getCurrentUser(principal);
		    return userService.setLeaderboard(user, share);
    	} catch (UserNotFoundException e) {
    		throw new ObjectNotFoundException("Current user not found");
    	}
	}
    
    @RequestMapping(value = "/announcements", method = RequestMethod.GET)
    public List<AnnouncementDao> getAnnouncements(Principal principal) {
    	try {
    	    UserEntity user = userService.getCurrentUser(principal);
		    return userService.getAnnouncements(user);
    	} catch (UserNotFoundException e) {
    		throw new ObjectNotFoundException("Current user not found");
    	}
	}

    @RequestMapping(value = "/announcements/{type}", method = RequestMethod.PUT)
    public AnnouncementDao newAnnouncement(Principal principal, 
    		@PathVariable("type") Integer type, @RequestBody String message) {
    	try {
    	    UserEntity user = userService.getCurrentUser(principal);
		    return userService.postAnnouncement(user, AnnouncementType.fromInt(type), message, true);
    	} catch (UserNotFoundException e) {
    		throw new ObjectNotFoundException("Current user not found");
    	} catch (AccessControlException e) {
    	    throw new ForbiddenException(e);
		}
	}

    @RequestMapping(value = "/announcements", method = RequestMethod.DELETE)
    public void deleteAnnouncement(Principal principal) {
    	try {
    	    UserEntity user = userService.getCurrentUser(principal);
		    userService.deleteUserAnnouncement(user);
    	} catch (UserNotFoundException e) {
    		throw new ObjectNotFoundException("Current user not found");
    	} catch (AnnouncementNotFoundException e) {
			throw new ObjectNotFoundException("Announcement not found");
		}
	}

    @RequestMapping(value = "/announcements/admin/{id}", method = RequestMethod.DELETE)
    public void deleteWholeAnnouncement(Principal principal, 
    		@PathVariable("id") Long id) {
    	try {
    	    UserEntity user = userService.getCurrentUser(principal);
		    userService.deleteAnnouncement(user, id);
    	} catch (UserNotFoundException e) {
    		throw new ObjectNotFoundException("Current user not found");
    	} catch (AnnouncementNotFoundException e) {
			throw new ObjectNotFoundException("Announcement not found");
		} catch (AccessControlException e) {
			throw new ForbiddenException(e);
		}
	}

}