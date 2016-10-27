package it.graphitech.smeSpire;

import gov.nasa.worldwind.geom.Vec4;

import java.util.ArrayList;
import java.util.HashMap;

public class SectorManager {
	public static HashMap<Integer , ArrayList<String>> lensSectorsMap = new HashMap<Integer, ArrayList<String>>();
	public static HashMap<String , Double> mapArcDistance = new HashMap<String, Double>();
	
	public static int numSectors = 4;
	static public int deltaDistance = 10;
	
	public static double returnDistance(int indexArc, int indexControPoint){
		int numSector = returnSector(indexArc,indexControPoint);
		//System.out.println("cerco la distanza dell'arco:"+indexArc+" in numSector: "+numSector);		
		//System.out.println(indexArc+"-"+indexControPoint);
		//ArrayList<Integer> orderedListOfArcs = lensSectorsMap.get(numSector);	
		ArrayList<String> orderedListOfArcs = lensSectorsMap.get(numSector);
		int index = orderedListOfArcs.indexOf(indexArc+"-"+indexControPoint);
		
		int numArcs = orderedListOfArcs.size();
		
		double maxDistance = deltaDistance*(numArcs-1);
		if(maxDistance>SharedVariables.maxDistance){
			return (SharedVariables.maxDistance/numArcs)*index;
		}
		return deltaDistance*index;
			
	}
	
	
	public static int returnIndexArcs(int indexArc, int indexControPoint){
		int numSector = returnSector(indexArc,indexControPoint);
		//System.out.println("cerco la distanza dell'arco:"+indexArc+" in numSector: "+numSector);		
		//System.out.println(indexArc+"-"+indexControPoint);
		//ArrayList<Integer> orderedListOfArcs = lensSectorsMap.get(numSector);	
		ArrayList<String> orderedListOfArcs = lensSectorsMap.get(numSector);
		int index = orderedListOfArcs.indexOf(indexArc+"-"+indexControPoint);
		
		return index;
	}
	
	public static int calculateSector(Vec4 p){
		double angle = computeAngle(p);
		double selSector = ((Math.PI+angle)/(2*Math.PI))*numSectors;
		/*
		System.out.println("calculate sector");
		System.out.println("p: "+p);
		System.out.println("angle: "+angle);
		System.out.println("selSector: "+selSector);
		*/
		return (int)selSector;
	}
	static int returnSector(int indexArc,int indexControlPoint){
		for(java.util.Map.Entry<Integer, ArrayList<String>> sector :lensSectorsMap.entrySet()){
			if(sector.getValue().contains(indexArc+"-"+indexControlPoint)){
				return sector.getKey();
			}
		}
		return -1;
		
	}
	
	 static public void printSectorContents(){
			for(java.util.Map.Entry<Integer, ArrayList<String>> sector :lensSectorsMap.entrySet()){
				System.out.println("sector "+sector.getKey());
				ArrayList<String> arcs = (sector.getValue());
					for(String in : arcs){
						double d= mapArcDistance.get(in);
						System.out.println("arc "+in+" with distance: "+d);
					}
			}
		}
	
	
	private static double computeAngle(Vec4 p){
		return Math.atan2(p.x,p.y);
		//return 0;
	}
	public static void initLensAnglesMap(int numSectors){		
		for(int i=0;i<numSectors;i++){
			lensSectorsMap.put(i, new ArrayList<String>());
		}
	}
}
