package it.graphitech.lifeimagine.wps.old;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import it.graphitech.lifeimagine.wps.util.ProcessUtil;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "SoilConsumption3", description = "The process returns the changes occurred between two years in the land cover of the pilot area.")
public class SoilConsumption3v1 implements GSProcess {
	
	/**
	 * WPS of the third soil consumption scenario.
	 * The process returns the changes occurred between two years in the land cover of the pilot area.
	 * 
	 * @param landuse			Feature collection of the land use of the municipalities.
	 * @param attribute			Attribute that contains the area value.
	 * @param startyear			Value of the start year of the change flow.
	 * @param endyear			Value of the end year of the change flow.
	 * @param mapname			Name for land cover change flow map created.
	 * 
	 * @return Name of the land cover change map loaded on the server.
	 * 
	 * A subprocess is executed as explained in the following procedure:
	 * - setting every necessary input
	 * - execute the process and save the output
	 * 
	 */
	@DescribeResult(name = "result", description = "Name of the land cover change map loaded on the server")
	public String execute(
			@DescribeParameter(name = "landuse", description = "Feature collection of the land use") SimpleFeatureCollection landFeature,
			@DescribeParameter(name = "attribute", description = "Attribute that contains the area value") String attribute,
			@DescribeParameter(name = "startyear", description = "Value of the start year of the change flow") String startYear,
			@DescribeParameter(name = "endyear", description = "Value of the end year of the change flow") String endYear,
			@DescribeParameter(name = "mapname", description = "Name for land cover change flow map created") String mapName) {
		
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		//hashmap containing the output of every subprocess
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		/*input.put("features", landFeature);
	    input.put("attribute", startYear);
	    output.put("startUniqueResult", ProcessUtil.executeProcess("gs", "Unique", input, "result"));
		
		input.clear();
		input.put("features", landFeature);
	    input.put("attribute", endYear);
	    output.put("endUniqueResult", ProcessUtil.executeProcess("gs", "Unique", input, "result"));
		
		//create a list with the classes from both years
		SimpleFeatureCollection startFeature = (SimpleFeatureCollection) output.get("startUniqueResult");
		SimpleFeatureCollection endFeature = (SimpleFeatureCollection) output.get("endUniqueResult");*/
		List<String> classList = ProcessUtil.valueList(landFeature, startYear);
		List<String> classList2 = ProcessUtil.valueList(landFeature, endYear);
		for (String s : classList2) {
			if (!classList.contains(s))
				classList.add(s);
		}
		
		//initialize matrix containing the land cover flow data, all values set to 0
		HashMap<String, HashMap<String, Number>> lccfMatrix = new HashMap<String, HashMap<String, Number>>();
		for (String c : classList) {
			HashMap<String, Number> map = new HashMap<String, Number>();
			for (String cc : classList) {
				map.put(cc, 0);
			}
			lccfMatrix.put(c, map);
		}
		
		//fill the matrix with values from land use data
		SimpleFeatureIterator iterator = landFeature.features();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				String sYear = (String) feature.getAttribute(startYear);
				String eYear = (String) feature.getAttribute(endYear);
				if (!sYear.isEmpty() && !eYear.isEmpty()) {
					HashMap<String, Number> map = lccfMatrix.get(sYear);
					//take the saved value in the matrix and add the new one
					Number savedValue = (Number) map.get(eYear);
					Number newValue = (Number) feature.getAttribute(attribute);
					float areaValue = savedValue.floatValue() + newValue.floatValue();
					map.put(eYear, areaValue);
					lccfMatrix.put(sYear, map);
				}
			}
		}
		finally {
			iterator.close();
		}
		
		//write the lccf matrix into a new csv file
		CSVWriter writer;
		String matrixName = mapName + "_matrix";
		try {
			writer = new CSVWriter(new FileWriter("/var/www/lifeimagine.graphitech-projects.com/data/" + matrixName + ".csv"), ';');
			//below lines refers to local path for testing
			//writer = new CSVWriter(new FileWriter("../data_dir/results/matrix.csv"), ';');
			List<String> xClasses = new ArrayList<String>(lccfMatrix.keySet());
			List<String> yClasses = new ArrayList<String>(lccfMatrix.keySet());
			Collections.sort(xClasses);
			Collections.sort(yClasses);
			//create and write csv first line with the land use classes
			xClasses.add(0, startYear + "\\" + endYear);
			String[] header = xClasses.toArray(new String[xClasses.size()]);
			writer.writeNext(header);
			for (String c : yClasses) {
				//create and write a line with class and values from the matrix
				List<String> s = new ArrayList<String>();
				s.add(0, c);
				for (String cc : yClasses) {
					s.add(String.format("%.5f", lccfMatrix.get(c).get(cc).floatValue()));
				}
				String[] array = s.toArray(new String[s.size()]);
				writer.writeNext(array);
			}
			writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		//read the legend for the lccf map from a csv file and create the corresponding matrix
		CSVReader reader;
		HashMap<String, HashMap<String, Number>> legendMatrix = new HashMap<String, HashMap<String, Number>>();
		try {
			reader = new CSVReader(new FileReader("/opt/apache-tomcat-7.0.67/geoserver_data/life-imagine/legend_RT.csv"), ';');
			//below lines refers to local path for testing
			//reader = new CSVReader(new FileReader("../data_dir/results/legend_RT.csv"), ';');
			List<String[]> file = reader.readAll();
			reader.close();
			//get header and remove it
			String[] header = file.get(0);
			file.remove(0);
			for (String[] s : file) {
				HashMap<String, Number> row = new HashMap<String, Number>();
				for (int i = 1; i < header.length; i++) {
					row.put(header[i], Integer.valueOf(s[i]));
				}
				legendMatrix.put(s[0], row);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		//generate the lccf map feature collection
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		SimpleFeatureIterator iter = landFeature.features();
		try {
			SimpleFeatureType schema = landFeature.getSchema();
			CoordinateReferenceSystem featureCRS = schema.getCoordinateReferenceSystem();
			int projCode = CRS.lookupEpsgCode(featureCRS, true);
			//create simple type for the collection
			SimpleFeatureType TYPE = DataUtilities.createType(mapName,
					"the_geom:Polygon:srid=" + projCode + "," +
					startYear + ":String," +
					endYear + ":String," +
					"LCF:int"
	        );
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
			try {
				while (iter.hasNext()){
					SimpleFeature feature = iter.next();
					String sYear = (String) feature.getAttribute(startYear);
					String eYear = (String) feature.getAttribute(endYear);
					
					//build a feature of the collection on at a time
					Geometry geom = (Geometry) feature.getDefaultGeometry();
					featureBuilder.add(geom);
					if (!sYear.isEmpty() && !eYear.isEmpty()) {
						if (legendMatrix.containsKey(sYear)) {
							featureBuilder.add(sYear);
							if (legendMatrix.get(sYear).containsKey(eYear)) {
								featureBuilder.add(eYear);
								featureBuilder.add(legendMatrix.get(sYear).get(eYear));
							}
						}
					}
					SimpleFeature f = featureBuilder.buildFeature(null);
					features.add(f);
				}
			}
			finally {
				iter.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		SimpleFeatureCollection sfc = (SimpleFeatureCollection) DataUtilities.collection(features);
		
		input.clear();
		input.put("features", sfc);
		input.put("workspace", "Life");
		input.put("store", "Imagine");
		input.put("name", mapName);
		input.put("styleName", "LCCF_RT");
	    output.put("collectionName", ProcessUtil.executeProcess("gs", "Import", input, "result"));
		
		return (String) output.get("collectionName");
	}
}