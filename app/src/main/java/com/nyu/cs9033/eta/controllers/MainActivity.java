package com.nyu.cs9033.eta.controllers;


import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
    protected final static String EXTRA_MESSAGE = "com.nyu.cs9033.eta.MESSAGE";
    private TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Log.v(TAG,"index"+1);
        // viewTrip.setOnClickListener(this);
		// TODO - fill in here
	}

	/**
	 * This method should start the
	 * Activity responsible for creating
	 * a Trip.
	 */
	public void startCreateTripActivity(View view) {
            Intent intent = new Intent(this, CreateTripActivity.class);
            startActivityForResult(intent,1);
        }
		// TODO - fill in here
	
	/**
	 * This method should start the
	 * Activity responsible for viewing
	 * a Trip.
	 */
	public void startViewTripActivity(View view) {
            Intent intent = new Intent(this, ViewTripActivity.class);
            startActivity(intent);
            }
            // TODO - fill in here
	
	/**
	 * Receive result from CreateTripActivity here.
	 * Can be used to save instance of Trip object
	 * which can be viewed in the ViewTripActivity.
	 * 
	 * Note: This method will be called when a Trip
	 * object is returned to the main activity. 
	 * Remember that the Trip will not be returned as
	 * a Trip object; it will be in the persisted
	 * Parcelable form. The actual Trip object should
	 * be created and saved in a variable for future
	 * use, i.e. to view the trip.
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO - fill in here
        if(resultCode != Activity.RESULT_OK) return;
        Uri contactUri = data.getData();
        String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
        };
        //Cursor c = getContentResolver().query(contactUri,queryFields,null,null,null);
	}
}
