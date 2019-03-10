package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.data.Team;
import com.clumsy.gymbadger.entities.LeaderEntity;
import com.clumsy.gymbadger.entities.UserEntity;
 
@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {
	UserEntity findOneByName(String name);
	
	@Query("SELECT u.id AS id, u.displayName AS displayName, p.badgeStatus AS status, COUNT(*) AS badges " +
			"FROM UserEntity u, GymPropsEntity p WHERE p.userId=u.id AND " + 
			"p.badgeStatus=4 AND u.shareData=true " + 
			"GROUP BY u.id, p.badgeStatus ORDER BY badges DESC, id ASC")
	List<LeaderEntity> findLeaders();
	
	@Query("SELECT u.id AS id, u.displayName AS displayName, p.badgeStatus AS status, COUNT(*) AS badges " +
			"FROM UserEntity u, GymPropsEntity p WHERE p.userId=u.id AND " + 
			"p.badgeStatus=4 AND u.shareData=true AND " +
			"p.gymId IN (SELECT g.id FROM GymEntity g WHERE g.area.region = ?1) " +
			"GROUP BY u.id, p.badgeStatus ORDER BY badges DESC, id ASC")
	List<LeaderEntity> findLeadersByRegion(Long region);
	
	@Query("SELECT u.id AS id, u.displayName AS displayName, p.badgeStatus AS status, COUNT(*) AS badges " +
			"FROM UserEntity u, GymPropsEntity p WHERE p.userId=u.id AND " + 
			"u.shareData=true " + 
			"GROUP BY u.id, p.badgeStatus ORDER BY badges DESC, id ASC")
	List<LeaderEntity> findTotals();
	
	@Query("SELECT u.id AS id, u.displayName AS displayName, p.badgeStatus AS status, COUNT(*) AS badges " +
			"FROM UserEntity u, GymPropsEntity p WHERE p.userId=u.id AND " + 
			"u.shareData=true AND " +
			"p.gymId IN (SELECT g.id FROM GymEntity g WHERE g.area.region = ?1) " +
			"GROUP BY u.id, p.badgeStatus ORDER BY badges DESC, id ASC")
	List<LeaderEntity> findTotalsByRegion(Long region);
	
	@Query("SELECT u.id AS id, u.displayName AS displayName, p.badgeStatus AS status, COUNT(*) AS badges " +
			"FROM UserEntity u, GymPropsEntity p WHERE p.userId=u.id AND " + 
			"p.badgeStatus=4 AND u.shareData=true AND u.team=?1 " + 
			"GROUP BY u.id, p.badgeStatus ORDER BY badges DESC, id ASC")
	List<LeaderEntity> findTeamLeaders(Team team);
	
	@Query("SELECT u.id AS id, u.displayName AS displayName, p.badgeStatus AS status, COUNT(*) AS badges " +
			"FROM UserEntity u, GymPropsEntity p WHERE p.userId=u.id AND " + 
			"p.badgeStatus=4 AND u.shareData=true AND u.team=?2 AND " +
			"p.gymId IN (SELECT g.id FROM GymEntity g WHERE g.area.region = ?1) " +
			"GROUP BY u.id, p.badgeStatus ORDER BY badges DESC, id ASC")
	List<LeaderEntity> findTeamLeadersByRegion(Long region, Team team);
}