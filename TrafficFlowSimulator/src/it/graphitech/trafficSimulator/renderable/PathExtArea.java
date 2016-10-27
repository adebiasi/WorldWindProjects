package it.graphitech.trafficSimulator.renderable;

import gov.nasa.worldwind.geom.Position;

/**
 * This class extends the PathExt class with the id that identifies the area of
 * the path and a flag that indicates if the area is to avoid
 * 
 * @author a.debiasi
 * 
 */
public class PathExtArea extends PathExt {

	private int area;
	private boolean isDangerArea = false;

	public PathExtArea(Iterable<? extends Position> positions) {
		super(positions);
		// TODO Auto-generated constructor stub
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public boolean isDangerArea() {
		return isDangerArea;
	}

	public void setDangerArea(boolean isDangerArea) {
		this.isDangerArea = isDangerArea;
	}

}
