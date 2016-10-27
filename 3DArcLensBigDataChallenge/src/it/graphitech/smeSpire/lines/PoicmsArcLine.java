package it.graphitech.smeSpire.lines;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;

public class PoicmsArcLine extends MyPolyline
{
	//private int SEGMENTS = 50;
	private int SEGMENTS = 15;
	private int WIDTH = 20;
	
	public PoicmsArcLine(){
	}
	
	public void init(Position source, Position destination, Color color, Globe eg){
		try{
		
			/*
			if (color == null) {
			Random random = new Random();
			setColor( new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()) );
		}
		else {
			setColor(color);
		}
		*/
		double distance = Position.ellipsoidalDistance(source, destination,	Earth.WGS84_EQUATORIAL_RADIUS, Earth.WGS84_POLAR_RADIUS);
		
		LinkedList<Position> points = new LinkedList<Position>();
		double value = Math.floor(Math.random() * 20)/100;
		// System.out.println(value);
		for (int i = 0; i < SEGMENTS; i++)
		{
			double interpolation = (double) i / (SEGMENTS-1);
			double arcHeight = (0.5 - interpolation) * 2.0;
			arcHeight = 1.0 - (arcHeight * arcHeight);
			Position p = Position.interpolate( interpolation, source, destination );
			p = new Position(p.latitude , p.longitude, arcHeight * distance * (0.1 + value));
			points.add(p);
		}
		this.setLineWidth(WIDTH);
		this.setAntiAliasHint(ANTIALIAS_NICEST);
		this.setPositions(points);
		}catch(Exception e){};
		
	}

	public PoicmsArcLine(Position source, Position destination, Color color, int rank , Globe el)
	{
		//this.WIDTH *= rank;
		/*this.WIDTH *= 1;
		if(rank == 1) this.WIDTH = 1;
		if(rank == 2) this.WIDTH = 1;
		if(rank == 3) this.WIDTH = 1;
		if(rank == 5) this.WIDTH *= 3;
		*/
		init(source, destination, color, el);
	}

}
