package it.graphitech.smeSpire.entry;

public class Entry {
	
	private String from_id;
	private String from_org;
	private double from_lat;
	private double from_lon;
	private int from_trafficDegree;
	
	private String to_id;
	private String to_org;
	private double to_lat;
	private double to_lon;
	private int to_trafficDegree;
	
	private int trafficDegree;
	
	public Entry(){
		
	}
	
	

	public int getFrom_trafficDegree() {
		return from_trafficDegree;
	}



	public void setFrom_trafficDegree(int from_trafficDegree) {
		this.from_trafficDegree = from_trafficDegree;
	}



	public int getTo_trafficDegree() {
		return to_trafficDegree;
	}



	public void setTo_trafficDegree(int to_trafficDegree) {
		this.to_trafficDegree = to_trafficDegree;
	}



	public String getFrom_org() {
		return from_org;
	}

	public void setFrom_org(String state) {
		this.from_org = state;
	}

	public String getTo_org() {
		return to_org;
	}

	public void setTo_org(String state) {
		this.to_org = state;
	}

	public String getFrom_id() {
		return from_id;
	}

	public void setFrom_id(String from) {
		this.from_id = from;
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

	public String getTo_id() {
		return to_id;
	}

	public void setTo_id(String to) {
		this.to_id = to;
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

	public int getTrafficDegree() {
		return trafficDegree;
	}

	public void setTrafficDegree(int trafficDegree) {
		this.trafficDegree = trafficDegree;
	}

	
}
