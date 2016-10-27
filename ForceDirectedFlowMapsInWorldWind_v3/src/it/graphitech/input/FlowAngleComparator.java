package it.graphitech.input;
import java.util.Comparator;


public class FlowAngleComparator implements Comparator<FlowSource> {
    @Override
    public int compare(FlowSource o1, FlowSource o2) {
    	//System.out.println("aaa");
    	//System.out.println(o2.angleFromOrigin);
    	//System.out.println(o1.angleFromOrigin);
    	
    	int compareOrigin = o1.getOrigin().compareTo(o2.getOrigin());    	
    	if(compareOrigin!=0) return compareOrigin;
    	
    	int compareAngle= Double.compare(o1.angleFromOrigin,o2.angleFromOrigin);
    	//if(compareAngle!=0) return compareAngle;
    	return compareAngle;
    	/*
    }else if(o1.getOrigin().compareTo(o2.getOrigin())==1){
    		return 1;
    	}
    	else 
    	{    		
    	if(o2.angleFromOrigin>=o1.angleFromOrigin){
    		return 1;
    	}else if(o2.angleFromOrigin<o1.angleFromOrigin){
    		return -1;
    	}
    	
    	else return 0;
        //return Integer.signum((int)(o2.angleFromOrigin-o1.angleFromOrigin));
    }
    */
    }
}