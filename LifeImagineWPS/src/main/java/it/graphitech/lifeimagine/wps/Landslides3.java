package it.graphitech.lifeimagine.wps;

import java.util.HashMap;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;

import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

import it.graphitech.lifeimagine.wps.parser.Type;
import it.graphitech.lifeimagine.wps.parser.WFSParser;
import it.graphitech.lifeimagine.wps.util.ProcessUtil;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "Landslides3", description = "The process return the points of interest that are inside a zone with the selected hazard category and inside the landslide inventory areas buffered by the amount selected.")
public class Landslides3 implements GSProcess {
	
	/**
	 * WPS of the third landslide scenario.
	 * The process return the points of interest that are inside a zone with the selected hazard category and inside the landslide inventory areas buffered by the amount selected.
	 * 
	 * @param poi				Feature collection of the cultural heritage points of interest or of the terraces.
	 * @param hazards			Feature collection of the hazard areas.
	 * @param landslides		Feature collection of the landslide events inventory.
	 * @param class				Value of the hazard class of interest.
	 * @param distance			Value of the buffer distance in meters.
	 * 
	 * @return Points of interest matching the input criteria.
	 * 
	 * A subprocess is executed as explained in the following procedure:
	 * - setting every necessary input
	 * - execute the process and save the output
	 * 
	 */
	@DescribeResult(name = "result", description = "Points of interest matching the input criteria")
	public SimpleFeatureCollection execute(
			@DescribeParameter(name = "poi", description = "Feature collection of the cultural heritage points of interest or of the terraces.") String poiURL,
			//http://lifeimagine.graphitech-projects.com/deegree/services/nz_rl_ch?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:ExposedElement&srsName=EPSG:4326
			
			@DescribeParameter(name = "hazards", description = "Feature collection of the hazard areas") String hazardURL,
			//http://lifeimagine.graphitech-projects.com/deegree/services/nz_rl?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:HazardArea&srsName=EPSG:4326
			
			@DescribeParameter(name = "landslides", description = "Feature collection of the landslide events inventory") String inventoryURL,
			//http://lifeimagine.graphitech-projects.com/deegree/services/nz_rl?service=WFS&request=GetFeature&version=2.0.0&typeNames=nz-core:ObservedEvent&srsName=EPSG:4326
			
			@DescribeParameter(name = "class", description = "Value of the hazard class of interest") String hazardClass,
			//Pg1
			
			@DescribeParameter(name = "distance", description = "Value of the buffer distance in meters") Double inventoryDistance,
			//100
			
			@DescribeParameter(name = "type", description = "Type of the points of interest") String geometry) {
			//POINT
		
		WFSParser poiParser;
		if (geometry.equals("POLYGON"))
		{
			System.out.println("dato in input: poligono");
			poiParser = new WFSParser(new Type("nz-core:ExposedElement", null), null, 4326, Type.Geom.POLYGON);
		}
		else
		{
			System.out.println("dato in input punto");
			poiParser = new WFSParser(new Type("nz-core:ExposedElement", null), null, 4326, Type.Geom.POINT);
		}
		
		SimpleFeatureCollection poiFeature = poiParser.parseWFS(poiURL);
		
		WFSParser hazardParser = new WFSParser(new Type("nz-core:HazardArea", Type.Reg.RL), null, 4326, Type.Geom.POLYGON);
		SimpleFeatureCollection hazardFeature = hazardParser.parseWFS(hazardURL);
		
		WFSParser inventoryParser = new WFSParser(new Type("nz-core:ObservedEvent", Type.Reg.RL), null, 4326, Type.Geom.POLYGON);
		SimpleFeatureCollection inventoryFeature = inventoryParser.parseWFS(inventoryURL);
		
	
		
		
		Object bufferResultInv;
		//Object bufferResultHaz;
		Object intersectionResult;
		Object filteredHazardFeatures;
		
		{
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		//hashmap containing the output of every subprocess
		//HashMap<String, Object> output = new HashMap<String, Object>();	    
		//input.put("features", inventoryFeature);
		input.put("features",( (SimpleFeatureCollection) inventoryFeature ));
	    input.put("distance", inventoryDistance / 111111.0);
	    //output.put("bufferResultInv", ProcessUtil.executeProcess("gs", "BufferFeatureCollection", input, "result"));
	    System.out.println("-Buffer inventario frane con "+inventoryFeature.size()+" elementi in input");
	    bufferResultInv = ProcessUtil.executeProcess("gs", "BufferFeatureCollection", input, "result");
	    System.out.println("output "+((SimpleFeatureCollection)bufferResultInv).size());
	    input.clear();
		}
	    
		
		{
			
		HashMap<String, Object> input = new HashMap<String, Object>();	    
		//input.put("features",  ProcessUtil.dissolve2( (SimpleFeatureCollection)ProcessUtil.valueFilter(hazardFeature, "qualitativeLikelihood", hazardClass)));
		input.put("features",  ( (SimpleFeatureCollection)ProcessUtil.valueFilter(hazardFeature, "qualitativeLikelihood", hazardClass)));
	    input.put("distance", 0.00000001);
	    //output.put("bufferResultHaz", ProcessUtil.executeProcess("gs", "BufferFeatureCollection", input, "result"));
	    System.out.println("-Buffer aree di pericolosità con "+hazardFeature.size()+" elementi");
	    filteredHazardFeatures = ProcessUtil.executeProcess("gs", "BufferFeatureCollection", input, "result");		
	    System.out.println("output "+((SimpleFeatureCollection)filteredHazardFeatures).size()); 
	    input.clear();
	    
			/*
		System.out.println("faccio filtro e poi dissolve");
			// filteredHazardFeatures = 	ProcessUtil.reduce( (SimpleFeatureCollection)ProcessUtil.valueFilter(hazardFeature, "qualitativeLikelihood", hazardClass));
		 filteredHazardFeatures = 	ProcessUtil.reduce((SimpleFeatureCollection)ProcessUtil.valueFilter(hazardFeature, "qualitativeLikelihood", hazardClass));
		System.out.println("finito filtro e dissolve");
		*/
			}
	    
		
		{
		HashMap<String, Object> input = new HashMap<String, Object>();		
		//input.put("first feature collection", ProcessUtil.dissolve( (SimpleFeatureCollection) bufferResultHaz ));
		//input.put("second feature collection", ProcessUtil.dissolve( (SimpleFeatureCollection) bufferResultInv ));
	    
		//input.put("first feature collection",  bufferResultHaz );
		input.put("first feature collection",   ProcessUtil.dissolve2((SimpleFeatureCollection)filteredHazardFeatures) );
		input.put("second feature collection",   ProcessUtil.dissolve2((SimpleFeatureCollection)bufferResultInv) );
	    
		//output.put("intersectionResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		System.out.println("-Intersection tra buf inventario frane e buf aree pericolosità-");
	    intersectionResult = ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result");
	    input.clear();
		}
	    
		
	//	return (SimpleFeatureCollection) intersectionResult;
		
		
		Object finalResult;
		//check the POI type, points = inclusion, polygons = intersection
		String type = ProcessUtil.getGeometryType(poiFeature);
		if (type.toString().equals("Point")) {
			HashMap<String, Object> input = new HashMap<String, Object>();			
			input.put("first", poiFeature);
			input.put("second", intersectionResult);
		    //output.put("finalResult", ProcessUtil.executeProcess("gs", "InclusionFeatureCollection", input, "result"));
			System.out.println("inclusion");
		     finalResult =  ProcessUtil.executeProcess("gs", "InclusionFeatureCollection", input, "result");
		     System.out.println("risultato inclusion con "+((SimpleFeatureCollection)finalResult).size());
		     input.clear();
		}
		else {
			HashMap<String, Object> input = new HashMap<String, Object>();			
			input.put("features", poiFeature);
		    input.put("distance", 0.00000001);
		    //output.put("bufferResultPoi", ProcessUtil.executeProcess("gs", "BufferFeatureCollection", input, "result"));
		    System.out.println("Buffer 3");
			Object bufferResultPoi = ProcessUtil.executeProcess("gs", "BufferFeatureCollection", input, "result");
		    
		    input.clear();
			input.put("first feature collection",bufferResultPoi);
			input.put("second feature collection", intersectionResult);
		    //output.put("finalResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
			System.out.println("intersection 2");
		     finalResult = ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result");
		    
		}
		
		//return (SimpleFeatureCollection) filteredHazardFeatures;
		//return (SimpleFeatureCollection) intersectionResult;
		return (SimpleFeatureCollection) finalResult;
	}
}