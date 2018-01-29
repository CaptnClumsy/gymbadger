package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.UserAnnouncementEntity;

@Repository
public interface UserAnnouncementRepo extends JpaRepository<UserAnnouncementEntity, Long> {
	List<UserAnnouncementEntity> findAllByUserId(Long userId);
	UserAnnouncementEntity findOneByAnnouncementIdAndUserId(Long id, Long userId);
	List<UserAnnouncementEntity> findAllByAnnouncementId(Long id);
}
