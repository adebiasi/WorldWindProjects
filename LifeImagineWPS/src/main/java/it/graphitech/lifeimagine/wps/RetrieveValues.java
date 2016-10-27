package it.graphitech.lifeimagine.wps;

import it.graphitech.lifeimagine.wps.util.ProcessUtil;

import java.util.ArrayList;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;


	@SuppressWarnings("deprecation")
	@DescribeProcess(title = "RetrieveValues", description = "The process lists the values attributes of the features of a feature collection.")
	public class RetrieveValues implements GSProcess {

		
		@DescribeResult(name = "result", description = "Lists the values attributes of the features of a feature collection.")
		public String execute(
				@DescribeParameter(name = "feature collection", description = "Feature collection in input") SimpleFeatureCollection featureCollection,
				@DescribeParameter(name = "attribute", description = "Name of the attribute") String attribute
				) {
			ArrayList<String> list =  (ArrayList<String>) ProcessUtil.valueList(featureCollection, attribute);
			
			String res ="";
			
			for(String l : list){
				res = res.concat(l+";");
			}
			
			return res;
		}
	}

