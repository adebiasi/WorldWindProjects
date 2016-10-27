package it.graphitech.smeSpire.layers;

import it.graphitech.smeSpire.shapefile.ShapefileLoader;

import java.net.URL;

import java.util.List;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;

import gov.nasa.worldwind.util.WWIO;





public class ShapeLayer{
	
	private WorldWindow wwd;
	
	public ShapeLayer(WorldWindow wwd){
		
		this.wwd = wwd;
		
		String SERVER = "http://95.110.208.97/smespire/applet/shp/eu27.shp";
        
        ShapefileLoader loader = new ShapefileLoader();
        loader.numPolygonsPerLayer = 0;
 
    	URL url = WWIO.makeURL(SERVER);
    	List<Layer> layers = loader.createLayersFromSource(url);
			
    	
    	for (int i = 1; i < layers.size(); i++)
        {
            layers.get(i).setPickEnabled(false);
            layers.get(i).setEnabled(false);
            insertBeforeCompass(this.wwd, layers.get(i));
        }
}

	public static void insertBeforeCompass(WorldWindow wwd, Layer layer)
	{
	    // Insert the layer into the layer list just before the compass.
	    int compassPosition = 0;
	    LayerList layers = wwd.getModel().getLayers();
	    for (Layer l : layers)
	    {
	        if (l instanceof CompassLayer)
	            compassPosition = layers.indexOf(l);
	    }
	    layers.add(compassPosition, layer);
	}
	
	public WorldWindow getWwd() {
		return wwd;
	}
	
	public void setWwd(WorldWindow wwd) {
		this.wwd = wwd;
	}

}
