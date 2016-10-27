package fluidSimulator;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;


import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Renderable;

public class Variables {

	public static Iterable<Renderable> buildings;
	
	public static int timer = 
		//100;
		70;
		//5000;
	static public int N =
		70;
	//5;
		
	//TRENTO
	/*
	public static double lat1= 		
		46.0381;
	
	public static double lat2= 			
	46.0783;
		
	public static double lon1= 
		11.1016;
	
	public static double lon2= 
		11.1469;
		
	*/
	
	public static double lat1= 		
		//46.04210;	
	//46.0666;
		46.0593;
	public static double lat2= 	
	//46.044;
		//46.0681;
	
	46.0603;
	public static double lon1= 
		//11.1225;
		//11.1199;
		11.1329;
	public static double lon2=		
	//11.1261;
	//11.1222;
		11.1346;
	
	public static double initAltitude = 10000;
	
	public static double minDensityValue = 0;
	public   static double maxDensityValue = 1;
	
	public static double minVelocityValue = -1;
	public   static double maxVelocityValue = 1;
	   
	public static String fileBuilding="sim_data\\buildings\\shapes\\edfici_projected_lite.shp";
	
	
	//static int size = (N+2)*(N+2);
	static public int size = (Variables.N+2)*(Variables.N+2);

	
	///////////////////////////////////
	//fluid simulator parameters
/////////////////////////////////	
	
	//time spacing between the snapshots
	static public float dt=
		//0.1f;
		//////////0.4f;
	0.5f;
	//da 0.0050 a 0.5
	//static public float dt=0.5f;
	
	//da 0 a 5
	
	static public float diff=
		//0.000005f;
		 // 0.0000004f;
		 0.000000000001f;
	//da 10 a 0
	//static public float visc=0.000001f;
	static public float visc=
		//0.00005f;
		 // 0.0000005f;
		    0.0000000005f;
	//static public float visc=0.000000001f;
	
	//public static ArrayList<Point> inputPointList=new ArrayList<Point>();;
	//public static ArrayList<PointVel> inputVelPointList=new ArrayList<PointVel>();;

	
	
	public static boolean isInsideArea(Position pos){
		if(pos==null){
			return false;
		}
		
		if((pos.latitude.degrees>lat1)&(pos.latitude.degrees<lat2)){
			if((pos.longitude.degrees>lon1)&(pos.longitude.degrees<lon2)){
			//System.out.println("inside");
				return true;
			}
		}
		//System.out.println("outside");
		return false;
	}
	
	
	public static Point getXy(Position pos){
		
		
		double deltaLat=pos.latitude.degrees-lat1;
		double deltaLon=pos.longitude.degrees-lon1;
		double latLength=lat2-lat1;
		double lonLength=lon2-lon1;
		
		int y =N-1-  (int)((deltaLat/latLength)*N);
		int x =(int)((deltaLon/lonLength)*N);
		
		return new Point(x,y);
		
	}
	
public static Point getXy(LatLon latLon){
		
		
		double deltaLat=latLon.latitude.degrees-lat1;
		double deltaLon=latLon.longitude.degrees-lon1;
		double latLength=lat2-lat1;
		double lonLength=lon2-lon1;
		
		int y =N-1-  (int)((deltaLat/latLength)*N);
		int x =(int)((deltaLon/lonLength)*N);
		
		return new Point(x,y);
		
	}
}
