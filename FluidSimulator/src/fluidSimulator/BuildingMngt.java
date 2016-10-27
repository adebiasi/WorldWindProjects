package fluidSimulator;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Renderable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class BuildingMngt {

	public static class Building{
		
		ArrayList<Position> positionList = new ArrayList<Position>();
		
		
		// checks whether a point is inside a polygon
		static public boolean isPointInPoly(Point[] poly,Point pt)
		{
			boolean c = false;
			int l = poly.length;
			
			 for (int i=0,j=l-1;i<l;j=i++) {
			      if (( ((poly[i].y <= pt.y) && (pt.y < poly[j].y)) || ((poly[j].y <= pt.y) && (pt.y < poly[i].y))) &&
			            (pt.x < (poly[j].x - poly[i].x) * (pt.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x))
			      {
			        c = !c;
			      }
			 }
			return c;
		}
		
	
		
		// converts the given polygon into a grid of 1's
		public static void polyToGrid(double[] grid, Point[] poly) // "grid" and "poly" are passed as reference, because they are objects!!
		{
			int min_x = 11111111;
			int min_y = 11111111;
			int max_x = 0;
			int max_y = 0;
			
			// we look for the extreme points of the polygon
			for(int i = poly.length - 1; i >= 0; i--) {
				if(poly[i].x < min_x)
					min_x = poly[i].x;
				if(poly[i].x > max_x)
					max_x = poly[i].x;
				if(poly[i].y < min_y)
					min_y = poly[i].y;
				if(poly[i].y > max_y)
					max_y = poly[i].y;
			}
			
			// we search only inside the extreme points not the whole grid
			for(int i = min_x; i <= max_x; i++) {
				for(int j = min_y; j <= max_y; j++) {
					Point pt = new Point(0, 0); // do not use, because it doesn't work right!!! -> var pt =[{x: i, y: j}];
					pt.x = i;
					pt.y = j;
					if(isPointInPoly(poly, pt)) {
						//System.out.println(i+" "+j);
						int index= fluid.getIndex(i, j);
					
						grid[index] = 1;
						
					}
				}
			}
		}
		
		
		public Building(ArrayList<Position> positionList) {
			super();
			this.positionList = positionList;
		}

		
		
		private boolean ishigher(Position currPos){
			
			for(Position p: positionList){
				if(currPos.latitude.degrees<p.latitude.degrees){
					return false;
				}
			}
			
			return true;
			
		}
		
	}
	
	/*
	public static void init_buildingGrids (Building b,double[] grid){
		
ArrayList<Position> posList =b.positionList;
		
		Point p1 ;
		Point p2 ;
		
		 p1 = Variables.getXy(posList.get(0));
		 p2 = Variables.getXy(posList.get(1)); 
		 
		 init_segment( p1, p2, grid);
		 
		 p1 = Variables.getXy(posList.get(1));
		 p2 = Variables.getXy(posList.get(2)); 
		 
		 init_segment( p1, p2, grid);
		 
		 p1 = Variables.getXy(posList.get(2));
		 p2 = Variables.getXy(posList.get(3)); 
		 
		 init_segment( p1, p2, grid);
		 
		 p1 = Variables.getXy(posList.get(3));
		 p2 = Variables.getXy(posList.get(0)); 
		 
		 init_segment( p1, p2, grid);
	}
	*/
	public static void init_buildingGrids (Building b,double[] grid){
		
		ArrayList<Position> posList=b.positionList;
				
				Point p1 ;
				Point p2 ;
				Point p3 ;
				Point p4 ;
				
				 p1 = Variables.getXy(posList.get(0));
				 p2 = Variables.getXy(posList.get(1));				 
				 p3 = Variables.getXy(posList.get(2)); 
				 p4 = Variables.getXy(posList.get(3)); 
			
				 Point[] polygon ={p1,p2,p3,p4};
				 
				 
				 Building.polyToGrid(grid,polygon); 
			}
		
	
//public static void init_trentoBuildingGrids(ArrayList<Building> bList,double[] grid){
	public static void init_trentoBuildingGrids(double[] grid){
	
	Iterable<Renderable> buildings = Variables.buildings;
	
	Iterator<Renderable> rend= buildings.iterator();
	
	while(rend.hasNext()){
		Renderable r = rend.next();
		
		ExtrudedPolygon pol = (ExtrudedPolygon)r;
		 Iterator<? extends LatLon> it= pol.getOuterBoundary().iterator();
		 ArrayList<Point> polygon = new ArrayList<Point>();
		 
		 while(it.hasNext()){
		     	LatLon latlon = it.next();
		     	
		     	if((latlon.latitude.degrees<Variables.lat2)&(latlon.latitude.degrees>Variables.lat1)){
		     		if((latlon.longitude.degrees<Variables.lon2)&(latlon.longitude.degrees>Variables.lon1)){
		     			Point point = Variables.getXy(latlon);
						//System.out.println(point);
						polygon.add(point);
		     		}
		     	}
		     	
		     }
		 
		 Building.polyToGrid(grid,polygon.toArray(new Point[polygon.size()])); 
	}
	
	
    
     
    
	/*
	for(Building b: bList){
		ArrayList<Position> posList=b.positionList;
		Point[] polygon = new Point[posList.size()];
		
		for(int i =0;i<posList.size();i++){
			Position p = posList.get(i);
			System.out.println(p);
			Point point = Variables.getXy(p);
			System.out.println(point);
			polygon[i]=point;
		}
		
		Building.polyToGrid(grid,polygon); 
	}
	*/
		
			}
	
	/*
private static void init_segment(Point p1,Point p2,double[] grid){
	if(p1.x<p2.x){
		 if(p1.y<p2.y){
			 //case 1
			 init_segment(1, p1, p2, grid);
		 }else{
			 //case 2
			 init_segment(2, p1, p2, grid);
		 } 
	 }else{
		 if(p1.y<p2.y){
			 //case 4
			 init_segment(4, p1, p2, grid);
		 }else{
			 //case 3
			 init_segment(3, p1, p2, grid);
		 } 
	 }
	}
	*/
	
	/*
	private static void init_segment(int usecase,Point p1,Point p2,double[] grid){
		if(usecase==1){
			int delta_x=(p2.x-p1.x);
			int delta_y=(p2.y-p1.y);
			
			for(int i=p1.x;i<=p2.x;i++){
				int delta=(int)(((double)(i-p1.x)/(double)delta_x)*((double)delta_y));
				
				int newY=delta+p1.y;
				int newX=i;
				
				
				
				grid[fluid.getIndex(newX,newY)]=1;
				
				grid[fluid.getIndex(newX+1,newY)]=1;
				grid[fluid.getIndex(newX,newY-1)]=1;
			}
		}
		
		if(usecase==2){
			int delta_x=(p2.x-p1.x);
			int delta_y=(p1.y-p2.y);
			
			 
			 
			 for(int i=p1.x;i<=p2.x;i++){
					int delta=(int)(((double)(i-p1.x)/(double)delta_x)*((double)delta_y));
					
					int newY=p1.y-delta;
					int newX=i;
					grid[fluid.getIndex(newX,newY)]=1;
					
					grid[fluid.getIndex(newX-1,newY)]=1;
					grid[fluid.getIndex(newX,newY-1)]=1;
				}
			
		}
		
		
		if(usecase==3){
			 int delta_x=(p1.x-p2.x);
			 int delta_y=(p1.y-p2.y);
			
			 
				 for(int i=p2.x;i<=p1.x;i++){
					double delta_01=(((double)(i-p2.x)/(double)delta_x));
					System.out.println("delta: "+delta_01);
					int newY=p2.y+(int)(delta_01*(double)delta_y);				
					int newX=i;
					System.out.println(newX+ " "+newY);
					
					grid[fluid.getIndex(newX,newY)]=1;
					
					grid[fluid.getIndex(newX-1,newY)]=1;
					grid[fluid.getIndex(newX,newY+1)]=1;
				}
		}
		
		
		
		if(usecase==4){
			 int delta_x=(p1.x-p2.x);
			 int delta_y=(p2.y-p1.y);
			
				 
			for(int i=p1.y;i<=p2.y;i++){
				double delta_01=(((double)(i-p1.y)/(double)delta_y));
				
				int newX=p1.x-(int)(delta_01*(double)delta_x);				
				int newY=i;
				
				grid[fluid.getIndex(newX,newY)]=1;
				
				grid[fluid.getIndex(newX+1,newY)]=1;
				grid[fluid.getIndex(newX,newY+1)]=1;
			
			}
			
		}
		
	}
	*/
	/*
	public static void init_buildingGrids (Building b,double[] grid_x,double[] grid_y){
		
		ArrayList<Position> posList =b.positionList;
		
		Point p1 ;
		Point p2 ;
		
		 p1 = Variables.getXy(posList.get(0));
		 p2 = Variables.getXy(posList.get(1));
		
		 System.out.println("p0: "+p1);
		 System.out.println("p1: "+p2);
		 
		int delta_x=(p2.x-p1.x);
		int delta_y=(p2.y-p1.y);
		
		for(int i=p1.x;i<=p2.x;i++){
			System.out.print("1");
			int delta=(int)(((double)(i-p1.x)/(double)delta_x)*((double)delta_y));
			
			int newY=delta+p1.y;
			int newX=i;
			grid_y[fluid.getIndex(newX,newY)]=1;
			
			if(p1.y>p2.y){
			grid_x[fluid.getIndex(newX,newY)]=10;
			}else{
				grid_x[fluid.getIndex(newX,newY)]=-10;	
			}
		}
		
		
		
		
		 p1 = Variables.getXy(posList.get(1));
		 p2 = Variables.getXy(posList.get(2));
		
		 delta_x=(p2.x-p1.x);
		 delta_y=(p2.y-p1.y);
		
		 if(delta_x<0) delta_x*=-1;
		 if(delta_y<0) delta_y*=-1;
		 
		 System.out.println("p1: "+p1);
		 System.out.println("p2: "+p2);
		 
		for(int i=p1.y;i<=p2.y;i++){
			System.out.print("@@@");
			double delta_01=(((double)(i-p1.y)/(double)delta_y));
			System.out.println("delta: "+delta_01);
			int newX;
			if(p1.x>p2.x){
			 newX=p1.x-(int)(delta_01*(double)delta_x);
			}else{
				newX=p2.x-(int)(delta_01*(double)delta_x);
			}
			int newY=i;
			System.out.println(newX+ " "+newY);
			
			grid_x[fluid.getIndex(newX,newY)]=100;
			
			if(p1.x>p2.x){
			grid_y[fluid.getIndex(newX,newY)]=1;
			}else{
				grid_y[fluid.getIndex(newX,newY)]=-1;	
			}
		}
		
		
		
		 p1 = Variables.getXy(posList.get(2));
		 p2 = Variables.getXy(posList.get(3));
		
		 delta_x=(p2.x-p1.x);
		 delta_y=(p2.y-p1.y);
		
		 if(delta_x<0) delta_x*=-1;
		 if(delta_y<0) delta_y*=-1;
		
		 System.out.println("p2: "+p1);
		 System.out.println("p3: "+p2);
		 
		 System.out.println("deltax "+delta_x);
		 System.out.println("deltay "+delta_y);
		 
		 for(int i=p2.x;i<=p1.x;i++){
				System.out.print("---");
				double delta_01=(((double)(i-p2.x)/(double)delta_x));
				System.out.println("delta: "+delta_01);
				int newY;
				if(p1.y>p2.y){
				 newY=(int)(delta_01*(double)delta_y)+p2.y;
				}else{
					newY=(int)(delta_01*(double)delta_y)+p1.y;
				}
				int newX=i;
				System.out.println(newX+ " "+newY);
				
				grid_y[fluid.getIndex(newX,newY)]=1;
				
				if(p1.y>p2.y){
				grid_x[fluid.getIndex(newX,newY)]=1000;
				}else{
					grid_y[fluid.getIndex(newX,newY)]=-1000;	
				}
			}
		
		
		 p1 = Variables.getXy(posList.get(3));
		 p2 = Variables.getXy(posList.get(0));
		
		 delta_x=(p2.x-p1.x);
		 delta_y=(p2.y-p1.y);
		
		 if(delta_x<0) delta_x*=-1;
		 if(delta_y<0) delta_y*=-1;
		 
		 System.out.println("p3: "+p1);
		 System.out.println("p4: "+p2);
		 
		 System.out.println("deltax "+delta_x);
		 System.out.println("deltay "+delta_y);
		 
		for(int i=p2.y;i<=p1.y;i++){
			System.out.print("///");
			double delta_01=(((double)(i-p2.y)/(double)delta_y));
			System.out.println("delta: "+delta_01);
			int newX;
			if(p1.x>p2.x){
			 newX=p1.x-(int)(delta_01*(double)delta_x);
			}else{
				newX=p2.x-(int)(delta_01*(double)delta_x);
			}
			int newY=i;
			System.out.println(newX+ " "+newY);
			
			grid_x[fluid.getIndex(newX,newY)]=10000;
			
			if(p1.x>p2.x){
			grid_y[fluid.getIndex(newX,newY)]=1;
			}else{
				grid_y[fluid.getIndex(newX,newY)]=-1;	
			}
		}
		
		
	}
	*/
	
	public static void set_buildings(int N,double[] src,double[] buildings){
		
		for ( int i=1 ; i<=N ; i++ ) {
			for ( int j=1 ; j<=N ; j++ ) {
				
				if(buildings[fluid.getIndex(i, j)]!=0){
					//src[fluid.getIndex(i, j)]=buildings[fluid.getIndex(i, j)];
					src[fluid.getIndex(i, j)]=0;
				}
			}
		}
		
	}
	
	public static void print(int n,double[] grid){
		//for(int y=0;y<n+2;y++){
		for(int y=n+1;y>=0;y--){
			for(int x=0;x<n+2;x++){
				int res;
				if(grid[fluid.getIndex(x,y)]==0){
					System.out.print(" "+" ");
				}
				else
				System.out.print(grid[fluid.getIndex(x,y)]+" ");
			}
			System.out.println(" ");
		}
	}
	
	public static Building createBuildings() {
	ArrayList<Position> positions = new ArrayList<Position>();
		
		
		positions.add(Position.fromDegrees(46.0641, 11.1118));
		positions.add(Position.fromDegrees(46.0599, 11.1192));
		positions.add(Position.fromDegrees(46.0541, 11.1092));
		positions.add(Position.fromDegrees(46.0566, 11.1026));
		
		Building b = new Building(positions);
		
		return b;
	}
	
	public static ArrayList<Building> createTrentoBuildings() {
		
		
		
		
		ArrayList<Building> buildings = new ArrayList<Building>();
		ArrayList<Position> positions = new ArrayList<Position>();
			
		positions.add(Position.fromDegrees(46.043746682640204,11.123464904657073));
		positions.add(Position.fromDegrees(46.043762362492465,11.123347631710768));
		positions.add(Position.fromDegrees(46.04368911386397,11.123327505506028));
		positions.add(Position.fromDegrees(46.04366808625574,11.123485544795525));
		positions.add(Position.fromDegrees(46.04370858514006,11.123495368740501));
		positions.add(Position.fromDegrees(46.04370068147016,11.123562274542472));
		positions.add(Position.fromDegrees(46.043659198062294,11.123552154322706));
		positions.add(Position.fromDegrees(46.04364727911692,11.123641395210162));
		positions.add(Position.fromDegrees(46.043721244796416,11.123661678543764));
		positions.add(Position.fromDegrees(46.043739252832815,11.123520842623702));
		positions.add(Position.fromDegrees(46.043746682640204,11.123464904657073));
			
		
			Building b = new Building(positions);
			buildings.add(b);
			
			positions = new ArrayList<Position>();
			positions.add(Position.fromDegrees(46.043659198062294,11.123552154322706));
			positions.add(Position.fromDegrees(46.04370068147016,11.123562274542472));
			positions.add(Position.fromDegrees(46.04370858514006,11.123495368740501));
			positions.add(Position.fromDegrees(46.04366808625574,11.123485544795525));
			positions.add(Position.fromDegrees(46.043659198062294,11.123552154322706));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			
			positions.add(Position.fromDegrees(46.04375545032261,11.1236711321689));
			positions.add(Position.fromDegrees(46.04377417548494,11.12353045289713));
			positions.add(Position.fromDegrees(46.043739252832815,11.123520842623702));
			positions.add(Position.fromDegrees(46.043721244796416,11.123661678543764));
			positions.add(Position.fromDegrees(46.04375545032261,11.1236711321689));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			positions.add(Position.fromDegrees(46.04224251848003,11.123636023038785));
			positions.add(Position.fromDegrees(46.04236619122851,11.123616206418951));
			positions.add(Position.fromDegrees(46.04234982412665,11.123407237236647));
			positions.add(Position.fromDegrees(46.04229620343532,11.123415780449395));
			positions.add(Position.fromDegrees(46.04230296924978,11.123502116397045));
			positions.add(Position.fromDegrees(46.042233007153264,11.123513393605776));
			positions.add(Position.fromDegrees(46.04224251848003,11.123636023038785));
			
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
	
			positions.add(Position.fromDegrees(46.0425529514828,11.125266712193131));
			positions.add(Position.fromDegrees(46.04257636235953,11.12509196631008));
			positions.add(Position.fromDegrees(46.04246595507273,11.125061495310186));
			positions.add(Position.fromDegrees(46.042444507437175,11.125232309576099));
			positions.add(Position.fromDegrees(46.0425529514828,11.125266712193131));
			
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			positions.add(Position.fromDegrees(46.0425205386144,11.125306051847792));
			positions.add(Position.fromDegrees(46.04254668551477,11.12531325848778));
			positions.add(Position.fromDegrees(46.0425529514828,11.125266712193131));
			positions.add(Position.fromDegrees(46.042444507437175,11.125232309576099));
			positions.add(Position.fromDegrees(46.042438069868666,11.125283243517549));
			positions.add(Position.fromDegrees(46.0425205386144,11.125306051847792));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			positions.add(Position.fromDegrees(46.04332433639073,11.125403961891966));
			positions.add(Position.fromDegrees(46.04331579218009,11.125403634146894));
			positions.add(Position.fromDegrees(46.04331345463684,11.125563164054325));
			positions.add(Position.fromDegrees(46.04341806816689,11.125566401726752));
			positions.add(Position.fromDegrees(46.043419983935586,11.125434384921753));
			positions.add(Position.fromDegrees(46.043385350191464,11.12543344413309));
			positions.add(Position.fromDegrees(46.043385771975196,11.125405930746993));
			positions.add(Position.fromDegrees(46.04332433639073,11.125403961891966));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();

			positions.add(Position.fromDegrees(46.04335476145993,11.125026306843967));
			positions.add(Position.fromDegrees(46.04336336645548,11.124820100584136));
			positions.add(Position.fromDegrees(46.04330386280224,11.124815880028317));
			positions.add(Position.fromDegrees(46.04329660690431,11.125022137807768));
			positions.add(Position.fromDegrees(46.04335476145993,11.125026306843967));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			positions.add(Position.fromDegrees(46.04335476145993,11.125026306843967));
			positions.add(Position.fromDegrees(46.04335350976125,11.125059733736693));
			positions.add(Position.fromDegrees(46.04341840734298,11.125064290679086));
			positions.add(Position.fromDegrees(46.043421603297844,11.124974715925848));
			positions.add(Position.fromDegrees(46.04348074718462,11.124978922868474));
			positions.add(Position.fromDegrees(46.04348386994823,11.124888440473775));
			positions.add(Position.fromDegrees(46.04345618552238,11.124886474058167));
			positions.add(Position.fromDegrees(46.04345828209191,11.124826971615004));
			positions.add(Position.fromDegrees(46.04336336645548,11.124820100584136));
			positions.add(Position.fromDegrees(46.04335476145993,11.125026306843967));

			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			positions.add(Position.fromDegrees(46.04356848647908,11.124485978500326));
			positions.add(Position.fromDegrees(46.04366754500122,11.124507225064463));
			positions.add(Position.fromDegrees(46.043674464694156,11.124440023213495));
			positions.add(Position.fromDegrees(46.04357549609668,11.12441878021438));
			positions.add(Position.fromDegrees(46.04356848647908,11.124485978500326));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			positions.add(Position.fromDegrees(46.04366738619996,11.124675499144091));
			positions.add(Position.fromDegrees(46.04367320534601,11.124599724781781));
			positions.add(Position.fromDegrees(46.043557093587616,11.124561409765464));
			positions.add(Position.fromDegrees(46.043550913257995,11.124695331349855));
			positions.add(Position.fromDegrees(46.043664191718676,11.12471660657988));
			positions.add(Position.fromDegrees(46.04366738619996,11.124675499144091));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			positions.add(Position.fromDegrees(46.043545342313955,11.124815834569567));
			positions.add(Position.fromDegrees(46.04371004643298,11.124849809712197));
			positions.add(Position.fromDegrees(46.04371900280576,11.124726334017648));
			positions.add(Position.fromDegrees(46.043664191718676,11.12471660657988));
			positions.add(Position.fromDegrees(46.043550913257995,11.124695331349855));
			positions.add(Position.fromDegrees(46.043545342313955,11.124815834569567));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			positions.add(Position.fromDegrees(46.04381503196392,11.124561994337643));
			positions.add(Position.fromDegrees(46.043820953535544,11.12456299688232));
			positions.add(Position.fromDegrees(46.043825457371554,11.124475668810906));
			positions.add(Position.fromDegrees(46.043805233079766,11.124474247119753));
			positions.add(Position.fromDegrees(46.04372803302059,11.124468185133798));
			positions.add(Position.fromDegrees(46.04372009351777,11.124604753884949));
			positions.add(Position.fromDegrees(46.04367320534601,11.124599724781781));
			positions.add(Position.fromDegrees(46.04366738619996,11.124675499144091));
			positions.add(Position.fromDegrees(46.0437858704449,11.124692708836207));
			positions.add(Position.fromDegrees(46.043791083441064,11.124591449268173));
			positions.add(Position.fromDegrees(46.04379278470287,11.12455803933941));
			positions.add(Position.fromDegrees(46.04381503196392,11.124561994337643));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			positions.add(Position.fromDegrees(46.043791083441064,11.124591449268173));
			positions.add(Position.fromDegrees(46.04381334030688,11.124594887656855));
			positions.add(Position.fromDegrees(46.04381503196392,11.124561994337643));
			positions.add(Position.fromDegrees(46.04379278470287,11.12455803933941));
			positions.add(Position.fromDegrees(46.043791083441064,11.124591449268173));
			
			b = new Building(positions);
			buildings.add(b);
			positions = new ArrayList<Position>();
			
			return buildings;
		}
	
	public static void main(String[] args) {
		
		//ArrayList<Building> buildings = new ArrayList<Building>();
	
		//buildings.add(new Building(positions));
		
		 double[] grid= new double[Variables.size];
		  
		 //Building b=createBuildings();
		 
		 //init_buildingGrids(b,grid);
		
		 ArrayList<Building> bildings = createTrentoBuildings();
		// init_trentoBuildingGrids(bildings, grid);
		 
		print(Variables.N,grid);
		//print(Variables.N,gridy);
	}
	
}
