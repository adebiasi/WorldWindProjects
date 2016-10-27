package it.graphitech.lifeimagine.wps.util;

import java.util.HashMap;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "CustomReproject", description = "The process re-projects feature collection and cleans it from invalid geometries.")
public class CustomReproject implements GSProcess {
	
	/**
	 * WPS used in the scenario processes.
	 * The process re-projects feature collection and cleans it from invalid geometries.
	 * 
	 * @param features		Feature collection to re-project.
	 * @param code			Value of the new projection code.
	 * 
	 * @return Re-projected feature collection.
	 * 
	 * A subprocess is executed as explained in the following procedure:
	 * - setting every necessary input
	 * - execute the process and save the output
	 * 
	 */
	@DescribeResult(name = "result", description = "Re-projected feature collection")
	public SimpleFeatureCollection execute(
			@DescribeParameter(name = "features", description = "Feature collection to re-project") SimpleFeatureCollection featureCollection,
			@DescribeParameter(name = "code", description = "Value of the new projection code") String code) {
			
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		//hashmap containing the output of every subprocess
		HashMap<String, Object> output = new HashMap<String, Object>();
				
		int counter = 0;
		SimpleFeatureCollection newCollection;
		//temporary array for storing the buffered features
		SimpleFeature[] features = new SimpleFeature[ProcessUtil.count(featureCollection)];
		SimpleFeatureIterator iterator = featureCollection.features();
		try {
			while(iterator.hasNext()){
				SimpleFeature feature = iterator.next();
				//copy the feature (geometry and attributes)
				features[counter] = feature;
				Geometry geom = (Geometry) feature.getDefaultGeometry();
				if(geom.getGeometryType() == "MultiPolygon") {
					//geometry buffered by 0 to fix imperfections
					features[counter].setDefaultGeometry(geom.buffer(0));
				}
				counter++;
			}
			newCollection = (SimpleFeatureCollection) DataUtilities.collection(features);
		}
		finally {
			iterator.close();
		}
		
		SimpleFeatureType schema = newCollection.getSchema();
		CoordinateReferenceSystem featureCRS = schema.getCoordinateReferenceSystem();
		CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
		
		try {
			//create old and new crs
			int projCode = CRS.lookupEpsgCode(featureCRS, true);
			String epsg = "EPSG:" + projCode;
			CoordinateReferenceSystem oldCRS = factory.createCoordinateReferenceSystem(epsg);
			CoordinateReferenceSystem newCRS = factory.createCoordinateReferenceSystem(code);
			
			input.put("features", newCollection);
			input.put("forcedCRS", oldCRS);
			input.put("targetCRS", newCRS);
		    output.put("reprojectResult", ProcessUtil.executeProcess("gs", "Reproject", input, "result"));
			
			return (SimpleFeatureCollection) output.get("reprojectResult");
		}
		catch(FactoryException fe) {
			fe.printStackTrace();
			
			return featureCollection;
		}
	}

}
