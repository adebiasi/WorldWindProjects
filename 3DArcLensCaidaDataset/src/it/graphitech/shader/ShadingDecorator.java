package it.graphitech.shader;



import it.graphitech.core.MyAbstractSceneController;
import it.graphitech.core.MyBasicSceneController;
import it.graphitech.monitor.PathTest;
import it.graphitech.smeSpire.SharedVariables;
import it.graphitech.smeSpire.buffers.TransformFeedbackObject;
import it.graphitech.smeSpire.layers.MyRenderableLayer;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.DrawContext;

public class ShadingDecorator {
    
    private GLSL glsl_pointDetection;
    private final File vertfile_pointDetection;
    private final File fragfile_pointDetection;
    private final File geomfile_pointDetection;
    private final File pointDetection_shadPath;
    PathTest pt_pointDetection;
   
    
    private GLSL glsl_HideLinesInsideLens;
    private final File vertfile_HideLinesInsideLens;
    private final File fragfile_HideLinesInsideLens;
    
    private final File hideLinesInsideLens_shadPath;
    PathTest pt_HideLinesInsideLens;
    
    
    public ShadingDecorator(final File pointDetection_shaderPath,    		
    		final File vertexShaderFile_pointDetection, 
                            final File fragmentShaderFile_pointDetection, 
                            final File geometryShaderFile_pointDetection ,
                            
                            final File hideLinesInsideLens_shaderPath,
                    		final File vertexShaderFile_HideLinesInsideLens, 
                                            final File fragmentShaderFile_HideLinesInsideLens                                            
    		) {
        vertfile_pointDetection = vertexShaderFile_pointDetection;
        fragfile_pointDetection = fragmentShaderFile_pointDetection;
        geomfile_pointDetection = geometryShaderFile_pointDetection;
        pointDetection_shadPath = pointDetection_shaderPath;
        pt_pointDetection = new PathTest();
        pt_pointDetection.setFile(pointDetection_shadPath);
        pt_pointDetection.start();
    
        
        vertfile_HideLinesInsideLens = vertexShaderFile_HideLinesInsideLens;
        fragfile_HideLinesInsideLens = fragmentShaderFile_HideLinesInsideLens;        
        hideLinesInsideLens_shadPath = hideLinesInsideLens_shaderPath;
        pt_HideLinesInsideLens = new PathTest();
        pt_HideLinesInsideLens.setFile(hideLinesInsideLens_shadPath);
        pt_HideLinesInsideLens.start();
   
    }

   
    public void preRender(DrawContext dc, Layer layer) {
    	
    	
       
        
        if (glsl_pointDetection== null) {
        	
        	System.out.println("in preRender: glsl == null");
        	glsl_pointDetection = new GLSL(dc.getGL().getGL2());
        	
        	glsl_pointDetection.loadVertexShader(vertfile_pointDetection);           
        	glsl_pointDetection.loadGeometryShader(geomfile_pointDetection,GL2.GL_LINE_STRIP_ADJACENCY_ARB, GL.GL_TRIANGLE_STRIP);
          	glsl_pointDetection.loadFragmentShader(fragfile_pointDetection);
            
        	
        	MyBasicSceneController.fboManager.transformFeedback = new TransformFeedbackObject(dc.getGL().getGL2());
		  	 String[] varyingNames = new String[] { "result" };
    	     	MyBasicSceneController.fboManager.transformFeedback.setVaryings(glsl_pointDetection.getProgramObject(), varyingNames);
        }
        
      
      
        if(pt_pointDetection.isModified){
        	System.out.println("updating advect vertices shaders");
       
    	glsl_pointDetection.updateVertexShader(vertfile_pointDetection);
    	glsl_pointDetection.updateGeometryShader(geomfile_pointDetection,GL2.GL_LINE_STRIP_ADJACENCY_ARB, GL.GL_TRIANGLE_STRIP);        	
    	glsl_pointDetection.updateFragmentShader(fragfile_pointDetection);
        
    	MyBasicSceneController.fboManager.transformFeedback = new TransformFeedbackObject(dc.getGL().getGL2());
		 String[] varyingNames = new String[] { "result" }; 
	MyBasicSceneController.fboManager.transformFeedback.setVaryings(glsl_pointDetection.getProgramObject(), varyingNames);
    	
    	pt_pointDetection.isModified=false;
   	 }
        
        
        
        
        
 if (glsl_HideLinesInsideLens== null) {
        	glsl_HideLinesInsideLens = new GLSL(dc.getGL().getGL2());        	
        	glsl_HideLinesInsideLens.loadVertexShader(vertfile_HideLinesInsideLens);           
        	glsl_HideLinesInsideLens.loadFragmentShader(fragfile_HideLinesInsideLens);
        
        	
        	((MyRenderableLayer)layer).setShader(glsl_HideLinesInsideLens);
        }
        
      
      
        if(pt_pointDetection.isModified){
        	System.out.println("in preRender: glsl == null");
        	glsl_HideLinesInsideLens = new GLSL(dc.getGL().getGL2());        	
        	glsl_HideLinesInsideLens.loadVertexShader(vertfile_HideLinesInsideLens);           
        	glsl_HideLinesInsideLens.loadFragmentShader(fragfile_HideLinesInsideLens);
    	
        	((MyRenderableLayer)layer).setShader(glsl_HideLinesInsideLens);
        	
    	pt_pointDetection.isModified=false;
   	 }
        
  
    	
        layer.preRender(dc);
    }

  
   
    public void render(DrawContext dc, Layer layer) {
       if(layer.isEnabled()){
    	
         
    	   
    	if(MyAbstractSceneController.renderTextureForPointsDetection){
    		
    		if(SharedVariables.lensIsActive){
    	if (glsl_pointDetection != null) {
        	
    		glsl_pointDetection.useShaders();
    		glsl_pointDetection.startShader();
     GL2 gl = dc.getGL().getGL2();
      
   
     int loc_screen_height = -1;
     int loc_screen_width = -1;
     int loc_lensCenter = -1;
     int loc_lensDiameter = -1;
     int loc_numElements = -1;
     
     Vec4 sp = SharedVariables.screenPoint;      
     java.awt.Rectangle viewport = dc.getView().getViewport();
      float screen_width=(float)viewport.getWidth();
      float screen_height=(float)viewport.getHeight();
      int numElements = SharedVariables.computeNumVerticesPerLine();
      
     loc_screen_height = gl.glGetUniformLocation(glsl_pointDetection.getProgramObject(), "screen_h");
     loc_screen_width = gl.glGetUniformLocation(glsl_pointDetection.getProgramObject(), "screen_w");
     loc_lensCenter = gl.glGetUniformLocation(glsl_pointDetection.getProgramObject(), "centerPos");
     loc_lensDiameter = gl.glGetUniformLocation(glsl_pointDetection.getProgramObject(), "lensDiameter");
     loc_numElements = gl.glGetUniformLocation(glsl_pointDetection.getProgramObject(), "numElements");
     
        gl.glUniform1f(loc_screen_height, screen_height);
     gl.glUniform1f(loc_screen_width, screen_width);
     gl.glUniform2f(loc_lensCenter, (float)sp.x,(float)sp.y);
     gl.glUniform1f(loc_lensDiameter, (float)SharedVariables.lense_h);
     gl.glUniform1i(loc_numElements, numElements);
     
   	gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
     
            MyBasicSceneController.fboManager.bindFramebufferObject_PointDetection(dc);
            layer.render(dc);
            MyBasicSceneController.fboManager.unbindFramebufferObject(dc);  
            //glsl.endShader();

            glsl_pointDetection.endShader();
           
        }
    		}
    	}else{
    		
    		
    		//if(SharedVariables.lensIsActive)
    		{
    	if (glsl_HideLinesInsideLens != null) {
    	
    		glsl_HideLinesInsideLens.useShaders();
    	//	glsl_HideLinesInsideLens.startShader();
    		layer.render(dc);
    	//	glsl_HideLinesInsideLens.endShader();
    	}
    	//dc.getGL().getGL2().glUseProgram(0);  
    	/*
    	MyBasicSceneController.fboManager.bindFramebufferObject_OnScreen(dc);
        layer.render(dc);
       //TestQuad.drawFullsizeQuad(dc);
        MyBasicSceneController.fboManager.unbindFramebufferObject(dc);
        */  
       }
    }
    
       }
    }
}