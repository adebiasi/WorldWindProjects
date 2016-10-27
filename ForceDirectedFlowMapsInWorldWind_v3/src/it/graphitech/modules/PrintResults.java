package it.graphitech.modules;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import it.graphitech.Variables;


public class PrintResults {

	static BufferedWriter out;
	static String imageFormat = 
			//"png";
			"jpg";
	
	private static void assignDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		
		String date_str = dateFormat.format(date);
		
		Variables.executionDate=date_str;
	}
	
	public static void createFile(String fileName){
		
		assignDate();
		
		try{
			File dir = new File("results/"+Variables.executionDate+"/");
			dir.mkdir();
			  // Create file 
			  FileWriter fstream = new FileWriter("results/"+Variables.executionDate+"/"+fileName+"_"+Variables.executionDate+".txt");
			   out = new BufferedWriter(fstream);
			  
			  }catch (Exception e){//Catch exception if any
			  System.err.println("Error in createFile: " + e.getMessage());
			  }
		
	}
	
	
	public static void closeFile( ){
		try{
		//Close the output stream
		  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error in CloseFile: " + e.getMessage());
			  }
	}
	
	public static void writeVariables(){
		
		String lengthMiddleSegment = "lengthMiddleSegment: "+String.valueOf(Variables.lengthMiddleSegment);
		String step_energy = "step_energy: "+String.valueOf(Variables.curr_energy);
				
		String electrostatic_force_factor = "electrostatic_force_factor: "+String.valueOf(Variables.electrostatic_force_factor);
		String spring_force_factor = "spring_force_factor: "+String.valueOf(Variables.spring_force_factor);
		String reject_force_factor = "reject_force_factor: "+String.valueOf(Variables.reject_force_factor);
		
		String distanceRejectRadius = "distanceRejectRadius: "+String.valueOf(Variables.rejectBufferInMeters);
		
		String samePositionRadius = "distanceSamePosition: "+String.valueOf(Variables.samePositionDistanceInMeters);
		
		writeLine(lengthMiddleSegment);
		writeLine(step_energy);
		writeLine(electrostatic_force_factor);
		writeLine(spring_force_factor);
		writeLine(reject_force_factor);
		writeLine(distanceRejectRadius);
		writeLine(samePositionRadius);
		writeFirstLine();
	}
	private static void writeFirstLine(){
		String row = "Iteration"+";"+"time"+";"+"energy"+";"+"num nodes"+";"+"reject force is enabled"+";"+"spring force is enabled"+";"+"electrostatic force is enabled"+";"+"str_force"+";"+"rep_force"+";"+"elec_force";
    	writeLine(row);
	}
	
	public static void writeResults(){
		
		    	float elapsedTimeSec = Variables.elapsedTime/1000F;
		    	
		    	DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
		    	otherSymbols.setDecimalSeparator(',');
		    	otherSymbols.setGroupingSeparator('.'); 
		    	DecimalFormat df = new DecimalFormat("#.##", otherSymbols);
		    	
		    	
		    	
		    	String time = df.format(elapsedTimeSec);
		    	String energy = df.format(Variables.total_energy);
		    	
		    	String stress_energy = df.format(Variables.total_stress_force);
		    	String repulsive_energy = df.format(Variables.total_repulsive_force);
		    	String electr_energy = df.format(Variables.total_electr_force);
		    	
		    	String row = Variables.num_iteration+";"+time+";"+energy+";"+MiddleNodeGeneration.getNumNodes()+";"+Variables.isStable+";"+Variables.enableSpringForce+";"+Variables.enableElectrostaticForce+";"+stress_energy+";"+repulsive_energy+";"+electr_energy;


		    	writeLine(row);
		    
	}
	
	
	private static void writeLine(String line){
		try{
		out.write(line);
		out.newLine();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error in writeline: " + e.getMessage());
			  }
		  
	}
	
	public static void screenShootCapture(){
		new ScreenShotAction(Render.wwd);
	}
	
	
}
