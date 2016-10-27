package it.graphitech.trafficSimulator.renderableManager;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Position.PositionList;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.ogc.kml.KMLAbstractGeometry;
import gov.nasa.worldwind.ogc.kml.KMLDocument;
import gov.nasa.worldwind.ogc.kml.KMLPlacemark;
import gov.nasa.worldwind.ogc.kml.KMLPolygon;
import gov.nasa.worldwind.ogc.kml.KMLRoot;
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import gov.nasa.worldwind.render.AbstractSurfaceShape;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;

import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;

import it.graphitech.trafficSimulator.GlobalInstances;
import it.graphitech.trafficSimulator.TrafficSim;
import it.graphitech.trafficSimulator.TrafficSim.AppFrame;
import it.graphitech.trafficSimulator.entities.Area;
import it.graphitech.trafficSimulator.entities.DepartureArea;
import it.graphitech.trafficSimulator.entities.DestinationArea;

import it.graphitech.trafficSimulator.renderable.PathExtArea;
import it.graphitech.trafficSimulator.renderable.PoicmsArcLine;
import it.graphitech.trafficSimulator.renderable.emitter.Emitter;
import it.graphitech.trafficSimulator.renderable.emitter.EmitterDestAreas;
import it.graphitech.trafficSimulator.renderable.kml.KMLAbstractFeature;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;

public class AreaManager {

	// list of colors used to identify the destination areas
	static public Color[] colors = { Color.RED, Color.BLUE, Color.YELLOW,
			Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.PINK, Color.WHITE,
			Color.BLACK, Color.CYAN, Color.DARK_GRAY, Color.GRAY,
			Color.LIGHT_GRAY };

	// list with all the departure areas
	static java.util.ArrayList<DepartureArea> departureAreas = new java.util.ArrayList<DepartureArea>();
	// list with all the destination areas
	static java.util.ArrayList<DestinationArea> destinationareas = new java.util.ArrayList<DestinationArea>();

	// list with size equal at the number of vehicles generated from the current
	// departure area, it contains the id that identifies the destination area
	// of each vehicle
	static java.util.ArrayList<Integer> idAreasForEachVehicle;

	// renderable layer for the areas
	static final RenderableLayer areasLayer = new RenderableLayer();
	// renderable layer for the arcs that start from a departure area and arrive
	// at a destination area
	static final RenderableLayer arrowsLayer = new RenderableLayer();

	public static void initAreasLayer(WorldWindow wwd, AppFrame ts) {

		areasLayer.setName("Areas");
		insertBeforePlacenames(wwd, areasLayer);
		ts.getLayerPanel().update(wwd);
	}

	public static void initArrowsLayer(WorldWindow wwd, AppFrame ts) {

		arrowsLayer.setName("Arrows");
		insertBeforePlacenames(wwd, arrowsLayer);
		ts.getLayerPanel().update(wwd);
	}

	public static java.util.ArrayList<Integer> getIdAreasForEachVehicle() {
		return idAreasForEachVehicle;
	}

	public static void generateIdAreasForEachVehicle() {
		idAreasForEachVehicle = new ArrayList<Integer>();
	}

	public static void setIdAreasForEachVehicle(
			java.util.ArrayList<Integer> idAreasForEachVehicle) {
		AreaManager.idAreasForEachVehicle = idAreasForEachVehicle;
	}

	/**
	 * generates the polygon that represents the departure area (where the cars
	 * start)
	 * 
	 * @param pathPositions
	 * @param drawInterior
	 */
	public static void generatePolygonArea(ArrayList<Position> pathPositions,
			boolean drawInterior) {

		AbstractSurfaceShape circleMarker;

		circleMarker = new SurfacePolygon(pathPositions);

		ShapeAttributes attr = new BasicShapeAttributes();
		attr.setDrawInterior(drawInterior);
		attr.setInteriorMaterial(Material.WHITE);
		attr.setOutlineMaterial(Material.WHITE);
		attr.setOutlineOpacity(0.5);

		((SurfacePolygon) circleMarker).setAttributes(attr);

		areasLayer.addRenderable(circleMarker);

	}

	/**
	 * generates the polygon that represents the danger area (where the cars
	 * cannot pass through)
	 * 
	 * @param pathPositions
	 * @param drawInterior
	 */
	public static void generateDangerPolygonArea(
			ArrayList<Position> pathPositions, boolean drawInterior) {

		AbstractSurfaceShape circleMarker;

		circleMarker = new SurfacePolygon(pathPositions);
		ShapeAttributes attr = new BasicShapeAttributes();
		attr.setDrawInterior(drawInterior);
		attr.setInteriorMaterial(Material.BLACK);
		attr.setOutlineMaterial(Material.BLACK);
		attr.setInteriorOpacity(0.5);
		((SurfacePolygon) circleMarker).setAttributes(attr);
		areasLayer.addRenderable(circleMarker);

	}

	/**
	 * generates the polygon that represents the destination area (where the
	 * cars arrive)
	 * 
	 * @param area
	 */
	public static void generatePolyginDestinationArea(Area area) {

		AbstractSurfaceShape circleMarker;

		circleMarker = new SurfacePolygon(area.positions);
		
		ShapeAttributes attr = new BasicShapeAttributes();
		attr.setDrawInterior(true);
		attr.setInteriorOpacity(0.5);
		attr.setInteriorMaterial(getMaterial(area.getColor()));
		attr.setOutlineMaterial(getMaterial(area.getColor()));
		((SurfacePolygon) circleMarker).setAttributes(attr);
		areasLayer.addRenderable(circleMarker);

	}

	/**
	 * generate the arc that connects the departure area with the destination
	 * area the input "val" represents the number of cars start from the
	 * departure area and arrive to the destination area, (between 0 and 1)
	 * 
	 * @param start
	 * @param end
	 * @param val
	 */
	public static void generateArcs(Area start, Area end, double val) {

		Position p1 = start.getMiddlePos();
		Position p2 = end.getMiddlePos();
		// Create and set an attribute bundle.
		double el1 = GlobalInstances.getGlobe().getElevation(
				Angle.fromDegreesLatitude(p1.getLatitude().degrees),
				Angle.fromDegreesLongitude(p1.getLongitude().degrees));
		double el2 = GlobalInstances.getGlobe().getElevation(
				Angle.fromDegreesLatitude(p2.getLatitude().degrees),
				Angle.fromDegreesLongitude(p2.getLongitude().degrees));

		Position startPos = Position.fromDegrees(p1.getLatitude().degrees,
				p1.getLongitude().degrees, el1);
		Position endPos = Position.fromDegrees(p2.getLatitude().degrees,
				p2.getLongitude().degrees, el2);
		ShapeAttributes attrs = new BasicShapeAttributes();
		attrs.setOutlineMaterial(getMaterial(end.getColor()));
		attrs.setOutlineWidth(20d);

		PoicmsArcLine arc = new PoicmsArcLine(startPos, endPos, end.getColor(),
				(int) (val * 10));

		arrowsLayer.addRenderable(arc);

	}

	/**
	 * assign at n cars (numVehicles) the destination area identified as
	 * "idDestArea" this function updates the list "idAreasForEachVehicle"
	 * 
	 * @param numVehicles
	 * @param idDestArea
	 */

	public static void assignNumVehiclesToDestination(int numVehicles,
			int idDestArea) {

		int numV = numVehicles;

		for (int i = 0; i < idAreasForEachVehicle.size(); i++) {
			if (numV == 0) {
				break;
			}

			int j = idAreasForEachVehicle.get(i);
			if (j == -1) {
				idAreasForEachVehicle.set(i, idDestArea);
				numV--;
			}
		}

	}

	/**
	 * returns the number of cars generated from the current departure area
	 * without assigned destination this function checks the list
	 * "idAreasForEachVehicle"
	 * 
	 * @return
	 */

	public static int getNumVehiclesWithNoDestination() {

		int numVechicles = 0;

		for (int i = 0; i < idAreasForEachVehicle.size(); i++) {
			int j = idAreasForEachVehicle.get(i);
			if (j == -1) {
				numVechicles++;
			}
		}
		return numVechicles;
	}

	/**
	 * returns the number of cars generated from the current departure area this
	 * function checks the list "idAreasForEachVehicle"
	 */
	public static int getNumVehicles() {

		return idAreasForEachVehicle.size();
	}

	/**
	 * returns the reference position of the area identified with the id
	 * "idArea"
	 * 
	 * @param idArea
	 * @return
	 */
	public static Position getReferencePosition(int idArea) {
		return destinationareas.get(idArea).getReferencePosition();
	}

	/**
	 * returns a list of integer, each value is -1, and the size of the array
	 * equal to the sum of the cars generated by all the emitters
	 * 
	 * @param emitters
	 * @return
	 */
	public static ArrayList<Integer> createIdDestAreaArray(
			ArrayList<Emitter> emitters) {
		ArrayList<Integer> array = new ArrayList<Integer>();
		for (Emitter emit : emitters) {
			int numCars = ((EmitterDestAreas) emit).getNumCars();
			for (int i = 0; i < numCars; i++)
				array.add(new Integer(-1));

		}
		return array;
	}

	/**
	 * returns a list of integer, each value is -1, and the size of the array
	 * equal to the sum of the cars generated by all the emitters it also copy
	 * the generated array into the "idAreasForEachVehicle"
	 * 
	 * @param emitters
	 * @return
	 */
	public static ArrayList<Integer> createIdDestAreaArrayForEachEmitter(
			ArrayList<Emitter> emitters) {

		ArrayList<Integer> array = new ArrayList<Integer>();
		for (int i = 0; i < emitters.size(); i++) {
			EmitterDestAreas em = (EmitterDestAreas) emitters.get(i);
			int numcars = em.getNumCars();
			for (int j = 0; j < numcars; j++) {
				array.add(-1);
			}

		}

		idAreasForEachVehicle = array;
		return idAreasForEachVehicle;
	}

	/**
	 * returns a list of integers that contains the indexes of the destinatio
	 * areas with the departure area equal to "departureArea"
	 * 
	 * @param departureArea
	 * @return
	 */
	public static ArrayList<Integer> getArrayIdDestArea(Area departureArea) {

		ArrayList<Integer> ids = new ArrayList<Integer>();

		for (int i = 0; i < destinationareas.size(); i++) {
			if (destinationareas.get(i).getDepartureArea()
					.equals(departureArea)) {
				ids.add(i);
			}
		}
		return ids;
	}

	public static void printNumPoint() {
		for (int i = 0; i < destinationareas.size(); i++) {
			int numPoints = destinationareas.get(i).positions.size();
			System.out.println("points: " + numPoints);
		}
	}

	/**
	 * returns the last departure area inserted in the array "departureAreas"
	 * 
	 * @return
	 */
	public static DepartureArea getLastDepartureArea() {
		return departureAreas.get(departureAreas.size() - 1);
	}

	/**
	 * create the departure area composed by the positions in input
	 * 
	 * @param positions
	 */
	public static void createDepartureArea(ArrayList<Position> positions) {

		DepartureArea area = new DepartureArea(positions);
		area.setColor(destinationareas.size());

		departureAreas.add(area);

	}

	/**
	 * create the destination area composed by the positions in input, the
	 * reference position and the associated departure area
	 * 
	 * @param positions
	 * @param departureArea
	 * @param position
	 * @return
	 */
	public static DestinationArea createDestiantionArea(
			ArrayList<Position> positions, Area departureArea, Position position) {

		DestinationArea area = new DestinationArea(positions);
		area.setColor(destinationareas.size());
		area.setDepartureArea(departureArea);
		area.setReferencePosition(position);
		destinationareas.add(area);

		return area;
	}

	/**
	 * return the index of the destination area that contains the position "pos"
	 * 
	 * @param pos
	 * @return
	 */
	public static int getAreaFromPosition(Position pos) {

		for (Area area : destinationareas) {
			// if(ExtraOperationsManager.isInto(pos, area.p1, area.p2)){
			if (GeometryManager.isLocationInside(pos, area.positions)) {
				return area.getIndex();
			}
		}
		return -1;
	}

	/**
	 * print information about the segments that compose the path
	 * 
	 * @param path
	 * @param positions
	 */
	private static void printPolygonInsideArea(PathExtArea path,
			ArrayList<Position> positions) {

		Iterator<? extends Position> it = path.getPositions().iterator();

		String pathString = "";
		String positionString = "";

		while (it.hasNext()) {

			Position currPosition = it.next();
			pathString = pathString.concat(currPosition.toString() + " ");

		}

		for (Position p : positions) {
			positionString = positionString.concat(p.toString() + " ");
		}

		System.out.println(pathString + "- IS INTO -" + positionString);

	}

	/**
	 * returns the index of the destination area that contains all the segment
	 * of the path in input
	 * 
	 * @param path
	 * @return
	 */
	public static int getAreaFromPath(PathExtArea path) {

		for (DestinationArea area : destinationareas) {
			if (GeometryManager.isCompletelyInto(path, area.positions)) {
				//printPolygonInsideArea(path, area.positions);
				return area.getIndex();
			}

		}
		return -1;
	}

	public static Color[] getColors() {
		return colors;
	}

	public static void setColors(Color[] colors) {
		AreaManager.colors = colors;
	}

	private static Material getMaterial(Color color) {
		if (color == Color.YELLOW) {
			return Material.YELLOW;
		}
		if (color == Color.BLACK) {
			return Material.BLACK;
		}
		if (color == Color.BLUE) {
			return Material.BLUE;
		}
		if (color == Color.CYAN) {
			return Material.CYAN;
		}
		if (color == Color.DARK_GRAY) {
			return Material.DARK_GRAY;
		}
		if (color == Color.GRAY) {
			return Material.GRAY;
		}
		if (color == Color.GREEN) {
			return Material.GREEN;
		}
		if (color == Color.LIGHT_GRAY) {
			return Material.LIGHT_GRAY;
		}
		if (color == Color.MAGENTA) {
			return Material.MAGENTA;
		}
		if (color == Color.ORANGE) {
			return Material.ORANGE;
		}
		if (color == Color.PINK) {
			return Material.PINK;
		}
		if (color == Color.RED) {
			return Material.RED;
		}
		if (color == Color.WHITE) {
			return Material.WHITE;
		}
		return null;

	}

	public static void insertBeforePlacenames(WorldWindow wwd, Layer layer) {
		// Insert the layer into the layer list just before the placenames.
		int compassPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers) {
			if (l instanceof PlaceNameLayer)
				compassPosition = layers.indexOf(l);
		}

		layers.add(compassPosition, layer);
	}

	/**
	 * it is possible to load a shape file created by a chemical pollutant
	 * simulator a danger area that represent the output of the simulation will
	 * be generated
	 * 
	 * @param frame
	 * @return
	 */

	public static RenderableLayer insertOutput(AppFrame frame) {
		RenderableLayer layer = new RenderableLayer();
		ArrayList<Position> positions = new ArrayList<Position>();

		try {
			File fileKml;
			KMLRoot kmlRoot = null;
			// Create a file chooser
			final JFileChooser fc = new JFileChooser();

			// In response to a button click:
			int returnVal = fc.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				fileKml = fc.getSelectedFile();
				// This is where a real application would open the file.
				// File fileKml = new
				// File(CustomizableVariables.fileOutputSimulation);
				kmlRoot = new KMLRoot(fileKml);
				kmlRoot.parse();
			} else {
				System.out.println("Open command cancelled by user.");
			}

			// Create a KMLController to adapt the KMLRoot to the World Wind
			// renderable interface.
			KMLController kmlController = new KMLController(kmlRoot);

			System.out.println((String) kmlRoot.getField(AVKey.DISPLAY_NAME));
			layer.setName("Output Simulation");

			layer.addRenderable(kmlController);

			Set<Map.Entry<String, Object>> fieldList = kmlController
					.getKmlRoot().getFields().getEntries();
			for (Map.Entry<String, Object> entry : fieldList) {
				if (entry.getValue() instanceof KMLAbstractFeature) {
				}
				if (entry.getValue() instanceof KMLDocument) {
					java.util.List<gov.nasa.worldwind.ogc.kml.KMLAbstractFeature> list = ((KMLDocument) entry
							.getValue()).getFeatures();

					Iterator<gov.nasa.worldwind.ogc.kml.KMLAbstractFeature> iter = list
							.listIterator();
					while (iter.hasNext()) {
						gov.nasa.worldwind.ogc.kml.KMLAbstractFeature obj = iter
								.next();
						if (obj instanceof KMLPlacemark) {
							KMLAbstractGeometry geom = ((KMLPlacemark) obj)
									.getGeometry();
							if (geom instanceof KMLPolygon) {
								PositionList posList = ((KMLPolygon) geom)
										.getOuterBoundary().getCoordinates();

								for (Position pos : posList.list) {
									positions.add(pos);
								}
								break;
							}
//							System.out.println(((KMLPlacemark) obj)
//									.getGeometry());
						}

					}

				}
			}

			AreaManager.generateDangerPolygonArea(positions, true);
			Set<PathExtArea> setPaths = GeometryManager.getSelectedSegments(
					positions, TrafficSim.streetGraph.getPaths().values());
			positions = null;

			for (PathExtArea path : setPaths) {
				path.setDangerArea(true);
			}

			TrafficSim.streetGraph.setDangerAreasIncluded(true);
			TrafficSim.streetGraph.buildJGraphWithoutDangerAreas();
			TrafficSim.streetGraph.buildGraphWithoutDangerAreas();
			TrafficSim.streetGraph.updateColors();

		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.getWwd().getModel().getLayers().add(layer);
		return layer;
	}
}
