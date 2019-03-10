package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.AreaEntity;

@Repository
public interface AreaRepo extends JpaRepository<AreaEntity, Long> {
	@Query("SELECT t FROM AreaEntity t WHERE t.region = ?1 ORDER BY t.name ASC")
	List<AreaEntity> findAllByRegionOrderByNameAsc(Long region);
	
	List<AreaEntity> findAllByOrderByNameAsc();
}