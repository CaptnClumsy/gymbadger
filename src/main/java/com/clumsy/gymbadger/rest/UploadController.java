package com.clumsy.gymbadger.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.clumsy.gymbadger.data.BadgeUploadResultDao;
import com.clumsy.gymbadger.data.UploadDao;
import com.clumsy.gymbadger.entities.UserEntity;
import com.clumsy.gymbadger.services.UploadService;
import com.clumsy.gymbadger.services.UserNotFoundException;
import com.clumsy.gymbadger.services.UserService;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
	
	@Autowired
	private UploadService uploadService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/badges", method = RequestMethod.POST)
	public List<BadgeUploadResultDao> UploadBadges(@RequestParam("files[]") List<MultipartFile> files,
		Principal principal) throws Exception {
		if (files==null || files.isEmpty()) {
	    	return new ArrayList<BadgeUploadResultDao>();
	    }
		try {
			final UserEntity user = userService.getCurrentUser(principal);
			UploadDao dao = uploadService.begin(user.getId());
		    for(int i=0; i< files.size(); i++) {
		        if(!files.get(i).isEmpty()) {
		            uploadService.add(dao, files.get(i).getOriginalFilename(), files.get(i).getInputStream());
		        }
		    }
		    List<BadgeUploadResultDao> gyms = uploadService.process(dao);
		    uploadService.end(user.getId());
		    return gyms;
		} catch (UserNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
		
	}
}