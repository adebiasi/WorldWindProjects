package applett;

/**
 * FluidPanel.java
 * 
 * Display panel for fluidfields. Handles user mouse input and interactions with the Fluidfield.
 * 
 * @author David Wu
 * @version 1.0, November 5, 2007
 */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*;

import applett_gui.MouseGridPanel;
import applett_image.ColorFuncDouble;

public class FluidPanel extends MouseGridPanel
{
	private static final ColorFuncDouble cFunc = new ColorFuncDouble();

	private MasterGUI myGUI;
	private FluidField myField;
	
	private boolean button1;
	private boolean button3;
	private int lastX;
	private int lastY;
	
	public FluidPanel(MasterGUI gui)
	{
		super(false);
		myGUI = gui;
	}
	
	public void setField(FluidField field)
	{
		myField = field;
		setPreferredSize(new Dimension(Math.min(myField.getXSize()*2,800), Math.min(myField.getYSize()*2,800)));
		resizeToBoard(myField.getXSize(), myField.getYSize(), 0, 0, false);
	}
	
	public void componentResized(ComponentEvent e)
	{
		super.componentResized(e);
		if(myField != null)
		{resizeToBoard(myField.getXSize(), myField.getYSize(), 0, 0, false);}
	}

	public void refresh()
	{
		if(myField == null)
		{
			Graphics2D g = getDrawGraphics();
			g.setColor(Color.GRAY);
			g.fillRect(0,0,getXPixel(),getYPixel());
			g.dispose();
			return;
		}
		
		cFunc.setBufferedImageLerp(getDrawImage(),0,0,getDrawImage().getWidth(),getDrawImage().getHeight(),
				myField.getXSize(), myField.getYSize(), 
				myField.getLayer(0), myField.getLayer(1), myField.getLayer(2));
			
		/*
		if(fluidImage == null || fluidImage.getWidth() != myField.getXSize() || fluidImage.getHeight() != myField.getYSize())
		{fluidImage = new BufferedImage(myField.getXSize(), myField.getYSize(), BufferedImage.TYPE_3BYTE_BGR);}
		
		cFunc.setBufferedImage(fluidImage, fluidImage.getWidth(), fluidImage.getHeight(),
				myField.getLayer(0), myField.getLayer(1), myField.getLayer(2));
		
		Graphics2D g = getDrawGraphics();
		g.drawImage(fluidImage,0,0,getXPixel(),getYPixel(),0,0,fluidImage.getWidth(), fluidImage.getHeight(), null);
		g.dispose();
		*/
		repaint();
	}
	
	public void mousePressed(MouseEvent e)
	{
		int button = e.getButton();
		if(button == MouseEvent.BUTTON1)
		{
			lastX = e.getX();
			lastY = e.getY();
			addi(e.getX(), e.getY());
			button1 = true;
		}
		else if(button == MouseEvent.BUTTON3)
		{
			button3 = true;
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{
		int button = e.getButton();
		if(button == MouseEvent.BUTTON1)
		{
			button1 = false;
		}
		else if(button == MouseEvent.BUTTON3)
		{
			button3 = false;
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{
		if(button1)
		{
			addi(e.getX(), e.getY());
		}
		
		if(button1 || button3)
		{
			//System.out.println("adadfadfas");
			int dx = e.getX() - lastX;
			int dy = e.getY() - lastY;
			addv(e.getX(), e.getY(), dx, dy);
		}
		
		lastX = e.getX();
		lastY = e.getY();
	}
	/*
	public void addi(int xPixel, int yPixel)
	{
		int r = myGUI.mySidePanel.getR();
		int g = myGUI.mySidePanel.getG();
		int b = myGUI.mySidePanel.getB();
		r += (int)(Math.random()*31)-15;
		g += (int)(Math.random()*31)-15;
		b += (int)(Math.random()*31)-15;
		if(r < 0) {r = 0;}
		if(g < 0) {g = 0;}
		if(b < 0) {b = 0;}
		if(r > 255) {r = 255;}
		if(g > 255) {g = 255;}
		if(b > 255) {b = 255;}
		
		int xSize = myField.getXSize();
		int ySize = myField.getYSize();
		
		int x1 = xPixel*xSize/getXPixel();
		int y1 = yPixel*ySize/getYPixel();
		
		int xRadius = xSize*myGUI.mySidePanel.getPaintSize()/50/40;
		int yRadius = ySize*myGUI.mySidePanel.getPaintSize()/50/40;
		
		int x0 = x1-xRadius;
		int y0 = y1-yRadius;
		
		int x2 = x1+xRadius;
		int y2 = y1+yRadius;
		
		if(x0 < 0) {x0 = 0;}
		if(x2 < 0) {x2 = 0;}
		if(y0 < 0) {y0 = 0;}
		if(y2 < 0) {y2 = 0;}
		if(x0 >= xSize) {x0 = xSize-1;}
		if(x2 >= xSize) {x2 = xSize-1;}
		if(y0 >= ySize) {y0 = ySize-1;}
		if(y2 >= ySize) {y2 = ySize-1;}
		
		for(int x = x0; x <= x2; x++)
		{
			for(int y = y0; y <= y2; y++)
			{
				int dx = x - x1;
				int dy = y - y1;
				if((double)dx*dx*yRadius*yRadius + (double)dy*dy*xRadius*xRadius <= (double)xRadius*xRadius*yRadius*yRadius)
				{
					System.out.println("dentro");
					int k = myField.getK(x,y);
					myField.setInk(0,k,r/255.0);
					myField.setInk(1,k,g/255.0);
					myField.setInk(2,k,b/255.0);
				}
			}
		}
	}
	*/
	
	
	public void addi(int xPixel, int yPixel)
	{
		
		/*
		int r = myGUI.mySidePanel.getR();
		int g = myGUI.mySidePanel.getG();
		int b = myGUI.mySidePanel.getB();
		r += (int)(Math.random()*31)-15;
		g += (int)(Math.random()*31)-15;
		b += (int)(Math.random()*31)-15;
		if(r < 0) {r = 0;}
		if(g < 0) {g = 0;}
		if(b < 0) {b = 0;}
		if(r > 255) {r = 255;}
		if(g > 255) {g = 255;}
		if(b > 255) {b = 255;}
		*/
		
		int xSize = myField.getXSize();
		int ySize = myField.getYSize();
		
		int x1 = xPixel*xSize/getXPixel();
		int y1 = yPixel*ySize/getYPixel();
		
		/*
		int xRadius = xSize*myGUI.mySidePanel.getPaintSize()/50/40;
		int yRadius = ySize*myGUI.mySidePanel.getPaintSize()/50/40;
		
		int x0 = x1-xRadius;
		int y0 = y1-yRadius;
		
		int x2 = x1+xRadius;
		int y2 = y1+yRadius;
		
		if(x0 < 0) {x0 = 0;}
		if(x2 < 0) {x2 = 0;}
		if(y0 < 0) {y0 = 0;}
		if(y2 < 0) {y2 = 0;}
		if(x0 >= xSize) {x0 = xSize-1;}
		if(x2 >= xSize) {x2 = xSize-1;}
		if(y0 >= ySize) {y0 = ySize-1;}
		if(y2 >= ySize) {y2 = ySize-1;}
		*/
		
		
					System.out.println("dentro");
					int k = myField.getK(x1,y1);
					myField.setInk(0,k,150.0);
					myField.setInk(1,k,150.0);
					myField.setInk(2,k,150.0);
				
	}
	
	public void addv(int xPixel, int yPixel, int xMag, int yMag)
	{
		int xSize = myField.getXSize();
		int ySize = myField.getYSize();
		
		double xVel = (double)xMag*xSize/getXPixel()*2;
		double yVel = (double)yMag*ySize/getYPixel()*2;
		
		int x1 = xPixel*xSize/getXPixel();
		int y1 = yPixel*ySize/getYPixel();
		
		int xRadius = xSize*myGUI.mySidePanel.getPaintSize()/50/40;
		int yRadius = ySize*myGUI.mySidePanel.getPaintSize()/50/40;
		
		int x0 = x1-xRadius;
		int y0 = y1-yRadius;
		
		int x2 = x1+xRadius;
		int y2 = y1+yRadius;
		
		if(x0 < 0) {x0 = 0;}
		if(x2 < 0) {x2 = 0;}
		if(y0 < 0) {y0 = 0;}
		if(y2 < 0) {y2 = 0;}
		if(x0 >= xSize) {x0 = xSize-1;}
		if(x2 >= xSize) {x2 = xSize-1;}
		if(y0 >= ySize) {y0 = ySize-1;}
		if(y2 >= ySize) {y2 = ySize-1;}
		
		//System.out.println("radius: "+xRadius);
		
		for(int x = x0; x <= x2; x++)
		{
			for(int y = y0; y <= y2; y++)
			{
				int dx = x - x1;
				int dy = y - y1;
				if((double)dx*dx*yRadius*yRadius + (double)dy*dy*xRadius*xRadius <= (double)xRadius*xRadius*yRadius*yRadius)
				{
					int k = myField.getK(x,y);
					myField.addVel(k,xVel,yVel);
					//System.out.println("xVel:"+xVel+" yVel:"+yVel);
				}
			}
		}
	}
}
