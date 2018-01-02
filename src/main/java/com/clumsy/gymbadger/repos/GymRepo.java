package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.GymEntity;
 
@Repository
public interface GymRepo extends JpaRepository<GymEntity, Long> {
	@Query("SELECT t FROM GymEntity t WHERE t.deleted = false ORDER BY t.name ASC")
	List<GymEntity> findAllNotDeleted();
}