package com.roboto503.geotrack.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LocationsDataSource {
	
	//database variables
	private SQLiteDatabase db; //database
	private DatabaseHelper dbHelper; //helper
	private String[] tableColumns = {DatabaseHelper.COLUMN_ID,DatabaseHelper.COLUMN_LON, DatabaseHelper.COLUMN_LAT, DatabaseHelper.COLUMN_DATE}; //column names
	
	
	/** constructor*/
	public LocationsDataSource(Context context){
		//create new instance of dbHelper class
		dbHelper = new DatabaseHelper(context);
	}//LocationsDataSource
	
	
	/** opens the database*/
	public void openDb() throws SQLException{
		//open database using dbHelper class
		db = dbHelper.getWritableDatabase();
	}//openDb
	
	
	/** closes the database*/
	public void closeDb(){
		//closes any open database
		dbHelper.close();
	}//closeDb
	
	
	/** creates a new location item*/
	public GeoTrackerLocation createLocation(String longitude, String latitude, String date){
		//To create a new GeoTrackerLocation we need longitude, latitude and date. These are given as parameters. Let's put them in key-value -pairs before inserting them into database
		ContentValues values = new ContentValues();
		values.put(dbHelper.COLUMN_LON, longitude);
		values.put(dbHelper.COLUMN_LAT, latitude);
		values.put(dbHelper.COLUMN_DATE, date);
		
		//inserts a row in database and also assigns row value to the long id
		long id = db.insert(dbHelper.TABLE_LOCATION, null, values);
	
		//get the added row from database
		Cursor cursor = db.query(dbHelper.TABLE_LOCATION, tableColumns, dbHelper.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		
		//create a new instance of GeoTracker location
		GeoTrackerLocation newLocation = cursorToLocation(cursor);
	
		cursor.close();
		
		return newLocation;
	}// createLocation
	
	
	/** deletes a row from database using an id*/
	public void deleteLocation(long id){
		db.delete(dbHelper.TABLE_LOCATION, dbHelper.COLUMN_ID + " = " + id , null);
	}//deleteLocation
	
	
	/** deletes a row from database*/
	public void deleteLocation(GeoTrackerLocation location){
		//id to figure out which location is going to be deleted
		long id = location.getId();
		db.delete(dbHelper.TABLE_LOCATION, dbHelper.COLUMN_ID + " = " + id , null);
	}//deleteLocation
	
	
	/* gets all the locations in database and returns them (for the listview)*/
	public List<GeoTrackerLocation> getAllLocations(){
		List<GeoTrackerLocation> locations = new ArrayList<GeoTrackerLocation>();
		
		//create new cursor to store data from database
		Cursor cursor = db.query(dbHelper.TABLE_LOCATION, tableColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		//loops through the table columns stored in cursor
		while(!cursor.isAfterLast()){
			//create the location
			GeoTrackerLocation location = cursorToLocation(cursor);
			//add location to the list
			locations.add(cursorToLocation(cursor));
			//move to next row
			cursor.moveToNext();
		}//while
		//end cursor
		cursor.close();
		
		return locations;
	}//getAllLocations
	
	
	/** creates location object by drawing variable fields from database*/
	private GeoTrackerLocation cursorToLocation(Cursor cursor){
		GeoTrackerLocation location = new GeoTrackerLocation();
		location.setId(cursor.getLong(0)); //the first index of the table is the id
		location.setLongitude(cursor.getString(1)); //the second index of the table is the longitude
		location.setLatitude(cursor.getString(2));//the third index of the table is the latitude
		location.setDate(cursor.getString(3));//the fourth index of the table is the date
		
		return location;
	}//cursorToLocation
	
	
}
