package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.AnnouncementEntity;

@Repository
public interface AnnouncementRepo extends JpaRepository<AnnouncementEntity, Long> {
	List<AnnouncementEntity> findAllByUserId(Long userId);
}
