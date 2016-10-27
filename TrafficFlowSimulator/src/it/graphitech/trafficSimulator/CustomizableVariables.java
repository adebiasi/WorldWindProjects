package it.graphitech.trafficSimulator;

import java.io.BufferedReader;
import java.io.FileReader;


/**
 * This class contains all the variable that the user can modify in order to customize the application
 * @author a.debiasi
 *
 */
public class CustomizableVariables {

	
	/**
	 * variables related to the cars
	 */
	public static int updateRate = 30;
	public static int numStepForRoutingPathCalculation = 30;
	public static double minSpeed = 25;
	public static double deltaSpeed = 150;
	public static  int CAR_RADIUS = 3;
	public static int CARSDISTANCE = 25;
	public static int PASSABLECARS = 5;
	public static int carFrequency = 10;//in millisec.
	public static int carSpeed = 400;
public static int numMaxCars=300;
	/**
	 * variables related to the roads
	 */
	public static int PER_CAR_PENALTY = 80; // Penalty for each car in a segment
	public static int max_cars_per_path = 10;

	/**
	 * variables related to the parking lots
	 */
	public static int numCarsPerParkingLot = 5;
	
	/**
	 * variables related to the extruded line used to analyze the traffic
	 */
	//public static double minHue = 180.0d / 360.0d;
	//public static double maxHue = 360.0d / 360.0d;
	public static double minHue = 120.0d / 360.0d;
	public static double maxHue = 0.0d / 360.0d;
	
	
	/**
	 * variables related to the location of the input data
	 */
	public static String wfsServer = "http://193.205.215.100/geoserver/wfs";
	
	public static String typeName = "cite:Home_emitter";
	public static String fileBuilding = "sim_data\\buildings\\shapes\\edfici_projected_lite.shp";
	public static String fileRoads = "sim_data\\PolyRoads\\unione_strade.shp";
	public static String fileOutputSimulation = "sim_data\\PollutionSimulation\\test.kml";
	public static  String KML_TRENTO_DATA =
			// "sim_data/Roads_Geo_WGS84_v2.KML";
			"http://193.205.215.100/TempWebFolder/Traffic/Roads_Geo_WGS84.txt";
	
	public static  int RENDERALTITUDE = 0; // Altitude of roads and cars
	
	
	
	public static void openFile(String dataDirectory){
		 
		 try {
			 
			 String s;
		 
			BufferedReader reader =
				new BufferedReader(
					new FileReader(dataDirectory) );
		
			int index=0;
			
			while( (s = reader.readLine()) != null ){
				checkVariables(s);
			}
		 }catch(Exception e){
				e.printStackTrace();
			}
	}
	

	static private void checkVariables(String line){
		
		String startTag;
		String endTag="</";
		
		String[] variables = {"updateRate","numStepForRoutingPathCalculation","minSpeed","deltaSpeed","CAR_RADIUS","CARSDISTANCE",
				"PASSABLECARS","carFrequency","carSpeed",
				"PER_CAR_PENALTY","max_cars_per_path",
				"numCarsPerParkingLot","minHue","maxHue","wfsServer",
				"typeName","fileBuilding","fileRoads","fileOutputSimulation","KML_TRENTO_DATA","RENDERALTITUDE","maxcar"};
		int i =0;
		for(String var : variables){
			startTag="<"+var+">";
			int startindex=line.indexOf(startTag);
			int endIndex=line.indexOf(endTag, startindex);
			
			if((startindex!=-1)&(endIndex!=-1)){
			String res=line.substring(startindex+startTag.length(), endIndex);
			System.out.println("value: "+res);
			assignVariable(res,i);
			}
			i++;
		}
		
	}
	
	private static void assignVariable(String val,int i){
		
		if(i==0){
			updateRate=Integer.valueOf(val);
		}else 
			if(i==1){
				numStepForRoutingPathCalculation=Integer.valueOf(val);
			}else
				if(i==2){
					minSpeed=Double.valueOf(val);
				}else 
					if(i==3){
						deltaSpeed=Double.valueOf(val);
					}else
						if(i==4){
							CAR_RADIUS=Integer.valueOf(val);
						}else 
							if(i==5){
								CARSDISTANCE=Integer.valueOf(val);
							}else
								if(i==6){
									PASSABLECARS=Integer.valueOf(val);
								}else 
									if(i==7){
										carFrequency=Integer.valueOf(val);
									}else
										if(i==8){
											carSpeed=Integer.valueOf(val);
										}else 
											if(i==9){
												PER_CAR_PENALTY=Integer.valueOf(val);
											}else if(i==10){
													max_cars_per_path=Integer.valueOf(val);
												}else
													if(i==11){
														numCarsPerParkingLot=Integer.valueOf(val);
													}else 
														if(i==12){															
															minHue=Double.valueOf(val);
														}else
															if(i==13){
																maxHue=Integer.valueOf(val);
															}else 
																if(i==14){
																	wfsServer=(val);
																}else
																	if(i==15){
																		typeName=(val);
																	}else 
																		if(i==16){
																			fileBuilding=(val);
																		}else
																			if(i==17){
																				fileRoads=(val);
																			}else
																				if(i==18){
																					fileOutputSimulation=(val);
																				}else 
																					if(i==19){
																						KML_TRENTO_DATA=(val);
																					}else
																						if(i==20){
																							RENDERALTITUDE=Integer.valueOf(val);
																						}else if(i==21){
																							numMaxCars=Integer.valueOf(val);
																						}
	
}
	

	
	public static void main(String[] args) {
		openFile("src/config/simulation/CustomVariables.txt");
	}
}
