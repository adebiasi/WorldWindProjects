package it.graphitech.trafficSimulator.entities;

import it.graphitech.trafficSimulator.renderable.PathExtArea;
import it.graphitech.trafficSimulator.renderable.car.Car;

import java.util.Vector;

/**
 * Class containing information about a segment of the graph, it contains all
 * the information the the segment "path"
 * 
 * @author a.debiasi
 * 
 */
public class SegmentInfo {

	public PathExtArea path; // Correspondent path of the network
	public PathExtArea analysisPath; // Correspondent path of the network that
										// is extruded for the visual analysis
	public double length; // Segment length
	public int counter; // Cars counter
	public int total_counter; // Cars counter
	public Vector<Car> cars; // List of cars in the segment

	public SegmentInfo(PathExtArea path, PathExtArea analysisPath, double length) {
		this.path = path;
		this.analysisPath = analysisPath;
		this.length = length;
		this.counter = 0;
		this.total_counter = 0;
		this.cars = new Vector<Car>();
	}
}
