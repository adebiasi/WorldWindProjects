package it.listeners;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;


import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class MouseMovement implements MouseListener{

	WorldWindowGLCanvas wwd;
	 AirspaceLayer airspacesLayer;
	 AirspaceLayer deformedAirspaces;
	 RenderableLayer gridLayer;
	 
	public MouseMovement(WorldWindowGLCanvas wwd, AirspaceLayer airspacesLayer,RenderableLayer gridLayer,AirspaceLayer deformedAirspaces) {
	this.wwd = wwd;
	this.airspacesLayer = airspacesLayer;
	this.gridLayer=gridLayer;
	this.deformedAirspaces=deformedAirspaces;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("EVENT");
     	 
//		 Operations.removeRenderGrids(gridLayer, deformedAirspaces);
//		 
//     	 Position p = wwd.getView().getEyePosition();
//     	// Position p = Position.fromDegrees(143, 43,0);     
//     	Position p2 =Position.fromDegrees(p.latitude.degrees, p.longitude.degrees, 0);
//     	 
//     	 System.out.println("position: "+p2);
//     Vec4 normal = wwd.getView().getGlobe().computeSurfaceNormalAtLocation(p.latitude, p.longitude);
//     System.out.println("NORMAL: "+normal);	
//     
//  boolean isVisible=  Operations.isPositionVisible(wwd, wwd.getModel().getGlobe().computePointFromLocation(p));
// 
//  Operations.findPointOverHorizon(p2, wwd);
//  
//  
//  Operations.renderGrids(gridLayer,deformedAirspaces);
//  
//  System.out.println("IS VISIBLE: "+isVisible);
//  
//     RenderableObjects.generateAirspaces(p2, airspacesLayer, Color.RED);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
 
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
      
      
     // 	wwd.redraw();
     // 	wwd.redrawNow();
	}

	
	
	
}
