package it.graphitech.worldwind;

import javax.swing.SwingUtilities;

import it.graphitech.Variables;
import it.graphitech.modules.MainModule;
import it.graphitech.modules.PrintResults;
import it.graphitech.modules.Render;
import it.graphitech.swing.ButtonPanel;
import it.graphitech.worldwind.FlowMapsInWorldWind_v3.AppFrame;

//import it.graphitech.integration.TrafficSim.AppFrame;

//this thread manage the rendering of the cars
public class WorkerThread extends Thread {

	private volatile boolean enableRender = false;
	private volatile boolean enableScreenshot = false;

	

	public WorkerThread() {
		
	
		
	}

	public void run() {
		while (true) {
		
			//enableRender=true;
			//SwingUtilities.invokeLater(new Runnable() {
				//public void run() {
					//System.out.println("rendering...");
					//Render.update();
					//System.out.println("START");
			
					MainModule.iterate();    
					if(enableRender){
						MainModule.updateRendering();
						enableRender=false;
			}
					
					if(enableScreenshot){
						MainModule.updateRendering();
						PrintResults.screenShootCapture();
						enableScreenshot=false;
					}
				
				//}
			//});
			try {
				Thread.sleep(Variables.updateRateThread);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void render()
    {
		enableRender = true;
    }

	public void takeScreenShots()
    {
		enableScreenshot = true;
    }
	

}
