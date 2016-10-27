package it.graphitech.worldwind;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;

public class testClass {
	
	public static void test2 (Position p1, Model dc){
		
		Vec4 point = dc.getGlobe().computePointFromPosition(p1);
		Position p2 = dc.getGlobe().computePositionFromPoint(point);
		
				
				System.out.println(p2);
	}
	
	
	public static void test (Position p1, Position p2,  Model dc){
		
		Vec4 point =  dc.getGlobe().computePointFromPosition(p1);
		Vec4 next =  dc.getGlobe().computePointFromPosition(p2);
		
		Vec4 res = generateParallelPoints(point, null, next, 10000, dc.getGlobe());
		
		Position resPos = dc.getGlobe().computePositionFromPoint(res);
		
		System.out.println(resPos);
		
		
	}
	
	 private static Vec4 generateParallelPoints(Vec4 point, Vec4 prev, Vec4 next, double halfWidth, Globe globe)
	    {
	        if ((point == null) || (prev == null && next == null))
	        {
	            String message = Logging.getMessage("nullValue.PointIsNull");
	            Logging.logger().severe(message);
	            throw new IllegalArgumentException(message);
	        }
	     
	        if (globe == null)
	        {
	            String message = Logging.getMessage("nullValue.GlobeIsNull");
	            Logging.logger().severe(message);
	            throw new IllegalArgumentException(message);
	        }

	        Vec4 offset;
	        Vec4 normal = globe.computeSurfaceNormalAtPoint(point);
	        System.out.println("normal vector: "+normal);
	        
	        
	        // Compute vector in the direction backward along the line.
	        Vec4 backward = (prev != null) ? prev.subtract3(point) : point.subtract3(next);

	        // Compute a vector perpendicular to segment BC, and the globe normal vector.
	        Vec4 perpendicular = backward.cross3(normal);

	        double length;
	        // If both next and previous points are supplied then calculate the angle that bisects the angle current, next, prev.
	        if (next != null && prev != null && !Vec4.areColinear(prev, point, next))
	        {
	            // Compute vector in the forward direction.
	            Vec4 forward = next.subtract3(point);

	            // Calculate the vector that bisects angle ABC.
	            offset = forward.normalize3().add3(backward.normalize3());
	            offset = offset.normalize3();

	            // Compute the scalar triple product of the vector BC, the normal vector, and the offset vector to
	            // determine if the offset points to the left or the right of the control line.
	            double tripleProduct = perpendicular.dot3(offset);
	            if (tripleProduct < 0)
	            {
	                offset = offset.multiply3(-1);
	            }

	            // Determine the length of the offset vector that will keep the left and right lines parallel to the control
	            // line.
	            Angle theta = backward.angleBetween3(offset);
	            if (!Angle.ZERO.equals(theta)){
	            	
	            	System.out.println(" Determine the length of the offset vector");
	            	
	                length = halfWidth / theta.sin();
	            }
	            else
	                length = halfWidth;
	        }
	        else
	        {
	            offset = perpendicular.normalize3();
	            length = halfWidth;
	        }
	        offset = offset.multiply3(length);

	        // Determine the left and right points by applying the offset.
	        //Vec4 ptRight = point.add3(offset);
	       Vec4 ptLeft = point.subtract3(offset);

	        // Convert cartesian points to geographic.
	        //Position posLeft = globe.computePositionFromPoint(ptLeft);
	       // Position posRight = globe.computePositionFromPoint(ptRight);
	return ptLeft;
	        //leftPositions.add(posLeft);
	        //rightPositions.add(posRight);
	    }
	    
}
