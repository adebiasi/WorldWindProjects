package it.graphitech.trafficSimulator.renderable.emitter;

import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.renderable.car.Car;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Ellipsoid;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;

public class Emitter extends Ellipsoid {
	
	protected long lastTime;
	protected Position position;
	/**
	 * The two points of the segment in which emitter is located.
	 * The position 0 is the only valid for one-way road.
	 */
	protected Position[] destinations; 
	//protected double carFrequency=CustomizableVariables.carFrequency;
	protected double carSpeed=CustomizableVariables.carSpeed;
	protected boolean active;
	
	private boolean oneway;	
	private boolean destinationsSwapped; //If destinations are swapped in the destinations array
	
	/**
	 * Emitter constructor
	 * @param position
	 * @param destinations
	 */
	public Emitter(Position position, Position[] destinations) {
		super(new Position(
				position.getLatitude(), position.getLongitude(),
				CustomizableVariables.RENDERALTITUDE), 14, 14, 14);
		this.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        this.setAttributes(getAttrs());
        this.setVisible(true);
        this.setValue(AVKey.DISPLAY_NAME, "Emitter");
        
		this.lastTime = System.currentTimeMillis();
		this.position = position;
		this.destinations = destinations;
		//this.carFrequency = 1000;
		//this.carSpeed = 400;
		this.oneway = false;
		this.active = false;
		this.destinationsSwapped = false;
	}
	
 
	
	/**
	 * 
	 * @return the car speed
	 */
	public double getCarSpeed() {
		return this.carSpeed;
	}
	
	/**
	 * 
	 * @param carSpeed
	 */
	public void setCarSpeed(double carSpeed) {
		this.carSpeed = carSpeed;
	}
	
	/**
	 * 
	 * @return true if the emitter is on a one-way road
	 */
	public boolean isOneWay() {
		return this.oneway;
	}
	/**
	 * 
	 * @param value 
	 */
	public void setOneway(boolean value) {
		this.oneway = value;
	}
	
	/**
	 * Disable the emitter
	 */
	public void disable() {
		this.active = false;
	}
	
	/**
	 * Enable the emitter
	 */
	public void enable() {
		this.active = true;
		this.lastTime = System.currentTimeMillis();
	}
	
	/**
	 * 
	 * @return the state of the emitter
	 */
	public boolean isActive() {
		return this.active;
	}
	
	/**
	 * Destination can be swapped only for emitters in two-way roads.
	 * @return the state (swapped or not) of the destination
	 */
	public boolean areDestinationsSwapped() {
		return this.destinationsSwapped;
	}
	
	/**
	 * Changes cars emission direction. It works only for emitters in
	 * two-way roads.
	 */
	public void invertDirections() {
		if(!isOneWay()) {
			Position temp = this.destinations[0];
			this.destinations[0] = this.destinations[1];
			this.destinations[1] = temp;
			destinationsSwapped = !destinationsSwapped;
		}
	}
	
	/**
	 * 
	 * @return the points of the segment in which the emitter is located
	 */
	public Position[] getDestinations() {
		return this.destinations;
	}
	
	/**
	 * Emit a new car considering its last invocation and the cars generation rate
	 * @return
	 */
	public Car emitCar() {
		
				
		Car car = null;
		long newTime = System.currentTimeMillis();
		long timeDelta = newTime - this.lastTime;
		//if(this.active && timeDelta > this.carFrequency) {
		if(this.active) {
			
			
			car = new Car(this.position, this.destinations[0], this.destinations[1], this.carSpeed);
		//	this.lastTime += this.carFrequency;
		}
		return car;
	}
	
	/**
	 * 
	 * @return the shape attributes of the emitter
	 */
	private ShapeAttributes getAttrs() {
		ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorMaterial(Material.ORANGE);
        attrs.setInteriorOpacity(1.0);
        attrs.setEnableLighting(true);
        attrs.setOutlineMaterial(Material.RED);
        attrs.setOutlineWidth(2d);
        attrs.setDrawInterior(true);
        attrs.setDrawOutline(false);
        return attrs;
	}
}
