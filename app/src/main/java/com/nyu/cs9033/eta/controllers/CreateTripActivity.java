package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.TripDatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.TextWatcher;
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
    private EditText trip_time;
    private Date date;
    private EditText trip_name;
    private Trip trip = new Trip();
    private TextView trip_friend;
    private SQLiteDatabase db;
    private TripDatabaseHelper tripDatabaseHelper;
    static final int REQUEST_DATA = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        setTitle("Create trip");
        Button CreateTrip = (Button) findViewById(R.id.create);
        Button CancelTrip = (Button) findViewById(R.id.cancel);
        Button AddFriends = (Button) findViewById(R.id.plusFriends);
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
        AddFriends.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,REQUEST_DATA);
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
        //todo how to deal with friends
        trip_destination = (EditText)findViewById(R.id.destination);
        String Vtrip_destination = trip_destination.getText().toString().trim();
        trip_time = (EditText)findViewById(R.id.time);
        String Vtrip_date = trip_time.getText().toString().trim();
        if (TextUtils.isEmpty(Vtrip_name)|| trip.getFriends()==null || TextUtils.isEmpty(Vtrip_destination) || TextUtils.isEmpty(Vtrip_date) ) {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_LONG).show();
            return null;
        } else {
            Log.e(Integer.toString(tripID),TAG+"4321");
            trip.setName(Vtrip_name);
            trip.setDestination(Vtrip_destination);
            trip.setTime(Vtrip_date);
            return trip;
        }
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
        if(trip!=null) {
            //Get the database then insert trip
            tripDatabaseHelper = new TripDatabaseHelper(this);
            tripDatabaseHelper.insertTrip(trip);
            finish();
            return true;
        } else return false;
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
        Intent intent = new Intent(this,MainActivity.class);
	    setResult(RESULT_CANCELED);
        finish();
	}

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO - fill in here
        if(data!=null) {
            Log.e("4321"+TAG,"");
            if (resultCode!=Activity.RESULT_OK) return;
                Log.e("4321"+TAG,"4321");
                Uri uri = data.getData();
                String[] queryFields = new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME
                };

                    Cursor cursor = getApplication().getContentResolver()
                            .query(uri, queryFields, null, null, null);
                    // check to make sure you got the results
                    if(cursor.getCount()==0){
                        cursor.close();
                        return;
                    }
                    // Get first row (will be only row in most cases)
                    cursor.moveToFirst();
                    String person = cursor.getString(0);
                    Log.e("4321"+TAG,person);
                    trip_friend = (TextView) findViewById(R.id.friends);
                    Log.e("4321"+TAG,trip_friend.toString());
                    if(trip.getFriends()==null) {
                        trip_friend.append(person);
                        trip.setFriends(person);
                    } else {
                        trip_friend.append(", ");
                        trip_friend.append(person);
                        trip.setFriends(person);
                    }

                    cursor.close();
        }
    }
}
