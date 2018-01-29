package com.clumsy.gymbadger.data;

import java.util.Date;

import com.clumsy.gymbadger.entities.PokemonEntity;
import com.clumsy.gymbadger.entities.UserBadgeHistoryEntity;
import com.clumsy.gymbadger.entities.UserGymHistoryEntity;
import com.clumsy.gymbadger.entities.UserRaidHistoryEntity;

import lombok.Data;

@Data
public class GymHistoryDao implements Comparable<GymHistoryDao> {
	private Long id;
    private Date dateTime;
    private HistoryType type;
    private BossDao pokemon;
    private Boolean caught;
    private GymBadgeStatus status;

	public static GymHistoryDao fromEntities(Object historyEntry, Object historyData) {
		if (!(historyEntry instanceof UserGymHistoryEntity)) {
			return null;
		}
		final GymHistoryDao dao = new GymHistoryDao();
		dao.setId(((UserGymHistoryEntity)historyEntry).getId());
		dao.setDateTime(((UserGymHistoryEntity)historyEntry).getDateTime());
		if ((historyData instanceof UserRaidHistoryEntity)) {
			dao.setType(HistoryType.RAID);
			final PokemonEntity pokemonEntity = ((UserRaidHistoryEntity)historyData).getPokemon();
			if (pokemonEntity!=null) {
			    final BossDao boss = new BossDao(pokemonEntity.getId(), pokemonEntity.getName());
			    dao.setPokemon(boss);
			}
			dao.setCaught(((UserRaidHistoryEntity)historyData).getCaught());
			return dao;
		} else if ((historyData instanceof UserBadgeHistoryEntity)) {
			dao.setType(HistoryType.BADGE);
			dao.setStatus(((UserBadgeHistoryEntity)historyData).getBadgeStatus());
			return dao;
		} else {
			return null;
		}
	}

	@Override
	public int compareTo(GymHistoryDao o) {
		if (o.getId()==id) {
			return 0;
		}
		return o.getDateTime().compareTo(dateTime);
	}
}