/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.smeSpire.lines.cubicCurve;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.WWDotNetLayerSetConverter;
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
import it.graphitech.ColorsList;
import it.graphitech.colorGradient.ColorGradient;
import it.graphitech.core.MyAbstractSceneController;
import it.graphitech.smeSpire.RenderTextureOnScreen;
import it.graphitech.smeSpire.SectorManager;
import it.graphitech.smeSpire.SharedVariables;
import it.graphitech.smeSpire.layers.MyRenderableLayer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

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
public class CubicSplinePolyline extends AVListImpl implements Renderable,
// OrderedRenderable,
		Movable,
		// Restorable,
		MeasurableLength, ExtentHolder {

	public ArrayList<Vec4> infoPointsOnLens;
	// public ArrayList<Integer> indPointsToRemove;

	String from;
	String to;

	int transitDegree;
	int transitDegreeNodeTo;
	int transitDegreeNodeFrom;
	
	
	protected boolean followTerrain = false;
	protected boolean computeOffest = true;

	// boolean isAffectedByLense = false;
	boolean isFirstNodeInsideLense = false;
	boolean isLastNodeInsideLense = false;
	// boolean wasAffectedByLense = false;

	Vec4 firstNodeWorldPosition;
	Vec4 lastNodeWorldPosition;
	// Vec4 originalControlPoint=null;
	// Position originalControlPosition=null;

	public final static int GREAT_CIRCLE = WorldWind.GREAT_CIRCLE;
	public final static int LINEAR = WorldWind.LINEAR;
	public final static int RHUMB_LINE = WorldWind.RHUMB_LINE;
	public final static int LOXODROME = RHUMB_LINE;

	public final static int ANTIALIAS_DONT_CARE = WorldWind.ANTIALIAS_DONT_CARE;
	public final static int ANTIALIAS_FASTEST = WorldWind.ANTIALIAS_FASTEST;
	public final static int ANTIALIAS_NICEST = WorldWind.ANTIALIAS_NICEST;

	protected Color colorForUnmodifiedArc = Color.WHITE;
	protected Color colorArcInsideLens = Color.WHITE;
	protected Color[] colorsFormodifiedArc = null;
	public Color pickColor = new Color(0);
	private RenderableControlPoints renderableControlPoints;
	private RenderableControlPoints updRenderableControlPoints;
	// protected ArrayList<Position> positions;
	// protected Vec4 referenceCenterPoint;
	protected int antiAliasHint = GL.GL_FASTEST;
	// protected Color color = Color.WHITE;
	//protected double lineWidth = 1;
	protected boolean filled = false; // makes it a polygon

	protected double offset = 0;
	protected double terrainConformance = 10;
	protected int pathType = GREAT_CIRCLE;

	protected short stipplePattern = (short) 0xAAAA;
	protected int stippleFactor = 0;

	protected boolean highlighted = false;
	//protected Color highlightColor = new Color(1f, 1f, 1f, 0.5f);
	protected Object delegateOwner;
	protected LengthMeasurer measurer = new LengthMeasurer();
	// protected long geomGenTimeStamp = -Long.MAX_VALUE;
	// protected double geomGenVE = 1;
	// protected double eyeDistance;

	protected long frameNumber = -1; // identifies frame used to calculate these
										// values
	protected Layer pickLayer;

	int lineIndex;
	protected MyRenderableLayer myRendLayer;

	// protected List<List<Vec4>> currentSpans;
	protected List<Vec4> currentListOfVertices;

	protected Cubic[] cubicCurveFunctionsX;
	protected Cubic[] cubicCurveFunctionsY;
	protected Cubic[] cubicCurveFunctionsZ;
	// protected DoubleBuffer bufferPoints;

	protected int numVerticesPerLine;

	// protected List<List<Vec4>> currentUpdatedSpans;
	protected List<Vec4> currentListOfUpdatedVertices;
	protected Cubic[] updCubicCurveFunctionsX;
	protected Cubic[] updCubicCurveFunctionsY;
	protected Cubic[] updCubicCurveFunctionsZ;

	// protected DoubleBuffer bufferUpdatedPoints;
	protected FloatBuffer bufferPoints;
	protected FloatBuffer bufferUpdatedPoints;
	protected IntBuffer bufferIndexVectices;
	protected int bufferUpdPointSize;

	protected static class ExtentInfo {
		// The extent depends on the state of the globe used to compute it, and
		// the vertical exaggeration.
		protected Extent extent;
		protected double verticalExaggeration;
		protected Globe globe;
		protected Object globeStateKey;

		public ExtentInfo(Extent extent, DrawContext dc) {
			this.extent = extent;
			this.verticalExaggeration = dc.getVerticalExaggeration();
			this.globe = dc.getGlobe();
			this.globeStateKey = dc.getGlobe().getStateKey(dc);
		}

		protected boolean isValid(DrawContext dc) {
			return this.verticalExaggeration == dc.getVerticalExaggeration()
					&& this.globe == dc.getGlobe()
					&& globeStateKey.equals(dc.getGlobe().getStateKey(dc));
		}
	}

	protected HashMap<Globe, ExtentInfo> extents = new HashMap<Globe, ExtentInfo>(
			2); // usually only 1, but few at most

	private Color getColorFromNodePosition(String nodePos) {
		int index = myRendLayer.getNodeIndex(nodePos);
		return ColorsList.getCurrentColor(index);
	}

	public void drawNewOrderedRenderable(DrawContext dc) {
		GL2 gl = dc.getGL().getGL2();

		int attrBits = GL2.GL_HINT_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT;
		// if (!MyAbstractSceneController.isPreRenderingPhase)
		{
			// if (this.color.getAlpha() != 255)
			attrBits |= GL.GL_COLOR_BUFFER_BIT;
		}
		gl.glPushAttrib(attrBits);

		
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
			
				
		
		boolean isRenderUndesiredArcs = myRendLayer.isRenderUndesiredArcs();

		try {

			//origin inside lens -> filter arc
			if (isFirstNodeInsideLense() && !isLastNodeInsideLense()) {

				if (!isRenderUndesiredArcs) {
					//System.out.println("origin inside lens -> filter arc");
					filterArc(gl,  getFrom(), firstNodeWorldPosition,transitDegreeNodeFrom);
				}
			} else
				//destination inside lens -> filter arc
			if (isLastNodeInsideLense() && !isFirstNodeInsideLense()) {
				if (!isRenderUndesiredArcs) {
//System.out.println("destination inside lens -> filter arc");
					filterArc(gl,  getTo(), lastNodeWorldPosition,transitDegreeNodeTo);
				}
			} else
				//origin and destination inside lens -> draw arc
			if (isLastNodeInsideLense() && isFirstNodeInsideLense()) {

				if (!isRenderUndesiredArcs) {
					System.out.println("TRANSIT DEGREE: "+transitDegree);
				unfilterArc(gl);				
				}
			} else
//undesired arc -> deform arc
			if (isAffectedByLense() & SharedVariables.lensIsActive) {
				if (this.currentListOfUpdatedVertices == null)
					return;

				if (isRenderUndesiredArcs) {			
					deformArc(gl);
				}
//contex arc -> draw arc
			} else if (isRenderUndesiredArcs) {

				drawArc(gl);
				
				//SharedVariables.counterOfSimpleLines++;				
				//setColor(gl, colorForUnmodifiedArc);				
				//gl.glLineWidth((float) SharedVariables.lineWidth);
				//drawLines(gl, myRendLayer.getBufferAllPointsIndex(),
					//	numVerticesPerLine, lineIndex * numVerticesPerLine);
			
			}
		} finally {
			gl.glPopAttrib();
		}
	}
	private void deformArc(GL2 gl){
		SharedVariables.counterOfDistortedLines++;
		
		//setToModifiedLinesColor(gl, colorsFormodifiedArc[0]);
		//setColorBasedOnTransitDegree(gl);
		setColorWithTransparencyBasedOnTransitDegree(gl);
		gl.glLineWidth((float) SharedVariables.lineWidth);
		drawLines(gl, myRendLayer.getUpdBufferElement(lineIndex),
				bufferUpdPointSize, 0);

	}
	
	private void filterArc(GL2 gl,String node,Vec4 nodePos, int degreeNode){
	drawNode(gl, node, nodePos,degreeNode);
	
	
	SharedVariables.counterOfFilteredLines++;					
	myRendLayer.useShader(gl);
	
	
	//Color col = getColorFromNodePosition(node);
	//setColorToLinesPartiallyInsideTheLens(gl, col);
	
	setColorBasedOnTransitDegree(gl);
	
	setWidthToLinesOfInterest(gl);
	//gl.glLineWidth((float) SharedVariables.lineOfInterestWidth);
	
	drawLines(gl, myRendLayer.getBufferAllPointsIndex(),
			numVerticesPerLine, lineIndex * numVerticesPerLine);
	myRendLayer.dontUseShader();
	}

	
	private void unfilterArc(GL2 gl){
		SharedVariables.counterOfLinesInsideLens++;
		
		
		gl.glDisable(GL2.GL_DEPTH_TEST);
		
		drawNode(gl, getTo(), lastNodeWorldPosition, transitDegreeNodeTo);
		drawNode(gl, getFrom(), firstNodeWorldPosition,transitDegreeNodeFrom );
			
		
		//Color col = colorArcInsideLens;		
		//setColorToLinesCompletelyInsideLens(gl, col);
		
		setColorBasedOnTransitDegree(gl);
		setWidthToContextLines(gl);
		
		drawLines(gl, myRendLayer.getBufferAllPointsIndex(),
			numVerticesPerLine, lineIndex * numVerticesPerLine);

	// gl.glDepthMask(true);
	gl.glEnable(GL2.GL_DEPTH_TEST);

	}
		
	
	
private void drawArc(GL2 gl){
		
		SharedVariables.counterOfSimpleLines++;	
		
		
		setColorToContextLines(gl, colorForUnmodifiedArc);
		setWidthToContextLines(gl);
		drawLines(gl, myRendLayer.getBufferAllPointsIndex(),
				numVerticesPerLine, lineIndex * numVerticesPerLine);
	}
	
private void setWidthToContextLines(GL2 gl){
	gl.glLineWidth((float) SharedVariables.lineWidth);
}

private void setWidthToLinesOfInterest(GL2 gl){
	gl.glLineWidth((float) SharedVariables.lineOfInterestWidth);
}

private void setColorToContextLines(GL2 gl, Color color) {

	//	setBlending(gl);
	//setColor(gl, color);
	//setColorWithTransparency(gl, color);
	setColorWithTransparencyBasedOnTransitDegree(gl);
}
private void setBlending(GL2 gl) {
	
	
	
	gl.glEnable(GL.GL_BLEND);
	gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	
	//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
	//gl.glBlendEquation(GL.GL_FUNC_MIN);
	//gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
	//gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
	
	//gl.glBlendFuncSeparate(GL2.GL_Max, GL2.GL_DST_ALPHA,
		//	 GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA);
	//gl.glBlendEquation(GL.GL_FUNC_MIN);
	// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ZERO);

	// gl.glEnable(GL.GL_BLEND);
	// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	// gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
}
	private void drawNode(GL2 gl, String nodePosition, Vec4 pos, int nodeDegree) {

		//Color col = getColorFromNodePosition(nodePosition);
		//setColor(gl, col);
		
		setColorNodeBasedOnTransitDegree(gl,nodeDegree);
		
		drawNode(gl, pos);
		
		

	}

	private void drawNode(GL2 gl, Vec4 pos) {
		GLUT glut = new GLUT();

		OGLStackHandler ogsh = new OGLStackHandler();
		ogsh.pushModelview(gl);
		try {

			gl.glTranslated(pos.x, pos.y, pos.z);
			// drawSphere(gl, 1000000.0, 20, 20);
			// drawQuad(gl);
			glut.glutSolidSphere(50000.0, 4, 4);
		} finally {
			ogsh.pop(gl);

		}

		// gl.glPushMatrix();
		// gl.glPopMatrix();

	}

	/*
	 * private void drawQuad(GL2 gl){ gl.glBegin(GL2.GL_QUADS); // Start drawing
	 * a quad primitive
	 * 
	 * gl.glVertex3f(-10000.0f, -10000.0f, 000.0f); // The bottom left corner
	 * gl.glVertex3f(-10000.0f, 10000.0f, 000.0f); // The top left corner
	 * gl.glVertex3f(10000.0f, 10000.0f, 000.0f); // The top right corner
	 * gl.glVertex3f(10000.0f, -10000.0f, 000.0f); // The bottom right corner
	 * 
	 * gl.glEnd(); }
	 */

	void drawSphere(GL2 gl, double r, int lats, int longs) {
		int i, j;
		for (i = 0; i <= lats; i++) {
			double lat0 = Math.PI * (-0.5 + (double) (i - 1) / lats);
			double z0 = Math.sin(lat0);
			double zr0 = Math.cos(lat0);

			double lat1 = Math.PI * (-0.5 + (double) i / lats);
			double z1 = Math.sin(lat1);
			double zr1 = Math.cos(lat1);

			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (j = 0; j <= longs; j++) {
				double lng = 2 * Math.PI * (double) (j - 1) / longs;
				double x = Math.cos(lng);
				double y = Math.sin(lng);

				gl.glNormal3d(x * zr0, y * zr0, z0);
				gl.glVertex3d(x * zr0, y * zr0, z0);
				gl.glNormal3d(x * zr1, y * zr1, z1);
				gl.glVertex3d(x * zr1, y * zr1, z1);
			}
			gl.glEnd();
		}
	}

	/*
	 * private void drawLine(GL2 gl, Vec4 p1 , Vec4 p2){ gl.glLineWidth(2.5f);
	 * //gl.glColor3f(1.0f, 0.0f, 0.0f); gl.glBegin(GL2.GL_LINES);
	 * 
	 * gl.glVertex3d(p1.x, p1.y, p1.z); gl.glVertex3d(p2.x, p2.y, p2.z);
	 * 
	 * gl.glEnd(); }
	 */
	private void setColor(GL2 gl, Color color) {

		// if (color.getAlpha() != 255)
		{
			blending(gl);
		}

		// System.out.println("color.getAlpha(): "+ color.getAlpha());

		gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(),
				(byte) color.getBlue(),(byte)255// (byte) color.getAlpha()

		);
	}

	private void setColorWithTransparency(GL2 gl, Color color) {

		// if (color.getAlpha() != 255)
		{
			blending(gl);
		}

		// System.out.println("color.getAlpha(): "+ color.getAlpha());

		gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(),
				(byte) color.getBlue(),(byte)70// (byte) color.getAlpha()

		);
	}

	private void setColorWithTransparencyBasedOnTransitDegree(GL2 gl) {

		// if (color.getAlpha() != 255)
		{
			blending(gl);
		}

		// System.out.println("color.getAlpha(): "+ color.getAlpha());
Color color = ColorGradient.returnInterpolatedColor(transitDegree, SharedVariables.minTransitDegree, SharedVariables.maxTransitDegree);
		gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(),
				(byte) color.getBlue(),(byte)70// (byte) color.getAlpha()

		);
	}

	private void setColorBasedOnTransitDegree(GL2 gl) {

		// if (color.getAlpha() != 255)
		{
			blending(gl);
		}

		// System.out.println("color.getAlpha(): "+ color.getAlpha());
Color color = ColorGradient.returnInterpolatedColor(transitDegree, SharedVariables.minTransitDegree, SharedVariables.maxTransitDegree);
		gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(),
				(byte) color.getBlue(),(byte)255// (byte) color.getAlpha()

		);
	}
	
	
	private void setColorNodeBasedOnTransitDegree(GL2 gl, int degree) {

		// if (color.getAlpha() != 255)
		{
			blending(gl);
		}

		// System.out.println("color.getAlpha(): "+ color.getAlpha());
Color color = ColorGradient.returnInterpolatedColor(degree, SharedVariables.minTransitDegree, SharedVariables.maxTransitDegree);
		gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(),
				(byte) color.getBlue(),(byte)255// (byte) color.getAlpha()

		);
	}
	
	private void setToModifiedLinesColor(GL2 gl, Color color) {

		// if (color.getAlpha() != 255)
		{
			// blending(gl);
		}
		gl.glEnable(GL.GL_BLEND);
		// gl.glBlendEquationSeparate(GL2.GL_FUNC_ADD, GL2.GL_MIN);
		// gl.glBlendEquation(GL2.GL_MAX);
		// gl.glEnable(GL2.GL_ALPHA_TEST);
		// gl.glAlphaFunc(GL2.GL_GREATER,0.9f);
		// gl.glAlphaFunc(GL.GL_EQUAL,1.0f);

		// gl.glBlendFuncSeparate(GL.GL_ONE, GL.GL_ONE, GL.GL_ONE, GL.GL_ZERO);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glBlendEquation(GL2.GL_FUNC_ADD);
		// gl.glBlendFunc(GL.GL_ONE_MINUS_DST_ALPHA, GL.GL_ZERO);
		// System.out.println("color.getAlpha(): "+ color.getAlpha());
		// gl.glBlendEquation(GL2.GL_MIN);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA);
		// gl.glEnable(GL.GL_BLEND);

		/*
		 * gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte)
		 * color.getBlue(), (byte) 255);
		 */
		gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
	}

	private void blending(GL2 gl) {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ZERO);

		// gl.glEnable(GL.GL_BLEND);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
	}

	/*
	 * private void blendingForLinesCompletelyInsideLens(GL2 gl){
	 * gl.glEnable(GL.GL_BLEND); //gl.glBlendFunc(GL.GL_SRC_ALPHA,
	 * GL.GL_ONE_MINUS_SRC_ALPHA); //gl.glBlendFunc(GL.GL_SRC_ALPHA,
	 * GL.GL_ONE_MINUS_SRC_ALPHA);
	 * 
	 * // gl.glEnable(GL.GL_BLEND); gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
	 * //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA); //
	 * gl.glBlendFunc(GL.GL_ZERO, GL.GL_DST_ALPHA); // gl.glBlendFunc(GL.GL_ONE,
	 * GL.GL_ONE); }
	 */
	private void blendingForLinesPartiallyInsideLens(GL2 gl) {
		gl.glEnable(GL.GL_BLEND);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		// gl.glBlendEquation(GL2.GL_MIN);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA);
		// gl.glBlendFuncSeparate(GL2.GL_SRC_ALPHA, GL2.GL_DST_ALPHA,
		// GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA);
		// gl.glEnable(GL.GL_BLEND);
	}

	private void setColorToLinesCompletelyInsideLens(GL2 gl, Color color) {

		// if (color.getAlpha() != 255)
		{
			// blendingForLinesCompletelyInsideLens(gl);
			// gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
		}

		gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(),
				(byte) color.getBlue(), (byte) 255);
	}

	private void setColorToLinesPartiallyInsideTheLens(GL2 gl, Color color) {

		// if (color.getAlpha() != 255)
		{
			blendingForLinesPartiallyInsideLens(gl);
			// gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
		}
		// gl.glDisable(GL2.GL_BLEND);

		//gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(),
			//	(byte) color.getBlue(), (byte) 0);
		setColor(gl, color);
	}

	
	
	public void drawLines(GL2 gl, int bufferIndex, int numVertices,
			int startingIndex) {
		// if (this.currentListOfVertices == null)
		// return;
		int geometryType = GL2.GL_LINE_STRIP;
		// int geometryType = GL2.GL_LINE_STRIP_ADJACENCY_ARB;
		// gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,
		// myRendLayer.getBufferElement(lineIndex));
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferIndex);
		gl.glEnableVertexAttribArray(0);
		gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);

		// gl.glBindBuffer(GL.GL_ARRAY_BUFFER,
		// myRendLayer.getVectivesIndexElement(lineIndex));
		// gl.glEnableVertexAttribArray(1);
		// gl.glVertexAttribPointer(1, 1, GL.GL_FLOAT , false, 0, 0);

		// gl.glDrawArrays(geometryType, 0, numVerticesPerLine);
		gl.glDrawArrays(geometryType, startingIndex, numVertices);

		gl.glDisableVertexAttribArray(0);
		// gl.glDisableVertexAttribArray(1);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

	}

	// funzione da modificare
	public void makeOriginalVertices(DrawContext dc) {

		if (this.currentListOfVertices == null) {

			this.currentListOfVertices = new ArrayList<Vec4>();
		} else {

			this.currentListOfVertices.clear();
		}

		if (renderableControlPoints.getPositions().size() < 1)
			return;

		double[] xpoints = createXPoints(renderableControlPoints.points);
		double[] ypoints = createYPoints(renderableControlPoints.points);
		double[] zpoints = createZPoints(renderableControlPoints.points);

		cubicCurveFunctionsX = createCubicCurveFunctions(
				renderableControlPoints.getPositions().size() - 1, xpoints);
		cubicCurveFunctionsY = createCubicCurveFunctions(
				renderableControlPoints.getPositions().size() - 1, ypoints);
		cubicCurveFunctionsZ = createCubicCurveFunctions(
				renderableControlPoints.getPositions().size() - 1, zpoints);

		// numVerticesPerLine =
		// ((SharedVariables.numSubsegments+1)*(SharedVariables.numControlPoints-1))-(SharedVariables.numControlPoints-2);
		numVerticesPerLine = SharedVariables.computeNumVerticesPerLine();

		// System.out.println("bufferSize: "+bufferPointSize);
		for (int i = 1; i <= renderableControlPoints.getPositions().size(); i++) {

			if (i < renderableControlPoints.getPositions().size()) {
			} else
				break;

			Vec4 ptB = renderableControlPoints.points.get(i);

			ArrayList<Vec4> span = null;
			// se è il primo segmento inserisco anche il primo punto
			// altrimenti non lo inserisco perchè è uguale all'ultimo del
			// segmento precedente
			if (i == 1) {

				Vec4 ptA = renderableControlPoints.points.get(0);
				// System.out.println("inserisco punto A: "+ptA);
				// span = this.clipAndAdd(dc, ptA, span);
				currentListOfVertices.add(ptA);

			}

			span = this.makeSegment(dc,
					// ptA,
					ptB, cubicCurveFunctionsX[i - 1],
					cubicCurveFunctionsY[i - 1], cubicCurveFunctionsZ[i - 1]);

			// printSpan(span);

			if (span != null) {
				// aggiungere condizione: se non è già stata renderizzata
				this.addSpan(span);

			}
			// ptA = ptB;
		}

	}

	public void createBuffers(DrawContext dc) {

		bufferPoints = createPointsBuffer(numVerticesPerLine,
				currentListOfVertices);
		bufferIndexVectices = createVerticesIntIndexBuffer(numVerticesPerLine);

		/*
		 * setArrayBuffer(dc,numVerticesPerLine*3,bufferPoints,myRendLayer.
		 * getBufferElement(lineIndex));
		 * setArrayBuffer(dc,numVerticesPerLine,bufferIndexVectices
		 * ,myRendLayer.getVectivesIndexElement(lineIndex));
		 */
	}

	private void makeUpdatedVertices(DrawContext dc) {

		if (this.currentListOfUpdatedVertices == null)
			this.currentListOfUpdatedVertices = new ArrayList<Vec4>();
		else
			this.currentListOfUpdatedVertices.clear();

		if (updRenderableControlPoints.getPositions().size() < 1)
			return;

		double[] xpoints = createXPoints(updRenderableControlPoints.points);
		double[] ypoints = createYPoints(updRenderableControlPoints.points);
		double[] zpoints = createZPoints(updRenderableControlPoints.points);

		updCubicCurveFunctionsX = createCubicCurveFunctions(
				updRenderableControlPoints.getSize() - 1, xpoints);
		updCubicCurveFunctionsY = createCubicCurveFunctions(
				updRenderableControlPoints.getSize() - 1, ypoints);
		updCubicCurveFunctionsZ = createCubicCurveFunctions(
				updRenderableControlPoints.getSize() - 1, zpoints);

		int numSubsegments = SharedVariables.numSubsegments;
		bufferUpdPointSize = ((numSubsegments + 1) * (updRenderableControlPoints
				.getSize() - 1)) - (updRenderableControlPoints.getSize() - 2);

		for (int i = 1; i <= updRenderableControlPoints.getSize(); i++) {
			if (i < updRenderableControlPoints.getSize()) {
			} else
				break;

			Vec4 ptB = updRenderableControlPoints.points.get(i);

			// System.out.println("creo subcurva da: "+posA+" a "+posB);
			ArrayList<Vec4> span = null;
			// se è il primo segmento inserisco anche il primo punto
			// altrimenti non lo inserisco perchè è uguale all'ultimo del
			// segmento precedente
			if (i == 1) {
				// System.out.println("inserisco punto A: "+ptA);
				Vec4 ptA = updRenderableControlPoints.points.get(0);
				// span = this.clipAndAdd(dc, ptA, span);
				currentListOfUpdatedVertices.add(ptA);
			}

			span = this.makeSegment(
					dc,
					// ptA,
					ptB, updCubicCurveFunctionsX[i - 1],
					updCubicCurveFunctionsY[i - 1],
					updCubicCurveFunctionsZ[i - 1]);

			if (span != null) {
				// aggiungere condizione: se non è già stata renderizzata
				this.addUpdatedSpan(span);
			}

			// ptA = ptB;
		}

		bufferUpdatedPoints = createPointsBuffer(bufferUpdPointSize,
				currentListOfUpdatedVertices);
		setArrayBuffer(dc, bufferUpdPointSize * 3, bufferUpdatedPoints,
				myRendLayer.getUpdBufferElement(lineIndex));
	}

	private void setArrayBuffer(DrawContext dc, int numPoints,
			FloatBuffer buffer, int indexBuffer) {
		// Create Vertex Array.

		buffer.position(0);
		GL gl = dc.getGL();
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, indexBuffer); // gl.glBufferData(GL.GL_ARRAY_BUFFER,
															// bufferSize*3*8,
															// bufferPoints,
															// GL.GL_STATIC_DRAW);

		int numElements = numPoints;
		gl.glBufferData(GL.GL_ARRAY_BUFFER, numElements
				* SharedVariables.SIZEOF_FLOAT, buffer, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

	}

	private FloatBuffer createVerticesFloatIndexBuffer(int size) {

		FloatBuffer bufferIndexVectives = SharedVariables.newFloatBuffer(size);
		float[] indices = new float[size];
		for (int i = 0; i < size; i++) {
			float val = i + (lineIndex * size);
			indices[(i)] = val;
		}
		bufferIndexVectives.put(indices); // this also uses the data from
											// earlier
		bufferIndexVectives.position(0);
		return bufferIndexVectives;

	}

	private IntBuffer createVerticesIntIndexBuffer(int size) {

		IntBuffer bufferIndexVectives = SharedVariables.newIntBuffer(size);
		int[] indices = new int[size];
		for (int i = 0; i < size; i++) {
			int val = i + (lineIndex * size);
			indices[(i)] = val;
		}
		bufferIndexVectives.put(indices); // this also uses the data from
											// earlier
		bufferIndexVectives.position(0);
		return bufferIndexVectives;

	}

	private FloatBuffer createPointsBuffer(int size,
			List<Vec4> currentUpdatedSpans) {

		FloatBuffer pointsBuffer = SharedVariables.newFloatBuffer(size * 3);

		float[] points = new float[size * 3];

		int i = 0;
		// for(List<Vec4> span: currentUpdatedSpans){
		for (Vec4 p : currentUpdatedSpans) {
			points[(i * 3)] = (float) p.x;
			points[(i * 3) + 1] = (float) p.y;
			points[(i * 3) + 2] = (float) p.z;
			i++;
		}
		// }

		pointsBuffer.put(points); // this also uses the data from earlier
		pointsBuffer.position(0);
		return pointsBuffer;
	}

	public FloatBuffer getBufferPoints() {
		return bufferPoints;
	}

	public IntBuffer getBufferIndexVectices() {
		return bufferIndexVectices;
	}

	/*
	 * public Color getColor() { return color; }
	 * 
	 * public void setColor(Color color) { this.color = color; }
	 */
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTransitDegree(int transitDegree){
		this.transitDegree = transitDegree;
	}
	
	public void setTransitDegreeNodeTo(int transitDegree){
		this.transitDegreeNodeTo = transitDegree;
	}
	
	public void setTransitDegreeNodeFrom(int transitDegree){
		this.transitDegreeNodeFrom = transitDegree;
	}
	
	
	public void setTo(String to) {
		this.to = to;
	}

	public CubicSplinePolyline(RenderableControlPoints renderableControlPoints) {

		this.measurer.setFollowTerrain(this.followTerrain);
		this.measurer.setPathType(this.pathType);
		this.renderableControlPoints = renderableControlPoints;

		this.updRenderableControlPoints = new RenderableControlPoints();
		this.updRenderableControlPoints
				.setControlPointPosition((ArrayList<Position>) renderableControlPoints
						.getPositions().clone());
		this.updRenderableControlPoints
				.setControlPoint((ArrayList<Vec4>) renderableControlPoints.points
						.clone());

	}

	private void reset() {

		if (this.currentListOfVertices != null)
			this.currentListOfVertices.clear();

		this.currentListOfVertices = null;

		if (this.currentListOfUpdatedVertices != null)
			this.currentListOfUpdatedVertices.clear();

		this.currentListOfUpdatedVertices = null;

	}

	public int getAntiAliasHint() {
		return antiAliasHint;
	}

	public void setAntiAliasHint(int hint) {
		if (!(hint == ANTIALIAS_DONT_CARE || hint == ANTIALIAS_FASTEST || hint == ANTIALIAS_NICEST)) {
			String msg = Logging.getMessage("generic.InvalidHint");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		this.antiAliasHint = hint;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	public int getPathType() {
		return pathType;
	}

	public String getPathTypeString() {
		return this.getPathType() == GREAT_CIRCLE ? AVKey.GREAT_CIRCLE : this
				.getPathType() == RHUMB_LINE ? AVKey.RHUMB_LINE : AVKey.LINEAR;
	}

	/**
	 * Sets the type of path to draw, one of {@link #GREAT_CIRCLE}, which draws
	 * each segment of the path as a great circle, {@link #LINEAR}, which
	 * determines the intermediate positions between segments by interpolating
	 * the segment endpoints, or {@link #RHUMB_LINE}, which draws each segment
	 * of the path as a line of constant heading.
	 *
	 * @param pathType
	 *            the type of path to draw.
	 *
	 * @see <a href="{@docRoot}/overview-summary.html#path-types">Path Types</a>
	 */
	public void setPathType(int pathType) {
		this.reset();
		this.pathType = pathType;
		this.measurer.setPathType(pathType);
	}

	/**
	 * Sets the type of path to draw, one of {@link AVKey#GREAT_CIRCLE}, which
	 * draws each segment of the path as a great circle, {@link AVKey#LINEAR},
	 * which determines the intermediate positions between segments by
	 * interpolating the segment endpoints, or {@link AVKey#RHUMB_LINE}, which
	 * draws each segment of the path as a line of constant heading.
	 *
	 * @param pathType
	 *            the type of path to draw.
	 *
	 * @see <a href="{@docRoot}/overview-summary.html#path-types">Path Types</a>
	 */
	public void setPathType(String pathType) {
		if (pathType == null) {
			String msg = Logging.getMessage("nullValue.PathTypeIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		this.setPathType(pathType.equals(AVKey.GREAT_CIRCLE) ? GREAT_CIRCLE
				: pathType.equals(AVKey.RHUMB_LINE)
						|| pathType.equals(AVKey.LOXODROME) ? RHUMB_LINE
						: LINEAR);
	}

	public boolean isFollowTerrain() {
		return followTerrain;
	}

	/**
	 * Indicates whether the path should follow the terrain's surface. If the
	 * value is <code>true</code>, the elevation values in this path's positions
	 * are ignored and the path is drawn on the terrain surface. Otherwise the
	 * path is drawn according to the elevations given in the path's positions.
	 * If following the terrain, the path may also have an offset. See
	 * {@link #setOffset(double)};
	 *
	 * @param followTerrain
	 *            <code>true</code> to follow the terrain, otherwise
	 *            <code>false</code>.
	 */
	public void setFollowTerrain(boolean followTerrain) {

		this.reset();
		this.followTerrain = followTerrain;
		this.measurer.setFollowTerrain(followTerrain);
		this.extents.clear();
	}

	public double getOffset() {
		return offset;
	}

	/**
	 * Specifies an offset, in meters, to add to the path points when the path's
	 * follow-terrain attribute is true. See {@link #setFollowTerrain(boolean)}.
	 *
	 * @param offset
	 *            the path pffset in meters.
	 */
	public void setOffset(double offset) {

		this.reset();
		this.offset = offset;
		this.extents.clear();
	}

	public double getTerrainConformance() {
		return terrainConformance;
	}

	/**
	 * Specifies the precision to which the path follows the terrain when the
	 * follow-terrain attribute is true. The conformance value indicates the
	 * approximate length of each sub-segment of the path as it's drawn, in
	 * pixels. Lower values specify higher precision, but at the cost of
	 * performance.
	 *
	 * @param terrainConformance
	 *            the path conformance in pixels.
	 */

	public void setTerrainConformance(double terrainConformance) {
		this.terrainConformance = terrainConformance;
	}

	/*
	public double getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
	}
*/
	/**
	 * Returns the length of the line as drawn. If the path follows the terrain,
	 * the length returned is the distance one would travel if on the surface.
	 * If the path does not follow the terrain, the length returned is the
	 * distance along the full length of the path at the path's elevations and
	 * current path type.
	 *
	 * @return the path's length in meters.
	 */
	public double getLength() {
		Iterator<ExtentInfo> infos = this.extents.values().iterator();
		return infos.hasNext() ? this.measurer.getLength(infos.next().globe)
				: 0;
	}

	public double getLength(Globe globe) {
		// The length measurer will throw an exception and log the error if
		// globe is null
		return this.measurer.getLength(globe);
	}

	public LengthMeasurer getMeasurer() {
		return this.measurer;
	}

	public short getStipplePattern() {
		return stipplePattern;
	}

	/**
	 * Sets the stipple pattern for specifying line types other than solid. See
	 * the OpenGL specification or programming guides for a description of this
	 * parameter. Stipple is also affected by the path's stipple factor,
	 * {@link #setStippleFactor(int)}.
	 *
	 * @param stipplePattern
	 *            the stipple pattern.
	 */
	public void setStipplePattern(short stipplePattern) {
		this.stipplePattern = stipplePattern;
	}

	public int getStippleFactor() {
		return stippleFactor;
	}

	/**
	 * Sets the stipple factor for specifying line types other than solid. See
	 * the OpenGL specification or programming guides for a description of this
	 * parameter. Stipple is also affected by the path's stipple pattern,
	 * {@link #setStipplePattern(short)}.
	 *
	 * @param stippleFactor
	 *            the stipple factor.
	 */
	public void setStippleFactor(int stippleFactor) {
		this.stippleFactor = stippleFactor;
	}

	/**
	 * Specifies the number of intermediate segments to draw for each segment
	 * between positions. The end points of the intermediate segments are
	 * calculated according to the current path type and follow-terrain setting.
	 *
	 * @param numSubsegments
	 *            the number of intermediate subsegments.
	 */

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}
/*
	public Color getHighlightColor() {
		return this.highlightColor;
	}
*/
	/*
	public void setHighlightColor(Color highlightColor) {
		if (highlightColor == null) {
			String message = Logging.getMessage("nullValue.ColorIsNull");
			Logging.logger().severe(message);
			throw new IllegalStateException(message);
		}

		this.highlightColor = highlightColor;
	}
*/
	/**
	 * Returns the delegate owner of this Polyline. If non-null, the returned
	 * object replaces the Polyline as the pickable object returned during
	 * picking. If null, the Polyline itself is the pickable object returned
	 * during picking.
	 *
	 * @return the object used as the pickable object returned during picking,
	 *         or null to indicate that the Polyline is returned during picking.
	 */
	public Object getDelegateOwner() {
		return this.delegateOwner;
	}

	/**
	 * Specifies the delegate owner of this Polyline. If non-null, the delegate
	 * owner replaces the Polyline as the pickable object returned during
	 * picking. If null, the Polyline itself is the pickable object returned
	 * during picking.
	 *
	 * @param owner
	 *            the object to use as the pickable object returned during
	 *            picking, or null to return the Polyline.
	 */
	public void setDelegateOwner(Object owner) {
		this.delegateOwner = owner;
	}

	/**
	 * Returns this Polyline's enclosing volume as an
	 * {@link gov.nasa.worldwind.geom.Extent} in model coordinates, given a
	 * specified {@link gov.nasa.worldwind.globes.Globe} and vertical
	 * exaggeration (see
	 * {@link gov.nasa.worldwind.SceneController#getVerticalExaggeration()}.
	 *
	 * @param globe
	 *            the Globe this Polyline is related to.
	 * @param verticalExaggeration
	 *            the vertical exaggeration to apply.
	 *
	 * @return this Polyline's Extent in model coordinates.
	 *
	 * @throws IllegalArgumentException
	 *             if the Globe is null.
	 */
	public Extent getExtent(Globe globe, double verticalExaggeration) {
		if (globe == null) {
			String message = Logging.getMessage("nullValue.GlobeIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		return this.computeExtent(globe, verticalExaggeration);
	}

	/**
	 * Returns this Polyline's enclosing volume as an
	 * {@link gov.nasa.worldwind.geom.Extent} in model coordinates, given a
	 * specified {@link gov.nasa.worldwind.render.DrawContext}. The returned
	 * Extent may be different than the Extent returned by calling
	 * {@link #getExtent(gov.nasa.worldwind.globes.Globe, double)} with the
	 * DrawContext's Globe and vertical exaggeration. Additionally, this may
	 * cache the computed extent and is therefore potentially faster than
	 * calling {@link #getExtent(gov.nasa.worldwind.globes.Globe, double)}.
	 *
	 * @param dc
	 *            the current DrawContext.
	 *
	 * @return this Polyline's Extent in model coordinates.
	 *
	 * @throws IllegalArgumentException
	 *             if the DrawContext is null, or if the Globe held by the
	 *             DrawContext is null.
	 */
	public Extent getExtent(DrawContext dc) {
		if (dc == null) {
			String message = Logging.getMessage("nullValue.DrawContextIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		if (dc.getGlobe() == null) {
			String message = Logging
					.getMessage("nullValue.DrawingContextGlobeIsNull");
			Logging.logger().severe(message);
			throw new IllegalArgumentException(message);
		}

		ExtentInfo extentInfo = this.extents.get(dc.getGlobe());
		if (extentInfo != null && extentInfo.isValid(dc)) {
			return extentInfo.extent;
		} else {
			extentInfo = new ExtentInfo(this.computeExtent(dc), dc);
			this.extents.put(dc.getGlobe(), extentInfo);
			return extentInfo.extent;
		}
	}

	protected Extent computeExtent(Globe globe, double verticalExaggeration) {
		// Sector sector = Sector.boundingSector(this.getPositions());
		Sector sector = Sector.boundingSector(renderableControlPoints
				.getPositions());

		double[] minAndMaxElevations;
		if (this.isFollowTerrain()) {
			minAndMaxElevations = globe.getMinAndMaxElevations(sector);
		} else {
			// minAndMaxElevations =
			// computeElevationExtremes(this.getPositions());
			minAndMaxElevations = computeElevationExtremes(renderableControlPoints
					.getPositions());
		}
		minAndMaxElevations[0] += this.getOffset();
		minAndMaxElevations[1] += this.getOffset();

		return Sector.computeBoundingBox(globe, verticalExaggeration, sector,
				minAndMaxElevations[0], minAndMaxElevations[1]);
	}

	protected Extent computeExtent(DrawContext dc) {
		return this.computeExtent(dc.getGlobe(), dc.getVerticalExaggeration());
	}

	protected static double[] computeElevationExtremes(
			Iterable<? extends Position> positions) {
		double[] extremes = new double[] { Double.MAX_VALUE, -Double.MAX_VALUE };
		for (Position pos : positions) {
			if (extremes[0] > pos.getElevation())
				extremes[0] = pos.getElevation(); // min
			if (extremes[1] < pos.getElevation())
				extremes[1] = pos.getElevation(); // max
		}

		return extremes;
	}

	public void pick(DrawContext dc, Point pickPoint) {

	}

	public void render(DrawContext dc) {

		// This render method is called three times during frame generation.
		// It's first called as a {@link Renderable}
		// during <code>Renderable</code> picking. It's called again during
		// normal rendering. And it's called a third
		// time as an OrderedRenderable. The first two calls determine whether
		// to add the polyline to the ordered
		// renderable list during pick and render. The third call just draws the
		// ordered renderable.
		if (dc == null) {
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
	 * @param imageSource
	 *            the texture image source. May be a {@link String} identifying
	 *            a file path or URL, a {@link File}, or a {@link java.net.URL}.
	 * @param texCoords
	 *            the (s, t) texture coordinates aligning the image to the
	 *            polygon. There must be one texture coordinate pair, (s, t),
	 *            for each polygon location in the polygon's outer boundary.
	 * @param texCoordCount
	 *            the number of texture coordinates, (s, v) pairs, specified.
	 *
	 * @throws IllegalArgumentException
	 *             if the image source is not null and either the texture
	 *             coordinates are null or inconsistent with the specified
	 *             texture-coordinate count, or there are fewer than three
	 *             texture coordinate pairs.
	 */

	protected WWTexture makeTexture(Object imageSource) {
		return new LazilyLoadedTexture(imageSource, true);
	}

	RenderTextureOnScreen sr = SharedVariables.sr;

	private boolean isLastNodeInsideLense() {
		return isLastNodeInsideLense;
	}

	public void setLastNodeInsideLense(boolean isNodeInsideLense) {
		this.isLastNodeInsideLense = isNodeInsideLense;
	}

	private boolean isFirstNodeInsideLense() {
		return isFirstNodeInsideLense;
	}

	public void setFirstNodeInsideLense(boolean isNodeInsideLense) {
		this.isFirstNodeInsideLense = isNodeInsideLense;
	}

	private void initUpdateControlPoints() {
		this.updRenderableControlPoints
				.setControlPointPosition((ArrayList<Position>) renderableControlPoints
						.getPositions().clone());
		this.updRenderableControlPoints
				.setControlPoint((ArrayList<Vec4>) renderableControlPoints.points
						.clone());

	}

	protected void movePoints(DrawContext dc, ArrayList<Vec4> infoPoints) {

		// initUpdateControlPoints();

		if (!isFirstNodeInsideLense() & !isLastNodeInsideLense()) {

			if (isAffectedByLense() & SharedVariables.lensIsActive) {

				initUpdateControlPoints();

				// Vec4 middlePoint = null;
				if (infoPoints.size() == 2) {
					// computeIndexOfElementToRemove(dc,
					// SharedVariables.screenPoint,-1,-1);

					setCustomColor(2);

					Vec4 worldPoint0 = getVertices().get(
							(int) infoPoints.get(0).z);
					Vec4 worldPoint1 = getVertices().get(
							(int) infoPoints.get(1).z);

					Vec4 point0 = SharedVariables
							.computeScreenCoordinates(worldPoint0);
					Vec4 point1 = SharedVariables
							.computeScreenCoordinates(worldPoint1);

					removeControlPointsInsideLens(dc,
							(int) infoPoints.get(0).z,
							(int) infoPoints.get(1).z);

					// System.out.println("indice 0: "+(int)infoPoints.get(0).z+" elemento 0 : "+point0);
					// System.out.println("indice 1: "+(int)infoPoints.get(1).z+" elemento 1 : "+point1);

					Vec4 mp = calculateNormalizedMiddlePoint(point0, point1);

					// moveAndUpdatePoint(dc,mp);

					Vec4 movedScreenCoordinates = movePoint(mp,
							SharedVariables.screenPoint, 1);
					Vec4 newP = SharedVariables
							.computeWorldCoordinatesFromScreenPoint(movedScreenCoordinates);

					double indexA = infoPoints.get(0).z
							/ ((double) getVertices().size());
					double indexB = infoPoints.get(1).z
							/ ((double) getVertices().size());

					double minInd = (indexA < indexB) ? indexA : indexB;
					double maxInd = (minInd == indexA) ? indexB : indexA;

					int minInd_int = (int) Math.floor(minInd
							* (renderableControlPoints.points.size() - 1));
					int maxInd_int = (int) Math.ceil(maxInd
							* (renderableControlPoints.points.size() - 1));

					// int minIndex = (ind1<ind2)? ind1 : ind2 ;
					/*
					 * System.out.println("renderableControlPoints.points.size(): "
					 * +renderableControlPoints.points.size());
					 * System.out.println
					 * ("points.get(0).getIndex(): "+points.get(0).getIndex());
					 * System
					 * .out.println("points.get(1).getIndex(): "+points.get
					 * (1).getIndex());
					 * System.out.println("ind1= "+ind1+" ind2= "
					 * +ind2+" minInd: "+minInd);
					 */
					// int ind=findNearestControlPoint(dc,mp);
					updRenderableControlPoints.points.add(minInd_int + 1, newP);

					if (SharedVariables.showMiddlePoint) {

						// sr.draw(dc, SharedVariables.screenPoint,2);
						sr.draw(dc, point0, 1);
						sr.draw(dc, point1, 1);

						sr.drawText(dc, point0, worldPoint0);
						sr.drawText(dc, point1, worldPoint1);

						// sr.draw(dc, movedScreenCoordinates,1);
					}
					if (SharedVariables.showIntermediatePoint) {

						drawControlPoint(dc);
					}

				} else if (infoPoints.size() < 2) {
					// setCustomColor(1);

				} else if (infoPoints.size() == 3) {

					setCustomColor(3);
				} else {

					setCustomColor(4);

					/*
					 * Vec4 point0 =
					 * getVertices().get((int)infoPoints.get(0).z); Vec4 point3
					 * = getVertices().get((int)infoPoints.get(3).z);
					 * 
					 * 
					 * double
					 * d1=SharedVariables.computeWorldCoordinatesFromScreenPoint
					 * (point0).distanceTo3(dc.getView().getEyePoint()); double
					 * d2
					 * =SharedVariables.computeWorldCoordinatesFromScreenPoint(
					 * point3).distanceTo3(dc.getView().getEyePoint());
					 * 
					 * int i=-1; int j=-1; if(d1<d2){ i=0; j=2; }else{ i=2; j=0;
					 * }
					 * 
					 * Vec4 pointA =
					 * getVertices().get((int)infoPoints.get(i).z); Vec4 pointB
					 * = getVertices().get((int)infoPoints.get(i+1).z);
					 * 
					 * 
					 * Vec4 mp= calculateNormalizedMiddlePoint(pointA, pointB);
					 * // moveAndUpdatePoint(dc,mp); Vec4
					 * movedScreenCoordinates=movePoint(mp,
					 * SharedVariables.screenPoint,1); Vec4 newP =
					 * SharedVariables.computeWorldCoordinatesFromScreenPoint(
					 * movedScreenCoordinates); int
					 * ind=findNearestControlPoint(dc,mp);
					 * updRenderableControlPoints.points.set(ind,newP);
					 * 
					 * 
					 * //checkControlPointsInsideLens(dc,
					 * SharedVariables.screenPoint,ind,-1);
					 * computeIndexOfElementToRemove(dc,
					 * SharedVariables.screenPoint,ind,-1);
					 * removeControlPointsInsideLens(dc);
					 * 
					 * 
					 * 
					 * if(SharedVariables.debugMode){ System.out
					 * .println("ind1: "
					 * +ind+"pos on screen: "+movedScreenCoordinates); } if(
					 * (SharedVariables.isPositionVisible(dc,
					 * SharedVariables.computeWorldCoordinatesFromPosition
					 * ((getOrigin())))) &&
					 * (SharedVariables.isPositionVisible(dc,
					 * SharedVariables.computeWorldCoordinatesFromPosition
					 * (getDestination()))) ){
					 * 
					 * //if(true){ color=Color.ORANGE;
					 * 
					 * 
					 * Vec4 pointC =
					 * getVertices().get((int)infoPoints.get(j).z); Vec4 pointD
					 * = getVertices().get((int)infoPoints.get(j+1).z);
					 * 
					 * Vec4 mp2= calculateNormalizedMiddlePoint(pointC, pointD);
					 * // moveAndUpdatePoint(dc,mp); Vec4
					 * movedScreenCoordinates2=movePoint(mp2,
					 * SharedVariables.screenPoint,2); Vec4 newP2 =
					 * SharedVariables.computeWorldCoordinatesFromScreenPoint(
					 * movedScreenCoordinates2); int
					 * ind2=findNearestControlPoint(dc,mp2);
					 * updRenderableControlPoints.points.set(ind2,newP2);
					 * if(SharedVariables.debugMode){ System.out
					 * .println("ind2: "
					 * +ind2+" pos on screen: "+movedScreenCoordinates2); } //
					 * checkControlPointsInsideLens(dc,
					 * SharedVariables.screenPoint,ind,ind2);
					 * 
					 * computeIndexOfElementToRemove(dc,
					 * SharedVariables.screenPoint,ind,ind2);
					 * removeControlPointsInsideLens(dc);
					 * 
					 * int minInd = (ind<ind2) ? ind : ind2; // MyPoint2D mp3=
					 * calculateNormalizedMiddelPoint(newP2, newP);
					 * 
					 * if(SharedVariables.debugMode){ System.out
					 * .println("min ind+1: "+(minInd+1)); }
					 * 
					 * Vec4 init1 = new Vec4(movedScreenCoordinates.x,
					 * movedScreenCoordinates.y); Vec4 init2 = new
					 * Vec4(movedScreenCoordinates2.x,
					 * movedScreenCoordinates2.y); Vec4 sp = new
					 * Vec4(SharedVariables.screenPoint.x,
					 * SharedVariables.screenPoint.y); Vec4
					 * p1=init1.subtract3(sp) .normalize3(); Vec4
					 * p2=init2.subtract3(sp) .normalize3(); Vec4
					 * mp3=p1.add3(p2).normalize3(); Vec4 mp3b = new Vec4(mp3.x,
					 * mp3.y,
					 * (movedScreenCoordinates.z+movedScreenCoordinates2.z)/2);
					 * 
					 * 
					 * 
					 * Vec4 movedScreenCoordinates3=movePoint(mp3b,
					 * SharedVariables.screenPoint,3); Vec4 newP3 =
					 * SharedVariables.computeWorldCoordinatesFromScreenPoint(
					 * movedScreenCoordinates3);
					 * 
					 * if(SharedVariables.debugMode){ System.out
					 * .println("size prima: "
					 * +updRenderableControlPoints.points.size()); System.out
					 * .println
					 * ("inserisco punto in indice "+minInd+1+" pos on screen: "
					 * +movedScreenCoordinates3); }
					 * updRenderableControlPoints.points.add(minInd+1,newP3);
					 * 
					 * if(SharedVariables.debugMode){ System.out
					 * .println("size dop: "
					 * +updRenderableControlPoints.points.size()); }
					 * 
					 * }
					 */
				}

				makeUpdatedVertices(dc);

			} else {
				// color=Color.WHITE;
				colorForUnmodifiedArc = new Color(255, 255, 255, 70);
				//colorForUnmodifiedArc = new Color(255, 255, 255, 255);
				// colorsFormodifiedArc=null;
				// updRenderableControlPoints.positions=renderableControlPoints.positions;
				resetOriginalControlPoint();

				// initUpdateArray();
			}
		} else {
			// color=Color.WHITE;
			colorForUnmodifiedArc = Color.GREEN;
			// colorsFormodifiedArc=null;
			// System.out.println("resetto original control points");
			// updRenderableControlPoints.positions.set(1,
			// originalControlPosition);
			resetOriginalControlPoint();
			// makeUpdatedVertices(dc);
		}
	}

	private void setCustomColor(int numIntersectingPoints) {

		Color[] res = { Color.WHITE, Color.WHITE };
		colorsFormodifiedArc = res;
		/*
		 * if(numIntersectingPoints==2){ Color[] res =
		 * {SharedVariables.intersectInTwoPoints
		 * ,SharedVariables.intersectInTwoPoints}; colorsFormodifiedArc=res;
		 * }else if(numIntersectingPoints==1){ Color[] res =
		 * {SharedVariables.intersectInOnePoints
		 * ,SharedVariables.intersectInOnePoints}; colorsFormodifiedArc=res;
		 * }else if(numIntersectingPoints==3){ Color[] res =
		 * {SharedVariables.intersectInThreePoints
		 * ,SharedVariables.intersectInThreePoints}; colorsFormodifiedArc=res;
		 * }else if(numIntersectingPoints==4){ Color[] res =
		 * {SharedVariables.intersectInFourPoints
		 * ,SharedVariables.intersectInFourPoints}; colorsFormodifiedArc=res;
		 * 
		 * }
		 */
	}

	public Vec4 calculateMiddelPoint(Vec4 point1, Vec4 point2) {
		Vec4 init1 = new Vec4(point1.x, point1.y);
		Vec4 init2 = new Vec4(point2.x, point2.y);
		Vec4 sp = new Vec4(SharedVariables.screenPoint.x,
				SharedVariables.screenPoint.y);

		Vec4 p1 = init1.subtract3(sp);
		Vec4 p2 = init2.subtract3(sp)

		;

		Vec4 midP = p1.add3(p2);
		// .divide3(2);
		// Vec4 middlePoint=midP;
		// .add3(SharedVariables.screenPoint);

		// middlePoint=points.get(0).add3(points.get(1)).divide3(2);
		// System.out.println("middle point: "+middlePoint);
		Vec4 mp = new Vec4(midP.x, midP.y,
		// middlePoint.z,
				(point1.z + point2.z) / 2, 0);
		return mp;
	}

	private Vec4 calculateNormalizedMiddlePoint(Vec4 point1inScreenCoordinates,
			Vec4 point2inScreenCoordinates) {
		Vec4 init1 = new Vec4(point1inScreenCoordinates.x,
				point1inScreenCoordinates.y);
		Vec4 init2 = new Vec4(point2inScreenCoordinates.x,
				point2inScreenCoordinates.y);
		Vec4 sp = new Vec4(SharedVariables.screenPoint.x,
				SharedVariables.screenPoint.y);

		Vec4 p1 = init1.subtract3(sp).normalize3();
		Vec4 p2 = init2.subtract3(sp).normalize3();

		Vec4 midP = p1.add3(p2).normalize3();

		Vec4 mp = new Vec4(
				midP.x,
				midP.y,
				// middlePoint.z,
				(point1inScreenCoordinates.z + point2inScreenCoordinates.z) / 2,
				1);
		return mp;
	}

	private void resetOriginalControlPoint() {
		// updRenderableControlPoints.positions.set(1, originalControlPosition);

		this.updRenderableControlPoints
				.setControlPointPosition((ArrayList<Position>) renderableControlPoints
						.getPositions().clone());
		this.updRenderableControlPoints
				.setControlPoint((ArrayList<Vec4>) renderableControlPoints.points
						.clone());
	}

	public Position getOrigin() {
		// return renderableControlPoints.positions.get(0);
		return renderableControlPoints.getOrigin();
	}

	public Position getDestination() {
		// return
		// renderableControlPoints.positions.get(renderableControlPoints.positions.size()-1);
		return renderableControlPoints.getDestination();
	}

	private void drawControlPoint(DrawContext dc) {

		for (int i = 1; i < updRenderableControlPoints.points.size() - 1; i++) {
			Vec4 p = updRenderableControlPoints.points.get(i);
			Vec4 screenPoint = SharedVariables.computeScreenCoordinates(p);
			SharedVariables.sr.drawText(dc, screenPoint, p);
			sr.draw(dc, screenPoint, 2);

		}

	}

	private void removeControlPointsInsideLens(DrawContext dc, int index1,
			int index2) {
		/*
		 * System.out.println("LEGGO LISTA INDICI DA ELIMINARE di linea: "+lineIndex
		 * ); for(int in: indPointsToRemove){ System.out.println("ind: "+in); }
		 */

		double indexCp1 = ((double) index1 / (double) (numVerticesPerLine - 1))
				* (double) (SharedVariables.numControlPoints - 1);
		double indexCp2 = ((double) index2 / (double) (numVerticesPerLine - 1))
				* (double) (SharedVariables.numControlPoints - 1);

		int minInd_int = (int) Math.ceil(indexCp1);
		int maxInd_int = (int) Math.floor(indexCp2);

		/*
		 * System.out.println("indexCp1: "+indexCp1);
		 * System.out.println("indexCp2: "+indexCp2);
		 * System.out.println("minInd_int: "+minInd_int);
		 * System.out.println("maxInd_int: "+maxInd_int);
		 */

		ArrayList<Vec4> newPointList = new ArrayList<>();
		newPointList.add(updRenderableControlPoints.points.get(0));
		for (int i = 1; i < updRenderableControlPoints.points.size() - 1; i++) {
			Vec4 p = updRenderableControlPoints.points.get(i);
			// Vec4 screenPoint=SharedVariables.computeScreenCoordinates(p);

			if ((i >= minInd_int) & (i <= maxInd_int)) {
				// System.out.println("rimuovo control point "+i);
			} else {
				newPointList.add(p);

			}

		}

		newPointList.add(updRenderableControlPoints.points
				.get(updRenderableControlPoints.points.size() - 1));
		updRenderableControlPoints.points = newPointList;
	}

	protected Vec4 movePoint(Vec4 currentScreenCoordinates, Vec4 center,
			int numControlPoint) {

		// Vec4 currentScreenCoordinates =
		// SharedVariables.computeScreenCoordinates(p);

		// double diffX=(currentScreenCoordinates.x-center.x);
		// double diffY=(currentScreenCoordinates.y-center.y);
		double distance = 0;
		if (!SharedVariables.revealEdgeStructure) {
			// distance =
			// SharedVariables.returnDistance(currentScreenCoordinates);
		} else {
			distance = SectorManager.returnDistance(getLineIndex(),
					numControlPoint);
		}
		Vec4 d = currentScreenCoordinates;
		// .subtract3(center);

		// normalize(diffX, diffY);

		// Vec4 d = new Vec4(diffX, diffY);
		// d=d.normalize3();
		d = d.multiply3((SharedVariables.lense_w / 2 + distance));

		// Vec4 res = new Vec4(d.x, d.y, currentScreenCoordinates.z);
		Vec4 res = center.add3(d);

		// return center.add3(d);
		return new Vec4(res.x, res.y, currentScreenCoordinates.z);

	}

	/**
	 * If the scene controller is rendering ordered renderables, this method
	 * draws this placemark's image as an ordered renderable. Otherwise the
	 * method determines whether this instance should be added to the ordered
	 * renderable list.
	 * <p/>
	 * The Cartesian and screen points of the placemark are computed during the
	 * first call per frame and re-used in subsequent calls of that frame.
	 *
	 * @param dc
	 *            the current draw context.
	 */
	private void draw(DrawContext dc) {

		{

			ArrayList<Vec4> pointsOnLens = getInfoPointsOnLens();
			/*
			 * if(isAffectedByLense()&SharedVariables.lensIsActive){
			 * this.makeUpdatedVertices(dc); }
			 */
			movePoints(dc, pointsOnLens);
			this.drawNewOrderedRenderable(dc);
		}

		return;

	}

	public ArrayList<Vec4> getInfoPointsOnLens() {
		return infoPointsOnLens;
	}

	private void initInfoPointsOnLens() {
		infoPointsOnLens = new ArrayList<>();
	}

	public void initPreProcessingStuff() {
		initInfoPointsOnLens();
		initUpdateControlPoints();
		setFirstNodeInsideLense(false);
		setLastNodeInsideLense(false);
	}

	/**
	 * Indicates whether the shape is visible in the current view.
	 *
	 * @param dc
	 *            the draw context.
	 *
	 * @return true if the shape is visible, otherwise false.
	 */
	protected boolean intersectsFrustum(DrawContext dc) {
		Extent extent = this.getExtent(dc);
		if (extent == null)
			return true; // don't know the visibility, shape hasn't been
							// computed yet

		if (dc.isPickingMode())
			return dc.getPickFrustums().intersectsAny(extent);

		return dc.getView().getFrustumInModelCoordinates().intersects(extent);
	}

	// private Cubic[] createCubicCurveFunctions(int n, int[] x)
	private Cubic[] createCubicCurveFunctions(int n, double[] x) {
		/*
		 * calculates the natural cubic spline that interpolates y[0], y[1], ...
		 * y[n] The first segment is returned as C[0].a + C[0].b*u + C[0].c*u^2
		 * + C[0].d*u^3 0<=u <1 the other segments are in C[1], C[2], ... C[n-1]
		 */

		double[] gamma = new double[n + 1];
		double[] delta = new double[n + 1];
		double[] D = new double[n + 1];
		int i;
		/*
		 * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]|
		 * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
		 * 1| | . | |3(x[n] - x[n-2])| [ 1 2] [D[n]] [3(x[n] - x[n-1])]
		 * 
		 * by using row operations to convert the matrix to upper triangular and
		 * then back sustitution. The D[i] are the derivatives at the knots.
		 */

		gamma[0] = 1.0 / 2.0;
		for (i = 1; i < n; i++) {
			gamma[i] = 1 / (4 - gamma[i - 1]);
		}
		gamma[n] = 1 / (2 - gamma[n - 1]);

		delta[0] = 3 * (x[1] - x[0]) * gamma[0];
		for (i = 1; i < n; i++) {
			delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i];
		}
		delta[n] = (3 * (x[n] - x[n - 1]) - delta[n - 1]) * gamma[n];

		D[n] = delta[n];
		for (i = n - 1; i >= 0; i--) {
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		/* now compute the coefficients of the cubics */
		Cubic[] C = new Cubic[n];
		for (i = 0; i < n; i++) {
			C[i] = new Cubic((double) x[i], D[i], 3 * (x[i + 1] - x[i]) - 2
					* D[i] - D[i + 1], 2 * (x[i] - x[i + 1]) + D[i] + D[i + 1]);
		}
		return C;
	}

	/*
	 * double[] createXPoints(ArrayList<Position> positions, DrawContext dc){
	 * 
	 * double[] xpoints = new double[positions.size()];
	 * 
	 * for(int i=0;i<positions.size();i++){ Position pos = positions.get(i);
	 * Vec4 coord = dc.getGlobe().computePointFromPosition(pos.getLatitude(),
	 * pos.getLongitude(), pos.elevation);
	 * 
	 * xpoints[i]=coord.x; }
	 * 
	 * return xpoints; }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * double[] createYPoints(ArrayList<Position> positions, DrawContext dc){
	 * 
	 * double[] xpoints = new double[positions.size()];
	 * 
	 * for(int i=0;i<positions.size();i++){ Position pos = positions.get(i);
	 * Vec4 coord = dc.getGlobe().computePointFromPosition(pos.getLatitude(),
	 * pos.getLongitude(), pos.elevation);
	 * 
	 * xpoints[i]=coord.y; }
	 * 
	 * return xpoints; }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * double[] createZPoints(ArrayList<Position> positions, DrawContext dc){
	 * 
	 * double[] xpoints = new double[positions.size()];
	 * 
	 * for(int i=0;i<positions.size();i++){ Position pos = positions.get(i);
	 * Vec4 coord = dc.getGlobe().computePointFromPosition(pos.getLatitude(),
	 * pos.getLongitude(), pos.elevation);
	 * 
	 * xpoints[i]=coord.z; }
	 * 
	 * return xpoints; }
	 */

	double[] createXPoints(ArrayList<Vec4> points) {

		double[] xpoints = new double[points.size()];

		for (int i = 0; i < points.size(); i++) {
			Vec4 coord = points.get(i);
			// Vec4 coord =
			// dc.getGlobe().computePointFromPosition(pos.getLatitude(),
			// pos.getLongitude(),
			// pos.elevation);

			xpoints[i] = coord.x;
		}

		return xpoints;
	}

	double[] createYPoints(ArrayList<Vec4> points) {

		double[] xpoints = new double[points.size()];

		for (int i = 0; i < points.size(); i++) {
			Vec4 coord = points.get(i);
			// Vec4 coord =
			// dc.getGlobe().computePointFromPosition(pos.getLatitude(),
			// pos.getLongitude(),
			// pos.elevation);

			xpoints[i] = coord.y;
		}

		return xpoints;
	}

	double[] createZPoints(ArrayList<Vec4> points) {

		double[] xpoints = new double[points.size()];

		for (int i = 0; i < points.size(); i++) {
			Vec4 coord = points.get(i);
			// Vec4 coord =
			// dc.getGlobe().computePointFromPosition(pos.getLatitude(),
			// pos.getLongitude(),
			// pos.elevation);

			xpoints[i] = coord.z;
		}

		return xpoints;
	}

	private boolean isAffectedByLense() {
		if (infoPointsOnLens != null) {
			if (infoPointsOnLens.size() != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compute points on either side of a line segment. This method requires a
	 * point on the line, and either a next point, previous point, or both.
	 *
	 * @param point
	 *            Center point about which to compute side points.
	 * @param prev
	 *            Previous point on the line. May be null if {@code next} is
	 *            non-null.
	 * @param next
	 *            Next point on the line. May be null if {@code prev} is
	 *            non-null.
	 * @param leftPositions
	 *            Left position will be added to this list.
	 * @param rightPositions
	 *            Right position will be added to this list.
	 * @param halfWidth
	 *            Distance from the center line to the left or right lines.
	 * @param globe
	 *            Current globe.
	 */
	/*
	 * protected Vec4 generateParallelPoints(Vec4 point, Vec4 prev, Vec4 next,
	 * double halfWidth, Globe globe) { if ((point == null) || (prev == null &&
	 * next == null)) { String message =
	 * Logging.getMessage("nullValue.PointIsNull");
	 * Logging.logger().severe(message); throw new
	 * IllegalArgumentException(message); }
	 * 
	 * if (globe == null) { String message =
	 * Logging.getMessage("nullValue.GlobeIsNull");
	 * Logging.logger().severe(message); throw new
	 * IllegalArgumentException(message); }
	 * 
	 * Vec4 offset; Vec4 normal = globe.computeSurfaceNormalAtPoint(point);
	 * 
	 * 
	 * 
	 * // Compute vector in the direction backward along the line. Vec4 backward
	 * = (prev != null) ? prev.subtract3(point) : point.subtract3(next);
	 * 
	 * // Compute a vector perpendicular to segment BC, and the globe normal
	 * vector. Vec4 perpendicular = backward.cross3(normal);
	 * 
	 * double length; // If both next and previous points are supplied then
	 * calculate the angle that bisects the angle current, next, prev. if (next
	 * != null && prev != null && !Vec4.areColinear(prev, point, next)) { //
	 * Compute vector in the forward direction. Vec4 forward =
	 * next.subtract3(point);
	 * 
	 * // Calculate the vector that bisects angle ABC. offset =
	 * forward.normalize3().add3(backward.normalize3()); offset =
	 * offset.normalize3();
	 * 
	 * // Compute the scalar triple product of the vector BC, the normal vector,
	 * and the offset vector to // determine if the offset points to the left or
	 * the right of the control line. double tripleProduct =
	 * perpendicular.dot3(offset); if (tripleProduct < 0) { offset =
	 * offset.multiply3(-1); }
	 * 
	 * // Determine the length of the offset vector that will keep the left and
	 * right lines parallel to the control // line. Angle theta =
	 * backward.angleBetween3(offset); if (!Angle.ZERO.equals(theta)){
	 * 
	 * 
	 * length = halfWidth / theta.sin(); } else length = halfWidth; } else {
	 * offset = perpendicular.normalize3(); length = halfWidth; } offset =
	 * offset.multiply3(length);
	 * 
	 * // Determine the left and right points by applying the offset. //Vec4
	 * ptRight = point.add3(offset); Vec4 ptLeft = point.subtract3(offset);
	 * 
	 * // Convert cartesian points to geographic. //Position posLeft =
	 * globe.computePositionFromPoint(ptLeft); // Position posRight =
	 * globe.computePositionFromPoint(ptRight); return ptLeft;
	 * //leftPositions.add(posLeft); //rightPositions.add(posRight); }
	 */
	protected void printSpan(ArrayList<Vec4> span) {

		// System.out.println("print span");

		for (Vec4 item : span) {

			System.out.println(item);
		}

	}

	protected void addSpan(ArrayList<Vec4> span) {
		/*
		 * if (span != null && span.size() > 0) this.currentSpans.add(span);
		 */
		if (span != null && span.size() > 0)
			this.currentListOfVertices.addAll(span);

	}

	protected void addUpdatedSpan(ArrayList<Vec4> span) {
		if (span != null && span.size() > 0)
			this.currentListOfUpdatedVertices.addAll(span);
	}

	public List<Vec4> getVertices() {
		return this.currentListOfVertices;
	}

	protected ArrayList<Vec4> makeSegment(DrawContext dc,
			// Vec4 ptA,
			Vec4 ptB, Cubic cubicCurveFunctionX, Cubic cubicCurveFunctionY,
			Cubic cubicCurveFunctionZ) {

		int numSubsegments = SharedVariables.numSubsegments;

		// System.out.println("Make Segment");
		ArrayList<Vec4> span = null;
		// s va da 0 a 1
		for (int i = 1; i <= numSubsegments; i++) {
			double s = (double) i / (double) numSubsegments;

			if (s > 1) {
				// span = this.clipAndAdd(dc, ptA, span);
			} else {

				double x = cubicCurveFunctionX.eval(s);
				double y = cubicCurveFunctionY.eval(s);
				double z = cubicCurveFunctionZ.eval(s);
				ptB = new Vec4(x, y, z);

			}

			// System.out.println("inserisco punto b: "+ptB);
			span = this.clipAndAdd(dc, ptB, span);

		}

		return span;
	}

	// funzione da modificare

	@SuppressWarnings({ "UnusedDeclaration" })
	protected ArrayList<Vec4> clipAndAdd(DrawContext dc, Vec4 ptB,
			ArrayList<Vec4> span) {

		return this.addPointToSpan(ptB, span);
	}

	protected ArrayList<Vec4> addPointToSpan(Vec4 p, ArrayList<Vec4> span) {

		if (span == null)
			span = new ArrayList<Vec4>();

		// ///////////////////span.add(p.subtract3(this.referenceCenterPoint));
		span.add(p);

		return span;
	}

	private static void printPosition(DrawContext dc, Vec4 p, String where) {

		System.out.println("in " + where);
		System.out.println("elevation!!: " + p);
		// }
	}

	public Position getReferencePosition() {
		if (this.renderableControlPoints.getPositions().size() < 1) {
			return null;
		} else if (this.renderableControlPoints.getPositions().size() < 3) {
			return this.renderableControlPoints.getPositions().get(0);
		} else {
			return this.renderableControlPoints.getPositions().get(
					this.renderableControlPoints.getPositions().size() / 2);
		}
	}

	public void move(Position delta) {
		if (delta == null) {
			String msg = Logging.getMessage("nullValue.PositionIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		Position refPos = this.getReferencePosition();

		// The reference position is null if this Polyline has no positions. In
		// this case moving the Polyline by a
		// relative delta is meaningless because the Polyline has no geographic
		// location. Therefore we fail softly by
		// exiting and doing nothing.
		if (refPos == null)
			return;

		this.moveTo(refPos.add(delta));
	}

	public void moveTo(Position position) {
		if (position == null) {
			String msg = Logging.getMessage("nullValue.PositionIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		this.reset();
		this.extents.clear();

		Position oldRef = this.getReferencePosition();

		// The reference position is null if this Polyline has no positions. In
		// this case moving the Polyline to a new
		// reference position is meaningless because the Polyline has no
		// geographic location. Therefore we fail softly
		// by exiting and doing nothing.
		if (oldRef == null)
			return;

		double elevDelta = position.getElevation() - oldRef.getElevation();

		for (int i = 0; i < this.renderableControlPoints.getPositions().size(); i++) {
			Position pos = this.renderableControlPoints.getPositions().get(i);

			Angle distance = LatLon.greatCircleDistance(oldRef, pos);
			Angle azimuth = LatLon.greatCircleAzimuth(oldRef, pos);
			LatLon newLocation = LatLon.greatCircleEndPosition(position,
					azimuth, distance);
			double newElev = pos.getElevation() + elevDelta;

			this.renderableControlPoints.getPositions().set(i,
					new Position(newLocation, newElev));
		}
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public void setLineIndex(int index) {
		this.lineIndex = index;
	}

	public MyRenderableLayer getLayer() {
		return myRendLayer;
	}

	public void setRendLayer(MyRenderableLayer layer) {
		this.myRendLayer = layer;
	}

	public int getNumRendControlPoints() {
		return renderableControlPoints.getSize();
	}

	public void setLastNodeWorldPosition(Vec4 pos) {
		lastNodeWorldPosition = pos;
	}

	public void setFirstNodeWorldPosition(Vec4 pos) {
		firstNodeWorldPosition = pos;
	}

}
