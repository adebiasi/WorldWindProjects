package fluidSimulator;

import java.awt.Point;
import java.util.ArrayList;


import fluidSimulator.BuildingMngt.Building;
import gov.nasa.worldwind.util.BufferFactory;
import gov.nasa.worldwind.util.BufferWrapper;

public class fluid {

	//static public int N = 15;
	
	
	
	
	static double[] v ;
	static  double[] u;
	static  double[] dens;

	static  double[] dens_prev;
	static  double[] u_prev;
	static  double[] v_prev;
	
	static  double[] dens_src;
	static  double[] u_src;
	static  double[] v_src;
	
	
	static  double[] buildings;
	//static  double[] v_buildings;
	
	//density evolves for 3 causes
	
	// Grid grid = new Grid(N);



	 public  static int getIndex(int i,int j){
		 return i+(Variables.N+2)*j;
	 }
	 
	 /*
private static  void SWAP(double[] x0, double[] x){
	double[] temp;
	temp=x0;
	x0=x;
	x=temp;
}
*/
//first step
	 
	 //N: num rows & cols
	 //s: source for a given frame
	 //dt: time spacing between the snapshots
	
	 private static void add_source(int N, double[] x, double[] s, float dt){
		 int i;
		 int size = (N+2)*(N+2);
		 
		 for(i=0;i<size;i++){
			 x[i] += dt*s[i];
			 s[i] = 0;
		 }
	 }

	 //N: num rows & cols
	 //b: 
	 //x: next densities
	 //x0: previous densities
	 //diff: diffusion rate
	 //dt:time spacing between the snapshots
	/* 
private void diffuse_bad(int N, int b, float[] x, float[] x0, float diff,  float dt){
	
	int i,j;
	float a = dt*diff*N*N;
	
	for(i=1;i<=N;i++){
		for(j=1;j<-N;j++){
			x[getIndex(i,j)]=x0[getIndex(i,j)]+a*(x0[getIndex(i-1,j)]+x0[getIndex(i+1,j)]+x0[getIndex(i,j-1)]+x0[getIndex(i,j+1)]-4*x0[getIndex(i,j)]);
		}
	}
	
	set_bnd(N,b,x);
}
	 */
	 
	//second step
//N: num rows & cols
//b: 0 è orrizzontale (u), 1 è verticale (V)
//x: next densities
//x0: previous densities
//diff: diffusion rate
//dt:time spacing between the snapshots

private static  void diffuse(int N, int b, double[] x, double[] x0, float diff,  float dt){
	
	int i,j,k;
	float a = dt*diff*N*N;
	
	//System.out.println("a: "+a);
	
	for(k=0;k<=20;k++){
	for(i=1;i<=N;i++){
		for(j=1;j<=N;j++){
			x[getIndex(i,j)]=(x0[getIndex(i,j)]+a*(x[getIndex(i-1,j)]+x[getIndex(i+1,j)]+x[getIndex(i,j-1)]+x[getIndex(i,j+1)])/(1+4*a));
		}
	}
	
	set_bnd(N,b,x);
/*
BuildingMngt.set_buildings(N,x,u_buildings);
BuildingMngt.set_buildings(N,x,v_buildings);
*/
	}
}

//from applet
private static void advect(int N, int b, double[] dest, double[] src, double[] xVelocity, double[] yVelocity, double dt)
{	
	
	//System.out.println("DT: "+dt);
	//for(int y = 1; y < N-1; y++)
		for(int y=1;y<N-1;y++)
	{
		for (int x = 1; x < N-1; x++) 
		{
	       //Reverse velocity, since we are interpolating backwards
            //xSrc and ySrc is the position of the source density.
			double xSrc = x-dt*xVelocity[getIndex(x,y)]; 
			double ySrc = y-dt*yVelocity[getIndex(x,y)];
			
			if(xSrc < 0.5) {xSrc = 0.5;} 
			if(xSrc > N - 1.5) {xSrc = N - 1.5;}
			int xi0 = (int)xSrc; 
			int xi1 = xi0+1;
			
			if(ySrc < 0.5) {ySrc = 0.5;}
			if(ySrc > N - 1.5) {ySrc = N - 1.5;} 
			int yi0 = (int)ySrc; 
			int yi1 = yi0+1;
			
			
			
			//Linear interpolation factors. Ex: 0.6 and 0.4
			double xProp1 = xSrc-xi0;
			double xProp0 = 1.0-xProp1; 
			double yProp1 = ySrc-yi0; 
			double yProp0 = 1.0-yProp1;
			
			//System.out.println("(xProp1 = xSrc-xi0) "+xProp1+" = "+xSrc +" - "+xi0);
			//System.out.println("(yProp1 = ySrc-yi0) "+yProp1+" = "+ySrc +" - "+yi0);
			
		//	System.out.println(xProp1+" "+xProp0+" "+yProp1+" "+yProp0);
			
			if(buildings[getIndex(x,y)]!=0){
				//System.out.println("c'è qualcosa");
			}else{
			
			dest[getIndex(x,y)] = 
				xProp0*(yProp0*src[getIndex(xi0,yi0)] + yProp1*src[getIndex(xi0,yi1)]) +
				xProp1*(yProp0*src[getIndex(xi1,yi0)] + yProp1*src[getIndex(xi1,yi1)]);
			}
		/*	
			if(dest[getIndex(x,y)]>0){
				System.out.println(dest[getIndex(x,y)]+" = "+
					xProp0+"*"+"("+yProp0+"*"+src[getIndex(xi0,yi0)]+"+"+yProp1+"*"+src[getIndex(xi0,yi1)]+")"+"+"+
					xProp1+"*"+"("+yProp0+"*"+src[getIndex(xi1,yi0)]+"+"+yProp1+"*"+src[getIndex(xi1,yi1)]+")");
			}
			*/
		}
	}
	set_bnd(N,b, dest);
	
}
	//third step
/*
static private void advect(int N,int b, double[] dest, double[] src, double[] u, double[] v, double dt ){
		
		int i,j,xi0,yi0,xi1,yi1;
		double xSrc,ySrc,xProp0,yProp0,xProp1,yProp1,dt0;
		
		dt0 = dt*N;
		//dt0 = dt;
		for(i=1;i<=N;i++){
			for(j=1;j<=N;j++){
				xSrc = i-dt0*u[getIndex(i,j)]; 
				ySrc = j-dt0*v[getIndex(i,j)];
				
				System.out.println(xSrc + " "+ySrc );
				
				if (xSrc<0.5) xSrc=0.5f;
				if (xSrc>N+0.5) xSrc=N+ 0.5f; 
				xi0=(int)xSrc; 
				xi1=xi0+1;
				
				if (ySrc<0.5) ySrc=0.5f; 
				if (ySrc>N+0.5) ySrc=N+ 0.5f; 
				yi0=(int)ySrc; 
				yi1=yi0+1;
				
				xProp1 = xSrc-xi0; 
				xProp0 = 1.0-xProp1; 
				yProp1 = ySrc-yi0; 
				yProp0 = 1.0-yProp1;
				
				//System.out.println(xProp1+" "+xProp0+" "+yProp1+" "+yProp0);
				
				dest[getIndex(i,j)] = 
					xProp0*(yProp0*src[getIndex(xi0,yi0)]+yProp1*src[getIndex(xi0,yi1)])+
					xProp1*(yProp0*src[getIndex(xi1,yi0)]+yProp1*src[getIndex(xi1,yi1)]);
				
				
		//		System.out.println("index: "+i0+" "+i1+" "+j0+" "+j1);
				
			//	System.out.println("d[]"+d0[getIndex(i0,j0)]);
				
			//	System.out.println(dest[getIndex(i,j)]+" = "+
				//		xProp0+"*"+"("+yProp0+"*"+src[getIndex(xi0,yi0)]+"+"+yProp1+"*"+src[getIndex(xi0,yi1)]+")"+"+"+
					//	xProp1+"*"+"("+yProp0+"*"+src[getIndex(xi1,yi0)]+"+"+yProp1+"*"+src[getIndex(xi1,yi1)]+")");
		
			}
		}
		//System.out.println("prima di bound");
		//draw_dens ( N, dest );
		set_bnd ( N, b, dest );
		//System.out.println("dopo di bound");
		//draw_dens ( N, dest );
	}
	*/
/*
private static void new_advect(int N, int b, double[] dest, double[] src, double[] xVelocity, double[] yVelocity, double dt)
{	
	for(int y = 1; y <= N; y++)
	{
		int yIndex = y*N+1;
		for (int x = 1; x <= N; x++) 
		{
			int k = x + yIndex;
            //Reverse velocity, since we are interpolating backwards
            //xSrc and ySrc is the position of the source density.
			double xSrc = x-dt*xVelocity[k]; 
			double ySrc = y-dt*yVelocity[k];
			
			if(xSrc < 0.5) {xSrc = 0.5;} 
			if(xSrc > N + 0.5) {xSrc = N + 0.5;}
			int xi0 = (int)xSrc; 
			int xi1 = xi0+1;
			
			if(ySrc < 0.5) {ySrc = 0.5;}
			if(ySrc > N + 0.5) {ySrc = N + 0.5;} 
			int yi0 = (int)ySrc; 
			int yi1 = yi0+1;
			
			//Linear interpolation factors. Ex: 0.6 and 0.4
			double xProp1 = xSrc-xi0;
			double xProp0 = 1.0-xProp1; 
			double yProp1 = ySrc-yi0; 
			double yProp0 = 1.0-yProp1;
			
			dest[getIndex(x,y)] = 
			//dest[k] = 
				xProp0*(yProp0*src[getIndex(xi0,yi0)] + yProp1*src[getIndex(xi0,yi1)]) +
				xProp1*(yProp0*src[getIndex(xi1,yi0)] + yProp1*src[getIndex(xi1,yi1)]);
			
		}
	}
	set_bnd(N,b, dest);
}
*/
	//all steps
static private void dens_step ( int N, double[] x, double[] x0, double[] u, double[] v, float diff,
			float dt )
			{
	
	
		//	add_source ( N, x, x0, dt );
			
	
			if(diff>0){
			//SWAP ( x0, x ); 
				double[] temp;
				temp=x0;
				x0=x;
				x=temp;
				
				diffuse ( N, 0, x, x0, diff, dt );
			}
			
		
	//System.out.println("prima");
	//		printMinMaxDensity(N);
			
			double[] temp;
			temp=x0;
			x0=x;
			x=temp;
			//SWAP ( x0, x );
			advect ( N, 0, x, x0, u, v, dt );
			
		//	System.out.println("dopo");
	//		printMinMaxDensity(N);
				}



private static  void printMinMaxDensity(int n){
	
	double min = 100;
	double max= -100;
	
	System.out.println("----------------");
	for(int j=0;j<n+2;j++){
	for(int i=0;i<n+2;i++){
			//System.out.print("  "+dens[getIndex(i, j)]);
			if(dens[getIndex(i, j)]<min){
				min=dens[getIndex(i, j)];
			}
			if(dens[getIndex(i, j)]>max){
				max=dens[getIndex(i, j)];
			}
			
	}
		
	}
	System.out.println("min: "+min+" ,max: "+max);
	System.out.println("----------------");
	
}


//v0,u0 are the force fields
private static void vel_step ( int N, double[] u, double[] v, double[] u0, double[] v0,
		float visc, float dt )
		{
		add_source ( N, u, u0, dt ); add_source ( N, v, v0, dt );
		
		/*
		draw_dens ( N, v );
		draw_dens ( N, v0 );
		*/
		
		//SWAP ( u0, u ); 
		
		double[] temp;
		temp=u0;
		u0=u;
		u=temp;
		//draw_dens ( N, u );
		diffuse ( N, 1, u, u0, visc, dt );
		//draw_dens ( N, u );
		
		
			
		//SWAP ( v0, v ); 
		//double[] temp;
		temp=v0;
		v0=v;
		v=temp;
		//draw_dens ( N, v );
		//draw_dens ( N, v0 );
		diffuse ( N, 2, v, v0, visc, dt );
		//draw_dens ( N, v );
		//System.out.println("V");
		//draw_dens ( N, v );
		
		
		/*
		draw_dens ( N, v );
		draw_dens ( N, v0 );
		*/
		project ( N, u, v, u0, v0 );
		//SWAP ( u0, u ); 
		temp=u0;
		u0=u;
		u=temp;
		
		//SWAP ( v0, v );
		temp=v0;
		v0=v;
		v=temp;
		//System.out.println("V");
		//draw_dens ( N, v );
		
		/*
		draw_dens ( N, v );
		draw_dens ( N, v0 );
		*/
		
		advect ( N, 1, u, u0, u0, v0, dt ); 
		advect ( N, 2, v, v0, u0, v0, dt );
		project ( N, u, v, u0, v0 );
		}


static private void project ( int N, double[] u, double[] v, double[] p, double[] div )
{
int i, j, k;
float h;
h = 1.0f/N;
for ( i=1 ; i<=N ; i++ ) {
for ( j=1 ; j<=N ; j++ ) {
div[getIndex(i,j)] = -0.5f*h*(u[getIndex(i+1,j)]-u[getIndex(i-1,j)]+
v[getIndex(i,j+1)]-v[getIndex(i,j-1)]);
p[getIndex(i,j)] = 0;
}
}
set_bnd ( N, 0, div ); set_bnd ( N, 0, p );

for ( k=0 ; k<20 ; k++ ) {
for ( i=1 ; i<=N ; i++ ) {
for ( j=1 ; j<=N ; j++ ) {
p[getIndex(i,j)] = (div[getIndex(i,j)]+p[getIndex(i-1,j)]+p[getIndex(i+1,j)]+
p[getIndex(i,j-1)]+p[getIndex(i,j+1)])/4;
}
}
set_bnd ( N, 0, p );


}
for ( i=1 ; i<=N ; i++ ) {
for ( j=1 ; j<=N ; j++ ) {
u[getIndex(i,j)] -= 0.5*(p[getIndex(i+1,j)]-p[getIndex(i-1,j)])/h;
v[getIndex(i,j)] -= 0.5*(p[getIndex(i,j+1)]-p[getIndex(i,j-1)])/h;
}
}
set_bnd ( N, 1, u ); set_bnd ( N, 2, v );
//BuildingMngt.set_buildings(N, u, buildings);BuildingMngt.set_buildings(N, v, buildings);
}


static void set_bnd ( int N, int b, double[] x )
{
	//System.out.println("PRIMA");
	//draw_dens ( N, x );
int i;
for ( i=1 ; i<=N ; i++ ) {
x[getIndex(0 ,i)] = b==1 ? -x[getIndex(1,i)] : x[getIndex(1,i)];
x[getIndex(N+1,i)] = b==1 ? -x[getIndex(N,i)] : x[getIndex(N,i)];
x[getIndex(i,0 )] = b==2 ? -x[getIndex(i,1)] : x[getIndex(i,1)];
x[getIndex(i,N+1)] = b==2 ? -x[getIndex(i,N)] : x[getIndex(i,N)];
}
x[getIndex(0 ,0 )] = 0.5f*(x[getIndex(1,0 )]+x[getIndex(0 ,1)]);
x[getIndex(0 ,N+1)] = 0.5f*(x[getIndex(1,N+1)]+x[getIndex(0 ,N )]);
x[getIndex(N+1,0 )] = 0.5f*(x[getIndex(N,0 )]+x[getIndex(N+1,1)]);
x[getIndex(N+1,N+1)] = 0.5f*(x[getIndex(N,N+1)]+x[getIndex(N+1,N )]);

//System.out.println("dopo");
//draw_dens ( N, x );
}


/*
private void test(){
	
boolean simulating = true;


 init();

 System.out.println("size: "+size);


 
 
//while ( simulating )
 for(int i=0;i<105;i++)
{

	//draw_vel(N,u);
	//draw_vel(N,v_prev);
	 get_from_UI ( dens_prev, u_prev, v_prev,i );	
//vel_step ( Variables.N, u, v, u_prev, v_prev, visc, dt );

//draw_vel(N,v);

dens_step ( Variables.N, dens, dens_prev, u, v, diff, dt );

//System.out.println("draw dens");
//draw_dens ( N, dens );
//draw_vel(N,u);
//draw_vel(N,v);
}
}
*/

private void get_from_UI (double[] dens_prev,double[] u_prev,double[] v_prev ,int index){
	
	for(int i=0;i<dens_prev.length;i++){
		u_prev[i]=0;
		v_prev[i]=0;
		dens_prev[i]=0;
	}
	
	//if(index<5)
	dens_prev[getIndex(5, 5)]=3000;
	//u_prev[getIndex(5, 5)]=0;
	//v_prev[getIndex(2, 2)]=10f;
}

private static  void draw_dens (int n,double[] dens){
	System.out.println("----------------");
	for(int j=0;j<n+2;j++){
	for(int i=0;i<n+2;i++){
			System.out.print("  "+dens[getIndex(i, j)]);
	}
		System.out.println();
	}
	System.out.println("----------------");
}

private void draw_vel (int n,float[] v){
	System.out.println("----VELOCITY-------");
	for(int j=0;j<n+2;j++){
	for(int i=0;i<n+2;i++){
			System.out.print("  "+v[getIndex(i, j)]);
	}
		System.out.println();
	}
	System.out.println("----------------");
}


/*
public static void main(String[] args) {
	
	new fluid().test();
	
}
*/

public static void init(){
	  v = new double[Variables.size];
	  u = new double[Variables.size];
	 dens = new double[Variables.size];

	  dens_prev = new double[Variables.size];
	  u_prev = new double[Variables.size];
	  v_prev = new double[Variables.size];
	  
	  dens_src = new double[Variables.size];
	  u_src = new double[Variables.size];
	  v_src = new double[Variables.size];
	  
	  buildings = new double[Variables.size];
	  //v_buildings = new double[size];
	  
	  //Building b=BuildingMngt.createBuildings();		 
	  //BuildingMngt.init_buildingGrids(b,buildings);
	  //ArrayList<Building> bildings = BuildingMngt.createTrentoBuildings();
	  //BuildingMngt.init_trentoBuildingGrids(bildings, buildings);
	  BuildingMngt.init_trentoBuildingGrids( buildings);
}

public static void reset(){
	  v = new double[Variables.size];
	  u = new double[Variables.size];
	 dens = new double[Variables.size];
	  dens_prev = new double[Variables.size];
	  u_prev = new double[Variables.size];
	  v_prev = new double[Variables.size];
	  dens = new double[Variables.size];
	  u = new double[Variables.size];
	  v = new double[Variables.size];
}

public static void simulate(){
	
	
	 //updateInputVariables();
addStuff();
	// System.out.println("size: "+size);

	//
	
	//System.out.println("draw  dens 0 ");
	/*
	draw_dens ( N, dens );
	draw_dens ( N, v );
	draw_dens ( N, u );
	draw_dens ( N, v_prev );
	draw_dens ( N, u_prev );
	 */
//	draw_dens ( N, dens );
		//draw_vel(N,u);
		//draw_vel(N,v_prev);
		// get_from_UI ( dens_prev, u_prev, v_prev,i );	
	
	
	vel_step ( Variables.N, u, v, u_prev, v_prev, Variables.visc, Variables.dt );


//	draw_dens ( N, dens );

	
	dens_step ( Variables.N, dens, dens_prev, u, v, Variables.diff, Variables.dt );

//	draw_dens ( N, dens );
//	draw_dens ( N, dens );
	
	}

static public BufferWrapper getDensityBufferWrapper(){
	
	double[] values = new double[Variables.N*Variables.N];
	//System.out.println(".....");
	BufferFactory factory = new BufferFactory.DoubleBufferFactory();
	int i=0;
	for (int y = 0; y < Variables.N; y++)
    {       
        for (int x = 0; x < Variables.N; x++)
        {          
           
              //  values[x + y * N] = dens_prev[getIndex(x, y)];
        	double d = dens[getIndex(x, y)];
        	//double d = dens[getIndex(1, 1)];
        	 // values[x + y * Variables.N] = d;
        	 values[i] = d;
        	 i++;
        	 
        	// values[x + y * Variables.N] = 1;
  //  if(dens[getIndex(x, y)]!=0.0){
    //    	  System.out.println("dens[getIndex(x, y): "+dens[getIndex(x, y)]);
   // }
        }
    }
	
	BufferWrapper buffer = factory.newBuffer(Variables.N*Variables.N);
    //buffer.putDouble(0, dens_prev, 0, N*N);
	//buffer.putDouble(0, dens, 0, size);
	buffer.putDouble(0, values, 0, Variables.N*Variables.N);
	
    return buffer;
}


static public BufferWrapper getVelocityYBufferWrapper(){
	
	double[] values = new double[Variables.N*Variables.N];
	//System.out.println(".....");
	BufferFactory factory = new BufferFactory.DoubleBufferFactory();
	
	for (int y = 0; y < Variables.N; y++)
    {       
        for (int x = 0; x < Variables.N; x++)
        {          
           
              //  values[x + y * N] = dens_prev[getIndex(x, y)];
        	  values[x + y * Variables.N] = v[getIndex(x+1, y+1)];
  //  if(dens[getIndex(x, y)]!=0.0){
    //    	  System.out.println("dens[getIndex(x, y): "+dens[getIndex(x, y)]);
   // }
        }
    }
	
	BufferWrapper buffer = factory.newBuffer(Variables.N*Variables.N);
    //buffer.putDouble(0, dens_prev, 0, N*N);
	//buffer.putDouble(0, dens, 0, size);
	buffer.putDouble(0, values, 0, Variables.N*Variables.N);
	
    return buffer;
}

static public BufferWrapper getVelocityXBufferWrapper(){
	
	double[] values = new double[Variables.N*Variables.N];
	//System.out.println(".....");
	BufferFactory factory = new BufferFactory.DoubleBufferFactory();
	
	for (int y = 0; y < Variables.N; y++)
    {       
        for (int x = 0; x < Variables.N; x++)
        {          
           
              //  values[x + y * N] = dens_prev[getIndex(x, y)];
        	  values[x + y * Variables.N] = u[getIndex(x+1, y+1)];
  //  if(dens[getIndex(x, y)]!=0.0){
    //    	  System.out.println("dens[getIndex(x, y): "+dens[getIndex(x, y)]);
   // }
        }
    }
	
	BufferWrapper buffer = factory.newBuffer(Variables.N*Variables.N);
    //buffer.putDouble(0, dens_prev, 0, N*N);
	//buffer.putDouble(0, dens, 0, size);
	buffer.putDouble(0, values, 0, Variables.N*Variables.N);
	
    return buffer;
}

static public void initInputVariables(){
	
	for(int i=0;i<Variables.size;i++){
		//double r1=Math.random();
		//double r2=Math.random();
		//double r3=Math.random();
		u_prev[i]=0;
		v_prev[i]=0;
		dens_prev[i]= 0;
	}
	
}

static public void addStuff(){
	for(int i = 0; i<Variables.size; i++)
	{
		//if(dens_src[i]>0){
			//System.out.println("dens_src[i]>0: "+dens_src[i]);
		//}
			dens[i] += dens_src[i];
			dens_src[i] = 0;
		
		u[i] += u_src[i];
		v[i] += v_src[i];
		u_src[i] = 0;
		v_src[i] = 0;
	}
}

/*
static public void updateInputVariables(){
	
	if(Variables.inputPointList.size()!=0){
		
		for(Point p : Variables.inputPointList){
		// p = Variables.inputPoint;
			double v1=Math.random();
			
		dens_prev[getIndex(p.x, p.y)]=v1-dens[getIndex(p.x, p.y)];
		
		}
		
		
		if(Variables.inputVelPointList.size()!=0){
			
			for(PointVel p : Variables.inputVelPointList){
			// p = Variables.inputPoint;
			
				v_prev[getIndex(p.getPoint().x, p.getPoint().y)]+=p.getUVel();
				u_prev[getIndex(p.getPoint().x, p.getPoint().y)]+=p.getVVel();
			}
		}
			
	
		Variables.inputPointList=new ArrayList<Point>();
		Variables.inputVelPointList=new ArrayList<PointVel>();
	}
	
}
*/
/*
static public void updateInputVariables(int i){
	dens_src[i]=+200e3;
		
}
*/
static public void setDensSrc(int x,int y,double value){
	
	double res = value+0.2;
	
	if(res < 0) {res = 0;}
	if(res > 1) {res = 1;}
	
	dens_src[getIndex(x, y)] = res - dens[getIndex(x, y)];
	//System.out.println("nuovi valori inseriti: "+dens_src[getIndex(x, y)]);
}


static public void setVelSrc(int px,int py, int xMag, int yMag)
{
	int xSize = Variables.N+2;
	int ySize = Variables.N+2;
	
	double xVel = (double)xMag;
	double yVel = (double)yMag;
	
	int x1 = px;
	int y1 = py;
	
	int xRadius = 1;
	int yRadius = 1;
	
	int x0 = x1-xRadius;
	int y0 = y1-yRadius;
	
	int x2 = x1+xRadius;
	int y2 = y1+yRadius;
	
	if(x0 < 0) {x0 = 0;}
	if(x2 < 0) {x2 = 0;}
	if(y0 < 0) {y0 = 0;}
	if(y2 < 0) {y2 = 0;}
	if(x0 >= xSize) {x0 = xSize-1;}
	if(x2 >= xSize) {x2 = xSize-1;}
	if(y0 >= ySize) {y0 = ySize-1;}
	if(y2 >= ySize) {y2 = ySize-1;}
	
	for(int x = x0; x <= x2; x++)
	{
		for(int y = y0; y <= y2; y++)
		{
			int dx = x - x1;
			int dy = y - y1;
			//if is inside the circle
		//	System.out.println(xMag+" "+yMag);
			if((double)dx*dx*yRadius*yRadius + (double)dy*dy*xRadius*xRadius <= (double)xRadius*xRadius*yRadius*yRadius)
			{
				v_src[getIndex(px, py)]+=yVel;
				u_src[getIndex(px, py)]+=xVel;
			}
		}
	}
}
}