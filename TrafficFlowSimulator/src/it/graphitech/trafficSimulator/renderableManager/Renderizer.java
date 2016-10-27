package it.graphitech.trafficSimulator.renderableManager;

import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.WWMath;

import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.GlobalInstances;
import it.graphitech.trafficSimulator.renderable.car.Car;
import it.graphitech.trafficSimulator.renderable.emitter.Emitter;
import it.graphitech.trafficSimulator.renderable.emitter.ParkingArea;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Class encapsulating the rendering of Car and Emitter objects
 * 
 */
public class Renderizer {

	private final String CAR = "CAR";
	private final String EMITTER = "EMITTER";
	private final String PARKING_AREA = "PARKING_AREA";

	private Map<Object, Renderable> renderMap; // Renderable objects map
	private Map<String, RenderableLayer> layers; // Layers map

	public Renderizer() {
		this.renderMap = new HashMap<Object, Renderable>();
		this.layers = new HashMap<String, RenderableLayer>();

		RenderableLayer carLayer = new RenderableLayer();
		RenderableLayer emitterLayer = new RenderableLayer();
		RenderableLayer parkingAreaLayer = new RenderableLayer();

		carLayer.setName("Cars");
		emitterLayer.setName("Emitters");
		emitterLayer.setEnabled(false);
		parkingAreaLayer.setName("parkingArea");
		parkingAreaLayer.setEnabled(false);

		this.layers.put(CAR, carLayer);
		this.layers.put(EMITTER, emitterLayer);
		this.layers.put(PARKING_AREA, parkingAreaLayer);

		GlobalInstances.getWwd().getModel().getLayers().add(carLayer);
		GlobalInstances.getWwd().getModel().getLayers().add(emitterLayer);
		GlobalInstances.getWwd().getModel().getLayers().add(parkingAreaLayer);
	}

	/**
	 * Render a car
	 * 
	 * @param car
	 *            car to render
	 */
	public void renderCar(Car car) {
		if (!this.renderMap.containsKey(car)) {
			this.renderMap.put(car, car.getRenderable());
			this.layers.get(CAR).addRenderable(car.getRenderable());
		}
	}

	/**
	 * Remove a car from rendering
	 * 
	 * @param car
	 */
	public void unrenderCar(Car car) {
		if (this.renderMap.containsKey(car)) {
			this.renderMap.remove(car);
			this.layers.get(CAR).removeRenderable(car.getRenderable());
		}
	}

	/**
	 * 
	 * @param emitter
	 *            emitter to render
	 */
	public void renderEmitter(Emitter emitter) {
		if (!this.renderMap.containsKey(emitter)) {
			this.renderMap.put(emitter, emitter);
			this.layers.get(EMITTER).addRenderable(emitter);

	
		}
	}

	public void renderParkingArea(ParkingArea parkingArea) {
		if (!this.renderMap.containsKey(parkingArea)) {
			this.renderMap.put(parkingArea, parkingArea);
			this.layers.get(PARKING_AREA).addRenderable(parkingArea);

			
			int maxValue= parkingArea.getInit_size();
			int currValue=parkingArea.getSize();
			
			Material material = new Material(getColor(currValue, 0, maxValue));
			parkingArea.getAttributes().setInteriorMaterial(material);
			//parkingArea.getAttributes().setInteriorMaterial(Material.BLUE);

		}
	}

	
	public static void updateParkingAreas() {
		RenderableLayer layer =(RenderableLayer)GlobalInstances.getWwd().getModel().getLayers().getLayerByName("parkingArea");
		if(layer!=null){
		for(Renderable renderable : layer.getRenderables()){
			//for(Renderable renderable : this.layers.get(PARKING_AREA).getRenderables()){

				ParkingArea parkingArea = (ParkingArea)renderable;
			int maxValue= parkingArea.getInit_size();
			int currValue=parkingArea.getSize();
			
			Material material = new Material(getColor(currValue, 0, maxValue));
			parkingArea.getAttributes().setInteriorMaterial(material);
			//parkingArea.getAttributes().setInteriorMaterial(Material.BLUE);
			}
		
		}
		}
	
	
	/**
	 * Remove an emitter from rendering
	 * 
	 * @param emitter
	 */
	public void unrenderEmitter(Emitter emitter) {
		if (this.renderMap.containsKey(emitter)) {
			this.renderMap.remove(emitter);
			this.layers.get(EMITTER).removeRenderable(emitter);

			// if (emitter instanceof EmitterDest) {
			// this.layers.get(EMITTER).removeRenderable(
			// ((EmitterDest) emitter).getDestinationObject());
			// }
		}
	}

	public void unrenderParkingArea(ParkingArea parkingArea) {
		if (this.renderMap.containsKey(parkingArea)) {
			this.renderMap.remove(parkingArea);
			this.layers.get(PARKING_AREA).removeRenderable(parkingArea);

		}
	}

	/**
	 * Clean the layers
	 */
	public void reset() {
		this.layers.get(CAR).removeAllRenderables();
		this.layers.get(EMITTER).removeAllRenderables();
	}
	
	private static Color getColor(int value, int minValue, int maxValue) {

		if (value > CustomizableVariables.max_cars_per_path) {
			value = CustomizableVariables.max_cars_per_path;
		}
		return createColorGradientAttributes(value, minValue,
				maxValue,
				CustomizableVariables.maxHue,CustomizableVariables.minHue);

	}

	private static Color createColorGradientAttributes(final double value,
			double minValue, double maxValue, double minHue, double maxHue) {
		double hueFactor = WWMath.computeInterpolationFactor(value, minValue,
				maxValue);
		Color color = Color.getHSBColor(
				(float) WWMath.mixSmooth(hueFactor, minHue, maxHue), 1f, 1f);
		double opacity = WWMath.computeInterpolationFactor(value, minValue,
				minValue + (maxValue - minValue) * 0.1);
		Color rgbaColor = new Color(color.getRed(), color.getGreen(),
				color.getBlue(), (int) (255 * opacity));
		return rgbaColor;
	}

}
