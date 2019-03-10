package com.clumsy.gymbadger.data;

import com.clumsy.gymbadger.entities.RegionEntity;

import lombok.Data;

@Data
public class RegionDao {

	private Long id;
	private String text;
	
	public RegionDao() {
	}

	public RegionDao(final Long id, final String displayname) {
		this.id=id;
		this.text=displayname;
	}

	public static RegionDao fromRegionEntity(RegionEntity region) {
		return new RegionDao(region.getId(), region.getDisplayName());
	}

}
