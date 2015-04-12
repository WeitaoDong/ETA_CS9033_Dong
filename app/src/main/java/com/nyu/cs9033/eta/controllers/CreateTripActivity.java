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
	private TextView trip_destination;
    private EditText trip_time;
    private Date date;
    private EditText trip_name;
    private Trip trip = new Trip();
    private TextView trip_friend;
    private SQLiteDatabase db;
    private TripDatabaseHelper tripDatabaseHelper;
    static final int REQUEST_DATA = 1;
    static final int REQUEST_LOCATION = 2;
    static final String uri_location = "location://com.example.nyu.hw3api";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        setTitle("Create trip");
        Button CreateTrip = (Button) findViewById(R.id.create);
        Button CancelTrip = (Button) findViewById(R.id.cancel);
        Button AddFriends = (Button) findViewById(R.id.plusFriends);
        Button CheckPlace = (Button) findViewById(R.id.checkPlace);
        Button SearchPlace = (Button) findViewById(R.id.Search);
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
        CheckPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri_location));
                intent.putExtra("searchVal","NYU Poly, New York::Chinese");
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,REQUEST_LOCATION);
                }
            }
        });
        SearchPlace.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri_location));
                EditText place = (EditText) findViewById(R.id.place);
                String place1 = place.getText().toString().trim();
                EditText food = (EditText) findViewById(R.id.food);
                String food1 = food.getText().toString().trim();
                intent.putExtra("searchVal",place1+"::"+food1);
                if(intent.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(intent,REQUEST_LOCATION);
                }
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
        // Get the Trip_name and time then save it to Trip
        trip_name = (EditText)findViewById(R.id.name);
        String Vtrip_name = trip_name.getText().toString().trim();
        trip_time = (EditText)findViewById(R.id.time);
        String Vtrip_date = trip_time.getText().toString().trim();
        // Judge whether user has finished all the form or not
        if (TextUtils.isEmpty(Vtrip_name)||
                trip.getFriends()==null ||
                trip.getDestination()==null ||
                TextUtils.isEmpty(Vtrip_date) ) {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_LONG).show();
            return null;
        } else {
            // Save the trip_name and time, others are saved by OnActivityResult function
            trip.setName(Vtrip_name);
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
            //Get the database then insert trip finally return to MainActivity
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
        // Check whether the data is null
        if(data!=null) {
//            Log.e("4321" +TAG,"4321 + " + requestCode);
            switch (requestCode){
                case REQUEST_DATA:
                    // Get the name from data
                    Uri uri = data.getData();
                    String[] queryFields = new String[]{
                            ContactsContract.Contacts.DISPLAY_NAME
                    };

                    Cursor cursor = getApplication().getContentResolver()
                            .query(uri, queryFields, null, null, null);
                    // check to make sure you got the results
                    if (cursor.getCount() == 0) {
                        cursor.close();
                        return;
                    }
                    // Get first row (will be only row in most cases)
                    cursor.moveToFirst();
                    String person = cursor.getString(0);
                    // Display the name to the friends TextView
                    trip_friend = (TextView) findViewById(R.id.friends);
                    // Judge it whether the first one, if it is not add ", " before the name, then save it to Trip
                    if (trip.getFriends() == null ||
                            trip.ConvertFriendsToString(trip.getFriends()).length() == 0) {
                        trip_friend.append(person);
                        trip.setFriends(person);
                    } else {
                        trip_friend.append(", ");
                        trip_friend.append(person);
                        person = ", " + person;
                        trip.setFriends(person);
                    }

                    cursor.close();
                    break;
                case REQUEST_LOCATION:
//                    Log.e("4321" + TAG, "4321 = " + requestCode);
                    // Get the Bundle from data
                    Bundle extras = data.getExtras();
                    // Get the ArrayList named retVal
                    ArrayList<String> res = new ArrayList<String>(extras.getStringArrayList("retVal"));
                    // Get the first String name
                    String Des_name = res.get(0);
                    // Display the name to the friends TextView
                    trip_destination = (TextView) findViewById(R.id.destination);
                    trip_destination.append(Des_name);
                    // Save it to Trip
                    trip.setDestination(Des_name);
                    break;
            }
        }
    }
}
