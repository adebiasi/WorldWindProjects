/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.worldwind;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;

import it.graphitech.modules.MiddleNodeGeneration;
import it.graphitech.objects.Node;
import it.graphitech.render.PartialCappedCylinder;
import it.graphitech.render.RenderableNode;

import java.awt.*;

/**
 * @author Patrick Murris
 * @version $Id: BasicDragger.java 649 2012-06-18 17:47:49Z tgaskins $
 */
public class NodeDragger implements SelectListener
{
    private final WorldWindow wwd;
    private boolean dragging = false;
    private boolean useTerrain = true;

    private Point dragRefCursorPoint;
    private Vec4 dragRefObjectPoint;
    private double dragRefAltitude;

    public NodeDragger(WorldWindow wwd)
    {
        if (wwd == null)
        {
            String msg = ("nullValue.WorldWindow");
            
            throw new IllegalArgumentException(msg);
        }

        this.wwd = wwd;
    }

    public NodeDragger(WorldWindow wwd, boolean useTerrain)
    {
        if (wwd == null)
        {
            String msg = ("nullValue.WorldWindow");
            
            throw new IllegalArgumentException(msg);
        }

        this.wwd = wwd;
        this.setUseTerrain(useTerrain);
    }

    public boolean isUseTerrain()
    {
        return useTerrain;
    }

    public void setUseTerrain(boolean useTerrain)
    {
        this.useTerrain = useTerrain;
    }

    public boolean isDragging()
    {
        return this.dragging;
    }

    public void selected(SelectEvent event)
    {
        if (event == null)
        {
            String msg = ("nullValue.EventIsNull");
            
            throw new IllegalArgumentException(msg);
        }

        if (event.getEventAction().equals(SelectEvent.DRAG_END))
        {
        	System.out.println("dragging end");
            this.dragging = false;
            
            
            Object topObject = event.getTopObject();
            if(topObject.getClass().equals(RenderableNode.class)){
            RenderableNode renderableNode = (RenderableNode)topObject;
            gov.nasa.worldwind.geom.Position rendPosition = renderableNode.getReferencePosition();
            String id = (renderableNode).getIdNode();
            
            //Node n = MiddleNodeGeneration.nodes.get(id);
            Node n = MiddleNodeGeneration.getNodeFromId(id);
            it.graphitech.objects.Position xy = new it.graphitech.objects.Position(rendPosition.longitude.degrees, rendPosition.latitude.degrees);
            n.setPosition(xy);
           // n.setBlocked(true);
            event.consume();
            }
           
        }
        else if (event.getEventAction().equals(SelectEvent.DRAG))
        {
            DragSelectEvent dragEvent = (DragSelectEvent) event;
            Object topObject = dragEvent.getTopObject();
            if (topObject == null)
                return;

            if (!(topObject instanceof Movable))
                return;

            if (!(topObject instanceof RenderableNode))
                return;
            
            System.out.println("provo il drag: "+topObject.getClass());
            
            if(
            		topObject.getClass().equals(RenderableNode.class)
            		
            		){
            
            	 System.out.println("ok draggo il nodo");
            Movable dragObject = (Movable) topObject;
            View view = wwd.getView();
            Globe globe = wwd.getModel().getGlobe();

            // Compute dragged object ref-point in model coordinates.
            // Use the Icon and Annotation logic of elevation as offset above ground when below max elevation.
            Position refPos = dragObject.getReferencePosition();
            if (refPos == null)
                return;

            Vec4 refPoint = globe.computePointFromPosition(refPos);

            if (!this.isDragging())   // Dragging started
            {
                // Save initial reference points for object and cursor in screen coordinates
                // Note: y is inverted for the object point.
                this.dragRefObjectPoint = view.project(refPoint);
                // Save cursor position
                this.dragRefCursorPoint = dragEvent.getPreviousPickPoint();
                // Save start altitude
                this.dragRefAltitude = globe.computePositionFromPoint(refPoint).getElevation();
            }

            // Compute screen-coord delta since drag started.
            int dx = dragEvent.getPickPoint().x - this.dragRefCursorPoint.x;
            int dy = dragEvent.getPickPoint().y - this.dragRefCursorPoint.y;

            // Find intersection of screen coord (refObjectPoint + delta) with globe.
            double x = this.dragRefObjectPoint.x + dx;
            double y = event.getMouseEvent().getComponent().getSize().height - this.dragRefObjectPoint.y + dy - 1;
            Line ray = view.computeRayFromScreenPoint(x, y);
            Position pickPos = null;
            // Use intersection with sphere at reference altitude.
            Intersection inters[] = globe.intersect(ray, this.dragRefAltitude);
            if (inters != null)
                pickPos = globe.computePositionFromPoint(inters[0].getIntersectionPoint());

            if (pickPos != null)
            {
                // Intersection with globe. Move reference point to the intersection point,
                // but maintain current altitude.
                Position p = new Position(pickPos, dragObject.getReferencePosition().getElevation());
                dragObject.moveTo(p);
            }
            this.dragging = true;
            event.consume();
            }
        }
    }
}