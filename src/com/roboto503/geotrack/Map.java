package com.roboto503.geotrack;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.roboto503.geotrack.db.LocationsDataSource;
import com.roboto503.geotrack.db.GeoTrackerLocation;

public class Map extends MapActivity {

	private MapController mapCont;
	private MapView mapView;
	private GeoTrackOverlays gtOverlays;
	private MyLocationOverlay myLocOverlay;
	
	//database
	private LocationsDataSource ds;
	private List<GeoTrackerLocation> dbLocationValues; //list of locations stored in database
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.map); //layout defined in map.xml
		
		initializeLocations();
		
		initializeMap();
	
	}//onCreate
	
	private void createMarkers(List<GeoTrackerLocation> listOfLocations) {
		// create the overlays for the map from list of geotracklocations
		
		// loops through given list of locations and creates geopoints 
		for(GeoTrackerLocation location : listOfLocations){
			//list.add(putData("longitude: " + location.getLongitude() + " latitude: " + location.getLatitude(), location.getGeotag()));
			// creates a new GeoPoint by getting the longitude and latitude from location's parsed string. The parse must be multiplied with 1E6 because constructor of GeoPointproduct accepts longitude and latitude in microdegrees (degrees * 1E6). 
			//GeoPoint point = new GeoPoint((int) (Float.parseFloat(location.getLongitude()) * 1E6), (int) (Float.parseFloat(location.getLatitude()) * 1E6));
			//GeoPoint point = new GeoPoint((int) (Float.parseFloat(location.getLongitude())), (int) (Float.parseFloat(location.getLatitude())));
			//GeoPoint point = new GeoPoint((int) (Double.parseDouble(location.getLongitude()) * 1e6), (int) (Double.parseDouble(location.getLatitude()) * 1e6));
			GeoPoint point = new GeoPoint((int) (Double.parseDouble(location.getLatitude()) * 1e6), (int) (Double.parseDouble(location.getLongitude()) * 1e6));
			Log.i("GEOPOINT", String.valueOf(point.getLatitudeE6()) + " " + String.valueOf(point.getLongitudeE6()));
			OverlayItem overlayItem = new OverlayItem(point, "", "");
			gtOverlays.addOverlay(overlayItem);
			Log.i("OVERLAY SIZE", String.valueOf(gtOverlays.size()));
			mapView.getOverlays().add(gtOverlays);
			Log.i("MAP OVERLAY SIZE", String.valueOf(mapView.getOverlays().size()));
		}//for
		
	}

	private void initializeLocations() {
		// open database
		ds = new LocationsDataSource(this);
		ds.openDb();
		
		// get stored locations
		dbLocationValues = ds.getAllLocations();
		
		// finally close the database
		ds.closeDb();
	}

	private void initializeMap() {
		// gets the mapView
		mapView = (MapView) findViewById(R.id.map);
		// sets built in zoom controls so user can zoom in and out
		mapView.setBuiltInZoomControls(true);
		
		mapView.setSatellite(true);
		
		// return controller for the map so zoom animation and pan can be controlled
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
		
		//marker for geolocations laid on map
		Drawable marker = getResources().getDrawable(R.drawable.ic_launcher_red);
		
		gtOverlays = new GeoTrackOverlays(this, marker);
		
		createMarkers(dbLocationValues);
		
		//createMarker();
	
	}//initializeMap

	private void createMarker() {
		// TODO Auto-generated method stub
		
	}//createMarker

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
	
		//disable mylocation and compass
		myLocOverlay.disableMyLocation();
		myLocOverlay.disableCompass();
	}//onPause
	
}
