package com.clumsy.gymbadger.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clumsy.gymbadger.data.BossDao;
import com.clumsy.gymbadger.data.GymExportDao;
import com.clumsy.gymbadger.data.GymSummaryDao;
import com.clumsy.gymbadger.entities.UserEntity;
import com.google.common.collect.Lists;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Service
public class ExportService {

	@Autowired
	private GymService gymService;
	
	@Autowired
	private PokemonService pokemonService;
	
	public byte[] exportToCsv(final UserEntity user, final List<Long> ids, final List<Long> areas, final String sort) throws GymNotFoundException, ExportException {
		// Query the gyms we need to export
		List<GymSummaryDao> gyms = null;
		if (ids != null && ids.size() != 0) {
		    gyms = gymService.getGymSummariesById(user.getId(), ids);
		} else {
		    gyms = gymService.getGymSummariesByArea(user.getId(), areas);
		}
		
		// Query all raid bosses
		final List<BossDao> bosses = pokemonService.getAllRaidBosses();
		final Map<Long, BossDao> bossMap = bosses.stream().collect(Collectors.toMap(BossDao::getId, x-> x));
		
		// Build the export data 
		final List<GymExportDao> gymExports = Lists.newArrayList(Lists.transform(gyms, gym -> {
			// Lookup the pokemon caught from the raid boss list
			String pokemonName = "";
			if (gym.getPokemonId() != null) {
				if (bossMap.get(gym.getPokemonId()) != null) {
					pokemonName = bossMap.get(gym.getPokemonId()).getText();
				}
			}
			// Convert to an export object
			final GymExportDao dao = GymExportDao.fromGymSummary(gym, pokemonName);	
			return dao;
		}));

		final ExportHeaders<GymExportDao> strategy = new ExportHeaders<GymExportDao>();
        strategy.setType(GymExportDao.class);     	

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(baos);
		final StatefulBeanToCsvBuilder<GymExportDao> builder = new StatefulBeanToCsvBuilder<GymExportDao>(writer);
		final StatefulBeanToCsv<GymExportDao> beanWriter = builder.withMappingStrategy(strategy).build();
	    try {
			beanWriter.write(gymExports);
			writer.close();
			return baos.toByteArray();
		} catch (CsvDataTypeMismatchException e) {
			throw new ExportException(e);
		} catch (CsvRequiredFieldEmptyException e) {
			throw new ExportException(e);
		} catch (IOException e) {
			throw new ExportException(e);
		}    
	}
}
