package com.clumsy.gymbadger.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.clumsy.gymbadger.data.Team;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;

	@Column(name = "displayname")
	private String displayName;

	@Column(name = "admin")
	private Boolean admin;
	
	@Column(name = "sharedata")
	private Boolean shareData;

	@Column(name = "team")
	@Enumerated(EnumType.ORDINAL)
	private Team team;
}
