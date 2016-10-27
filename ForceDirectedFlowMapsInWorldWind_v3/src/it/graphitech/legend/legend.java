package it.graphitech.legend;

import it.graphitech.colors.ConvertColors;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;

public class legend extends Frame
{
	int radius = 100;
	int yRadius=90;
	static int  initNumCircles=10;
	static int  numCircles=10;
	int h=0;
	int x=radius/2;
	int starting_y = 1000;
	
	  public legend()
	  {
	    setTitle("Special Effects");
	    setSize(2000,2000);
	    setVisible(true);
	  }
	  public void paint(Graphics g)
	  {		
		//  Color c1 =getColor(10,10,10);
		//   g.setColor(c1);
		//  g.fillOval(25,starting_y,radius,radius); // to draw circle
		  
		  int rc=0;
		  int gc=0;
		  int bc=0;
		  
		  double delta = 255.0/(double)numCircles;
		  
		  
		  for(int i=0;i<=numCircles;i++){	
			   rc=i;
			   Color c =getColor(rc,numCircles-rc,0);
			   g.setColor(c);
			  g.fillOval(50+(i*radius),starting_y,radius,radius); // to draw circle
		  }
		  
		  numCircles--;
		  h++;
		  for(int i=0;i<=numCircles;i++){	 
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,1);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
		  
		  numCircles--;
		  h++;
		  x=x+radius/2;
		  for(int i=0;i<=numCircles;i++){	
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,2);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
		  
		  numCircles--;
		  h++;
		  x=x+radius/2;
		  for(int i=0;i<=numCircles;i++){	
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,3);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
		  
		  numCircles--;
		  h++;
		  x=x+radius/2;
		  for(int i=0;i<=numCircles;i++){
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,4);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
		  
		  numCircles--;
		  h++;
		  x=x+radius/2;
		  for(int i=0;i<=numCircles;i++){	 
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,5);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
		  
		  numCircles--;
		  h++;
		  x=x+radius/2;
		  for(int i=0;i<=numCircles;i++){	
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,6);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
		  
		  numCircles--;
		  h++;
		  x=x+radius/2;
		  for(int i=0;i<=numCircles;i++){	
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,7);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
		  
		  numCircles--;
		  h++;
		  x=x+radius/2;
		  for(int i=0;i<=numCircles;i++){
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,8);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
		  
		  numCircles--;
		  h++;
		  x=x+radius/2;
		  for(int i=0;i<=numCircles;i++){
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,9);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
		  
		  numCircles--;
		  h++;
		  x=x+radius/2;
		  for(int i=0;i<=numCircles;i++){
			  rc=i;
			   Color c =getColor(rc,numCircles-rc,9);
		  g.setColor(c);
			    g.fillOval(50+x+(i*radius),starting_y-(h*yRadius),radius,radius); // to draw circle
				  }
	  }
	  
	  private static Color getColor(int posR,int posG,int posB){
		  System.out.println(posR+" "+posG+" "+posB);
		  
		  double r = 255*((double)posR)/((double)initNumCircles);
		  double g = 255*((double)posG)/((double)initNumCircles);
		  double b = 255*((double)posB)/((double)initNumCircles);
		  
		  System.out.println(r+" "+g+" "+b);
		  
		  Color c = ConvertColors.RYBtoRGB_Color(r,g,b);
		  System.out.println(c);
		//  c = new Color(c.getRed(),c.getGreen(),c.getBlue(),255);
		//  c.set(modifier_pattern ret_type_pattern type_pattern.id_pattern)Alpha(1.0);
		  //Color c =new Color((int)r,(int)g,(int)b); 
		  return c;
	  }
	  
	  public static void main(String args[])
	  {
	    new legend();
	  }
	}


