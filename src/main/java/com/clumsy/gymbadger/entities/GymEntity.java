package com.clumsy.gymbadger.entities;

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
@Table(name = "gyms")
public class GymEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "lat")
	private Double latitude;
	
	@Column(name = "long")
	private Double longitude;

	@Column(name = "park")
	private Boolean park;

	@JoinColumn(name = "areaid")
	@ManyToOne
	private AreaEntity area;
	
	@Column(name = "deleted")
	private Boolean deleted;
	
	@Column(name = "image_url")
	private String imageUrl;
}
