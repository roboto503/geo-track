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
	private List<GeoTrackerLocation> values;
	//####################
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//###################
		ds = new LocationsDataSource(this);
		ds.openDb();
		values = ds.getAllLocations();
		//###################
		
		ArrayList<Map<String, String>> list = createRows();
		
		String[] from = {"lonlat","geotag"};
		int[] to = { R.id.lonlat, R.id.geotag };
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.locations_row, from, to);
		setListAdapter(simpleAdapter);
		
	
	}//onCreate
	
	private ArrayList<Map<String, String>> createRows() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		//TODO
		//miten saa listasta values mapattua arvot listaan list?
		
		//loop through string tables of latlon and geotag
		//for(int i=0; i < locations.length; i++){
	    	//list.add(putData(locations[i], geotags[i]));
	    //}//for
		
	    return list;
	}//createRows

	private Map<String, String> putData(String lonlat, String geotag) {
		HashMap<String, String> item = new HashMap<String, String>();
	    item.put("lonlat", lonlat);
	    item.put("geotag", geotag);
	    return item;
	}//puData

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this,"selected " + id, Toast.LENGTH_SHORT).show();
	}//onListItemClick

}
