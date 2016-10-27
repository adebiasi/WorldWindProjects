package it.graphitech.trafficSimulator.entities;

import it.graphitech.trafficSimulator.renderableManager.AreaManager;

import java.awt.Color;
import java.util.ArrayList;

import gov.nasa.worldwind.geom.Position;

public class Area {

	// the index of the area
	private int index;

	
	// the points that compose the area
	public ArrayList<Position> positions;

	// the middle point of the area, used to create the arc that starts from the
	// departure area
	public Position middlePos;

	// position used from the cars as target destination
	public Position referencePosition;

	// the color that identifies the area
	private Color color;

	public Area(ArrayList<Position> positions) {
		this.positions = positions;
		calculateMiddlePoint();
	}

	/**
	 * takes in input an id and set the color
	 * 
	 * @param index
	 */
	public void setColor(int index) {
		this.index = index;
		color = AreaManager.colors[index];
	}

	/**
	 * calculates the middle position of the area
	 */
	private void calculateMiddlePoint() {
		int size = positions.size();

		double middleLat = 0;
		double middleLon = 0;

		for (int i = 0; i < size; i++) {

			Position p = positions.get(i);
			double lat = p.latitude.degrees;
			double lon = p.longitude.degrees;

			middleLat += lat;
			middleLon += lon;

		}

		middleLat /= size;
		middleLon /= size;

		middlePos = Position.fromDegrees(middleLat, middleLon);
	}

	/**
	 * get the middle position of the area
	 * 
	 * @return
	 */
	public Position getMiddlePos() {
		return middlePos;
	}

	/**
	 * set the middle position of the area
	 * 
	 * @param middlePos
	 */
	public void setMiddlePos(Position middlePos) {
		this.middlePos = middlePos;
	}

	/**
	 * get the color
	 * 
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * set the color
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * get the reference position
	 * 
	 * @return
	 */
	public Position getReferencePosition() {
		return referencePosition;
	}

	/**
	 * set the reference position
	 * 
	 * @param referencePosition
	 */
	public void setReferencePosition(Position referencePosition) {
		this.referencePosition = referencePosition;
	}

	/**
	 * get the index that identifies the area
	 * 
	 * @return
	 */
	public int getIndex() {
		return index;
	}

}