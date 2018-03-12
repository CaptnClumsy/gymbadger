package com.clumsy.gymbadger.data;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class FavouritesDao {
	private Date since;
	private Long total;
	private List<FavouriteDao> favourites;
}
