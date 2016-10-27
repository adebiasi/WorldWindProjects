package it.graphitech.smeSpire;


import it.graphitech.GeneratorOfRenderableObjects;
import it.graphitech.smeSpire.entry.Entry;
import it.graphitech.smeSpire.framebuffer.MyBasicOrbitView;
import it.graphitech.smeSpire.framebuffer.MyFlatOrbitView;
import it.graphitech.smeSpire.layers.GlobeAnnotation;

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
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.SkyColorLayer;
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.airspaces.CappedCylinder;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;

public class FlatGlobeOption implements KeyListener{
	
	
	static public  WorldWindow wwd = null; 
	boolean isFlat=false;
	private Globe roundGlobe;
    private FlatGlobe flatGlobe;
	
    public FlatGlobeOption(WorldWindow wwd)
    {
       
        this.wwd = wwd;
       
        {
            this.flatGlobe = new EarthFlat();
            this.roundGlobe = wwd.getModel().getGlobe();
        }
       
    }
    
	public void enableFlatGlobe(boolean flat)
    {
       
if(wwd.getView() instanceof MyFlatOrbitView)
       // if(!flat)
        {
            // Switch to round globe
            wwd.getModel().setGlobe(roundGlobe) ;
            // Switch to orbit view and update with current position
            MyFlatOrbitView flatOrbitView = (MyFlatOrbitView)wwd.getView();
            MyBasicOrbitView orbitView = new MyBasicOrbitView();
            orbitView.setCenterPosition(flatOrbitView.getCenterPosition());
            orbitView.setZoom(flatOrbitView.getZoom( ));
            orbitView.setHeading(flatOrbitView.getHeading());
            orbitView.setPitch(flatOrbitView.getPitch());
           
            orbitView.setGlobe(roundGlobe);
            
            wwd.setView(orbitView);
            
           
            
            // Change sky layer
            LayerList layers = wwd.getModel().getLayers();
            for(int i = 0; i < layers.size(); i++)
            {
                if(layers.get(i) instanceof SkyColorLayer)
                    layers.set(i, new SkyGradientLayer());
            }
        }
        else
        {
            // Switch to flat globe
            wwd.getModel().setGlobe(flatGlobe);
           // flatGlobe.setProjection(this.getProjection());
            // Switch to flat view and update with current position
           
            
            
            MyBasicOrbitView orbitView = (MyBasicOrbitView)wwd.getView();
            MyFlatOrbitView flatOrbitView = new MyFlatOrbitView();
            flatOrbitView.setCenterPosition(orbitView.getCenterPosition());
            flatOrbitView.setZoom(orbitView.getZoom( ));
            flatOrbitView.setHeading(orbitView.getHeading());
            flatOrbitView.setPitch(orbitView.getPitch());
            
            flatOrbitView.setGlobe(flatGlobe);
        	
            
            
            wwd.setView(flatOrbitView);
            
            
        
            // Change sky layer
            LayerList layers = wwd.getModel().getLayers();
            for(int i = 0; i < layers.size(); i++)
            {
                if(layers.get(i) instanceof SkyGradientLayer)
                    layers.set(i, new SkyColorLayer());
            }
        }
        
/*
	String dataPath = "./src/data/test1.csv";
	ArrayList<String> entries = new ArrayList<String>();
	HashMap<String, Entry> city_to_city = new HashMap<String, Entry>();
    Main_TEST.AppFrame.loadFlowsDataFromFILE(dataPath,entries,city_to_city); 
    
   	 new GeneratorOfRenderableObjects(wwd,  entries,  city_to_city, "all","alla");
     new GeneratorOfRenderableObjects(wwd,  entries,  city_to_city, "340","340a");
  */   
     
     updateWW();
     
     
    }

	private void updateWW(){
		wwd.redraw();
	     //SharedVariables.frame.getLayerPanel().update(wwd);
	     //SharedVariables.frame.pack();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
		//System.out.println("char: "+e.getKeyChar());
		
		if(e.getKeyChar()==' '){
			System.out.println("press space");
			SharedVariables.pos= SharedVariables.computePositionFromWorldCoordinates(wwd.getView().getCenterPoint());
			Vec4 current = SharedVariables.computeScreenCoordinates(wwd.getView().getCenterPoint());        					
			SharedVariables.screenPoint=current;
			((GlobeAnnotation)SharedVariables.currentAnnotation).setPosition(SharedVariables.pos);
			updateWW();
		}
		
		
		if(e.getKeyChar()=='f'){
			System.out.println("press F");
			isFlat=!isFlat;
			enableFlatGlobe(isFlat);
	//		int size = wwd.getModel().getLayers().size();
	//		 System.out.println("size 1: "+size);
		//	 Main_STANDALONE_v3.AppFrame.removeLayers();
	//		Main_STANDALONE_v3.AppFrame.createLayers(SharedVariables.wwd);
//			Main_STANDALONE_v3.AppFrame.layerPanel.update(wwd);
//			  size = wwd.getModel().getLayers().size();
//			 System.out.println("size 2: "+size);
		}
		
		
		
		
		if(e.getKeyChar()=='l'){
			
				wwd.getModel().getLayers().getLayerByName("Stars").setEnabled(false);
			wwd.getModel().getLayers().getLayerByName("Place Names").setEnabled(false);
			
			
			
			
		}
		
		if(e.getKeyChar()=='d'){
			System.out.println("press D");
			SharedVariables.showIntermediatePoint=!SharedVariables.showIntermediatePoint;
			updateWW();
		}
		
		if(e.getKeyChar()=='m'){
			System.out.println("press m");
			SharedVariables.lineWidth+=1.0;
			System.out.println("spessore: "+SharedVariables.lineWidth);
			updateWW();
		}
		if(e.getKeyChar()=='n'){
			System.out.println("press n");
			SharedVariables.lineWidth-=1.0;
			System.out.println("spessore: "+SharedVariables.lineWidth);
			updateWW();
		}
		
		if(e.getKeyChar()=='v'){
			System.out.println("press v");
			SharedVariables.lineOfInterestWidth+=1.0;
			System.out.println("spessore: "+SharedVariables.lineWidth);
			updateWW();
		}
		if(e.getKeyChar()=='b'){
			System.out.println("press b");
			SharedVariables.lineOfInterestWidth-=1.0;
			System.out.println("spessore: "+SharedVariables.lineWidth);
			updateWW();
		}
		
		if(e.getKeyChar()=='p'){
			System.out.println("press p");
			SharedVariables.lense_h+=5;
			SharedVariables.lense_w+=5;
			SharedVariables.currentAnnotation.getAttributes().setSize(new Dimension((int)SharedVariables.lense_w, (int)SharedVariables.lense_h));
			SharedVariables.currentAnnotation.getAttributes().setDrawOffset(new Point(0, -(int)(SharedVariables.lense_h/2)));
			updateWW();
		}
		
		
		if(e.getKeyChar()=='o'){
			System.out.println("press o");
			SharedVariables.lense_h-=5;
			SharedVariables.lense_w-=5;
			SharedVariables.currentAnnotation.getAttributes().setSize(new Dimension((int)SharedVariables.lense_w, (int)SharedVariables.lense_h));
			SharedVariables.currentAnnotation.getAttributes().setDrawOffset(new Point(0, -(int)(SharedVariables.lense_h/2)));
			updateWW();
		}
		
		if(e.getKeyChar()=='z'){
			SharedVariables.maxDistance+=10;
			System.out.println("SectorManager.deltaDistance: "+SharedVariables.maxDistance);
			updateWW();
		}
		if(e.getKeyChar()=='x'){
			SharedVariables.maxDistance-=10;
			System.out.println("SectorManager.deltaDistance: "+SharedVariables.maxDistance);
			updateWW();
		}
		
		
		if(e.getKeyChar()=='s'){
			System.out.println("press s");
			SharedVariables.revealEdgeStructure=!SharedVariables.revealEdgeStructure;
			updateWW();
		}
		if(e.getKeyChar()=='a'){
			System.out.println("press a");
			SharedVariables.lensIsActive=!SharedVariables.lensIsActive;
			updateWW();
		}
		if(e.getKeyChar()=='r'){
			System.out.println("press r");
			SharedVariables.wwd.getView().setEyePosition(Position.fromDegrees(0, 0,10000000));
			updateWW();
		}
		if(e.getKeyChar()=='t'){
			System.out.println("press t");
			SharedVariables.showTextures=!SharedVariables.showTextures;
			updateWW();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	//	System.out.println("press key");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("press key");
	}
	
	
	
	
}
