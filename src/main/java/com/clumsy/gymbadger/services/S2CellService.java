package com.clumsy.gymbadger.services;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.clumsy.gymbadger.data.S2CellDao;
import com.clumsy.gymbadger.data.S2PointDao;
import com.google.common.geometry.S2Cap;
import com.google.common.geometry.S2Cell;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2RegionCoverer;

@Service
public class S2CellService {

	private static double kEarthCircumferenceMeters = 1000 * 40075.017;

	private static double EarthMetersToRadians(double meters) {
	  return (2 * Math.PI) * (meters / kEarthCircumferenceMeters);
	}

	public ArrayList<S2CellDao> getCells(Integer level, Double lat, Double lng) {
		
	    double radius = EarthMetersToRadians(1000);
	    
		S2Cap cap = S2Cap.fromAxisHeight(
	    	      S2LatLng.fromDegrees(lat, lng).normalized().toPoint(),
	    	      (radius * radius) / 2);

	    ArrayList<S2CellId> cellIds = new ArrayList<>();
	    S2RegionCoverer.getSimpleCovering(cap, S2LatLng.fromDegrees(lat, lng).normalized().toPoint(), level, cellIds);

	    ArrayList<S2CellDao> daos = new ArrayList<>(cellIds.size());
	    for (S2CellId cellId : cellIds) {
	    	S2Cell cell = new S2Cell(cellId);
	    	S2PointDao[] pos = new S2PointDao[4];
	    	
	    	S2LatLng s2LatLng = new S2LatLng(cell.getVertex(0)); // Bottom right
	    	S2PointDao point = new S2PointDao(s2LatLng.latDegrees(), s2LatLng.lngDegrees());
	    	pos[2] = point;
	    	
	    	s2LatLng = new S2LatLng(cell.getVertex(1)); // Top right
	    	point = new S2PointDao(s2LatLng.latDegrees(), s2LatLng.lngDegrees());
	    	pos[1] = point;
	    	
	    	s2LatLng = new S2LatLng(cell.getVertex(2)); // Top left
	    	point = new S2PointDao(s2LatLng.latDegrees(), s2LatLng.lngDegrees());
	    	pos[0] = point;
	    	
	    	s2LatLng = new S2LatLng(cell.getVertex(3)); // Bottom left
	    	point = new S2PointDao(s2LatLng.latDegrees(), s2LatLng.lngDegrees());
	    	pos[3] = point;
	    	
	    	S2CellDao cellDao = new S2CellDao();
	    	cellDao.setPos(pos);
	    	daos.add(cellDao);
	    }
		return daos;
	}

}
