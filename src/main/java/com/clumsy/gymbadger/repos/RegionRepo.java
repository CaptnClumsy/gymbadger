package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.RegionEntity;

@Repository
public interface RegionRepo extends JpaRepository<RegionEntity, Long> {
	List<RegionEntity> findAllByOrderByDisplayNameAsc();
}
