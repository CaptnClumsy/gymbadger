package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.CommentEntity;

@Repository
public interface CommentRepo extends JpaRepository<CommentEntity, Long> {
	@Query("SELECT t FROM CommentEntity t WHERE t.gym.id = ?1 AND (t.user.id = ?2 OR t.isPublic = true) ORDER BY t.createDate ASC")
	List<CommentEntity> findAllByGym(Long gymid, Long userid);
}