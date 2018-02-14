package com.clumsy.gymbadger.data;

import com.clumsy.gymbadger.entities.PokemonEntity;

import lombok.Data;

@Data
public class BossDao {

	private Long id;
	private String text;
	
	public BossDao() {
	}

	public BossDao(final Long id, final String name) {
		this.id=id;
		this.text=name;
	}

	public static BossDao fromPoekmonEntity(PokemonEntity pokemon) {
		return new BossDao(pokemon.getId(), pokemon.getName());
	}

}
