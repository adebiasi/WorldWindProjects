package it.graphitech.trafficSimulator;

//import it.graphitech.integration.TrafficSim.AppFrame;

import it.graphitech.trafficSimulator.TrafficSim.AppFrame;
import it.graphitech.trafficSimulator.core.CarManager;
import it.graphitech.trafficSimulator.core.EmitterManager;

import javax.swing.SwingUtilities;

//this thread manage the rendering of the cars
public class WorkerThread extends Thread {

	protected Object carManager;
	protected Object emitterManager;
	protected AppFrame appFrame;

	private CarManager carManagerRef;
	private EmitterManager emitterManagerRef;

	public WorkerThread(Object carManager, Object emitterManager,
			AppFrame appFrame) {
		this.carManager = carManager;
		this.appFrame = appFrame;
		this.carManagerRef = (CarManager) carManager;
		this.emitterManagerRef = (EmitterManager) emitterManager;
	}

	public void run() {
		while (true) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					emitterManagerRef.manageEmitters();
					carManagerRef.render();
					appFrame.getWwd().redraw();
				}
			});
			try {
				Thread.sleep(CustomizableVariables.updateRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
