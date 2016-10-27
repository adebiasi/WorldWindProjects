package it.listeners;




import it.SharedVariables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.globes.FlatGlobe;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.SkyColorLayer;
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.airspaces.CappedCylinder;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;

public class KeyOption implements KeyListener{
	
	
	static public  WorldWindow wwd = null; 
	AirspaceLayer deformedAirspaces;
	static private int  defIndex;
	static private int  maxIndex=100;
	
	static public int idFunction = 1;
	
    public KeyOption(WorldWindow wwd, AirspaceLayer deformedAirspaces)
    {       
        wwd = wwd;        
        this.deformedAirspaces= deformedAirspaces;
        defIndex=maxIndex/2;
        
       // SharedVariables.distCoeff = (double) 0.4;	
		SharedVariables.distCoeff2= SharedVariables.calculateDistParam2(SharedVariables.distCoeff);
		
    }
    
	
    

	@Override
	public void keyPressed(KeyEvent e) {
		
		
		if(e.getKeyChar()=='1'){
			idFunction = 1;
		}
		
if(e.getKeyChar()=='2'){
			idFunction = 2;
		}

if(e.getKeyChar()=='3'){
	idFunction = 3;
}

if(e.getKeyChar()=='4'){
	idFunction = 4;
}
if(e.getKeyChar()=='5'){
	idFunction = 5;
}

if(e.getKeyChar()=='6'){
	idFunction = 6;
}

		if(e.getKeyChar()=='a'){
			System.out.println("press a");
			defIndex++;			
			SharedVariables.distCoeff = (double) KeyOption.defIndex / (KeyOption.maxIndex - 1);			
			System.out.println("alpha: "+SharedVariables.distCoeff+", beta: "+SharedVariables.distCoeff2);
		}
		
		
		if(e.getKeyChar()=='z'){
			System.out.println("press z");
			defIndex--;			
			SharedVariables.distCoeff = (double) KeyOption.defIndex / (KeyOption.maxIndex - 1);			
			System.out.println("alpha: "+SharedVariables.distCoeff+", beta: "+SharedVariables.distCoeff2);
		}
		
		if(e.getKeyChar()=='d'){
			System.out.println("press d for coeff2");
			SharedVariables.distCoeff2=SharedVariables.distCoeff2+0.025;
			System.out.println("alpha: "+SharedVariables.distCoeff+", beta: "+SharedVariables.distCoeff2);
		}
		
		
		if(e.getKeyChar()=='c'){
			System.out.println("press c for coeff2");
			SharedVariables.distCoeff2=SharedVariables.distCoeff2-0.025;
			System.out.println("alpha: "+SharedVariables.distCoeff+", beta: "+SharedVariables.distCoeff2);
		}
		
		if(e.getKeyChar()=='x'){
			System.out.println("press x");
		SharedVariables.lensIsActive=!SharedVariables.lensIsActive;
	//	System.out.println("distCoeff2: "+SharedVariables.distCoeff2);
		}
		
		
		if(e.getKeyChar()=='.'){
			System.out.println("press .");
			defIndex++;
			updateCoeff();
			//SharedVariables.distCoeff = (double) KeyOption.defIndex / (KeyOption.maxIndex - 1);		
			//SharedVariables.distCoeff2= SharedVariables.calculateDistParam2(SharedVariables.distCoeff);
			//System.out.println("alpha: "+SharedVariables.distCoeff+", beta: "+SharedVariables.distCoeff2);
		}
		if(e.getKeyChar()==','){
			System.out.println("press ,");
			defIndex--;
			updateCoeff();
			
		}
		if(e.getKeyChar()=='p'){
			System.out.println("press p");
			SharedVariables.lense_h+=5;
			SharedVariables.lense_w+=5;
			SharedVariables.currentAnnotation.getAttributes().setSize(new Dimension((int)SharedVariables.lense_w, (int)SharedVariables.lense_h));
			SharedVariables.currentAnnotation.getAttributes().setDrawOffset(new Point(0, -(int)(SharedVariables.lense_h/2)));
		}
		
		
		if(e.getKeyChar()=='o'){
			System.out.println("press o");
			SharedVariables.lense_h-=5;
			SharedVariables.lense_w-=5;
			SharedVariables.currentAnnotation.getAttributes().setSize(new Dimension((int)SharedVariables.lense_w, (int)SharedVariables.lense_h));
			SharedVariables.currentAnnotation.getAttributes().setDrawOffset(new Point(0, -(int)(SharedVariables.lense_h/2)));
		}
		
			wwd.redraw();
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	
	private void updateCoeff(){
		SharedVariables.distCoeff = (double) KeyOption.defIndex / (KeyOption.maxIndex - 1);	
		SharedVariables.distCoeff2= SharedVariables.calculateDistParam2(SharedVariables.distCoeff);
		System.out.println("alpha: "+SharedVariables.distCoeff+", beta: "+SharedVariables.distCoeff2);
	}
	
}
