package it.graphitech.colors;

public class main {

	public static void main(String[] args) {
		/*
		double[] q00 = {0,0};		
		double[] q01 = {0,1};		
		double[] q10 = {1,0};		
		double[] q11 = {1,1};
		*/
		/*
		double[] q000 = {1,1,1};
		double[] q001 = {1,1,0};
		double[] q010 = {0.163,0.373,0.6};
		double[] q011 = {0,0.66,0.2};
		double[] q100 = {1,0,0};
		double[] q101 = {1,0.5,0};
		double[] q110 = {0.5,0.5,0};
		double[] q111 = {0.2,0.094,0};
		*/
		/*
		double[] q000 = {0,0,0};
		double[] q001 = {0,0,1};
		double[] q010 = {0,1,0};
		double[] q011 = {0,1,1};
		double[] q100 = {1,0,0};
		double[] q101 = {1,0,1};
		double[] q110 = {1,1,0};
		double[] q111 = {1,1,1};
		*/
		/*
		double x = 0.5;
		double y = 0.5;
		*/
		
		//double[] res = ConvertColors.RGBtoRYB(x, y, z, q000, q001, q010, q011, q100, q101, q110, q111);
		//System.out.println( res[0]/4+" - "+res[1]/4+ " - "+ res[2]/4);
		double r = 1.0;
		double g = 0.5;
		double b = 0.25;
		
		double[] res = ConvertColors.RYBtoRGB(r,g,b);
		System.out.println( res[0]+" - "+res[1]+" - "+res[2]);
	//
	}
	
}
