package it.graphitech.modules;


import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceCircle;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.render.airspaces.Orbit;
import gov.nasa.worldwind.util.WWUtil;
import it.graphitech.Operations;
import it.graphitech.Variables;
import it.graphitech.colors.ConvertColors;
import it.graphitech.objects.Node;
import it.graphitech.objects.Position;
import it.graphitech.render.PartialCappedCylinder;
import it.graphitech.render.RenderableNode;
import it.graphitech.render.cubicCurve.CubicSplinePolyline;
import it.graphitech.render.cubicCurve.RenderableControlPoints;

import java.awt.Color;
import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;


public class Render {

	static public  WorldWindowGLCanvas wwd = null; // World Wind Canvas
	static RenderableLayer nodesLayer = new RenderableLayer();
	static RenderableLayer intermediateNodesLayer = new RenderableLayer();
	static RenderableLayer edgesLayer = new RenderableLayer();
	static RenderableLayer rejectLayer = new RenderableLayer();
	static RenderableLayer distanceLayer = new RenderableLayer();
	static RenderableLayer samePosLayer = new RenderableLayer();
	static RenderableLayer curveLayer = new RenderableLayer();
//	static ArrayList<CubicSplinePolyline> listCurves = new ArrayList<CubicSplinePolyline>();
	
	static  AirspaceLayer edgesAirspaces = new AirspaceLayer();
	static  AirspaceLayer nodesAirspaces = new AirspaceLayer();
	static AirspaceLayer intermediateNodesAirspaces = new AirspaceLayer();

	private static void initNodesLayer(){
		nodesLayer.removeAllRenderables();
		nodesAirspaces.removeAllAirspaces();
		//listEdges = new HashMap<String,Polyline>();
	}
	private static void initEdgesLayer(){
		edgesLayer.removeAllRenderables();
		edgesAirspaces.removeAllAirspaces();
		//listEdges = new HashMap<String,Polyline>();
	}
	private static void initCurvesLayer(){
		curveLayer.removeAllRenderables();
		
		//listEdges = new HashMap<String,Polyline>();
	}
	private static void initRejectLayer(){
		rejectLayer.removeAllRenderables();
		//listEdges = new HashMap<String,Polyline>();
	}
	private static void initDistanceLayer(){
		distanceLayer.removeAllRenderables();
		//listEdges = new HashMap<String,Polyline>();
	}
	private static void initSamePosLayer(){
		samePosLayer.removeAllRenderables();
		//listEdges = new HashMap<String,Polyline>();
	}
	private static void initMiddleNodeLayer(){
		intermediateNodesLayer.removeAllRenderables();
		intermediateNodesAirspaces.removeAllAirspaces();
		
	}
	public static void createLayers(){
		
	
		
		
		wwd.getModel().getLayers().add(edgesAirspaces);
		edgesAirspaces.setName("edgesAirspaces");
		edgesAirspaces.setEnableLighting(false);
		
		wwd.getModel().getLayers().add(nodesAirspaces);
		nodesAirspaces.setName("nodesAirspaces");
		nodesAirspaces.setEnableLighting(false);
		nodesAirspaces.setPickEnabled(true);
		
		wwd.getModel().getLayers().add(intermediateNodesAirspaces);
		intermediateNodesAirspaces.setName("intermediateNodesAirspaces");
		intermediateNodesAirspaces.setEnableLighting(false);
		
		wwd.getModel().getLayers().add(edgesLayer);
		edgesLayer.setName("Edges");
		edgesLayer.setPickEnabled(true);
		
		
		
		wwd.getModel().getLayers().add(intermediateNodesLayer);
		intermediateNodesLayer.setName("IntermediateNodes");
		
		wwd.getModel().getLayers().add(rejectLayer);
		rejectLayer.setName("RejectArea");
		rejectLayer.setPickEnabled(false);
		rejectLayer.setEnabled(false);
		
		
		wwd.getModel().getLayers().add(distanceLayer);
		distanceLayer.setName("DistanceArea");
		distanceLayer.setPickEnabled(false);
		distanceLayer.setEnabled(false);
		
		
		wwd.getModel().getLayers().add(samePosLayer);
		samePosLayer.setName("SamePosArea");
		samePosLayer.setPickEnabled(false);
		samePosLayer.setEnabled(false);
		
		wwd.getModel().getLayers().add(nodesLayer);
		nodesLayer.setName("Nodes");
		

		wwd.getModel().getLayers().add(curveLayer);
		curveLayer.setName("Curves");
curveLayer.setEnabled(true);
	}
	

	public static void enableEdgesLayer(boolean en){
		edgesLayer.setEnabled(en);	
		edgesAirspaces.setEnabled(en);
	}
	public static void enableRejectsLayer(boolean en){
		rejectLayer.setEnabled(en);	
	}
	public static void enableDistanceLayer(boolean en){
		distanceLayer.setEnabled(en);	
	}
	public static void enableIntermediateNodesLayer(boolean en){
		intermediateNodesLayer.setEnabled(en);	
		intermediateNodesAirspaces.setEnabled(en);
	}
	public static void enableNodesLayer(boolean en){
		nodesLayer.setEnabled(en);	
	}
	
	public static void enableCurvesLayer(){
		curveLayer.setEnabled(!curveLayer.isEnabled());	
		
	}
	public static void enableEdgesLayer(){
		edgesLayer.setEnabled(!edgesLayer.isEnabled());	
		edgesAirspaces.setEnabled(!edgesAirspaces.isEnabled());
	}
	public static void enableRejectsLayer(){
		rejectLayer.setEnabled(!rejectLayer.isEnabled());	
	}
	public static void enableDistancesLayer(){
		distanceLayer.setEnabled(!distanceLayer.isEnabled());	
	}
	public static void enableSamePosLayer(){
		samePosLayer.setEnabled(!samePosLayer.isEnabled());	
	}
	public static void enableIntermediateNodesLayer(){
		intermediateNodesLayer.setEnabled(!intermediateNodesLayer.isEnabled());	
		intermediateNodesAirspaces.setEnabled(!intermediateNodesAirspaces.isEnabled());
	}
	public static void enableNodesLayer(){
		nodesLayer.setEnabled(!nodesLayer.isEnabled());	
		nodesAirspaces.setEnabled(!nodesAirspaces.isEnabled());
	}
	
	private static void resetLayers(){
		
		//intermediateNodesLayer.removeAllRenderables();
		initNodesLayer();
		//flowsLayer.removeAllRenderables();		
		initEdgesLayer();
		initMiddleNodeLayer();
		initRejectLayer();
		initDistanceLayer();
		initCurvesLayer();
		initSamePosLayer();
	}
	
	
	
	private static void drawNodesAndEdges( Collection<Node> nodes) {

			System.out.println("drawNodesAndEdges");
		//for(Entry<String,Node> e : MiddleNodeGeneration.nodes.entrySet()){
		for(Node currNode : nodes){
			//Node currNode=e.getValue();
			
			if(Variables.enable_render_edges){
				drawEdge(currNode);				
			}		
			
			drawNode( currNode);
			if(currNode.isRoot()){
				//do nothing
			}else
			if(!currNode.isMiddleNode()){
				drawRejectArea(currNode);
			}else{
				drawSamePosArea(currNode);
				drawDistanceArea(currNode);
			}
			//drawForces(g, currNode);			
		}
		
		createCurves(nodes);
		
	}

	private static void createCurves( Collection<Node> nodes) {

		System.out.println("CREO LE CURVE!!!!");
	//	listCurves = new ArrayList<CubicSplinePolyline>();
		
		ArrayList<RenderableControlPoints> renderableControlPointsArray = new ArrayList<>();
		
		//create renderableControlPointsArray
		//for(Entry<String,Node> e : MiddleNodeGeneration.nodes.entrySet()){
		for(Node currNode : nodes){
			//Node currNode=e.getValue();
			if(currNode.isLeaf()){
				RenderableControlPoints renderableControlPoints = createCurve(currNode);
				renderableControlPointsArray.add(renderableControlPoints);
			}
			
		}
		
		
		
		//order renderableControlPointsArray based on the magnitude
		Collections.sort(renderableControlPointsArray, new Comparator<RenderableControlPoints>(){
			  public int compare(RenderableControlPoints s1, RenderableControlPoints s2) {
			    return (int)s2.getMagnitude()-(int)s1.getMagnitude();
				//  return (int)s1.getMagnitude()-(int)s2.getMagnitude();
			  }
			});
		
		
		//set the nodes to areRendered and render them
		for(RenderableControlPoints controlsPoints : renderableControlPointsArray){
			
			//printMagnitudes(controlsPoints);
			checkRenderableCurves(controlsPoints);
//		}		
		
		/*
		Collections.sort(renderableControlPointsArray, new Comparator<RenderableControlPoints>(){
			  public int compare(RenderableControlPoints s1, RenderableControlPoints s2) {
			   // return (int)s2.getMagnitude()-(int)s1.getMagnitude();
				  return (int)s1.getMagnitude()-(int)s2.getMagnitude();
			  }
			});
		*/
	//	for(RenderableControlPoints controlsPoints : renderableControlPointsArray){
			//Node leaf = MiddleNodeGeneration.nodes.get(controlsPoints.getLeafId());
			//Node leaf = MiddleNodeGeneration.getNodeFromId(controlsPoints.getLeafId());
			//Color col = Variables.listColors[leaf.getNumRoot()];
			//System.out.println("magnitude: "+controlsPoints.getMagnitude());
			//drawCurve(controlsPoints, col);
			drawCurve(controlsPoints);
		}
		
		//printCurves();
		
		
		//renderCurves();
		resetRenderAttribute();
		
	}
/*
	private static void renderCurves(){
		curveLayer.removeAllRenderables();
		for(Renderable rend  :listCurves ){
			curveLayer.addRenderable(rend);
		}
	}
	*/
	private static void printMagnitudes(RenderableControlPoints contrPoints){
		
		System.out.println("contr point");
		/*
		for(double magn: contrPoints.magnitudes){
			
			System.out.println("magn: "+magn);
			
		}
		*/
		System.out.println("contr point di magn: "+contrPoints.getMagnitude());
		
	}
	
	
	private static void resetRenderAttribute(){
		
//for(Entry<String,Node> e : MiddleNodeGeneration.nodes.entrySet()){
		for(Node currNode: MiddleNodeGeneration.getNodeValues()){
		//	Node currNode=e.getValue();
currNode.setToRender(false);			
}
	}
	
	private static void checkRenderableCurves(RenderableControlPoints controlsPoints){
		for(int i=0;i<controlsPoints.idNodes.size();i++){
			String id=controlsPoints.idNodes.get(i);
			//Node n = MiddleNodeGeneration.nodes.get(id);
			Node n = MiddleNodeGeneration.getNodeFromId(id);
			if(!n.isToRender()){
				controlsPoints.toRender.set(i,true);
				n.setToRender(true);
			}
			//System.out.println("control point elevation "+controlsPoints.positions.get(i).elevation);
			//System.out.println("toRender? "+controlsPoints.toRender.get(i));
			
		}
	}
	
private static RenderableControlPoints createCurve( Node leafNode) {
	
	
	//System.out.println("CREAO Punti intermedi");
	//ArrayList<gov.nasa.worldwind.geom.Position> positions = new ArrayList<>();
	RenderableControlPoints renderableControlPoints = new RenderableControlPoints();
	
	gov.nasa.worldwind.geom.Position p1 = gov.nasa.worldwind.geom.Position.fromDegrees(leafNode.getPosition().getY(), leafNode.getPosition().getX());
	//positions.add(p1);
	
	
	//double thickness = getValue(leafNode.getNodeMagnitude(),Operations.minFlowMagnitude,Operations.maxFlowMagnitude,Variables.minWidth, Variables.maxWidth);	
	double thickness = Variables.returnThickness(leafNode.getNodeMagnitude());

	System.out.println("in createCurve of "+leafNode.getName()+" - "+thickness);
	
	Color col = null;
	
	// if(leafNode.getNumRoot()>=1){
	 if(Variables.numOrigin>1){
		 System.out.println("1. leafNode.getNumRoot(): "+leafNode.getNumRoot());
		 col = Variables.listColors[leafNode.getNumRoot()];
	//renderableControlPoints.addControlPoint(p, pred.getNodeMagnitude(),pred.getId());
	 }else{
		 System.out.println("qua non deve andare");
		 col = returnColor(leafNode.getNodeMagnitude_R(), leafNode.getNodeMagnitude_G(), leafNode.getNodeMagnitude_B(),leafNode.getNodeMagnitude());
	 }
	
	//System.out.println("leaf m: "+leafNode.getNodeMagnitude()+" r: "+leafNode.getNodeMagnitude_R()+" g: "+leafNode.getNodeMagnitude_G()+" b: "+leafNode.getNodeMagnitude_B()+" col: "+col);
	
	String idPred =leafNode.getPrev_id_node(); 
	double predThickeness = getPrevThickness(leafNode.getId());	
	Node pred= MiddleNodeGeneration.getNodeFromId(idPred);
	Node predNode= MiddleNodeGeneration.getNodeFromId(pred.getPrev_id_node());
	
	
	double shift;

	if((predNode!=null)
		&&
		(pred.getPosition()!=null)
		&&
		(predNode.getPosition()!=null)
		){
	 shift = giveShift(predNode, pred, leafNode);
	
	}else  {
		shift = 0;
	}
	
	
	renderableControlPoints.addControlPoint(p1, thickness,leafNode.getId(),col,shift,predThickeness);
	
	
	
	//renderableControlPoints.addColor(col);
	//renderableControlPoints.addControlPoint(p1, leafNode.getNodeMagnitude(),leafNode.getId());
	
	//System.out.println("crate curve with num thickness: "+renderableControlPoints);
	
	
	//Node pred = MiddleNodeGeneration.nodes.get(idPred);
	Node nextNode = leafNode;
	//Node pred = MiddleNodeGeneration.getNodeFromId(idPred);
	
	
	while(!pred.isRoot()){
		
		if(
				(pred.isMiddleNode())
				//(!pred.isMiddleNode())
				//||
				//(pred.getNext_id_nodes().size()>1)
				||
				(pred.isRejected())
				){
		gov.nasa.worldwind.geom.Position p = gov.nasa.worldwind.geom.Position.fromDegrees(pred.getPosition().getY(), pred.getPosition().getX());
		//positions.add(p);
		// thickness = getValue(pred.getNodeMagnitude(),Operations.minFlowMagnitude,Operations.maxFlowMagnitude,Variables.minWidth, Variables.maxWidth);	
		 thickness = Variables.returnThickness(pred.getNodeMagnitude());
		 
		 Color col2;
		 
		 //System.out.println("pred.getNumRoot(): "+pred.getNumRoot());
		 
		 //if(pred.getNumRoot()>=1){
			 if(Variables.numOrigin>1){
			//	 System.out.println("2. leafNode.getNumRoot(): "+leafNode.getNumRoot());
			 col2 = Variables.listColors[leafNode.getNumRoot()];
		//renderableControlPoints.addControlPoint(p, pred.getNodeMagnitude(),pred.getId());
		 }else{
			 //System.out.println("neanche qua");
			 col2 = returnColor(pred.getNodeMagnitude_R(), pred.getNodeMagnitude_G(), pred.getNodeMagnitude_B(),pred.getNodeMagnitude());
		 }
		 predThickeness = getPrevThickness(pred.getId());
		  predNode= MiddleNodeGeneration.getNodeFromId(pred.getPrev_id_node());
			Node predPredNode= MiddleNodeGeneration.getNodeFromId(predNode.getPrev_id_node());
		 
			if((predPredNode!=null)
				&&
				(pred.getPosition()!=null)
				&&
				(predPredNode.getPosition()!=null)
				){
				
	//			System.out.println("pred node: "+predPredNode.getPosition());
				
		 shift = giveShift(predPredNode, predNode, pred);
			}else{
				shift=0;
			}
			
		renderableControlPoints.addControlPoint(p, thickness,pred.getId(),col2,shift,predThickeness);
		
	//	renderableControlPoints.addColor(col2);
		}
		
		nextNode= pred;
		pred=MiddleNodeGeneration.getNodeFromId(pred.getPrev_id_node());
	}
	
	gov.nasa.worldwind.geom.Position p = gov.nasa.worldwind.geom.Position.fromDegrees(pred.getPosition().getY(), pred.getPosition().getX());
	//positions.add(p);
	// thickness = getValue(pred.getNodeMagnitude(),Operations.minFlowMagnitude,Operations.maxFlowMagnitude,Variables.minWidth, Variables.maxWidth);	
	 thickness = Variables.returnThickness(pred.getNodeMagnitude());
	
	 Color col2;
	 //if(leafNode.getNumRoot()>=1){	
		 if(Variables.numOrigin>1){
			 col2 = Variables.listColors[leafNode.getNumRoot()];
		//renderableControlPoints.addControlPoint(p, pred.getNodeMagnitude(),pred.getId());
		 }else{
	  col2 = returnColor(pred.getNodeMagnitude_R(), pred.getNodeMagnitude_G(), pred.getNodeMagnitude_B(),pred.getNodeMagnitude());
	 }
	//renderableControlPoints.addControlPoint(p, pred.getNodeMagnitude(),pred.getId());
	 renderableControlPoints.addControlPoint(p, thickness,pred.getId(),col2,0,0);
	 
	//	renderableControlPoints.addColor(col2);
	
	/*
	Color col = Variables.listColors[leafNode.getNumRoot()];
	drawCurve(positions, col);
	*/
	return renderableControlPoints;
	}

private static double getPrevThickness(String id){
	Node node =  MiddleNodeGeneration.getNodeFromId(id);
	Node previousNode =  MiddleNodeGeneration.getNodeFromId(node.getPrev_id_node());
	 double predMagnitude =previousNode.getNodeMagnitude();
	 return Variables.returnThickness(predMagnitude);
}

private static double giveShift(Node predNode, Node node, Node nextNode){
	Set<String> brothers=node.getNext_id_nodes();
	
	Position p1 =node.getPosition();
	Position p2 =nextNode.getPosition();
	Position p3 =predNode.getPosition();
	
	//System.out.println(p1+" "+p2+" "+p3);
	
//	double currAngle2 = calculateAngle(node.getPosition(), nextNode.getPosition(),predNode.getPosition());
	double currAngle = angleBetween(p1,p2,p3);
	double shift=0;
	
	//System.out.println("id: "+nextNode.getId()+"angle node: "+currAngle+" angle2: "+currAngle2);
	
	for(String id : brothers ){
		if(id.compareTo(nextNode.getId())!=0){
			Node brother = MiddleNodeGeneration.getNodeFromId(id);
			//Node nextBrother =  MiddleNodeGeneration.getNodeFromId(brother.getN);
			//double brotherAngle = calculateAngle(predNode.getPosition(), node.getPosition(), brother.getPosition());
			//double brotherAngle2 = calculateAngle( node.getPosition(), brother.getPosition(),predNode.getPosition());
			double brotherAngle = angleBetween( node.getPosition(), brother.getPosition(),predNode.getPosition());
			if(predNode.getPosition().getY()<node.getPosition().getY()){
				
				if(predNode.getPosition().getX()<node.getPosition().getX()){
					//System.out.println("a");
			if(brotherAngle<currAngle){
				//System.out.println("ha l'angolo: "+brotherAngle+"che è maggiore di "+currAngle+"quindi aggiungo allo shift :"+brother.getNodeMagnitude());
				shift+=Variables.returnThickness(brother.getNodeMagnitude());
			}
				}
				else{
					//System.out.println("b");
						if(brotherAngle<=currAngle){
						//	System.out.println("ha l'angolo: "+brotherAngle+"che è maggiore di "+currAngle+"quindi aggiungo allo shift :"+brother.getNodeMagnitude());
							shift+=Variables.returnThickness(brother.getNodeMagnitude());
						}
					}
			}else{
				if(predNode.getPosition().getX()<node.getPosition().getX()){
				//System.out.println("c");
				if(brotherAngle<=currAngle){
					//System.out.println("ha l'angolo: "+brotherAngle+"che è maggiore di "+currAngle+"quindi aggiungo allo shift :"+brother.getNodeMagnitude());
					shift+=Variables.returnThickness(brother.getNodeMagnitude());
				}
				}
			else{
				//System.out.println("d");
					if(brotherAngle<=currAngle){
					//	System.out.println("ha l'angolo: "+brotherAngle+"che è maggiore di "+currAngle+"quindi aggiungo allo shift :"+brother.getNodeMagnitude());
						shift+=Variables.returnThickness(brother.getNodeMagnitude());
					}
				}
		}
		//else{
		//	Node brother = MiddleNodeGeneration.getNodeFromId(id);
		//	System.out.println("node magn: "+brother.getNodeMagnitude()+" th: "+returnThickness(brother.getNodeMagnitude()));
		//}
	}
	}
//	System.out.println("shift thickness: "+shift);
	
	return shift;
}

private static double calculateAngle(Position p1,Position p2,Position p3){
	double l1x = p2.getX() - p1.getX();
	double l1y = p2.getY() - p1.getY();
	double l2x = p3.getX() - p1.getX();
	double l2y = p3.getY() - p1.getY();
	double ang1 = Math.atan2(l1y, l1x);
	double ang2 = Math.atan2(l2y, l2x);

	double angle =  Math.abs(Math.toDegrees(ang2 - ang1));
		
	/*
	if (angle > 180) {
			angle = 360 - angle;
		}
		*/
		
	//	System.out.println("angolo tra "+p1+" "+p2+" "+p3+": "+angle );
		
		return angle;
}
private static double angleBetween(Position center, Position current, Position previous) {

	  double angle =  Math.toDegrees(Math.atan2(current.getX() - center.getX(),current.getY() - center.getY())-
	                        Math.atan2(previous.getX()- center.getX(),previous.getY()- center.getY()));
	  
	  if(angle<0){
		  return 360+angle;
	  }
	  return angle;
	}

private static double calculateAngle2(Position center, Position current, Position previous) {
	  double v1x = current.getX() - center.getX(); 
	  double v1y = current.getY() - center.getY();

	  //need to normalize:
	  double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
	  v1x /= l1;
	  v1y /= l1;

	  double v2x = previous.getX() - center.getX();
	  double v2y = previous.getY() - center.getY();

	  //need to normalize:
	  double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
	  v2x /= l2;
	  v2y /= l2;    

	  double rad = Math.acos( v1x * v2x + v1y * v2y );

	  double degres = Math.toDegrees(rad);
	  return degres;
	}
	
	private static void drawEdge(Node node) {
		
		
		if(node.isMiddleNode()){
		
			
		Position p1 = node.getPosition();
		
		
		for(String id_p2 : node.getNext_id_nodes()){
		//String id_p2=node.getNext_id_node();
		//Node node2 = MiddleNodeGeneration.nodes.get(id_p2);
			Node node2 = MiddleNodeGeneration.getNodeFromId(id_p2);
		Position p2= node2.getPosition();
		
		Color col = null;
		
		if(node.getNumRoot()>1){
			 col = Variables.listColors[node.getNumRoot()];
		}else{		
		 col = returnColor(node.getNodeMagnitude_R(), node.getNodeMagnitude_G(), node.getNodeMagnitude_B(),node.getNodeMagnitude());
		}
		//double thickness = getValue(node2.getNodeMagnitude(),Operations.minFlowMagnitude,Operations.maxFlowMagnitude ,Variables.minFlowWidth, Variables.maxFlowWidth);
		System.out.println("in drawEdge of node: "+node.getName());
		double thickness = Variables.returnThickness(node2.getNodeMagnitude());
		//drawLine(node.getId()+" "+node2.getId(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), col,thickness,node2.getNodeMagnitude());
		drawLineAirspace(node.getId()+" "+node2.getId(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), col,thickness,node2.getNodeMagnitude());
		}
		
		
if(node.getIdOrigin().compareTo(node.getPrev_id_node())==0){
			
			Node node_origin = MiddleNodeGeneration.getNodeFromId(node.getIdOrigin());	
			Position p0 = node_origin.getPosition();
			
			Color col = null;
			
			if(node.getNumRoot()>1){
				 col = Variables.listColors[node.getNumRoot()];
			}else{	
			 col = returnColor(node.getNodeMagnitude_R(), node.getNodeMagnitude_G(), node.getNodeMagnitude_B(),node.getNodeMagnitude());
			}
			//double thickness = getValue(node.getNodeMagnitude(),Operations.minFlowMagnitude,Operations.maxFlowMagnitude,Variables.minFlowWidth, Variables.maxFlowWidth);
			double thickness = Variables.returnThickness(node.getNodeMagnitude());
			
			//drawLine(node_origin+" "+node.getId(), p0.getX(), p0.getY(), p1.getX(), p1.getY(), col,thickness,node.getNodeMagnitude());
			drawLineAirspace(node_origin+" "+node.getId(), p0.getX(), p0.getY(), p1.getX(), p1.getY(), col,thickness,node.getNodeMagnitude());
		}
		
		}
	}
	

	
	private static Color returnColor(double r_val,double g_val, double b_val,double sum){
		double maxValue = returnMaxValue(r_val, g_val, b_val);
		
		//double sum = r_val+ g_val+b_val;
		
		/*
		int r=(int)((r_val/maxValue)*255);
		int g=(int)((g_val/maxValue)*255);
		int b=(int)((b_val/maxValue)*255);
		*/
		
		int r=(int)((r_val/sum)*255);
		int g=(int)((g_val/sum)*255);
		int b=(int)((b_val/sum)*255);
		
		/*
		System.out.println("Operations.minFlow_R, Operations.maxFlow_R: "+Operations.minFlow_R+" "+ Operations.maxFlow_R);
		System.out.println("Operations.minFlow_G, Operations.maxFlow_G: "+Operations.minFlow_G+" "+ Operations.maxFlow_G);
		System.out.println("Operations.minFlow_B, Operations.maxFlow_B: "+Operations.minFlow_B+" "+ Operations.maxFlow_B);
		*/
		/*
		double normR=getColorValue(r_val, Operations.minFlow_R, Operations.maxFlow_R);
		double normG=getColorValue(g_val, Operations.minFlow_G, Operations.maxFlow_G);
		double normB=getColorValue(b_val, Operations.minFlow_B, Operations.maxFlow_B);
		*/
		/*
		System.out.println("Operations.minFlow_R_perc, Operations.maxFlow_R_perc: "+Operations.minFlow_R_perc+" "+ Operations.maxFlow_R_perc);
		System.out.println("Operations.minFlow_G_perc, Operations.maxFlow_G_perc: "+Operations.minFlow_G_perc+" "+ Operations.maxFlow_G_perc);
		System.out.println("Operations.minFlow_B_perc, Operations.maxFlow_B_perc: "+Operations.minFlow_B_perc+" "+ Operations.maxFlow_B_perc);
	*/
		/*
		System.out.println("r_val/sum: "+r_val/sum);
		System.out.println("g_val/sum: "+g_val/sum);
		System.out.println("b_val/sum: "+b_val/sum);
		*/
		double normR=getColorValue(r_val/sum, Operations.minFlow_R_perc, Operations.maxFlow_R_perc);
		double normG=getColorValue(g_val/sum, Operations.minFlow_G_perc, Operations.maxFlow_G_perc);
		double normB=getColorValue(b_val/sum, Operations.minFlow_B_perc, Operations.maxFlow_B_perc);
		
		
		
		if(Double.isNaN(normR)){normR=0;}	
		if(Double.isNaN(normG)){normG=0;}
		if(Double.isNaN(normB)){normB=0;}
		
		Color col;
		if((r==0)&(g==0)&(b==0)){
		 col=Color.GREEN;
		}else{
			if(Variables.useRYB){
				System.out.println("val: "+r_val+" "+ g_val+" "+ b_val);
				System.out.println("ryb: "+r+" "+ g+" "+ b);
				System.out.println("norm ryb: "+normR+" "+ normG+" "+ normB);
				if(Variables.normalizeRangeAttributes){
					col = ConvertColors.RYBtoRGB_Color(normR, normG, normB);
				}else{
					col = ConvertColors.RYBtoRGB_Color(r, g, b);
				}
					
		//	
				
				System.out.println("final ryb color: "+col);
			}else{
				col=	new Color(r,g,b);
			}
			//col = new Color(r,g,b);
			//col = new Color(r,g,b);
		}
		
		return col;
	}
	
	
private static double getColorValue(double magnitude, double minMagnitude, double maxMagnitude){
		
	double minValue=0;
	double maxValue=255;
	
		float ratio=(float)((magnitude-minMagnitude)/(maxMagnitude-minMagnitude));
		return ((maxValue-minValue)*ratio)+minValue;
		
	}
	
	private static double returnMaxValue(double r,double g, double b){
		
		if((r>=g)&&(r>=b)){
			return r;
		} else
		
		if((g>=r)&&(g>=b)){
			return g;
		} else
		
		if((b>=g)&&(b>=r)){
			return b;
		}else return 0;
		
	}
	
	private static void drawRejectArea(Node node) {
		double radiusArea;
		
	//	double minRadius=Variables.leafNodeMinRadius;
	//	double maxRadius=Variables.leafNodeMaxRadius;
	
	//double thickness = Variables.getValue(node.getNodeMagnitude(),Operations.minLeafNodeFlowMagnitude,Operations.maxLeafNodeFlowMagnitude,minRadius, maxRadius);
		double thickness = Variables.returnNodeLeafThickness(node.getNodeMagnitude());
		
		Position xy = node.getPosition();

		double x = xy.getX();
		double y = xy.getY();
		
		gov.nasa.worldwind.geom.Position currPos = gov.nasa.worldwind.geom.Position.fromDegrees(y, x);
		
		Color col = Color.BLUE;
		
		SurfaceCircle objNeigh=null;
		//if(!isIntermediateNode){
		
		System.out.println("drawRejectArea: "+Variables.rejectBufferInMeters);
		
			radiusArea=thickness+Variables.rejectBufferInMeters;
			 objNeigh = new SurfaceCircle(currPos, radiusArea);
			objNeigh.setHighlighted(false);
			objNeigh.setAttributes(getAttributes(new Material(col),false));	
			objNeigh.setVisible(true);
			objNeigh.setIntervals(16);
			//listNeighbors.put(id, objNeigh);
			rejectLayer.addRenderable(objNeigh);
		//}
	}
	
	private static void drawSamePosArea(Node node) {
		double radiusArea;
		
		Position xy = node.getPosition();

		double x = xy.getX();
		double y = xy.getY();
		
		gov.nasa.worldwind.geom.Position currPos = gov.nasa.worldwind.geom.Position.fromDegrees(y, x);
		
		Color col = Color.BLUE;
		
		SurfaceCircle objNeigh=null;
		//if(!isIntermediateNode){
			radiusArea=Variables.samePositionDistanceInMeters;
			 objNeigh = new SurfaceCircle(currPos, radiusArea);
			objNeigh.setHighlighted(false);
			objNeigh.setAttributes(getAttributes(new Material(col),false));	
			objNeigh.setVisible(true);
			objNeigh.setIntervals(16);
			//listNeighbors.put(id, objNeigh);
			samePosLayer.addRenderable(objNeigh);
		//}
	}
	
	private static void drawDistanceArea(Node node) {
		double radiusArea;
		
		Position xy = node.getPosition();

		double x = xy.getX();
		double y = xy.getY();
		
		gov.nasa.worldwind.geom.Position currPos = gov.nasa.worldwind.geom.Position.fromDegrees(y, x);
		
		Color col = Color.BLUE;
		
		SurfaceCircle objNeigh=null;
		//if(!isIntermediateNode){
			radiusArea=Variables.attractionRadiusInMeters;
			 objNeigh = new SurfaceCircle(currPos, radiusArea);
			objNeigh.setHighlighted(false);
			objNeigh.setAttributes(getAttributes(new Material(col),false));	
			objNeigh.setVisible(true);
			objNeigh.setIntervals(16);
			//listNeighbors.put(id, objNeigh);
			distanceLayer.addRenderable(objNeigh);
		//}
	}
	
	private static void drawNode(Node node) {
		Position xy = node.getPosition();

		double x = xy.getX();
		double y = xy.getY();
		

		if(node.isRoot()){
	
			Color col = Variables.rootNodeColor;
			double radius=Variables.rootNodeRadius;
//			g.setColor(col);
//			g.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
			//fillCircle(node.getId(),null,null,x,y,col,radius,false);
			if(Operations.totFlow_R_perc!=0){
				if(!Variables.useDifferentAttributes){
					createPieChart(node.getId(),node, radius*(0.8));
					fillAirspaceCircle(node.getId(),null,null,x,y,col,radius,false,true,node.getIndex(),0,0);
					}else{
						createMultiPieChart(node.getId(),node, radius*(0.8));
						fillAirspaceCircle(node.getId(),null,null,x,y,col,radius,false,true,node.getIndex(),0,0);
					}
				
			}else{
				fillAirspaceCircle(node.getId(),null,null,x,y,col,radius,false,true,node.getIndex(),0,0);
			}
			
			
		
		}else
		if(node.isLeaf()){
		
			/*
			double minRadius=Variables.leafNodeMinRadius;
				double maxRadius=Variables.leafNodeMaxRadius;
			
			double thickness = getValue(node.getNodeMagnitude(),Operations.minLeafNodeFlowMagnitude,Operations.maxLeafNodeFlowMagnitude,minRadius, maxRadius);
			*/
			double thickness = Variables.returnNodeLeafThickness(node.getNodeMagnitude());
			
			if((node.getNodeMagnitude_B()==0)&(node.getNodeMagnitude_G()==0)&(node.getNodeMagnitude_R()==0)){
				Color col = Color.BLUE;
				//System.out.println("DRAW LEAF NODE");
				fillAirspaceCircle(node.getId(),null,node.getName(),x,y,col,thickness,false,false,node.getIndex(),0,0);
			}else{
				if(!Variables.useDifferentAttributes){
				createPieChart(node.getId(),node, thickness);
				}else{
					createMultiPieChart(node.getId(),node, thickness);	
				}
			}
			
			//fillAirspaceCircle(node.getId(),node.getName(),"index: "+node.getIndex(),x,y,col,thickness,false,false,node.getIndex(),0,0);
		}else{
			//System.out.println("is middle node.......");
			Color col;
			double radius;
			
			if(node.getNext_id_nodes().size()>1){
				col = Variables.forkNodeColor;
				  radius=Variables.nodeRadius*2;
			}else if(node.isRejected()){
			//	col = Variables.forkNodeColor;
				 col = Variables.middleNodeColor;
				 radius=Variables.nodeRadius*2;
			}
			else if(node.isShouldBeRemoved()){
			//	col = Color.YELLOW;
				 col = Variables.middleNodeColor;
				 radius=Variables.nodeRadius*2;
			}
			else{
				 col = Variables.middleNodeColor;
				  radius=Variables.nodeRadius*2;
			}
			
			
			
			if(Variables.enable_render_nodes){
				double electrForce = node.getElectrostaticForce().length();
				double stressForce = node.getSpringForce().length();
				//fillCircle(node.getId(),null,String.valueOf(node.getNodeMagnitude()),x,y,col,radius,true);
				//String label = ("index: "+node.getIndex()+" Up: "+node.getUpAngleIndex()+" Down: "+node.getDownAngleIndex());
				String label = String.valueOf(node.getNodeMagnitude()+" "+Variables.returnThickness(node.getNodeMagnitude())+" min F: "+Operations.minFlowMagnitude+" max F:"+Operations.maxFlowMagnitude+" min W: "+Variables.minFlowWidth+" max W: "+Variables.maxFlowWidth);
				fillAirspaceCircle(node.getId(),null,label,x,y,col,radius,true,false,node.getIndex(),electrForce,stressForce);
			}
			
		}		
		
	}
	
private static void createMultiPieChart(String id,Node node,double thickness){
		
		ArrayList<Airspace> airspaces = new ArrayList<Airspace>();
		
		double x= node.getPosition().getX();
		double y= node.getPosition().getY();
		gov.nasa.worldwind.geom.Position currPos = gov.nasa.worldwind.geom.Position.fromDegrees(y, x);
		
		double r;
		double g;
		double b;
		 double magnitude;
		
		if(node.isRoot()){
		
		 r =Operations.totFlow_R_perc;
		 g = Operations.totFlow_G_perc;
		 b =Operations.totFlow_B_perc;
		 magnitude= Operations.totFlow;
			System.out.println("è root: "+r+" - "+g+" - "+b+" : "+magnitude);
		}else{
			 r = node.getNodeMagnitude_R();
			 g = node.getNodeMagnitude_G();
			 b = node.getNodeMagnitude_B();
			  magnitude = node.getNodeMagnitude();
		}
		
		boolean onlyTwoAttributes = false;
		if((r==0)||(g==0)||(b==0)){
			onlyTwoAttributes=true;
		}
		
		//double sum = r+g+b;
		
		
		//System.out.println("sum: "+sum);
		//System.out.println("magn: "+magnitude);
		
		double startAzim = 0;
		double endAzim = 0;
		
		if(r!=0){
			
			 endAzim = r/magnitude*360.0;
			Color col = Color.red;
			if(Variables.useRYB){
				col = ConvertColors.RYBtoRGB_Color(255, 0, 0);
				//col = Color.YELLOW;
			}
			
			RenderableNode cyl = new RenderableNode(id);
        cyl.setCenter(currPos);
        //cyl.setRadius(thickness);
        cyl.setRadii(thickness*(2.0/3.0), thickness);
        if(onlyTwoAttributes){
        	cyl.setRadii(thickness*(1.5/3.0), thickness);	
        }
        setupDefaultMaterialForNodes(cyl,col,Variables.outlineFlowColor);
        cyl.setAzimuths(Angle.fromDegrees(startAzim), Angle.fromDegrees(endAzim));
        cyl.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
        cyl.setTerrainConforming(false, false);
        cyl.setValue(AVKey.DISPLAY_NAME, "-> "+node.getName()+" "+r);     
        
        
        cyl.setEnableLevelOfDetail(false);
        
        airspaces.add(cyl);
        
        
        
        

		
		 endAzim = r/magnitude*360.0;
		
		
		
		RenderableNode cyl2 = new RenderableNode(id);
   cyl2.setCenter(currPos);
   //cyl2.setRadius(thickness);
   cyl2.setRadii(thickness*(2.0/3.0), thickness);
   if(onlyTwoAttributes){
   	cyl2.setRadii(thickness*(1.5/3.0), thickness);	
   }

   setupDefaultMaterialForNodes(cyl2,Color.WHITE,Variables.outlineFlowColor);
   cyl2.setAzimuths(Angle.fromDegrees(endAzim), Angle.fromDegrees(360));
   cyl2.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
   cyl2.setTerrainConforming(false, false);
   cyl2.setValue(AVKey.DISPLAY_NAME, "-> "+node.getName()+" "+r);     
   
   
   cyl2.setEnableLevelOfDetail(false);
   
   airspaces.add(cyl2);
        
        //startAzim=endAzim;
		}
		
		if(g!=0){
			
			 endAzim = (g/magnitude*360.0);
			Color col = Color.GREEN;
			
			if(Variables.useRYB){
				col = ConvertColors.RYBtoRGB_Color(0, 255, 0);
				//col = Color.YELLOW;
			}
			
			RenderableNode cyl = new RenderableNode(id);
       cyl.setCenter(currPos);
       //cyl.setRadius(thickness*(2.0/3.0));
       cyl.setRadii(thickness*(1.0/3.0), thickness*(2.0/3.0));
       if(onlyTwoAttributes){
       	cyl.setRadii(0, thickness*(1.5/3.0));	
       }

       setupDefaultMaterialForNodes(cyl,col,Variables.outlineFlowColor);
       cyl.setAzimuths(Angle.fromDegrees(startAzim), Angle.fromDegrees(endAzim));
       cyl.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
       cyl.setTerrainConforming(false, false);
       cyl.setValue(AVKey.DISPLAY_NAME, "-> "+node.getName()+" "+g);        
       
       cyl.setEnableLevelOfDetail(false);
       
       airspaces.add(cyl);
       
       
       
  	
		
		RenderableNode cyl2 = new RenderableNode(id);
cyl2.setCenter(currPos);
//cyl2.setRadius(thickness*(2.0/3.0));
cyl2.setRadii(thickness*(1.0/3.0), thickness*(2.0/3.0));
if(onlyTwoAttributes){
   	cyl2.setRadii(0, thickness*(1.5/3.0));	
   }
setupDefaultMaterialForNodes(cyl2,Color.WHITE,Variables.outlineFlowColor);
cyl2.setAzimuths(Angle.fromDegrees(endAzim),Angle.fromDegrees(360));
cyl2.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
cyl2.setTerrainConforming(false, false);
cyl2.setValue(AVKey.DISPLAY_NAME, "-> "+node.getName()+" "+g);        

cyl2.setEnableLevelOfDetail(false);

airspaces.add(cyl2);
       
       
     //  startAzim=endAzim;
		}
		
		if(b!=0){
			
			 endAzim = (b/magnitude*360.0);
			Color col = Color.BLUE;
			if(Variables.useRYB){
				col = ConvertColors.RYBtoRGB_Color(0, 0, 255);
				//col = Color.YELLOW;
			}
			RenderableNode cyl = new RenderableNode(id);
       cyl.setCenter(currPos);
       cyl.setRadius(thickness*(1.0/3.0));
       if(onlyTwoAttributes){
    	   	cyl.setRadii(0, thickness*(1.5/3.0));	
    	   }
       setupDefaultMaterialForNodes(cyl,col,Variables.outlineFlowColor);
       cyl.setAzimuths(Angle.fromDegrees(startAzim), Angle.fromDegrees(endAzim));
       cyl.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
       cyl.setTerrainConforming(false, false);
       cyl.setValue(AVKey.DISPLAY_NAME, "-> "+node.getName()+" "+b);        
       
       cyl.setEnableLevelOfDetail(false);
       
       airspaces.add(cyl);
       
       
       
       
   	RenderableNode cyl2 = new RenderableNode(id);
    cyl2.setCenter(currPos);
    cyl2.setRadius(thickness*(1.0/3.0));
    if(onlyTwoAttributes){
       	cyl2.setRadii(0, thickness*(1.5/3.0));	
       }
    setupDefaultMaterialForNodes(cyl2,Color.WHITE,Variables.outlineFlowColor);
    cyl2.setAzimuths(Angle.fromDegrees(endAzim), Angle.fromDegrees(360));
    cyl2.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
    cyl2.setTerrainConforming(false, false);
    cyl2.setValue(AVKey.DISPLAY_NAME, "-> "+node.getName()+" "+b);        
    
    cyl2.setEnableLevelOfDetail(false);
    
    airspaces.add(cyl2);
       
       
       
       startAzim=endAzim;
		}
		
		
		nodesAirspaces.addAirspaces(airspaces);
		}
	
	private static void createPieChart(String id,Node node,double thickness){
		
		ArrayList<Airspace> airspaces = new ArrayList<Airspace>();
		
		double x= node.getPosition().getX();
		double y= node.getPosition().getY();
		gov.nasa.worldwind.geom.Position currPos = gov.nasa.worldwind.geom.Position.fromDegrees(y, x);
		
		double r;
		double g;
		double b;
		
		if(!node.isRoot()){
		 r = node.getNodeMagnitude_R();
		 g = node.getNodeMagnitude_G();
		 b = node.getNodeMagnitude_B();
		}else{
			 r =Operations.totFlow_R_perc;
			 g = Operations.totFlow_G_perc;
			 b = Operations.totFlow_B_perc;	
		}
		double sum = r+g+b;
//double magnitude = node.getNodeMagnitude();
		
		System.out.println("sum: "+sum);
	//	System.out.println("magn: "+magnitude);
		
		double startAzim = 0;
		double endAzim = 0;
		
		if(r!=0){
			
			 endAzim = r/sum*360.0;
			Color col = Color.red;
			if(Variables.useRYB){
				col = ConvertColors.RYBtoRGB_Color(255, 0, 0);
				//col = Color.YELLOW;
			}
			
			RenderableNode cyl = new RenderableNode(id);
        cyl.setCenter(currPos);
        cyl.setRadius(thickness);
        setupDefaultMaterialForNodes(cyl,col,Variables.outlineFlowColor);
        cyl.setAzimuths(Angle.fromDegrees(startAzim), Angle.fromDegrees(endAzim));
        cyl.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
        cyl.setTerrainConforming(false, false);
        cyl.setValue(AVKey.DISPLAY_NAME, "-> "+node.getName()+" "+r);     
        
        
        cyl.setEnableLevelOfDetail(false);
        
        airspaces.add(cyl);
        
        startAzim=endAzim;
		}
		
		if(g!=0){
			
			 endAzim = startAzim+(g/sum*360.0);
			Color col = Color.GREEN;
			
			if(Variables.useRYB){
				col = ConvertColors.RYBtoRGB_Color(0, 255, 0);
				//col = Color.YELLOW;
			}
			
			RenderableNode cyl = new RenderableNode(id);
       cyl.setCenter(currPos);
       cyl.setRadius(thickness);
       setupDefaultMaterialForNodes(cyl,col,Variables.outlineFlowColor);
       cyl.setAzimuths(Angle.fromDegrees(startAzim), Angle.fromDegrees(endAzim));
       cyl.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
       cyl.setTerrainConforming(false, false);
       cyl.setValue(AVKey.DISPLAY_NAME, "-> "+node.getName()+" "+g);        
       
       cyl.setEnableLevelOfDetail(false);
       
       airspaces.add(cyl);
       
       startAzim=endAzim;
		}
		
		if(b!=0){
			
			 endAzim = startAzim+(b/sum*360.0);
			Color col = Color.BLUE;
			if(Variables.useRYB){
				col = ConvertColors.RYBtoRGB_Color(0, 0, 255);
				//col = Color.YELLOW;
			}
			RenderableNode cyl = new RenderableNode(id);
       cyl.setCenter(currPos);
       cyl.setRadius(thickness);
       setupDefaultMaterialForNodes(cyl,col,Variables.outlineFlowColor);
       cyl.setAzimuths(Angle.fromDegrees(startAzim), Angle.fromDegrees(endAzim));
       cyl.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
       cyl.setTerrainConforming(false, false);
       cyl.setValue(AVKey.DISPLAY_NAME, "-> "+node.getName()+" "+b);        
       
       cyl.setEnableLevelOfDetail(false);
       
       airspaces.add(cyl);
       
       startAzim=endAzim;
		}
		
		
		nodesAirspaces.addAirspaces(airspaces);
		}
	
	private static void fillAirspaceCircle(String id,String name,String label,double x, double y,Color col,double radius,boolean isIntermediateNode,boolean isRootNode,int index,double electrForce,double stressForce){
		
		gov.nasa.worldwind.geom.Position currPos = gov.nasa.worldwind.geom.Position.fromDegrees(y, x);
	{
				
	// Cylinder.
    //CappedCylinder cyl = new CappedCylinder();
		RenderableNode cyl = new RenderableNode(id);
    cyl.setCenter(currPos);
    cyl.setRadius(radius);
    if(!isRootNode){
    cyl.setAltitudes(Variables.minNodeAltitude,Variables.maxNodeAltitude);
    }else{
    	cyl.setAltitudes(Variables.minNodeAltitude,Variables.maxRootNodeAltitude);	
    }
    cyl.setTerrainConforming(false, false);
    //cyl.setValue(AVKey.DISPLAY_NAME, "index: "+index+", magnitude: "+(magnitude)+" electr: "+electrForce+", stress: "+stressForce);
    cyl.setValue(AVKey.DISPLAY_NAME, "name -> "+label);
    setupDefaultMaterialForNodes(cyl,col,Variables.outlineFlowColor);
    
	
	if(!isIntermediateNode){
	nodesAirspaces.addAirspace(cyl);
	
	}else{
		intermediateNodesAirspaces.addAirspace(cyl);
		//neighborsLayer.addRenderable(objNeigh);
	}
	
	}
	
}

	
	private static void drawCurve(RenderableControlPoints points
			//,Color col
			){
				
		CubicSplinePolyline polyline = new CubicSplinePolyline(points);
		//polyline.setColor(col);
	
		polyline.setFollowTerrain(false);
		polyline.setOffset(Variables.maxFlowAltitude);
		//polyline.setPositions(points.positions);
		
		curveLayer.addRenderable(polyline);
		//listCurves.add(polyline);
		
	}
	

private static void drawLineAirspace(String id,double x1, double y1,double x2, double y2,Color col,double thickness,double magnitude){
	
	gov.nasa.worldwind.geom.Position p1 = gov.nasa.worldwind.geom.Position.fromDegrees(y1, x1);
	gov.nasa.worldwind.geom.Position p2 = gov.nasa.worldwind.geom.Position.fromDegrees(y2, x2);
//Left Orbit
Orbit orbit = new Orbit();
orbit.setLocations(p1, p2);
orbit.setAltitudes(10000.0, Variables.maxFlowAltitude);
orbit.setWidth(thickness);
orbit.setOrbitType(Orbit.OrbitType.CENTER);
orbit.setTerrainConforming(false, false);
orbit.setValue(AVKey.DISPLAY_NAME, String.valueOf(magnitude+" --- "+thickness));
setupDefaultMaterial(orbit, col,Variables.outlineFlowColor);




edgesAirspaces.addAirspace(orbit);

}

static void setupDefaultMaterialForNodes(Airspace a, Color color,Color outlineColor)
{
    a.getAttributes().setDrawOutline(true);
    //Material material = new Material(color);
    
    a.getAttributes().setMaterial(new Material(color));
    //a.getAttributes().setOutlineMaterial(new Material(WWUtil.makeColorBrighter(color)));
    a.getAttributes().setOutlineMaterial(new Material(outlineColor));
    a.getAttributes().setOpacity(1);
   // a.getAttributes().setOutlineOpacity(0.9);
   // a.getAttributes().setOutlineWidth(3.0);
}

static void setupDefaultMaterial(Airspace a, Color color,Color outlineColor)
{
    a.getAttributes().setDrawOutline(false);
    a.getAttributes().setMaterial(new Material(color));
    //a.getAttributes().setOutlineMaterial(new Material(WWUtil.makeColorBrighter(color)));
    a.getAttributes().setOutlineMaterial(new Material(outlineColor));
    a.getAttributes().setOpacity(1);
   // a.getAttributes().setOutlineOpacity(0.9);
   // a.getAttributes().setOutlineWidth(3.0);
}
	
	private static ShapeAttributes getAttributes(Material color, boolean drawInterior) {
		ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorOpacity(1.0);
        //attrs.setEnableLighting(true);
        attrs.setInteriorMaterial(color);
        attrs.setOutlineMaterial(color);
        attrs.setOutlineWidth(0.1d);
        attrs.setDrawInterior(drawInterior);
        attrs.setDrawOutline(true);
        return attrs;
	}
	
	
	
	public static void updateRendering(){
		 
		Render.resetLayers();
		
		 System.out.println("IN UPDATE!!");
		 if(MiddleNodeGeneration.getNumNodes()!=0){
			// Render.initEdgesLayer();
			// Render.initMiddleNodeLayer();
		Render.drawNodesAndEdges( MiddleNodeGeneration.getNodeValues());	
		 }
		 
		 redraw();
	}

	private static void redraw(){
	wwd.repaint();
	wwd.redraw();
	}
	
}
