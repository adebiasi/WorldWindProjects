package it;


import it.entities.Entry;
import it.entities.Node;
import it.listeners.KeyOption;

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
	public static final double WGS84_EQUATORIAL_RADIUS = 6378137.0; 
	static public int focusAreaRadiusMeters = 3000000;
	
	//static public int focusAreaRadius = 6000000;	
	//static public double halfWidth = 6000000;
	
	
	static public double halfWidth = 3500000;
	//static public double halfWidth = 1000000;
	
	
	//public static Color lineColor =Color.RED;
	
	//public static int lineWidth =3;
	
	//public static Color unselectedLineColor=Color.WHITE;
	
	
	public static Color unselectedLineColor= new Color(255, 255, 255, 255);
	//public static Color unselectedLineColor= new Color(255, 255, 255, 150);	
	static public double lineWidth = 2;
	
	
	static public double lineOfInterestWidth = 2;
	public static Color lineColor =Color.WHITE;
	//public static Color lineColor =Color.RED;
	
	//public static double distCoeff = 0.4;
	public static double distCoeff = 1.0;
	public static double distCoeff2 = 0.1;
	
	 static public int numSubsegments = 5;
	//static public int numSubsegments = 5;
	// public static int numControlPoints = 5;
	 public static int numControlPoints = 5;
	 
	// static public String selectedLayer = "globe";
	// static public String selectedLayer = "distance2";
	// static public String selectedLayer = "~600 arcs";
	// static public String selectedLayer = "2archi_test";
	// static public String selectedLayer = "meno di 600 arcs";
	 
	 
	 static public String selectedLayer = 
			 //"HostwayInternational";
	 //"Airtel";
	 "HurricaneElectric";
	 //"Internode";
	 //"Packetexchange";
	 
	// static public String selectedLayer = "Australia arcs";
	// static public String selectedLayer = "pochi archi";
	// static public String selectedLayer = "3archi_test";
	// static public String selectedLayer = "4777 arcs";
	// static public String selectedLayer = "34354 arcs";
	// static public String selectedLayer = "13282 arcs";
	 
	 static public ArrayList<String> entries;
     static public HashMap<String, Entry> city_to_city;
     static public HashMap<String, Node>     nodes;
	//static public AppFrame frame;
	//static public it.graphitech.smeSpire.Main_TEST.AppFrame frame2;
	
	public static Annotation currentAnnotation;
	
	public static Position pos;
	public static Vec4 screenPoint;
	public static WorldWindow wwd;

	//static public final String texturePath = "images/world.jpg"; // TODO: make configurable
	static public final String texturePath = "images/world.topo.bathy.200405.jpg"; // TODO: make configurable

	
	public  static boolean debugMode = false;
	public static boolean showIntermediatePoint = false;
	public static boolean showMiddlePoint = false;
	public static boolean showOnlyNearArc = true;
	
	//public static boolean useGlobalLineWidth = false; 
	public static boolean lensIsActive = true;
	
	public static boolean alwaysDeform = true;
	
	public static boolean showTextures = false;
	
	public static double lense_h=150;
	public static double lense_w=150;
	
	//public static int numControlPoints = 3;
	
	//public static double globalLineWidth= 4;
	
	
	static public double maxDistance = 50;
	
	//static public double focusAreaRadiusScreenCoord = lense_w/2;
	
	
	static public boolean revealEdgeStructure = true;
	
	

	 public static final int SIZEOF_FLOAT = 4;
	  public static final int SIZEOF_INT = 32;
	  
	  
	  public static final int radius1 = 50000;
	  public static final int radius2 = 100000;
	  //public static final int radius3a = 500000;
	  //public static final int radius4a = 1000000;
	  
	  
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

public static boolean isInsideViewPort(Position position){
	Vec4 worldCoordPoint = computeWorldCoordinatesFromPosition(position);
	Vec4 sp = computeScreenCoordinates(worldCoordPoint);	
	Rectangle viewport =  wwd.getView().getViewport();
	int offset = 100;
	Rectangle smallerRect = new Rectangle(viewport.x+offset, viewport.y+offset, viewport.width-(2*offset), viewport.height-(2*offset));
	/*
	System.out.println("Viewport: "+viewport);
	System.out.println("smallerRect: "+smallerRect);
	System.out.println("screen point: "+sp);
	*/
	return smallerRect.contains(sp.x,sp.y);
	
}

public static Globe getGlobe(){
	return wwd.getModel().getGlobe();
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

//ritorna da 0 a 1 
public static double calculateDistortionCoefficent(double deformUserParam,double deformUserParam2,double valY){

	double res;
	
	if(KeyOption.idFunction==1){
	//double res = 1 - (Math.log(2)/Math.log(2+(valY)));
		res = (Math.pow((valY/2),2));
	//double res = (Math.pow((valY),2))/2;
	}else{
		res = 1 - (Math.log(2)/Math.log(2+(valY)));	
	}
	
	//
	return res*deformUserParam;
	//System.out.println("VAL: "+valY+" res: "+res);
	
// double res = valY;
//	double additionalDistortion = res*deformUserParam2;
//	return  deformUserParam+additionalDistortion;
//	return 0.25;
	
	
}

public static double calculateDistParam2(double p1){
	return (0.5-p1)/2;
}

/*
public static double calculateDistortionCoefficent(double valY){
	double additionalDistortion = 
			valY*distCoeff2;
			//0;
	//double originalInterpolationValue = interpolationValue;
	return  distCoeff+additionalDistortion;
}
*/
public static Vec4 getEyePoint(){
	View view = wwd.getView();
	return view.getEyePoint();	
}
/*
public static Position getEyePosition(View view){
	
	return view.getEyePosition();
}
*/
public static Position getEyePositionOverTheGlobe(){
	View view = wwd.getView();
	Position eyePositionOverTheGlobe = gov.nasa.worldwind.util.RayCastingSupport.intersectRayWithTerrain(view.getGlobe(), view.getEyePoint(),  view.getEyePoint().multiply3(-1));
	return eyePositionOverTheGlobe;
}








public static boolean isPositionVisible(
		Vec4 point_in_worldCoordinates
		//, 
		//Position pos,
		//Vec4 sceenPoint
		){
	
		Vec4[] points=RayCastingSupport.intersectRayWithTerrainReturns2Points(SharedVariables.getGlobe(), 
			//dc.getView().getEyePoint()
			SharedVariables.getEyePoint()
			,  point_in_worldCoordinates.subtract3(
					//dc.getView().getEyePoint()
					SharedVariables.getEyePoint()
					).normalize3(),100,10);
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
