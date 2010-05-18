package org.geoconme.gps.model;


public class CoordObj {
	private String lat;
	private String lon;
	private String date;
	
	public CoordObj(String lat, String lon, String date) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.date = date;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
}
