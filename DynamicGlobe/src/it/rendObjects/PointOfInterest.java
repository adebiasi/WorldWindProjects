package it.rendObjects;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.airspaces.AirspaceAttributes;
import gov.nasa.worldwind.render.airspaces.SphereAirspace;

public class PointOfInterest extends SphereAirspace{

	Position movedPosition;
	Position originalPosition;
	boolean isOverDeformedSurface;
	
	public PointOfInterest(Position originalPosition) {
		super();
		this.originalPosition=new Position(originalPosition.latitude, originalPosition.longitude, 0);
		
		// TODO Auto-generated constructor stub
	}

	public PointOfInterest(AirspaceAttributes arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public PointOfInterest(LatLon arg0, double arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	
	public void setLocation(Position newPos) {
		// TODO Auto-generated method stub
		if(!isOverDeformedSurface){
			super.setAltitude(originalPosition.getElevation());
			super.setLocation(originalPosition);
		}else{
			//originalPosition=this.getReferencePosition();
			System.out.println("new Pos: "+newPos);
			super.setAltitude(newPos.getElevation());
			super.setLocation(newPos);
			
		}
		
		
	}

	
	
	public Position getOriginalPosition() {
		return originalPosition;
	}

	public void setOriginalPosition(Position originalPosition) {
		this.originalPosition = originalPosition;
	}

	@Override
	public Position getReferencePosition() {
		// TODO Auto-generated method stub
		return super.getReferencePosition();
	}

	public boolean isOverDeformedSurface() {
		return isOverDeformedSurface;
	}

	public void setOverDeformedSurface(boolean isOverDeformedSurface) {
		this.isOverDeformedSurface = isOverDeformedSurface;
	}

	
	
	
	
}
