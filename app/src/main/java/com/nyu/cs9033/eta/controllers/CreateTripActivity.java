package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class CreateTripActivity extends Activity {
	
	private static final String TAG = "CreateTripActivity";
	private TextView destination;
    private TextView friends;
    private TextView start_time;
    private Date date;
    ArrayList<String> AllName = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        friends = (TextView)findViewById(R.id.friends);
        destination = (TextView)findViewById(R.id.destination);
        start_time = (TextView)findViewById(R.id.start_time);
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
        ArrayList<Person> persons = new ArrayList<Person>();
        Trip p = new Trip();
        p.setDestination(destination.toString());
        p.setTime(date);
        for(String name : AllName){
                Person newPerson = new Person();
                newPerson.setName(name);
                persons.add(newPerson);
        }
        p.setFriends(persons);
        return p;

		// TODO - fill in here

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
        return true;
		// TODO - fill in here

	}

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
