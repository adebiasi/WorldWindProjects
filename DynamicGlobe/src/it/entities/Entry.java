package it.entities;

public class Entry {
	
	private String from;
	private double from_lat;
	private double from_lon;
	
	private String state;
	
	private String to;
	private double to_lat;
	private double to_lon;
	
	private double ec_funding;
	private int rank;

	public Entry(){
		
	}
	
	

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}



	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public double getFrom_lat() {
		return from_lat;
	}

	public void setFrom_lat(double from_lat) {
		this.from_lat = from_lat;
	}

	public double getFrom_lon() {
		return from_lon;
	}

	public void setFrom_lon(double from_lon) {
		this.from_lon = from_lon;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public double getTo_lat() {
		return to_lat;
	}

	public void setTo_lat(double to_lat) {
		this.to_lat = to_lat;
	}

	public double getTo_lon() {
		return to_lon;
	}

	public void setTo_lon(double to_lon) {
		this.to_lon = to_lon;
	}

	public double getEc_funding() {
		return ec_funding;
	}

	public void setEc_funding(double ec_funding) {
		this.ec_funding = ec_funding;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}
