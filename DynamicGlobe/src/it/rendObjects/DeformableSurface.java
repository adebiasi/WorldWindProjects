package it.rendObjects;
import it.LoadTerrain;
import it.entities.Grid;
import it.main.MainDeformableGlobe;
import it.shader.GLSL;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.render.Renderable;


public class DeformableSurface 
//implements OrderedRenderable{
implements Renderable{

	Grid grid;
	//LoadTerrain loadTerrain;
	ArrayList<Integer> indices;
	GLSL glsl_trans;
	
	public DeformableSurface(Grid grid,GLSL glsl_trans) {
		super();
		this.grid=grid;
		this.glsl_trans=glsl_trans;
		// loadTerrain = new LoadTerrain();
		
		// TODO Auto-generated constructor stub
	}
/*
	@Override
	public double getDistanceFromEye() {
		// TODO Auto-generated method stub
		return 10000000;
	}

	@Override
	public void pick(DrawContext arg0, Point arg1) {
		// TODO Auto-generated method stub
		
	}
*/
	@Override
	public void render(DrawContext dc) {
		// TODO Auto-generated method stub
		
		
		if(indices==null){
		int cols = grid.numPoints;
		int rows = grid.numPoints;
		
		int n   = 0;
		int colSteps = cols * 2;
		int rowSteps = rows - 1;
		 indices = new ArrayList<Integer>();
		for ( int r = 0; r < rowSteps; r++ ) {
		    for ( int c = 0; c < colSteps; c++ ) {
		        int t = c + r * colSteps;

		        if ( c == colSteps - 1 ) {
		            indices.add( n );
		        }
		        else {
		        	indices.add( n );

		            if ( t%2 == 0 ) {
		                n += cols;
		            }
		            else {
		               // (r%2 == 0) ? n -= (cols-1) : n -= (cols+1);
		                if(r%2 == 0){
		                	 n -= (cols-1);
		                }else{
		                	n -= (cols+1);
		                }
		            }
		        }
		    }
		}
		}
	
		
		MainDeformableGlobe.loadTerrain.drawGridWithTriangles(dc, indices, grid,glsl_trans);
		
		
		/*
		
		System.out.println("real");
		
		for(int x =0 ;x<grid.numPoints-1;x++){
			for(int y =0 ;y<grid.numPoints-1;y++){
				
				System.out.println("x: "+x+", y: "+y);
				System.out.println("x: "+(x+1)+", y: "+y);
				System.out.println("x: "+x+", y: "+(y+1));
				System.out.println("x: "+(x+1)+", y: "+(y+1));
				
		MainDeformableGlobe.loadTerrain.loadTexture(
				dc,
				grid.deformedGrid[x][y] , 
				grid.deformedGrid[x+1][y], 
				grid.deformedGrid[x][y+1] , 
				grid.deformedGrid[x+1][y+1],
				grid.grid[x][y] , 
				grid.grid[x+1][y], 
				grid.grid[x][y+1] , 
				grid.grid[x+1][y+1]				
				);
			}
		}
		*/
		
	}

}
