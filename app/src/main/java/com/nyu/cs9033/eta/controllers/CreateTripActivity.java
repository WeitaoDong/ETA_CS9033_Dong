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
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class CreateTripActivity extends Activity {
	
	private static final String TAG = "CreateTripActivity";
	private TextView destination;
    private TextView friends;
    private TextView tripTime;
    private Date date;
    private TextView real_name;
    ArrayList<String> AllName = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        real_name = (TextView)findViewById(R.id.name);
        friends = (TextView)findViewById(R.id.friends);
        destination = (TextView)findViewById(R.id.destination);
        tripTime = (TextView)findViewById(R.id.time);
        setContentView(R.layout.activity_create_trip);
        setTitle("Create trip");

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
        String trip_friends = friends.getText().toString().trim();
        String trip_date = tripTime.getText().toString().trim();
        String trip_destination = destination.getText().toString().trim();
        Trip trip = new Trip();
        if (TextUtils.isEmpty(trip_friends) || TextUtils.isEmpty(trip_date) || TextUtils.isEmpty(trip_date) ) {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_LONG).show();
        } else {
            ArrayList<Person> persons = new ArrayList<Person>();
            trip.setName(trip_friends);
            trip.setDestination(trip_destination);
            trip.setTime(date);
            return trip;
        }
        return trip;
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
        Iterator<Person> iterator = trip.getFriends().iterator();
        Intent intent = new Intent(CreateTripActivity.this,MainActivity.class);
        setResult(RESULT_OK,intent);
        intent.putExtra("name",createTrip());
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
	    setResult(RESULT_CANCELED);
        finish();
		// TODO - fill in here
	}
}
