package it.graphitech.trafficSimulator.renderable;

import gov.nasa.worldwind.geom.Position;

/**
 * 
 * Class extending the World Wind Path class The information added are: road id,
 * road name, one-way status, and road length
 */

public class PathExt extends Path_v2 {
	private String id;
	private String name;
	private boolean oneway;
	private double length;

	/**
	 * 
	 * @return the road ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            the road ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return the road name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return the one-way status
	 */
	public boolean isOneway() {
		return oneway;
	}

	/**
	 * 
	 * @param oneway
	 */
	public void setOneway(boolean oneway) {
		this.oneway = oneway;
	}

	/**
	 * 
	 * @return the road length
	 */
	public double getLength() {
		return this.length;
	}

	/**
	 * 
	 * @param length
	 *            the length of the road
	 */
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * Constructor
	 * 
	 * @param positions
	 *            the positions determining the path
	 */
	public PathExt(Iterable<? extends Position> positions) {
		super(positions);
	}
}
