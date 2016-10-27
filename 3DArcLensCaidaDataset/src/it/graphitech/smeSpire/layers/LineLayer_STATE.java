package it.graphitech.smeSpire.layers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import it.graphitech.smeSpire.entry.Entry;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.render.Polyline;

public class LineLayer_STATE extends MyRenderableLayer{
	
	private WorldWindow wwd;
	private ArrayList<String> entries;
	private HashMap<String, Entry> cities;
	
	private String state;
	private int rank;
	
	private double max_EC;
	private double min_EC;
	
	private double radius_cylinder;
    private double alt;
	
	public LineLayer_STATE(WorldWindow wwd, ArrayList<String> entries, HashMap<String, Entry> cities, String name){
		this.wwd = wwd;
		this.state = state;
		this.entries = entries;
		this.cities = cities;
		
		this.setName(name);
		this.setEnabled(false);
		
		
		
		this.setPickEnabled(false);
		
		
		
        Color color = null;
 
	        for(int i=0;i<this.entries.size();i++){
	        	//System.out.println(i);
	        	
	        	String city = this.entries.get(i);
	        	
	        	Entry entry = this.cities.get(city);
	        	
	        	min_EC = 2000;
	        	max_EC = 99999;
	        	radius_cylinder = 5000;
                alt = 5000;
                rank = 1;
                
                color = Color.decode("#006600"); //FFFFCC
	        	/*
                boolean set = false;
	        	
	        	for(int j=0;j<5;j++){
		        	if(entry.getEc_funding() >= min_EC && entry.getEc_funding() <= max_EC){
			        	set = true;
			        	break;
		        	}
		        	else{
		        		radius_cylinder *= 1.2;
		                alt *= 1.5;
		                rank++;
		        		
		        		if(j==1){min_EC = 100000;max_EC = 499999;color = Color.decode("#CCCC66");} //FFFF33
		        		else if(j==2){min_EC = 500000;max_EC = 999999;color = Color.decode("#FFCC33");} //FF9933
		        		else if(j==3){min_EC = 1000000;max_EC = 4999999;color = Color.decode("#FF3333");}
		        		else if(j==4){min_EC = 5000000;max_EC = Double.MAX_VALUE;color = Color.decode("#DF174F");}
		        	}
	        	}
*/
	        //	if(set && entry.getState().compareToIgnoreCase(state) == 0){
           //     System.out.println("entry.getState(): "+entry.getState());
                
            	
	        		
	        		if(entry.getFrom_id().compareTo(entry.getTo_id()) != 0){
	        			//System.out.println(city);
	        			//PoicmsArcLine curve = new PoicmsArcLine(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()), color, rank, this.wwd.getView().getGlobe());          	
	        			
	        		
	        			
	        			//DynamicPolyline curve = new DynamicPolyline(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()), color, rank, this.wwd.getView().getGlobe());
	        	
	        			
	        			//RenderableControlPoints renderableControlPoints = new RenderableControlPoints();
	        			//renderableControlPoints.positions=(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
	        			
	        			//renderableControlPoints.setControlPointPosition(setControlPoints(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()),  this.wwd.getView().getGlobe()));
	        			//renderableControlPoints.calculatePoints();
	        			
	        			ArrayList<Position> list = new ArrayList<>();
	        			list.add(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()));
	        			list.add(Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()));
	        			
	        			Polyline curve = new Polyline(list);
	        			curve.setFollowTerrain(true);
	        			//System.out.println("entry.getFrom(): "+entry.getFrom()+" entry.getTo(): "+entry.getTo());
	        			//curve.setFrom(entry.getFrom());
	        			//curve.setTo(entry.getTo());
	        			//	ArrayList<Position> p = new ArrayList<Position>();
	        	//		p.add(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()));
	        //			p.add(Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()));
	        			
	        			
	        			//Polyline curve = new Polyline();
	        			//PoicmsArcLine curve = new  PoicmsArcLine(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), Position.fromDegrees(entry.getTo_lat(), entry.getTo_lon()), color, rank, this.wwd.getView().getGlobe());
	        			
	        			//curve.setPositions(p);
	        			this.addRenderable(curve);
	        		}
	        		else{
	        			
	        			/*
	        			//System.out.println(city);
	        			// Create and set an attribute bundle.
	                    ShapeAttributes attrs = new BasicShapeAttributes();
	                    
	                    attrs.setInteriorMaterial(new Material(color));
	                    attrs.setInteriorOpacity(0.7);
	                    attrs.setDrawOutline(false);

	        			// Cylinder with equal axes, CLAMP_TO_GROUND
	                    Cylinder cylinder = new Cylinder(Position.fromDegrees(entry.getFrom_lat(), entry.getFrom_lon()), radius_cylinder, alt, radius_cylinder);
	                    cylinder.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
	                    cylinder.setAttributes(attrs);
	                    this.addRenderable(cylinder);
	                    //System.out.println("FATTO " + entry.getFrom_lat() + " " + entry.getFrom_lon());
	                     * 
	                     */
	        		}
	        	
	        }
        
        System.out.println("NUM CONNECTIONS: "+this.getNumRenderables());
        insertBeforeCompass(this.wwd, this);
	}

	public ArrayList<Position> setControlPoints(Position source, Position destination, Globe eg){
int SEGMENTS = 5;
	

double distance = Position.ellipsoidalDistance(source, destination,	Earth.WGS84_EQUATORIAL_RADIUS, Earth.WGS84_POLAR_RADIUS);
distance=distance/2;
//double distance =2000000;
			ArrayList<Position> points = new ArrayList<Position>();
			
			//double value = Math.floor(Math.random() * 20)/100;
			
			for (int i = 0; i < SEGMENTS; i++)
			{
				double interpolation = (double) i / (SEGMENTS-1);
				
				double arcHeight=distance*Math.sin(Math.PI*interpolation);
				//double arcHeight = (0.5 - interpolation) * 2.0;
				//arcHeight = 1.0 - (arcHeight * arcHeight);
				Position p = Position.interpolate( interpolation, source, destination );
				//p = new Position(p.latitude , p.longitude, arcHeight * distance * (0.1 + value));
				p = new Position(p.latitude , p.longitude, arcHeight);
				points.add(p);
			}
			
		
			
			return(points);
	}
	
	/*
	@Override
	protected void doRender(DrawContext dc) {
		// TODO Auto-generated method stub
		super.doRender(dc);
		
		PickedObject po =this.pickSupport.getTopObject(dc, new java.awt.Point(0, 0));
		if(po!=null){
		System.out.println("po: "+po);
    	}
		this.doPick(dc, new java.awt.Point(0, 0));
		po =this.pickSupport.getTopObject(dc, new java.awt.Point(0, 0));
		if(po!=null){
		System.out.println("po: "+po);
    	}
		//this.myPick(dc, new java.awt.Point(0, 0));
		//PickedObject po = this.pickSupport.getTopObject(dc, new java.awt.Point(0, 0));
		//PickedObject po = this.wwd.getSceneController().getPickedObjectList().getTopPickedObject();
    	//if(po!=null){
		//System.out.println("po: "+po);
    	//}
	}
*/
/*
	public void myPick(DrawContext dc, java.awt.Point pickPoint)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        if (pickPoint == null)
            return;

        this.pickSupport.clearPickList();
        this.pickSupport.beginPicking(dc);

        GL gl = dc.getGL();
        gl.glShadeModel(GL.GL_FLAT);

        try
        {
        
            PickedObject pickedSector = this.pickSupport.getTopObject(dc, pickPoint);
            if (pickedSector == null || pickedSector.getObject() == null)
                return; // no sector picked

            System.out.println("cè qualcosa");
            SectorGeometry sector = (SectorGeometry) pickedSector.getObject();
            gl.glDepthFunc(GL.GL_LEQUAL);
            sector.pick(dc, pickPoint);
        }
        finally
        {
            gl.glShadeModel(GL.GL_SMOOTH); // restore to default explicitly to avoid more expensive pushAttrib
            gl.glDepthFunc(GL.GL_LESS); // restore to default explicitly to avoid more expensive pushAttrib

            this.pickSupport.endPicking(dc);
            this.pickSupport.clearPickList();
        }
    }
*/
	
	

	public static void insertBeforeCompass(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof CompassLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }



/*
	@Override
	protected void doPick(DrawContext dc, Point arg1) {
		// TODO Auto-generated method stub
//		System.out.println("DO PICK");
//		for(int i=0;i<100;i++){
			Point p1 = new Point(0, 0);
			super.doPick(dc, p1);
//		}
		
	}
	*/
}
