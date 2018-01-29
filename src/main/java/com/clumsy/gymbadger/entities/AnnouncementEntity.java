package com.clumsy.gymbadger.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.clumsy.gymbadger.data.AnnouncementType;

import lombok.Data;

@Data
@Entity
@Table(name = "announcements")
public class AnnouncementEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@JoinColumn(name = "userid")
	@ManyToOne
	private UserEntity user;

	@Column(name = "message")
	private String message;

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private AnnouncementType type;

}
