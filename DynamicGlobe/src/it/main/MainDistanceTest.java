package it.main;

import it.Ellipsoid;
import it.GeodeticCalculator;
import it.Operations;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;

public class MainDistanceTest {
public static void main(String[] args) {
	
	
	LatLon ll1 = LatLon.fromDegrees(13.022930588118802, 162.1750116364145) ;
	LatLon ll2 = LatLon.fromDegrees(-13.158237375011195, -17.929727101359486) ;
	
	
	//LatLon ll1 = LatLon.fromDegrees(13.022930588118802, 162.1750116364145) ;
	//LatLon ll2 = LatLon.fromDegrees(50.158237375011195, -17.929727101359486) ;
	
	
	System.out.println("INIZIO");
	
	//double distance = Operations.ellipsoidalDistance(ll1, ll2, Earth.WGS84_EQUATORIAL_RADIUS, Earth.WGS84_POLAR_RADIUS);
	double distance2 = GeodeticCalculator.calculateGeodeticCurve(
			Ellipsoid.WGS84, 
			ll1.getLatitude().radians, ll1.getLongitude().radians, 
			ll2.getLatitude().radians, ll2.getLongitude().radians);
	
	//System.out.println("FINE: "+distance);
	System.out.println("FINE2: "+distance2);
}
}
