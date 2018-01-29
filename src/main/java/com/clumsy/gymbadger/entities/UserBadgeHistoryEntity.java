package com.clumsy.gymbadger.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.clumsy.gymbadger.data.GymBadgeStatus;

import lombok.Data;

@Data
@Entity
@Table(name = "user_badge_history")
public class UserBadgeHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "badge_status")
	@Enumerated(EnumType.ORDINAL)
    private GymBadgeStatus badgeStatus;

}
