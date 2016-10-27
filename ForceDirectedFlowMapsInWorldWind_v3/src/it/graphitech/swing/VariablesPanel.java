package it.graphitech.swing;





import it.graphitech.Variables;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class VariablesPanel extends JPanel{

	//JTextField distanceNeighbors_text;
	static public JTextField distanceRejectArea_text;
	//JTextField distanceNearNeighbors_text;
	//JTextField numSegment_text;
	JTextField samePositionArea_text;
	
	JLabel distanceNeighbors_label;
	JLabel distanceRejectArea_label;
	JLabel distanceNearNeighbors_label;
	//JLabel numSegment_label;
	//JLabel step_label;
	JLabel samePositionArea_label;
	
	public static JTextField elect_Force_text;
	public static JTextField spring_Force_text;
	//JTextField step_text;
	JTextField reject_Force_text;
	
	JLabel elect_Force_label;
	JLabel spring_Force_label;
	JLabel reject_Force_label;
	
	
	JLabel energy_label;
	JTextField energy_text;
	
	//static DrawPanel drawPanel;
	
	public VariablesPanel() {
		super();
		
	
		/*
		step_label = new JLabel("step:");
		this.add(step_label);		
		step_text = new JTextField(String.valueOf(Variables.index_step));	
		step_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.index_step=Double.valueOf(step_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.index_step=Double.valueOf(step_text.getText());
			}
		});
		
		this.add(step_label);
		this.add(step_text);
		*/
		
		energy_label = new JLabel("energy:");
		this.add(energy_label);		
		energy_text = new JTextField(String.valueOf(Variables.curr_energy));	
		energy_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.initial_energy=Double.valueOf(energy_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.curr_energy=Double.valueOf(energy_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.curr_energy=Double.valueOf(energy_text.getText());
			}
		});
		
		this.add(energy_label);
		this.add(energy_text);
		
		/*
		distanceNeighbors_label = new JLabel("distanceNeighbors");
		this.add(distanceNeighbors_label);		
		distanceNeighbors_text = new JTextField(String.valueOf(Variables.distanceNeighbors));	
		distanceNeighbors_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.distanceNeighbors=Double.valueOf(distanceNeighbors_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.distanceNeighbors=Double.valueOf(distanceNeighbors_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.distanceNeighbors=Double.valueOf(distanceNeighbors_text.getText());
			}
		});
		
		
		this.add(distanceNeighbors_text);
	*/
		
		
		
		distanceRejectArea_label = new JLabel("reject buffer");
		this.add(distanceRejectArea_label);		
		distanceRejectArea_text = new JTextField(String.valueOf(Variables.rejectBufferInMeters));	
		distanceRejectArea_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.distanceNeighbors=Double.valueOf(distanceNeighbors_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
				Variables.rejectBufferInMeters=Double.valueOf(distanceRejectArea_text.getText());
				//System.out.println("AGGIORNO DISTANCE: "+Variables.rejectBufferInMeters);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.rejectBufferInMeters=Double.valueOf(distanceRejectArea_text.getText());
			}
		});
		
		
		this.add(distanceRejectArea_text);
		
		
		
		samePositionArea_label = new JLabel("same position");
		this.add(samePositionArea_label);		
		samePositionArea_text = new JTextField(String.valueOf(Variables.samePositionDistanceInMeters));	
		samePositionArea_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.distanceNeighbors=Double.valueOf(distanceNeighbors_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.samePositionDistanceInMeters=Double.valueOf(samePositionArea_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.samePositionDistanceInMeters=Double.valueOf(samePositionArea_text.getText());
			}
		});
		
		
		this.add(samePositionArea_text);
		
//		distanceNearNeighbors_label = new JLabel("distanceNearNeighbors");
//		this.add(distanceNearNeighbors_label);
//		distanceNearNeighbors_text = new JTextField(String.valueOf(Variables.distanceNearNeighbors));	
//distanceNearNeighbors_text.getDocument().addDocumentListener(new DocumentListener() {
//			
//			@Override
//			public void removeUpdate(DocumentEvent e) {
//				// TODO Auto-generated method stub
//				Variables.distanceNearNeighbors=Double.valueOf(distanceNearNeighbors_text.getText());
//			}
//			
//			@Override
//			public void insertUpdate(DocumentEvent e) {
//				// TODO Auto-generated method stub
//				Variables.distanceNearNeighbors=Double.valueOf(distanceNearNeighbors_text.getText());
//			}
//			
//			@Override
//			public void changedUpdate(DocumentEvent e) {
//				// TODO Auto-generated method stub
//				Variables.distanceNearNeighbors=Double.valueOf(distanceNearNeighbors_text.getText());
//			}
//		});
//		this.add(distanceNearNeighbors_text);		
		
		/*
		numSegment_label = new JLabel("distance btw nodes");
		this.add(numSegment_label);
		numSegment_text = new JTextField(String.valueOf(Variables.lengthMiddleSegment));	
		numSegment_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
			//	Variables.numSegment=Integer.valueOf(numSegment_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.lengthMiddleSegment=Double.valueOf(numSegment_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.lengthMiddleSegment=Double.valueOf(numSegment_text.getText());
			}
		});
		this.add(numSegment_text);	
	*/
		
		
		
		
		spring_Force_label = new JLabel("spring_Force");
		this.add(spring_Force_label);
		spring_Force_text = new JTextField(String.valueOf(Variables.spring_force_factor));	
		spring_Force_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.spring_force_factor=Double.valueOf(spring_Force_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.spring_force_factor=Double.valueOf(spring_Force_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.spring_force_factor=Double.valueOf(spring_Force_text.getText());
			}
		});
		this.add(spring_Force_text);	
	
		reject_Force_label = new JLabel("reject_Force");
		this.add(reject_Force_label);
		reject_Force_text = new JTextField(String.valueOf(Variables.reject_force_factor));	
		reject_Force_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.electrostatic_force_factor=Double.valueOf(elect_Force_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.reject_force_factor=Double.valueOf(reject_Force_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.reject_force_factor=Double.valueOf(reject_Force_text.getText());
			}
		});
		this.add(reject_Force_text);	
	
		elect_Force_label = new JLabel("elect_Force");
		this.add(elect_Force_label);
		elect_Force_text = new JTextField(String.valueOf(Variables.electrostatic_force_factor));	
		elect_Force_text.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//Variables.electrostatic_force_factor=Double.valueOf(elect_Force_text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.electrostatic_force_factor=Double.valueOf(elect_Force_text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				Variables.electrostatic_force_factor=Double.valueOf(elect_Force_text.getText());
			}
		});
		this.add(elect_Force_text);	
	
		
	}
}
