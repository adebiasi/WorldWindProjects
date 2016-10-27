package it.graphitech.swing;




import it.graphitech.Variables;
import it.graphitech.modules.MainModule;
import it.graphitech.modules.PrintResults;
import it.graphitech.modules.Render;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


public class ButtonPanel extends JPanel{

	JButton button_open_input;
	JButton button_run;
	//JButton button_prev;
	JButton button_move;
	static public JButton button_auto_move;	
	JButton button_render;
	JButton button_screenshots;
	JButton button_create_tree;
	
	//static JLabel num_iteration ;
	
	//static Timer timer;
	
	
	
	
	public ButtonPanel() {
		super();
		
	
		
		button_open_input = new JButton("Open Origin-Destination Data");

		button_open_input.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					//Create a file chooser
					 JFileChooser fc = new JFileChooser();
					 int returnVal = fc.showOpenDialog(null);

				        if (returnVal == JFileChooser.APPROVE_OPTION) {
				            //////////////////////////
				        	//Render.resetLayers();
				        	/////////////////////////
				        	
				        	//Variables.curr_energy=Variables.initial_energy;
				        	
				        	File file = fc.getSelectedFile();
				        	Variables.fileName=file.getName();
				        	
				        	
				        	
				        	String path = file.getAbsolutePath();
				        	
				        	
				        	MainModule.init(path);
							MainModule.updateRendering();
							
							Render.enableCurvesLayer();
				        } else {
				           
				        }
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
		
		
		}});
		
		
		this.add(button_open_input);
		
	
	
	button_run = new JButton("Reset");
	button_run.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			MainModule.reset();
			MainModule.updateRendering();
	
	}});
	
	this.add(button_run);
	
	
	/*
	button_prev = new JButton("Only SpringForce");
	button_prev.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			ForceDirectedTechnique.executeOnlySpringForce();
			
	}});
	*/
	//this.add(button_prev);
	
	
	
	
	
	
	
	/*
	ActionListener animate = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
        	start();        	
        }
    };
     timer = new Timer(1,animate);
   */
     button_move = new JButton("MOVE");
 	button_move.addActionListener(new ActionListener() {
 		
 		@Override
 		public void actionPerformed(ActionEvent event) {
 			// TODO Auto-generated method stub
 			//NodeManager.printLeafNodes();
 			MainModule.iterate();
 			MainModule.updateRendering();
 			PrintResults.screenShootCapture();
 		}
 	});
 	this.add(button_move);
	
	button_auto_move = new JButton("Start");
	button_auto_move.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent event) {
		MainModule.start();
		}
	});
	
	this.add(button_auto_move);
	
	
	
	
	
	button_render = new JButton("Render Now");
	button_render.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent event) {
//			if(Variables.enable_stability){
//				button_enableStability.setText("Enable Stability");
//			}else{
//				button_enableStability.setText("Disable Stability");
//			}
			//Variables.enable_stability=true;
			MainModule.updateRenderingDuringExecution();
		}
		
	});
	
	this.add(button_render);
	
	
	
	button_screenshots = new JButton("Take Screenshots");
	button_screenshots.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent event) {
//			if(Variables.enable_stability){
//				button_enableStability.setText("Enable Stability");
//			}else{
//				button_enableStability.setText("Disable Stability");
//			}
			//Variables.enable_stability=true;
			MainModule.takeScreenshotsDuringExecution();
		}
		
	});
	
	this.add(button_screenshots);
	
	
	
	//num_iteration = new JLabel("num Iteration:");
	//this.add(num_iteration);
	}
	
	
	
	
	
	
	
	
}
