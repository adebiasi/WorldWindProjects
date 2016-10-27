package it.graphitech.trafficSimulator.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.GlobalInstances;
import it.graphitech.trafficSimulator.TrafficSim.AppFrame;
import it.graphitech.trafficSimulator.renderable.PathExtArea;
import it.graphitech.trafficSimulator.renderable.emitter.ParkingArea;
import it.graphitech.trafficSimulator.renderableManager.GraphManager;

/**
 * This class manages the parking slots to render
 */
public class ParkingAreaManager {
	private static int parkingAreaNumber = 0; // Incremental ID of parking Area
	private List<ParkingArea> parkingAreas;

	public ParkingAreaManager() {
		parkingAreas = new ArrayList<ParkingArea>();

	}

	public Iterator<ParkingArea> iterator() {
		return parkingAreas.iterator();
	}

	/**
	 * adds a parking area to the list
	 */
	public void addParkingArea(ParkingArea parkingArea) {
		GlobalInstances.getRenderizer().renderParkingArea(parkingArea);
		parkingArea.setValue(AVKey.DISPLAY_NAME, "Parking Area #"
				+ parkingAreaNumber++);
		parkingAreas.add(parkingArea);
	}

	public int parkingAreasNumber() {
		return parkingAreas.size();
	}

	/**
	 * removes a parking area from the list
	 */
	public void removeParkingArea(ParkingArea parkingArea) {
		parkingArea.disable();
		parkingAreas.remove(parkingArea);

		GlobalInstances.getRenderizer().unrenderParkingArea(parkingArea);
	}

	/**
	 * checks if there are available parking slots in the input position
	 */
	public boolean checkingForFreeParkingArea(Position position) {

		Iterator<ParkingArea> it = iterator();
		while (it.hasNext()) {
			ParkingArea parkingArea = it.next();
			if (parkingArea.getPosition().equals(position)) {
				
				if (parkingArea.isAvailable()) {
					parkingArea.decreaseSize();
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * Resets the emitters list
	 */
	public void reset() {
		parkingAreas = new ArrayList<ParkingArea>();
		parkingAreaNumber = 0;
	}

	/**
	 * creates a parking slots in the middle position of each path
	 * 
	 * @param paths
	 * @param emitterManager
	 * @param streetGraph
	 * @return
	 */
	public static Position insertMiddleParkingArea(Set<PathExtArea> paths,
			EmitterManager emitterManager, GraphManager streetGraph) {
		Iterator<PathExtArea> it = paths.iterator();
		Position oldPos = null;
		int currParking=0;
		
		while (it.hasNext()) {
			PathExtArea path = it.next();
			
			Iterator<? extends Position> it2 = path.getPositions().iterator();
			oldPos = null;
			while (it2.hasNext()) {
				Position newPos = it2.next();
				if (oldPos != null) {
					Position midPos = Position.interpolate(0.5, newPos, oldPos);
					if(currParking>3){
					addParkingArea(midPos, path, streetGraph);
					currParking=0;
					}
					
				}
				currParking++;
				oldPos = newPos;
			}
		}

		return oldPos;

	}

	/**
	 * Renders a parking slot in the position of input
	 * 
	 * @param pos
	 * @param emitterManager
	 * @param path
	 * @param streetGraph
	 */
	private static void addParkingArea(Position pos, PathExtArea path,
			GraphManager streetGraph) {
		Position goal = new Position(pos.getLatitude(), pos.getLongitude(),
		// Position goal = new Position(lat, lon,
				CustomizableVariables.RENDERALTITUDE);

		Position[] nearDest = streetGraph.getNearestPositionPairFromPathPoint(
				path, goal);
		streetGraph.updateGraph(nearDest[0], nearDest[1], goal, path);
		streetGraph.addNodeInJGraph(nearDest[0], nearDest[1], goal);

		ParkingArea parkingArea = new ParkingArea(goal);

		AppFrame.parkingAreaManager.addParkingArea(parkingArea);
	}
}
