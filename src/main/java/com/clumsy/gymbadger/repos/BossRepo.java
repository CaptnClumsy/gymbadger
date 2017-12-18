package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.PokemonEntity;

@Repository
public interface BossRepo extends JpaRepository<PokemonEntity, Long> {
	List<PokemonEntity> findAllByRaidBoss(Boolean isBoss);
}
