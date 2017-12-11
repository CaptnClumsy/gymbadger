package com.clumsy.gymbadger.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.DefaultsEntity;
 
@Repository
public interface DefaultsRepo extends JpaRepository<DefaultsEntity, Long> {
	DefaultsEntity findOneByUserid(Long userId);
}