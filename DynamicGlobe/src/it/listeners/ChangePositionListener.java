package it.listeners;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;


import it.Operations;
import it.SharedVariables;
import it.ManageRendObjects.ManageAirspaces;
import it.main.MainDeformableGlobe;
import it.rendLayers.MyRenderableLayer;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class ChangePositionListener implements PositionListener,RenderingListener{

	WorldWindowGLCanvas wwd;
	
	 AirspaceLayer deformedAirspaces;
	 MyRenderableLayer gridLayer;
	 
	public ChangePositionListener(WorldWindowGLCanvas wwd,MyRenderableLayer gridLayer,AirspaceLayer deformedAirspaces) {
	this.wwd = wwd;
	this.gridLayer=gridLayer;
	this.deformedAirspaces=deformedAirspaces;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void moved(PositionEvent arg0) {

		updateScene();
 
	}

	@Override
	public void stageChanged(RenderingEvent event) {
		// TODO Auto-generated method stub
		if (event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP)){
		updateScene();
		}
	}

	
	private void updateScene(){
		Operations.removeRenderGrids(gridLayer, deformedAirspaces);
	 	
		
		Position eyePosOverGlobe =SharedVariables.getEyePositionOverTheGlobe();
	 //USE LENS
		Position focusPos =SharedVariables.pos;
    	//System.out.println("eyePos: "+eyePos);
    	if(SharedVariables.lensIsActive){   
 Operations.generateGridsForEachPoi(eyePosOverGlobe,focusPos, wwd);
  Operations.renderDeformableSurfacesFromGrids(gridLayer,deformedAirspaces,gridLayer.getGlsl_trans());
    	}
	}
	
	
}
