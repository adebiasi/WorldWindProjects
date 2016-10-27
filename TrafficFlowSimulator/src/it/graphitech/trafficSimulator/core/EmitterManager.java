package it.graphitech.trafficSimulator.core;

import gov.nasa.worldwind.avlist.AVKey;

import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.GlobalInstances;
import it.graphitech.trafficSimulator.renderable.car.Car;
import it.graphitech.trafficSimulator.renderable.emitter.Emitter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EmitterManager {

	private List<Emitter> emitters;
	private ArrayList<Emitter> lastAddedEmitters = new ArrayList<Emitter>();
	private CarManager carManager;
	private static int emitterNumber = 0; // Incremental ID of emitters
	private long lastTime = 0;	
	int numGeneratedCars=0;
	boolean wait=false;
	/**
	 * Emitter Manager constructor
	 * 
	 * @param carManager
	 * @param streetGraph
	 */
	public EmitterManager(CarManager carManager) {
		emitters = new ArrayList<Emitter>();
		this.carManager = carManager;
		}

	/**
	 * Asks emitters to generate cars. If an emitter returns a car, it is added
	 * in the car manager
	 */
	public void manageEmitters() {
		
	
		
		
		for (Emitter emitter : emitters) {
			
			if(emitter.isActive()){
			
			long newTime = System.currentTimeMillis();
			long timeDelta = newTime - lastTime;
			if(carManager.getNumCars()<CustomizableVariables.numMaxCars){
			
				
			if((numGeneratedCars>10)&&(timeDelta < CustomizableVariables.carFrequency)){
			//	System.out.println("wait!");
				wait=true;	
				numGeneratedCars=0;
			}
		
				
				if(!wait){
			Car car = emitter.emitCar();
			if (car != null) {
					carManager.addCar(car);
				lastTime=new Long(newTime);
				numGeneratedCars++;
			}
//			
			
			}
				else if (timeDelta > CustomizableVariables.carFrequency){
					//System.out.println("don't wait");
					wait=false;
				}
			
		}
		
			}
		
		}
	}

	/**
	 * 
	 * @return an iterator on the emitters list
	 */
	public Iterator<Emitter> iterator() {
		return emitters.iterator();
	}

	/**
	 * Add an emitter in the emitter manager
	 * 
	 * @param emitter
	 *            the emitter to add
	 */
	public void addEmitters(Emitter emitter) {
		GlobalInstances.getRenderizer().renderEmitter(emitter);
		emitter.setValue(AVKey.DISPLAY_NAME, "Emitter #" + emitterNumber++);
		emitters.add(emitter);
		//lastAddedEmitter = emitter;
		lastAddedEmitters.add(emitter);
	}



	public ArrayList<Emitter> getLastAddedEmitters() {
		return this.lastAddedEmitters;
	}

	/**
	 * 
	 * @return the number of emitters
	 */
	public int emittersNumber() {
		return emitters.size();
	}

	/**
	 * Resets the emitters list
	 */
	public void reset() {
		emitters = new ArrayList<Emitter>();
		emitterNumber = 0;
	}

	public void resetLastEmmitters() {
		lastAddedEmitters = new ArrayList<Emitter>();
	}
	
	public void shuffle(){
		Collections.shuffle(emitters);
	}
}
