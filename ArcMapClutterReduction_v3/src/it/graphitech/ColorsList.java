package it.graphitech;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorsList {

	static int index =0;
	/*
	public static Color[][] colorList = {
			{Color.GREEN,Color.BLUE},
			{Color.ORANGE,Color.CYAN},
			{Color.YELLOW,Color.DARK_GRAY},
			{Color.MAGENTA,Color.PINK},
			{Color.BLUE,Color.YELLOW},
			{Color.PINK,Color.GREEN},
			};
	*/
	
	public static ArrayList<Color[]> colorList ;
	
	 private static final Random rnd = new Random();
	
	 
	 static public void generateColorList(){
		
		int numColors = 50;
		colorList = new ArrayList<Color[]>();

		while(colorList.size()<numColors){
			Color c = new Color(rnd.nextInt());
		Color[] cols = {c,c};
		if(!colorList.contains(cols)){
		colorList.add(cols);
		}
		
		}
	}
	
	 static public void generateColorListHSB(){
			
			int numColors = 250;
			colorList = new ArrayList<Color[]>();

			Color c = generateColorHSB(0.0f);
			insertColor(c);
			
			 c = generateColorHSB(0.5f);
			insertColor(c);
			
			 c = generateColorHSB(0.75f);
			insertColor(c);
			
			
			while(colorList.size()<numColors){
				 c = generateColorHSB(rnd.nextFloat());
				insertColor(c);
			}
		}
		
	 private static void insertColor(Color c){
		 Color[] cols = {c,c};
			if(!colorList.contains(cols)){
			colorList.add(cols);
			}
			
	 }
	 
	 
	 private static Color generateColorHSB(float index){
		 
		 //float hue = 0.9f; //hue
		 float hue = (float)index; //hue
		 float saturation = 1.0f; //saturation
		 float brightness = 0.8f; //brightness

		 Color myRGBColor = Color.getHSBColor(hue, saturation, brightness);
		 return myRGBColor;
	 }
	 
	 
	static public Color[] getCurrColors(){
		
		index =colorList.size() %  index ; 
		
		System.out.println("index: "+index);
		Color[] res =  colorList.get(index);
		index++;
		return res;
	}
	
	
	static public void resetIndex(){
		index =0;
	}
	
	static public Color getCurrentColor(int currIndex){
		currIndex = currIndex % colorList.size(); 
		
		Color res =  colorList.get(currIndex)[0];
		
		return res;
	}
}
