package applett;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;

/**
 * Sidepanel.java
 * 
 * Basic side panel with sliders to control the fluid properties.
 * 
 * @author David Wu
 * @version 1.0, November 5, 2007
 */

public class SidePanel extends JPanel implements ActionListener, ChangeListener
{
	private MasterGUI myGUI;
	
	private JSlider myRSlider;
	private JSlider myGSlider;
	private JSlider myBSlider;
	private JSlider myPaintSizeSlider;
	private JSlider myDiffusionSlider;
	private JSlider myViscositySlider;
	private JSlider myVorticitySlider;
	private JSlider myIterationSlider;
	private JSlider myTimestepSlider;
	
	private JButton myNewFieldButton;
	
	private JPanel myColorPanelR;
	private JPanel myColorPanelG;
	private JPanel myColorPanelB;
	
	public SidePanel(MasterGUI gui)
	{
		myGUI = gui;
		
		myRSlider = new JSlider(0,255,255);
		myGSlider = new JSlider(0,255,255);
		myBSlider = new JSlider(0,255,255);
		
		myPaintSizeSlider = new JSlider(0,100,50);
		
		myDiffusionSlider = new JSlider(0,100,0);
		myViscositySlider = new JSlider(0,100,0);
		myVorticitySlider = new JSlider(0,100,25);
		myIterationSlider = new JSlider(1,100,8);
		myTimestepSlider = new JSlider(1,100,20);
		
		myNewFieldButton = new JButton("New Field");
		
		//myColorPanel = new JPanel();
		//myColorPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		//myColorPanel.setPreferredSize(new Dimension(100,100));

		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		JPanel panel;
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(new JLabel("Red  :"));
		panel.add(myRSlider);
		add(panel);
		myColorPanelR = panel;
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(new JLabel("Green:"));
		panel.add(myGSlider);
		add(panel);
		myColorPanelG = panel;
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(new JLabel("Blue :"));
		panel.add(myBSlider);
		add(panel);
		myColorPanelB = panel;
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(new JLabel("Splat Size:"));
		panel.add(myPaintSizeSlider);
		add(panel);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(new JLabel("Diffusion"));
		panel.add(myDiffusionSlider);
		add(panel);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(new JLabel("Viscosity"));
		panel.add(myViscositySlider);
		add(panel);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(new JLabel("Vorticity"));
		panel.add(myVorticitySlider);
		add(panel);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(new JLabel("Timestep"));
		panel.add(myTimestepSlider);
		add(panel);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(new JLabel("Iterations"));
		panel.add(myIterationSlider);
		add(panel);
		
		add(myNewFieldButton);
		
		myRSlider.addChangeListener(this);
		myGSlider.addChangeListener(this);
		myBSlider.addChangeListener(this);
		myNewFieldButton.addActionListener(this);
		
		refreshColor();
		
	}

	public void actionPerformed(ActionEvent e)
	{
		myGUI.newField(false);
	}
	
	public void stateChanged(ChangeEvent e)
	{
		refreshColor();
	}
	
	public void refreshColor()
	{
		Color c = new Color(myRSlider.getValue(), myGSlider.getValue(), myBSlider.getValue());
		//myGUI.mySideFrame.setForeground(c);
		myRSlider.setBackground(c);
		myGSlider.setBackground(c);
		myBSlider.setBackground(c);
		myColorPanelR.setBackground(c);
		myColorPanelG.setBackground(c);
		myColorPanelB.setBackground(c);
	}

	public int getR()
	{return myRSlider.getValue();}
	
	public int getG()
	{return myGSlider.getValue();}
	
	public int getB()
	{return myBSlider.getValue();}
	
	public int getPaintSize()
	{return myPaintSizeSlider.getValue();}
	
	public double getDiff()
	{return myDiffusionSlider.getValue()/20.0;}
	
	public double getVisc()
	{return myViscositySlider.getValue()/10.0;}
	
	public double getVort()
	{return myVorticitySlider.getValue()/25.0;}
	
	public int getIter()
	{return myIterationSlider.getValue();}
	
	public double getTime()
	{return myTimestepSlider.getValue()/200.0;}
	
}
