package it.layers;

import it.entities.Entry;
import it.entities.Node;

import java.util.Collection;
import java.util.HashMap;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;

public class CurvesLayer extends RenderableLayer{

	HashMap<String, Node> nodes;

	public CurvesLayer(HashMap<String, Node> nodes) {
		super();
		this.nodes = nodes;
	}
	
	public Position getOriginalPositionNode(String id){		
		return nodes.get(id).getOriginalPosition();		
	}
	
public Position getCurrPositionNode(String id){		
		return nodes.get(id).getCurrPosition();		
	}

public void setCurrPositionNode(String id,Position newPos){	
	 nodes.get(id).setCurrPosition(newPos);	
}

/*
public void setIsDeformedNode(String id, boolean isDef){
	System.out.println("n:< "+ id);
	Node n = nodes.get(id);
	System.out.println("n .> "+n.getName());
	n.setIsDeformed(isDef);
}
*/
public boolean isDeformedNode(String id){
	return nodes.get(id).isDeformed();
}

public boolean isVisibleNode(String id){
	return nodes.get(id).isVisible();
}


public Collection<Node> getNodes(){
	return nodes.values();
}

public Node getNode(String id){
	return nodes.get(id);
}


public double getDistanceFromCamera(String id){
	return nodes.get(id).getDistanceFromCamera();
}
/*
public boolean isAlreadyChecked(String id){
	return nodes.get(id).isAlreadyChecked();
}

public boolean setAlreadyChecked(String id, boolean b){
	return nodes.get(id).setAlreadyChecked(b);
}
*/
public boolean setMustBeDeformed(String id, boolean b){
	return nodes.get(id).setMustBeDeformed(b);
}

public boolean mustBeDeformed(String id){
	return nodes.get(id).mustBeDeformed();
}

public void setDistanceFromCamera(String id, double distance){
	nodes.get(id).setDistanceFromCamera(distance);
}

public String getName(String id){
	return nodes.get(id).getName();
}
}
