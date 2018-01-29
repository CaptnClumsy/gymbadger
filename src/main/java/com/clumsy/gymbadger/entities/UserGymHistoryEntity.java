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

import com.clumsy.gymbadger.data.HistoryType;

import lombok.Data;

@Data
@Entity
@Table(name = "user_gym_history")
public class UserGymHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "datetime")
	private Date dateTime;

	@Column(name = "userid")
	private Long userId;
	
	@Column(name = "gymid")
	private Long gymId;
	
	@Column(name = "historyid")
	private Long historyId;

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private HistoryType type;

}
