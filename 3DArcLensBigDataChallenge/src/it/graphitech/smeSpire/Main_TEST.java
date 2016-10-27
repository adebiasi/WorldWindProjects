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
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.render.airspaces.AirspaceAttributes;
import gov.nasa.worldwind.render.airspaces.BasicAirspaceAttributes;
import gov.nasa.worldwind.render.airspaces.CappedCylinder;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import gov.nasa.worldwindx.examples.LayerPanel;
import gov.nasa.worldwindx.examples.Airspaces.AirspacesController;
import gov.nasa.worldwindx.examples.util.*;
import it.graphitech.GeneratorOfRenderableObjects;
import it.graphitech.smeSpire.entry.Entry;
import it.graphitech.smeSpire.framebuffer.MyBasicOrbitView;
import it.graphitech.smeSpire.layers.LineLayer_STATE;
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
public class Main_TEST{
	
    public static class AppPanel extends JPanel
    {
        protected WorldWindow wwd;
        protected StatusBar statusBar;
        protected ToolTipController toolTipController;
        protected HighlightController highlightController;

        
        
        public AppPanel(Dimension canvasSize, boolean includeStatusBar)
        {

        	super(new BorderLayout());

			this.wwd = this.createWorldWindow();
		    SharedVariables.wwd=wwd;
			((Component) this.wwd).setPreferredSize(canvasSize);

			// Create the default model as described in the current worldwind
			// properties.
			Model m = (Model) WorldWind
					.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
			this.wwd.setModel(m);

			this.add(((Component) this.wwd), BorderLayout.CENTER);
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

    protected static class AppFrame extends JFrame
    {
    	
    	//CurveLayer_STATE l1;
    	private Dimension canvasSize = new Dimension(800, 600);

    	private AnnotationLayer annLayer = new AnnotationLayer();
        
    	//protected AirspaceLayer aglAirspaces;
        
      //  protected Airspace lastHighlit;
      //  protected AirspaceAttributes lastAttrs;
        
        protected AppPanel wwjPanel;
        protected LayerPanel layerPanel;
        protected StatisticsPanel statsPanel;
 
        private ArrayList<String> entries;
        private HashMap<String, Entry> city_to_city;

        protected BasicDragger dragger;
        
        public AppFrame()
        {
            this.initialize(true, true, false);
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
        	//view.setFrustum(newFrustum);
        	//SharedVariables.wwd.setView(new FlatOrbitView());
        	SharedVariables.wwd.setView(view);
            	
        	
        	
        	
            //CARICO PUNTI
            this.entries = new ArrayList<String>();
        	this.city_to_city = new HashMap<String, Entry>();            
        	String dataPath = "./src/data/test1.csv";        	
            this.loadFlowsDataFromFILE(dataPath,this.entries,this.city_to_city); 
            new LineLayer_STATE(this.getWwd(), this.entries, this.city_to_city, "all","test1");
           
                    
            this.entries = new ArrayList<String>();
        	this.city_to_city = new HashMap<String, Entry>();            
        	 dataPath = "./src/data/test2.csv";        	
            this.loadFlowsDataFromFILE(dataPath,this.entries,this.city_to_city); 
            new GeneratorOfRenderableObjects(this.getWwd(), this.entries, this.city_to_city, "all","test2");
            
            
            this.entries = new ArrayList<String>();
        	this.city_to_city = new HashMap<String, Entry>();            
        	 dataPath = "./src/data/test3.csv";        	
            this.loadFlowsDataFromFILE(dataPath,this.entries,this.city_to_city); 
            new GeneratorOfRenderableObjects(this.getWwd(), this.entries, this.city_to_city, "all","test3");
            
            
            
            /*
            
            
            this.entries = new ArrayList<String>();
        	this.city_to_city = new HashMap<String, Entry>();
            
        	 dataPath = "./src/data/test4.csv";
        	
            this.loadFlowsDataFromFILE(dataPath); 
            new CurveLayer_STATE(this.getWwd(), this.entries, this.city_to_city, "all");
            
            */	
            
            
            
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
            
            Annotation currentAnnotation = new GlobeAnnotation("",
                    Position.fromDegrees(0, 0, 0), defaultAttributes);
            SharedVariables.pos= Position.fromDegrees(0, 0, 0); 
            
           
            
            annLayer.addAnnotation(currentAnnotation);
            
            //doLoadDemoAirspaces();
            //insertBeforePlacenames(getWwd(), this.aglAirspaces);
            insertBeforePlacenames(getWwd(), annLayer);
            
            //AGGIORNO LA LISTA DEI LAYER DISPONIBILI
            this.getLayerPanel().update(this.getWwd());

            this.pack();

            
            Dimension d = getWwd().getSceneController().getDrawContext().getPickPointFrustumDimension();
            Rectangle d2 = getWwd().getSceneController().getDrawContext().getPickRectangle();
            System.out.println("getPickPointFrustumDimension: "+d);
            System.out.println("getPickRectangle: "+d2);
            // Center the application on the screen.
           // WWUtil.alignComponent(null, this, AVKey.CENTER);
           // this.setResizable(true);
            
            
          //  MyBasicOrbitView view = new MyBasicOrbitView();
        	//view.setFrustum(newFrustum);
        	//SharedVariables.wwd.setView(new FlatOrbitView());
        	//SharedVariables.wwd.setView(view);
        }

       
        
        public void initializeSelectionMonitoring()
        {
            this.dragger = new BasicDragger(this.getWwd());
        
            
            this.getWwd().addPositionListener(new PositionListener() {
				
				@Override
				public void moved(PositionEvent pe) {
					
					
					// TODO Auto-generated method stub
					//Point sp = pe.getScreenPoint();
					System.out.println("position POINT: "+pe.getPosition());
					
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
                                 getWwd().redraw();
                            }
                        }
                        
                    	}
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
    
        static public void loadFlowsDataFromFILE(String dataPath,ArrayList<String> entries, HashMap<String, Entry> city_to_city) {
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
        			
	        		//if(true){
        						
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
        		
        		System.out.println("FINITO LINES");
        		
        		in.close();
        	}catch (Exception e){System.err.println("Error: " + e.getMessage() + " " + i);}
		}
        
        private void loadPiesDataFromFILE() {      	
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
	        		if(!this.entries.contains(key)){
	        			this.entries.add(key);
	        			this.city_to_city.put(key, entry);
	        		}
	        		else{
	       				Entry updated = this.city_to_city.get(key);
	       				updated.setEc_funding(updated.getEc_funding() + entry.getEc_funding());
	       				this.city_to_city.put(key, updated);
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
         //   SharedVariables.frame2=frame;
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
        Main_TEST.start("World Wind Application", AppFrame.class);
    }
}
