package applett;

/**
 * Fluidfield.java
 * 
 * Internal respresentation of the fluid, handles all the calculations, using a iterative linear solver.
 * 
 * Uses the method described by Jos Stam in http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf,
 * with the addition of vorticity confinement and an overrelaxation factor on the linear solver to improve the
 * rate of convergence. Credits to Jos Stam for the fluid simulation algorithm.
 * 
 * @author David Wu
 * @version 1.0, November 5, 2007
 */

public class FluidField 
{	
	private double[] xVel;
	private double[] yVel;
	private double[] xVelP;
	private double[] yVelP;
	private double[][] layers;
	private double[][] layersP;
	private double[] curl;
	private double[] curlAbs;
	private double[][] layerSrc;
	private double[] xVelSrc;
	private double[] yVelSrc;
	
	private int xSize;
	private int ySize;
	private int kSize;
	private int numLayers;
	
	private double viscosity;
	private double diffusion;
	private double vorticity;
	private double timestep;
	private int iterations;
	
	private boolean cheapDiffusion;
	
	public FluidField(int x, int y, int l, boolean cheap)
	{this(x,y,new double[l][x*y], new double[x*y], new double[x*y], 0, 0, 1, 1, 20, cheap);}
	
	public FluidField(int x, int y, double visc, double diff, double vort, double time, int iter, boolean cheap)
	{this(x,y,new double[1][x*y], new double[x*y], new double[x*y], visc, diff, vort, time, iter, cheap);}
	
	public FluidField(int x, int y, double[] layer, double visc, double diff, double vort, double time, int iter, boolean cheap)
	{this(x,y,new double[][] {layer}, new double[x*y], new double[x*y], visc, diff, vort, time, iter, cheap);}
	
	public FluidField(int x, int y, double[][] layers, double[] xVel, double[] yVel, 
			double visc, double diff, double vort, double time, int iter, boolean cheap)
	{
		xSize = x;
		ySize = y;
		kSize = x*y;
		numLayers = layers.length;
		
		this.layers = layers;
		this.xVel = xVel;
		this.yVel = yVel;
		
		xVelP = xVel.clone();
		yVelP = yVel.clone();
		layersP = new double[layers.length][];
		for(int i = 0; i<layers.length; i++)
		{layersP[i] = layers[i].clone();}
		
		curl = new double[kSize];
		curlAbs = new double[kSize];
		layerSrc = new double[numLayers][kSize];
		xVelSrc = new double[kSize];
		yVelSrc = new double[kSize];
		
		viscosity = visc;
		diffusion = diff;
		vorticity = vort;
		timestep = time;
		iterations = iter;
		cheapDiffusion = cheap;
	}
	
	public final int getXSize()
	{return xSize;}
	
	public final int getYSize()
	{return ySize;}
	
	public final int getKSize()
	{return kSize;}
	
	public final int getNumLayers()
	{return numLayers;}
	
	public final int getK(int x, int y)
	{return x + y*xSize;}
	
	public double[] getXVel()
	{return xVel;}
	
	public double[] getYVel()
	{return yVel;}
	
	public double[] getLayer(int i)
	{return layers[i];}
	
	public double getTimeStep()
	{return timestep;}
	
	public double getDiffusion()
	{return diffusion;}
	
	public double getViscosity()
	{return viscosity;}
	
	public double getVorticity()
	{return vorticity;}
	
	public int getIterations()
	{return iterations;}
	
	public boolean isCheap()
	{return cheapDiffusion;}
	
	public void setTimeStep(double step)
	{timestep = step;}
	
	public void setDiffusion(double diff)
	{diffusion = diff;}
	
	public void setViscosity(double visc)
	{viscosity = visc;}
	
	public void setVorticity(double vort)
	{vorticity = vort;}
	
	public void setIterations(int iter)
	{iterations = iter;}
	
	public void setCheap(boolean cheap)
	{cheapDiffusion = cheap;}
	
	public void setInk(int layer, int k, double value)
	{
		
		layerSrc[layer][k] = value - layers[layer][k];
		
		System.out.println("nuovi valori inserito "+layerSrc[layer][k]);
	}
	
	public void addVel(int k, double xValue, double yValue)
	{
		xVelSrc[k] += xValue;
		yVelSrc[k] += yValue;
	}
	
	private   void draw_dens (int n,double[][] dens){
		System.out.println("----------------");
		for(int j=0;j<n;j++){
		for(int i=0;i<n;i++){
				System.out.print("  "+dens[0][getK(i, j)]);
		}
			System.out.println();
		}
		System.out.println("----------------");
	}
	
	private   void draw_dens (int n,double[] dens){
		System.out.println("----------------");
		for(int j=0;j<n;j++){
		for(int i=0;i<n;i++){
				System.out.print("  "+dens[getK(i, j)]);
		}
			System.out.println();
		}
		System.out.println("----------------");
	}
	
	public void step()
	{
		//System.out.println("---------gdxfhxdfjnxdfjn------------");
	/*
		System.out.println("diffusion: "+ diffusion);
		System.out.println("viscosity: "+viscosity); 
		System.out.println("timestep: "+ timestep);
		*/
		//draw_dens(xSize,layers);
		
		addStuff();
		
		//draw_dens(xSize,xVel);
		velocityStep();
		
		//draw_dens(xSize,xVel);
		densityStep();
		
		//draw_dens(xSize,xVel);
	}
	
	private void addStuff()
	{
		for(int i = 0; i<layers.length; i++)
		{
			for(int k = 0; k<layers[i].length; k++)
			{
				layers[i][k] += layerSrc[i][k];
				layerSrc[i][k] = 0;
			}
		}
		
	
		for(int k = 0; k<kSize; k++)
		{
			xVel[k] += xVelSrc[k];
			yVel[k] += yVelSrc[k];
			xVelSrc[k] = 0;
			yVelSrc[k] = 0;
		}
		
	}
	
	private void stupidSolve(double[] dest, double[] src, int b, double a)
    {
		for (int y = 1; y < ySize-1; y++)
        {
			int yIndex = y*xSize;
	        for (int x = 1; x < xSize-1; x++)
	        {
	        	int k = x+yIndex;
                dest[k] = (a*(src[k-1] + src[k+1] + src[k-xSize] + src[k+xSize]) + src[k])/(1+4*a);
            }
        }
        setBounds(b, dest);
    }
	
	//Improved gauss-siedel by adding relaxation factor. Overrelaxation at 1.5 seems strong, and higher values
	//create small-scale instablity (mixing) but seem to produce reasonable incompressiblity even faster.
	//4-10 iterations is good for real-time, and not noticably inaccurate. For real accuracy, upwards of 20 is good.
	private void linearSolve(double[] dest, double[] src, int b, double a, double c)
    {
		double wMax = 1.9;
		double wMin = 1.5;
        for (int i = 0; i < iterations; i++)
        {
        	double w = Math.max((wMin-wMax)*i/60.0+wMax,wMin);
        	for (int y = 1; y < ySize-1; y++)
            {
        		int yIndex = y*xSize;
	            for (int x = 1; x < xSize-1; x++)
	            {
	            	int k = x+yIndex;
                    dest[k] = dest[k] + w*((a * (dest[k-1] + dest[k+1] + dest[k-xSize] + dest[k+xSize]) + src[k]) / c - dest[k]);
                    //dest[getK(x, y)] = (a * (dest[getK(x-1,y)] + dest[getK(x+1,y)] + dest[getK(x,y-1)] + dest[getK(x,y+1)]) + src[getK(x,y)]) / c;
                }
            }
            setBounds(b, dest);
        }
    }
	
	private void diffuse(double[] dest, double[] src, int b, double diff, double dt)
	{
		double a = dt*diff;
		if(cheapDiffusion)
		{stupidSolve(dest,src,b,a);}
		else
		{linearSolve(dest, src, b, a, 1 + 4*a);}
	}
	
	
	
	private void advect(double[] dest, double[] src, double[] xVelocity, double[] yVelocity, int b, double dt)
	{	
		
		//System.out.println("DT: "+dt);
		for(int y = 1; y < ySize-1; y++)
		{
			int yIndex = y*xSize;
			for (int x = 1; x < xSize-1; x++) 
			{
				int k = x + yIndex;
                //Reverse velocity, since we are interpolating backwards
                //xSrc and ySrc is the position of the source density.
				double xSrc = x-dt*xVelocity[k]; 
				double ySrc = y-dt*yVelocity[k];
				
				if(xSrc < 0.5) {xSrc = 0.5;} 
				if(xSrc > xSize - 1.5) {xSrc = xSize - 1.5;}
				int xi0 = (int)xSrc; 
				int xi1 = xi0+1;
				
				if(ySrc < 0.5) {ySrc = 0.5;}
				if(ySrc > ySize - 1.5) {ySrc = ySize - 1.5;} 
				int yi0 = (int)ySrc; 
				int yi1 = yi0+1;
				
				//Linear interpolation factors. Ex: 0.6 and 0.4
				double xProp1 = xSrc-xi0;
				double xProp0 = 1.0-xProp1; 
				double yProp1 = ySrc-yi0; 
				double yProp0 = 1.0-yProp1;
				
				//System.out.println(xProp1+" "+xProp0+" "+yProp1+" "+yProp0);
				
				if(xProp0==0){
					System.out.println("xProp0");
				}
				if(yProp0==0){
					System.out.println("yProp0");
				}
				if(yProp1==0){
					System.out.println("yProp1");
				}
				if(xProp1==0){
					System.out.println("xProp1");
				}
				
				dest[k] = 
					xProp0*(yProp0*src[getK(xi0,yi0)] + yProp1*src[getK(xi0,yi1)]) +
					xProp1*(yProp0*src[getK(xi1,yi0)] + yProp1*src[getK(xi1,yi1)]);
				
			}
		}
		setBounds(b, dest);
	}
	
	/*
 private void advect(int N,double[] src, double[] dest , double[] u, double[] v,int b, double dt ){
		
		int i,j,xi0,yi0,xi1,yi1;
		double xSrc,ySrc,xProp0,yProp0,xProp1,yProp1,dt0;
		
		dt0 = dt;
		//dt0 = dt;
		for(i=1;i<=N-1;i++){
			int yIndex = i*N;
			for(j=1;j<=N-1;j++){
				int k = j + yIndex;
				
				xSrc = i-dt0*u[getK(i,j)]; 
				ySrc = j-dt0*v[getK(i,j)];
				
				
				if (xSrc<0.5) xSrc=0.5f;
				if (xSrc>N-1.5) xSrc=N-1.5f; 
				xi0=(int)xSrc; 
				xi1=xi0+1;
				
				if (ySrc<0.5) ySrc=0.5f; 
				if (ySrc>N-1.5) ySrc=N- 1.5f; 
				yi0=(int)ySrc; 
				yi1=yi0+1;
				
				xProp1 = xSrc-xi0; 
				xProp0 = 1.0-xProp1; 
				yProp1 = ySrc-yi0; 
				yProp0 = 1.0-yProp1;
				
				dest[k] = 
				//dest[getK(i,j)] = 
					xProp0*(yProp0*src[getK(xi0,yi0)]+yProp1*src[getK(xi0,yi1)])+
					xProp1*(yProp0*src[getK(xi1,yi0)]+yProp1*src[getK(xi1,yi1)]);
				
			
		//		System.out.println("index: "+i0+" "+i1+" "+j0+" "+j1);
				
			//	System.out.println("d[]"+d0[getIndex(i0,j0)]);
				
			//	System.out.println(dest[getIndex(i,j)]+" = "+
				//		xProp0+"*"+"("+yProp0+"*"+src[getIndex(xi0,yi0)]+"+"+yProp1+"*"+src[getIndex(xi0,yi1)]+")"+"+"+
					//	xProp1+"*"+"("+yProp0+"*"+src[getIndex(xi1,yi0)]+"+"+yProp1+"*"+src[getIndex(xi1,yi1)]+")");
		
			}
		}
		//System.out.println("prima di bound");
		//draw_dens ( N, dest );
		setBounds (  b, dest );
		//System.out.println("dopo di bound");
		//draw_dens ( N, dest );
	}
	*/
	
	private void project(double[] xV, double[] yV, double[] p, double[] div)
	{
		double h = 0.1;///(xSize-2);
		for(int y = 1; y < ySize-1; y++) 
		{
			int yIndex = y*xSize;
			for(int x = 1; x < xSize-1; x++)
			{
				int k = x + yIndex;
				//Negative divergence
				div[k] = -0.5 * h * (xV[k+1] - xV[k-1] + yV[k+xSize] - yV[k-xSize]);
				//Pressure field
				p[k] = 0;
			}
		}
		setBounds(0, div); 
		setBounds(0, p);
		
		linearSolve(p,div,0,1,4);
		
		for(int y = 1; y < ySize-1; y++) 
		{
			int yIndex = y*xSize;
			for(int x = 1; x < xSize-1; x++)
			{
				int k = x + yIndex;
				xV[k] -= 0.5*(p[k+1]-p[k-1])/h;
				yV[k] -= 0.5*(p[k+xSize]-p[k-xSize])/h;
			}
		}
		setBounds(1, xV); 
		setBounds(2, yV);
	}
	
	private void vorticityConfinement(double[] xForce, double[] yForce)
    {
        //Calculate magnitude of curl(u,v) for each cell. (|w|)
		for (int y = 1; y < ySize-1; y++)
        {
			int yIndex = y*xSize;
			for (int x = 1; x < xSize-1; x++)
	        {
				int k = x + yIndex;
				double du_dy = (xVel[k+xSize] - xVel[k-xSize]) * 0.5f;
                double dv_dx = (yVel[k+1] - yVel[k-1]) * 0.5f;
            	//double du_dy = (xVel[getK(x, y + 1)] - xVel[getK(x, y - 1)]) * 0.5f;
                //double dv_dx = (yVel[getK(x + 1, y)] - yVel[getK(x - 1, y)]) * 0.5f;

                // curl =  du_dy - dv_dx;
                curl[k] = du_dy - dv_dx;
                curlAbs[k] = Math.abs(curl[k]);
            }
        }

		for (int y = 2; y < ySize-2; y++)
        {
			int yIndex = y*xSize;
	        for (int x = 2; x < xSize-2; x++)
	        {
	        	int k = x + yIndex;
                // Find derivative of the magnitude (n = del |w|)
	        	double dw_dx = (curlAbs[k+1] - curlAbs[k-1]) * 0.5f;
                double dw_dy = (curlAbs[k+xSize] - curlAbs[k-xSize]) * 0.5f;
                //double dw_dx = (curlAbs[getK(x + 1, y)] - curlAbs[getK(x - 1, y)]) * 0.5f;
                //double dw_dy = (curlAbs[getK(x, y + 1)] - curlAbs[getK(x, y - 1)]) * 0.5f;

                // Calculate vector length. (|n|)
                // Add small factor to prevent divide by zeros.
                double length = Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.000001;

                // N = ( n/|n| )
                dw_dx /= length;
                dw_dy /= length;

                double v = curl[k];

                // N x w
                xForce[k] = dw_dy * -v * vorticity;
                yForce[k] = dw_dx *  v * vorticity;
            }
        }
    }
	
	public void densityStep()
	{
		if(timestep <= 0)
		{return;}
		
		double[] temp;
		for(int i = 0; i<layers.length; i++)
		{
			
			
			if(diffusion > 0)
			{
				temp = layersP[i];
				layersP[i] = layers[i];
				layers[i] = temp;
				
				diffuse(layers[i], layersP[i], 0, diffusion, timestep);
			}
			
			
			
			
			temp = layersP[i];
			layersP[i] = layers[i];
			layers[i] = temp;
			
			//System.out.println("prima");
			//printMinMaxDensity(xSize,layers[i]);	
			
			advect(layers[i], layersP[i], xVel, yVel, 0, timestep);
			
			//System.out.println("dopo");
			//printMinMaxDensity(xSize,layers[i]);	
			
			}
	}
	
	
	private   void printMinMaxDensity(int n,double[] dens){
		
		double min = 100;
		double max= -100;
		
		System.out.println("----------------");
		//for(int j=0;j<n+2;j++){
		//for(int i=0;i<n+2;i++){
		for(int j=0;j<n;j++){
			for(int i=0;i<n;i++){
				//System.out.print("  "+dens[getIndex(i, j)]);
				if(dens[getK(i, j)]<min){
					min=dens[getK(i, j)];
				}
				if(dens[getK(i, j)]>max){
					max=dens[getK(i, j)];
				}
				
		}
			
		}
		System.out.println("min: "+min+" ,max: "+max);
		System.out.println("----------------");
		
	}
	
	public void velocityStep()
	{
		double[] temp;
		
		
		if(vorticity > 0)
		{
			vorticityConfinement(xVelP, yVelP);
			addSource(xVel, xVelP);
			addSource(yVel, yVelP);
		}
		
		if(viscosity > 0)
		{
			temp = xVelP;
			xVelP = xVel;
			xVel = temp;
			
			temp = yVelP;
			yVelP = yVel;
			yVel = temp;

			diffuse(xVel, xVelP, 1, viscosity, timestep);
			diffuse(yVel, yVelP, 2, viscosity, timestep);
			slow(xVel, viscosity);
			slow(yVel, viscosity);
			project(xVel, yVel, xVelP, yVelP);
		}
		
		
		temp = xVelP;
		xVelP = xVel;
		xVel = temp;
		
		temp = yVelP;
		yVelP = yVel;
		yVel = temp;
		
		advect(xVel, xVelP, xVelP, yVelP, 1, timestep);	
		advect(yVel, yVelP, xVelP, yVelP, 2, timestep);	
		project(xVel, yVel, xVelP, yVelP);
	}
	
	private void slow(double[] d, double visc)
	{
		double factor = 1.0/(visc*timestep/20.0+1.0);
		for(int i = 0; i<d.length; i++)
		{
			d[i] *= factor;
		}
	}
	
	private void addSource(double[] x, double[] x0)
    {
        for (int i = 0; i < x.length; i++)
        {
            x[i] += timestep * x0[i];
        }
    }
	
	public void setBounds(int b, double[] d)
	{
		if(b == 1)
		{
			for(int y = 0; y < ySize; y++) 
			{
				d[getK(0,y)] = 0;
				d[getK(xSize-1,y)] = 0;	
			}
			for(int x = 1; x < xSize-1; x++) 
			{
				d[getK(x,0)] = d[getK(x,1)];
				d[getK(x,ySize-1)] = d[getK(x,ySize-2)];
			}
		}
		else if(b == 2)
		{
			for(int x = 0; x < xSize; x++) 
			{
				d[getK(x,0)] = 0;
				d[getK(x,ySize-1)] = 0;	
			}
			for(int y = 1; y < ySize-1; y++) 
			{
				d[getK(0,y)] = d[getK(1,y)];
				d[getK(xSize-1,y)] = d[getK(xSize-2,y)];	
			}
		}
		else
		{
			for(int x = 1; x < xSize-1; x++) 
			{
				d[getK(x,0)] = d[getK(x,1)];
				d[getK(x,ySize-1)] = d[getK(x,ySize-2)];	
			}
			for(int y = 1; y < ySize-1; y++) 
			{
				d[getK(0,y)] = d[getK(1,y)];
				d[getK(xSize-1,y)] = d[getK(xSize-2,y)];	
			}

			d[getK(0,0)] = 0.5*(d[getK(0,1)]+d[getK(1,0)]);
			d[getK(0,ySize-1)] = 0.5*(d[getK(1,ySize-1)]+d[getK(0,ySize-2)]);
			d[getK(xSize-1,0)] = 0.5*(d[getK(xSize-1,1)]+d[getK(xSize-2,0)]);
			d[getK(xSize-1,ySize-1)] = 0.5*(d[getK(xSize-1,ySize-2)]+d[getK(xSize-2,ySize-1)]);
		}
		
	}
	
	
}
