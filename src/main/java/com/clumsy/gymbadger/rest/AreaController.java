package com.clumsy.gymbadger.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.entities.AreaEntity;
import com.clumsy.gymbadger.repos.AreaRepo;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

	@Autowired
	private AreaRepo areaRepo;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public List<AreaEntity> getAreas() {
		return areaRepo.findAll();
	}
}
