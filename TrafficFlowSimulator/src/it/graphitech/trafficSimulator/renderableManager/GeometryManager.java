package it.graphitech.trafficSimulator.renderableManager;

import gov.nasa.worldwind.geom.Position;
import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.TrafficSim.AppFrame;
import it.graphitech.trafficSimulator.renderable.PathExtArea;
import it.graphitech.trafficSimulator.renderable.emitter.ParkingArea;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * contains functions related to polygons, segments and points
 * 
 * @author a.debiasi
 * 
 */
public class GeometryManager {

	/**
	 * returns the paths that are inside a polygon composed by the list of
	 * points "positions"
	 * 
	 * @param positions
	 * @param paths
	 * @return
	 */
	public static Set<PathExtArea> getSelectedSegments(
			ArrayList<Position> positions, Iterable<PathExtArea> paths) {

		Set<PathExtArea> setPaths = new HashSet<PathExtArea>();

		Iterator<PathExtArea> it = paths.iterator();
		while (it.hasNext()) {

			PathExtArea pe = it.next();

			boolean isinto1 = isCompletelyInto(pe, positions);

			if (isinto1) {
				setPaths.add(pe);
			}
		}

		return setPaths;
	}

	/**
	 * check if a segment is inside a polygon composed by the list of points
	 * "positions"
	 * 
	 * @param pe
	 * @param positions
	 * @return
	 */
	public static boolean isCompletelyInto(PathExtArea pe,
			ArrayList<Position> positions) {
		Iterator<? extends Position> it = pe.getPositions().iterator();

		while (it.hasNext()) {

			Position currPosition = it.next();

			if (!isLocationInside(currPosition, positions)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * checks if the point is inside a polygon composed by the list of points
	 * "positions"
	 * 
	 * @param point
	 * @param positions
	 * @return
	 */
	public static boolean isLocationInside(Position point,
			ArrayList<? extends Position> positions) {

		boolean result = false;
		Position p1 = positions.get(0);
		for (int i = 1; i < positions.size(); i++) {
			Position p2 = positions.get(i);

			if (((p2.getLatitude().degrees <= point.getLatitude().degrees && point
					.getLatitude().degrees < p1.getLatitude().degrees) || (p1
					.getLatitude().degrees <= point.getLatitude().degrees && point
					.getLatitude().degrees < p2.getLatitude().degrees))
					&& (point.getLongitude().degrees < (p1.getLongitude().degrees - p2
							.getLongitude().degrees)
							* (point.getLatitude().degrees - p2.getLatitude().degrees)
							/ (p1.getLatitude().degrees - p2.getLatitude().degrees)
							+ p2.getLongitude().degrees))
				result = !result;

			p1 = p2;
		}
		return result;
	}

}
