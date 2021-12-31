package com.clumsy.gymbadger.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clumsy.gymbadger.data.RegionDao;
import com.clumsy.gymbadger.entities.RegionEntity;
import com.clumsy.gymbadger.repos.RegionRepo;
import com.google.common.collect.Lists;

@Service
public class RegionService {

	private final RegionRepo regionRepo;

	@Autowired
	RegionService(final RegionRepo regionRepo) {
		this.regionRepo = regionRepo;
	}

	@Transactional(readOnly = true)
	public List<RegionDao> getAllRegions() {
		List<RegionEntity> regionList = regionRepo.findAllByOrderByDisplayNameAsc();
		return Lists.newArrayList(Lists.transform(regionList, region -> {
			final RegionDao dao = RegionDao.fromRegionEntity(region);
			return dao;
        }));
	}	

	@Transactional(readOnly = true)
	public RegionEntity getRegion(final Long id) throws RegionNotFoundException {
		final Optional<RegionEntity> entity = regionRepo.findById(id);
		if (!entity.isPresent()) {
			throw new RegionNotFoundException("Unable to query region");
		}
		return entity.get();
	}

	@Transactional(readOnly = true)
	public RegionEntity getRegion(String regionname) throws RegionNotFoundException {
		final RegionEntity entity = regionRepo.findOneByName(regionname);
		if (entity == null) {
			throw new RegionNotFoundException("Unable to query region");
		}
		return entity;
	}

}
