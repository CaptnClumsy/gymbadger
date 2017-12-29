package com.clumsy.gymbadger.entities;

import java.util.Date;

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
@Table(name = "comments")
public class CommentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "createdate")
	private Date createDate;

	@JoinColumn(name = "gymid")
	@ManyToOne
	private GymEntity gym;
	
	@JoinColumn(name = "userid")
	@ManyToOne
	private UserEntity user;
	
	@Column(name = "public")
	private Boolean isPublic;
	
	@Column(name = "comment")
	private String comment;
}
