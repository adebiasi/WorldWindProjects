package applett;

import java.awt.BorderLayout;
import java.awt.Toolkit;


import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import applett_image.ColorFuncDouble;

/**
 * MasterGUI.java
 * 
 * Outerlevel of the GUI. Functions as the JApplet when run as an applet, or else tracks and manages the two frames
 * used for the fluid panel and the side panel. Also contains the main thread for the fluid.
 * 
 * TODO: Drawing code much improved. Still, it is a bottleneck. Goal is to have acceptable speed fullscreen.
 * TODO: Improve ColorFunc
 * TODO: A framerate counter would be nice. And make the windows center themselves, and make the fluid window larger 
 * to begin with. And add some fireworks and explosives and swirler tools.
 * TODO: Make a game out of this too. =)
 * 
 * @author David Wu
 * @version 1.0, November 5, 2007
 */

public class MasterGUI extends JApplet
{
	public JFrame myFluidFrame;
	public JFrame mySideFrame;
	
	public FluidPanel myFluidPanel;
	public SidePanel mySidePanel;
	
	public FluidField myField;
	
	public boolean isApplication;
	
	public Thread animThread;
	
	public MasterGUI()
	{
		
	}
	
	public MasterGUI(boolean isApp)
	{
		isApplication = isApp;
		
		mySidePanel = new SidePanel(this);
		myFluidPanel = new FluidPanel(this);
		
		mySideFrame = new JFrame();
		mySideFrame.setTitle("Settings");
		mySideFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mySideFrame.getContentPane().add(mySidePanel);
		mySideFrame.pack();
		
		myFluidFrame = new JFrame();
		myFluidFrame.setTitle("FluidSim");
		myFluidFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFluidFrame.getContentPane().add(myFluidPanel);
		myFluidFrame.pack();
		
		initField(200);
		
		newField(true);

		Toolkit tk = myFluidFrame.getToolkit();
		myFluidFrame.setLocation((tk.getScreenSize().width-myFluidFrame.getWidth()-mySideFrame.getWidth())/2, 
				(tk.getScreenSize().height-myFluidFrame.getHeight())/2);
		mySideFrame.setLocation(myFluidFrame.getLocation().x+myFluidFrame.getWidth(), myFluidFrame.getLocation().y);
		myFluidFrame.setVisible(true);
		mySideFrame.setVisible(true);
		start();
	}
	
	public void init()
	{
		isApplication = false;
		mySidePanel = new SidePanel(this);
		myFluidPanel = new FluidPanel(this);
		initField(100);
		mySidePanel.setSize(400,400);
		myFluidPanel.setSize(250,400);
		add(myFluidPanel,BorderLayout.LINE_START);
		add(mySidePanel,BorderLayout.LINE_END);
		addMouseListener(myFluidPanel);
		
	}
	
	public void start()
	{
		if(animThread == null)
		{
			animThread = new AnimThread();
		}
		animThread.start();
	}
	
	public void stop()
	{
		animThread = null;
	}

	
	public void newField(boolean exit)
	{
		int x = 0;
		
		String xString = JOptionPane.showInputDialog(mySideFrame, "Resolution (40-500)");
		try{x = Integer.parseInt(xString);}
		catch(Exception e){x = 0;}
		if(x < 40 || x > 500)
		{
			if(exit && isApplication) {System.exit(0);}
			return;
		}
		
		initField(x);
	}
	
	private void initField(int size)
	{
		FluidField newField = new FluidField(size,size,3,true);
		myField = newField;
		myFluidPanel.setField(newField);	
		
		if(isApplication)
		{
			myFluidFrame.pack();	
		}
	}
	
	public static final ColorFuncDouble cFunc = new ColorFuncDouble();
	
	private class AnimThread extends Thread
	{
		public void run()
		{
			while(Thread.currentThread() == animThread)
			{
				if(myFluidPanel != null &&  myField != null)
				{
					myField.setDiffusion(mySidePanel.getDiff());
					myField.setIterations(mySidePanel.getIter());
					myField.setTimeStep(mySidePanel.getTime());
					myField.setViscosity(mySidePanel.getVisc());
					myField.setVorticity(mySidePanel.getVort());
					myField.step();
					cFunc.normalize(myField.getLayer(0));
					cFunc.normalize(myField.getLayer(1));
					cFunc.normalize(myField.getLayer(2));
					myFluidPanel.refresh();
				}
			}
			
		}
	}
	
}
