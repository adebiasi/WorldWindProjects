package it;

import com.sun.javafx.geom.Vec2d;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;

public class GeoOperation2 {

	 
	 
	public static double perpendicularDistanceFromAPlaneInsersectingTheOrigin(Position line1, Position line2, Position poi)
	{
	    Vec4 a = SharedVariables.computeWorldCoordinatesFromPosition(line1);
	    Vec4 b = SharedVariables.computeWorldCoordinatesFromPosition(line2);
	    Vec4 c = SharedVariables.computeWorldCoordinatesFromPosition(poi);
/*
	    System.out.println("a: "+a);
	    System.out.println("b: "+b);
	    System.out.println("c: "+c);
	*/    //a.cross3(b);
	   
	//Vec4 G = vectorProduct(a, b);
	Vec4 G = a.cross3(b).normalize3();
	    //double d = -G.x*(b.x)-G.y*(b.y)-G.z*(b.z);
	    
	double value = c.dot3(G);
	    //double value = dotProduct(c, G);
	    
	    //Vec4 F = vectorProduct(c, G);
	    //double value = dotProduct(F, G);
	    
	    return value;
	}
	
	public static boolean isLocationInside(Position point, Position[] positions, Position pivot, Position maxYPos)
    {
		//double v1 = perpendicularDistanceFromAPlaneInsersectingTheOrigin(positions[0], positions[1], point);
		//da cambiare
		//double v2 = perpendicularDistanceFromAPlaneInsersectingTheOrigin(positions[1], positions[3], point);
		
		//double v3 = perpendicularDistanceFromAPlaneInsersectingTheOrigin(positions[3], positions[2], point);
		//da cambiare
		//double v4 = perpendicularDistanceFromAPlaneInsersectingTheOrigin(positions[2], positions[4], point);
		
		double distanceFromCenterAxis = perpendicularDistanceFromAPlaneInsersectingTheOrigin(pivot, maxYPos, point);
		//System.out.println(" "+v1+" "+v2+ " "+v3+" "+v4);
		Position posOnCenteredAxis = calculatePositionInMiddleAxis(pivot, maxYPos, point, distanceFromCenterAxis);
		
		Angle anglePoi = LatLon.greatCircleDistance(pivot, posOnCenteredAxis);
		Angle angleSlice = LatLon.greatCircleDistance(pivot, maxYPos);
		
		double ang1 = Operations.calculateSurfaceCameraViewAngle(posOnCenteredAxis);
		double ang2 = Operations.calculateSurfaceCameraViewAngle(pivot);
		
		boolean distBtwPovotUpdPoi = (ang1<=ang2)? true : false;
		
		//if((v1>0)&(v2>0)&(v3>0)&(v4>0)){
		//if((v1>0)&(v3>0)&(Math.abs(distanceFromCenterAxis)<SharedVariables.halfWidth)){
		if((anglePoi.compareTo(angleSlice)!=1)&(Math.abs(distanceFromCenterAxis)<SharedVariables.halfWidth)&(distBtwPovotUpdPoi)){
			return true;
		}
		return false;
		
    }
	

	
	public static Position calculatePositionInMiddleAxis(Position pivot, Position posmaxY, Position poi, double distance){
		Vec4 offset;
		//Vec4 normal = globe.computeSurfaceNormalAtPoint(point);
	
		// Compute a vector perpendicular to segment BC, and the globe normal
		// vector.
		
		Vec4 a = SharedVariables.computeWorldCoordinatesFromPosition(pivot);
	    Vec4 b = SharedVariables.computeWorldCoordinatesFromPosition(posmaxY);
	    Vec4 c = SharedVariables.computeWorldCoordinatesFromPosition(poi);
		
		Vec4 perpendicular = a.cross3(b);

		offset = perpendicular.normalize3();	
		offset = offset.multiply3(distance);
		Vec4 res = c.subtract3(offset);
		Position p =  SharedVariables.computePositionFromWorldCoordinates(res);
		
		return p;
	}
		

}
