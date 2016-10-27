package it.graphitech.smeSpire.buffers;


import it.graphitech.smeSpire.Main_STANDALONE_CAIDA;



import java.awt.Component;
import java.io.File;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import com.jogamp.common.util.VersionNumber;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class FBOManager {

	protected int framebufferObject_PointDetection;
	protected int framebufferObject_DensityMap;
	
	static public int h_window_in_pixels = 0;
	static public int w_window_in_pixels = 0;
	private boolean firstTime = false;
	// static public final Object textureCacheKey_DensityMap = new Object();
	// static public final Object textureCacheKey_DepthMap = new Object();
	// static public final Object textureCacheKey_GradientMap = new Object();
	// static public final Object textureCacheKey_OnScreen = new Object();
	
	 

	public static int pointDetectionTextureObject;
	public static int depthTextureObject;
	
	public TransformFeedbackObject transformFeedback;
	
	public void setup(DrawContext dc){
		if(!firstTime){
			
			GLProfile profile = Configuration.getMaxCompatibleGLProfile();
			String profileName = profile.getName();
			
			System.out.println("profileName: "+profileName);
			
			String openGL_version = dc.getGL().glGetString(GL2.GL_VERSION);
			String GLSL_version = dc.getGL().glGetString(GL2.GL_SHADING_LANGUAGE_VERSION);
			
			VersionNumber vers_number = dc.getGL().getGL2().getContext().getGLSLVersionNumber();
			String vers_str = dc.getGL().getGL2().getContext().getGLSLVersionString();
			
		
			
			System.out.println("openGL_version: "+openGL_version);
			System.out.println("GLSL_version: "+GLSL_version);
			System.out.println("getGLSLVersionNumber: "+vers_number);
			System.out.println("getGLSLVersionString: "+vers_str);
			
			
            h_window_in_pixels =((Component)Main_STANDALONE_CAIDA.wwd).getSize().height;
            w_window_in_pixels =((Component)Main_STANDALONE_CAIDA.wwd).getSize().width;
         
			System.out.println("h_window_in_pixels: "+h_window_in_pixels);
            System.out.println("w_window_in_pixels: "+w_window_in_pixels);
		//createFBO
            createFramebufferObjects(dc);
		
		
	  
        int pointDetection = this.createTexture((int)w_window_in_pixels, (int)h_window_in_pixels,GL2.GL_RGBA);
      		pointDetectionTextureObject = pointDetection;
          //    dc.getTextureCache().put(textureCacheKey_OnScreen, texture_on_Screen);
        
        //createDepthTexture
       
      		//Texture depth_texture = this.createDepthTexture((int)w_window_in_pixels, (int)h_window_in_pixels);
        //dc.getTextureCache().put(textureCacheKey_DepthMap, depth_texture);
		int depth_texture = this.createTexture((int)w_window_in_pixels, (int)h_window_in_pixels,GL2.GL_DEPTH_COMPONENT);
      		depthTextureObject = depth_texture;
      //createDepthTexture
       // Texture gradient_texture = this.createTexture((int)w_window_in_pixels, (int)h_window_in_pixels);
       // int gradient_texture = this.createTexture((int)w_window_in_pixels, (int)h_window_in_pixels,GL2.GL_RGBA);
        //attachTexturetoColorFramebuffer	
       
      
		
		 bindFramebufferObject_PointDetection(dc);
			
			attachTexturetoColorFramebuffer(dc, pointDetectionTextureObject);
			attachTexturetoToDepth(dc, depth_texture);
			//dc.getGL().getGL2().glDrawBuffer(GL2.GL_NONE); 
			
			unbindFramebufferObject(dc);
		
		
		firstTime=true;
		}
	}
	
	 protected void attachTexturetoColorFramebuffer(DrawContext dc, int textureObject)
	    {
	    	
	    	GL2 gl = dc.getGL().getGL2();

	        // Attach the texture as color attachment 0 to the framebuffer.
	        if (textureObject != -1)
	        {
	        	System.out.println("attachTexturetoColorFramebuffer with TextureObject: "+textureObject);
	            gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_2D,
	            		textureObject, 0);
	            this.checkFramebufferStatus(dc);
	        }
	        // If the texture is null, detach color attachment 0 from the framebuffer.
	        else
	        {
	        	System.out.println("detach color attachment 0 from the framebuffer");
	            gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_2D, 0, 0);
	        }
	    }
	 

	 protected void attachTexturetoToDepth(DrawContext dc, int texture)
	    {
	    	GL2 gl = dc.getGL().getGL2();

	        // Attach the texture as color attachment 0 to the framebuffer.
	        if (texture != -1)
	        {
	        	System.out.println("BIND FRAMEBUFFER DEPTH");
	        	gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER,  GL2.GL_DEPTH_ATTACHMENT, GL2.GL_TEXTURE_2D, texture, 0);
	        	//gl.glFramebufferTextureARB(GL2.GL_FRAMEBUFFER,  GL2.GL_DEPTH_ATTACHMENT, texture.getTextureObject(),0);
	       // 	dc.getGL().getGL2().glDrawBuffer(GL2.GL_NONE); 
	        	
	            this.checkFramebufferStatus(dc);
	        }
	        // If the texture is null, detach color attachment 0 from the framebuffer.
	        else
	        {
	        	System.out.println("UNBIND FRAMEBUFFER DEPTH");
	        	//System.out.println("TEXTURE NULL!!!");
	           gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_TEXTURE_2D, 0, 0);
	          // gl.glFramebufferTextureARB(GL2.GL_FRAMEBUFFER,  GL2.GL_DEPTH_ATTACHMENT, 0, 0);
	       
	        }
	    }
	 
	
	  protected void checkFramebufferStatus(DrawContext dc)
	    {
	        int status = dc.getGL().glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);

	        switch (status)
	        {
	            // Framebuffer is configured correctly and supported on this hardware.
	            case GL2.GL_FRAMEBUFFER_COMPLETE:
	            {
	            	//System.out.println("FramebufferStatus ok");
	                break;
	            }
	            // Framebuffer is configured correctly, but not supported on this hardware.
	            case GL2.GL_FRAMEBUFFER_UNSUPPORTED:
	                throw new IllegalStateException(getFramebufferStatusString(status));
	                // Framebuffer is configured incorrectly. This should never happen, but we check anyway.
	            default:
	                throw new IllegalStateException(getFramebufferStatusString(status));
	        }
	    }
	  protected static String getFramebufferStatusString(int status)
	    {
	        switch (status)
	        {
	            case GL2.GL_FRAMEBUFFER_COMPLETE:
	                return Logging.getMessage("OGL.FramebufferComplete");
	            case GL2.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
	                return Logging.getMessage("OGL.FramebufferIncompleteAttachment");
	            case GL2.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
	                return Logging.getMessage("OGL.FramebufferIncompleteDimensions");
	            case GL2.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
	                return Logging.getMessage("OGL.FramebufferIncompleteDrawBuffer");
	       //     case GL2.GL_FRAMEBUFFER_INCOMPLETE:
	         //       return Logging.getMessage("OGL.FramebufferIncompleteDuplicateAttachment");
	            case GL2.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
	                return Logging.getMessage("OGL.FramebufferIncompleteFormats");
	            case GL2.GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT_ARB:
	                return Logging.getMessage("OGL.FramebufferIncompleteLayerCount");
	            case GL2.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS_ARB:
	                return Logging.getMessage("OGL.FramebufferIncompleteLayerTargets");
	            case GL2.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
	                return Logging.getMessage("OGL.FramebufferIncompleteMissingAttachment");
	            case GL2.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
	                return Logging.getMessage("OGL.FramebufferIncompleteMultisample");
	            case GL2.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
	                return Logging.getMessage("OGL.FramebufferIncompleteReadBuffer");
	            case GL2.GL_FRAMEBUFFER_UNSUPPORTED:
	                return Logging.getMessage("OGL.FramebufferUnsupported");
	            default:
	                return null;
	        }
	    }
	protected void createFramebufferObjects(DrawContext dc)
    {
        // Binding a framebuffer object causes all GL operations to operate on the attached textures and renderbuffers
        // (if any).
	int numFrameBuffers= 1;
		
        int[] framebuffers = new int[numFrameBuffers];

        GL2 gl = dc.getGL().getGL2();
        gl.glGenFramebuffers(numFrameBuffers, framebuffers, 0);
        
     
        this.framebufferObject_PointDetection = framebuffers[0];
        if (this.framebufferObject_PointDetection == 0)
        {
            throw new IllegalStateException("Frame Buffer Object Gradient not created.");
        }
    }
	
	 protected int createTexture(int width, int height, int format)
	    {	    	
		 System.out.println("createTexture");
		 
	    	 GL2 gl = GLContext.getCurrent().getGL().getGL2();
	 
	    	//
	   
	        int[] tmp = new int[1];
            gl.glGenTextures(1, tmp, 0);
           int textureObject = tmp[0];
   
           System.out.println("creo texture: "+textureObject);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, textureObject);
	   
            
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);

	        if(format==GL2.GL_R32F){
	        	System.out.println("in formato R float");
	        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, format,width, height, 0,GL2.GL_RED,  GL2.GL_FLOAT, null);
	        }else
	        	if(format==GL2.GL_RG32F){
	        		System.out.println("in formato R,G float");
	        	//gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, format,width, height, 0,format,  GL2.GL_FLOAT, null);	
	        	gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, format, width, height, 0, GL2.GL_RG, GL2.GL_FLOAT, null);
	        }else{
	        	System.out.println("in formato R,G,B");
	        	gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, format,width, height, 0,format,  GL2.GL_UNSIGNED_BYTE, null);
	        }
	         return textureObject;
	    }
	 
	 

		  
	 
	 
	 protected Texture createFloatingPointTexture(int width, int height)
	    {	    	
		 System.out.println("createTexture");
		 
	    	 GL2 gl = GLContext.getCurrent().getGL().getGL2();
	 
	        TextureData td = new TextureData(
	        		gl.getGLProfile(), 
	        		GL2.GL_RGB32F,           // internal format 
	        		width, height,
	        		0, 
	        		 GL2.GL_RGB32F,           // pixel format
	        		 GL2.GL_UNSIGNED_BYTE,  // pixel type
	        		false,  // mipmap
	        		false, false, // dataIsCompressed, mustFlipVertically
	        		null, null); // buffer, flusher
     
	        Texture t = TextureIO.newTexture(td);
	        t.bind(gl);

	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);

	        gl.glCopyTexImage2D(GL2.GL_TEXTURE_2D, 0, td.getInternalFormat(), 0, 0, td.getWidth(), td.getHeight(),
	            td.getBorder());
        
	        return t;
	    }
	

	  
	    
	    protected Texture createSingleChannelTexture(int width, int height)
	    {
	    	
	    	 GL2 gl = GLContext.getCurrent().getGL().getGL2();
	    	
	    	

	        TextureData td = new TextureData(
	        		gl.getGLProfile(), 
	        		GL2.GL_RED,           // internal format 
	        		width, height,
	        		0, 
	        		GL2.GL_RED,           // pixel format
	        		 GL2.GL_UNSIGNED_BYTE,  // pixel type
	        		false,  // mipmap
	        		false, false, // dataIsCompressed, mustFlipVertically
	        		null, null); // buffer, flusher

	     //   gl3.glTexImage2D(GL3.GL_TEXTURE_RECTANGLE, 0, GL3.GL_R16I, width, height, 0, GL3.GL_RED_INTEGER, GL3.GL_SHORT, null);
	       
	        
	        Texture t = TextureIO.newTexture(td);
	        t.bind(gl);

	        
	       

	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
	        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);

	        gl.glCopyTexImage2D(GL2.GL_TEXTURE_2D, 0, td.getInternalFormat(), 0, 0, td.getWidth(), td.getHeight(),
	            td.getBorder());
	        
	        
	        return t;
	    }
	   
	 public void bindFramebufferObject_PointDetection(DrawContext dc){
	    	GL2 gl = dc.getGL().getGL2(); 
	    	gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, framebufferObject_PointDetection);
	    }
	 public void unbindFramebufferObject(DrawContext dc)
	    {	   
	        GL2 gl = dc.getGL().getGL2();
	        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	      
	    }
}
