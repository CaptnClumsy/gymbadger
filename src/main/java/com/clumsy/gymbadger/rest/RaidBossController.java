package com.clumsy.gymbadger.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clumsy.gymbadger.data.BossDao;
import com.clumsy.gymbadger.services.PokemonService;

@RestController
@RequestMapping("/api/bosses")
public class RaidBossController {

	@Autowired
	private PokemonService pokemonService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public List<BossDao> getBosses() {
		return pokemonService.getAllRaidBosses();
	}

}
