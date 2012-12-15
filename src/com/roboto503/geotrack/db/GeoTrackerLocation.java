package com.roboto503.geotrack.db;

public class GeoTrackerLocation {
	
	//location field variables
	private long id;
	private String longitude;
	private String latitude;
	private String date;
	
	//accessor methods to get and set id, longitude, latitude and geotag
	public long getId(){
		return id;
	}//getId
	
	public void setId(long id){
		this.id=id;
	}//setId
	
	public String getLongitude(){
		return longitude;
	}//getLongitude
	
	public void setLongitude(String longitude){
		this.longitude = longitude;
	}//setLongitude
	
	public String getLatitude(){
		return latitude;
	}//getLatitude
	
	public void setLatitude(String latitude){
		this.latitude = latitude;
	}//setLatitude
	
	public String getDate(){
		return date;
	}//getGeotag
	
	public void setDate(String date){
		this.date = date;
	}//setGeotag
	
	
}
