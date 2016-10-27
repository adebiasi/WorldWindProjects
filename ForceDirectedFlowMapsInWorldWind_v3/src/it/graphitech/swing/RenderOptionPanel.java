package it.graphitech.swing;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import it.graphitech.Variables;
import it.graphitech.modules.Render;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class RenderOptionPanel  extends JPanel{

//JTextField nodeRadius_text;
//JLabel nodeRadius_label;

public static JTextField min_text;
JLabel min_label;

public static JTextField max_text;
JLabel max_label;

JButton enable_circle;
JButton enable_distance;
JButton enable_circle_same_pos;
JButton enable_edge;
JButton enable_curve;
JButton enable_node;
JButton enable_int_node;
//JButton enable_tree;

	public RenderOptionPanel() {
		super();
		
		
		min_label = new JLabel("min width");
		this.add(min_label);
		min_text = new JTextField(String.valueOf(Variables.minWidth));	
		min_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.nodeRadius=Double.valueOf(nodeRadius_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.minWidth=Float.valueOf(min_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.minWidth=Float.valueOf(min_text.getText());
			}
		});
		this.add(min_text);	
		
		max_label = new JLabel("max width");
		this.add(max_label);
		max_text = new JTextField(String.valueOf(Variables.maxWidth));	
		max_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.nodeRadius=Double.valueOf(nodeRadius_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.maxWidth=Float.valueOf(max_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.maxWidth=Float.valueOf(max_text.getText());
			}
		});
		this.add(max_text);	
		
		
		/*
		nodeRadius_label = new JLabel("nodeRadius");
		this.add(nodeRadius_label);
		nodeRadius_text = new JTextField(String.valueOf(Variables.nodeRadius));	
		nodeRadius_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.nodeRadius=Double.valueOf(nodeRadius_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.nodeRadius=Double.valueOf(nodeRadius_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.nodeRadius=Double.valueOf(nodeRadius_text.getText());
			}
		});
		this.add(nodeRadius_text);	
		*/
		enable_distance = new JButton("enable Distance Areas");
		enable_distance.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Variables.enable_render_circle_neighbours=!Variables.enable_render_circle_neighbours;
				
				Render.enableDistancesLayer();
			}
		
		});
		this.add(enable_distance);
		
		enable_circle = new JButton("enable Reject Areas");
		enable_circle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Variables.enable_render_circle_neighbours=!Variables.enable_render_circle_neighbours;
				
				Render.enableRejectsLayer();
			}
		
		});
		this.add(enable_circle);
		
		enable_circle_same_pos = new JButton("enable Same pos Areas");
		enable_circle_same_pos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Variables.enable_render_circle_neighbours=!Variables.enable_render_circle_neighbours;
				
				Render.enableSamePosLayer();
			}
		
		});
		this.add(enable_circle_same_pos);
		
		enable_edge = new JButton("enable Edges");
		enable_edge.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Variables.enable_render_edges=!Variables.enable_render_edges;
				Render.enableEdgesLayer();
			}
		});
		this.add(enable_edge);
		
		enable_curve = new JButton("enable Curves");
		enable_curve.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Variables.enable_render_edges=!Variables.enable_render_edges;
				Render.enableCurvesLayer();
			}
		});
		this.add(enable_curve);
		
		enable_node = new JButton("enable Nodes");
		enable_node.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Variables.enable_render_nodes=!Variables.enable_render_nodes;
				Render.enableNodesLayer();
			}
		});
		this.add(enable_node);
		
		enable_int_node = new JButton("enable Intermediate Nodes");
		enable_int_node.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Variables.enable_render_nodes=!Variables.enable_render_nodes;
				Render.enableIntermediateNodesLayer();
			}
		});
		this.add(enable_int_node);
	
		
//		enable_tree = new JButton("enable Flows");
//		enable_tree.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				// TODO Auto-generated method stub
//				//Variables.enable_render_tree=!Variables.enable_render_tree;
//				Render.enableFlowsLayer();
//			}
//		});
//		this.add(enable_tree);
		
		
	}
}
