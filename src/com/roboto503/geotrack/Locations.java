package com.roboto503.geotrack;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.roboto503.geotrack.db.GeoTrackerLocation;
import com.roboto503.geotrack.db.LocationsDataSource;

public class Locations extends ListActivity {
	
	//String locations [] = {"lat:2 lon: 3","lat:4 lon: 5","lat:1 lon: 5","lat:8 lon: 8","lat:9 lon: 2","lat:2 lon: 4","lat:5 lon: 3"};
	//String geotags [] = {"geotag 1","geotag 2","geotag 3","geotag 4","geotag 5","geotag 6","geotag 7"};
	
	//####################
	private LocationsDataSource ds;
	private List<GeoTrackerLocation> dbLocationValues;
	//####################
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//###################
		ds = new LocationsDataSource(this); //database functionality
		ds.openDb(); //opens database
		dbLocationValues = ds.getAllLocations(); //gets a list of all location values stored in database
		//###################
		
		ArrayList<Map<String, String>> list = createRows(dbLocationValues); //Maps locations from the list in key pair values so they can later be printed for the listview
		
		String[] from = {"lonlat","geotag"};
		int[] to = { R.id.lonlat, R.id.geotag };
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.locations_row, from, to);
		setListAdapter(simpleAdapter);
		
	
		//finally close database
		ds.closeDb();
	}//onCreate
	
	private ArrayList<Map<String, String>> createRows(List<GeoTrackerLocation> listOfLocations) { //values
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		// loops through given list of locations, and strips location's values (longitude, latitude and geotag) to the right places in row item using putData method 
		for(GeoTrackerLocation location : listOfLocations){
			list.add(putData("longitude: " + location.getLongitude() + " latitude: " + location.getLatitude(), location.getGeotag()));
		}//for

	    return list;
	}//createRows

	private Map<String, String> putData(String lonlat, String geotag) {
		HashMap<String, String> item = new HashMap<String, String>();
	    item.put("lonlat", lonlat);
	    item.put("geotag", geotag);
	    return item;
	}//puData
	
	/*
	private Map<String, String> putData(String lonlat, String geotag) {
		HashMap<String, String> item = new HashMap<String, String>();
	    item.put("lonlat", lonlat);
	    item.put("geotag", geotag);
	    return item;
	}//puData
	*/

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this,"selected " + id, Toast.LENGTH_SHORT).show();
	}//onListItemClick

	
}
