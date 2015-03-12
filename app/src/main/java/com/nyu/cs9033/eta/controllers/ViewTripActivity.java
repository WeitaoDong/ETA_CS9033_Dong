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
import java.util.Iterator;

public class ViewTripActivity extends Activity {

	private static final String TAG = "ViewTripActivity";
	private TextView destination;
    private TextView time;
    private TextView friends;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);
        setTitle("View Trip");
        destination = (TextView) findViewById(R.id.destination);
        Log.v(destination.toString(),"222");
        time = (TextView) findViewById(R.id.time);
        friends = (TextView) findViewById(R.id.friends);
        Trip trip = getTrip(getIntent());
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
        ArrayList<Trip> trips = i.getExtras().getParcelableArrayList("parcel");
        Trip trip = trips.get(0);

//        i = getIntent();
//        String message = i.getStringExtra(MainActivity.EXTRA_MESSAGE);
//        TextView textView = new TextView(this);
//        textView.setText(message);
		// TODO - fill in here

		return trip;
	}

	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip The Trip model used to
	 * populate the View.
	 */
	public void viewTrip(Trip trip) {
        if(trip!=null) {
            destination.setText(trip.getDestination());
            time.setText(trip.getTime().toString());
            Iterator<Person> itr = trip.getFriends().iterator();
            while (itr.hasNext()) {
                friends.append(itr.next().getName());
            }
        } else {
            new AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_action_error)
                            .setTitle("Alert")
                            .setMessage("No trip found, please setup your own trip!")
                            .setPositiveButton("Confirm",null).show();
            return;
        }

		// TODO - fill in here
	}
}
