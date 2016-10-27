package it;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Intersection;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.airspaces.AbstractAirspace;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.render.airspaces.SphereAirspace;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWMath;
import it.ManageRendObjects.ManageAirspaces;
import it.entities.Grid;
import it.entities.Node;
import it.layers.CurvesLayer;
import it.layers.GlobeAnnotation;
import it.listeners.KeyOption;
import it.main.MainDeformableGlobe;
import it.rendObjects.DeformableSurface;
import it.rendObjects.PointOfInterest;
import it.rendObjects.curve.CubicSplinePolyline;
import it.shader.GLSL;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.sun.javafx.geom.Vec2d;

public class Operations {

	public static void generatePOIs() {

		MainDeformableGlobe.pois = new ArrayList<>();

		// Position p = Position.fromDegrees(153, 42,0);
		// HelloWorldWind.pois.add(p);

		Position p2 = Position.fromDegrees(14, 13, 0);
		MainDeformableGlobe.pois.add(p2);

		Position p3 = Position.fromDegrees(50, 20, 0);
		MainDeformableGlobe.pois.add(p3);

		Position p4 = Position.fromDegrees(73, 100, 0);
		MainDeformableGlobe.pois.add(p4);

		Position p5 = Position.fromDegrees(160, 13, 0);
		MainDeformableGlobe.pois.add(p5);

	}

	/*
	public static void renderPOIs(AirspaceLayer poisLayer) {
		for (Position p : MainDeformableGlobe.pois) {

			ManageAirspaces.generateAirspaces(p, poisLayer, Color.YELLOW, 1);

		}
	}
*/
	static int numSlices = 0;

	private static void resetNode(CurvesLayer layer, String idNode) {

		Node n = layer.getNode(idNode);
		n.setDistanceFromCamera(0);
		n.setIsDeformed(false);
		n.setCurrPosition(n.getOriginalPosition());
		// n.setAlreadyChecked(false);
		n.setMustBeDeformed(false);
		n.setIsOnExistingSlice(false);
		n.setIsVisible(false);
	}

	public static void generateGridsForEachPoi(
	// AirspaceLayer airspacesLayer,
			Position cameraPos, Position focusPos, WorldWindowGLCanvas wwd) {

		MainDeformableGlobe.gridPois = new ArrayList<Grid>();

		// update the focus area
		AirspaceLayer focusArea = (AirspaceLayer) wwd.getModel().getLayers()
				.getLayerByName("Focus Area");
		focusArea.removeAllAirspaces();

		// ManageAirspaces.generateAirspaces(cameraPos, focusArea,
		// Color.WHITE,1);
		// ManageAirspaces.generateFocusArea(cameraPos, focusArea, Color.WHITE,
		// SharedVariables.focusAreaRadius);

		// remove the nodes
		AirspaceLayer airspaces = (AirspaceLayer) wwd.getModel().getLayers()
				.getLayerByName("Nodes");
		airspaces.removeAllAirspaces();
		// remove the labels
		AnnotationLayer annLayer = (AnnotationLayer) wwd.getModel().getLayers()
				.getLayerByName("Labels");
		annLayer.removeAllAnnotations();

		// resetto tutti i nodi origine e destinazione (isDeformed e distance)
		CurvesLayer layer = (CurvesLayer) wwd.getModel().getLayers()
				.getLayerByName(SharedVariables.selectedLayer);
		Iterable<Renderable> iter = (layer).getRenderables();
		Iterator<Renderable> it = iter.iterator();
		while (it.hasNext()) {
			CubicSplinePolyline currCurve = (CubicSplinePolyline) it.next();

			currCurve.setImportant(false);

			String originIdNode = currCurve.getIdOrigin();
			String destIdNode = currCurve.getIdDest();

			resetNode(layer, originIdNode);
			resetNode(layer, destIdNode);
		}

		// calcolo la distanza tra la camera e il nodo
		it = iter.iterator();
		while (it.hasNext()) {
			boolean importance = false;
			CubicSplinePolyline currCurve = (CubicSplinePolyline) it.next();
			Position originPos = (currCurve).getOrigin();
			Position destPos = (currCurve).getDestination();

			boolean isOriginVisible = isVisibleUsingVectors(wwd, originPos);
			boolean isDestinationVisible = isVisibleUsingVectors(wwd, destPos);

			//Node node_origin = layer.getNode(currCurve.getIdOrigin());
			//Node node_dest = layer.getNode(currCurve.getIdDest());
			
			//node_origin.setIsVisible(isOriginVisible);
			//node_dest.setIsVisible(isDestinationVisible);
			
			boolean destInFocus = false;
			boolean originInFocus = false;
			//if (isOriginVisible) {
				originInFocus = nodeIsInFocusInScreenDistance(focusPos,
						originPos);
			//}
			//if (isDestinationVisible) {
				destInFocus = nodeIsInFocusInScreenDistance(focusPos, destPos);
			//}
			// per ogni linea calcolo la distanza dei nodi e controllo se devono
			// essere deformati

			// controllo il nodo di origine
			// if(!layer.isAlreadyChecked(currCurve.getIdOrigin())){
			// layer.setAlreadyChecked(currCurve.getIdOrigin(),true);
			if (destInFocus||SharedVariables.alwaysDeform) {
				if (!layer.mustBeDeformed(currCurve.getIdOrigin())) {
					// if(!isPositionVisible( wcOriginPos))
					// if(!isVisibleUsingVectors( wwd,originPos))
					if (!isOriginVisible) {
						layer.setMustBeDeformed(currCurve.getIdOrigin(), true);
						double distance = getDistance(cameraPos, originPos);
						layer.setDistanceFromCamera(currCurve.getIdOrigin(),
								distance);
					}
				}
				importance = true;
			}

			// controllo il nodo di destinazione
			// if(!layer.isAlreadyChecked(currCurve.getIdDest())){
			// layer.setAlreadyChecked(currCurve.getIdDest(),true);

			if (originInFocus||SharedVariables.alwaysDeform) {
				if (!layer.mustBeDeformed(currCurve.getIdDest())) {
					// if(!isPositionVisible( wcDestPos))
					// if(!isVisibleUsingVectors( wwd,destPos))
					if (!isDestinationVisible) {
						layer.setMustBeDeformed(currCurve.getIdDest(), true);
						double distance = getDistance(cameraPos, destPos);
						layer.setDistanceFromCamera(currCurve.getIdDest(),
								distance);
					}

				}
				importance = true;
			}

			currCurve.setImportant(importance);

		}
		// ordino la lista dei nodi per distanza
		List<Node> list = new ArrayList<Node>(layer.getNodes());

		Collections.sort(list, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				// TODO Auto-generated method stub
				return Double.compare(o2.getDistanceFromCamera(),
						o1.getDistanceFromCamera());
			}
		});

		// per ogni nodo, dal più lontano al più vicino faccio check node
		for (Node currNode : list) {
			// System.out.println("DISTANCE: "+currNode.getDistanceFromCamera());
			// creo i nodi e le label
			// Iterator<Node> node_it = layer.getNodes().iterator();
			// while(node_it.hasNext()){
			// Node currNode = node_it.next();
			Position originalPos = currNode.getOriginalPosition();
			// Vec4 wcOriginalPos =
			// wwd.getModel().getGlobe().computePointFromLocation(originalPos);
			if (currNode.mustBeDeformed()) {
				// System.out.println("MUST BE DEFORMED: "+currNode.getName());
				checkNode(currNode,
						cameraPos, wwd, originalPos
						// , wcOriginalPos
						, airspaces);
			}
		}

		
		
		for (Node currNode : list) {
			
			Position originalPos = currNode.getOriginalPosition();
			
			if (currNode.isOnExistingSlice()) {
				placeOnExistingSlice(currNode,  originalPos, airspaces);
			}
		}
		
		
		// creo i nodi e le label
		Iterator<Node> node_it = layer.getNodes().iterator();
		while (node_it.hasNext()) {
			Node currNode = node_it.next();
			Position p = currNode.getCurrPosition();
			//System.out.println("CURR POSITION: "+p);
			Vec4 wcPoiPos = SharedVariables.wwd.getModel().getGlobe().computePointFromPosition(p);
			
			
			boolean isNodeVisible = isPointNotHiddenBehindTheGlobe(wcPoiPos);
			currNode.setIsVisible(isNodeVisible);
			if(isNodeVisible){
			ManageAirspaces.generateAirspaces(p, airspaces, Color.BLUE, 1);
			}else{
				ManageAirspaces.generateAirspaces(p, airspaces, Color.YELLOW, 1);
					
			}
			GlobeAnnotation a = MainDeformableGlobe.createLabel(p,
					currNode.getName());
			annLayer.addAnnotation(a);
		}

		// System.out.println("num slices : "+numSlices);
		numSlices = 0;
	}

	private static boolean isVisibleUsingVectors(WorldWindowGLCanvas wwd,
			Position poi) {

		
		// da 0 a pi
		double angle = calculateSurfaceCameraViewAngle( poi);

		boolean isVisible = false;

		if (angle > (Math.PI / 2 + 0.4)) {
			isVisible = true;
		}

		// boolean isVisibleOtherMethod = isPositionVisible( wcPoiPos);

		// System.out.println("isVisible: ("+isVisible+","+isVisibleOtherMethod+"), ANGLE: "+angle);
		return isVisible;
	}

	public static double calculateSurfaceCameraViewAngle(Position poi) {

		Vec4 wcPoiPos = SharedVariables.wwd.getModel().getGlobe().computePointFromLocation(poi);
		Vec4 normPoint = SharedVariables.wwd.getModel().getGlobe()
				.computeSurfaceNormalAtPoint(wcPoiPos);
		Vec4 eyePoint = SharedVariables.wwd.getView().getEyePoint();
		Vec4 forwPoint = wcPoiPos.subtract3(eyePoint);

		double num = normPoint.dot3(forwPoint);
		double denum = normPoint.getLength3() * forwPoint.getLength3();
		// da 0 a pi
		double angle = Math.acos(num / denum);

		
		return angle;
	}
	
	/*
	private static boolean nodeIsInFocusInWorldDistance(Position camera,
			Position node) {

		// double distance = Position.ellipsoidalDistance(camera, node, 6378137,
		// 6356752.3);
		double distance = getDistance(camera, node);
		// System.out.println("distance between "+camera+" and "+node+" : "+distance);
		if (distance < SharedVariables.focusAreaRadiusMeters) {
			// System.out.println("distance: "+distance);
			return true;
		}
		return false;
	}
*/
	private static boolean nodeIsInFocusInScreenDistance(Position camera,
			Position node) {
		Vec4 wcNode = SharedVariables.computeWorldCoordinatesFromPosition(node);

		if (isPositionVisible(wcNode)) {
			// double distance = Position.ellipsoidalDistance(camera, node,
			// 6378137, 6356752.3);
			Vec4 p2 = SharedVariables.computeScreenCoordinates(wcNode);

			Vec4 p1 = SharedVariables.computeScreenCoordinates(SharedVariables
					.computeWorldCoordinatesFromPosition(camera));
			// System.out.println("distance between "+camera+" and "+node+" : "+distance);
			double distance = p1.distanceTo3(p2);
			double focusAreaRadiusScreenCoord = SharedVariables.lense_w / 2;
			if (distance < focusAreaRadiusScreenCoord) {
				// System.out.println("distance: "+distance);
				return true;
			}
		}
		return false;
	}

	private static int nodeIsInsideExistingGrid(Position node) {

		// System.out.println("point: "+node);
		int size = MainDeformableGlobe.gridPois.size();
		// for(int i=0; i<size;i++){
		for (int i = size - 1; i >= 0; i--) {
			Grid g = MainDeformableGlobe.gridPois.get(i);
			Position[] bbox = g.returnGridBBox();

			// if(pointIsInsideBBox(node, bbox))
			if (GeoOperation2.isLocationInside(node, bbox, g.getPivot(),
					g.getLastPointOnMiddleAxis())) {
				// System.out.println("si è dentro");
				return i;
			}
			// else{
			// System.out.println("non è dentro");
			// }
		}

		return -1;

	}

	private static void placeOnExistingSlice(Node node,
			Position poiPos
			// ,Vec4 wcPoiPos
			, AirspaceLayer airspaces) {
		
	 
		// valY va da 0 a 1,
		// 0 sono i punti sull'orizzonte
		// 1 sono i punti opposti all'orizzonte
		// interpolationValue va da 0 a 1
		// 0 poca distorsione
		// 1 alta distorsione

	 	int indexExistingGrid = nodeIsInsideExistingGrid(poiPos);

		if (indexExistingGrid != -1) {
			//System.out.println("NODE INSIDE EXISTING GRID");
			Grid g = MainDeformableGlobe.gridPois.get(indexExistingGrid);
	 		Position poi = poiPos;

			double distFromMiddleAxis = GeoOperation2
					.perpendicularDistanceFromAPlaneInsersectingTheOrigin(
							g.getPivot(), g.getLastPointOnMiddleAxis(), poi);
		 

			Position poiOnYAxis = GeoOperation2.calculatePositionInMiddleAxis(
					g.getPivot(), g.getLastPointOnMiddleAxis(), poi,
					distFromMiddleAxis);
			
			
			
			
			
			
			double smallDistOnY = getDistance(g.getPivot(), poiOnYAxis);
			double distOnY = getDistance(g.getPivot(),
					g.getLastPointOnMiddleAxis());
			double ratioY = smallDistOnY / distOnY;

			double interpolationValue = SharedVariables
					.calculateDistortionCoefficent(g.getParamDist1(),
							g.getParamDist2(), ratioY);

			Position oppCenteredPoi = interpolateGreatCircle(2, poiOnYAxis,
					g.getPivot());

			 //ManageAirspaces.generateAirspaces(poiOnYAxis, airspaces,Color.RED,0);
			 //ManageAirspaces.generateAirspaces(oppCenteredPoi, airspaces,Color.YELLOW,0);
			//ManageAirspaces.generateAirspaces(g.getPivot(), airspaces,Color.BLUE,0);
			
			//System.out.println("dist: "+distFromMiddleAxis+", interpolationValue: "+interpolationValue);
			
				Position updPoiPos = g.updateExtraPoints(
		 			g.getPivot(), poiOnYAxis, oppCenteredPoi,
		 				interpolationValue,
		 			distFromMiddleAxis, airspaces
		 			);
				setOnExistngSlice(node, false);
		setDeformedNode( node, true, updPoiPos);
		}
	}
	
	 	private static void checkNode( Node node,
			Position cameraPos, WorldWindowGLCanvas wwd, Position poiPos
		
			, AirspaceLayer airspaces) {
	 
		
	 
		// valY va da 0 a 1,
		// 0 sono i punti sull'orizzonte
		// 1 sono i punti opposti all'orizzonte
		// interpolationValue va da 0 a 1
		// 0 poca distorsione
		// 1 alta distorsione

	 	int indexExistingGrid = nodeIsInsideExistingGrid(poiPos);

		if (indexExistingGrid != -1) {

			setOnExistngSlice(node, true);
			/*
			Grid g = MainDeformableGlobe.gridPois.get(indexExistingGrid);
	 		Position poi = poiPos;

			double distFromMiddleAxis = GeoOperation2
					.perpendicularDistanceFromAPlaneInsersectingTheOrigin(
							g.getPivot(), g.getLastPointOnMiddleAxis(), poi);
		 

			Position poiOnYAxis = GeoOperation2.calculatePositionInMiddleAxis(
					g.getPivot(), g.getLastPointOnMiddleAxis(), poi,
					distFromMiddleAxis);
			// ManageAirspaces.generateAirspaces(poiOnYAxis, airspaces,
			// Color.RED,0);
			double smallDistOnY = getDistance(g.getPivot(), poiOnYAxis);
			double distOnY = getDistance(g.getPivot(),
					g.getLastPointOnMiddleAxis());
			double ratioY = smallDistOnY / distOnY;

			double interpolationValue = SharedVariables
					.calculateDistortionCoefficent(g.getParamDist1(),
							g.getParamDist2(), ratioY);

			Position oppCenteredPoi = interpolateGreatCircle(2, poiOnYAxis,
					g.getPivot());

		
			Position updPoiPos = g.updateExtraPoints(
		 			g.getPivot(), poiOnYAxis, oppCenteredPoi,
		 				interpolationValue,
		 			distFromMiddleAxis, airspaces
		 			);

			// setDeformedNode(currCurve, isDest,true,updPoiPos);
			setDeformedNode( node, true, updPoiPos);
			*/
		} else {

			// double horiz = 0.7;
			double horiz = 1.0;
			double coeffPosNode = 1.2;
			
			//Double stepForHorizon = findHorizonPointFromCameraToPoi(wwd,cameraPos, poiPos, horiz);
			Position horizonPosition = findHorizonPointFromCameraToPoi(wwd,cameraPos, poiPos, horiz);
			// ManageAirspaces.generateAirspaces(poiPos, airspaces,
			// Color.PINK,0);

			if (horizonPosition != null) {
			//if (stepForHorizon != null) {
				Position horizPos = interpolateGreatCircle(0.9,
						cameraPos, horizonPosition);
		 
				Position oppPoiPos = interpolateGreatCircle(2, poiPos, horizPos);
		 
				double valY = 1 / coeffPosNode;
        		Grid grid = new Grid();

				// /////////////////////////////////////////////
				// //////////////////Updated position//////////
				// ///////////////////////////////////////////
				boolean nodeInsideScreen = false;
				Position updPoiPos = null;
				double p1 = SharedVariables.distCoeff;
				double p2 = SharedVariables.calculateDistParam2(p1);
				
				//System.out.println("---------------> "+p1);
				
				while (!nodeInsideScreen) {

					if(p1>=0){
					 p2 = SharedVariables.calculateDistParam2(p1);
					}else{
						p2-= 0.01;	
					}
					double interpolationValue = SharedVariables
							.calculateDistortionCoefficent(p1, p2, valY);

					updPoiPos = grid.updatePoiPosition(horizPos, poiPos,
							oppPoiPos, 
			 				interpolationValue,
				 			airspaces);

					nodeInsideScreen = SharedVariables
							.isInsideViewPort(updPoiPos);

					//System.out.println("nodeInsideScreen: "+nodeInsideScreen+", p1: "+p1+" , p2: "+p2);
					
					if ((nodeInsideScreen) || ((p1 <= 0))&&(p2 <= 0.18)) {
						//System.out.println("setDistortionParams: " + p1 + " , "	+ p2);
						grid.setDistortionParams(p1, p2);
						break;
					} else if(p1 > 0){
						p1 -= 0.02;
					}
				}

			 
				// System.out.println("deformo "+node.getName()+" su griglia nuova");
				setDeformedNode( node, true, updPoiPos);

			 	grid.generateSurfaceGrid(poiPos, horizPos, oppPoiPos,
						airspaces,
				 		coeffPosNode);
 
				grid.generateDeformedGrid(poiPos, horizPos, oppPoiPos,
						airspaces, grid.getParamDist1(), grid.getParamDist2(),
			 			coeffPosNode);

				// System.out.println("fine generateDeformedGrid");
				MainDeformableGlobe.gridPois.add(grid);

		 

				numSlices++;
			}

			// }
		}
	}

	private static void setDeformedNode(Node node,
			boolean deform, Position pos) {

		node.setIsDeformed(deform);
		if (pos == null) {
			node.setCurrPosition(node.getOriginalPosition());
			// currDestPosition=(originalDestPosition);
		} else {
			// originalPosition=this.getReferencePosition();
			// System.out.println("new Pos: "+newPos);
			// currDestPosition=(newPos);
			node.setCurrPosition(pos);

		}
	}

	private static void setOnExistngSlice(Node node,
			boolean isOnExistingSlice) {
		node.setIsOnExistingSlice(isOnExistingSlice);
		
	}
	
	
	/*
	private static Double findHorizonPointFromCameraToPoi(
			WorldWindowGLCanvas wwd, Position cameraPos, Position poiPos,
			double v) {
		// Vec4 wcPoi =
		// wwd.getModel().getGlobe().computePointFromLocation(poiPos);

		double step = 0.005;
		// double halfWidth = 3500000;

		// if (!isPositionVisible( wcPoi)) {
		if (!isVisibleUsingVectors(wwd, poiPos)) {

			for (double i = v; i > 0; i = i - step) {

				// Position horizPos = Position.interpolateGreatCircle(i,
				// poiPos,
				// System.out.println("I: "+i);
				Position horizPos = interpolateGreatCircle(i, cameraPos, poiPos);
				// Vec4 horizPosWc = wwd.getModel().getGlobe()
				// .computePointFromLocation(horizPos);

				double nextI = i + 2 * step;

				// if (isPositionVisible( horizPosWc)) {
				if (isVisibleUsingVectors(wwd, horizPos)) {
					// System.out.println("IS  VISIBLE horizPosWc con v: "+nextI);
					// finalPos=res;
					return nextI;
				}
			}
		}
		// return finalPos;
		return null;
	}
*/
	
	private static Position findHorizonPointFromCameraToPoi(
			WorldWindowGLCanvas wwd, Position cameraPos, Position poiPos,
			double v) {
		// Vec4 wcPoi =
		// wwd.getModel().getGlobe().computePointFromLocation(poiPos);

		double step = 0.005;
		// double halfWidth = 3500000;

		// if (!isPositionVisible( wcPoi)) {
		if (!isVisibleUsingVectors(wwd, poiPos)) {

			for (double i = v; i > 0; i = i - step) {

				// Position horizPos = Position.interpolateGreatCircle(i,
				// poiPos,
				// System.out.println("I: "+i);
				Position horizPos = interpolateGreatCircle(i, cameraPos, poiPos);
				// Vec4 horizPosWc = wwd.getModel().getGlobe()
				// .computePointFromLocation(horizPos);

				//double nextI = i + 2 * step;

				// if (isPositionVisible( horizPosWc)) {
				if (isVisibleUsingVectors(wwd, horizPos)) {
					// System.out.println("IS  VISIBLE horizPosWc con v: "+nextI);
					// finalPos=res;
					return horizPos;
				}
			}
		}
		// return finalPos;
		return null;
	}
	
	
	public static double getDistance(Position p, Position p2) {

		return ellipsoidalDistance(p, p2, Earth.WGS84_EQUATORIAL_RADIUS,
				Earth.WGS84_POLAR_RADIUS);

		/*
		 * return GeodeticCalculator.calculateGeodeticCurve( Ellipsoid.WGS84,
		 * p.getLatitude().radians, p.getLongitude().radians,
		 * p2.getLatitude().radians, p2.getLongitude().radians);
		 */
		// Vec4 wc1 =
		// HelloWorldWind.world.getModel().getGlobe().computePointFromLocation(p);
		// Vec4 wc2 =
		// HelloWorldWind.world.getModel().getGlobe().computePointFromLocation(p2);
		// return wc1.distanceTo3(wc2);
	}

	/**
	 * Computes the distance between two points on an ellipsoid iteratively.
	 * <p/>
	 * NOTE: This method was copied from the UniData NetCDF Java library.
	 * http://www.unidata.ucar.edu/software/netcdf-java/
	 * <p/>
	 * Algorithm from U.S. National Geodetic Survey, FORTRAN program "inverse,"
	 * subroutine "INVER1," by L. PFEIFER and JOHN G. GERGEN. See
	 * http://www.ngs.noaa.gov/TOOLS/Inv_Fwd/Inv_Fwd.html
	 * <p/>
	 * Original documentation: SOLUTION OF THE GEODETIC INVERSE PROBLEM AFTER
	 * T.VINCENTY MODIFIED RAINSFORD'S METHOD WITH HELMERT'S ELLIPTICAL TERMS
	 * EFFECTIVE IN ANY AZIMUTH AND AT ANY DISTANCE SHORT OF ANTIPODAL
	 * STANDPOINT/FOREPOINT MUST NOT BE THE GEOGRAPHIC POLE
	 * <p/>
	 * Requires close to 1.4 E-5 seconds wall clock time per call on a 550 MHz
	 * Pentium with Linux 7.2.
	 *
	 * @param p1
	 *            first position
	 * @param p2
	 *            second position
	 * @param equatorialRadius
	 *            the equatorial radius of the globe in meters
	 * @param polarRadius
	 *            the polar radius of the globe in meters
	 *
	 * @return distance in meters between the two points
	 */
	public static double ellipsoidalDistance(LatLon p1, LatLon p2,
			double equatorialRadius, double polarRadius) {
		// TODO: I think there is a non-iterative way to calculate the distance.
		// Find it and compare with this one.
		// TODO: What if polar radius is larger than equatorial radius?
		final double F = (equatorialRadius - polarRadius) / equatorialRadius; // flattening
																				// =
																				// 1.0
																				// /
																				// 298.257223563;
		final double R = 1.0 - F;
		final double EPS = 0.5E-13;
		// final double EPS = 0.5E-3;

		if (p1 == null || p2 == null) {
			String message = Logging.getMessage("nullValue.PositionIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		// Algorithm from National Geodetic Survey, FORTRAN program "inverse,"
		// subroutine "INVER1," by L. PFEIFER and JOHN G. GERGEN.
		// http://www.ngs.noaa.gov/TOOLS/Inv_Fwd/Inv_Fwd.html
		// Conversion to JAVA from FORTRAN was made with as few changes as
		// possible
		// to avoid errors made while recasting form, and to facilitate any
		// future
		// comparisons between the original code and the altered version in
		// Java.
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
		// Conversion to JAVA from FORTRAN was made with as few changes as
		// possible
		// to avoid errors made while recasting form, and to facilitate any
		// future
		// comparisons between the original code and the altered version in
		// Java.
		//
		// IMPLICIT REAL*8 (A-H,O-Z)
		// COMMON/CONST/PI,RAD

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

		do {
			// System.out.println("nel do");
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
			if (C2A > 0.) {
				CZ = -CZ / C2A + CY;
			}
			E = CZ * CZ * 2. - 1.;
			C = ((-3. * C2A + 4.) * F + 4.) * C2A * F / 16.;
			D = X;
			X = ((E * CY * C + CZ) * SY * C + Y) * SA;
			X = (1. - C) * X * F + GLON2 - GLON1;

			// IF(DABS(D-X).GT.EPS) GO TO 100
		} while (Math.abs(D - X) > EPS);

		// FAZ = Math.atan2(TU1, TU2);
		// BAZ = Math.atan2(CU1 * SX, BAZ * CX - SU1 * CU2) + Math.PI;
		X = Math.sqrt((1. / R / R - 1.) * C2A + 1.) + 1.;
		X = (X - 2.) / X;
		C = 1. - X;
		C = (X * X / 4. + 1.) / C;
		D = (0.375 * X * X - 1.) * X;
		X = E * CY;
		S = 1. - E - E;
		S = ((((SY * SY * 4. - 3.) * S * CZ * D / 6. - X) * D / 4. + CZ) * SY
				* D + Y)
				* C * equatorialRadius * R;

		return S;
	}

	public static void renderDeformableSurfacesFromGrids(RenderableLayer layer,
			AirspaceLayer deformedAirspaces, GLSL glsl) {

		for (Grid g : MainDeformableGlobe.gridPois) {
			DeformableSurface deformableSurface = new DeformableSurface(g, glsl);
			layer.addRenderable(deformableSurface);

			if (MainDeformableGlobe.isDebug == true) {
				g.drawDeformedGrid(deformedAirspaces
				// , KeyOption.defIndex,
				// KeyOption.maxIndex
				);
				// g.drawOppositeGrid(deformedAirspaces);
				g.drawGrid(deformedAirspaces);
			}
		}
	}

	public static Vec4 computePointFromLocation(Position pos) {
		Globe globe = MainDeformableGlobe.world.getModel().getGlobe();
		return globe.computePointFromLocation(pos);
	}

	public static void removeRenderGrids(RenderableLayer layer,
			AirspaceLayer deformedAirspaces) {

		layer.removeAllRenderables();
		deformedAirspaces.removeAllAirspaces();
	}

	public static Position[] generateParallelPointsOverTheGlobe(Vec4 point,
			Vec4 prev, Vec4 next,
			// List<Position> leftPositions,
			// List<Position> rightPositions,
			double length, Globe globe) {

		Vec4 offset;
		// Vec4 normal = globe.computeSurfaceNormalAtPoint(point);
		Vec4 normal = (point);

		// Compute vector in the direction backward along the line.
		Vec4 backward = (prev != null) ? prev.subtract3(point) : point
				.subtract3(next);

		// Compute a vector perpendicular to segment BC, and the globe normal
		// vector.
		Vec4 perpendicular = backward.cross3(normal);

		offset = perpendicular.normalize3();

		offset = offset.multiply3(length);

		// System.out.println("Offset: "+offset);

		// Determine the left and right points by applying the offset.
		Vec4 ptRight = point.add3(offset);
		Vec4 ptLeft = point.subtract3(offset);

		// Convert cartesian points to geographic.
		// Position posLeft = globe.computePositionFromPoint(ptLeft);
		// Position posRight = globe.computePositionFromPoint(ptRight);
		// Position posLeft = globe.computePositionFromPoint(ptLeft);
		// Position posRight = globe.computePositionFromPoint(ptRight);

		Position posLeftOverTerrain = gov.nasa.worldwind.util.RayCastingSupport
				.intersectRayWithTerrain(globe, ptLeft, point.multiply3(-1));
		Position posRightOverTerrain = gov.nasa.worldwind.util.RayCastingSupport
				.intersectRayWithTerrain(globe, ptRight, point.multiply3(-1));

		// leftPositions.add(Position.fromDegrees(posLeft.latitude.degrees,
		// posLeft.longitude.degrees));
		// rightPositions.add(Position.fromDegrees(posRight.latitude.degrees,
		// posRight.longitude.degrees));

		Position[] res = { posLeftOverTerrain, posRightOverTerrain };

		return res;

		// leftPositions.add(posLeft);
		// rightPositions.add(posRight);
	}

public static boolean isPointNotHiddenBehindTheGlobe(
		Vec4 point_in_worldCoordinates
		//, 
		//Position pos,
		//Vec4 sceenPoint
		){
	
		Vec4[] points=intersectRayWithTerrainReturns2Points(SharedVariables.getGlobe(), 
			//dc.getView().getEyePoint()
			SharedVariables.getEyePoint()
			,  point_in_worldCoordinates.subtract3(
					//dc.getView().getEyePoint()
					SharedVariables.getEyePoint()
					).normalize3(),100,10);
	//Position terrainPosition=intersectRayWithTerrain(dc.getGlobe(), dc.getView().getEyePoint(),  point,100,10);

	if((points==null)){
	//	System.out.println("non ci sono intersezioni");
		return true;
		}	
	if((points[0]==null)){
	//System.out.println("il punto 0 nn c'è!");
	return true;
	}	
	
	//System.out.println("ci sono intersezioni: "+points[0]+ " ---- "+points[1]);
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






public static Vec4[] intersectRayWithTerrainReturns2Points(Globe globe, Vec4 origin, Vec4 direction, double sampleLength, double precision)
{
	
	Vec4[] res = new Vec4[2];
	
    if (globe == null)
    {
        String msg = Logging.getMessage("nullValue.GlobeIsNull");
        Logging.logger().severe(msg);
        throw new IllegalArgumentException(msg);
    }
    if (origin == null || direction == null)
    {
        String msg = Logging.getMessage("nullValue.Vec4IsNull");
        Logging.logger().severe(msg);
        throw new IllegalArgumentException(msg);
    }
    if (sampleLength < 0)
    {
        String msg = Logging.getMessage("generic.ArgumentOutOfRange", sampleLength);
        Logging.logger().severe(msg);
        throw new IllegalArgumentException(msg);
    }
    if (precision < 0)
    {
        String msg = Logging.getMessage("generic.ArgumentOutOfRange", precision);
        Logging.logger().severe(msg);
        throw new IllegalArgumentException(msg);
    }

    Position pos = null;
    direction = direction.normalize3();

   // System.out.println("globe.getMaxElevation(): "+globe.getMaxElevation());
    
    // Check whether we intersect the globe at it's highest elevation
    Intersection inters[] = globe.intersect(new Line(origin, direction), 0);
    if (inters != null)
    {
        // Sort out intersection points and direction
        Vec4 p1 = inters[0].getIntersectionPoint();
        Vec4 p2 = null;
        if (p1.subtract3(origin).dot3(direction) < 0)
        {
        	System.out.println("wrong direction");
            p1 = null; // wrong direction
        }
        if (inters.length == 2)
        {
        //	System.out.println("trovati 2 intersezioni");
            p2 = inters[1].getIntersectionPoint();
            if (p2.subtract3(origin).dot3(direction) < 0)
            {System.out.println("wrong direction");
                p2 = null; // wrong direction
            }
        }

        if (p1 == null && p2 == null)   // both points in wrong direction
        {
        	System.out.println("entrambi i punti in wrong direction");
        	return null;
        }
            

        if (p1 != null && p2 != null)
        {
            // Outside sphere move to closest point
            if (origin.distanceTo3(p1) > origin.distanceTo3(p2))
            {
                // switch p1 and p2
                Vec4 temp = p2;
                p2 = p1;
                p1 = temp;
                
              
            }
        //    System.out.println("punto più vicino: "+p1);
        //    System.out.println("punto più lontano: "+p2);
        }
        else
        {
            // single point in right direction: inside sphere
            p2 = p2 == null ? p1 : p2;
            p1 = origin;
            //System.out.println("solo un punto: "+p2);
        }
       
        
/*
        // Sample between p1 and p2
        Vec4 point = intersectSegmentWithTerrain(globe, p1, p2, sampleLength, precision);
        if (point != null)
            pos = globe.computePositionFromPoint(point);

    }
    return pos;
    */
        res[0] = p1;
        res[1] = p2;
        
    }
   
    return res;
}

	private static boolean isPositionVisible(Vec4 point_in_worldCoordinates) {

		Globe globe = SharedVariables.getGlobe();

		Vec4[] points = RayCastingSupport
				.intersectRayWithTerrainReturns2Points(globe,
						SharedVariables.getEyePoint(),
						// wwd.getView().getEyePoint(),
						point_in_worldCoordinates.subtract3(
						// wwd.getView().getEyePoint()
								SharedVariables.getEyePoint()).normalize3(),
						100, 10);

		double distance1 = point_in_worldCoordinates.distanceTo3(points[0]);
		double distance2 = point_in_worldCoordinates.distanceTo3(points[1]);

		if (distance1 < distance2) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Returns the an interpolated location along the great-arc between
	 * <code>value1</code> and <code>value2</code>. The position's elevation
	 * components are linearly interpolated as a simple 1D scalar value. The
	 * interpolation factor <code>amount</code> defines the weight given to each
	 * value, and is clamped to the range [0, 1]. If <code>a</code> is 0 or
	 * less, this returns <code>value1</code>. If <code>amount</code> is 1 or
	 * more, this returns <code>value2</code>. Otherwise, this returns the
	 * position on the great-arc between <code>value1</code> and
	 * <code>value2</code> with a linearly interpolated elevation component, and
	 * corresponding to the specified interpolation factor.
	 * 
	 * @param amount
	 *            the interpolation factor
	 * @param value1
	 *            the first position.
	 * @param value2
	 *            the second position.
	 * 
	 * @return an interpolated position along the great-arc between
	 *         <code>value1</code> and <code>value2</code>, with a linearly
	 *         interpolated elevation component.
	 * 
	 * @throws IllegalArgumentException
	 *             if either location is null.
	 */
	public static Position interpolateGreatCircle(double amount,
			Position value1, Position value2) {
		if (value1 == null || value2 == null) {
			String message = Logging.getMessage("nullValue.PositionIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		LatLon latLon = LatLoninterpolateGreatCircle(amount, value1, value2);
		// Elevation is independent of geographic interpolation method (i.e.
		// rhumb, great-circle, linear), so we
		// interpolate elevation linearly.
		double elevation = WWMath.mix(amount, value1.getElevation(),
				value2.getElevation());

		return new Position(latLon, elevation);
	}

	/**
	 * Returns the an interpolated location along the great-arc between
	 * <code>value1</code> and <code>value2</code>. The interpolation factor
	 * <code>amount</code> defines the weight given to each value, and is
	 * clamped to the range [0, 1]. If <code>a</code> is 0 or less, this returns
	 * <code>value1</code>. If <code>amount</code> is 1 or more, this returns
	 * <code>value2</code>. Otherwise, this returns the location on the
	 * great-arc between <code>value1</code> and <code>value2</code>
	 * corresponding to the specified interpolation factor.
	 *
	 * @param amount
	 *            the interpolation factor
	 * @param value1
	 *            the first location.
	 * @param value2
	 *            the second location.
	 *
	 * @return an interpolated location along the great-arc between
	 *         <code>value1</code> and <code>value2</code>.
	 *
	 * @throws IllegalArgumentException
	 *             if either location is null.
	 */
	private static LatLon LatLoninterpolateGreatCircle(double amount,
			LatLon value1, LatLon value2) {
		if (value1 == null || value2 == null) {
			String message = Logging.getMessage("nullValue.LatLonIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		if (LatLon.equals(value1, value2))
			return value1;

		// double t = clamp(amount, 0d, 1d);
		double t = amount;

		Angle azimuth = LatLon.greatCircleAzimuth(value1, value2);
		Angle distance = LatLon.greatCircleDistance(value1, value2);
		Angle pathLength = Angle.fromDegrees(t * distance.degrees);

		return LatLon.greatCircleEndPosition(value1, azimuth, pathLength);
	}

}
