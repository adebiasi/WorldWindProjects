package it.graphitech.modules;

import it.graphitech.Operations;
import it.graphitech.Variables;
import it.graphitech.objects.Node;
import it.graphitech.objects.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UpdateTreeStructure {

	static HashMap<String, String> nodesToRemoveWithMergeNodes = new HashMap<>();

	/*
	 * private static String getSubstituteNode(String id){
	 * 
	 * String res = id ; while(nodesToRemoveWithMergeNodes.containsKey(res)){
	 * String newRes=nodesToRemoveWithMergeNodes.get(res);
	 * //System.out.println("sostituisco: "+res+" con "+newRes); res=newRes; }
	 * return res; }
	 */
//	private static boolean isInRemoveList(String id) {
//		return nodesToRemoveWithMergeNodes.containsKey(id);
//	}
//
//	private static int countNumberThatShouldBeRemoved() {
//
//		int num = 0;
//
//		for (Node n : MiddleNodeGeneration.getNodeValues()) {
//			if (n.isShouldBeRemoved()) {
//				num++;
//			}
//		}
//
//		return num;
//	}

	// controllo che ogni nodo non abbia una qualche relazione con un nodod da
	// eliminare (nodo precedente, nodi successivi, nodi vicini)
//	public static void UpdateRemovedNodes() {
//
//		System.out.println("num nodi nella lista nera: "
//				+ nodesToRemoveWithMergeNodes.size());
//		int i = 0;
//		for (Node n : MiddleNodeGeneration.getNodeValues()) {
//			System.out.println("controllo nodo " + i);
//			/*
//			 * System.out.println("---------------------");
//			 * System.out.println("is middle: "+n.isMiddleNode());
//			 * System.out.println("is leaf: "+n.isLeaf());
//			 * System.out.println("is root: "+n.isRoot());
//			 * System.out.println("---------------------");
//			 */
//			// System.out.println("il nodo: "+n.getId()+" ha questi vicini:"+n.getNeighbours());
//
//			// controllo il nodo precedente
//
//			String idPrev = n.getPrev_id_node();
//			System.out.println("id prev è : " + idPrev);
//			if (isInRemoveList(idPrev)) {
//				System.out.println("devo sostituire prev");
//				String subId = getSubstituteNode(idPrev);
//				n.setPrev_id_node(subId);
//			}
//			System.out.println("id prev controllato");
//
//			// controllo i nodi successivi
//			for (Object nextObj : n.getNext_id_nodes().toArray()) {
//				System.out.println("size Next: " + n.getNext_id_nodes().size());
//				String idNext = (String) nextObj;
//				if (isInRemoveList(idNext)) {
//					n.getNext_id_nodes().remove(idNext);
//					String subId = getSubstituteNode(idNext);
//					n.getNext_id_nodes().add(subId);
//				}
//			}
//
//			// controllo i nodi vicini
//			if (n.isMiddleNode()) {
//				// System.out.println("size: "+n.getNeighbours().size());
//
//				Object[] listNeigh = n.getNeighbours().toArray();
//				System.out.println("size Neighbours: "
//						+ n.getNeighbours().size());
//
//				if (listNeigh.length >= 1) {
//					String idN1 = (String) listNeigh[0];
//					// System.out.println("il vicino è: "+idN1);
//					if (isInRemoveList(idN1)) {
//
//						String subId = getSubstituteNode(idN1);
//						// System.out.println("sostituisco : "+idN1+" con "+subId);
//						n.substituteNeighbours(idN1, subId);
//					}
//				}
//				if (listNeigh.length >= 2) {
//					String idN2 = (String) listNeigh[1];
//					// System.out.println("il vicino è: "+idN2);
//					if (isInRemoveList(idN2)) {
//						String subId = getSubstituteNode(idN2);
//						// System.out.println("sostituisco : "+idN2+" con "+subId);
//						n.substituteNeighbours(idN2, subId);
//					}
//				}
//
//			}
//			i++;
//		}
//		System.out.println("controllati tutti");
//
//	}

	public static void removeNodesAndUpdateNeighbours(
	// HashMap<String, Node> nodes
	) {
		Set<String> idToRemove = new HashSet<>();

		// for(Node n: nodes.values()){
		for (Node n : MiddleNodeGeneration.getNodeValues()) {
			if (n.isRemove()) {
				// if(n.getIndex()==1){
				// if(n.getId().compareTo("-120.57469222239071 , 37.83135110931463 - -120.823074866985 , 37.824644286057")==0){
				// System.out.println("aggiungo "+n.getId()+
				// " a lista di nodi da rimuovere");
				// }
				// }
				idToRemove.add(n.getId());
			}

			/*
			if (n.getNewNeighbours() != null) {
				// System.out.println("neigh: "+n.getNeighbours().size());
				// System.out.println("new neigh: "+n.getNewNeighbours().size());
				Set<String> nb = new HashSet<>();
				nb.addAll(n.getNewNeighbours());
				n.setNeighbours(nb);
				n.setNewNeighbours(null);
				// System.out.println("neigh2: "+n.getNeighbours().size());
			}
			*/
			
		}

		// System.out.println("n. nodes: "+nodes.size());
		// System.out.println(idToRemove.size()+" nodes removed");

		for (String id : idToRemove) {
			// if(id.compareTo("-120.57469222239071 , 37.83135110931463 - -120.823074866985 , 37.824644286057")==0){
			// System.out.println("rimuovo");
			// System.out.println("num nodes prima: "+MiddleNodeGeneration.getNumNodes());
			// }
			// nodes.remove(id);
			//System.out.println("rimuovo effettivamente il nodo");
			MiddleNodeGeneration.removeNode(id);

			// if(id.compareTo("-120.57469222239071 , 37.83135110931463 - -120.823074866985 , 37.824644286057")==0){
			// System.out.println("num nodes dopo: "+MiddleNodeGeneration.getNumNodes());
			// }

		}
		// System.out.println("n. nodes: "+nodes.size());
	}

	public static void checkAggregation(
	// HashMap<String, Node> nodes
	) {
		nodesToRemoveWithMergeNodes = new HashMap<String, String>();
		// System.out.println("iteration");
		
		// create a neighbour list equal to the old one - at every middle node
		// complexity: 0(n)
		
		
		//createNewNeighbours();
		
		
		/*
		 * for (Object key : nodes.keySet().toArray()) { Node e =
		 * MiddleNodeGeneration.getNodeFromId((String)key);
		 */
		for (Node e : MiddleNodeGeneration.getNodeValues()) {

			if (e.isMiddleNode()) {
				if (!e.isRemove()) {
					
					Node neigh =getNodeToRemove(e);
					if(neigh!=null){
					/*
					// each node has at most 2 neighbours
					if (e.getNeighbours() != null) {
						for (String idNeigh : e.getNeighbours()) {

							// Node neigh =
							// MiddleNodeGeneration.nodes.get(idNeigh);
							Node neigh = MiddleNodeGeneration
									.getNodeFromId(idNeigh);

							if (!neigh.isRemove()) {
								if (hasGreaterMagnitude(e, neigh)) {
									if (nodesAreInTheSamePosition(e, neigh)) {
									System.out.println("vicini ma non hanno gli stessi pred");
											// if(nodesHaveNearIndex(e, neigh)){
												if (predNodesAreTheSame(e, neigh)) {
*/
							
												// elimino "neigh"!!!!
						/*						
						System.out.println("metto: "
														+ e.getId()
														+ " al posto di:"
														+ neigh.getId());
							*/			
												// setToRemoveNode(neigh.getId());
												setToRemoveNode(neigh);

						
												// add the neighbours of "neigh"
												// to the neighbours of "e"
												addNeighboursToNode(e, neigh);

												// update position
												updatePositionToNode(neigh, e);
												// add magnitude
												addMagnitudeToNode(neigh, e);
												// aggiungo a "e" tutti i next
												// di "neigh"
												addNextNodeToNode(neigh, e);

												substituteRemoveNodeInNodesConnections(
														neigh, e.getId());

											}
											/*
											 * else{ System.out.println(
											 * "nodi sono vicini ma non hanno lo stesso pred"
											 * ); }
											 */
										}
									}

								}
								/*
								 * else{ System.out.println("un nodo"+e.getId()+
								 * " ha un vicino che dovrebbe essere stato rimosso: "
								 * +neigh.getId()); }
								 */
							}
					//	}
				//	}
				
	//		}

	//	}
	//}

	private static Node getNodeToRemove(Node e){
		if (e.getNeighbours() != null) {
			for (String idNeigh : e.getNeighbours()) {

				// Node neigh =
				// MiddleNodeGeneration.nodes.get(idNeigh);
				Node neigh = MiddleNodeGeneration
						.getNodeFromId(idNeigh);

				if (!neigh.isRemove()) {
					if (hasGreaterMagnitude(e, neigh)) {
						if (nodesAreInTheSamePosition(e, neigh)) {
						//System.out.println("vicini ma non hanno gli stessi pred");
								// if(nodesHaveNearIndex(e, neigh)){
									if (predNodesAreTheSame(e, neigh)) {
										return neigh;
									}
						}
					}
				}
			}
		}
		return null;
	}
	
	private static void substituteRemoveNodeInNodesConnections(
			Node nodeToRemove, String idNode) {

		String idToRemove = nodeToRemove.getId();

		String prevId = nodeToRemove.getPrev_id_node();
		Node prevNode = MiddleNodeGeneration.getNodeFromId(prevId);
		prevNode.getNext_id_nodes().remove(idToRemove);
		prevNode.getNext_id_nodes().add(idNode);
/*
		for (String idNeighbour : nodeToRemove.getNewNeighbours()) {
			if (idNode.compareTo(idNeighbour) != 0) {
				Node neigNode = MiddleNodeGeneration.getNodeFromId(idNeighbour);
				neigNode.getNewNeighbours().remove(idToRemove);
				neigNode.getNewNeighbours().add(idNode);
			}
		}
*/
		
		for (String idNeighbour : nodeToRemove.getNeighbours()) {
			if (idNode.compareTo(idNeighbour) != 0) {
				Node neigNode = MiddleNodeGeneration.getNodeFromId(idNeighbour);
				neigNode.getNeighbours().remove(idToRemove);
				neigNode.getNeighbours().add(idNode);
			}
		}
		
		for (String idNext : nodeToRemove.getNext_id_nodes()) {
			Node nextNode = MiddleNodeGeneration.getNodeFromId(idNext);
			nextNode.setPrev_id_node(idNode);
		}
	}

	private static void addNeighboursToNode(Node e, Node neigh) {
		
		/*
		for (String idNeighbour : neigh.getNewNeighbours()) {
			if (e.getId().compareTo(idNeighbour) != 0) {
				e.getNewNeighbours().add(idNeighbour);
			}
		}
		e.getNewNeighbours().remove(neigh.getId());
		*/
		
		
		for (String idNeighbour : neigh.getNeighbours()) {
			if (e.getId().compareTo(idNeighbour) != 0) {
				e.getNeighbours().add(idNeighbour);
			}
		}
		e.getNeighbours().remove(neigh.getId());
		
	}

	private static void setToRemoveNode(Node n) {

	//	System.out.println("setto come REMOVE: " + n.getId());
		n.setRemove(true);

	}

	private static void createNewNeighbours() {

		for (Node n : MiddleNodeGeneration.getNodeValues()) {
			if (n.isMiddleNode()) {

				Set<String> newN = new HashSet<String>();
				newN.addAll(n.getNeighbours());

				// MiddleNodeGeneration.getNodeFromId(n.getId()).setNewNeighbours(newN);
				n.setNewNeighbours(newN);
			}
		}
	}

	private static void addNextNodeToNode(Node oldNode, Node newNode) {
		newNode.addNext_id_nodes(oldNode.getNext_id_nodes());
	}

//	private static void substituteNeighbours_NodeToNext_NodeToPredecessors(
//			String oldId, String newId) {
//
//		for (Node n : MiddleNodeGeneration.getNodeValues()) {
//			if (!n.isRoot()) {
//				if (n.getPrev_id_node().compareTo(oldId) == 0) {
//					n.setPrev_id_node(newId);
//				}
//
//				if (n.getNext_id_nodes().contains(oldId)) {
//					n.getNext_id_nodes().remove(oldId);
//					n.getNext_id_nodes().add(newId);
//				}
//
//			}
//
//			if (n.isMiddleNode()) {
//
//				if (n.getNewNeighbours().contains(oldId)) {
//					// MiddleNodeGeneration.nodes.get(n.getId()).substituteNewNeighbours(oldId,
//					// newId);
//					n.substituteNewNeighbours(oldId, newId);
//				}
//
//				if (n.getId().compareTo(newId) == 0) {
//					n.getNewNeighbours().remove(newId);
//				}
//			}
//		}
//	}

	/*
	 * private static void substituteNeighbours(String oldId, String newId){
	 * 
	 * //System.out.println("----old id to remove: "+oldId);
	 * 
	 * //for(Node n : MiddleNodeGeneration.nodes.values()){ for(Node n :
	 * MiddleNodeGeneration.getNodeValues()){ if(n.isMiddleNode()){
	 * 
	 * if(n.getNewNeighbours().contains(oldId)){
	 * //MiddleNodeGeneration.nodes.get
	 * (n.getId()).substituteNewNeighbours(oldId, newId);
	 * n.substituteNewNeighbours(oldId, newId); }
	 * 
	 * if(n.getId().compareTo(newId)==0){ n.getNewNeighbours().remove(newId); }
	 * } } } //
	 * 
	 * 
	 * 
	 * 
	 * private static void substituteNodeToNext(String oldId, String newId){
	 * //for(Node n : MiddleNodeGeneration.nodes.values()){ for(Node n :
	 * MiddleNodeGeneration.getNodeValues()){ if(!n.isRoot()){
	 * if(n.getNext_id_nodes().contains(oldId)){
	 * n.getNext_id_nodes().remove(oldId); n.getNext_id_nodes().add(newId); }
	 * 
	 * } } }
	 * 
	 * private static void substituteNodeToPredecessors(String oldId, String
	 * newId){ //for(Node n : MiddleNodeGeneration.nodes.values()){ for(Node n :
	 * MiddleNodeGeneration.getNodeValues()){ if(!n.isRoot()){
	 * if(n.getPrev_id_node().compareTo(oldId)==0){ n.setPrev_id_node(newId); }
	 * } } }
	 */

	private static void addMagnitudeToNode(Node oldNode, Node newNode) {

		newNode.addNodeMagnitude(oldNode.getNodeMagnitude());
		newNode.addRGBNodeMagnitude(oldNode.getNodeMagnitude_R(),
				oldNode.getNodeMagnitude_G(), oldNode.getNodeMagnitude_B());

		double flow_R_perc = oldNode.getNodeMagnitude_R()/oldNode.getNodeMagnitude();
		double flow_G_perc = oldNode.getNodeMagnitude_G()/oldNode.getNodeMagnitude();
		double flow_B_perc = oldNode.getNodeMagnitude_B()/oldNode.getNodeMagnitude();
		
		
		if (newNode.getNodeMagnitude() > Operations.maxFlowMagnitude) {
			Operations.maxFlowMagnitude = newNode.getNodeMagnitude();
		}
		if (newNode.getNodeMagnitude() < Operations.minFlowMagnitude) {
			Operations.minFlowMagnitude = newNode.getNodeMagnitude();
		}
		
		if (newNode.getNodeMagnitude_R() > Operations.maxFlow_R) {
			Operations.maxFlow_R = newNode.getNodeMagnitude_R();
		}
		if (newNode.getNodeMagnitude_R() < Operations.minFlow_R) {
			Operations.minFlow_R = newNode.getNodeMagnitude_R();
		}
		
		if (newNode.getNodeMagnitude_G() > Operations.maxFlow_G) {
			Operations.maxFlow_G = newNode.getNodeMagnitude_G();
		}
		if (newNode.getNodeMagnitude_G() < Operations.minFlow_G) {
			Operations.minFlow_G = newNode.getNodeMagnitude_G();
		}
		
		if (newNode.getNodeMagnitude_B() > Operations.maxFlow_B) {
			Operations.maxFlow_B = newNode.getNodeMagnitude_B();
		}
		if (newNode.getNodeMagnitude_B() < Operations.minFlow_B) {
			Operations.minFlow_B = newNode.getNodeMagnitude_B();
		}

		
		
		if (flow_R_perc > Operations.maxFlow_R_perc) {
			Operations.maxFlow_R_perc = flow_R_perc;
		}
		if (flow_R_perc < Operations.minFlow_R_perc) {
			Operations.minFlow_R_perc = flow_R_perc;
		}
		
		if (flow_G_perc > Operations.maxFlow_G_perc) {
			Operations.maxFlow_G_perc = flow_G_perc;
		}
		if (flow_G_perc < Operations.minFlow_G_perc) {
			Operations.minFlow_G_perc = flow_G_perc;
		}
		
		if (flow_B_perc > Operations.maxFlow_B_perc) {
			Operations.maxFlow_B_perc = flow_B_perc;
		}
		if (flow_B_perc < Operations.minFlow_B_perc) {
			Operations.minFlow_B_perc = flow_B_perc;
		}

		
	}

	private static void updatePositionToNode(Node oldNode, Node newNode) {

		Position p1 = newNode.getPosition();
		Position p2 = oldNode.getPosition();

		double updX = (p1.getX() + p2.getX()) / 2;
		double updY = (p1.getY() + p2.getY()) / 2;

		Position updP = new Position(updX, updY);

		newNode.setPosition(updP);
	}

//	private static void updateAngleIndexes(Node n1, Node n2) {
//
//		int abs =
//		// Math.abs
//		(n2.getDownAngleIndex() - n1.getUpAngleIndex());
//		int abs2 =
//		// Math.abs
//		(n1.getDownAngleIndex() - n2.getUpAngleIndex());
//		if ((abs == 1) || (abs == n2.getMaxAngleIndex())) {
//			n2.setDownAngleIndex(n1.getDownAngleIndex());
//		} else if ((abs2 == 1) || (abs2 == n2.getMaxAngleIndex())) {
//			n2.setUpAngleIndex(n1.getUpAngleIndex());
//		} else {
//			System.out.println("impossibile");
//		}
//
//		// System.out.println("risulta "+n2.getUpAngleIndex()+" -- "+n2.getDownAngleIndex());
//
//	}

	public static boolean nodesHaveNearIndex(Node n1, Node n2) {

		if (n2.isMiddleNode()) {

			int abs =
			// Math.abs
			(n2.getDownAngleIndex() - n1.getUpAngleIndex());
			int abs2 =
			// Math.abs
			(n1.getDownAngleIndex() - n2.getUpAngleIndex());

			if ((abs == 1) || (abs2 == 1) || (abs == n1.getMaxAngleIndex())
					|| (abs2 == n2.getMaxAngleIndex())) {
				return true;
			} else {
				// System.out.println("non si possono unire "+n1.getUpAngleIndex()+" -- "+n1.getDownAngleIndex()+" con "+n2.getUpAngleIndex()+" -- "+n2.getDownAngleIndex());
				return false;
			}

		}

		return false;
	}

	private static boolean predNodesAreTheSame(Node n1, Node n2) {

		if (n2.isMiddleNode()) {
			// Node p1 = MiddleNodeGeneration.nodes.get(n1.getPrev_id_node());
			// Node p2 = MiddleNodeGeneration.nodes.get(n2.getPrev_id_node());

			Node p1 = MiddleNodeGeneration.getNodeFromId(n1.getPrev_id_node());
			Node p2 = MiddleNodeGeneration.getNodeFromId(n2.getPrev_id_node());

		//	System.out.println("p1 è da eliminare? "+p1.isRemove());
		//	System.out.println("p2 è da eliminare? "+p2.isRemove());
			
		//	System.out.println("pred di "+n1.getId()+" è "+p1.getId());
		//	System.out.println("pred di "+n2.getId()+" è "+p2.getId());
			return (p1.getId().compareTo(p2.getId()) == 0);
		}

		return false;
	}

	private static boolean nodesAreInTheSamePosition(Node n1, Node n2) {
		double distance = n1.getPosition().calculateDistance(n2.getPosition());
		return (distance < Variables.samePositionDistanceInMeters);

	}

	private static boolean hasGreaterMagnitude(Node n1, Node n2) {
		return (n1.getNodeMagnitude() >= n2.getNodeMagnitude());
	}

	public static boolean NoMoreNodesToRemove() {

		for (Node n : MiddleNodeGeneration.getNodeValues()) {
			if (n.isMiddleNode()) {
				//if (n.isShouldBeRemoved()) {
				if (n.isRemove()) {
					return false;
				}
			}
		}
		return true;

	}

	public static void printNodesToRemove() {

		for (Node n : MiddleNodeGeneration.getNodeValues()) {
			if (n.isMiddleNode()) {
				//if (n.isShouldBeRemoved()) {
				if (n.isRemove()) {
					String idPrev =n.getPrev_id_node();
					Node prev = MiddleNodeGeneration.getNodeFromId(idPrev);
					System.out.println("id: "+n.getId()+" index: "+n.getIndex()+" pred: "+prev.getId()+" index: "+prev.getIndex());
				}
			}
		}
		

	}
	
}
