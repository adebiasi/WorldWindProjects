package it.graphitech.smeSpire;

import it.graphitech.smeSpire.Main_STANDALONE_v3.AppFrame;
import it.graphitech.smeSpire.entry.Entry;
import it.graphitech.smeSpire.framebuffer.MyBasicOrbitView;
import it.graphitech.smeSpire.framebuffer.MyFlatOrbitView;
import it.graphitech.smeSpire.lines.cubicCurve.CubicSplinePolyline;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.List;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.media.opengl.GL;

import com.jogamp.opengl.util.texture.Texture;



/*
import com.sun.opengl.util.texture.Texture;
*/
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Intersection;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.view.orbit.OrbitView;

public class SharedVariables {

	static public int counterOfFilteredLines=0;
	static public int counterOfDistortedLines=0;
	static public int counterOfSimpleLines=0;
	static public int counterOfLinesInsideLens=0;
	
	
	static public int minNumCallsToFilter = 100;
	
	 static public int numSubsegments = 30;
	//static public int numSubsegments = 5;
	// public static int numControlPoints = 5;
	 public static int numControlPoints = 7;
	 
	 static public ArrayList<String> entries;
     static public HashMap<String, Entry> city_to_city;
     static public HashMap<String, Position>     nodes;
	//static public AppFrame frame;
	//static public it.graphitech.smeSpire.Main_TEST.AppFrame frame2;
	
	public static Annotation currentAnnotation;
	
	public static Position pos;
	public static Vec4 screenPoint;
	public static WorldWindow wwd;

	public static RenderTextureOnScreen sr = new RenderTextureOnScreen();

	
	public  static boolean debugMode = false;
	public static boolean showIntermediatePoint = false;
	public static boolean showTime = true;
	public static boolean showMiddlePoint = false;
	public static boolean showOnlyNearArc = true;
	
	//public static boolean useGlobalLineWidth = false; 
	public static boolean lensIsActive = true;
	public static boolean showTextures = false;
	
	public static double lense_h=150;
	public static double lense_w=150;
	
	
	//public static double nodeDiameter = 50000.0;
	public static double nodeDiameter = 30000.0;
	//public static int numControlPoints = 3;
	
	//public static double globalLineWidth= 4;
	static public double lineWidth = 1;
	static public double lineOfInterestWidth = 1;
	static public double maxDistance = 50;
	
	
	static public int chooseColorConfigurations = 0;
	
	static public Color intersectInTwoPoints=Color.RED; 
	static public Color intersectInOnePoints=Color.BLUE;
	static public Color intersectInThreePoints=Color.YELLOW;
	static public Color intersectInFourPoints=Color.PINK;
	
	
	
	static public boolean revealEdgeStructure = true;
	
	

	 public static final int SIZEOF_FLOAT = 4;
	  public static final int SIZEOF_INT = 32;
		 public static IntBuffer newIntBuffer(int numElements){
		 
			 return IntBuffer.allocate(numElements);
			 /*
			 ByteBuffer bb = newByteBuffer(numElements * SIZEOF_INT);
		    return bb.asIntBuffer();
		    */
		 }
		 
		 
		 public static FloatBuffer newFloatBuffer(int numElements){
			 ByteBuffer bb = newByteBuffer(numElements * SIZEOF_FLOAT);
			    return bb.asFloatBuffer();
			 }
		 
		 private static ByteBuffer newByteBuffer(int numElements) {
			    ByteBuffer bb = ByteBuffer.allocateDirect(numElements);
			    bb.order(ByteOrder.nativeOrder());
			    return bb;
			  }
		
	
	public static int computeNumVerticesPerLine(){
		return ((SharedVariables.numSubsegments+1)*(SharedVariables.numControlPoints-1))-(SharedVariables.numControlPoints-2); 
	}
	
public static Vec4 computeScreenCoordinates(Vec4 globePoint){
	
	View view = wwd.getView();
	return view.project(globePoint);
}


public static Vec4 computeWorldCoordinatesFromScreenPoint(Vec4 screenPoint){
	
	View view = wwd.getView();
	return view.unProject(screenPoint);
}

public static Vec4 computeWorldCoordinatesFromPosition(Position position){
	
	if(wwd==null){
		System.out.println("WWD null");
	}
	
	View view = wwd.getView();	
	
	if(view==null){
		System.out.println("view null");
	}
	
	if(view.getGlobe()==null){
		System.out.println("globe null");
	}
	
	return  view.getGlobe().computePointFromPosition(position);
}

public static Position computePositionFromWorldCoordinates(Vec4 globePoint){
	
	Globe globe = wwd.getModel().getGlobe();
	return globe.computePositionFromPoint(globePoint);
}





public static boolean isPositionVisible(DrawContext dc
		,
		Vec4 point_in_worldCoordinates
		//, 
		//Position pos,
		//Vec4 sceenPoint
		){
	
	
	if( (SharedVariables.wwd.getView() instanceof MyFlatOrbitView)){
		return true;
	}
	
//	Line ray = dc.getView().computeRayFromScreenPoint(sceenPoint.x, sceenPoint.y);
	
	//Vec4 terrainPositionScreen=RayCastingSupport.intersectSegmentWithTerrain(dc.getGlobe(), dc.getView().getEyePoint(),  point);
	//Position terrainPosition = computePositionFromWorldCoordinates(terrainPositionScreen);
	
	//Position terrainPosition=RayCastingSupport.intersectRayWithTerrain(dc.getGlobe(), dc.getView().getEyePoint(),  point.subtract3(dc.getView().getEyePoint()).normalize3());
	
	//System.out.println("point in world coordinate: "+point_in_worldCoordinates);
	//System.out.println(" dc.getView().getEyePoint(): "+ dc.getView().getEyePoint());
	Vec4[] points=RayCastingSupport.intersectRayWithTerrainReturns2Points(dc.getGlobe(), dc.getView().getEyePoint(),  point_in_worldCoordinates.subtract3(dc.getView().getEyePoint()).normalize3(),100,10);
	//Position terrainPosition=intersectRayWithTerrain(dc.getGlobe(), dc.getView().getEyePoint(),  point,100,10);

	if((points==null)){
		//System.out.println("points[0]==null!!!!!");
		return true;
		}	
	if((points[0]==null)){
	//System.out.println("points[0]==null!!!!!");
	return true;
	}	
double distance1=point_in_worldCoordinates.distanceTo3(points[0]);
double distance2=point_in_worldCoordinates.distanceTo3(points[1]);

//System.out.println("distance1: "+distance1);
//System.out.println("distance2: "+distance2);

if(distance1<distance2){
	return true;
}
else{
	return false;
}

}










}
