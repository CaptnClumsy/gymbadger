package com.clumsy.gymbadger.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "regions")
public class RegionEntity {

	@Id
	@Column(name = "id")
	private Long id;
		
	@Column(name = "name")
	private String name;

	@Column(name = "displayname")
	private String displayName;
	
	@Column(name = "init_pos_zoom")
	private Integer zoom;

	@Column(name = "init_pos_lat")
	private Double latitude;
	
	@Column(name = "init_pos_long")
	private Double longitude;

}

