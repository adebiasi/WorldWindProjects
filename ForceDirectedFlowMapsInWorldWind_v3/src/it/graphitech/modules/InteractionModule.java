package it.graphitech.modules;

import it.graphitech.worldwind.NodeDragger;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.util.BasicDragger;


public class InteractionModule  {

	protected boolean enabled = true;
	WorldWindowGLCanvas wwd;
	 protected NodeDragger dragger;
	 
	 
	public InteractionModule(WorldWindowGLCanvas wwd) {
		this.wwd=wwd;
		this.initializeSelectionMonitoring();
	}
	
	 public void initializeSelectionMonitoring()
     {
         this.dragger = new NodeDragger(this.getWwd());
         this.getWwd().addSelectListener(new SelectListener()
         {
             public void selected(SelectEvent event)
             {
                 
                 // Have drag events drag the selected object.
                  if (event.getEventAction().equals(SelectEvent.DRAG_END)
                     || event.getEventAction().equals(SelectEvent.DRAG))
                 {
                 	//System.out.println("dragging event");
                     // Delegate dragging computations to a dragger.
                     dragger.selected(event);

                 }
             }
         });
     }
	
	 
	 public WorldWindow getWwd()
     {
         return wwd;
     }

}
