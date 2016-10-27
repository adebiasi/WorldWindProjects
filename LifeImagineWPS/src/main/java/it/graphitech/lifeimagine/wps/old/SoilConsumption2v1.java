package it.graphitech.lifeimagine.wps.old;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.jaitools.numeric.Range;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;

import au.com.bytecode.opencsv.CSVReader;
import it.graphitech.lifeimagine.wps.util.ProcessUtil;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "SoilConsumption2", description = "The process calculates the soil consumption indicators for the selected municipality.")
public class SoilConsumption2v1 implements GSProcess {
	
	/**
	 * WPS of the second soil consumption scenario.
	 * The process calculates the soil consumption indicators for the selected municipality.
	 * The indicators are: Largest Patch Index, Remaining Patch Size, Edge Density, Mean Urban Patch Area, Built-up Area, Artificial Area and Population Density.
	 * 
	 * @param imperviousness	Raster containing the imperviousness data.
	 * @param municipalities	Feature collection of the municipalities areas.
	 * @param landuse			Feature collection of the land use.
	 * @param code				Value of the municipality code of interest.
	 * @param indicators		List of indicators to process. Separated by semicolons without whitespaces.
	 * 							Specify: LPI for Largest Patch Index, RMPS for Remaining Patch Size, ED for Edge Density,
	 * 							MPA for Mean urban Patch Area, BU for Built-up area, AA for Artificial Area, PD for Population Density
	 * 
	 * @return JSON of indicators and their values of the selected municipality.
	 * 
	 * A subprocess is executed as explained in the following procedure:
	 * - setting every necessary input
	 * - execute the process and save the output
	 * 
	 */
	@DescribeResult(name = "result", description = "List of indicators and their values of the selected municipality")
	public String execute(
			@DescribeParameter(name = "imperviousness", description = "Raster containing the imperviousness data") GridCoverage2D imperv,
			@DescribeParameter(name = "municipalities", description = "Feature collection of the municipalities areas") SimpleFeatureCollection municipFeature,
			@DescribeParameter(name = "landuse", description = "Feature collection of the land use") SimpleFeatureCollection landFeature,
			@DescribeParameter(name = "code", description = "Value of the municipality code of interest") String code,
			@DescribeParameter(name = "indicators", description = "List of indicators to process. Separated by semicolons without whitespaces."
					+ "Specify: LPI for Largest Patch Index, RMPS for Remaining Patch Size, ED for Edge Density,"
					+ "MPA for Mean urban Patch Area, BU for Built-up area, AA for Artificial Area, PD for Population Density") String indicators) {
		
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		//hashmap containing the output of every subprocess
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		input.put("data", imperv);
		//built-up areas are defined by an imperviousness value >= 30
		Range<Integer> range = new Range<Integer>(30, true, 100, true);
		List<Range<Integer>> list = new ArrayList<Range<Integer>>();
		list.add(range);
	    input.put("ranges", list);
	    output.put("builtUp", ProcessUtil.executeProcess("gs", "PolygonExtraction", input, "result"));
		
	    //create filter for selected municipality code
	    Filter municipFilter = ProcessUtil.createFilter("EQUALS", "PRO_COM", new Object[]{code});
		input.clear();
		input.put("features", municipFeature);
		input.put("filter", municipFilter);
	    output.put("municipArea", ProcessUtil.executeProcess("gs", "Query", input, "result"));
		
	    //create filter for land use artificial areas only
	    Filter artifFilter = ProcessUtil.combineFilters("OR",
	    		ProcessUtil.createFilter("BETWEEN", "COD_USO", new Object[]{100, 200}),
	    		ProcessUtil.createFilter("BETWEEN", "COD_USO", new Object[]{1000, 2000})); 
		input.clear();
		input.put("features", landFeature);
		input.put("filter", artifFilter);
	    output.put("artifArea", ProcessUtil.executeProcess("gs", "Query", input, "result"));
		
		SimpleFeatureCollection municipArea = (SimpleFeatureCollection) output.get("municipArea");
		
		input.clear();
		input.put("first feature collection", output.get("builtUp"));
		input.put("second feature collection", municipArea);
	    output.put("intersect1", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		
		input.clear();
		input.put("features", output.get("artifArea"));
		input.put("code", "EPSG:32632");
	    output.put("reproject", ProcessUtil.executeProcess("gs", "CustomReproject", input, "result"));
		
		input.clear();
		input.put("first feature collection", output.get("reproject"));
		input.put("second feature collection", municipArea);
	    output.put("intersect2", ProcessUtil.executeProcess("gs", "IntersectionFeatureCollection", input, "result"));
		
		//collect values to calculate the indicators
		SimpleFeatureCollection builtUpArea = (SimpleFeatureCollection) output.get("intersect1");
		SimpleFeatureCollection artifArea = (SimpleFeatureCollection) output.get("intersect2");
		double builtUpAreaArea = ProcessUtil.totalArea(builtUpArea);
		double builtUpPerimeter = ProcessUtil.totalLength(builtUpArea);
		double builtUpGPA = greaterPatchArea(builtUpArea);
		double artifAreaArea = ProcessUtil.totalArea(artifArea);
		double municAreaArea = ProcessUtil.totalArea(municipArea);
		int builtUpSize = ProcessUtil.count(builtUpArea);
		
		JsonObjectBuilder report = Json.createObjectBuilder();
		//calculate only the specified indicators
		String[] indicat = indicators.split(";");
		for (int i = 0; i < indicat.length; i++) {
			if (indicat[i].equals("LPI"))
				report.add("LPI (%)", ProcessUtil.round((builtUpGPA * 100) / builtUpAreaArea, 2));
			
			if (indicat[i].equals("RMPS"))
				report.add("RMPS (ha)", ProcessUtil.round(((builtUpAreaArea - builtUpGPA) / (builtUpSize - 1)) * 0.0001, 2));
			
			if (indicat[i].equals("ED"))
				report.add("ED (m/ha)", ProcessUtil.round(builtUpPerimeter / (builtUpAreaArea * 0.0001), 2));
			
			if (indicat[i].equals("MPA"))
				report.add("MPA (ha)", ProcessUtil.round((builtUpAreaArea / builtUpSize) * 0.0001, 2));
			
			if (indicat[i].equals("BU"))
				report.add("BU (%)", ProcessUtil.round((builtUpAreaArea * 100) / municAreaArea, 2));
			
			if (indicat[i].equals("AA"))
				report.add("AA (ha)", ProcessUtil.round(artifAreaArea * 0.0001, 2));
			
			if (indicat[i].equals("PD"))
				report.add("PD (inhab/ha)", ProcessUtil.round(population("/opt/apache-tomcat-7.0.67/geoserver_data/life-imagine/istat.csv", code) / (municAreaArea * 0.0001), 2));
		}
		JsonObjectBuilder result = Json.createObjectBuilder().add("report", report);
		return result.build().toString();
	}
	
	/**
	 * Get the area value of the largest feature in a collection.
	 * 
	 * @param collection Feature collection.
	 * 
	 * @return The area value of the largest patch.
	 */
	public static double greaterPatchArea(SimpleFeatureCollection collection) {
		SimpleFeatureIterator iterator = collection.features();
		double max = 0;
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				if (geometry.getArea() > max) {
					max = geometry.getArea();
				}
			}
			return max;
		}
		finally {
			iterator.close();
		}
	}
	
	/**
	 * Extract the population value of a specific municipality from a csv file.
	 * 
	 * @param file Path of the file.
	 * @param code Municipality code.
	 * 
	 * @return The population number.
	 */
	public static double population(String file, String code) {
		try {
			//init with path and separator
			CSVReader reader = new CSVReader(new FileReader(file), ';');
			List<String[]> csv = reader.readAll();
			int popIndex = 0;
			int codeIndex = 0;
			double total = 0;
			for (int i = 0; i < csv.get(0).length; i++) {
				//get the index of the column named P1, containing the population value
				if (csv.get(0)[i].compareTo("P1") == 0) {
					popIndex = i;
				}
				//get the index of the column named PROCOM, containing the municipality code
				if (csv.get(0)[i].compareTo("PROCOM") == 0) {
					codeIndex = i;
				}
			}
			Iterator<String[]> iterator = csv.iterator();
			//skip the first line of the csv
			if (iterator.hasNext()) {
				iterator.next();
			}
			while (iterator.hasNext()) {
				String[] line = (String[]) iterator.next();
				//if code is the one of the municipality selected get the population value
				if (line[codeIndex].compareTo(code) == 0) {
					total += Double.parseDouble(line[popIndex]);
				}
			}
			reader.close();
			return total;
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}