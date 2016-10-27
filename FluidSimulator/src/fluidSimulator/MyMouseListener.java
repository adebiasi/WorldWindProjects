package fluidSimulator;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class MyMouseListener implements MouseListener, MouseMotionListener, KeyListener{

	WorldWindowGLCanvas wwd;
	
	private int lastX;
	private int lastY;
	
	public MyMouseListener(WorldWindowGLCanvas wwd) {
		super();
		this.wwd = wwd;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int button = e.getButton();
		if(button == MouseEvent.BUTTON1)
		{
		Position pos = wwd.getCurrentPosition();
		
		if(Variables.isInsideArea(pos)){
			e.consume();
		}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	static boolean  button1;
	static boolean  button2;
	
	@Override
	public void mousePressed(MouseEvent e) {
	//	System.out.println("mousePressed");
		// TODO Auto-generated method stub
		
		
		int button = e.getButton();
		if(button == MouseEvent.BUTTON1)
		{
		Position pos = wwd.getCurrentPosition();
		
		if(Variables.isInsideArea(pos)){
			Point p = Variables.getXy(pos);
			System.out.println(p);
			//Variables.inputPointList.add(p);
			addi(p);
			lastX = (int)e.getX();
			lastY = (int)e.getY();
			button1 = true;
		//	System.out.println("premo:"+button1);
			e.consume();
		}
		
		}
		
		if(button == MouseEvent.BUTTON3)
		{
		Position pos = wwd.getCurrentPosition();
		
		if(Variables.isInsideArea(pos)){
			//Point p = Variables.getXy(pos);
			//System.out.println(p);
			//Variables.inputPointList.add(p);
			//addi(p);
			lastX = (int)e.getX();
			lastY = (int)e.getY();
			button2 = true;
		//	System.out.println("premo:"+button1);
			e.consume();
		}
		
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		int button = e.getButton();
		if(button == MouseEvent.BUTTON1)
		{
			
			button1 = false;
		//	System.out.println("rilascio: "+button1);
			e.consume();
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//System.out.println("mouseDragged");
		
		// TODO Auto-generated method stub
		
		if(button1){
		
Position pos = wwd.getCurrentPosition();
		
		if(Variables.isInsideArea(pos)){
			Point p = Variables.getXy(pos);
	
			//System.out.println("inside");
		//System.out.println("draggo: "+button1);
	//	if(button1)
		{
	
			addi(p);
				int dx = e.getX() - lastX;
			int dy = e.getY() - lastY;
			
			
			
			addv(p, dx, dy);
			
		}
		
		lastX = e.getX();
		lastY = e.getY();
		e.consume();
		}
		}
		if(button2){
			
			Position pos = wwd.getCurrentPosition();
					
					if(Variables.isInsideArea(pos)){
						Point p = Variables.getXy(pos);
				
						//System.out.println("inside");
					//System.out.println("draggo: "+button1);
				//	if(button1)
					{
				
						//addi(p);
							int dx = e.getX() - lastX;
						int dy = e.getY() - lastY;
						
						
						
						addv(p, dx, dy);
						
					}
					
					lastX = e.getX();
					lastY = e.getY();
					e.consume();
					}
					}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	//System.out.println("moved");

	}
	public void addi(Point p)
	{
		//Variables.inputPointList.add(new Point(p.x+1,p.y+1));
		fluid.setDensSrc(p.x, p.y, Math.random());
		
	}
	
	public void addv(Point p, int xMag, int yMag)
	{
		fluid.setVelSrc(p.x, p.y, xMag, yMag);
		
	}

	@Override
	public void keyPressed(KeyEvent k) {
		// TODO Auto-generated method stub
		//System.out.println(k.getKeyChar());
		if(k.getKeyChar()=='r'){
			fluid.reset();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
