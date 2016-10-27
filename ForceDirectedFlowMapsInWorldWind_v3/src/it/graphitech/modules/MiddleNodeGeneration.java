package it.graphitech.modules;

import it.graphitech.Operations;
import it.graphitech.Variables;
import it.graphitech.input.FlowComparator;
import it.graphitech.input.FlowSource;
import it.graphitech.input.NodeAngleComparator;
import it.graphitech.input.NodeAngleComparator2;
import it.graphitech.objects.Node;
import it.graphitech.objects.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class MiddleNodeGeneration {

	static private HashMap<String, Node> nodes = new HashMap<>();
	//static private ArrayList<String> nodesId = new ArrayList<>();
	static public Collection<Node> leafNodes = new ArrayList<Node>();
	double first_step = 0.005;
	int numRoot = 0;

	public MiddleNodeGeneration() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Set<Entry<String,Node>>  getEntrySetNodes(){
		return MiddleNodeGeneration.nodes.entrySet();
	}
	
	public static Collection<Node> getNodeValues(){
		return MiddleNodeGeneration.nodes.values();
	}
	
	
	public static Node getNodeFromId(String idInteractingNode) {
		return MiddleNodeGeneration.nodes
		.get(idInteractingNode);
	}
	
	public static void initNodes(){
		MiddleNodeGeneration.nodes=new HashMap<>();
	}
	
	public static int getNumNodes(){
		return MiddleNodeGeneration.nodes.size();
	}
	
	public static void removeNode(String key){
		 MiddleNodeGeneration.nodes.remove(key);
	}
	
	
	public void run(ArrayList<FlowSource> inputFlows) {

		nodes = new HashMap<>();
		//nodesId = new ArrayList<String>();

		// System.out.println("createNodesId");
		//createNodesId(inputFlows);
		// int id_tree=1;

		for (FlowSource flow : inputFlows) {
			System.out.println("analizzo flow input: " + flow.flowMagnitude);
			Position xy_start = new Position(flow.lonStart, flow.latStart);
			Position xy_end = new Position(flow.lonDest, flow.latDest);

			System.out.println("xy_start: " + xy_start + " - xy_end: " + xy_end);

			if (xy_start.equals(xy_end)) {
				System.out.println("start node equal to destination node!");
			} else {

				String idStartNode = Node.createId(xy_start, xy_start);
				String id_endNode = Node.createId(xy_end, xy_start);
				double length = xy_start.calculateDistance(xy_end);

				//START NODE CREATION
				if (!nodes.containsKey(idStartNode)) {
					numRoot++;
					System.out.println("CREO ROOT: " + idStartNode);
					Node nodeStart = new Node(0, xy_start, idStartNode,
							id_endNode, flow.flowMagnitude, idStartNode, length);
					nodeStart.setNumRoot(numRoot);
					System.out.println("start numroot: "+numRoot);
					nodes.put(idStartNode, nodeStart);

				}
				
				Node node_start = nodes.get(idStartNode);
				//END NODE CREATION
				Node nodeEnd = new Node(-1, xy_end, idStartNode, id_endNode,
						flow.flowMagnitude, id_endNode, length);
				nodeEnd.setMagnitudeRGB(flow.getFlowMagnitudeR(), flow.getFlowMagnitudeG(), flow.getFlowMagnitudeB());
				nodeEnd.setUpAngleIndex(flow.getAngleIndex());
				nodeEnd.setDownAngleIndex(flow.getAngleIndex());
				nodeEnd.setMaxAngleIndex(flow.getMaxAngleIndex());
				
				if(flow.getName()!=null){
					System.out.println("set name");
					nodeEnd.setName(flow.getName());
				}
				
				nodeEnd.setPrev_id_node(idStartNode);
				// nodeEnd.setIdOrigin(xy_start);
				//da modificare
				nodeEnd.setNumRoot(node_start.getNumRoot());
				System.out.println("end numroot: "+numRoot);
				nodes.put(id_endNode, nodeEnd);
				leafNodes.add(nodeEnd);


				

				
				
				
				
				

				//int numSegment = (int) (length / Variables.lengthMiddleSegment);

				// System.out.println("length: "+length);
				// System.out.println("num: "+Variables.numSegment);
				System.out.println("setMagnitudeRGB: "+nodeEnd.getNodeMagnitude_R()+" , "+nodeEnd.getNodeMagnitude_G()+" , "+nodeEnd.getNodeMagnitude_B());
				//createMiddleNodes(node_start, nodeEnd, numSegment, length);
				createMiddleNodes(node_start, nodeEnd, Variables.lengthMiddleSegment, length);
			}
		}

		printRoots();
		System.out.println("");

		updateAngleIndexes();
		
		Variables.numOrigin=numRoot;
		
	}

	private static void updateAngleIndexes(){
		Collection<Node> nodesSet = nodes.values();
		
		ArrayList<Node>  nodesArray = new ArrayList<>(nodesSet);
		
		
		//order example:
		/*
		 * idOrigin = a index = 1 angle = 40
		 * idOrigin = a index = 1 angle = 50
		 * idOrigin = a index = 2 angle = 10
		 * idOrigin = a index = 2 angle = 60
		 * idOrigin = b ....
		 */
		Collections.sort(nodesArray, new NodeAngleComparator());
		
		int lastIndex=-1;
		int ind=0;
		
		for (Node n : nodesArray) {
			
	
			
			if((n.getIndex()==-1)||(n.getIndex()==0)){
				
			}else{
				if(n.getIndex()==lastIndex){
					n.setUpAngleIndex(ind);
					n.setDownAngleIndex(ind);
					ind++;
				}else{
					ind=0;
					n.setUpAngleIndex(ind);
					n.setDownAngleIndex(ind);
					ind++;
					lastIndex=n.getIndex();
				}
			}
				
		}
		
	
		
		Collections.sort(nodesArray, new NodeAngleComparator2());
		 lastIndex=-1;
		 int numNodes=0;
for (Node n : nodesArray) {
			
			if((n.getIndex()==-1)||(n.getIndex()==0)){
				
			}else{
				if(n.getIndex()==lastIndex){
					n.setMaxAngleIndex(numNodes);
				}else{
					numNodes=n.getUpAngleIndex();
					n.setMaxAngleIndex(numNodes);
					lastIndex=n.getIndex();
				}
			}
				
		}

/*
System.out.println("update angle index 2");
for (Node n : nodesArray) {
	System.out.println("idOrigin: "+n.getIdOrigin()+"id: " + n.getId() + " index: "
			+ n.getIndex()+" angleIndex: "+n.getDownAngleIndex()+" max: "+n.getMaxAngleIndex());
}
*/
	}
	
	private void printRoots() {
		for (Node n : nodes.values()) {
			if (n.isRoot()) {
				System.out.println("root: " + n.getId() + " ->"
						+ n.getPosition());
			}
		}
	}

	
	/*
	private void createNodesId(ArrayList<FlowSource> inputFlows) {

		for (FlowSource flow : inputFlows) {

			// Position xy_start = Render.getxy(flow.latStart, flow.lonStart);
			Position xy_start = new Position(flow.lonStart, flow.latStart);
			String id = xy_start.getX() + " - " + xy_start.getY();
			if (!nodesId.contains(id)) {
				// System.out.println("add: "+id);
				nodesId.add(id);
			}
		}

	}

*/
	public void assignNeighbours() {
		for (Node e : nodes.values()) {

			if (((!e.isLeaf()) && (!e.isRoot()))) {

				Set<String> neig = new HashSet<>();
				
				for (Node interacting_node : nodes.values()) {
					// check if the node is not the same
					if (interacting_node.getId().compareTo(e.getId()) != 0) {
						
						// check if the node is not a leaf node
						if (!interacting_node.isLeaf()) {
							
							// if the origin is the same and the destination is
							// different
							if ((
									(e.getIdOrigin() == interacting_node.getIdOrigin()) 
									&
									(interacting_node.getIdDestination().compareTo(e.getIdDestination()) != 0))
									// OR is the origin is not the same
									//|| ((e.getIdOrigin() != interacting_node
										//	.getIdOrigin()))
											) {
								
								
								//System.out.println("add n?");
								
								if (Math.abs(e.getIndex()
										- interacting_node.getIndex()) < Variables.deltaNeighbourIndex) {
									
									
									if(UpdateTreeStructure.nodesHaveNearIndex(e,interacting_node)){
									neig.add(interacting_node.getId());
									//System.out.println("add n");
									}
									
									
								}
							}

						}
						/*
						else {
							System.out.println("add lead node as interaction node");
							neig.add(interacting_node.getId());
						}
						*/
					} 
				}

				/*
				if(neig.size()!=2){
					System.out.println("n index: "+e.getIndex());
				System.out.println("# interacting nodes: "+neig.size());
				}
				*/
				e.setNeighbours(neig);

			}
		}
	}


	
	private void createMiddleNodes(Node startNode, Node endNode,
			double lengthMiddleSegments, double length) {

		int numSegments = (int) (length / lengthMiddleSegments);
		System.out.println("createMiddleNodes, num segments: " + numSegments);
if(Variables.max_step<numSegments){
	Variables.max_step=numSegments;
}
		// double distance=NodeManager.getDistance(startNode, endNode);
		Node[] listPosition = new Node[numSegments + 1];

		listPosition[0] = startNode;

		for (int i = 1; i < numSegments; i++) {
			Position pos = getPosition(startNode.getId(), endNode.getId(),
					//(double) i / (double) numSegments
					(lengthMiddleSegments*i)/length
					);
			Node intNode = createNode(i, pos, startNode, endNode, length);
			intNode.setMaxIndex(numSegments);
			
			//System.out.println(endNode.getUpAngleIndex()+" - "+endNode.getDownAngleIndex()+" - "+endNode.getMaxAngleIndex());
			
			intNode.setMagnitudeRGB(endNode.getNodeMagnitude_R(), endNode.getNodeMagnitude_G(), endNode.getNodeMagnitude_B());
			
			intNode.setUpAngleIndex(endNode.getUpAngleIndex());
			intNode.setDownAngleIndex(endNode.getDownAngleIndex());
			intNode.setMaxAngleIndex(endNode.getMaxAngleIndex());
			//intNode.setAngleFromOrigin(endNode.getAngleFromOrigin());
			
			listPosition[i] = intNode;
		}

		if (numSegments > 0) {
			endNode.setPrev_id_node(listPosition[numSegments - 1].getId());
		}
		listPosition[numSegments] = endNode;

		for (int i = 1; i < numSegments; i++) {

			Node prevNode = listPosition[i - 1];
			Node nextNode = listPosition[i + 1];

			listPosition[i].setPrev_id_node(prevNode.getId());
			listPosition[i].addNext_id_node(nextNode.getId());
			// System.out.println("nextNode.getId(): "+nextNode.getId());
			nodes.put(listPosition[i].getId(), listPosition[i]);

		}

	}

	private Node createNode(int i, Position pos, Node startNode, Node endNode,
			double length) {
		String id = Node.createId(pos, startNode.getPosition());

		Node intNode = new Node(i, pos, startNode.getId(), endNode.getId(),
				endNode.getNodeMagnitude(), id, length);
		intNode.setNumRoot(startNode.getNumRoot());
		
		/*
		double distance = intNode.distanceFrom(startNode.getPosition());
		intNode.setInitDistanceFromStartingNode(distance);
*/
		return intNode;
	}

	private Position getPosition(String idStart, String idEnd, double intermValue) {

		Node node_start = nodes.get(idStart);
		Node node_end = nodes.get(idEnd);

		double diffX = (node_end.getPosition().getX() - node_start
				.getPosition().getX()) * intermValue;
		double diffY = (node_end.getPosition().getY() - node_start
				.getPosition().getY()) * intermValue;

		Position res = new Position(node_start.getPosition().getX() + diffX,
				node_start.getPosition().getY() + diffY);

		return res;
	}

	
	public static void updateMaxFlowMagnitude(){
		for(Node node : nodes.values()){ 
				
			if(node.getNodeMagnitude()>Operations.maxFlowMagnitude){
				Operations.maxFlowMagnitude=node.getNodeMagnitude();
			}
			
		}
		
		Variables.updateFlowWidth();
		
	}
	
}
