package it.graphitech.trafficSimulator.entities;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * 
 * Edge extending the basic default weighted one from JGraphT. Information on
 * the two positions of the segment and its lenght are added.
 * 
 */
public class MyEdge extends DefaultWeightedEdge {

	private static final long serialVersionUID = -8848142414618566622L;

	// is the length in meter of the edge that represent a road segment
	private double length;

	public Object getA() {
		return this.getSource();
	}

	public Object getB() {
		return this.getTarget();
	}

	public double getLength() {
		return this.length;
	}

	public void setLength(double length) {
		this.length = length;
	}
}
