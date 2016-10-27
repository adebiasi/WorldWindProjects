package it.graphitech.smeSpire;

import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.OGLStackHandler;
import it.graphitech.smeSpire.buffers.FBOManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.gl2.GLUgl2;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;





public class RenderTextureOnScreen {

	
	static public int h_texture_in_pixels = 300;
	static public int w_texture_in_pixels = 300;
	
	
	protected int iconWidth1;
	protected int iconHeight1;
	protected int iconWidth2;
	protected int iconHeight2;
	protected int iconWidth3;
	protected int iconHeight3;

	
protected String iconFilePath = "images/Mirino3.png"; // TODO: make configurable
public String getIconFilePath()
{
    return iconFilePath;
}

protected String iconFilePath2 = "images/Mirino4.png"; // TODO: make configurable
public String getIconFilePath2()
{
    return iconFilePath2;
}
protected String iconFilePath3 = "images/Mirino5.png"; // TODO: make configurable
public String getIconFilePath3()
{
    return iconFilePath3;
}




public void drawFrameBufferOnTheScreen(DrawContext dc){

	//System.out.println("DRAW FRAME BUFFER");
   // if (this.getIconFilePath() == null)
     //   return;

    GL2 gl = dc.getGL().getGL2();
    OGLStackHandler ogsh = new OGLStackHandler();

    try
    {
    	
        gl.glDisable(GL.GL_DEPTH_TEST);

        double small_width=w_texture_in_pixels;
        double small_height=h_texture_in_pixels;
       
       
      
     
        // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
        // into the GL projection matrix.
        java.awt.Rectangle viewport = dc.getView().getViewport();
        
        
       // double screen_width=FBOManager.w_window_in_pixels;
        double screen_width=viewport.getWidth();
        //double screen_height=FBOManager.h_window_in_pixels;
        double screen_height=viewport.getHeight();
        //System.out.println("screen_width: "+screen_width);
        //System.out.println("screen_height: "+screen_height);
        
        ogsh.pushProjectionIdentity(gl);
        double aspect = screen_width / screen_height;
        double maxwh = screen_width > screen_height ? screen_width : screen_height;
        if (maxwh == 0)
            maxwh = 1;
            
        gl.glViewport(0, 0, (int)screen_width,(int) screen_height);        
        //gl.glViewport(0, 0, (int)screen_width,(int) screen_height);
        gl.glOrtho(0d, screen_width, 0d, screen_height, -0.6 * maxwh, 0.6 * maxwh);
       
        /* 
        double aspect = screen_width / screen_height;
        gl.glViewport(0, 0, (int)screen_width, (int)screen_height);
        gl.glOrtho(0.0 * aspect, 50.0 * aspect, -50.0, 50.0, 1.0, -1.0);
        */
        ogsh.pushModelviewIdentity(gl);
        //double scale = this.computeScale(viewport);
        
     
       double offset = 0.1;
     //   Vec4 locationSW = new Vec4(x, y, 0);
        if (FBOManager.pointDetectionTextureObject != -1)
        {
        	int textureObject = FBOManager.pointDetectionTextureObject;
        	 gl.glPushMatrix();
        	 gl.glScaled(small_width, small_height, 1d);
        	
        //	System.out.println("draw iconTexture1");
        	//System.out.println("iconTexture1 != null");
        	drawQuad(dc, offset, 1);   
        	drawTexture(dc, textureObject, offset, 1);
        	 gl.glPopMatrix();
        	}
        if (FBOManager.depthTextureObject != -1)
        {
        	int textureObject = FBOManager.depthTextureObject;
        	 gl.glPushMatrix();
        	 gl.glScaled(small_width, small_height, 1d);
        	//System.out.println("draw iconTexture2");
        	drawQuad(dc, offset, 2);   
        	drawTexture(dc, textureObject, offset, 2);
        	 gl.glPopMatrix();
        	}
     
       
            
            gl.glViewport(0, 0, (int)viewport.width,(int) viewport.height);  
    }
    finally
    {
        dc.restoreDefaultDepthTesting();
        dc.restoreDefaultCurrentColor();

      //  gl.glEnable(GL.GL_DEPTH_TEST);
        
    //    if (!dc.isPickingMode())
        {
            gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
            gl.glDisable(GL2.GL_TEXTURE_2D); // restore to default texture state
            dc.restoreDefaultBlending();
            gl.glEnable(GL2.GL_DEPTH_TEST);
        }

        ogsh.pop(gl);
    }
}















public void drawFrameBuffer(DrawContext dc){

	
   // if (this.getIconFilePath() == null)
     //   return;

    GL2 gl = dc.getGL().getGL2();
    OGLStackHandler ogsh = new OGLStackHandler();

    try
    {
    	
        gl.glDisable(GL.GL_DEPTH_TEST);

        double width=SharedVariables.lense_w;
        double height=SharedVariables.lense_h;
       
     
        // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
        // into the GL projection matrix.
        java.awt.Rectangle viewport = dc.getView().getViewport();
        ogsh.pushProjectionIdentity(gl);
        double maxwh = width > height ? width : height;
        if (maxwh == 0)
            maxwh = 1;
        gl.glViewport(0, 0, (int)width*9,(int) height);        
        gl.glOrtho(0d, width*9, 0d, height, -0.6 * maxwh, 0.6 * maxwh);
       
         
        ogsh.pushModelviewIdentity(gl);
        //double scale = this.computeScale(viewport);
        
        
      //  Vec4 locationSW = this.computeLocation(viewport);
        
       // double width = this.iconWidth * this.iconScale;
       // double height = this.iconHeight * this.iconScale;
        gl.glScaled(width, height, 1d);
     //   double x= width / 2;
     //   double y= height / 2;
       double offset = 0.1;
     //   Vec4 locationSW = new Vec4(x, y, 0);
    
             
               
                offset = 0.1;
               //   Vec4 locationSW = new Vec4(x, y, 0);
                  if (FBOManager.pointDetectionTextureObject!= -1)
                  {
                	  System.out.println("DISEGNO TEXTURE POINT DETECTION");
                  	int textureObject = FBOManager.pointDetectionTextureObject;
                  	 gl.glPushMatrix();
                  	 gl.glScaled(width, height, 1d);
                  	
                  //	System.out.println("draw iconTexture1");
                  	//System.out.println("iconTexture1 != null");
                  	drawQuad(dc, offset, 1);   
                  	drawTexture(dc, textureObject, offset, 1);
                  	 gl.glPopMatrix();
                  	}
               
               
               
            
            gl.glViewport(0, 0, (int)viewport.width,(int) viewport.height);  
    }
    finally
    {
        dc.restoreDefaultDepthTesting();
        dc.restoreDefaultCurrentColor();

      //  gl.glEnable(GL.GL_DEPTH_TEST);
        
    //    if (!dc.isPickingMode())
        {
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
            gl.glDisable(GL.GL_TEXTURE_2D); // restore to default texture state
            dc.restoreDefaultBlending();
            gl.glEnable(GL.GL_DEPTH_TEST);
        }

        ogsh.pop(gl);
    }
}

private void drawTexture(DrawContext dc,int textureObject, double offset, int numTexture){
	
	//System.out.println("DRAW TEXTURE");
	
	GL2 gl = dc.getGL().getGL2();
	
	
	
	//System.out.println("targetText: "+textureObject);
	
	double x1 = numTexture-1+offset*numTexture;
	double x2 = numTexture+offset*numTexture;
	double y1 = 0d;
			double y2 = 1;
	
   	//gl.glColor3f(1.0f, 1.0f, 1.0f);   // set the color of the quad
	
	gl.glEnable(GL2.GL_TEXTURE_2D);        	  	
	setBlending(gl);
    //iconTexture.bind(gl);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, textureObject);
    gl.glBegin(GL2.GL_QUADS);
    //gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
    gl.glTexCoord2d(0.0, 0.0);
    gl.glVertex2d(x1, y1);
    //gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
    gl.glTexCoord2d(1.0, 0.0);
    gl.glVertex2d(x2, y1);
    //gl.glTexCoord2d(texCoords.right(), texCoords.top());
    gl.glTexCoord2d(1.0, 1.0);
    gl.glVertex2d(x2, y2);
    //gl.glTexCoord2d(texCoords.left(), texCoords.top());
    gl.glTexCoord2d(0.0, 1.0);
    gl.glVertex2d(x1, y2);
    gl.glEnd();       
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    gl.glDisable(GL2.GL_TEXTURE_2D); // restore to default texture state
    dc.restoreDefaultBlending();
}

private void drawQuad(DrawContext dc, double offset,int numQuad){
	GL2 gl = dc.getGL().getGL2();
	gl.glBegin(GL2.GL_QUADS);
    gl.glVertex2d(numQuad-1+offset*numQuad,0);              
    gl.glVertex2d(numQuad+offset*numQuad, 0);               
    gl.glVertex2d(numQuad+offset*numQuad,1);              
    gl.glVertex2d(numQuad-1+offset*numQuad, 1);
    gl.glEnd();
}

private boolean isOdd(int arcIndex){
	return ((arcIndex%2)==1);
}

public void drawText(DrawContext dc,Vec4 screenPoint,String text, boolean isOutliner , int arcIndex)
{
	

	locationCenter=screenPoint;

    GL2 gl = dc.getGL().getGL2();
    OGLStackHandler ogsh = new OGLStackHandler();
    GLUgl2 glu = new GLUgl2();
    GLUT glut = new GLUT();
    try
    {
    	//System.out.println("in draw gl.glDisable(GL.GL_DEPTH_TEST)");
        gl.glDisable(GL.GL_DEPTH_TEST);

        double width=0;
        double height=0;
            width = this.iconWidth3 * this.iconScale;
            height = this.iconHeight3 * this.iconScale;          
     
        // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
        // into the GL projection matrix.
        java.awt.Rectangle viewport = dc.getView().getViewport();
        ogsh.pushProjectionIdentity(gl);
        double maxwh = width > height ? width : height;
        if (maxwh == 0)
            maxwh = 1;
        gl.glOrtho(0d, viewport.width, 0d, viewport.height, -0.6 * maxwh, 0.6 * maxwh);

        ogsh.pushModelviewIdentity(gl);
        //double scale = this.computeScale(viewport);
        
      //  Vec4 locationSW = this.computeLocation(viewport);
        
       // double width = this.iconWidth * this.iconScale;
       // double height = this.iconHeight * this.iconScale;

        double x= this.locationCenter.x - width / 2;
        double y= this.locationCenter.y - height / 2;
       
        Vec4 locationSW = new Vec4(x, y, 0);
        
        /*
        double x_str = arrotonda(screenPoint.x,3);
        double y_str = arrotonda(screenPoint.y,3);
        double z_str = arrotonda(screenPoint.z,3);
        		
        double x_world_str = arrotonda(worldPoint.x,3);
        double y_world_str = arrotonda(worldPoint.y,3);
        double z_world_str = arrotonda(worldPoint.z,3);
        */
    //    System.out.println("stampo: "+text);
        if(SharedVariables.chooseColorConfigurations==4){
        	if(isOutliner){
        		gl.glColor4d(1d, 0d, 0d,1d);
        		
        		int offset = 0;
        		if(isOdd(arcIndex)){
        			offset=-120;
        		}
        		
        	 gl.glRasterPos2i((int)locationSW.x+offset, (int)locationSW.y); // raster position in 2D
             //glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,text);
        	 //gl.glRotatef(-90, 1, 1, 1); 
        	 glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,text);
        	
        	 
        	}
        }else{        
        gl.glRasterPos2i((int)locationSW.x, (int)locationSW.y); // raster position in 2D
        glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,text);
        //gl.glRasterPos2i(2,-2); // raster position in 2D
        //glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,text);
        }
       // double heading = this.computeHeading(dc.getView());
     //   gl.glRasterPos2i((int)locationSW.x, (int)locationSW.y-15); // raster position in 2D
     //   glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "x2: "+x_world_str+", y2: "+y_world_str+", z2: "+z_world_str);
/*
        gl.glTranslated(locationSW.x, locationSW.y, locationSW.z);
        Texture iconTexture = null;

	  iconTexture = dc.getTextureCache().getTexture(this.getIconFilePath3());
	     if (iconTexture == null)
	     {
	     	System.out.println("iconTexture == null");
	         this.initializeTexture3(dc);     
	     }

            if (iconTexture != null)
            {            	
                gl.glEnable(GL.GL_TEXTURE_2D);
                iconTexture.bind(gl);
                gl.glColor4d(1d, 1d, 1d,1d);
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                TextureCoords texCoords = iconTexture.getImageTexCoords();
                gl.glScaled(width, height, 1d);
                dc.drawUnitQuad(texCoords);
               
            }
*/
        
    }
    finally
    {
        dc.restoreDefaultDepthTesting();
        dc.restoreDefaultCurrentColor();

      //  gl.glEnable(GL.GL_DEPTH_TEST);
        
   
        {
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
            gl.glDisable(GL.GL_TEXTURE_2D); // restore to default texture state
            dc.restoreDefaultBlending();
            gl.glEnable(GL.GL_DEPTH_TEST);
        }

        ogsh.pop(gl);
    }
}
public void drawText(DrawContext dc,Vec4 screenPoint, Vec4 worldPoint )
{
	

	locationCenter=screenPoint;

    GL2 gl = dc.getGL().getGL2();
    OGLStackHandler ogsh = new OGLStackHandler();
    GLUgl2 glu = new GLUgl2();
    GLUT glut = new GLUT();
    try
    {
    	//System.out.println("in draw gl.glDisable(GL.GL_DEPTH_TEST)");
        gl.glDisable(GL.GL_DEPTH_TEST);

        double width=0;
        double height=0;
            width = this.iconWidth3 * this.iconScale;
            height = this.iconHeight3 * this.iconScale;          
     
        // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
        // into the GL projection matrix.
        java.awt.Rectangle viewport = dc.getView().getViewport();
        ogsh.pushProjectionIdentity(gl);
        double maxwh = width > height ? width : height;
        if (maxwh == 0)
            maxwh = 1;
        gl.glOrtho(0d, viewport.width, 0d, viewport.height, -0.6 * maxwh, 0.6 * maxwh);

        ogsh.pushModelviewIdentity(gl);
        //double scale = this.computeScale(viewport);
        
      //  Vec4 locationSW = this.computeLocation(viewport);
        
       // double width = this.iconWidth * this.iconScale;
       // double height = this.iconHeight * this.iconScale;

        double x= this.locationCenter.x - width / 2;
        double y= this.locationCenter.y - height / 2;
       
        Vec4 locationSW = new Vec4(x, y, 0);
        
        
        double x_str = arrotonda(screenPoint.x,3);
        double y_str = arrotonda(screenPoint.y,3);
        double z_str = arrotonda(screenPoint.z,3);
        		
        double x_world_str = arrotonda(worldPoint.x,3);
        double y_world_str = arrotonda(worldPoint.y,3);
        double z_world_str = arrotonda(worldPoint.z,3);
        
        
        gl.glRasterPos2i((int)locationSW.x, (int)locationSW.y); // raster position in 2D
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "x: "+x_str+", y: "+y_str+", z: "+z_str);
       // double heading = this.computeHeading(dc.getView());
        gl.glRasterPos2i((int)locationSW.x, (int)locationSW.y-15); // raster position in 2D
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "x2: "+x_world_str+", y2: "+y_world_str+", z2: "+z_world_str);
/*
        gl.glTranslated(locationSW.x, locationSW.y, locationSW.z);
        Texture iconTexture = null;

	  iconTexture = dc.getTextureCache().getTexture(this.getIconFilePath3());
	     if (iconTexture == null)
	     {
	     	System.out.println("iconTexture == null");
	         this.initializeTexture3(dc);     
	     }

            if (iconTexture != null)
            {            	
                gl.glEnable(GL.GL_TEXTURE_2D);
                iconTexture.bind(gl);
                gl.glColor4d(1d, 1d, 1d,1d);
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                TextureCoords texCoords = iconTexture.getImageTexCoords();
                gl.glScaled(width, height, 1d);
                dc.drawUnitQuad(texCoords);
               
            }
*/
        
    }
    finally
    {
        dc.restoreDefaultDepthTesting();
        dc.restoreDefaultCurrentColor();

      //  gl.glEnable(GL.GL_DEPTH_TEST);
        
   
        {
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
            gl.glDisable(GL.GL_TEXTURE_2D); // restore to default texture state
            dc.restoreDefaultBlending();
            gl.glEnable(GL.GL_DEPTH_TEST);
        }

        ogsh.pop(gl);
    }
}

private double arrotonda(double x, int num ){
	  double x_str = x;
      x_str = x_str * 100;
      		
      		double x_str2 = Math.round(x_str);        	
      		x_str2 = x_str2 / (Math.pow(10, num));
      		return x_str2;
}

public void draw(DrawContext dc,Vec4 p, int type)
{
	

	locationCenter=p;
   // if (this.getIconFilePath() == null)
     //   return;

    GL2 gl = dc.getGL().getGL2();
    OGLStackHandler ogsh = new OGLStackHandler();
    GLUgl2 glu = new GLUgl2();
    GLUT glut = new GLUT();
    try
    {
    	//System.out.println("in draw gl.glDisable(GL.GL_DEPTH_TEST)");
        gl.glDisable(GL.GL_DEPTH_TEST);

        double width=0;
        double height=0;
        if(type==1){
         width = this.iconWidth1 * this.iconScale;
         height = this.iconHeight1 * this.iconScale;
      //   System.out.println("iconWidth1: "+iconWidth1);
      //   System.out.println("iconScale: "+iconScale);    
        }
        if(type==2){
            width = this.iconWidth2 * this.iconScale;
            height = this.iconHeight2 * this.iconScale;          
           }
        if(type==3){
            width = this.iconWidth3 * this.iconScale;
            height = this.iconHeight3 * this.iconScale;          
           }
     
        // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
        // into the GL projection matrix.
        java.awt.Rectangle viewport = dc.getView().getViewport();
        ogsh.pushProjectionIdentity(gl);
        double maxwh = width > height ? width : height;
        if (maxwh == 0)
            maxwh = 1;
        gl.glOrtho(0d, viewport.width, 0d, viewport.height, -0.6 * maxwh, 0.6 * maxwh);

        ogsh.pushModelviewIdentity(gl);
        //double scale = this.computeScale(viewport);
        
      //  Vec4 locationSW = this.computeLocation(viewport);
        
       // double width = this.iconWidth * this.iconScale;
       // double height = this.iconHeight * this.iconScale;

        double x= this.locationCenter.x - width / 2;
        double y= this.locationCenter.y - height / 2;
       
        Vec4 locationSW = new Vec4(x, y, 0);
        
    
       // double heading = this.computeHeading(dc.getView());
       

        gl.glTranslated(locationSW.x, locationSW.y, locationSW.z);
        Texture iconTexture = null;
if(type==1){
             iconTexture = dc.getTextureCache().getTexture(this.getIconFilePath());
            if (iconTexture == null)
            {
            	System.out.println("iconTexture == null");
                this.initializeTexture(dc);     
            }
}else if(type==2){
	  iconTexture = dc.getTextureCache().getTexture(this.getIconFilePath2());
     if (iconTexture == null)
     {
     	System.out.println("iconTexture == null");
         this.initializeTexture2(dc);     
     }
}else{
	  iconTexture = dc.getTextureCache().getTexture(this.getIconFilePath3());
	     if (iconTexture == null)
	     {
	     	System.out.println("iconTexture == null");
	         this.initializeTexture3(dc);     
	     }
}
            if (iconTexture != null)
            {            	
                gl.glEnable(GL.GL_TEXTURE_2D);
                iconTexture.bind(gl);
                gl.glColor4d(1d, 1d, 1d,1d);
                setBlending(gl);
                TextureCoords texCoords = iconTexture.getImageTexCoords();
                gl.glScaled(width, height, 1d);
                dc.drawUnitQuad(texCoords);
                gl.glRasterPos2i((int)locationSW.x, (int)locationSW.y); // raster position in 2D
                glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "We're going to the moon!");
            }

        
    }
    finally
    {
        dc.restoreDefaultDepthTesting();
        dc.restoreDefaultCurrentColor();

      //  gl.glEnable(GL.GL_DEPTH_TEST);
        
   
        {
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
            gl.glDisable(GL.GL_TEXTURE_2D); // restore to default texture state
            dc.restoreDefaultBlending();
            gl.glEnable(GL.GL_DEPTH_TEST);
        }

        ogsh.pop(gl);
    }
}

private void setBlending(GL2 gl){
	gl.glEnable(GL.GL_BLEND);
    //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
}



private void initializeTexture(DrawContext dc)
{
	
	System.out.println("sono qua");
    Texture iconTexture = dc.getTextureCache().getTexture(this.getIconFilePath());
//  
    if (iconTexture != null){
   System.out.println("c'è gia texture");
     iconTexture.bind(dc.getGL());
    	return;
    }

    try
    {
        InputStream iconStream = this.getClass().getResourceAsStream("/" + this.getIconFilePath());
        if (iconStream == null)
        {
            File iconFile = new File(this.iconFilePath);
            if (iconFile.exists())
            {
            	
                iconStream = new FileInputStream(iconFile);
            }
        }

        iconTexture = TextureIO.newTexture(iconStream, false, null);
        iconTexture.bind(dc.getGL());
        this.iconWidth1 = iconTexture.getWidth();
        this.iconHeight1 = iconTexture.getHeight();
      //  System.out.println("in initTexture: "+iconWidth1+" "+iconHeight1);
       
        
        dc.getTextureCache().put(this.getIconFilePath(), iconTexture);
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
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
    // Enable texture anisotropy, improves "tilted" compass quality.
    int[] maxAnisotropy = new int[1];
    gl.glGetIntegerv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy, 0);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy[0]);

    
}

private void initializeTexture2(DrawContext dc)
{
	
	
    Texture iconTexture = dc.getTextureCache().getTexture(this.getIconFilePath2());
//  
    if (iconTexture != null){
   
     iconTexture.bind(dc.getGL());
    	return;
    }

    try
    {
        InputStream iconStream = this.getClass().getResourceAsStream("/" + this.getIconFilePath2());
        if (iconStream == null)
        {
            File iconFile = new File(this.iconFilePath2);
            if (iconFile.exists())
            {
            	
                iconStream = new FileInputStream(iconFile);
            }
        }

        iconTexture = TextureIO.newTexture(iconStream, false, null);
        iconTexture.bind(dc.getGL());
        this.iconWidth2 = iconTexture.getWidth();
        this.iconHeight2 = iconTexture.getHeight();
        
       
        
        dc.getTextureCache().put(this.getIconFilePath2(), iconTexture);
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
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
    // Enable texture anisotropy, improves "tilted" compass quality.
    int[] maxAnisotropy = new int[1];
    gl.glGetIntegerv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy, 0);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy[0]);

    
}

private void initializeTexture3(DrawContext dc)
{
	
	
    Texture iconTexture = dc.getTextureCache().getTexture(this.getIconFilePath3());
//  
    if (iconTexture != null){
   
     iconTexture.bind(dc.getGL());
    	return;
    }

    try
    {
        InputStream iconStream = this.getClass().getResourceAsStream("/" + this.getIconFilePath3());
        if (iconStream == null)
        {
            File iconFile = new File(this.iconFilePath3);
            if (iconFile.exists())
            {
            	
                iconStream = new FileInputStream(iconFile);
            }
        }

        iconTexture = TextureIO.newTexture(iconStream, false, null);
        iconTexture.bind(dc.getGL());
        this.iconWidth3 = iconTexture.getWidth();
        this.iconHeight3 = iconTexture.getHeight();
        
       
        
        dc.getTextureCache().put(this.getIconFilePath3(), iconTexture);
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
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
    // Enable texture anisotropy, improves "tilted" compass quality.
    int[] maxAnisotropy = new int[1];
    gl.glGetIntegerv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy, 0);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy[0]);

    
}



protected double iconScale = 0.1;
protected Vec4 locationCenter = null;

}
