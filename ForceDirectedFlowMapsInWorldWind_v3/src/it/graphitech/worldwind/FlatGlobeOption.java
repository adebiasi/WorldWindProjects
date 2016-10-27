package it.graphitech.worldwind;

import it.graphitech.Variables;
import it.graphitech.render.cubicCurve.CubicSplinePolyline;
import it.graphitech.render.cubicCurve.RenderableControlPoints;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;
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
       

        if(!flat)
        {
            // Switch to round globe
            wwd.getModel().setGlobe(roundGlobe) ;
            // Switch to orbit view and update with current position
            FlatOrbitView flatOrbitView = (FlatOrbitView)wwd.getView();
            BasicOrbitView orbitView = new BasicOrbitView();
            orbitView.setCenterPosition(flatOrbitView.getCenterPosition());
            orbitView.setZoom(flatOrbitView.getZoom( ));
            orbitView.setHeading(flatOrbitView.getHeading());
            orbitView.setPitch(flatOrbitView.getPitch());
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
            BasicOrbitView orbitView = (BasicOrbitView)wwd.getView();
            FlatOrbitView flatOrbitView = new FlatOrbitView();
            flatOrbitView.setCenterPosition(orbitView.getCenterPosition());
            flatOrbitView.setZoom(orbitView.getZoom( ));
            flatOrbitView.setHeading(orbitView.getHeading());
            flatOrbitView.setPitch(orbitView.getPitch());
            wwd.setView(flatOrbitView);
            // Change sky layer
            LayerList layers = wwd.getModel().getLayers();
            for(int i = 0; i < layers.size(); i++)
            {
                if(layers.get(i) instanceof SkyGradientLayer)
                    layers.set(i, new SkyColorLayer());
            }
        }
        
        wwd.redraw();
    }

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("press key");
		if(e.getKeyChar()=='f'){
			System.out.println("press F");
			isFlat=!isFlat;
			enableFlatGlobe(isFlat);
		}
		
		if(e.getKeyChar()=='c'){
			System.out.println("press c");
			
			Position p1 = Position.fromDegrees(43, 11, 0);
			Position p2 = Position.fromDegrees(40, 5, 3000);
			
			//testClass.test(p1, p2, (wwd.getModel()));
			testClass.test2(p1, (wwd.getModel()));
			
			wwd.redraw();
			wwd.redrawNow();
		}
		if(e.getKeyChar()=='l'){
			
			//wwd.getModel().getLayers().getLayerByName("World Map").setEnabled(!wwd.getModel().getLayers().getLayerByName("World Map").isEnabled());
			//wwd.getModel().getLayers().getLayerByName("OpenStreetMap Cycle").setEnabled(!wwd.getModel().getLayers().getLayerByName("OpenStreetMap Cycle").isEnabled());
			wwd.getModel().getLayers().getLayerByName("Surface Images").setEnabled(!wwd.getModel().getLayers().getLayerByName("Surface Images").isEnabled());
			//wwd.getModel().getLayers().getLayerByName("NASA Blue Marble Image").setEnabled(!wwd.getModel().getLayers().getLayerByName("NASA Blue Marble Image").isEnabled());
			//wwd.getModel().getLayers().getLayerByName("Blue Marble (WMS) 2004").setEnabled(!wwd.getModel().getLayers().getLayerByName("Blue Marble (WMS) 2004").isEnabled());
			
			
			
			
			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("press key");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("press key");
	}
	
	
	
	
}
