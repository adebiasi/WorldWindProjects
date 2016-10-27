package it.graphitech.smeSpire.layers;

import it.graphitech.smeSpire.shapefile.ShapefileLoader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.net.URL;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.util.WWIO;

public class ScaleLayer extends RenderableLayer{
	
	private WorldWindow wwd;
	private ScreenAnnotation annotation;
	private Font font = new Font("SansSerif", Font.PLAIN, 14);
	
	private int IMG_WIDTH = 210;
	private int IMG_HEIGHT = 100;
	
	public ScaleLayer(WorldWindow wwd)
    {
		this.wwd = wwd;
        this.initialize();
    }
	
	protected void initialize()
    {
		String SERVER = "http://95.110.208.97/smespire/applet/img/scale.png";
 
    	URL url = WWIO.makeURL(SERVER);
		
        // Set up screen annotation that will display the layer list
        this.annotation = new ScreenAnnotation("", new Point(IMG_WIDTH/2+5, 5));

        // Set annotation so that it will not force text to wrap (large width) and will adjust it's width to
        // that of the text. A height of zero will have the annotation height follow that of the text too.
        this.annotation.getAttributes().setSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
        this.annotation.getAttributes().setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);

        // Set appearance attributes
        this.annotation.getAttributes().setCornerRadius(5);
        //this.annotation.getAttributes().setFont(this.font);
        //this.annotation.getAttributes().setHighlightScale(1);
        //this.annotation.getAttributes().setTextColor(Color.WHITE);
        this.annotation.getAttributes().setBackgroundColor(new Color(0f, 0f, 0f, 0f));
        //this.annotation.getAttributes().setInsets(new Insets(6, 6, 6, 6));
        this.annotation.getAttributes().setBorderWidth(1);
        this.annotation.getAttributes().setBorderColor(new Color(0.7f, 0.73f, 0f, 1f));
        this.annotation.getAttributes().setImageSource(url);
        this.addRenderable(this.annotation);
        
        insertBeforeCompass(this.wwd, this);
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
}
