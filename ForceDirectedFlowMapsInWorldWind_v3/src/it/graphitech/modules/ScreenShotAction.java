/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package it.graphitech.modules;

import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.WorldWindow;
import it.graphitech.Variables;

import javax.swing.*;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL;

import com.jogamp.opengl.util.awt.Screenshot;

import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author tag
 * @version $Id: ScreenShotAction.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class ScreenShotAction implements RenderingListener
{
	static String imageFormat = 
			//"png";
			"jpg";
    WorldWindow wwd;
    private File snapFile;
   

    public ScreenShotAction(WorldWindow wwd)
    {
      
        this.wwd = wwd;
        
        takeScreenShots();
    }
    
    
    public void takeScreenShots(){
    	// Wait until the user provide the file and its path
    	String filepath = "results/"+Variables.executionDate+"/"+Variables.fileName+"_"+Variables.executionDate+"_"+Variables.renderAtIteration+"."+imageFormat;

    	if(Variables.executionDate.compareTo("")==0){
    		filepath = "results/"+"NoDate"+"/"+Variables.fileName+"_"+Variables.executionDate+"_"+Variables.renderAtIteration+"."+imageFormat;
    	}
    	
    	// Check if the user cancel the operation
    	if(filepath == null)
    	return;

    	// Create a file
    	snapFile = new File(filepath);
    	
    	 this.wwd.removeRenderingListener(this); // ensure not to add a duplicate
         this.wwd.addRenderingListener(this);
    }

    

   

    public void stageChanged(RenderingEvent event)
    {
        if (event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP) && this.snapFile != null)
        {
            try
            {
                GLAutoDrawable glad = (GLAutoDrawable) event.getSource();
                int[] viewport = new int[4];
                glad.getGL().glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
                Screenshot.writeToFile(this.snapFile, viewport[2] + 10, viewport[3], false);
                glad.getGL().glViewport(0, 0, glad.getWidth(), glad.getHeight());
                System.out.printf("Image saved to file %s\n", this.snapFile.getPath());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                this.snapFile = null;
                this.wwd.removeRenderingListener(this);
            }
        }
    }
//
//    private String composeSuggestedName()
//    {
//        String baseName = "WWJSnapShot";
//        String suffix = ".png";
//
//        File currentDirectory = this.fileChooser.getCurrentDirectory();
//
//        File candidate = new File(currentDirectory.getPath() + File.separatorChar + baseName + suffix);
//        for (int i = 1; candidate.exists(); i++)
//        {
//            String sequence = String.format("%03d", i);
//            candidate = new File(currentDirectory.getPath() + File.separatorChar + baseName + sequence + suffix);
//        }
//
//        return candidate.getPath();
//    }
}
