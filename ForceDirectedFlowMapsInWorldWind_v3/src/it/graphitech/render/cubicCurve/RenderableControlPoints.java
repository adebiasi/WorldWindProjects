package it.graphitech.render.cubicCurve;

import java.awt.Color;
import java.util.ArrayList;

public class RenderableControlPoints {

	public ArrayList<gov.nasa.worldwind.geom.Position> positions = new ArrayList<gov.nasa.worldwind.geom.Position>();;
	public ArrayList<String> idNodes= new ArrayList<String>();
	public ArrayList<Boolean> toRender= new ArrayList<Boolean>();
	
	ArrayList<Color> colors = new ArrayList<Color>();
	
	public ArrayList<Double> magnitudes = new ArrayList<Double>();
	ArrayList<Double> shifts = new ArrayList<Double>();
	ArrayList<Double> prevMagnitudes = new ArrayList<Double>();
	/*
	public void addControlPoint(gov.nasa.worldwind.geom.Position position, boolean toRender, double magnitude){
		this.positions.add(position);
		this.toRender.add(toRender);
		this.magnitudes.add(magnitude);
	}
	*/
	public void addControlPoint(gov.nasa.worldwind.geom.Position position, double magnitude,String idNode,Color color,double shift,double prevMagnitude){
		this.positions.add(position);
		this.toRender.add(false);
		this.magnitudes.add(magnitude);
		this.idNodes.add(idNode);
		this.colors.add(color);
		this.shifts.add(shift);
		this.prevMagnitudes.add(prevMagnitude);
	}
	
	public double getMagnitude(){
		return this.magnitudes.get(0);
	}
	
	public String getLeafId(){
		return this.idNodes.get(0);
	}
	
	/*
	public void addColor(Color color){
		this.colors.add(color);
	}
	*/
}
