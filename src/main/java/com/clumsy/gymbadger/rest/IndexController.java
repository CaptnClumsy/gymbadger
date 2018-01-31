package com.clumsy.gymbadger.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.services.GymNotFoundException;
import com.clumsy.gymbadger.services.GymService;

@Controller
public class IndexController {

	@Autowired
	private GymService gymService;

    @RequestMapping("/")
    public String hello(Model model, HttpServletRequest request, @RequestParam(value="gymid", required=false) Long gymId) {
    	model.addAttribute("googleMapsAPIKey", "AIzaSyA9OSRLuuH3o7YhJ6bRUVtD9TxhlSDqSDU");
    	try {
    		if (gymId!=null) {
				GymEntity gym = gymService.getGym(gymId);
				model.addAttribute("gymName", gym.getName());
				model.addAttribute("gymDescription", "Gym in the "+gym.getArea().getName()+" area.");
				model.addAttribute("gymUrl", request.getRequestURL().toString() + "?" + request.getQueryString());
				model.addAttribute("gymImageUrl", gym.getImageUrl());
    		} else {
    			model.addAttribute("gymName", "Gymbadger");
				model.addAttribute("gymDescription", "");
				model.addAttribute("gymUrl", request.getRequestURL().toString());
				model.addAttribute("gymImageUrl", "https://www.gymbadger.com/images/badger.png");
    		}
		} catch (GymNotFoundException e) {
			throw new ObjectNotFoundException(e);
		}
        return "index";
    }

}
