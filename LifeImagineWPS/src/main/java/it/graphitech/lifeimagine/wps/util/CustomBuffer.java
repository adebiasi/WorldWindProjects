package it.graphitech.lifeimagine.wps.util;

import java.util.HashMap;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "CustomIntersection", description = "The process intersects two feature collection and returns the result.")
public class CustomBuffer implements GSProcess {
	
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
			@DescribeParameter(name = "feature collection", description = "Feature collection to buffer") SimpleFeatureCollection featureCollection,
			@DescribeParameter(name = "distance", description = "Distance of the buffer in meters") Double distance) {
			
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		//hashmap containing the output of every subprocess
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		//empty the hashmap of the old input
		input.put("features", featureCollection);
	    input.put("code", "EPSG:4326");
	    output.put("featureReproject", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
	    
	    input.clear();
		input.put("features", output.get("featureReproject"));
		input.put("distance", distance / 111111.0);
	    output.put("finalResult", ProcessUtil.executeProcess("gs", "BufferFeatureCollection", input, "result"));
			    
		return (SimpleFeatureCollection) output.get("finalResult");
	}
}
