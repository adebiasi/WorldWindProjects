package it;


import it.entities.Entry;
import it.entities.Node;
import it.layers.CurvesLayer;
import it.rendObjects.curve.CubicSplinePolyline;
import it.rendObjects.curve.RenderableControlPoints;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GL;

import jogamp.nativewindow.NWJNILibLoader;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Cylinder;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.terrain.SectorGeometry;
import gov.nasa.worldwind.util.Logging;

public class GeneratorOfRenderableObjects{
	
	private WorldWindow wwd;
	//static int numControlPoints =6;
		
	public GeneratorOfRenderableObjects(
			WorldWindow wwd, 
			HashMap<String, Node> nodes,
			ArrayList<String> entries, 
			HashMap<String, Entry> cities, 
			String state,String name){
		this.wwd = wwd;
		
		
     
        
		CurvesLayer layer = new CurvesLayer(nodes);
        layer.setName(name);
        layer.setEnabled(false);
        
        //RenderableLayer layer = new RenderableLayer();
       // layer.setName("arcs over the globe");
        layer.setPickEnabled(false);

        for(int i=0;i<entries.size();i++){
        	//System.out.println(i);
        	
        	String city = entries.get(i);
        	
        	Entry entry = cities.get(city);
        
        
            
        	if((entry.getState().compareToIgnoreCase(state) == 0)||(state.compareToIgnoreCase("all")==0)){
        		
        	
        		
        		if(entry.getFrom().compareTo(entry.getTo()) != 0){
        	
        			
        			RenderableControlPoints renderableControlPoints = new RenderableControlPoints();
        			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
        			double distance = distance(entry.getFrom_lat(),entry.getFrom_lon(), entry.getTo_lat(), entry.getTo_lon());
        			
        			renderableControlPoints.setControlPointPosition(setControlPointsOverGlobe(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),distance));
        			renderableControlPoints.calculatePoints();
        			
        			CubicSplinePolyline curve = new CubicSplinePolyline(renderableControlPoints,layer);
        			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
        			
        			/*
        			Position origin = Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon());
        			Position dest = Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon());        			
        			curve.setOriginalOriginPosition(origin);
        			curve.setOriginalDestPosition(dest);
        			*/
        			curve.setIdOrigin(entry.getFrom());
        			curve.setIdDest(entry.getTo());
        			
        			/*
        			layer.addNodexWithIndex(entry.getFrom());
        			layer.addNodexWithIndex(entry.getTo());
        			*/
        			layer.addRenderable(curve);
        		}
        	
        	}
        }
    
        
     
    System.out.println("NUM CONNECTIONS: "+layer.getNumRenderables());
    insertBeforeCompass(this.wwd, layer);
	}


	/*
	public static void  GeneratorOfRenderableObjectsOnFlatMap(WorldWindow wwd, ArrayList<String> entries, HashMap<String, Entry> cities, String state,String name){
		//this.wwd = wwd;
		
	
        
        CurvesLayer layerOnFlatMap = new CurvesLayer(cities);
        layerOnFlatMap.setName(name);
        layerOnFlatMap.setEnabled(false);
        
        //RenderableLayer layer = new RenderableLayer();
       // layer.setName("arcs over the globe");
        layerOnFlatMap.setPickEnabled(false);

        for(int i=0;i<entries.size();i++){
        	//System.out.println(i);
        	
        	String city = entries.get(i);
        	
        	Entry entry = cities.get(city);
        
        
            
        	if((entry.getState().compareToIgnoreCase(state) == 0)||(state.compareToIgnoreCase("all")==0)){
        		
        	
        		
        		if(entry.getFrom().compareTo(entry.getTo()) != 0){
        	
        			
        			RenderableControlPoints rendCPOver2DMap = new RenderableControlPoints();
        			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
        			double distance = distance(entry.getFrom_lat(),entry.getFrom_lon(), entry.getTo_lat(), entry.getTo_lon());
        			
        			rendCPOver2DMap.setControlPointPosition(setControlPointsOver2DMap(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),distance));
        			
        			rendCPOver2DMap.calculatePointsOver2DMap();
        			CubicSplinePolyline curveOver2DMap = new CubicSplinePolyline(rendCPOver2DMap,layerOnFlatMap);
        			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
        			curveOver2DMap.setFrom(entry.getFrom());
        			curveOver2DMap.setTo(entry.getTo());
        			
        		
        			layerOnFlatMap.addRenderable(curveOver2DMap);
        		}
        	
        	}
        }
  
        
      
    System.out.println("NUM CONNECTIONS: "+layerOnFlatMap.getNumRenderables());
    insertBeforeCompass(wwd, layerOnFlatMap);
	}
	
	  */
	
	
	
	
	


	
	
	
	public static final double distance(double lat1, double lon1, double lat2, double lon2)
	{
	    double theta = lon1 - lon2;
	    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	    dist = Math.acos(dist);
	    dist = rad2deg(dist);
	  
	    dist = dist * 60 * 1.1515;
	    dist = dist * 1609.344;
	 //   dist=dist*Earth.WGS84_EQUATORIAL_RADIUS;
	     
	
	     
	    return (dist);
	}
	
	/**
	 * <p>This function converts decimal degrees to radians.</p>
	 *
	 * @param deg - the decimal to convert to radians
	 * @return the decimal converted to radians
	 */
	private static final double deg2rad(double deg)
	{
	    return (deg * Math.PI / 180.0);
	}
	 
	/**
	 * <p>This function converts radians to decimal degrees.</p>
	 *
	 * @param rad - the radian to convert
	 * @return the radian converted to decimal degrees
	 */
	private static final double rad2deg(double rad)
	{
	    return (rad * 180 / Math.PI);
	}
	
	
	public  static ArrayList<Position> setControlPointsOverGlobe(Position source, Position destination,double distance){

//double distance =2000000;
			ArrayList<Position> points = new ArrayList<Position>();
			
			//double value = Math.floor(Math.random() * 20)/100;
			
			for (int i = 0; i < SharedVariables.numControlPoints; i++)
			{
				double interpolation = (double) i / (SharedVariables.numControlPoints-1);
				
				double arcHeight=(distance/4)*Math.sin(Math.PI*interpolation);
				
			//	System.out.println("arcHeight: "+arcHeight);
				//double arcHeight = (0.5 - interpolation) * 2.0;
				//arcHeight = 1.0 - (arcHeight * arcHeight);
				Position p = Position.interpolateGreatCircle( interpolation, source, destination );
				//p = new Position(p.latitude , p.longitude, arcHeight * distance * (0.1 + value));
				p = new Position(p.latitude , p.longitude, arcHeight);
				points.add(p);
			}
			
		
			
			return(points);
	}
	
	
	public  static ArrayList<Position> setControlPointsOverGlobeWithAltitude(Position source, Position destination,double distance){

		//double distance =2000000;
					ArrayList<Position> points = new ArrayList<Position>();
					
					//double value = Math.floor(Math.random() * 20)/100;
					
					for (int i = 0; i < SharedVariables.numControlPoints; i++)
					{
						double interpolation = (double) i / (SharedVariables.numControlPoints-1);
						
						double arcHeight=(distance/4)*Math.sin(Math.PI*interpolation);
						
						double deltaElev = destination.elevation-source.elevation;
						
					//	double currElev = deltaElev*interpolation;
						double currElev = source.elevation+deltaElev*interpolation;
					//	double currElev = 0;
						
					//	System.out.println("arcHeight: "+arcHeight);
						//double arcHeight = (0.5 - interpolation) * 2.0;
						//arcHeight = 1.0 - (arcHeight * arcHeight);
						Position p = Position.interpolateGreatCircle( interpolation, source, destination );
						//p = new Position(p.latitude , p.longitude, arcHeight * distance * (0.1 + value));
						p = new Position(p.latitude , p.longitude, arcHeight+currElev);
						points.add(p);
					}
					
				
					
					return(points);
			}
	
	
	public static ArrayList<Position> setControlPointsOver2DMap(Position source, Position destination,double distance){

		//double distance =2000000;
					ArrayList<Position> points = new ArrayList<Position>();
					
					//double value = Math.floor(Math.random() * 20)/100;
					
					for (int i = 0; i < SharedVariables.numControlPoints; i++)
					{
						double interpolation = (double) i / (SharedVariables.numControlPoints-1);
						
						double arcHeight=(distance/2)*Math.sin(Math.PI*interpolation);
						
					//	System.out.println("arcHeight: "+arcHeight);
						//double arcHeight = (0.5 - interpolation) * 2.0;
						//arcHeight = 1.0 - (arcHeight * arcHeight);
						Position p = Position.interpolate( interpolation, source, destination );
						//p = new Position(p.latitude , p.longitude, arcHeight * distance * (0.1 + value));
						p = new Position(p.latitude , p.longitude, arcHeight);
						points.add(p);
					}
					
				
					
					return(points);
			}
	

	

	public static void insertBeforeCompass(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof CompassLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }



}
