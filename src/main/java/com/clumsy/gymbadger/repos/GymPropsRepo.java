package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.GymPropsEntity;

@Repository
public interface GymPropsRepo extends JpaRepository<GymPropsEntity, Long> {
	
	GymPropsEntity findOneByUserIdAndGymId(Long userId, Long gymId);
	List<GymPropsEntity> findAllByUserId(Long userId);
	
}
