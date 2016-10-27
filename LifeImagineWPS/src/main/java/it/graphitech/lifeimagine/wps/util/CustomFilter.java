package it.graphitech.lifeimagine.wps.util;


import java.util.HashMap;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.opengis.filter.Filter;


@SuppressWarnings("deprecation")
@DescribeProcess(title = "CustomFilter", description = "The process filters a feature collection and returns the result.")
public class CustomFilter implements GSProcess {

	@DescribeResult(name = "result", description = "The process filters a FeatureCollection")
	public SimpleFeatureCollection execute(
			@DescribeParameter(name = "feature", description = "Feature collection") SimpleFeatureCollection feature,
			@DescribeParameter(name = "type", description = "filter type") String type,
			@DescribeParameter(name = "attribute", description = "Attribute to filter") String attribute,
			@DescribeParameter(name = "value", description = "Value to filter") String value) {
		
		Filter filter = ProcessUtil.createFilter(type, attribute, new Object[]{value});
		HashMap<String, Object> input = new HashMap<String, Object>();
		HashMap<String, Object> input2 = new HashMap<String, Object>();
		
		input.clear();
		input.put("features", feature);
	    input.put("filter", filter);
	    SimpleFeatureCollection filteredFeatures =  (SimpleFeatureCollection)ProcessUtil.executeProcess("gs", "Query", input, "result");
	    
	    input2.put("features", filteredFeatures);
	    input2.put("code", "EPSG:4326");
	    return (SimpleFeatureCollection) ProcessUtil.executeProcess("gs", "CustomReproject", input2, "result");
	    
	}
	
}
