package it.graphitech.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import it.graphitech.Operations;
import it.graphitech.Variables;
import it.graphitech.input.FlowAngleComparator;
import it.graphitech.input.FlowComparator;
import it.graphitech.input.FlowDecrAngleComparator;
import it.graphitech.input.FlowSource;
import it.graphitech.swing.ButtonPanel;
import it.graphitech.swing.StatusBar;
import it.graphitech.worldwind.ScreenshotsThread;
import it.graphitech.worldwind.WorkerThread;

public class MainModule {

	public static boolean isActive = false;
	static WorkerThread worker;
	static ScreenshotsThread screenWorker;

	public static void updateRenderingDuringExecution() {
		worker.render();
	}

	public static void takeScreenshotsDuringExecution() {
		worker.takeScreenShots();
	}

	public static void updateRendering() {
		try {
Variables.updateFlowWidth();
			Render.updateRendering();
			Variables.renderAtIteration = Variables.num_iteration;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void start() {
		// TODO Auto-generated method stub
		// NodeManager.printLeafNodes();
		Variables.time_start = System.currentTimeMillis();
		Variables.num_iteration = 0;

		if (!MainModule.isActive) {
			// button_auto_move.setText("Stop");
			if (Variables.printResults) {
				PrintResults.createFile(Variables.fileName);
				PrintResults.writeVariables();
				PrintResults.screenShootCapture();
			}

			setLabelOfButtonStart("Stop");
			worker = new WorkerThread();
			worker.start();

			screenWorker = new ScreenshotsThread();
			screenWorker.start();
			// timer.start();
			MainModule.isActive = !MainModule.isActive;
		} else {
			// button_auto_move.setText("Start");
			setLabelOfButtonStart("Start");
			// timer.stop();
			worker.stop();
			screenWorker.stop();
			if (Variables.printResults) {
				PrintResults.closeFile();
			}
			MainModule.isActive = !MainModule.isActive;
		}

		// Render.update();
	}

	public static void init(String path) {
		Operations.clearVariables();
		// This is where a real application would open the file.
		ArrayList<FlowSource> flows = Operations.loadInputData(path);

		Collections.sort(flows, new FlowAngleComparator());
		assignIndexAngles(flows);
		System.out.println("prima fase");
		printIndexAngles(flows);
		Collections.sort(flows, new FlowDecrAngleComparator());
		assignMaxIndexAngles(flows);
		System.out.println("seconda fase");
		printIndexAngles(flows);
		Collections.sort(flows, new FlowComparator());

		Operations.inputFlows = flows;

		Operations.deltaLon = (Operations.maxLon - Operations.minLon);
		Operations.deltaLat = (Operations.maxLat - Operations.minLat);

		// double deltaLon=Operations.deltaLon/Operations.deltaLon;
		// double deltaLat=Operations.deltaLat/Operations.deltaLon;

		MiddleNodeGeneration alg = new MiddleNodeGeneration();
		alg.run(Operations.inputFlows);

		alg.assignNeighbours();

	}

	private static void assignIndexAngles(ArrayList<FlowSource> flows) {

		String lastFlowOriginID = flows.get(0).getOrigin();
		int index = 0;
		for (int i = 0; i < flows.size(); i++) {

			if (flows.get(i).getOrigin().compareTo(lastFlowOriginID) == 0) {
				flows.get(i).setAngleIndex(index);
				flows.get(i).setMaxAngleIndex(flows.size() - 1);
			} else {
				index = 0;
				lastFlowOriginID = flows.get(i).getOrigin();
				flows.get(i).setAngleIndex(index);
			}

			index++;
		}

	}

	private static void assignMaxIndexAngles(ArrayList<FlowSource> flows) {

		String lastFlowOriginID = flows.get(0).getOrigin();
		int maxIndex = flows.get(0).getAngleIndex();
		for (int i = 0; i < flows.size(); i++) {

			if (flows.get(i).getOrigin().compareTo(lastFlowOriginID) == 0) {
				flows.get(i).setMaxAngleIndex(maxIndex);
			} else {
				lastFlowOriginID = flows.get(i).getOrigin();
				maxIndex = flows.get(i).getAngleIndex();
				flows.get(i).setMaxAngleIndex(maxIndex);
			}

		}

	}

	private static void printIndexAngles(ArrayList<FlowSource> flows) {

		for (int i = 0; i < flows.size(); i++) {
			System.out.println("# flow: " + flows.get(i).getOrigin()
					+ " angle: " + flows.get(i).getAngleFromOrigin()
					+ " index: " + flows.get(i).getAngleIndex()
					+ " max angle: " + flows.get(i).getMaxAngleIndex());
		}

	}

	public static void reset() {
		try {
			// Variables.curr_energy=Variables.initial_energy;
			/*
			 * Render.initEdgesLayer(); Render.initMiddleNodeLayer();
			 */
			// /////////////////////////
			// Render.resetLayers();
			// //////////////////////////
			// MiddleNodeGeneration.nodes=new HashMap<>();
			Render.updateRendering();
			MiddleNodeGeneration.initNodes();
			MiddleNodeGeneration alg = new MiddleNodeGeneration();
			alg.run(Operations.inputFlows);
			alg.assignNeighbours();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static synchronized void iterate() {

		// System.out.println("iterate!");

		// int n=UpdateTreeStructure.countNumberThatShouldBeRemoved();
		// System.out.println("PRIMA ci sono "+n+" nodi che dovrebbero essere rimossi");

		resetForces();
		UpdateTreeStructure.checkAggregation();

		UpdateTreeStructure.removeNodesAndUpdateNeighbours();
		// MiddleNodeGeneration.nodes
		MiddleNodeGeneration.updateMaxFlowMagnitude();
		// UpdateTreeStructure.UpdateRemovedNodes();

		if (UpdateTreeStructure.NoMoreNodesToRemove()) {
			//System.out.println("non ci sono nodi da rimuovere quindi applico algoritmo delle forze");
			ForceDirectedTechnique.executeAlgorithm();
		}else{
			//System.out.println("ci sono i seguenti nodi da eliminare con i rispettivi predecessori:");
			UpdateTreeStructure.printNodesToRemove();
		}

		// n=UpdateTreeStructure.countNumberThatShouldBeRemoved();
		// System.out.println("DOPO ci sono "+n+" nodi che dovrebbero essere rimossi");

		/*
		 * if(Variables.num_iteration==Variables.num_max_iteration){
		 * System.out.println("STABLE"); Variables.enable_stability=true; }
		 */
		Variables.num_iteration++;

		if (Variables.printResults) {
			PrintResults.writeResults();
		}
		
		StatusBar.updateStatusBar();
		
		if (Variables.enable_stability) {
			// timer.stop();
			isActive = !isActive;
			// Render.enableOnlyFlowsLayer();
			worker.stop();
			Render.updateRendering();
			Variables.enable_stability = false;
			// button_auto_move.setText("Start");
			setLabelOfButtonStart("Start");

		}
		// Render.update();
		Variables.elapsedTime = System.currentTimeMillis()
				- Variables.time_start;

		
		checkStability();
	}

	
	private  static void resetForces(){
		Variables.total_energy = 0;
		Variables.total_electr_force = 0;
		Variables.total_repulsive_force = 0;
		Variables.total_stress_force = 0;
	}
	
	private static void checkStability(){
		
	//	System.out.println("total_energy: "+Variables.total_energy);
	//	System.out.println("low_energy: "+Variables.low_energy);
		
		if(Variables.min_nodes>MiddleNodeGeneration.getNumNodes()){
			Variables.min_nodes=MiddleNodeGeneration.getNumNodes();
			Variables.min_iteraction_step=Variables.num_iteration;
		}
		
		if(Variables.total_energy<Variables.low_energy){
			Variables.min_iteraction_step=Variables.num_iteration;
			Variables.low_energy=Variables.total_energy;
			//System.out.println("trovato minimo! a "+Variables.min_iteraction_step);
			//System.out.println("prev: "+Variables.prev_total_energy +" energy: "+Variables.total_energy);
		}else{
			if(Variables.num_iteration>Variables.min_iteraction_step+200){
				
				
				if(!Variables.isStable){
				System.out.println("IS STABLE!!!! con : "+MiddleNodeGeneration.getNumNodes()+" nodi dopo "+Variables.elapsedTime+" sec.");
				
				Variables.isStable=true;
				Variables.low_energy=Double.MAX_VALUE;
				}else{
					System.out.println("fermiamo tutto");
					//updateRenderingDuringExecution();
					updateRendering();
					worker.stop();
					screenWorker.stop();
					//if (Variables.printResults) {
						//PrintResults.closeFile();
					//}
					MainModule.isActive = false;
				}
				
			}
		}
	}
	
	private static void setLabelOfButtonStart(String label) {
		ButtonPanel.button_auto_move.setText(label);
	}
}
