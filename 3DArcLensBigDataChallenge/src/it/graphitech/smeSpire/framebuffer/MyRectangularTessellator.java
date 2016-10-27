/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.smeSpire.framebuffer;
/*
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.j2d.TextRenderer;
*/
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.geom.Cylinder;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.terrain.RectangularTessellator;
import gov.nasa.worldwind.terrain.SectorGeometry;
import gov.nasa.worldwind.terrain.SectorGeometryList;
import gov.nasa.worldwind.terrain.Tessellator;
import gov.nasa.worldwind.terrain.SectorGeometry.GeographicTextureCoordinateComputer;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import java.awt.*;
import java.nio.*;
import java.util.*;
import java.util.List;

/**
 * @author tag
 * @version $Id: RectangularTessellator.java 858 2012-10-18 18:22:57Z tgaskins $
 */
public class MyRectangularTessellator extends RectangularTessellator
{
	
	
	
	@Override
	protected void renderMultiTexture(DrawContext dc, RectTile tile, int numTextureUnits) {
		// TODO Auto-generated method stub
		
		//super.renderMultiTexture(arg0, arg1, arg2);
		
		if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (numTextureUnits < 1)
        {
            String msg = Logging.getMessage("generic.NumTextureUnitsLessThanOne");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.render(dc, tile, numTextureUnits);
		
	}



	@Override
	protected long render(DrawContext dc, RectTile tile, int numTextureUnits) {
		// TODO Auto-generated method stub
	//	return super.render(arg0, arg1, arg2);
		
		
		
		  if (tile.getRi() == null)
	        {
	            String msg = Logging.getMessage("nullValue.RenderInfoIsNull");
	            Logging.logger().severe(msg);
	            throw new IllegalStateException(msg);
	        }

	        if (dc.getGLRuntimeCapabilities().isUseVertexBufferObject())
	        {
	            if (!this.renderVBO(dc, tile, numTextureUnits))
	            {
	            	System.out.println("!this.renderVBO(dc, tile, numTextureUnits)");
	                // Fall back to VA rendering. This is an error condition at this point because something went wrong with
	                // VBO fill or binding. But we can still probably draw the tile using vertex arrays.
	                dc.getGL().glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	                dc.getGL().glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
	                this.renderVA(dc, tile, numTextureUnits);
	            }
	        }
	        else
	        {
	        	System.out.println("this.renderVA(dc, tile, numTextureUnits);");
	            this.renderVA(dc, tile, numTextureUnits);
	        }

	        return tile.getRi().getIndices().limit() - 2; // return number of triangles rendered
		
	}

	protected boolean renderVBO(DrawContext dc, RectTile tile, int numTextureUnits)
    {
        if (tile.getRi().isVboBound() || this.bindVbos(dc, tile, numTextureUnits))
        {
        	//  System.out.println("SONO QUA");
        //	  dc.getGL().getGL2().glEnable(GL.GL_BLEND);
        //  	dc.getGL().getGL2().glBlendFunc(GL.GL_ONE, GL.GL_ONE);
  	       // dc.getGL().getGL2().glDisable(GL.GL_BLEND);
  	//		dc.getGL().getGL2().glColor4d(1.0d, 1.0d, 1.0d, 0.0d);
        	//
        	 // dc.getGL().glEnable(GL2.GL_LIGHTING);
            // Render the tile
            dc.getGL().glDrawElements(GL.GL_TRIANGLE_STRIP, tile.getRi().getIndices().limit(), GL.GL_UNSIGNED_INT, 0);

  	    	//dc.getGL().getGL2().glColor4d(1.0d, 1.0d, 1.0d, 1.0d);
  			//dc.getGL().getGL2().glEnable(GL.GL_BLEND);
            return true;
        }
        else
        {
            return false;
        }
    }

	public void render(DrawContext dc, Object tile)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        dc.getView().setReferenceCenter(dc, ((RectTile)tile).getRi().getReferenceCenter());
       // dc.getView().setReferenceCenter(dc, ((RectTile)tile).ri.referenceCenter);
        
     //   this.render(dc, (RectTile)tile, 1);
        this.renderVA(dc, (RectTile)tile, 1);
        
        dc.getView().popReferenceCenter(dc);
    }
	
	
	
	
	 public void renderVA(DrawContext dc, RectTile tile, int numTextureUnits)
	    {
		 
		 
		
		 
	        GL2 gl = dc.getGL().getGL2();
	      //  dc.getGL().glEnable(GL.GL_DEPTH_TEST);
	      //    dc.getGL(). glDepthFunc(GL.GL_LESS);
	        gl.glVertexPointer(3, GL.GL_FLOAT, 0, tile.getRi().getVertices().rewind());
	        /*
	        for (int i = 0; i < numTextureUnits; i++)
	        {
	          gl.glClientActiveTexture(GL.GL_TEXTURE0 + i);
	           gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
	            Object texCoords = dc.getValue(AVKey.TEXTURE_COORDINATES);
	            if (texCoords != null && texCoords instanceof DoubleBuffer)
	                gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, ((DoubleBuffer) texCoords).rewind());
	            else
	                gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, tile.getRi().getTexCoords().rewind());
	        }
*/
	  
	        gl.glDrawElements(javax.media.opengl.GL.GL_TRIANGLE_STRIP, tile.getRi().getIndices().limit(),
	            javax.media.opengl.GL.GL_UNSIGNED_INT, tile.getRi().getIndices().rewind());
	        
	      
	    }
	/*
	public void render(DrawContext dc, RectTile tile)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.render(dc, tile, 1);
    }
    */
	
	
	
	
	
	
	
	
	 protected static class MyRectTile extends RectangularTessellator.RectTile
	    {

		@Override
		public void beginRendering(DrawContext arg0, int arg1) {
			// TODO Auto-generated method stub
			super.beginRendering(arg0, arg1);
		}


		@Override
		public void endRendering(DrawContext arg0) {
			// TODO Auto-generated method stub
			super.endRendering(arg0);
		}

		

		@Override
		public double getCellSize() {
			// TODO Auto-generated method stub
			return super.getCellSize();
		}


		@Override
		public int getDensity() {
			// TODO Auto-generated method stub
			return super.getDensity();
		}


		@Override
		public Extent getExtent() {
			// TODO Auto-generated method stub
			return super.getExtent();
		}


		@Override
		public int getLevel() {
			// TODO Auto-generated method stub
			return super.getLevel();
		}


		@Override
		public int getMaxColorCode() {
			// TODO Auto-generated method stub
			return super.getMaxColorCode();
		}


		@Override
		public int getMinColorCode() {
			// TODO Auto-generated method stub
			return super.getMinColorCode();
		}


		@Override
		public double getResolution() {
			// TODO Auto-generated method stub
			return super.getResolution();
		}


		@Override
		public RenderInfo getRi() {
			// TODO Auto-generated method stub
			return super.getRi();
		}


		@Override
		public Sector getSector() {
			// TODO Auto-generated method stub
			return super.getSector();
		}


		@Override
		public Vec4 getSurfacePoint(Angle arg0, Angle arg1, double arg2) {
			// TODO Auto-generated method stub
			return super.getSurfacePoint(arg0, arg1, arg2);
		}


		@Override
		public RectangularTessellator getTessellator() {
			// TODO Auto-generated method stub
			return super.getTessellator();
		}


		@Override
		public Intersection[] intersect(double arg0) {
			// TODO Auto-generated method stub
			return super.intersect(arg0);
		}


		@Override
		public Intersection[] intersect(Line arg0) {
			// TODO Auto-generated method stub
			return super.intersect(arg0);
		}


		@Override
		public DoubleBuffer makeTextureCoordinates(
				GeographicTextureCoordinateComputer arg0) {
			// TODO Auto-generated method stub
			return super.makeTextureCoordinates(arg0);
		}


		@Override
		public PickedObject[] pick(DrawContext arg0, List<? extends Point> arg1) {
			// TODO Auto-generated method stub
			return super.pick(arg0, arg1);
		}


		@Override
		public void pick(DrawContext arg0, Point arg1) {
			// TODO Auto-generated method stub
			super.pick(arg0, arg1);
		}


		@Override
		public void render(DrawContext arg0, boolean arg1) {
			// TODO Auto-generated method stub
			super.render(arg0, arg1);
		}


		@Override
		public void render(DrawContext arg0) {
			// TODO Auto-generated method stub
			super.render(arg0);
		}


		@Override
		public void renderBoundingVolume(DrawContext arg0) {
			// TODO Auto-generated method stub
			super.renderBoundingVolume(arg0);
		}


		@Override
		public void renderMultiTexture(DrawContext arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			super.renderMultiTexture(arg0, arg1, arg2);
		}


		@Override
		public void renderMultiTexture(DrawContext arg0, int arg1) {
			// TODO Auto-generated method stub
			super.renderMultiTexture(arg0, arg1);
		}


		@Override
		public void renderTileID(DrawContext arg0) {
			// TODO Auto-generated method stub
			super.renderTileID(arg0);
		}


		@Override
		public void renderWireframe(DrawContext arg0, boolean arg1, boolean arg2) {
			// TODO Auto-generated method stub
			super.renderWireframe(arg0, arg1, arg2);
		}


		public MyRectTile(RectangularTessellator arg0, Extent arg1, int arg2,
				int arg3, Sector arg4 ) {
			
			super(arg0, arg1, arg2, arg3, arg4);
			// TODO Auto-generated constructor stub
		}
		
		
		public Vec4 getReferenceCenter(){
			return ((MyRenderInfo)this.ri).getReferenceCenter();
		}
	    }
	 
	 protected static class MyRenderInfo extends RectangularTessellator.RenderInfo{

		public MyRenderInfo(DrawContext arg0, int arg1, FloatBuffer arg2,
				Vec4 arg3) {
			super(arg0, arg1, arg2, arg3);
			// TODO Auto-generated constructor stub
		}
		 
	 }
}
