/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package it.graphitech.trafficSimulator.renderable.kml;

import gov.nasa.worldwind.render.DrawContext;

/**
 * Interface for rendering KML elements.
 *
 * @author tag
 * @version $Id: KMLRenderable.java 1 2011-07-16 23:22:47Z dcollins $
 */
public interface KMLRenderable
{
    /**
     * Pre-render this element.
     *
     * @param tc the current KML traversal context.
     * @param dc the current draw context.
     *
     * @throws IllegalArgumentException if either the traversal context or the draw context is null.
     */
    void preRender(KMLTraversalContext tc, DrawContext dc);

    /**
     * Render this element.
     *
     * @param tc the current KML traversal context.
     * @param dc the current draw context.
     *
     * @throws IllegalArgumentException if either the traversal context or the draw context is null.
     */
    void render(KMLTraversalContext tc, DrawContext dc);
}
