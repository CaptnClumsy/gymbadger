package com.clumsy.gymbadger.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.RegionDao;
import com.clumsy.gymbadger.services.RegionService;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

	@Autowired
	private RegionService regionService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public List<RegionDao> getRegions() {
		return regionService.getAllRegions();
	}

}
