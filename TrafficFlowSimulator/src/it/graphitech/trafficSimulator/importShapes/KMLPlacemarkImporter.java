package it.graphitech.trafficSimulator.importShapes;


import gov.nasa.worldwind.ogc.kml.KMLAbstractContainer;
import gov.nasa.worldwind.ogc.kml.KMLAbstractFeature;
import gov.nasa.worldwind.ogc.kml.KMLConstants;
import gov.nasa.worldwind.ogc.kml.KMLPlacemark;
import gov.nasa.worldwind.ogc.kml.KMLRoot;



import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for importing the placemarks in a KML file
 * 
 */
public class KMLPlacemarkImporter {
	private List<KMLPlacemark> placemarks;
	private String KMLFilePath;
	
	public KMLPlacemarkImporter() {
		placemarks = new ArrayList<KMLPlacemark>();
	}
	
	/**
	 * 
	 * @param filePath the file in KML format
	 */
	public void setKMLFilePath(String filePath) {
		this.KMLFilePath = filePath;
	}
	
	/**
	 * Parses the file and fill the placemarks list with placemarks
	 */
    public void parseKMLFile()
    {
    	placemarks.clear();
    	
        try
        {
        	URL url = new URL(this.KMLFilePath);
        	KMLRoot root = new KMLRoot(url,KMLConstants.KML_MIME_TYPE);
        	 
        	 root.parse();
            
            parseRecursive(root.getFeature());           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @return the placemarks list
     */
    public List<KMLPlacemark> getPlacemarks() {
    	return placemarks;
    }
    
    /**
     * Recursive procedure for parsing the KML data
     * @param feature
     */
    private void parseRecursive(KMLAbstractFeature feature) {
    	if (feature instanceof KMLAbstractContainer) {
    		KMLAbstractContainer container = (KMLAbstractContainer) feature;
            for (KMLAbstractFeature f : container.getFeatures()) {
            	parseRecursive(f);
            }
        } else if (feature instanceof KMLPlacemark) {
        	//System.out.println("is placemark");
        	placemarks.add((KMLPlacemark) feature);
        }
    }
}
