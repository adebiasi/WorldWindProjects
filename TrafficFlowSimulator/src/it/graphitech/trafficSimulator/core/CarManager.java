package it.graphitech.trafficSimulator.core;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;

import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.GlobalInstances;
import it.graphitech.trafficSimulator.TrafficSim;
import it.graphitech.trafficSimulator.TrafficSim.AppFrame;
import it.graphitech.trafficSimulator.renderable.car.Car;
import it.graphitech.trafficSimulator.renderable.car.CarWithStatus;
import it.graphitech.trafficSimulator.renderableManager.AreaManager;
import it.graphitech.trafficSimulator.renderableManager.GraphManager;
import it.graphitech.trafficSimulator.renderableManager.Renderizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CarManager {

	private List<Car> cars; // list of cars
	private boolean dieOnDeadEnd; // Parameter for removing cars in two-way
									// dead-end
	private long lastTime; // Last time of render() invocation
	private Globe g; // World Wind globe

	/**
	 * CarManager constructor
	 * 
	 * @param streetGraph
	 */
	public CarManager() {
		this.cars = new ArrayList<Car>();
		this.dieOnDeadEnd = false;
		this.lastTime = System.currentTimeMillis();

		this.g = GlobalInstances.getGlobe();
	}

	/**
	 * Adds a new car in the network: in the car manager, in the segment data
	 * and in the renderer
	 * 
	 * @param car
	 *            the car to add
	 */
	public void addCar(Car car) {
		this.cars.add(car);
		this.getStreetGraph().addNewCarToSegment(car, car.getOrigin(),
				car.getDestination());
		GlobalInstances.getRenderizer().renderCar(car);

	}

	/**
	 * remove the car from the network
	 * 
	 * @param car
	 */
	private void removeCar(Car car) {
		this.cars.remove(car);
		GlobalInstances.getRenderizer().unrenderCar(car);
	}

	/**
	 * Resets the cars list
	 */
	public void reset() {
		this.cars = new ArrayList<Car>();
	}

	/**
	 * 
	 * @return the die on dead end parameter
	 */
	public boolean getDieOnDeadEnd() {
		return this.dieOnDeadEnd;
	}

	/**
	 * 
	 * @param value
	 */
	public void setDieOnDeadEnd(boolean value) {
		this.dieOnDeadEnd = value;
	}

	/**
	 * Selects a valid destination reachable from the current one, excluding the
	 * provenance one
	 * 
	 * @param origin
	 * @param lastVisited
	 * @return the next position to reach
	 */
	public Position chooseDestination(Position origin, Position lastVisited) {
		List<Position> positions = getStreetGraph().reachableFrom(origin);
		Collections.shuffle(positions);
		Iterator<Position> i = positions.iterator();
		Position result = null;
		while (i.hasNext()) {
			Position pos = i.next();
			if (!pos.equals(lastVisited)) {
				result = pos;
			}
		}
		return result;
	}

	/**
	 * Selects a valid destination reachable from the current one, excluding the
	 * provenance one
	 * 
	 * @param origin
	 * @param lastVisited
	 * @return the next position to reach
	 */
	public Position chooseDestinationInsideArea(Position origin,
			Position lastVisited) {
		List<Position> positions = getStreetGraph().reachableFrom(origin);
		// List<Position> positions =
		// getStreetGraph().reachableJgraphFrom(origin);
		Collections.shuffle(positions);
		Iterator<Position> i = positions.iterator();
		Position result = null;
		while (i.hasNext()) {
			Position pos = i.next();
			if ((!pos.equals(lastVisited))
					&& (AreaManager.getAreaFromPosition(pos) == AreaManager
							.getAreaFromPosition(lastVisited))) {
				result = pos;
			}
		}
		return result;
	}

	public Position chooseRandomDestination(Position origin,
			Position lastVisited) {
		List<Position> positions = getStreetGraph().reachableFrom(origin);
		// List<Position> positions =
		// getStreetGraph().reachableJgraphFrom(origin);
		Collections.shuffle(positions);
		Iterator<Position> i = positions.iterator();
		Position result = null;
		while (i.hasNext()) {
			Position pos = i.next();
			if (!pos.equals(lastVisited)) {
				result = pos;
			}
		}
		return result;
	}

	/**
	 * Compute the next node of the best path from origin to destination goal
	 * 
	 * @param origin
	 * @param goal
	 * @return the next node of the best path
	 */
	public Position chooseNextNode(Position origin, Position goal) {
		List<Position> positions = getStreetGraph().getNextNodeForDestination(
				origin, goal);
		if (positions.isEmpty()) {
			return null;
		} else {
			return positions.get(0);
		}
	}

	/**
	 * Compute the next node of the best path from origin to destination area
	 * 
	 * @param origin
	 * @param goal
	 * @return the next node of the best path
	 */
	public List<Position> chooseNextNodes(CarWithStatus car) {
		Position origin = car.getOrigin();
		Position goal = car.getGoalDestination();

		List<Position> positions = getStreetGraph().getNextNodesForDestination(
				origin, goal);
		if (positions == null) {
			car.setStatus(CarWithStatus.NO_DESTINATION);
			return null;
		} else if (positions.isEmpty()) {
			return null;
		} else {
			if (positions.size() < CarWithStatus.pathSize) {
				return positions;
			} else
				return positions.subList(0, CarWithStatus.pathSize);
		}
	}

	/**
	 * decide the position of all cars
	 */
	public void render() {
		long newTime = System.currentTimeMillis();
		long timeDelta = newTime - this.lastTime;
		this.lastTime = newTime;

		List<Car> carToRemove = new ArrayList<Car>();

		for (int i = 0; i < cars.size(); ++i) {
			Car car = cars.get(i);
			if (car.getDestination().equals(car.getPosition())) {
				Position destination = null;
				if (car instanceof CarWithStatus) {
					manageAreaCar((CarWithStatus) car, carToRemove);
				} else if (car.hasGoalDestination()) {
					manageDestCar(car, carToRemove, destination);

				}
				// Car with no destination
				else {
					manageCar(car, carToRemove, destination);

				}
			} else {

				Position currentPos = car.getPosition();
				Position originPos = car.getOrigin();
				Position destinationPos = car.getDestination();

				if (!currentPos.equals(destinationPos)) {

					/*
					 * Computing the car translation considering its direction,
					 * its speed and the time elapsed from the previous render()
					 * invocation.
					 */

					Vec4 p1 = g.computePointFromPosition(originPos);
					Vec4 p2 = g.computePointFromPosition(destinationPos);
					double distance = p1.distanceTo3(p2);
					double travelTime = distance
							/ (car.getSpeed() * 1000 / 3600000);
					double longDiff = destinationPos.getLongitude().degrees
							- originPos.getLongitude().degrees;
					double latDiff = destinationPos.getLatitude().degrees
							- originPos.getLatitude().degrees;
					double longInc = longDiff * timeDelta / travelTime;
					double latInc = latDiff * timeDelta / travelTime;

					Position newPosition = Position.fromDegrees(
							currentPos.getLatitude().degrees + latInc,
							currentPos.getLongitude().degrees + longInc);
					car.moveTo(new Position(newPosition.getLatitude(),
							newPosition.getLongitude(),
							CustomizableVariables.RENDERALTITUDE));

					getStreetGraph()
							.setCarSpeed(car, originPos, destinationPos);
				}
			}
		}

		for (Car car : carToRemove) {

			removeCar(car);

		}
		
		
		
		
		Renderizer.updateParkingAreas();
	}

	/**
	 * check if the car with no destination area must be removed from the
	 * network
	 * 
	 * @param car
	 * @param carToRemove
	 * @param destination
	 */
	private void manageCar(Car car, List<Car> carToRemove, Position destination) {

		destination = chooseDestination(car.getPosition(), car.getOrigin());
		// Car has reached a dead end. It could go back
		if (destination == null) {
			if (dieOnDeadEnd) {
				carToRemove.add(car);
				getStreetGraph().removeCarFromSegment(car, car.getOrigin(),
						car.getDestination());
			} else {
				// If the car can go back (it is not a one-way dead end)
				if (getStreetGraph().segmentExists(car.getPosition(),
						car.getOrigin())) {
					destination = car.getOrigin();
					getStreetGraph().updateSegments(car, car.getOrigin(),
							car.getPosition(), destination);
					car.setOrigin(car.getPosition());
					car.setDestination(destination);
				}
				// else remove the car
				else {
					carToRemove.add(car);
					getStreetGraph().removeCarFromSegment(car, car.getOrigin(),
							car.getDestination());
				}
			}
		} else {
			getStreetGraph().updateSegments(car, car.getOrigin(),
					car.getPosition(), destination);
			car.setOrigin(car.getPosition());
			car.setDestination(destination);
		}

	}

	/**
	 * check if the car with a goal position must be removed from the network
	 * 
	 * @param car
	 * @param carToRemove
	 * @param destination
	 */
	private void manageDestCar(Car car, List<Car> carToRemove,
			Position destination) {

		// Car has reached the destination
		if (car.getGoalDestination().equals(car.getDestination())) {
			carToRemove.add(car);
			getStreetGraph().removeCarFromSegment(car, car.getOrigin(),
					car.getDestination());
			// These cars end up in on of the segment of the destination node,
			// thus they disappear from the counting for the original path
		}
		// Car has not reached the destination. Get the next node of the path
		else {
			// System.out.println("CAR HAS NOT REACHED DESTINATION");
			destination = chooseNextNode(car.getPosition(),
					car.getGoalDestination());
			if (destination != null) {
				getStreetGraph().updateSegments(car, car.getOrigin(),
						car.getPosition(), destination);
				car.setOrigin(car.getPosition());
				car.setDestination(destination);
			} else {
				carToRemove.add(car);
				getStreetGraph().removeCarFromSegment(car, car.getOrigin(),
						car.getDestination());
			}
		}

	}

	/**
	 * check if the car with a destination area must be removed from the network
	 * 
	 * @param car
	 * @param carToRemove
	 */
	private void manageAreaCar(CarWithStatus car, List<Car> carToRemove) {

		if (car.getStatus() == CarWithStatus.REACHING_AREA) {

			if (AreaManager.getAreaFromPosition(car.getOrigin()) == car
					.getArea()) {
				car.setStatus(CarWithStatus.REACHING_PARKING_LOT);
			} else {
				if (car.getPath().size() == 0) {
					List<Position> path = chooseNextNodes(car);
					car.setPath(path);
				}

				if (car.getPath() != null) {
					getStreetGraph().updateSegments(car, car.getOrigin(),
							car.getPosition(), car.getPath().get(0));
					car.setOrigin(car.getPosition());
					car.setDestination(car.getPath().get(0));

					car.getPath().remove(0);

				} else {
					carToRemove.add(car);
					getStreetGraph().removeCarFromSegment(car, car.getOrigin(),
							car.getDestination());
				}
			}
		}

		if (car.getStatus() == CarWithStatus.REACHING_PARKING_LOT) {
			
			Position destination = chooseDestinationInsideArea(
					car.getPosition(), car.getOrigin());
			// Car has reached a dead end. It could go back
			if (AppFrame.parkingAreaManager.checkingForFreeParkingArea(car
					.getDestination())) {
				
				carToRemove.add(car);
				getStreetGraph().removeCarFromSegment(car, car.getOrigin(),
						car.getDestination());
			} else

			if (destination == null) {
				if (dieOnDeadEnd) {
					carToRemove.add(car);
					getStreetGraph().removeCarFromSegment(car, car.getOrigin(),
							car.getDestination());
				} else {
					// If the car can go back (it is not a one-way dead end)
					if (getStreetGraph().segmentExists(car.getPosition(),
							car.getOrigin())) {
						destination = car.getOrigin();
						getStreetGraph().updateSegments(car, car.getOrigin(),
								car.getPosition(), destination);
						car.setOrigin(car.getPosition());
						car.setDestination(destination);
					}
					// else remove the car
					else {
						carToRemove.add(car);
						getStreetGraph().removeCarFromSegment(car,
								car.getOrigin(), car.getDestination());
					}
				}
			} else {
				getStreetGraph().updateSegments(car, car.getOrigin(),
						car.getPosition(), destination);
				car.setOrigin(car.getPosition());
				car.setDestination(destination);
			}
		}

		if (car.getStatus() == CarWithStatus.NO_DESTINATION) {
			Position destination = chooseRandomDestination(car.getPosition(),
					car.getOrigin());
			// Car has reached a dead end. It could go back

			if (destination == null) {
				if (dieOnDeadEnd) {
					carToRemove.add(car);
					getStreetGraph().removeCarFromSegment(car, car.getOrigin(),
							car.getDestination());
				} else {
					// If the car can go back (it is not a one-way dead end)
					if (getStreetGraph().segmentExists(car.getPosition(),
							car.getOrigin())) {
						destination = car.getOrigin();
						getStreetGraph().updateSegments(car, car.getOrigin(),
								car.getPosition(), destination);
						car.setOrigin(car.getPosition());
						car.setDestination(destination);
					}
					// else remove the car
					else {
						carToRemove.add(car);
						getStreetGraph().removeCarFromSegment(car,
								car.getOrigin(), car.getDestination());
					}
				}
			} else {
				getStreetGraph().updateSegments(car, car.getOrigin(),
						car.getPosition(), destination);
				car.setOrigin(car.getPosition());
				car.setDestination(destination);
			}
		}

	}

	public void printNumCars() {
		
			System.out.println("# cars: " + cars.size());
		
	}

	public int getNumCars(){
		return cars.size();
	}
	
	private GraphManager getStreetGraph() {
		return TrafficSim.streetGraph;
	}

}
