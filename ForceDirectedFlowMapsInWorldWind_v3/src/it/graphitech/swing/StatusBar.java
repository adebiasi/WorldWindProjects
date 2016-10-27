/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.swing;

import it.graphitech.Variables;
import it.graphitech.modules.MiddleNodeGeneration;

import javax.swing.*;
import java.awt.*;
/**
 * @author tag
 * @version $Id: StatusBar.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class StatusBar extends JPanel 
{
   


    static JLabel numIterations = new JLabel("numIterations: 0");
    static JLabel elapsedTime = new JLabel("elapsedTime: 0 sec.");
    static JLabel totalEnergy = new JLabel("totalEnergy: 0 ");
   
    static JLabel electr_Energy = new JLabel("electr_Energy: 0 ");
    static JLabel repulsiveEnergy = new JLabel("repul_Energy: 0 ");
    static JLabel stressEnergy = new JLabel("stress_Energy: 0 ");
    
    static JLabel numNodes = new JLabel("numNodes: 0 ");
    //protected final JLabel altDisplay = new JLabel("c");
    //protected final JLabel eleDisplay = new JLabel("d");

  

    public StatusBar()
    {
        super(new GridLayout(1, 0));

       

        numIterations.setHorizontalAlignment(SwingConstants.CENTER);
        elapsedTime.setHorizontalAlignment(SwingConstants.CENTER);
        numNodes.setHorizontalAlignment(SwingConstants.CENTER);
        
        electr_Energy.setHorizontalAlignment(SwingConstants.CENTER);
        repulsiveEnergy.setHorizontalAlignment(SwingConstants.CENTER);
        stressEnergy.setHorizontalAlignment(SwingConstants.CENTER);
        
        totalEnergy.setHorizontalAlignment(SwingConstants.CENTER);
        //eleDisplay.setHorizontalAlignment(SwingConstants.CENTER);

        this.add(numIterations);
        this.add(elapsedTime);
        this.add(numNodes);
        
        this.add(electr_Energy);
        this.add(repulsiveEnergy);
        this.add(stressEnergy);
        
        this.add(totalEnergy);
        //this.add(lonDisplay);
        //this.add(eleDisplay);
        


        

    }

   

    public static void updateStatusBar(){
    	float elapsedTimeSec = Variables.elapsedTime/1000F;		
		StatusBar.numIterations.setText("num Iteration: "+Variables.num_iteration);
		StatusBar.elapsedTime.setText("elapsed time: "+elapsedTimeSec+" sec.");
		StatusBar.totalEnergy.setText("TotalEnergy: "+Variables.total_energy);
		
		StatusBar.electr_Energy.setText("electr_Energy: "+Variables.total_electr_force);
		StatusBar.repulsiveEnergy.setText("repul_Energy: "+Variables.total_repulsive_force);
		StatusBar.stressEnergy.setText("stress_Energy: "+Variables.total_stress_force);
		
		StatusBar.numNodes.setText("num Nodes: "+ MiddleNodeGeneration.getNumNodes());
    }

    
}
