package it.listeners;

import it.SharedVariables;
import it.layers.GlobeAnnotation;
import gov.nasa.worldwind.Movable;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.util.BasicDragger;

public class LensListener {
	protected BasicDragger dragger;
    public void initializeSelectionMonitoring()
    {
        this.dragger = new BasicDragger(SharedVariables.wwd);
    
        
        SharedVariables.wwd.addPositionListener(new PositionListener() {
			
			@Override
			public void moved(PositionEvent pe) {
				
				
				// TODO Auto-generated method stub
				//Point sp = pe.getScreenPoint();
				//System.out.println("position POINT: "+pe.getPosition());
				
				Globe globe = SharedVariables.wwd.getModel().getGlobe();        					
				Vec4 globePoint = globe.computePointFromPosition(SharedVariables.pos);
				Vec4 current = SharedVariables.computeScreenCoordinates(globePoint);        					
				SharedVariables.screenPoint=current;
			}
		});
        
    
   
        SharedVariables.wwd.addSelectListener(new SelectListener()
        {
            public void selected(SelectEvent event)
            {
            
                  // Have rollover events highlight the rolled-over object.
                if (event.getEventAction().equals(SelectEvent.ROLLOVER) && !dragger.isDragging())
                {
                }
                // Have drag events drag the selected object.
                else if (event.getEventAction().equals(SelectEvent.DRAG_END)
                    || event.getEventAction().equals(SelectEvent.DRAG))
                {
                //	System.out.println("dragging event");
                    // Delegate dragging computations to a dragger.
                   
                	if(
                			//(isAirspace(event.getTopObject()))||
                			(isAnnotation(event.getTopObject()))){
                	
                		//if(isAirspace(event.getTopObject())){
                		dragger.selected(event);
                		//}
                	
                	PickedObjectList pol = SharedVariables.wwd.getObjectsAtCurrentPosition();
                    if (pol != null)
                    {
                    	Movable dragObject = (Movable) event.getTopObject();
                    	 Position refPos = dragObject.getReferencePosition();
//                        
               //     	Marker dragObject = (Marker) event.getTopObject();
               //    	 Position refPos = dragObject.getPosition();
//                       
                    	
                    	 if (refPos == null){
                          System.out.println("ref pos null");
                        	 return;
                         }
                        // Vec4 refPoint = getWwd().getModel().getGlobe().computePointFromPosition(refPos);
                      //   System.out.println("update pos");
                    	SharedVariables.pos=refPos;                           
                    	Globe globe = SharedVariables.wwd.getModel().getGlobe();        					
    					Vec4 globePoint = globe.computePointFromPosition(SharedVariables.pos);
    					Vec4 current = SharedVariables.computeScreenCoordinates(globePoint);        					
    					SharedVariables.screenPoint=current;
                    }
                	
                    // We missed any roll-over events while dragging, so highlight any under the cursor now,
                    // or de-highlight the dragged shape if it's no longer under the cursor.
                    if (event.getEventAction().equals(SelectEvent.DRAG_END))
                    {
                         pol = SharedVariables.wwd.getObjectsAtCurrentPosition();
                        if (pol != null)
                        {
                        	Movable dragObject = (Movable) event.getTopObject();
                        	 Position refPos = dragObject.getReferencePosition();
                         	 
                             if (refPos == null)
                                 return;
                  
                        	SharedVariables.pos=refPos;
                       //      getWwd().redraw();
                        }
                    }
                    
                	}
                }
                /*
                else if (event.getEventAction().equals(SelectEvent.LEFT_CLICK))
                {
                	
                	
                	
                	System.out.println(SharedVariables.wwd.getCurrentPosition());
                }
                */
            }
        });
    }
    
    
    protected boolean isAnnotation(Object o){
    	if (o instanceof GlobeAnnotation)
        {
    		return true;
        }
    	return false;
    }
    
   
    
    protected boolean isAirspace(Object o){
    	if (o instanceof Airspace)
        {
    		return true;
        }
    	return false;
    }
    
}
