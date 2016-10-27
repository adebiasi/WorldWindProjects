package it.graphitech.colorGradient;

import java.awt.Color;

public class ColorGradient {
	static Color color1= Color.WHITE;
	static Color color2= Color.RED;
	static Color color3= Color.ORANGE;
	
	static Color outlier = new Color(50, 50, 50,255);
	static Color noOutlier = new Color(0, 50, 0,255);
	
	static public Color returnInterpolatedColor(double number, double minNumber, double maxNumber){
	 double ratio = (number-minNumber)/(maxNumber-minNumber);
	 //System.out.println("ratio: "+ratio);
	 //System.out.println(""+number+" -- "+minNumber+" --- "+maxNumber);
     int red = (int) (color2.getRed() * ratio + color1.getRed() * (1 - ratio));
     int green = (int) (color2.getGreen() * ratio + color1.getGreen() * (1 - ratio));
     int blue = (int) (color2.getBlue() * ratio + color1.getBlue() * (1 - ratio));
     //int alpha = (int) (255 * ratio + 0 * (1 - ratio));
     int alpha = 255;
     Color stepColor = new Color(red, green, blue,alpha);
     return stepColor;
	}
	
	
	
	static public Color returnInterpolatedColorSingleState(double number, double minNumber, double maxNumber){
		 double ratio = (number-minNumber)/(maxNumber-minNumber);
		 //System.out.println("ratio: "+ratio);
		 //System.out.println(""+number+" -- "+minNumber+" --- "+maxNumber);
	     int red = (int) (color3.getRed() * ratio + color1.getRed() * (1 - ratio));
	     int green = (int) (color3.getGreen() * ratio + color1.getGreen() * (1 - ratio));
	     int blue = (int) (color3.getBlue() * ratio + color1.getBlue() * (1 - ratio));
	     //int alpha = (int) (255 * ratio + 0 * (1 - ratio));
	     int alpha = 255;
	     Color stepColor = new Color(red, green, blue,alpha);
	     return stepColor;
		}
	
	/*
	static public Color returnInterpolatedColor(double number, double minNumber, double maxNumber){
	
	     Color stepColor = new Color(255, 5, 1,255);
	     return stepColor;
		}
	*/
	static public Color returnOutlineColor(boolean isOutliner){
		if(isOutliner){
			 return outlier;
		}else{
			return noOutlier;
		}
	   
	 
		}
	
	
}
