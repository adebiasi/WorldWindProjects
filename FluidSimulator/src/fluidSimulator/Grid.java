package fluidSimulator;

public class Grid {

	static int n;
	static int[] u,v,u_prev,v_prev,dens,dens_prev;
	
	public Grid(int n) {
		super();
		this.n= n;
	}
	
	public static int getU(int i,int j){
		return u[(i)+(n+2)*(j)];
	}
	
	public static int getV(int i,int j){
		return v[(i)+(n+2)*(j)];
	}
	
	public static int getUprev(int i,int j){
		return u_prev[(i)+(n+2)*(j)];
	}
	
	public static int getVprev(int i,int j){
		return v_prev[(i)+(n+2)*(j)];
	}
	
	public static int getDens(int i,int j){
		return dens[(i)+(n+2)*(j)];
	}
	
	public static int getDens_prev(int i,int j){
		return dens_prev[(i)+(n+2)*(j)];
	}
	
	
	public static void setU(int i,int j,int value){
		 u[(i)+(n+2)*(j)]=value;
	}
	
	public static void setV(int i,int j,int value){
		 v[(i)+(n+2)*(j)]=value;
	}
	
	public static void setUprev(int i,int j,int value){
		u_prev[(i)+(n+2)*(j)]=value;
	}
	
	public static void setVprev(int i,int j,int value){
		v_prev[(i)+(n+2)*(j)]=value;
	}
	
	public static void setDens(int i,int j,int value){
		dens[(i)+(n+2)*(j)]=value;
	}
	
	public static void setDens_prev(int i,int j,int value){
		dens_prev[(i)+(n+2)*(j)]=value;
	}
}
