package it.graphitech.lifeimagine.wps.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geoserver.wps.process.GeoServerProcessors;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.NameImpl;
import org.geotools.process.Process;
import org.geotools.process.ProcessFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.util.InternationalString;
import org.opengis.util.ProgressListener;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

public class ProcessUtil {
	/**
	 * Execute a registered processing service. Used to create a subprocess.
	 * 
	 * @param type			Process type.
	 * @param name			Name of the process.
	 * @param input			Input of the process.
	 * @param outputName	Output name.
	 * 
	 * @return Output of the process.
	 * 
	 */
	public static Object executeProcess(String type, String name, HashMap<String, Object> input, String outputName) {
		Map<String, Object> output = new HashMap<String, Object>();
		Set<ProcessFactory> factories = GeoServerProcessors.getProcessFactories();
		Iterator<ProcessFactory> iterator = factories.iterator();
		while(iterator.hasNext()) {
			ProcessFactory fact = (ProcessFactory) iterator.next();
			Name processName = new NameImpl(type, name);
			Set<Name> names = fact.getNames();
			if(names.contains(processName)) {
				Process p = fact.create(processName);
				output = p.execute(input, createProgressListener());
			}
		}
		return output.get(outputName);
	}
	
	/**
	 * Creates a filter of the specified type.
	 * 
	 * @param type			Type of filter, equals/between/...
	 * @param property		Value of the attribute to filter on.
	 * @param var			Array of the filter values.
	 * 
	 * @return New filter.
	 * 
	 */
//	public static Filter createFilter(String type, String property, Object[] var) {
//		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
//		if (type.equals("EQUALS")) {
//			return ff.equals(ff.property(property), ff.literal(var[0]));
//		}
//		else if (type.equals("BETWEEN")) {
//			return ff.between(ff.property(property), ff.literal(var[0]), ff.literal(var[1]));
//		}
//		else if (type.equals("LIKE")) {
//			return ff.like(ff.property(property), ff.literal(var[0]).toString(), "*", "#", "|");
//		}
//		else if (type.equals("CONTAINS")) {
//			return ff.contains(ff.property(property), ff.literal(var[0]));
//		}
//		else {
//			return null;
//		}
//		//more to be added
//	}
	
	/**
	 * Combines two filters using the type specified
	 * 
	 * @param type			Type of combine, and/or.
	 * @param filter1		First filter.
	 * @param filter2		Second filter.
	 * 
	 * @return Combined filter.
	 *  
	 */
	public static Filter combineFilters(String type, Filter filter1, Filter filter2) {
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
		if (type.equals("AND")) {
			return ff.and(filter1, filter2);
		}
		else if (type.equals("OR")) {
			return ff.or(filter1, filter2);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Lists the attributes of the features of a feature collection.
	 * 
	 * @param collection	Feature collection.
	 * 
	 * @return String list of the attributes.
	 * 
	 */
	public static List<String> attributesList(SimpleFeatureCollection collection) {
		SimpleFeatureIterator it = collection.features();
		try {
			List<String> list = new ArrayList<String>();
			while (it.hasNext()) {
				SimpleFeature feature = it.next();
				for (Property p : feature.getProperties()) {
					String attr = p.getName().toString();
					if (!list.contains(attr))
						list.add(attr);
				}
			}
			return list;
		}
		finally {
			it.close();
		}
	}
	
	
	public static Filter createFilter(String type, String property, Object[] var) {
		  FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
		  if (type.equals("EQUALS")) {
		   return ff.equals(ff.property(property), ff.literal(var[0]));
		  }
		  else if (type.equals("GREATER")) {
		   return ff.greater(ff.property(property), ff.literal(var[0]));
		  }
		  else if (type.equals("EQ_GREATER")) {
		   return ff.greaterOrEqual(ff.property(property), ff.literal(var[0]));
		  }
		  else if (type.equals("LESS")) {
		   return ff.less(ff.property(property), ff.literal(var[0]));
		  }
		  else if (type.equals("EQ_LESS")) {
		   return ff.lessOrEqual(ff.property(property), ff.literal(var[0]));
		  }
		  else if (type.equals("BETWEEN")) {
		   return ff.between(ff.property(property), ff.literal(var[0]), ff.literal(var[1]));
		  }
		  else if (type.equals("LIKE")) {
		   return ff.like(ff.property(property), ff.literal(var[0]).toString(), "*", "#", "|");
		  }
		  else if (type.equals("CONTAINS")) {
		   return ff.contains(ff.property(property), ff.literal(var[0]));
		  }
		  else {
		   return null;
		  }
		 }
	
	/**
	 * Lists the attribute values of a feature.
	 * 
	 * @param collection	Feature collection.
	 * @param attribute		Feature attribute .
	 * 
	 * @return String list of the values.
	 * 
	 */
	public static List<String> valueList(SimpleFeatureCollection collection, String attribute) {
		SimpleFeatureIterator it = collection.features();
		try {
			List<String> list = new ArrayList<String>();
			while (it.hasNext()) {
				SimpleFeature feature = it.next();
				String attr = feature.getAttribute(attribute).toString();
				if (!list.contains(attr))
					list.add(attr);
			}
			return list;
		}
		finally {
			it.close();
		}
	}
	
	/**
	 * Lists the attribute values of a feature.
	 * 
	 * @param collection	Feature collection.
	 * @param attribute		Feature attribute.
	 * 
	 * @return String list of the values.
	 * 
	 */
	public static SimpleFeatureCollection valueFilter(SimpleFeatureCollection collection, String attribute, String value) {
		SimpleFeatureIterator it = collection.features();
		try {
			List<SimpleFeature> feats = new ArrayList<SimpleFeature>();
			while (it.hasNext()) {
				SimpleFeature feature = it.next();
				String attr = feature.getAttribute(attribute).toString();
				if (attr.equals(value))
					feats.add(feature);
			}
			return DataUtilities.collection(feats);
		}
		finally {
			it.close();
		}
	}
	
	public static String getGeometryType(SimpleFeatureCollection collection) {
		SimpleFeatureIterator it = collection.features();
		try {
			if (it.hasNext()) {
				SimpleFeature feature = it.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				return geometry.getGeometryType();
			}
		}
		finally {
			it.close();
		}
		return null;
	}
	
	public static SimpleFeatureCollection dissolve(SimpleFeatureCollection collection) {
		System.out.println("inizio dissolve");
		boolean first = true;
		Geometry finalGeometry = null;
		SimpleFeature feat = null;
		SimpleFeatureCollection sfc;
		SimpleFeatureIterator it = collection.features();
		try {
			while(it.hasNext()) {
				SimpleFeature feature = it.next();
				//System.out.println("geometry name: "+feature.getName());
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				if (first) {
					finalGeometry = geometry;
					feat = feature;
					first = false;
				}
				else
					finalGeometry = finalGeometry.union(geometry);
			}
		}
		finally {
			it.close();
		}
		
		feat.setDefaultGeometry(finalGeometry);
		sfc = (SimpleFeatureCollection) DataUtilities.collection(feat);
		System.out.println("fine dissolve");
		return sfc;
	}
	
	public static SimpleFeatureCollection reduce(SimpleFeatureCollection collection) {
		SimpleFeatureIterator it = collection.features();
		PrecisionModel precModel = new PrecisionModel(1000);
		try {
			while(it.hasNext()) {
			SimpleFeature feature = it.next();
			
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				Geometry res = GeometryPrecisionReducer.reduce(geometry, precModel );
				feature.setDefaultGeometry(res);
			}
		}
			finally {
				it.close();
			}
		return collection;
	}
	
	
	public static SimpleFeatureCollection dissolve2(SimpleFeatureCollection collection) {
		System.out.println("inizio dissolve2");
		PrecisionModel precModel = new PrecisionModel(100000);
		boolean first = true;
		Geometry finalGeometry = null;
		SimpleFeature feat = null;
		SimpleFeatureCollection sfc;
		SimpleFeatureIterator it = collection.features();
		try {
			while(it.hasNext()) {
				SimpleFeature feature = it.next();
				//System.out.println("geometry name: "+feature.getName());
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
//				System.out.println("original geom ");
//				for (Coordinate c : geometry.getCoordinates()) {
//					System.out.println(c.toString());
//				}
				
				Geometry res = GeometryPrecisionReducer.reduce(geometry, precModel );
//				System.out.println("mod geom ");
//				for(Coordinate c : res.getCoordinates()){
//					System.out.println(c.toString());
//				}
				
				if (first) {
					finalGeometry = res;
					feat = feature;
					first = false;
				}
				else
					finalGeometry = finalGeometry.union(res);
			}
		}
		finally {
			it.close();
		}
		
		feat.setDefaultGeometry(finalGeometry);
		sfc = (SimpleFeatureCollection) DataUtilities.collection(feat);
		System.out.println("fine dissolve");
		return sfc;
	}
	/**
	 * Calculates the total area of a feature collection by adding the area of its features.
	 * 
	 * @param collection	Feature collection.
	 * 
	 * @return Area value.
	 * 
	 */
	public static double totalArea(SimpleFeatureCollection collection) {
		SimpleFeatureIterator it = collection.features();
		double total = 0;
		try {
			while(it.hasNext()) {
				SimpleFeature feature = it.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				total += geometry.getArea();
			}
			return total;
		}
		finally {
			it.close();
		}
	}
	
	/**
	 * Calculates the perimeter of the features of a feature collection.
	 * 
	 * @param collection	Feature collection.
	 * 
	 * @return Perimeter value.
	 * 
	 */
	public static double totalLength(SimpleFeatureCollection collection) {
		SimpleFeatureIterator it = collection.features();
		double total = 0;
		try {
			while(it.hasNext()) {
				SimpleFeature feature = it.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				total += geometry.getLength();
			}
			return total;
		}
		finally {
			it.close();
		}
	}
	
	/**
	 * Counts the number of features of a collection. "collection.size()" has some problems.
	 * 
	 * @param collection	Feature collection.
	 * 
	 * @return Count value.
	 * 
	 */
	public static int count(SimpleFeatureCollection collection) {
		SimpleFeatureIterator it = collection.features();
		int total = 0;
		try {
			while(it.hasNext()) {
				it.next();
				total ++;
			} 
			return total;
		}
		finally {
			it.close();
		}
	}
	
	/**
	 * Rounds a value to a number of spaces.
	 * 
	 * @param value			Number to round.
	 * @param places		Number of spaces to round to.
	 * 
	 * @return Rounded number.
	 * 
	 */
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Creates a process progress listener. Used in subprocesses.
	 * 
	 * @return Progress listener.
	 * 
	 */
	private static ProgressListener createProgressListener() {
		return new ProgressListener() {
			
			public void setTask(InternationalString task) {
			}
			
			public InternationalString getTask() {
				return null;
			}
			
			public float getProgress() {
				return 0;
			}
			
			public void warningOccurred(String source, String margin, String warning) {
			}
			
			public void started() {
			}
			
			public void setDescription(String description) {
			}
			
			public void setCanceled(boolean cancel) {
			}
			
			public void progress(float percent) {
			}
			
			public boolean isCanceled() {
				return false;
			}
			
			public String getDescription() {
				return null;
			}
			
			public void exceptionOccurred(Throwable exception) {
			}
			
			public void dispose() {
			}
			
			public void complete() {
			}
		};
	}
}
