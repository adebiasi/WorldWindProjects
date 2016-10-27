/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package it.graphitech.layer;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.mercator.BasicMercatorTiledImageLayer;
import gov.nasa.worldwind.layers.mercator.MercatorSector;

import gov.nasa.worldwind.util.*;

import java.net.*;

public class OSMMapnikLayer extends BasicMercatorTiledImageLayer
{
    public OSMMapnikLayer()
    {
        super(makeLevels());
    }

    private static LevelSet makeLevels()
    {
        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params.setValue(AVKey.DATA_CACHE_NAME, "Earth/OSM-Mercator/OpenStreetMap Mapnik");
        //params.setValue(AVKey.SERVICE, "http://a.tile.openstreetmap.org/");
        params.setValue(AVKey.SERVICE, "http://c.tile.stamen.com/");
        //params.setValue(AVKey.DATASET_NAME, "h");
        params.setValue(AVKey.DATASET_NAME, "watercolor");
        params.setValue(AVKey.FORMAT_SUFFIX, ".png");
        params.setValue(AVKey.NUM_LEVELS, 16);
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
       
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle
            .fromDegrees(22.5d), Angle.fromDegrees(45d)));
        params.setValue(AVKey.SECTOR, new MercatorSector(-1.0, 1.0, Angle.NEG180, Angle.POS180));
        
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder());

        return new LevelSet(params);
    }

    private static class URLBuilder implements TileUrlBuilder
    {
        public URL getURL(Tile tile, String imageFormat)
            throws MalformedURLException
        {
        	/*
        	System.out.println("http://c.tile.stamen.com/watercolor/"
                + (tile.getLevelNumber() + 3) + "/" + tile.getColumn() + "/"
                + ((1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow()) + ".png");
        	*/
        	/*
        	return new URL("http://c.tile.stamen.com/watercolor/"
                    + 5 + "/" + tile.getColumn() + "/"
                    + ((1 << (tile.getLevelNumber()) + 1) - 1 - tile.getRow()) + ".png");
        	*/
        	
        	return new URL("http://c.tile.stamen.com/watercolor/"
					+ (tile.getLevelNumber() + 3) +"/"+ tile.getColumn()+"/"
        			+ ((1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow()) + ".png");
        	/*
        	return new URL("http://c.tile.stamen.com/watercolor/"
                    + (tile.getLevelNumber() + 3) + "/" + tile.getColumn() + "/"
                    + ((1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow()) + ".png");
                    */
                
        }
    }

    @Override
    public String toString()
    {
        return "OpenStreetMap";
    }
}
