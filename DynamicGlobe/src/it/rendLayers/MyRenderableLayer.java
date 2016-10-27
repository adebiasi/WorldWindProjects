package it.rendLayers;
import java.io.File;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

import it.LoadTerrain;
import it.SharedVariables;
import it.main.MainDeformableGlobe;
import it.monitor.PathTest;
import it.shader.GLSL;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;


public class MyRenderableLayer extends RenderableLayer{

	private GLSL glsl_trans;
	private final File vertfile_trans;
    private final File fragfile_trans;
   // private final File geomfile_densityMap;
    
    private final File shadPath;

   // public boolean isModified;
    PathTest pt;
	
    /*
	public MyRenderableLayer() {
		super();
		// TODO Auto-generated constructor stub
	}
*/
	 public MyRenderableLayer(File vertfile_trans,
				File fragfile_trans, File shadPath) {
			super();
			
			this.vertfile_trans = vertfile_trans;
			this.fragfile_trans = fragfile_trans;
			this.shadPath = shadPath;
			
			   pt = new PathTest();
		        pt.setFile(this.shadPath);
		        //pt.setSd(this);
		        pt.start();
		}
	
	 
		public void preRender(DrawContext dc) {
	       
			if (glsl_trans == null) {
	        	System.out.println("in preRender: glsl == null");
	        	glsl_trans = new GLSL(dc.getGL().getGL2());
	        	glsl_trans.loadVertexShader(vertfile_trans);
	        	glsl_trans.loadFragmentShader(fragfile_trans);
	        	
	        }
	       
	         if(pt.isModified){
	        	System.out.println("updating shaders");
	        	glsl_trans.updateVertexShader(vertfile_trans); 	
	        	glsl_trans.updateFragmentShader(fragfile_trans);	        	
	        	pt.isModified=false;
	   	 }
	        
	       
	    }
	 
	@Override
	protected void doRender(DrawContext dc) {
		// TODO Auto-generated method stub
		
		GL2 gl = dc.getGL().getGL2();
		
		if (glsl_trans != null) {
			glsl_trans.useShaders();
			glsl_trans.startShader();
    		
		
			
		MainDeformableGlobe.loadTerrain.initializeTexture(dc);		
		
		  Texture iconTexture = dc.getTextureCache().getTexture(SharedVariables.texturePath);
		  gl.glEnable(GL.GL_TEXTURE_2D);
		  gl.glActiveTexture(GL.GL_TEXTURE0);		  
		  iconTexture.bind(gl);
		  //gl.glBindTexture(GL2.GL_TEXTURE_2D, iconTexture.getTextureObject() );
	         	
		      	int handle = gl.glGetUniformLocation(glsl_trans.getProgramObject(), "currTexture");	            
	         	dc.getGL().getGL2().glUniform1i(handle, 0);
	    
		
		super.doRender(dc);
		
		
		   gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
           gl.glDisable(GL2.GL_TEXTURE_2D); // restore to default texture state
       
		
		glsl_trans.endShader();
		}
	}


	public GLSL getGlsl_trans() {
		return glsl_trans;
	}

	
	
}
