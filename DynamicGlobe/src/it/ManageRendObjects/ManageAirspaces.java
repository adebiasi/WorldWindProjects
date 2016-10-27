package it.ManageRendObjects;
import it.SharedVariables;
import it.rendObjects.PointOfInterest;

import java.awt.Color;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.render.airspaces.CappedCylinder;
import gov.nasa.worldwind.render.airspaces.SphereAirspace;
import gov.nasa.worldwind.util.WWUtil;


public class ManageAirspaces {
/*
	public static void generateAirspaces(Position pos, AirspaceLayer airspaces){
		
		  SphereAirspace sphere = new SphereAirspace();
          sphere.setLocation(pos);
          sphere.setAltitude(5000.0);
          sphere.setTerrainConforming(true);
          sphere.setRadius(50000.0);
          setupDefaultMaterial(sphere, Color.ORANGE);
          airspaces.addAirspace(sphere);
	}
	*/
	public static void generateAirspaces(Position pos, AirspaceLayer airspaces,Color color,double radius){
		
		  PointOfInterest sphere = new PointOfInterest(pos);
	        sphere.setLocation(pos);
	        sphere.setAltitude(pos.getAltitude());
	        sphere.setEnableLevelOfDetail(false);
	        //sphere.setTerrainConforming(true);
	        sphere.setTerrainConforming(false);
	        sphere.setRadius(radius);
	        setupDefaultMaterial(sphere, color);
	        airspaces.addAirspace(sphere);
		
	}
	
	public static void generateFocusArea(Position pos, AirspaceLayer airspaces,Color color,double radius){
		
	CappedCylinder cyl = new CappedCylinder();
    cyl.setCenter(pos);
    cyl.setRadii(radius*0.96, radius);
    cyl.setAltitudes(0,10000);
    cyl.setEnableLevelOfDetail(false);
    cyl.setTerrainConforming(false);
    //cyl.setTerrainConforming(true, true);
    //cyl.setValue(AVKey.DISPLAY_NAME, "30,000m Radius Cylinder. Top & bottom terrain conformance.");
    setupDefaultMaterial(cyl, color);
    airspaces.addAirspace(cyl);
	}
	
	public static void generateAirspaces(Position pos, AirspaceLayer airspaces,Color color, int size){
		double radius;
		if(size==0){
			radius = SharedVariables.radius1;
		}else {
			radius = SharedVariables.radius2;
		}
		
		
		generateAirspaces(pos, airspaces, color, radius);
		
		
	}
	
	  protected static void setupDefaultMaterial(Airspace a, Color color)
      {
          a.getAttributes().setDrawOutline(true);
          a.getAttributes().setMaterial(new Material(color));
          a.getAttributes().setOutlineMaterial(new Material(WWUtil.makeColorBrighter(color)));
          a.getAttributes().setOpacity(0.8);
          a.getAttributes().setOutlineOpacity(0.9);
          a.getAttributes().setOutlineWidth(3.0);
      }
}
