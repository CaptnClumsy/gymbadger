package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.GymEntity;
 
@Repository
public interface GymRepo extends JpaRepository<GymEntity, Long> {
	
	@Query("SELECT t FROM GymEntity t WHERE t.deleted = false ORDER BY t.name ASC")
	List<GymEntity> findAllGyms();
	
	@Query("SELECT t FROM GymEntity t WHERE t.deleted = false AND t.area.id IN (SELECT t1.id FROM AreaEntity t1 WHERE t1.region = ?1) ORDER BY t.name ASC")
	List<GymEntity> findAllGymsByRegion(Long region);

	@Query("SELECT t FROM GymEntity t WHERE t.deleted = false AND t.id IN (?1) ORDER BY t.name ASC")
	List<GymEntity> findGyms(List<Long> ids);
	
	@Query("SELECT t FROM GymEntity t WHERE t.deleted = false AND t.area.id IN ?1 ORDER BY t.name ASC")
	List<GymEntity> findGymsByArea(List<Long> areas);
}