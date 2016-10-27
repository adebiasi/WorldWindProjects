package it.graphitech;

import it.graphitech.input.FlowSource;
import it.graphitech.modules.MiddleNodeGeneration;
import it.graphitech.modules.Render;
import it.graphitech.objects.Position;
import it.graphitech.objects.TreeNode;
import it.graphitech.swing.VariablesPanel;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Operations {

	public static Color[] colors = { Color.YELLOW,Color.WHITE, Color.BLUE, Color.ORANGE,Color.MAGENTA,Color.CYAN,Color.DARK_GRAY,Color.PINK };

	public static double minLat = 25;
	public static double maxLat = 50;
	public static double minLon = -130;
	public static double maxLon = -60;

	public static double deltaLat;
	public static double deltaLon;

	public static double minFlowMagnitude = 999999;
	public static double maxFlowMagnitude = -60;

	public static double minFlow_R = 999999;
	public static double maxFlow_R = -60;
	public static double minFlow_G = 999999;
	public static double maxFlow_G = -60;
	public static double minFlow_B = 999999;
	public static double maxFlow_B = -60;

	
	public static double minFlow_R_perc = 1;
	public static double maxFlow_R_perc = 0;
	public static double minFlow_G_perc = 1;
	public static double maxFlow_G_perc = 0;
	public static double minFlow_B_perc = 1;
	public static double maxFlow_B_perc = 0;
	
	public static double totFlow=0;
	public static double totFlow_R_perc = 0;
	public static double totFlow_G_perc = 0;
	public static double totFlow_B_perc = 0;
	
	
	
	public static double minLeafNodeFlowMagnitude = 999999;
	public static double maxLeafNodeFlowMagnitude = -60;
	
	public static ArrayList<FlowSource> inputFlows;
	//public static ArrayList<Segment> segments;
	//public static List<double[]> startingNodes;

	public static String mapDirectory = "map.png";
	public static String dataDirectory = "input.txt";

	public static double maxDistance = 0;

	public static int lastindex = 1;

	static public ArrayList<FlowSource> loadInputData(String dataDirectory) {

		System.out.println("load input data");
		
		ArrayList<FlowSource> inputFlows = new ArrayList<>();

		double minLat = 300;
		double minLon = 300;
		double maxLat = -300;
		double maxLon = -300;

		double minFlowMagnitude = 9999999;
		double maxFlowMagnitude = 0;

		try {
			String s;
			BufferedReader reader = new BufferedReader(new FileReader(
					dataDirectory));

			//int index = 0;

			while ((s = reader.readLine()) != null) {
				// System.out.println("->"+s);
				String[] data = s.split(",");

				System.out.println("num blocchi per flusso: "+data.length);
				
				double latStart = Double.valueOf(data[0]);
				double lonStart = Double.valueOf(data[1]);
				double latDest = Double.valueOf(data[2]);
				double lonDest = Double.valueOf(data[3]);
				double flowMagnitude = Double.valueOf(data[4]);

				totFlow=totFlow+flowMagnitude;
				
				if (latStart < minLat) {
					minLat = latStart;
				}
				if (latStart > maxLat) {
					maxLat = latStart;
				}
				if (latDest < minLat) {
					minLat = latDest;
				}
				if (latDest > maxLat) {
					maxLat = latDest;
				}

				if (lonStart < minLon) {
					minLon = lonStart;
				}
				if (lonStart > maxLon) {
					maxLon = lonStart;
				}
				if (lonDest < minLon) {
					minLon = lonDest;
				}
				if (lonDest > maxLon) {
					maxLon = lonDest;
				}

				System.out.println(flowMagnitude +" > "+ maxFlowMagnitude+" ?");
				if (flowMagnitude > maxFlowMagnitude) {
					maxFlowMagnitude = flowMagnitude;
					System.out.println("all'inizio maxFlowMagnitude = "+maxFlowMagnitude);
				}
				if (flowMagnitude < minFlowMagnitude) {
					minFlowMagnitude = flowMagnitude;
				}

				//System.out.println("flow magn: " + flowMagnitude);
				double currDistance = Position.calculateDistance(new Position(lonStart, latStart), new Position(lonDest, latDest));
				FlowSource flow = new FlowSource(latStart, lonStart, latDest,
						lonDest, flowMagnitude);
				
if(currDistance>maxDistance){
	maxDistance=	currDistance;
}			
				
				
				inputFlows.add(flow);
				
				
if(data.length==6){
	System.out.println("ha name:" +data[5]);
				flow.setName(data[5]);	
				}
if(data.length==9){
	System.out.println("RGB VALUES");
	flow.setName(data[5]);			
	flow.setMagnitudeRGB(Double.valueOf(data[6]),Double.valueOf(data[7]),Double.valueOf(data[8]));	
	totFlow_R_perc=Double.valueOf(data[6])+totFlow_R_perc;
	totFlow_G_perc=Double.valueOf(data[7])+totFlow_G_perc;
	totFlow_B_perc=Double.valueOf(data[8])+totFlow_B_perc;
				}


		//		index++;
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Errore: " + e);
			System.exit(1);
		}

		System.out.println("loaded " + inputFlows.size() + " flows");

		System.out.println("max distance: "+maxDistance);
		
		Operations.maxLat = maxLat + 2;
		Operations.minLat = minLat - 2;
		Operations.maxLon = maxLon + 2;
		Operations.minLon = minLon - 2;

		Operations.maxFlowMagnitude = maxFlowMagnitude;
		Operations.minFlowMagnitude = minFlowMagnitude;

		Operations.maxLeafNodeFlowMagnitude = maxFlowMagnitude;
		Operations.minLeafNodeFlowMagnitude = minFlowMagnitude;
		
		
		//Variables.electrostatic_force_factor = Variables.lengthMiddleSegment/ 16.76;
		//Variables.electrostatic_force_factor = 0.01;
		
		//Variables.spring_force_factor = Variables.lengthMiddleSegment/100616;
		//Variables.nodeRootRadius=0;
		System.out.println("INIT VARIABLES");
		Variables.initVariables(maxDistance);
		
		VariablesPanel.elect_Force_text.setText(String.valueOf(Variables.electrostatic_force_factor));
		VariablesPanel.elect_Force_text.revalidate(); 
		VariablesPanel.elect_Force_text.repaint();
		
		VariablesPanel.spring_Force_text.setText(String.valueOf(Variables.spring_force_factor));
		VariablesPanel.spring_Force_text.revalidate(); 
		VariablesPanel.spring_Force_text.repaint();
		
		
		
		System.out.println("Variables.lengthMiddleSegment: "+Variables.lengthMiddleSegment);
		System.out.println("min: " + minFlowMagnitude);
		System.out.println("max: " + maxFlowMagnitude);

		return inputFlows;
	}

	public static void retrieveMap(double minx, double miny, double maxx,
			double maxy, double width, double height, String mapDirectory) {

		try {
			URL url = new URL(
					"http://www2.demis.nl/wms/wms.asp?wms=WorldMap&request=getmap&version=1.0.7&layers=Countries,Bathymetry,Topography,Hillshading,Builtup%20areas,Coastlines,Waterbodies,Inundated,Rivers,Streams,Railroads,Highways,Roads,Trails,Borders,Cities,Settlements,Spot%20elevations,Airports,Ocean%20features&STYLES=&SRS=EPSG:4326"
							+ "&BBOX=" +
							// "-123" +
							minx + "," +
							// "26" +
							miny + "," +
							// "-67" +
							maxx + "," +
							// "49" +
							maxy + "&WIDTH=" +
							// "600" +
							width + "&HEIGHT=" +
							// "600" +
							height + "&FORMAT=image/png&");

			BufferedInputStream in = null;
			FileOutputStream fout = null;
			try {
				in = new BufferedInputStream(url.openStream());
				fout = new FileOutputStream(mapDirectory);

				byte data[] = new byte[1024];
				int count;
				while ((count = in.read(data, 0, 1024)) != -1) {
					fout.write(data, 0, count);
				}
			} finally {
				if (in != null)
					in.close();
				if (fout != null)
					fout.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<double[]> returnStartingNodes(ArrayList<FlowSource> flows) {

		List<double[]> list = new ArrayList<double[]>();

		for (FlowSource s : flows) {
			if (!list.contains(s)) {
				list.add(s.getStartingPoint());
			}
		}
		return list;
	}

	

	public static void clearVariables() {
		minLat = 25;
		maxLat = 50;
		minLon = -130;
		maxLon = -60;

	
		minFlowMagnitude = 999999;
		maxFlowMagnitude = -60;

		maxLeafNodeFlowMagnitude= -60;
		minLeafNodeFlowMagnitude = 999999;
		
		totFlow_B_perc=0;
		totFlow_R_perc=0;
		totFlow_G_perc=0;
		
		inputFlows = null;
		//segments = null;
		
		MiddleNodeGeneration.initNodes();
		//MiddleNodeGeneration.nodesId=new ArrayList<String>();
	
	}



}
