package it.graphitech.lifeimagine.wps.old;

import java.util.HashMap;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.opengis.filter.Filter;

import it.graphitech.lifeimagine.wps.util.ProcessUtil;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "Landslides3", description = "The process return the points of interest that are inside a zone with the selected hazard category and inside the landslide inventory areas buffered by the amount selected.")
public class Landslides3v1 implements GSProcess {
	
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
			@DescribeParameter(name = "poi", description = "Feature collection of the cultural heritage points of interest or of the terraces.") SimpleFeatureCollection poiFeature,
			@DescribeParameter(name = "hazards", description = "Feature collection of the hazard areas") SimpleFeatureCollection hazardFeature,
			@DescribeParameter(name = "landslides", description = "Feature collection of the landslide events inventory") SimpleFeatureCollection inventoryFeature,
			@DescribeParameter(name = "class", description = "Value of the hazard class of interest") String hazardClass,
			@DescribeParameter(name = "distance", description = "Value of the buffer distance in meters") Double inventoryDistance) {
		
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		//hashmap containing the output of every subprocess
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		/*input.put("features", poiFeature);
	    input.put("code", "EPSG:4326");
	    output.put("reprojectCulturalResult", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
		
		input.clear();
		input.put("features", inventoryFeature);
	    input.put("code", "EPSG:4326");
	    output.put("reprojectInventoryResult", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));*/
		
	    //create filter to select a specific hazard class
		Filter filter = ProcessUtil.createFilter("EQUALS", "CLASSE", new Object[]{hazardClass});
		input.clear();
		input.put("features", hazardFeature);
	    input.put("filter", filter);
	    output.put("queryResult", ProcessUtil.executeProcess("gs", "Query", input, "result"));
		
		/*input.clear();
		input.put("features", output.get("queryResult"));
	    input.put("code", "EPSG:4326");
	    output.put("reprojectHazardResult", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));*/
		
		input.clear();
		input.put("features", inventoryFeature);
		//input.put("features", output.get("reprojectInventoryResult"));
		//estimated method to convert meters into degrees
	    input.put("distance", inventoryDistance / 111111.0);
	    output.put("bufferResult", ProcessUtil.executeProcess("gs", "BufferFeatureCollection", input, "result"));
		
		/*input.clear();
		input.put("first feature collection", output.get("queryResult"));
	    input.put("second feature collection", output.get("bufferResult"));
	    output.put("intersectionResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		
		input.clear();
		//check the POI type, points = inclusion, polygons = intersection
		String type = ProcessUtil.getGeometryType(poiFeature);
		if (type.toString().equals("Point")) {
			input.put("first", poiFeature);
		    input.put("second", output.get("intersectionResult"));
		    output.put("finalResult", ProcessUtil.executeProcess("gs", "InclusionFeatureCollection", input, "result"));
		}
		else {
			input.put("first feature collection", output.get("intersectionResult"));
		    input.put("second feature collection", poiFeature);
		    output.put("finalResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		}*/
		
		return (SimpleFeatureCollection) output.get("bufferResult");
	}
}