package it.graphitech.modules;



import it.graphitech.Variables;
import it.graphitech.objects.Node;
import it.graphitech.objects.Position;
import it.graphitech.objects.Vector2d;

public class ForceDirectedTechnique {

	public static void executeAlgorithm() {

		
		if(!Variables.isStable){
		//for (Node e : MiddleNodeGeneration.nodes.values()) {
			for (Node e :MiddleNodeGeneration.getNodeValues()) {
			if (((!e.isLeaf()) && (!e.isRoot() && (!e.isBlocked())))) {
				/*
				Node startingNode = 
					//	MiddleNodeGeneration.nodes.get(e.getIdOrigin());
				MiddleNodeGeneration.getNodeFromId(e.getIdOrigin());
				
				
				double distanceFromStartingNode = e.distanceFrom(startingNode
						.getPosition());
				e.setCurrentDistanceFromStartingNode(distanceFromStartingNode);
*/
				
				
				//test(e);
				
				
					
				if (Variables.enableElectrostaticForce
						//|| Variables.enableRejectForce
						) {
				//	System.out.println("neigh: "+e.getNeighbours().size());
					
					for (String idInteractingNode : e.getNeighbours()) {
						Node interactingNode = 
								MiddleNodeGeneration.getNodeFromId(idInteractingNode);
								//MiddleNodeGeneration.nodes
								//.get(idInteractingNode);

						if (interactingNode == null) {
							System.out.println("in " + e.getId()
									+ " non è stato trovato: "
									+ idInteractingNode);
							System.out.println("neigh: " + e.getNeighbours());
						}

						if (e.getId() == null) {
							System.out.println(" non è stato trovato"
									+ e.getId());
						}
						// check if the node is not the same
						//String i_id = interactingNode.getId();						
						//if (i_id.compareTo(e.getId()) != 0) {

							double distance = getDistancenMapUnit(e, interactingNode);
							
							if (e.isEnabled()) {
								if (Variables.enableElectrostaticForce) {
									checkElectrostaticForce(e, interactingNode,
											distance);
								}
							}

							
						//}else{
						//	System.out.println("interaction node == node");
						//}
					}
				}
				
				if (Variables.enableSpringForce) {
					// calculate spring force
					calculateSpringForce(e);
				}
				
				
				}
			/*
			else
				{}
*/
			}

		}else{

			for (Node e :MiddleNodeGeneration.getNodeValues()) {
				if (((!e.isLeaf()) && (!e.isRoot() && (!e.isBlocked())))) {
			
			
			if (Variables.enableSpringForce) {
				// calculate spring force
				//if(!e.isRejected()){
				calculateSpringForce(e);
				//}
			}
				}
				
			}
			
			for (Node e : MiddleNodeGeneration.getNodeValues()) {
				updateNode(e);
			}
			
			for (Node e :MiddleNodeGeneration.getNodeValues()) {
				if (((!e.isLeaf()) && (!e.isRoot() && (!e.isBlocked())))) {
			
			for (Node interactingNode : MiddleNodeGeneration.leafNodes) {
			
			
				double distance = getDistancenMapUnit(e, interactingNode);
				
				distance=distance-(Variables.returnThickness(e.getNodeMagnitude())/(2*Variables.mapUnitInMeters));
		//		double minRadius=Variables.leafNodeMinRadius;
		//		double maxRadius=Variables.leafNodeMaxRadius;
			
			//double thickness = getValue(interactingNode.getNodeMagnitude(),Operations.minLeafNodeFlowMagnitude,Operations.maxLeafNodeFlowMagnitude,minRadius, maxRadius);
			double thickness = Variables.returnNodeLeafThickness(interactingNode.getNodeMagnitude());
				
				checkRejectForce(e, interactingNode,
						(Variables.rejectBufferInMeters+thickness)/Variables.mapUnitInMeters,
						distance);
			}
			
				}
			}
			
		
		}

		// update node position
		//for (Node e : MiddleNodeGeneration.nodes.values()) {
		for (Node e : MiddleNodeGeneration.getNodeValues()) {
			Variables.prev_total_energy = Variables.total_energy;
			Variables.total_energy += e.getDisplacement().length();
			Variables.total_electr_force += e.getElectrostaticForce().length();
			Variables.total_repulsive_force += e.getRepulsiveForce().length();
			Variables.total_stress_force += e.getSpringForce().length();
			updateNode(e);

		}

	}
/*
	private static void test(Node n){
		
		Position p = new Position(70, 30);
		
double distance = n.getPosition().calculateDistance(p);
		
		distance =  distance/Variables.maxDistance;
		
		if(n.getIndex()==n.getMaxIndex()/2){
	Vector2d electrostatic_force = new Vector2d(
					n.getPosition(), p);

			if (electrostatic_force.length() > 0.001) {
				electrostatic_force.normalize();
				double factor = (Variables.electrostatic_force_factor)
						/ distance;
	
				//electrostatic_force.scale(factor*Variables.lengthMiddleSegment);
				electrostatic_force.scale(factor);
				
				n.addDisplacement(electrostatic_force);
				n.addElectrostaticForce(electrostatic_force);

			}
		
		}
		
	}
	*/
	private static boolean predNodesAreTheSame(Node n1, Node n2) {

		if (n2.isMiddleNode()) {
			//Node p1 = MiddleNodeGeneration.nodes.get(n1.getPrev_id_node());
			Node p1 = MiddleNodeGeneration.getNodeFromId(n1.getPrev_id_node());
			//Node p2 = MiddleNodeGeneration.nodes.get(n2.getPrev_id_node());
			Node p2 = MiddleNodeGeneration.getNodeFromId(n2.getPrev_id_node());
			return (p1.getId().compareTo(p2.getId()) == 0);
		}
		return false;
	}


	private static void updateNode(Node node) {
		node.update();

	}

	//return distance not in meters
	private static double getDistancenMapUnit(Node node1, Node node2) {
		Position p1= node1.getPosition();
		Position p2= node2.getPosition();
		double distance = p1.calculateDistance(p2);
		
	
		
		//return distance;
		return (distance/Variables.mapUnitInMeters);
	}

	private static void checkRejectForce(Node targetNode, Node interactingNode,
			double radius, double distance) {

		if (interactingNode.isLeaf()) {
			{
				//if (distance <= radius) {
				if (distance <= radius) {
					
					//calculateRejectForce(targetNode, interactingNode, distance);
					calculateRejectForce(targetNode, interactingNode, radius-distance);
					targetNode.setRejected(true);
				}
			}
		}
	}

	private static void checkElectrostaticForce(Node targetNode,
			Node interactingNode, double distance) {

		// if(n.isMiddleNode()){
		if (!interactingNode.isLeaf()) {
				
				
			// if the origin is not the same OR the destination is different
			if (((targetNode.getIdOrigin() != interactingNode.getIdOrigin()) ||

			(interactingNode.getIdDestination().compareTo(
					targetNode.getIdDestination()) != 0))) {

				// if (predNodesAreInTheSamePosition(targetNode,
				if (predNodesAreTheSame(targetNode, interactingNode)) {

					/*
					double angle = calculateAngle(
					
									MiddleNodeGeneration.getNodeFromId(targetNode.getPrev_id_node()
									).getPosition(),
							targetNode.getPosition(),

							interactingNode.getPosition());
				
					if (angle > 180) {
						angle = 360 - angle;
					}
					*/
				//	if(angle<40)
					{
						
						calculateElectrostaticForce(targetNode,
								interactingNode, distance);
					}
				}
			}
			}

		

	}

	private static double calculateAngle(Position p1, Position p2, Position p3) {
		double l1x = p2.getX() - p1.getX();
		double l1y = p2.getY() - p1.getY();
		double l2x = p3.getX() - p1.getX();
		double l2y = p3.getY() - p1.getY();
		double ang1 = Math.atan2(l1y, l1x);
		double ang2 = Math.atan2(l2y, l2x);

		return Math.abs(Math.toDegrees(ang2 - ang1));
	}

	private static void calculateRejectForce(Node targetNode,
			Node interactingNode, double distance) {

		Vector2d reject_force = new Vector2d(targetNode.getPosition(),
				interactingNode.getPosition());

		//if (reject_force.length() > 0.001) {
			reject_force.normalize();

			//double factor = -(Variables.reject_force_factor) / distance;
			double factor = -distance;
			
			reject_force.scale(factor
					//*Variables.lengthMiddleSegment
					);
			targetNode.setRepulsiveForce(reject_force);
			targetNode.addDisplacement(reject_force);

		//}
	}

	private static void calculateElectrostaticForce(Node targetNode,
			Node interactingNode, double distance) {

		double magn = targetNode.getNodeMagnitude();
		double magn2 = interactingNode.getNodeMagnitude();
		if(distance<(Variables.attractionRadiusInMeters/Variables.mapUnitInMeters)){
	//	if (magn2 >= magn) {
			if (true) {
			Vector2d electrostatic_force = new Vector2d(
					targetNode.getPosition(), interactingNode.getPosition());

			//double rad = (Variables.attractionRadius*Variables.unitFactor)/Variables.maxDistance;
			//double unitPerIteration = (Variables.electrostatic_force_factor*Variables.unitFactor)/Variables.maxDistance;
			
			
			if(distance<1){				
				targetNode.setShouldBeRemoved(true);
				distance=1;
				if(targetNode.getIndex()==1){
			}
			}
			
			
			
		
			/*
			System.out.println("distance "+distance);
			System.out.println("rad: "+rad);
			System.out.println("unitPerIteration: "+unitPerIteration);
			*/
		//	if (electrostatic_force.length() > 0.001) {
				electrostatic_force.normalize();
				double factor = Variables.electrostatic_force_factor/distance;

			//	double factor = targetNode.getSpringForce().length();
				electrostatic_force.scale(factor);
			
				
				electrostatic_force.scale(magn2/(magn2+magn));
						//System.out.println("factor2: "+factor*Variables.lengthMiddleSegment);
				
			
/*
				electrostatic_force.normalize();		
				double factor = targetNode.getSpringForce().length();					
				electrostatic_force.scale(factor);
*/
				
			
				
				targetNode.addDisplacement(electrostatic_force);
				targetNode.addElectrostaticForce(electrostatic_force);
		}
			//}
				
				
			}	
				
				
		
	}

	
/*
	private static void calculateElectrostaticForce_old(Node targetNode,
			Node interactingNode, double distance) {

		double magn = targetNode.getNodeMagnitude();
		double magn2 = interactingNode.getNodeMagnitude();

		if (magn2 >= magn) {
			Vector2d electrostatic_force = new Vector2d(
					targetNode.getPosition(), interactingNode.getPosition());

		//	if (electrostatic_force.length() > 0.001) {
				electrostatic_force.normalize();
				double factor = (Variables.unitFactor*Variables.electrostatic_force_factor)
						/ distance;

				electrostatic_force.scale(factor);
			
		//	System.out.println("electrostatic_force: "+electrostatic_force.length());
				//electrostatic_force.scale(factor*Variables.lengthMiddleSegment);
				
				//System.out.println("factor2: "+factor*Variables.lengthMiddleSegment);
				
				targetNode.addDisplacement(electrostatic_force);
				targetNode.addElectrostaticForce(electrostatic_force);

			//}
		}
	}
*/
	/*
	private static void calculateSpringForce_old(Node targetNode) {

		Node prev_node = 
				//MiddleNodeGeneration.nodes.get(targetNode.getPrev_id_node());
		MiddleNodeGeneration.getNodeFromId(targetNode.getPrev_id_node());
		// vettore della forza con nodo prec
		Vector2d prev_spring_force = new Vector2d(targetNode.getPosition(),
				prev_node.getPosition());
					//prev_spring_force.scale(1.d/Variables.lengthMiddleSegment);
		
		int numNextNodes = targetNode.getNext_id_nodes().size();

		// moltiplico - la forza sul il pred node - con il num di next nodes
		Vector2d next_avg_spring_force = new Vector2d();

		double totLength = targetNode.getNodeMagnitude();

		for (String nextId : targetNode.getNext_id_nodes()) {
			Node next_node = 
					//MiddleNodeGeneration.nodes.get(nextId);
			MiddleNodeGeneration.getNodeFromId(nextId);
			Vector2d next_spring_force = new Vector2d(targetNode.getPosition(),
					next_node.getPosition());
						//next_spring_force.scale(1.d/Variables.lengthMiddleSegment);
			// se i next nodes sono 2 o più -> divido -forza col next node- con
			// -la somma delle forze sui next nodes-
			if (numNextNodes > 1) {
				double nextNodeforce = next_node.getNodeMagnitude()	;
				double factor = nextNodeforce / totLength;

				
				
				
				next_spring_force.scale(factor);
			}

			next_avg_spring_force.add(next_spring_force);
		}

		// aggiungo la forza del nodo pred con la forza di tutti i nodo next
		prev_spring_force.add(next_avg_spring_force);		
		//System.out.println("spring force (a): "+prev_spring_force.length());
		//prev_spring_force.scale(Variables.spring_force_factor);
						prev_spring_force.scale(Variables.spring_force_factor);
			//			System.out.println("spring force (b): "+prev_spring_force.length());
		//aggiungo questo
						//prev_spring_force.scale(1/Variables.maxDistance);
						//prev_spring_force.scale(1/Variables.lengthMiddleSegment);
						
					
		//if (prev_spring_force.length() > Variables.minSpringForce) {
			//System.out.println("ok");
			//prev_spring_force.scale(Variables.lengthMiddleSegment);
			
			
		//	System.out.println("spring force (c): "+prev_spring_force.length());
			
			targetNode.setSpringForce(prev_spring_force);
			targetNode.addDisplacement(prev_spring_force);
		//}
		
	}
	*/
	private static void calculateSpringForce(Node targetNode) {

		Node prev_node = MiddleNodeGeneration.getNodeFromId(targetNode.getPrev_id_node());
		// vettore della forza con nodo prec
		Vector2d prev_spring_force = new Vector2d(targetNode.getPosition(),
				prev_node.getPosition());
					//prev_spring_force.scale(1.d/Variables.lengthMiddleSegment);
		prev_spring_force.normalize();
		double distance_prev = getDistancenMapUnit(targetNode, prev_node);
		prev_spring_force.scale(distance_prev);
		
		int numNextNodes = targetNode.getNext_id_nodes().size();

		// moltiplico - la forza sul il pred node - con il num di next nodes
		Vector2d next_avg_spring_force = new Vector2d();

		double totLength = targetNode.getNodeMagnitude();

		for (String nextId : targetNode.getNext_id_nodes()) {
			Node next_node = MiddleNodeGeneration.getNodeFromId(nextId);
			Vector2d next_spring_force = new Vector2d(targetNode.getPosition(),
					next_node.getPosition());
			next_spring_force.normalize();
			double distance_next = getDistancenMapUnit(targetNode, next_node);
			//System.out.println("distance_next: "+distance_next);
			next_spring_force.scale(distance_next);
						//next_spring_force.scale(1.d/Variables.lengthMiddleSegment);
			// se i next nodes sono 2 o più -> divido -forza col next node- con
			// -la somma delle forze sui next nodes-
			if (numNextNodes > 1) {
				double nextNodeforce = next_node.getNodeMagnitude()	;
				double factor = nextNodeforce / totLength;

				
				
				
				next_spring_force.scale(factor);
			}

			next_avg_spring_force.add(next_spring_force);
		}

		// aggiungo la forza del nodo pred con la forza di tutti i nodo next
		prev_spring_force.add(next_avg_spring_force);		
		//prev_spring_force.scale(Variables.spring_force_factor);
						//prev_spring_force.scale(Variables.spring_force_factor);
			//aggiungo questo
						//prev_spring_force.scale(1/Variables.maxDistance);
						//prev_spring_force.scale(1/Variables.lengthMiddleSegment);
						//prev_spring_force.scale(Variables.numMiddleNodesInMaxFlow);
						
					
		
		//altrimenti va dalla parte opposta (<0.5)
		prev_spring_force.scale(Variables.spring_force_factor);
		
		prev_spring_force.scale(Variables.numMiddleNodesInMaxFlow/30.0);
			
		
	//	if(targetNode.getElectrostaticForce().length()>0){
		
		if(prev_spring_force.length()>Variables.elasticFactor){
		
			//System.out.println("ok");
			targetNode.setSpringForce(prev_spring_force);
			targetNode.addDisplacement(prev_spring_force);
			
		}
		
	
	}
	
	
	private static void calculateSimpleSpringForce(Node targetNode) {

		Node prev_node = MiddleNodeGeneration.getNodeFromId(targetNode.getPrev_id_node());
		// vettore della forza con nodo prec
		Vector2d prev_spring_force = new Vector2d(targetNode.getPosition(),
				prev_node.getPosition());
					//prev_spring_force.scale(1.d/Variables.lengthMiddleSegment);
		prev_spring_force.normalize();
		double distance_prev = getDistancenMapUnit(targetNode, prev_node);
		prev_spring_force.scale(distance_prev);
		
		int numNextNodes = targetNode.getNext_id_nodes().size();

		// moltiplico - la forza sul il pred node - con il num di next nodes
		Vector2d next_avg_spring_force = new Vector2d();

		double totLength = targetNode.getNodeMagnitude();

		System.out.println("-----NODE-----");
		
		for (String nextId : targetNode.getNext_id_nodes()) {
			Node next_node = MiddleNodeGeneration.getNodeFromId(nextId);
			Vector2d next_spring_force = new Vector2d(targetNode.getPosition(),
					next_node.getPosition());
			next_spring_force.normalize();
			double distance_next = getDistancenMapUnit(targetNode, next_node);
			//System.out.println("distance_next: "+distance_next);
			next_spring_force.scale(distance_next);
						//next_spring_force.scale(1.d/Variables.lengthMiddleSegment);
			// se i next nodes sono 2 o più -> divido -forza col next node- con
			// -la somma delle forze sui next nodes-
			if (numNextNodes > 1) {
				//double nextNodeforce = next_node.getNodeMagnitude()	;
				double factor = 1.0 / numNextNodes;

				//double nextNodeforce = next_node.getNodeMagnitude()	;
				//double factor = nextNodeforce / totLength;
				
				System.out.println("FACTOR: "+factor);
				
				next_spring_force.scale(factor);
			}

			next_avg_spring_force.add(next_spring_force);
		}

		// aggiungo la forza del nodo pred con la forza di tutti i nodo next
		prev_spring_force.add(next_avg_spring_force);		
		//prev_spring_force.scale(Variables.spring_force_factor);
						//prev_spring_force.scale(Variables.spring_force_factor);
			//aggiungo questo
						//prev_spring_force.scale(1/Variables.maxDistance);
						//prev_spring_force.scale(1/Variables.lengthMiddleSegment);
						//prev_spring_force.scale(Variables.numMiddleNodesInMaxFlow);
						
					
		
		//altrimenti va dalla parte opposta (<0.5)
		prev_spring_force.scale(Variables.spring_force_factor);
		
		prev_spring_force.scale(Variables.numMiddleNodesInMaxFlow/30.0);
			
		
	//	if(targetNode.getElectrostaticForce().length()>0){
		
		if(prev_spring_force.length()>Variables.elasticFactor){
		
			//System.out.println("ok");
			targetNode.setSpringForce(prev_spring_force);
			targetNode.addDisplacement(prev_spring_force);
			
		}
		
	
	}
}
