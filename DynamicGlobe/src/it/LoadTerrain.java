package it;
import it.entities.Grid;
import it.shader.GLSL;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.media.opengl.GL;


import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.OGLStackHandler;


public class LoadTerrain {

    static public final Object textureCacheKey = new Object();
    //protected String texturePath = "images/Mirino3.png"; // TODO: make configurable
     
	private void loadTexture(DrawContext dc, Position ld, Position rd, Position lu, Position ru,Position originalLd, Position originalRd, Position originalLu, Position originalRu){
		
		//Texture iconTexture = dc.getTextureCache().getTexture(textureCacheKey);
		Texture iconTexture = dc.getTextureCache().getTexture(textureCacheKey);
		
		
		
		 if (iconTexture == null )
	        {	            
			  
			 initializeTexture(dc);
	          
	        }
		
		
		 
		 Vec4 p1 =computeWorldCoordinatesFromPosition(dc, ld);
		 Vec4 p2 =computeWorldCoordinatesFromPosition(dc, rd);
		 Vec4 p3 =computeWorldCoordinatesFromPosition(dc, lu);
		 Vec4 p4 =computeWorldCoordinatesFromPosition(dc, ru);
		 
		 
		 Vec4 t1 = computeTextureCoordinateFromPosition(originalLd);
		 Vec4 t2 = computeTextureCoordinateFromPosition(originalRd);
		 Vec4 t3 = computeTextureCoordinateFromPosition(originalLu);
		 Vec4 t4 = computeTextureCoordinateFromPosition(originalRu);
		 
		 
		 
		 
		 
		 drawGridWithQuad(dc, p1,p2,p3,p4,t1,t2,t3,t4);
	}
	
	public void initializeTexture(DrawContext dc)
	{
		
	//	System.out.println("sono qua");
	    Texture iconTexture = dc.getTextureCache().getTexture(SharedVariables.texturePath);
	//  
	    if (iconTexture != null){
	  // System.out.println("c'è gia texture");
	     iconTexture.bind(dc.getGL());
	    	return;
	    }
	   
	    
	    System.out.println("INIT TEXTURE");
	    try
	    {
	        InputStream iconStream = this.getClass().getResourceAsStream("/" + SharedVariables.texturePath);
	        if (iconStream == null)
	        {
	            File iconFile = new File(SharedVariables.texturePath);
	            if (iconFile.exists())
	            {
	            	
	                iconStream = new FileInputStream(iconFile);
	            }
	        }

	        iconTexture = TextureIO.newTexture(iconStream, false, null);
	        iconTexture.bind(dc.getGL());
	    //    this.iconWidth1 = iconTexture.getWidth();
	    //    this.iconHeight1 = iconTexture.getHeight();
	      //  System.out.println("in initTexture: "+iconWidth1+" "+iconHeight1);
	       
	        
	        dc.getTextureCache().put(SharedVariables.texturePath, iconTexture);
	    }
	    catch (IOException e)
	    {
	        String msg = Logging.getMessage("layers.IOExceptionDuringInitialization");
	        Logging.logger().severe(msg);
	        throw new WWRuntimeException(msg, e);
	    }

	    GL gl = dc.getGL();
	    
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);//_MIPMAP_LINEAR);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	   
	   
	    //gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
	    //gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
	    
	    
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
	    
	    
	    // Enable texture anisotropy, improves "tilted" compass quality.
	    int[] maxAnisotropy = new int[1];
	    gl.glGetIntegerv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy, 0);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy[0]);

	    
	}

	
	/*
	private void draw2(DrawContext dc, Vec4 p1, Vec4 p2, Vec4 p3, Vec4 p4){
	  GL gl = dc.getGL();

      int attrBits = GL.GL_HINT_BIT | GL.GL_CURRENT_BIT | GL.GL_LINE_BIT;      

      gl.glPushAttrib(attrBits);
   

  
    //  System.out.println("QAQQQQQq");
      
      try
      {
        
         

              // Since segments can very often be very short -- two vertices -- use explicit rendering. The
              // overhead of batched rendering, e.g., gl.glDrawArrays, is too high because it requires copying
              // the vertices into a DoubleBuffer, and DoubleBuffer creation and access performs relatively poorly.
              gl.glBegin(GL.GL_LINE);
             
                  gl.glVertex3d(p1.x, p1.y, p1.z);
                  gl.glVertex3d(p2.x, p2.y, p2.z);
                  gl.glVertex3d(p3.x, p3.y, p3.z);
                  gl.glVertex3d(p4.x, p4.y, p4.z);
             
              gl.glEnd();
          

         
      }
      finally
      {
      

          gl.glPopAttrib();
   
     //     isAffectedByLense=false;
          
      }
	}
	*/
	
	/*
	public void drawGridWithTriangles(DrawContext dc, ArrayList<Integer> indices, Grid grid)
	{
		
//System.out.println("drawGridWithTriangles");

	    GL gl = dc.getGL();
	    int attrBits = GL2.GL_HINT_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT;      

	    gl.getGL2().glPushAttrib(attrBits);
	 
	    gl.glEnable (GL.GL_BLEND); 
	    gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

	    try
	    {
	    //    gl.glDisable(GL.GL_DEPTH_TEST);

	      
	 
	              //	System.out.println("DRAW TEXTURE");
	              
	            	
	            	
	            	// gl.glColor4d(1d, 1d, 1d,1d);
	           
	                Vec4 prevPosInTexture = null;
	                
	                gl.getGL2().glBegin(GL2.GL_TRIANGLE_STRIP);   
	            	//System.out.println("INDICES");
	        		for(Integer i : indices){
	        			//System.out.println(i);
	        			Position p = grid.getPositionInDeformedGridAtIndex(i);
	        			 Vec4 v =LoadTerrain.computeWorldCoordinatesFromPosition(dc, p);
	        			 
	        				Position posInTexture = grid.getPositionInGridAtIndex(i);
	        			 Vec4 t = LoadTerrain.computeTextureCoordinateFromPosition(posInTexture);
	        	Vec4 originalT = new Vec4(t.x, t.y);
	        			 double newX ;
	        			 double newY ;
	        			 if(prevPosInTexture!=null){
	        				 if(Math.abs(t.x-prevPosInTexture.x)>0.5){
	        					 
	        					 if(t.x>prevPosInTexture.x){
	        						 newX=-(1-t.x);
	        					 }else{
	        						 newX=(1+t.x);
	        					 }
	        					 
	        					 t = new Vec4(newX, t.y);
	        				 }
	        				 if(Math.abs(t.y-prevPosInTexture.y)>0.5){
	        					 if(t.y>prevPosInTexture.y){
	        						 newY=-(1-t.y);
	        					 }else{
	        						 newY=(1+t.y);
	        					 }
	        					 
	        					 t = new Vec4(t.x, newY);
	        					
	        				 }
	        			 }
	        			 
	        			
	        			 
	        			 gl.getGL2().glTexCoord2d(t.x, t.y);
	 	                gl.getGL2().glVertex3d(v.x, v.y, v.z);   
	        			 
	 	             //  prevPosInTexture=originalT;
	 	               prevPosInTexture=t;
	        	
	                
	                gl.getGL2().glEnd();
	            }
	          
	     
	        //drawQuad(dc, p3, p4, p2, p1);
	    }
	    finally
	    {
	    	dc.getGL().getGL2().glPopAttrib();
	        dc.restoreDefaultDepthTesting();
	        dc.restoreDefaultCurrentColor();

	         {
	             dc.restoreDefaultBlending();
	       //     gl.glEnable(GL.GL_DEPTH_TEST);
	        }

	      
	    }
	}
	*/
	
	
	public void drawGridWithTriangles(DrawContext dc, ArrayList<Integer> indices, Grid grid,GLSL glsl_trans)
	{
		
//System.out.println("drawGridWithTriangles");

	    GL gl = dc.getGL();
	    int attrBits = GL2.GL_HINT_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT;      

	    gl.getGL2().glPushAttrib(attrBits);
	 
	   
	    gl.glEnable (GL.GL_BLEND); 
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
	    /*
	    gl.glBlendEquation(GL2.GL_MAX); 
	    gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE);
	*/
	    try
	    {
	   
	     
	      
	        Texture iconTexture = null;

		  iconTexture = dc.getTextureCache().getTexture(SharedVariables.texturePath);
		     if (iconTexture == null)
		     {
		     //	System.out.println("iconTexture == null");
		         this.initializeTexture(dc);     
		     }

	            if (iconTexture != null)
	            {           
	            //	System.out.println("DRAW TEXTURE");
	              
	            	gl.glEnable(GL.GL_TEXTURE_2D);
	                iconTexture.bind(dc.getGL());
	               
	            	
	            	
	            	// gl.glColor4d(1d, 1d, 1d,1d);
	           
	                Vec4 prevPosInTexture = null;
	                
	                gl.getGL2().glBegin(GL2.GL_TRIANGLE_STRIP);   
	            	//System.out.println("INDICES");
	        		for(Integer i : indices){
	        			//System.out.println(i);
	        			Position p = grid.getPositionInDeformedGridAtIndex(i);
	        			 Vec4 v =LoadTerrain.computeWorldCoordinatesFromPosition(dc, p);
	        			 
	        				Position posInTexture = grid.getPositionInGridAtIndex(i);
	        			 Vec4 t = LoadTerrain.computeTextureCoordinateFromPosition(posInTexture);
	        	Vec4 originalT = new Vec4(t.x, t.y);
	        			 double newX ;
	        			 double newY ;
	        			 if(prevPosInTexture!=null){
	        				 if(Math.abs(t.x-prevPosInTexture.x)>0.5){
	        					 
	        					 if(t.x>prevPosInTexture.x){
	        						 newX=-(1-t.x);
	        					 }else{
	        						 newX=(1+t.x);
	        					 }
	        					 
	        					 t = new Vec4(newX, t.y);
	        					 //t = prevPosInTexture;
	        				 }
	        				 if(Math.abs(t.y-prevPosInTexture.y)>0.5){
	        				 if(t.y>prevPosInTexture.y){
	        						 newY=-(1-t.y);
	        					 }else{
	        						 newY=(1+t.y);
	        					 }
	        					 
	        					 t = new Vec4(t.x, newY);
	        					 //t = prevPosInTexture;
	        				 }
	        			 }
	        			 
	        				float ind = (int)(i/7);
	        				float ind_x = ((int)i%7)/6.0f;
	        		float ind_y = (ind)/6.0f;
	        	
	        		
	        		//System.out.println("indices: "+i+", ind: "+ ind+" x: "+ind_x+" y: "+ind_y);
	        			 int loc_myIndex = -1;
	        			
	        			  loc_myIndex = gl.getGL2().glGetUniformLocation(glsl_trans.getProgramObject(), "myIndex");
	        			// System.out.println("loc_myIndex: "+loc_myIndex);
	        			 gl.getGL2().glUniform1f(loc_myIndex, i);
	        			
	        			 gl.getGL2().glTexCoord2d(t.x, t.y);
	        			 gl.getGL2().glNormal3f(i, ind_x,ind_y);
	 	                gl.getGL2().glVertex3d(v.x, v.y, v.z);   
	        			 
	 	             //  prevPosInTexture=originalT;
	 	               prevPosInTexture=t;
	        		}
	                
	                gl.getGL2().glEnd();
	            }
	          
	     
	        //drawQuad(dc, p3, p4, p2, p1);
	    }
	    finally
	    {
	    	dc.getGL().getGL2().glPopAttrib();
	        //dc.restoreDefaultDepthTesting();
	        //dc.restoreDefaultCurrentColor();

	    	gl.glDisable(GL.GL_BLEND); 
	            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	            gl.glDisable(GL.GL_TEXTURE_2D); // restore to default texture state
	         //   dc.restoreDefaultBlending();
	       //     gl.glEnable(GL.GL_DEPTH_TEST);
	            
	          //  gl.glDepthMask(true);
		      //  gl.glEnable(GL.GL_DEPTH_TEST);
	        

	      
	    }
	}
	
	
private void drawGridWithQuad(DrawContext dc, Vec4 p1, Vec4 p2, Vec4 p3, Vec4 p4,Vec4 t1, Vec4 t2, Vec4 t3, Vec4 t4)
{
	


    GL gl = dc.getGL();
    int attrBits = GL2.GL_HINT_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT;      

    gl.getGL2().glPushAttrib(attrBits);
 

    try
    {
    //    gl.glDisable(GL.GL_DEPTH_TEST);

        double width=0;
        double height=0;
     
     
        // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
        // into the GL projection matrix.
        //java.awt.Rectangle viewport = dc.getView().getViewport();
     
           
        
        
     
        Texture iconTexture = null;

	  iconTexture = dc.getTextureCache().getTexture(SharedVariables.texturePath);
	     if (iconTexture == null)
	     {
	     //	System.out.println("iconTexture == null");
	         this.initializeTexture(dc);     
	     }

            if (iconTexture != null)
            {           
            //	System.out.println("DRAW TEXTURE");
                gl.glEnable(GL.GL_TEXTURE_2D);
                iconTexture.bind(dc.getGL());
               // gl.glColor4d(1d, 1d, 1d,1d);
           
                
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                
               
                
            //    TextureCoords texCoords = iconTexture.getImageTexCoords();
               // gl.glScaled(width, height, 1d);
              //  dc.drawUnitQuad(texCoords);
                
                
                //drawQuad(dc, p3, p4, p2, p1,t3,t4,t2,t1);
                drawQuad(dc, p3, p4, p2, p1,t3,t4,t2,t1);
            }
          
     
        //drawQuad(dc, p3, p4, p2, p1);
    }
    finally
    {
    	dc.getGL().getGL2().glPopAttrib();
        dc.restoreDefaultDepthTesting();
        dc.restoreDefaultCurrentColor();

         {
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
            gl.glDisable(GL.GL_TEXTURE_2D); // restore to default texture state
            dc.restoreDefaultBlending();
       //     gl.glEnable(GL.GL_DEPTH_TEST);
        }

      
    }
}


public static Vec4 computeTextureCoordinateFromPosition(Position position){
	
	double[] pos = position.asDegreesArray();
	return project(pos[0], pos[1]);
}
//
//public static void main(String[] args) {
//	Position p = Position.fromDegrees(-90, -180);
//	
//	Vec4 v = computeTextureCoordinateFromPosition(p);
//	System.out.println("x: "+v.x+" y: "+v.y);
//}

public final static double QUARTERPI = Math.PI/4.0;

public static Vec4 project(double lat, double lon) {
	

        double x = lon/180;
        double y =-1*( lat/90);
    
      //  x=x/Math.PI;
      //  y=y/Math.PI;
        
    return new Vec4((x+1)/2, (y+1)/2);
}



public static Vec4 computeWorldCoordinatesFromPosition(DrawContext dc,Position position){
	
		
	return  dc.getGlobe().computePointFromPosition(position);
}	

private void drawQuad(DrawContext dc, Vec4 topLeft , Vec4 topRight ,Vec4 bottomRight ,Vec4 bottomLeft , Vec4 text1 , Vec4 text2 ,Vec4 text3 ,Vec4 text4){
//	System.out.println("draw quad1: "+p1);
//	System.out.println("draw quad2: "+p2);
//	System.out.println("draw quad3: "+p3);
//	System.out.println("draw quad4: "+p4);
	GL gl = dc.getGL();
	
	
	/*
	gl.glBegin(GL.GL_QUADS);   
    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3d(topLeft.x, topLeft.y, topLeft.z);   // Top Left
    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3d(topRight.x, topRight.y, topRight.z);   // Top Right
    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3d(bottomRight.x, bottomRight.y, bottomRight.z);   // Bottom Right
    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3d(bottomLeft.x, bottomLeft.y, bottomLeft.z);   // Bottom Left
    gl.glEnd();
	*/
	
	
    gl.getGL2().glBegin(GL2.GL_QUADS);   
    gl.getGL2().glTexCoord2d(text1.x, text1.y);
    gl.getGL2().glVertex3d(topLeft.x, topLeft.y, topLeft.z);   // Top Left
    gl.getGL2().glTexCoord2d(text2.x, text2.y);
    gl.getGL2().glVertex3d(topRight.x, topRight.y, topRight.z);   // Top Right
    gl.getGL2().glTexCoord2d(text3.x, text3.y);
    gl.getGL2().glVertex3d(bottomRight.x, bottomRight.y, bottomRight.z);   // Bottom Right
    gl.getGL2().glTexCoord2d(text4.x, text4.y);
    gl.getGL2().glVertex3d(bottomLeft.x, bottomLeft.y, bottomLeft.z);   // Bottom Left
    gl.getGL2().glEnd();
    
}

}
