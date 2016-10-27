package it.graphitech.lifeimagine.wps;

import it.graphitech.lifeimagine.wps.otherClasses.GDALTest;
import it.graphitech.lifeimagine.wps.otherClasses.RangeLookup_old;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

import org.jaitools.media.jai.vectorize.VectorizeDescriptor;
import org.jaitools.numeric.Range;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.util.AffineTransformation;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.raster.CoverageUtilities;
import org.geotools.process.raster.RasterProcess;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.process.ProcessException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.util.ProgressListener;

/**
 * A process for raster to vector conversion. Regions of uniform value in an
 * input {@linkplain GridCoverage2D} are converted into {@linkplain Polygon}s by
 * tracing the cell boundaries. Results are returned as a
 * {@linkplain SimpleFeatureCollection} in which each feature corresponds to a
 * raster region with the boundary {@code Polygon} as its default geometry
 * ("the_geom") and the value of the raster region cells as an attribute
 * ("value").
 * <p>
 * Optionally, a list of classification ranges (
 * {@linkplain org.jaitools.numeric.Range} objects) can be provided to
 * pre-classify the input coverage values into intervals. Vectorizing can also
 * be restricted to a sub-area of the coverage and/or a subset of raster values
 * (by defining values to treat as no-data).
 * 
 * @author Simone Giannecchini, GeoSolutions
 * @since 8.0
 *
 * @source $URL$
 * @version $Id$
 */
@DescribeProcess(title = "Polygon Extraction", description = "Extracts vector polygons from a raster, based on regions which are equal or in given ranges")
public class PolygonExtractionGDAL_GT implements RasterProcess {

	public PolygonExtractionGDAL_GT() {
		System.out.println("contructior");
	}

	/**
	 * Executes the raster to vector process.
	 * 
	 * @param coverage
	 *            the input grid coverage
	 * 
	 * @param band
	 *            the coverage band to process; defaults to 0 if {@code null}
	 * 
	 * @param insideEdges
	 *            whether boundaries between raster regions with data values
	 *            (ie. not NODATA) should be returned; defaults to {@code true}
	 *            if {@code null}
	 * 
	 * @param roi
	 *            optional polygonal {@code Geometry} to define a sub-area
	 *            within which vectorizing will be done
	 * 
	 * @param noDataValues
	 *            optional list of values to treat as NODATA; regions with these
	 *            values will not be represented in the returned features; if
	 *            {@code null}, 0 is used as the single NODATA value; ignored if
	 *            {@code  classificationRanges} is provided
	 * 
	 * @param classificationRanges
	 *            optional list of {@code Range} objects to pre-classify the
	 *            input coverage prior to vectorizing; values not included in
	 *            the list will be treated as NODATA; values in the first
	 *            {@code Range} are classified to 1, those in the second
	 *            {@code Range} to 2 etc.
	 * 
	 * @param progressListener
	 *            an optional listener
	 * 
	 * @return a feature collection where each feature has a {@code Polygon}
	 *         ("the_geom") and an attribute "value" with value of the
	 *         corresponding region in either {@code coverage} or the classified
	 *         coverage (when {@code classificationRanges} is used)
	 * 
	 * @throws ProcessException
	 */

	@DescribeResult(name = "result", description = "The extracted polygon features")
	public SimpleFeatureCollection execute(
			@DescribeParameter(name = "data", description = "Path of the file") String path,
			//@DescribeParameter(name = "minValue", description = "Min value") Integer minValue,
			//@DescribeParameter(name = "maxValue", description = "Min value") Integer maxValue,
			@DescribeParameter(name = "band", description = "Source band to use (default = 0)", min = 0, defaultValue = "0") Integer band,
			ProgressListener progressListener) throws ProcessException {

		System.out.println("EXCECUTE PROCESS PolygonExtraction...");

//		String javaLibPath = System.getProperty("java.library.path");
//		Map<String, String> envVars = System.getenv();
//		System.out.println(envVars.get("Path"));
//		System.out.println("javaLibPath: " + javaLibPath);
//		for (String var : envVars.keySet()) {
//			System.out.println("examining: " + var);
//			System.out.println("result: " + envVars.get(var));
//			if (envVars.get(var).equals(javaLibPath)) {
//				System.out.println(var);
//			}
//		}

		gdal.AllRegister();

//		String version = gdal.VersionInfo("VERSION_NUM");
//		String version2 = gdal.VersionInfo("RELEASE_NAME");
//
//		System.out.println("version: " + version);
//		System.out.println("version2: " + version2);

		ogr.RegisterAll();

		// Dataset hDataset =
		// gdal.Open("C:\\Users\\a.debiasi\\Desktop\\reclassified.tiff",
		// gdalconstConstants.GA_ReadOnly);
		// Dataset hDataset =
		// gdal.Open("http://lifeimagine.graphitech-projects.com/data/reclassified.tiff",
		// gdalconstConstants.GA_ReadOnly);
		
		
		Dataset hDataset = gdal
		//		.Open("/var/www/lifeimagine.graphitech-projects.com/data/reclassified.tiff",
				.Open(path,
						gdalconstConstants.GA_ReadOnly);
		
		
		Band rasterBand = hDataset.GetRasterBand(1);
		// Driver driver = ogr.GetDriverByName("GeoJSON");
		// DataSource dataSource =
		// driver.CreateDataSource("C:\\Users\\a.debiasi\\Desktop\\destination.geojson");

		Driver driver = ogr.GetDriverByName("Memory");
		DataSource dataSource = driver.CreateDataSource("out");

		SpatialReference sr = new SpatialReference();
		sr.ImportFromWkt(hDataset.GetProjectionRef());

		System.out.println(hDataset.GetProjectionRef());

		Layer outputLayer = dataSource.CreateLayer("destination", sr);

		// FieldDefn field_def = new FieldDefn("DN",4);
		FieldDefn field_def = new FieldDefn("DN", ogr.OFTInteger);
		outputLayer.CreateField(field_def);
		gdal.Polygonize(rasterBand, null, outputLayer, 0, null, null);
		int n = outputLayer.GetFeatureCount();

		//outputLayer.SetAttributeFilter("DN = 1");
		//int filteredFeatures = outputLayer.GetFeatureCount();

		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
				//filteredFeatures);

		try {
			int proj = 32632;

			SimpleFeatureType TYPE = DataUtilities.createType(
					"it.GraphiTech.Features", "the_geom:Polygon:srid=" + proj
							+ "," + "DN:int");

			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

			System.out.println("tot features: " + n);
			//System.out.println("filteredFeatures: " + filteredFeatures);
			for (int fid = 0; fid < n; fid++) {
				
					Feature feat = outputLayer.GetFeature(fid);
				//	 System.out.println("feat.GetFID(): "+feat.GetFID());
				//	 System.out.println("feat.GetFieldAsString(DN): "+feat.GetFieldAsString("DN"));
					
				//int val = Integer.valueOf(feat.GetFieldAsString("DN"));
				if (feat.GetFieldAsString("DN").compareTo("1") == 0) {
					// if ((val>=minValue)&&(val<=maxValue) ){

					org.gdal.ogr.Geometry g = feat.GetGeometryRef();

					// Geometry geom = new Geometry;
					Geometry geom = convertGeometry(fid,g, proj);

					if (geom != null) {
						featureBuilder.add(geom);
						featureBuilder.add(feat.GetFieldAsString("DN"));
						SimpleFeature f = featureBuilder.buildFeature(null);
						features.add(f);
					} else {
						System.out.println("a geometry is null!");
					}
					
				
		
			
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// return value
		return DataUtilities.collection(features);
	}

	public static Geometry convertGeometry(int fid,org.gdal.ogr.Geometry geom,
			int projection) {

		Geometry geometry = null;
		try {
			double precision = PrecisionModel.maximumPreciseValue;
			GeometryFactory geomFactory = new GeometryFactory(
					new PrecisionModel(precision), projection);

			org.gdal.ogr.Geometry boundGeom = geom.GetBoundary();

			
			if (boundGeom != null) {

				// System.out.println("boundGeom!=null");
				// int geomCount = boundGeom.GetGeometryCount();
				// System.out.println("geomCount: "+geomCount);
				int numPoints = boundGeom.GetPointCount();
				//System.out.println("points: " + numPoints);
				
				if(numPoints > 0 ){
				Coordinate[] coordinates = new Coordinate[numPoints];
				LinearRing externalRing = null;
				// List<LinearRing> internalRings = new
				// ArrayList<LinearRing>(rings.getLength());

				for (int i = 0; i < numPoints; i++) {
					 //System.out.println(i+" : "+ boundGeom.GetPoint(i)[0]+
					 //" "+boundGeom.GetPoint(i)[1]);
					coordinates[i] = new Coordinate(boundGeom.GetPoint(i)[0],
							boundGeom.GetPoint(i)[1]);
				}
				externalRing = geomFactory.createLinearRing(coordinates);
				
				geometry = (Geometry) geomFactory.createPolygon(externalRing);
				// break;
				 }else{
					 int geocount = boundGeom.GetGeometryCount(); 
					 if(geocount>0){
				
				
					//System.out.println("GetPointCount: " + numPoints);
					//System.out.println("GetGeometryCount: "+geocount);
					
					LinearRing externalRing = null;
					 List<LinearRing> internalRings = new
					 ArrayList<LinearRing>(geocount-1);
					
					for (int i = 0; i < geocount; i++) {
						org.gdal.ogr.Geometry g = boundGeom.GetGeometryRef(i);
						int num = g.GetPointCount();
						Coordinate[] coordinates = new Coordinate[num];
						
						
						//System.out.println("NUM: "+num);
						for (int j = 0; j < num; j++) {
							coordinates[j] = new Coordinate(g.GetPoint(j)[0],
									g.GetPoint(j)[1]);
						}
						
						if(i == 0){
							externalRing = geomFactory.createLinearRing(coordinates);
						}else{
							internalRings.add(geomFactory.createLinearRing(coordinates));						
					}
					}
					 LinearRing[] intRings = new LinearRing[internalRings.size()];
					 intRings = internalRings.toArray(intRings);
					geometry = (Geometry) geomFactory.createPolygon(externalRing,intRings);
					}
				 }
			} else {
				System.out.println("a boundGeom is null!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// storeValue(node.getNodeName().split(":")[1], geometry, tempIndex);
		// System.out.println(node.getNodeName().split(":")[1]);
		return geometry;
	}

}