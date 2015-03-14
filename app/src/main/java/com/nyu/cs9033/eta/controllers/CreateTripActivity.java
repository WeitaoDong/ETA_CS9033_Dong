package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class CreateTripActivity extends Activity {
	
	private static final String TAG = "CreateTripActivity";
	private EditText destination;
    private EditText friends;
    private EditText tripTime;
    private Date date;
    private EditText real_name;
    ArrayList<String> AllName = new ArrayList<String>();
    Trip trip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        setTitle("Create trip");
        Button CreateTrip = (Button) findViewById(R.id.done);
        Button CancelTrip = (Button) findViewById(R.id.cancel);
        CreateTrip.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Trip trip = createTrip();
                saveTrip(trip);
            }
        });
        CancelTrip.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                cancelTrip();
            }
        });

		// TODO - fill in here
	}
	
	/**
	 * This method should be used to
	 * instantiate a Trip model object.
	 * 
	 * @return The Trip as represented
	 * by the View.
	 */
	public Trip createTrip() {
        real_name = (EditText)findViewById(R.id.name);
        String trip_name = real_name.getText().toString().trim();
        friends = (EditText)findViewById(R.id.friends);
        String friend = friends.getText().toString().trim();
        destination = (EditText)findViewById(R.id.destination);
        String trip_destination = destination.getText().toString().trim();
        tripTime = (EditText)findViewById(R.id.time);
        String trip_date = tripTime.getText().toString().trim();
        if (TextUtils.isEmpty(trip_name)|| TextUtils.isEmpty(friend) || TextUtils.isEmpty(trip_date) || TextUtils.isEmpty(trip_date) ) {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_LONG).show();
            return null;
        } else {
            Trip trip = new Trip(trip_name,friend,trip_destination,trip_date);

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
        createTrip();
        Intent intent = new Intent(CreateTripActivity.this,MainActivity.class);
//        intent.putExtra("",createTrip(findViewById(R.id.done)));
        intent.putExtra("create trip",trip);
        setResult(RESULT_OK, intent);
        finish();
        return true;
		// TODO - fill in here
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
		// TODO - fill in here
	}
}
