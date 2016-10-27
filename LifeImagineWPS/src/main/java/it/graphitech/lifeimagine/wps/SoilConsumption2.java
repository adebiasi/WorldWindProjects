package it.graphitech.lifeimagine.wps;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.geotools.process.raster.PolygonExtractionProcess;
import org.jaitools.numeric.Range;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;

import com.vividsolutions.jts.geom.Geometry;

import it.graphitech.lifeimagine.wps.otherClasses.GDALTest;
import it.graphitech.lifeimagine.wps.otherClasses.RangeLookup_old;
import it.graphitech.lifeimagine.wps.parser.Type;
import it.graphitech.lifeimagine.wps.parser.WFSParser;
import it.graphitech.lifeimagine.wps.util.ProcessUtil;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "SoilConsumption2", description = "The process calculates the soil consumption indicators for the selected municipality.")
public class SoilConsumption2 implements GSProcess {

	/**
	 * WPS of the second soil consumption scenario. The process calculates the
	 * soil consumption indicators for the selected municipality. The indicators
	 * are: Largest Patch Index, Remaining Patch Size, Edge Density, Mean Urban
	 * Patch Area, Built-up Area, Artificial Area and Population Density.
	 * 
	 * @param imperviousness
	 *            Raster containing the imperviousness data.
	 * @param municipalities
	 *            Feature collection of the municipalities areas.
	 * @param landuse
	 *            Feature collection of the land use.
	 * @param code
	 *            Value of the municipality code of interest.
	 * @param indicators
	 *            List of indicators to process. Separated by semicolons without
	 *            whitespaces. Specify: LPI for Largest Patch Index, RMPS for
	 *            Remaining Patch Size, ED for Edge Density, MPA for Mean urban
	 *            Patch Area, BU for Built-up area, AA for Artificial Area, PD
	 *            for Population Density
	 * 
	 * @return JSON of indicators and their values of the selected municipality.
	 * 
	 *         A subprocess is executed as explained in the following procedure:
	 *         - setting every necessary input - execute the process and save
	 *         the output
	 * 
	 */
	@DescribeResult(name = "result", description = "List of indicators and their values of the selected municipality")
	public String execute(
			@DescribeParameter(name = "imperviousness", description = "Raster containing the imperviousness data") GridCoverage2D imperv,
			@DescribeParameter(name = "landuse", min = 0, description = "Url of the feature collection of the land use") String landURL,
			@DescribeParameter(name = "municipalities", description = "Url of the feature collection of the municipalities areas") String municipURL,
			@DescribeParameter(name = "code", description = "Value of the municipality code of interest") String code,
			@DescribeParameter(name = "indicators", description = "List of indicators to process. Separated by semicolons without whitespaces."
					+ "Specify: LPI for Largest Patch Index, RMPS for Remaining Patch Size, ED for Edge Density,"
					+ "MPA for Mean urban Patch Area, BU for Built-up area, AA for Artificial Area, PD for Population Density") String indicators)
			throws Exception {

		SimpleFeatureCollection landFeature = null;
		
		if(landURL!=null){
		WFSParser landParser = new WFSParser(new Type("lcv:LandCoverUnit",
				Type.Reg.RL_TG), null, 4326, Type.Geom.POLYGON);
		 landFeature = landParser.parseWFS(landURL);
		}
		
		WFSParser municipParser = new WFSParser(new Type(
				"pd:StatisticalDistribution", null), new Type(
				"su-vector:AreaStatisticalUnit", null), 3044, Type.Geom.POLYGON);
		SimpleFeatureCollection municipFeature = municipParser
				.parseWFS(municipURL);

		// hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
String coverageName = imperv.getName().toString();
System.out.println("coverageName: "+coverageName);

		input.put("data", imperv);
		// built-up areas are defined by an imperviousness value >= 30
		Range<Integer> range = new Range<Integer>(30, true, 100, true);
		List<Range> list = new ArrayList<Range>();
		list.add(range);
		input.put("ranges", list);		
		System.out.println("eseguo PolygonExtractionProcess");
		
		//PolygonExtractionGT polExtr = new PolygonExtractionGT();
		//SimpleFeatureCollection featureCollection = polExtr.execute(imperv,
		//		null, null, null, null, list, 1, 0.0, 0, null);
System.out.println("Execute RangeLookup_old");
		RangeLookup_old lookup = new RangeLookup_old();
		GridCoverage2D coverage = lookup.execute(
				   imperv,
                   Integer.valueOf(0),
                   list,
                   null);
		
		System.out.println("save result");
		
		//String filename = "/var/www/lifeimagine.graphitech-projects.com/data/Life-HRL.12.reclassified.tif";
		String filename = "/var/www/lifeimagine.graphitech-projects.com/data/"+ coverageName +"reclassified.tif";
		
		try {
	        GeoTiffWriteParams wp = new GeoTiffWriteParams();
	        wp.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
	        wp.setCompressionType("LZW");
	        ParameterValueGroup params = new GeoTiffFormat().getWriteParameters();
	        params.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(wp);
	        GeoTiffWriter writer = new GeoTiffWriter(new File(filename));
	        writer.write(coverage, (GeneralParameterValue[]) params.values().toArray(new GeneralParameterValue[1]));
	    } catch (Exception e) {
	       
	        e.printStackTrace();
	    }
		
		System.out.println("poligonyze");
		
		PolygonExtractionGDAL_GT polExtr = new PolygonExtractionGDAL_GT();
				SimpleFeatureCollection featureCollection = polExtr.execute(
						filename, 
						//30,100,
						0, null);

		
		int numPolygons = featureCollection.size();
		System.out.println("num poly: " + numPolygons);

		SimpleFeatureIterator iterator = featureCollection.features();
		while (iterator.hasNext()) {
			SimpleFeature feature = iterator.next();
			Object o = feature.getAttribute("value");
			//System.out.println("p: " + o);
		}

		input.clear();
		input.put("features", featureCollection);
		input.put("code", "EPSG:4326");
		SimpleFeatureCollection reprojectExtract = (SimpleFeatureCollection) ProcessUtil
				.executeProcess("gs", "CustomReproject", input, "result");

		input.clear();
		input.put("features", municipFeature);
		input.put("code", "EPSG:4326");
		SimpleFeatureCollection reprojectFilter = (SimpleFeatureCollection) ProcessUtil
				.executeProcess("gs", "CustomReproject", input, "result");

		// create filter for selected municipality code
		Filter municipFilter = ProcessUtil.createFilter("LIKE", "localId",
				new Object[] { code + "*" });
		input.clear();
		input.put("features", reprojectFilter);
		input.put("filter", municipFilter);
		SimpleFeatureCollection municipArea = (SimpleFeatureCollection) ProcessUtil
				.executeProcess("gs", "Query", input, "result");

		
		
		
		input.clear();
		input.put("features", municipArea);
		input.put("code", "EPSG:3044");
		SimpleFeatureCollection reprojectMunic = (SimpleFeatureCollection) ProcessUtil
				.executeProcess("gs", "CustomReproject", input, "result");
		
		//GDALTest.createShapeFile(reprojectMunic,"reprojectMunic");
		//GDALTest.createShapeFile(reprojectExtract,"reprojectExtract");
			input.clear();
		input.put("first feature collection", reprojectExtract);
		input.put("second feature collection",
				ProcessUtil.dissolve(municipArea));
		SimpleFeatureCollection intersect_polyg_munic_area = (SimpleFeatureCollection) ProcessUtil
				.executeProcess("gs", "IntersectionFeatureCollection", input,
						"result");

		
		
		input.clear();
		input.put("features", intersect_polyg_munic_area);
		input.put("code", "EPSG:3044");
		SimpleFeatureCollection reprojectIntersect_polyg_munic_area = (SimpleFeatureCollection) ProcessUtil
				.executeProcess("gs", "CustomReproject", input, "result");
		
		GDALTest.createShapeFile(reprojectIntersect_polyg_munic_area,"Intersect_polyg_munic_"+code+"_"+coverageName);
		
		
		double artifAreaArea = 0;
		if (landURL != null) {
		// create filter for land use artificial areas only
		Filter artifFilter = ProcessUtil.createFilter("LIKE", "class1",
				new Object[] { "*/LandCoverClassValue/1*" });
		input.clear();
		input.put("features", landFeature);
		input.put("filter", artifFilter);
		SimpleFeatureCollection filteredLandFeature = (SimpleFeatureCollection) ProcessUtil
				.executeProcess("gs", "Query", input, "result");

		input.clear();
		input.put("second feature collection", filteredLandFeature);
		input.put("first feature collection",
				ProcessUtil.dissolve((SimpleFeatureCollection) municipArea));
		SimpleFeatureCollection intersect_artif_munic_Area = (SimpleFeatureCollection) ProcessUtil
				.executeProcess("gs", "IntersectionFeatureCollection", input,
						"result");

		input.clear();
		input.put("features", intersect_artif_munic_Area);
		input.put("code", "EPSG:3044");
		SimpleFeatureCollection reprojectIntersect_artif_munic_Area = (SimpleFeatureCollection) ProcessUtil
				.executeProcess("gs", "CustomReproject", input, "result");
		
		SimpleFeatureCollection artifArea = reprojectIntersect_artif_munic_Area;
		 artifAreaArea = ProcessUtil.totalArea(artifArea);
		}
		

		// collect values to calculate the indicators
		SimpleFeatureCollection builtUpArea = reprojectIntersect_polyg_munic_area;		
		
		double builtUpAreaArea = ProcessUtil.totalArea(builtUpArea);
		double builtUpPerimeter = ProcessUtil.totalLength(builtUpArea);
		double builtUpGPA = greaterPatchArea(builtUpArea);
		
		double municAreaArea = ProcessUtil.totalArea(reprojectMunic);
		int builtUpSize = ProcessUtil.count(builtUpArea);

		System.out.println("builtUpAreaArea: "+builtUpAreaArea);
		System.out.println("builtUpPerimeter: "+builtUpPerimeter);
		System.out.println("builtUpGPA: "+builtUpGPA);
		System.out.println("municAreaArea: "+municAreaArea);
		System.out.println("builtUpSize: "+builtUpSize);
		
		JsonObjectBuilder report = Json.createObjectBuilder();
		// calculate only the specified indicators
		String[] indicat = indicators.split(";");
		for (int i = 0; i < indicat.length; i++) {
			if (indicat[i].equals("LPI"))
				report.add("LPI (%)", ProcessUtil.round((builtUpGPA * 100)
						/ builtUpAreaArea, 2));

			if (indicat[i].equals("RMPS"))
				report.add(
						"RMPS (ha)",
						ProcessUtil
								.round(((builtUpAreaArea - builtUpGPA) / (builtUpSize - 1)) * 0.0001,
										2));

			if (indicat[i].equals("ED"))
				report.add(
						"ED (m/ha)",
						ProcessUtil.round(builtUpPerimeter
								/ (builtUpAreaArea * 0.0001), 2));

			if (indicat[i].equals("MPA"))
				report.add("MPA (ha)", ProcessUtil.round(
						(builtUpAreaArea / builtUpSize) * 0.0001, 2));

			if (indicat[i].equals("BU"))
				report.add(
						"BU (%)",
						ProcessUtil.round((builtUpAreaArea * 100)
								/ municAreaArea, 2));

			if (landURL != null) {
				if (indicat[i].equals("AA"))
					report.add("AA (ha)",
							ProcessUtil.round(artifAreaArea * 0.0001, 2));
			}

			if (indicat[i].equals("PD"))
				report.add("PD (inhab/ha)",
						ProcessUtil.round(populationDensity(reprojectMunic), 2));
		}
		JsonObjectBuilder result = Json.createObjectBuilder().add("report",
				report);
		return result.build().toString();
	}

	/**
	 * Get the area value of the largest feature in a collection.
	 * 
	 * @param collection
	 *            Feature collection.
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
		} finally {
			iterator.close();
		}
	}

	public static double populationDensity(SimpleFeatureCollection collection) {
		SimpleFeatureIterator it = collection.features();
		try {
			double totalPop = 0;
			double totalArea = 0;
			while (it.hasNext()) {
				SimpleFeature feature = it.next();
				if (feature.getAttribute("value") != null
						&& feature.getAttribute("areaValue") != null)
					totalPop += Double.parseDouble(feature
							.getAttribute("value").toString())
							* Double.parseDouble(feature.getAttribute(
									"areaValue").toString());
				if (feature.getAttribute("areaValue") != null)
					totalArea += Double.parseDouble(feature.getAttribute(
							"areaValue").toString());
			}
			return totalPop / totalArea;
		} finally {
			it.close();
		}
	}
}