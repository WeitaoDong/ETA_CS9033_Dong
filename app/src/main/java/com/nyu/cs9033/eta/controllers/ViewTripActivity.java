package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.TripDatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ViewTripActivity extends Activity {

	private static final String TAG = "ViewTripActivity";
    private static SQLiteDatabase db;
    private static TripDatabaseHelper tripDatabaseHelper;
    private Trip trip;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setTitle("View Trip");
        setContentView(R.layout.activity_view_trip);
        // Judge whether it is from MainActivity or it is from TripHistoryActivity
        if(getIntent().hasExtra("tripName")){
            getViewTrip(getIntent());
        } else {
            Trip trip = getTrip();
            viewTrip(trip);
        }
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
	public Trip getTrip() {
        // Get the database, and then set it to Trip
        tripDatabaseHelper = new TripDatabaseHelper(this);
        Cursor cursor = tripDatabaseHelper.getReadableDatabase().rawQuery("select * from trips order by _id desc",null);
        if(cursor.moveToFirst()) {
            trip = new Trip();
            trip.setName(cursor.getString(1));
            trip.setFriends(cursor.getString(2));
            trip.setDestination(cursor.getString(3));
            trip.setTime(cursor.getString(4));
        }
        return trip;
    }


    public void getViewTrip(Intent i) {
        // If it is from TripHistoryActivity then check the name from database, set them to Trip
        tripDatabaseHelper = new TripDatabaseHelper(this);
        Cursor cursor = tripDatabaseHelper.getReadableDatabase().rawQuery("select * from trips where name = ?; ",new String[]{i.getStringExtra("tripName")});
        if(cursor.moveToFirst()) {
            TextView name = (TextView) findViewById(R.id.name);
            name.setText(cursor.getString(1));
            TextView friends = (TextView) findViewById(R.id.friends);
            friends.setText(cursor.getString(2));
            TextView destination = (TextView) findViewById(R.id.destination);
            destination.setText(cursor.getString(3));
            TextView time = (TextView) findViewById(R.id.time);
            time.setText(cursor.getString(4));
        }

    }


	/**
	 * Populate the View using a Trip model.
	 * 
	 * @param trip The Trip model used to
	 * populate the View.
	 */
	public void viewTrip(Trip trip) {
        // If it is from MainActivity then set them from the getTrip function's trip
        if(trip!=null) {
            TextView name1 = (TextView) findViewById(R.id.name);
            name1.setText(trip.getName());
            TextView friends1 = (TextView) findViewById(R.id.friends);
            String friends2 =trip.ConvertFriendsToString(trip.getFriends());
            friends1.setText(friends2);
            TextView destination1 = (TextView) findViewById(R.id.destination);
            destination1.setText(trip.getDestination());
            TextView time1 = (TextView) findViewById(R.id.time);
            time1.setText(trip.getTime());

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
