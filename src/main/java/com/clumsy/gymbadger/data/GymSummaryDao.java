package com.clumsy.gymbadger.data;

import java.util.Date;

import com.clumsy.gymbadger.entities.AreaEntity;
import com.clumsy.gymbadger.entities.GymEntity;
import com.clumsy.gymbadger.entities.GymPropsEntity;

import lombok.Data;

@Data
public class GymSummaryDao {

	private Long id;
	private String name;

	private Double lat;
	private Double lng;

	private GymBadgeStatus status;
	
	private Date lastRaid;

	private Boolean park;
	
	private AreaEntity area;
	
	private Long pokemonId;
	
	private Boolean caught;

	private String imageUrl;

	public GymSummaryDao() {
	}

	public GymSummaryDao(final Long id, final String name, final Double lat, final Double lng, final Boolean park,
			final AreaEntity area, final String imageUrl) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.park = park;
		this.area = area;
		this.status = GymBadgeStatus.NONE;
		this.lastRaid = null;
		this.imageUrl=imageUrl;
	}

	public static GymSummaryDao fromGymEntity(final GymEntity gym, final GymPropsEntity props) {
		GymSummaryDao dao = new GymSummaryDao(gym.getId(), gym.getName(), gym.getLatitude(), gym.getLongitude(), 
			gym.getPark(), gym.getArea(), gym.getImageUrl());
		if (props != null) {
			dao.setStatus(props.getBadgeStatus());
			if (props.getLastRaid()!=null) {
		        dao.setLastRaid(props.getLastRaid().getLastRaid());
		        dao.setCaught(props.getLastRaid().getCaught());
		        if (props.getLastRaid().getPokemon()!=null) {
		            dao.setPokemonId(props.getLastRaid().getPokemon().getId());
		        }
			}
		}
		return dao;
	}
}
