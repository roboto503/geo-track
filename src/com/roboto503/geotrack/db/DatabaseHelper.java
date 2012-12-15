package com.roboto503.geotrack.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

	//Table and column titles
	public static final String TABLE_LOCATION = "lonlat";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LON = "longitude";
	public static final String COLUMN_LAT = "latitude";
	public static final String COLUMN_DATE = "date";
	
	//Database name and version
	private static final String DATABASE_NAME = "locations.db";
	private static final int DATABASE_VERSION = 1;
	
	//Create and drop database sql statements
	private static final String DATABASE_CREATE = "create table " + TABLE_LOCATION + " (" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_LON + " text not null, " + COLUMN_LAT + " text not null, " + COLUMN_DATE + " text not null);";	
	private static final String DATABASE_DROP = "drop table if exists " + TABLE_LOCATION;
	
	
	/** creates the database helper*/
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}//DatabaseHelper

	
	/** creates the database if not exists*/
	@Override
	public void onCreate(SQLiteDatabase db) {
		//executes database create sql statement
		db.execSQL(DATABASE_CREATE);	
	}//onCreate

	
	/** upgrades existing database if necessary*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//if database is upgraded, the existing database will be destroyed along old data
		db.execSQL(DATABASE_DROP);
		//recreate new database
		onCreate(db);
	}//onUpgrade

}
