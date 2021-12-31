package com.clumsy.gymbadger.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.CommentDao;
import com.clumsy.gymbadger.entities.CommentEntity;
import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.entities.UserEntity;
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

	@Transactional
	public CommentDao createComment(final UserEntity user, final GymEntity gym, final String comment, final Boolean isPublic) {
		CommentEntity newComment = new CommentEntity();
		newComment.setCreateDate(new Date());
		newComment.setUser(user);
		newComment.setComment(comment);
		newComment.setIsPublic(isPublic);
		newComment.setGym(gym);
		CommentEntity savedComment = commentRepo.save(newComment);
		return CommentDao.fromCommentEntity(savedComment);
	}

	@Transactional
	public void deleteComment(final UserEntity user, final Long commentId) throws CommentNotFoundException, AccessControlException {
		Optional<CommentEntity> comment = commentRepo.findById(commentId);
		if (!comment.isPresent()) {
			throw new CommentNotFoundException("Unable to find specified comment");
		}
		if (comment.get().getUser().getId()!=user.getId()) {
			throw new AccessControlException("User was not author of the comment");
		}
		commentRepo.deleteById(commentId);
	}	
}