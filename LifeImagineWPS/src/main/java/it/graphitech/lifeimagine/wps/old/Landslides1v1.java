package it.graphitech.lifeimagine.wps.old;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import it.graphitech.lifeimagine.wps.util.ProcessUtil;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "Landslides1", description = "The process reports the information about the area of interest drawn on the map. It compares the landslide hazard and inventory data with the road network to calculate the statistics.")
public class Landslides1v1 implements GSProcess {
	
	/**
	 * WPS of the first landslide scenario.
	 * The process reports the information about the area of interest drawn on the map.
	 * It compares the landslide hazard and inventory data with the road network to calculate the statistics.
	 * 
	 * @param areaofinterest 	Geometry of the area of interest selected by the user.
	 * @param hazards 			Feature collection of the hazard areas.
	 * @param landslides 		Feature collection of the landslide events inventory.
	 * @param roads				Feature collection of the roads network.
	 * @param projection		Value of the projection code used in the process.
	 * @param attribute			Attribute that contains the hazard category value of interest.
	 * 
	 * @return JSON of the report of the area of interest.
	 * 
	 * A subprocess is executed as explained in the following procedure:
	 * - setting every necessary input
	 * - execute the process and save the output
	 * 
	 */
	@DescribeResult(name = "result", description = "Report of the area of interest")
	public String execute(
			@DescribeParameter(name = "areaofinterest", description = "Geometry of the area of interest") Geometry areaInterest,
			@DescribeParameter(name = "hazards", description = "Feature collection of the hazard areas") SimpleFeatureCollection hazardFeature,
			@DescribeParameter(name = "landslides", description = "Feature collection of the landslide events inventory") SimpleFeatureCollection inventoryFeature,
			@DescribeParameter(name = "roads", description = "Feature collection of the roads network") SimpleFeatureCollection roadsFeature,
			@DescribeParameter(name = "attribute", description = "Attribute that contains the hazard category value") String attribute) {
		
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		//hashmap containing the output of every subprocess
		HashMap<String, Object> output = new HashMap<String, Object>();

		try {
			input.put("geometry", areaInterest);
			input.put("crs", CRS.decode("EPSG:4326"));
			input.put("typeName", "areas");
			output.put("featureResult", ProcessUtil.executeProcess("gs", "Feature", input, "result"));
			
			//empty the hashmap of the old input
			input.clear();
			input.put("features", output.get("featureResult"));
		    input.put("code", "EPSG:3044");
		    output.put("featureReproject", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	    input.clear();
	    input.put("features", inventoryFeature);
	    input.put("code", "EPSG:3044");
	    output.put("invReproject", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
	    	    
	    input.clear();
	    input.put("features", hazardFeature);
	    input.put("code", "EPSG:3044");
	    output.put("hazReproject", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
	    
	    /*input.put("features", roadsFeature);
	    input.put("code", "EPSG:3044");
	    output.put("roadReproject", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));*/
	    
	    try {
		    input.clear();
		    CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
		    CoordinateReferenceSystem oldCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
			CoordinateReferenceSystem newCRS = factory.createCoordinateReferenceSystem("EPSG:3044");
			
			input.put("features", roadsFeature);
			input.put("forcedCRS", oldCRS);
			input.put("targetCRS", newCRS);
		    output.put("roadReproject", ProcessUtil.executeProcess("gs", "Reproject", input, "result"));
	    }
	    catch(FactoryException fe) {
			fe.printStackTrace();
		}
		
		input.clear();
		input.put("first feature collection", output.get("invReproject"));
		input.put("second feature collection", output.get("featureReproject"));
		output.put("invIntersResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		
		input.clear();
		input.put("first feature collection", output.get("hazReproject"));
		input.put("second feature collection", output.get("featureReproject"));
		input.put("first attributes to retain", Arrays.asList(attribute));
		output.put("hazIntersResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		
		input.clear();
		input.put("first feature collection", output.get("roadReproject"));
		input.put("second feature collection", output.get("featureReproject"));
		output.put("roadIntersResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		
		//check if there are roads in the selected area, if so execute the subprocess
		SimpleFeatureCollection roadNetwork = (SimpleFeatureCollection) output.get("roadIntersResult");
		double roadLength = 0;
		if (ProcessUtil.count(roadNetwork) != 0) {
			input.clear();
			input.put("first feature collection", output.get("roadIntersResult"));
			input.put("second feature collection", output.get("invIntersResult"));
			output.put("roadInters2Result", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
			roadLength = ProcessUtil.totalLength((SimpleFeatureCollection) output.get("roadInters2Result"));
		}
		
		//save the area values needed to create the final report
		SimpleFeatureCollection landslidesEvents = (SimpleFeatureCollection) output.get("invIntersResult");
		double inventoryArea = ProcessUtil.totalArea((SimpleFeatureCollection) output.get("invIntersResult"));
		double hazardArea = ProcessUtil.totalArea((SimpleFeatureCollection) output.get("hazIntersResult"));
		double zoneArea = ProcessUtil.totalArea((SimpleFeatureCollection) output.get("featureReproject"));
		List<String> hazardValues = ProcessUtil.valueList((SimpleFeatureCollection) output.get("hazIntersResult"), "LS_HAZ_" + attribute);
		
		//for every category of hazard calculate the statistics
		JsonObjectBuilder classes = Json.createObjectBuilder();
		for (String s : hazardValues) {
			SimpleFeatureCollection sub = (SimpleFeatureCollection) ProcessUtil.valueFilter((SimpleFeatureCollection) output.get("hazIntersResult"), "LS_HAZ_" + attribute, s);
			JsonObjectBuilder hazard = Json.createObjectBuilder()
					.add("area", ProcessUtil.round(ProcessUtil.totalArea(sub) * 0.000001, 3))
					.add("perc", ProcessUtil.round((ProcessUtil.totalArea(sub) * 100) / zoneArea, 2));
			classes.add(s, hazard);
		}
		
		//build the JSON string of the WPS result
		JsonObjectBuilder report = Json.createObjectBuilder();
		report.add("report", Json.createObjectBuilder()
				.add("area", ProcessUtil.round(zoneArea * 0.000001, 3))
				.add("inventory", Json.createObjectBuilder()
						.add("number", ProcessUtil.count(landslidesEvents))
						.add("area", ProcessUtil.round(inventoryArea * 0.000001, 3))
						.add("perc", ProcessUtil.round((inventoryArea * 100) / zoneArea, 2)))
				.add("hazard", Json.createObjectBuilder()
						.add("area", ProcessUtil.round(hazardArea * 0.000001, 3))
						.add("perc", ProcessUtil.round((hazardArea * 100) / zoneArea, 2))
						.add("classes", classes))
				.add("roads", Json.createObjectBuilder()
						.add("length", ProcessUtil.round(roadLength * 0.001, 3))));
			return report.build().toString();
}
}