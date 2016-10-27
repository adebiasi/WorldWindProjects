package it.graphitech.trafficSimulator.renderable.car;

import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.GlobalInstances;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Ellipsoid;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;

public class Car {
	
	private Ellipsoid ellipsoid;
	
	private Position origin; // Provenance position
	private Position destination; // Destination position
	private Position goalPosition; // Ultimate destination (destination emitters)
	
	private double speed;
	private double originalSpeed;
	
	private double lastDiff = 9999999; // Delta between current position and destination
	
	private Globe g;
	
	/**
	 * Car constructor
	 * @param currPos the current position
	 * @param origin car origin
	 * @param destination car destination
	 * @param speed car speed
	 */
	public Car(Position currPos, Position origin, Position destination, double speed) {		
		g = GlobalInstances.getGlobe();		
		this.origin = origin;
		this.destination = destination;
		this.speed = speed;
		this.originalSpeed = speed;    
		this.ellipsoid = build3D(currPos);
	}

	/**
	 * 
	 * @return the origin position
	 */
	public Position getOrigin() {
		return this.origin;
	}
	
	/**
	 * 
	 * @param origin
	 */
	public void setOrigin(Position origin) {
		this.origin = origin;
	}
	
	/**
	 * 
	 * @return the destination position
	 */
	public Position getDestination() {
		return this.destination;
	}
	
	/**
	 * 
	 * @param destination
	 */
	public void setDestination(Position destination) {
		this.destination = destination;
	}
	
	/**
	 * 
	 * @return the car speed
	 */
	public double getSpeed() {
		return this.speed;
	}
	
	/**
	 * 
	 * @param speed
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	/**
	 * 
	 * @return the speed passed during construction
	 */
	public double getOriginalSpeed() {
		return this.originalSpeed;
	}
	
	/**
	 * 
	 * @return car current position
	 */
	public Position getPosition() {
		return this.ellipsoid.getCenterPosition();
	}
	
	/**
	 * 
	 * @return the World Wind renderable object
	 */
	public Renderable getRenderable() {
		return this.ellipsoid;
	}
	
	/**
	 * Move the car towards a position. If the car reaches its destination
	 * it is moved into the destination
	 * @param p the position
	 */
	public void moveTo(Position p) {		
		this.ellipsoid.moveTo(p);
		Vec4 p1 = g.computePointFromPosition(this.ellipsoid.getCenterPosition());
		Vec4 p2 = g.computePointFromPosition(this.destination);
		double diff = p1.distanceTo3(p2);
		if(lastDiff < diff) {
			this.ellipsoid.moveTo(this.destination);
			lastDiff = 9999999;
		} else {
			lastDiff = diff;
		}
	}
	
	/**
	 * 
	 * @return the destination goal. Property set by Destination emitters
	 */
	public Position getGoalDestination() {
		return this.goalPosition;
	}
	
	/**
	 * Set the destination goal. Property set by Destination emitters
	 * @param goal
	 */
	public void setGoalDestination(Position goal) {
		this.goalPosition = goal;
		this.setColor(Material.MAGENTA);
	}
	
	/**
	 * 
	 * @return true if car has a destination goal
	 */
	public boolean hasGoalDestination() {
		return this.goalPosition != null;
	}
	
	/**
	 * Set color of the car
	 * @param material a Material World Wind object
	 */
	public void setColor(Material material) {
		this.ellipsoid.getAttributes().setInteriorMaterial(material);
	}
	
	/**
	 * Create a new ellipsoid
	 * @param currPos the position of the ellipsoid
	 * @return
	 */
	private Ellipsoid build3D(Position currPos) {
		Ellipsoid obj = new Ellipsoid(currPos, CustomizableVariables.CAR_RADIUS, CustomizableVariables.CAR_RADIUS, CustomizableVariables.CAR_RADIUS);
		obj.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
		obj.setAttributes(getAttributes(Material.WHITE));
		obj.setDetailHint(-0.5);
		
		obj.setVisible(true);
		obj.setValue(AVKey.DISPLAY_NAME, "Car");
		return obj;
	}
	
	/**
	 * Get the shape attributes for a renderable object
	 * @param color color of the object
	 * @return the shape attributes
	 */
	private ShapeAttributes getAttributes(Material color) {
		ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorOpacity(1.0);
        attrs.setEnableLighting(true);
        attrs.setInteriorMaterial(color);
        attrs.setOutlineMaterial(Material.RED);
        attrs.setOutlineWidth(0.1d);
        attrs.setDrawInterior(true);
        attrs.setDrawOutline(false);
        return attrs;
	}
}
