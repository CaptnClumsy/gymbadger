package com.clumsy.gymbadger.data;

import lombok.Data;

@Data
public class NewCommentDao {
	private Long gymid;
    private String text;
    private Boolean ispublic;
}
