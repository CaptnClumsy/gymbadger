package com.clumsy.gymbadger.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.entities.AreaEntity;
import com.clumsy.gymbadger.repos.AreaRepo;
import com.clumsy.gymbadger.services.Constants;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

	@Autowired
	private AreaRepo areaRepo;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public List<AreaEntity> getAreas(@PathVariable("id") Long region) {
		if (region==Constants.DEFAULT_REGION) {
		    return areaRepo.findAllByOrderByNameAsc();
		} else {
			return areaRepo.findAllByRegionOrderByNameAsc(region);
		}
		
	}
}
