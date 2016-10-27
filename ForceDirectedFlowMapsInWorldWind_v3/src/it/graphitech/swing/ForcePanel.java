package it.graphitech.swing;


import it.graphitech.Variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ForcePanel extends JPanel{

	JButton button_enableRejectForce;
	JButton button_enableElectrostaticForce;
	JButton button_enableSpringForce;
	//JButton button_enableInverseElectrostaticForce;
	
	public ForcePanel() {

//	button_enableRejectForce = new JButton("Enable Reject Force");
//	button_enableRejectForce.addActionListener(new ActionListener() {
//		
//		@Override
//		public void actionPerformed(ActionEvent event) {
//			if(Variables.enableRejectForce){
//				button_enableRejectForce.setText("Enable Reject Force");
//			}else{
//				button_enableRejectForce.setText("Disable Reject Force");
//			}
//			Variables.enableRejectForce=!Variables.enableRejectForce;
//		}
//		
//	});
//	
//	this.add(button_enableRejectForce);
//	
	
	
	
	button_enableElectrostaticForce = new JButton("Enable Electrostatic Force");
	button_enableElectrostaticForce.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(Variables.enableElectrostaticForce){
				button_enableElectrostaticForce.setText("Enable Electrostatic Force");
			}else{
				button_enableElectrostaticForce.setText("Disable Electrostatic Force");
			}
			Variables.enableElectrostaticForce=!Variables.enableElectrostaticForce;
		}
		
	});
	
	this.add(button_enableElectrostaticForce);
	
	
	button_enableSpringForce = new JButton("Enable Spring Force");
	button_enableSpringForce.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(Variables.enableSpringForce){
				button_enableSpringForce.setText("Enable Spring Force");
			}else{
				button_enableSpringForce.setText("Disable Spring Force");
			}
			Variables.enableSpringForce=!Variables.enableSpringForce;
		}
		
	});
	
	this.add(button_enableSpringForce);
	
	
	
	
	button_enableRejectForce = new JButton("Enable Reject Force");
	button_enableRejectForce.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(Variables.isStable){
				button_enableRejectForce.setText("Enable stability");
			}else{
				button_enableRejectForce.setText("Disable stability");
			}
			Variables.isStable=!Variables.isStable;
		}
		
	});
	
	this.add(button_enableRejectForce);
	
	
	/*
	button_enableInverseElectrostaticForce = new JButton("Enable Inverse Electrostatic Force");
	button_enableInverseElectrostaticForce.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(Variables.enableInverseElectrostaticForce){
				button_enableInverseElectrostaticForce.setText("Enable Inverse Electrostatic Force");
			}else{
				button_enableInverseElectrostaticForce.setText("Disable Inverse Electrostatic Force");
			}
			Variables.enableInverseElectrostaticForce=!Variables.enableInverseElectrostaticForce;
		}
		
	});
	
	this.add(button_enableInverseElectrostaticForce);
	*/
	}
}
