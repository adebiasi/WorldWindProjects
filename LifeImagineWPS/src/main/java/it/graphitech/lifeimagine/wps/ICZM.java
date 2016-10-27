package it.graphitech.lifeimagine.wps;

import it.graphitech.lifeimagine.wps.util.ProcessUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.geotools.coverage.Category;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollections;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.geotools.referencing.CRS;
import org.geotools.util.NumberRange;
import org.jaitools.media.jai.zonalstats.ZonalStats;
import org.jaitools.media.jai.zonalstats.ZonalStatsDescriptor;
import org.jaitools.media.jai.zonalstats.ZonalStatsOpImage;
import org.jaitools.numeric.Range;
import org.jaitools.numeric.Statistic;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.operation.TransformException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "ICZM", description = "")
public class ICZM implements GSProcess {

	@DescribeResult(name = "result", description = "")
	public String execute(
			@DescribeParameter(name = "TotSediment", description = "") Double totSedimentValue,
			@DescribeParameter(name = "ExtraSediment", description = "") Double extraSedimentValue,
			@DescribeParameter(name = "WaveSediment", description = "") Double waveSedimentValue,
			//@DescribeParameter(name = "WaveRaster", min = 0, description = "") GridCoverage2D waveSedimentRaster,
			@DescribeParameter(name = "RiverSediment", description = "") Double riverSedimentValue,
			@DescribeParameter(name = "LandSediment", min = 0, description = "") Double landSlideValue,
			@DescribeParameter(name = "LandRaster", min = 0, description = "") GridCoverage2D landSlideRaster) {
		Double numResult = totSedimentValue + extraSedimentValue
				- riverSedimentValue;

	//	HashMap<String, Object> input = new HashMap<String, Object>();
	//	HashMap<String, Object> output = new HashMap<String, Object>();
	
System.out.println("primo valore intermedio: "+numResult);
		// PROCESS Feature
		//SimpleFeatureCollection zones = getSimpleFeatureCollection(input, output);

	/*	if (waveSedimentValue == null) {		
			System.out.println("eseguo valore sedimenti mare");
			// PROCESS RasterZonalStatistics
		//	waveSedimentValue = executeRasterZonalStatisticsWPS(input, output, waveSedimentRaster,zones );
			
			try{
		     ZonalStats stats = processStatistics(waveSedimentRaster);
		     double sum = stats.statistic(Statistic.SUM).results().get(0).getValue();
	            double avg = stats.statistic(Statistic.MEAN).results().get(0).getValue();
	            double range = stats.statistic(Statistic.RANGE).results().get(0).getValue();
	            double min = stats.statistic(Statistic.MIN).results().get(0).getValue();
	            double max = stats.statistic(Statistic.MAX).results().get(0).getValue();
	            
	            System.out.println("waveSedimentValue--- sum: "+sum+" - avg: "+avg+" range: "+range+" min: "+min+" max: "+max);
	            waveSedimentValue = sum;
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println("valore: "+waveSedimentValue);
		}*/
		numResult -= waveSedimentValue;
		
		
		if (landSlideValue == null) {
			System.out.println("eseguo valore sedimenti frane");
			// PROCESS RasterZonalStatistics
			//landSlideValue=executeRasterZonalStatisticsWPS(input, output, landSlideRaster,zones );		
		
			
			try{
		     ZonalStats stats = processStatistics(landSlideRaster);
		     double sum = stats.statistic(Statistic.SUM).results().get(0).getValue();
	            double avg = stats.statistic(Statistic.MEAN).results().get(0).getValue();
	            double range = stats.statistic(Statistic.RANGE).results().get(0).getValue();
	            double min = stats.statistic(Statistic.MIN).results().get(0).getValue();
	            double max = stats.statistic(Statistic.MAX).results().get(0).getValue();
	            
	            System.out.println("landSlideValue--- sum: "+sum+" - avg: "+avg+" range: "+range+" min: "+min+" max: "+max);              
	            
	            landSlideValue = -sum;
		}catch(Exception e){
			e.printStackTrace();
		}
			System.out.println("valore: "+landSlideValue);
		}
		numResult -= landSlideValue;
		
		
		JsonObjectBuilder result = Json.createObjectBuilder().add("report",
				numResult);
		return result.build().toString();

	}

    private ZonalStats processStatistics(GridCoverage2D cropped) throws TransformException {
        // double checked with the tasmania simple test data, this transformation
    int band = 0;
  try {             

            // check if the novalue is != from NaN
            GridSampleDimension sampleDimension = cropped.getSampleDimension(0);
            List<Category> categories = sampleDimension.getCategories();
            List<Range<Double>> novalueRangeList = null;
            if (categories != null) {
                for (Category category : categories) {
                    String catName = category.getName().toString();
                    if (catName.equalsIgnoreCase("no data")) {
                        NumberRange range = category.getRange();
                        double min = range.getMinimum();
                        double max = range.getMaximum();
                        if (!Double.isNaN(min) && !Double.isNaN(max)) {
                            
                        	System.out.println("range min max: "+min+" ---- "+max);
                        	
                        	// we have to filter those out
                            Range<Double> novalueRange = new Range<Double>(min, true, max, true);
                            novalueRangeList = new ArrayList<Range<Double>>();
                            novalueRangeList.add(novalueRange);
                        }
                        break;
                    }
                }
            }

             
           // run the stats via JAI
            Statistic[] reqStatsArr = new Statistic[] { Statistic.MAX, Statistic.MIN,
                    Statistic.RANGE, Statistic.MEAN, Statistic.SDEV, Statistic.SUM };
            final ZonalStatsOpImage zsOp = new ZonalStatsOpImage(
                    cropped.getRenderedImage(),
                    null, 
                    null, 
                    null, 
                    reqStatsArr, 
                    new Integer[] { band }, 
                    null, 
                    null,
                    null,
                    null,
                    false,
                    novalueRangeList);
            
            
            return (ZonalStats) zsOp.getProperty(ZonalStatsDescriptor.ZONAL_STATS_PROPERTY);
        } finally {
            // dispose coverages
            if (cropped != null) {
                cropped.dispose(true);
            }
        }

    }
	
	private SimpleFeatureCollection getSimpleFeatureCollection(HashMap<String, Object> input,HashMap<String, Object> output){
	
		SimpleFeatureCollection zones=null;
		try{
		GeometryFactory fact = new GeometryFactory();
		Coordinate[] coordinates = new Coordinate[] { new Coordinate(-180, -90),
				new Coordinate(180, -90), new Coordinate(180, 90),
				new Coordinate(-180, 90) };

		LinearRing linear = new GeometryFactory().createLinearRing(coordinates);
		Polygon poly = new Polygon(linear, null, fact);
		
		input.clear();
		input.put("geometry", poly);
		input.put("crs", CRS.decode("EPSG:4326"));
		input.put("typeName", "Polygons");
		
		
				
		 zones = (SimpleFeatureCollection) ProcessUtil.executeProcess("gs", "Feature", input, "result");
		
		 SimpleFeatureIterator it = zones.features();
			while (it.hasNext()) {
			    SimpleFeature feature = it.next();
			    
			  System.out.println("--- c'è una feature ----");
			  for (Property p : feature.getProperties()) {
					String attr = p.getName().toString();
					System.out.println("..... attr: "+attr);
			  }
			}
		
		}catch(Exception e){e.printStackTrace();
			
		}
		return zones;
	}
		
		
	
	
	private Double executeRasterZonalStatisticsWPS(HashMap<String, Object> input, GridCoverage2D raster,SimpleFeatureCollection zones){
		
		Double sum = 0.0;
		
		input.clear();
		input.put("data", raster);
		input.put("band", 0);
		input.put("zones", zones);
		
		SimpleFeatureCollection outputCollection = (SimpleFeatureCollection) ProcessUtil.executeProcess("gs",
				"RasterZonalStatistics", input, "result");
		
		
		 SimpleFeatureIterator it = outputCollection.features();
		while (it.hasNext()) {
		    SimpleFeature feature = it.next();
		    String val = feature.getAttribute("sum").toString();
		    sum = Double.valueOf(val);
		}
		
		return sum;
		
	}
	
	
}
