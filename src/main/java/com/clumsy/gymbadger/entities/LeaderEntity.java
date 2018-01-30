package com.clumsy.gymbadger.entities;

import com.clumsy.gymbadger.data.GymBadgeStatus;

public interface LeaderEntity {
	Long getId();
	String getdisplayName();
    Integer getBadges();
    GymBadgeStatus getStatus();
}
