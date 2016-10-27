package it.LoadData;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;

import it.entities.Link;
import it.entities.Route;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class Importer {

	//static public HashMap<String, Position> nodes;
	//static public ArrayList<String> entries;
	static public HashMap<String, Route> links;

	static private int idIndex=0;
	

	static private int airlineIndex=1;
	static private int airlineIDIndex=2;
	static private int airlineNameIndex=3;
	static private int airlineActiveIndex=4;
	static private int airlineCountryIndex=5;
	static private int airlineOccurIndex=6;
	
	
	static private int fromIDIndex=9;
	static private int fromLatIndex=11;
	static private int fromLonIndex=12;
	static private int toIDIndex=15;
	static private int toLatIndex=17;
	static private int toLonIndex=18;
	
	static private int routeCodeShareIndex=19;
	static private int routeStopsIndex=20;
	
	static private int routeEquipmentIndex=21;
	static private int routeDistanceIndex=22;
	
	static BufferedWriter out;
	static BufferedReader br;
	
	//static String dataPath = "input/AllData.csv";
	static String dataPath = "input/SubsetData2.csv";
	
	public static void main(String[] args) {
		
		
	//	String dataPath = "input/a.txt";
		
		calculate_and_export_Distances(dataPath);
		
		/*
		LatLon from = LatLon.fromDegrees(-37.673333,	144.843333);
		LatLon to = LatLon.fromDegrees(-35.306944,	149.195);
		
		
double distance= Position.ellipsoidalDistance(from, to, 6378137, 6356752.3);
System.out.println("distance: "+distance);
*/
		
	}
	
	
	
	static public void loadRoutesFromFILE(){
		links=new HashMap<String, Route>();
		
		loadRoutesFromFILE(dataPath);
		 System.out.println("all arcs");
         
         System.out.println("# archi: "+links.size());
	}
	
	static public void loadRoutesFromFILE(String dataPath) {
	
		System.out.println("loadRoutesFromFILE ");
		
		int i = 1;
		try {

		openInFile(dataPath);
		
		
			String strLine;

			while ((strLine = br.readLine()) != null) {
				
				if(i!=1){
				System.out.println("line: "+strLine);
				String[] data = strLine.split(";");

				//Link entity = createEntity(data);

				Route route = createRoute(data);
				
				// insert node
				//insertFrom_To_Nodes(entity);
				// insert link
				insertLink(route);
				}
i++;
			}

			System.out.println("process finished");

			//in.close();
			closeInFile();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error in loadRoutesFromFILE: " + e.getMessage() + " " + i);
		}
	}

	
	/*
	static public void loadFlowsDataFromFILE() {
		links=new HashMap<String, Link>();
		loadFlowsDataFromFILE(dataPath);
	}
	
	
	
	
	static public void loadFlowsDataFromFILE(String dataPath) {
		int i = 1;
		try {

		openInFile(dataPath);
		
		
			String strLine;

			while ((strLine = br.readLine()) != null) {
				
				if(i!=1){
				System.out.println("line: "+strLine);
				String[] data = strLine.split(";");

				Link entity = createEntity(data);


				// insert node
				//insertFrom_To_Nodes(entity);
				// insert link
				insertLink(entity);
				}
i++;
			}

			System.out.println("process finished");

			//in.close();
			closeInFile();
			closeOutFile();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage() + " " + i);
		}
	}
*/


	static public void calculate_and_export_Distances(String dataPath) {
		int i = 1;
		try {

		openInFile(dataPath);
		createOutFile("distances5");
		
			String strLine;

			while ((strLine = br.readLine()) != null) {
				
				if(i!=1){
				System.out.println("line: "+strLine);
				String[] data = strLine.split(";");

				Link entity = createEntity(data);
				entity.calculateGeographicDistance();
				//writeLine("1: "+entity.getDistance()+" 2: "+entity.getDistance2());
				//writeLine(""+Double.valueOf(entity.getDistance()).longValue());
				writeLine(""+(entity.getDistance()));
				// insert node
				//insertFrom_To_Nodes(entity);
				// insert link
				//insertLink(entity);
				}
i++;
			}

			System.out.println("process finished");

			//in.close();
			closeInFile();
			closeOutFile();
		} catch (Exception e) {
			System.err.println("Error in calculate_and_export_Distances: " + e.getMessage() + " " + i);
		}
	}
	
	/*
	private static void insertLink(Link entity) {
		String key = entity.getFrom() + "-" + entity.getTo();
		if (!entries.contains(key)) {
			entries.add(key);
			city_to_city.put(key, entity);
		} else {
			System.out.println("link already inserted");
			// Link updated = city_to_city.get(key);
			// updated.setEc_funding(updated.getEc_funding() +
			// entity.getEc_funding());
			// city_to_city.put(key, updated);
		}
	}
*/
	
	/*
	private static void insertLink(Link entity) {
		links.put(entity.getId(), entity);
	}
	*/
	private static void insertLink(Route entity) {
		links.put(entity.getId(), entity);
	}
	/*
	private static void insertFrom_To_Nodes(Link entity) {
		if (!nodes.containsKey(entity.getFrom())) {
			nodes.put(entity.getFrom(), entity.getFrom_pos());
		}
		if (!nodes.containsKey(entity.getTo())) {
			nodes.put(entity.getTo(), entity.getTo_pos());
		}
	}
*/
	
	private static Route createRoute(String[] data) {

		String airlineID = data[airlineIDIndex].trim();
		//System.out.println("airlineID: "+airlineID);
		String airlineName = data[airlineNameIndex].trim();
		//System.out.println("airlineName: "+airlineName);
		boolean isAirlineActive = Boolean.getBoolean(data[airlineActiveIndex]);
		//System.out.println("isAirlineActive: "+isAirlineActive);
		String airlineCountry = data[airlineCountryIndex].trim();
		//System.out.println("airlineCountry: "+airlineCountry);
		double distance = 0;
		if(data[routeDistanceIndex]!=null){
				System.out.println(data[routeDistanceIndex]);
		 distance = Double.parseDouble(data[routeDistanceIndex].replace(",", ".").trim());
		//System.out.println("distance: "+distance);
		}
		
		String equipments = data[routeEquipmentIndex].trim();
		//System.out.println("equipments: "+equipments);
		//System.out.println("data[routeCodeShareIndex]: "+data[routeCodeShareIndex]);
			Boolean	isCodeShare = Boolean.getBoolean(data[routeCodeShareIndex]);
		//System.out.println("data[routeStopsIndex]: "+data[routeStopsIndex]);
			int stops =  Integer.valueOf(data[routeStopsIndex]);

		System.out.println("distance: "+distance);
		
		
		
String from = data[fromIDIndex].trim();
		
		
		
		double fromLat = Double.parseDouble(data[fromLatIndex].replace(",", ".").trim());
		double fromLon = Double.parseDouble(data[fromLonIndex].replace(",", ".").trim());
		String to = data[toIDIndex].trim();
		double toLat = Double.parseDouble(data[toLatIndex].replace(",", ".").trim());
		double toLon = Double.parseDouble(data[toLonIndex].replace(",", ".").trim());
		
				
		Route route = new Route(data[idIndex]);
		route.setLocation(from, fromLat, fromLon, to, toLat, toLon);
		
		route.setRouteAttriburtes(airlineID, airlineName, isAirlineActive, airlineCountry, isCodeShare, stops, equipments, distance);
		
		return route;
	}
	
	private static Link createEntity(String[] data) {

		System.out.println("from: "+data[fromIDIndex].trim());
		System.out.println("fromLat: "+data[fromLatIndex]);
		System.out.println("fromLon: "+data[fromLonIndex]);
		System.out.println("to: "+data[toIDIndex]);
		System.out.println("toLat: "+data[toLatIndex]);
		System.out.println("toLon: "+data[toLonIndex]);
		
		String from = data[fromIDIndex].trim();
		
		
		
		double fromLat = Double.parseDouble(data[fromLatIndex].replace(",", ".").trim());
		double fromLon = Double.parseDouble(data[fromLonIndex].replace(",", ".").trim());
		String to = data[toIDIndex].trim();
		double toLat = Double.parseDouble(data[toLatIndex].replace(",", ".").trim());
		double toLon = Double.parseDouble(data[toLonIndex].replace(",", ".").trim());
		
				
		Link entry = new Link(data[idIndex]);
		entry.setLocation(from, fromLat, fromLon, to, toLat, toLon);

		return entry;
	}
	
	
	public static void openInFile(String dataPath){
		try{
		FileInputStream fstream = new FileInputStream(dataPath);
		DataInputStream in = new DataInputStream(fstream);
		 br = new BufferedReader(new InputStreamReader(in));
		 }catch (Exception e){//Catch exception if any
			  System.err.println("Error in openInFile: " + e.getMessage());
			  }
	}
		
	
	public static void createOutFile(String fileName){
		
	
		
		try{
			File dir = new File("export/");
			dir.mkdir();
			  // Create file 
			  FileWriter fstream = new FileWriter("export/"+fileName+".txt");
			   out = new BufferedWriter(fstream);
			  
			  }catch (Exception e){//Catch exception if any
			  System.err.println("Error in createFile: " + e.getMessage());
			  }
		
	}
	
	
	public static void closeOutFile( ){
		try{
		//Close the output stream
		  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error in closeOutFile: " + e.getMessage());
			  }
	}
	
	public static void closeInFile( ){
		try{
		//Close the output stream
		  br.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error in closeInFile: " + e.getMessage());
			  }
	}
	
	
	
	
	
	public static void writeLine(String line){
		try{
		out.write(line);
		out.newLine();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error in writeline: " + e.getMessage());
			  }
		  
	}
}
