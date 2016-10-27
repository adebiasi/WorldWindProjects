/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.render.cubicCurve;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.LazilyLoadedTexture;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.WWTexture;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.util.measure.LengthMeasurer;
import it.graphitech.Operations;
import it.graphitech.Variables;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.sun.prism.impl.BufferUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;

/**
 * @author tag
 * @version $Id: Polyline.java 914 2012-11-28 02:21:56Z pabercrombie $
 */
public class CubicSplinePolyline extends AVListImpl implements Renderable, OrderedRenderable, Movable, Restorable,
    MeasurableLength, ExtentHolder
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

	
    protected Texture texture; // an optional texture for the base polygon
	//protected String texturePath = "texture/earth.png";
	protected String texturePath = "texture/5percent_lineegrandi.png";
	protected int numSubsegments = 10;
    protected boolean followTerrain = false;
	protected boolean computeOffest= true;
	
    public final static int GREAT_CIRCLE = WorldWind.GREAT_CIRCLE;
    public final static int LINEAR = WorldWind.LINEAR;
    public final static int RHUMB_LINE = WorldWind.RHUMB_LINE;
    public final static int LOXODROME = RHUMB_LINE;

    public final static int ANTIALIAS_DONT_CARE = WorldWind.ANTIALIAS_DONT_CARE;
    public final static int ANTIALIAS_FASTEST = WorldWind.ANTIALIAS_FASTEST;
    public final static int ANTIALIAS_NICEST = WorldWind.ANTIALIAS_NICEST;

    protected RenderableControlPoints renderableControlPoints;
   //protected ArrayList<Position> positions;
    protected Vec4 referenceCenterPoint;
    protected int antiAliasHint = GL.GL_FASTEST;
    //protected Color color = Color.WHITE;
    protected double lineWidth = 1;
    protected boolean filled = false; // makes it a polygon
    protected boolean closed = false; // connect last point to first

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
    protected PickSupport pickSupport = new PickSupport();
    protected long frameNumber = -1; // identifies frame used to calculate these values
    protected Layer pickLayer;

    
    
    
    protected List<List<Vec4>> currentSpans;
    protected Cubic[] cubicCurveFunctionsX;
    protected Cubic[] cubicCurveFunctionsY;
    
    protected List<Double> magnitude;
    protected List<Color> colors;
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

    
    
    public CubicSplinePolyline(RenderableControlPoints renderableControlPoints) {
    	
    	this.measurer.setFollowTerrain(this.followTerrain);
        this.measurer.setPathType(this.pathType);
		this.renderableControlPoints = renderableControlPoints;
		
		
		
		
	}
/*
	public CubicSplinePolyline()
    {
        this.setPositions(null);
        this.measurer.setFollowTerrain(this.followTerrain);
        this.measurer.setPathType(this.pathType);
    }

    public CubicSplinePolyline(Iterable<? extends Position> positions)
    {
        this.setPositions(positions);
        this.measurer.setFollowTerrain(this.followTerrain);
        this.measurer.setPathType(this.pathType);
    }

    public CubicSplinePolyline(Iterable<? extends LatLon> positions, double elevation)
    {
        this.setPositions(positions, elevation);
        this.measurer.setFollowTerrain(this.followTerrain);
        this.measurer.setPathType(this.pathType);
    }
*/
    private void reset()
    {
    	
    	
        if (this.currentSpans != null)
            this.currentSpans.clear();
        
        
        this.currentSpans = null;
        
        if (this.magnitude != null)
            this.magnitude.clear();
        
        
        this.magnitude = null;
    }

    /*
    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        if (color == null)
        {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.color = color;
    }
*/
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

    public int getNumSubsegments()
    {
        return numSubsegments;
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
        this.numSubsegments = numSubsegments;
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
    public void setPositions(Iterable<? extends Position> inPositions)
    {
        this.reset();
        this.positions = new ArrayList<Position>();
        this.extents.clear();
        if (inPositions != null)
        {
            for (Position position : inPositions)
            {
                this.positions.add(position);
            }
            this.measurer.setPositions(this.positions);
        }

        if ((this.filled && this.positions.size() < 3))
        {
            String msg = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
    }
*/
    
    
    public RenderableControlPoints getRenderableControlPoints() {
		return renderableControlPoints;
	}

	public void setRenderableControlPoints(
			RenderableControlPoints renderableControlPoints) {
		
		this.reset();
		this.renderableControlPoints = renderableControlPoints;
		 this.extents.clear();
	}

	/**
     * Sets the paths positions as latitude and longitude values at a constant altitude.
     *
     * @param inPositions the latitudes and longitudes of the positions.
     * @param altitude    the elevation to assign each position.
     */
	/*
    public void setPositions(Iterable<? extends LatLon> inPositions, double altitude)
    {
        this.reset();
        this.positions = new ArrayList<Position>();
        this.extents.clear();
        if (inPositions != null)
        {
            for (LatLon position : inPositions)
            {
                this.positions.add(new Position(position, altitude));
            }
            this.measurer.setPositions(this.positions);
        }

        if (this.filled && this.positions.size() < 3)
        {
            String msg = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public Iterable<Position> getPositions()
    {
        return this.positions;
    }
*/
    public boolean isClosed()
    {
        return closed;
    }

    public void setClosed(boolean closed)
    {
        this.closed = closed;
    }

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
    	Sector sector = Sector.boundingSector(renderableControlPoints.positions);
    	
        double[] minAndMaxElevations;
        if (this.isFollowTerrain())
        {
            minAndMaxElevations = globe.getMinAndMaxElevations(sector);
        }
        else
        {
            //minAndMaxElevations = computeElevationExtremes(this.getPositions());
        	minAndMaxElevations = computeElevationExtremes(renderableControlPoints.positions);
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
    public void setTextureImageSource(Object imageSource, float[] texCoords, int texCoordCount)
    {
        if (imageSource == null)
        {
            this.wwTexture = null;
            this.textureCoordsBuffer = null;
            return;
        }

        if (texCoords == null)
        {
            String message = Logging.getMessage("generic.ListIsEmpty");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (texCoordCount < 3 || texCoords.length < 2 * texCoordCount)
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.imageSource = imageSource;
        this.wwTexture = this.makeTexture(this.imageSource);
        
        // Determine whether the tex-coord list needs to be closed.
        boolean closeIt = texCoords[0] != texCoords[texCoordCount - 2] || texCoords[1] != texCoords[texCoordCount - 1];

        int size = 2 * (texCoordCount + (closeIt ? 1 : 0));
        if (this.textureCoordsBuffer == null || this.textureCoordsBuffer.capacity() < size)
        {
            this.textureCoordsBuffer = BufferUtil.newFloatBuffer(size);
        }
        else
        {
            this.textureCoordsBuffer.limit(this.textureCoordsBuffer.capacity());
            this.textureCoordsBuffer.rewind();
        }

        for (int i = 0; i < 2 * texCoordCount; i++)
        {
            this.textureCoordsBuffer.put(texCoords[i]);
        }

        if (closeIt)
        {
            this.textureCoordsBuffer.put(this.textureCoordsBuffer.get(0));
            this.textureCoordsBuffer.put(this.textureCoordsBuffer.get(1));
        }
    }
    */
    protected WWTexture makeTexture(Object imageSource)
    {
        return new LazilyLoadedTexture(imageSource, true);
    }
    
    
    protected void initializeTexture(DrawContext dc)
    {
         texture = dc.getTextureCache().getTexture(texturePath);
        if (texture != null)
            return;

        try
        {
            InputStream iconStream = this.getClass().getResourceAsStream("/" + texturePath);
            if (iconStream == null)
            {
                File iconFile = new File(texturePath);
                if (iconFile.exists())
                {
                    iconStream = new FileInputStream(iconFile);
                }
            }

            texture = TextureIO.newTexture(iconStream, false, null);
            texture.bind(dc.getGL());
            
            dc.getTextureCache().put(texturePath, texture);
        }
        catch (IOException e)
        {
            String msg = Logging.getMessage("layers.IOExceptionDuringInitialization");
            Logging.logger().severe(msg);
            throw new WWRuntimeException(msg, e);
        }

        GL gl = dc.getGL();
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);//_MIPMAP_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        // Enable texture anisotropy, improves "tilted" compass quality.
        int[] maxAnisotropy = new int[1];
        gl.glGetIntegerv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy, 0);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy[0]);
    }
    
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
        if (dc.isOrderedRenderingMode())
        {
            this.drawOrderedRenderable(dc);
            return;
        }

        /*
        if(wwTexture== null){
        
           // InputStream stream = getClass().getResourceAsStream("/texture/earth.png");
        	 float[] texCoords = new float[] {0, 0, 1, 0, 1, 1, 0, 1};
        	 int texCoordCount = 4;
        	setTextureImageSource("/texture/earth.png", texCoords, texCoordCount);
            
           
        }
        */
        
        
        
            texture = dc.getTextureCache().getTexture(texturePath);
            if (texture == null)
            {
                this.initializeTexture(dc);
                texture = dc.getTextureCache().getTexture(texturePath);
                if (texture == null)
                {
                    String msg = Logging.getMessage("generic.ImageReadFailed");
                    Logging.logger().finer(msg);
                    return;
                }
            }	
        
        // The rest of the code in this method determines whether to queue an ordered renderable for the polyline.
/*
        if (this.positions.size() < 2)
            return;
*/
        if (renderableControlPoints.positions.size() < 2)
            return;
        
        
        // vertices potentially computed every frame to follow terrain changes
        if (this.currentSpans == null || (this.followTerrain && this.geomGenTimeStamp != dc.getFrameTimeStamp())
            || this.geomGenVE != dc.getVerticalExaggeration())
        {
            // Reference center must be computed prior to computing vertices.
            this.computeReferenceCenter(dc);
            //this.eyeDistance = this.referenceCenterPoint.distanceTo3(dc.getView().getEyePoint());
            this.eyeDistance =-renderableControlPoints.getMagnitude();
            //System.out.println("this.eyeDistance: "+this.eyeDistance);
            this.makeVertices(dc);
            this.geomGenTimeStamp = dc.getFrameTimeStamp();
            this.geomGenVE = dc.getVerticalExaggeration();
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
    
    
    protected void drawOrderedRenderable(DrawContext dc)
    {
        GL2 gl = dc.getGL().getGL2();
IntBuffer range  = BufferUtil.newIntBuffer(2);
        //GLint range[2];
        gl.glGetIntegerv(GL.GL_ALIASED_LINE_WIDTH_RANGE, range);
        gl.glGetIntegerv(GL.GL_SMOOTH_LINE_WIDTH_RANGE, range);
        
       // System.out.println(range.get(0)+" - "+range.get(1));
        
        int attrBits = GL2.GL_HINT_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT;
        if (!dc.isPickingMode())
        {
           // if (this.color.getAlpha() != 255)
                attrBits |= GL.GL_COLOR_BUFFER_BIT;
        }

        gl.glPushAttrib(attrBits);
       
        
        /////////////////////////////
         dc.getView().pushReferenceCenter(dc, this.referenceCenterPoint);
////////////////////////
        
        
        boolean projectionOffsetPushed = false; // keep track for error recovery

        try
        {
        	
        	/*
            if (!dc.isPickingMode())
            {
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
                // We cannot depend on the layer to set a pick color for us because this Polyline is picked during ordered
                // rendering. Therefore we set the pick color ourselves.
                Color pickColor = dc.getUniquePickColor();
                Object userObject = this.getDelegateOwner() != null ? this.getDelegateOwner() : this;
                this.pickSupport.addPickableObject(pickColor.getRGB(), userObject, null);
                gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
            }
*/
            
            
            
        	bubbleSort(magnitude, colors, currentSpans);
        	
            
            if (this.stippleFactor > 0)
            {
                gl.glEnable(GL2.GL_LINE_STIPPLE);
                gl.glLineStipple(this.stippleFactor, this.stipplePattern);
            }
            else
            {
                gl.glDisable(GL2.GL_LINE_STIPPLE);
            }

            
            
            
            int hintAttr = GL.GL_LINE_SMOOTH_HINT;
           // if (this.filled)
                hintAttr = GL2.GL_POLYGON_SMOOTH_HINT;
            gl.glHint(hintAttr, this.antiAliasHint);

            int primType = GL.GL_LINE_STRIP;
            //if (this.filled)
                primType = GL2.GL_POLYGON;

            if (dc.isPickingMode())
                gl.glLineWidth((float) this.lineWidth + 8);
            else
                gl.glLineWidth((float) this.lineWidth);

            
            
            if (this.followTerrain)
            {
            	
            
                dc.pushProjectionOffest(0.99);
                projectionOffsetPushed = true;
            }


            if (this.currentSpans == null)
                return;
/*
            for (List<Vec4> span : this.currentSpans)
            {
                if (span == null)
                    continue;

                // Since segments can very often be very short -- two vertices -- use explicit rendering. The
                // overhead of batched rendering, e.g., gl.glDrawArrays, is too high because it requires copying
                // the vertices into a DoubleBuffer, and DoubleBuffer creation and access performs relatively poorly.
                gl.glBegin(primType);
                for (Vec4 p : span)
                {
                    gl.glVertex3d(p.x, p.y, p.z);
                }
                gl.glEnd();
            }
*/
       
/*
            gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, this.textureCoordsBuffer.rewind());
            dc.getGL().glEnable(GL.GL_TEXTURE_2D);
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            gl.glBindTexture(GL.GL_TEXTURE_2D,textureCoordsBuffer.get());
  */
            
            
           	gl.glDisable(GL.GL_DEPTH_TEST);
        	if (texture != null)
            {
                gl.glEnable(GL.GL_TEXTURE_2D);
                texture.bind(gl);

               // gl.glColor4d(1d, 1d, 1d, 1);
                gl.glEnable(GL.GL_BLEND);
               // gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
              //  TextureCoords texCoords = texture.getImageTexCoords();
             //   gl.glScaled(1, 1, 1d);
             //   dc.drawUnitQuad(texCoords);
            }
            
           	gl.glBegin(GL.GL_TRIANGLE_STRIP);    
        
        	
            for(int i=0;i<this.currentSpans.size();i++)
            //for (List<Vec4> span : this.currentSpans)
            {
            	List<Vec4> span =this.currentSpans.get(i);
            	
            	double currMagnitude = this.magnitude.get(i);
            	/*
            	if(currMagnitude<Operations.maxFlowMagnitude/6){            		
            		currMagnitude=Operations.maxFlowMagnitude/6;
            	}
            	*/
            	if(currMagnitude<Variables.minRenderableFlowWidth){    
            		//System.out.println("è minore");
            		currMagnitude=Variables.minRenderableFlowWidth;
            	}
            	
            	
            	//System.out.println("magnitude: "+currMagnitude);
            	Color currColor = this.colors.get(i);
            	dc.getGL().getGL2().glColor4ub((byte) currColor.getRed(), (byte) currColor.getGreen(),
                        (byte) currColor.getBlue(), (byte) currColor.getAlpha());
            	gl.glLineWidth((float)currMagnitude);
                if (span == null)
                    continue;

                // Since segments can very often be very short -- two vertices -- use explicit rendering. The
                // overhead of batched rendering, e.g., gl.glDrawArrays, is too high because it requires copying
                // the vertices into a DoubleBuffer, and DoubleBuffer creation and access performs relatively poorly.
            
     
               	
            
               	
       //     System.out.println("inizio span");
           //   	drawWidthLine(gl, span.get(1), span.get(0),  dc, -currMagnitude/2);
                for (int j=1;j<span.size();j++){
                	Vec4 p = span.get(j);
                	Vec4 pred = span.get(j-1);
                	
                	//System.out.println("draw line da "+p+" a "+p2);
                    	//drawCylinder(gl, p.x, p.y, p.z, p2.x, p2.y, p2.z, currMagnitude/2);
                	 
                	
                	// Apply texture.                	
                   // earthTexture.enable();
                   // earthTexture.bind();
                    
                	drawWidthLine(gl, pred, p,  dc, currMagnitude/2);
           //     	drawDisk(gl, p2,p, currMagnitude/2);
                	//drawCylinder(gl, p.x, p.y, p.z, p2.x, p2.y, p2.z, currMagnitude/2);
                	
                
                }
            	drawWidthLine(gl, span.get(span.size()-1), span.get(span.size()-2),  dc, -currMagnitude/2);  
         
                
       
            
           
                /*
                gl.glBegin(primType);
                for (Vec4 p : span)
                {
                    gl.glVertex3d(p.x, p.y, p.z);
                }
                gl.glEnd();
                */
            }
       	 gl.glEnd();
         
            
            gl.glDisable(GL.GL_BLEND);               
            gl.glDisable(GL.GL_TEXTURE_2D);                
        	gl.glEnable(GL.GL_DEPTH_TEST);
            
            /*
            if (this.highlighted)
            {
                if (!dc.isPickingMode())
                {
                    if (this.highlightColor.getAlpha() != 255)
                    {
                        gl.glEnable(GL.GL_BLEND);
                        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                    }
                    dc.getGL().glColor4ub((byte) this.highlightColor.getRed(), (byte) this.highlightColor.getGreen(),
                        (byte) this.highlightColor.getBlue(), (byte) this.highlightColor.getAlpha());

                    gl.glLineWidth((float) this.lineWidth + 2);
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
            */
        }
        finally
        {
            if (projectionOffsetPushed)
                dc.popProjectionOffest();

            gl.glPopAttrib();
            dc.getView().popReferenceCenter(dc);
        }
    }

    /*
    public float[] getFirstPerpVector(float x, float y, float z) {
    	  float[] result = {0.0f,0.0f,0.0f};
    	  // That's easy.
    	  if (x == 0.0f || y == 0.0f || z == 0.0f) {
    	    if (x == 0.0f)
    	      result[0] = 1.0f;
    	    else if (y == 0.0f)
    	      result[1] = 1.0f;
    	    else
    	      result[2] = 1.0f;
    	  }
    	  else {
    	    // If xyz is all set, we set the z coordinate as first and second argument .
    	    // As the scalar product must be zero, we add the negated sum of x and y as third argument
    	    result[0] = z;      //scalp = z*x
    	    result[1] = z;      //scalp = z*(x+y)
    	    result[2] = -(x+y); //scalp = z*(x+y)-z*(x+y) = 0
    	    // Normalize vector
    	    float length = 0.0f;
    	    for (float f : result)
    	      length += f*f;
    	    length = (float) Math.sqrt(length);
    	    for (int i=0; i<3; i++)
    	      result[i] /= length;
    	  }
    	  return result;
    	}
    	*/
/*
    	public void drawCylinder(GL gl, float x1, float y1, float z1, float x2, float y2, float z2) {
    	  final int X = 0,
    	            Y = 1,
    	            Z = 2;
    	  // Get components of difference vector
    	  float x = x1-x2,
    	        y = y1-y2,
    	        z = z1-z2;
    	  float[] firstPerp = getFirstPerpVector(x,y,z);
    	  // Get the second perp vector by cross product
    	  float[] secondPerp = new float[3];
    	  secondPerp[X] = y*firstPerp[Z]-z*firstPerp[Y];
    	  secondPerp[Y] = z*firstPerp[X]-x*firstPerp[Z];
    	  secondPerp[Z] = x*firstPerp[Y]-y*firstPerp[X];
    	  // Normalize vector
    	  float length = 0.0f;
    	  for (float f : secondPerp)
    	    length += f*f;
    	  length = (float) Math.sqrt(length);
    	  for (int i=0; i<3; i++)
    	    secondPerp[i] /= length;

    	  // Having now our vectors, here we go:
    	  // First points; you can have a cone if you change the radius R1

    	  final int ANZ = 32;  // number of vertices
    	  final float FULL = (float) (2.0f*Math.PI),
    	              R1   = 4.0f; // radius
    	  float[][] points = new float[ANZ+1][3];
    	  for (int i=0; i<ANZ; i++) {
    	    float angle = FULL*(i/(float) ANZ);

    	    points[i][X] = (float) (R1*(Math.cos(angle)*firstPerp[X]+Math.sin(angle)*secondPerp[X]));
    	    points[i][Y] = (float) (R1*(Math.cos(angle)*firstPerp[Y]+Math.sin(angle)*secondPerp[Y]));
    	    points[i][Z] = (float) (R1*(Math.cos(angle)*firstPerp[Z]+Math.sin(angle)*secondPerp[Z]));
    	  }
    	  // Set last to first
    	  System.arraycopy(points[0],0,points[ANZ],0,3);

    	  gl.glColor3f(1.0f,0.0f,0.0f);
    	  gl.glBegin(GL.GL_TRIANGLE_FAN);
    	  gl.glVertex3f(x1,y1,z1);
    	  for (int i=0; i<=ANZ; i++) {
    	    gl.glVertex3f(x1+points[i][X],
    	                  y1+points[i][Y],
    	                  z1+points[i][Z]);
    	  }
    	  gl.glEnd();

    	  gl.glBegin(GL.GL_TRIANGLE_FAN);
    	  gl.glVertex3f(x2,y2,z2);
    	  for (int i=0; i<=ANZ; i++) {
    	    gl.glVertex3f(x2+points[i][X],
    	                  y2+points[i][Y],
    	                  z2+points[i][Z]);
    	  }
    	  gl.glEnd();

    	  gl.glBegin(GL.GL_QUAD_STRIP);
    	  for (int i=0; i<=ANZ; i++) {
    	    gl.glVertex3f(x1+points[i][X],
    	                  y1+points[i][Y],
    	                  z1+points[i][Z]);
    	    gl.glVertex3f(x2+points[i][X],
    	                  y2+points[i][Y],
    	                  z2+points[i][Z]);
    	  }
    	  gl.glEnd();      
    	}
*/
    public void drawDisk(GL2 gl, Vec4 p1 ,Vec4 p2,double radius) {
    	 final int X = 0,
   	            Y = 1,
   	            Z = 2;
    	 
    	double x1 = p1.getX();
    	double y1 = p1.getY();
    	double z1 = p1.getZ();
    	double x2 = p2.getX();
    	double y2 = p2.getY();
    	double z2 = p2.getZ();
    	 
   	  // Get components of difference vector
   	double x = x1-x2,
   	        y = y1-y2,
   	        z = z1-z2;
   	double[] diffVector = {x,y,z};
    // Normalize vector
   	double length = 0.0;
   	  for (double f : diffVector)
   	    length += f*f;
   	  length = (double) Math.sqrt(length);
   	  for (int i=0; i<3; i++)
   		diffVector[i] /= length;
   	
   	
   	double[] firstPerp = getFirstPerpVector(x,y,z);
   
   	  // Get the second perp vector by cross product
   	double[] secondPerp = new double[3];
   	  secondPerp[X] = y*firstPerp[Z]-z*firstPerp[Y];
   	  secondPerp[Y] = z*firstPerp[X]-x*firstPerp[Z];
   	  secondPerp[Z] = x*firstPerp[Y]-y*firstPerp[X];
   	  
   	
   	  
   	  // Normalize vector
   	 length = 0.0;
   	  for (double f : secondPerp)
   	    length += f*f;
   	  length = (double) Math.sqrt(length);
   	  for (int i=0; i<3; i++)
   	    secondPerp[i] /= length;

   	  // Having now our vectors, here we go:
   	  // First points; you can have a cone if you change the radius R1

   	  final int ANZ = 32;  // number of vertices
   	  final double FULL = (double) (2.0*Math.PI);
   	             
   	double[][] points = new double[ANZ+1][3];
   	  for (int i=0; i<ANZ; i++) {
   		double angle = FULL*(i/(double) ANZ);

   	    points[i][X] = (double) (radius/2*(Math.cos(angle)*diffVector[X])+radius*(Math.sin(angle)*secondPerp[X]));
   	    points[i][Y] = (double) (radius/2*(Math.cos(angle)*diffVector[Y])+radius*(Math.sin(angle)*secondPerp[Y]));
   	    points[i][Z] = (double) (radius/2*(Math.cos(angle)*diffVector[Z])+radius*(Math.sin(angle)*secondPerp[Z]));
   	  }
   	  // Set last to first
   	  System.arraycopy(points[0],0,points[ANZ],0,3);

   	//  gl.glColor3d(1.0,0.0,0.0);
   	  gl.glBegin(GL2.GL_TRIANGLE_FAN);
   	  gl.glVertex3d(x1,y1,z1);
   	  for (int i=0; i<=ANZ; i++) {
   	    gl.glVertex3d(x1+points[i][X],
   	                  y1+points[i][Y],
   	                  z1+points[i][Z]);
   	  }
   	  gl.glEnd();

   	  /*
   	  gl.glBegin(GL.GL_TRIANGLE_FAN);
   	  gl.glVertex3d(x2,y2,z2);
   	  for (int i=0; i<=ANZ; i++) {
   	    gl.glVertex3d(x2+points[i][X],
   	                  y2+points[i][Y],
   	                  z2+points[i][Z]);
   	  }
   	  gl.glEnd();
*/
   	  /*
   	  gl.glBegin(GL.GL_QUAD_STRIP);
   	  for (int i=0; i<=ANZ; i++) {
   	    gl.glVertex3d(x1+points[i][X],
   	                  y1+points[i][Y],
   	                  z1+points[i][Z]);
   	    gl.glVertex3d(x2+points[i][X],
   	                  y2+points[i][Y],
   	                  z2+points[i][Z]);
   	  }
   	  gl.glEnd();   
   	  */
  	}
    
    
    	private double[] getFirstPerpVector(double x, double y, double z) {
    		double[] result = {0.0,0.0,0.0};
      	  // That's easy.
      	  if (x == 0.0 || y == 0.0 || z == 0.0) {
      	    if (x == 0.0)
      	      result[0] = 1.0;
      	    else if (y == 0.0)
      	      result[1] = 1.0;
      	    else
      	      result[2] = 1.0;
      	  }
      	  else {
      	    // If xyz is all set, we set the z coordinate as first and second argument .
      	    // As the scalar product must be zero, we add the negated sum of x and y as third argument
      	    result[0] = z;      //scalp = z*x
      	    result[1] = z;      //scalp = z*(x+y)
      	    result[2] = -(x+y); //scalp = z*(x+y)-z*(x+y) = 0
      	    // Normalize vector
      	  double length = 0.0;
      	    for (double f : result)
      	      length += f*f;
      	    length = (double) Math.sqrt(length);
      	    for (int i=0; i<3; i++)
      	      result[i] /= length;
      	  }
      	  return result;
      	}

      	private void drawCylinder(GL2 gl, double x1, double y1, double z1, double x2, double y2, double z2,double radius) {
      	  final int X = 0,
      	            Y = 1,
      	            Z = 2;
      	  // Get components of difference vector
      	double x = x1-x2,
      	        y = y1-y2,
      	        z = z1-z2;
      	double[] firstPerp = getFirstPerpVector(x,y,z);
      	  // Get the second perp vector by cross product
      	double[] secondPerp = new double[3];
      	  secondPerp[X] = y*firstPerp[Z]-z*firstPerp[Y];
      	  secondPerp[Y] = z*firstPerp[X]-x*firstPerp[Z];
      	  secondPerp[Z] = x*firstPerp[Y]-y*firstPerp[X];
      	  // Normalize vector
      	double length = 0.0;
      	  for (double f : secondPerp)
      	    length += f*f;
      	  length = (double) Math.sqrt(length);
      	  for (int i=0; i<3; i++)
      	    secondPerp[i] /= length;

      	  // Having now our vectors, here we go:
      	  // First points; you can have a cone if you change the radius R1

      	  final int ANZ = 32;  // number of vertices
      	  final double FULL = (double) (2.0*Math.PI);
      	             
      	double[][] points = new double[ANZ+1][3];
      	  for (int i=0; i<ANZ; i++) {
      		double angle = FULL*(i/(double) ANZ);

      	    points[i][X] = (double) (radius*(Math.cos(angle)*firstPerp[X]+Math.sin(angle)*secondPerp[X]));
      	    points[i][Y] = (double) (radius*(Math.cos(angle)*firstPerp[Y]+Math.sin(angle)*secondPerp[Y]));
      	    points[i][Z] = (double) (radius*(Math.cos(angle)*firstPerp[Z]+Math.sin(angle)*secondPerp[Z]));
      	  }
      	  // Set last to first
      	  System.arraycopy(points[0],0,points[ANZ],0,3);

      	//  gl.glColor3d(1.0,0.0,0.0);
      	  gl.glBegin(GL.GL_TRIANGLE_FAN);
      	  gl.glVertex3d(x1,y1,z1);
      	  for (int i=0; i<=ANZ; i++) {
      	    gl.glVertex3d(x1+points[i][X],
      	                  y1+points[i][Y],
      	                  z1+points[i][Z]);
      	  }
      	  gl.glEnd();

      	  gl.glBegin(GL.GL_TRIANGLE_FAN);
      	  gl.glVertex3d(x2,y2,z2);
      	  for (int i=0; i<=ANZ; i++) {
      	    gl.glVertex3d(x2+points[i][X],
      	                  y2+points[i][Y],
      	                  z2+points[i][Z]);
      	  }
      	  gl.glEnd();

      	  gl.glBegin(GL2.GL_QUAD_STRIP);
      	  for (int i=0; i<=ANZ; i++) {
      	    gl.glVertex3d(x1+points[i][X],
      	                  y1+points[i][Y],
      	                  z1+points[i][Z]);
      	    gl.glVertex3d(x2+points[i][X],
      	                  y2+points[i][Y],
      	                  z2+points[i][Z]);
      	  }
      	  gl.glEnd();      
      	}

    
    	
      	private void drawWidthLine2(GL2 gl,Vec4 point1,Vec4 point2, DrawContext dc,double radius){
        	
      	//	point1=point1.add3(referenceCenterPoint);
      	//	point2=point2.add3(referenceCenterPoint);
      		
      	  		point1=point1.subtract3(referenceCenterPoint);
          		point2=point2.subtract3(referenceCenterPoint);
        
      		
      		 // Compute side points at the start of the line.
      		Vec4 r1 = this.generateParallelPoints(point1, null, point2,  radius, dc.getGlobe());
      		 // Compute side points at the start of the line.
      		Vec4 r2 = this.generateParallelPoints(point1, null, point2,  -radius, dc.getGlobe());
      		// Compute side points at the end of the line.
      		Vec4 r3 = generateParallelPoints(point2, point1, null, radius, dc.getGlobe());
      	// Compute side points at the end of the line.
      		Vec4 r4 = generateParallelPoints(point2, point1, null, -radius, dc.getGlobe());
      		
      		
      		
      		/*
      		Vec4 r1 = generateParallelPoints(point1, point1, point2, radius, dc.getGlobe());
      		Vec4 r2 = generateParallelPoints(point1, point1, point2, -radius, dc.getGlobe());
      		Vec4 r3 = generateParallelPoints(point2, point1, point2, radius, dc.getGlobe());
      		Vec4 r4 = generateParallelPoints(point2, point1, point2, -radius, dc.getGlobe());
      		*/
      		
      				
      		r1=r1.add3(referenceCenterPoint);
      		r2=r2.add3(referenceCenterPoint);
      		r3=r3.add3(referenceCenterPoint);
      		r4=r4.add3(referenceCenterPoint);
      	
      		/*
      		System.out.println("aaaaaa");
      		System.out.println(point1+" pos: "+dc.getModel().getGlobe().computePositionFromPoint(point1).elevation);
      		System.out.println(point2+" pos: "+dc.getModel().getGlobe().computePositionFromPoint(point2).elevation);
      		System.out.println(r1+" pos: "+dc.getModel().getGlobe().computePositionFromPoint(r1).elevation);
      		System.out.println(r2+" pos: "+dc.getModel().getGlobe().computePositionFromPoint(r2).elevation);
      		System.out.println(r3+" pos: "+dc.getModel().getGlobe().computePositionFromPoint(r3).elevation);
      		System.out.println(r4+" pos: "+dc.getModel().getGlobe().computePositionFromPoint(r4).elevation);
      		System.out.println("bbbb");
      		*/
      		/*
      		 * 
      		 *
      		System.out.println("p1: "+point1);
      		System.out.println("p2: "+point2);
      		System.out.println("r1: "+r1);
      		System.out.println("r2: "+r2);
      		System.out.println("r3: "+r3);
      		System.out.println("r4: "+r4);
      		*/
      		/*
      		gl.glBegin (GL.GL_LINES);
      	     gl.glVertex3d (point1.x, point1.y, point1.z);
      	     gl.glVertex3d (point2.x, point2.y, point2.z);
      	   gl.glEnd();
      		*/
      		
      		/*
      	 gl.glBegin (GL.GL_LINES);
  	     gl.glVertex3d (r1.x, r1.y, r1.z);
  	     gl.glVertex3d (r3.x, r3.y, r3.z);
  	   gl.glEnd();
  		*/
      	   /*
      		 gl.glBegin (GL.GL_LINES);
      	     gl.glVertex3d (r2.x, r2.y, r2.z);
      	     gl.glVertex3d (r4.x, r4.y, r4.z);
      	   gl.glEnd();
        	*/
      //	   gl.glEnable(GL.GL_DEPTH_FUNC);
      //		gl.glDepthFunc(GL.GL_LESS);
      		
      		
      		if (texture != null)
            {
                gl.glEnable(GL.GL_TEXTURE_2D);
                texture.bind(gl);

               // gl.glColor4d(1d, 1d, 1d, 1);
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
              //  TextureCoords texCoords = texture.getImageTexCoords();
             //   gl.glScaled(1, 1, 1d);
             //   dc.drawUnitQuad(texCoords);
            }
      		
        		
        	gl.glBegin(GL2.GL_QUADS);
        	gl.glTexCoord2d(0.0, 0.0);
        	gl.glVertex3d(r1.x,r1.y,r1.z);
        				//gl.glTexCoord2d (1.0, 0.0);
        	gl.glTexCoord2d (0.0, 1.0);
        	gl.glVertex3d(r3.x,r3.y,r3.z);
        	gl.glTexCoord2d (1.0, 1.0);
        	gl.glVertex3d(r4.x,r4.y,r4.z);
        					//gl.glTexCoord2d (0.0, 1.0);
        	gl.glTexCoord2d (1.0, 0.0);
        	gl.glVertex3d(r2.x,r2.y,r2.z);
        	gl.glEnd();
        //	  gl.glDisable(GL.GL_DEPTH_FUNC);
        
      	/*
        	//gl.glTexCoord2d(0.0, 0.0);
        	gl.glVertex3d(r1.x,r1.y,r1.z);
        				//gl.glTexCoord2d (1.0, 0.0);
        	//gl.glTexCoord2d (0.0, 1.0);
        	gl.glVertex3d(r3.x,r3.y,r3.z);
        	//gl.glTexCoord2d (1.0, 1.0);
        	gl.glVertex3d(r4.x,r4.y,r4.z);
        					//gl.glTexCoord2d (0.0, 1.0);
        	//gl.glTexCoord2d (1.0, 0.0);
        	gl.glVertex3d(r2.x,r2.y,r2.z);
        	*/
        	
        	
        	
        	gl.glDisable(GL.GL_TEXTURE_2D);
      	
      	}
      	
    	private void drawWidthLine(GL2 gl,Vec4 point1,Vec4 point2, DrawContext dc,double radius){
        	
          	//	point1=point1.add3(referenceCenterPoint);
          	//	point2=point2.add3(referenceCenterPoint);
          		
          	  		point1=point1.subtract3(referenceCenterPoint);
              		point2=point2.subtract3(referenceCenterPoint);
            
          		
          		 // Compute side points at the start of the line.
          		Vec4 r1 = this.generateParallelPoints(point1, null, point2,  radius, dc.getGlobe());
          		 // Compute side points at the start of the line.
          		Vec4 r2 = this.generateParallelPoints(point1, null, point2,  -radius, dc.getGlobe());
          		// Compute side points at the end of the line.
      //    		Vec4 r3 = generateParallelPoints(point2, point1, null, radius, dc.getGlobe());
          	// Compute side points at the end of the line.
        //  		Vec4 r4 = generateParallelPoints(point2, point1, null, -radius, dc.getGlobe());
          		
          		
         		
          		
          		r1=r1.add3(referenceCenterPoint);
          		r2=r2.add3(referenceCenterPoint);
        //  		r3=r3.add3(referenceCenterPoint);
         // 		r4=r4.add3(referenceCenterPoint);
        
          	/*
          		if (texture != null)
                {
                    gl.glEnable(GL.GL_TEXTURE_2D);
                    texture.bind();

                   // gl.glColor4d(1d, 1d, 1d, 1);
                    gl.glEnable(GL.GL_BLEND);
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                  //  TextureCoords texCoords = texture.getImageTexCoords();
                 //   gl.glScaled(1, 1, 1d);
                 //   dc.drawUnitQuad(texCoords);
                }
          	*/
          //		System.out.println("ref: "+point1+" pred: "+point2+" "+r1+" "+r2);
          		
            	gl.glTexCoord2d(0.0, 0.0);
            	gl.glVertex3d(r1.x,r1.y,r1.z);
            	gl.glTexCoord2d (1.0, 0.0);
            	gl.glVertex3d(r2.x,r2.y,r2.z);
            				//gl.glTexCoord2d (1.0, 0.0);
            	//gl.glTexCoord2d (0.0, 1.0);
            //	gl.glVertex3d(r3.x,r3.y,r3.z);
            	//gl.glTexCoord2d (1.0, 1.0);
           // 	gl.glVertex3d(r4.x,r4.y,r4.z);
            					//gl.glTexCoord2d (0.0, 1.0);
            	//gl.glTexCoord2d (1.0, 0.0);
            	//gl.glVertex3d(r2.x,r2.y,r2.z);
            	
            	
            	
            	
          //  	gl.glDisable(GL.GL_TEXTURE_2D);
          	
          	}
      	
/*
    private void drawWidthLine(Vec4 point1,Vec4 point2, double width,GL gl){
    	//Calculate a vector between start and end points
    	Vec4 v = new Vec4(point2.x - point1.y,point2.x - point1.y);
    	

    	//Then calculate a perpendicular to it (just swap X and Y coordinates)
    	Vec4 p = new Vec4(v.y,-v.x);
    	

    	//Normalize that perpendicular
    	
    	double length = Math.sqrt(p.x * p.x + p.y * p.y); //Thats length of perpendicular
    	Vec4 n = new Vec4(p.x / length,p.y / length);
    	

    	//Calculate 4 points that form a rectangle by adding normalized perpendicular and multiplying it by half of the desired width
    	Vec4 r1 = new Vec4(point1.x + n.x * width / 2,point1.y + n.y * width / 2);
    	Vec4 r2 = new Vec4(point1.x - n.x * width / 2,point1.y - n.y * width / 2);
    	Vec4 r3 = new Vec4(point2.x + n.x * width / 2,point2.y + n.y * width / 2);
    	Vec4 r4 = new Vec4(point2.x - n.x * width / 2,point2.y - n.y * width / 2);
    	
    	gl.glBegin(gl.GL_QUADS);
    	
//    	gl.glColor4f(((rgba>>24)&0xff)/255.0f,
//    	          ((rgba>>16)&0xff)/255.0f, 
//    	          ((rgba>>8)&0xff)/255.0f,
//    	          (rgba&0xff)/255.0f);
//    	          
    	gl.glVertex3d(r1.x,r1.y,0);
    	gl.glVertex3d(r2.x,r2.y,0);
    	gl.glVertex3d(r3.x,r3.y,0);
    	gl.glVertex3d(r4.x,r4.y,0);
    	gl.glEnd();
    }
*/

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
    
    double[] createXPoints(ArrayList<Position> positions){
    	
    	double[] xpoints = new double[positions.size()];
    	
    	for(int i=0;i<positions.size();i++){
    		xpoints[i]=positions.get(i).getLongitude().degrees;
    	}
    	
    	return xpoints;
    }
    
double[] createYPoints(ArrayList<Position> positions){
    	
    	double[] ypoints = new double[positions.size()];
    	
    	for(int i=0;i<positions.size();i++){
    		ypoints[i]=positions.get(i).getLatitude().degrees;
    	}
    	
    	return ypoints;
    }
    
    //funzione da modificare
    private void makeVertices(DrawContext dc)
    {
    //	System.out.println("clear in make vertices");
    	
        if (this.currentSpans == null)
            this.currentSpans = new ArrayList<List<Vec4>>();
        else
            this.currentSpans.clear();

        if (this.magnitude == null)
            this.magnitude = new ArrayList<Double>();
        else
            this.magnitude.clear();
        
        if (this.colors == null)
            this.colors = new ArrayList<Color>();
        else
            this.colors.clear();
        
        if (renderableControlPoints.positions.size() < 1)
            return;

       // readControlPoints();
        
        ArrayList<Position> updPositions = updatePositions(renderableControlPoints.positions,dc);
        
        int numControlPoints = renderableControlPoints.magnitudes.size();
    	//System.out.println("num points: "+numControlPoints);
     
    	
    	double[] xpoints = createXPoints(renderableControlPoints.positions);
        double[] ypoints = createYPoints(renderableControlPoints.positions);
        
        
        double[] updXpoints = createXPoints(updPositions);
        double[] updYpoints = createYPoints(updPositions);
        
        
        
     //   createMultipleParametricCurves(renderableControlPoints.positions, dc);
        
       
      //  cubicCurveFunctionsX=createCubicCurveFunctions(renderableControlPoints.positions.size()-1, updXpoints);
      //  cubicCurveFunctionsY=createCubicCurveFunctions(renderableControlPoints.positions.size()-1, updYpoints);
       
        
        cubicCurveFunctionsX=createCubicCurveFunctions(renderableControlPoints.positions.size()-1, xpoints,updXpoints);
        cubicCurveFunctionsY=createCubicCurveFunctions(renderableControlPoints.positions.size()-1, ypoints,updYpoints);
        
       // System.out.println("num cubic curves: "+cubicCurveFunctionsX.length);
        
        /*
       for(double magn : this.renderableControlPoints.magnitudes){
    	   System.out.println("magn control p : "+magn);
       }
       */
        /*
        cubicCurveFunctionsX=createCubicClosedCurveFunctions(renderableControlPoints.positions.size()-1, xpoints);
        cubicCurveFunctionsY=createCubicClosedCurveFunctions(renderableControlPoints.positions.size()-1, ypoints);
        */
      //  System.out.println("CREO CURVA");
       
       //starts from the leaf
        Position posA = renderableControlPoints.positions.get(0);
        
        
        //Vec4 ptA = this.computePoint(dc, posA, true);
        Vec4 ptA = this.computePoint(dc, posA, computeOffest);
        
            
        for (int i = 1; i <= renderableControlPoints.positions.size(); i++)
        {
        	
        	 Position newPosB  = null;
            Position posB;
            if (i < renderableControlPoints.positions.size()){
                posB = renderableControlPoints.positions.get(i);
                newPosB =   updPositions.get(i);
            }
            else if (this.closed)
                posB = renderableControlPoints.positions.get(0);
            else
                break;

            
            //Vec4 ptB = this.computePoint(dc, posB, true);
            Vec4 ptB = this.computePoint(dc, posB, computeOffest);
            
           
            if (this.followTerrain && !this.isSegmentVisible(dc, posA, posB, ptA, ptB))
            {
                posA = posB;
                ptA = ptB;
                continue;
            }

            /*
            Vec4 newptB=ptB; 
            Position newPosB=posB;
            //modifico pos di pta
            if(i>1){
            	 Vec4 ptPrev = this.computePoint(dc, renderableControlPoints.positions.get(i-2), true);
            	 double prevMagnitude  = this.renderableControlPoints.prevMagnitudes.get(i-1);
            	 double magnitude  = this.renderableControlPoints.magnitudes.get(i-1);
            	
            	 if(prevMagnitude!=magnitude){
            		 double shifts  = this.renderableControlPoints.shifts.get(i-1);
            		 System.out.println("magn: "+magnitude+" prevMagn: "+prevMagnitude+" shift: "+shifts);
            		 double newPos = (shifts*2)+(magnitude);
            		 double diff= prevMagnitude-newPos;
            		 System.out.println("DIFF: "+diff);
            		// newptB = movePointInPerpendicular(ptPrev,ptB,diff);
            		 newptB = generateParallelPoints(ptB, ptPrev, null, diff, dc.getGlobe());
            		 newPosB = dc.getGlobe().computePositionFromPoint(newptB);
            		 //newptB = ptB;
            		 //System.out.println("muovo: "+ptB+" in "+newptB);
            	 }else{
            		 //System.out.println("equal: "+magnitude);
            	 }
            }
            */
            //System.out.println("creo subcurva da: "+posA+" a "+posB);
            ArrayList<Vec4> span;
          //  System.out.println("sd");            
            /*
           if(posA.elevation!=20000){
           System.out.println("posA: "+posA.elevation);
           }
           if(posB.elevation!=20000){
           System.out.println("posB: "+posB.elevation);
           }
           if(newPosB.elevation!=20000){
               System.out.println("newPosB: "+newPosB.elevation);
               }
*/
      	
           // span = this.makeSegment(dc, posA, newPosB, ptA, ptB,cubicCurveFunctionsX[i-1],cubicCurveFunctionsY[i-1]);
            span = this.makeSegment(dc, posA, newPosB, ptA, ptB,cubicCurveFunctionsX[i-1],cubicCurveFunctionsY[i-1]);

            //printSpan(span);
            
            if (span != null){
            //aggiungere condizione: se non è già stata renderizzata
            boolean toRender = this.renderableControlPoints.toRender.get(i-1);
            double magnitude  = this.renderableControlPoints.magnitudes.get(i-1);
            Color color = this.renderableControlPoints.colors.get(i-1);
            	if(toRender){
            	this.addSpan(span);
            	//System.out.println(i+" addMagnitude: "+magnitude);
            	this.magnitude.add( magnitude);
            	this.colors.add( color);
            }
            
            }
            posA = posB;
            ptA = ptB;
        }
        
        
        
     //   System.out.println("PRINT SPAN");
       // printSpans(dc,currentSpans);
        
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
    
    private  void readControlPoints(){
    	System.out.println("READ CONTROL POINTS");
    	for( double m : renderableControlPoints.magnitudes){
    		System.out.println("magn: "+m);
    	}
    }
    
    private  ArrayList<Position> updatePositions(ArrayList<Position> positions, DrawContext dc){
    	
    	
    //	System.out.println("in updatePositions");
    	
    	int i=0;
    	ArrayList<Position> newPositions= new ArrayList<Position>();
    	
    	int numControlPoints = renderableControlPoints.magnitudes.size();
    	
    //	System.out.println("control points");
    	
    	for( Position p : positions){
    	
    	//	System.out.println(p);
    		
    		if(i<=0){
    			newPositions.add(p);
    		}
    		else
    		
    		/*
    		if(i==positions.size()-1){
    			newPositions.add(p);
    		}
    		else
    		*/
    		{
    			
    			/*
    			 Vec4 ptPrev = this.computePoint(dc, renderableControlPoints.positions.get(i-1), true);
    			 Vec4 currPt = this.computePoint(dc, renderableControlPoints.positions.get(i), true);
            	 */
    			
    	//		System.out.println("in updatePositions: ");
    			 Vec4 ptPrev = this.computePoint(dc, renderableControlPoints.positions.get(i-1), false);
    			 Vec4 currPt = this.computePoint(dc, renderableControlPoints.positions.get(i), false);
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
            		 
            	//	 System.out.println("modifico una posizione");
            		 double shifts  = this.renderableControlPoints.shifts.get(i-1);
            		// System.out.println("magn: "+magnitude+" prevMagn: "+prevMagnitude+" shift: "+shifts);
            		 double newPos = (-shifts)+(magnitude/2);
            		 double diff= newPos-(prevMagnitude/2);
            		// System.out.println("DIFF: "+diff);
            		// newptB = movePointInPerpendicular(ptPrev,ptB,diff);
            		
             		// System.out.println("->b: "+dc.getGlobe().computePositionFromPoint(ptPrev).elevation);
          			//System.out.println("->b: "+dc.getGlobe().computePositionFromPoint(currPt).elevation);
          		
            		 Vec4 newptB = generateParallelPoints(currPt, ptPrev, null, diff, dc.getGlobe());
            		 Position newPosB = dc.getGlobe().computePositionFromPoint(newptB);
    			
    	 			
            //		 System.out.println("-> newPosB: "+newPosB);
            		 
    			newPositions.add(newPosB);
            	 }else{
            		 newPositions.add(p); 
            	 }
    		}
    		    		
    		i++;
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

    
    //funzione da modificare
    protected ArrayList<Vec4> makeSegment(DrawContext dc, Position posA, Position posB, Vec4 ptA, Vec4 ptB,Cubic cubicCurveFunctionX, Cubic cubicCurveFunctionY)
    {
    	//System.out.println("Make Segment");
        ArrayList<Vec4> span = null;

        double arcLength = this.computeSegmentLength(dc, posA, posB);
        if (arcLength <= 0) // points differing only in altitude
        {
            span = this.addPointToSpan(ptA, span);
            if (!ptA.equals(ptB))
                span = this.addPointToSpan(ptB, span);
            return span;
        }
        /*
        for (double s = 0, p = 0; s < 1; )
        {

                p += arcLength / this.numSubsegments;

            s = p / arcLength;
*/
        for(int i =0;i<=this.numSubsegments;i++){
            //s va da 0 a 1            
        	double s = (double)i/(double)this.numSubsegments;
        	
            Position pos;
            if (s >= 1)
            {
                pos = posB;
            }
            else 
            {
            	/*
                double longitudeDegree=cubicCurveFunctionX.eval(s);
                double latitudeDegree=cubicCurveFunctionY.eval(s);
                */
            	LatLon latLon;
            	
            	double longitudeDegree=cubicCurveFunctionX.eval(s);
                double latitudeDegree=cubicCurveFunctionY.eval(s);
              
            	
                //System.out.println(s+": "+latitudeDegree+" "+longitudeDegree);
                
                 latLon = LatLon.fromDegrees(latitudeDegree, longitudeDegree);
            	//System.out.println(s+": "+latLon.latitude.+" "+longitudeDegree);
                
                pos = new Position(latLon, (1 - s) * posA.getElevation() + s * posB.getElevation());
                }
                        

            //ptB = this.computePoint(dc, pos, true);
            ptB = this.computePoint(dc, pos, computeOffest);
        

            //System.out.println("Position: "+pos.latitude.degrees+ " "+pos.longitude.degrees+ "  Point: "+ptB.x+" "+ptB.y+" "+ptB.z);
            
            span = this.clipAndAdd(dc, ptA, ptB, span);

            ptA = ptB;
        }

        return span;
    }

    
    @SuppressWarnings({"UnusedDeclaration"})
    protected ArrayList<Vec4> clipAndAdd(DrawContext dc, Vec4 ptA, Vec4 ptB, ArrayList<Vec4> span)
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

        span.add(p.subtract3(this.referenceCenterPoint));
      //  span.add(p);

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

        this.referenceCenterPoint = dc.computeTerrainPoint(refPos.getLatitude(), refPos.getLongitude(),
            this.offset);
        
        
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
        if (this.renderableControlPoints.positions.size() < 1)
        {
            return null;
        }
        else if (this.renderableControlPoints.positions.size() < 3)
        {
            return this.renderableControlPoints.positions.get(0);
        }
        else
        {
            return this.renderableControlPoints.positions.get(this.renderableControlPoints.positions.size() / 2);
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

        for (int i = 0; i < this.renderableControlPoints.positions.size(); i++)
        {
            Position pos = this.renderableControlPoints.positions.get(i);

            Angle distance = LatLon.greatCircleDistance(oldRef, pos);
            Angle azimuth = LatLon.greatCircleAzimuth(oldRef, pos);
            LatLon newLocation = LatLon.greatCircleEndPosition(position, azimuth, distance);
            double newElev = pos.getElevation() + elevDelta;

            this.renderableControlPoints.positions.set(i, new Position(newLocation, newElev));
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

        if (this.renderableControlPoints.positions != null)
        {
            // Create the base "positions" state object.
            RestorableSupport.StateObject positionsStateObj = rs.addStateObject("positions");
            if (positionsStateObj != null)
            {
                for (Position p : this.renderableControlPoints.positions)
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
        rs.addStateValueAsBoolean("closed", this.closed);
        rs.addStateValueAsBoolean("highlighted", this.highlighted);
        rs.addStateValueAsInteger("pathType", this.pathType);
        rs.addStateValueAsBoolean("followTerrain", this.followTerrain);
        rs.addStateValueAsDouble("offset", this.offset);
    //    rs.addStateValueAsDouble("terrainConformance", this.terrainConformance);
        rs.addStateValueAsDouble("lineWidth", this.lineWidth);
        rs.addStateValueAsInteger("stipplePattern", this.stipplePattern);
        rs.addStateValueAsInteger("stippleFactor", this.stippleFactor);
        rs.addStateValueAsInteger("numSubsegments", this.numSubsegments);

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
            renderableControlPoints.positions=newPositions;
        }

        Integer antiAliasHintState = restorableSupport.getStateValueAsInteger("antiAliasHint");
        if (antiAliasHintState != null)
            setAntiAliasHint(antiAliasHintState);

        Boolean isFilledState = restorableSupport.getStateValueAsBoolean("filled");
        if (isFilledState != null)
            setFilled(isFilledState);

        Boolean isClosedState = restorableSupport.getStateValueAsBoolean("closed");
        if (isClosedState != null)
            setClosed(isClosedState);

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
    	
    
}
