package it.graphitech.trafficSimulator.listener;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;

import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.TrafficSim;
import it.graphitech.trafficSimulator.ServiceInterface.DataLayer;
import it.graphitech.trafficSimulator.TrafficSim.AppFrame;
import it.graphitech.trafficSimulator.core.ParkingAreaManager;
import it.graphitech.trafficSimulator.entities.Area;
import it.graphitech.trafficSimulator.entities.DepartureArea;
import it.graphitech.trafficSimulator.renderable.PathExtArea;
import it.graphitech.trafficSimulator.renderable.emitter.Emitter;
import it.graphitech.trafficSimulator.renderable.emitter.EmitterDestAreas;
import it.graphitech.trafficSimulator.renderableManager.AreaManager;
import it.graphitech.trafficSimulator.renderableManager.GeometryManager;
import it.graphitech.trafficSimulator.swing.TrafficSimPanel;
import it.graphitech.trafficSimulator.swing.dialogs.AreaDialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Set;

public class MouseAdp extends MouseAdapter {

	protected AppFrame frame;
	public static TrafficSimPanel trafficSimPanel;

	ArrayList<Position> positions;
	Position position;

	public MouseAdp(AppFrame frame) {
		this.frame = frame;
		trafficSimPanel = frame.getTrafficSimPanel();

	}

	public WorldWindow getWwd() {
		return this.frame.getWwd();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		super.mouseClicked(arg0);
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		if (trafficSimPanel.selectAreaBtn.isSelected()) {

			if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
				if ((mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
					if (!mouseEvent.isControlDown()) {

						position = getWwd().getCurrentPosition();
						if (position != null) {

							if (trafficSimPanel.isSelectingDepartureArea()) {
								AppFrame.emitterManager.resetLastEmmitters();
							}
							if (positions == null) {

								positions = new ArrayList<Position>();
								positions.add(position);
							} else if (position.equals(positions.get(positions
									.size() - 1))) {

							} else {
								positions.add(position);
								AreaManager.generatePolygonArea(positions,
										false);
							}
						}
					}
				}

			} else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {


	position = getWwd().getCurrentPosition();
				if (position != null) {

					if (trafficSimPanel.isSelectingDepartureArea()) {

						positions.add(position);
						positions.add(positions.get(0));
						AreaManager.generatePolygonArea(positions, true);

						AreaManager.createDepartureArea(this.positions);
						Set<PathExtArea> setPaths = GeometryManager
								.getSelectedSegments(positions,
										TrafficSim.streetGraph.getPaths()
												.values());

						DataLayer.sendEmitterRequestPost(positions,
								frame.emitterManager, TrafficSim.streetGraph,
								setPaths);

						ArrayList<Emitter> listEmit = AppFrame.emitterManager
								.getLastAddedEmitters();

						DepartureArea departureArea = AreaManager
								.getLastDepartureArea();
						ArrayList<Integer> idDestAreaArrayForEachEmitter = AreaManager
								.createIdDestAreaArray(listEmit);
						departureArea
								.setIdDestAreaArrayForEachEmitter(idDestAreaArrayForEachEmitter);

						AreaManager.generateIdAreasForEachVehicle();
						positions = null;
						position = null;
						trafficSimPanel.setSelectingDestinationArea(true);
					} else if (trafficSimPanel.isSelectingDestinationArea()) {

						positions.add(position);
						positions.add(positions.get(0));
						AreaManager.generatePolygonArea(positions, true);

						Set<PathExtArea> setPaths = GeometryManager
								.getSelectedSegments(positions,
										TrafficSim.streetGraph.getPaths()
												.values());
						Position a_parkingArea_pos = ParkingAreaManager
								.insertMiddleParkingArea(setPaths,
										frame.emitterManager,
										TrafficSim.streetGraph);

						if (a_parkingArea_pos != null) {

							Position goal = new Position(
									a_parkingArea_pos.getLatitude(),
									a_parkingArea_pos.getLongitude(),
									CustomizableVariables.RENDERALTITUDE);

							Position[] nearDest = TrafficSim.streetGraph
									.getNearestPositionPairFromPathPoint(
											setPaths.iterator().next(),
											a_parkingArea_pos);

							ArrayList<Emitter> listEmit = AppFrame.emitterManager
									.getLastAddedEmitters();



							Area departureArea = AreaManager
									.getLastDepartureArea();

							Area destArea = AreaManager.createDestiantionArea(
									positions, departureArea, goal);

							AreaManager
									.generatePolyginDestinationArea(destArea);
							if (AreaManager.getIdAreasForEachVehicle().size() == 0) {

								AreaManager
										.createIdDestAreaArrayForEachEmitter(listEmit);
							}

							int numVehiclesWithNoDest = AreaManager
									.getNumVehiclesWithNoDestination();
							AreaDialog newContentPane = new AreaDialog(frame,
									numVehiclesWithNoDest);
							newContentPane.setSize(400, 150);

							newContentPane.setLocationRelativeTo(frame);
							newContentPane.setVisible(true);

							AreaManager.assignNumVehiclesToDestination(
									newContentPane.selectedValue,
									destArea.getIndex());

							TrafficSim.streetGraph.updateColors();

							int numVehicles = AreaManager.getNumVehicles();
							double valVehicles = ((double) newContentPane.selectedValue)
									/ ((double) numVehicles);

//							System.out
//									.println(newContentPane.selectedValue
//											+ " / " + numVehicles + " = "
//											+ valVehicles);
							
							
							AreaManager.generateArcs(departureArea, destArea,
									valVehicles);

							int j = 0;

							for (int i = 0; i < listEmit.size(); i++) {

								EmitterDestAreas emitterDestArea = (EmitterDestAreas) listEmit
										.get(i);

								emitterDestArea.createArrayAreas();
								emitterDestArea.createArrayGoals();
								int numCars = emitterDestArea.getNumCars();
								for (int l = 0; l < numCars; l++) {
									int idArea = AreaManager
											.getIdAreasForEachVehicle().get(j);

									if (idArea != -1) {
										emitterDestArea.addArea(idArea);
										Position refPos = AreaManager
												.getReferencePosition(idArea);

							emitterDestArea.addGoal(refPos);
									}

									j++;
								}
								emitterDestArea
										.setNearGoalDestination(nearDest);

							}
							numVehiclesWithNoDest = AreaManager
									.getNumVehiclesWithNoDestination();
							//System.out.println("dopo num veicoli con nessuna dest: "+ numVehiclesWithNoDest);
							TrafficSim.streetGraph.addNodeInJGraph(nearDest[0],
									nearDest[1], goal);

							positions = null;
							position = null;

						}

					}

				}
			}

		}

		else if (trafficSimPanel.selectDangerAreaBtn.isSelected()) {

			if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
				if ((mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
					if (!mouseEvent.isControlDown()) {

						position = getWwd().getCurrentPosition();
						if (position != null) {

							if (trafficSimPanel.isSelectingDangerArea()) {

				if (positions == null) {

					positions = new ArrayList<Position>();
									positions.add(position);
								} else if (position.equals(positions
										.get(positions.size() - 1))) {

									positions.add(positions.get(0));
									AreaManager.generateDangerPolygonArea(
											positions, true);
									Set<PathExtArea> setPaths = GeometryManager
											.getSelectedSegments(positions,
													TrafficSim.streetGraph
															.getPaths()
															.values());
									positions = null;
									position = null;

									for (PathExtArea path : setPaths) {
										path.setDangerArea(true);
									}

									TrafficSim.streetGraph
											.setDangerAreasIncluded(true);
									TrafficSim.streetGraph
											.buildJGraphWithoutDangerAreas();
									TrafficSim.streetGraph
											.buildGraphWithoutDangerAreas();
									TrafficSim.streetGraph.updateColors();
								} else {
									positions.add(position);
									AreaManager.generateDangerPolygonArea(
											positions, false);
								}

							}

						}
					}
				}
			}

			else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {

				position = getWwd().getCurrentPosition();
				if (position != null) {

					positions.add(position);
					positions.add(positions.get(0));
					AreaManager.generateDangerPolygonArea(positions, true);
					Set<PathExtArea> setPaths = GeometryManager
							.getSelectedSegments(positions,
									TrafficSim.streetGraph.getPaths().values());
					positions = null;
					position = null;

					for (PathExtArea path : setPaths) {
						path.setDangerArea(true);
					}

					TrafficSim.streetGraph.setDangerAreasIncluded(true);
					TrafficSim.streetGraph.buildJGraphWithoutDangerAreas();
					TrafficSim.streetGraph.buildGraphWithoutDangerAreas();
					TrafficSim.streetGraph.updateColors();

				}

			}

		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		super.mouseReleased(arg0);
	}

}
