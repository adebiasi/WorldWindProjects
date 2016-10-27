package fluidSimulator;

import java.awt.Point;

public class PointVel {

	java.awt.Point point;
	double uVel;
	double vVel;
	
	
	public PointVel(Point point, double vel, double vel2) {
		super();
		this.point = point;
		uVel = vel;
		vVel = vel2;
	}


	public java.awt.Point getPoint() {
		return point;
	}


	public double getUVel() {
		return uVel;
	}


	public double getVVel() {
		return vVel;
	}
	
	
	
}
