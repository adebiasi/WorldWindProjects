package it.graphitech.trafficSimulator.entities;

import java.util.ArrayList;

import gov.nasa.worldwind.geom.Position;

/**
 * represent a destination area, extends the object Area
 * 
 * @author a.debiasi
 * 
 */
public class DestinationArea extends Area {

	public DestinationArea(ArrayList<Position> positions) {
		super(positions);
	}

	// is the departure area associated to this area
	public Area departureArea;

	public Area getDepartureArea() {
		return departureArea;
	}

	public void setDepartureArea(Area departureArea) {
		this.departureArea = departureArea;
	}
}
