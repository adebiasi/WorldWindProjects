package it.graphitech.trafficSimulator.renderableManager;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.geom.Position.PositionList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.ogc.kml.KMLExtendedData;
import gov.nasa.worldwind.ogc.kml.KMLLineString;
import gov.nasa.worldwind.ogc.kml.KMLPlacemark;
import gov.nasa.worldwind.ogc.kml.KMLSchemaData;
import gov.nasa.worldwind.ogc.kml.KMLSimpleData;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;

import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.GlobalInstances;
import it.graphitech.trafficSimulator.entities.MyEdge;
import it.graphitech.trafficSimulator.entities.SegmentInfo;
import it.graphitech.trafficSimulator.renderable.PathExtArea;
import it.graphitech.trafficSimulator.renderable.car.Car;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

public class GraphManager {

	private Map<String, PathExtArea> paths;
	private Map<String, PathExtArea> analysisPaths;

	private Map<String, Set<String>> graph;
	private ConcurrentHashMap<String, SegmentInfo> segmentMap;
	private List<KMLPlacemark> placemarks;

	private boolean dangerAreasIncluded = false;

	DefaultDirectedWeightedGraph<String, MyEdge> jGraph;

	private Map<String, Set<String>> graphWithoutDangerAreas;
	DefaultDirectedWeightedGraph<String, MyEdge> jGraphWithoutDangerAreas;
	private RenderableLayer layer_areas;

	/**
	 * Takes a list of placemarks and sets up the application data structures
	 * 
	 * @param placemarks
	 *            list of placemarks
	 */
	public GraphManager(List<KMLPlacemark> placemarks) {
		this.placemarks = placemarks;

		buildRenderablePaths(placemarks);
		buildRenderableAnalysisPaths(placemarks);
		buildGraph();
		buildSegmentMap();
		buildJGraph();

		renderPathsInAreasLayer();
		StreetsAnalysisManager.initStreetsAnalysisLayer(analysisPaths);
	}

	/**
	 * Transorms the placemarks in paths (PathExt). The method adds the created
	 * paths in a map, whom keys are the paths' IDs
	 * 
	 * @param placemarks
	 */
	private void buildRenderablePaths(List<KMLPlacemark> placemarks) {
		paths = new HashMap<String, PathExtArea>();

		System.out.println("# placemarks: " + placemarks.size());

		// Iterate through the placemarks
		for (int index = 0; index < placemarks.size(); ++index) {
			// Retrieve placemark data
			KMLPlacemark placemark = placemarks.get(index);
			KMLExtendedData extendedData = placemark.getExtendedData();
			List<KMLSchemaData> schemaData = extendedData.getSchemaData();
			List<KMLSimpleData> simpleData = schemaData.get(0).getSimpleData();

			String id = null;
			String name = null;
			boolean oneway = false;

			// Set id, name and oneway vars if they are available
			Iterator<KMLSimpleData> i = simpleData.iterator();
			while (i.hasNext()) {
				KMLSimpleData data = i.next();
				if (data.getField("name").equals("Name")) {
					name = data.getCharacters();
				}
				if (data.getField("name").equals("id")) {
					id = data.getCharacters();
				}
				if (data.getField("name").equals("oneway")) {
					oneway = data.getCharacters().equals("yes");
				}
			}

			// Retrieve placemark geometry
			KMLLineString kmlLine = (KMLLineString) placemark.getGeometry();
			PositionList positions = kmlLine.getCoordinates();
			Iterator<? extends Position> j = (Iterator<? extends Position>) positions.list
					.iterator();

			// Add the positions to the path
			ArrayList<Position> pathPositions = new ArrayList<Position>();
			while (j.hasNext()) {
				Position pos = j.next();
				pathPositions.add(new Position(pos.getLatitude(), pos
						.getLongitude(), CustomizableVariables.RENDERALTITUDE));
			}

			// Create the path
			PathExtArea path = new PathExtArea(pathPositions);
			path.setVisible(true);
			// path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
			path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
			path.setPathType(AVKey.GREAT_CIRCLE);

			path.setArea(AreaManager.getAreaFromPosition(pathPositions.get(0)));
			// Attach street ID
			if (id != null) {
				path.setId(id);
			}

			// Attach street name
			if (name == null) {
				name = "";
			}
			path.setName(name);
			path.setValue(AVKey.DISPLAY_NAME, name);

			path.setOneway(oneway);
			path.setLength(computePathLength(path));


			paths.put(path.getId(), path);
		}

		System.out.println("num paths: " + paths.size());
	}

	private void buildRenderableAnalysisPaths(List<KMLPlacemark> placemarks) {
		analysisPaths = new HashMap<String, PathExtArea>();

		// Iterate through the placemarks
		for (int index = 0; index < placemarks.size(); ++index) {
			// Retrieve placemark data
			KMLPlacemark placemark = placemarks.get(index);
			KMLExtendedData extendedData = placemark.getExtendedData();
			List<KMLSchemaData> schemaData = extendedData.getSchemaData();
			List<KMLSimpleData> simpleData = schemaData.get(0).getSimpleData();

			String id = null;
			String name = null;
			boolean oneway = false;

			// Set id, name and oneway vars if they are available
			Iterator<KMLSimpleData> i = simpleData.iterator();
			while (i.hasNext()) {
				KMLSimpleData data = i.next();
				if (data.getField("name").equals("Name")) {
					name = data.getCharacters();
				}
				if (data.getField("name").equals("id")) {
					id = data.getCharacters();
				}
				if (data.getField("name").equals("oneway")) {
					oneway = data.getCharacters().equals("yes");
				}
			}

			// Retrieve placemark geometry
			KMLLineString kmlLine = (KMLLineString) placemark.getGeometry();
			PositionList positions = kmlLine.getCoordinates();
			Iterator<? extends Position> j = (Iterator<? extends Position>) positions.list
					.iterator();

			// Add the positions to the path
			ArrayList<Position> pathPositions = new ArrayList<Position>();
			while (j.hasNext()) {
				Position pos = j.next();
				pathPositions.add(new Position(pos.getLatitude(), pos
						.getLongitude(), CustomizableVariables.RENDERALTITUDE));
			}

			// Create the path
			PathExtArea path = new PathExtArea(pathPositions);
			path.setVisible(true);
			path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
			// path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
			path.setPathType(AVKey.GREAT_CIRCLE);

			path.setArea(AreaManager.getAreaFromPosition(pathPositions.get(0)));
			// Attach street ID
			if (id != null) {
				path.setId(id);
			}

			// Attach street name
			if (name == null) {
				name = "";
			}
			path.setName(name);
			path.setValue(AVKey.DISPLAY_NAME, name);

			path.setOneway(oneway);
			path.setLength(computePathLength(path));
			analysisPaths.put(path.getId(), path);
		}

		System.out.println("num paths: " + analysisPaths.size());
	}

	public void updateColors() {

		layer_areas.removeAllRenderables();
		// layer_areas.setName("Streets with Areas");

		// AreasInfo.printNumPoint();

		for (PathExtArea path : paths.values()) {

			ShapeAttributes attrs = new BasicShapeAttributes();

			path.setArea(AreaManager.getAreaFromPath(path));

			int index = path.getArea();

			Color col = (index != -1) ? AreaManager.colors[index] : Color.WHITE;
			attrs.setOutlineMaterial(new Material(col));

			if (path.isDangerArea()) {
				col = Color.BLUE;
				attrs.setOutlineMaterial(new Material(col));
			}

			attrs.setOutlineWidth(2d);
			path.setAttributes(attrs);

			layer_areas.addRenderable(path);
		}

	}

	/**
	 * Builds a graph with path segments. The graph is encoded in a map having
	 * as keys a string representation of a segment node and as values the
	 * string representation of adjacent nodes
	 */
	private void buildGraph() {
		graph = new HashMap<String, Set<String>>();

		for (PathExtArea path : paths.values()) {
			ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
					.getPositions();
			int nPos = positions.size();
			for (int i = 0; i < nPos; ++i) {
				Position pos = positions.get(i);

				String key = getGraphKey(pos);
				if (!graph.containsKey(key)) {
					graph.put(key, new HashSet<String>());
				}

				if (i != nPos - 1) {
					graph.get(key).add(getGraphKey(positions.get(i + 1)));
				}

				if (i != 0 && !path.isOneway()) {
					graph.get(key).add(getGraphKey(positions.get(i - 1)));
				}
			}
		}
	}

	/**
	 * Builds a graph with path segments. The graph is encoded in a map having
	 * as keys a string representation of a segment node and as values the
	 * string representation of adjacent nodes
	 * 
	 * It doesn't take into account the paths that are inside a "dangerous" area
	 */
	public void buildGraphWithoutDangerAreas() {
		graphWithoutDangerAreas = new HashMap<String, Set<String>>();

		for (PathExtArea path : paths.values()) {

			if (!path.isDangerArea()) {
				ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
						.getPositions();
				int nPos = positions.size();
				for (int i = 0; i < nPos; ++i) {
					Position pos = positions.get(i);

					String key = getGraphKey(pos);
					if (!graphWithoutDangerAreas.containsKey(key)) {
						graphWithoutDangerAreas.put(key, new HashSet<String>());
					}

					if (i != nPos - 1) {
						graphWithoutDangerAreas.get(key).add(
								getGraphKey(positions.get(i + 1)));
					}

					if (i != 0 && !path.isOneway()) {
						graphWithoutDangerAreas.get(key).add(
								getGraphKey(positions.get(i - 1)));
					}
				}

			}
		}
	}

	public void updateGraph(Position position1, Position position2,
			Position middle, PathExtArea path) {
		if (!dangerAreasIncluded) {
			String key = getGraphKey(position1);
			graph.get(key).remove(getGraphKey(position2));
			graph.get(key).add(getGraphKey(middle));

			String key2 = getGraphKey(position2);
			graph.get(key2).remove(getGraphKey(position1));
			graph.get(key2).add(getGraphKey(middle));

			String key3 = getGraphKey(middle);
			graph.put(key3, new HashSet<String>());
			graph.get(key3).add(getGraphKey(position1));
			graph.get(key3).add(getGraphKey(position2));

		} else {
			String key = getGraphKey(position1);
			graphWithoutDangerAreas.get(key).remove(getGraphKey(position2));
			graphWithoutDangerAreas.get(key).add(getGraphKey(middle));

			String key2 = getGraphKey(position2);
			graphWithoutDangerAreas.get(key2).remove(getGraphKey(position1));
			graphWithoutDangerAreas.get(key2).add(getGraphKey(middle));

			String key3 = getGraphKey(middle);
			graphWithoutDangerAreas.put(key3, new HashSet<String>());
			graphWithoutDangerAreas.get(key3).add(getGraphKey(position1));
			graphWithoutDangerAreas.get(key3).add(getGraphKey(position2));
		}
	}

	/**
	 * Method used for building a key from a position. The key includes the
	 * coordinates of a position.
	 * 
	 * @param pos
	 * @return a string representation of a position
	 */
	private String getGraphKey(Position pos) {
		return pos.getLatitude().degrees + ";" + pos.getLongitude().degrees;
	}

	/**
	 * Converts a string representation of a position into a position
	 * 
	 * @param key
	 * @return the position
	 */
	private Position keyToPosition(String key) {
		String[] coordinates = key.split(";");
		Position pos = Position.fromDegrees(Double.parseDouble(coordinates[0]),
				Double.parseDouble(coordinates[1]));
		return new Position(pos.getLatitude(), pos.getLongitude(),
				CustomizableVariables.RENDERALTITUDE);
	}

	private void buildSegmentMap() {
		// segmentMap = new HashMap<String, SegmentInfo>();
		segmentMap = new ConcurrentHashMap<String, SegmentInfo>();

		Iterator<PathExtArea> it = paths.values().iterator();
		Iterator<PathExtArea> it2 = analysisPaths.values().iterator();

		while (it.hasNext()) {

			PathExtArea path = it.next();
			PathExtArea path2 = it2.next();

			ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
					.getPositions();
			int nPos = positions.size();
			for (int i = 0; i < nPos; ++i) {
				Position pos = positions.get(i);

				if (i != nPos - 1) {
					Position end = positions.get(i + 1);
					segmentMap.put(segmentToKey(pos, end), new SegmentInfo(
							path, path2, computeDistance(pos, end)));
				}

				if (i != 0 && !path.isOneway()) {
					Position end = positions.get(i - 1);
					segmentMap.put(segmentToKey(pos, end), new SegmentInfo(
							path, path2, computeDistance(pos, end)));
				}
			}
		}

		// printAllInfo();
	}

	/**
	 * Builds a key from the two points identifying a segment The key is built
	 * using the coordinates of the two points
	 * 
	 * @param p1
	 *            position one
	 * @param p2
	 *            position two
	 * @return
	 */
	private String segmentToKey(Position p1, Position p2) {
		return getGraphKey(p1) + ";" + getGraphKey(p2);
	}

	/**
	 * Compute the length of a path summing the lenght of its segments
	 * 
	 * @param path
	 * @return the length of the path
	 */
	public double computePathLength(PathExtArea path) {
		ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
				.getPositions();
		double length = 0;
		int nPos = positions.size();
		for (int i = 0; i < nPos - 1; ++i) {
			length += computeDistance(positions.get(i), positions.get(i + 1));
		}
		return length;
	}

	/**
	 * Updates the information related to a segment, namely information about
	 * the cars in it. The car is in position p2, it is going towards position
	 * p3 and it was coming from position p1. This means that it is exiting from
	 * the segment identified by the position pair (p1, p2) and entering the
	 * segment (p2, p3).
	 * 
	 * @param car
	 * @param p1
	 *            car origin
	 * @param p2
	 *            car position
	 * @param p3
	 *            car next destination
	 */
	public void updateSegments(Car car, Position p1, Position p2, Position p3) {
		// my modify
		if ((p1 != null) & (p2 != null) & (p3 != null)) {

			decrementSegment(car, p1, p2);
			incrementSegment(car, p2, p3);

			String key1 = segmentToKey(p1, p2);
			String key2 = segmentToKey(p2, p3);

			if ((key1 != null) & (key2 != null)) {

				SegmentInfo seg1 = segmentMap.get(key1);
				SegmentInfo seg2 = segmentMap.get(key2);

				if ((seg1 != null) && (seg2 != null)) {
					PathExtArea path1 = seg1.path;
					PathExtArea path2 = seg2.path;
					if (path1 != path2) {
						/*
						 * The visual attributes of the paths are set
						 * considering the number of cars in them and their
						 * length.
						 */
						double w1 = computeWidth(totalCarsInPath(path1),
								path1.getLength());
						double w2 = computeWidth(totalCarsInPath(path2),
								path2.getLength());
						path1.getAttributes().setOutlineWidth(w1);
						path2.getAttributes().setOutlineWidth(w2);

						//int currentCountedCarsInPath = seg1.counter;
						//int currentCountedCarsInPath2 = seg2.counter;

						int currentCountedCarsInPath = currentCarsInPath(path1);
						int currentCountedCarsInPath2 = currentCarsInPath(path2);
						
						int totalCountedCarsInPath = totalCountedCarsInPath(path1)[2];
						int totalCountedCarsInPath2 = totalCountedCarsInPath(path2)[2];

						StreetsAnalysisManager.updateStreetAnalysisLayer(seg1,
								totalCountedCarsInPath,
								currentCountedCarsInPath);
						StreetsAnalysisManager.updateStreetAnalysisLayer(seg2,
								totalCountedCarsInPath2,
								currentCountedCarsInPath2);

					}

				}
			}

		}
	}

	/**
	 * Checks if there is a segment identified by position p1 and p2
	 * 
	 * @param p1
	 * @param p2
	 * @return true if the segment is in the graph, false otherwise
	 */
	public boolean segmentExists(Position p1, Position p2) {
		return segmentMap.containsKey(segmentToKey(p1, p2));
	}

	/**
	 * Adds a car to the segment identified by position p1 and p2
	 * 
	 * @param car
	 * @param p1
	 * @param p2
	 */
	private void incrementSegment(Car car, Position p1, Position p2) {
		String key = segmentToKey(p1, p2);
		SegmentInfo info = segmentMap.get(key);

		if (info != null) {

			info.counter++;
			info.total_counter++;
			// Add a car at the beginning of the list
			info.cars.add(0, car);

			if (!dangerAreasIncluded) {
				// Update edge weight
				MyEdge edge = jGraph.getEdge(getGraphKey(p1), getGraphKey(p2));
				double newWeight = edge.getLength()
						+ CustomizableVariables.PER_CAR_PENALTY * info.counter;
				jGraph.setEdgeWeight(edge, newWeight);

			} else {

		MyEdge edge = jGraphWithoutDangerAreas.getEdge(getGraphKey(p1),
						getGraphKey(p2));

				if (edge == null) {
					System.out.println("this edge doesn't exist in the graph");
				}

				double edgeLength = edge.getLength();
				int numCars = info.counter;
				double newWeight = edgeLength
						+ CustomizableVariables.PER_CAR_PENALTY * numCars;

				jGraphWithoutDangerAreas.setEdgeWeight(edge, newWeight);
			}

		}
	}

	/**
	 * Removes a car from the segment identified by position p1 and p2
	 * 
	 * @param car
	 * @param p1
	 * @param p2
	 */
	private void decrementSegment(Car car, Position p1, Position p2) {
		String key = segmentToKey(p1, p2);
		if (key != null) {
			SegmentInfo info = segmentMap.get(key);

			if (info != null) {
				boolean isRemoved = info.cars.remove(car);

				if (isRemoved) {
					info.counter--;

					if (!dangerAreasIncluded) {
						// Update edge weight
						MyEdge edge = jGraph.getEdge(getGraphKey(p1),
								getGraphKey(p2));
						double newWeight = edge.getLength()
								+ CustomizableVariables.PER_CAR_PENALTY
								* info.counter;

						if (newWeight <= 0) {
							System.out.println("isRemoved: " + isRemoved);
							System.out.println("edge.getLength(): "
									+ edge.getLength());
							System.out.println("info.counter: " + info.counter);
							System.out.println("WEIGHT < 0: " + newWeight);
						}

						jGraph.setEdgeWeight(edge, newWeight);

					} else {

				MyEdge edge = jGraphWithoutDangerAreas.getEdge(
								getGraphKey(p1), getGraphKey(p2));
						double newWeight = edge.getLength()
								+ CustomizableVariables.PER_CAR_PENALTY
								* info.counter;

						if (newWeight <= 0) {
							System.out.println("isRemoved: " + isRemoved);
							System.out.println("edge.getLength(): "
									+ edge.getLength());
							System.out.println("info.counter: " + info.counter);
							System.out.println("WEIGHT < 0: " + newWeight);
						}

						jGraphWithoutDangerAreas.setEdgeWeight(edge, newWeight);
					}

				}
			}
		}
	}

	/**
	 * Selects the right speed of a car moving in the segment identified by
	 * positions p1 and p2. The queue of cars in the segment is retrieved. If a
	 * faster car has a number of cars in front of it greater than the passable
	 * cars number, the faster car slows down in the moment it is approaching
	 * the first slower car. Otherwise the car speed is not changed. When a
	 * slowed car is not constrained, it reaches back its original speed.
	 * 
	 * @param car
	 * @param p1
	 * @param p2
	 */
	public void setCarSpeed(Car car, Position p1, Position p2) {
		int CARSDISTANCE = CustomizableVariables.CARSDISTANCE; // Braking
																// distance
		int PASSABLECARS = CustomizableVariables.PASSABLECARS; // Number of cars
																// passable in
																// the segments

		String key = segmentToKey(p1, p2);

		if (key != null) {
			SegmentInfo info = segmentMap.get(key);
			if (info != null) {
				Integer carIndex = info.cars.indexOf(car); // Position of the
															// car in the queue
				int carsSize = info.cars.size(); // Cars in the queue

				if (carIndex != null) {

					/*
					 * If there are more than one car and our car has other cars
					 * in front of it...
					 */
					if (carsSize > 1 && carIndex + 1 < carsSize) {
						// Retrieve the next car in the queue
						Car nextCar = info.cars.get(carIndex + 1);
						// Compute its distance
						double carDistance = computeDistance(
								nextCar.getPosition(), car.getPosition());
						// If the car are sufficiently near and we cannot pass
						if (carDistance < CARSDISTANCE
								&& carsSize - carIndex + 1 > PASSABLECARS) {
							// Slow down
							double speed = nextCar.getSpeed() / 2;
							if (speed < 10) {
								speed = 10;
							}
							car.setSpeed(speed);
						}
					} else {
						// Back to the original speed
						car.setSpeed(car.getOriginalSpeed());
					}
				}

			}
		}
	}

	/**
	 * Add a new car to a segment identified by positions p1 and p2. The visual
	 * aspect of the segment is updated too
	 * 
	 * @param car
	 * @param p1
	 * @param p2
	 */
	public void addNewCarToSegment(Car car, Position p1, Position p2) {
		incrementSegment(car, p1, p2);
		SegmentInfo pathSI = segmentMap.get(segmentToKey(p1, p2));
		double w = computeWidth(totalCarsInPath(pathSI.path),
				pathSI.path.getLength());
		pathSI.path.getAttributes().setOutlineWidth(w);

	}

	/**
	 * Remove a car from the segment identified by positions p1 and p2. The
	 * visual aspect of the segment is updated too
	 * 
	 * @param car
	 * @param p1
	 * @param p2
	 */
	public void removeCarFromSegment(Car car, Position p1, Position p2) {
		decrementSegment(car, p1, p2);
		SegmentInfo pathSI = segmentMap.get(segmentToKey(p1, p2));
		if (pathSI != null) {
			double w = computeWidth(totalCarsInPath(pathSI.path),
					pathSI.path.getLength());
			pathSI.path.getAttributes().setOutlineWidth(w);
		}

	}

	/**
	 * Computes the number of cars in a path.
	 * 
	 * @param path
	 * @return ret[0] cars in the normal way, ret[1] cars in the opposite way,
	 *         ret[2] total number of cars
	 */
	public int[] totalCarsInPath(PathExtArea path) {
		ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
				.getPositions();
		int ret[] = new int[3];
		ret[0] = ret[1] = ret[2] = 0;
		int nPos = positions.size();
		for (int i = 0; i < nPos; ++i) {
			if (i != nPos - 1) {
				ret[0] += segmentMap.get(segmentToKey(positions.get(i),
						positions.get(i + 1))).counter;
			}
			if (i != 0 && !path.isOneway()) {
				ret[1] += segmentMap.get(segmentToKey(positions.get(i),
						positions.get(i - 1))).counter;
			}
		}

		ret[2] = ret[0] + ret[1];
		return ret;
	}

	public int[] totalCountedCarsInPath(PathExtArea path) {
		ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
				.getPositions();
		int ret[] = new int[3];
		ret[0] = ret[1] = ret[2] = 0;
		int nPos = positions.size();
		for (int i = 0; i < nPos; ++i) {
			if (i != nPos - 1) {
				ret[0] += segmentMap.get(segmentToKey(positions.get(i),
						positions.get(i + 1))).total_counter;
			}
			if (i != 0 && !path.isOneway()) {
				ret[1] += segmentMap.get(segmentToKey(positions.get(i),
						positions.get(i - 1))).total_counter;
			}
		}

		ret[2] = ret[0] + ret[1];
		return ret;
	}

	public int currentCarsInPath(PathExtArea path) {
		ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
				.getPositions();
		int numMaxCars=0;
		
		int nPos = positions.size();
		for (int i = 0; i < nPos; ++i) {
			if (i != nPos - 1) {
				int count = segmentMap.get(segmentToKey(positions.get(i),
						positions.get(i + 1))).counter;
				if(count>numMaxCars){numMaxCars=count;}
							}
			if (i != 0 && !path.isOneway()) {
				int count = segmentMap.get(segmentToKey(positions.get(i),
						positions.get(i - 1))).counter;
				if(count>numMaxCars){numMaxCars=count;
				
			}
		}
		}
		
		return numMaxCars;
	}
	
	/**
	 * Computes the width of the visual representation of a path considered its
	 * length and congestion level
	 * 
	 * @param cars
	 * @param pathLength
	 * @return the width of the path
	 */
	private double computeWidth(int[] cars, double pathLength) {
		double minWidth = 2;
		double maxWidth = 10;
		double width = minWidth;

		double carTreshold = pathLength / 22;

		if (cars[0] > carTreshold || cars[1] > carTreshold) {
			width = maxWidth;
		} else {
			double max = Math.max(cars[0], cars[1]);
			width = max * maxWidth / carTreshold;
		}

		if (width < minWidth) {
			width = minWidth;
		}

		return width;
	}

	/*
	 * JGraph related code
	 */

	/**
	 * Builds a specular graph using JGraphT objects
	 */
	private void buildJGraph() {

		System.out.println("BUILD JGRAPH");

		jGraph = new DefaultDirectedWeightedGraph<String, MyEdge>(MyEdge.class);

		for (PathExtArea path : paths.values()) {
			ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
					.getPositions();
			int nPos = positions.size();

			for (Position position : positions) {
				String key = getGraphKey(position);
				if (!jGraph.containsVertex(key)) {
					jGraph.addVertex(key);
				}
			}

			for (int i = 0; i < nPos; ++i) {
				Position pos = positions.get(i);

				if (i != nPos - 1) {
					String v1 = getGraphKey(pos);
					String v2 = getGraphKey(positions.get(i + 1));
					MyEdge edge = new MyEdge();
					jGraph.addEdge(v1, v2, edge);

					double edgeLength = computeDistance(pos,
							positions.get(i + 1));
					edge.setLength(edgeLength);
					jGraph.setEdgeWeight(edge, edgeLength);
				}

				if (i != 0 && !path.isOneway()) {
					String v1 = getGraphKey(pos);
					String v2 = getGraphKey(positions.get(i - 1));
					MyEdge edge = new MyEdge();
					jGraph.addEdge(v1, v2, edge);

					double edgeLength = computeDistance(pos,
							positions.get(i - 1));
					edge.setLength(edgeLength);
					jGraph.setEdgeWeight(edge, edgeLength);
				}
			}
		}
	}

	public void buildJGraphWithoutDangerAreas() {

		System.out.println("BUILD JGRAPH with dangerous areas");
		
		jGraphWithoutDangerAreas = new DefaultDirectedWeightedGraph<String, MyEdge>(
				MyEdge.class);

		for (PathExtArea path : paths.values()) {

			if (!path.isDangerArea()) {
				ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
						.getPositions();
				int nPos = positions.size();

				for (Position position : positions) {
					String key = getGraphKey(position);
					if (!jGraphWithoutDangerAreas.containsVertex(key)) {
						jGraphWithoutDangerAreas.addVertex(key);
					}
				}

				for (int i = 0; i < nPos; ++i) {
					Position pos = positions.get(i);

					if (i != nPos - 1) {
						String v1 = getGraphKey(pos);
						String v2 = getGraphKey(positions.get(i + 1));
						MyEdge edge = new MyEdge();
						jGraphWithoutDangerAreas.addEdge(v1, v2, edge);

						double edgeLength = computeDistance(pos,
								positions.get(i + 1));
						edge.setLength(edgeLength);
						jGraphWithoutDangerAreas
								.setEdgeWeight(edge, edgeLength);
					}

					if (i != 0 && !path.isOneway()) {
						String v1 = getGraphKey(pos);
						String v2 = getGraphKey(positions.get(i - 1));
						MyEdge edge = new MyEdge();
						jGraphWithoutDangerAreas.addEdge(v1, v2, edge);

						double edgeLength = computeDistance(pos,
								positions.get(i - 1));
						edge.setLength(edgeLength);
						jGraphWithoutDangerAreas
								.setEdgeWeight(edge, edgeLength);
					}
				}
			}
		}
	}

	public int getNumVertex() {
		if (!dangerAreasIncluded) {
			return jGraph.vertexSet().size();
		} else {
			// System.out.println("DANGER!!");
			return jGraphWithoutDangerAreas.vertexSet().size();
		}

	}

	/**
	 * Adds an intermediate node between two positions p1 and p2.
	 * 
	 * @param p1
	 * @param p2
	 * @param newPos
	 */
	public void addNodeInJGraph(Position p1, Position p2, Position newPos) {

		SegmentInfo segmentInfo = segmentMap.get(segmentToKey(p1, p2));
		PathExtArea path = segmentInfo.path;
		PathExtArea analysisPath = segmentInfo.analysisPath;

		String v1 = getGraphKey(p1);
		String v2 = getGraphKey(p2);
		String newV = getGraphKey(newPos);

		if (!dangerAreasIncluded) {

			jGraph.addVertex(newV);

			MyEdge edge = new MyEdge();
			jGraph.addEdge(v1, newV, edge);

			double edgeLength = computeDistance(p1, newPos);
			edge.setLength(edgeLength);
			jGraph.setEdgeWeight(edge, edgeLength);

			segmentMap.put(segmentToKey(p1, newPos), new SegmentInfo(path,
					analysisPath, edgeLength));

			edge = new MyEdge();
			jGraph.addEdge(newV, v2, edge);

			edgeLength = computeDistance(newPos, p2);
			edge.setLength(edgeLength);
			jGraph.setEdgeWeight(edge, edgeLength);

			segmentMap.put(segmentToKey(newPos, p2), new SegmentInfo(path,
					analysisPath, edgeLength));

			if (!path.isOneway()) {
				edge = new MyEdge();
				jGraph.addEdge(newV, v1, edge);

				edgeLength = computeDistance(newPos, p1);
				edge.setLength(edgeLength);
				jGraph.setEdgeWeight(edge, edgeLength);

				segmentMap.put(segmentToKey(newPos, p1), new SegmentInfo(path,
						analysisPath, edgeLength));

				edge = new MyEdge();
				jGraph.addEdge(v2, newV, edge);

				edgeLength = computeDistance(p2, newPos);
				edge.setLength(edgeLength);
				jGraph.setEdgeWeight(edge, edgeLength);

				segmentMap.put(segmentToKey(p2, newPos), new SegmentInfo(path,
						analysisPath, edgeLength));
			}
		} else {

		jGraphWithoutDangerAreas.addVertex(newV);

			MyEdge edge = new MyEdge();
			jGraphWithoutDangerAreas.addEdge(v1, newV, edge);

			double edgeLength = computeDistance(p1, newPos);
			edge.setLength(edgeLength);
			jGraphWithoutDangerAreas.setEdgeWeight(edge, edgeLength);

			segmentMap.put(segmentToKey(p1, newPos), new SegmentInfo(path,
					analysisPath, edgeLength));

			edge = new MyEdge();
			jGraphWithoutDangerAreas.addEdge(newV, v2, edge);

			edgeLength = computeDistance(newPos, p2);
			edge.setLength(edgeLength);
			jGraphWithoutDangerAreas.setEdgeWeight(edge, edgeLength);

			segmentMap.put(segmentToKey(newPos, p2), new SegmentInfo(path,
					analysisPath, edgeLength));

			if (!path.isOneway()) {
				edge = new MyEdge();
				jGraphWithoutDangerAreas.addEdge(newV, v1, edge);

				edgeLength = computeDistance(newPos, p1);
				edge.setLength(edgeLength);
				jGraphWithoutDangerAreas.setEdgeWeight(edge, edgeLength);

				segmentMap.put(segmentToKey(newPos, p1), new SegmentInfo(path,
						analysisPath, edgeLength));

				edge = new MyEdge();
				jGraphWithoutDangerAreas.addEdge(v2, newV, edge);

				edgeLength = computeDistance(p2, newPos);
				edge.setLength(edgeLength);
				jGraphWithoutDangerAreas.setEdgeWeight(edge, edgeLength);

				segmentMap.put(segmentToKey(p2, newPos), new SegmentInfo(path,
						analysisPath, edgeLength));
			}
		}

	}

	public void renderPathsInAreasLayer() {

		this.layer_areas = new RenderableLayer();
		GlobalInstances.getWwd().getModel().getLayers().add(layer_areas);

		layer_areas.removeAllRenderables();
		layer_areas.setName("Streets with Areas");

		for (PathExtArea path : paths.values()) {

			ShapeAttributes attrs = new BasicShapeAttributes();

			int index = path.getArea();
			Color col = (index != -1) ? AreaManager.colors[index] : Color.WHITE;

			attrs.setOutlineMaterial(new Material(col));

			attrs.setOutlineWidth(2d);
			path.setAttributes(attrs);
			path.setFollowTerrain(true);

			layer_areas.addRenderable(path);
		}
	}

	/**
	 * Using the graph, returns all the adjacent nodes of node at position pos
	 * 
	 * @param pos
	 * @return the adjacent nodes of pos
	 */
	public List<Position> reachableFrom(Position pos) {
		String key = getGraphKey(pos);
		List<Position> positions = new ArrayList<Position>();

		if (!dangerAreasIncluded) {
			if (graph.containsKey(key)) {
				Set<String> adjacents = graph.get(key);
				for (String adjacent : adjacents) {
					positions.add(keyToPosition(adjacent));
				}
			}
		} else {
			if (graphWithoutDangerAreas.containsKey(key)) {
				Set<String> adjacents = graphWithoutDangerAreas.get(key);
				for (String adjacent : adjacents) {
					positions.add(keyToPosition(adjacent));
				}
			}
		}

		return positions;
	}

	/**
	 * Computes the best path between position pos and dest using segments
	 * weights
	 * 
	 * @param pos
	 * @param dest
	 * @return the list of positions of the best path. Returs null if the path
	 *         does not exist
	 */
	public List<Position> getNextNodeForDestination(Position pos, Position dest) {
		List<MyEdge> edges = null;
		if (!dangerAreasIncluded) {
			edges = DijkstraShortestPath.findPathBetween(jGraph,
					getGraphKey(pos), getGraphKey(dest));
		} else {
			edges = DijkstraShortestPath.findPathBetween(
					jGraphWithoutDangerAreas, getGraphKey(pos),
					getGraphKey(dest));
		}

		String nextNode = (String) edges.get(0).getB();
		List<Position> positions = new ArrayList<Position>();
		positions.add(keyToPosition(nextNode));

		return positions;
	}

	public List<Position> getNextNodesForDestination(Position pos, Position dest) {
		List<MyEdge> edges = null;
		if (!dangerAreasIncluded) {
			edges = DijkstraShortestPath.findPathBetween(jGraph,
					getGraphKey(pos), getGraphKey(dest));
		} else {
			edges = DijkstraShortestPath.findPathBetween(
					jGraphWithoutDangerAreas, getGraphKey(pos),
					getGraphKey(dest));
		}

		List<Position> positions = new ArrayList<Position>();

		if (edges == null) {
			return null;
		}

		for (MyEdge edge : edges) {
			String nextNode = (String) edge.getB();
			positions.add(keyToPosition(nextNode));
		}
		return positions;
	}

	/**
	 * Utility method for retrieving a random position from the graph
	 * 
	 * @return a random position from the graph
	 */
	public Position getRandomPosition() {
		Random generator = new Random();

		if (!dangerAreasIncluded) {
			Object[] array = graph.keySet().toArray();
			String key = (String) array[generator.nextInt(array.length)];
			return keyToPosition(key);
		} else {
			Object[] array = graphWithoutDangerAreas.keySet().toArray();
			String key = (String) array[generator.nextInt(array.length)];
			return keyToPosition(key);
		}
	}

	/**
	 * Utility method for retrieving the same position from the graph
	 * 
	 * @return the same position from the graph
	 */
	public Position getFixedPosition() {
		if (!dangerAreasIncluded) {
			String key = (String) graph.keySet().toArray()[16];
			return keyToPosition(key);
		} else {
			String key = (String) graphWithoutDangerAreas.keySet().toArray()[16];
			return keyToPosition(key);
		}
	}

	/**
	 * Finds the two positions identifying the segment of the path clicked by
	 * the user. Whenever a user click on a path we get the click position and
	 * the path clicked. A path can contain more segments. In order to retrieve
	 * the right segment the method iterates over all the segments and find the
	 * more likely clicked one.
	 * 
	 * Every segment is divided in small parts. Then the part which yields the
	 * smaller distance from the clicked point will be owned by the clicked
	 * segment.
	 * 
	 * @param path
	 * @param pos
	 * @return the position of the clicked segment
	 */
	public Position[] getNearestPositionPairFromPathPoint(PathExtArea path,
			Position pos) {
		Vec4 emitterPoint = GlobalInstances.getGlobe()
				.computePointFromPosition(pos);
		ArrayList<? extends Position> positions = (ArrayList<? extends Position>) path
				.getPositions();
		int nPos = positions.size();
		int nSegments = 12; // For better precision this should be higher for
							// longer segments
		int smallerPos = 0;
		double distance = 999999;
		for (int i = 0; i < nPos - 1; ++i) {
			Position pos1 = positions.get(i);
			Position pos2 = positions.get(i + 1);

			double longDiff = pos2.getLongitude().degrees
					- pos1.getLongitude().degrees;
			double latDiff = pos2.getLatitude().degrees
					- pos1.getLatitude().degrees;
			double longInc = longDiff / nSegments;
			double latInc = latDiff / nSegments;

			for (int j = 0; j < nSegments; ++j) {
				Position newPosition = Position.fromDegrees(
						pos1.getLatitude().degrees + latInc * nSegments,
						pos1.getLongitude().degrees + longInc * nSegments);
				Vec4 newPosPoint = GlobalInstances.getGlobe()
						.computePointFromPosition(newPosition);
				double newDistance = newPosPoint.distanceTo3(emitterPoint);
				if (newDistance < distance) {
					distance = newDistance;
					smallerPos = i;
				}
			}
		}

		Position[] availDest = new Position[2];
		availDest[0] = positions.get(smallerPos);
		availDest[1] = positions.get(smallerPos + 1);

		return availDest;
	}

	/**
	 * Computes the distance between two positions
	 * 
	 * @param p1
	 * @param p2
	 * @return the distance between the two positions
	 */
	private double computeDistance(Position p1, Position p2) {
		Vec4 point1 = GlobalInstances.getGlobe().computePointFromPosition(p1);
		Vec4 point2 = GlobalInstances.getGlobe().computePointFromPosition(p2);
		double distance = point1.distanceTo3(point2);
		return distance;
	}

	/**
	 * Prints the number of car in a segment
	 */
	public void printCars() {
		System.out
				.println("-------------------PRINT INFO------------------------");
		for (Entry<String, SegmentInfo> entry : segmentMap.entrySet()) {
			if (entry.getValue().total_counter > 0) {
				System.out.println(entry.getValue().path.getName() + ": "
						+ entry.getValue().total_counter);
			}
		}
		System.out
				.println("------------------------------------------------------");
	}

	public void printAllInfo() {
		System.out
				.println("-------------------PRINT ALL INFO------------------------");
		for (Entry<String, SegmentInfo> entry : segmentMap.entrySet()) {
			System.out.println(entry.getValue().path.getName() + ": "
					+ entry.getValue().total_counter);

		}
		System.out
				.println("------------------------------------------------------");
	}

	/**
	 * Return the name of the path identified by positions p1 and p2
	 * 
	 * @param p1
	 * @param p2
	 * @return the name of the path. An empty string if the path does not exist
	 */
	public String lookUpSegment(Position p1, Position p2) {
		String key = segmentToKey(p1, p2);
		if (segmentMap.containsKey(key)) {
			return segmentMap.get(key).path.getName();
		}
		return "";
	}

	/**
	 * Resets the streetGraph. A clean state is restored.
	 */
	public void reset() {
		buildRenderablePaths(placemarks);
		buildRenderableAnalysisPaths(placemarks);
		buildGraph();
		buildSegmentMap();
		buildJGraph();
		// renderPaths();
		renderPathsInAreasLayer();
		StreetsAnalysisManager.initStreetsAnalysisLayer(analysisPaths);
	}

	public Map<String, PathExtArea> getPaths() {
		return paths;
	}

	public Map<String, Set<String>> getGraph() {

		if (!dangerAreasIncluded) {
			return graph;
		} else {
			return graphWithoutDangerAreas;
		}

	}

	public ConcurrentHashMap<String, SegmentInfo> getSegmentMap() {
		return segmentMap;
	}

	public void setDangerAreasIncluded(boolean dangerAreasIncluded) {
		this.dangerAreasIncluded = dangerAreasIncluded;
	}

}
