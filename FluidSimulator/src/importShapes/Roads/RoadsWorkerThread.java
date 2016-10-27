package importShapes.Roads;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.render.AbstractSurfaceShape;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.util.VecBuffer;


import java.awt.Color;
import java.io.File;

import javax.swing.SwingUtilities;

import main.AnalyticSurfaceDemo.AppFrame;

public class RoadsWorkerThread extends Thread
{
    private File file;
    private WorldWindow wwd;
    private AppFrame ts;

    public RoadsWorkerThread(File file, WorldWindow wwd,AppFrame ts)
    {
        this.file = file;
        this.wwd = wwd;
        this.ts=ts;
    }

    public void run()
    {
        Shapefile sf = new Shapefile(this.file);

        final RenderableLayer layer = new RenderableLayer();
        layer.setName("Roads");
        

        try
        {
            while (sf.hasNext())
            {
                ShapefileRecord r = sf.nextRecord();
                if (r == null)
                    continue;

                //printShapefileInfo(r);

                if (r.getNumberOfPoints() < 4)
                    continue;

                layer.addRenderable(this.makeShape(r));
            }
        }
        finally
        {
            sf.close();
        }

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
            	layer.setEnabled(false);
            	layer.setPickEnabled(false);
            	
                insertBeforePlacenames(wwd, layer);
                ts.getLayerPanel().update(wwd);
            }
        });
/*
        this.wwd.addSelectListener(new SelectListener()
        {
            public void selected(SelectEvent event)
            {
                if (event.getTopObject() instanceof ExtrudedPolygon)
                    System.out.println("EXTRUDED POLYGON SELECTED");
            }
        });
        */
    }
    
   // protected String[] heightKeys = new String[] {"height", "Height", "HEIGHT", "elevation", "Elevation", "ELEVATION"};

    protected AbstractSurfaceShape makeShape(ShapefileRecord record)
    {
    	//Material m = new Material(new Color((float)(0.4 + Math.random() * .2f), (float)(0.4 + Math.random() * .2f), (float)(0.4 + Math.random() * .2f)));
        //Double height = null;
        //TODO: Add additional code to read other attributes
        
        /*
        for (String key : heightKeys)
        {
            Object o = record.getAttributes().getValue(key);
            if (o != null)
            {
                height = Double.parseDouble(o.toString());
            }
        }
        */
        //TODO Subclass Extruded polygon to show other attributes or use pgon.setValue(key, value);
        
        //ExtrudedPolygon pgon = new ExtrudedPolygon();
        VecBuffer vb = record.getPointBuffer(0);
        AbstractSurfaceShape pgon = new SurfacePolygon(vb.getLocations());
        
        //pgon.setOuterBoundary(vb.getLocations());
        //pgon.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        
        
        ShapeAttributes normalShapeAttributes = new BasicShapeAttributes();
        normalShapeAttributes.setInteriorMaterial(Material.GRAY);
        normalShapeAttributes.setEnableLighting(false);
        normalShapeAttributes.setEnableAntialiasing(false);
        
        
        normalShapeAttributes.setDrawInterior(true);
       // normalShapeAttributes.setOutlineMaterial(Material.BLACK);
        //((SurfacePolygon)pgon).set
        pgon.setAttributes(normalShapeAttributes);
        
        
        pgon.setHighlighted(false);
        pgon.setEnableBatchPicking(false);
        
        return pgon;
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
}