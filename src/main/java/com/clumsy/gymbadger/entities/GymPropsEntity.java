package com.clumsy.gymbadger.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.clumsy.gymbadger.data.GymBadgeStatus;

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

	@JoinColumn(name = "lastraidid")
	@OneToOne
	UserRaidHistoryEntity lastRaid;

}