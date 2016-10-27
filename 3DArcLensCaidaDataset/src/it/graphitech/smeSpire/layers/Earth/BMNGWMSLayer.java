/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.smeSpire.layers.Earth;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Document;

/**
 * @author tag
 * @version $Id: BMNGWMSLayer.java 1958 2014-04-24 19:25:37Z tgaskins $
 */
public class BMNGWMSLayer extends WMSTiledImageLayer
{
    public BMNGWMSLayer()
    {
        super(getConfigurationDocument(), null);
    }

    protected static Document getConfigurationDocument()
    {
        return WWXML.openDocumentFile("config/Earth/BMNGWMSLayer2.xml", null);
    }

	@Override
	public void render(DrawContext dc) {
		
		
		
		if(this.isEnabled()){
			//dc.getGL().getGL2().glDisable(GL.GL_BLEND);
			//dc.getGL().getGL2().glColor4d(1.0d, 1.0d, 1.0d, 0.0d);
			super.render(dc);
			//dc.getGL().getGL2().glColor4d(1.0d, 1.0d, 1.0d, 1.0d);
			//dc.getGL().getGL2().glEnable(GL.GL_BLEND);
			
		}
    
	}
	
	/*
	 protected void clearFrame(DrawContext dc)
	    {
	        Color cc = dc.getClearColor();
	        dc.getGL().glClearColor(cc.getRed(), cc.getGreen(), cc.getBlue(), cc.getAlpha());
	        dc.getGL().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	    }
	
	 public void color(DrawContext dc, java.awt.Color color)
	    {
	        if (dc == null)
	        {
	            String message = Logging.getMessage("nullValue.DrawContextIsNull");
	            Logging.logger().severe(message);
	            throw new IllegalArgumentException(message);
	        }

	        if (color == null)
	        {
	            String message = Logging.getMessage("nullValue.ColorIsNull");
	            Logging.logger().severe(message);
	            throw new IllegalArgumentException(message);
	        }

	       

	        float[] compArray = new float[4];
	        color.getRGBComponents(compArray);
	        // Premultiply color components by the alpha component.
	        compArray[0] *= compArray[3];
	        compArray[1] *= compArray[3];
	        compArray[2] *= compArray[3];

	        GL2 gl = dc.getGL().getGL2();
	        gl.glClearColor(compArray[0], compArray[1], compArray[2], compArray[3]);
	        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	    }
	    */
}
