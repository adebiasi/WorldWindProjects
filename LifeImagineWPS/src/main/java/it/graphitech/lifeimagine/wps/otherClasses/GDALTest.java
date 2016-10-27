package it.graphitech.lifeimagine.wps.otherClasses;

import it.graphitech.lifeimagine.wps.PolygonExtractionGDAL_GT;
import it.graphitech.lifeimagine.wps.parser.Type;
import it.graphitech.lifeimagine.wps.util.ProcessUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.ogr.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.PrecisionModel;

public class GDALTest {
	
	
//	 int numBands;
//	 
//	 
//	 Dataset hDataset;
//
//	 public GDALTest(String filename){
//	  gdal.AllRegister();
//	  hDataset = gdal.Open(filename, gdalconstConstants.GA_ReadOnly);
//	  this.numBands = hDataset.getRasterCount();
//	 }
//	 /**
//	  * @param args
//	  */
//	 public static void main(String[] args) {
//	  if(args.length == 0){
//	   System.out.println("You must pass the file name as an argument");
//	  } else {
//		  GDALTest instance = new GDALTest(args[0]);
//	  System.out.println(instance.numBands);
//	  }
//	 }
	
	
	public static void createShapeFile(SimpleFeatureCollection collection, String name){
		 /*
         * Get an output file name and create the new shapefile
         */
		int i = Integer.valueOf((int)(Math.random()*100));
		try{
		String path = "/var/www/lifeimagine.graphitech-projects.com/data/testing/"+name+".shp";
        File newFile = new File(path);

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);

        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
        newDataStore.createSchema(collection.getSchema());

        /*
         * You can comment out this line if you are using the createFeatureType method (at end of
         * class file) rather than DataUtilities.createType
         */
        newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
        
        /*
         * Write the features to the shapefile
         */
        Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(collection);
                transaction.commit();

            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();

            } finally {
                transaction.close();
            }
            
        } else {
            System.out.println(typeName + " does not support read/write access");
           
        }
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	
	
	
	
	public static void gdalPolygonize(String[] args){

	    gdal.AllRegister();
	    

String version = gdal.VersionInfo("VERSION_NUM");
String version2 = gdal.VersionInfo("RELEASE_NAME");

System.out.println("version: "+version);
System.out.println("version2: "+version2);
	    
	    ogr.RegisterAll();
	    //args = gdal.GeneralCmdLineProcessor(args);

	    //Dataset hDataset = gdal.Open("C:\\Users\\a.debiasi\\Desktop\\reclassified.tiff", gdalconstConstants.GA_ReadOnly);
	    //Dataset hDataset = gdal.Open("http://lifeimagine.graphitech-projects.com/data/reclassified.tiff", gdalconstConstants.GA_ReadOnly);
	    Dataset hDataset = gdal.Open("http://lifeimagine.graphitech-projects.com/data/Life-HRL.12.tif", gdalconstConstants.GA_ReadOnly);
	    Band rasterBand = hDataset.GetRasterBand(1);
	    
	    
	    //Driver driver = ogr.GetDriverByName("GeoJSON");
	    //DataSource dataSource =  driver.CreateDataSource("C:\\Users\\a.debiasi\\Desktop\\destination.geojson");
	    
	    Driver driver = ogr.GetDriverByName("Memory");
	    DataSource dataSource =  driver.CreateDataSource("out");
	    
	    
	    SpatialReference sr = new SpatialReference();
        sr.ImportFromWkt(hDataset.GetProjectionRef());
	    
        System.out.println(hDataset.GetProjectionRef());
        
	    Layer outputLayer =dataSource.CreateLayer("destination",sr);
	    
	    //FieldDefn field_def = new FieldDefn("DN",4);
	    FieldDefn field_def = new FieldDefn("DN",ogr.OFTInteger);
	    outputLayer.CreateField(field_def);
	    gdal.Polygonize(rasterBand, null, outputLayer, 0,null, null);
	    int n = outputLayer.GetFeatureCount();
	    
	 //   outputLayer.SetAttributeFilter( "DN = 1" );
	 //   int filteredFeatures = outputLayer.GetFeatureCount();
	    
	   
	   
	   
	   List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		
	   try{
	   SimpleFeatureType TYPE = DataUtilities.createType("it.GraphiTech.Features",
				"the_geom:Polygon:srid=" + 32632 + "," +
				"DN:int" 
       );
	   
	   String namespace = TYPE.getName().getNamespaceURI();
		String localpart = TYPE.getName().getLocalPart();
		String uri = TYPE.getName().getURI();
		
		
		System.out.println("-------->namespace: "+namespace+", localpart: "+localpart+" uri: "+ uri);
	   
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
	   
	   System.out.println("tot features: "+n);
	  // System.out.println("filteredFeatures: "+filteredFeatures);
	   for(int fid=0;fid<n;fid++){
		   Feature feat =  outputLayer.GetFeature(fid);
		   int field = Integer.valueOf(feat.GetFieldAsString("DN"));
		   System.out.println("field: "+field);
		   //if(feat.GetFieldAsString("DN").compareTo("1")==0){
		   if((field>30)&&(field<100)){
	  
//			   int fcount = feat.GetFieldCount();
//			   for (int i = 0; i < fcount; i++) {
//				String field = feat.GetFieldAsString(i);
//				System.out.println("field: "+field);
//				
//			}
			   Geometry geometry = null;
			   double precision = PrecisionModel.maximumPreciseValue;
				GeometryFactory geomFactory = new GeometryFactory(
						new PrecisionModel(precision), 32632);
			   
			   org.gdal.ogr.Geometry geom = feat.GetGeometryRef();
			   org.gdal.ogr.Geometry boundGeom = geom.GetBoundary();

			   int geomType = geom.GetGeometryType();
//			   System.out.println("geomType: "+geomType);
				
			   if (boundGeom != null) {

					// System.out.println("boundGeom!=null");
					// int geomCount = boundGeom.GetGeometryCount();
					// System.out.println("geomCount: "+geomCount);
					int numPoints = boundGeom.GetPointCount();
					System.out.println("points: " + numPoints);
					
					if(numPoints > 0 ){
					Coordinate[] coordinates = new Coordinate[numPoints];
					LinearRing externalRing = null;
					// List<LinearRing> internalRings = new
					// ArrayList<LinearRing>(rings.getLength());

					for (int i = 0; i < numPoints; i++) {
						 System.out.println(i+" : "+ boundGeom.GetPoint(i)[0]+
						 " "+boundGeom.GetPoint(i)[1]);
						coordinates[i] = new Coordinate(boundGeom.GetPoint(i)[0],
								boundGeom.GetPoint(i)[1]);
					}
					externalRing = geomFactory.createLinearRing(coordinates);
					
					geometry = (Geometry) geomFactory.createPolygon(externalRing);
					// break;
					 }else{
						 int geocount = boundGeom.GetGeometryCount(); 
						 if(geocount>0){
					
					
						System.out.println("GetPointCount: " + numPoints);
						System.out.println("GetGeometryCount: "+geocount);
						
						LinearRing externalRing = null;
						 List<LinearRing> internalRings = new
						 ArrayList<LinearRing>(geocount-1);
						
						for (int i = 0; i < geocount; i++) {
							org.gdal.ogr.Geometry g = boundGeom.GetGeometryRef(i);
							int num = g.GetPointCount();
							Coordinate[] coordinates = new Coordinate[num];
							
							
							System.out.println("NUM: "+num);
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
			   
			   featureBuilder.add(feat.GetGeometryRef());
			   featureBuilder.add(feat.GetFieldAsString("DN"));
			   SimpleFeature f = featureBuilder.buildFeature(null);
				features.add(f);
			   
	  
		   }
	   }
	   
	    
	   }catch(Exception e){
		   e.printStackTrace();
	   }
//	    
//	    gdal.AllRegister();
//        Dataset ds = gdal.Open("C:\\Users\\a.debiasi\\Desktop\\reclassified.tiff");
//        ogr.RegisterAll();      
//        DataSource dest = ogr.GetDriverByName("ESRI Shapefile").CreateDataSource("C:\\Users\\a.debiasi\\Desktop\\test");
//        SpatialReference sr = new SpatialReference();
//        sr.ImportFromWkt(ds.GetProjectionRef());
//        Layer dst_layer = dest.CreateLayer("Value", sr);
//        
//        String dst_fldnm = "DN";
//        dst_layer.CreateField(new FieldDefn(dst_fldnm,ogr.OFTInteger)); 
//        int dst_field = 0;
//        Band b = ds.GetRasterBand(ds.getRasterCount());
//gdal.Polygonize(b, null, dst_layer, dst_field);
//	    try{
//File file = new File("C:\\Users\\a.debiasi\\Desktop\\test\\Value.shp");
//FileDataStore store = FileDataStoreFinder.getDataStore(file);
//SimpleFeatureSource fsource =  store.getFeatureSource();
//SimpleFeatureStore shpFeatureStore = (SimpleFeatureStore) fsource;
//
//SimpleFeatureCollection features = fsource.getFeatures();
//
//SimpleFeatureIterator iter = features.features();
//while(iter.hasNext()){
//	SimpleFeature sf = iter.next();
//	
//}
//
//createShapeFile(features);
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }
	}

	
	static private void createShapeFileLocal(SimpleFeatureCollection features) throws SchemaException, IOException{
		/*
         * Get an output file name and create the new shapefile
         */
		final SimpleFeatureType TYPE = DataUtilities.createType("Location",
                "location:Point:srid=4326," + // <- the geometry attribute: Point type
                        "name:String," + // <- a String attribute
                        "number:Integer" // a number attribute
        );
		File newFile = new File("C:\\Users\\a.debiasi\\Desktop\\test\\new.shp");
        

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);

        ShapefileDataStore shpDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
        shpDataStore.createSchema(TYPE);

        String shpTypeName = shpDataStore.getTypeNames()[0];
        SimpleFeatureSource shpFeatureSource = shpDataStore.getFeatureSource(shpTypeName);

        SimpleFeatureStore shpFeatureStore = (SimpleFeatureStore) shpFeatureSource;

        /*
         * You can comment out this line if you are using the createFeatureType method (at end of
         * class file) rather than DataUtilities.createType
         */
        shpDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
        
        
        Transaction transaction = new DefaultTransaction("create");
        shpFeatureStore.setTransaction(transaction);

        try {
            shpFeatureStore.addFeatures(features);
            transaction.commit();
        } catch (Exception problem) {
            problem.printStackTrace();
            transaction.rollback();

        } finally {
            transaction.close();
        }

	}
	
	
	private static void checkIndicators(){
		double builtUpAreaArea = 1455210.483330577;
		double builtUpPerimeter = 71374.14965072415;
		double builtUpGPA = 479318.5401657847;		
		double municAreaArea = 8457942.460709257;
		int builtUpSize = 187;

		
		

		
		
		// calculate only the specified indicators
	
	System.out.println("LPI (%)"+ ProcessUtil.round((builtUpGPA * 100)/ builtUpAreaArea, 2));
System.out.println("RMPS (ha)"+	ProcessUtil.round(((builtUpAreaArea - builtUpGPA) / (builtUpSize - 1)) * 0.0001,2));

		System.out.println("ED (m/ha)"+	ProcessUtil.round(builtUpPerimeter/ (builtUpAreaArea * 0.0001), 2));

		System.out.println("MPA (ha)"+ProcessUtil.round((builtUpAreaArea / builtUpSize) * 0.0001, 2));

			System.out.println(	"BU (%)"+ProcessUtil.round((builtUpAreaArea * 100)/ municAreaArea, 2));

	
	}
	
	public static void main(String[] args) {
		
		
//		String arg = "reclassified.tiff -f \"GeoJSON\" destination.geojson";
//		String[] input = arg.split(" ");
//		//GDALTest.gdalPolygonize(input);
//		SimpleFeatureCollection sfc = new PolygonExtractionGDAL_GT().execute(null, 1, null);
//		SimpleFeatureIterator it = sfc.features();
//		
//		while(it.hasNext()){
//			SimpleFeature s = it.next();
//			
//			String id = s.getID();
//			String namespace = s.getName().getNamespaceURI();
//			String localpart = s.getName().getLocalPart();
//			String uri = s.getName().getURI();
//			Geometry geom = (Geometry)s.getDefaultGeometry();
//			System.out.println("id: "+id);
//			System.out.println(geom.getNumPoints());
//			Coordinate[] coord = geom.getCoordinates();
//			for (int i = 0; i < geom.getNumPoints(); i++) {
//				System.out.println(coord[i]);
//			}
//			//System.out.println("namespace: "+namespace+", localpart: "+localpart+" uri: "+ uri);
//		}
//		
		
		GDALTest.checkIndicators();
		//GDALTest.gdalPolygonize(args);
	}
	
}
