package it.graphitech.lifeimagine.wps.parser;

import it.graphitech.lifeimagine.wps.otherClasses.SerialSimpleFeature;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class WFSParser {
	
	private static Type.Geom geometryType;
	
	private static int projection;
	
	private static int tempIndex;
	
	private static Object[] tempFeature;
	
	private static SimpleFeature[] featureList;
	private static SerialSimpleFeature[] serialFeatureList;
	
	private static Type featureType;
	
	private static Type referenceType;
	
	public WFSParser(Type mainType, Type optionalType, int proj, Type.Geom geom) {
		featureType = mainType;
		geometryType = geom;
		referenceType = optionalType;
		projection = proj;
		tempFeature = new Object[featureType.getAttributes().size()];
		tempIndex = 0;
	}
	
	public SimpleFeatureCollection parseWFS(String url) {
		Document doc = getDocument(url);
		SimpleFeatureType type = buildType(geometryType);
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
		if (doc.getFirstChild().getNodeName().equals("wfs:FeatureCollection")) {
			if(featureType.getFeature().equals("pd:StatisticalDistribution")) {
				NodeList members = doc.getElementsByTagName("pd:StatisticalValue");
				if (members.getLength() > 0) {
					featureList = new SimpleFeature[members.getLength()];
					for (int i = 0; i < members.getLength(); i++) {
						parseFeature(featureType, (Element) members.item(i));
						featureBuilder.addAll(tempFeature);
						featureList[i] = featureBuilder.buildFeature(null);
						tempIndex = 0;
					}
				}
			}
			else {
				NodeList members = doc.getElementsByTagName("wfs:member");
				if (members.getLength() > 0) {
					featureList = new SimpleFeature[members.getLength()];
					for (int i = 0; i < members.getLength(); i++) {
						parseFeature(featureType, getFirstElement(members.item(i)));
						featureBuilder.addAll(tempFeature);
						featureList[i] = featureBuilder.buildFeature(null);
						tempIndex = 0;
					}
				}
			}
		}
		return DataUtilities.collection(featureList);
	}
	
	public SerialSimpleFeature[] parseWFSLocal(String url) {
		Document doc = getDocumentLocal(url);
		SimpleFeatureType type = buildType(geometryType);
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
		
		System.out.println("start parsing");
		
		if (doc.getFirstChild().getNodeName().equals("wfs:FeatureCollection")) {
			if(featureType.getFeature().equals("pd:StatisticalDistribution")) {
				NodeList members = doc.getElementsByTagName("pd:StatisticalValue");
				if (members.getLength() > 0) {
					serialFeatureList = new SerialSimpleFeature[members.getLength()];
					for (int i = 0; i < members.getLength(); i++) {
						parseFeature(featureType, (Element) members.item(i));
						featureBuilder.addAll(tempFeature);
						serialFeatureList[i] = (SerialSimpleFeature)featureBuilder.buildFeature(null);
						tempIndex = 0;
					}
				}
			}
			else {
				NodeList members = doc.getElementsByTagName("wfs:member");
				if (members.getLength() > 0) {
					serialFeatureList = new SerialSimpleFeature[members.getLength()];
					for (int i = 0; i < members.getLength(); i++) {
						parseFeature(featureType, getFirstElement(members.item(i)));
						featureBuilder.addAll(tempFeature);
						SimpleFeature tempFeature = featureBuilder.buildFeature(null);;
						serialFeatureList[i] = SerializeFeature(tempFeature);
						tempIndex = 0;
					}
				}
			}
		}
		
		System.out.println("parsing ended");
		
		return serialFeatureList;
	}
	
	
	
	private SerialSimpleFeature SerializeFeature(SimpleFeature simpleFeature){
		
		String attr1 = simpleFeature.getAttribute("class1").toString();
		String attr2 = simpleFeature.getAttribute("class2").toString();
		Object geom = simpleFeature.getDefaultGeometry();
		
		SerialSimpleFeature s = new SerialSimpleFeature(attr1, attr2, geom);
		return s;
	}
	
	
	private static Document getDocument(String url) {
		InputStream inputStream;
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		try {
			inputStream = cacheData(url);
			//inputStream = new URL(url).openStream();
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setIgnoringComments(true);
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(inputStream);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}
	
	private static Document getDocumentLocal(String url) {
		InputStream inputStream;
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		try {
			inputStream = cacheDataLocal(url);
			
			System.out.println("GML retrieved");
			//inputStream = new URL(url).openStream();
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setIgnoringComments(true);
			documentBuilder = documentBuilderFactory.newDocumentBuilder();			
			
			document = documentBuilder.parse(inputStream);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}
	
	private static InputStream cacheData(String url) {
		String localURL = "/opt/apache-tomcat-7.0.67/geoserver_data/life-imagine/cache/";
		int len = url.split("/").length;
		String file = url.split("/")[len - 1] + ".gml";
		try {
			File data = new File(localURL + file);
			if (data.exists()) {
				System.out.println("the cached file exists");
				return new FileInputStream(localURL + file);
			}
			else {
				System.out.println("the cached file has to be created");
				FileUtils.copyURLToFile(new URL(url), data);
				return new URL(url).openStream(); 
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static InputStream cacheDataLocal(String url) {
		String localURL = "genData/";
		int len = url.split("/").length;
		String file_no_ext = url.split("/")[len - 1];
		System.out.println("file_no_ext: "+file_no_ext);
		file_no_ext=file_no_ext.replace(".", "_");
		file_no_ext=file_no_ext.replace(":", "_");
		System.out.println("file_no_ext: "+file_no_ext);

		file_no_ext=file_no_ext.replace("?", "_");
		file_no_ext=file_no_ext.replace("&", "_");
		file_no_ext=file_no_ext.replace("=", "_");
		String file = file_no_ext + ".gml";
		try {
			File data = new File(localURL + file);
			if (data.exists()) {
				System.out.println("the cached file exists");
				return new FileInputStream(localURL + file);
			}
			else {
				System.out.println("the cached file has to be created");
				System.out.println("data directory: "+data);
				FileUtils.copyURLToFile(new URL(url), data);
				return new URL(url).openStream(); 
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	private static Element getFirstElement(Node node) {
		if (node.hasAttributes()) {
			return (Element) node;
		}
		else {
			if (node.hasChildNodes()) {
				return getFirstElement(node.getFirstChild());
			}
			else {
				return getFirstElement(node.getNextSibling());
			}
		}
	}
	
	private static void parseFeature(Type type, Element element) {
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			if (element.getChildNodes().item(i).getNodeName() == type.getGeometryTag()) {
				parseGeometry(element.getChildNodes().item(i), geometryType);
			}
			else {
				parseTag(element.getChildNodes().item(i));
			}
		}
	}
	
	private static void parseTag(Node node) {
		if (node.hasChildNodes()) {
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				parseTag(node.getChildNodes().item(i));
			}
		}
		else if (node.getNodeValue() != null) {
			if (!node.getNodeValue().trim().equals("")) {
				storeValue(node.getParentNode().getNodeName().split(":")[1], node.getNodeValue(), tempIndex);
				//System.out.println(node.getParentNode().getNodeName().split(":")[1] + " - " + node.getNodeValue());
			}
		}
		else if (node.hasAttributes()) {
			Element elem = (Element) node;
			if (!elem.hasAttribute("xsi:nil")) {
				if (elem.hasAttribute("xlink:href")) {
					if (elem.getAttribute("xlink:href").indexOf("http://lifeimagine.graphitech-projects.com/deegree/services/") == 0) {
						try {
							parseReference(elem.getAttribute("xlink:href"));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					else {
						storeValue(elem.getNodeName().split(":")[1], elem.getAttribute("xlink:href"), tempIndex);
						//System.out.println(elem.getNodeName().split(":")[1] + " - " + elem.getAttribute("xlink:href"));
					}
				}
			}
		}
	}
	
	private static void parseGeometry(Node node, Type.Geom geom) {
		Element elem = getFirstElement(node);
		Geometry geometry = null;
		try {
			GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), projection);
			NodeList rings = elem.getChildNodes();
			switch (geom) {
				case POINT:
					Coordinate coordinate = null;
					for (int i = 0; i < rings.getLength(); i++) {
						Node ring = rings.item(i);
						if (!ring.getNodeName().equals("#text")) {
							String content = ring.getTextContent();
							content = content.trim();
							String[] positions = content.split(" ");
							coordinate = new Coordinate(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]));
						}
					}
					geometry = (Geometry) geomFactory.createPoint(coordinate);
					break;
				case LINESTRING:
					throw new Exception("Parser handles only polygons and points at the moment");
				case POLYGON:
					LinearRing externalRing = null;
					List<LinearRing> internalRings = new ArrayList<LinearRing>(rings.getLength());
					for (int i = 0; i < rings.getLength(); i++) {
						Node ring = rings.item(i);
						if (!ring.getNodeName().equals("#text")) {
							String content = ring.getTextContent();
							content = content.trim();
							String[] positions = content.split(" ");
							int k = 0;
							Coordinate[] coordinates = new Coordinate[positions.length / 2];
							for (int j = 0; j < positions.length - 1; j = j + 2) {
								coordinates[k++] = new Coordinate(Double.parseDouble(positions[j]), Double.parseDouble(positions[j + 1]));
							}
							if (ring.getNodeName().split(":")[1].equals("exterior")) {
								externalRing = geomFactory.createLinearRing(coordinates);
							}
							else if (ring.getNodeName().split(":")[1].equals("interior")) {
								internalRings.add(geomFactory.createLinearRing(coordinates));
							}
						}
					}
					LinearRing[] intRings = new LinearRing[internalRings.size()];
					intRings = internalRings.toArray(intRings);
					geometry = (Geometry) geomFactory.createPolygon(externalRing, intRings);
					break;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		storeValue(node.getNodeName().split(":")[1], geometry, tempIndex);
		//System.out.println(node.getNodeName().split(":")[1]);
	}
	
	private static void parseReference(String url) {
		Document doc = getDocument(url);
		Element feature = getFirstElement(doc);
		parseFeature(referenceType, feature);
	}
	
	private static void storeValue(String nodeName, Object nodeValue, int index) {
		if (featureType.attributesContains(nodeName, tempIndex) && tempIndex == index) {
			tempFeature[tempIndex++] = nodeValue;
		}
		else {
			int i = nextIndex(nodeName);
			if (i != -1) {
				if (i < tempIndex) {
					tempFeature[i] = nodeValue;
				}
				else if (i > tempIndex) {
					tempFeature[tempIndex++] = null;
					storeValue(nodeName, nodeValue, i);
				}
			}
		}
	}
	
	private static int nextIndex(String nodeName) {
		int index = -1;
		for (String attribute : featureType.getAttributes()) {
			if (attribute.indexOf(nodeName) != -1) {
				int i = featureType.getAttributes().indexOf(attribute);
				if (i < tempFeature.length)
					if (tempFeature[i] == null)
						index = i;
				else
					index = i;
			}
		}
		return index;
	}
	
	private static SimpleFeatureType buildType(Type.Geom geom) {
		try {
			SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
			typeBuilder.setName(featureType.getFeature());
			for (String attr : featureType.getAttributes()) {
				if (attr.contains("geometry") && !attr.contains("geometry2")) {
					System.out.println("projection: "+projection);
					typeBuilder.setCRS(CRS.decode("EPSG:" + projection));
					switch (geom) {
						case POINT:
							typeBuilder.add(attr, Point.class);
							break;
						case LINESTRING:
							typeBuilder.add(attr, LineString.class);
							break;
						case POLYGON:
							typeBuilder.add(attr, Polygon.class);
							break;
					}
				}
				else {
					typeBuilder.add(attr, String.class);
				}
			}
			return typeBuilder.buildFeatureType();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}