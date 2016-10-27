/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.sceneController;

import gov.nasa.worldwind.render.DrawContext;

/**
 * @author Tom Gaskins
 * @version $Id: BasicSceneController.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class MyBasicSceneController extends AbstractSceneController
{
    public void doRepaint(DrawContext dc)
    {
    	
    	//double start = System.nanoTime();
    	
        this.initializeFrame(dc);
        try
        {
            this.applyView(dc);
            this.createPickFrustum(dc);
            this.createTerrain(dc);
            this.preRender(dc);
            this.clearFrame(dc);
            this.pick(dc);
            this.clearFrame(dc);
            this.draw(dc);
        }
        finally
        {
            this.finalizeFrame(dc);
          //  double elapsed = System.nanoTime() - start;
          //  double seconds = (double)elapsed / 1000000000.0;
          //  System.out.println("time for a repaint: "+seconds+", FPS: "+(1.0/seconds));
        }
    }
}
