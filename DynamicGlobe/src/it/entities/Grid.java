package it.entities;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AirspaceLayer;
import it.GeoOperation2;
import it.Operations;
import it.SharedVariables;
import it.ManageRendObjects.ManageAirspaces;
import it.main.MainDeformableGlobe;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

public class Grid {

	public int numPoints = 7;
	public Position[][] grid = new Position[numPoints][numPoints];
	private Position[][] oppositeGrid = new Position[numPoints][numPoints];
	private Position[][] deformedGrid = new Position[numPoints][numPoints];

	private Position pivot;
	private Position lastPointOnMiddleAxis;

	private double paramDist1;
	private double paramDist2;
	
	public void generateSurfaceGrid(
			Position poiPos, 
			Position horizPos,
			Position oppPos
			//,Position cameraPos
			, AirspaceLayer airspacesLayer
			//, int maxValue
			, double v) {
		//double halfWidth = 3500000;
		double interpolationValue = 0;
		Globe globe = MainDeformableGlobe.world.getModel().getGlobe();

		double singleWidth;

		singleWidth = (SharedVariables.halfWidth * 2) / (numPoints - 1);
		//Vec4 wcInt = Operations.computePointFromLocation(intPos);
		//Vec4 wcPoi = Operations.computePointFromLocation(poiPos);
		Vec4 wcOppPos = Operations.computePointFromLocation(oppPos);
		//System.out.println("generateSurfaceGrid");
		Vec4 prevWcInterpolatedCenteredYPos = null;
		
		for (int currPointInY = 0; currPointInY < numPoints; currPointInY++) {

			double valY = ((double) (currPointInY))
					/ ((double) (numPoints - 1));

			//System.out.println("valY: "+valY);
			
			Position updCenteredYPos = Operations.interpolateGreatCircle(valY*v, horizPos, poiPos);
			Position updOppCenteredYPos = Operations.interpolateGreatCircle(valY*v, horizPos,oppPos);
			
			/*
			double step = 0.02;
			for (double i = step; i < 1; i = i + step) {
				//Position test = Position.interpolateGreatCircle(i, poiPos,horizPos );
				//ManageAirspaces.generateAirspaces(test, airspacesLayer, Color.GREEN,0);
				
				//Position test2 = Position.interpolateGreatCircle(i+(step/2), cameraPos, poiPos);
				Position test2 = Position.interpolateGreatCircle(i+(step/2), poiPos,cameraPos);
				ManageAirspaces.generateAirspaces(test2, airspacesLayer, Color.GREEN,0);
			}
			*/
			
			
			Position interpolatedCenteredYPos = Operations.interpolateGreatCircle(interpolationValue,
					updCenteredYPos, updOppCenteredYPos);
			Vec4 wcInterpolatedCenteredYPos = Operations.computePointFromLocation(interpolatedCenteredYPos);
			
			if(valY==0){
				this.setPivot(interpolatedCenteredYPos);
			}
			if(valY==1){
				this.setLastPointOnMiddleAxis(interpolatedCenteredYPos);
			}
			
			
			//Vec4 altitudeVec = calculateVectorForAltitude(globe, updCenteredYPos, updOppCenteredYPos, wcInterpolatedCenteredYPos, interpolationValue);
		
			

			for (int currPointInX = 0; currPointInX <= numPoints/2; currPointInX++) {

				if(currPointInX!=((double)numPoints/2)){
		
				double percIndex;

				if (numPoints % 2 == 1) {
					percIndex = (currPointInX) - ((numPoints) / 2);
				} else {
					percIndex = ((double) currPointInX)
							- (((double) (numPoints - 1)) / 2);
				}
				double width = singleWidth * percIndex;

				try {

					Position[] defPosLR;

					//System.out.println("half width: "+width);
					
					if(prevWcInterpolatedCenteredYPos==null){
					defPosLR = Operations.generateParallelPointsOverTheGlobe(
							wcInterpolatedCenteredYPos, null, wcOppPos, Math.abs(width),
							globe);
					}else{
						
						defPosLR = Operations.generateParallelPointsOverTheGlobe(
								wcInterpolatedCenteredYPos, null, prevWcInterpolatedCenteredYPos, Math.abs(width),
								globe);	
					
					/*	Position[] t  = Operations.test(
								wcInterpolatedCenteredYPos, null, prevWcInterpolatedCenteredYPos, Math.abs(width),
								globe);	
						ManageAirspaces.generateAirspaces(t[0], airspacesLayer, Color.BLUE,2);
						ManageAirspaces.generateAirspaces(t[1], airspacesLayer, Color.YELLOW,2);
						*/
					}
					
					
					
					
						Position rightDefPos = defPosLR[0];			
						
						
						
						// System.out.println("PRENDO LA DESTRA: "+rightDefPos);
						grid[currPointInX][currPointInY] = rightDefPos;
						//ManageAirspaces.generateAirspaces(rightDefPos, airspacesLayer, Color.ORANGE,2);
						
						/*
						for(double i =0;i<1;i=i+0.2){
						Position t = Operations.interpolateGreatCircle(i,
								rightDefPos, interpolatedCenteredYPos);	
						ManageAirspaces.generateAirspaces(t, airspacesLayer, Color.PINK,2);
						}
						*/
						
						Position leftDefPos = defPosLR[1];
					//	 System.out.println("PRENDO LA SINISTRA: "+leftDefPos);
						grid[(numPoints-1)-currPointInX][currPointInY] = leftDefPos;
						//ManageAirspaces.generateAirspaces(leftDefPos, airspacesLayer, Color.ORANGE,2);
						/*
						if(currPointInY==(numPoints)-1){
						ManageAirspaces.generateAirspaces(leftDefPos, airspacesLayer, Color.ORANGE);
						}else{
							ManageAirspaces.generateAirspaces(leftDefPos, airspacesLayer, Color.BLACK);	
						}
						*/
						
					//grid[currPointInX][currPointInY] = defPos;

				} catch (Exception e) {
					e.printStackTrace();
				}
				;

			}
			}
			
			prevWcInterpolatedCenteredYPos = wcInterpolatedCenteredYPos;
			
		}
		
		//System.out.println("fine generated grid");
	}
	
	
/*
	public void generateOppositeSurfaceGrid(Position poiPos, Position horizPos,
			Position oppPos, 
		//	AirspaceLayer airspacesLayer,
			int maxValue) {
		double halfWidth = 3500000;
		double interpolationValue = 1;

		Globe globe = MainDeformableGlobe.world.getModel().getGlobe();

		double singleWidth;

		singleWidth = (halfWidth * 2) / (numPoints - 1);
		Vec4 wcPoi = Operations.computePointFromLocation(poiPos);
		
		
		for (int currPointInY = 0; currPointInY < numPoints; currPointInY++) {

			double valY = ((double) (currPointInY))
					/ ((double) (numPoints - 1));

			Position updCenteredYPos = Operations.interpolation(valY, horizPos, poiPos);
			Position updOppCenteredYPos = Operations.interpolation(valY, horizPos, oppPos);
			
			Position interpolatedCenteredYPos = Operations.interpolation(interpolationValue,
					updCenteredYPos, updOppCenteredYPos);
			Vec4 wcInterpolatedCenteredYPos = Operations.computePointFromLocation(interpolatedCenteredYPos);
			
			//Vec4 altitudeVec = calculateVectorForAltitude(globe, updCenteredYPos, updOppCenteredYPos, wcInterpolatedCenteredYPos, interpolationValue);
		
			

			for (int currPointInX = 0; currPointInX <= numPoints/2; currPointInX++) {

				if(currPointInX!=((double)numPoints/2)){
				
				double percIndex;

				if (numPoints % 2 == 1) {
					percIndex = (currPointInX) - ((numPoints) / 2);
				} else {
					percIndex = ((double) currPointInX)
							- (((double) (numPoints - 1)) / 2);
				}
				double width = singleWidth * percIndex;

				try {

					Position[] defPosLR;
 
					defPosLR = Operations.generateParallelPoints(
							wcInterpolatedCenteredYPos, wcPoi, null, Math.abs(width),
							globe);
					
					
					// System.out.println("PRENDO LA DESTRA: "+percIndex+" "+currPointInX);
						Position rightDefPos = defPosLR[0];					
						oppositeGrid[currPointInX][currPointInY] = rightDefPos;
					//	 System.out.println("PRENDO LA SINISTRA: "+percIndex+" "+currPointInX);
						Position leftDefPos = defPosLR[1];
						oppositeGrid[(numPoints-1)-currPointInX][currPointInY] = leftDefPos;
				
				} catch (Exception e) {
				}
				;

			}
			}
		}
	}
*/
	public Position updatePoiPosition(Position horizPos, 
			Position poiPos, Position oppPoiPos,
			//Position limitPos, Position oppLimitPos, 
			//double coeffPosNode,
			//int value,int maxValue
			//double deformUserParam,
			double interpolationValue,
			//double valY,
			AirspaceLayer airspaces
			) {
			Globe globe = MainDeformableGlobe.world.getModel().getGlobe();
			
				Position updCenteredYPos = poiPos;
				Position updOppCenteredYPos = oppPoiPos;	
	//	Position updCenteredYPos = Operations.interpolation(valY*coeffPosNode, horizPos, poiPos);
	//	Position updOppCenteredYPos = Operations.interpolation(valY*coeffPosNode, horizPos, oppPoiPos);
		//double interpolationValue = SharedVariables.calculateDistortionCoefficent(deformUserParam,SharedVariables.distCoeff2,valY);
		
		
			Position interpolatedCenteredYPos = calculateInterpolatedPositionFrom3Positions(interpolationValue, updCenteredYPos, horizPos, updOppCenteredYPos);		
			Vec4 wcInterpolatedCenteredYPos	= globe.computePointFromPosition(interpolatedCenteredYPos);
	
			
			//double distanceFromPoiHorizonOppPoi = calculateDistanceFrom3Positions(updCenteredYPos, horizPos, updOppCenteredYPos);
			
			double distanceFromPoiHorizon = Operations.getDistance(horizPos, updCenteredYPos);
		
			
			Vec4 altitudeVec = 
					//calculateVectorForAltitude(globe, updCenteredYPos, updOppCenteredYPos, wcInterpolatedCenteredYPos, interpolationValue);
					//calculateVectorForAltitude(globe, distanceFromPoiHorizonOppPoi, wcInterpolatedCenteredYPos, interpolationValue);
					calculateVectorForAltitude(globe, distanceFromPoiHorizon, wcInterpolatedCenteredYPos, interpolationValue);
			return addVectortoPosition(globe, interpolatedCenteredYPos, altitudeVec);
	}
	
	
	public void setDistortionParams(double distort1, double distort2){
		
		this.paramDist1=distort1;
		this.paramDist2=distort2;
		
	}
	
	public double getParamDist1(){
		return paramDist1;
	}
	
	public double getParamDist2(){
		return paramDist2;
	}
	
	public Position updateExtraPoints(
			//Position horizPos, 
			//Position poiPos, 
			//Position oppPoiPos,
			
			Position centeredPivot,
			Position centeredPoi,			
			Position oppCenteredPoi,
			//Position lineOnX1, Position lineOnX2,
			//Position lineOnY1, Position lineOnY2,
			//Position limitPos, Position oppLimitPos, 
			//double coeffPosNode,
			double interpolationValue,
			//int value,int maxValue
			//double deformUserParam,
			//double valY,
			double distance,
			AirspaceLayer airspaces
			//Position centeredPos
			) {
	
			Globe globe = MainDeformableGlobe.world.getModel().getGlobe();
	
	
			//valY va da 0 a 1, 
			//	0 sono i punti sull'orizzonte
			//	1 sono i punti opposti all'orizzonte
			//interpolationValue va da 0 a 1
			// 0 poca distorsione
			// 1 alta distorsione			
			//double interpolationValue =SharedVariables.calculateDistortionCoefficent(deformUserParam,SharedVariables.distCoeff2,valY);
		

			Position centerAxisPos = calculateInterpolatedPositionFrom3Positions(interpolationValue, centeredPoi, centeredPivot, oppCenteredPoi);		
			//Position centerAxisPos = globe.computePositionFromPoint(wcInterpolatedCenteredYPos);
			Vec4 wcCenterAxisPos = Operations.computePointFromLocation(centerAxisPos);

			
			////////////////////////////////////
			//////////////////ALTITUDE///////////
			////////////////////////////////////
			//double distancePosAFromHorizon = Operations.getDistance(centeredPoi, horizPos);	
			//double distancePosBFromHorizon = Operations.getDistance(oppCenteredPoi, horizPos);				
			//double distanceFromPoiHorizonOppPoi = distancePosAFromHorizon+distancePosBFromHorizon;
			
			//double distanceFromPoiHorizonOppPoi = calculateDistanceFrom3Positions(centeredPoi, centeredPivot, oppCenteredPoi);
			
			double distanceFromPoiHorizon = Operations.getDistance(centeredPivot, centeredPoi);
			//double distanceFromPoiUpdPoi = Operations.getDistance(centerAxisPos, centeredPoi);
			
			//Angle anglePoi = LatLon.greatCircleDistance(centeredPivot, centeredPoi);
			
			//System.out.println("ANNNNGLEEEEEE: "+anglePoi.degrees);
			
			Vec4 altitudeVec = calculateVectorForAltitude(globe, 
					//updCenteredYPos, updOppCenteredYPos, 
					//distance,
					//distanceFromPoiHorizonOppPoi,
					distanceFromPoiHorizon,
					wcCenterAxisPos, interpolationValue);
		
			Position[] movedPoints=Operations.generateParallelPointsOverTheGlobe(wcCenterAxisPos, globe.computePointFromPosition(centeredPivot), null, distance, globe);		
			Position movedPoint;
			
			
			// ManageAirspaces.generateAirspaces(centerAxisPos, airspaces,Color.YELLOW,0);
			//ManageAirspaces.generateAirspaces(centeredPivot, airspaces,Color.BLUE,0);
		
			double ang1 = Operations.calculateSurfaceCameraViewAngle(centeredPivot);
			double ang2 = Operations.calculateSurfaceCameraViewAngle(centerAxisPos);
			
			boolean distBtwPovotUpdPoi = (ang1<=ang2)? true : false;
			
			
			//System.out.println("ang1: "+ang1);
			//System.out.println("ang2: "+ang2);
			//System.out.println("distBtwPovotUpdPoi: "+distBtwPovotUpdPoi);
			
			//System.out.println("interpolationValue: "+interpolationValue);
			
			//qua da cambiare lo 0.5
			/*
			if(interpolationValue<0.5){
				movedPoint=movedPoints[1];
			}else{
				movedPoint=movedPoints[0];
			}
			*/
			if(!distBtwPovotUpdPoi){
				movedPoint=movedPoints[1];
			}else{
				movedPoint=movedPoints[0];
			}
			
			Position movedPoiWithAltitude=addVectortoPosition(globe, movedPoint, altitudeVec);	
			return movedPoiWithAltitude;
		
	}
	
	
	public void generateDeformedGrid(Position poiPos, Position horizPos,
			Position oppPoiPos,
			AirspaceLayer airspacesLayer, 
			//int value,
			//int maxValue,
			//double interpolationValue,
			double deformUserParam,
			double deformUserParam2,
			double coeffPosNode) {

		double halfWidth = SharedVariables.halfWidth;
		
		//double interpolationValue = (double) value / (maxValue - 1);

		Globe globe = MainDeformableGlobe.world.getModel().getGlobe();

		double singleWidth;

		singleWidth = (halfWidth * 2) / (numPoints - 1);
		Vec4 wcOppPoiPos = Operations.computePointFromLocation(oppPoiPos);

		//Vec4 prevWcInterpolatedCenteredYPos = null;
		//double prevDistance = 0;
		
		//System.out.println("generateDeformedGrid");
		
		//airspacesLayer.removeAllAirspaces();
		
		for (int currPointInY = 0; currPointInY < numPoints; currPointInY++) {

			
			double valY = ((double) (currPointInY))
					/ ((double) (numPoints - 1));

		
			
		
			Position updCenteredYPos = Operations.interpolateGreatCircle(valY*coeffPosNode, horizPos, poiPos);
			Position updOppCenteredYPos = Operations.interpolateGreatCircle(valY*coeffPosNode, horizPos, oppPoiPos);
			
		//	double distance = Operations.getDistance(updCenteredYPos, updOppCenteredYPos);	
				/*
			double distancePosAFromHorizon = Operations.getDistance(updCenteredYPos, horizPos);	
			double distancePosBFromHorizon = Operations.getDistance(updOppCenteredYPos, horizPos);	
			double distanceFromPoiHorizonOppPoi = distancePosAFromHorizon+distancePosBFromHorizon;
			*/
			//double distanceFromPoiHorizonOppPoi = calculateDistanceFrom3Positions(updCenteredYPos, horizPos, updOppCenteredYPos);
			
			double distanceFromPoiHorizon = Operations.getDistance(updCenteredYPos, horizPos);
			
			//System.out.println("deformUserParam: "+deformUserParam+" , deformUserParam2: "+deformUserParam2);
			//valY va da 0 a 1, 
			//	0 sono i punti sull'orizzonte
			//	1 sono i punti più lontani dall'orizzonte
			//interpolationValue va da 0 a 1
			// 0 poca distorsione
			// 1 alta distorsione			
			double interpolationValue =SharedVariables.calculateDistortionCoefficent(deformUserParam,deformUserParam2,valY);
			//System.out.println("valY: "+valY+" originalInterValue: "+originalInterpolationValue+" nuovoInterValue: "+interpolationValue);			
			System.out.println("valY: "+valY+" InterValue: "+interpolationValue);
			Position centerAxisPos = calculateInterpolatedPositionFrom3Positions(interpolationValue, updCenteredYPos, horizPos, updOppCenteredYPos);	
			Vec4 wcCenterAxisPos = Operations.computePointFromLocation(centerAxisPos);
			Vec4 altitudeVec = calculateVectorForAltitude(globe, 
					//updCenteredYPos, updOppCenteredYPos, 
					//distance,
					//distanceFromPoiHorizonOppPoi,
					distanceFromPoiHorizon,
					wcCenterAxisPos, interpolationValue);
		
			
			
			for (int currPointInX = 0; currPointInX <= numPoints/2; currPointInX++) {

				if(currPointInX!=((double)numPoints/2)){
				
				double percIndex;

				if (numPoints % 2 == 1) {
					percIndex = (currPointInX) - ((numPoints) / 2);
				} else {
					percIndex = ((double) currPointInX)
							- (((double) (numPoints - 1)) / 2);
				}
				double width = singleWidth * percIndex;

			//	try {

					Position[] defPosLR;

			
						defPosLR = Operations.generateParallelPointsOverTheGlobe(
								wcCenterAxisPos, null, wcOppPoiPos, Math.abs(width),
								globe);
						
					//if (width < 0) {
					//	 System.out.println("PRENDO LA DESTRA: "+percIndex+" "+currPointInX);
						Position rightDefPos = defPosLR[0];
						//
						
						
						rightDefPos=addVectortoPosition(globe, rightDefPos, altitudeVec);
						//ManageAirspaces.generateAirspaces(rightDefPos, airspacesLayer, Color.GREEN,1);
						//if(prevWcInterpolatedCenteredYPos!=null){
						//ManageAirspaces.generateAirspaces(SharedVariables.computePositionFromWorldCoordinates(prevWcInterpolatedCenteredYPos), airspacesLayer, Color.GREEN,1);
						//}
				//		System.out.println("deformedGrid["+currPointInX+"]["+currPointInY+"]");
						deformedGrid[currPointInX][currPointInY] = rightDefPos;
					//	ManageAirspaces.generateAirspaces(rightDefPos, airspacesLayer, Color.ORANGE,2);
						
					//} else {
						// System.out.println("PRENDO LA SINISTRA: "+percIndex+" "+currPointInX);
						Position leftDefPos = defPosLR[1];
						leftDefPos=addVectortoPosition(globe, leftDefPos, altitudeVec);
						
						deformedGrid[(numPoints-1)-currPointInX][currPointInY] = leftDefPos;
				//		ManageAirspaces.generateAirspaces(leftDefPos, airspacesLayer, Color.ORANGE,2);
						
	
			}
			}		
			
			
		
		}
			
	}

	/*
private double calculateDistanceFrom3Positions(Position p1, Position p2, Position p3){

	double distancePosAFromHorizon = Operations.getDistance(p1, p2);	
	double distancePosBFromHorizon = Operations.getDistance(p2, p3);	
return distancePosAFromHorizon+distancePosBFromHorizon;
	}
	*/
	private Position calculateInterpolatedPositionFrom3Positions(double interpolationValue,Position updCenteredYPos, Position horizPos, Position updOppCenteredYPos){
		//noooooooooooooo
		Position interpolatedCenteredYPos;
		
		//il valore ha come riferimento l'orizzonte
		if(interpolationValue>=0.5){
			double newInterpolationValue=(interpolationValue-0.5)*2;
			//newInterpolationValue = newInterpolationValue+additionalDistortion;
			//System.out.println("sopra 0.5: interpolationValue diventa: "+newInterpolationValue);
			 interpolatedCenteredYPos = Operations.interpolateGreatCircle(newInterpolationValue, horizPos, updOppCenteredYPos);
			
		}else{
			double newInterpolationValue=(interpolationValue)*2;
			//newInterpolationValue = newInterpolationValue+additionalDistortion;
			//System.out.println("sotto 0.5: interpolationValue diventa: "+newInterpolationValue);
			 interpolatedCenteredYPos = Operations.interpolateGreatCircle(newInterpolationValue,updCenteredYPos , horizPos);
		}
				
	//	Vec4 wcInterpolatedCenteredYPos = Operations.computePointFromLocation(interpolatedCenteredYPos);
	//return wcInterpolatedCenteredYPos;
		return interpolatedCenteredYPos;
	}
	
	public static Position getAntipode(Position in) {
		double outputLat = 0;
		double outputLon = 0;
			        if (in.getLatitude().degrees > 0) outputLat = in.getLatitude().degrees - Math.PI;
			        else outputLat = in.getLatitude().degrees + Math.PI;
			        
			        
			        outputLon = -in.getLongitude().degrees;
			        
			        return Position.fromDegrees(outputLat, outputLon);
			    }
	
	//private static Vec4 calculateVectorForAltitude(Globe globe, Position posA, Position posB, Vec4 interpolatedPoint, double interpolationValue){
	private static Vec4 calculateVectorForAltitude(Globe globe, double distance, Vec4 interpolatedPoint, double interpolationValue){
		
		
		
		if (Double.isNaN(distance)) {
			//System.out.println("DISTANCE = 0");
			distance = 0;
		}
		//System.out.println("distance between: "+posA+" - "+posB);
		//System.out.println("DISTANCE: "+distance);
		
		double arcHeight = calculateHeight(distance, interpolationValue);
					
		Vec4 normPoint = globe.computeSurfaceNormalAtPoint(interpolatedPoint);
		Vec4 altitudeVec = normPoint.multiply3(arcHeight);
		
		return altitudeVec;
	}
	
	private static double calculateHeight(double distance, double interpolationValue){
		double defCoeff = 1;
		double arcHeight = 
				//(distance / 2)
				(distance )
				* Math.sin(Math.PI * interpolationValue * defCoeff);
		
		return arcHeight;
	}
	
	private Position addVectortoPosition(Globe globe, Position pos,Vec4 vec){
		Vec4 wcDefPos = Operations.computePointFromLocation(pos);					
		Vec4 res = wcDefPos.add3(vec);
		return globe.computePositionFromPoint(res);
	}
	
	
	public void drawGrid(AirspaceLayer airspaces) {
		drawOneGrid(grid, airspaces, Color.GREEN);
	}

	public void drawOppositeGrid(AirspaceLayer airspaces) {
		drawOneGrid(oppositeGrid, airspaces, Color.RED);
	}

	private void drawOneGrid(Position[][] g, AirspaceLayer airspaces,
			Color color) {
		for (Position[] pList : g) {
			for (Position pos : pList) {
				ManageAirspaces.generateAirspaces(pos, airspaces, color,1);
			}
		}
	}

	

	public void drawDeformedGrid(AirspaceLayer airspaces
			//, int value,
			//int maxValue
			) {

		// createDeformedGrid(value, maxValue);

		for (int x = 0; x < numPoints; x++) {

			// Position p = oppositeGrid[x][0];

			for (int y = 0; y < numPoints; y++) {

				// Position p = oppositeGrid[x][y];
				// Position p2 = grid[x][y];

				// init(p, p2, Color.BLUE, airspaces,value,maxValue);
				Position pos = deformedGrid[x][y];
				ManageAirspaces.generateAirspaces(pos, airspaces, Color.WHITE,0);
			}
		}
	}

	

	public Position returnOppositePosition(Position pos1, Position middlePos) {

		Position res = pos1.subtract(middlePos);

		res = middlePos.subtract(res);

		return res;

	}

	
	public Position getPositionInGridAtIndex(int index){
		
		int x = ( index % numPoints);
		int y = (index / numPoints);
		
		//System.out.println("index: "+ index+", x: "+x+" y: "+y);
		
		return grid[x][y];
		}

public Position getPositionInDeformedGridAtIndex(int index){
		
		int x = ( index % numPoints);
		int y = (index / numPoints);
		
		//System.out.println("index: "+ index+", x: "+x+" y: "+y);
		
		return deformedGrid[x][y];
		}
public Position[] returnGridBBox(){
	Position[] list = new Position[5];
	
	//bottomLeft
	list[0]= grid[0][0];
	//bottomRight
	list[1]=  grid[numPoints-1][0];
	//topLeft
	list[2]=  grid[0][numPoints-1];
	//topRight
	list[3]=  grid[numPoints-1][numPoints-1];
	
	
	
	list[4]= grid[0][0];
	return list;
}



public Position getPivot() {
	return pivot;
}



public void setPivot(Position pivot) {
	this.pivot = pivot;
}



public Position getLastPointOnMiddleAxis() {
	return lastPointOnMiddleAxis;
}



public void setLastPointOnMiddleAxis(Position lastPointOnMiddleAxis) {
	this.lastPointOnMiddleAxis = lastPointOnMiddleAxis;
}


}
