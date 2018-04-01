package com.clumsy.gymbadger.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "user_raid_history")
public class UserRaidHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "last_raid")
	private Date lastRaid;
	
	@JoinColumn(name = "pokemonid")
	@ManyToOne
	private PokemonEntity pokemon;
	
	@Column(name = "caught")
	private Boolean caught;
	
	@Column(name = "shiny")
	private Boolean shiny;

}