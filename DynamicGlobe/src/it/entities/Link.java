package it.entities;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.Logging;

public class Link {
	
	 String id;
	
	 String from;
	 double from_lat;
	 double from_lon;
	 Position from_pos;
	//private String state;
	
	 String to;
	 double to_lat;
	 double to_lon;
	 Position to_pos;
	
	 double distance;
	 double distance2;
	 
	public Link(String id){
		this.id=id;
	}
	

	public Link(){
		
	}
	
	
	public void setLocation(String from, double from_lat, double from_lon,String to,double to_lat, double to_lon) {
		this.from = from;
		this.to = to;
		
		this.from_lat=from_lat;
		this.from_lon=from_lon;
		
		this.to_lat=to_lat;
		this.to_lon=to_lon;
		
		//calculateGeographicDistance();
		Position from_pos = Position.fromDegrees(this.getFrom_lat(), this.getFrom_lon());
		Position to_pos = Position.fromDegrees(this.getTo_lat(), this.getTo_lon());
		
		this.to_pos=to_pos;
		this.from_pos=from_pos;
	}

	
	
	
	public String getId() {
		return id;
	}




	public double getDistance2() {
		return distance2;
	}


	public Position getFrom_pos() {
		return from_pos;
	}


	public Position getTo_pos() {
		return to_pos;
	}


	public double getDistance() {
		return distance;
	}


	public String getFrom() {
		return from;
	}

	

	public double getFrom_lat() {
		return from_lat;
	}

	

	public double getFrom_lon() {
		return from_lon;
	}

	

	public String getTo() {
		return to;
	}

	

	public double getTo_lat() {
		return to_lat;
	}

	

	public double getTo_lon() {
		return to_lon;
	}

	

	public void calculateGeographicDistance(){
		LatLon from = LatLon.fromDegrees(this.getFrom_lat(), this.getFrom_lon());
		LatLon to = LatLon.fromDegrees(this.getTo_lat(), this.getTo_lon());

	
		
double distance= ellipsoidalDistance(from, to, 6378137, 6356752.3);
System.out.println("distance: "+distance);
double distance2= calculateDistance(this.getFrom_lat(), this.getFrom_lon(),this.getTo_lat(), this.getTo_lon());
this.distance=distance;
this.distance2=distance2;

	}
	
	
	
	private static final double calculateDistance(double lat1, double lon1, double lat2, double lon2)
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
	
	 private static double ellipsoidalDistance(LatLon p1, LatLon p2, double equatorialRadius, double polarRadius)
	    {
	        // TODO: I think there is a non-iterative way to calculate the distance. Find it and compare with this one.
	        // TODO: What if polar radius is larger than equatorial radius?
	        final double F = (equatorialRadius - polarRadius) / equatorialRadius; // flattening = 1.0 / 298.257223563;
	        final double R = 1.0 - F;
	        final double EPS = 0.5E-13;

	        if (p1 == null || p2 == null)
	        {
	            String message = Logging.getMessage("nullValue.PositionIsNull");
	            Logging.logger().severe(message);
	            throw new IllegalArgumentException(message);
	        }

	        // Algorithm from National Geodetic Survey, FORTRAN program "inverse,"
	        // subroutine "INVER1," by L. PFEIFER and JOHN G. GERGEN.
	        // http://www.ngs.noaa.gov/TOOLS/Inv_Fwd/Inv_Fwd.html
	        // Conversion to JAVA from FORTRAN was made with as few changes as possible
	        // to avoid errors made while recasting form, and to facilitate any future
	        // comparisons between the original code and the altered version in Java.
	        // Original documentation:
	        // SOLUTION OF THE GEODETIC INVERSE PROBLEM AFTER T.VINCENTY
	        // MODIFIED RAINSFORD'S METHOD WITH HELMERT'S ELLIPTICAL TERMS
	        // EFFECTIVE IN ANY AZIMUTH AND AT ANY DISTANCE SHORT OF ANTIPODAL
	        // STANDPOINT/FOREPOINT MUST NOT BE THE GEOGRAPHIC POLE
	        // A IS THE SEMI-MAJOR AXIS OF THE REFERENCE ELLIPSOID
	        // F IS THE FLATTENING (NOT RECIPROCAL) OF THE REFERNECE ELLIPSOID
	        // LATITUDES GLAT1 AND GLAT2
	        // AND LONGITUDES GLON1 AND GLON2 ARE IN RADIANS POSITIVE NORTH AND EAST
	        // FORWARD AZIMUTHS AT BOTH POINTS RETURNED IN RADIANS FROM NORTH
	        //
	        // Reference ellipsoid is the WGS-84 ellipsoid.
	        // See http://www.colorado.edu/geography/gcraft/notes/datum/elist.html
	        // FAZ is forward azimuth in radians from pt1 to pt2;
	        // BAZ is backward azimuth from point 2 to 1;
	        // S is distance in meters.
	        //
	        // Conversion to JAVA from FORTRAN was made with as few changes as possible
	        // to avoid errors made while recasting form, and to facilitate any future
	        // comparisons between the original code and the altered version in Java.
	        //
	        //IMPLICIT REAL*8 (A-H,O-Z)
	        //  COMMON/CONST/PI,RAD

	        double GLAT1 = p1.getLatitude().radians;
	        double GLAT2 = p2.getLatitude().radians;
	        double TU1 = R * Math.sin(GLAT1) / Math.cos(GLAT1);
	        double TU2 = R * Math.sin(GLAT2) / Math.cos(GLAT2);
	        double CU1 = 1. / Math.sqrt(TU1 * TU1 + 1.);
	        double SU1 = CU1 * TU1;
	        double CU2 = 1. / Math.sqrt(TU2 * TU2 + 1.);
	        double S = CU1 * CU2;
	        double BAZ = S * TU2;
	        double FAZ = BAZ * TU1;
	        double GLON1 = p1.getLongitude().radians;
	        double GLON2 = p2.getLongitude().radians;
	        double X = GLON2 - GLON1;
	        double D, SX, CX, SY, CY, Y, SA, C2A, CZ, E, C;
	        do
	        {
	            SX = Math.sin(X);
	            CX = Math.cos(X);
	            TU1 = CU2 * SX;
	            TU2 = BAZ - SU1 * CU2 * CX;
	            SY = Math.sqrt(TU1 * TU1 + TU2 * TU2);
	            CY = S * CX + FAZ;
	            Y = Math.atan2(SY, CY);
	            SA = S * SX / SY;
	            C2A = -SA * SA + 1.;
	            CZ = FAZ + FAZ;
	            if (C2A > 0.)
	            {
	                CZ = -CZ / C2A + CY;
	            }
	            E = CZ * CZ * 2. - 1.;
	            C = ((-3. * C2A + 4.) * F + 4.) * C2A * F / 16.;
	            D = X;
	            X = ((E * CY * C + CZ) * SY * C + Y) * SA;
	            X = (1. - C) * X * F + GLON2 - GLON1;
	            //IF(DABS(D-X).GT.EPS) GO TO 100
	        }
	        while (Math.abs(D - X) > EPS);

	        //FAZ = Math.atan2(TU1, TU2);
	        //BAZ = Math.atan2(CU1 * SX, BAZ * CX - SU1 * CU2) + Math.PI;
	        X = Math.sqrt((1. / R / R - 1.) * C2A + 1.) + 1.;
	        X = (X - 2.) / X;
	        C = 1. - X;
	        C = (X * X / 4. + 1.) / C;
	        D = (0.375 * X * X - 1.) * X;
	        X = E * CY;
	        S = 1. - E - E;
	        S = ((((SY * SY * 4. - 3.) * S * CZ * D / 6. - X) * D / 4. + CZ) * SY
	            * D + Y) * C * equatorialRadius * R;

	        return S;
	    }
	
}
