/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.rendObjects.curve;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.WWDotNetLayerSetConverter;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.LazilyLoadedTexture;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.WWTexture;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.util.measure.LengthMeasurer;
import it.GeneratorOfRenderableObjects;
import it.SharedVariables;
import it.layers.CurvesLayer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.*;
import java.util.List;

/**
 * @author tag
 * @version $Id: Polyline.java 914 2012-11-28 02:21:56Z pabercrombie $
 */
public class CubicSplinePolyline extends AVListImpl 
implements Renderable, 
OrderedRenderable
//Movable, 
//Restorable,
 //   MeasurableLength, 
 //   ExtentHolder
{
	
//	
//	 /** The image source for this shape's texture, if any. */
//    protected Object imageSource; // image source for the optional texture
//    /** If an image source was specified, this is the WWTexture form. */
//    protected WWTexture wwTexture; // an optional texture for the base polygon
//    /** This shape's rotation, in degrees positive counterclockwise. */
//   // protected Double rotation; // in degrees; positive is CCW
//    /** This shape's texture coordinates. */
//    protected FloatBuffer textureCoordsBuffer; // texture coords if texturing

	
  //  protected Texture texture; // an optional texture for the base polygon
	//protected String texturePath = "texture/earth.png";
	//protected String texturePath = "texture/5percent_lineegrandi.png";
	
	//double lineWidth = 1;
	
	public String from;
	public String to;
	
//	protected int numSubsegments = 10;
    protected boolean followTerrain = false;
	protected boolean computeOffest= true;
	
	//boolean isAffectedByLense = false;
	//boolean isNodeInsideLense = false;
	boolean isImportant = false;
	//boolean wasAffectedByLense = false;
	
	//Vec4 originalControlPoint=null;
//	Position originalControlPosition=null;
	
    public final static int GREAT_CIRCLE = WorldWind.GREAT_CIRCLE;
    public final static int LINEAR = WorldWind.LINEAR;
    public final static int RHUMB_LINE = WorldWind.RHUMB_LINE;
    public final static int LOXODROME = RHUMB_LINE;

    public final static int ANTIALIAS_DONT_CARE = WorldWind.ANTIALIAS_DONT_CARE;
    public final static int ANTIALIAS_FASTEST = WorldWind.ANTIALIAS_FASTEST;
    public final static int ANTIALIAS_NICEST = WorldWind.ANTIALIAS_NICEST;

    protected Color color = Color.WHITE;
    public Color pickColor = new Color(0);
    private RenderableControlPoints renderableControlPoints;
    private RenderableControlPoints updRenderableControlPoints;
   //protected ArrayList<Position> positions;
   
    
    
    //protected Vec4 referenceCenterPoint;
    
    
    
    
    protected int antiAliasHint = GL.GL_FASTEST;
    //protected Color color = Color.WHITE;
    protected double lineWidth = 1;
    protected boolean filled = false; // makes it a polygon
  

    protected double offset = 0;
   protected double terrainConformance = 10;
    protected int pathType = GREAT_CIRCLE;
    
    protected short stipplePattern = (short) 0xAAAA;
    protected int stippleFactor = 0;
    
    protected boolean highlighted = false;
    protected Color highlightColor = new Color(1f, 1f, 1f, 0.5f);
    protected Object delegateOwner;
    protected LengthMeasurer measurer = new LengthMeasurer();
    protected long geomGenTimeStamp = -Long.MAX_VALUE;
    protected double geomGenVE = 1;
    protected double eyeDistance;


  //  protected PickSupport pickSupport = new PickSupport();
 
    
    boolean  originalVerticesCalculated = false;
    
    
    protected long frameNumber = -1; // identifies frame used to calculate these values
    protected Layer pickLayer;

    
    int index;
    protected CurvesLayer layer;
    
    
    protected List<List<Vec4>> currentSpans;
    protected Cubic[] cubicCurveFunctionsX;
    protected Cubic[] cubicCurveFunctionsY;
    protected Cubic[] cubicCurveFunctionsZ;
    //protected DoubleBuffer bufferPoints;
    
    //protected FloatBuffer bufferPoints;
    //protected int bufferPointSize;
    
    protected List<List<Vec4>> currentUpdatedSpans;
    protected Cubic[] updCubicCurveFunctionsX;
    protected Cubic[] updCubicCurveFunctionsY;
    protected Cubic[] updCubicCurveFunctionsZ;
    
   // protected DoubleBuffer bufferUpdatedPoints;
    protected FloatBuffer bufferUpdatedPoints;
    protected int bufferUpdPointSize;
   
    
    
    
    // protected List<Double> magnitude;
   // protected List<Color> colors;
    // Manage an extent for each globe the polyline's associated with.

    protected static class ExtentInfo
    {
        // The extent depends on the state of the globe used to compute it, and the vertical exaggeration.
        protected Extent extent;
        protected double verticalExaggeration;
        protected Globe globe;
        protected Object globeStateKey;

        public ExtentInfo(Extent extent, DrawContext dc)
        {
            this.extent = extent;
            this.verticalExaggeration = dc.getVerticalExaggeration();
            this.globe = dc.getGlobe();
            this.globeStateKey = dc.getGlobe().getStateKey(dc);
        }

        protected boolean isValid(DrawContext dc)
        {
            return this.verticalExaggeration == dc.getVerticalExaggeration() && this.globe == dc.getGlobe()
                && globeStateKey.equals(dc.getGlobe().getStateKey(dc));
        }
    }

    protected HashMap<Globe, ExtentInfo> extents = new HashMap<Globe, ExtentInfo>(2); // usually only 1, but few at most

    
    
    public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getIdOrigin() {
		return from;
	}

	public void setIdOrigin(String from) {
		this.from = from;
	}

	public String getIdDest() {
		return to;
	}

	public void setIdDest(String to) {
		this.to = to;
	}

	public CubicSplinePolyline(RenderableControlPoints renderableControlPoints, CurvesLayer layer) {
    	
    	this.measurer.setFollowTerrain(this.followTerrain);
        this.measurer.setPathType(this.pathType);
		this.renderableControlPoints = renderableControlPoints;
		
		this.layer=layer;
		this.updRenderableControlPoints =  new RenderableControlPoints();
		/*
		this.updRenderableControlPoints.positions= new ArrayList<Position>();
		this.updRenderableControlPoints.positions.addAll((ArrayList<Position>)renderableControlPoints.positions.clone());
		*/
		this.updRenderableControlPoints.setControlPointPosition((ArrayList<Position>)renderableControlPoints.getPositions().clone());
		this.updRenderableControlPoints.setControlPoint((ArrayList<Vec4>)renderableControlPoints.points.clone());
	//	originalControlPosition=this.renderableControlPoints.positions.get(1);
		
		
	}

    private void reset()
    {   	
    	
        if (this.currentSpans != null)
            this.currentSpans.clear();        
        
        this.currentSpans = null;
   
        if (this.currentUpdatedSpans != null)
            this.currentUpdatedSpans.clear();        
        
        this.currentUpdatedSpans = null;
        
    }

  
    public int getAntiAliasHint()
    {
        return antiAliasHint;
    }

    public void setAntiAliasHint(int hint)
    {
        if (!(hint == ANTIALIAS_DONT_CARE || hint == ANTIALIAS_FASTEST || hint == ANTIALIAS_NICEST))
        {
            String msg = Logging.getMessage("generic.InvalidHint");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.antiAliasHint = hint;
    }

    public boolean isFilled()
    {
        return filled;
    }

    public void setFilled(boolean filled)
    {
        this.filled = filled;
    }

    public int getPathType()
    {
        return pathType;
    }

    public String getPathTypeString()
    {
        return this.getPathType() == GREAT_CIRCLE ? AVKey.GREAT_CIRCLE
            : this.getPathType() == RHUMB_LINE ? AVKey.RHUMB_LINE : AVKey.LINEAR;
    }

    /**
     * Sets the type of path to draw, one of {@link #GREAT_CIRCLE}, which draws each segment of the path as a great
     * circle, {@link #LINEAR}, which determines the intermediate positions between segments by interpolating the
     * segment endpoints, or {@link #RHUMB_LINE}, which draws each segment of the path as a line of constant heading.
     *
     * @param pathType the type of path to draw.
     *
     * @see <a href="{@docRoot}/overview-summary.html#path-types">Path Types</a>
     */
    public void setPathType(int pathType)
    {
        this.reset();
        this.pathType = pathType;
        this.measurer.setPathType(pathType);
    }

    /**
     * Sets the type of path to draw, one of {@link AVKey#GREAT_CIRCLE}, which draws each segment of the path as a great
     * circle, {@link AVKey#LINEAR}, which determines the intermediate positions between segments by interpolating the
     * segment endpoints, or {@link AVKey#RHUMB_LINE}, which draws each segment of the path as a line of constant
     * heading.
     *
     * @param pathType the type of path to draw.
     *
     * @see <a href="{@docRoot}/overview-summary.html#path-types">Path Types</a>
     */
    public void setPathType(String pathType)
    {
        if (pathType == null)
        {
            String msg = Logging.getMessage("nullValue.PathTypeIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.setPathType(pathType.equals(AVKey.GREAT_CIRCLE) ? GREAT_CIRCLE
            : pathType.equals(AVKey.RHUMB_LINE) || pathType.equals(AVKey.LOXODROME) ? RHUMB_LINE : LINEAR);
    }

    public boolean isFollowTerrain()
    {
        return followTerrain;
    }

    /**
     * Indicates whether the path should follow the terrain's surface. If the value is <code>true</code>, the elevation
     * values in this path's positions are ignored and the path is drawn on the terrain surface. Otherwise the path is
     * drawn according to the elevations given in the path's positions. If following the terrain, the path may also have
     * an offset. See {@link #setOffset(double)};
     *
     * @param followTerrain <code>true</code> to follow the terrain, otherwise <code>false</code>.
     */
    public void setFollowTerrain(boolean followTerrain)
    {
    	
        this.reset();
        this.followTerrain = followTerrain;
        this.measurer.setFollowTerrain(followTerrain);
        this.extents.clear();
    }

    public double getOffset()
    {
        return offset;
    }

    /**
     * Specifies an offset, in meters, to add to the path points when the path's follow-terrain attribute is true. See
     * {@link #setFollowTerrain(boolean)}.
     *
     * @param offset the path pffset in meters.
     */
    public void setOffset(double offset)
    {
    
        this.reset();
        this.offset = offset;
        this.extents.clear();
    }

    public double getTerrainConformance()
    {
        return terrainConformance;
    }

    /**
     * Specifies the precision to which the path follows the terrain when the follow-terrain attribute is true. The
     * conformance value indicates the approximate length of each sub-segment of the path as it's drawn, in pixels.
     * Lower values specify higher precision, but at the cost of performance.
     *
     * @param terrainConformance the path conformance in pixels.
     */
  
    
    public void setTerrainConformance(double terrainConformance)
    {
        this.terrainConformance = terrainConformance;
    }

    public double getLineWidth()
    {
        return lineWidth;
    }

    public void setLineWidth(double lineWidth)
    {
        this.lineWidth = lineWidth;
    }

    /**
     * Returns the length of the line as drawn. If the path follows the terrain, the length returned is the distance one
     * would travel if on the surface. If the path does not follow the terrain, the length returned is the distance
     * along the full length of the path at the path's elevations and current path type.
     *
     * @return the path's length in meters.
     */
    public double getLength()
    {
        Iterator<ExtentInfo> infos = this.extents.values().iterator();
        return infos.hasNext() ? this.measurer.getLength(infos.next().globe) : 0;
    }

    public double getLength(Globe globe)
    {
        // The length measurer will throw an exception and log the error if globe is null
        return this.measurer.getLength(globe);
    }

    public LengthMeasurer getMeasurer()
    {
        return this.measurer;
    }

    public short getStipplePattern()
    {
        return stipplePattern;
    }

    /**
     * Sets the stipple pattern for specifying line types other than solid. See the OpenGL specification or programming
     * guides for a description of this parameter. Stipple is also affected by the path's stipple factor, {@link
     * #setStippleFactor(int)}.
     *
     * @param stipplePattern the stipple pattern.
     */
    public void setStipplePattern(short stipplePattern)
    {
        this.stipplePattern = stipplePattern;
    }

    public int getStippleFactor()
    {
        return stippleFactor;
    }

    /**
     * Sets the stipple factor for specifying line types other than solid. See the OpenGL specification or programming
     * guides for a description of this parameter. Stipple is also affected by the path's stipple pattern, {@link
     * #setStipplePattern(short)}.
     *
     * @param stippleFactor the stipple factor.
     */
    public void setStippleFactor(int stippleFactor)
    {
        this.stippleFactor = stippleFactor;
    }

  

    /**
     * Specifies the number of intermediate segments to draw for each segment between positions. The end points of the
     * intermediate segments are calculated according to the current path type and follow-terrain setting.
     *
     * @param numSubsegments the number of intermediate subsegments.
     */
    public void setNumSubsegments(int numSubsegments)
    {
    	
        this.reset();
        SharedVariables.numSubsegments = numSubsegments;
    }

    public boolean isHighlighted()
    {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted)
    {
        this.highlighted = highlighted;
    }

    public Color getHighlightColor()
    {
        return this.highlightColor;
    }

    public void setHighlightColor(Color highlightColor)
    {
        if (highlightColor == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        this.highlightColor = highlightColor;
    }

    /**
     * Specifies the path's positions.
     *
     * @param inPositions the path positions.
     */
   
    
    /*
    public RenderableControlPoints getRenderableControlPoints() {
		return renderableControlPoints;
	}
*/
    
    /*
	public void setRenderableControlPoints(
			RenderableControlPoints renderableControlPoints) {
		
		this.reset();
		this.renderableControlPoints = renderableControlPoints;
		 this.extents.clear();
	}
*/
	/**
     * Sets the paths positions as latitude and longitude values at a constant altitude.
     *
     * @param inPositions the latitudes and longitudes of the positions.
     * @param altitude    the elevation to assign each position.
     */


    /**
     * Returns the delegate owner of this Polyline. If non-null, the returned object replaces the Polyline as the
     * pickable object returned during picking. If null, the Polyline itself is the pickable object returned during
     * picking.
     *
     * @return the object used as the pickable object returned during picking, or null to indicate that the Polyline is
     *         returned during picking.
     */
    public Object getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /**
     * Specifies the delegate owner of this Polyline. If non-null, the delegate owner replaces the Polyline as the
     * pickable object returned during picking. If null, the Polyline itself is the pickable object returned during
     * picking.
     *
     * @param owner the object to use as the pickable object returned during picking, or null to return the Polyline.
     */
    public void setDelegateOwner(Object owner)
    {
        this.delegateOwner = owner;
    }

    /**
     * Returns this Polyline's enclosing volume as an {@link gov.nasa.worldwind.geom.Extent} in model coordinates, given
     * a specified {@link gov.nasa.worldwind.globes.Globe} and vertical exaggeration (see {@link
     * gov.nasa.worldwind.SceneController#getVerticalExaggeration()}.
     *
     * @param globe                the Globe this Polyline is related to.
     * @param verticalExaggeration the vertical exaggeration to apply.
     *
     * @return this Polyline's Extent in model coordinates.
     *
     * @throws IllegalArgumentException if the Globe is null.
     */
    public Extent getExtent(Globe globe, double verticalExaggeration)
    {
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.computeExtent(globe, verticalExaggeration);
    }

    /**
     * Returns this Polyline's enclosing volume as an {@link gov.nasa.worldwind.geom.Extent} in model coordinates, given
     * a specified {@link gov.nasa.worldwind.render.DrawContext}. The returned Extent may be different than the Extent
     * returned by calling {@link #getExtent(gov.nasa.worldwind.globes.Globe, double)} with the DrawContext's Globe and
     * vertical exaggeration. Additionally, this may cache the computed extent and is therefore potentially faster than
     * calling {@link #getExtent(gov.nasa.worldwind.globes.Globe, double)}.
     *
     * @param dc the current DrawContext.
     *
     * @return this Polyline's Extent in model coordinates.
     *
     * @throws IllegalArgumentException if the DrawContext is null, or if the Globe held by the DrawContext is null.
     */
    public Extent getExtent(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (dc.getGlobe() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        ExtentInfo extentInfo = this.extents.get(dc.getGlobe());
        if (extentInfo != null && extentInfo.isValid(dc))
        {
            return extentInfo.extent;
        }
        else
        {
            extentInfo = new ExtentInfo(this.computeExtent(dc), dc);
            this.extents.put(dc.getGlobe(), extentInfo);
            return extentInfo.extent;
        }
    }

    protected Extent computeExtent(Globe globe, double verticalExaggeration)
    {
        //Sector sector = Sector.boundingSector(this.getPositions());
    	Sector sector = Sector.boundingSector(renderableControlPoints.getPositions());
    	
        double[] minAndMaxElevations;
        if (this.isFollowTerrain())
        {
            minAndMaxElevations = globe.getMinAndMaxElevations(sector);
        }
        else
        {
            //minAndMaxElevations = computeElevationExtremes(this.getPositions());
        	minAndMaxElevations = computeElevationExtremes(renderableControlPoints.getPositions());
        }
        minAndMaxElevations[0] += this.getOffset();
        minAndMaxElevations[1] += this.getOffset();

        return Sector.computeBoundingBox(globe, verticalExaggeration, sector, minAndMaxElevations[0],
            minAndMaxElevations[1]);
    }

    protected Extent computeExtent(DrawContext dc)
    {
        return this.computeExtent(dc.getGlobe(), dc.getVerticalExaggeration());
    }

    protected static double[] computeElevationExtremes(Iterable<? extends Position> positions)
    {
        double[] extremes = new double[] {Double.MAX_VALUE, -Double.MAX_VALUE};
        for (Position pos : positions)
        {
            if (extremes[0] > pos.getElevation())
                extremes[0] = pos.getElevation(); // min
            if (extremes[1] < pos.getElevation())
                extremes[1] = pos.getElevation(); // max
        }

        return extremes;
    }

    public double getDistanceFromEye()
    {
        return this.eyeDistance;
    }

    
    
    
    public void pick(DrawContext dc, Point pickPoint)
    {
        // This method is called only when ordered renderables are being drawn.
        // Arg checked within call to render.
       
    	/*
    	this.pickSupport.clearPickList();
        try
        {
            this.pickSupport.beginPicking(dc);
            this.render(dc);
        }
        finally
        {
            this.pickSupport.endPicking(dc);
            this.pickSupport.resolvePick(dc, pickPoint, this.pickLayer);
        }
        */
    }

    
    
    
    
    public void render(DrawContext dc)
    {
        // This render method is called three times during frame generation. It's first called as a {@link Renderable}
        // during <code>Renderable</code> picking. It's called again during normal rendering. And it's called a third
        // time as an OrderedRenderable. The first two calls determine whether to add the polyline to the ordered
        // renderable list during pick and render. The third call just draws the ordered renderable.
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (dc.getSurfaceGeometry() == null)
            return;

        this.draw(dc);
    }
    /**
     * Specifies the texture to apply to this polygon.
     *
     * @param imageSource   the texture image source. May be a {@link String} identifying a file path or URL, a {@link
     *                      File}, or a {@link java.net.URL}.
     * @param texCoords     the (s, t) texture coordinates aligning the image to the polygon. There must be one texture
     *                      coordinate pair, (s, t), for each polygon location in the polygon's outer boundary.
     * @param texCoordCount the number of texture coordinates, (s, v) pairs, specified.
     *
     * @throws IllegalArgumentException if the image source is not null and either the texture coordinates are null or
     *                                  inconsistent with the specified texture-coordinate count, or there are fewer
     *                                  than three texture coordinate pairs.
     */
  /*
    protected WWTexture makeTexture(Object imageSource)
    {
        return new LazilyLoadedTexture(imageSource, true);
    }
    */
  
    
    
    
	
	
	
	
	private void resetOriginalControlPoint(){
		//updRenderableControlPoints.positions.set(1, originalControlPosition);
		
		//this.updRenderableControlPoints.positions.clear();
		//this.updRenderableControlPoints.positions.addAll((ArrayList<Position>)renderableControlPoints.positions.clone());
		//this.updRenderableControlPoints.setControlPoint((ArrayList<Position>)renderableControlPoints.getPositions().clone());
		
		this.updRenderableControlPoints.setControlPointPosition((ArrayList<Position>)renderableControlPoints.getPositions().clone());
		this.updRenderableControlPoints.setControlPoint((ArrayList<Vec4>)renderableControlPoints.points.clone());
	}
	
    public Position getOrigin(){
    	//return renderableControlPoints.positions.get(0);
    	//return renderableControlPoints.getOrigin();
    	
    	return layer.getOriginalPositionNode(from);
    	
    }
    
    public Position getDestination(){
    	//return renderableControlPoints.positions.get(renderableControlPoints.positions.size()-1);
    	//return renderableControlPoints.getDestination();
    	return layer.getOriginalPositionNode(to);
    }
    
    /*
    private boolean inBoundary(DrawContext dc){
    	Position pos = renderableControlPoints.positions.get(1);
    	 Vec4 po = dc.getGlobe().computePointFromPosition(pos);
		 Vec4 currentScreenCoordinates = dc.getView().project(po);
    	return (currentScreenCoordinates.distanceTo2(SharedVariables.screenPoint)>(SharedVariables.lense_h/2-10));
    }
    */
   
    
    


    /**
     * If the scene controller is rendering ordered renderables, this method draws this placemark's image as an ordered
     * renderable. Otherwise the method determines whether this instance should be added to the ordered renderable
     * list.
     * <p/>
     * The Cartesian and screen points of the placemark are computed during the first call per frame and re-used in
     * subsequent calls of that frame.
     *
     * @param dc the current draw context.
     */
    protected void draw(DrawContext dc)
    {
    	
    	
    	boolean isDestOverDeformedSurface = layer.isDeformedNode(to);
    	boolean isOriginOverDeformedSurface = layer.isDeformedNode(from);
    	
    	boolean isDestVisible = layer.isVisibleNode(to);
    	boolean isOriginOVisible = layer.isDeformedNode(from);
    	
    	//System.out.println("---> "+isDestVisible+" - "+isOriginOVisible);
    	
        if (dc.isOrderedRenderingMode())
        {
        
        	// movePoints(dc);
        	 if(isDestOverDeformedSurface||isOriginOverDeformedSurface){
              this.makeUpdatedVertices(dc);
             }
        	 
        	 if(isDestVisible&isOriginOVisible){
        	 dc.getGL().glDisable(GL.GL_DEPTH_TEST);
        	 }
           // this.drawNewOrderedRenderable(dc);
        	 this.drawOldOrderedRenderable(dc);
        	 if(isDestVisible&isOriginOVisible){
        	 dc.getGL().glEnable(GL.GL_DEPTH_TEST);
        	 }
            return;
        }

      
        // The rest of the code in this method determines whether to queue an ordered renderable for the polyline.
/*
        if (this.positions.size() < 2)
            return;
*/
        if (renderableControlPoints.getPositions().size() < 2)
            return;
        
        
        // vertices potentially computed every frame to follow terrain changes
        if (this.currentSpans == null || (this.followTerrain && this.geomGenTimeStamp != dc.getFrameTimeStamp())
            || this.geomGenVE != dc.getVerticalExaggeration())
        {
            // Reference center must be computed prior to computing vertices.
            this.computeReferenceCenter(dc);
            //this.eyeDistance = this.referenceCenterPoint.distanceTo3(dc.getView().getEyePoint());
            
            /////////////////////////////////////////////////////////////////this.eyeDistance =-renderableControlPoints.getMagnitude();
            
            
          
            //System.out.println("this.eyeDistance: "+this.eyeDistance);
            //System.out.println("in draw");
           
            
            if(originalVerticesCalculated==false){
            	//System.out.println("originalVerticesCalculated");
            this.makeOriginalVertices(dc);
            originalVerticesCalculated=true;
            }
            
            this.geomGenTimeStamp = dc.getFrameTimeStamp();
            this.geomGenVE = dc.getVerticalExaggeration();
        
            //this.eyeDistance = this.referenceCenterPoint.distanceTo3(dc.getView().getEyePoint());
            this.eyeDistance = 0;
        }

        if (this.currentSpans == null || this.currentSpans.size() < 1)
            return;

        if (this.intersectsFrustum(dc))
        {
            if (dc.isPickingMode())
                this.pickLayer = dc.getCurrentLayer();

            dc.addOrderedRenderable(this); // add the ordered renderable
        }
    }

    
    
    
    public void bubbleSort(List<Double> magnitude,List<Color> colors,List<List<Vec4>> currentSpans) {
        boolean swapped = true;
        int j = 0;
        
        double tmpMagn;
        Color tmpCol;
        List<Vec4> tmpSpan;
        
        while (swapped) {
            swapped = false;
            j++;
            for (int i = 0; i < magnitude.size() - j; i++) {
                if (magnitude.get(i) > magnitude.get(i+1)) {
                    
                	tmpMagn = magnitude.get(i);
                    magnitude.set(i,magnitude.get(i+1));
                    magnitude.set(i+1, tmpMagn);
                    
                    tmpCol = colors.get(i);
                    colors.set(i,colors.get(i+1));
                    colors.set(i+1, tmpCol);
                    
                    tmpSpan = currentSpans.get(i);
                    currentSpans.set(i,currentSpans.get(i+1));
                    currentSpans.set(i+1, tmpSpan);
                    
                    swapped = true;
                }
            }
        }
    }
    
    /*
    public void drawNewOrderedRenderable(DrawContext dc)
    {
        GL gl = dc.getGL();

        int attrBits = GL.GL_HINT_BIT | GL.GL_CURRENT_BIT | GL.GL_LINE_BIT ;
        if (!dc.isPickingMode())
        {
            if (this.color.getAlpha() != 255)
                attrBits |= GL.GL_COLOR_BUFFER_BIT;
        }
      gl.glPushAttrib(attrBits);
      dc.getView().pushReferenceCenter(dc, this.referenceCenterPoint);
    
        boolean projectionOffsetPushed = false; // keep track for error recovery

      //  System.out.println("QAQQQQQq");
        
        try
        {
            if (!dc.isPickingMode())
            {
        //    	System.out.println("non picking mode");
                if (this.color.getAlpha() != 255)
                {
                    gl.glEnable(GL.GL_BLEND);
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                }
                dc.getGL().glColor4ub((byte) this.color.getRed(), (byte) this.color.getGreen(),
                    (byte) this.color.getBlue(), (byte) this.color.getAlpha());
            }
            else
            {
              	Color pickColor = this.pickColor;
                 gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
              //  System.out.println("pick color to render (rgb): "+pickColor.getRGB());
            }

          
           // int primType = GL.GL_LINE_STRIP;
            
            
            if (dc.isPickingMode()){
            	 //  gl.glLineWidth((float) this.lineWidth + 8);
            	  gl.glLineWidth((float) this.lineWidth);
            }             
            else{
                //gl.glLineWidth((float) this.lineWidth);
                gl.glLineWidth((float) this.lineWidth);
            }
           
          
            
            if (this.followTerrain)
            {
                dc.pushProjectionOffest(0.99);
                projectionOffsetPushed = true;
            }

            
            
            
            if (!dc.isPickingMode()){
            //	dc.getGL().glDepthMask (true) ;
            //	dc.getGL().glClear(dc.getGL().GL_DEPTH_BUFFER_BIT);
            	
            	
            	if(isAffectedByLense()){
            	
            	
            if (this.currentUpdatedSpans == null)
          //  	  if (this.currentSpans == null)
                return;

            
            
          
            
            // bufferUpdPointSize = (updRenderableControlPoints.getSize()-1)*(numSubsegments+2); 
            gl.glEnableClientState( GL.GL_VERTEX_ARRAY );
           // gl.glVertexPointer( 3, GL.GL_DOUBLE, 0, bufferUpdatedPoints );
            gl.glVertexPointer( 3, GL.GL_FLOAT, 0, bufferUpdatedPoints );
           
            gl.glDrawArrays(GL.GL_LINE_STRIP, 0, bufferUpdPointSize-1); //Draw the vertices, once again how does this know which vertices to draw? (Does it always use the ones in GL_ARRAY_BUFFER)
            gl.glDisableClientState( GL.GL_VERTEX_ARRAY );
                
            	}else{
            		 if (this.currentSpans == null)
                         return;

            		 
            		 
            		
            		   //int bufferSize = (renderableControlPoints.getSize()-1)*(numSubsegments+2); 
                       gl.glEnableClientState( GL.GL_VERTEX_ARRAY );
                       
                       gl.glBindBuffer(GL.GL_ARRAY_BUFFER, layer.getBufferElement(index));
                       //gl.glVertexPointer(3, GL.GL_DOUBLE, 0, 0l);
                       gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0l);
                       //gl.glVertexPointer( 3, GL.GL_DOUBLE, 0, bufferPoints );
                       gl.glDrawArrays(GL.GL_LINE_STRIP, 0, bufferPointSize-1); 
                       gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);                       
                       gl.glDisableClientState( GL.GL_VERTEX_ARRAY );
                       
             	}
            
        }else{
        	
        	//DEPTH BUFFER
        	//dc.getGL().glDepthMask (true) ;
        	
        	//dc.getGL().glClear(GL.GL_DEPTH_BUFFER_BIT);
        	 if (this.currentSpans == null)
                 return;

             //int bufferSize = (updRenderableControlPoints.getSize()-1)*(numSubsegments+2); 
             gl.glEnableClientState( GL.GL_VERTEX_ARRAY );
            
             gl.glBindBuffer(GL.GL_ARRAY_BUFFER, layer.getBufferElement(index));
            // gl.glVertexPointer(3, GL.GL_DOUBLE, 0, 0l);
             gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0l);
           
            //  gl.glVertexPointer( 3, GL.GL_DOUBLE, 0, bufferPoints );
            
             
             gl.glDrawArrays(GL.GL_LINE_STRIP, 0, bufferPointSize-1); 
             gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
             gl.glDisableClientState( GL.GL_VERTEX_ARRAY );
             
        }
            
            
         
        }
        finally
        {
            if (projectionOffsetPushed)
                dc.popProjectionOffest();

            gl.glPopAttrib();
           dc.getView().popReferenceCenter(dc);
            
       //     isAffectedByLense=false;
            
        }
    }
   
  
    
    */
    
    
    
    public void drawOldOrderedRenderable(DrawContext dc)
    {
    	
    	boolean isDestOverDeformedSurface = layer.isDeformedNode(to);
    	boolean isOriginOverDeformedSurface = layer.isDeformedNode(from);
    	
    	Color currColor = SharedVariables.unselectedLineColor;
    	double lineWidth = 1;
    	
    	if(isImportant){
    		currColor=SharedVariables.lineColor;
    		lineWidth=SharedVariables.lineOfInterestWidth;
    		//currColor=Color.RED;
    		//currColor=Color.WHITE;
    	}else{
    		//currColor=new Color(255, 255, 255, 150);
    		currColor=SharedVariables.unselectedLineColor;
    		lineWidth=SharedVariables.lineWidth;
    	}
     	
        GL2 gl = dc.getGL().getGL2();

        int attrBits = GL2.GL_HINT_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT ;
        if (!dc.isPickingMode())
        {
            if (currColor.getAlpha() != 255)
                attrBits |= GL.GL_COLOR_BUFFER_BIT;
        }
    gl.glPushAttrib(attrBits);
    
       
   //  dc.getView().pushReferenceCenter(dc, this.referenceCenterPoint);

       
        
        boolean projectionOffsetPushed = false; // keep track for error recovery

      //  System.out.println("QAQQQQQq");
        
        try
        {
            if (!dc.isPickingMode())
            {
        //    	System.out.println("non picking mode");
                if (currColor.getAlpha() != 255)
                {
                    gl.glEnable(GL.GL_BLEND);
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                }
                dc.getGL().getGL2().glColor4ub((byte) currColor.getRed(), (byte) currColor.getGreen(),
                    (byte) currColor.getBlue(), (byte) currColor.getAlpha());
            }
            else
            {
            	
                // We cannot depend on the layer to set a pick color for us because this Polyline is picked during ordered
                // rendering. Therefore we set the pick color ourselves.
                //Color pickColor = dc.getUniquePickColor();
            	Color pickColor = this.pickColor;
            	
                //Object userObject = this.getDelegateOwner() != null ? this.getDelegateOwner() : this;
                //System.out.println("add pickable object");
               // this.pickSupport.addPickableObject(pickColor.getRGB(), userObject, null);
                gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
              //  System.out.println("pick color to render (rgb): "+pickColor.getRGB());
            }

    
            int primType = GL.GL_LINE_STRIP;
            
            
            if (dc.isPickingMode()){
            	 //  gl.glLineWidth((float) this.lineWidth + 8);
            	  gl.glLineWidth((float) lineWidth);
            }             
            else{
                //gl.glLineWidth((float) this.lineWidth);
                gl.glLineWidth((float) lineWidth);
            }
           
        
            
            if (this.followTerrain)
            {
                dc.pushProjectionOffest(0.99);
                projectionOffsetPushed = true;
            }

            
            
            
            if (!dc.isPickingMode()){
            //	dc.getGL().glDepthMask (true) ;
            //	dc.getGL().glClear(dc.getGL().GL_DEPTH_BUFFER_BIT);
            	
            	
            	if(isDestOverDeformedSurface||isOriginOverDeformedSurface){
            	
            
            		
            if (this.currentUpdatedSpans == null)
          //  	  if (this.currentSpans == null)
                return;

         
            
            gl.glBegin(primType);
            for (List<Vec4> span : this.currentUpdatedSpans)
            {
                if (span == null)
                    continue;
          for (Vec4 p : span)
                {
                	
                    gl.glVertex3d(p.x, p.y, p.z);
                  
                }
              
            }

            gl.glEnd(); 
           
             
            	}else{
            		 if (this.currentSpans == null)
                         return;
  		 
                     for (List<Vec4> span : this.currentSpans)
                     {
                         if (span == null)
                             continue;
                          gl.glBegin(primType);
                         for (Vec4 p : span)
                         {
                             gl.glVertex3d(p.x, p.y, p.z);
                         }
                         gl.glEnd();
                     }
                     
                     
                  
            	}
            
        }else{
        	
        	//DEPTH BUFFER
        	//dc.getGL().glDepthMask (true) ;      	
        	//dc.getGL().glClear(GL.GL_DEPTH_BUFFER_BIT);
        	 if (this.currentSpans == null)
                 return;

        	 
        	
             for (List<Vec4> span : this.currentSpans)
             {
                 if (span == null)
                     continue;

                    gl.glBegin(primType);
                 for (Vec4 p : span)
                 {
                     gl.glVertex3d(p.x, p.y, p.z);
                 }
                 gl.glEnd();
             }
           
             
        
             
        }
            
          
        }
        finally
        {
            if (projectionOffsetPushed)
                dc.popProjectionOffest();

            gl.glPopAttrib();
     //      dc.getView().popReferenceCenter(dc);
            
       //     isAffectedByLense=false;
            
        }
    }
    
    	
      
    /**
     * Indicates whether the shape is visible in the current view.
     *
     * @param dc the draw context.
     *
     * @return true if the shape is visible, otherwise false.
     */
    protected boolean intersectsFrustum(DrawContext dc)
    {
        Extent extent = this.getExtent(dc);
        if (extent == null)
            return true; // don't know the visibility, shape hasn't been computed yet

        if (dc.isPickingMode())
            return dc.getPickFrustums().intersectsAny(extent);

        return dc.getView().getFrustumInModelCoordinates().intersects(extent);
    }

    //private Cubic[] createCubicCurveFunctions(int n, int[] x)
    private Cubic[] createCubicCurveFunctions(int n, double[] x)
    {
    	/* calculates the natural cubic spline that interpolates
    	y[0], y[1], ... y[n]
    	The first segment is returned as
    	C[0].a + C[0].b*u + C[0].c*u^2 + C[0].d*u^3 0<=u <1
    	the other segments are in C[1], C[2], ...  C[n-1] */
   	
    	    double[] gamma = new double[n+1];
    	    double[] delta = new double[n+1];
    	    double[] D = new double[n+1];
    	    int i;
    	    /* We solve the equation
    	       [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
    	       |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
    	       |  1 4 1   | | .  | = |      .         |
    	       |    ..... | | .  |   |      .         |
    	       |     1 4 1| | .  |   |3(x[n] - x[n-2])|
    	       [       1 2] [D[n]]   [3(x[n] - x[n-1])]
    	       
    	       by using row operations to convert the matrix to upper triangular
    	       and then back sustitution.  The D[i] are the derivatives at the knots.
    	       */
    	    
    	    gamma[0] = 1.0/2.0;
    	    for ( i = 1; i < n; i++) {
    	      gamma[i] = 1/(4-gamma[i-1]);
    	    }
    	    gamma[n] = 1/(2-gamma[n-1]);
    	    
    	    delta[0] = 3*(x[1]-x[0])*gamma[0];
    	    for ( i = 1; i < n; i++) {
    	      delta[i] = (3*(x[i+1]-x[i-1])-delta[i-1])*gamma[i];
    	    }
    	    delta[n] = (3*(x[n]-x[n-1])-delta[n-1])*gamma[n];
    	    
    	    D[n] = delta[n];
    	    for ( i = n-1; i >= 0; i--) {
    	      D[i] = delta[i] - gamma[i]*D[i+1];
    	    }

    	    /* now compute the coefficients of the cubics */
    	    Cubic[] C = new Cubic[n];
    	    for ( i = 0; i < n; i++) {
    	      C[i] = new Cubic((double)x[i], D[i], 3*(x[i+1] - x[i]) - 2*D[i] - D[i+1],
    			       2*(x[i] - x[i+1]) + D[i] + D[i+1]);
    	    }
    	    return C;
    	  }

    
    private Cubic[] createCubicCurveFunctions(int n, double[] x,double[] startX)
    {
    	/* calculates the natural cubic spline that interpolates
    	y[0], y[1], ... y[n]
    	The first segment is returned as
    	C[0].a + C[0].b*u + C[0].c*u^2 + C[0].d*u^3 0<=u <1
    	the other segments are in C[1], C[2], ...  C[n-1] */
   	
    	    double[] gamma = new double[n+1];
    	    double[] delta = new double[n+1];
    	    double[] D = new double[n+1];
    	    int i;
    	    /* We solve the equation
    	       [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
    	       |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
    	       |  1 4 1   | | .  | = |      .         |
    	       |    ..... | | .  |   |      .         |
    	       |     1 4 1| | .  |   |3(x[n] - x[n-2])|
    	       [       1 2] [D[n]]   [3(x[n] - x[n-1])]
    	       
    	       by using row operations to convert the matrix to upper triangular
    	       and then back sustitution.  The D[i] are the derivatives at the knots.
    	       */
    	    
    	    gamma[0] = 1.0/2.0;
    	    for ( i = 1; i < n; i++) {
    	      gamma[i] = 1/(4-gamma[i-1]);
    	    }
    	    gamma[n] = 1/(2-gamma[n-1]);
    	    
    	    delta[0] = 3*(x[1]-x[0])*gamma[0];
    	    for ( i = 1; i < n; i++) {
    	      delta[i] = (3*(startX[i+1]-x[i-1])-delta[i-1])*gamma[i];
    	    }
    	    delta[n] = (3*(x[n]-x[n-1])-delta[n-1])*gamma[n];
    	    
    	    D[n] = delta[n];
    	    for ( i = n-1; i >= 0; i--) {
    	      D[i] = delta[i] - gamma[i]*D[i+1];
    	    }

    	    /* now compute the coefficients of the cubics */
    	    Cubic[] C = new Cubic[n];
    	    for ( i = 0; i < n; i++) {
    	      C[i] = new Cubic((double)x[i], D[i], 3*(startX[i+1] - x[i]) - 2*D[i] - D[i+1],
    			       2*(x[i] - startX[i+1]) + D[i] + D[i+1]);
    	    }
    	    return C;
    	  }
    
    private Cubic[] createCubicClosedCurveFunctions(int n, double[] x)
    {
    	double[] w = new double[ n + 1 ];
		double[] v = new double[ n + 1 ];
		double[] y = new double[ n + 1 ];
		double[] D = new double[ n + 1 ];
		/*
		 * Solves the equation [4 1 1] [D[0]] [3(x[1] - x[n]) ] |1 4 1 | |D[1]|
		 * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
		 * 1| | . | |3(x[n] - x[n-2])| [1 1 4] [D[n]] [3(x[0] - x[n-1])]
		 * 
		 * by decomposing the matrix into upper triangular and lower matrices
		 * and then back sustitution. See Spath "Spline Algorithms for Curves
		 * and Surfaces" pp. 19-21. The D[i] are the derivatives at the knots.
		 */
		double z = 1.0f / 4.0f;
		w[ 1 ] = v[ 1 ] = z;
		y[ 0 ] = z * 3 * (x[ 1 ] - x[ n ]);

		double H = 4;
		double F = 3 * (x[ 0 ] - x[ n - 1 ]);
		double G = 1;

		
		
		for ( int i = 1; i < n; i++ )
		{
			v[ i + 1 ] = z = 1 / (4 - v[ i ]);
			w[ i + 1 ] = -z * w[ i ];
			y[ i ] = z * (3 * (x[ i + 1 ] - x[ i - 1 ]) - y[ i - 1 ]);
			H = H - G * w[ i ];
			F = F - G * y[ i - 1 ];
			G = -v[ i ] * G;
		}
		H = H - (G + 1) * (v[ n ] + w[ n ]);
		y[ n ] = F - (G + 1) * y[ n - 1 ];

		
		
		D[ n ] = y[ n ] / H;
		D[ n - 1 ] = y[ n - 1 ] - (v[ n ] + w[ n ]) * D[ n ];

		for ( int i = n - 2; i >= 0; i-- )
		{
			D[ i ] = y[ i ] - v[ i + 1 ] * D[ i + 1 ] - w[ i + 1 ] * D[ n ];
		}

		
		
		// compute the coefficients of the cubics
		Cubic[] C = new Cubic[ n + 1 ];
		for ( int i = 0; i < n; i++ )
		{
			C[ i ] = new Cubic( x[ i ], D[ i ], 3 * (x[ i + 1 ] - x[ i ]) - 2 * D[ i ] - D[ i + 1 ], 2
				* (x[ i ] - x[ i + 1 ]) + D[ i ] + D[ i + 1 ] );
		}
		C[ n ] = new Cubic( x[ n ], D[ n ], 3 * (x[ 0 ] - x[ n ]) - 2 * D[ n ] - D[ 0 ], 2 * (x[ n ] - x[ 0 ]) + D[ n ]
			+ D[ 0 ] );

		return C;
    	  }

    public static PointDouble[] computeBezier(PointDouble[] controlPoints,
			int numCurvePoints) throws java.lang.IllegalArgumentException {
		if (controlPoints.length < 2 || numCurvePoints < 2) {
			throw new IllegalArgumentException(
					"At least two control points must be provided to compute a Bezier and at least 2 data points must be returned.");
		}
		int bezierOrder = controlPoints.length - 1;
		PointDouble[] returnValue = new PointDouble[numCurvePoints];

		/*
		 * Parametric spacing between points. Subtract 1 so if numCurvePoints is
		 * 3, dt = 1/(3-1) or 0.5 so curve data points are generated at t = 0,
		 * 0.5, and 1.
		 */

		double dt = 1.0 / (double) (numCurvePoints - 1);
		double t = 0.0; // Parametric running variable from t = 0 to t = 1.
		for (int i = 0; i < returnValue.length; i++) {
			double x = 0.0, y = 0.0;
			for (int j = 0; j < controlPoints.length; j++) {
				double weight = combinatoric(bezierOrder, j)
						* Math.pow(1.0 - t, bezierOrder - j) * Math.pow(t, j);
				x += controlPoints[j].x * weight;
				y += controlPoints[j].y * weight;
			}
			
			returnValue[i] = new PointDouble(x,y);
			t += dt;
		}
		return returnValue;
	}
    
    public static int combinatoric(int row, int col) {
		return (int) (factorial(row) / (factorial(col) * factorial(row - col)));
	}

	public static long factorial(int num) {
		long sum = 1;

		while (num > 1) {
			sum *= num;
			num--;
		}
		return sum;
	}
    
	/*
    double[] createXPoints(ArrayList<Position> positions){
    	
    	double[] xpoints = new double[positions.size()];
    	
    	for(int i=0;i<positions.size();i++){
    		xpoints[i]=positions.get(i).getLongitude().degrees;
    	}
    	
    	return xpoints;
    }
    */
 double[] createXPoints(ArrayList<Position> positions, DrawContext dc){
    	
    	double[] xpoints = new double[positions.size()];
    	
    	for(int i=0;i<positions.size();i++){
    		Position pos = positions.get(i);
    		 Vec4 coord =  dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
    				  pos.elevation);
    		
    		xpoints[i]=coord.x;
    	}
    	
    	return xpoints;
    }
 
 
 
 
    
 double[] createYPoints(ArrayList<Position> positions, DrawContext dc){
 	
 	double[] xpoints = new double[positions.size()];
 	
 	for(int i=0;i<positions.size();i++){
 		Position pos = positions.get(i);
 		 Vec4 coord =  dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
 				  pos.elevation);
 		
 		xpoints[i]=coord.y;
 	}
 	
 	return xpoints;
 }
 
 
 
 
 
 
 
 double[] createZPoints(ArrayList<Position> positions, DrawContext dc){
	 	
	 	double[] xpoints = new double[positions.size()];
	 	
	 	for(int i=0;i<positions.size();i++){
	 		Position pos = positions.get(i);
	 		 Vec4 coord =  dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
	 				  pos.elevation);
	 		
	 		xpoints[i]=coord.z;
	 	}
	 	
	 	return xpoints;
	 }
    
 /*
double[] createYPoints(ArrayList<Position> positions){
    	
    	double[] ypoints = new double[positions.size()];
    	
    	for(int i=0;i<positions.size();i++){
    		ypoints[i]=positions.get(i).getLatitude().degrees;
    	}
    	
    	return ypoints;
    }
   */ 






double[] createXPoints(ArrayList<Vec4> points){
	
	double[] xpoints = new double[points.size()];
	
	for(int i=0;i<points.size();i++){
		Vec4 coord = points.get(i);
		// Vec4 coord =  dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
			//	  pos.elevation);
		
		xpoints[i]=coord.x;
	}
	
	return xpoints;
}

double[] createYPoints(ArrayList<Vec4> points){
	

	double[] xpoints = new double[points.size()];
	
	for(int i=0;i<points.size();i++){
		Vec4 coord = points.get(i);
		// Vec4 coord =  dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
			//	  pos.elevation);
		
		xpoints[i]=coord.y;
	}
	
	return xpoints;
}

double[] createZPoints(ArrayList<Vec4> points){
	

	double[] xpoints = new double[points.size()];
	
	for(int i=0;i<points.size();i++){
		Vec4 coord = points.get(i);
		// Vec4 coord =  dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
			//	  pos.elevation);
		
		xpoints[i]=coord.z;
	}
	
	return xpoints;
}


    //funzione da modificare
    private void makeOriginalVertices(DrawContext dc)
    {
 
    	
        if (this.currentSpans == null)
            this.currentSpans = new ArrayList<List<Vec4>>();
        else
            this.currentSpans.clear();
  
        if (renderableControlPoints.getPositions().size() < 1)
            return;

    	/*
    	double[] xpoints = createXPoints(renderableControlPoints.positions,dc);
        double[] ypoints = createYPoints(renderableControlPoints.positions,dc);
        double[] zpoints = createZPoints(renderableControlPoints.positions,dc);
        */
        double[] xpoints = createXPoints(renderableControlPoints.points);
        double[] ypoints = createYPoints(renderableControlPoints.points);
        double[] zpoints = createZPoints(renderableControlPoints.points);
        
        cubicCurveFunctionsX=createCubicCurveFunctions(renderableControlPoints.getPositions().size()-1, xpoints);
        cubicCurveFunctionsY=createCubicCurveFunctions(renderableControlPoints.getPositions().size()-1, ypoints);
        cubicCurveFunctionsZ=createCubicCurveFunctions(renderableControlPoints.getPositions().size()-1, zpoints);
        
     
       //starts from the leaf
       //Position posA = renderableControlPoints.getPositions().get(0);
        
        
        //Vec4 ptA = this.computePoint(dc, posA, true);
        //Vec4 ptA = this.computePoint(dc, posA, computeOffest);
        
        Vec4 ptA = renderableControlPoints.points.get(0);
     
            
         //bufferPointSize = (renderableControlPoints.getSize()-1)*(numSubsegments+2); 
     
         
         // System.out.println("bufferSize: "+bufferPointSize);
        for (int i = 1; i <= renderableControlPoints.getPositions().size(); i++)
        {
        	
        //	 Position newPosB  = null;
           // Position posB;
            if (i < renderableControlPoints.getPositions().size()){
             //   posB = renderableControlPoints.getPositions().get(i);
             //   newPosB =   updPositions.get(i);
            }
            
            else
                break;

            
            //Vec4 ptB = this.computePoint(dc, posB, true);
         
            //Vec4 ptB = this.computePoint(dc, posB, computeOffest);
            
            Vec4 ptB = renderableControlPoints.points.get(i);
           /*
            if (this.followTerrain && !this.isSegmentVisible(dc, posA, posB, ptA, ptB))
            {
                posA = posB;
                ptA = ptB;
                continue;
            }
*/
          
            //System.out.println("creo subcurva da: "+posA+" a "+posB);
            ArrayList<Vec4> span;
                   //span = this.makeSegment(dc, posA, newPosB, ptA, ptB,cubicCurveFunctionsX[i-1],cubicCurveFunctionsY[i-1]);
          
            span = this.makeSegment(dc, ptA, ptB,cubicCurveFunctionsX[i-1],cubicCurveFunctionsY[i-1],cubicCurveFunctionsZ[i-1]);

            //printSpan(span);
            
            if (span != null){
            //aggiungere condizione: se non è già stata renderizzata
          //  boolean toRender = this.renderableControlPoints.toRender.get(i-1);
          //  double magnitude  = this.renderableControlPoints.magnitudes.get(i-1);
          //  Color color = this.renderableControlPoints.colors.get(i-1);
          //  	if(toRender){
            	this.addSpan(span);
            	//System.out.println(i+" addMagnitude: "+magnitude);
            //	this.magnitude.add( magnitude);
            //	this.colors.add( color);
            //}
            
            }
           // posA = posB;
            ptA = ptB;
        }
        
        
        
     //   System.out.println("PRINT SPAN");
       // printSpans(dc,currentSpans);
        //System.out.println("CREA BUFFER POINT");
        
        /*
        bufferPoints =  createIndexBuffer(bufferPointSize,currentSpans);
        createVA(dc,bufferPointSize);
        */
    }
    
    /*
    private void createVA(DrawContext dc, int bufferSize){
    	//System.out.println("CREATE VA");
    //	
    	
    	// Create Vertex Array.
    	 	GL gl=dc.getGL();
    	//int bufferSize = (renderableControlPoints.getSize()-1)*(numSubsegments+2); 
    	gl.glBindBuffer(GL.GL_ARRAY_BUFFER,layer.getBufferElement(index));
       
    	
    	//gl.glBufferData(GL.GL_ARRAY_BUFFER, bufferSize*3*8, bufferPoints, GL.GL_STATIC_DRAW);
    	gl.glBufferData(GL.GL_ARRAY_BUFFER, bufferSize*3*4, bufferPoints, GL.GL_STATIC_DRAW);
        
    	
    	gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    	   	
    	
    }
    */
    private void makeUpdatedVertices(DrawContext dc)
    {
  
    	RenderableControlPoints renderableControlPoints = new RenderableControlPoints();
		double distance = GeneratorOfRenderableObjects.distance(
				getCurrOriginPosition().getLatitude().degrees,getCurrOriginPosition().getLongitude().degrees,
				getCurrDestPosition().getLatitude().degrees,getCurrDestPosition().getLongitude().degrees);
		
		renderableControlPoints.setControlPointPosition(
				GeneratorOfRenderableObjects.setControlPointsOverGlobeWithAltitude(
						Position.fromDegrees(getCurrOriginPosition().getLatitude().degrees,getCurrOriginPosition().getLongitude().degrees,getCurrOriginPosition().getElevation()), 
						Position.fromDegrees(getCurrDestPosition().getLatitude().degrees,getCurrDestPosition().getLongitude().degrees,getCurrDestPosition().getElevation()),distance));
		renderableControlPoints.calculatePoints();
    	
		updRenderableControlPoints=renderableControlPoints;
		
        if (this.currentUpdatedSpans == null)
            this.currentUpdatedSpans = new ArrayList<List<Vec4>>();
        else
            this.currentUpdatedSpans.clear();
  
        if (updRenderableControlPoints.getPositions().size() < 1)
            return;

    	/*
    	double[] xpoints = createXPoints(updRenderableControlPoints.positions,dc);
        double[] ypoints = createYPoints(updRenderableControlPoints.positions,dc);
        double[] zpoints = createZPoints(updRenderableControlPoints.positions,dc);
        */
        
        double[] xpoints = createXPoints(updRenderableControlPoints.points);
        double[] ypoints = createYPoints(updRenderableControlPoints.points);
        double[] zpoints = createZPoints(updRenderableControlPoints.points);
     
        
        updCubicCurveFunctionsX=createCubicCurveFunctions(updRenderableControlPoints.getSize()-1, xpoints);
        updCubicCurveFunctionsY=createCubicCurveFunctions(updRenderableControlPoints.getSize()-1, ypoints);
        updCubicCurveFunctionsZ=createCubicCurveFunctions(updRenderableControlPoints.getSize()-1, zpoints);
        
     
       //starts from the leaf
       // Position posA = updRenderableControlPoints.getPositions().get(0);
        
        
        //Vec4 ptA = this.computePoint(dc, posA, true);
       // Vec4 ptA = this.computePoint(dc, posA, computeOffest);
        Vec4 ptA = updRenderableControlPoints.points.get(0);
        
         bufferUpdPointSize = (updRenderableControlPoints.getSize()-1)*(SharedVariables.numSubsegments+2); 
  
   
            
        for (int i = 1; i <= updRenderableControlPoints.getSize(); i++)
        {
        	
        //	 Position newPosB  = null;
          //  Position posB;
            if (i < updRenderableControlPoints.getSize()){
            //    posB = updRenderableControlPoints.getPositions().get(i);
             //   newPosB =   updPositions.get(i);
            }
            
            else
                break;

            
            //Vec4 ptB = this.computePoint(dc, posB, true);
          //  Vec4 ptB = this.computePoint(dc, posB, computeOffest);
            Vec4 ptB = updRenderableControlPoints.points.get(i);
           /*
            if (this.followTerrain && !this.isSegmentVisible(dc, posA, posB, ptA, ptB))
            {
                posA = posB;
                ptA = ptB;
                continue;
            }
*/
          
            //System.out.println("creo subcurva da: "+posA+" a "+posB);
            ArrayList<Vec4> span;
                   //span = this.makeSegment(dc, posA, newPosB, ptA, ptB,cubicCurveFunctionsX[i-1],cubicCurveFunctionsY[i-1]);
          
            span = this.makeSegment(dc, ptA, ptB,updCubicCurveFunctionsX[i-1],updCubicCurveFunctionsY[i-1],updCubicCurveFunctionsZ[i-1]);

         
            //printSpan(span);
            
            if (span != null){
            //aggiungere condizione: se non è già stata renderizzata
          //  boolean toRender = this.renderableControlPoints.toRender.get(i-1);
          //  double magnitude  = this.renderableControlPoints.magnitudes.get(i-1);
          //  Color color = this.renderableControlPoints.colors.get(i-1);
          //  	if(toRender){
            	this.addUpdatedSpan(span);
            	//System.out.println(i+" addMagnitude: "+magnitude);
            //	this.magnitude.add( magnitude);
            //	this.colors.add( color);
            //}
            
            }
        //    posA = posB;
            ptA = ptB;
        }
        
     //   bufferUpdatedPoints =  createIndexBuffer(bufferUpdPointSize,currentUpdatedSpans);
        
     //   System.out.println("PRINT SPAN");
       // printSpans(dc,currentSpans);
        
    }

    //Position currDestPosition;
	//Position originalDestPosition;
	//boolean isDestOverDeformedSurface;
    
	 //Position currOriginPosition;
	//	Position originalOriginPosition;
		//boolean isOriginOverDeformedSurface;

		
	
	public Position getCurrDestPosition() {
		return layer.getCurrPositionNode(to);
			//return currDestPosition;
		}

		/*
		public void setCurrDestPosition(Position currDestPosition) {
			this.currDestPosition = currDestPosition;
		}
*/
		public Position getCurrOriginPosition() {
			//return currOriginPosition;
			return layer.getCurrPositionNode(from);
		}

		
		public String getDestName() {
			return layer.getName(to);
				//return currDestPosition;
			}

			/*
			public void setCurrDestPosition(Position currDestPosition) {
				this.currDestPosition = currDestPosition;
			}
	*/
			public String getOriginName() {
				//return currOriginPosition;
				return layer.getName(from);
			}
		
		/*
		public void setCurrOriginPosition(Position currOriginPosition) {
			this.currOriginPosition = currOriginPosition;
		}
*/
	public void setDestLocation(Position newPos) {
		// TODO Auto-generated method stub
		if(newPos==null){
			layer.setCurrPositionNode(to,layer.getOriginalPositionNode(to));
			//currDestPosition=(originalDestPosition);
		}else{
			//originalPosition=this.getReferencePosition();
			//System.out.println("new Pos: "+newPos);
			//currDestPosition=(newPos);
			layer.setCurrPositionNode(to,newPos);
		}
		
		
	}
	
	
	
	/*
	public void setOriginalDestPosition(Position originalDestPosition) {
		this.originalDestPosition = originalDestPosition;
	}

	public void setOriginalOriginPosition(Position originalOriginPosition) {
		this.originalOriginPosition = originalOriginPosition;
	}
*/
	public void setOriginLocation(Position newPos) {
		// TODO Auto-generated method stub
		if(newPos==null){
			layer.setCurrPositionNode(from,layer.getOriginalPositionNode(from));
			//currOriginPosition=(originalOriginPosition);
		}else{
			//originalPosition=this.getReferencePosition();
			//System.out.println("new Pos: "+newPos);
			layer.setCurrPositionNode(from,newPos);
			//currOriginPosition=(newPos);
			
		}
		
		
	}
	
	public void setOriginDistance(double distance){
		layer.setDistanceFromCamera(from, distance);
	}
	
	public void setDestDistance(double distance){
		layer.setDistanceFromCamera(to, distance);
	}
	
	public boolean isDestOverDeformedSurface() {
		//return isDestOverDeformedSurface;
		return layer.isDeformedNode(to);
	}
/*
	public void setDestOverDeformedSurface(boolean isOverDeformedSurface) {
		//System.out.println("setDestOverDeformedSurface: "+isOverDeformedSurface);
		layer.setIsDeformedNode(to, isOverDeformedSurface);
		//this.isDestOverDeformedSurface = isOverDeformedSurface;
	}
*/
	/*
	public void setNodeOverDeformedSurface(Position originPos  , boolean isOverDeformedSurface) {
		//System.out.println("setDestOverDeformedSurface: "+isOverDeformedSurface);
		layer.setIsDeformedNode(to, isOverDeformedSurface);
		//this.isDestOverDeformedSurface = isOverDeformedSurface;
	}
	*/
	public boolean isOriginOverDeformedSurface() {
		//return isOriginOverDeformedSurface;
		return layer.isDeformedNode(from);
	}

	/*
	public void setOriginOverDeformedSurface(boolean isOverDeformedSurface) {
		//System.out.println("setOriginOverDeformedSurface: "+isOverDeformedSurface);
		layer.setIsDeformedNode(from, isOverDeformedSurface);
	//	this.isOriginOverDeformedSurface = isOverDeformedSurface;
	}
    */
    //private DoubleBuffer createIndexBuffer(int size,List<List<Vec4>> currentUpdatedSpans){
    private FloatBuffer createIndexBuffer(int size,List<List<Vec4>> currentUpdatedSpans){
   
    	//DoubleBuffer indexBuffer = BufferUtil.newDoubleBuffer(size * 3);
    	//FloatBuffer indexBuffer = BufferUtil.newFloatBuffer(size * 3);
    	FloatBuffer indexBuffer = null;
    	
    	
    	float[] indices = new float[size* 3]; 

    	int i=0;
    	for(List<Vec4> span: currentUpdatedSpans){
    		for(Vec4 p : span){
    			indices[(i*3)]=(float)p.x;
    			indices[(i*3)+1]=(float)p.y;
    			indices[(i*3)+2]=(float)p.z;
    			i++;
    		}
    	}
    	
    	indexBuffer.put(indices); //this also uses the data from earlier
    	indexBuffer.position(0); 
    	return indexBuffer;
    }
	public boolean isImportant() {
		return isImportant;
	}


	public void setImportant(boolean isAffectedByLense) {
	//	this.wasAffectedByLense=this.isAffectedByLense;
	//	System.out.println(getOrigin()+" "+getDestination());
		this.isImportant = isAffectedByLense;
	}
    
    
    private void printSpans(DrawContext dc ,List<List<Vec4>> spans){
    	
    	int i=0;
    	int size = spans.size();
    	//System.out.println("SIZE: "+size);
    	
    	for(List<Vec4> currS:spans){
    	int j=0;
    		
    		for(Vec4 v : currS){
    			
    			//printPosition(dc, v, "spans "+i+" "+j);
    			j++;
    		}
    	
    		
    		i++;
    	}
    	
    	
    }
    
    /*
    private void createMultipleParametricCurves(ArrayList<Position> positions, DrawContext dc){
    	int i=0;
    	ArrayList<Position> newPositions= new ArrayList<Position>();
    	
    	int numControlPoints = renderableControlPoints.magnitudes.size();
    	cubicCurveFunctionsX = new Cubic[0];
    	cubicCurveFunctionsY = new Cubic[0];
    	System.out.println("num points: "+numControlPoints);
    	
    	for( Position p : positions){
    	
    		if(i==0){
    			newPositions.add(p);
    		}else{
    			
    			 Vec4 ptPrev = this.computePoint(dc, renderableControlPoints.positions.get(i-1), true);
    			 Vec4 currPt = this.computePoint(dc, renderableControlPoints.positions.get(i), true);
            	 //double prevMagnitude  = this.renderableControlPoints.prevMagnitudes.get(i);
    			 double prevMagnitude  = this.renderableControlPoints.magnitudes.get(i-1);
            	 double magnitude  = this.renderableControlPoints.magnitudes.get(i);
            	
            	 if(
            	 (prevMagnitude!=magnitude)
            	 &&
            	 (prevMagnitude!=0)
            	 &&
            	 (i!=numControlPoints-1)
            			 )
            	 {
            	            
            		 newPositions.add(p); 
            		   double[] xpoints = createXPoints(newPositions);
            	        double[] ypoints = createYPoints(newPositions);
            	                    	        
            	        cubicCurveFunctionsX=concat(cubicCurveFunctionsX,createCubicCurveFunctions(newPositions.size()-1, xpoints));
            	        cubicCurveFunctionsY=concat(cubicCurveFunctionsY,createCubicCurveFunctions(newPositions.size()-1, ypoints));
            	            	 			
    			newPositions= new ArrayList<Position>();
    			
    			 }else{
            		 newPositions.add(p); 
            	 }
    		}
    		    		
    		i++;
    	}
    	
    	if(newPositions.size()>=1){
    	  double[] xpoints = createXPoints(newPositions);
	        double[] ypoints = createYPoints(newPositions);
    	cubicCurveFunctionsX=concat(cubicCurveFunctionsX,createCubicCurveFunctions(newPositions.size()-1, xpoints));
        cubicCurveFunctionsY=concat(cubicCurveFunctionsY,createCubicCurveFunctions(newPositions.size()-1, ypoints));
    	}
    	
    	System.out.println("num cubic curves: "+cubicCurveFunctionsX.length);
    }
    */
    Cubic[] concat(Cubic[] A, Cubic[] B) {
    	   int aLen = A.length;
    	   int bLen = B.length;
    	   Cubic[] C= new Cubic[aLen+bLen];
    	   System.arraycopy(A, 0, C, 0, aLen);
    	   System.arraycopy(B, 0, C, aLen, bLen);
    	   return C;
    	}
    /*
    private  void readControlPoints(){
    	System.out.println("READ CONTROL POINTS");
    	for( double m : renderableControlPoints.magnitudes){
    		System.out.println("magn: "+m);
    	}
    }
    */
    private  ArrayList<Position> updatePositions(ArrayList<Position> positions, DrawContext dc){
    	
    	
    
    	ArrayList<Position> newPositions= new ArrayList<Position>();
    	
        	
    //	System.out.println("control points");
    	
    	for( Position p : positions){
    	
    			newPositions.add(p);    	
    		    		    		
    		
    	}
    	
    	return newPositions;
    }
    
    /**
     * Compute points on either side of a line segment. This method requires a point on the line, and either a next
     * point, previous point, or both.
     *
     * @param point          Center point about which to compute side points.
     * @param prev           Previous point on the line. May be null if {@code next} is non-null.
     * @param next           Next point on the line. May be null if {@code prev} is non-null.
     * @param leftPositions  Left position will be added to this list.
     * @param rightPositions Right position will be added to this list.
     * @param halfWidth      Distance from the center line to the left or right lines.
     * @param globe          Current globe.
     */
    protected Vec4 generateParallelPoints(Vec4 point, Vec4 prev, Vec4 next, double halfWidth, Globe globe)
    {
        if ((point == null) || (prev == null && next == null))
        {
            String message = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
     
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4 offset;
        Vec4 normal = globe.computeSurfaceNormalAtPoint(point);
       
        
        
        // Compute vector in the direction backward along the line.
        Vec4 backward = (prev != null) ? prev.subtract3(point) : point.subtract3(next);

        // Compute a vector perpendicular to segment BC, and the globe normal vector.
        Vec4 perpendicular = backward.cross3(normal);

        double length;
        // If both next and previous points are supplied then calculate the angle that bisects the angle current, next, prev.
        if (next != null && prev != null && !Vec4.areColinear(prev, point, next))
        {
            // Compute vector in the forward direction.
            Vec4 forward = next.subtract3(point);

            // Calculate the vector that bisects angle ABC.
            offset = forward.normalize3().add3(backward.normalize3());
            offset = offset.normalize3();

            // Compute the scalar triple product of the vector BC, the normal vector, and the offset vector to
            // determine if the offset points to the left or the right of the control line.
            double tripleProduct = perpendicular.dot3(offset);
            if (tripleProduct < 0)
            {
                offset = offset.multiply3(-1);
            }

            // Determine the length of the offset vector that will keep the left and right lines parallel to the control
            // line.
            Angle theta = backward.angleBetween3(offset);
            if (!Angle.ZERO.equals(theta)){
            	
            	
                length = halfWidth / theta.sin();
            }
            else
                length = halfWidth;
        }
        else
        {
            offset = perpendicular.normalize3();
            length = halfWidth;
        }
        offset = offset.multiply3(length);

        // Determine the left and right points by applying the offset.
        //Vec4 ptRight = point.add3(offset);
       Vec4 ptLeft = point.subtract3(offset);

        // Convert cartesian points to geographic.
        //Position posLeft = globe.computePositionFromPoint(ptLeft);
       // Position posRight = globe.computePositionFromPoint(ptRight);
return ptLeft;
        //leftPositions.add(posLeft);
        //rightPositions.add(posRight);
    }
    
    /*
    private Vec4 movePointInPerpendicular(Vec4 point1,Vec4 point2, double width){
    	//Calculate a vector between start and end points
    	Vec4 v = new Vec4(point2.x - point1.y,point2.x - point1.y);
    	

    	//Then calculate a perpendicular to it (just swap X and Y coordinates)
    	Vec4 p = new Vec4(v.y,-v.x);
    	

    	//Normalize that perpendicular
    	
    	double length = Math.sqrt(p.x * p.x + p.y * p.y); //Thats length of perpendicular
    	Vec4 n = new Vec4(p.x / length,p.y / length);
    	

    	//Calculate 4 points that form a rectangle by adding normalized perpendicular and multiplying it by half of the desired width
    	//Vec4 r1 = new Vec4(point1.x + n.x * width / 2,point1.y + n.y * width / 2);
    	//Vec4 r2 = new Vec4(point1.x - n.x * width / 2,point1.y - n.y * width / 2);
    	Vec4 r3 = new Vec4(point2.x + n.x * width / 2,point2.y + n.y * width / 2);
    	//Vec4 r4 = new Vec4(point2.x - n.x * width / 2,point2.y - n.y * width / 2);
    	
    	return r3;
    }
    */
    protected void printSpan(ArrayList<Vec4> span){
    	
    	//System.out.println("print span");
    	
    	for(Vec4 item:span){
    		
    		System.out.println(item);
    	}
    	
    }
    
    protected void addSpan(ArrayList<Vec4> span)
    {
        if (span != null && span.size() > 0)
            this.currentSpans.add(span);
    }

    protected void addUpdatedSpan(ArrayList<Vec4> span)
    {
        if (span != null && span.size() > 0)
            this.currentUpdatedSpans.add(span);
    }

    
    protected boolean isSegmentVisible(DrawContext dc, Position posA, Position posB, Vec4 ptA, Vec4 ptB)
    {
        Frustum f = dc.getView().getFrustumInModelCoordinates();

        if (f.contains(ptA))
            return true;

        if (f.contains(ptB))
            return true;

        if (ptA.equals(ptB))
            return false;

        Position posC = Position.interpolateRhumb(0.5, posA, posB);
        
        
        //Vec4 ptC = this.computePoint(dc, posC, true);
        Vec4 ptC = this.computePoint(dc, posC, computeOffest);
        
        if (f.contains(ptC))
            return true;

        double r = Line.distanceToSegment(ptA, ptB, ptC);
        Cylinder cyl = new Cylinder(ptA, ptB, r == 0 ? 1 : r);
        return cyl.intersects(dc.getView().getFrustumInModelCoordinates());
    }

    protected Vec4 computePoint(DrawContext dc, Position pos, boolean applyOffset)
    {
        if (this.followTerrain)
        {
           double height = !applyOffset ? 0 : this.offset;
            // computeTerrainPoint will apply vertical exaggeration
            return dc.computeTerrainPoint(pos.getLatitude(), pos.getLongitude(), height);
        }
        else
        {
            double height = pos.getElevation() + (applyOffset ? this.offset : 0);
            return dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
                height * dc.getVerticalExaggeration());
        }
    }

    protected double computeSegmentLength(DrawContext dc, Position posA, Position posB)
    {
        LatLon llA = new LatLon(posA.getLatitude(), posA.getLongitude());
        LatLon llB = new LatLon(posB.getLatitude(), posB.getLongitude());

        Angle ang = LatLon.greatCircleDistance(llA, llB);

        if (this.followTerrain)
        {
            return ang.radians * (dc.getGlobe().getRadius() + this.offset * dc.getVerticalExaggeration());
        }
        else
        {
            double height = this.offset + 0.5 * (posA.getElevation() + posB.getElevation());
            return ang.radians * (dc.getGlobe().getRadius() + height * dc.getVerticalExaggeration());
        }
    }

    

    protected ArrayList<Vec4> makeSegment(DrawContext dc, Vec4 ptA, Vec4 ptB,Cubic cubicCurveFunctionX, Cubic cubicCurveFunctionY,Cubic cubicCurveFunctionZ)
    {
    	
    	
    	//System.out.println("Make Segment");
        ArrayList<Vec4> span = null;
        span = this.clipAndAdd(dc, ptA, span);
        /*
        double arcLength = ptA.distanceTo3(ptB);
        if (arcLength <= 0) // points differing only in altitude
        {
            span = this.addPointToSpan(ptA, span);
            if (!ptA.equals(ptB))
                span = this.addPointToSpan(ptB, span);
            return span;
        }
        */
        /*
        for (double s = 0, p = 0; s < 1; )
        {

                p += arcLength / this.numSubsegments;

            s = p / arcLength;
*/
        for(int i =0;i<=SharedVariables.numSubsegments;i++){
            //s va da 0 a 1            
        	double s = (double)i/(double)SharedVariables.numSubsegments;
        	
          
            if (s > 1)
            {
            //	  span = this.clipAndAdd(dc, ptA, span);
            }
            else 
            {
            	/*
                double longitudeDegree=cubicCurveFunctionX.eval(s);
                double latitudeDegree=cubicCurveFunctionY.eval(s);
                */
       
            	
            	double x=cubicCurveFunctionX.eval(s);
                double y=cubicCurveFunctionY.eval(s);
                double z=cubicCurveFunctionZ.eval(s);
                ptB = new Vec4(x, y, z);
            
                //System.out.println(s+": "+latitudeDegree+" "+longitudeDegree);
                
            //     latLon = LatLon.fromDegrees(latitudeDegree, longitudeDegree);
            	//System.out.println(s+": "+latLon.latitude.+" "+longitudeDegree);
                
              //  pos = new Position(latLon, (1 - s) * posA.getElevation() + s * posB.getElevation());
                }
                        

            //ptB = this.computePoint(dc, pos, true);
         //   ptB = this.computePoint(dc, pos, computeOffest);
     

            //System.out.println("Position: "+pos.latitude.degrees+ " "+pos.longitude.degrees+ "  Point: "+ptB.x+" "+ptB.y+" "+ptB.z);
            
            span = this.clipAndAdd(dc, ptB, span);

          //  ptA = ptB;
        }

       
        
        return span;
    }

    
    //funzione da modificare
   
    
    
    @SuppressWarnings({"UnusedDeclaration"})
    protected ArrayList<Vec4> clipAndAdd(DrawContext dc, Vec4 ptB, ArrayList<Vec4> span)
    {
        // Line clipping appears to be useful only for long lines with few segments. It's costly otherwise.
        // TODO: Investigate trade-off of line clipping.
//        if (Line.clipToFrustum(ptA, ptB, dc.getView().getFrustumInModelCoordinates()) == null)
//        {
//            if (span != null)
//            {
//                this.addSpan(span);
//                span = null;
//            }
//            return span;
//        }

        if (span == null){
          //  span = this.addPointToSpan(ptA, span);
        //    printPosition(dc, ptA,"in clipAndAdd ptA se span == null");
        }
       
       // printPosition(dc, ptB," in clipAndAdd ptB");
        
        return this.addPointToSpan(ptB, span);
    }

    protected ArrayList<Vec4> addPointToSpan(Vec4 p, ArrayList<Vec4> span)
    {
    	
    	
        if (span == null)
            span = new ArrayList<Vec4>();

   //     span.add(p.subtract3(this.referenceCenterPoint));
        span.add(p);

        return span;
    }

    protected void computeReferenceCenter(DrawContext dc)
    {
        // The reference position is null if this Polyline has no positions. In this case computing the Polyline's
        // Cartesian reference point is meaningless because the Polyline has no geographic location. Therefore we exit
        // without updating the reference point.
        Position refPos = this.getReferencePosition();
        if (refPos == null)
            return;

     //   this.referenceCenterPoint = dc.computeTerrainPoint(refPos.getLatitude(), refPos.getLongitude(),
       //     this.offset);
        
        
    }

    private static void printPosition(DrawContext dc, Vec4 p,String where){
    	
    	//Position pos = dc.getModel().getGlobe().computePositionFromPoint(p);
    	
    //	if(
    	//		(pos.elevation!=20000)
    		//	)
    	//		{
    		System.out.println("in "+where);
    		System.out.println("elevation!!: "+p);
    	//}
    }
    
    
    
    public Position getReferencePosition()
    {
        if (this.renderableControlPoints.getPositions().size() < 1)
        {
            return null;
        }
        else if (this.renderableControlPoints.getPositions().size() < 3)
        {
            return this.renderableControlPoints.getPositions().get(0);
        }
        else
        {
            return this.renderableControlPoints.getPositions().get(this.renderableControlPoints.getPositions().size() / 2);
        }
    }




    public void move(Position delta)
    {
        if (delta == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position refPos = this.getReferencePosition();

        // The reference position is null if this Polyline has no positions. In this case moving the Polyline by a
        // relative delta is meaningless because the Polyline has no geographic location. Therefore we fail softly by
        // exiting and doing nothing.
        if (refPos == null)
            return;

        this.moveTo(refPos.add(delta));
    }

    
    
    
    public void moveTo(Position position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.reset();
        this.extents.clear();

        Position oldRef = this.getReferencePosition();

        // The reference position is null if this Polyline has no positions. In this case moving the Polyline to a new
        // reference position is meaningless because the Polyline has no geographic location. Therefore we fail softly
        // by exiting and doing nothing.
        if (oldRef == null)
            return;

        double elevDelta = position.getElevation() - oldRef.getElevation();

        for (int i = 0; i < this.renderableControlPoints.getPositions().size(); i++)
        {
            Position pos = this.renderableControlPoints.getPositions().get(i);

            Angle distance = LatLon.greatCircleDistance(oldRef, pos);
            Angle azimuth = LatLon.greatCircleAzimuth(oldRef, pos);
            LatLon newLocation = LatLon.greatCircleEndPosition(position, azimuth, distance);
            double newElev = pos.getElevation() + elevDelta;

            this.renderableControlPoints.getPositions().set(i, new Position(newLocation, newElev));
        }
    }



    /**
     * Returns an XML state document String describing the public attributes of this Polyline.
     *
     * @return XML state document string describing this Polyline.
     */
    public String getRestorableState()
    {
        RestorableSupport rs = RestorableSupport.newRestorableSupport();
        // Creating a new RestorableSupport failed. RestorableSupport logged the problem, so just return null.
        if (rs == null)
            return null;
/*
        if (this.color != null)
        {
            String encodedColor = RestorableSupport.encodeColor(this.color);
            if (encodedColor != null)
                rs.addStateValueAsString("color", encodedColor);
        }
*/
        if (this.highlightColor != null)
        {
            String encodedColor = RestorableSupport.encodeColor(this.highlightColor);
            if (encodedColor != null)
                rs.addStateValueAsString("highlightColor", encodedColor);
        }

        if (this.renderableControlPoints.getPositions() != null)
        {
            // Create the base "positions" state object.
            RestorableSupport.StateObject positionsStateObj = rs.addStateObject("positions");
            if (positionsStateObj != null)
            {
                for (Position p : this.renderableControlPoints.getPositions())
                {
                    // Save each position only if all parts (latitude, longitude, and elevation) can be
                    // saved. We will not save a partial iconPosition (for example, just the elevation).
                    if (p != null && p.getLatitude() != null && p.getLongitude() != null)
                    {
                        // Create a nested "position" element underneath the base "positions".
                        RestorableSupport.StateObject pStateObj =
                            rs.addStateObject(positionsStateObj, "position");
                        if (pStateObj != null)
                        {
                            rs.addStateValueAsDouble(pStateObj, "latitudeDegrees",
                                p.getLatitude().degrees);
                            rs.addStateValueAsDouble(pStateObj, "longitudeDegrees",
                                p.getLongitude().degrees);
                            rs.addStateValueAsDouble(pStateObj, "elevation",
                                p.getElevation());
                        }
                    }
                }
            }
        }

        rs.addStateValueAsInteger("antiAliasHint", this.antiAliasHint);
        rs.addStateValueAsBoolean("filled", this.filled);
    
        rs.addStateValueAsBoolean("highlighted", this.highlighted);
        rs.addStateValueAsInteger("pathType", this.pathType);
        rs.addStateValueAsBoolean("followTerrain", this.followTerrain);
        rs.addStateValueAsDouble("offset", this.offset);
    //    rs.addStateValueAsDouble("terrainConformance", this.terrainConformance);
        rs.addStateValueAsDouble("lineWidth", this.lineWidth);
        rs.addStateValueAsInteger("stipplePattern", this.stipplePattern);
        rs.addStateValueAsInteger("stippleFactor", this.stippleFactor);
    

        RestorableSupport.StateObject so = rs.addStateObject(null, "avlist");
        for (Map.Entry<String, Object> avp : this.getEntries())
        {
            this.getRestorableStateForAVPair(avp.getKey(), avp.getValue() != null ? avp.getValue() : "", rs, so);
        }

        return rs.getStateAsXml();
    }

    /**
     * Restores publicly settable attribute values found in the specified XML state document String. The document
     * specified by <code>stateInXml</code> must be a well formed XML document String, or this will throw an
     * IllegalArgumentException. Unknown structures in <code>stateInXml</code> are benign, because they will simply be
     * ignored.
     *
     * @param stateInXml an XML document String describing a Polyline.
     *
     * @throws IllegalArgumentException If <code>stateInXml</code> is null, or if <code>stateInXml</code> is not a well
     *                                  formed XML document String.
     */
    public void restoreState(String stateInXml)
    {
        if (stateInXml == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        RestorableSupport restorableSupport;
        try
        {
            restorableSupport = RestorableSupport.parse(stateInXml);
        }
        catch (Exception e)
        {
            // Parsing the document specified by stateInXml failed.
            String message = Logging.getMessage("generic.ExceptionAttemptingToParseStateXml", stateInXml);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message, e);
        }

        /*
        String colorState = restorableSupport.getStateValueAsString("color");
        if (colorState != null)
        {
            Color color = RestorableSupport.decodeColor(colorState);
            if (color != null)
                setColor(color);
        }

        colorState = restorableSupport.getStateValueAsString("highlightColor");
        if (colorState != null)
        {
            Color color = RestorableSupport.decodeColor(colorState);
            if (color != null)
                setHighlightColor(color);
        }
*/
        // Get the base "positions" state object.
        RestorableSupport.StateObject positionsStateObj = restorableSupport.getStateObject("positions");
        if (positionsStateObj != null)
        {
            ArrayList<Position> newPositions = new ArrayList<Position>();
            // Get the nested "position" states beneath the base "positions".
            RestorableSupport.StateObject[] positionStateArray =
                restorableSupport.getAllStateObjects(positionsStateObj, "position");
            if (positionStateArray != null && positionStateArray.length != 0)
            {
                for (RestorableSupport.StateObject pStateObj : positionStateArray)
                {
                    if (pStateObj != null)
                    {
                        // Restore each position only if all parts are available.
                        // We will not restore a partial position (for example, just the elevation).
                        Double latitudeState = restorableSupport.getStateValueAsDouble(pStateObj, "latitudeDegrees");
                        Double longitudeState = restorableSupport.getStateValueAsDouble(pStateObj, "longitudeDegrees");
                        Double elevationState = restorableSupport.getStateValueAsDouble(pStateObj, "elevation");
                        if (latitudeState != null && longitudeState != null && elevationState != null)
                            newPositions.add(Position.fromDegrees(latitudeState, longitudeState, elevationState));
                    }
                }
            }

            // Even if there are no actual positions specified, we set positions as an empty list.
            // An empty set of positions is still a valid state.
            //setPositions(newPositions);
           // renderableControlPoints.getPositions=newPositions;
         //   renderableControlPoints.setControlPoint(newPositions);
        //	this.updRenderableControlPoints.setControlPointPosition((ArrayList<Position>)renderableControlPoints.getPositions().clone());
    //		this.updRenderableControlPoints.setControlPoint((ArrayList<Vec4>)renderableControlPoints.points.clone());
        }

        Integer antiAliasHintState = restorableSupport.getStateValueAsInteger("antiAliasHint");
        if (antiAliasHintState != null)
            setAntiAliasHint(antiAliasHintState);

        Boolean isFilledState = restorableSupport.getStateValueAsBoolean("filled");
        if (isFilledState != null)
            setFilled(isFilledState);

    

        Boolean isHighlightedState = restorableSupport.getStateValueAsBoolean("highlighted");
        if (isHighlightedState != null)
            setHighlighted(isHighlightedState);

        Integer pathTypeState = restorableSupport.getStateValueAsInteger("pathType");
        if (pathTypeState != null)
            setPathType(pathTypeState);

      //  Boolean isFollowTerrainState = restorableSupport.getStateValueAsBoolean("followTerrain");
      //  if (isFollowTerrainState != null)
        //    setFollowTerrain(isFollowTerrainState);

        Double offsetState = restorableSupport.getStateValueAsDouble("offset");
        if (offsetState != null)
            setOffset(offsetState);

        Double terrainConformanceState = restorableSupport.getStateValueAsDouble("terrainConformance");

        if (terrainConformanceState != null)
            setTerrainConformance(terrainConformanceState);

        Double lineWidthState = restorableSupport.getStateValueAsDouble("lineWidth");
        if (lineWidthState != null)
            setLineWidth(lineWidthState);

        Integer stipplePatternState = restorableSupport.getStateValueAsInteger("stipplePattern");
        if (stipplePatternState != null)
            setStipplePattern(stipplePatternState.shortValue());

        Integer stippleFactorState = restorableSupport.getStateValueAsInteger("stippleFactor");
        if (stippleFactorState != null)
            setStippleFactor(stippleFactorState);

        Integer numSubsegmentsState = restorableSupport.getStateValueAsInteger("numSubsegments");
        if (numSubsegmentsState != null)
            setNumSubsegments(numSubsegmentsState);

        RestorableSupport.StateObject so = restorableSupport.getStateObject(null, "avlist");
        if (so != null)
        {
            RestorableSupport.StateObject[] avpairs = restorableSupport.getAllStateObjects(so, "");
            if (avpairs != null)
            {
                for (RestorableSupport.StateObject avp : avpairs)
                {
                    if (avp != null)
                        this.setValue(avp.getName(), avp.getValue());
                }
            }
        }
    }
    
    
     static class PointDouble{
    	
    	public double x;
    	public double y;
    	
    	
    	
		public PointDouble() {
			super();
		}



		public PointDouble(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}
    	
    	
    }


	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

    	
    
}
