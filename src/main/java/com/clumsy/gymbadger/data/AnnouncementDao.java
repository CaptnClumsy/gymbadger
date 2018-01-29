package com.clumsy.gymbadger.data;

import com.clumsy.gymbadger.entities.UserAnnouncementEntity;

import lombok.Data;

@Data
public class AnnouncementDao {
	private Long id;
	private String name;
	private String message;
	private AnnouncementType type;

	public static AnnouncementDao fromEntity(UserAnnouncementEntity entity) {
		AnnouncementDao dao = new AnnouncementDao();
		dao.setId(entity.getId());
		if (entity.getAnnouncement().getUser().getId()!=0) {
		    dao.setName(entity.getAnnouncement().getUser().getDisplayName());
		}
		dao.setMessage(entity.getAnnouncement().getMessage());
		dao.setType(entity.getAnnouncement().getType());
		return dao;
	}

}
