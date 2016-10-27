package it.graphitech.objects;

import java.util.ArrayList;

public class TreeNode {

	int numRoot;
	Position position;
	
	
	String idOrigin;
	//ArrayList<String> idDestinationList;
	String prev_id_node;
	String prev_id_TreeNode;
	//ArrayList<String> prev_id_nodes;
	//ArrayList<String> next_id_nodes;
	
	double nodeMagnitude;
	
	
	String id;

	String id_leaf;
	double leaf_magnitude;

	public int getNumRoot() {
		return numRoot;
	}


	public void setNumRoot(int numRoot) {
		this.numRoot = numRoot;
	}


	

	public Position getPosition() {
		return position;
	}


	public void setPosition(Position position) {
		this.position = position;
	}


	public String getIdOrigin() {
		return idOrigin;
	}


	public void setIdOrigin(String idOrigin) {
		this.idOrigin = idOrigin;
	}


	


	public double getNodeMagnitude() {
		return nodeMagnitude;
	}


	public void setNodeMagnitude(double nodeMagnitude) {
		this.nodeMagnitude = nodeMagnitude;
	}

	public void addNodeMagnitude(double nodeMagnitude) {
		this.nodeMagnitude+= nodeMagnitude;
	}


	

	


	public String getPrev_id_node() {
		return prev_id_node;
	}


	public void setPrev_id_node(String prev_id_node) {
		this.prev_id_node = prev_id_node;
	}


	public String getPrev_id_TreeNode() {
		return prev_id_TreeNode;
	}


	public void setPrev_id_TreeNode(String prev_id_TreeNode) {
		this.prev_id_TreeNode = prev_id_TreeNode;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public boolean isRoot() {
		return (prev_id_node==null);
	}


	public String getId_leaf() {
		return id_leaf;
	}


	public void setId_leaf(String id_leaf) {
		this.id_leaf = id_leaf;
	}


	public double getLeaf_magnitude() {
		return leaf_magnitude;
	}


	public void setLeaf_magnitude(double leaf_magnitude) {
		this.leaf_magnitude = leaf_magnitude;
	}
	
	
}
