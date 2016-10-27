package it.graphitech;


import it.graphitech.shader.DecoratedLayer;
import it.graphitech.shader.ShadingDecorator;
import it.graphitech.smeSpire.SharedVariables;
import it.graphitech.smeSpire.layers.MyRenderableLayer;
import it.graphitech.smeSpire.lines.cubicCurve.CubicSplinePolyline;
import it.graphitech.smeSpire.lines.cubicCurve.RenderableControlPoints;

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
		
	public GeneratorOfRenderableObjects(WorldWindow wwd, ArrayList<String> entries, HashMap<String, it.graphitech.smeSpire.entry.Entry> cities, String state,String name){
		this.wwd = wwd;
		
		
          
		File vertexShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.vert");
        File fragmentShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.frag");          
        File geometryShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.geom");
          File dir_IntersectingPointsDetection_Shaders = new File("shaders/IntersectingPointsDetection");
          
	
      	File vertexShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.vert");
        File fragmentShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.frag");          
        //File geometryShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.geom");
          File dir_HideLinesInsideLens_Shaders = new File("shaders/HideLinesInsideLens");
    
          
        
          ShadingDecorator shardingDecorator = new ShadingDecorator(dir_IntersectingPointsDetection_Shaders,
        		  vertexShaderFile_IntersectingPointsDetection , 
        		  fragmentShaderFile_IntersectingPointsDetection ,
        		  geometryShaderFile_IntersectingPointsDetection,
        		  dir_HideLinesInsideLens_Shaders,
        		  vertexShaderFile_HideLinesInsideLens , 
        		  fragmentShaderFile_HideLinesInsideLens
        		 
        		  );
      
        
        /*
        ShadingDecorator shardingDecorator = new ShadingDecorator(dirShaders,
      		  vertexShaderFile_densityMap , 
      		  fragmentShaderFile_densityMap ,
      		  geometryShaderFile_densityMap
      		  );
      		  */
     
        
        MyRenderableLayer layer = new MyRenderableLayer();
        layer.setName(name);
        layer.setEnabled(false);
        
        //RenderableLayer layer = new RenderableLayer();
       // layer.setName("arcs over the globe");
        layer.setPickEnabled(false);

        for(int i=0;i<entries.size();i++){
        	//System.out.println(i);
        	
        	String city = entries.get(i);
        	
        	it.graphitech.smeSpire.entry.Entry entry = cities.get(city);
        
        
            
        	if((entry.getState().compareToIgnoreCase(state) == 0)||(state.compareToIgnoreCase("all")==0)){
        		
        	
        		
        		if(entry.getFrom().compareTo(entry.getTo()) != 0){
        	
        			
        			RenderableControlPoints renderableControlPoints = new RenderableControlPoints();
        			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
        			double distance = distance(entry.getFrom_lat(),entry.getFrom_lon(), entry.getTo_lat(), entry.getTo_lon());
        			
        			renderableControlPoints.setControlPointPosition(setControlPointsOverGlobe(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),distance));
        			renderableControlPoints.calculatePoints();
        			
        			CubicSplinePolyline curve = new CubicSplinePolyline(renderableControlPoints);
        			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
        			curve.setFrom(entry.getFrom());
        			curve.setTo(entry.getTo());
        			
        			layer.addNodexWithIndex(entry.getFrom());
        			layer.addNodexWithIndex(entry.getTo());
        			
        			layer.addRenderable(curve);
        		}
        	
        	}
        }
    
        
        DecoratedLayer layerWithShader = new DecoratedLayer(layer, shardingDecorator);
    System.out.println("NUM CONNECTIONS: "+layer.getNumRenderables());
    insertBeforeCompass(this.wwd, layerWithShader);
	}


	
	public static void  GeneratorOfRenderableObjectsOnFlatMapBIGDATACHALLENGE(WorldWindow wwd, ArrayList<String> entries, HashMap<String, 
			it.graphitech.smeSpire.entry.Entry> cities, String state,String name,
			ArrayList<Double> minmaxAvgTime,ArrayList<Integer> minmaxNumberCalls){
		//this.wwd = wwd;
		
		
          
		File vertexShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.vert");
        File fragmentShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.frag");          
        File geometryShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.geom");
          File dir_IntersectingPointsDetection_Shaders = new File("shaders/IntersectingPointsDetection");
          
	
      	File vertexShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.vert");
        File fragmentShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.frag");          
        //File geometryShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.geom");
          File dir_HideLinesInsideLens_Shaders = new File("shaders/HideLinesInsideLens");
    
          
        
          ShadingDecorator shardingDecorator = new ShadingDecorator(dir_IntersectingPointsDetection_Shaders,
        		  vertexShaderFile_IntersectingPointsDetection , 
        		  fragmentShaderFile_IntersectingPointsDetection ,
        		  geometryShaderFile_IntersectingPointsDetection,
        		  dir_HideLinesInsideLens_Shaders,
        		  vertexShaderFile_HideLinesInsideLens , 
        		  fragmentShaderFile_HideLinesInsideLens
        		 
        		  );
      
        
        /*
        ShadingDecorator shardingDecorator = new ShadingDecorator(dirShaders,
      		  vertexShaderFile_densityMap , 
      		  fragmentShaderFile_densityMap ,
      		  geometryShaderFile_densityMap
      		  );
      		  */
     
        
        MyRenderableLayer layerOnFlatMap = new MyRenderableLayer();
        layerOnFlatMap.setName(name);
        layerOnFlatMap.setEnabled(false);
        
        //RenderableLayer layer = new RenderableLayer();
       // layer.setName("arcs over the globe");
        layerOnFlatMap.setPickEnabled(false);

        
        
        for(int i=0;i<entries.size();i++){
        	//System.out.println(i);
        	
        	String city = entries.get(i);
        	
        	it.graphitech.smeSpire.entry.Entry entry = cities.get(city);
        
        
            
        	if((entry.getState().compareToIgnoreCase(state) == 0)||(state.compareToIgnoreCase("all")==0)){
        		
        	
        		
        		if(entry.getFrom().compareTo(entry.getTo()) != 0){
        	
        			
        			RenderableControlPoints rendCPOver2DMap = new RenderableControlPoints();
        			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
        			double distance = distance(entry.getFrom_lat(),entry.getFrom_lon(), entry.getTo_lat(), entry.getTo_lon());
        			
        			rendCPOver2DMap.setControlPointPosition(setControlPointsOver2DMap(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),distance));
        			
        			rendCPOver2DMap.calculatePointsOver2DMap();
        			CubicSplinePolyline curveOver2DMap = new CubicSplinePolyline(rendCPOver2DMap);
        			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
        			curveOver2DMap.setFrom(entry.getFrom());
        			curveOver2DMap.setTo(entry.getTo());
        			
        			curveOver2DMap.setAvgTime(entry.getAvg_time());
        			curveOver2DMap.setNumberCalls(entry.getNumber_calls());
        			curveOver2DMap.setMinAvgTime(minmaxAvgTime.get(0));
        			curveOver2DMap.setMinNumberCalls(minmaxNumberCalls.get(0));
        			curveOver2DMap.setMaxAvgTime(minmaxAvgTime.get(1));
        			curveOver2DMap.setMaxNumberCalls(minmaxNumberCalls.get(1));
        			
        			
        			
        			
        			curveOver2DMap.calculateColors();
        			
        			layerOnFlatMap.addNodexWithIndex(entry.getFrom());
        			layerOnFlatMap.addNodexWithIndex(entry.getTo());
        			
        			layerOnFlatMap.addRenderable(curveOver2DMap);
        		}
        	
        	}
        }
    
        
        DecoratedLayer layerWithShader = new DecoratedLayer(layerOnFlatMap, shardingDecorator);
    System.out.println("NUM CONNECTIONS: "+layerOnFlatMap.getNumRenderables());
    insertBeforeCompass(wwd, layerWithShader);
	}
	
	
	public static void  GeneratorOfRenderableObjectsOnFlatMapBIGDATACHALLENGE_time(WorldWindow wwd, ArrayList<String> entries, HashMap<String, 
			it.graphitech.smeSpire.entry.Entry> cities, String state,String name,
			ArrayList<Double> minmaxAvgTime,ArrayList<Integer> minmaxNumberCalls,ArrayList<Integer> minmaxTotNumberCalls){
		//this.wwd = wwd;
		
		
          
		File vertexShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.vert");
        File fragmentShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.frag");          
        File geometryShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.geom");
          File dir_IntersectingPointsDetection_Shaders = new File("shaders/IntersectingPointsDetection");
          
	
      	File vertexShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.vert");
        File fragmentShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.frag");          
        //File geometryShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.geom");
          File dir_HideLinesInsideLens_Shaders = new File("shaders/HideLinesInsideLens");
    
          
        
          ShadingDecorator shardingDecorator = new ShadingDecorator(dir_IntersectingPointsDetection_Shaders,
        		  vertexShaderFile_IntersectingPointsDetection , 
        		  fragmentShaderFile_IntersectingPointsDetection ,
        		  geometryShaderFile_IntersectingPointsDetection,
        		  dir_HideLinesInsideLens_Shaders,
        		  vertexShaderFile_HideLinesInsideLens , 
        		  fragmentShaderFile_HideLinesInsideLens
        		 
        		  );
      
        
        /*
        ShadingDecorator shardingDecorator = new ShadingDecorator(dirShaders,
      		  vertexShaderFile_densityMap , 
      		  fragmentShaderFile_densityMap ,
      		  geometryShaderFile_densityMap
      		  );
      		  */
     
        
        MyRenderableLayer layerOnFlatMap = new MyRenderableLayer();
        layerOnFlatMap.setName(name);
        layerOnFlatMap.setEnabled(false);
        
        //RenderableLayer layer = new RenderableLayer();
       // layer.setName("arcs over the globe");
        layerOnFlatMap.setPickEnabled(false);

        createFakeLine(layerOnFlatMap,entries.get(0),cities);
        
        for(int i=0;i<entries.size();i++){
        	//System.out.println(i);
        	
        	String city = entries.get(i);
        	
        	it.graphitech.smeSpire.entry.Entry entry = cities.get(city);
        
        
            
        	if((entry.getState().compareToIgnoreCase(state) == 0)||(state.compareToIgnoreCase("all")==0)){
        		
        	
        		
        		if(entry.getFrom().compareTo(entry.getTo()) != 0){
        	
        			
        			RenderableControlPoints rendCPOver2DMap = new RenderableControlPoints();
        			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
        			double distance = distance(entry.getFrom_lat(),entry.getFrom_lon(), entry.getTo_lat(), entry.getTo_lon());
        			
        			rendCPOver2DMap.setControlPointPosition(setControlPointsOver2DMap(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),distance));
        			
        			rendCPOver2DMap.calculatePointsOver2DMap();
        			CubicSplinePolyline curveOver2DMap = new CubicSplinePolyline(rendCPOver2DMap);
        			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
        			curveOver2DMap.setFrom(entry.getFrom());
        			curveOver2DMap.setTo(entry.getTo());
        			curveOver2DMap.setTime(entry.getTime());
        			
        			curveOver2DMap.setAvgTime(entry.getAvg_time());
        			curveOver2DMap.setNumberCalls(entry.getNumber_calls());
        			
        			
        			curveOver2DMap.setTotNumberCalls(entry.getTotNumber_calls());
        			
        			curveOver2DMap.setMinAvgTime(minmaxAvgTime.get(0));
        			curveOver2DMap.setMinNumberCalls(minmaxNumberCalls.get(0));
        			curveOver2DMap.setMaxAvgTime(minmaxAvgTime.get(1));
        			curveOver2DMap.setMaxNumberCalls(minmaxNumberCalls.get(1));
        			
        			curveOver2DMap.setMaxNumberCallsPerState(entry.getTotMaxNumberCalls());
        			curveOver2DMap.setMinNumberCallsPerState(entry.getTotMinNumberCalls());
        		
        			
        			curveOver2DMap.setMinTotNumberCalls(minmaxTotNumberCalls.get(0));        			
        			curveOver2DMap.setMaxTotNumberCalls(minmaxTotNumberCalls.get(1));
        			
        			curveOver2DMap.setNumberCallsOutliner(entry.isOutlierNumberCall());
        			curveOver2DMap.setAvgTimeOutliner(entry.isOutlierAvgTime());
        			
        			curveOver2DMap.setTotNumberCallsOutliner(entry.getTotOutlinerNumberCalls());
        			curveOver2DMap.setTotAvgTimeOutliner(entry.getTotOutlinerAvgTime());
        		
        			
        			curveOver2DMap.calculateColors();
        			
        			layerOnFlatMap.addNodexWithIndex(entry.getFrom());
        			layerOnFlatMap.addNodexWithIndex(entry.getTo());
        			
        			layerOnFlatMap.addRenderable(curveOver2DMap);
        		}
        	
        	}
        }
    
        
        DecoratedLayer layerWithShader = new DecoratedLayer(layerOnFlatMap, shardingDecorator);
    System.out.println("NUM CONNECTIONS: "+layerOnFlatMap.getNumRenderables());
    insertBeforeCompass(wwd, layerWithShader);
	}
	
	
	private static void createFakeLine( MyRenderableLayer layerOnFlatMap,String city,HashMap<String, 
			it.graphitech.smeSpire.entry.Entry> cities){
	
    	
    	it.graphitech.smeSpire.entry.Entry entry = cities.get(city);
    
    
        
    	
    		
    	
    		
    		if(entry.getFrom().compareTo(entry.getTo()) != 0){
    	
    			
    			RenderableControlPoints rendCPOver2DMap = new RenderableControlPoints();
    			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
    			double distance = distance(entry.getFrom_lat(),entry.getFrom_lon(), entry.getTo_lat(), entry.getTo_lon());
    			
    			//rendCPOver2DMap.setControlPointPosition(setControlPointsOver2DMap(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.ZERO,distance));
    			rendCPOver2DMap.setControlPointPosition(setControlPointsOver2DMap(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()),0));
    			
    			rendCPOver2DMap.calculatePointsOver2DMap();
    			CubicSplinePolyline curveOver2DMap = new CubicSplinePolyline(rendCPOver2DMap);
    			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
    			curveOver2DMap.setFrom(entry.getFrom());
    			curveOver2DMap.setTo(entry.getTo());
    			curveOver2DMap.setTime(entry.getTime());
    			
    			curveOver2DMap.setAvgTime(0);
    			curveOver2DMap.setNumberCalls(0);
    			
    			
    			curveOver2DMap.setTotNumberCalls(0);
    			
    			curveOver2DMap.setMinAvgTime(0);
    			curveOver2DMap.setMinNumberCalls(0);
    			curveOver2DMap.setMaxAvgTime(0);
    			curveOver2DMap.setMaxNumberCalls(0);
    			
    			curveOver2DMap.setMaxNumberCallsPerState(0);
    			curveOver2DMap.setMinNumberCallsPerState(0);
    		
    			
    			curveOver2DMap.setMinTotNumberCalls(0);        			
    			curveOver2DMap.setMaxTotNumberCalls(0);
    			
    			curveOver2DMap.setNumberCallsOutliner(false);
    			
    			
    			curveOver2DMap.setTotNumberCallsOutliner(0);
    			curveOver2DMap.setTotAvgTimeOutliner(0);
    		
    			
    			curveOver2DMap.calculateColors();
    			
    			layerOnFlatMap.addNodexWithIndex(entry.getFrom());
    			layerOnFlatMap.addNodexWithIndex(entry.getTo());
    			
    			
    			layerOnFlatMap.addRenderable(curveOver2DMap);
    		}
    	
    	
	}
	
	public static void GeneratorOfRenderableObjectsBIGDATACHALLENGE(WorldWindow wwd, ArrayList<String> entries, HashMap<String, 
			it.graphitech.smeSpire.entry.Entry> cities, String state,String name,
			ArrayList<Double> minmaxAvgTime,ArrayList<Integer> minmaxNumberCalls){
		//this.wwd = wwd;
		
		
          
		File vertexShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.vert");
        File fragmentShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.frag");          
        File geometryShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.geom");
          File dir_IntersectingPointsDetection_Shaders = new File("shaders/IntersectingPointsDetection");
          
	
      	File vertexShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.vert");
        File fragmentShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.frag");          
        //File geometryShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.geom");
          File dir_HideLinesInsideLens_Shaders = new File("shaders/HideLinesInsideLens");
    
          
        
          ShadingDecorator shardingDecorator = new ShadingDecorator(dir_IntersectingPointsDetection_Shaders,
        		  vertexShaderFile_IntersectingPointsDetection , 
        		  fragmentShaderFile_IntersectingPointsDetection ,
        		  geometryShaderFile_IntersectingPointsDetection,
        		  dir_HideLinesInsideLens_Shaders,
        		  vertexShaderFile_HideLinesInsideLens , 
        		  fragmentShaderFile_HideLinesInsideLens
        		 
        		  );
      
        
        /*
        ShadingDecorator shardingDecorator = new ShadingDecorator(dirShaders,
      		  vertexShaderFile_densityMap , 
      		  fragmentShaderFile_densityMap ,
      		  geometryShaderFile_densityMap
      		  );
      		  */
     
        
        MyRenderableLayer layer = new MyRenderableLayer();
        layer.setName(name);
        layer.setEnabled(false);
        
        //RenderableLayer layer = new RenderableLayer();
       // layer.setName("arcs over the globe");
        layer.setPickEnabled(false);

        for(int i=0;i<entries.size();i++){
        	//System.out.println(i);
        	
        	String city = entries.get(i);
        	
        	it.graphitech.smeSpire.entry.Entry entry = cities.get(city);
        
        
            
        	if((entry.getState().compareToIgnoreCase(state) == 0)||(state.compareToIgnoreCase("all")==0)){
        		
        	
        		
        		if(entry.getFrom().compareTo(entry.getTo()) != 0){
        	
        			
        			RenderableControlPoints renderableControlPoints = new RenderableControlPoints();
        			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
        			double distance = distance(entry.getFrom_lat(),entry.getFrom_lon(), entry.getTo_lat(), entry.getTo_lon());
        			
        			renderableControlPoints.setControlPointPosition(setControlPointsOverGlobe(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),distance));
        			renderableControlPoints.calculatePoints();
        			
        			CubicSplinePolyline curve = new CubicSplinePolyline(renderableControlPoints);
        			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
        			curve.setFrom(entry.getFrom());
        			curve.setTo(entry.getTo());
        			
        			
        			curve.setAvgTime(entry.getAvg_time());
        			curve.setNumberCalls(entry.getNumber_calls());
        			
        			
        			
        			curve.setMinAvgTime(minmaxAvgTime.get(0));
        			curve.setMinNumberCalls(minmaxNumberCalls.get(0));
        			curve.setMaxAvgTime(minmaxAvgTime.get(1));
        			curve.setMaxNumberCalls(minmaxNumberCalls.get(1));
        			
        			curve.setTotAvgTimeOutliner(entry.getTotOutlinerAvgTime());
        			curve.setTotNumberCallsOutliner(entry.getTotOutlinerNumberCalls());
        			curve.calculateColors();
        			
        			layer.addNodexWithIndex(entry.getFrom());
        			layer.addNodexWithIndex(entry.getTo());
        			
        			layer.addRenderable(curve);
        		}
        	
        	}
        }
    
        
        DecoratedLayer layerWithShader = new DecoratedLayer(layer, shardingDecorator);
    System.out.println("NUM CONNECTIONS: "+layer.getNumRenderables());
    insertBeforeCompass(wwd, layerWithShader);
	}

	public static void GeneratorOfRenderableObjectsBIGDATACHALLENGE_time(WorldWindow wwd, ArrayList<String> entries, HashMap<String, 
			it.graphitech.smeSpire.entry.Entry> cities, String state,String name,
			ArrayList<Double> minmaxAvgTime,ArrayList<Integer> minmaxNumberCalls,ArrayList<Integer> minmaxTotNumberCalls){
		//this.wwd = wwd;
		
		
          
		File vertexShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.vert");
        File fragmentShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.frag");          
        File geometryShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.geom");
          File dir_IntersectingPointsDetection_Shaders = new File("shaders/IntersectingPointsDetection");
          
	
      	File vertexShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.vert");
        File fragmentShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.frag");          
        //File geometryShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.geom");
          File dir_HideLinesInsideLens_Shaders = new File("shaders/HideLinesInsideLens");
    
          
        
          ShadingDecorator shardingDecorator = new ShadingDecorator(dir_IntersectingPointsDetection_Shaders,
        		  vertexShaderFile_IntersectingPointsDetection , 
        		  fragmentShaderFile_IntersectingPointsDetection ,
        		  geometryShaderFile_IntersectingPointsDetection,
        		  dir_HideLinesInsideLens_Shaders,
        		  vertexShaderFile_HideLinesInsideLens , 
        		  fragmentShaderFile_HideLinesInsideLens
        		 
        		  );
      
        
        /*
        ShadingDecorator shardingDecorator = new ShadingDecorator(dirShaders,
      		  vertexShaderFile_densityMap , 
      		  fragmentShaderFile_densityMap ,
      		  geometryShaderFile_densityMap
      		  );
      		  */
     
        
        MyRenderableLayer layer = new MyRenderableLayer();
        layer.setName(name);
        layer.setEnabled(false);
        
        //RenderableLayer layer = new RenderableLayer();
       // layer.setName("arcs over the globe");
        layer.setPickEnabled(false);
        createFakeLine(layer,entries.get(0),cities);
        for(int i=0;i<entries.size();i++){
        	//System.out.println(i);
        	
        	String city = entries.get(i);
        	
        	it.graphitech.smeSpire.entry.Entry entry = cities.get(city);
        
        
            
        	if((entry.getState().compareToIgnoreCase(state) == 0)||(state.compareToIgnoreCase("all")==0)){
        		
        	
        		
        		if(entry.getFrom().compareTo(entry.getTo()) != 0){
        	
        			
        			RenderableControlPoints renderableControlPoints = new RenderableControlPoints();
        			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
        			double distance = distance(entry.getFrom_lat(),entry.getFrom_lon(), entry.getTo_lat(), entry.getTo_lon());
        			
        			renderableControlPoints.setControlPointPosition(setControlPointsOverGlobe(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),distance));
        			renderableControlPoints.calculatePoints();
        			
        			CubicSplinePolyline curve = new CubicSplinePolyline(renderableControlPoints);
        			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
        			curve.setFrom(entry.getFrom());
        			curve.setTo(entry.getTo());
        			
        			curve.setTime(entry.getTime());
        			curve.setAvgTime(entry.getAvg_time());
        			curve.setNumberCalls(entry.getNumber_calls());
        			curve.setMinAvgTime(minmaxAvgTime.get(0));
        			curve.setMinNumberCalls(minmaxNumberCalls.get(0));
        			curve.setMaxAvgTime(minmaxAvgTime.get(1));
        			curve.setMaxNumberCalls(minmaxNumberCalls.get(1));
        			
        			curve.setMinTotNumberCalls(minmaxTotNumberCalls.get(0));        			
        			curve.setMaxTotNumberCalls(minmaxTotNumberCalls.get(1));
        			
        			curve.setMaxNumberCallsPerState(entry.getTotMaxNumberCalls());
        			curve.setMinNumberCallsPerState(entry.getTotMinNumberCalls());
        			
        			curve.setTotNumberCalls(entry.getTotNumber_calls());
        			
        			curve.setNumberCallsOutliner(entry.isOutlierNumberCall());
        			curve.setAvgTimeOutliner(entry.isOutlierAvgTime());
        			
        			curve.setTotNumberCallsOutliner(entry.getTotOutlinerNumberCalls());
        			curve.setTotAvgTimeOutliner(entry.getTotOutlinerAvgTime());
        			
        			
        			curve.calculateColors();
        			
        			layer.addNodexWithIndex(entry.getFrom());
        			layer.addNodexWithIndex(entry.getTo());
        			
        			layer.addRenderable(curve);
        		}
        	
        	}
        }
    
        
        DecoratedLayer layerWithShader = new DecoratedLayer(layer, shardingDecorator);
    System.out.println("NUM CONNECTIONS: "+layer.getNumRenderables());
    insertBeforeCompass(wwd, layerWithShader);
	}

	
	public static void  GeneratorOfRenderableObjectsOnFlatMap(WorldWindow wwd, ArrayList<String> entries, HashMap<String, 
			it.graphitech.smeSpire.entry.Entry> cities, String state,String name){
		//this.wwd = wwd;
		
		
          
		File vertexShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.vert");
        File fragmentShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.frag");          
        File geometryShaderFile_IntersectingPointsDetection = new File("shaders/IntersectingPointsDetection/IntersectingPointsDetection.geom");
          File dir_IntersectingPointsDetection_Shaders = new File("shaders/IntersectingPointsDetection");
          
	
      	File vertexShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.vert");
        File fragmentShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.frag");          
        //File geometryShaderFile_HideLinesInsideLens = new File("shaders/HideLinesInsideLens/HideLinesInsideLens.geom");
          File dir_HideLinesInsideLens_Shaders = new File("shaders/HideLinesInsideLens");
    
          
        
          ShadingDecorator shardingDecorator = new ShadingDecorator(dir_IntersectingPointsDetection_Shaders,
        		  vertexShaderFile_IntersectingPointsDetection , 
        		  fragmentShaderFile_IntersectingPointsDetection ,
        		  geometryShaderFile_IntersectingPointsDetection,
        		  dir_HideLinesInsideLens_Shaders,
        		  vertexShaderFile_HideLinesInsideLens , 
        		  fragmentShaderFile_HideLinesInsideLens
        		 
        		  );
      
        
        /*
        ShadingDecorator shardingDecorator = new ShadingDecorator(dirShaders,
      		  vertexShaderFile_densityMap , 
      		  fragmentShaderFile_densityMap ,
      		  geometryShaderFile_densityMap
      		  );
      		  */
     
        
        MyRenderableLayer layerOnFlatMap = new MyRenderableLayer();
        layerOnFlatMap.setName(name);
        layerOnFlatMap.setEnabled(false);
        
        //RenderableLayer layer = new RenderableLayer();
       // layer.setName("arcs over the globe");
        layerOnFlatMap.setPickEnabled(false);

        for(int i=0;i<entries.size();i++){
        	//System.out.println(i);
        	
        	String city = entries.get(i);
        	
        	it.graphitech.smeSpire.entry.Entry entry = cities.get(city);
        
        
            
        	if((entry.getState().compareToIgnoreCase(state) == 0)||(state.compareToIgnoreCase("all")==0)){
        		
        	
        		
        		if(entry.getFrom().compareTo(entry.getTo()) != 0){
        	
        			
        			RenderableControlPoints rendCPOver2DMap = new RenderableControlPoints();
        			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
        			double distance = distance(entry.getFrom_lat(),entry.getFrom_lon(), entry.getTo_lat(), entry.getTo_lon());
        			
        			rendCPOver2DMap.setControlPointPosition(setControlPointsOver2DMap(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),distance));
        			
        			rendCPOver2DMap.calculatePointsOver2DMap();
        			CubicSplinePolyline curveOver2DMap = new CubicSplinePolyline(rendCPOver2DMap);
        			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
        			curveOver2DMap.setFrom(entry.getFrom());
        			curveOver2DMap.setTo(entry.getTo());
        			
        			layerOnFlatMap.addNodexWithIndex(entry.getFrom());
        			layerOnFlatMap.addNodexWithIndex(entry.getTo());
        			
        			layerOnFlatMap.addRenderable(curveOver2DMap);
        		}
        	
        	}
        }
    
        
        DecoratedLayer layerWithShader = new DecoratedLayer(layerOnFlatMap, shardingDecorator);
    System.out.println("NUM CONNECTIONS: "+layerOnFlatMap.getNumRenderables());
    insertBeforeCompass(wwd, layerWithShader);
	}
	
	
	
	
	


	
	
	
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
	
	
	private static ArrayList<Position> setControlPointsOverGlobe(Position source, Position destination,double distance){

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
	
	
	
	public static ArrayList<Position> setControlPointsOver2DMap(Position source, Position destination,double distance){

		//double distance =2000000;
					ArrayList<Position> points = new ArrayList<Position>();
					
					//double value = Math.floor(Math.random() * 20)/100;
					
					for (int i = 0; i < SharedVariables.numControlPoints; i++)
					{
						double interpolation = (double) i / (SharedVariables.numControlPoints-1);
						
						double arcHeight=(distance/8)*Math.sin(Math.PI*interpolation);
						
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
