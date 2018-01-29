package com.clumsy.gymbadger.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.UserRaidHistoryEntity;

@Repository
public interface UserRaidHistoryRepo extends JpaRepository<UserRaidHistoryEntity, Long> {

}