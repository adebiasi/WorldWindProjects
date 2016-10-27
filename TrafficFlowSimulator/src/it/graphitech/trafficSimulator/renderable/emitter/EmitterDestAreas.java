package it.graphitech.trafficSimulator.renderable.emitter;

import java.awt.Color;
import java.util.ArrayList;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.renderable.car.CarWithStatus;
import it.graphitech.trafficSimulator.renderableManager.AreaManager;

public class EmitterDestAreas extends Emitter {
	

	
	private ArrayList<Position> goals = new ArrayList<Position>(); ;
	private ArrayList<Integer> areas = new ArrayList<Integer>();
	
	private int numCars;
	private int currCar=0;
	/**
	 * The two points of the segment in which the destination is located
	 */
	private Position[] nearGoalDestination;
	private Renderable destinationObject;

	/**
	 * First constructor. The goal can be set in a second moment via setGoal()
	 * @param position
	 * @param destinations
	 */
	public EmitterDestAreas(Position position, Position[] destinations,int numCars) {
		super(position, destinations);

		this.numCars=numCars;
	}
	
	
	/**
	 * 
	 * @return the destination goal
	 */
	
	
	/**
	 * 
	 * @return the two points of the segment in which the destination is located
	 */
	public Position[] getNearGoalDestination() {
		return this.nearGoalDestination;
	}
	
	/**
	 * 
	 * @param destinations
	 */
	public void setNearGoalDestination(Position[] destinations) {
		this.nearGoalDestination = destinations;
	}
	
	/**
	 * 
	 * @return the World Wind renderable object of the destination
	 */
	public Renderable getDestinationObject() {
		return this.destinationObject;
	}
	
	/**
	 * 
	 * @param object the World Wind renderable object of the destination
	 */
	public void setDestinationObject(Renderable object) {
		this.destinationObject = object;
	}
	
	
	
	public ArrayList<Position> getGoals(){
		return this.goals;
	}
	
	public Position getOneGoal(int i){
		return this.goals.get(i);
	}
	
	public void setGoal(ArrayList<Position> goals) {
		this.goals = goals;
	}
	
	public void addGoal(Position goal) {
		this.goals.add(goal);
	}
	
	
	public ArrayList<Integer> getAreas(){
		return this.areas;
	}
	
	public int getOneArea(int i){
		return this.areas.get(i);
	}
	
	public void setAreas(ArrayList<Integer> areas) {
		this.areas = areas;
	}
	
	public void createArrayAreas() {
		this.areas = new ArrayList<Integer>(numCars);
	}
	
	public void createArrayGoals() {
		this.goals = new ArrayList<Position>(numCars);
	}
	
	public void addArea(Integer area) {
		this.areas.add(area);
	}
	
	
	public int getNumCars() {
		return this.numCars;
	}
	
	
	/**
	 * 
	 * @param goal
	 */
	

	/**
	 * Emit a new car considering its last invocation and the cars generation rate.
	 * Generated cars have also a destination goal
	 * @return
	 */

	public CarWithStatus emitCar() {
		CarWithStatus car = null;
		//long newTime = System.currentTimeMillis();
		//long timeDelta = newTime - this.lastTime;
		//if(this.active && timeDelta > this.carFrequency) {
		if(this.active) {
			if(currCar<numCars){
			double speed=CustomizableVariables.minSpeed+Math.random()*CustomizableVariables.deltaSpeed;
			
			car = new CarWithStatus(this.position, this.destinations[0], this.destinations[1],speed);
			
			if(currCar>=this.areas.size()){				
				return null;
			}
			int index=this.areas.get(currCar);
			
			car.setArea(index);
			car.setGoalDestination(this.goals.get(currCar));
//			this.lastTime += this.carFrequency;
//			this.carFrequency=2000+Math.random()*6000;
			Color col = (currCar!=-1)?AreaManager.colors[this.areas.get(currCar)]:Color.WHITE;
			car.setColor(new Material(col));
			
			
			
			currCar++;
			}
			
		}
		
		return car;
	}
	
	
}
