package it.graphitech.colors;

import java.awt.Color;

public class ConvertColors {
	

		public static double[] RYBtoRGB(double iR, double iY, double iB)
		{
			double oR,oG,oB;
			double x0, x1, x2, x3, y0, y1;
			//red
			x0 = cubicInt(iB, 1.0f, 0.163f);
			x1 = cubicInt(iB, 1.0f, 0.0f);
			x2 = cubicInt(iB, 1.0f, 0.5f);
			x3 = cubicInt(iB, 1.0f, 0.2f);
			y0 = cubicInt(iY, x0, x1);
			y1 = cubicInt(iY, x2, x3);
			oR = cubicInt(iR, y0, y1);
			//green
			x0 = cubicInt(iB, 1.0f, 0.373f);
			x1 = cubicInt(iB, 1.0f, 0.66f);
			x2 = cubicInt(iB, 0.0f, 0.0f);
			x3 = cubicInt(iB, 0.5f, 0.094f);
			y0 = cubicInt(iY, x0, x1);
			y1 = cubicInt(iY, x2, x3);
			oG = cubicInt(iR, y0, y1);
			//blue
			x0 = cubicInt(iB, 1.0f, 0.6f);
			x1 = cubicInt(iB, 0.0f, 0.2f);
			x2 = cubicInt(iB, 0.0f, 0.5f);
			x3 = cubicInt(iB, 0.0f, 0.0f);
			y0 = cubicInt(iY, x0, x1);
			y1 = cubicInt(iY, x2, x3);
			oB = cubicInt(iR, y0, y1);
			int r= (int)oR;
			int g= (int)oG;
			int b= (int)oB;
			/*
			return new Color(
					r,
					g,
					b);
					*/
			return new double[]{oR,oG,oB};
		}
		
		public static Color RYBtoRGB_Color(double iR, double iY, double iB){
			double[] cols=RYBtoRGB(iR/255,iY/255,iB/255);
			return new Color((int)(255*cols[0]),(int)(255*cols[1]),(int)(255*cols[2]));
}
		
		//Perform a biased (non-linear) interpolation between values A and B
		//using t as the interpolation factor.
		static private double cubicInt(double t, double A, double B){
			double weight = t*t*(3-2*t);
		return A + weight*(B-A);
		}

}