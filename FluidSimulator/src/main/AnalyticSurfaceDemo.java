package main;
/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration. 
All Rights Reserved. 
*/


import fluidSimulator.MyMouseListener;
import fluidSimulator.Variables;
import fluidSimulator.fluid;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Illustrates how to configure and display a 3D geographic grid of scalar data using the World Wind <code>{@link
 * AnalyticSurface}</code>. Analytic surface defines a grid over a geographic <code>{@link Sector}</code> at a specified
 * altitude, and enables the caller to specify the color and height at each grid point.
 * <p/>
 * This illustrates three key AnalyticSurface configurations: <ul> <li>Displaying a static data set where each grid
 * point uses color and height to indicate the data's magnitude.</li> <li>Displaying data that varies by color over time
 * on the terrain surface.</li> <li>Displaying data that varies by color and height over time at a specified
 * altitude.</li> </ul>
 *
 * @author dcollins
 * @version $Id: AnalyticSurfaceDemo.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class AnalyticSurfaceDemo extends ApplicationTemplate
{
   // protected static final String DATA_PATH = "gov/nasa/worldwindx/examples/data/wa-precip-24hmam-5km.tif";

    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        protected static final double HUE_BLUE = 240d / 360d;
        protected static final double HUE_RED = 0d / 360d;
        protected RenderableLayer FluidDensitySurfaceLayer;
        protected RenderableLayer FluidVelocity_x_SurfaceLayer;
        protected RenderableLayer FluidVelocity_y_SurfaceLayer;

        public AppFrame()
        {
        	File file = new File(Variables.fileBuilding);
            Thread t = new importShapes.buildings.BuildingWorkerThread(file, getWwd(),this);
            //t.start();
            t.run();
        	
        	fluid.init();
            this.initAnalyticSurfaceLayer();
            this.getWwd().getView().setEyePosition(new Position(new LatLon(Angle.fromDegreesLatitude(Variables.lat1),Angle.fromDegreesLongitude(Variables.lon1)), Variables.initAltitude));
        }

        protected void initAnalyticSurfaceLayer()
        {
            this.FluidDensitySurfaceLayer = new RenderableLayer();
            this.FluidDensitySurfaceLayer.setPickEnabled(false);
            this.FluidDensitySurfaceLayer.setName("Fluid Density");
            insertBeforePlacenames(this.getWwd(), this.FluidDensitySurfaceLayer);
            
            
            this.FluidVelocity_x_SurfaceLayer = new RenderableLayer();
            this.FluidVelocity_x_SurfaceLayer.setPickEnabled(false);
            this.FluidVelocity_x_SurfaceLayer.setName("Fluid X Velocity");
            insertBeforePlacenames(this.getWwd(), this.FluidVelocity_x_SurfaceLayer);
            
            this.FluidVelocity_x_SurfaceLayer.setEnabled(false);
            
            this.FluidVelocity_y_SurfaceLayer = new RenderableLayer();
            this.FluidVelocity_y_SurfaceLayer.setPickEnabled(false);
            this.FluidVelocity_y_SurfaceLayer.setName("Fluid Y Velocity");
            
            this.FluidVelocity_y_SurfaceLayer.setEnabled(false);
            
            insertBeforePlacenames(this.getWwd(), this.FluidVelocity_y_SurfaceLayer);
            
            this.getLayerPanel().update(this.getWwd());

            //createRandomAltitudeSurface(HUE_BLUE, HUE_RED, 40, 40, this.analyticSurfaceLayer);
            createRandomColorSurface( Variables.N, Variables.N, 
            		this.FluidDensitySurfaceLayer,this.FluidVelocity_x_SurfaceLayer,this.FluidVelocity_y_SurfaceLayer);
System.out.println("add MyMouseListener");
       //this.addMouseListener(new MyMouseListener(this.getWwd()));
       this.getWwd().getInputHandler().addMouseListener(new MyMouseListener(this.getWwd()));
       this.getWwd().getInputHandler().addMouseMotionListener(new MyMouseListener(this.getWwd()));
       this.getWwd().getInputHandler().addKeyListener(new MyMouseListener(this.getWwd()));
       
       
       
       this.getWwd().getModel().getLayers().getLayerByName("MS Virtual Earth Aerial").setEnabled(true);
    }

    protected static Renderable createLegendRenderable(final AnalyticSurface surface, final double surfaceMinScreenSize,
        final AnalyticSurfaceLegend legend)
    {
        return new Renderable()
        {
            public void render(DrawContext dc)
            {
                Extent extent = surface.getExtent(dc);
                if (!extent.intersects(dc.getView().getFrustumInModelCoordinates()))
                    return;

                if (WWMath.computeSizeInWindowCoordinates(dc, extent) < surfaceMinScreenSize)
                    return;

                legend.render(dc);
            }
        };
    }

    static public int i =0;
    
    static AnalyticSurface surface_density;
    static AnalyticSurface surface_velocity_x;
    static AnalyticSurface surface_velocity_y;
    static BufferWrapper densityBuffer;
    static BufferWrapper velocity_x_Buffer;
    static BufferWrapper velocity_y_Buffer;
    //static double minValue = -200e3;
    //static double maxValue = 200e3;
    
    
    protected static void createRandomColorSurface( int width, int height,
        RenderableLayer densityLayer,RenderableLayer velocity_x_Layer,RenderableLayer velocity_y_Layer)
    {
       

    	surface_density  = new AnalyticSurface();
        //surface.setSector(Sector.fromDegrees(25, 35, -110, -100));
    	surface_density.setSector(Sector.fromDegrees(
        		Variables.lat1,
        		Variables.lat2,
        		Variables.lon1,
        		Variables.lon2
        		));
    	surface_density.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
    	surface_density.setDimensions(width, height);
    	surface_density.setClientLayer(densityLayer);
    	densityLayer.addRenderable(surface_density);

        
        
        
        surface_velocity_x  = new AnalyticSurface();
        //surface.setSector(Sector.fromDegrees(25, 35, -110, -100));
        surface_velocity_x.setSector(Sector.fromDegrees(
        		Variables.lat1,
        		Variables.lat2,
        		Variables.lon1,
        		Variables.lon2
        		));
        surface_velocity_x.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        surface_velocity_x.setDimensions(width, height);
        surface_velocity_x.setClientLayer(velocity_x_Layer);
        velocity_x_Layer.addRenderable(surface_velocity_x);
        
        
        
        surface_velocity_y  = new AnalyticSurface();
        //surface.setSector(Sector.fromDegrees(25, 35, -110, -100));
        surface_velocity_y.setSector(Sector.fromDegrees(
        		Variables.lat1,
        		Variables.lat2,
        		Variables.lon1,
        		Variables.lon2
        		));
        surface_velocity_y.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        surface_velocity_y.setDimensions(width, height);
        surface_velocity_y.setClientLayer(velocity_y_Layer);
        velocity_y_Layer.addRenderable(surface_velocity_y);
         
        
        Timer timer = new Timer(Variables.timer, new ActionListener()
        {
            protected long startTime = -1;

            public void actionPerformed(ActionEvent e)
            {
                if (this.startTime < 0)
                    this.startTime = System.currentTimeMillis();

           //    fluid.initInputVariables();
              
               //fluid.updateInputVariables(i);
               //i++;
                fluid.simulate();
          
                densityBuffer = fluid.getDensityBufferWrapper();
                surface_density.setValues(createColorGradientGridValues(
                		densityBuffer, Variables.minDensityValue,Variables.maxDensityValue, HUE_BLUE, HUE_RED));

                
                velocity_x_Buffer = fluid.getVelocityXBufferWrapper();
                surface_velocity_x.setValues(createColorGradientGridValues(
                		velocity_x_Buffer, Variables.minVelocityValue,Variables.maxVelocityValue, HUE_BLUE, HUE_RED));

                
                velocity_y_Buffer = fluid.getVelocityYBufferWrapper();
                surface_velocity_y.setValues(createColorGradientGridValues(
                		velocity_y_Buffer, Variables.minVelocityValue,Variables.maxVelocityValue, HUE_BLUE, HUE_RED));
                
                     if (surface_density.getClientLayer() != null)
                    	 surface_density.getClientLayer().firePropertyChange(AVKey.LAYER, null, surface_density.getClientLayer());
            
                   
        if (surface_velocity_x.getClientLayer() != null)
        	surface_velocity_x.getClientLayer().firePropertyChange(AVKey.LAYER, null, surface_density.getClientLayer());

      
      if (surface_velocity_y.getClientLayer() != null)
    	  surface_velocity_y.getClientLayer().firePropertyChange(AVKey.LAYER, null, surface_density.getClientLayer());
}
    });
        timer.start();
        
        
         
        
        AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
        attr.setDrawShadow(false);
        attr.setInteriorOpacity(0.6);
        attr.setOutlineWidth(3);
        surface_density.setSurfaceAttributes(attr);
        surface_velocity_x.setSurfaceAttributes(attr);
        surface_velocity_y.setSurfaceAttributes(attr);
    }

   

    public static Iterable<? extends AnalyticSurface.GridPointAttributes> createColorGradientGridValues(
        BufferWrapper firstBuffer, double minValue, double maxValue,
        double minHue, double maxHue)
    {
        ArrayList<AnalyticSurface.GridPointAttributes> attributesList
            = new ArrayList<AnalyticSurface.GridPointAttributes>();

        
        long length = firstBuffer.length();
        for (int i = 0; i < length; i++)
        {
            double value =  firstBuffer.getDouble(i);
            
           
            
         
            attributesList.add(
                AnalyticSurface.createColorGradientAttributes(value, minValue, maxValue, minHue, maxHue));
        }

        return attributesList;
    }


    }

   

    //**************************************************************//
    //********************  Random Grid Construction  **************//
    //**************************************************************//

    protected static final int DEFAULT_RANDOM_ITERATIONS = 1000;
    protected static final double DEFAULT_RANDOM_SMOOTHING = 0.5d;

    public static BufferWrapper randomGridValues(int width, int height, double min, double max, int numIterations,
        double smoothness, BufferFactory factory)
    {
        int numValues = width * height;
        double[] values = new double[numValues];

        for (int i = 0; i < numIterations; i++)
        {
            double offset = 1d - (i / (double) numIterations);

            int x1 = (int) Math.round(Math.random() * (width - 1));
            int x2 = (int) Math.round(Math.random() * (width - 1));
            int y1 = (int) Math.round(Math.random() * (height - 1));
            int y2 = (int) Math.round(Math.random() * (height - 1));
            int dx1 = x2 - x1;
            int dy1 = y2 - y1;

            for (int y = 0; y < height; y++)
            {
                int dy2 = y - y1;
                for (int x = 0; x < width; x++)
                {
                    int dx2 = x - x1;

                    if ((dx2 * dy1 - dx1 * dy2) >= 0)
                        values[x + y * width] += offset;
                }
            }
        }

        //smoothValues(width, height, values, smoothness);
        scaleValues(values, numValues, min, max);
        BufferWrapper buffer = factory.newBuffer(numValues);
        buffer.putDouble(0, values, 0, numValues);

        return buffer;
    }

    public static BufferWrapper randomGridValues(int width, int height, double min, double max)
    {
        return randomGridValues(width, height, min, max, DEFAULT_RANDOM_ITERATIONS, DEFAULT_RANDOM_SMOOTHING,
            new BufferFactory.DoubleBufferFactory());
    }

    protected static void scaleValues(double[] values, int count, double minValue, double maxValue)
    {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < count; i++)
        {
            if (min > values[i])
                min = values[i];
            if (max < values[i])
                max = values[i];
        }

        for (int i = 0; i < count; i++)
        {
            values[i] = (values[i] - min) / (max - min);
            values[i] = minValue + values[i] * (maxValue - minValue);
        }
    }

    


    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Analytic Surface", AppFrame.class);
    }
}
