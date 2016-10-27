package it.graphitech.input;

import it.graphitech.objects.Position;

public class FlowSource {

	public double latStart,lonStart,latDest,lonDest,flowMagnitude;
	public double flowMagnitudeR,flowMagnitudeG,flowMagnitudeB;
	
	double angleFromOrigin;
	int angleIndex;
	int maxAngleIndex;
	
	String sourceData;
String name;
String origin;
	public FlowSource(String sourceData) {
		super();
		this.sourceData = sourceData;
		String[] data=sourceData.split(",");
		
		latStart=Double.valueOf(data[0]);
		lonStart=Double.valueOf(data[1]);
		latDest=Double.valueOf(data[2]);
		lonDest=Double.valueOf(data[3]);
		flowMagnitude=Double.valueOf(data[4]);
		origin=latStart+" , "+lonStart;
	}

	public FlowSource(double latStart, double lonStart, double latDest,
			double lonDest, double flowMagnitude) {
		super();
		this.latStart = latStart;
		this.lonStart = lonStart;
		this.latDest = latDest;
		this.lonDest = lonDest;
		this.flowMagnitude = flowMagnitude;
		setAngleFromOrigin();
		origin=latStart+" , "+lonStart;
	}
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMagnitudeRGB(double r,double g, double b) {
		flowMagnitudeR=r;
		flowMagnitudeG=g;
		flowMagnitudeB=b;
		
		System.out.println("R: "+flowMagnitudeR+" G: "+flowMagnitudeG+"B: "+flowMagnitudeB);
		
	}
	
	public double[] getStartingPoint(){
		 double[] res = {latStart,lonStart}; 
		 return res;
	}
	
	
	
	
	private void setAngleFromOrigin() {
	    
		double y=latStart;
		double x=lonStart;
		double targetY=latDest;
		double targetX=lonDest;
		
		double angle = (double) Math.toDegrees(Math.atan2(targetX - x, targetY - y));

	    if(angle < 0){
	        angle += 360;
	    }

	    angleFromOrigin = angle;
	}
	
	public double getAngleFromOrigin() {
		return angleFromOrigin;
	}

	public int getAngleIndex() {
		return angleIndex;
	}

	public void setAngleIndex(int angleIndex) {
		this.angleIndex = angleIndex;
	}

	public int getMaxAngleIndex() {
		return maxAngleIndex;
	}

	public void setMaxAngleIndex(int maxAngleIndex) {
		this.maxAngleIndex = maxAngleIndex;
	}

	public String getOrigin() {
		return origin;
	}

	public double getFlowMagnitudeR() {
		return flowMagnitudeR;
	}

	public double getFlowMagnitudeG() {
		return flowMagnitudeG;
	}

	public double getFlowMagnitudeB() {
		return flowMagnitudeB;
	}
	
	
	
}
