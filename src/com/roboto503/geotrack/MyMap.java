package com.roboto503.geotrack;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.roboto503.geotrack.db.LocationsDataSource;
import com.roboto503.geotrack.db.GeoTrackerLocation;

public class MyMap extends MapActivity {

	//UI items
	private MapController mapCont;
	private MapView mapView;
	private GeoTrackOverlays gtOverlays;
	private MyLocationOverlay myLocOverlay;
	private ImageView backBtn; 
	
	//database
	private LocationsDataSource ds;
	private List<GeoTrackerLocation> dbLocationValues; //list of locations stored in database
	
	//other
	private long selectedLocation; //stores an id of a selected location 
	private boolean showOnlySelected = false; //flag used to indicate whether user wants to see only selected location or all the locations
	
	
	/** Creates and initializes the Activity*/
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.map); //layout defined in map.xml
		
		//get intent that launched this activity
		Intent intent = getIntent();
		selectedLocation = intent.getLongExtra("LOCATION_ID", -1);
		
		//get stored locations from database
		initializeLocations();
		
		//figure out whether user has selected a certain location ot not
		if(selectedLocation != -1){
			//print only selected location
			showOnlySelected = true;
			initializeMap();
		}else{
			//print all locations
			initializeMap();
		}//if
	}//onCreate

	
	/** gets all stored locations from database*/
	private void initializeLocations() {
		// create database helper and open database
		ds = new LocationsDataSource(this);
		ds.openDb();
		
		// get stored locations
		dbLocationValues = ds.getAllLocations();
		
		// finally close the database
		ds.closeDb();
	}

	
	/** Initializes the UI-items */
	private void initializeMap() {
		//get ImageView (arrow at the top left corner)
		backBtn = (ImageView) findViewById(R.id.map_home_btn);
		
		//set onClickListener to ImageView
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//finish this activity and return back in stack (either MainMenu or Locations)
				finish();
			}
		});
		
		// gets the mapView
		mapView = (MapView) findViewById(R.id.map);
		
		// sets built in zoom controls so user can zoom in and out
		mapView.setBuiltInZoomControls(true);
		
		//instead of cartoon-like map we can use more "realistic-looking" map
		//mapView.setSatellite(true);
		
		//return controller for the map so zoom animation and pan can be controlled
		mapCont = mapView.getController();
		
		//get current location
		myLocOverlay = new MyLocationOverlay(this, mapView);
		
		//add current location to database overlays
		mapView.getOverlays().add(myLocOverlay);
		
		//draw current location on mapView. This is done in Runnable so UI won't freeze
		myLocOverlay.runOnFirstFix(new Runnable(){

			@Override
			public void run() {
				mapView.getController().animateTo(myLocOverlay.getMyLocation());
				
			}//run
			
		});//myLocOverlays.runOnFirst
		
		//create markers that are laid on map
		Drawable marker = getResources().getDrawable(R.drawable.ic_launcher_red); //what icon is used to represent marker
		gtOverlays = new GeoTrackOverlays(this, marker); //list of all overlays
		createMarkers(dbLocationValues); //draws markers on the map
	}//initializeMap

	
	/** creates the markers for laid on the map*/
	private void createMarkers(List<GeoTrackerLocation> listOfLocations) {
		//whether user wants to see only selected location
		if(showOnlySelected){
			for(GeoTrackerLocation location : listOfLocations){
				if(location.getId() == selectedLocation){
					// creates a new GeoPoint by getting the longitude and latitude from location's parsed string. The parse must be multiplied with 1E6 because constructor of GeoPointproduct accepts longitude and latitude in microdegrees (degrees * 1E6). 
					GeoPoint point = new GeoPoint((int) (Double.parseDouble(location.getLatitude()) * 1e6), (int) (Double.parseDouble(location.getLongitude()) * 1e6));
					
					//creates a new overlay item
					OverlayItem overlayItem = new OverlayItem(point, "", "");
					gtOverlays.addOverlay(overlayItem);
					
					//add the point
					mapView.getOverlays().add(gtOverlays);
					
					// centers map to the last added point
					mapCont.animateTo(point);
				}//if
			}//for
		}else{
			// loops through given list of locations and creates geopoints, which are needed to draw marker on the map 
			for(GeoTrackerLocation location : listOfLocations){
				// creates a new GeoPoint by getting the longitude and latitude from location's parsed string. The parse must be multiplied with 1E6 because constructor of GeoPointproduct accepts longitude and latitude in microdegrees (degrees * 1E6). 
				GeoPoint point = new GeoPoint((int) (Double.parseDouble(location.getLatitude()) * 1e6), (int) (Double.parseDouble(location.getLongitude()) * 1e6));
				
				//creates a new overlay item
				OverlayItem overlayItem = new OverlayItem(point, "", "");
				gtOverlays.addOverlay(overlayItem);
				
				mapView.getOverlays().add(gtOverlays);
				
				// centers map to the last added point
				mapCont.animateTo(point);
			}//for
		}//if
	}//createmarkers
	
	
	/** implemented abstract method*/
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/** when activity is paused*/
	@Override
	protected void onPause() {
		super.onPause();
	
		//disable mylocation and compass
		myLocOverlay.disableMyLocation();
		myLocOverlay.disableCompass();
	}//onPause
	
	/** event handler for menu options*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// which option in an options menu was selected 
		switch(item.getItemId()){
		case R.id.map_show_selected: 
			if(selectedLocation != -1){
				//clear existing overlays and reinitialize marker creation
				mapView.getOverlays().clear();
				showOnlySelected = true;
				initializeMap();
			}else{
				//inform user that there are no selected location
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_selected_location), Toast.LENGTH_SHORT).show();
			}//if
			break;
		case R.id.map_show_all:
			//clear existing overlays and reinitialize marker creation
			mapView.getOverlays().clear();
			showOnlySelected = false;
			initializeMap();
			break;
		}//switch
		
		return super.onOptionsItemSelected(item);
	}//onOptionsMenuSelected
	
	
	/** create options menu (menu that pops up as you press menu button on the device) */ 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//get menu inflater and inflate the menu's layout declared in map_menu.xml 
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}//onCreateOptoinsMenu
	
}
