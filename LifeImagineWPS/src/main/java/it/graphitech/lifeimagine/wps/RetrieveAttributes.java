package it.graphitech.lifeimagine.wps;

import java.awt.List;
import java.util.ArrayList;

import it.graphitech.lifeimagine.wps.util.ProcessUtil;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;

@SuppressWarnings("deprecation")
@DescribeProcess(title = "RetrieveAttributes", description = "The process lists the attributes of the features of a feature collection.")
public class RetrieveAttributes implements GSProcess {

	
	@DescribeResult(name = "result", description = "Lists the attributes of the features of a feature collection.")
	public String execute(
			@DescribeParameter(name = "feature collection", description = "Feature collection in input") SimpleFeatureCollection featureCollection
			) {
		ArrayList<String> list =  (ArrayList<String>) ProcessUtil.attributesList(featureCollection);
		
		String res ="";
		
		for(String l : list){
			res = res.concat(l+";");
		}
		
		return res;
	}
}
