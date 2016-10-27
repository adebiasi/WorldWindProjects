package it.graphitech.trafficSimulator.swing;

import gov.nasa.worldwind.layers.RenderableLayer;
import it.graphitech.trafficSimulator.TrafficSim.AppFrame;
import it.graphitech.trafficSimulator.renderableManager.AreaManager;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * This Jpanel contains all the buttons and the check list of the layers
 * 
 * @author a.debiasi
 * 
 */
public class TrafficSimPanel extends JPanel implements ActionListener {

	public static final String ACTION_COMMAND_ACTIVATE_EMITTERS = "ActionCommandActivateEmitters";
	public static final String ACTION_COMMAND_DEACTIVATE_EMITTERS = "ActionCommandDeactivateEmitters";
	public static final String ACTION_COMMAND_SELECT_AREA = "ActionCommandSelectArea";
	public static final String ACTION_COMMAND_SELECT_DANGER_AREA = "ActionCommandSelectDangerArea";
	/**
	 * 
	 */
	private static final long serialVersionUID = 7371751805485051630L;

	protected EventListenerList eventListeners = new EventListenerList();

	//public JToggleButton destAreaEmitterBtn;
	public JToggleButton selectAreaBtn;
	public JToggleButton selectDangerAreaBtn;
	public JButton importDangerAreaBtn;
	private JButton activateEmitters;
	private JButton deactivateEmitters;

	
	public boolean  isStartedTheSimulation=false;
	boolean selectingDepartureArea;

	boolean selectingDetinationArea;
	boolean selectingDangerArea;

	AppFrame appFrame;

	public TrafficSimPanel(AppFrame appFrame) {
		this.makePanel();
		this.appFrame = appFrame;
	}

	private void makePanel() {
		this.setLayout(new GridLayout(0, 1, 0, 5)); // rows, cols, hgap, vgap
		this.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(9, 9,
				9, 9), new TitledBorder("Traffic Simulator")));

	
		selectDangerAreaBtn = new JToggleButton("Select Danger Area");
		selectDangerAreaBtn.setActionCommand(ACTION_COMMAND_SELECT_DANGER_AREA);

		selectDangerAreaBtn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JToggleButton btn = (JToggleButton) e.getSource();
				if (btn.isSelected()) {
					
					selectAreaBtn.setEnabled(false);
					importDangerAreaBtn.setEnabled(false);
					activateEmitters.setEnabled(false);
					deactivateEmitters.setEnabled(false);
					setSelectingDangerArea(true);
				} else {
					
					
					if(!isStartedTheSimulation){
						selectAreaBtn.setEnabled(true);
						importDangerAreaBtn.setEnabled(true);				
						
					}
					activateEmitters.setEnabled(true);
					deactivateEmitters.setEnabled(true);
					
					
					
					setSelectingDangerArea(false);
				}
			}
		});

		this.add(selectDangerAreaBtn);

		importDangerAreaBtn = new JButton("Import Danger Area");

		importDangerAreaBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				RenderableLayer layer = AreaManager.insertOutput(appFrame);
				appFrame.getWwd().getModel().getLayers().add(layer);
				appFrame.getWwd().redraw();
				appFrame.getWwd().repaint();
			}

		});
		this.add(importDangerAreaBtn);

		
		selectAreaBtn = new JToggleButton("Select Area");
		selectAreaBtn.setActionCommand(ACTION_COMMAND_SELECT_AREA);

		selectAreaBtn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JToggleButton btn = (JToggleButton) e.getSource();
				if (btn.isSelected()) {
					selectDangerAreaBtn.setEnabled(false);
					importDangerAreaBtn.setEnabled(false);
					activateEmitters.setEnabled(false);
					deactivateEmitters.setEnabled(false);
					setSelectingDepartureArea(true);
				} else {
					activateEmitters.setEnabled(true);
					deactivateEmitters.setEnabled(true);
					
					
//					if(!isStartedTheSimulation){
//						selectDangerAreaBtn.setEnabled(true);
//						importDangerAreaBtn.setEnabled(true);
//					}
					setSelectingDepartureArea(false);
				}
			}
		});

		this.add(selectAreaBtn);

		
		activateEmitters = new JButton("Activate All Emitters");
		activateEmitters.setActionCommand(ACTION_COMMAND_ACTIVATE_EMITTERS);
		activateEmitters.addActionListener(this);
		this.add(activateEmitters);

		deactivateEmitters = new JButton("Deactivate All Emitters");
		deactivateEmitters.setActionCommand(ACTION_COMMAND_DEACTIVATE_EMITTERS);
		deactivateEmitters.addActionListener(this);
		this.add(deactivateEmitters);

	}

	public void addActionListener(ActionListener listener) {
		this.eventListeners.add(ActionListener.class, listener);
	}

	public void removeActionListener(ActionListener listener) {
		this.eventListeners.remove(ActionListener.class, listener);
	}

	public boolean isSelectingDepartureArea() {
		return selectingDepartureArea;
	}

	public boolean isSelectingDestinationArea() {
		return selectingDetinationArea;
	}

	public boolean isSelectingDangerArea() {
		return selectingDangerArea;
	}

	public void setSelectingDepartureArea(boolean bool) {
		if (bool == true) {
			selectingDepartureArea = bool;
			selectingDetinationArea = !bool;

		} else {
			selectingDepartureArea = bool;
			selectingDetinationArea = bool;

		}
	}

	public void setSelectingDangerArea(boolean bool) {
		
		selectingDangerArea = bool;

	}

	public void setSelectingDestinationArea(boolean bool) {
		if (bool == true) {
			selectingDepartureArea = !bool;
			selectingDetinationArea = bool;

		} else {
			selectingDepartureArea = bool;
			selectingDetinationArea = bool;

		}
	}

	public void actionPerformed(ActionEvent actionEvent) {
		this.callActionListeners(actionEvent);
	}

	protected void callActionListeners(ActionEvent actionEvent) {
		ActionListener[] actionListeners = this.eventListeners
				.getListeners(ActionListener.class);
		if (actionListeners == null)
			return;

		for (ActionListener listener : actionListeners) {
			listener.actionPerformed(actionEvent);
		}
	}

}
