package com.clumsy.gymbadger.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.UserRaidHistoryEntity;

@Repository
public interface UserRaidHistoryRepo extends JpaRepository<UserRaidHistoryEntity, Long> {

	@Query("select r.id from UserRaidHistoryEntity r, UserGymHistoryEntity h " +
	 " where r.id=h.historyId and " +
	 "h.userId=?1 and " +
	 "h.gymId=?2 and " +
	 "r.id = (select MAX(r1.id) from UserRaidHistoryEntity r1, UserGymHistoryEntity h1 " +
	 "     where r1.id=h1.historyId and " +
	 "     h1.gymId=h.gymId and " +
	 "     h1.userId=h.userId and " +
	 "     r1.lastRaid = (select MAX(r2.lastRaid) from UserRaidHistoryEntity r2, UserGymHistoryEntity h2 " +
	 "     where r2.id=h2.historyId and " +
	 "     h2.gymId=h1.gymId and " +
	 "     h2.userId=h1.userId " +
	 "   ) " +
	 " )")
	 Long findLatestRaid(final Long userid, final Long gymid);
}
