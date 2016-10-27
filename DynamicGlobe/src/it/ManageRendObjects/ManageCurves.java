package it.ManageRendObjects;

import it.entities.Route;
import it.rendObjects.curve.CubicSplinePolyline;
import it.rendObjects.curve.RenderableControlPoints;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
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

public class ManageCurves{
	
	private WorldWindow wwd;
	static int numControlPoints =3;
		/*
	public ManageCurves(WorldWindow wwd, HashMap<String, Route> links){
		this.wwd = wwd;
		
    
 
        Iterator<java.util.Map.Entry<String, Route>> it = links.entrySet().iterator();
        
        RenderableLayer layer = new RenderableLayer();
        layer.setName("arcs over the globe");
        layer.setPickEnabled(false);
        
        RenderableLayer layerOnFlatMap = new RenderableLayer();
        layerOnFlatMap.setName("arcs over 2D Map");
        layerOnFlatMap.setPickEnabled(false);
        
	       while(it.hasNext()){
	        	//System.out.println(i);
	        	
	        	
	        	
	        	Route route = (it.next().getValue());
	        	
	        
                
         //  System.out.println("route.getDistance(): "+route.getDistance());
	        	
	        			
	        			RenderableControlPoints rendCPOverGlobe = new RenderableControlPoints();	        			
	        			//rendCPOverGlobe.setControlPointPosition(setControlPointsOverGlobe(Position.fromDegrees(route.getFrom_lat(), route.getFrom_lon()), Position.fromDegrees(route.getTo_lat(), route.getTo_lon()),route.getDistance()));
	        			rendCPOverGlobe.setControlPointPosition(setControlPointsOverGlobe(route.getFrom_pos(), route.getTo_pos(),route.getDistance()));
	        			rendCPOverGlobe.calculatePointsOverGlobe(wwd);
	        			
	        			CubicSplinePolyline curve = new CubicSplinePolyline(rendCPOverGlobe,layer);
	        			//curve.setFrom(route.getFrom());
	        			//curve.setTo(route.getTo());
	        			layer.addRenderable(curve);
	        			
	        			
	        			
	        			
	        			RenderableControlPoints rendCPOver2DMap = new RenderableControlPoints();
	        			
	        			rendCPOver2DMap.setControlPointPosition(setControlPointsOver2DMap(route.getFrom_pos(), route.getTo_pos(),route.getDistance()));
	        			
	        			rendCPOver2DMap.calculatePointsOver2DMap();
	        			
	        			CubicSplinePolyline curveOver2DMap = new CubicSplinePolyline(rendCPOver2DMap);
	        			//curve.setFrom(route.getFrom());
	        			//curve.setTo(route.getTo());
	        			layerOnFlatMap.addRenderable(curveOver2DMap);
	        			
	        		}
	        		
	        	
	      
        
        System.out.println("NUM CONNECTIONS: "+layer.getNumRenderables());
        insertBeforeCompass(wwd, layer);
        insertBeforeCompass(wwd, layerOnFlatMap);
	}

	*/
	
	public ArrayList<Position> setControlPointsOverGlobe(Position source, Position destination,double distance){

//double distance =2000000;
			ArrayList<Position> points = new ArrayList<Position>();
			
			//double value = Math.floor(Math.random() * 20)/100;
			
			for (int i = 0; i < numControlPoints; i++)
			{
				double interpolation = (double) i / (numControlPoints-1);
				
				double arcHeight=(distance/2)*Math.sin(Math.PI*interpolation);
				
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
	
	
	
	public ArrayList<Position> setControlPointsOver2DMap(Position source, Position destination,double distance){

		//double distance =2000000;
					ArrayList<Position> points = new ArrayList<Position>();
					
					//double value = Math.floor(Math.random() * 20)/100;
					
					for (int i = 0; i < numControlPoints; i++)
					{
						double interpolation = (double) i / (numControlPoints-1);
						
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
