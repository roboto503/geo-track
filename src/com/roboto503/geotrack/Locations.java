package com.roboto503.geotrack;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.roboto503.geotrack.db.GeoTrackerLocation;
import com.roboto503.geotrack.db.LocationsDataSource;

public class Locations extends FragmentActivity{
	
	/** Database variables */
	private LocationsDataSource ds;
	private List<GeoTrackerLocation> dbLocationValues;
	
	//UI itmes
	private ListView listView;
	private ImageView backBtn;
	
	private long selectedLocation;

	private final int DELETE_ALERT = 0;
	
	/** Contextmenu variables*/
	private final static int SHOW = Menu.FIRST;
	private final static int DELETE = Menu.FIRST + 1;
	private final static int CANCEL = Menu.FIRST + 2;
	
	/** */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locations_list);
		
		initializeUI();
		
		
	}//onCreate
	
	private void initializeUI(){
		
		listView = (ListView) findViewById(R.id.list);
		backBtn = (ImageView) findViewById(R.id.locations_home_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(getApplicationContext(), MainMenu.class);
				//startActivity(intent);
				finish();
			}
		});
		
		updateListView();
	}
	
	/** initializes the listview*/
	private void updateListView(){
		//open database
		ds = new LocationsDataSource(this); //database functionality
		ds.openDb(); //opens database
		dbLocationValues = ds.getAllLocations(); //gets a list of all location values stored in database

		//populate list
		ArrayList<Map<String, String>> list = createRows(dbLocationValues); //Maps locations from the list in key pair values so they can later be printed for the listview
		
		String[] from = {"lonlat","geotag"};
		int[] to = { R.id.lonlat, R.id.geotag };
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.locations_row, from, to);
		
		listView.setAdapter(simpleAdapter);
		registerForContextMenu(listView);

		//finally close database
		ds.closeDb();
		
		
	}//initializeListView
	
	/** */
	private ArrayList<Map<String, String>> createRows(List<GeoTrackerLocation> listOfLocations) { //values
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		// loops through given list of locations, and strips location's values (longitude, latitude and date) to the right places in row item using putData method 
		for(GeoTrackerLocation location : listOfLocations){
			//list.add(putData("longitude: " + location.getLongitude() + " latitude: " + location.getLatitude(), String.valueOf(location.getId())));
			list.add(putData("longitude: " + location.getLongitude() + " latitude: " + location.getLatitude(), location.getDate()));
		}//for

	    return list;
	}//createRows

	/** */
	private Map<String, String> putData(String lonlat, String geotag) {
		HashMap<String, String> item = new HashMap<String, String>();
	    item.put("lonlat", lonlat);
	    item.put("geotag", geotag);
	    return item;
	}//puData
	
	/** creates dialogs*/
	@Override
	protected Dialog onCreateDialog(int id) {
		
		AlertDialog.Builder builder; 
		switch(id){
		case DELETE_ALERT:
			//creates a new dialog
			builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.locations_delete_alert);
			builder.setCancelable(true);
			
			//set buttons to the dialog, set OnClick event handlers to the buttons
			builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {	
				//OnClick handler for continue button in GPS dialog 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//deletes location from database
					try{
						ds.openDb();
						ds.deleteLocation(selectedLocation);
						ds.closeDb();
					}catch(Exception e){
						Toast.makeText(getApplicationContext(), R.string.operation_failed, Toast.LENGTH_SHORT).show();
					}finally{
						//simpleAdapter.notifyDataSetChanged();
						//clear listview and reinitialize database, adapter and listview
						listView.clearChoices();
						updateListView();
					}
					return;
				}//onClick
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				//OnClick handler for cancel button in GPS dialog 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getApplicationContext(), R.string.database_location_deleted, Toast.LENGTH_SHORT).show();
					return;
				}
			});
			
			//create the dialog and show it to the user
			AlertDialog dialog = builder.create();
			dialog.show();
			break;
			
		}
		return super.onCreateDialog(id);
	}
	
	/** creates contextmenu for the listview*/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//add operations for context menu
		menu.add(0,SHOW, 0, getResources().getString(R.string.show_on_map));
		menu.add(0, DELETE, 0, getResources().getString(R.string.delete));
		menu.add(0, CANCEL, 0, getResources().getString(R.string.cancel));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		selectedLocation = dbLocationValues.get(info.position).getId();
		switch(item.getItemId()){
		case SHOW:
			//Intent -> id mukaan
			break;
		case DELETE:
			onCreateDialog(DELETE_ALERT);
			break;
		case CANCEL:
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	/*
	private Map<String, String> putData(String lonlat, String geotag) {
		HashMap<String, String> item = new HashMap<String, String>();
	    item.put("lonlat", lonlat);
	    item.put("geotag", geotag);
	    return item;
	}//puData
	*/

	/*
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this,"selected " + id, Toast.LENGTH_SHORT).show();
	}//onListItemClick
	 */
	
	
}
