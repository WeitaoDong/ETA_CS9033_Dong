package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.TripDatabaseHelper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateTripActivity extends Activity {
	
	private static final String TAG = "CreateTripActivity";
    private int tripID;
	private TextView trip_destination;
    private EditText trip_date;
    private EditText trip_time;
    private String tripTime;
    private Calendar c;
    private Date date;
    private EditText trip_name;
    private Trip trip = new Trip();
    private TextView trip_friend;
    private SQLiteDatabase db;
    private TripDatabaseHelper tripDatabaseHelper;
    static final int REQUEST_DATA = 1;
    static final int REQUEST_LOCATION = 2;
    static final String uri_location = "location://com.example.nyu.hw3api";
    private static ArrayList<String> namelist;
    private ArrayList<String> locationList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        setTitle("Create trip");
        namelist = new ArrayList<String>();
        locationList = new ArrayList<String>();

        Button CreateTrip = (Button) findViewById(R.id.create);
        Button CancelTrip = (Button) findViewById(R.id.cancel);
        Button AddFriends = (Button) findViewById(R.id.plusFriends);
        Button CheckPlace = (Button) findViewById(R.id.checkPlace);
        Button SearchPlace = (Button) findViewById(R.id.Search);
        CreateTrip.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
//                trip = new Trip();
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
                intent.putExtra("searchVal", "NYU Poly, New York::Chinese");
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
                intent.putExtra("searchVal", place1 + "::" + food1);
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
        c = Calendar.getInstance();
        trip_name = (EditText)findViewById(R.id.name);
        Log.e(TAG, trip_name.getText().toString());
        String Vtrip_name = trip_name.getText().toString().trim();
        trip_date = (EditText)findViewById(R.id.create_trip_date);
        trip_date.setFocusable(false);
        trip_date.setClickable(true);
        trip_date.setInputType(InputType.TYPE_NULL);

        trip_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateTripActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker dp, int year,
                                                  int month, int dayOfMonth) {
                                trip_date.setText(year + "-" + (month + 1) + "-"
                                        + dayOfMonth);

                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        trip_time = (EditText) findViewById(R.id.tripTime);
        trip_time.setFocusable(false);
        trip_time.setClickable(true);
        trip_time.setInputType(InputType.TYPE_NULL);

        trip_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimePickerDialog(CreateTripActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                trip_time.setText(hourOfDay + ":" + minute);
                            }
                        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                        true).show();
            }
        });

        String Vtrip_date = " ";
        Vtrip_date = trip_date.getText().toString()+ " " +trip_time.getText().toString();
        Log.e(TAG + "123", Vtrip_date);

        // Judge whether user has finished all the form or not
        if (TextUtils.isEmpty(Vtrip_name)||
                trip_friend == null ||
                trip_destination == null ||
                Vtrip_date==" " ) { // errors
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
//            trip = new Trip();
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
            Toast.makeText(CreateTripActivity.this, "Create a trip successfully!",Toast.LENGTH_SHORT).show();
            finish();
            return true;
        } else {
            this.trip = new Trip();
            return false;
        }
	}

    // This method is used to post command and data to server, and receive
    public String POST(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("command", "CREATE_TRIP");
            JSONArray jarrayLocation = new JSONArray(locationList);
            jsonObject.accumulate("location", jarrayLocation);
            Date date = new Date();
            jsonObject.accumulate("datetime", date.getTime());
            // todo trip.getName
            JSONArray jarrayPeople = new JSONArray(trip.getName());
            jsonObject.accumulate("people", jarrayPeople);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.i(TAG, json);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the
            // content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    // This method is used to change the received json object to json string.
    public static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject json = new JSONObject(result);
                int status = json.getInt("response_code");
                if (status == 0) {
                    Toast.makeText(getBaseContext(),
                            "Data received correctly!", Toast.LENGTH_LONG)
                            .show();
                    tripID = Integer.valueOf(String.valueOf(json.getLong("trip_id")));
                    Trip tmptrip = createTrip();
                    saveTrip(tmptrip);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "JSON parse failed");
            }
        }
    }

    public void ondone() {
        final Button toMain = (Button) findViewById(R.id.create);
        toMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                trip_name = (EditText) findViewById(R.id.name);
//                tripname = et1.getText().toString();


                tripTime = trip_date.getText().toString() + " " + trip_time.getText().toString();


                trip_name = (EditText) findViewById(R.id.name);
                String startname = trip_name.getText().toString();

//                startname += " ";
//                startname += partnername;
//
//                partnername = startname;
//                // add post function here.
//                //New part for geting trip_id from web server.
//                if (isConnected()) {
//                    new HttpAsyncTask()
//                            .execute("http://cs9033-homework.appspot.com");
//                } else {
//                    txtContacts.setText("You are NOT conncted");
//                }
            }
        });
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
//        Intent intent = new Intent(this,MainActivity.class);
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
                    ContentResolver contentResolver = getContentResolver();

                    Uri contactData = data.getData();
                    Cursor cursor = contentResolver.query(contactData, null, null, null,
                            null);
                    // check to make sure you got the results
                    if (cursor.getCount() == 0) {
                        cursor.close();
                        return;
                    }
                    trip_friend = (TextView) findViewById(R.id.friends);
                    // Get first row (will be only row in most cases)
                    cursor.moveToFirst();
                    String name = cursor.getString(cursor
                            .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    // todo error
                    if (namelist.contains(name)) {
                        Toast.makeText(CreateTripActivity.this, "You have added this person",Toast.LENGTH_SHORT).show();
                        return;
                    } else {

                        //todo save the number
                        // Display the name to the friends TextView
//trip.getFriends() == null ||
//                        trip.ConvertFriendsToString(trip.getFriends()).length() == 0
                        // Judge it whether the first one, if it is not add ", " before the name, then save it to Trip
                        if (namelist.isEmpty()) {
                            trip_friend.append(name);
                        } else {
                            trip_friend.append(", "+name);
//                            name = ", " + name;
                        }
                        namelist.add(name);
                        trip.setFriends(name);
                        Log.e(TAG+"NAME",trip.getFriends().toString());
                        cursor.close();
                        break;
                    }
                case REQUEST_LOCATION:

                    // Get the Bundle from data
                    Bundle extras = data.getExtras();

                    // Get the ArrayList named retVal
                    locationList = extras.getStringArrayList("retVal");

                    // Get the first String name
                    String Des_name = locationList.get(0);

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
