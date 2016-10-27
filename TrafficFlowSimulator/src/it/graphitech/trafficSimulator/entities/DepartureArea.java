package it.graphitech.trafficSimulator.entities;

import java.util.ArrayList;

import gov.nasa.worldwind.geom.Position;

/**
 * represent a departure area, extends the object Area it contains the list of
 * integer that represents the id of the destination area associated for each
 * vehicle
 * 
 * @author a.debiasi
 * 
 */

public class DepartureArea extends Area {

	public DepartureArea(ArrayList<Position> positions) {
		super(positions);
	}

	// the list of integer that represents the id of the destination area
	// associated for each vehicle
	private ArrayList<Integer> idDestAreaArrayForEachEmitter;

	public ArrayList<Integer> getIdDestAreaArrayForEachEmitter() {
		return idDestAreaArrayForEachEmitter;
	}

	public void setIdDestAreaArrayForEachEmitter(
			ArrayList<Integer> idDestAreaArrayForEachEmitter) {
		this.idDestAreaArrayForEachEmitter = idDestAreaArrayForEachEmitter;
	}
}
