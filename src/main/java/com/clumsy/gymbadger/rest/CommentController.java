package com.clumsy.gymbadger.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.CommentDao;
import com.clumsy.gymbadger.data.CommentListDao;
import com.clumsy.gymbadger.data.NewCommentDao;
import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.services.CommentService;
import com.clumsy.gymbadger.services.GymNotFoundException;
import com.clumsy.gymbadger.services.GymService;
import com.clumsy.gymbadger.services.UserNotFoundException;
import com.clumsy.gymbadger.services.UserService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private GymService gymService;
	
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public CommentListDao getComments(@PathVariable("id") Long gymId, Principal principal) {
    	CommentListDao list = new CommentListDao();
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		list.setLoggedin(false);
    	} else {
    		list.setLoggedin(true);
    	}
		try {
			final UserEntity user = userService.getCurrentUser(principal);
			list.setComments(commentService.getAllComments(gymId, user.getId()));
			return list;
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public CommentDao addComment(@RequestBody NewCommentDao newComment, Principal principal) {
    	if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || principal == null) {
    		throw new NotLoggedInException();
    	}
		try {
			final UserEntity user = userService.getCurrentUser(principal);
			final GymEntity gym = gymService.getGym(newComment.getGymid());
			return commentService.createComment(user, gym, newComment.getText(), newComment.getIspublic());
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		} catch (GymNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }
}
