/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.smeSpire.layers;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;
import it.graphitech.ColorsList;
import it.graphitech.core.MyAbstractSceneController;
import it.graphitech.core.MyBasicSceneController;
import it.graphitech.shader.GLSL;
import it.graphitech.smeSpire.SectorManager;
import it.graphitech.smeSpire.SharedVariables;
import it.graphitech.smeSpire.lines.cubicCurve.CubicSplinePolyline;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * The <code>RenderableLayer</code> class manages a collection of
 * {@link gov.nasa.worldwind.render.Renderable} objects for rendering, picking,
 * and disposal.
 * 
 * @author tag
 * @version $Id: RenderableLayer.java 607 2012-05-29 16:20:51Z tgaskins $
 * @see gov.nasa.worldwind.render.Renderable
 */
public class MyRenderableLayer extends AbstractLayer {

	boolean renderUndesiredArcs;
	
	HashMap<String, Integer> nodesWithIndex = new HashMap<>();
	int indexNode =0;
	
	 FloatBuffer bufferAllPoints;
	 IntBuffer bufferAllIndexVectices;
	 
	 IntBuffer vertexArray_forBufferAllPoints;
	 IntBuffer vertexArray_forBufferAllIndexVectices;
	 
	//IntBuffer indexArray;
	boolean isFirstTimeForVBO = true;
	boolean isFirstTimeForBindBuffer = true;
	IntBuffer vertexArray;
	IntBuffer updVertexArray;
	IntBuffer verticesIndexArray;
	
protected ArrayList<Renderable> renderables = new ArrayList<Renderable>();
	protected Iterable<Renderable> renderablesOverride;
	
	/**
	 * Creates a new <code>RenderableLayer</code> with a null
	 * <code>delegateOwner</code>
	 */
	public MyRenderableLayer() {
		
	}
	
	
	GLSL shader;
	
	public void setShader(GLSL shader){
		this.shader=shader;
	}
	
	public void useShader(GL2 gl){
		//shader.useShaders();
		shader.startShader();

		 int loc_lensDiameter = -1;
 		int loc_lensCenter = -1;
 		  Vec4 sp = SharedVariables.screenPoint;      
 		
 	//	System.out.println("renderizzo con shader!!!");
 		 loc_lensCenter = gl.glGetUniformLocation(shader.getProgramObject(), "centerPos");
 		 loc_lensDiameter = gl.glGetUniformLocation(shader.getProgramObject(), "lensDiameter");
 		 gl.glUniform2f(loc_lensCenter, (float)sp.x,(float)sp.y);
 		 gl.glUniform1f(loc_lensDiameter, (float)SharedVariables.lense_h);
	}
	
public void dontUseShader(){
	shader.endShader();
	}
	
	
	public boolean isRenderUndesiredArcs() {
	return renderUndesiredArcs;
}

	/**
	 * Adds the specified <code>renderable</code> to this layer's internal
	 * collection. If this layer's internal collection has been overridden with
	 * a call to {@link #setRenderables(Iterable)}, this will throw an
	 * exception.
	 * <p/>
	 * If the <code>renderable</code> implements
	 * {@link gov.nasa.worldwind.avlist.AVList}, the layer forwards its property
	 * change events to the layer's property change listeners. Any property
	 * change listeners the layer attaches to the <code>renderable</code> are
	 * removed in
	 * {@link #removeRenderable(gov.nasa.worldwind.render.Renderable)},
	 * {@link #removeAllRenderables()}, or {@link #dispose()}.
	 * 
	 * @param renderable
	 *            Renderable to add.
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>renderable</code> is null.
	 * @throws IllegalStateException
	 *             If a custom Iterable has been specified by a call to
	 *             <code>setRenderables</code>.
	 */
	public int getBufferAllPointsIndex(){
		return vertexArray_forBufferAllPoints.get(0);
	}
	
	public void addRenderable(Renderable renderable) {
		if (renderable == null) {
			String msg = Logging.getMessage("nullValue.RenderableIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		if (this.renderablesOverride != null) {
			String msg = Logging
					.getMessage("generic.LayerIsUsingCustomIterable");
			Logging.logger().severe(msg);
			throw new IllegalStateException(msg);
		}

		this.renderables.add(renderable);

		// Attach the layer as a property change listener of the renderable.
		// This forwards property change events from
		// the renderable to the SceneController.
		if (renderable instanceof AVList)
			((AVList) renderable).addPropertyChangeListener(this);
	}

	/**
	 * Adds the contents of the specified <code>renderables</code> to this
	 * layer's internal collection. If this layer's internal collection has been
	 * overriden with a call to {@link #setRenderables(Iterable)}, this will
	 * throw an exception.
	 * <p/>
	 * If any of the <code>renderables</code> implement
	 * {@link gov.nasa.worldwind.avlist.AVList}, the layer forwards their
	 * property change events to the layer's property change listeners. Any
	 * property change listeners the layer attaches to the
	 * <code>renderable</code> are removed in
	 * {@link #removeRenderable(gov.nasa.worldwind.render.Renderable)},
	 * {@link #removeAllRenderables()}, or {@link #dispose()}.
	 * 
	 * @param renderables
	 *            Renderables to add.
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>renderables</code> is null.
	 * @throws IllegalStateException
	 *             If a custom Iterable has been specified by a call to
	 *             <code>setRenderables</code>.
	 */
	public void addRenderables(Iterable<? extends Renderable> renderables) {
		if (renderables == null) {
			String msg = Logging.getMessage("nullValue.IterableIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		if (this.renderablesOverride != null) {
			String msg = Logging
					.getMessage("generic.LayerIsUsingCustomIterable");
			Logging.logger().severe(msg);
			throw new IllegalStateException(msg);
		}

		for (Renderable renderable : renderables) {
			// Internal list of renderables does not accept null values.
			if (renderable != null)
				this.renderables.add(renderable);

			// Attach the layer as a property change listener of the renderable.
			// This forwards property change events
			// from the renderable to the SceneController.
			if (renderable instanceof AVList)
				((AVList) renderable).addPropertyChangeListener(this);
		}
	}

	/**
	 * Removes the specified <code>renderable</code> from this layer's internal
	 * collection, if it exists. If this layer's internal collection has been
	 * overridden with a call to {@link #setRenderables(Iterable)}, this will
	 * throw an exception.
	 * <p/>
	 * If the <code>renderable</code> implements
	 * {@link gov.nasa.worldwind.avlist.AVList}, this stops forwarding the its
	 * property change events to the layer's property change listeners. Any
	 * property change listeners the layer attached to the
	 * <code>renderable</code> in
	 * {@link #addRenderable(gov.nasa.worldwind.render.Renderable)} or
	 * {@link #addRenderables(Iterable)} are removed.
	 * 
	 * @param renderable
	 *            Renderable to remove.
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>renderable</code> is null.
	 * @throws IllegalStateException
	 *             If a custom Iterable has been specified by a call to
	 *             <code>setRenderables</code>.
	 */
	public void removeRenderable(Renderable renderable) {
		if (renderable == null) {
			String msg = Logging.getMessage("nullValue.RenderableIsNull");
			Logging.logger().severe(msg);
			throw new IllegalArgumentException(msg);
		}

		if (this.renderablesOverride != null) {
			String msg = Logging
					.getMessage("generic.LayerIsUsingCustomIterable");
			Logging.logger().severe(msg);
			throw new IllegalStateException(msg);
		}

		this.renderables.remove(renderable);

		// Remove the layer as a property change listener of the renderable.
		// This prevents the renderable from keeping a
		// dangling reference to the layer.
		if (renderable instanceof AVList)
			((AVList) renderable).removePropertyChangeListener(this);
	}

	/**
	 * Clears the contents of this layer's internal Renderable collection. If
	 * this layer's internal collection has been overriden with a call to
	 * {@link #setRenderables(Iterable)}, this will throw an exception.
	 * <p/>
	 * If any of the <code>renderables</code> implement
	 * {@link gov.nasa.worldwind.avlist.AVList}, this stops forwarding their
	 * property change events to the layer's property change listeners. Any
	 * property change listeners the layer attached to the
	 * <code>renderables</code> in
	 * {@link #addRenderable(gov.nasa.worldwind.render.Renderable)} or
	 * {@link #addRenderables(Iterable)} are removed.
	 * 
	 * @throws IllegalStateException
	 *             If a custom Iterable has been specified by a call to
	 *             <code>setRenderables</code>.
	 */
	public void removeAllRenderables() {
		if (this.renderablesOverride != null) {
			String msg = Logging
					.getMessage("generic.LayerIsUsingCustomIterable");
			Logging.logger().severe(msg);
			throw new IllegalStateException(msg);
		}

		this.clearRenderables();
	}

	protected void clearRenderables() {
		if (this.renderables != null && this.renderables.size() > 0) {
			// Remove the layer as property change listener of any renderables.
			// This prevents the renderables from
			// keeping a dangling references to the layer.
			for (Renderable renderable : this.renderables) {
				if (renderable instanceof AVList)
					((AVList) renderable).removePropertyChangeListener(this);
			}

			this.renderables.clear();
		}
	}

	public int getNodeIndex(String pos){
		return nodesWithIndex.get(pos);
	}
	
	public void addNodexWithIndex(String pos){
		if(!nodesWithIndex.containsKey(pos)){
		nodesWithIndex.put(pos, indexNode);
		indexNode++;
		}
	}
	
	public int getNumRenderables() {
		if (this.renderablesOverride != null) {
			int size = 0;
			// noinspection UnusedDeclaration
			for (Renderable r : this.renderablesOverride) {
				++size;
			}

			return size;
		} else {
			return this.renderables.size();
		}
	}

	/**
	 * Returns the Iterable of Renderables currently in use by this layer. If
	 * the caller has specified a custom Iterable via
	 * {@link #setRenderables(Iterable)}, this will returns a reference to that
	 * Iterable. If the caller passed <code>setRenderables</code> a null
	 * parameter, or if <code>setRenderables</code> has not been called, this
	 * returns a view of this layer's internal collection of Renderables.
	 * 
	 * @return Iterable of currently active Renderables.
	 */
	public Iterable<Renderable> getRenderables() {
		return this.getActiveRenderables();
	}

	/**
	 * Returns the Iterable of currently active Renderables. If the caller has
	 * specified a custom Iterable via {@link #setRenderables(Iterable)}, this
	 * will returns a reference to that Iterable. If the caller passed
	 * <code>setRenderables</code> a null parameter, or if
	 * <code>setRenderables</code> has not been called, this returns a view of
	 * this layer's internal collection of Renderables.
	 * 
	 * @return Iterable of currently active Renderables.
	 */
	protected Iterable<Renderable> getActiveRenderables() {
		if (this.renderablesOverride != null) {
			return this.renderablesOverride;
		} else {
			// Return an unmodifiable reference to the internal list of
			// renderables.
			// This prevents callers from changing this list and invalidating
			// any invariants we have established.
			return java.util.Collections
					.unmodifiableCollection(this.renderables);
		}
	}

	/**
	 * Overrides the collection of currently active Renderables with the
	 * specified <code>renderableIterable</code>. This layer will maintain a
	 * reference to <code>renderableIterable</code> strictly for picking and
	 * rendering. This layer will not modify the reference, or dispose of its
	 * contents. This will also clear and dispose of the internal collection of
	 * Renderables, and will prevent any modification to its contents via
	 * <code>addRenderable,
	 * addRenderables, removeRenderables, or dispose</code>.
	 * <p/>
	 * Unlike {@link #addRenderable(gov.nasa.worldwind.render.Renderable)} or
	 * {@link #addRenderables(Iterable)}, this does not forward any of the
	 * renderable's property change events to the layer's property change
	 * listeners. Since the layer is not in control of the iIterable's contents,
	 * attaching property change listeners to the renderables could cause the
	 * them to hold dangling references to the layer. If any of the renderables
	 * in the Iterable rely on forwarding property change events for proper
	 * operation - such as
	 * {@link gov.nasa.worldwind.render.AbstractBrowserBalloon} - use
	 * {@link #addRenderables(Iterable)} instead.
	 * <p/>
	 * If the specified <code>renderableIterable</code> is null, this layer
	 * reverts to maintaining its internal collection.
	 * 
	 * @param renderableIterable
	 *            Iterable to use instead of this layer's internal collection,
	 *            or null to use this layer's internal collection.
	 */
	public void setRenderables(Iterable<Renderable> renderableIterable) {
		this.renderablesOverride = renderableIterable;
		// Dispose of the internal collection of Renderables.
		this.disposeRenderables();
		// Clear the internal collection of Renderables.
		this.clearRenderables();
	}

	/**
	 * Opacity is not applied to layers of this type because each renderable
	 * typically has its own opacity control.
	 * 
	 * @param opacity
	 *            the current opacity value, which is ignored by this layer.
	 */
	@Override
	public void setOpacity(double opacity) {
		super.setOpacity(opacity);
	}

	/**
	 * Returns the layer's opacity value, which is ignored by this layer because
	 * each of its renderables typiically has its own opacity control.
	 * 
	 * @return The layer opacity, a value between 0 and 1.
	 */
	@Override
	public double getOpacity() {
		return super.getOpacity();
	}

	/**
	 * Disposes the contents of this layer's internal Renderable collection, but
	 * does not remove any elements from that collection.
	 * <p/>
	 * If any of layer's internal Renderables implement
	 * {@link gov.nasa.worldwind.avlist.AVList}, this stops forwarding their
	 * property change events to the layer's property change listeners. Any
	 * property change listeners the layer attached to the
	 * <code>renderables</code> in
	 * {@link #addRenderable(gov.nasa.worldwind.render.Renderable)} or
	 * {@link #addRenderables(Iterable)} are removed.
	 * 
	 * @throws IllegalStateException
	 *             If a custom Iterable has been specified by a call to
	 *             <code>setRenderables</code>.
	 */
	public void dispose() {
		if (this.renderablesOverride != null) {
			String msg = Logging
					.getMessage("generic.LayerIsUsingCustomIterable");
			Logging.logger().severe(msg);
			throw new IllegalStateException(msg);
		}

		this.disposeRenderables();
	}

	protected void disposeRenderables() {
		if (this.renderables != null && this.renderables.size() > 0) {
			for (Renderable renderable : this.renderables) {
				try {
					// Remove the layer as a property change listener of the
					// renderable. This prevents the renderable
					// from keeping a dangling reference to the layer.
					if (renderable instanceof AVList)
						((AVList) renderable)
								.removePropertyChangeListener(this);

					if (renderable instanceof Disposable)
						((Disposable) renderable).dispose();
				} catch (Exception e) {
					String msg = Logging
							.getMessage("generic.ExceptionAttemptingToDisposeRenderable");
					Logging.logger().severe(msg);
					// continue to next renderable
				}
			}
		}

		this.renderables.clear();
	}

	protected void doPreRender(DrawContext dc) {
		createVBO(dc);
		this.doPreRender(dc, this.getActiveRenderables());
	}

	
	private void createVBO_ForAllPoints(DrawContext dc, int size) {
		GL gl = dc.getGL();
		vertexArray_forBufferAllPoints = IntBuffer.allocate(size);
		gl.glGenBuffers(size, vertexArray_forBufferAllPoints);
	}
	private void createVBO_ForAllIndexVertices(DrawContext dc, int size) {
		GL gl = dc.getGL();
		vertexArray_forBufferAllIndexVectices = IntBuffer.allocate(size);
		gl.glGenBuffers(size, vertexArray_forBufferAllIndexVectices);
	}
	
	private void createVBO(DrawContext dc, int size) {
		GL gl = dc.getGL();
		vertexArray = IntBuffer.allocate(size);
		gl.glGenBuffers(size, vertexArray);
	}
	
	private void createVBOUpdVertices(DrawContext dc, int size) {
		GL gl = dc.getGL();
		updVertexArray = IntBuffer.allocate(size);
		gl.glGenBuffers(size, updVertexArray);
		
		System.out.println("createVBOUpdVertices: "+size);
		
	}

	/*
	public int getBufferElement(int ind) {
		return vertexArray.get(ind);
	}
*/
	public int getUpdBufferElement(int ind) {
		return updVertexArray.get(ind);
	}
	
	/*
	public int getVectivesIndexElement(int ind) {
		return verticesIndexArray.get(ind);
	}
	*/
	private void createVBO(DrawContext dc) {
		if (isFirstTimeForVBO) {
			
			
		
			System.out.println("FIRST TIME");
			int ind = 0;
			for (Renderable renderable : renderables) {
			((CubicSplinePolyline) renderable).setLineIndex(ind);
				((CubicSplinePolyline) renderable).setRendLayer(this);
				ind++;

			}
			createVBO(dc, this.getNumRenderables());
			createVBOUpdVertices(dc, this.getNumRenderables());
			//createIndexBuffer(dc,this.getNumRenderables());
			createVBOIndices(dc,  this.getNumRenderables());
			
			
			createVBO_ForAllPoints(dc, 1);
			createVBO_ForAllIndexVertices(dc, 1);
			
			int numVerticesPerLine;
			if(bufferAllPoints==null){
				// numVerticesPerLine = ((SharedVariables.numSubsegments+1)*(SharedVariables.numControlPoints-1))-(SharedVariables.numControlPoints-2);
				 numVerticesPerLine = SharedVariables.computeNumVerticesPerLine();
				System.out.println("numVerticesPerLine: "+numVerticesPerLine);
				bufferAllPoints        = initFloatBuffer(numVerticesPerLine*renderables.size()*3);
				bufferAllIndexVectices = initIntBuffer(numVerticesPerLine*renderables.size());
			}
			
			
			
			isFirstTimeForVBO = false;
		}
	}

	private void createVBOIndices(DrawContext dc, int size) {
		GL gl = dc.getGL();
		verticesIndexArray = IntBuffer.allocate(size);
		gl.glGenBuffers(size, verticesIndexArray);
		
		System.out.println("verticesIndexArray: "+size);
		
	}
	
	
	/*
	 public static final int SIZEOF_INT = 4;
	 private IntBuffer newIntBuffer(int numElements){
	 ByteBuffer bb = newByteBuffer(numElements * SIZEOF_INT);
	    return bb.asIntBuffer();
	 }
	 public static ByteBuffer newByteBuffer(int numElements) {
		    ByteBuffer bb = ByteBuffer.allocateDirect(numElements);
		    bb.order(ByteOrder.nativeOrder());
		    return bb;
		  }
	  public static final int SIZEOF_FLOAT = 4;
		 private FloatBuffer newFloatBuffer(int numElements){
		 ByteBuffer bb = newByteBuffer(numElements * SIZEOF_FLOAT);
		    return bb.asFloatBuffer();
		 }
	 */

	protected void doPick(DrawContext dc, java.awt.Point pickPoint) {
	
	}

	private void clearFrame(DrawContext dc) {
		Color cc = dc.getClearColor();
		dc.getGL().glClearColor(cc.getRed(), cc.getGreen(), cc.getBlue(),
				cc.getAlpha());
		dc.getGL().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

	protected void doRender(DrawContext dc) {

		Vec4 sp = SharedVariables.screenPoint;
		if (SharedVariables.showIntermediatePoint) {
			
			SharedVariables.sr.draw(dc, sp, 3);
		}

		
		if(SharedVariables.debugMode){
		System.out.println("start time for normal render");
		}
		long startTime = System.currentTimeMillis();
		//orderArcsInSectors(dc);
	//SharedVariables.printSectorContents();
		
		this.doRenderNow(dc);
		long estimatedTime = System.currentTimeMillis() - startTime;
		if(SharedVariables.debugMode){
		System.out.println("time needed for normal render: "
				+ estimatedTime);
		}
	}

	private void orderArcsInSectors(DrawContext dc) {
		//System.out.println("genero settori");
		
		for (Renderable renderable : renderables) {
			CubicSplinePolyline arc = ((CubicSplinePolyline) renderable);
			int arcIndex = arc.getLineIndex();
			
			ArrayList<Vec4> pointOnLens = arc.getInfoPointsOnLens();
			
			if(pointOnLens!=null){
			pointOnLens= checkInfoPointsOnLens(pointOnLens);
			}
			arc.infoPointsOnLens=pointOnLens;
			
			Vec4 mp = null;
			if(pointOnLens!=null){
			if (pointOnLens.size() == 2) {
				
				Vec4 infoPoint1 = pointOnLens.get(0);
				Vec4 infoPoint2 = pointOnLens.get(1);
				
				int indexPoint1 = (int)infoPoint1.z;
				int indexPoint2 = (int)infoPoint2.z;
				
					
				Vec4 v1 = SharedVariables.computeScreenCoordinates(arc.getVertices().get(indexPoint1));
				Vec4 v2 = SharedVariables.computeScreenCoordinates(arc.getVertices().get(indexPoint2));
				
				
				
				mp = arc.calculateMiddelPoint(v1,v2);
				 //mp = arc.calculateMiddelPoint(pointOnLens.get(0),pointOnLens.get(1));
				// SharedVariables.sr.draw(dc, mp,1);
			}
			if (pointOnLens.size() == 4) {
				double d1 = SharedVariables
						.computeWorldCoordinatesFromScreenPoint(
								pointOnLens.get(0)).distanceTo3(
								dc.getView().getEyePoint());
				double d2 = SharedVariables
						.computeWorldCoordinatesFromScreenPoint(
								pointOnLens.get(3)).distanceTo3(
								dc.getView().getEyePoint());

				int i = -1;
				if (d1 < d2) {
					i = 0;
				} else {
					i = 2;
				}

				 mp = arc.calculateMiddelPoint(pointOnLens.get(i),
						pointOnLens.get(i + 1));

			}
			}
			
			if(mp!=null){
			int sector = SectorManager.calculateSector(mp);
			//double distance=mp.distanceTo2(SharedVariables.screenPoint);
			
			double distance=Math.sqrt((mp.x*mp.x)+(mp.y*mp.y));
			//System.out.println("insert arco con indice "+arcIndex+" con distance: "+distance);
			SectorManager.mapArcDistance.put(arcIndex+"-"+1, distance);
			//ArrayList<Integer> listIdArcsInSector = SharedVariables.lensSectorsMap.get(sector);
			ArrayList<String> listIdArcsInSector = SectorManager.lensSectorsMap.get(sector);
			if(listIdArcsInSector.size()!=0){
			
				//System.out.println("la list del settore "+sector+" non è vuota");
				boolean inserted = false;
				for(int i=0;i<listIdArcsInSector.size();i++){
				if(distance<SectorManager.mapArcDistance.get(listIdArcsInSector.get(i))){
					//System.out.println("inserisco "+arcIndex+" in index: "+i);
					listIdArcsInSector.add(i, arcIndex+"-"+1);
					inserted=true;
					break;
				}
			}		
				if(inserted==false){
					//System.out.println("inserisco "+arcIndex+" in fondo");
					listIdArcsInSector.add(arcIndex+"-"+1);
				}
			}
			
			else{
				//System.out.println("la list del settore "+sector+" è vuota");
				listIdArcsInSector.add(arcIndex+"-"+1);
			}
			}

		}
	}

	private ArrayList<Vec4> checkInfoPointsOnLens(ArrayList<Vec4> pointOnLens){
		
		if(pointOnLens.size()==1){
			return new ArrayList<>();
		}
		if(pointOnLens.size()==3){
			System.out.println("ha 3 elementi");
			
			for(Vec4 v : pointOnLens){
				System.out.println("punto: "+v.w);
			}
			
			
			if(pointOnLens.get(0).w==1){
				System.out.println("rimuovo il primo");
				pointOnLens.remove(0);
			}else if(pointOnLens.get(2).w==0){
				System.out.println("rimuovo l'ultimo");
				pointOnLens.remove(2);
			}
		
			
		}
		return pointOnLens;
	}
	
	@Override
	public boolean isPickEnabled() {
		// TODO Auto-generated method stub
		// return true;
		return super.isPickEnabled();
	}

	protected void doPreRender(DrawContext dc,
			Iterable<? extends Renderable> renderables) {
		
		/*
		for (Renderable renderable : renderables) {
			try {
				// If the caller has specified their own Iterable,
				// then we cannot make any guarantees about its contents.
				if (renderable != null && renderable instanceof PreRenderable)
					((PreRenderable) renderable).preRender(dc);
			} catch (Exception e) {
				String msg = Logging
						.getMessage("generic.ExceptionWhilePrerenderingRenderable");
				Logging.logger().severe(msg);
				// continue to next renderable
			}
		}
		*/
		
		
	}


	protected void doRenderNow(DrawContext dc
		//	,Iterable<? extends Renderable> renderables
			) {
	
		
		if(MyAbstractSceneController.renderTextureForPointsDetection){
			//System.out.println("inizio renderTextureForPointsDetection");
			  GL2 gl = dc.getGL().getGL2();
				
			
	            MyBasicSceneController.fboManager.transformFeedback.initTransformFeedbackBuffer();
	           
	           // System.out.println("...................");
	            gl.glEnable(GL2.GL_RASTERIZER_DISCARD);            
	            MyBasicSceneController.fboManager.transformFeedback.bind();
	            MyBasicSceneController.fboManager.transformFeedback.begin(GL2.GL_POINTS);
	            
	            SectorManager.initLensAnglesMap(SectorManager.numSectors);
				SectorManager.mapArcDistance= new HashMap<String, Double>();
				
				
				
				
	            
			for (Renderable renderable : renderables) {
				try {
					// If the caller has specified their own Iterable,
					// then we cannot make any guarantees about its contents.
					if (renderable != null) {
						
						CubicSplinePolyline line = (CubicSplinePolyline)renderable;
						line.initPreProcessingStuff();
						
					if(line.getVertices()==null){
						line.makeOriginalVertices(dc);
						line.createBuffers(dc);
						
						bufferAllPoints.put(line.getBufferPoints());
						line.getBufferPoints().position(0);
						bufferAllIndexVectices.put(line.getBufferIndexVectices());
						line.getBufferIndexVectices().position(0);
						
						
					}
			         
					
				
					/*
					//line.drawNewOrderedRenderable(dc);
					 int geometryType =  GL2.GL_LINE_STRIP_ADJACENCY_ARB; 
		         		line.drawLines(gl,geometryType);
					//	renderable.render(dc);
*/
						
					
					}
				} catch (Exception e) {
					String msg = Logging
							.getMessage("generic.ExceptionWhileRenderingRenderable");
					Logging.logger().log(java.util.logging.Level.SEVERE, msg, e);
					// continue to next renderable
				}

			
			}
			
			/*
			bufferAllIndexVectices.position(0);
			while(bufferAllIndexVectices.hasRemaining()){
				int value = bufferAllIndexVectices.get();
				System.out.println("currValue: "+value);
			}
			*/
			
			//int numVerticesPerLine = ((SharedVariables.numSubsegments+1)*(SharedVariables.numControlPoints-1))-(SharedVariables.numControlPoints-2);
			int numVerticesPerLine = SharedVariables.computeNumVerticesPerLine();
			
			if(isFirstTimeForBindBuffer){
				
				
				int numTotElements = numVerticesPerLine*renderables.size()*3;
				int numTotVertices = numVerticesPerLine*renderables.size();
				
				System.out.println("BIND bufferAllPoints, index: "+vertexArray_forBufferAllPoints.get(0)+" with size: "+numTotElements);
				System.out.println("BIND bufferAllIndexVectices, index: "+vertexArray_forBufferAllIndexVectices.get(0)+" with size: "+numTotVertices);
				
				bindFloatData(dc, numTotElements, bufferAllPoints,vertexArray_forBufferAllPoints.get(0));
				
				
				
				
				bindIntData(dc, numTotVertices, bufferAllIndexVectices,vertexArray_forBufferAllIndexVectices.get(0));
			
			
			isFirstTimeForBindBuffer=false;
			}
			
			
			
			 int geometryType =  GL2.GL_LINE_STRIP_ADJACENCY_ARB; 
			drawAllLines(gl, geometryType, numVerticesPerLine*renderables.size());
			
			 MyBasicSceneController.fboManager.transformFeedback.end();
	            gl.glFlush();
	            gl.glDisable(GL2.GL_RASTERIZER_DISCARD);        
	    
			
	         //   MyBasicSceneController.fboManager.transformFeedback.readFloatBuffer();
	           // System.out.println("fine renderTextureForPointsDetection");
		}
		else{
		//	System.out.println("inizio normal");
			//usare il external feedback buffer
			
		/*
			IntBuffer fBuffer = MyBasicSceneController.fboManager.transformFeedback.getIntBuffer();			
			
			for (int i = 0; i <fBuffer.limit(); i++) {
	            	
	            	int isValid = fBuffer.get(i);	            	i++;
	            	int lineIndex  =  fBuffer.get(i);	            	i++;
	            	//int lineIndex  =  (int)(fBuffer.get(i));	            	i++;
	            	int pointIndex = fBuffer.get(i);	            	i++;
	            	int type =        fBuffer.get(i);	            	
	      */      	
	            	
		
			
			FloatBuffer fBuffer = MyBasicSceneController.fboManager.transformFeedback.getFloatBuffer();			
				System.out.println("CHECK BUFFER");
			for (int i = 0; i <fBuffer.limit(); i++) {
	            	
	            	float isValid = fBuffer.get(i);	            	i++;
	            	float lineIndex  =  fBuffer.get(i);	            	i++;
	            	//int lineIndex  =  (int)(fBuffer.get(i));	            	i++;
	            	float pointIndex = fBuffer.get(i);	            	i++;
	            	float type =        fBuffer.get(i);	            	
	            	
	            	
	            	              
	            		                
	            		               
	            		                if(isValid==0){
	            		                	break;
	            		                }
	            		               	            		                
	            		                 
	            		                CubicSplinePolyline line = (CubicSplinePolyline)renderables.get(Math.round(lineIndex));
	            		                
	            		                Vec4 v = new Vec4(isValid, lineIndex, pointIndex, type);
	            		                
	            		                Vec4 currVertex = line.getVertices().get((int)v.z);
	            		                if(SharedVariables.isPositionVisible(dc,currVertex)){	            		                
	            		                	
/*
		            		                System.out.println("(x) isValid: "+ isValid);
		            		                System.out.println("(y) lineIndex: (float) "+lineIndex+" (int) "+(int)lineIndex);
		            		                System.out.println("(z) pointIndex: "+pointIndex);
		            		                System.out.println("(w) type: "+type);
	*/            		                
	            		                if((type==1.0)||(type==0.0)){
	            		                	line.infoPointsOnLens.add(v);
	            		                }else if(type==3.0){
	            		                 }else if(type==4.0){
	            		                	line.setFirstNodeInsideLense(true);
	            		                	line.setFirstNodeWorldPosition(currVertex);
	            		                }else if(type==5.0){
	            		                	line.setLastNodeInsideLense(true);
	            		                	line.setLastNodeWorldPosition(currVertex);
	            		                }
	            		                }
	            		               
	            		               
	            		                
if(i>MyBasicSceneController.fboManager.transformFeedback.getNumTransformFeedbackBufferElements()-8){
	System.out.println("transformFeedback QUESIPIENO!!!!!!!!!!!!!!!!!!!!!!!!!!");
}
	            }
			
			
		SharedVariables.counterOfDistortedLines=0;
		SharedVariables.counterOfFilteredLines=0;
		SharedVariables.counterOfSimpleLines=0;
		SharedVariables.counterOfLinesInsideLens=0;
			
	       
			orderArcsInSectors(dc);
    
			
	//		ColorsList.resetIndex();
			
			//FIRST RENDER THE DEFORMED ARCS
		
			
			renderUndesiredArcs = true;
		for (Renderable renderable : renderables) {
				if (renderable != null) {
					renderable.render(dc);
			}			 
		}

		dc.getGL().glClear(GL2.GL_DEPTH_BUFFER_BIT);
		dc.getGL().glColorMask(false, false, false, false);
		dc.getLayers().getLayerByName(MyAbstractSceneController.layerEarthName).render(dc); 
		dc.getGL().glColorMask(true, true, true, true);
		
		
		
	renderUndesiredArcs = false;
		//THEN RENDER THE COLORED ARCS
		for (Renderable renderable : renderables) {
				if (renderable != null) {
					renderable.render(dc);
			}			 
		}

	
		 
		if(SharedVariables.showTextures){
		//SharedVariables.sr.drawFrameBuffer(dc);
			SharedVariables.sr.drawFrameBufferOnTheScreen(dc);
		}
		
				
		System.out.println("counterOfDistortedLines: "+SharedVariables.counterOfDistortedLines);
		System.out.println("counterOfFilteredLines: "+SharedVariables.counterOfFilteredLines);
		System.out.println("counterOfSimpleLines: "+SharedVariables.counterOfSimpleLines);
		
		}
	}

	
	
	  public void drawAllLines(GL2 gl,int geometryType, int numVertices){
	    
		 gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexArray_forBufferAllPoints.get(0));
	    	
	   	  gl.glEnableVertexAttribArray(0);            	  	  
		        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);    
		     
			        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexArray_forBufferAllIndexVectices.get(0));
		    	  gl.glEnableVertexAttribArray(1);            	  	  
			        gl.glVertexAttribPointer(1, 1, GL2.GL_FLOAT , false, 0, 0);     
		        //  gl.glVertexAttribPointer(1, 1, GL2.GL_INT , false, 0, 0);
		        
			    
			        /*
		        gl.glDrawElements(
		       		 geometryType,      // mode
		       		 bufferPointSize,    // count
		            GL.GL_UNSIGNED_INT,   // type
		            0           // element array buffer offset
		           // (bufferPointSize-1)*index
		        );
		        */
		       
		        
		        
		       gl.glDrawArrays(geometryType, 0, numVertices); 
		       
		        gl.glDisableVertexAttribArray(0);
		        gl.glDisableVertexAttribArray(1);
		        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);       
		     
	    }
	
	
	 private void bindFloatData(DrawContext dc, int numElements,FloatBuffer bufferPoints, int indexBuffer){
		    	// Create Vertex Array.		 
		 bufferPoints.position(0);		 
		    	 	GL gl=dc.getGL();
		    	 	gl.glBindBuffer(GL.GL_ARRAY_BUFFER, indexBuffer);   
		    	gl.glBufferData(GL.GL_ARRAY_BUFFER, numElements*SharedVariables.SIZEOF_FLOAT, bufferPoints, GL.GL_STATIC_DRAW);
		    	gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);		    	   	
		    }
	 
	 
	 
	 private void bindIntData(DrawContext dc, int numElements,IntBuffer bufferPoints, int indexBuffer){
	    	// Create Vertex Array.		 
	 bufferPoints.position(0);		 
	    	 	GL gl=dc.getGL();
	    	 	gl.glBindBuffer(GL.GL_ARRAY_BUFFER, indexBuffer);   
	    	gl.glBufferData(GL.GL_ARRAY_BUFFER, numElements*SharedVariables.SIZEOF_INT, bufferPoints, GL.GL_STATIC_DRAW);
	    	gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);		    	   	
	    }
	
	 
	 
	 private IntBuffer initIntBuffer(int size){
		   
			    	IntBuffer pointsBuffer = SharedVariables.newIntBuffer(size);
			    	return pointsBuffer;
			    }
	
	 private FloatBuffer initFloatBuffer(int size){
		   
	    	FloatBuffer pointsBuffer = SharedVariables.newFloatBuffer(size);
	    	return pointsBuffer;
	    }

	 
	 
	
	protected boolean isCubicCurve(Object o) {
		if (o instanceof CubicSplinePolyline) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return Logging.getMessage("layers.RenderableLayer.Name");
	}
	
	

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation forwards the message to each Renderable that
	 * implements {@link MessageListener}.
	 * 
	 * @param message
	 *            The message that was received.
	 */
	@Override
	public void onMessage(Message message) {
		for (Renderable renderable : this.renderables) {
			try {
				if (renderable instanceof MessageListener)
					((MessageListener) renderable).onMessage(message);
			} catch (Exception e) {
				String msg = Logging
						.getMessage("generic.ExceptionInvokingMessageListener");
				Logging.logger().log(java.util.logging.Level.SEVERE, msg, e);
				// continue to next renderable
			}
		}
	}
	
	public Collection<Renderable>  getCollectionRenderable()
	{
		return renderables;
	}

}
