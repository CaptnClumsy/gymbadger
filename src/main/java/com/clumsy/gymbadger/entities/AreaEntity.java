package com.clumsy.gymbadger.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "areas")
public class AreaEntity {

	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;

}