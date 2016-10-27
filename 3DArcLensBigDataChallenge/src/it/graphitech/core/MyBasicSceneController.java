/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.core;



import it.graphitech.smeSpire.buffers.FBOManager;
import gov.nasa.worldwind.AbstractSceneController;
import gov.nasa.worldwind.render.DrawContext;

/**
 * @author Tom Gaskins
 * @version $Id: BasicSceneController.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class MyBasicSceneController extends MyAbstractSceneController
{
	
	
static public FBOManager fboManager = new FBOManager();
static public String[] layerNames = {
	//"FLAT MAP: meno di ~600 arcs","meno di ~600 arcs","~600 arcs", "1 arc", "pochi archi","2969 arcs",
	//"~30000 arcs","~13000 arcs","FLAT MAP: ~600 arcs","longdistance arcs","FLAT MAP: longdistance arcs",
	"BIG DATA CHALLENGE arcs","FLAT MAP: BIG DATA CHALLENGE arcs",
	//"BDC TIME MILANO arcs","FLAT MAP: BDC TIME MILANO arcs",
	"BDC TIME NAPOLI arcs","FLAT MAP: BDC TIME NAPOLI arcs"
	};	
//static public String[] layerNames = {"~600 arcs"};
//static public String[] layerNames = {"pochi archi"};

    public void doRepaint(DrawContext dc)
    {
    
    	fboManager.setup(dc);
    	
    	//fboManager.setup(dc);
    	
    	
        this.initializeFrame(dc);
        
        try
        {
       
        	
            this.applyView(dc);
            this.createPickFrustum(dc);
            this.createTerrain(dc);
            this.preRender(dc);
         
            this.clearFrame(dc);
         
            this.pick(dc);
            
            
      
         
       
           
        
			 MyBasicSceneController.fboManager.bindFramebufferObject_PointDetection(dc);
			 clearFrame(dc);			
			 MyBasicSceneController.fboManager.unbindFramebufferObject(dc);
            this.clearFrame(dc);
          
            
          
            //long tStart = System.currentTimeMillis();
            

          
            
            this.draw(dc);
            /*
            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;
            System.out.println("elapsedSeconds: "+elapsedSeconds);
            */
            //this.resetExternalBuffer(dc);
        }
        finally
        {
            this.finalizeFrame(dc);
          
        }
    }
    
    
    
    /*
    private void setExternalBuffer(DrawContext dc){
    	MainClass.fbs.renderOnExternalBuffer(dc, 1);	
    }
    
    private void resetExternalBuffer(DrawContext dc){
    	MainClass.fbs.restoreSituation(dc);	
    }
    */
}
