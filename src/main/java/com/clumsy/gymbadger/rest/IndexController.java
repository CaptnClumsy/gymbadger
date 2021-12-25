package com.clumsy.gymbadger.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.entities.RegionEntity;
import com.clumsy.gymbadger.services.GymNotFoundException;
import com.clumsy.gymbadger.services.GymService;
import com.clumsy.gymbadger.services.RegionNotFoundException;
import com.clumsy.gymbadger.services.RegionService;

@Controller
public class IndexController {

	@Autowired
	private GymService gymService;
	
	@Autowired
	private RegionService regionService;

    @Value("${google.api.key}")
    private String apiKey;

	 
    private String getIndexWithGym(Model model, HttpServletRequest request, Long gymId) {
    	model.addAttribute("googleMapsAPIKey", apiKey);
    	try {
    		if (gymId!=null) {
				GymEntity gym = gymService.getGym(gymId);
				model.addAttribute("gymName", gym.getName());
				model.addAttribute("gymDescription", "Gym in the "+gym.getArea().getName()+" area.");
				model.addAttribute("gymUrl", request.getRequestURL().toString() + "?" + request.getQueryString());
				model.addAttribute("gymImageUrl", gym.getImageUrl());
				model.addAttribute("regionName", "all");
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

    private void addRegionInfo(Model model, String regionName) {
    	try {
    		if (regionName.equals("none")) {
    			model.addAttribute("regionId", 0L);
	        	model.addAttribute("regionName", regionName);
	        	model.addAttribute("regionLat", 0.0);
	        	model.addAttribute("regionLong", 0.0);
	        	model.addAttribute("regionZoom", 0);
    		} else {
	    		RegionEntity region = regionService.getRegion(regionName);
	    		model.addAttribute("regionId", region.getId());
	        	model.addAttribute("regionName", region.getName());
	        	model.addAttribute("regionLat", region.getLatitude());
	        	model.addAttribute("regionLong", region.getLongitude());
	        	model.addAttribute("regionZoom", region.getZoom());
    		}
    	} catch (RegionNotFoundException e) {
    		throw new ObjectNotFoundException(e);
    	}
    }

    @RequestMapping("/")
    public String getIndex(Model model, HttpServletRequest request, @RequestParam(value="gymid", required=false) Long gymId) {
    	addRegionInfo(model, "none");
    	return getIndexWithGym(model, request, gymId);
    }

    @RequestMapping("/{regionName}")
    public String getIndex(Model model, HttpServletRequest request, @PathVariable String regionName,
    		@RequestParam(value="gymid", required=false) Long gymId) {

    	addRegionInfo(model, regionName);
    	return getIndexWithGym(model, request, gymId);
    }

}
