package it.graphitech.render;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.airspaces.AirspaceAttributes;

public class RenderableNode extends 
//CappedCylinder{
PartialCappedCylinder{

	String idNode;
	
	public RenderableNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RenderableNode(String id) {
		super();
		idNode=id;
		// TODO Auto-generated constructor stub
	}

	public String getIdNode() {
		return idNode;
	}

	
	
}
