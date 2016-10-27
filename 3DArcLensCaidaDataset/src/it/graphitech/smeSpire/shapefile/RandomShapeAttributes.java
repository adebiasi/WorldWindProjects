/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package it.graphitech.smeSpire.shapefile;

import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.WWUtil;

import java.awt.*;

/**
 * @author dcollins
 * @version $Id: RandomShapeAttributes.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class RandomShapeAttributes
{
    protected int attrIndex = 0;
    protected PointPlacemarkAttributes[] pointAttrs;
    protected ShapeAttributes[] polylineAttrs;
    protected ShapeAttributes[] polygonAttrs;

    public RandomShapeAttributes()
    {
        this.initialize();
    }

    protected void initialize()
    {
        this.pointAttrs = new PointPlacemarkAttributes[]
            {
                this.createPointAttributes(new Color(0.7f, 0.73f, 0f, 1f)),
            };

        this.polylineAttrs = new ShapeAttributes[]
            {
                this.createPolylineAttributes(new Color(0.7f, 0.73f, 0f, 1f)),
            };

        this.polygonAttrs = new ShapeAttributes[]
            {
                this.createPolygonAttributes(new Color(0.7f, 0.73f, 0f, 1f)),
            };
    }

    public PointPlacemarkAttributes nextPointAttributes()
    {
        return this.pointAttrs[this.attrIndex++ % this.pointAttrs.length];
    }

    public ShapeAttributes nextPolylineAttributes()
    {
        return this.polylineAttrs[this.attrIndex++ % this.polylineAttrs.length];
    }

    public ShapeAttributes nextPolygonAttributes()
    {
        return this.polygonAttrs[this.attrIndex++ % this.polygonAttrs.length];
    }

    protected PointPlacemarkAttributes createPointAttributes(Color color)
    {
        PointPlacemarkAttributes attrs = new PointPlacemarkAttributes();
        attrs.setUsePointAsDefaultImage(true);
        attrs.setLineMaterial(new Material(color));
        attrs.setScale(7d);
        return attrs;
    }

    protected ShapeAttributes createPolylineAttributes(Color color)
    {
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setOutlineMaterial(new Material(color));
        attrs.setOutlineWidth(1.5);
        return attrs;
    }

    protected ShapeAttributes createPolygonAttributes(Color color)
    {
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorMaterial(new Material(color));
        attrs.setOutlineMaterial(new Material(WWUtil.makeColorBrighter(color)));
        attrs.setInteriorOpacity(0.2);
        attrs.setOutlineWidth(1);
        attrs.setOutlineOpacity(1);
        return attrs;
    }
}
