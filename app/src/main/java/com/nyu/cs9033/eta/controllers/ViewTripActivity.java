package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;

public class ViewTripActivity extends Activity {

	private static final String TAG = "ViewTripActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Trip trip = getTrip(getIntent());
        setTitle("View Trip");
//        setResult

//        destination = (TextView) findViewById(R.id.destination);
//        time = (TextView) findViewById(R.id.time);
//        friends = (TextView) findViewById(R.id.friends);
//        Log.v(getIntent().toString(),"222");
//        Trip trip = getTrip(getIntent());
        setContentView(R.layout.activity_view_trip);
        viewTrip(trip);

	}
	
	/**
	 * Create a Trip object via the recent trip that
	 * was passed to TripViewer via an Intent.
	 * 
	 * @param i The Intent that contains
	 * the most recent trip data.
	 * 
	 * @return The Trip that was most recently
	 * passed to TripViewer, or null if there
	 * is none.
	 */
	public Trip getTrip(Intent i) {
        Trip trip = i.getParcelableExtra("create trip");
        if (trip != null) {
            return trip;
        } else return null;
    }


	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip The Trip model used to
	 * populate the View.
	 */
	public void viewTrip(Trip trip) {
        if(trip!=null) {
            TextView name1 = (TextView) findViewById(R.id.name);
            name1.setText(trip.getName());
            TextView friends1 = (TextView) findViewById(R.id.destination);
            friends1.setText(trip.getFriends());
            TextView destination1 = (TextView) findViewById(R.id.friends);
            destination1.setText(trip.getDestination());
            TextView time1 = (TextView) findViewById(R.id.time);
            time1.setText(trip.getTime());

//            while (itr.hasNext()) {
//                friends.append(itr.next().getName());
//            }
        } else {
            new AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_action_error)
                            .setTitle("Alert")
                            .setMessage("No trip found, please create trip!")
                            .setPositiveButton("Confirm",null).show();
        }

		// TODO - fill in here
	}
}
