package it.graphitech.trafficSimulator.ServiceInterface;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import it.graphitech.trafficSimulator.CustomizableVariables;
import it.graphitech.trafficSimulator.GlobalInstances;
import it.graphitech.trafficSimulator.core.EmitterManager;
import it.graphitech.trafficSimulator.renderable.PathExtArea;
import it.graphitech.trafficSimulator.renderable.emitter.EmitterDestAreas;
import it.graphitech.trafficSimulator.renderableManager.GraphManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * This class manages the requests for the emitters' information
 * 
 * @author a.debiasi
 * 
 */

public class DataLayer {

	/**
	 * generate a getFeature request to a wfs service
	 * 
	 * @param positions
	 * @param emitterManager
	 * @param streetGraph
	 * @param paths
	 */
	public static void sendEmitterRequestPost(ArrayList<Position> positions,
			EmitterManager emitterManager, GraphManager streetGraph,
			Set<PathExtArea> paths) {

		String posString = "";

		for (Position pos : positions) {

			double lat = pos.latitude.degrees;
			double lon = pos.longitude.degrees;

			posString = posString.concat(lon + "," + lat + " ");

		}
		Position p = positions.get(0);
		double lat = p.latitude.degrees;
		double lon = p.longitude.degrees;

		posString = posString.concat(lon + "," + lat + " ");

		try {
			String data = "<wfs:GetFeature service=\"WFS\" version=\"1.0.0\" "
					+ "outputFormat=\"GML2\" "
					+ "xmlns:topp=\"http://www.openplans.org/topp\" "
					+ "xmlns:wfs=\"http://www.opengis.net/wfs\" "
					+ "xmlns:ogc=\"http://www.opengis.net/ogc\" "
					+ "xmlns:gml=\"http://www.opengis.net/gml\" "
					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xsi:schemaLocation=\"http://www.opengis.net/wfs "
					+ "                   http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\">"
					+ "<wfs:Query typeName=\"" + CustomizableVariables.typeName
					+ "\">" +

					" <ogc:Filter>" +

					"<Intersects>" + "<PropertyName>" + "the_geom"
					+ "	</PropertyName>" + "<gml:Polygon>"
					+ "<gml:outerBoundaryIs>" + "<gml:LinearRing>"
					+ "<gml:coordinates>" + posString + "</gml:coordinates>"
					+ "</gml:LinearRing>" + "</gml:outerBoundaryIs>"
					+ "</gml:Polygon>" + "</Intersects>" +

					"</ogc:Filter>" +

					"</wfs:Query>" + "</wfs:GetFeature>";



			// Send data
			URL url = new URL(CustomizableVariables.wfsServer + "?"
					+ "request=GetFeature");
			System.out.println(url);
			URLConnection conn = url.openConnection();

			conn.setRequestProperty("Content-Type", "application/xml");
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			// Get the response
System.out.println(data);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();

			String soft = "";
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				// Process line...
				
				soft = soft.concat(line);
			}
			wr.close();
			rd.close();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			System.out.println(soft);
			Document doc = db.parse(new InputSource(new StringReader(soft)));

			addEmitters(doc, emitterManager, streetGraph, paths);

		} catch (Exception io) {
			System.out.println(io.getMessage());
		}

	}

	public static void addEmitters(Document doc, EmitterManager emitterManager,
			GraphManager streetGraph, Set<PathExtArea> paths) {

		try {

			Element rootNode = doc.getDocumentElement();
			NodeList list = rootNode.getChildNodes();

			for (int i = 1; i < list.getLength(); i++) {

				Element node = (Element) list.item(i);

				NodeList sublist = ((Element) node.getChildNodes().item(0))
						.getChildNodes();

				Position currPos = null;
				 int numPers=0;

				for (int j = 0; j < sublist.getLength(); j++) {
					String name = sublist.item(j).getNodeName();

					if (name.compareTo("cite:POP_P") == 0) {

						 String val = sublist.item(j).getTextContent();
						 numPers=Integer.valueOf(val);

					}
					if (name.compareTo("cite:the_geom") == 0) {

						String val = sublist.item(j).getTextContent();

						String[] latlon = val.split(",");
						currPos = Position.fromDegrees(
								Double.parseDouble(latlon[1]),
								Double.parseDouble(latlon[0]));
					}

				}

				addEmitterAreasDest(currPos, 5, emitterManager, paths,
						streetGraph);
				
//				addEmitterAreasDest(currPos, numPers, emitterManager, paths,
//						streetGraph);

			}

		} catch (Exception io) {
			System.out.println(io.getMessage());
		}
	}

	private static void addEmitterAreasDest(Position pos, int numPers,
			EmitterManager emitterManager, Set<PathExtArea> paths,
			GraphManager streetGraph) {
		ResultPath res = getNearestPositionPairFromPaths(paths, pos);

		EmitterDestAreas emitterDest = new EmitterDestAreas(pos,
				res.getAvailDest(), numPers);

		emitterDest.setOneway(res.getCurrPath().isOneway());

		emitterManager.addEmitters(emitterDest);

	}

	private static ResultPath getNearestPositionPairFromPaths(
			Set<PathExtArea> paths, Position pos) {
		Vec4 emitterPoint = GlobalInstances.getGlobe()
				.computePointFromPosition(pos);
		Position[] availDest = new Position[2];
		;
		PathExtArea nearestPath = null;

		Iterator<PathExtArea> it = paths.iterator();

		double distance = 999999999;

		while (it.hasNext()) {
			int smallerPos = 0;
			PathExtArea currPath = it.next();

			ArrayList<? extends Position> positions = (ArrayList<? extends Position>) currPath
					.getPositions();
			int nPos = positions.size();
			int nSegments = 20; // For better precision this should be higher
								// for longer segments

			for (int i = 0; i < nPos - 1; ++i) {
				Position pos1 = positions.get(i);
				Position pos2 = positions.get(i + 1);

				double longDiff = pos2.getLongitude().degrees
						- pos1.getLongitude().degrees;
				double latDiff = pos2.getLatitude().degrees
						- pos1.getLatitude().degrees;
				double longInc = longDiff / nSegments;
				double latInc = latDiff / nSegments;

				for (int j = 0; j < nSegments; ++j) {
					Position newPosition = Position.fromDegrees(
							pos1.getLatitude().degrees + latInc * nSegments,
							pos1.getLongitude().degrees + longInc * nSegments);
					Vec4 newPosPoint = GlobalInstances.getGlobe()
							.computePointFromPosition(newPosition);

					double newDistance = newPosPoint.distanceTo3(emitterPoint);

					if (newDistance < distance) {

						distance = newDistance;
						smallerPos = i;
						nearestPath = currPath;
						availDest = new Position[2];
						availDest[0] = positions.get(smallerPos);
						availDest[1] = positions.get(smallerPos + 1);
					}
				}
			}

		}
		ResultPath res = new ResultPath();

		res.setAvailDest(availDest);
		res.setCurrPath(nearestPath);

		return res;
	}

	/**
	 * Main method for the testing of the request
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		ArrayList<Position> pos = new ArrayList<Position>();

		Position p1 = Position.fromDegrees(46.0605, 11.1295);
		Position p2 = Position.fromDegrees(46.0605, 11.130);
		Position p3 = Position.fromDegrees(46.061, 11.130);
		Position p4 = Position.fromDegrees(46.061, 11.1295);

		pos.add(p4);
		pos.add(p3);
		pos.add(p2);
		pos.add(p1);

		sendEmitterRequestPost(pos, null, null, null);

	}
}
