package it.main;
/*
Copyright (C) 2001 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/


import it.GeneratorOfRenderableObjects;
import it.GeoOperation2;
import it.LoadTerrain;
import it.Operations;
import it.SharedVariables;
import it.StatisticsPanel;
import it.entities.Entry;
import it.entities.Grid;
import it.entities.Node;
import it.layers.GlobeAnnotation;
import it.listeners.ChangePositionListener;
import it.listeners.KeyOption;
import it.listeners.LensListener;
import it.rendLayers.MyRenderableLayer;
import it.sceneController.MyBasicSceneController;
import it.views.MyBasicOrbitView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.AWTInputHandler;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.data.WWDotNetLayerSetConverter;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.PerformanceStatistic;
import gov.nasa.worldwindx.examples.LayerPanel;
import gov.nasa.worldwindx.examples.util.HighlightController;
import gov.nasa.worldwindx.examples.util.ToolTipController;
import gov.nasa.worldwindx.examples.view.AddAnimator;

/**
 * This is the most basic World Wind program.
 *
 * @version $Id: HelloWorldWind.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class MainDeformableGlobe
{
    // An inner class is used rather than directly subclassing JFrame in the main class so
    // that the main can configure system properties prior to invoking Swing. This is
    // necessary for instance on OS X (Macs) so that the application name can be specified.
//static Grid grid ;
//static Grid grid2 ;
//static Grid grid3 ;
	
static public ArrayList<Position> pois ;
static public ArrayList<Grid> gridPois ;

static public WorldWindow world;

static public LoadTerrain loadTerrain = new LoadTerrain();

static public boolean isDebug = true;

static  public AirspaceLayer  poisLayer;


static javax.swing.Timer hoverTimer = new javax.swing.Timer(0, new ActionListener()
{
    public void actionPerformed(ActionEvent actionEvent)
    {
    	WorldWindow wwd = SharedVariables.wwd;
    	//  if (wwd.getSceneController().getPerFrameStatistics().size() < 1)
          //    return;

      //    PerformanceStatistic[] pfs = new PerformanceStatistic[wwd.getPerFrameStatistics().size()];
         /*
          pfs = wwd.getSceneController().getPerFrameStatistics().toArray(pfs);
          Arrays.sort(pfs);
          for (PerformanceStatistic stat : pfs)
          {
        	   String val = (stat.toString());
               System.out.println(".. : "+val);
             
          }
          */
        //  System.out.println("FrameRate: "+wwd.getSceneController().getFrameTime());
          wwd.redrawNow();
      
    }
});


    private static class AppFrame extends javax.swing.JFrame
    {
    	 StatisticsPanel statsPanel;
    	
    	 AirspaceLayer airspacesLayer;
    	 AirspaceLayer nodesAirspacesLayer;
    	 AirspaceLayer focusAreaAirspacesLayer;
    	
    	 AirspaceLayer deformedAirspaces;
    	 
    	 AnnotationLayer labelsLayer ;
    	 
    	 AnnotationLayer annLayer = new AnnotationLayer();
    	 
    	 //MyRenderableLayer layer = new MyRenderableLayer();
		  File vertexShaderFile_transparency = new File("shader/transparency.vert");
	        File fragmentShaderFile_transparency = new File("shader/transparency.frag");      
	        File dirShaders = new File("shader/");
  		       		               	

    	 MyRenderableLayer layer = new MyRenderableLayer(vertexShaderFile_transparency, fragmentShaderFile_transparency, dirShaders); 
    	 protected static LayerPanel layerPanel;
    	 
        public AppFrame()
        {
            WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
            world=wwd;
            wwd.setPreferredSize(new java.awt.Dimension(1000, 800));
            this.getContentPane().add(wwd, java.awt.BorderLayout.CENTER);
            this.pack();
               
            
            
			MyBasicSceneController myBsc = new MyBasicSceneController();
			myBsc.addPropertyChangeListener(wwd.getView());			
			wwd.setSceneController(myBsc);

            
            
            
            
            wwd.setModel(new BasicModel());
            
            MyBasicOrbitView view = new MyBasicOrbitView();
            view.setGlobe(wwd.getModel().getGlobe());
            wwd.setView(view);
            
            SharedVariables.wwd=wwd;
            
  /*          
             grid = new Grid();
             grid2 = new Grid();
             grid3 = new Grid();
    */        
             airspacesLayer = new AirspaceLayer();
            this.airspacesLayer.setName("Airspaces");    
            airspacesLayer.setPickEnabled(false);
            nodesAirspacesLayer = new AirspaceLayer();
            this.nodesAirspacesLayer.setName("Nodes");   
            labelsLayer = new AnnotationLayer();
            this.labelsLayer.setName("Labels");   
            this.labelsLayer.setEnabled(false);
            focusAreaAirspacesLayer = new AirspaceLayer();
            this.focusAreaAirspacesLayer.setName("Focus Area");   
            deformedAirspaces = new AirspaceLayer();
            this.deformedAirspaces.setName("Deformed Airspaces");     
            deformedAirspaces.setEnabled(false);
            poisLayer = new AirspaceLayer();
            poisLayer.setName("POIS Airspaces");     
            
        	Layer l1 = wwd.getModel().getLayers().getLayerByName("Stars");//.setEnabled(false);
			Layer l2 = wwd.getModel().getLayers().getLayerByName("Place Names");//.setEnabled(false);
			//Layer l3 = wwd.getModel().getLayers().getLayerByName("NASA Blue Marble Image");//.setEnabled(false);
			Layer l4 = wwd.getModel().getLayers().getLayerByName("World Map");//.setEnabled(false);        	
			Layer l5 = wwd.getModel().getLayers().getLayerByName("Compass");//.setEnabled(false);
			Layer l6 = wwd.getModel().getLayers().getLayerByName("Scale bar");//.setEnabled(false);
			Layer l7 = wwd.getModel().getLayers().getLayerByName("USDA NAIP");//.setEnabled(false);
			Layer l8 = wwd.getModel().getLayers().getLayerByName("USDA NAIP USGS");//.setEnabled(false);
			Layer l9 = wwd.getModel().getLayers().getLayerByName("MS Virtual Earth Aerial");//.setEnabled(false);
			Layer l10 = wwd.getModel().getLayers().getLayerByName("Bing Imagery");//.setEnabled(false);        	
			Layer l11= wwd.getModel().getLayers().getLayerByName("USGS Urban Area Ortho");//.setEnabled(false);
			Layer l12 = wwd.getModel().getLayers().getLayerByName("Political Boundaries");//.setEnabled(false);
			Layer l13= wwd.getModel().getLayers().getLayerByName("Open Street Map");//.setEnabled(false);
			Layer l14 = wwd.getModel().getLayers().getLayerByName("Earth at Night");//.setEnabled(false);
			Layer l15 = wwd.getModel().getLayers().getLayerByName("USGS Topographic Maps 1:250K");//.setEnabled(false);
			Layer l16= wwd.getModel().getLayers().getLayerByName("USGS Topographic Maps 1:100K");//.setEnabled(false);
			Layer l17 = wwd.getModel().getLayers().getLayerByName("USGS Topographic Maps 1:24K");//.setEnabled(false);
			Layer l18 = wwd.getModel().getLayers().getLayerByName("i-cubed Landsat");//.setEnabled(false);
			//Layer l19 = wwd.getModel().getLayers().getLayerByName("Blue Marble May 2004");//.setEnabled(false);
		 
			
			wwd.getModel().getLayers().getLayerByName("Atmosphere").setEnabled(false);//.setEnabled(false);
			
			
			
			wwd.getModel().getLayers().remove(l1);
			wwd.getModel().getLayers().remove(l2);
			//wwd.getModel().getLayers().remove(l3);
			wwd.getModel().getLayers().remove(l4);
			wwd.getModel().getLayers().remove(l5);
			wwd.getModel().getLayers().remove(l6);
			wwd.getModel().getLayers().remove(l7);
			wwd.getModel().getLayers().remove(l8);
			wwd.getModel().getLayers().remove(l9);
			wwd.getModel().getLayers().remove(l10);
			wwd.getModel().getLayers().remove(l11);
			wwd.getModel().getLayers().remove(l12);
			wwd.getModel().getLayers().remove(l13);
			wwd.getModel().getLayers().remove(l14);
			wwd.getModel().getLayers().remove(l15);
			wwd.getModel().getLayers().remove(l16);
			wwd.getModel().getLayers().remove(l17);
			//wwd.getModel().getLayers().remove(l18);
			//wwd.getModel().getLayers().remove(l19);
            
            //Operations.generatePOIs();
            //Operations.renderPOIs(poisLayer);
            
            /*
            Position pos = Position.fromDegrees(43, 43);            
            RenderableObjects.generateAirspaces(pos, airspaces);
            
            Position pos2 = Position.fromDegrees(23, 63);            
            RenderableObjects.generateAirspaces(pos2, airspaces);
            
            Position pos3 = grid.returnOppositePosition(pos, pos2);
            RenderableObjects.generateAirspaces(pos3, airspaces);
            */
            /*
            Position ld = Position.fromDegrees(3, 23,0);            
            Position rd = Position.fromDegrees(3, 93,0);            
            Position lu = Position.fromDegrees(83, 23,0);            
            Position ru = Position.fromDegrees(83, 93,0);            
            
            grid.generateGrid(ld, rd, lu, ru);
            DeformableSurface deformableSurface = new DeformableSurface(grid);
            
            //grid.drawGrid(airspaces);
            //grid.drawOppositeGrid(airspaces);
            //grid.drawAllDeformedGrids(deformedAirspaces);
         layer.addRenderable(deformableSurface);
         Position ld2 = Position.fromDegrees(63, 43,0);            
         Position rd2 = Position.fromDegrees(63, 83,0);            
         Position lu2 = Position.fromDegrees(43, 43,0);            
         Position ru2 = Position.fromDegrees(43, 83,0);            
         
         grid2.generateGrid(ld2, rd2, lu2, ru2);
         DeformableSurface deformableSurface2 = new DeformableSurface(grid2);
         
         //grid.drawGrid(airspaces);
         //grid.drawOppositeGrid(airspaces);
         //grid.drawAllDeformedGrids(deformedAirspaces);
      layer.addRenderable(deformableSurface2);
            
      Position ld3 = Position.fromDegrees(43, 43,0);            
      Position rd3 = Position.fromDegrees(63, 43,0);            
      Position lu3 = Position.fromDegrees(43, 83,0);            
      Position ru3 = Position.fromDegrees(63, 83,0);            
      
      grid3.generateGrid(ld3, rd3, lu3, ru3);
      DeformableSurface deformableSurface3 = new DeformableSurface(grid3);
      
      //grid3.drawGrid(airspaces);
      //grid3.drawOppositeGrid(airspaces);
      //grid.drawAllDeformedGrids(deformedAirspaces);
   layer.addRenderable(deformableSurface3);
      */
      
			
		//	double res = GeoOperation2.pointOnTopOfALine(Position.fromDegrees(0, 0), Position.fromDegrees(0, 100), Position.fromDegrees(-50, 50));
			
		//	System.out.println("RES: "+res);
      
  // Position p = Position.fromDegrees(143, 43,0);     
//	 RenderableObjects.generateAirspaces(p, airspacesLayer, Color.RED);
      
        	KeyOption keyOption = new KeyOption(wwd,deformedAirspaces);
        	wwd.getInputHandler().addKeyListener(keyOption);
            
       /// 	MouseMovement mouse = new MouseMovement(wwd,airspacesLayer,layer,deformedAirspaces);
        	
        	//   wwd.addMouseListener(mouse);
              
//        	   this.getWwd().addRenderingListener(new RenderingListener() {
//				
//				@Override
//				public void stageChanged(RenderingEvent arg0) {
//					// TODO Auto-generated method stub
//					System.out.println("CHANGE");
//				}
//			});
        	
        	createLayers(wwd);
        	
        	
        	SurfaceImage whiteImage = new SurfaceImage();
        	whiteImage.setImageSource("images/White.png", Sector.FULL_SPHERE);
        	whiteImage.setPickEnabled(false);
        	
        	RenderableLayer layerWhite = new RenderableLayer();
        	layerWhite.setName("White Layer");
        	layerWhite.setEnabled(false);
        	layerWhite.setPickEnabled(false);
        	layerWhite.addRenderable(whiteImage);
        	//layerWhite.setOpacity(0.99);
        	
        	
        	ChangePositionListener changePos = new ChangePositionListener(wwd,layer,deformedAirspaces);
        	 //  this.getWwd().addPositionListener(changePos);
        	
        	   this.getWwd().addRenderingListener(changePos);
        	   
        	   /*
        	   this.getWwd().addRenderingListener(new RenderingListener() {
				
				@Override
				public void stageChanged(RenderingEvent event) {
					// TODO Auto-generated method stub
					if (event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP)){
						System.out.println("aaassas");
					}else{
						System.out.println("dsds");
					}
				}
			});
        	   */
        	   
        	   
        	insertBeforePlacenames(wwd, this.layer);
            insertBeforePlacenames(wwd, this.airspacesLayer);
            insertBeforePlacenames(wwd, this.nodesAirspacesLayer);
            insertBeforePlacenames(wwd, this.labelsLayer);
            insertBeforePlacenames(wwd, this.focusAreaAirspacesLayer);
            insertBeforePlacenames(wwd, this.deformedAirspaces);
            insertBeforePlacenames(wwd, poisLayer);
            
            insertBeforePlacenames(wwd, layerWhite);
         
            createLens(annLayer, wwd);
            
            this.layerPanel = new LayerPanel(this.getWwd(), null);
			this.getContentPane().add(this.layerPanel, BorderLayout.WEST);

			new LensListener().initializeSelectionMonitoring();
			
			

			this.statsPanel = new StatisticsPanel(this.getWwd(),
					new Dimension(250, 800));
			this.getContentPane().add(this.statsPanel, BorderLayout.EAST);
            
			
			
			hoverTimer.start();
        }
        
        
        public WorldWindow getWwd()
        {
            return world;
        }
    }

   
    private void fill(WorldWindow wwd)
    {
        if (wwd.getSceneController().getPerFrameStatistics().size() < 1)
            return;

        PerformanceStatistic[] pfs = new PerformanceStatistic[wwd.getPerFrameStatistics().size()];
        pfs = wwd.getSceneController().getPerFrameStatistics().toArray(pfs);
        Arrays.sort(pfs);
        for (PerformanceStatistic stat : pfs)
        {
            String val = (stat.toString());
           System.out.println(".. : "+val);
        }
    }
    
    public static void createLayers(WorldWindow wwd){
    	
    	String dataPath;
         
     //CARICO PUNTI
     SharedVariables.entries = new ArrayList<String>();
     SharedVariables.nodes =new HashMap<String, Node>();     
     SharedVariables.city_to_city = new HashMap<String, Entry>();            
 	 dataPath = "./src/data/Airport2Airport2onlyTheBiggest2.csv";
 	 String layerName = "~600 arcs";
     loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
     
     System.out.println("~600 arcs");
     System.out.println("# nodi: "+SharedVariables.nodes.size());
     System.out.println("# archi: "+SharedVariables.city_to_city.size());
     insertAnnotations(wwd,SharedVariables.nodes,"~600 arcs annotations");
     new GeneratorOfRenderableObjects(wwd, SharedVariables.nodes,SharedVariables.entries, SharedVariables.city_to_city, "all",layerName);
   
     
   //CARICO PUNTI
     SharedVariables.entries = new ArrayList<String>();
     SharedVariables.nodes =new HashMap<String, Node>();     
     SharedVariables.city_to_city = new HashMap<String, Entry>();            
 	 dataPath = "./src/data/Airport2Airport2onlyTheBiggest4.csv";
 	  layerName = "meno di 600 arcs";
     loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
     
     System.out.println("meno di 600 arcs");
     System.out.println("# nodi: "+SharedVariables.nodes.size());
     System.out.println("# archi: "+SharedVariables.city_to_city.size());
     insertAnnotations(wwd,SharedVariables.nodes,"~600 arcs annotations");
     new GeneratorOfRenderableObjects(wwd, SharedVariables.nodes,SharedVariables.entries, SharedVariables.city_to_city, "all",layerName);
   
     
	 dataPath = "./src/data/HostwayInternational.graphml.txt";
	  layerName = "HostwayInternational";
	  createArcs(layerName, dataPath,null);   
	  
	  dataPath = "./src/data/Airtel.graphml.txt";
	  layerName = "Airtel";
	  createArcs(layerName, dataPath,null);   
	  
	  dataPath = "./src/data/HurricaneElectric.graphml.txt";
	  layerName = "HurricaneElectric";
	  createArcs(layerName, dataPath,null);   
	  
	  dataPath = "./src/data/Internode.graphml.txt";
	  layerName = "Internode";
	  createArcs(layerName, dataPath,null);   
	  
	  dataPath = "./src/data/Packetexchange.graphml.txt";
	  layerName = "Packetexchange";
	  createArcs(layerName, dataPath,null);   
	  
    
     
     
     dataPath = "./src/data/Airport2Airport3.csv";
	  layerName = "4777 arcs";
	  createArcs(layerName, dataPath,null);   
     
  
     
     
  	 dataPath = "./src/data/daAustralia.csv";
 	  layerName = "Australia arcs";
 	  createArcs(layerName, dataPath,null);   
 	  
 	
     
     
     dataPath = "./src/data/globe.csv";
	  layerName = "globe";
	  createArcs(layerName, dataPath,null);   
     
	
    
     //CARICO PUNTI
     dataPath = "./src/data/2archi_test.csv";
 	 layerName = "2archi_test";
 	createArcs(layerName, dataPath,"2archi_test annotations");   
     
   
     
     dataPath = "./src/data/3archi_test.csv";
 	 layerName = "1archi_test";
 	createArcs(layerName, dataPath,"1archi_test annotations");   
     
  
     
     dataPath = "./src/data/distance.csv";
 	 layerName = "distance";
 	createArcs(layerName, dataPath,"distance annotations");   
     
     
     
     
     dataPath = "./src/data/distance2.csv";
 	 layerName = "distance2";
     createArcs(layerName, dataPath,"distance2 annotations");   
     
   
    
layerName="pochi archi";
dataPath = "./src/data/pochi_archi_test3.csv";
createArcs(layerName, dataPath,null);   

      
  }
	
    
    
    private static void createArcs(String layerName, String dataPath,String annLayerName){
    	
        SharedVariables.entries = new ArrayList<String>();
         SharedVariables.nodes = new HashMap<String, Node>();     
         SharedVariables.city_to_city = new HashMap<String, Entry>();            
     	        	
         //dataPath = "./src/data/un_arco.csv";
     	 System.out.println(layerName);
         loadFlowsDataFromFILE(dataPath,SharedVariables.nodes,SharedVariables.entries,SharedVariables.city_to_city); 
   	  System.out.println("# nodi: "+SharedVariables.nodes.size());
 	     System.out.println("# archi: "+SharedVariables.city_to_city.size());     
 	     if(annLayerName!=null){
 	    	insertAnnotations(SharedVariables.wwd,SharedVariables.nodes,"distance2 annotations");
 	     }
 	     
         new GeneratorOfRenderableObjects(SharedVariables.wwd, SharedVariables.nodes, SharedVariables.entries, SharedVariables.city_to_city, "all",layerName);
  
    }
    
    static private void createLens(AnnotationLayer annLayer,WorldWindowGLCanvas wwd){
    	 AnnotationAttributes defaultAttributes = new AnnotationAttributes();
         defaultAttributes.setLeader(AVKey.SHAPE_NONE);
         defaultAttributes.setFrameShape(AVKey.SHAPE_ELLIPSE);
         defaultAttributes.setAdjustWidthToText(null);
         
         //defaultAttributes.setCornerRadius(10);
         defaultAttributes.setSize(new Dimension((int)SharedVariables.lense_w, (int)SharedVariables.lense_h));
         //defaultAttributes.setInsets(new Insets(8, 8, 8, 8));
         defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .2f));
         defaultAttributes.setTextColor(Color.WHITE);    
         defaultAttributes.setBorderColor(new Color(1f, 1f, 1f,1f));
         defaultAttributes.setBorderWidth(2);
         //defaultAttributes.setDistanceMinScale(.5);
         //defaultAttributes.setDistanceMaxScale(2);
         defaultAttributes.setDistanceMinOpacity(.1);
         defaultAttributes.setOpacity(.9);
         //defaultAttributes.setLeaderGapWidth(14);
         defaultAttributes.setScale(1);
         defaultAttributes.setDrawOffset(new Point(0, -(int)(SharedVariables.lense_h/2)));
         
         SharedVariables.currentAnnotation = new GlobeAnnotation("",
                 Position.fromDegrees(0, 0, 0), defaultAttributes);
         SharedVariables.pos= Position.fromDegrees(0, 0, 0); 
         
        
         annLayer.setPickEnabled(true);
         annLayer.addAnnotation(SharedVariables.currentAnnotation);
         
         
                  
         //doLoadDemoAirspaces();
         //insertBeforePlacenames(getWwd(), this.aglAirspaces);
         insertBeforePlacenames(wwd, annLayer);
    }
    
    static public GlobeAnnotation createLabel(Position p , String name){
    	GlobeAnnotation a = new GlobeAnnotation(name, p);
		
		 // Create default attributes
       AnnotationAttributes defaultAttributes = new AnnotationAttributes();
       defaultAttributes.setCornerRadius(10);
      // defaultAttributes.setInsets(new Insets(8, 8, 8, 8));
       defaultAttributes.setFrameShape(
       		AVKey.SHAPE_NONE
       	//	AVKey.SHAPE_RECTANGLE
       		); 
       //defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .5f));
       defaultAttributes.setBackgroundColor(null);
       defaultAttributes.setTextColor(Color.RED);
       defaultAttributes.setDrawOffset(new Point(0, -25));
       defaultAttributes.setDistanceMinScale(.5);
       defaultAttributes.setDistanceMaxScale(2);
       defaultAttributes.setDistanceMinOpacity(.5);
       defaultAttributes.setLeaderGapWidth(0);
     //  defaultAttributes.setDrawOffset(new Point(0, 0));
       defaultAttributes.setOpacity(1);
       defaultAttributes.setFont(Font.decode("Arial-PLAIN-18"));
       
       a.setAttributes(defaultAttributes);
       a.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
       a.setAlwaysOnTop(true);
       
       return a;
    }
	
    static public void insertAnnotations(WorldWindow wwd,HashMap<String, Node> list, String name){
    	
    	AnnotationLayer annLayer = new AnnotationLayer();
    	annLayer.setName(name);
    	
    	annLayer.setPickEnabled(false);
    	
    	int index =0;
    	
    	for(Node node: list.values()){
    		Position p = node.getOriginalPosition();
    		Position newPos = Position.fromDegrees(p.getLatitude().degrees,p.getLongitude().degrees,5);
    		
    		//String nameAnn = p.toString();
    		//String nameAnn = ""+index;
    		//nameAnn=reverseIt(nameAnn);
    		
    		//GlobeAnnotation a = createLabel(newPos, nameAnn);
    		GlobeAnnotation a = createLabel(newPos, node.getName());
            
            index++;            
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
    static public void loadFlowsDataFromFILE(String dataPath,HashMap<String, Node> nodes,ArrayList<String> entries, HashMap<String, Entry> city_to_city) {
    	int i=1;
    	
    	int indexNameNode =0;
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
    			if(!nodes.containsKey(entry.getFrom())){
    				Position p = Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon());
    				String nameAnn=reverseIt(""+indexNameNode);
    				Node node = new Node(p,""+nameAnn);
    				indexNameNode++;
    				nodes.put(entry.getFrom(), node);	        			
        		}
    			if(!nodes.containsKey(entry.getTo())){
    				Position p = Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon());
    				String nameAnn=reverseIt(""+indexNameNode);
    				Node node = new Node(p,""+nameAnn);
    				indexNameNode++;
    				nodes.put(entry.getTo(), node);	      		
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
    
    static AppFrame frame;
    public static void main(String[] args)
    {
    	
    	 
         
        if (Configuration.isMacOS())
        {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Hello World Wind");
        }

         frame = new AppFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                // Create an AppFrame and immediately make it visible. As per Swing convention, this
                // is done within an invokeLater call so that it executes on an AWT thread.
                frame.setVisible(true);
            }
        });
    }
}