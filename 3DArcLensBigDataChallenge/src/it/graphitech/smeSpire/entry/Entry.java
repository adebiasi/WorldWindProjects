package it.graphitech.smeSpire.entry;

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

	
	private int number_calls;
	private int totNumber_calls;
	private double avg_time;
	private String time;
	
	private boolean isOutlierNumberCall;
	private boolean isOutlierAvgTime;
	
	
	int totOutlinerNumberCalls;
	int totOutlinerAvgTime;
	double totAvgNumber_calls;
	int totMinNumberCalls;
	int totMaxNumberCalls;
	
	public Entry(){
		
	}
	
	

	public int getTotMinNumberCalls() {
		return totMinNumberCalls;
	}



	public void setTotMinNumberCalls(int totMinNumberCalls) {
		this.totMinNumberCalls = totMinNumberCalls;
	}



	public int getTotMaxNumberCalls() {
		return totMaxNumberCalls;
	}



	public void setTotMaxNumberCalls(int totMaxNumberCalls) {
		this.totMaxNumberCalls = totMaxNumberCalls;
	}



	public double getTotAvgNumber_calls() {
		return totAvgNumber_calls;
	}



	public void setTotAvgNumber_calls(double totAvgNumber_calls) {
		this.totAvgNumber_calls = totAvgNumber_calls;
	}



	public int getTotOutlinerNumberCalls() {
		return totOutlinerNumberCalls;
	}



	public void setTotOutlinerNumberCalls(int totOutlinerNumberCalls) {
		this.totOutlinerNumberCalls = totOutlinerNumberCalls;
	}



	public int getTotOutlinerAvgTime() {
		return totOutlinerAvgTime;
	}



	public void setTotOutlinerAvgTime(int totOutlinerAvgTime) {
		this.totOutlinerAvgTime = totOutlinerAvgTime;
	}



	public boolean isOutlierNumberCall() {
		return isOutlierNumberCall;
	}



	public void setOutlierNumberCall(boolean isOutlierNumberCall) {
		this.isOutlierNumberCall = isOutlierNumberCall;
	}



	public boolean isOutlierAvgTime() {
		return isOutlierAvgTime;
	}



	public void setOutlierAvgTime(boolean isOutlierAvgTime) {
		this.isOutlierAvgTime = isOutlierAvgTime;
	}



	public String getTime() {
		return time;
	}



	public void setTime(String time) {
		this.time = time;
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



	public int getNumber_calls() {
		return number_calls;
	}

	public int getTotNumber_calls() {
		return totNumber_calls;
	}


	public void setNumber_calls(int number_calls) {
		this.number_calls = number_calls;
	}

	public void setTotNumber_calls(int totNumber_calls) {
		this.totNumber_calls = totNumber_calls;
	}

	public double getAvg_time() {
		return avg_time;
	}



	public void setAvg_time(double avg_time) {
		this.avg_time = avg_time;
	}
	
	
}
