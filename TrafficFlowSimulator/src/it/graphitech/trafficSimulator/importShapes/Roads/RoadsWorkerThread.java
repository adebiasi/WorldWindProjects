package it.graphitech.trafficSimulator.importShapes.Roads;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.render.AbstractSurfaceShape;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.util.VecBuffer;
import it.graphitech.trafficSimulator.TrafficSim.AppFrame;


import java.io.File;

import javax.swing.SwingUtilities;

/**
 * it is a thread used to load a shape file that contains the reads and it creates SurfacePolygons for each road
 * @author a.debiasi
 *
 */
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

    }
    
   protected AbstractSurfaceShape makeShape(ShapefileRecord record)
    {
    	
         VecBuffer vb = record.getPointBuffer(0);
        AbstractSurfaceShape pgon = new SurfacePolygon(vb.getLocations());
        
        
        ShapeAttributes normalShapeAttributes = new BasicShapeAttributes();
        normalShapeAttributes.setInteriorMaterial(Material.GRAY);
        normalShapeAttributes.setEnableLighting(false);
        normalShapeAttributes.setEnableAntialiasing(false);
        
        
        normalShapeAttributes.setDrawInterior(true);
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