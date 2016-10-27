package it.graphitech.worldwind;


import gov.nasa.worldwind.Factory;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.TiledImageLayer;
import gov.nasa.worldwind.layers.Earth.OSMMapnikLayer;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import gov.nasa.worldwindx.examples.WMSLayersPanel;

import it.graphitech.Variables;
import it.graphitech.modules.InteractionModule;
import it.graphitech.modules.Render;
import it.graphitech.swing.ButtonPanel;
import it.graphitech.swing.ForcePanel;
import it.graphitech.swing.RenderOptionPanel;
import it.graphitech.swing.VariablesPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

//the main class of the project
public class FlowMapsInWorldWind_v3 extends ApplicationTemplate {

	static ButtonPanel buttonPanel;
	static VariablesPanel variablesPanel;	
	static ForcePanel forcePanel;
	static RenderOptionPanel renderOptionPanel;

	 protected static final String WHITE = "texture/white.png";

	public static class AppFrame extends ApplicationTemplate.AppFrame {

		private static final long serialVersionUID = 4070261473999416852L;

		

		// public EmitterSettings emitterSettings;

		public AppFrame() {
			super(true, false, false);

		
			
			javax.swing.Box box = javax.swing.Box.createVerticalBox();
			//box.add(this.trafficSimPanel);
			//this.getLayerPanel().add(box, BorderLayout.SOUTH);

		
			
			forcePanel = new ForcePanel();
			buttonPanel = new ButtonPanel();
			variablesPanel = new VariablesPanel();
			renderOptionPanel = new RenderOptionPanel();
			
			InteractionModule interactionModule = new InteractionModule(this.getWwd());

			
			JPanel panel_button_variables = new JPanel(new BorderLayout(4, 4));
			panel_button_variables.add(buttonPanel,BorderLayout.NORTH);
			panel_button_variables.add(forcePanel,BorderLayout.CENTER);
			
			
			JPanel p1 = new JPanel(new BorderLayout(3, 3));
			p1.add(variablesPanel,BorderLayout.CENTER);
			p1.add(renderOptionPanel,BorderLayout.SOUTH);
			panel_button_variables.add(p1, BorderLayout.SOUTH);
			
			JPanel gui = null;
			gui = new JPanel(new BorderLayout(3, 3));
			//gui.add(buttonPanel, BorderLayout.NORTH);
			gui.add(panel_button_variables, BorderLayout.NORTH);
			//gui.add(null, BorderLayout.CENTER);
			
			
			this.getContentPane().add(gui, BorderLayout.NORTH);
			
			
		//	this.getWwd().getSceneController().setVerticalExaggeration(0); 
			
			Render.wwd=this.getWwd();
			Render.createLayers();

			
			FlatGlobeOption flatOption = new FlatGlobeOption(this.getWwd());
			
			flatOption.enableFlatGlobe(true);
			
			this.getWwd().getInputHandler().addKeyListener(flatOption);
			
			
	
			
			  SurfaceImage si1 = new SurfaceImage(WHITE, new ArrayList<LatLon>(Arrays.asList(
	                    LatLon.fromDegrees(-90d, 180d),
	                    LatLon.fromDegrees(-90d, -180d),
	                    LatLon.fromDegrees(90d, -180d),
	                    LatLon.fromDegrees(90d, 180d)
	                )));
			
			  RenderableLayer imagelayer = new RenderableLayer();
			  imagelayer.setName("Surface Images");
			  imagelayer.setPickEnabled(false);
			  imagelayer.addRenderable(si1);
			  imagelayer.setEnabled(true);

              insertBeforeCompass(this.getWwd(), imagelayer);
			
if(this.getLayerPanel()!=null){
			this.getLayerPanel().update(this.getWwd());
}



/*
WMSTiledImageLayer layer = getWMSLayer("http://ogc.bgs.ac.uk/cgi-bin/topography/wms", "TOPOGRAPHY");
this.getWwd().getModel().getLayers().add(layer);
*/
/*
WMSTiledImageLayer layer2 = getWMSLayer("http://demo.cubewerx.com/demo/cubeserv/cubeserv.cgi", "Foundation.polbndl_1m");
this.getWwd().getModel().getLayers().add(layer2);
*/


/*
WMSTiledImageLayer layer3 = getWMSLayer("http://services.nationalmap.gov/ArcGIS/services/TNM_Blank_US/MapServer/WMSServer", "2");
this.getWwd().getModel().getLayers().add(layer3);

WMSTiledImageLayer layer4 = getWMSLayer("http://basemap.nationalmap.gov/ArcGIS/services/USGSTopo/MapServer/WMSServer", "121");
this.getWwd().getModel().getLayers().add(layer4);
*/
//Layer waterColorLayer = new it.graphitech.layer.OSMMapnikLayer();
Layer waterColorLayer2 = new it.graphitech.layer.OSMCycleMapLayer();

//((TiledImageLayer)waterColorLayer2).setDetailHint(2.0d);

if(Variables.waterColor==true){
this.getWwd().getModel().getLayers().add(waterColorLayer2);

//WMSTiledImageLayer layer6 = getWMSLayer("http://www.megx.net/wms/gms", "satellite_mod");
//this.getWwd().getModel().getLayers().add(layer6);


WMSTiledImageLayer layer2 = getWMSLayer("http://www.opengis.uab.cat/cgi-bin/world/MiraMon.cgi", "admin_level1-world");
this.getWwd().getModel().getLayers().add(layer2);
((TiledImageLayer) layer2).setDetailHint(0.6);
}

//WMSTiledImageLayer layer2 = getWMSLayer("http://suite.opengeo.org/geoserver/wms", "usa:states");
//this.getWwd().getModel().getLayers().add(layer2);
//((TiledImageLayer) layer2).setDetailHint(0.6);



//DISABLE UNUSEFUL LAYERS
this.getWwd().getModel().getLayers().getLayerByName("World Map").setEnabled(false);
this.getWwd().getModel().getLayers().getLayerByName("Compass").setEnabled(false);
this.getWwd().getModel().getLayers().getLayerByName("NASA Blue Marble Image").setEnabled(false);
//this.getWwd().getModel().getLayers().getLayerByName("Blue Marble (WMS) 2004").setEnabled(true);
this.getWwd().getModel().getLayers().getLayerByName("i-cubed Landsat").setEnabled(false);
this.getWwd().getModel().getLayers().getLayerByName("Place Names").setEnabled(false);
this.getWwd().getModel().getLayers().getLayerByName("RejectArea").setEnabled(false);
this.getWwd().getModel().getLayers().getLayerByName("IntermediateNodes").setEnabled(false);
this.getWwd().getModel().getLayers().getLayerByName("Scale bar").setEnabled(false);
this.getWwd().getModel().getLayers().getLayerByName("View Controls").setEnabled(false);
this.getWwd().getModel().getLayers().getLayerByName("intermediateNodesAirspaces").setEnabled(false);
this.getWwd().getModel().getLayers().getLayerByName("Curves").setEnabled(false);

for(Layer l: this.getWwd().getModel().getLayers()){
	System.out.println(l.getName()+" - "+l.isEnabled());
}

//this.getWwd().getModel().getLayers().getLayerByName("Political Boundaries").setEnabled(true);
		}

		private WMSTiledImageLayer getWMSLayer(String wmsURL, String layerName) {
			  			URI uri = WWIO.makeURI(wmsURL);
			              
			              WMSCapabilities caps = null;
			  
			              try
			              {
			                  caps = WMSCapabilities.retrieve(uri);
			                  //System.out.println(caps.toString());
			                  caps.parse();
			              }
			              catch (Exception e)
			              {
			                  e.printStackTrace();
			              
			             }    
			             
			             WMSLayerCapabilities wmsLayerCapabilities = caps.getLayerByName(layerName);
			             
			             final List<WMSLayerCapabilities> namedLayerCaps = caps.getNamedLayers();
			             if (namedLayerCaps == null) {
			             	
			             	System.out.println("not existent layers in capabilities");
			             
			             }
			                 
			             
			             if(wmsLayerCapabilities==null) {
			             	
			             	System.out.println("wms layer "+layerName+" is not supported.");
			             	System.out.println("Supported layers are:");
			             	try
			                 {
			                     for (WMSLayerCapabilities lc : namedLayerCaps)
			                     {
			                     	System.out.println("> "+lc.getName());
			                     }
			                 }
			                 catch (Exception e)
			                 {
			                     e.printStackTrace();
			                     
			                 }
			                 //return;
			             }
			 
			             AVList params = new AVListImpl();
			             params.setValue(AVKey.LAYER_NAMES, layerName);
			             params.setValue(AVKey.URL_CONNECT_TIMEOUT, 30000);
			             params.setValue(AVKey.URL_READ_TIMEOUT, 30000);
			             params.setValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT, 60000);
			             //params.setValue(AVKey.OPACITY, 0.5);
			             params.setValue(AVKey.OPACITY, 1.0);
			             //params.setValue(AVKey.STYLE_NAMES,"color");
			     		
			             
			             WMSTiledImageLayer wmsLayer = new WMSTiledImageLayer(caps, params);
			             if(layerName!=null)
			             	wmsLayer.setName(layerName);
			 			return wmsLayer;
			 		}
			     }

        
    

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		ApplicationTemplate.start("Force Directed Flow Maps", AppFrame.class);
	}

}
