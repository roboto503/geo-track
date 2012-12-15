package com.roboto503.geotrack;

import java.text.DecimalFormat;

import com.roboto503.geotrack.db.GeoTrackerLocation;
import com.roboto503.geotrack.db.LocationsDataSource;
import com.roboto503.geotrack.MyMap;

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
import android.text.format.Time;
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
	private Button map; //launches map activity
	private ImageView trackingIndicator; //icon to show whether tracking is on or off
	private TextView lonInd; //indicator for received longitude updates
	private TextView latInd; //indicator for received latitude updates
	private TextView dateInd; //indicator for current date
	private String date = "date";
	
	private static final int ACTIVATE_GPS_ALERT = 1; // constant for identifying dialog 
	
	//Location
	private LocationManager locManager;
	private String provider;
	private boolean isEnabled = false; //flag for requesting location updates
	private long minTime = 500; //update time interval in milliseconds
	private float minDistance = 5.0f; //update distance interval in meters
	private DecimalFormat decFormat = new DecimalFormat("#.###"); //DecimalFormat for location's longitude & latitude. We only need three decimals. 
	
	//database
	private LocationsDataSource ds;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        
        //initialize date, UI and GPS
        date = date();
        
        initializeUI();
        
        initializeGPS();
        
       // open database 
       ds = new LocationsDataSource(this);
       ds.openDb();
        
    }//onCreate
    
    /** initializes UI items declared in main_menu.xml*/
    public void initializeUI(){
    	//find buttons from layout main_menu layout view
    	start = (Button) findViewById(R.id.main_menu_start_btn);
    	stop = (Button) findViewById(R.id.main_menu_stop_btn);
    	locations = (Button) findViewById(R.id.main_menu_locations_btn);
    	map = (Button) findViewById(R.id.main_menu_map_btn);
    	
    	//initialize indicators
    	trackingIndicator = (ImageView) findViewById(R.id.main_menu_pic);
    	lonInd = (TextView) findViewById(R.id.main_menu_lon);
    	latInd = (TextView) findViewById(R.id.main_menu_lat);
    	dateInd = (TextView) findViewById(R.id.main_menu_date);
    	
    	//initialize indicators with default values  
    	lonInd.setText(R.string.main_menu_longitude_ind);
	    latInd.setText(R.string.main_menu_latitude_ind);
    	dateInd.setText(date);
    	trackingIndicator.setImageResource(R.drawable.ic_launcher_yellow);
    	
    	//set onClick-listeners to buttons. Because this class implements onClicListener class, we can use this pointer
    	start.setOnClickListener(this);
    	stop.setOnClickListener(this);
    	locations.setOnClickListener(this);
    	map.setOnClickListener(this);
    }//initializeUI

    /** */
    private String date(){
    	//get time for the date field of the location
    	Time today = new Time(Time.getCurrentTimezone());
    	today.setToNow();
    	
    	//create date string
    	StringBuilder sb = new StringBuilder();
    	sb.append(getResources().getString(R.string.main_menu_date_ind));
    	sb.append(today.monthDay);
    	sb.append("/");
    	sb.append(today.month);
    	sb.append("/");
    	sb.append(today.year);
    	return sb.toString();
    }

    /** Initializes GPS */
    private void initializeGPS(){
    	locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    provider = locManager.getBestProvider(criteria, false);
	    
	    //ask user to turn on gps or mobile network so that tracking is possible
	    if(!locManager.isProviderEnabled(provider)){
	    	onCreateDialog(ACTIVATE_GPS_ALERT);
	    }
	    
	    isEnabled=true;
	    updateUI();
    }//initializeGPS
    
    /**Event handlers for the UI buttons */
	@Override
	public void onClick(View v) {
		Intent intent;
		// figure out which button was pressed
		switch(v.getId()){
		case R.id.main_menu_start_btn:
			checkTrackingStatus();
			break;
		case R.id.main_menu_stop_btn:
			//
			checkTrackingStatus();
			break;
		case R.id.main_menu_locations_btn:
			//launch locations activity
			intent = new Intent(this, Locations.class);
			startActivity(intent);
			break;
		case R.id.main_menu_map_btn:
			//launch map activity
			intent = new Intent(this, MyMap.class);
			startActivity(intent);
			break;
		}//switch	
	}//onClick
	
	/** check the status of tracking*/
	private void checkTrackingStatus(){
		if(isEnabled){
			//if tracking on, stop tracking, remove location updates and update UI
			locManager.removeUpdates(this);
			isEnabled = false;
			updateUI();
		}else{
			//if tracking is off, stop tracking, start asking location updates and update UI
			locManager.requestLocationUpdates(provider, minTime, minDistance, this);
		    Location location = locManager.getLastKnownLocation(provider);
		    isEnabled = true;
		    updateUI();
		}
	}//checkTrackingStatus 
	
	/** updateUI to inform user whether tracking is on or off*/
	private void updateUI(){
		if(isEnabled){
			stop.setEnabled(true);
			start.setEnabled(false);
			trackingIndicator.setImageResource(R.drawable.ic_launcher);
		}else{
		    stop.setEnabled(false);
		    start.setEnabled(true);
		    trackingIndicator.setImageResource(R.drawable.ic_launcher_red);
		}
	}//updateUI
	
	/** event handlers for menu items*/
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
	
	/** create options menu (menu that pops up as you press menu button on the device) */ 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}//onCreateOptoinsMenu
	
	/** */
	@Override
	protected void onResume() {
		//when resuming activity, we reopen the database and request location updates
		ds.openDb();
		
		super.onResume();
		
		//when resuming activity, we start requesting location updates again
		locManager.requestLocationUpdates(provider, 400, 1, this);
		isEnabled = true;
	}
	
	/** */
	@Override
	protected void onPause() {
		//when pausing the activity, we close database 
		ds.closeDb();
		
		super.onPause();
		
		//when pausing activity, we also dump locationupdates
		locManager.removeUpdates(this);
		isEnabled = false;
	}

	/** When location is changed, create a new database entry and update indicators*/
	@Override
	public void onLocationChanged(Location location) {
	    //insert location into the database
	    ds.createLocation(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()), date); //for more precise location
	    //ds.createLocation(String.valueOf(decFormat.format(location.getLongitude())), String.valueOf(decFormat.format(location.getLatitude())), date); //I think three decimals is enough 
	    
	    //update location indicators
	    lonInd.setText(getResources().getString(R.string.main_menu_longitude_ind) + decFormat.format(location.getLongitude()));
	    latInd.setText(getResources().getString(R.string.main_menu_latitude_ind) + decFormat.format(location.getLatitude()));
	}

	/** implemented abstract method*/
	@Override
	public void onProviderDisabled(String provider) {
		// 
		Toast.makeText(this, "The provider is disabled: " + provider, Toast.LENGTH_SHORT).show();
	}//onProviderDisabled

	/** implemented abstract method*/
	@Override
	public void onProviderEnabled(String provider) {
		// let the user know that provider is enabled
		Toast.makeText(this, "New provider is enabled: " + provider, Toast.LENGTH_SHORT).show();
	}//onProviderEnabled

	/** implemented abstract method*/
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}//onStatusChanged
	
	/** Dialogs to interact with the user*/
	@Override
	protected Dialog onCreateDialog(int id) {
		//method variables
		String alertMsg = "";
		String continueLbl = getResources().getString(R.string.proceed);
		String cancelLbl = getResources().getString(R.string.cancel);
		
		//Which dialog is started
		switch(id){
		//Dialog to ask user to enable gps 
		case ACTIVATE_GPS_ALERT:
			alertMsg = getResources().getString(R.string.main_menu_gps_alert);
			
			//creates a new dialog
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(alertMsg);
			builder.setCancelable(true);
			
			//set buttons to the dialog, set OnClick event handlers to the buttons
			builder.setPositiveButton(continueLbl, new DialogInterface.OnClickListener() {	
				//OnClick handler for continue button in GPS dialog 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//takes user to the settings to turn gps on
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}//onClick
			});
			builder.setNegativeButton(cancelLbl, new DialogInterface.OnClickListener() {
				//OnClick handler for cancel button in GPS dialog 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			
			//create the dialog and show it to the user
			AlertDialog dialog = builder.create();
			dialog.show();
			break;
		}
		return super.onCreateDialog(id);
	}//onCreateDialog
	
}