package it.graphitech.objects;

public class Position {

	double x;
	double y;
//	public Position(int x, int y) {
//		super();
//		this.x = x;
//		this.y = y;
//	}
	
	public Position(double x, double y) {
		super();
		
		this.x=x;
		this.y=y;
//		this.x = arrotonda(x,3);
//		this.y = arrotonda(y,3);
//		
		
		
		
	}
	
	
	// ARROTONDAMENTO CLASSICO
//	   public static double arrotonda(double value, int numCifreDecimali) {
//	      double temp = Math.pow(10, numCifreDecimali);
//	      return Math.round(value * temp) / temp;
//	   }
//	
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
public void move(Vector2d vector){
		
		this.x=this.x+vector.getX();
		this.y=this.y+vector.getY();
		
		
		//this.x = arrotonda(x,3);
		//this.y = arrotonda(y,3);
	}
	
public void moveMeters(Vector2d vector){
	
	/*this.x=this.x+vector.getX();
	this.y=this.y+vector.getY();
	*/
	//Position, decimal degrees
	 double dn =vector.getY();
	 double de  =vector.getX();
	 
	 //Earth’s radius, sphere
	 double R=6378137;

	
	 //Coordinate offsets in radians
	 double dLat = dn/R;
	 double dLon = de/(R*Math.cos(Math.PI*this.y/180));

	 //OffsetPosition, decimal degrees
	 this.y=this.y+ dLat * 180/Math.PI;
			 this.x=this.x + dLon * 180/Math.PI; 
	//this.x = arrotonda(x,3);
	//this.y = arrotonda(y,3);
}


@Override
public String toString() {
	// TODO Auto-generated method stub
	return "Position("+x+" - "+y+")";
}

/*
 private static double calculateDistance(Position xy_start, Position xy_dest ){
//	double Sum = 0.0;
//   
//       Sum = Sum + Math.pow((xy_start.getX()-xy_dest.getX()),2.0);
//       Sum = Sum + Math.pow((xy_start.getY()-xy_dest.getY()),2.0);
//       
       
       double a = xy_start.x - xy_dest.x;
		double b = xy_start.y - xy_dest.y;
		return Math.sqrt(a * a + b * b);
       
   // return Math.sqrt(Sum);
}
 */
public static double calculateDistance(Position xy_start, Position xy_dest ){
	return distFrom(xy_start.y, xy_start.x, xy_dest.y, xy_dest.x);
}


//distance in meters
 public  double calculateDistance( Position xy_dest ){
//	double Sum = 0.0;
//   
//       Sum = Sum + Math.pow((x-xy_dest.getX()),2.0);
//       Sum = Sum + Math.pow((y-xy_dest.getY()),2.0);
//       
//    return Math.sqrt(Sum);
//    
    
	return distFrom(y, x, xy_dest.y, xy_dest.x);
	//return calculateDistance(new Position(y, x),xy_dest); 
//    double a = x - xy_dest.x;
//  		double b = y - xy_dest.y;
//  		return Math.sqrt(a * a + b * b);
}


 private static float distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return new Float(dist * meterConversion).floatValue();
	    }
 
@Override
public boolean equals(Object obj) {
	// TODO Auto-generated method stub
	if(obj instanceof Position){
	Position posObj=(Position) obj;
		if((posObj.getX()==this.x)&(posObj.getY()==this.y)){
			return true;
		}
	}
	return false;
		
}
 
 
}
