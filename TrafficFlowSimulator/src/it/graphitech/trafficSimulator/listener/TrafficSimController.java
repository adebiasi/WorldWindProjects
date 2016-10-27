package it.graphitech.trafficSimulator.listener;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.ogc.kml.KMLPlacemark;
import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.GlobalInstances;
import it.graphitech.trafficSimulator.TrafficSim;
import it.graphitech.trafficSimulator.TrafficSim.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;

import it.graphitech.trafficSimulator.importShapes.KMLPlacemarkImporter;
import it.graphitech.trafficSimulator.renderable.PathExtArea;
import it.graphitech.trafficSimulator.renderable.emitter.Emitter;
import it.graphitech.trafficSimulator.renderableManager.GraphManager;
//import it.graphitech.trafficSimulator.swing.EmitterSettings;
import it.graphitech.trafficSimulator.swing.TrafficSimPanel;

import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TrafficSimController implements ActionListener, SelectListener,
		ItemListener, ChangeListener {

	public static final String ACTION_COMMAND_LOAD_STREETS = "ActionCommandLoadStreets";
	public static final String ACTION_COMMAND_ACTIVATE_EMITTERS = "ActionCommandActivateEmitters";
	public static final String ACTION_COMMAND_DEACTIVATE_EMITTERS = "ActionCommandDeactivateEmitters";
	public static final String ACTION_COMMAND_CLEAN_ALL = "ActionCommandCleanAll";

	protected AppFrame frame;
	protected TrafficSimPanel trafficSimPanel;

	public TrafficSimController(AppFrame frame) {
		this.frame = frame;
		this.trafficSimPanel = frame.getTrafficSimPanel();
		// this.emitterSettings = frame.getEmitterSettings();
	}

	public WorldWindow getWwd() {
		return this.frame.getWwd();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (ACTION_COMMAND_LOAD_STREETS.equalsIgnoreCase(e.getActionCommand())) {
			this.doLoadStreets();
		} else if (ACTION_COMMAND_ACTIVATE_EMITTERS.equalsIgnoreCase(e
				.getActionCommand())) {
			trafficSimPanel.isStartedTheSimulation=true;
			trafficSimPanel.selectDangerAreaBtn.setEnabled(false);
			trafficSimPanel.importDangerAreaBtn.setEnabled(false);
			
			//shuffle
			frame.emitterManager.shuffle();
			
			doActivateOrDeactivateEmitters(true);
			
		} else if (ACTION_COMMAND_DEACTIVATE_EMITTERS.equalsIgnoreCase(e
				.getActionCommand())) {
			doActivateOrDeactivateEmitters(false);
		}

		else {
			
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}

	public void doLoadStreets() {
		// Import the KML data through the KMLImporter
		KMLPlacemarkImporter kmlImporter = new KMLPlacemarkImporter();
		kmlImporter.setKMLFilePath(CustomizableVariables.KML_TRENTO_DATA);
		kmlImporter.parseKMLFile();
		List<KMLPlacemark> placemarks = kmlImporter.getPlacemarks();

		TrafficSim.streetGraph = new GraphManager(placemarks);
	}

	public void doActivateOrDeactivateEmitters(boolean activate) {
		Iterator<Emitter> i = AppFrame.emitterManager.iterator();
		// Emitter currentEmitter = this.emitterSettings.getCurrentEmitter();
		while (i.hasNext()) {
			Emitter e = i.next();
			if (activate) {
				e.enable();
			} else {				
				e.disable();

			}
		}
	}

	public void resetAll() {
		AppFrame.carManager.reset();
		AppFrame.emitterManager.reset();
		TrafficSim.streetGraph.reset();
		GlobalInstances.getRenderizer().reset();
	}

	@Override
	public void selected(SelectEvent event) {

		if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)) {

			if (event.hasObjects()) {
				Object topObject = event.getTopObject();
				// A path is clicked
				if (topObject instanceof PathExtArea) {
					PathExtArea path = (PathExtArea) topObject;
					boolean oneway = path.isOneway();



				}

			}
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

	}

}
