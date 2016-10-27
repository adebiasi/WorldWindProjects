package it.graphitech.trafficSimulator.renderable.car;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.worldwind.geom.Position;
import it.graphitech.trafficSimulator.CustomizableVariables;


/**
 * It is an extenson of the Car class
 * This class contains information about the status of the car, the destination area and the path to follow in order to reach the destination
 * 
 * @author a.debiasi
 *
 */
public class CarWithStatus extends Car{

	/**
	 * the possible states of the car 
	 */
	static public final String REACHING_AREA= "REACHING_AREA";
	static public final String REACHING_PARKING_LOT= "REACHING_PARKING_LOT";
	static public final String NO_DESTINATION= "NO_DESTINATION";
	
	/**
	 * the number of next road the car knows before recalculate the path
	 */
	static public final int pathSize= CustomizableVariables.numStepForRoutingPathCalculation;
	
	String status = REACHING_AREA;
	int area;
	List<Position> path = new ArrayList<Position>();
	
	public CarWithStatus(Position currPos, Position origin,
			Position destination, double speed) {
		super(currPos, origin, destination, speed);		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public List<Position> getPath() {
		return path;
	}

	public void setPath(List<Position> path) {
		this.path = path;
	}

	
}
