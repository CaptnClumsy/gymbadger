package com.clumsy.gymbadger.services;

import com.opencsv.bean.ColumnPositionMappingStrategy;

public class ExportHeaders<T> extends ColumnPositionMappingStrategy<T> {
    private static final String[] HEADER = new String[]{"ID", "Gym", "Latitude", "Longitude", "Area", "Is Park",
    		"Gym Badge", "Last Raided", "Raid Boss", "Caught"};

    @Override
    public String[] generateHeader() {
        return HEADER;
    }
}
