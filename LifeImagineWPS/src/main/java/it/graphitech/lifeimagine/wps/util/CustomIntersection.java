package it.graphitech.lifeimagine.wps.util;

import java.util.HashMap;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.opengis.feature.type.Name;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "CustomIntersection", description = "The process intersects two feature collection and returns the result.")
public class CustomIntersection implements GSProcess {
	
	/**
	 * WPS used in the scenario processes.
	 * The process intersects two feature collection and returns the result.
	 * 
	 * @param firstFeatureCollection		Feature collection to re-project.
	 * @param secondFeatureCollection			Value of the new projection code.
	 * 
	 * @return The intersection between the two feature collection.
	 * 
	 * A subprocess is executed as explained in the following procedure:
	 * - setting every necessary input
	 * - execute the process and save the output
	 * 
	 */
	@DescribeResult(name = "result", description = "The intersection between the two feature collection.")
	public SimpleFeatureCollection execute(
			@DescribeParameter(name = "first feature collection", description = "First feature collection to intersect") SimpleFeatureCollection firstFeatureCollection,
			@DescribeParameter(name = "second feature collection", description = "Second feature collection to intersect") SimpleFeatureCollection secondFeatureCollection) {
			
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		//hashmap containing the output of every subprocess
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		//empty the hashmap of the old input
		input.put("features", firstFeatureCollection);
	    input.put("code", "EPSG:4326");
	    output.put("firstFeatureReproject", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
	    
	    input.clear();
	    input.put("features", secondFeatureCollection);
	    input.put("code", "EPSG:4326");
	    output.put("secondFeatureReproject", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
	    
	    input.clear();
		//check the POI type, points = inclusion, polygons = intersection
	    SimpleFeatureCollection firstCollection = (SimpleFeatureCollection) output.get("firstFeatureReproject");
	    SimpleFeatureCollection secondCollection = (SimpleFeatureCollection) output.get("secondFeatureReproject");
		Name firstType = firstCollection.getSchema().getType("the_geom").getName();
		Name secondType = secondCollection.getSchema().getType("the_geom").getName();
		if (firstType.toString().equals("Point")) {
			if (secondType.toString().equals("Point")) {
				output.put("finalResult", null);
			}
			else {
				input.put("first", firstCollection);
			    input.put("second", secondCollection);
			    output.put("finalResult", ProcessUtil.executeProcess("gs", "InclusionFeatureCollection", input, "result"));
			}
		}
		else if (secondType.toString().equals("Point")) {
			input.put("first", secondCollection);
		    input.put("second", firstCollection);
		    output.put("finalResult", ProcessUtil.executeProcess("gs", "InclusionFeatureCollection", input, "result"));
		}
		else {
			input.put("first feature collection", firstCollection);
		    input.put("second feature collection", secondCollection);
		    output.put("finalResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		}
	    
		return (SimpleFeatureCollection) output.get("finalResult");
	}

}
