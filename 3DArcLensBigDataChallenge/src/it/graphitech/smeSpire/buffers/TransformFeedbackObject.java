package it.graphitech.smeSpire.buffers;



import it.graphitech.smeSpire.SharedVariables;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

public class TransformFeedbackObject {

	GL2 gl;
	
	IntBuffer tfObject;
	//int usage = GL2.GL_DYNAMIC_READ;
	int usage = GL2.GL_STATIC_READ;
	int numTransformFeedbackBufferElements  = 500000;
	
	FloatBuffer emptyBuffer;
	
	public TransformFeedbackObject(GL2 gl) {
		
		// Create transform feedback buffer
		this.gl = gl;
		tfObject = IntBuffer.allocate(1);
	    gl.glGenBuffers(1, tfObject);
	    
	    emptyBuffer = SharedVariables.newFloatBuffer(numTransformFeedbackBufferElements);	
		
	    initTransformFeedbackBuffer();
	    System.out.println("TBO: "+tfObject.get(0));
	}
public int getNumTransformFeedbackBufferElements(){
	return numTransformFeedbackBufferElements;
}
	
	
	public void initTransformFeedbackBuffer(){
		gl.glBindBuffer(GL2.GL_TRANSFORM_FEEDBACK_BUFFER, tfObject.get(0));
		//IntBuffer feedback = newIntBuffer(10000);
		/*
		FloatBuffer feedback = SharedVariables.newFloatBuffer(numTransformFeedbackBufferElements);	
		 gl.glBufferData(GL2.GL_TRANSFORM_FEEDBACK_BUFFER, numTransformFeedbackBufferElements, feedback, usage);
		 */
		 gl.glBufferData(GL2.GL_TRANSFORM_FEEDBACK_BUFFER, numTransformFeedbackBufferElements, emptyBuffer, usage);
	}

	
	public void bind() 
	{
	   // gl.glBindTransformFeedbackNV(GL2.GL_TRANSFORM_FEEDBACK_NV, feedback.get(0));
	
		 gl.glBindBufferBase(GL2.GL_TRANSFORM_FEEDBACK_BUFFER, 0, tfObject.get(0));
	}

	public void unbind()
	{
		 gl.glBindBufferBase(GL2.GL_TRANSFORM_FEEDBACK_BUFFER, 0, 0);
	 //   gl.glBindTransformFeedbackNV(GL2.GL_TRANSFORM_FEEDBACK_NV, 0);
	}

	//primitiveMode:
	//GL_POINTS — GL_POINTS
	//GL_LINES — GL_LINES, GL_LINE_LOOP, GL_LINE_STRIP, GL_LINES_ADJACENCY, GL_LINE_STRIP_ADJACENCY
	//GL_TRIANGLES — GL_TRIANGLES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES_ADJACENCY, GL_TRIANGLE_STRIP_ADJACENCY
	
	
	public void begin(int primitiveMode)
	{
		gl.glBeginTransformFeedback(primitiveMode);
	}

	public void pause()
	{
	    gl.glPauseTransformFeedbackNV();
	}

	public void resume()
	{
	    gl.glResumeTransformFeedbackNV();
	}

	public void end()
	{
		gl.glEndTransformFeedback();
	}
	
	
	public void draw(){
		//gl.glDrawTransformFeedbackNV(GL2.GL_LINE_STRIP_ADJACENCY_ARB, 0);
		System.out.println("gl.glDrawTransformFeedbackNV(GL2.GL_LINE_STRIP_ADJACENCY_ARB, 0);");
	}
	
	//before glLinkProgram!
	public void setVaryings(int programId,String[] varyingNames){
		
		    int bufferMode= 
		    		GL2.GL_INTERLEAVED_ATTRIBS; //Write all attributes to a single buffer object.
		    //GL2.GL_SEPARATE_ATTRIBS; 			//Writes attributes to multiple buffer objects or at different offsets into a buffer.
		    
		    
		    System.out.println("program id: "+programId);
		    
		    gl.glTransformFeedbackVaryings(programId, 1, varyingNames, bufferMode);
	
	}
	
	  public void readFloatBuffer(){
	        
	         // Fetch and print results
	            FloatBuffer feedback = SharedVariables.newFloatBuffer(numTransformFeedbackBufferElements);
	          
	            gl.glGetBufferSubData(GL2.GL_TRANSFORM_FEEDBACK_BUFFER, 0, feedback.capacity(), feedback);

	            System.out.println("read transform feedback buffer");
	            for (int i = 0; i < 15; i++) {
	                System.out.println("-: "+ feedback.get(i));
	            }
	  }
	  
	  
	  public FloatBuffer getFloatBuffer(){
	        
	         // Fetch and print results
	            FloatBuffer feedback = SharedVariables.newFloatBuffer(numTransformFeedbackBufferElements);	          
	            gl.glGetBufferSubData(GL2.GL_TRANSFORM_FEEDBACK_BUFFER, 0, feedback.capacity(), feedback);
	            return feedback;           
	           
	  }
	  
	  
	  public IntBuffer getIntBuffer(){
	        
	         // Fetch and print results
	            IntBuffer feedback = SharedVariables.newIntBuffer(numTransformFeedbackBufferElements);	          
	            gl.glGetBufferSubData(GL2.GL_TRANSFORM_FEEDBACK_BUFFER, 0, feedback.capacity(), feedback);
	            return feedback;           
	           
	  }
	
	  public void readIntBuffer(){
	        
	         // Fetch and print results
	            IntBuffer feedback = SharedVariables.newIntBuffer(numTransformFeedbackBufferElements);
	          
	            gl.glGetBufferSubData(GL2.GL_TRANSFORM_FEEDBACK_BUFFER, 0, feedback.capacity(), feedback);

	            for (int i = 0; i < 15; i++) {
	            	int value = feedback.get(i);
	            	
	                System.out.println("-: "+ value);
	            	
	            }
	  }
	 
	
}
