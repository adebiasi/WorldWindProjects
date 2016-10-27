package it.graphitech.lifeimagine.wps;

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

import com.vividsolutions.jts.geom.Geometry;

import it.graphitech.lifeimagine.wps.parser.Type;
import it.graphitech.lifeimagine.wps.parser.WFSParser;
import it.graphitech.lifeimagine.wps.util.ProcessUtil;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "Landslides1", description = "The process reports the information about the area of interest drawn on the map. It compares the landslide hazard and inventory data with the road network to calculate the statistics.")
public class Landslides1 implements GSProcess {
	
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
			@DescribeParameter(name = "hazards", description = "Feature collection of the hazard areas") String hazardURL,
			@DescribeParameter(name = "landslides", description = "Feature collection of the landslide events inventory") String inventoryURL,
			@DescribeParameter(name = "roads", description = "Feature collection of the roads network") SimpleFeatureCollection roadsFeature,
			@DescribeParameter(name = "region", description = "Region of the area of interest") String region) {
		
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		//hashmap containing the output of every subprocess
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		WFSParser hazardParser;
		if (region.equals("RT_BM"))
			hazardParser = new WFSParser(new Type("nz-core:HazardArea", Type.Reg.RT_BM), new Type("nz-core:ObservedEvent", Type.Reg.RT), 3044, Type.Geom.POLYGON);
		else if (region.equals("RT_TN"))
			hazardParser = new WFSParser(new Type("nz-core:HazardArea", Type.Reg.RT_TN), new Type("nz-core:ObservedEvent", Type.Reg.RT), 3044, Type.Geom.POLYGON);
		else
			hazardParser = new WFSParser(new Type("nz-core:HazardArea", Type.Reg.RL), null, 4326, Type.Geom.POLYGON);
		SimpleFeatureCollection hazardFeature = hazardParser.parseWFS(hazardURL);
		
		if (!region.equals("RL")) {
			input.clear();
			input.put("features", hazardFeature);
			input.put("code", "EPSG:4326");
		    output.put("reprojectHaz", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
		    
		    hazardFeature = (SimpleFeatureCollection) output.get("reprojectHaz");
		}
		
		WFSParser inventoryParser;
		if (region.equals("RL"))
			inventoryParser = new WFSParser(new Type("nz-core:ObservedEvent", Type.Reg.RL), null, 4326, Type.Geom.POLYGON);
		else
			inventoryParser = new WFSParser(new Type("nz-core:ObservedEvent", Type.Reg.RT), null, 4326, Type.Geom.POLYGON);
		SimpleFeatureCollection inventoryFeature = inventoryParser.parseWFS(inventoryURL);

		try {
			input.put("geometry", areaInterest);
			input.put("crs", CRS.decode("EPSG:4326"));
			input.put("typeName", "areas");
			output.put("featureResult", ProcessUtil.executeProcess("gs", "Feature", input, "result"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		input.clear();
		input.put("first feature collection", inventoryFeature);
		input.put("second feature collection", output.get("featureResult"));
		output.put("invIntersResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		
		input.clear();
		input.put("features", hazardFeature);
	    input.put("distance", 0.000001);
	    output.put("bufferResultHaz", ProcessUtil.executeProcess("gs", "BufferFeatureCollection", input, "result"));
		
		input.clear();
		input.put("first feature collection", output.get("bufferResultHaz"));
		input.put("second feature collection", output.get("featureResult"));
		input.put("first attributes to retain", Arrays.asList("qualitativeLikelihood"));
		output.put("hazIntersResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		    
		input.clear();
		input.put("first feature collection", roadsFeature);
		input.put("second feature collection", output.get("featureResult"));
		output.put("roadIntersResult", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		
		//check if there are roads in the selected area, if so execute the subprocess
		SimpleFeatureCollection roadNetwork = (SimpleFeatureCollection) output.get("roadIntersResult");
		double roadLength = 0;
		if (ProcessUtil.count(roadNetwork) != 0) {
			input.clear();
			input.put("first feature collection", output.get("roadIntersResult"));
			input.put("second feature collection", output.get("invIntersResult"));
			output.put("roadInters2Result", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
			
			try {
			    input.clear();
				input.put("features", output.get("roadInters2Result"));
				input.put("forcedCRS", CRS.decode("EPSG:4326"));
				input.put("targetCRS", CRS.decode("EPSG:3044"));
			    output.put("roadReproject", ProcessUtil.executeProcess("gs", "Reproject", input, "result"));
		    }
		    catch(FactoryException fe) {
				fe.printStackTrace();
			}

			roadLength = ProcessUtil.totalLength((SimpleFeatureCollection) output.get("roadReproject"));
		}
		
		input.clear();
		input.put("features", output.get("featureResult"));
		input.put("code", "EPSG:3044");
	    output.put("reprojectArea", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
	    
	    input.clear();
		input.put("features", output.get("invIntersResult"));
		input.put("code", "EPSG:3044");
	    output.put("reprojectInv", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
	    
	    input.clear();
		input.put("features", output.get("hazIntersResult"));
		input.put("code", "EPSG:3044");
	    output.put("reprojectHaz", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
		
		//save the area values needed to create the final report
		SimpleFeatureCollection landslidesEvents = (SimpleFeatureCollection) output.get("invIntersResult");
		double inventoryArea = ProcessUtil.totalArea((SimpleFeatureCollection) output.get("reprojectInv"));
		double hazardArea = ProcessUtil.totalArea((SimpleFeatureCollection) output.get("reprojectHaz"));
		double zoneArea = ProcessUtil.totalArea((SimpleFeatureCollection) output.get("reprojectArea"));
		List<String> hazardValues = ProcessUtil.valueList((SimpleFeatureCollection) output.get("reprojectHaz"), "nz-core:HazardArea_qualitativeLikelihood");
		
		//for every category of hazard calculate the statistics
		JsonObjectBuilder classes = Json.createObjectBuilder();
		for (String s : hazardValues) {
			SimpleFeatureCollection sub = (SimpleFeatureCollection) ProcessUtil.valueFilter((SimpleFeatureCollection) output.get("reprojectHaz"), "nz-core:HazardArea_qualitativeLikelihood", s);
			JsonObjectBuilder hazard = Json.createObjectBuilder()
					.add("area", ProcessUtil.round(ProcessUtil.totalArea(sub) * 0.000001, 3))
					.add("perc", ProcessUtil.round((ProcessUtil.totalArea(sub) * 100) / hazardArea, 2));
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