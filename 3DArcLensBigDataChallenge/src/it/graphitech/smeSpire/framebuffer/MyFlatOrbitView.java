/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.smeSpire.framebuffer;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;

/**
 * @author Patrick Muris
 * @version $Id: FlatOrbitView.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class MyFlatOrbitView extends FlatOrbitView
{
   

    public MyFlatOrbitView()
    {
    	super();
    }


public void update(DrawContext dc){
	//updateModelViewStateID();
	doApply(dc);
}
}
