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
public class ScreenshotsThread extends Thread {

	private volatile boolean enableRender = false;
	

	

	public ScreenshotsThread() {
		
	
		
	}

	public void run() {
		while (true) {
		
			if(MainModule.isActive){
				
				System.out.println("take screenshots");
				MainModule.updateRenderingDuringExecution();
		PrintResults.screenShootCapture();
				
			}
				
				//}
			//});
			try {
				Thread.sleep(Variables.updateScreenshotsRateThread);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void render()
    {
		enableRender = true;
    }

   
	

}
