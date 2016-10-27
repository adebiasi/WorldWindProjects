package it.graphitech.objects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import it.graphitech.Variables;

public class Node {

	int numRoot;
	Position pos;
	
	Position originalPos;
	
	Vector2d displacement;
	
	
	Vector2d SpringForce;
	Vector2d ElectrostaticForce;
	Vector2d repulsiveForce;
	
	String idOrigin;
	String idDestination;
	String prev_id_node;
	Set<String> next_id_nodes;
	
	double nodeMagnitude;
	double nodeMagnitude_R;
	double nodeMagnitude_G;
	double nodeMagnitude_B;

	
	double segmentLength;
	
	Set<String> neighbours;
	Set<String> newNeighbours;
	String id;
	/*
	private double initDistanceFromStartingNode;
	private double currentDistanceFromStartingNode;
	*/
	
	//è l'indice del nodo dalla root alla leaf 
	int index;
	int maxIndex;
	
	String name;
	
	boolean remove;
	
	boolean toRender=false;
	boolean isRejected = false;
	boolean isBlocked = false;
	
	boolean shouldBeRemoved = false;
	
	int upAngleIndex;
	int downAngleIndex;
	int maxAngleIndex;
	
	public Node(int index,Position pos, String idOrigin,String idDestination, double nodeMagnitude, String id,double segmentLength) {
		super();
		this.index=index;
		this.pos = pos;
		
		this.originalPos=new Position(pos.x, pos.y);
		
		this.idOrigin = idOrigin;
		this.idDestination = idDestination;
		this.nodeMagnitude = nodeMagnitude;
		this.id = id;
		this.segmentLength = segmentLength;
		displacement= new Vector2d();
		
		next_id_nodes  = new HashSet<>();
		
		SpringForce= new Vector2d();
		ElectrostaticForce= new Vector2d();
		repulsiveForce= new Vector2d();
		//initDistanceFromStartingNode=0;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Node node = (Node)obj;
		
		return (node.id==this.id);
	}
	public double getNodeMagnitude() {
		return nodeMagnitude;
	}
	public void setNodeMagnitude(double nodeMagnitude) {
		this.nodeMagnitude = nodeMagnitude;
	}
	
	public void addNodeMagnitude(double nodeMagnitude) {
		this.nodeMagnitude += nodeMagnitude;
	}

	public void addRGBNodeMagnitude(double r,double g,double b) {
		this.nodeMagnitude_R += r;
		this.nodeMagnitude_G += g;
		this.nodeMagnitude_B += b;
	}
	
	public Position getPosition(){
		return pos;
	}
	
	public void setPosition(Position xy){
		pos=xy;
	}

	
	
	public Position getOriginalPos() {
		return originalPos;
	}
	public void setOriginalPos(Position originalPos) {
		this.originalPos = originalPos;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

public  void setMagnitudeRGB(double r,double g,double b){
	
	//System.out.println("setMagnitudeRGB: "+nodeMagnitude_R+" , "+nodeMagnitude_G+" , "+nodeMagnitude_B);
	
	nodeMagnitude_R=r;
	nodeMagnitude_G=g;
	nodeMagnitude_B=b;
}



	public int getMaxIndex() {
	return maxIndex;
}
public void setMaxIndex(int maxIndex) {
	this.maxIndex = maxIndex;
}
	public double getNodeMagnitude_R() {
	return nodeMagnitude_R;
}
public double getNodeMagnitude_G() {
	return nodeMagnitude_G;
}
public double getNodeMagnitude_B() {
	return nodeMagnitude_B;
}
	public static String createId(Position nodePosition,Position originNodePosition){
		return nodePosition.x+" , "+nodePosition.y+" - "+originNodePosition.x+" , "+originNodePosition.y;
	}
	
	public boolean isRoot() {
		return (prev_id_node==null);
	}

	

	public boolean isLeaf() {
		
			return (next_id_nodes.size()==0)&!isRoot();	
				
	}

	public boolean isMiddleNode() {
		return !isLeaf()&!isRoot();
	}

	public String getIdOrigin() {
		return idOrigin;
	}

	public void setIdOrigin(String idOrigin) {
		this.idOrigin = idOrigin;
	}
	
	
	/*
	public void move(Vector2d vector){
		
		pos.move(vector);
		
	}
	*/
private void updatePosition(){
	
	
	Vector2d e = getElectrostaticForce();
	Vector2d s = getSpringForce();
	
	Vector2d tot = new Vector2d();
	tot.subtract(e);
	tot.subtract(s);
	
	
	
	//if(s.length()>0){
	/*
	if(e.length()>0){
		System.out.println("-----------------------------------------");
	System.out.println("displacement prima non in metri : "+displacement.length());	
	System.out.println("spring: "+s.length());
	System.out.println("electr: "+e.length());	
	System.out.println("tot: "+tot.length());
	System.out.println("-----------------------------------------");
	}
	*/
	/*
	if(displacement.length()>10000){
		displacement.normalize();
		displacement.scale(10000);
		System.out.println("alto displacement: "+displacement.length());
	}
	*/
	/*
	if(displacement.length()>Variables.curr_energy){
		displacement.normalize();
		displacement.scale(Variables.curr_energy);
	}
	*/
	//System.out.println("energy displacement prima : "+displacement.length());
	
	
	
	displacement.scale(Variables.mapUnitInMeters);

	
	//displacement.scale(Variables.unitMaxDistance);
	//displacement.scale(Variables.maxDistance/Variables.unitMaxDistance);
	//System.out.println("energy displacement dopo: "+displacement.length());
	/*
	if(e.length()>0){
		System.out.println("displ in meters: "+displacement);
		System.out.println("length in meters: "+displacement.length());
		System.out.println("pos prima: "+pos);
	}
	*/
	/*
	if(displacement.length()>0){
		System.out.println("displacement dopo in metri: "+displacement.length());
		}
	*/
	
		pos.moveMeters(displacement);
		/*
		if(e.length()>0){
		System.out.println("pos dopo: "+pos);
		}
		*/
		displacement=new Vector2d();
		resetForces();
	}





public Set<String> getNeighbours() {
	return neighbours;
}
public void setNeighbours(Set<String> neighbours) {
	this.neighbours = neighbours;
}

public void addNeighbours(Set<String> neighbours) {
	this.neighbours.addAll( neighbours);
}
public void update(){
	
//	System.out.println("displacement: "+displacement.length());
	
	updatePosition();
	
	
	}

////ARROTONDAMENTO CLASSICO
//public static double arrotonda(double value, int numCifreDecimali) {
//   double temp = Math.pow(10, numCifreDecimali);
//   return Math.round(value * temp) / temp;
//}
	public String getPrev_id_node() {
		return prev_id_node;
	}
	public void setPrev_id_node(String prev_id_node) {
		this.prev_id_node = prev_id_node;
	}
	public Set<String> getNext_id_nodes() {
		return next_id_nodes;
	}
	public void addNext_id_node(String next_id_node) {
		this.next_id_nodes.add(next_id_node);
	}
	
	public void addNext_id_nodes(Set<String> next_id_nodes) {
		this.next_id_nodes.addAll(next_id_nodes);
	}
	
	public Vector2d getDisplacement() {
		return displacement;
	}
	public void setDisplacement(Vector2d displacement) {
		this.displacement = displacement;
	}
	
	public void addDisplacement(Vector2d displacement) {
		this.displacement.subtract(displacement);
	}
	public double getSegmentLength() {
		return segmentLength;
	}
	public void setSegmentLength(double segmentLength) {
		this.segmentLength = segmentLength;
	}
	public String getIdDestination() {
		return idDestination;
	}
	public void setIdDestination(String idDestination) {
		this.idDestination = idDestination;
	}
	public Vector2d getSpringForce() {
		return SpringForce;
	}
	public void setSpringForce(Vector2d springForce) {
		SpringForce = springForce;
	}
	public Vector2d getRepulsiveForce() {
		return repulsiveForce;
	}
	public void setRepulsiveForce(Vector2d repulsiveForce) {
		this.repulsiveForce = repulsiveForce;
	}
	public Vector2d getElectrostaticForce() {
		return ElectrostaticForce;
	}
	public void addElectrostaticForce(Vector2d electrostaticForce) {
		//ElectrostaticForceToDraw = electrostaticForceToDraw;
		this.ElectrostaticForce.subtract(electrostaticForce);
	}
	public void resetForces() {
		//ElectrostaticForceToDraw = electrostaticForceToDraw;
		this.ElectrostaticForce=new Vector2d();
		this.SpringForce=new Vector2d();
		this.repulsiveForce=new Vector2d();
	}
	public int getIndex() {
		return index;
	}
	
	public boolean isEnabled(){
		
		return true;
	}
	
	
	public double distanceFrom(Position pos2){
		return pos.calculateDistance(pos2);
	}
	/*
	public double getInitDistanceFromStartingNode() {
		return initDistanceFromStartingNode;
	}
	public void setCurrentDistanceFromStartingNode(double currDistanceFromStartingNode) {
		this.currentDistanceFromStartingNode = currDistanceFromStartingNode;
	}
	public double getCurrentDistanceFromStartingNode() {
		return currentDistanceFromStartingNode;
	}
	public void setInitDistanceFromStartingNode(double initDistanceFromStartingNode) {
		this.initDistanceFromStartingNode = initDistanceFromStartingNode;
	}
	*/
	public int getNumRoot() {
		return numRoot;
	}
	public void setNumRoot(int numRoot) {
		this.numRoot = numRoot;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isRemove() {
		return remove;
	}
	public void setRemove(boolean remove) {
		this.remove = remove;
	}
	
	public Set<String> getNewNeighbours() {
		return newNeighbours;
	}
	public void setNewNeighbours(Set<String> neighbours) {
		this.newNeighbours = neighbours;
	}

	public void addNewNeighbours(Set<String> neighbours) {
		this.newNeighbours.addAll( neighbours);
	}
	
	public void substituteNewNeighbours(String oldId, String newId){
		newNeighbours.remove(oldId);	
		newNeighbours.add(newId);
	}
	public void substituteNeighbours(String oldId, String newId){
		neighbours.remove(oldId);	
		neighbours.add(newId);
	}
	
	public boolean isToRender() {
		return toRender;
	}
	public void setToRender(boolean toRender) {
		this.toRender = toRender;
	}
	public boolean isRejected() {
		return isRejected;
	}
	public void setRejected(boolean isRejected) {
		this.isRejected = isRejected;
	}
	public int getUpAngleIndex() {
		return upAngleIndex;
	}
	public void setUpAngleIndex(int angleIndex) {
		this.upAngleIndex = angleIndex;
	}
	public int getDownAngleIndex() {
		return downAngleIndex;
	}
	public void setDownAngleIndex(int angleIndex) {
		this.downAngleIndex = angleIndex;
	}
	public int getMaxAngleIndex() {
		return maxAngleIndex;
	}
	public void setMaxAngleIndex(int maxAngleIndex) {
		this.maxAngleIndex = maxAngleIndex;
	}
	public boolean isBlocked() {
		return isBlocked;
	}
	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}
	public boolean isShouldBeRemoved() {
		return shouldBeRemoved;
	}
	public void setShouldBeRemoved(boolean shouldBeRemoved) {
		this.shouldBeRemoved = shouldBeRemoved;
	}
	
	
}
