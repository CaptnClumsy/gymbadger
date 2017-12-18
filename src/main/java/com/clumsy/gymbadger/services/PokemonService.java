package com.clumsy.gymbadger.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.BossDao;
import com.clumsy.gymbadger.entities.PokemonEntity;
import com.clumsy.gymbadger.repos.BossRepo;
import com.google.common.collect.Lists;

@Service
public class PokemonService {

	private final BossRepo bossRepo;

	@Autowired
	PokemonService(final BossRepo bossRepo) {
		this.bossRepo = bossRepo;
	}

	@Transactional(readOnly = true)
	public List<BossDao> getAllRaidBosses() {
		List<PokemonEntity> pokemonList = bossRepo.findAllByRaidBoss(true);
		return Lists.newArrayList(Lists.transform(pokemonList, pokemon -> {
			final BossDao dao = BossDao.fromPoekmonEntity(pokemon);
			return dao;
        }));
	}	

}
