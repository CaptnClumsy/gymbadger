package com.clumsy.gymbadger.data;

import java.util.List;

import lombok.Data;

@Data
public class CommentListDao {
	private Boolean loggedin;
	private List<CommentDao> comments;
}
