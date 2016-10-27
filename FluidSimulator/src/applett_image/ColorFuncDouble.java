
package applett_image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * ColorFuncDouble.java
 * 
 * Class to perform various useful functions on colors and images, double version.
 * 
 * @author David Wu
 * @version 1.0, November 5, 2007
 */

public class ColorFuncDouble 
{
	public double f0;
	public double f1;
	public double f2;
	public double f3;
	
	private int[] argb;
	
	public static final int RGBA = 0;
	public static final int HSVA = 1;
	public static final int HSLA = 2;
	
	public void HSLAtoRGBA(double h, double s, double l)
	{
		if(s == 0)
		{
			f0 = l;
			f1 = l;
			f2 = l;
		}
		else
		{
			double q;
			if(l < 0.5)
			{q = l*(1.0+s);}
			else
			{q = l+s-l*s;}
			double p = 2.0*l-q;
			
			double t6 = 6.0*h + 2.0;
			t6 = t6%6.0;
			if(t6 < 1)
			{f0 = p + (q-p)*t6;}
			else if(t6 < 3)
			{f0 = q;}
			else if(t6 < 4)
			{f0 = p + (q-p)*(4.0-t6);}
			else
			{f0 = p;}
			
			t6 = 6.0*h;
			t6 = t6%6.0;
			if(t6 < 1)
			{f1 = p + (q-p)*t6;}
			else if(t6 < 3)
			{f1 = q;}
			else if(t6 < 4)
			{f1 = p + (q-p)*(4.0-t6);}
			else
			{f1 = p;}
			
			t6 = 6.0*h - 2.0 + 6.0;
			t6 = t6%6.0;
			if(t6 < 1)
			{f2 = p + (q-p)*t6;}
			else if(t6 < 3)
			{f2 = q;}
			else if(t6 < 4)
			{f2 = p + (q-p)*(4.0-t6);}
			else
			{f2 = p;}
		}
	}
	
	public void HSVAtoRGBA(double h, double s, double v)
	{
		h = h * 6.0;
		
		double mod = h%1.0;
		double hi = h-mod;
		double p = v*(1-s);
		double q = v*(1-mod*s);
		double t = v*(1-(1-mod)*s);
		
		double r,g,b;
		if(hi == 0.0)      {r = v; g = t; b = p;}
		else if(hi == 1.0) {r = q; g = v; b = p;}
		else if(hi == 2.0) {r = p; g = v; b = t;}
		else if(hi == 3.0) {r = p; g = q; b = v;}
		else if(hi == 4.0) {r = t; g = p; b = v;}
		else               {r = v; g = p; b = q;}
		
		f0 = r;
		f1 = g;
		f2 = b;		
	}
	
	public void RGBAtoHSLA(double r, double g, double b)
	{
		double max = Math.max(Math.max(r,g),b);
		double min = Math.min(Math.min(r,g),b);
		double diff = max-min;
		
		double l = (max+min)/2;
		double h;
		double s;
		
		if(diff == 0)
		{
			h = 0;
			s = 0;
		}
		else
		{
			s = (diff)/(2*Math.min(l,1.0-l));
			
			if(max == r)
			{h = ((g-b)/diff + 6)/6.0;}
			else if(max == g)
			{h = ((b-r)/diff + 2)/6.0;}
			else
			{h = ((r-g)/diff + 4)/6.0;}
			
			h = h % 1.0;
		}
		
		f0 = h;
		f1 = s;
		f2 = l;
	}
	
	public void RGBAtoHSVA(double r, double g, double b)
	{
		double max = Math.max(Math.max(r,g),b);
		double min = Math.min(Math.min(r,g),b);
		double diff = max-min;
			
		double h;
		double s;
		
		if(diff == 0)
		{
			h = 0;
			if(max == 0)
			{s = 0;}
			else
			{s = diff/max;}
		}
		else
		{
			s = s = diff/max;
			
			if(max == r)
			{h = ((g-b)/diff + 6)/6.0;}
			else if(max == g)
			{h = ((b-r)/diff + 2)/6.0;}
			else
			{h = ((r-g)/diff + 4)/6.0;}
			
			h = h % 1.0;
		}
		
		f0 = h;
		f1 = s;
		f2 = max;
	}
	
	public void toRGBA(int from, double h, double s, double x)
	{
		if(from == HSVA)
		{HSVAtoRGBA(h,s,x);}
		else if(from == HSLA)
		{HSLAtoRGBA(h,s,x);}
	}

	public void fromRGBA(int to, double r, double g, double b)
	{
		if(to == HSVA)
		{RGBAtoHSVA(r,g,b);}
		else if(to == HSLA)
		{RGBAtoHSLA(r,g,b);}
	}

	public void HSLAtoRGBA(double[] h, double[] s, double[] l, double[] r, double[] g, double[] b)
	{
		for(int i = 0; i<h.length; i++)
		{
			HSLAtoRGBA(h[i],s[i],l[i]);
			r[i] = f0;
			g[i] = f1;
			b[i] = f2;
		}
	}
	
	public void HSVAtoRGBA(double[] h, double[] s, double[] v, double[] r, double[] g, double[] b)
	{
		for(int i = 0; i<h.length; i++)
		{
			HSVAtoRGBA(h[i],s[i],v[i]);
			r[i] = f0;
			g[i] = f1;
			b[i] = f2;
		}
	}
	
	public void RGBAtoHSLA(double[] r, double[] g, double[] b, double[] h, double[] s, double[] l)
	{
		for(int i = 0; i<r.length; i++)
		{
			RGBAtoHSLA(r[i],g[i],b[i]);
			h[i] = f0;
			s[i] = f1;
			l[i] = f2;
		}
	}
	
	public void RGBAtoHSVA(double[] r, double[] g, double[] b, double[] h, double[] s, double[] v)
	{
		for(int i = 0; i<r.length; i++)
		{
			RGBAtoHSVA(r[i],g[i],b[i]);
			h[i] = f0;
			s[i] = f1;
			v[i] = f2;
		}
	}
	
	public double add(double f, double g)
	{return f+g;}
	
	public void add(double[] f, double[] g, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = f[i] + g[i];}
	}
	
	public double sub(double f, double g)
	{return f-g;}
	
	public void sub(double[] f, double[] g, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = f[i] - g[i];}
	}

	public double mult(double f, double g)
	{return f*g;}
	
	public void mult(double[] f, double[] g, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = f[i] * g[i];}
	}
	
	public double blend(double f, double g)
	{return f+g-f*g;}
	
	public void blend(double[] f, double[] g, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = f[i] + g[i] - f[i] * g[i];}
	}
	
	public double avg(double f, double g)
	{return (f+g)/2.0;}
	
	public void avg(double[] f, double[] g, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = (f[i] + g[i])/2.0;}
	}
	
	public double min(double f, double g)
	{return Math.min(f,g);}
	
	public void min(double[] f, double[] g, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = Math.min(f[i],g[i]);}
	}
	
	public double max(double f, double g)
	{return Math.max(f,g);}
	
	public void max(double[] f, double[] g, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = Math.max(f[i],g[i]);}
	}
	
	public void clear(double[] f, double val)
	{
		for(int i = 0; i<f.length; i++)
		{f[i] = val;}
	}
	
	public double magSq(double f, double g)
	{return f*f + g*g;}
	
	public void magSq(double[] f, double[] g, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = f[i]*f[i]+g[i]*g[i];}
	}
	
	public double magSq(double f, double g, double h)
	{return f*f + g*g + h*h;}
	
	public void magSq(double[] f, double[] g, double[] h, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = f[i]*f[i]+g[i]*g[i]+h[i]*h[i];}
	}
	
	public double mag(double f, double g)
	{return Math.sqrt(f*f + g*g);}
	
	public void mag(double[] f, double[] g, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = Math.sqrt(f[i]*f[i]+g[i]*g[i]);}
	}
	
	public double mag(double f, double g, double h)
	{return Math.sqrt(f*f + g*g + h*h);}
	
	public void mag(double[] f, double[] g, double[] h, double[] dest)
	{
		for(int i = 0; i<f.length; i++)
		{dest[i] = Math.sqrt(f[i]*f[i]+g[i]*g[i]+h[i]*h[i]);}
	}

	public void draw(Graphics gg, int xOffset, int yOffset, int xSize, int ySize, double[] r, double[] g, double[] b)
	{
		int i = 0;
		for(int y = yOffset; y<ySize+yOffset; y++)
		{
			for(int x = xOffset; x<xSize+xOffset; x++)
			{
				gg.setColor(new Color((int)(r[i]*255.0), (int)(g[i]*255.0), (int)(b[i]*255.0), 255));
				gg.drawLine(x,y,x,y);
				i++;
			}
		}
	}
	
	public void draw(Graphics gg, int xOffset, int yOffset, int xSize, int ySize, double[] r, double[] g, double[] b, double[] a)
	{
		int i = 0;
		for(int y = yOffset; y<ySize+yOffset; y++)
		{
			for(int x = xOffset; x<xSize+xOffset; x++)
			{
				gg.setColor(new Color((int)(r[i]*255.0), (int)(g[i]*255.0), (int)(b[i]*255.0), (int)(a[i]*255.0)));
				gg.drawLine(x,y,x,y);
				i++;
			}
		}
	}
	
	public void drawLerp(Graphics gg, int dx0, int dy0, int dx1, int dy1, int xSize, int ySize, 
			double[] r, double[] g, double[] b)
	{
		for(int y = dy0; y<dy1; y++)
		{
			double yProp = (y-dy0)*ySize/(dy1-dy0);
			int y0 = (int)Math.floor(yProp);
			int y1 = y0+1;
			yProp = yProp - y0;
			if(y1 >= ySize)
			{y1 = ySize-1;}
			
			for(int x = dx0; x<dx1; x++)
			{
				double xProp = (x-dx0)*xSize/(dx1-dx0);
				int x0 = (int)Math.floor(xProp);
				int x1 = x0+1;
				xProp = xProp-x1;
				if(x1 >= xSize)
				{x1 = xSize-1;}
				
				double rf = (1-yProp)*((1.0-xProp)*r[x0+xSize*y0] + xProp*r[x1+xSize*y0])
				             + yProp*((1.0-xProp)*r[x0+xSize*y1] + xProp*r[x1+xSize*y1]);
				double gf = (1-yProp)*((1.0-xProp)*g[x0+xSize*y0] + xProp*g[x1+xSize*y0])
	                         + yProp*((1.0-xProp)*g[x0+xSize*y1] + xProp*g[x1+xSize*y1]);
				double bf = (1-yProp)*((1.0-xProp)*b[x0+xSize*y0] + xProp*b[x1+xSize*y0])
	                         + yProp*((1.0-xProp)*b[x0+xSize*y1] + xProp*b[x1+xSize*y1]);
				
				gg.setColor(new Color((int)(rf*255.0), (int)(gf*255.0), (int)(bf*255.0), 255));
				gg.drawLine(x,y,x,y);
			}
		}
	}
	
	public void drawLerp(Graphics gg, int dx0, int dy0, int dx1, int dy1, int xSize, int ySize, 
			double[] r, double[] g, double[] b, double[] a)
	{
		for(int y = dy0; y<dy1; y++)
		{
			double yProp = (y-dy0)*ySize/(dy1-dy0);
			int y0 = (int)Math.floor(yProp);
			int y1 = y0+1;
			yProp = yProp - y0;
			if(y1 >= ySize)
			{y1 = ySize-1;}
			
			for(int x = dx0; x<dx1; x++)
			{
				double xProp = (x-dx0)*xSize/(dx1-dx0);
				int x0 = (int)Math.floor(xProp);
				int x1 = x0+1;
				xProp = xProp-x1;
				if(x1 >= xSize)
				{x1 = xSize-1;}
				
				double rf = (1-yProp)*((1.0-xProp)*r[x0+xSize*y0] + xProp*r[x1+xSize*y0])
				             + yProp*((1.0-xProp)*r[x0+xSize*y1] + xProp*r[x1+xSize*y1]);
				double gf = (1-yProp)*((1.0-xProp)*g[x0+xSize*y0] + xProp*g[x1+xSize*y0])
	                         + yProp*((1.0-xProp)*g[x0+xSize*y1] + xProp*g[x1+xSize*y1]);
				double bf = (1-yProp)*((1.0-xProp)*b[x0+xSize*y0] + xProp*b[x1+xSize*y0])
	                         + yProp*((1.0-xProp)*b[x0+xSize*y1] + xProp*b[x1+xSize*y1]);
				double af = (1-yProp)*((1.0-xProp)*a[x0+xSize*y0] + xProp*a[x1+xSize*y0])
	                         + yProp*((1.0-xProp)*a[x0+xSize*y1] + xProp*a[x1+xSize*y1]);
				
				gg.setColor(new Color((int)(rf*255.0), (int)(gf*255.0), (int)(bf*255.0), (int)(af*255.0)));
				gg.drawLine(x,y,x,y);
			}
		}
	}
	
	public void drawCLerp(Graphics gg, int dx0, int dy0, int dx1, int dy1, int xSize, int ySize, 
			double[] r, double[] g, double[] b)
	{
		for(int y = dy0; y<dy1; y++)
		{
			double yProp = (y-dy0)*ySize/(dy1-dy0);
			int y0 = (int)Math.floor(yProp);
			int y1 = y0+1;
			yProp = yProp - y0;
			if(y1 >= ySize)
			{y1 = ySize-1;}
			yProp = yProp*yProp*(3.0 - 2.0*yProp);
			
			for(int x = dx0; x<dx1; x++)
			{
				double xProp = (x-dx0)*xSize/(dx1-dx0);
				int x0 = (int)Math.floor(xProp);
				int x1 = x0+1;
				xProp = xProp-x1;
				if(x1 >= xSize)
				{x1 = xSize-1;}
				xProp = xProp*xProp*(3.0 - 2.0*xProp);
				
				double rf = (1.0-yProp)*((1.0-xProp)*r[x0+xSize*y0] + xProp*r[x1+xSize*y0])
				                + yProp*((1.0-xProp)*r[x0+xSize*y1] + xProp*r[x1+xSize*y1]);
				double gf = (1.0-yProp)*((1.0-xProp)*g[x0+xSize*y0] + xProp*g[x1+xSize*y0])
	                            + yProp*((1.0-xProp)*g[x0+xSize*y1] + xProp*g[x1+xSize*y1]);
				double bf = (1.0-yProp)*((1.0-xProp)*b[x0+xSize*y0] + xProp*b[x1+xSize*y0])
	                            + yProp*((1.0-xProp)*b[x0+xSize*y1] + xProp*b[x1+xSize*y1]);
				
				gg.setColor(new Color((int)(rf*255.0), (int)(gf*255.0), (int)(bf*255.0), 255));
				gg.drawLine(x,y,x,y);
			}
		}
	}
	
	public void drawCLerp(Graphics gg, int dx0, int dy0, int dx1, int dy1, int xSize, int ySize, 
			double[] r, double[] g, double[] b, double[] a)
	{
		for(int y = dy0; y<dy1; y++)
		{
			double yProp = (y-dy0)*ySize/(dy1-dy0);
			int y0 = (int)Math.floor(yProp);
			int y1 = y0+1;
			yProp = yProp - y0;
			if(y1 >= ySize)
			{y1 = ySize-1;}
			yProp = yProp*yProp*(3.0 - 2.0*yProp);
			
			for(int x = dx0; x<dx1; x++)
			{
				double xProp = (x-dx0)*xSize/(dx1-dx0);
				int x0 = (int)Math.floor(xProp);
				int x1 = x0+1;
				xProp = xProp-x1;
				if(x1 >= xSize)
				{x1 = xSize-1;}
				xProp = xProp*xProp*(3.0 - 2.0*xProp);
				
				double rf = (1.0-yProp)*((1.0-xProp)*r[x0+xSize*y0] + xProp*r[x1+xSize*y0])
				                + yProp*((1.0-xProp)*r[x0+xSize*y1] + xProp*r[x1+xSize*y1]);
				double gf = (1.0-yProp)*((1.0-xProp)*g[x0+xSize*y0] + xProp*g[x1+xSize*y0])
	                            + yProp*((1.0-xProp)*g[x0+xSize*y1] + xProp*g[x1+xSize*y1]);
				double bf = (1.0-yProp)*((1.0-xProp)*b[x0+xSize*y0] + xProp*b[x1+xSize*y0])
	                            + yProp*((1.0-xProp)*b[x0+xSize*y1] + xProp*b[x1+xSize*y1]);
				double af = (1.0-yProp)*((1.0-xProp)*a[x0+xSize*y0] + xProp*a[x1+xSize*y0])
	                            + yProp*((1.0-xProp)*a[x0+xSize*y1] + xProp*a[x1+xSize*y1]);
				
				gg.setColor(new Color((int)(rf*255.0), (int)(gf*255.0), (int)(bf*255.0), (int)(af*255.0)));
				gg.drawLine(x,y,x,y);
			}
		}
	}
	
	public BufferedImage getBufferedImage(int xSize, int ySize, double[] r, double[] g, double[] b)
	{
		BufferedImage img = new BufferedImage(xSize, ySize, BufferedImage.TYPE_4BYTE_ABGR);
		setBufferedImage(img,xSize,ySize,r,g,b);
		return img;
	}
	
	public BufferedImage getBufferedImage(int xSize, int ySize, double[] r, double[] g, double[] b, double[] a)
	{
		BufferedImage img = new BufferedImage(xSize, ySize, BufferedImage.TYPE_4BYTE_ABGR);
		setBufferedImage(img,xSize,ySize,r,g,b,a);
		return img;
	}

	public void setBufferedImage(BufferedImage img, int xSize, int ySize, double[] r, double[] g, double[] b)
	{
		int i = 0;
		for(int y = 0; y<ySize; y++)
		{
			for(int x = 0; x<xSize; x++)
			{
				int ri = (int)(r[i]*255.0);
				int gi = (int)(g[i]*255.0);
				int bi = (int)(b[i]*255.0);
				int ai = 255;
				img.setRGB(x, y, (ai << 24) | (ri << 16) | (gi << 8) | bi);
				i++;
			}
		}
	}
	
	public void setBufferedImage(BufferedImage img, int xSize, int ySize, double[] r, double[] g, double[] b, double[] a)
	{
		int i = 0;
		for(int y = 0; y<ySize; y++)
		{
			for(int x = 0; x<xSize; x++)
			{
				int ri = (int)(r[i]*255.0);
				int gi = (int)(g[i]*255.0);
				int bi = (int)(b[i]*255.0);
				int ai = (int)(a[i]*255.0);
				img.setRGB(x, y, (ai << 24) | (ri << 16) | (gi << 8) | bi);
				i++;
			}
		}
	}
	
	public void setBufferedImageLerp(BufferedImage img, int dx0, int dy0, int dx1, int dy1, int xSize, int ySize, 
			double[] r, double[] g, double[] b)
	{
		if(argb == null || argb.length < (dx1-dx0)*(dy1-dy0))
		{
			argb = new int[(dx1-dx0)*(dy1-dy0)];
		}
		int i = 0;
		for(int y = dy0; y<dy1; y++)
		{
			double yProp = (double)(y-dy0)*(ySize-1)/(dy1-dy0);
			int y0 = (int)(yProp);
			int y1 = y0+1;
			yProp = yProp - y0;
			int y0Index = xSize*y0;
			int y1Index = xSize*y1;
			double yPropInv = 1.0-yProp;
			
			for(int x = dx0; x<dx1; x++)
			{
				double xProp = (double)(x-dx0)*(xSize-1)/(dx1-dx0);
				int x0 = (int)(xProp);
				int x1 = x0+1;
				xProp = xProp - x0;
				double xPropInv = 1.0-xProp;
				
				int k00 = x0+y0Index;
				int k10 = x1+y0Index;
				int k01 = x0+y1Index;
				int k11 = x1+y1Index;
				
				double rf = yPropInv*(xPropInv*r[k00] + xProp*r[k10])
                             + yProp*(xPropInv*r[k01] + xProp*r[k11]);
				double gf = yPropInv*(xPropInv*g[k00] + xProp*g[k10])
                             + yProp*(xPropInv*g[k01] + xProp*g[k11]);
				double bf = yPropInv*(xPropInv*b[k00] + xProp*b[k10])
                             + yProp*(xPropInv*b[k01] + xProp*b[k11]);
				
				argb[i] = (255 << 24 | (int)(rf*255.0) << 16) | ((int)(gf*255.0) << 8) | (int)(bf*255.0);
				i++;
			}
		}
		img.setRGB(dx0,dy0,dx1-dx0,dy1-dy0,argb,0,dx1-dx0);
	}
	
	public void setBufferedImage(BufferedImage img, int dx0, int dy0, int dx1, int dy1, int xSize, int ySize, 
			double[] r, double[] g, double[] b)
	{
		if(argb == null || argb.length < (dx1-dx0)*(dy1-dy0))
		{
			argb = new int[(dx1-dx0)*(dy1-dy0)];
		}
		int i = 0;
		for(int y = dy0; y<dy1; y++)
		{
			int y0 = (y-dy0)*(ySize)/(dy1-dy0);
			int yIndex = xSize*y0;
			for(int x = dx0; x<dx1; x++)
			{
				int x0 = (x-dx0)*(xSize)/(dx1-dx0);
				int k = x0+yIndex;
				
				argb[i] = (255 << 24 | (int)(r[k]*255.0) << 16) | ((int)(g[k]*255.0) << 8) | (int)(b[k]*255.0);
				i++;
			}
		}
		img.setRGB(dx0,dy0,dx1-dx0,dy1-dy0,argb,0,dx1-dx0);
	}
	
	public void normalize(double[] f)
	{
		for(int i = 0; i<f.length; i++)
		{
			if(f[i] < 0.0)
			{f[i] = 0.0;}
			else if(f[i] > 1.0)
			{f[i] = 1.0;}
		}
	}
}
