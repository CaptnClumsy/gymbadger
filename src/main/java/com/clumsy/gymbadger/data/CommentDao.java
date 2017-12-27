package com.clumsy.gymbadger.data;

import java.util.Date;

import com.clumsy.gymbadger.entities.CommentEntity;

import lombok.Data;

@Data
public class CommentDao {

	private Long id;
	private Date createdate;
	private Long gymid;
	private Long userid;
	private String displayname;
	private String text;
	private Boolean ispublic;
	
	public CommentDao(final Long id, final Date createDate, final Long gymId, final Long userId, final String displayName, final String text, final Boolean ispublic) {
		this.id=id;
		this.createdate=createDate;
		this.gymid=gymId;
		this.userid=userId;
		this.displayname=displayName;
		this.text=text;
		this.ispublic=ispublic;
	}

	public static CommentDao fromCommentEntity(CommentEntity comment) {
		return new CommentDao(comment.getId(), comment.getCreateDate(), comment.getGym().getId(),
				comment.getUser().getId(), comment.getUser().getDisplayName(),
				comment.getComment(), comment.getIsPublic());
	}
}
