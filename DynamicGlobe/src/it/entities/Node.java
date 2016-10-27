package it.entities;

import gov.nasa.worldwind.geom.Position;

public class Node {
	
	public Node(Position originalPosition, String name) {
		super();
		this.originalPosition = originalPosition;
		currPosition = originalPosition;
		this.name = name;
	}
	
	Position originalPosition;
	Position currPosition;
	boolean isDeformed;
	boolean mustBeDeformed;
	String name;
	double distanceFromCamera;
	boolean isOnExistingSlice;
	boolean isVisible;
	//boolean isChecked;
	/*
	public boolean setAlreadyChecked(boolean isChecked) {
		return this.isChecked=isChecked;
	}
	*/
	public boolean setMustBeDeformed(boolean mustBeDeformed) {
		return this.mustBeDeformed=mustBeDeformed;
	}
	/*
	public boolean isAlreadyChecked() {
		return isChecked;
	}
	*/
	public boolean mustBeDeformed() {
		return mustBeDeformed;
	}
	
	public double getDistanceFromCamera() {
		return distanceFromCamera;
	}

	public void setDistanceFromCamera(double distance){
		this.distanceFromCamera = distance;
	}
	
	public Position getOriginalPosition() {
		return originalPosition;
	}
	
	public void setOriginalPosition(Position originalPosition) {
		this.originalPosition = originalPosition;
	}
	
	public Position getCurrPosition() {
		return currPosition;
	}
	
	public void setCurrPosition(Position currPosition) {
		this.currPosition = currPosition;
	}
	
	public boolean isDeformed(){
		return isDeformed;
	}
	
	public boolean isVisible(){
		return isVisible;
	}
	
	public void setIsDeformed(boolean isDef){
		isDeformed = isDef;
	}

	public void setIsVisible(boolean isVisible){
		this.isVisible = isVisible;
	}
	
	public String getName() {
		return name;
	}
	
	public void setIsOnExistingSlice(boolean isOnExistingSlice){
		this.isOnExistingSlice=isOnExistingSlice;
	}
	
	public boolean isOnExistingSlice(){
		return isOnExistingSlice;
	}
}
