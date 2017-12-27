package com.clumsy.gymbadger.rest;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.CommentDao;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.services.CommentService;
import com.clumsy.gymbadger.services.UserNotFoundException;
import com.clumsy.gymbadger.services.UserService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private UserService userService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDao> getComments(@PathVariable("id") Long gymId, Principal principal) {
		try {
			final UserEntity user = userService.getCurrentUser(principal);
			return commentService.getAllComments(gymId, user.getId());
			
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
    }
}
