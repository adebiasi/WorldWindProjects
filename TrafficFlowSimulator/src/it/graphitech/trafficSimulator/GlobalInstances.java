package it.graphitech.trafficSimulator;

import it.graphitech.trafficSimulator.renderableManager.Renderizer;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwind.globes.Globe;

/**
 * 
 * Class storing references of objects useful in different parts of the
 * application
 * 
 */
public class GlobalInstances {

	private static Globe globe = null; // World Wind Globe
	private static WorldWindowGLCanvas wwd = null; // World Wind Canvas
	private static ApplicationTemplate appTemp = null; // Application reference
	private static Renderizer renderizer = null; // Renderizer

	public static void setGlobe(Globe g) {
		globe = g;
	}

	public static Globe getGlobe() {
		return globe;
	}

	public static void setWwd(WorldWindowGLCanvas c) {
		wwd = c;
	}

	public static WorldWindowGLCanvas getWwd() {
		return wwd;
	}

	public static void setApplicationTemplate(ApplicationTemplate a) {
		appTemp = a;
	}

	public static ApplicationTemplate getApplicationTemplate() {
		return appTemp;
	}

	public static void setRenderizer(Renderizer r) {
		renderizer = r;
	}

	public static Renderizer getRenderizer() {
		return renderizer;
	}
}
