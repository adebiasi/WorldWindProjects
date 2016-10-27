package it.graphitech.trafficSimulator.ServiceInterface;

import gov.nasa.worldwind.geom.Position;
import it.graphitech.trafficSimulator.renderable.PathExtArea;

/**
 * This class contains the information contained in the response of the wfs
 * service
 * 
 * @author a.debiasi
 * 
 */
public class ResultPath {

	PathExtArea currPath;
	Position[] availDest;

	public PathExtArea getCurrPath() {
		return currPath;
	}

	public void setCurrPath(PathExtArea currPath) {
		this.currPath = currPath;
	}

	public Position[] getAvailDest() {
		return availDest;
	}

	public void setAvailDest(Position[] availDest) {
		this.availDest = availDest;
	}

}
