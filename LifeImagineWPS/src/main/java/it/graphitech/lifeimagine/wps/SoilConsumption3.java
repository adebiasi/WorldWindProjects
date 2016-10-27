package it.graphitech.lifeimagine.wps;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import it.graphitech.lifeimagine.wps.otherClasses.SerialSimpleFeature;
import it.graphitech.lifeimagine.wps.parser.SC3mngt;
import it.graphitech.lifeimagine.wps.parser.Type;
import it.graphitech.lifeimagine.wps.parser.WFSParser;
import it.graphitech.lifeimagine.wps.util.ProcessUtil;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "SoilConsumption3", description = "The process returns the changes occurred between two years in the land cover of the pilot area.")
public class SoilConsumption3 implements GSProcess {
	
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
	 * @throws Exception 
	 * 
	 */
	@DescribeResult(name = "result", description = "Name of the land cover change map loaded on the server")
	public String execute(
			@DescribeParameter(name = "landuse", description = "Feature collection of the land use") String landURL,
			@DescribeParameter(name = "mapname", description = "Name for land cover change flow map created") String mapName) throws Exception {
		/*
		WFSParser landParser = new WFSParser(new Type("lcv:LandCoverUnit", Type.Reg.RT), null, 3044, Type.Geom.POLYGON);
		SimpleFeatureCollection landFeature = landParser.parseWFS(landURL);
		
		//create a list with the classes from both years
		List<String> classList = new ArrayList<String>();
		for (String s : ProcessUtil.valueList(landFeature, "class1")) {
			classList.add(s.substring(s.lastIndexOf("/") + 1));
		}
		for (String s : ProcessUtil.valueList(landFeature, "class2")) {
			if (!classList.contains(s.substring(s.lastIndexOf("/") + 1)))
				classList.add(s.substring(s.lastIndexOf("/") + 1));
		}
		
		Collections.sort(classList);
		*/
		
		//landurl = http://lifeimagine.graphitech-projects.com/deegree/services/lc_sc?service=WFS&request=GetFeature&version=2.0.0&typeNames=lcv:LandCoverUnit";
		
		
		
		SerialSimpleFeature[] landFeature = SC3mngt.retrieveObjects(landURL);
		
		if(landFeature==null){
			throw new Exception("Loading Error for the file "+landURL);
		}else{
			System.out.println("there are "+landFeature.length +"featutes");
		}
		
		//create a list with the classes from both years
				List<String> classList = new ArrayList<String>();
				for(SerialSimpleFeature feature : landFeature){
					String cl1 = feature.getClass1();
					cl1=cl1.substring(cl1.lastIndexOf("/") + 1);
					if (!classList.contains(cl1))
						classList.add(cl1);
					String cl2 = feature.getClass2();
					cl2=cl2.substring(cl2.lastIndexOf("/") + 1);
					if (!classList.contains(cl2))
						classList.add(cl2);
				}
						
				Collections.sort(classList);
		
		
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
		//SimpleFeatureIterator iterator = landFeature.features();
		for(SerialSimpleFeature feature : landFeature){
				//SerialSimpleFeature feature = iterator.next();
				String sYear = feature.getClass1().substring(feature.getClass1().lastIndexOf("/") + 1);
				String eYear = feature.getClass2().substring(feature.getClass2().lastIndexOf("/") + 1);
				if (!sYear.isEmpty() && !eYear.isEmpty()) {
					HashMap<String, Number> map = lccfMatrix.get(sYear);
					//take the saved value in the matrix and add the new one
					Number savedValue = (Number) map.get(eYear);
					Geometry geom = (Geometry) feature.getGeom();
					Number newValue = geom.getArea();
					float areaValue = savedValue.floatValue() + newValue.floatValue();
					map.put(eYear, areaValue);
					lccfMatrix.put(sYear, map);
				}
			}
		
		
		
		//write the lccf matrix into a new csv file
		CSVWriter writer;
		String matrixName = mapName + "_matrix";
		System.out.println("matrixname: "+matrixName);
		try {
			writer = new CSVWriter(new FileWriter("/var/www/lifeimagine.graphitech-projects.com/data/" + matrixName + ".csv"), ';');
			List<String> xClasses = new ArrayList<String>(lccfMatrix.keySet());
			List<String> yClasses = new ArrayList<String>(lccfMatrix.keySet());
			Collections.sort(xClasses);
			Collections.sort(yClasses);
			//create and write csv first line with the land use classes
			xClasses.add(0, "\\");
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
		List<SimpleFeature> features = new ArrayList<SimpleFeature>(landFeature.length);
		//Iterator<SerialSimpleFeature> iter = landFeature.iterator();
		
		
		System.out.println("Creo la nuova geometria");
		int projCode = 3044;
		try {
			//SimpleFeatureType schema = landFeature.getSchema();
			//CoordinateReferenceSystem featureCRS = landFeature.getSchema().getCoordinateReferenceSystem();
			//int projCode = CRS.lookupEpsgCode(featureCRS, true);
			
			//create simple type for the collection
			SimpleFeatureType TYPE = DataUtilities.createType(mapName,
					"the_geom:Polygon:srid=" + projCode + "," +
					"startYear:String," +
					"endYear:String," +
					"LCF:int"
	        );
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
			try {
				for(SerialSimpleFeature feature : landFeature){
							String sYear = feature.getClass1().substring(feature.getClass1().lastIndexOf("/") + 1);
					String eYear = feature.getClass2().substring(feature.getClass2().lastIndexOf("/") + 1);
					
					//build a feature of the collection on at a time
					featureBuilder.add((Geometry) feature.getGeom());
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
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("CREATE gml named: "+mapName);
		createGML(mapName,features,projCode);
		return mapName;
		/*
		SimpleFeatureCollection sfc = (SimpleFeatureCollection) DataUtilities.collection(features);
		
		//hashmap containing the input of every subprocess
		HashMap<String, Object> input = new HashMap<String, Object>();
		
		input.clear();
		input.put("features", sfc);
		input.put("workspace", "Life");
		input.put("store", "Imagine");
		input.put("name", mapName);
		input.put("styleName", "LCCF_RT");
		
		System.out.println("upload del risultato su geoserver");
		ProcessUtil.executeProcess("gs", "Import", input, "result");
		System.out.println("processo eseguito");
		
		return mapName;
		*/
	}
	
	
	private void createGML(String name,List<SimpleFeature> features, int projCode){
		
		  try
		     {
		          FileOutputStream prova = new FileOutputStream("/var/www/lifeimagine.graphitech-projects.com/data/"+name+".gml");
		          PrintStream write = new PrintStream(prova);
		        
		                createGLMpart1(write,
		                		"S3_Land_Cover_changes_Map_GML",
		                		"LS3_Land_Cover_changes_Map",
		                		"LS3_Land_Cover_changes_Map",
		                		"LS3_Land_Cover_changes_Map_Nomenclature",
		                		"LifeImagine",
		                		"http://lifeimagine.graphitech-projects.com/data/legenda.ods",
		                		"LIFE_IMAGINE_Deliverable_A2_rev_2_1",
		                		"LIFE_IMAGINE_Deliverable_A2_rev_2_1",
		                		"2012-05-25T00:00:00.000",
		                		"publication",
		                		"http://www.life-imagine.eu/collaboration/attachments/download/1050/LIFE_IMAGINE_Deliverable%20%20A2_rev_2_1.pdf"
		                		);
		                createGLMpart2(write, features,  projCode,"S3_Land_Cover_changes_Map_GML");
		                write.println(createGLMpart3());
		          
		                write.close();
		     }
		      catch (IOException e)
		      {
		          System.out.println("Errore: " + e);
		          System.exit(1);
		      }
		
	}
	
	private void createGLMpart1(PrintStream write, 
			String insert_GML_ID,String insert_LCD_ID, String insert_dataset_name, 
			String insertLocalID, String insertNameSpaceValue, String insert_codelist_URL,
			String insert_DC_ID, String insert_document_name, String insertDateTime,
			String insert_document_date_type, String insert_document_link){		
		
		
		write.println("<?xml version=\"1.0\"?>");
		write.println("<gml:FeatureCollection gml:id=\""+insert_GML_ID+"\"" );
		write.println("xmlns:base=\"http://inspire.ec.europa.eu/schemas/base/3.3\"");
		write.println("xmlns:base2=\"http://inspire.ec.europa.eu/schemas/base2/2.0\"");
		write.println("xmlns:xlink=\"http://www.w3.org/1999/xlink\"");
		write.println("xmlns:lcv=\"http://inspire.ec.europa.eu/schemas/lcv/4.0\"");
		write.println("xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gco=\"http://www.isotc211.org/2005/gco\"");
		write.println("xmlns:gml=\"http://www.opengis.net/gml/3.2\"");
		write.println("xmlns:lcn=\"http://inspire.ec.europa.eu/schemas/lcn/4.0\"");
		write.println("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		write.println("xsi:schemaLocation=\"http://inspire.ec.europa.eu/schemas/lcv/4.0 http://inspire.ec.europa.eu/schemas/lcv/4.0/LandCoverVector.xsd\">");
		write.println("<gml:featureMember>");
		write.println("<lcv:LandCoverDataset gml:id=\""+insert_LCD_ID+"\">");
		write.println("<lcv:inspireId>");
		write.println("<base:Identifier>");
		write.println("<base:localId>\""+insert_LCD_ID+"\"</base:localId>");
		write.println("<base:namespace/>");
		write.println("</base:Identifier>");
		write.println("</lcv:inspireId>");
		write.println("<lcv:beginLifespanVersion xsi:nil=\"true\" nilReason=\"unknown\"/>");
		write.println("<lcv:extent/>");
		write.println("<lcv:name>\""+insert_dataset_name+"\"</lcv:name>");
		write.println("<lcv:nomenclatureDocumentation>");
		write.println("<lcn:LandCoverNomenclature>");
		write.println("<lcn:inspireId>");
		write.println("<base:Identifier>");
		write.println("<base:localId>\""+insertLocalID+"\"</base:localId>");
		write.println("<base:namespace>\""+insertNameSpaceValue+"\"</base:namespace>");
		write.println("</base:Identifier>");
		write.println("</lcn:inspireId>");
		write.println("<lcn:nomenclatureCodeList>"+insert_codelist_URL+"</lcn:nomenclatureCodeList>");
		write.println("<lcn:externalDescription>");
		write.println(	"<base2:DocumentCitation gml:id=\""+insert_DC_ID+"\">");
		write.println(	"<base2:name>\""+insert_document_name+"\"</base2:name>");
		write.println(	"<base2:date>");
		write.println(	"<gmd:CI_Date>");
		write.println(	"<gmd:date>");
		//write.println(	"<gco:DateTime>2012-05-25T00:00:00.000</gco:DateTime>");
		write.println(	"<gco:DateTime>"+insertDateTime+"</gco:DateTime>");
		write.println(	"</gmd:date>");
		write.println(	"<gmd:dateType>");
		write.println(	"<gmd:CI_DateTypeCode");
		write.println(	"codeListValue=\""+insert_document_date_type+"\"");
		write.println(	"codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml\"");
		write.println(	"/>");
		write.println(	"</gmd:dateType>");
		write.println(	"</gmd:CI_Date>");
		write.println(	"</base2:date>");
		write.println(	"<base2:link>"+insert_document_link+"</base2:link>");
		write.println(	"</base2:DocumentCitation>");
		write.println("</lcn:externalDescription>");
		write.println("<lcn:responsibleParty>");
		write.println(	"<base2:RelatedParty/>");
		write.println("</lcn:responsibleParty>");
		write.println("</lcn:LandCoverNomenclature>");
		write.println("</lcv:nomenclatureDocumentation>");
		write.println("<lcv:validFrom xsi:nil=\"true\" nilReason=\"unknown\"/>");
		write.println("<lcv:validTo xsi:nil=\"true\" nilReason=\"unknown\"/>");
		
		
	}
	
	private void createGLMpart2(PrintStream write, List<SimpleFeature> features, int projCode,String insert_GML_ID){		
		
		
		
		for(SimpleFeature f : features){
		
			String featureLine;
			String geom = "";
			String LCU_ID = f.getIdentifier().getID();
			String pol_ID = f.getIdentifier().getID();
			Integer className = (Integer)f.getAttribute("LCF");
			Geometry g = (Geometry)f.getDefaultGeometry();
			
			for(Coordinate c : g.getCoordinates()){
				String tempGeom = c.x+" "+c.y+" ";
				geom = geom.concat(tempGeom);
			}
			
		featureLine = 
				"<lcv:member>"+
		"<lcv:LandCoverUnit gml:id=\""+"lcuID_"+LCU_ID+"\">"+

		"<lcv:inspireId>"+
			"<base:Identifier>"+
				"<base:localId>"+LCU_ID+"</base:localId>"+
				"<base:namespace/>"+
			"</base:Identifier>"+
		"</lcv:inspireId>"+
		"<lcv:beginLifespanVersion xsi:nil=\"true\" nilReason=\"unknown\"/>"+
		"<lcv:geometry>"+
			"<gml:Polygon gml:id=\""+pol_ID+"\" srsDimension=\"2\""+
				" srsName=\"urn:ogc:def:crs:EPSG::"+ projCode  +"\">"+
				"<gml:exterior>"+
					"<gml:LinearRing>"+
						"<gml:posList>"+
					geom +
						/* "43.428972933511879 3.873842887588851"+
							"45.642542638874382 6.096520303724765 45.701049780907894"+
							"9.88136811121222 38.807799684570824 13.270228493768991"+
							"35.033841531268969 10.959641869298464 36.112115289355216"+
							"7.878859703337763 43.428972933511879"+
							"3.873842887588851"+*/
							"</gml:posList>"+
					"</gml:LinearRing>"+
				"</gml:exterior>"+
			"</gml:Polygon>"+
		"</lcv:geometry>"+
		"<lcv:landCoverObservation>"+
			"<lcv:LandCoverObservation>"+
				"<lcv:class"+
					" xlink:href=\""+"http://inspire.ec.europa.eu/codelist/LandCoverClassValue/lcf"+className+"\""+
					" xlink:title=\""+className+"\"/>"+
				"<lcv:mosaic xsi:nil=\"true\" nilReason=\"unknown\"/>"+
				"<lcv:observationDate  xsi:nil=\"true\" nilReason=\"unknown\"/>"+
			"</lcv:LandCoverObservation>"+
		"</lcv:landCoverObservation>"+
	"</lcv:LandCoverUnit>"+
"</lcv:member>";
//"<!--Duplicate the \"lcv:member\" element for each polygon-->";
		
		write.println(featureLine);
		
		}
				
	}
	
	
private String createGLMpart3(){		
		
		String part3 = 	"</lcv:LandCoverDataset>"+
		"</gml:featureMember>"+
		"</gml:FeatureCollection>";
	
				return part3;
	}
}