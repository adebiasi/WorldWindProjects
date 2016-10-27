package it.graphitech.lifeimagine.wps.otherClasses;




import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.List;
import java.util.MissingResourceException;

import javax.media.jai.RenderedOp;

import org.geotools.process.raster.CoverageUtilities;
import org.geotools.coverage.Category;
import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.factory.GeoTools;
import org.geotools.image.ImageWorker;
import org.geotools.renderer.i18n.Errors;
import org.geotools.resources.i18n.ErrorKeys;
import org.geotools.resources.image.ColorUtilities;
import org.jaitools.numeric.Range;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.util.ProgressListener;

/**
 * A raster reclassified process
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 * @author Emanuele Tajariol (GeoSolutions)
 * @author Simone Giannecchini (GeoSolutions)
 * @author Andrea Aime - GeoSolutions
 * @author Daniele Romagnoli - GeoSolutions
 *
 * @source $URL$
 */
public class RangeLookup  {
	/*
    private final static double DEFAULT_NODATA = 0d;

    
    
    public RangeLookup() {
		System.out.println("istantiate rangeLookup");
	}


	public  GridCoverage2D execute(
             GridCoverage2D coverage,
            Integer classificationBand,
            List<Range> classificationRanges,
           int[] outputPixelValues,
            Double noData,
            ProgressListener listener) throws MissingResourceException, Exception {
    	
    	
    	System.out.println("inside RangeLookup.execute");
    	
    	//
    	// initial checks
    	//
    	if(coverage==null){
    		throw new Exception(Errors.format(ErrorKeys.NULL_ARGUMENT_$1,"coverage"));
    	}
    	if(classificationRanges==null){
    		throw new Exception(Errors.format(ErrorKeys.NULL_ARGUMENT_$1,"classificationRanges"));
    	}
    	double nd = DEFAULT_NODATA;
    	//NoDataContainer noDataProperty = org.geotools.resources.coverage.CoverageUtilities.getNoDataProperty(coverage);
        if (noData != null) {
            nd = noData.doubleValue();
        } else if (noDataProperty != null) {
            nd = noDataProperty.getAsSingleValue();
        }
    	
    	if (outputPixelValues != null && outputPixelValues.length > 0){
    	    final int ranges = classificationRanges.size();
    	    if (ranges != outputPixelValues.length){
    	        throw new Exception(Errors.format(ErrorKeys.MISMATCHED_ARRAY_LENGTH, "outputPixelValues"));
    	    }
    	}

        RenderedImage sourceImage = coverage.getRenderedImage();
    	
        ImageWorker worker = new ImageWorker(sourceImage);
        
        // parse the band
        if (classificationBand != null) {
            final int band = classificationBand;
            final int numbands=sourceImage.getSampleModel().getNumBands();
            if(band<0 || numbands<=band){
            	throw new Exception(Errors.format(ErrorKeys.ILLEGAL_ARGUMENT_$2,"band",band));
            }
            
            if(band==0 && numbands>0 || band>0){
                worker.retainBands(new int []{band});
            }
        }


        //
        // Check the number of ranges we have in order to decide which type we can use for the output values. 
        // Our goal is to use the smallest possible data type that can hold the image values.
        //
        Object lookupTable;
        final int size=classificationRanges.size();
        int transferType = ColorUtilities.getTransferType(size);
        if(JAIExt.isJAIExtOperation("RLookup")){
            lookupTable = CoverageUtilities.getRangeLookupTableJAIEXT(classificationRanges, outputPixelValues, nd, transferType);
        }else{
            // Builds the range lookup table
            //final RangeLookupTable lookupTable;

            switch (transferType) {
                    case DataBuffer.TYPE_BYTE:
                            lookupTable = CoverageUtilities.getRangeLookupTable(classificationRanges, outputPixelValues, (byte) nd );
                            break;
                    case DataBuffer.TYPE_USHORT:
                            lookupTable = CoverageUtilities.getRangeLookupTable(classificationRanges, outputPixelValues, (short) nd );
                            break;
                    case DataBuffer.TYPE_INT:
                            lookupTable = CoverageUtilities.getRangeLookupTable(classificationRanges, outputPixelValues, nd );
                            break;                  
                    default:
                            throw new IllegalArgumentException(org.geotools.resources.i18n.Errors.format(ErrorKeys.ILLEGAL_ARGUMENT_$2,
                                    "classification ranges size",size));
                    }
            
        }
        worker.setROI(org.geotools.resources.coverage.CoverageUtilities.getROIProperty(coverage));
        worker.setBackground(new double[]{nd});
        final RenderedOp indexedClassification = worker.rangeLookup(lookupTable)
                .getRenderedOperation();

        
        //
        // build the output coverage
        //
        
        
        // build the output sample dimensions, use the default value ( 0 ) as the no data
        final GridSampleDimension outSampleDimension = new GridSampleDimension("classification",
                new Category[] { Category.NODATA }, null);
        final GridCoverageFactory factory = CoverageFactoryFinder.getGridCoverageFactory(GeoTools.getDefaultHints());
        HashMap<String,Object> properties = new HashMap<String,Object>(){{
        	put(NoDataContainer.GC_NODATA,new NoDataContainer(0d));
        }};
        org.geotools.resources.coverage.CoverageUtilities.setROIProperty(properties, worker.getROI());
        final GridCoverage2D output = factory.create("reclassified", indexedClassification, coverage
                .getGridGeometry(), new GridSampleDimension[] { outSampleDimension },
                new GridCoverage[] { coverage }, properties);
        return output;
    }
 
    
  
    public GridCoverage2D execute(GridCoverage2D coverage, Integer classificationBand,
            List<Range> classificationRanges, ProgressListener listener) throws MissingResourceException, Exception {
    	System.out.println("execute RangeLookup");
    	GridCoverage2D res = execute(coverage, classificationBand, classificationRanges, null, 0d, listener);
    	
        return res;
    }
   */
}
