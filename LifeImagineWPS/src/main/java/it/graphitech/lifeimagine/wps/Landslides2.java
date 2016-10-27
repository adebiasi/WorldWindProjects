package it.graphitech.lifeimagine.wps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;

/**
 * WPS of the second landslide scenario.
 * The process calculates the route from the start point to the end one.
 * The path returned by the route services avoids the landslide areas.
 * 
 * @startpoint		Geometry of the starting point of the route.
 * @endpoint		Geometry of the ending point of the route.
 * @option			Additional option to request a particular type of routing.
 * 
 * @return Routing path for different modality of transportation.
 * 
 * A subprocess is executed as explained in the following procedure:
 * - setting every necessary input
 * - execute the process and save the output
 * 
 */

@SuppressWarnings("deprecation")
@DescribeProcess(title = "Landslides2", description = "The process calculates the route from the start point to the end one. The path returned by the route services avoids the landslide areas")
public class Landslides2 implements GSProcess {
	
	@DescribeResult(name = "result", description = "Routing path for different modality of transportation")
	public String execute(
			@DescribeParameter(name = "startpoint", description = "Geometry of the starting point of the route") String startPoint,
			@DescribeParameter(name = "endpoint", description = "Geometry of the ending point of the route") String endPoint,
			@DescribeParameter(name = "option", description = "Additional option to request a particular type of routing") String routeOption) {
		
		String url = "http://lifeimagine.graphitech-projects.com//RoutingWebApplication/route?from=" + startPoint + "&to=" + endPoint;
		if (routeOption.equals("avoid")) {
			url += "&avoid=1";
		}
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
	
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			
			return response.toString();
		}
		catch(Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}
}