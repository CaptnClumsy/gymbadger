package com.clumsy.gymbadger.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.UserEntity;
 
@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {
	UserEntity findOneByName(String name);
}