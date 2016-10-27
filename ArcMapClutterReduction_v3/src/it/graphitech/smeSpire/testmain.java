package it.graphitech.smeSpire;

import gov.nasa.worldwind.geom.Vec4;

public class testmain {
public static void main(String[] args) {
	Vec4 v0 = new Vec4(0.0, 0.5);
	Vec4 v1 = new Vec4(0.5, 0.5);
	Vec4 v1b = new Vec4(0.5, 0);
	Vec4 v2 = new Vec4(0.5, -0.5);
	Vec4 v2b = new Vec4(0, -0.5);
	Vec4 v3 = new Vec4(-0.5, -0.5);
	Vec4 v4 = new Vec4(-0.5, 0.5);
	
	double a0 =  Math.atan2(v0.x,v0.y);
	double a1 =  Math.atan2(v1.x,v1.y);
	double a1b =  Math.atan2(v1b.x,v1b.y);
	double a2 =  Math.atan2(v2.x,v2.y);
	double a2b =  Math.atan2(v2b.x,v2b.y);
	double a3 =  Math.atan2(v3.x,v3.y);
	double a4 =  Math.atan2(v4.x,v4.y);
	
	
	System.out.println(a0+" "+a1+" "+a1b+" "+a2+" "+a2b+" "+a3+" "+a4);
}
}
