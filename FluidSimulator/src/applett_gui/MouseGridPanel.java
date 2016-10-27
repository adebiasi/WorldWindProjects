
package applett_gui;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * MouseGridPanel.java
 * 
 * Panel that can be extended to easily implement mouse input involving grid-calculations. Very useful when the display
 * size or position does not match with the internal resolution of a simulation. Also, useful for board game gui 
 * implementation, since this class handles pixel-coordinate to grid-coordinate calculations.
 * 
 * @author David Wu
 * @version 1.0, November 5, 2007
 */

public abstract class MouseGridPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener
{
	private int myXOffset;
	private int myYOffset;
	private int myXUnit;
	private int myYUnit;

	private boolean doDoubleBuffer;
	
	private BufferedImage myOnImage;
	private BufferedImage myOffImage;
	
	private static final Stroke THICKSTROKE = new BasicStroke(3);
	private static final Stroke THINSTROKE = new BasicStroke(1);
	
	public MouseGridPanel(boolean doubleBuffer)
	{
		this(1,1,1,1,1,1,doubleBuffer);
	}
	
	public MouseGridPanel(
			int xPixel, int yPixel, 
			int xOffset, int yOffset, 
			int xUnit, int yUnit,
			boolean doubleBuffer)
	{
		setSize(new Dimension(xPixel, yPixel));
		setPreferredSize(new Dimension(xPixel, yPixel));
		setMinimumSize(new Dimension(1, 1));
		
		myXOffset = xOffset;
		myYOffset = yOffset;
		myXUnit = xUnit;
		myYUnit = yUnit;
		
		doDoubleBuffer = doubleBuffer;
		regenImages();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
	}
	
	public int getXPixel()
	{return getWidth();}
	
	public int getYPixel()
	{return getHeight();}
	
	public int getXOffset()
	{return myXOffset;}
	
	public int getYOffset()
	{return myYOffset;}

	public int getXUnit()
	{return myXUnit;}
	
	public int getYUnit()
	{return myYUnit;}
	
	public void setPixel(int xPixel, int yPixel)
	{setSize(xPixel, yPixel);}
	
	public void setOffset(int xOffset, int yOffset)
	{
		myXOffset = xOffset;
		myYOffset = yOffset;
	}
	
	public void setUnit(int xUnit, int yUnit)
	{
		myXUnit = xUnit;
		myYUnit = yUnit;
	}
	
	public void addOffset(int dx, int dy)
	{
		myXOffset += dx;
		myYOffset += dy;
	}
	
	public void resizeToBoard(int xGrid, int yGrid, int minXBorder, int minYBorder, boolean beSquare)
	{
		int xGridSize = getWidth() - minXBorder * 2;
		int yGridSize = getHeight() - minYBorder * 2;
		if(xGridSize <=0)
		{xGridSize = 1;}
		if(yGridSize <=0)
		{yGridSize = 1;}
		
		myXUnit = xGridSize / xGrid;
		myYUnit = yGridSize / yGrid;
		
		if(myXUnit <= 0)
		{myXUnit = 1;}
		if(myYUnit <= 0)
		{myYUnit = 1;}
		
		if(beSquare)
		{
			int min = Math.min(myXUnit, myYUnit);
			myXUnit = min;
			myYUnit = min;
		}
		
		int xBorder = (getWidth() - xGrid*myXUnit)/2;
		int yBorder = (getHeight() - yGrid*myYUnit)/2;
		
		myXOffset = xBorder;
		myYOffset = yBorder;
	}
	
	public void resizeAroundCenter(int xUnit, int yUnit)
	{
		int xPC = getWidth()/2;
		int yPC = getHeight()/2;
		
		int xGC = getXGrid(xPC);
		int yGC = getYGrid(yPC);
		int xSC = getXSubGrid(xPC);
		int ySC = getYSubGrid(yPC);
		
		myXUnit = xUnit;
		myYUnit = yUnit;
		
		int xP = getXPixel(xGC,xSC);
		int yP = getYPixel(yGC,ySC);
		
		myXOffset += xPC-xP;
		myYOffset += yPC-yP;	
	}
	
	private void regenImages()
	{
		int x = Math.max(getWidth(),1);
		int y = Math.max(getHeight(),1);
		myOnImage = new BufferedImage(x,y, BufferedImage.TYPE_4BYTE_ABGR);
		if(doDoubleBuffer)
		{myOffImage = new BufferedImage(x,y, BufferedImage.TYPE_4BYTE_ABGR);}
	}
	
	protected void flipBuffer()
	{
		if(!doDoubleBuffer)
		{return;}
		BufferedImage temp = myOnImage;
		myOnImage = myOffImage;
		myOffImage = temp;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(myOnImage,0,0,null);
	}
	
	public Graphics2D getDrawGraphics()
	{
		if(!doDoubleBuffer)
		{return myOnImage.createGraphics();}
		return myOffImage.createGraphics();
	}
	
	public BufferedImage getDrawImage()
	{
		if(!doDoubleBuffer)
		{return myOnImage;}
		return myOffImage;
	}
	
	public void drawGrid(Graphics2D g, Color lineColor, int xGrid0, int yGrid0, int xGrid1, int yGrid1)
	{drawGrid(g, lineColor, THINSTROKE, THICKSTROKE, 0, 0, xGrid0, yGrid0, xGrid1, yGrid1);}
	
	public void drawGrid(Graphics2D g, Color lineColor, 
			Stroke lineStroke, Stroke borderStroke, 
			int xSubPixel, int ySubPixel,
			int xGrid0, int yGrid0, int xGrid1, int yGrid1)
	{
		Stroke s = g.getStroke();
		Color c = g.getColor();
		
		g.setStroke(lineStroke);
		g.setColor(lineColor);
		
		int x0Pixel = getXPixel(xGrid0);
		int y0Pixel = getYPixel(yGrid0);
		int x1Pixel = getXPixel(xGrid1);
		int y1Pixel = getYPixel(yGrid1);
		
		for(int x = xGrid0; x<xGrid1; x++)
		{
			int xPixel = getXPixel(x) + xSubPixel;
			g.drawLine(xPixel, y0Pixel, xPixel, y1Pixel);
		}
		
		for(int y = yGrid0; y<yGrid1; y++)
		{
			int yPixel = getYPixel(y) + ySubPixel;
			g.drawLine(x0Pixel, yPixel, x1Pixel, yPixel);
		}
		
		//paint border square of board
		g.setStroke(borderStroke);
		g.drawRect(x0Pixel, y0Pixel, x1Pixel - x0Pixel, y1Pixel - y0Pixel);
		
		g.setStroke(s);
		g.setColor(c);
	}
	
	public double getXGridD(int xPixel)
	{
		return (double)(xPixel - myXOffset) / myXUnit;
	}
	
	public double getYGridD(int yPixel)
	{
		return (double)(yPixel - myYOffset) / myYUnit;
	}
	
	public int getXGrid(int xPixel)
	{
		return (xPixel - myXOffset - getXSubGrid(xPixel)) / myXUnit;
	}
	
	public int getXSubGrid(int xPixel)
	{
		return ((xPixel - myXOffset)%myXUnit + myXUnit)%myXUnit;
	}
	
	public int getYGrid(int yPixel)
	{
		return (yPixel - myYOffset - getYSubGrid(yPixel)) / myYUnit;
	}
	
	public int getYSubGrid(int yPixel)
	{
		return ((yPixel - myYOffset)%myYUnit + myYUnit)%myYUnit;
	}
	
	public int getXPixel(double xGrid)
	{
		return (int)(xGrid*myXUnit + myXOffset);
	}
	
	
	public int getYPixel(double yGrid)
	{
		return (int)(yGrid*myYUnit + myYOffset);
	}
	
	public int getXPixel(int xGrid)
	{
		return xGrid*myXUnit + myXOffset;
	}
	
	public int getYPixel(int yGrid)
	{
		return yGrid*myYUnit + myYOffset;
	}
	
	public int getXPixel(int xGrid, int xSubGrid)
	{
		return xGrid*myXUnit + myXOffset + xSubGrid;
	}
	
	public int getYPixel(int yGrid, int ySubGrid)
	{
		return yGrid*myYUnit + myYOffset + ySubGrid;
	}
	
	public void componentResized(ComponentEvent e)
	{regenImages();}
	
	public void componentShown(ComponentEvent e)
	{}
	
	public void componentMoved(ComponentEvent e)
	{}
	
	public void componentHidden(ComponentEvent e)
	{}
	
	public void mouseDragged(MouseEvent e) 
	{}

	public void mouseMoved(MouseEvent e) 
	{}

	public void mouseClicked(MouseEvent e) 
	{}

	public void mouseEntered(MouseEvent e) 
	{}
	
	public void mouseExited(MouseEvent e) 
	{}

	public void mousePressed(MouseEvent e) 
	{}

	public void mouseReleased(MouseEvent e) 
	{}
	
}
