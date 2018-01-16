package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.LeaderEntity;
import com.clumsy.gymbadger.entities.UserEntity;
 
@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {
	UserEntity findOneByName(String name);
	
	@Query("SELECT u.id AS id, u.displayName AS displayName, COUNT(*) AS badges " +
			"FROM UserEntity u, GymPropsEntity p WHERE p.userId=u.id AND " + 
			"p.badgeStatus=4 AND u.shareData=true " + 
			"GROUP BY u.id ORDER BY badges DESC")
	List<LeaderEntity> findLeaders();
}