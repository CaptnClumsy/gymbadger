package com.clumsy.gymbadger.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "user_gym_props")
public class GymPropsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "gymid")
	private Long gymId;

	@Column(name = "userid")
	private Long userId;

	@Column(name = "badge_status")
	@Enumerated(EnumType.ORDINAL)
    private GymBadgeStatus badgeStatus;
	
	@Column(name = "badge_percent")
	private Integer badgePercent;
	
	@Column(name = "last_raid")
	private Date lastRaid;
}
