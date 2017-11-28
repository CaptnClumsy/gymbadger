package com.clumsy.gymbadger.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "defaults")
public class DefaultsEntity {

	@Id
	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "init_pos_zoom")
	private Integer zoom;

	@Column(name = "init_pos_lat")
	private Double latitude;
	
	@Column(name = "init_pos_long")
	private Double longitude;

}