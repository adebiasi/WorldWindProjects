package it.entities;

public class Route extends Link{

	private String airlineID;
	private String airlineName;
	private boolean isAirlineActive;
	private String airlineCountry;
	private boolean isCodeShare;
	private int stops;
	String equipments;
	
	
	
	
	
	public Route(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	public Route(){
		
	}
	
public void fromLink(Link link){
		
	
	this.id=link.getId();
		this.from=link.getFrom();
		this.from_lat=link.from_lat;
		this.from_lon=link.from_lon;
		this.from_pos=link.from_pos;
		
		this.to=link.to;
		this.to_lat=link.to_lat;
		this.to_lon=link.to_lon;
		this.to_pos=link.to_pos;
	}
	
	public void setRouteAttriburtes(
			String airlineID,String airlineName, boolean isAirlineActive,String airlineCountry, boolean isCodeShare, int stops,String equipments,double distance){
	
		this.airlineID=airlineID;
		this.airlineName=airlineName;
		this.isAirlineActive=isAirlineActive;
		this.airlineCountry=airlineCountry;
		this.isCodeShare=isCodeShare;
		this.stops=stops;
		this.equipments=equipments;
		System.out.println("in route distance: "+distance);
		this.distance=distance;
		System.out.println("in route this.distance: "+this.distance);
	}
	
}
