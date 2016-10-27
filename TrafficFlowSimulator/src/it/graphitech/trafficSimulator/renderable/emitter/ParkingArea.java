package it.graphitech.trafficSimulator.renderable.emitter;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Ellipsoid;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import it.graphitech.trafficSimulator.CustomizableVariables;

public class ParkingArea extends Ellipsoid {
	
	protected int init_size=CustomizableVariables.numCarsPerParkingLot;
	protected int size=init_size;
	
	protected long lastTime;
	protected Position position;
	/**
	 * The two points of the segment in which emitter is located.
	 * The position 0 is the only valid for one-way road.
	 */
	protected double carFrequency;
	protected double carSpeed;
	protected boolean active;
	
	private boolean oneway;	
	private boolean destinationsSwapped; //If destinations are swapped in the destinations array
	
	/**
	 * Emitter constructor
	 * @param position
	 * @param destinations
	 */
	public ParkingArea(Position position) {
		super(new Position(
				position.getLatitude(), position.getLongitude(),
				CustomizableVariables.RENDERALTITUDE), 14, 14, 14);
		this.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        this.setAttributes(getAttrs());
        this.setVisible(true);
        this.setValue(AVKey.DISPLAY_NAME, "Emitter");
        
		this.lastTime = System.currentTimeMillis();
		this.position = position;
		this.carFrequency = 1000;
		this.carSpeed = 400;
		this.oneway = false;
		this.active = false;
		this.destinationsSwapped = false;
	}
	
	/**
	 * 
	 * @return the car generation rate
	 */
	public double getCarFrequency() {
		return this.carFrequency;
	}
	
	/**
	 * 
	 * @param carFrequency
	 */
	public void setCarFrequency(double carFrequency) {
		this.carFrequency = carFrequency;
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

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	
	public boolean isAvailable(){
		return size>0;
	}
	
	public void decreaseSize(){
		size--;
	}

	
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getInit_size() {
		return init_size;
	}

	public void setInit_size(int init_size) {
		this.init_size = init_size;
	}
	
	
}
