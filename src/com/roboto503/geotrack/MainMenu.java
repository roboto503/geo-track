package com.roboto503.geotrack;

import java.text.DecimalFormat;

import com.roboto503.geotrack.db.GeoTrackerLocation;
import com.roboto503.geotrack.db.LocationsDataSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainMenu extends Activity implements OnClickListener, LocationListener {
    
	// UI items
	private Button start; //starts tracking
	private Button stop; //stops tracking
	private Button locations; //launches locations activity
	private ImageView trackingIndicator; //icon to show whether tracking is on or off
	private TextView lonInd; //indicator for received longitude updates
	private TextView latInd; //indicator for received latitude updates
	
	private static final int ACTIVATE_GPS_ALERT = 1; // constant for identifying dialog 
	
	//Location
	private LocationManager locManager;
	private String provider;
	private boolean isEnabled = false; //flag for requesting location updates
	private long minTime = 500; //update time interval in milliseconds
	private float minDistance = 5.0f; //update distance interval in meters
	private DecimalFormat decFormat = new DecimalFormat("#.###"); //DecimalFormat for location's longitude & latitude. We only need three decimals. 
	
	
	//test code
	//#######################
	//Database to store locations
	private LocationsDataSource ds;
	//#######################
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        
        //initialize UI
        initializeUI();
        
        //initialize GPS
        initializeGPS();
        
       // open database 
       ds = new LocationsDataSource(this);
       ds.openDb();
        
    }//onCreate
    
    //initializes UI items declared in main_menu.xml
    public void initializeUI(){
    	//find buttons from layout main_menu layout view
    	start = (Button) findViewById(R.id.main_menu_start_btn);
    	stop = (Button) findViewById(R.id.main_menu_stop_btn);
    	locations = (Button) findViewById(R.id.main_menu_locations_btn);
    	
    	trackingIndicator = (ImageView) findViewById(R.id.main_menu_pic);
    	lonInd = (TextView) findViewById(R.id.main_menu_lon);
    	latInd = (TextView) findViewById(R.id.main_menu_lat);
    	
    	
    	trackingIndicator.setImageResource(R.drawable.ic_launcher_red);
    	
    	//set onClick-listeners to buttons
    	start.setOnClickListener(this);
    	stop.setOnClickListener(this);
    	locations.setOnClickListener(this);
    }//initializeUI


    private void initializeGPS(){
    	locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    provider = locManager.getBestProvider(criteria, false);
	    //ask user to turn on gps or mobile network
	    if(!locManager.isProviderEnabled(provider)){
	    	onCreateDialog(ACTIVATE_GPS_ALERT);
	    }
    }//initializeGPS
    
    //Event handlers for the UI buttons
	@Override
	public void onClick(View v) {
		//GeoTrackerLocation location = null;
		Intent intent;
		
		// figure out which button was pressed
		switch(v.getId()){
		case R.id.main_menu_start_btn:
			//check whether tracking is on or not
			//if not, start tracking, otherwise tell user tracking is already on
			if(!isEnabled){
			    locManager.requestLocationUpdates(provider, minTime, minDistance, this);
			    Location location = locManager.getLastKnownLocation(provider);
			    
			    lonInd.setText(R.string.main_menu_longitude_ind + String.valueOf(decFormat.format(location.getLongitude())));
			    latInd.setText(R.string.main_menu_longitude_ind + String.valueOf(decFormat.format(location.getLatitude())));
			    isEnabled = true;
			    trackingIndicator.setImageResource(R.drawable.ic_launcher);
			}else{
				//inform user that the tracking is already set
				Toast.makeText(this, "tracking is already enabled", Toast.LENGTH_SHORT).show();
			}
			
			
			/*
			//#####################
			String [] testLocation = {"this is longitude","this is latitude","this is geotag... bitch"}; //testing database
			location = ds.createLocation(testLocation[0], testLocation[1], testLocation[2]); //testing database
			//#####################
			
			Toast.makeText(getApplicationContext(), "Location: " + testLocation[0] + " " + testLocation[1] + " " + testLocation[2] + " added to the database", Toast.LENGTH_SHORT).show();
			*/
			
			break;
		case R.id.main_menu_stop_btn:
			//check whether tracking is on or not
			//if yes, stop tracking, otherwise tell user tracking is already off
			if(isEnabled){
				locManager.removeUpdates(this);
				isEnabled = false;
				trackingIndicator.setImageResource(R.drawable.ic_launcher_red);
			}else{
				Toast.makeText(this, "Tracking is already turned off", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.main_menu_locations_btn:
			//launch locations activity
			intent = new Intent(this, Locations.class);
			startActivity(intent);
			break;
		}//switch	
	}//onClick
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// which option in an options menu was selected 
		switch(item.getItemId()){
		case R.id.main_menu_about:
			//launch about activity
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//create options menu (menu that pops up as you press menu button on the device) 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}//onCreateOptoinsMenu
	
	@Override
	protected void onResume() {
		//when resuming activity, we reopen the database and request location updates
		ds.openDb();
		
		super.onResume();
		
		//when resuming activity, we start requesting location updates again
		locManager.requestLocationUpdates(provider, 400, 1, this);
		isEnabled = true;
	}
	
	@Override
	protected void onPause() {
		//when pausing the activity, we close database 
		ds.closeDb();
		
		super.onPause();
		
		//when pausing activity, we also dump locationupdates
		locManager.removeUpdates(this);
		isEnabled = false;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lonInd.setText(R.string.main_menu_longitude_ind + String.valueOf(decFormat.format(location.getLongitude())));
	    latInd.setText(R.string.main_menu_latitude_ind + String.valueOf(decFormat.format(location.getLatitude())));
	    //insert location into the database
	    ds.createLocation(String.valueOf(decFormat.format(location.getLongitude())), String.valueOf(decFormat.format(location.getLatitude())), "geotag");
		//Toast.makeText(this, "lon: " + String.valueOf(location.getLongitude()) + " lat: " + String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
	    
	}

	@Override
	public void onProviderDisabled(String provider) {
		// 
		Toast.makeText(this, "The provider is disabled: " + provider, Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// let the user know that provider is enabled
		Toast.makeText(this, "New provider is enabled: " + provider, Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case ACTIVATE_GPS_ALERT:
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("To receive locations the GPS must be turned on. Proceed?");
			builder.setCancelable(true);
			builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//takes user to the settings to turn gps on
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					return;
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
			break;
		
		}
		return super.onCreateDialog(id);
	}//onCreateDialog
	
	
}