package it.graphitech.trafficSimulator;

import gov.nasa.worldwind.geom.Position;
import it.graphitech.trafficSimulator.core.CarManager;
import it.graphitech.trafficSimulator.core.EmitterManager;
import it.graphitech.trafficSimulator.core.ParkingAreaManager;
import it.graphitech.trafficSimulator.listener.MouseAdp;
import it.graphitech.trafficSimulator.listener.TrafficSimController;
import it.graphitech.trafficSimulator.renderableManager.AreaManager;
import it.graphitech.trafficSimulator.renderableManager.GraphManager;
import it.graphitech.trafficSimulator.renderableManager.Renderizer;
import it.graphitech.trafficSimulator.renderableManager.StreetsAnalysisManager;
//import it.graphitech.trafficSimulator.swing.EmitterSettings;
import it.graphitech.trafficSimulator.swing.TrafficSimPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

//the main class of the project
public class TrafficSim extends ApplicationTemplate {

 
	public static GraphManager streetGraph;

	public static class AppFrame extends ApplicationTemplate.AppFrame {

		private static final long serialVersionUID = 4070261473999416852L;

		public static EmitterManager emitterManager;
		public static ParkingAreaManager parkingAreaManager;
		public static CarManager carManager;

		private TrafficSimController controller;
		public TrafficSimPanel trafficSimPanel;

		// public EmitterSettings emitterSettings;

		public AppFrame() {
			super(true, true, false);

			// Set useful object references for global access
			GlobalInstances.setGlobe(getWwd().getModel().getGlobe());
			GlobalInstances.setWwd(getWwd());
			GlobalInstances.setRenderizer(new Renderizer());

			// Set Eye position on Trento
			getWwd().getView().setEyePosition(
					Position.fromDegrees(46.0660, 11.1372, 6000));

			// Create the views
			this.trafficSimPanel = new TrafficSimPanel(this);
			// this.emitterSettings = new EmitterSettings();

			// Add the Traffic Simulation panel into WorldWind UI
			javax.swing.Box box = javax.swing.Box.createVerticalBox();
			box.add(this.trafficSimPanel);
			this.getLayerPanel().add(box, BorderLayout.SOUTH);

			// Create the controller
			this.controller = new TrafficSimController(this);

			// Register this class as a listener for WorldWind
			getWwd().addSelectListener(this.controller);

			MouseAdp mAdp = new MouseAdp(this);
			getWwd().getInputHandler().addMouseListener(mAdp);

			getWwd().getInputHandler().addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent mouseEvent) {
					if (MouseAdp.trafficSimPanel.selectAreaBtn.isSelected()
							|| MouseAdp.trafficSimPanel.selectDangerAreaBtn
									.isSelected()) {
						if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
							mouseEvent.consume();
						}
					}
				}

			}

			);

			// Register this class as a listener for the views
			this.trafficSimPanel.addActionListener(this.controller);
		 
			this.pack();

			// Call the command for bootstrapping the application
			this.controller.actionPerformed(new ActionEvent(this, 0,
					TrafficSimController.ACTION_COMMAND_LOAD_STREETS));

			// Construct the CarManager
			 	carManager = new CarManager();

			// Construct the EmitterManager
			emitterManager = new EmitterManager(carManager);

			parkingAreaManager = new ParkingAreaManager();

			// Start the update thread
			new WorkerThread(carManager, emitterManager, this).start();

			File file = new File(CustomizableVariables.fileBuilding);
			Thread t = new it.graphitech.trafficSimulator.importShapes.buildings.BuildingWorkerThread(
					file, getWwd(), this);
			t.start();

			File file2 = new File(CustomizableVariables.fileRoads);
			Thread t2 = new it.graphitech.trafficSimulator.importShapes.Roads.RoadsWorkerThread(
					file2, getWwd(), this);
			t2.start();

			AreaManager.initAreasLayer(getWwd(), this);
			AreaManager.initArrowsLayer(getWwd(), this);

			StreetsAnalysisManager.updateStreetAnalysisLayer(streetGraph
					.getSegmentMap());

			this.getLayerPanel().update(this.getWwd());

		}

		public TrafficSimPanel getTrafficSimPanel() {
			return this.trafficSimPanel;
		}

	 
		public TrafficSimController getController() {
			return controller;
		}

		public void setController(TrafficSimController controller) {
			this.controller = controller;
		}

	}

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CustomizableVariables.openFile("CustomVariables.txt");
		ApplicationTemplate.start("Traffic Simulator", AppFrame.class);
	}
}
