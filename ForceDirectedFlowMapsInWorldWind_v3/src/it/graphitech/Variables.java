package it.graphitech;

import it.graphitech.swing.RenderOptionPanel;
import it.graphitech.swing.VariablesPanel;

import java.awt.Color;

public class Variables {

	//PARAMETERS TO SET BY THE USER
	public static double samePositionDistanceInMeters;
	static public double rejectBufferInMeters;
	static public double attractionRadiusInMeters;
	
	static public double spring_force_factor = 0.1;
	//it is a threshold for the min. elastic factor
	static public double elasticFactor ;

	
		static public int numMiddleNodesInMaxFlow = 50; 
	
	
	//	static public int numMiddleNodesInMaxFlow = 120;
//	static public int numMiddleNodesInMaxFlow = 80;
//static public int numMiddleNodesInMaxFlow = 12;
	
	
	
	/////////////////////////////////////
	
	
	

/////////////////////////////////////////
		static public boolean normalizeRangeAttributes=true;
		static public boolean useDifferentAttributes=true;
	static public boolean useRYB=true;
	static public boolean waterColor=false;
	//static public int numMiddleNodesInMaxFlow = 7;
	////////////////////////////////////////////////////////
	//lengthMiddleSegment = maxFlow length / num middle nodes
	//		the unit of measure 		//
//////////////////////////////////////////////////////
	//static public double lengthMiddleSegment=30000;
		static public double lengthMiddleSegment;
	
		static public double maxDistance;
		static public double unitFactor;
		static public double mapUnitInMeters;
	//	static public double unitMaxDistance;
	//static public double distanceNeighbors = 	500000;
	//static public double distanceRejectRadius = 70000;
	
////////////////////////////////////////////////////////
	//electrostatic_force_factor = lengthMiddleSegment / 16.76
////////////////////////////////////////////////////////
	//static public double electrostatic_force_factor = 1800.0;
	//static public double electrostatic_force_factor =   	0.000005;
	//	static public double electrostatic_force_factor = 0.000005;
		//static public double electrostatic_force_factor = 233;
	static public double electrostatic_force_factor = 1;
	
	
////////////////////////////////////////////////////////
	//spring_force_factor = lengthMiddleSegment / 100616
////////////////////////////////////////////////////////
	//static public double spring_force_factor = 0.3;

	
	static public int deltaNeighbourIndex = 1;
	
	//static public boolean enableRejectForce = false;
	static public boolean isStable = false;
	static public boolean enableElectrostaticForce = true;
	static public boolean enableOriginForce = true;
	//static public boolean enableInverseElectrostaticForce = false;
	static public boolean enableSpringForce = true;
	static public double reject_force_factor = 2;
	//static public double spring_force_factor = 0.25;
	
	
	//static public double inverse_electrostatic_force_factor = -0.6;
	
	
	
	//static public double step_energy= 0.0001;
	//static public double initial_energy= 0.1;
	
	//static public double curr_energy= 1.0;
	static public double curr_energy= 0.05;
			//static public double curr_energy= 0.006;
	
	
	static public boolean enable_stability = false;
	//static public double enabled_index = 100;
	//static public double index_step = 0.1;
	//static public double index_step = 1;
	static public double max_step = 1;
	
	//public static double samePositionX = 0.05;
	//public static double samePositionY = 0.05;
	
	//public static double samePositionDistance = 12000;
	//public static double samePositionDistance = 6000;
	
	
	//ok for california
	//public static double samePositionDistance = 2700;
	//public static double samePositionDistance = 2400;
	
	//= 3400;
	
	//public static double samePredecessorPositionX = 0.25;
	//public static double samePredecessorPositionY = 0.25;
	
	public static int num_iteration = 0;
	public static int num_max_iteration = 50000;
	
	public static long time_start;    
	public static long elapsedTime;
	
	//METTERE A 1
	//METTERE A 500 per video
	public static long updateRateThread = 1;
	public static long updateScreenshotsRateThread = 60000;
	
	public static double prev_total_energy=0;
	public static double total_energy=0;
	public static double total_electr_force=0;
	public static double total_stress_force=0;
	public static double total_repulsive_force=0;
	public static double low_energy=Double.MAX_VALUE;;
	
	//public static int maxIndex=30;
	public static int min_iteraction_step=0;
	public static int min_nodes=Integer.MAX_VALUE;
	
	//RENDERING VARIABLES
	static public double maxFlowAltitude = 10000;
	
	//static public double minNodeAltitude = maxFlowAltitude;
	static public double minNodeAltitude = maxFlowAltitude/2;
	static public double maxNodeAltitude = (maxFlowAltitude/2)*3+5;
	//static public double maxRootNodeAltitude = (maxFlowAltitude/2)*3+10;
	static public double maxRootNodeAltitude = (maxFlowAltitude/2);
	
	//static public double minWidth = 1;
	//static public double maxWidth = 7;
	/*
	static public double maxWidth =150000.0;
	static public double minWidth =20000.0;
	*/
	static public double maxWidth;
	static public double minWidth;
	
	static public boolean enable_render_circle_neighbours = false;
	static public boolean enable_render_edges = false;
	static public boolean enable_render_nodes = true;
	static public boolean enable_render_tree = true;
	static public Color c = Color.decode("#3f8abf");
	public static Color[] listColors = {c,Color.RED,Color.GREEN,Color.CYAN,Color.BLUE,Color.ORANGE,Color.BLACK,Color.YELLOW,Color.DARK_GRAY,Color.WHITE,Color.MAGENTA,Color.PINK,Color.GRAY,Color.LIGHT_GRAY};
	
	
	static public int numOrigin=0;
	/*
	static public double nodeRadius = minWidth/4;;
	//static public double nodeRadius = 5000;
	static public double nodeLeafRadius = maxWidth*0.5;
	static public double nodeRootRadius = maxWidth;
	static public double maxFlowWidth = maxWidth*1.5;
	static public double minFlowWidth = minWidth;
	*/
	
	static public double nodeRadius;
	//static public double nodeRadius = 5000;
	static public double leafNodeMinRadius;
	static public double leafNodeMaxRadius;
	static public double rootNodeRadius;
	static public double maxFlowWidth;
	static public double minFlowWidth;
	
	static public double minRenderableFlowWidth;
	
	static public Color middleNodeColor = Color.BLACK;
	static public Color forkNodeColor = Color.RED;
	
	//static public Color rootNodeColor = Color.decode("#28587b");
	static public Color rootNodeColor = Color.BLACK;
	//static public Color leafNodeColor = new Color(210,247,00);
	static public Color leafNodeColor = Color.GREEN;
	static public Color outlineFlowColor = Color.decode("#363e45");
			//.( #006400);
	
	//static public double minSpringForce = 0.008;
	static public double minSpringForce = 0.000003;
	//static public double minSpringForce = 0.000;
	
	
	static public String executionDate = "";
	static public String fileName = "";
	static public boolean printResults = true;
	static public int renderAtIteration = 0;
	
	
	static public void initVariables(double maxDistance){
		Variables.maxDistance=maxDistance;
		//unitFactor=1000;
		unitFactor=200;
		//Variables.unitMaxDistance=maxDistance/500000;
		Variables.lengthMiddleSegment=maxDistance/Variables.numMiddleNodesInMaxFlow;		
		
		System.out.println("max Distance: "+maxDistance);
		System.out.println("Length");
		
		
		
		
		mapUnitInMeters=maxDistance/unitFactor;
		
		//Variables.minWidth=Variables.maxDistance/200;		
		Variables.minWidth=mapUnitInMeters;
		////////////////////////////////Variables.maxWidth=mapUnitInMeters*1.5;
		Variables.maxWidth=mapUnitInMeters*2.5;
		
		RenderOptionPanel.max_text.setText(String.valueOf(Variables.maxWidth));
		RenderOptionPanel.min_text.setText(String.valueOf(Variables.minWidth));
		
		samePositionDistanceInMeters=mapUnitInMeters;
		
		elasticFactor=0.001;
		//elasticFactor=0.00001;
		
		
		//attractionRadiusInMeters=mapUnitInMeters*20;
		
		
		attractionRadiusInMeters=(maxDistance/Variables.numMiddleNodesInMaxFlow)*2;
		//attractionRadiusInMeters=(maxDistance/Variables.numMiddleNodesInMaxFlow)*0.5;
		
		rejectBufferInMeters = mapUnitInMeters*1;
		nodeRadius = minWidth/2;
		//static public double nodeRadius = 5000;
		//leafNodeMinRadius = maxWidth*0.5;
		
		//per il whisky//
		//leafNodeMaxRadius = maxWidth*1.0;
		
		//leafNodeMaxRadius = maxWidth*4.0;
		
		rootNodeRadius = maxWidth/2;
		
		Variables.updateFlowWidth();
		
		VariablesPanel.distanceRejectArea_text.setText((String.valueOf(Variables.rejectBufferInMeters)));
	}
	
	
	public static void updateFlowWidth(){
		//System.out.println("UPDATE FlowWidth");
		double ratio=Operations.minFlowMagnitude /Operations.maxFlowMagnitude ;
		//maxFlowWidth = maxWidth*2.5;
		////////maxFlowWidth = maxWidth*4;
		
		
		/*
		maxFlowWidth
		minRenderableFlowWidth
		leafNodeMaxRadius
		*/
		
		//per california age-sex-origin
		
		maxFlowWidth = maxWidth*5;
		minRenderableFlowWidth=Operations.maxFlowMagnitude/50;
		leafNodeMaxRadius = maxFlowWidth*0.5;
		
				
		//per il whisky top 30//
		/*
		maxFlowWidth = maxWidth*2;
		minRenderableFlowWidth=Operations.maxFlowMagnitude/20000;
		leafNodeMaxRadius = maxFlowWidth*1.0;
		*/
		
		/*
		//per il texas
		maxFlowWidth = maxWidth*3;
		minRenderableFlowWidth=Operations.maxFlowMagnitude/200;
		leafNodeMaxRadius = maxFlowWidth*1.0;
		*/
		
		//per colorado
		/*
		maxFlowWidth = maxWidth*6;
		minRenderableFlowWidth=Operations.maxFlowMagnitude/50;
		leafNodeMaxRadius = maxFlowWidth*1.0;
		*/
		
		//per trento
		/*
		maxFlowWidth = maxWidth*0.5;
		minRenderableFlowWidth=Operations.maxFlowMagnitude/100000;
		leafNodeMaxRadius = maxFlowWidth*1.0;
		*/
		//minRenderableFlowWidth=Operations.maxFlowMagnitude/10000;
		
		
		
		
		
		
		
		
		
leafNodeMinRadius = maxFlowWidth*0.2;		

		
		
		
//per texas age-sex-origin
		/*
		maxFlowWidth = maxWidth*6;
		minRenderableFlowWidth=Operations.maxFlowMagnitude/20;
		leafNodeMaxRadius = maxFlowWidth*0.5;
		*/
		
//per colorado age-origin-sex
/*		
maxFlowWidth = maxWidth*6;
		minRenderableFlowWidth=Operations.maxFlowMagnitude/5;
		leafNodeMinRadius = maxFlowWidth*0.1;		
	*/	
		
		
		nodeRadius = maxFlowWidth*0.1;
		//minFlowWidth = minWidth*0.5;
				minFlowWidth = maxFlowWidth*ratio;
				//rootNodeRadius = maxWidth*10/2;
				rootNodeRadius = maxFlowWidth/2;
				//minRenderableFlowWidth=Operations.maxFlowMagnitude/1000;
	}
	
	public static double returnThickness(double magnitude){
		/*
		System.out.println("magnitude: "+magnitude);
		System.out.println("1 "+Operations.minFlowMagnitude);
		System.out.println("2 "+Operations.maxFlowMagnitude);
		System.out.println("3 "+Variables.minFlowWidth);
		System.out.println("4 "+Variables.maxFlowWidth);
		*/
		double res;
		
//		if(magnitude<Operations.maxFlowMagnitude/20){
//			magnitude=Operations.maxFlowMagnitude/20;
//			 res = Variables.getValue(magnitude,Operations.minFlowMagnitude,Operations.maxFlowMagnitude ,Variables.minFlowWidth, Variables.maxFlowWidth);
//		}else{
			res = Variables.getValue(magnitude,Operations.minFlowMagnitude,Operations.maxFlowMagnitude ,Variables.minFlowWidth, Variables.maxFlowWidth);
		//}
		//System.out.println("res: "+res);
		return res;
	}
	
	public static double returnNodeLeafThickness(double magnitude){
		/*
		System.out.println("minLeafNodeFlowMagnitude: "+Operations.minLeafNodeFlowMagnitude);
		System.out.println("maxLeafNodeFlowMagnitude: "+Operations.maxLeafNodeFlowMagnitude);
		System.out.println("leafNodeMinRadius: "+Variables.leafNodeMinRadius);
		System.out.println("leafNodeMaxRadius: "+Variables.leafNodeMaxRadius);
		*/
	//	double minRadius=Variables.leafNodeMinRadius;
	//	double maxRadius=Variables.leafNodeMaxRadius;

		
		double res =Variables.getValue(magnitude,Operations.minLeafNodeFlowMagnitude,Operations.maxLeafNodeFlowMagnitude ,Variables.leafNodeMinRadius, Variables.leafNodeMaxRadius); 
		//System.out.println("in returnNodeLeafThickness: "+magnitude+" , res: "+res);
		return   res;
	}
	
private static double getValue(double magnitude, double minMagnitude, double maxMagnitude, double minValue,double maxValue){
		
		float ratio=(float)((magnitude-minMagnitude)/(maxMagnitude-minMagnitude));
		return ((maxValue-minValue)*ratio)+minValue;
		
	}
}
