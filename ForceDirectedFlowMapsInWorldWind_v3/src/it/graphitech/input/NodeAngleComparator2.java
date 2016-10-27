package it.graphitech.input;
import it.graphitech.objects.Node;

import java.util.Comparator;


public class NodeAngleComparator2 implements Comparator<Node> {
    @Override
    public int compare(Node n1, Node n2) {
    	
    	
    	int idOriginCompare = n1.getIdOrigin().compareTo(n2.getIdOrigin());    	
    	if(idOriginCompare!=0) return idOriginCompare;
    	
    	int indexCompare = Integer.compare(n1.getIndex(), n2.getIndex());
    	if(indexCompare!=0) return indexCompare;
    	
    	int angleIndexCompare = Integer.compare(n2.getUpAngleIndex(), n1.getUpAngleIndex());
    	return angleIndexCompare; 
    	
    	/*
    	if(n1.getIndex()>n2.getIndex()){    		
    		return 1;
    	}else if(n1.getIndex()<n2.getIndex()){
    		return -1;
    	}else 
    		if(n1.getUpAngleIndex()>n2.getUpAngleIndex()){
    			return -1;
    		}else{
    			return 1;
    		}
    		*/
       
    }
}