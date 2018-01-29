package com.clumsy.gymbadger.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.UserBadgeHistoryEntity;

@Repository
public interface UserBadgeHistoryRepo extends JpaRepository<UserBadgeHistoryEntity, Long> {

}

