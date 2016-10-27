package it.graphitech.lifeimagine.wps.parser;


import org.geotools.data.simple.SimpleFeatureCollection;













import it.graphitech.lifeimagine.wps.otherClasses.SerialSimpleFeature;

import java.awt.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class SC3mngt {

	
	public static void createIntermediateFile(String landURL){
	
	WFSParser landParser = new WFSParser(new Type("lcv:LandCoverUnit", Type.Reg.RT), null, 3044, Type.Geom.POLYGON);
	//	WFSParser landParser = new WFSParser(new Type("lcv:LandCoverUnit", Type.Reg.RT), null, 4326, Type.Geom.POLYGON);
	SerialSimpleFeature[] landFeature = landParser.parseWFSLocal(landURL);
	
	FileOutputStream fileOutputStream = null;
	ObjectOutputStream objectOutputStream = null;
	try {
		
		int len = landURL.split("/").length;
		String file_no_ext = landURL.split("/")[len - 1];
		System.out.println("file_no_ext: "+file_no_ext);
		file_no_ext=file_no_ext.replace(".", "_");
		file_no_ext=file_no_ext.replace(":", "_");
		System.out.println("file_no_ext: "+file_no_ext);

		file_no_ext=file_no_ext.replace("?", "_");
		file_no_ext=file_no_ext.replace("&", "_");
		file_no_ext=file_no_ext.replace("=", "_");
		
		
	fileOutputStream = new FileOutputStream("genData/"+file_no_ext+".txt");
	objectOutputStream = new ObjectOutputStream(fileOutputStream);
	objectOutputStream.writeObject(landFeature);
	objectOutputStream.close();
	fileOutputStream.close();
	System.out.println("Oggetto correttamente salvato su file.");
	} catch (IOException ex) {
	ex.printStackTrace();
	}
	
	}
	
	
	
	public static SerialSimpleFeature[] retrieveObjects(String landURL){
		
		int len = landURL.split("/").length;
		String file_no_ext = landURL.split("/")[len - 1];
		file_no_ext=file_no_ext.replace(".", "_");
		file_no_ext=file_no_ext.replace(":", "_");
		file_no_ext=file_no_ext.replace("?", "_");
		file_no_ext=file_no_ext.replace("&", "_");
		file_no_ext=file_no_ext.replace("=", "_");
		
		String fileName = "/opt/apache-tomcat-7.0.67/geoserver_data/life-imagine/cache/"+file_no_ext+".txt";
		
		System.out.println("Utilizzo il file: "+fileName);
		
		SerialSimpleFeature[] serialSimpleFeature = null;
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		
		try {
		fileInputStream = new FileInputStream(fileName);
		objectInputStream = new ObjectInputStream(fileInputStream);
		serialSimpleFeature = (SerialSimpleFeature[]) objectInputStream.readObject();
		objectInputStream.close();
		fileInputStream.close();
		} catch (IOException ex) {
		ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
		ex.printStackTrace();
		}
		
		return serialSimpleFeature;
	}
	
	
	public static void main(String[] args) {
		
		String s = "http://lifeimagine.graphitech-projects.com/deegree/services/lc_sc?service=WFS&request=GetFeature&version=2.0.0&typeNames=lcv:LandCoverUnit";
		
		
		createIntermediateFile(s);
	}
}
