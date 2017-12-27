package com.clumsy.gymbadger.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.CommentDao;
import com.clumsy.gymbadger.entities.CommentEntity;
import com.clumsy.gymbadger.repos.CommentRepo;
import com.google.common.collect.Lists;

@Service
public class CommentService {

	private final CommentRepo commentRepo;
	
	@Autowired
	CommentService(final CommentRepo commentRepo) {
		this.commentRepo = commentRepo;
	}
	
	@Transactional(readOnly = true)
	public List<CommentDao> getAllComments(final Long gymId, final Long userId) {
		List<CommentEntity> commentList = commentRepo.findAllByGym(gymId, userId);
		return Lists.newArrayList(Lists.transform(commentList, comment -> {
			final CommentDao dao = CommentDao.fromCommentEntity(comment);
			return dao;
        }));
	}	
}