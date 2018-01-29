package com.clumsy.gymbadger.data;

import java.text.SimpleDateFormat;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class GymExportDao {

	@CsvBindByPosition(position = 0)
	private Long id;
	@CsvBindByPosition(position = 1)
	private String name;

	@CsvBindByPosition(position = 2)
	private Double lat;
	@CsvBindByPosition(position = 3)
	private Double lng;

	@CsvBindByPosition(position = 4)
	private String area;
	@CsvBindByPosition(position = 5)
	private Boolean park;
	
	@CsvBindByPosition(position = 6)
	private GymBadgeStatus status;
	
	@CsvBindByPosition(position = 7)
	private String lastRaid;
	
	@CsvBindByPosition(position = 8)
	private String pokemon;
	
	@CsvBindByPosition(position = 9)
	private Boolean caught;

	public GymExportDao() {
	}

	public GymExportDao(final Long id, final String name, final Double lat, final Double lng, final Boolean park, final String area) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.park = park;
		this.area = area;
		this.status = GymBadgeStatus.NONE;
		this.lastRaid = null;
	}

	public static GymExportDao fromGymSummary(final GymSummaryDao gym, final String pokemon) {
		GymExportDao exportDao = new GymExportDao(gym.getId(), gym.getName(), gym.getLat(), gym.getLng(), gym.getPark(), gym.getArea().getName());
		exportDao.setStatus(gym.getStatus());
		if (gym.getLastRaid() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			exportDao.setLastRaid(dateFormat.format(gym.getLastRaid()));
		}
		exportDao.setPokemon(pokemon);
		exportDao.setCaught(gym.getCaught());
		return exportDao;
	}
}
