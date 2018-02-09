package com.clumsy.gymbadger.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clumsy.gymbadger.entities.UserGymHistoryEntity;

@Repository
public interface UserGymHistoryRepo extends JpaRepository<UserGymHistoryEntity, Long> {
	@Query("SELECT h, r FROM UserGymHistoryEntity h, UserRaidHistoryEntity r WHERE r.id=h.historyId AND h.userId=?1 AND h.gymId=?2 ORDER BY r.lastRaid ASC")
	List<Object> findAllRaidHistory(Long userId, Long gymId);
	@Query("SELECT h, b FROM UserGymHistoryEntity h, UserBadgeHistoryEntity b WHERE b.id=h.historyId AND h.userId=?1 AND h.gymId=?2 ORDER BY h.dateTime ASC")
	List<Object> findAllBadgeHistory(Long userId, Long gymId);
}
