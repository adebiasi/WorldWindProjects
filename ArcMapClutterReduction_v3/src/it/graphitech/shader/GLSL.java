//////////////////////////////////////////////////////////////////////////////////////
//
//////////////////////////////////////////////////////////////////////////////////////
package it.graphitech.shader;

import java.nio.*;
import java.io.*;

import javax.media.opengl.*;

///////////////////////////////////////////////////////
//
///////////////////////////////////////////////////////
public class GLSL {

  private int programObject;
  private GL2 gl;
  private boolean vertexShaderSupported;  
  private boolean geomShaderSupported;  
  private int vs;
  private int gs;
  private int fs;
  private String vshaderSource=null;
  private String gshaderSource=null;
  private String fshaderSource=null;


  ///////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////
  public GLSL(GL2 gl) {
    this.gl = gl;
    String extensions = gl.glGetString(GL2.GL_EXTENSIONS);
   
    /*
    String[] ext = extensions.split(" ");
    
    for(String e : ext){
    	System.out.println("- "+e);
    }
    */
    vertexShaderSupported = extensions.indexOf("GL_ARB_vertex_shader") != -1;
    geomShaderSupported = extensions.indexOf("GL_ARB_geometry_shader4") != -1;
    programObject = gl.glCreateProgram();  
    vs=-1;
    gs=-1;
    fs=-1;
    
  
    System.out.println("vertexShaderSupported: "+vertexShaderSupported);
    System.out.println("geomShaderSupported: "+geomShaderSupported);
    
  }

  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public boolean isShaderSupported() {
	return vertexShaderSupported;
  }

  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public String getVShaderSource() {
	return vshaderSource;
  }
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public String getGShaderSource() {
	return gshaderSource;
  }
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public String getFShaderSource() {
	return fshaderSource;
  }

  //////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////
  public void createVertexShader(String shader) {
	  this.vshaderSource = shader;
	 
    vs = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
    System.out.println("creo vertex shader glCreateShader: "+vs);
	 
    gl.glShaderSource(vs, 1, new String[]{vshaderSource},(int[]) null, 0);
    gl.glCompileShader(vs);
    
    gl.glAttachShader(programObject, vs);  
    //checkCompilerOutput(gl, vs);
 //   checkCompilerOutput(gl, programObject);
  }
  //////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////
  public void createGeometryShader(String shader, int inputGeometryType, int outputGeometryType) {
	  this.gshaderSource = shader;
	  
	 
	 
    gs = gl.glCreateShader(GL2.GL_GEOMETRY_SHADER_ARB);
    System.out.println("creo geom shader glCreateShader: "+gs);
	
    gl.glShaderSource(gs, 1, new String[]{gshaderSource},(int[]) null, 0);
    gl.glCompileShader(gs);
    
    gl.glAttachShader(programObject, gs);  
    //checkCompilerOutput(gl, gs);
  //  checkCompilerOutput(gl, programObject);
    int inputType =inputGeometryType;
   // 		GL2.GL_LINE_STRIP;
   // GL2.GL_POINTS;
    
    		//GL2.GL_LINE_STRIP_ADJACENCY_ARB;
 
    //  GL2.GL_LINES_ADJACENCY_ARB;
    int outputType =outputGeometryType;
    	    //		GL.GL_POINT_SIZE;
    	   // GL.GL_LINES;
    	    //GL.GL_LINES_ADJACENCY_EXT;
    	//    GL.GL_TRIANGLE_STRIP;
    	    //GL.GL_TRIANGLES_ADJACENCY_EXT;
    	    
    
    /*
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_INPUT_TYPE_ARB,type);
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_OUTPUT_TYPE_ARB, type);
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_VERTICES_OUT_ARB, 96);
    */

  
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_INPUT_TYPE_ARB,inputType);
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_OUTPUT_TYPE_ARB, outputType);
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_VERTICES_OUT_ARB, 6);
    
  
  }
  //////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////
  public void createFragmentShader(String shader) {
	
	  this.fshaderSource = shader;
	  
  
    fs = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
    System.out.println("creo frag shader glCreateShader: "+fs);
	
    gl.glShaderSource(fs, 1, new String[]{fshaderSource},(int[]) null, 0);
 
    gl.glCompileShader(fs);
   
    gl.glAttachShader(programObject, fs);  
   // checkCompilerOutput(gl, fs);
   // checkCompilerOutput(gl, programObject);
  }
   
  public void updateVertexShader(String shader) {
	  this.vshaderSource = shader;
	  
	 
   
   // vs = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
   // System.out.println("creo vertex shader glCreateShader: "+vs);
	 
    gl.glShaderSource(vs, 1, new String[]{vshaderSource},(int[]) null, 0);
    gl.glCompileShader(vs);
  //  checkCompilerOutput(gl, vs);
    gl.glAttachShader(programObject, vs);  
    
  }
  //////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////
  public void updateGeometryShader(String shader, int inputGeometryType, int outputGeometryType) {
	  this.gshaderSource = shader;
	  
	 
	 
    //gs = gl.glCreateShader(GL2.GL_GEOMETRY_SHADER_ARB);
    //System.out.println("creo geom shader glCreateShader: "+gs);
	
    gl.glShaderSource(gs, 1, new String[]{gshaderSource},(int[]) null, 0);
    gl.glCompileShader(gs);
    checkCompilerOutput(gl, gs);
    gl.glAttachShader(programObject, gs);  

    int inputType =inputGeometryType;
   
   // GL2.GL_LINE_STRIP_ADJACENCY_ARB;
    //		GL2.GL_LINE_STRIP;
    	//	 GL2.GL_POINTS;
   // GL2.GL_LINES_ADJACENCY_ARB;
    //GL.GL_LINES_ADJACENCY_EXT;
    //GL.GL_TRIANGLE_STRIP;
    //GL.GL_TRIANGLES_ADJACENCY_EXT;
    int outputType =outputGeometryType;
    	    //		GL.GL_POINT_SIZE;
    	   // GL.GL_LINES;
    	    //GL.GL_LINES_ADJACENCY_EXT;
    //	    GL.GL_TRIANGLE_STRIP;
    	    //GL.GL_TRIANGLES_ADJACENCY_EXT;
    	    
    
    /*
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_INPUT_TYPE_ARB,type);
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_OUTPUT_TYPE_ARB, type);
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_VERTICES_OUT_ARB, 96);
    */

  
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_INPUT_TYPE_ARB,inputType);
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_OUTPUT_TYPE_ARB, outputType);
    gl.glProgramParameteriARB(programObject, GL2.GL_GEOMETRY_VERTICES_OUT_ARB, 6);
    
  
  }
  //////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////
  public void updateFragmentShader(String shader) {
	
	  this.fshaderSource = shader;
	  
  
   // fs = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
   // System.out.println("creo frag shader glCreateShader: "+fs);
	
    gl.glShaderSource(fs, 1, new String[]{fshaderSource},(int[]) null, 0);
 
    gl.glCompileShader(fs);
   // checkCompilerOutput(gl, fs);
    gl.glAttachShader(programObject, fs);  
  }
   
  
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public void loadVertexShader(java.io.File file)
  {
	  System.out.println("VERTEX SHADER");
	  
	if(file==null) return;
    vshaderSource="";
    try {
	    BufferedReader br = new BufferedReader( new FileReader(file) );
	    String line="";
	    while(line!=null) {
		line = br.readLine();
		if(line!=null) {
			System.out.println(line);
			vshaderSource = vshaderSource.concat( line +"\n");
		}
	    }
	    br.close();
    } catch(Exception e) {
	e.printStackTrace();
      }

    createVertexShader(vshaderSource);
  
  }
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public void loadGeometryShader(java.io.File file, int inputGeometryType, int outputGeometryType)
  {
	  System.out.println("GEOMETRY SHADER");
	  
	if(file==null) return;
    gshaderSource="";
    try {
	    BufferedReader br = new BufferedReader( new FileReader(file) );
	    String line="";
	    while(line!=null) {
		line = br.readLine();
		if(line!=null){
			System.out.println(line);
			gshaderSource = gshaderSource.concat( line +"\n");
		}
	    }
	    br.close();
    } catch(Exception e) {
	e.printStackTrace();
      }

    createGeometryShader(gshaderSource,inputGeometryType,outputGeometryType);
  
  }
 
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  
 
  
  public void loadFragmentShader(java.io.File file)
  {
	  System.out.println("FRAGMENT SHADER");
	  
	if(file==null) return;
    fshaderSource="";
    try {
	    BufferedReader br = new BufferedReader( new FileReader(file) );
	    String line="";
	    while(line!=null) {
		line = br.readLine();

		if(line!=null) {
			System.out.println("line: "+line);
			fshaderSource = fshaderSource.concat( line +"\n");
		}
	    }
	    br.close();
    } catch(Exception e) {
	e.printStackTrace();
      }

    createFragmentShader(fshaderSource);
    
 
  }
 
  public void updateVertexShader(java.io.File file)
  {
	  System.out.println("VERTEX SHADER");
	  
	if(file==null) return;
    vshaderSource="";
    try {
	    BufferedReader br = new BufferedReader( new FileReader(file) );
	    String line="";
	    while(line!=null) {
		line = br.readLine();
		if(line!=null) {
			System.out.println(line);
			vshaderSource = vshaderSource.concat( line +"\n");
		}
	    }
	    br.close();
    } catch(Exception e) {
	e.printStackTrace();
      }

    updateVertexShader(vshaderSource);
  
  }
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public void updateGeometryShader(java.io.File file, int inputGeometryType, int outputGeometryType)
  {
	  System.out.println("GEOMETRY SHADER");
	  
	if(file==null) return;
    gshaderSource="";
    try {
	    BufferedReader br = new BufferedReader( new FileReader(file) );
	    String line="";
	    while(line!=null) {
		line = br.readLine();
		if(line!=null){
			System.out.println(line);
			gshaderSource = gshaderSource.concat( line +"\n");
		}
	    }
	    br.close();
    } catch(Exception e) {
	e.printStackTrace();
      }

    updateGeometryShader(gshaderSource,inputGeometryType,outputGeometryType);
  
  }
 
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  
  
  
  public void updateFragmentShader(java.io.File file)
  {
	  System.out.println("FRAGMENT SHADER");
	  
	if(file==null) return;
    fshaderSource="";
    try {
	    BufferedReader br = new BufferedReader( new FileReader(file) );
	    String line="";
	    while(line!=null) {
		line = br.readLine();

		if(line!=null) {
			System.out.println("line: "+line);
			fshaderSource = fshaderSource.concat( line +"\n");
		}
	    }
	    br.close();
    } catch(Exception e) {
	e.printStackTrace();
      }

    updateFragmentShader(fshaderSource);
    
    
  }
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public int getAttribLocation(String name)
  {
    return(gl.glGetAttribLocation(programObject,name));
  }
   
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public int getUniformLocation(String name)
  {
    return(gl.glGetUniformLocation(programObject,name));
  }

  //////////////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////////////
  public int getProgramObject() {
	return programObject;
  }
     
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public void useShaders()
  {
	 // System.out.println("useShaders");
    gl.glLinkProgram(programObject);
    checkCompilerOutput(gl, programObject);
    gl.glValidateProgram(programObject);
  
  }
   
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public void startShader()
  {
    gl.glUseProgram(programObject);  
  }
   
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public void endShader()
  {
    gl.glUseProgram(0);  
  }
   
  
  public void setUniform1i(String inName,int inValue) {
     /*
	  int tUniformLocation = gl.glGetUniformLocationARB(programObject,inName);
      if (tUniformLocation != -1) {
          gl.glUniform1iARB(tUniformLocation, inValue);
      } else {
          System.out.println("UNIFORM COULD NOT BE FOUND! NAME="+inName);
      }
      */
	  int tUniformLocation = gl.glGetUniformLocation(programObject,inName);
      if (tUniformLocation != -1) {
          gl.glUniform1i(tUniformLocation, inValue);
      } else {
          System.out.println("UNIFORM COULD NOT BE FOUND! NAME="+inName);
      }
   
  }
  ///////////////////////////////////////////////////////////
  //
  ///////////////////////////////////////////////////////////
  public void checkCompilerOutput(GL2 gl, int obj)  
  {
	  
	  IntBuffer isLinked = IntBuffer.allocate(1);
	  gl.glGetProgramiv(obj, GL2.GL_LINK_STATUS, isLinked);
	  
	    if(isLinked.get() == 0)
	    {
	    	System.out.println("GL_LINK_STATUS");
	    }
	  
    IntBuffer ib = IntBuffer.allocate(1);
    gl.glGetProgramiv(obj, GL2.GL_INFO_LOG_LENGTH, ib);
 
    

    
    int length = ib.get();
    if (length <= 1)  {
    	//System.out.println("len = 0");
	 return;
    }
    ByteBuffer compiler_buffer = ByteBuffer.allocate(length);
    ib.flip();
    gl.glGetProgramInfoLog(obj, length, ib, compiler_buffer);
    byte[] infoBytes = new byte[length];
    compiler_buffer.get(infoBytes);
    System.out.println("GLSL Error " + new String(infoBytes));
  }  
}
