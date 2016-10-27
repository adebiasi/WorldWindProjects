package it.graphitech.smeSpire.lines.cubicCurve;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.util.Logging;
import it.graphitech.smeSpire.SharedVariables;

import java.awt.Color;
import java.util.ArrayList;

public class RenderableControlPoints {

	private ArrayList<gov.nasa.worldwind.geom.Position> positions = new ArrayList<gov.nasa.worldwind.geom.Position>();
	public ArrayList<Vec4> points = new ArrayList<Vec4>();
	
	public int getSize(){
		return points.size();
	}
	
	public void setControlPointPosition(ArrayList<Position> positions){
		this.positions.clear();
this.positions.addAll(positions);
//calculatePoints();

	}
	
	
	public void setControlPoint(ArrayList<Vec4> points){
		this.points.clear();
this.points.addAll(points);
//calculatePoints();
	}
	
	public ArrayList<gov.nasa.worldwind.geom.Position> getPositions() {
		return positions;
	}


	public void calculatePoints() {
		points.clear();
		for(Position p: positions){
		//	System.out.println("position: "+p);
			points.add(SharedVariables.computeWorldCoordinatesFromPosition(p));
		}
	}
	
	
	public void calculatePointsOver2DMap() {
		points.clear();
		for(Position p: positions){
			//System.out.println("position: "+p);
			
			
			//Globe globe =  new EarthFlat();
			//if(globe!=null){
			//Vec4 point = globe.computePointFromPosition(p);			
			Vec4 point = geodeticToCartesian(p.getLatitude(),p.getLongitude(), p.getElevation(), "PROJECTION_MERCATOR");
			
			points.add(point);
			
			//System.out.println("point added");
			//}
		}
	}

	
	protected Vec4 geodeticToCartesian(Angle latitude, Angle longitude, double metersElevation,String projection)
    {
		  double equatorialRadius = 6378206.4;   // ellipsoid equatorial getRadius, in meters
		 
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4 cart = null;
        if (projection.equals("PROJECTION_LAT_LON"))
        {
            // Lat/Lon projection - plate carree
            cart = new Vec4(equatorialRadius * longitude.radians,
                equatorialRadius * latitude.radians,
                metersElevation);
        }
        else if (projection.equals("PROJECTION_MERCATOR"))
        {
            // Mercator projection
            if (latitude.degrees > 75)
                latitude = Angle.fromDegrees(75);
            if (latitude.degrees < -75)
                latitude = Angle.fromDegrees(-75);
            cart = new Vec4(equatorialRadius * longitude.radians,
                equatorialRadius * Math.log(Math.tan(Math.PI / 4 + latitude.radians / 2)),
                metersElevation);
        }
        else if (projection.equals("PROJECTION_SINUSOIDAL"))
        {
            // Sinusoidal projection
            double latCos = latitude.cos();
            cart = new Vec4(
                latCos > 0 ? equatorialRadius * longitude.radians * latitude.cos() : 0,
                equatorialRadius * latitude.radians,
                metersElevation);
        }
        else if (projection.equals("PROJECTION_MODIFIED_SINUSOIDAL"))
        {
            // Modified Sinusoidal projection
            double latCos = latitude.cos();
            cart = new Vec4(
                latCos > 0 ? equatorialRadius * longitude.radians * Math.pow(latCos, .3) : 0,
                equatorialRadius * latitude.radians,
                metersElevation);
        }
        else
        {
            String message = Logging.getMessage("generic.UnknownProjection", projection);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return cart;
    }
	public Position getOrigin(){
    	return positions.get(0);
    }
	
	 public Position getDestination(){
	    	return positions.get(positions.size()-1);
	    }
}
