package com.roboto503.geotrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainMenu extends Activity implements OnClickListener {
    
	Button start; //starts tracking
	Button stop; //stops tracking
	Button locations; //launches locations activity
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }//onCreate
    
    //initializes UI items declared in main_menu.xml
    public void initializeUI(){
    	//find buttons from layout main_menu layout view
    	start = (Button) findViewById(R.id.main_menu_start_btn);
    	stop = (Button) findViewById(R.id.main_menu_stop_btn);
    	locations = (Button) findViewById(R.id.main_menu_locations_btn);
    	
    	//set onClick-listeners to buttons
    	start.setOnClickListener(this);
    	stop.setOnClickListener(this);
    	locations.setOnClickListener(this);
    }//initializeUI


    //Event handlers for the UI buttons
	@Override
	public void onClick(View v) {
		// figure out which button was pressed
		switch(v.getId()){
		case R.id.main_menu_start_btn:
			//check whether tracking is on or not
			//if not, start tracking, otherwise tell user tracking is already on
			break;
		case R.id.main_menu_stop_btn:
			//check whether tracking is on or not
			//if yes, stop tracking, otherwise tell user tracking is already off
			break;
		case R.id.main_menu_locations_btn:
			//launch locations activity
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
}