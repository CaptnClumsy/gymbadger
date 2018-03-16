package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.AreaEntity;

@Repository
public interface AreaRepo extends JpaRepository<AreaEntity, Long> {
	List<AreaEntity> findAllByOrderByNameAsc();
}