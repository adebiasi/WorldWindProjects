/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.smeSpire.framebuffer;

import java.awt.Rectangle;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.ViewInputHandler;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.view.BasicView;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.BasicOrbitViewLimits;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewCollisionSupport;
import gov.nasa.worldwind.view.orbit.OrbitViewInputHandler;
import gov.nasa.worldwind.view.orbit.OrbitViewInputSupport;
import gov.nasa.worldwind.view.orbit.OrbitViewLimits;

import javax.media.opengl.GL;

/**
 * @author dcollins
 * @version $Id: BasicOrbitView.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class MyBasicOrbitView extends BasicOrbitView
{

	public MyBasicOrbitView() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public void test(Frustum f){
		this.frustum=f;
		updateModelViewStateID();
	}
	
	
	
	
	@Override
	public void setFarClipDistance(double arg0) {
		// TODO Auto-generated method stub
		super.setFarClipDistance(arg0);
	}
	
	
	
@Override
	public double computeFarClipDistance() {
		// TODO Auto-generated method stub
	//return (Double.MAX_VALUE);
	
	//double res= super.computeFarClipDistance()*4;
	double res= super.computeFarClipDistance()*2.5;
	//double res= super.computeFarClipDistance();
	//System.out.println("computeFarClipDistance: "+res);
		return res;
	}

@Override
public double computeNearClipDistance() {
	// TODO Auto-generated method stub
//return (Double.MAX_VALUE);

//double res= super.computeFarClipDistance()*4;
double res= super.computeNearClipDistance()*0.1;
//double res= super.computeFarClipDistance();
//System.out.println("computeFarClipDistance: "+res);
	return res;
}


public void setViewPort(Rectangle r){
	viewport=r;
	updateModelViewStateID();
}


public void update(DrawContext dc){
	//updateModelViewStateID();
	//System.out.println("farDistance: "+this.getFarClipDistance());
	//System.out.println("update");
	doApply(dc);
}
}
