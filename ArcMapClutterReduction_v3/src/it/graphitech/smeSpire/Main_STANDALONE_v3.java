/*
Copyright (C) 2001, 2011 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package it.graphitech.smeSpire;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.*;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwindx.examples.LayerPanel;
import gov.nasa.worldwindx.examples.util.*;
import it.graphitech.ColorsList;
import it.graphitech.GeneratorOfRenderableObjects;
import it.graphitech.core.MyBasicSceneController;
import it.graphitech.smeSpire.entry.Entry;
import it.graphitech.smeSpire.framebuffer.MyBasicOrbitView;
import it.graphitech.smeSpire.framebuffer.MyRectangularTessellator;
import it.graphitech.smeSpire.layers.GlobeAnnotation;
import it.graphitech.smeSpire.layers.MyRenderableLayer;
import it.graphitech.smeSpire.layers.Earth.BMNGWMSLayer;
import it.graphitech.smeSpire.statistics.StatisticsPanel;











import javax.swing.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Provides a base application framework for simple WorldWind examples. Examine other examples in this package to see
 * how it's used.
 *
 * @version $Id: ApplicationTemplate.java 264 2011-12-23 00:20:55Z tgaskins $
 */
public class Main_STANDALONE_v3{
	
	public static WorldWindow wwd;
	
    public static class AppPanel extends JPanel
    {
        
        protected StatusBar statusBar;
        protected ToolTipController toolTipController;
        protected HighlightController highlightController;

       
        
        public AppPanel(Dimension canvasSize, boolean includeStatusBar)
        {
//        	ightController(this.getWwd(), SelectEvent.ROLLOVER);
//            
        	super(new BorderLayout());

			wwd = this.createWorldWindow();
			
			
		 	MyBasicSceneController myBsc = new MyBasicSceneController();
			myBsc.addPropertyChangeListener(wwd.getView());		
			wwd.setSceneController(myBsc);
			
			
			
			
			
			
			
			
		    SharedVariables.wwd=wwd;
			((Component) wwd).setPreferredSize(canvasSize);

			// Create the default model as described in the current worldwind
			// properties.
			Model m = (Model) WorldWind
					.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
			wwd.setModel(m);

			this.add(((Component) wwd), BorderLayout.CENTER);
			if (includeStatusBar) {
				this.statusBar = new StatusBar();
				this.add(statusBar, BorderLayout.PAGE_END);
				//this.statusBar.setEventSource(wwd);
			}

			
			
		
			
			// Add controllers to manage highlighting and tool tips.
			this.toolTipController = new ToolTipController(this.getWwd(),
					AVKey.DISPLAY_NAME, null);
			this.highlightController = new HighlightController(this.getWwd(),
					SelectEvent.ROLLOVER);
        }

        protected WorldWindow createWorldWindow()
        {
            return new WorldWindowGLCanvas();
        }

        public WorldWindow getWwd()
        {
            return wwd;
        }

        public StatusBar getStatusBar()
        {
            return statusBar;
        }
    }

    public static class AppFrame extends JFrame
    {
    	
    	//CurveLayer_STATE l1;
    	private Dimension canvasSize = new Dimension(800, 600);

    	private AnnotationLayer annLayer = new AnnotationLayer();
        
    	//protected AirspaceLayer aglAirspaces;
        
      //  protected Airspace lastHighlit;
      //  protected AirspaceAttributes lastAttrs;
        
        protected AppPanel wwjPanel;
        protected static LayerPanel layerPanel;
        protected StatisticsPanel statsPanel;
 
       

        protected BasicDragger dragger;
        
        public AppFrame()
        {
            this.initialize(true, true, true);
        }

        public AppFrame(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel)
        {
            this.initialize(includeStatusBar, includeLayerPanel, includeStatsPanel);
        }

       
        
        protected void initialize(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel)
        {
        	// Create the WorldWindow.
			this.wwjPanel = this.createAppPanel(this.canvasSize,
					includeStatusBar);
			this.wwjPanel.setPreferredSize(canvasSize);

			// Put the pieces together.
			this.getContentPane().add(wwjPanel, BorderLayout.CENTER);
			if (includeLayerPanel) {
				this.layerPanel = new LayerPanel(this.wwjPanel.getWwd(), null);
				this.getContentPane().add(this.layerPanel, BorderLayout.WEST);
			}

			if (includeStatsPanel
					|| System.getProperty("gov.nasa.worldwind.showStatistics") != null) {
				this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(),
						new Dimension(250, canvasSize.height));
				this.getContentPane().add(this.statsPanel, BorderLayout.EAST);
			}
            
	        
		 
			
            // Create and install the view controls layer and register a controller for it with the World Window.
            //ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
            //insertBeforeCompass(getWwd(), viewControlsLayer);
            //this.getWwd().addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));

            // Register a rendering exception listener that's notified when exceptions occur during rendering.
            this.wwjPanel.getWwd().addRenderingExceptionListener(new RenderingExceptionListener()
            {
                public void exceptionThrown(Throwable t)
                {
                    if (t instanceof WWAbsentRequirementException)
                    {
                        String message = "Computer does not meet minimum graphics requirements.\n";
                        message += "Please install up-to-date graphics driver and try again.\n";
                        message += "Reason: " + t.getMessage() + "\n";
                        message += "This program will end when you press OK.";

                        JOptionPane.showMessageDialog(AppFrame.this, message, "Unable to Start Program",
                            JOptionPane.ERROR_MESSAGE);
                        System.exit(-1);
                    }
                }
            });

            // Search the layer list for layers that are also select listeners and register them with the World
            // Window. This enables interactive layers to be included without specific knowledge of them here.
            for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers())
            {
                if (layer instanceof SelectListener)
                {
                    this.getWwd().addSelectListener((SelectListener) layer);
                }
            }
            
            
           // this.aglAirspaces = new AirspaceLayer();
          
         
            
            
          //MIO CODICE
            
			FlatGlobeOption flatOption = new FlatGlobeOption(this.getWwd());
			
		//	flatOption.enableFlatGlobe(true);
			
			this.getWwd().getInputHandler().addKeyListener(flatOption);
			
			   
            MyBasicOrbitView view = new MyBasicOrbitView();
            view.setGlobe(getWwd().getModel().getGlobe());
            SharedVariables.wwd.setView(view);
           
        
        //	Globe g = getWwd().getModel().getGlobe();	
            SharedVariables.wwd.getView().setEyePosition(Position.fromDegrees(0, 0,10000000));
			createLayers(SharedVariables.wwd);
          /*
			BMNGWMSLayer lay = new BMNGWMSLayer();
	         lay.setName("WMS Layer");
	        // lay.set
	         insertBeforePlacenames(getWwd(), lay);
	        */ 
			Layer l1 = SharedVariables.wwd.getModel().getLayers().getLayerByName("Stars");//.setEnabled(false);
			//Layer l2 = SharedVariables.wwd.getModel().getLayers().getLayerByName("Place Names");//.setEnabled(false);
			Layer l3 = SharedVariables.wwd.getModel().getLayers().getLayerByName("NASA Blue Marble Image");//.setEnabled(false);
			Layer l4 = SharedVariables.wwd.getModel().getLayers().getLayerByName("World Map");//.setEnabled(false);        	
			Layer l5 = SharedVariables.wwd.getModel().getLayers().getLayerByName("Compass");//.setEnabled(false);
			Layer l6 = SharedVariables.wwd.getModel().getLayers().getLayerByName("Scale bar");//.setEnabled(false);
			Layer l7 = SharedVariables.wwd.getModel().getLayers().getLayerByName("USDA NAIP");//.setEnabled(false);
			Layer l8 = SharedVariables.wwd.getModel().getLayers().getLayerByName("USDA NAIP USGS");//.setEnabled(false);
			Layer l9 = SharedVariables.wwd.getModel().getLayers().getLayerByName("MS Virtual Earth Aerial");//.setEnabled(false);
			Layer l10 = SharedVariables.wwd.getModel().getLayers().getLayerByName("Bing Imagery");//.setEnabled(false);        	
			Layer l11= SharedVariables.wwd.getModel().getLayers().getLayerByName("USGS Urban Area Ortho");//.setEnabled(false);
			Layer l12 = SharedVariables.wwd.getModel().getLayers().getLayerByName("Political Boundaries");//.setEnabled(false);
			Layer l13= SharedVariables.wwd.getModel().getLayers().getLayerByName("Open Street Map");//.setEnabled(false);
			Layer l14 = SharedVariables.wwd.getModel().getLayers().getLayerByName("Earth at Night");//.setEnabled(false);
			Layer l15 = SharedVariables.wwd.getModel().getLayers().getLayerByName("USGS Topographic Maps 1:250K");//.setEnabled(false);
			Layer l16= SharedVariables.wwd.getModel().getLayers().getLayerByName("USGS Topographic Maps 1:100K");//.setEnabled(false);
			Layer l17 = SharedVariables.wwd.getModel().getLayers().getLayerByName("USGS Topographic Maps 1:24K");//.setEnabled(false);
			Layer l18 = SharedVariables.wwd.getModel().getLayers().getLayerByName("i-cubed Landsat");//.setEnabled(false);
		 
			
			SharedVariables.wwd.getModel().getLayers().getLayerByName("Atmosphere").setEnabled(false);//.setEnabled(false);
			
			
			
			SharedVariables.wwd.getModel().getLayers().remove(l1);
			//SharedVariables.wwd.getModel().getLayers().remove(l2);
			SharedVariables.wwd.getModel().getLayers().remove(l3);
			SharedVariables.wwd.getModel().getLayers().remove(l4);
			SharedVariables.wwd.getModel().getLayers().remove(l5);
			SharedVariables.wwd.getModel().getLayers().remove(l6);
			SharedVariables.wwd.getModel().getLayers().remove(l7);
			SharedVariables.wwd.getModel().getLayers().remove(l8);
			SharedVariables.wwd.getModel().getLayers().remove(l9);
			SharedVariables.wwd.getModel().getLayers().remove(l10);
			SharedVariables.wwd.getModel().getLayers().remove(l11);
			SharedVariables.wwd.getModel().getLayers().remove(l12);
			SharedVariables.wwd.getModel().getLayers().remove(l13);
			SharedVariables.wwd.getModel().getLayers().remove(l14);
			SharedVariables.wwd.getModel().getLayers().remove(l15);
			SharedVariables.wwd.getModel().getLayers().remove(l16);
			SharedVariables.wwd.getModel().getLayers().remove(l17);
			SharedVariables.wwd.getModel().getLayers().remove(l18);
			
			
            this.initializeSelectionMonitoring();
            
            
            AnnotationAttributes defaultAttributes = new AnnotationAttributes();
            defaultAttributes.setLeader(AVKey.SHAPE_NONE);
            defaultAttributes.setFrameShape(AVKey.SHAPE_ELLIPSE);
            defaultAttributes.setAdjustWidthToText(null);
            
            //defaultAttributes.setCornerRadius(10);
            defaultAttributes.setSize(new Dimension((int)SharedVariables.lense_w, (int)SharedVariables.lense_h));
            //defaultAttributes.setInsets(new Insets(8, 8, 8, 8));
            defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .5f));
            defaultAttributes.setTextColor(Color.WHITE);            
            //defaultAttributes.setDistanceMinScale(.5);
            //defaultAttributes.setDistanceMaxScale(2);
            defaultAttributes.setDistanceMinOpacity(.1);
            defaultAttributes.setOpacity(.3);
            //defaultAttributes.setLeaderGapWidth(14);
            defaultAttributes.setScale(1);
            defaultAttributes.setDrawOffset(new Point(0, -(int)(SharedVariables.lense_h/2)));
            
            SharedVariables.currentAnnotation = new GlobeAnnotation("",
                    Position.fromDegrees(0, 0, 0), defaultAttributes);
            SharedVariables.pos= Position.fromDegrees(0, 0, 0); 
            
           
            
            annLayer.addAnnotation(SharedVariables.currentAnnotation);
            
            
            getWwd().getModel().getGlobe().setTessellator(new MyRectangularTessellator());
            
            //doLoadDemoAirspaces();
            //insertBeforePlacenames(getWwd(), this.aglAirspaces);
            insertBeforePlacenames(getWwd(), annLayer);
            
            //AGGIORNO LA LISTA DEI LAYER DISPONIBILI
            this.getLayerPanel().update(this.getWwd());

            this.pack();

            
          //  Dimension d = getWwd().getSceneController().getDrawContext().getPickPointFrustumDimension();
          //  Rectangle d2 = getWwd().getSceneController().getDrawContext().getPickRectangle();
          //  System.out.println("getPickPointFrustumDimension: "+d);
          //  System.out.println("getPickRectangle: "+d2);
            // Center the application on the screen.
           // WWUtil.alignComponent(null, this, AVKey.CENTER);
           // this.setResizable(true);
            
            
           SectorManager.initLensAnglesMap(4);
        
    
           //ColorsList.generateColorList();
           ColorsList.generateColorListHSB();
           
           System.out.println("JOGL version: "+ Package.getPackage("javax.media.opengl").getImplementationVersion());
        }

       
        public static void removeLayers(){
        	List<Layer> listLayer = SharedVariables.wwd.getModel().getLayers().getLayersByClass(MyRenderableLayer.class);
        	SharedVariables.wwd.getModel().getLayers().removeAll(listLayer);
        }
        
        public static void createLayers(WorldWindow wwd){
        	
        	String dataPath;
        	
         //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes = new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/Airport2Airport2onlyTheBiggest.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         System.out.println("2969 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"2969 arcs annotations");
         
         new GeneratorOfRenderableObjects(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","2969 arcs");
        // new CurveLayer_STATE(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","all arcs");
         
         
        
         
         //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes =new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/Airport2Airport2onlyTheBiggest2.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
        System.out.println("~600 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"~600 arcs annotations");
         new GeneratorOfRenderableObjects(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","~600 arcs");
        
         
         
         
         //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes =new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/Airport2Airport2onlyTheBiggest2.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
        System.out.println("~600 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"FLAT MAP: ~600 arcs annotations");
         GeneratorOfRenderableObjects.GeneratorOfRenderableObjectsOnFlatMap(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","FLAT MAP: ~600 arcs");
        
       
       //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes =new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/Airport2Airport2onlyTheBiggest4.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
        System.out.println("meno di ~600 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"meno di ~600 arcs annotations");
         new GeneratorOfRenderableObjects(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","meno di ~600 arcs");
        
         
         
       //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes =new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/Airport2Airport2onlyTheBiggest4.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
        System.out.println("meno di ~600 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"FLAT MAP: meno di ~600 arcs annotations");
         GeneratorOfRenderableObjects.GeneratorOfRenderableObjectsOnFlatMap(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","FLAT MAP: meno di ~600 arcs");
        
       
         
         
         
         //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes =new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
         dataPath = "./src/data/Airport2Airport2Eu.csv";        	
        // loadFlowsDataFromFILENear(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city);
        System.out.println("~30000 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"~30000 arcs annotations");
         new GeneratorOfRenderableObjects(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","~30000 arcs");
       
       //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes =new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
         dataPath = "./src/data/Airport2Airport2Eu3.csv";        	
        // loadFlowsDataFromFILENear(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city);
        System.out.println("~13000 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"~13000 arcs annotations");
         new GeneratorOfRenderableObjects(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","~13000 arcs");
      
         
         //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes =new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
         dataPath = "./src/data/longdistance.csv";        	
        // loadFlowsDataFromFILENear(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city);
        System.out.println("longdistance arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"longdistance arcs annotations");
         new GeneratorOfRenderableObjects(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","longdistance arcs");
         GeneratorOfRenderableObjects.GeneratorOfRenderableObjectsOnFlatMap(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","FLAT MAP: longdistance arcs");
         
         
         
         /*
       //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes =new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
         dataPath = "./src/data/Airport2Airport2.csv";        	
         //loadFlowsDataFromFILENear(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city);
        System.out.println("~35000 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"~35000 arcs annotations");
         new GeneratorOfRenderableObjects(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","~35000 arcs");
         
         */
         /*
         //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes = new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/Airport2Airport2onlyTheBiggest3.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
        System.out.println("~307 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"~307 arcs annotations");
         new GeneratorOfRenderableObjects(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","~307 arcs");
         
         //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes = new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/Airport2Airport2onlyTheBiggest4.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
        System.out.println("~103 arcs");
         System.out.println("# nodi: "+SharedVariables.nodes.size());
         System.out.println("# archi: "+SharedVariables.city_to_city.size());
         insertAnnotations(wwd,SharedVariables.nodes,"~103 ann");
         new GeneratorOfRenderableObjects(wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","~103 arcs");
         */
         
       //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes = new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/testLong2.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         new GeneratorOfRenderableObjects(SharedVariables.wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","1 arc");
         
         /*
         //CARICO PUNTI
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes = new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/test1.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         new LineLayer_STATE(SharedVariables.wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","test1");
        */
                 /*
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes = new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/test2.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         new GeneratorOfRenderableObjects(SharedVariables.wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","test2");
         
         
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes = new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/test3.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         new GeneratorOfRenderableObjects(SharedVariables.wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","test3");
         
         
         
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes =new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	 dataPath = "./src/data/testitaly.csv";        	
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
         new GeneratorOfRenderableObjects(SharedVariables.wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","testItaly");
         
         SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes = new HashMap<String, Position>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
      	 dataPath = "./src/data/testitaly2.csv";        	
          loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
          new GeneratorOfRenderableObjects(SharedVariables.wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","testItaly2");
          */
          
          /*
          SharedVariables.entries = new ArrayList<String>();
          SharedVariables.nodes = new HashMap<String, Position>();     
          SharedVariables.city_to_city = new HashMap<String, Entry>();            
        	 dataPath = "./src/data/singolo_test.csv";        	
            loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
            new GeneratorOfRenderableObjects(SharedVariables.wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","singolo arco");
         */
            SharedVariables.entries = new ArrayList<String>();
            SharedVariables.nodes = new HashMap<String, Position>();     
            SharedVariables.city_to_city = new HashMap<String, Entry>();            
        	 dataPath = "./src/data/pochi_archi_test.csv";        	
            loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
            new GeneratorOfRenderableObjects(SharedVariables.wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","pochi archi");
            /* 
            
            SharedVariables.entries = new ArrayList<String>();
            SharedVariables.nodes = new ArrayList<String>();
            SharedVariables.city_to_city = new HashMap<String, Entry>();            
        	 dataPath = "./src/data/shortArcs.csv";        	
            loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
            new CurveLayer_STATE(SharedVariables.wwd, SharedVariables.entries, SharedVariables.city_to_city, "all","archi corti");
            */
      }
      
        
        public void initializeSelectionMonitoring()
        {
            this.dragger = new BasicDragger(this.getWwd());
        
            
            this.getWwd().addPositionListener(new PositionListener() {
				
				@Override
				public void moved(PositionEvent pe) {
					
					
					// TODO Auto-generated method stub
					//Point sp = pe.getScreenPoint();
					//System.out.println("position POINT: "+pe.getPosition());
					
					Globe globe = getWwd().getModel().getGlobe();        					
					Vec4 globePoint = globe.computePointFromPosition(SharedVariables.pos);
					Vec4 current = SharedVariables.computeScreenCoordinates(globePoint);        					
					SharedVariables.screenPoint=current;
				}
			});
            
        
       
            this.getWwd().addSelectListener(new SelectListener()
            {
                public void selected(SelectEvent event)
                {
                
                      // Have rollover events highlight the rolled-over object.
                    if (event.getEventAction().equals(SelectEvent.ROLLOVER) && !dragger.isDragging())
                    {
                    }
                    // Have drag events drag the selected object.
                    else if (event.getEventAction().equals(SelectEvent.DRAG_END)
                        || event.getEventAction().equals(SelectEvent.DRAG))
                    {
                    //	System.out.println("dragging event");
                        // Delegate dragging computations to a dragger.
                       
                    	if((isAirspace(event.getTopObject()))||(isAnnotation(event.getTopObject()))){
                    	
                    		//if(isAirspace(event.getTopObject())){
                    		dragger.selected(event);
                    		//}
                    	
                    	PickedObjectList pol = getWwd().getObjectsAtCurrentPosition();
                        if (pol != null)
                        {
                        	Movable dragObject = (Movable) event.getTopObject();
                        	 Position refPos = dragObject.getReferencePosition();
//                            
                   //     	Marker dragObject = (Marker) event.getTopObject();
                   //    	 Position refPos = dragObject.getPosition();
//                           
                        	
                        	 if (refPos == null){
                              System.out.println("ref pos null");
                            	 return;
                             }
                            // Vec4 refPoint = getWwd().getModel().getGlobe().computePointFromPosition(refPos);
                          //   System.out.println("update pos");
                        	SharedVariables.pos=refPos;                           
                        	Globe globe = getWwd().getModel().getGlobe();        					
        					Vec4 globePoint = globe.computePointFromPosition(SharedVariables.pos);
        					Vec4 current = SharedVariables.computeScreenCoordinates(globePoint);        					
        					SharedVariables.screenPoint=current;
                        }
                    	
                        // We missed any roll-over events while dragging, so highlight any under the cursor now,
                        // or de-highlight the dragged shape if it's no longer under the cursor.
                        if (event.getEventAction().equals(SelectEvent.DRAG_END))
                        {
                             pol = getWwd().getObjectsAtCurrentPosition();
                            if (pol != null)
                            {
                            	Movable dragObject = (Movable) event.getTopObject();
                            	 Position refPos = dragObject.getReferencePosition();
                             	 
                                 if (refPos == null)
                                     return;
                      
                            	SharedVariables.pos=refPos;
                           //      getWwd().redraw();
                            }
                        }
                        
                    	}
                    }else if (event.getEventAction().equals(SelectEvent.LEFT_CLICK))
                    {
                    	
                    	
                    	
                    	System.out.println(getWwd().getCurrentPosition());
                    }
                }
            });
        }
        
        
        protected boolean isAnnotation(Object o){
        	if (o instanceof GlobeAnnotation)
            {
        		return true;
            }
        	return false;
        }
        
       
        
        protected boolean isAirspace(Object o){
        	if (o instanceof Airspace)
            {
        		return true;
            }
        	return false;
        }
        
        /*
        protected boolean highlight(Object o)
        {
            if (this.lastHighlit == o)
                return false; // Same thing selected

            // Turn off highlight if on.
            if (this.lastHighlit != null)
            {
                this.lastHighlit.setAttributes(this.lastAttrs);
                this.lastHighlit = null;
                this.lastAttrs = null;
            }

            // Turn on highlight if selected object is a SurfaceImage.
            if (o instanceof Airspace)
            {
                this.lastHighlit = (Airspace) o;
                this.lastAttrs = this.lastHighlit.getAttributes();
                BasicAirspaceAttributes highlitAttrs = new BasicAirspaceAttributes(this.lastAttrs);
                highlitAttrs.setMaterial(Material.WHITE);
                this.lastHighlit.setAttributes(highlitAttrs);
            }

            return true;
        }
        */   
        
        static public void insertAnnotations(WorldWindow wwd,HashMap<String, Position> list, String name){
        	
        	AnnotationLayer annLayer = new AnnotationLayer();
        	annLayer.setName(name);
        	
        	annLayer.setPickEnabled(false);
        	
        	int index =0;
        	
        	for(Position p: list.values()){
        		Position newPos = Position.fromDegrees(p.getLatitude().degrees,p.getLongitude().degrees,20);
        		
        		//String nameAnn = p.toString();
        		String nameAnn = ""+index;
        		nameAnn=reverseIt(nameAnn);
        		
        		GlobeAnnotation a = new GlobeAnnotation(nameAnn, newPos);
        		index++;
        		 // Create default attributes
                AnnotationAttributes defaultAttributes = new AnnotationAttributes();
                defaultAttributes.setCornerRadius(10);
               // defaultAttributes.setInsets(new Insets(8, 8, 8, 8));
                defaultAttributes.setFrameShape(AVKey.SHAPE_NONE); 
                //defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .5f));
                defaultAttributes.setBackgroundColor(null);
                defaultAttributes.setTextColor(Color.WHITE);
                //defaultAttributes.setDrawOffset(new Point(25, 25));
                defaultAttributes.setDistanceMinScale(.5);
                defaultAttributes.setDistanceMaxScale(2);
                defaultAttributes.setDistanceMinOpacity(.5);
                defaultAttributes.setLeaderGapWidth(14);
                defaultAttributes.setDrawOffset(new Point(0, 0));
                defaultAttributes.setOpacity(1);
                
                a.setAttributes(defaultAttributes);
                a.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
                a.setAlwaysOnTop(false);
                annLayer.setEnabled(false);
               // a.setHeightInMeter(200000);
        		annLayer.addAnnotation(a);        		
        	}
        	
        	 int compassPosition = 0;
             LayerList layers =wwd.getModel().getLayers();
             for (Layer l : layers)
             {
                 if (l instanceof CompassLayer)
                     compassPosition = layers.indexOf(l);
             }
             layers.add(compassPosition, annLayer);
        	//SharedVariables.wwd.getModel().getLayers().add(annLayer);
        }
        
        public static String reverseIt(String source) {
            int i, len = source.length();
            StringBuffer dest = new StringBuffer(len);

            for (i = (len - 1); i >= 0; i--)
              dest.append(source.charAt(i));
            return dest.toString();
          }
        
        static public void loadFlowsDataFromFILE(String dataPath,HashMap<String, Position> nodes,ArrayList<String> entries, HashMap<String, Entry> city_to_city) {
        	int i=1;
        	try{
        		//String dataPath = "./src/data/City2City_flows.csv"; 
        		
        		//String dataPath = "./src/data/test.csv";
        		FileInputStream fstream = new FileInputStream(dataPath);
        		DataInputStream in = new DataInputStream(fstream);
        		BufferedReader br = new BufferedReader(new InputStreamReader(in));
        		 
        		String strLine;
        		
        		while ((strLine = br.readLine()) != null) {  
        			
        		
        			
        			Entry entry = new Entry();
        			
        			String[] data = strLine.split(";");
        			entry.setFrom(data[0].trim());
        			
        			entry.setState(data[1].trim());
        			
        			entry.setFrom_lat(Double.parseDouble(data[2].replace(",",".").trim()));
        			entry.setFrom_lon(Double.parseDouble(data[3].replace(",",".").trim()));
        			
        			entry.setTo(data[4].trim());
        			entry.setTo_lat(Double.parseDouble(data[6].replace(",",".").trim()));
        			entry.setTo_lon(Double.parseDouble(data[7].replace(",",".").trim()));
        			//System.out.println("OK" + i);
        			entry.setEc_funding(Double.parseDouble(data[8].trim()));
        			//entry.setRank(Integer.parseInt(data[9].trim()));
        			
        			//if(entry.getFrom().compareTo(entry.getTo()) == 0 ) System.out.println(i);
        			i++;
				
        				//if(true){
        		//	if(SharedVariables.showOnlyNearArc){
            		/*	
        				LatLon from = LatLon.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon());
        				LatLon to = LatLon.fromDegrees(entry.getTo_lat(), entry.getTo_lon());
        				LatLon eu = LatLon.fromDegrees(50.233152,10.180662);
        				double distance= Position.ellipsoidalDistance(from, to, 6378137, 6356752.3);
        				
        				double distanceFromEU= Position.ellipsoidalDistance(from, eu, 6378137, 6356752.3);
        				double distanceFromEU2= Position.ellipsoidalDistance(eu, to, 6378137, 6356752.3);
        			*/
        			
        				//if((distanceFromEU<1000000)||(distanceFromEU2<1000000)){
        					//if(true){
        				//	if((((distanceFromEU<1000000)||(distanceFromEU2<1000000)) & !SharedVariables.showOnlyNearArc)||(distance<=12900000)){
        			
	        		//if(true){
        			if(!nodes.containsKey(entry.getFrom())){
        				Position p = Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon());
        				nodes.put(entry.getFrom(), p);	        			
	        		}
        			if(!nodes.containsKey(entry.getTo())){
        				Position p = Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon());
        				nodes.put(entry.getTo(), p);	      		
	        		}
        			
        			
        						String key = entry.getFrom() + "-" + entry.getTo();
	        		if(!entries.contains(key)){
	        			entries.add(key);
	        			city_to_city.put(key, entry);
	        		}
	        		else{
	       				Entry updated = city_to_city.get(key);
	       				updated.setEc_funding(updated.getEc_funding() + entry.getEc_funding());
	       				city_to_city.put(key, updated);
	       			}	
	        		
        					}
        		//		}
        			
        			
        		//}
        		
        		System.out.println("FINITO LINES");
        		
        		in.close();
        	}catch (Exception e){System.err.println("Error: " + e.getMessage() + " " + i);}
		}
        
        static public void loadFlowsDataFromFILENear(String dataPath,HashMap<String, Position> nodes,ArrayList<String> entries, HashMap<String, Entry> city_to_city) {
        	
        	HashMap<String, Integer> numOccurrences = new HashMap<String, Integer>();
        	
        	int i=1;
        	try{
        		//String dataPath = "./src/data/City2City_flows.csv"; 
        		
        		//String dataPath = "./src/data/test.csv";
        		FileInputStream fstream = new FileInputStream(dataPath);
        		DataInputStream in = new DataInputStream(fstream);
        		BufferedReader br = new BufferedReader(new InputStreamReader(in));
        		 
        		String strLine;
        		
        		while ((strLine = br.readLine()) != null) {  
        			
        		
        			
        			Entry entry = new Entry();
        			
        			String[] data = strLine.split(";");
        			entry.setFrom(data[0].trim());
        			
        			entry.setState(data[1].trim());
        			
        			entry.setFrom_lat(Double.parseDouble(data[2].replace(",",".").trim()));
        			entry.setFrom_lon(Double.parseDouble(data[3].replace(",",".").trim()));
        			
        			entry.setTo(data[4].trim());
        			entry.setTo_lat(Double.parseDouble(data[6].replace(",",".").trim()));
        			entry.setTo_lon(Double.parseDouble(data[7].replace(",",".").trim()));
        			//System.out.println("OK" + i);
        			entry.setEc_funding(Double.parseDouble(data[8].trim()));
        			//entry.setRank(Integer.parseInt(data[9].trim()));
        			
        			//if(entry.getFrom().compareTo(entry.getTo()) == 0 ) System.out.println(i);
        			i++;
				
        				//if(true){
        		//	if(SharedVariables.showOnlyNearArc){
            			
        				LatLon from = LatLon.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon());
        				LatLon to = LatLon.fromDegrees(entry.getTo_lat(), entry.getTo_lon());
        				LatLon eu = LatLon.fromDegrees(50.233152,10.180662);
        				double distance= Position.ellipsoidalDistance(from, to, 6378137, 6356752.3);
        				
        				double distanceFromEU= Position.ellipsoidalDistance(from, eu, 6378137, 6356752.3);
        				double distanceFromEU2= Position.ellipsoidalDistance(eu, to, 6378137, 6356752.3);
        			
        			
        				if((distanceFromEU<1000000)&&(distanceFromEU2<1000000)){
        					//if(true){
        				//	if((((distanceFromEU<1000000)||(distanceFromEU2<1000000)) & !SharedVariables.showOnlyNearArc)||(distance<=12900000)){
        			
	        		//if(true){
        					if(checkNumOccurences(numOccurrences,entry)){
        					
        			if(!nodes.containsKey(entry.getFrom())){
        				Position p = Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon());
        				nodes.put(entry.getFrom(), p);	        			
	        		}
        			if(!nodes.containsKey(entry.getTo())){
        				Position p = Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon());
        				nodes.put(entry.getTo(), p);	      		
	        		}
        			
        			
        						String key = entry.getFrom() + "-" + entry.getTo();
	        		if(!entries.contains(key)){
	        			entries.add(key);
	        			city_to_city.put(key, entry);
	        			
	        			if(!numOccurrences.containsKey(entry.getFrom())){
	        				numOccurrences.put(entry.getFrom(), 1);
	        			}else{
	        			int numFrom = numOccurrences.get(entry.getFrom());
	        			numOccurrences.put(entry.getFrom(), numFrom+1);
	        			}
	        			
	        			if(!numOccurrences.containsKey(entry.getTo())){
	        				numOccurrences.put(entry.getTo(), 1);
	        			}else{
	        			int numTo = numOccurrences.get(entry.getTo());
	        			numOccurrences.put(entry.getTo(), numTo+1);
	        			}
	        		}
	        		else{
	       				Entry updated = city_to_city.get(key);
	       				updated.setEc_funding(updated.getEc_funding() + entry.getEc_funding());
	       				city_to_city.put(key, updated);
	       			}	
	        		
        					}
        				}
        		}
        			
        		//}
        		
        		System.out.println("FINITO LINES");
        		
        		in.close();
        	}catch (Exception e){System.err.println("Error: " + e.getMessage() + " " + i);}
		}
        
        private static  boolean checkNumOccurences(HashMap<String, Integer> numOccurrences, Entry entry) {
        	boolean isFromOk = false;
        	boolean isToOk = false;
        	
        	if(numOccurrences.containsKey(entry.getFrom())){
        		if(numOccurrences.get(entry.getFrom())<10){
        			isFromOk = true;
        		}else{
        			isFromOk = false;
        		}
        	}else{
        		isFromOk = true;
        	}
        	
        	if(numOccurrences.containsKey(entry.getTo())){
        		if(numOccurrences.get(entry.getTo())<5){
        			isToOk = true;
        		}else{
        			isToOk = false;
        		}
        	}else{
        		isToOk = true;
        	}
        	
        	
        	return (isToOk & isFromOk);
        }
        
        private static  void loadFlowsDataFromFILE() {
        	int i=1;
        	try{
        		//String dataPath = "./src/data/City2City_flows.csv"; 
        		//String dataPath = "./src/data/Airport2Airport2.csv";
        		//String dataPath = "./src/data/test.csv";
        		String dataPath = "./src/data/Airport2Airport2onlyTheBiggest.csv";
        		FileInputStream fstream = new FileInputStream(dataPath);
        		DataInputStream in = new DataInputStream(fstream);
        		BufferedReader br = new BufferedReader(new InputStreamReader(in));
        		 
        		String strLine;
        		
        		while ((strLine = br.readLine()) != null) {  
        			
        		
        			
        			Entry entry = new Entry();
        			
        			String[] data = strLine.split(";");
        			entry.setFrom(data[0].trim());
        			
        			entry.setState(data[1].trim());
        			
        			entry.setFrom_lat(Double.parseDouble(data[2].replace(",",".").trim()));
        			entry.setFrom_lon(Double.parseDouble(data[3].replace(",",".").trim()));
        			
        			entry.setTo(data[4].trim());
        			entry.setTo_lat(Double.parseDouble(data[6].replace(",",".").trim()));
        			entry.setTo_lon(Double.parseDouble(data[7].replace(",",".").trim()));
        			//System.out.println("OK" + i);
        			entry.setEc_funding(Double.parseDouble(data[8].trim()));
        			//entry.setRank(Integer.parseInt(data[9].trim()));
        			
        			//if(entry.getFrom().compareTo(entry.getTo()) == 0 ) System.out.println(i);
        			i++;

        			if(SharedVariables.showOnlyNearArc){
        			
        				LatLon from = LatLon.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon());
        				LatLon to = LatLon.fromDegrees(entry.getTo_lat(), entry.getTo_lon());
        				LatLon eu = LatLon.fromDegrees(50.233152,10.180662);
        				double distance= Position.ellipsoidalDistance(from, to, 6378137, 6356752.3);
        				
        				double distanceFromEU= Position.ellipsoidalDistance(from, eu, 6378137, 6356752.3);
        				double distanceFromEU2= Position.ellipsoidalDistance(eu, to, 6378137, 6356752.3);
        			
        				if(true){
        				//if((distanceFromEU<1000000)||(distanceFromEU2<1000000)){
        					//if(true){
        					if(distance>12900000){
        					//if(distance<12900){
        					//if(i%30==0){
        					
        					
	        		if(true){
        						
        						String key = entry.getFrom() + "-" + entry.getTo();
	        		if(! SharedVariables.entries.contains(key)){
	        			 SharedVariables.entries.add(key);
	        			 SharedVariables.city_to_city.put(key, entry);
	        		}
	        		else{
	       				Entry updated =  SharedVariables.city_to_city.get(key);
	       				updated.setEc_funding(updated.getEc_funding() + entry.getEc_funding());
	       			 SharedVariables.city_to_city.put(key, updated);
	       			}	
	        		
        				}
        					
        					
        				}
        					
        			}
        			}
        		}
        		
        		System.out.println("FINITO LINES");
        		
        		in.close();
        	}catch (Exception e){System.err.println("Error: " + e.getMessage() + " " + i);}
		}
        
        private  static void loadPiesDataFromFILE() {      	
        	int i=1;
        	try{
        		String dataPath = "./src/data/City2City_pies.csv"; 
        		FileInputStream fstream = new FileInputStream(dataPath);
        		DataInputStream in = new DataInputStream(fstream);
        		BufferedReader br = new BufferedReader(new InputStreamReader(in));
        		 
        		String strLine;
        		
        		while ((strLine = br.readLine()) != null) {  
        			
        			Entry entry = new Entry();
        			
        			String[] data = strLine.split(";");
        			entry.setFrom(data[0].trim());
        			
        			entry.setState(data[1].trim());
        			
        			entry.setFrom_lat(Double.parseDouble(data[3].replace(",",".").trim()));
        			entry.setFrom_lon(Double.parseDouble(data[2].replace(",",".").trim()));
        			
        			entry.setTo(entry.getFrom());
        			
        			
        			//System.out.println("OK" + i);i++;
        			entry.setEc_funding(Double.parseDouble(data[4].trim()));
        			//entry.setRank(Integer.parseInt(data[9].trim()));

	        		String key = entry.getFrom() + "-" + entry.getTo();
	        		if(! SharedVariables.entries.contains(key)){
	        			 SharedVariables.entries.add(key);
	        			 SharedVariables.city_to_city.put(key, entry);
	        		}
	        		else{
	       				Entry updated =  SharedVariables.city_to_city.get(key);
	       				updated.setEc_funding(updated.getEc_funding() + entry.getEc_funding());
	       			 SharedVariables.city_to_city.put(key, updated);
	       			}			
        		}
        		
        		System.out.println("FINITO PIES");
        		
        		in.close();
        	}catch (Exception e){System.err.println("Error: " + e.getMessage() + " " + i);}
		}

		protected AppPanel createAppPanel(Dimension canvasSize, boolean includeStatusBar)
        {
            return new AppPanel(canvasSize, includeStatusBar);
        }

    

        public AppPanel getWwjPanel()
        {
            return wwjPanel;
        }

        public WorldWindow getWwd()
        {
            return this.wwjPanel.getWwd();
        }

        public StatusBar getStatusBar()
        {
            return this.wwjPanel.getStatusBar();
        }

        public LayerPanel getLayerPanel()
        {
            return layerPanel;
        }

        public StatisticsPanel getStatsPanel()
        {
            return statsPanel;
        }

        public void setToolTipController(ToolTipController controller)
        {
            if (this.wwjPanel.toolTipController != null)
                this.wwjPanel.toolTipController.dispose();

            this.wwjPanel.toolTipController = controller;
        }

        public void setHighlightController(HighlightController controller)
        {
            if (this.wwjPanel.highlightController != null)
                this.wwjPanel.highlightController.dispose();

            this.wwjPanel.highlightController = controller;
        }
        
        protected List<Layer> makeShapefileLayers(String source)
        {
            
            
                ShapefileLoader loader = new ShapefileLoader();
                return loader.createLayersFromSource(source);
            
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

    public static void insertBeforePlacenames(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just before the placenames.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof PlaceNameLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }

    public static void insertAfterPlacenames(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just after the placenames.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof PlaceNameLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition + 1, layer);
    }

    public static void insertBeforeLayerName(WorldWindow wwd, Layer layer, String targetName)
    {
        // Insert the layer into the layer list just before the target layer.
        int targetPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l.getName().indexOf(targetName) != -1)
            {
                targetPosition = layers.indexOf(l);
                break;
            }
        }
        layers.add(targetPosition, layer);
    }

    static
    {
      //  System.setProperty("java.net.useSystemProxies", "true");
        if (Configuration.isMacOS())
        {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "World Wind Application");
            System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
            System.setProperty("apple.awt.brushMetalLook", "true");
        }
        else if (Configuration.isWindowsOS())
        {
            System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
        }
    }

    public static AppFrame start(String appName, Class appFrameClass)
    {
        if (Configuration.isMacOS() && appName != null)
        {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        }

        try
        {
            final AppFrame frame = (AppFrame) appFrameClass.newInstance();
            frame.setTitle(appName);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            java.awt.EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    frame.setVisible(true);
                }
            });

          //  SharedVariables.frame=frame;
            
            return frame;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void setupConfiguration()
    {
    	/*
    	Configuration.setValue(AVKey.INITIAL_ALTITUDE, 9000);
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 46.0696924);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, 11.1210886);
        */
    }

    public static void main(String[] args)
    {
    	//setupConfiguration();
        Main_STANDALONE_v3.start("World Wind Application", AppFrame.class);
    }
    
  
}
