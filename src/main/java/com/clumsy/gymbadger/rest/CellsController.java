package com.clumsy.gymbadger.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.S2CellDao;
import com.clumsy.gymbadger.services.S2CellService;

@RestController
@RequestMapping("/api/cells")
public class CellsController {

	@Autowired
	private S2CellService cellService;

	@RequestMapping(value = "/{level}/{lat}/{lng}/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<S2CellDao> getCells(Principal principal, @PathVariable("level") Integer level,
    	@PathVariable("lat") Double lat, @PathVariable("lng") Double lng) {
    	ArrayList<S2CellDao> cells = cellService.getCells(level, lat, lng);
    	return cells;
    }
	
}
