package it.graphitech.worldwind;

import it.graphitech.colors.main;
import it.graphitech.objects.Node;
import it.graphitech.objects.Position;
import it.graphitech.objects.Vector2d;

public class testing {
/*
	public static void main(String[] args) {
	Position p = new Position(40, 0);
	Position p2 = new Position(80, 0);
	//Position p_node = new Position(60, 20);
	Position p_node = new Position(20, 0);
	//p.moveMeters(new Vector2d(2000000, 0));
	
	
	Vector2d prev = new Vector2d(p,
			p_node);
				//prev_spring_force.scale(1.d/Variables.lengthMiddleSegment);
	prev.normalize();
	double distance_prev = p_node.calculateDistance(p);
	System.out.println("prev distance: "+distance_prev);
	prev.scale(distance_prev);
	
	
	Vector2d next = new Vector2d(p2,
			p_node);
				//prev_spring_force.scale(1.d/Variables.lengthMiddleSegment);
	next.normalize();
	double distance_next = p_node.calculateDistance(p2);
	System.out.println("prev distance: "+distance_next);
	next.scale(distance_next);
	
	prev.add(next);
	
	prev.scale(0.5);
	
	p_node.moveMeters(prev);
	
	System.out.println(p_node);
}
*/
	public static void main(String[] args) {
		
		//angolo tra Position(-117.56412022339542 - 38.09767971157798) Position(-115.89165407198678 - 38.17341059039343) Position(-119.1961742104419 - 37.94998195511267): 177.42156509449086
		//angolo tra Position(-117.56412022339542 - 38.09767971157798) Position(-115.9018373198912 - 37.97103332332809) Position(-119.1961742104419 - 37.94998195511267): 170.4720810584441
		//angolo tra Position(-117.56412022339542 - 38.09767971157798) Position(-115.8504455441325 - 38.544160632478025) Position(-119.1961742104419 - 37.94998195511267): 189.43212474637588
		
		Position p1 = new Position(-7, -18);
		Position p2 = new Position(-5, 8);
		Position p3 = new Position(-9, -7);
		double angle = calculateAngle(p2, p3, p1);
		double angle2 = function2(p2, p3, p1);
		System.out.println(angle);
		System.out.println(angle2);
	}
	
	
	private static double calculateAngle(Position p1,Position p2,Position p3){
		double l1x = p2.getX() - p1.getX();
		double l1y = p2.getY() - p1.getY();
		double l2x = p3.getX() - p1.getX();
		double l2y = p3.getY() - p1.getY();
		double ang1 = Math.atan2(l1y, l1x);
		double ang2 = Math.atan2(l2y, l2x);

		double angle =  Math.abs(Math.toDegrees(ang2 - ang1));
		
	/*	
		if (angle > 180) {
				angle = 360 - angle;
			}
		*/	
			
			System.out.println("angolo tra "+p1+" "+p2+" "+p3+": "+angle );
			
			
			return angle;
	}
	
	
	private static double function2(Position center, Position current, Position previous) {
		  double v1x = current.getX() - center.getX(); 
		  double v1y = current.getY() - center.getY();

		  //need to normalize:
		  double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
		  v1x /= l1;
		  v1y /= l1;

		  double v2x = previous.getX() - center.getX();
		  double v2y = previous.getY() - center.getY();

		  //need to normalize:
		  double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
		  v2x /= l2;
		  v2y /= l2;    

		  double rad = Math.acos( v1x * v2x + v1y * v2y );

		  double degres = Math.toDegrees(rad);
		  return degres;
		}
	
	/*
public static void main(String[] args) {
	

	double minMagnitude = 2523.0;
	double maxMagnitude = 95952.0;
	double minValue = 2113.264679703133;
	double maxValue = 80369.390625;
	
	
	
	double magnitude = 3000;
	double res= getValue(magnitude, minMagnitude, maxMagnitude, minValue, maxValue);
	
	
	double magnitude2 = 50000;
	double res2= getValue(magnitude2, minMagnitude, maxMagnitude, minValue, maxValue);
	
	double res3= getValue(magnitude2+magnitude, minMagnitude, maxMagnitude, minValue, maxValue);
	
	System.out.println("val= "+magnitude+" val2= "+magnitude2+" tot: "+(magnitude+magnitude2));
	System.out.println("res= "+res+" res2: "+res2+" tot: "+(res+res2)+" tot2: "+res3);
}
*/

//return distance not in meters
	private static double getDistance(Node node1, Node node2) {
		Position p1= node1.getPosition();
		Position p2= node2.getPosition();
		double distance = p1.calculateDistance(p2);
		
		//System.out.println("distance in meters: "+distance);
		//System.out.println("dist/maxDistance: "+(distance*100000)/Variables.maxDistance);
		
		//return distance/Variables.lengthMiddleSegment;
		
		
		return distance;
		//return (distance*Variables.unitFactor)/Variables.maxDistance;
	}

	
	
	
	
private static double getValue(double magnitude, double minMagnitude, double maxMagnitude, double minValue,double maxValue){
		
		float ratio=(float)((magnitude-minMagnitude)/(maxMagnitude-minMagnitude));
		return ((maxValue-minValue)*ratio)+minValue;
		
	}
	
}
