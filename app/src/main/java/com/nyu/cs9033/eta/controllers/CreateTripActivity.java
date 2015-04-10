package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.TripDatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CreateTripActivity extends Activity {
	
	private static final String TAG = "CreateTripActivity";
    private int tripID;
	private EditText trip_destination;
    private EditText trip_friend;
    private EditText trip_time;
    private Date date;
    private EditText trip_name;
    ArrayList<String> AllName = new ArrayList<String>();
    private Trip trip;
    private SQLiteDatabase db;
    private TripDatabaseHelper tripDatabaseHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        setTitle("Create trip");
        Button CreateTrip = (Button) findViewById(R.id.create);
        Button CancelTrip = (Button) findViewById(R.id.cancel);
        CreateTrip.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                trip = createTrip();
                saveTrip(trip);
            }
        });
        CancelTrip.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                cancelTrip();
            }
        });
	}
	
	/**
	 * This method should be used to
	 * instantiate a Trip model object.
	 * 
	 * @return The Trip as represented
	 * by the View.
	 */
	public Trip createTrip() {
        trip_name = (EditText)findViewById(R.id.name);
        String Vtrip_name = trip_name.getText().toString().trim();
        trip_friend = (EditText)findViewById(R.id.friends);
        String Vfriend = trip_friend.getText().toString().trim();
        trip_destination = (EditText)findViewById(R.id.destination);
        String Vtrip_destination = trip_destination.getText().toString().trim();
        trip_time = (EditText)findViewById(R.id.time);
        String Vtrip_date = trip_time.getText().toString().trim();
        if (TextUtils.isEmpty(Vtrip_name)|| TextUtils.isEmpty(Vfriend) || TextUtils.isEmpty(Vtrip_destination) || TextUtils.isEmpty(Vtrip_date) ) {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_LONG).show();
            return null;
        } else {
            Log.e(Integer.toString(tripID),TAG+"4321");
            Trip trip = new Trip();
            trip.setName(Vtrip_name);
            Log.e(TAG+"4321",Vfriend);
            trip.setFriends((Vfriend));
            trip.setDestination(Vtrip_destination);
            trip.setTime(Vtrip_date);
            return trip;
        }
//        return trip;
    }

	/**
	 * For HW2 you should treat this method as a 
	 * way of sending the Trip data back to the
	 * main Activity.
	 * 
	 * Note: If you call finish() here the Activity 
	 * will end and pass an Intent back to the
	 * previous Activity using setResult().
	 * 
	 * @return whether the Trip was successfully 
	 * saved.
	 */
	public boolean saveTrip(Trip trip) {
//        createTrip();
//        Log.v(TAG, "111");
        if(trip!=null) {

            //Get the database then insert trip
            tripDatabaseHelper = new TripDatabaseHelper(this);
            tripDatabaseHelper.insertTrip(trip);

//            Cursor cursor = db.rawQuery("select max( _id) from trips; ",null);
//            cursor.moveToFirst();
//            trip.setTripID(cursor.getInt(0)+1);
//            tripDatabaseHelper.insertTrip(trip);
//            Log.i(TAG + "4321", Integer.toString(cursor.getColumnIndex("name")));
//            Log.i(TAG+"4321",Integer.toString(cursor.getColumnIndex(" _id")));
//            Log.i(TAG+"4321",Integer.toString(cursor.getColumnIndex("friends")));
//            Intent returnIntent = new Intent();
//            setResult(RESULT_OK, returnIntent);
//            db.close();

            finish();
            return true;
        } else return false;
	}
//    public void onClick(View view){
//        String destination =
//    }

	/**
	 * This method should be used when a
	 * user wants to cancel the creation of
	 * a Trip.
	 * 
	 * Note: You most likely want to call this
	 * if your activity dies during the process
	 * of a trip creation or if a cancel/back
	 * button event occurs. Should return to
	 * the previous activity without a result
	 * using finish() and setResult().
	 */
	public void cancelTrip() {
        Intent intent = new Intent(this,MainActivity.class);
	    setResult(RESULT_CANCELED);
        finish();
	}


}
