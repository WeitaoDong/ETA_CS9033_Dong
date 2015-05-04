package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.TripDatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by weitao on 4/4/15.
 */
public class TripHistoryActivity extends Activity {

    private static final String TAG = "TripHistoryActivity";
    private static final String TABLE_TRIP = "trip";
    private TripDatabaseHelper tripDatabaseHelper;
    private SQLiteDatabase database;
    private List<Trip> tripList;
    private List<String> res;
    private ListView listView;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triphistoryactivity);
        listView = (ListView) findViewById(R.id.trip_history);
        setTitle("Trip_History");
        viewName();
    }

    // View the trip_name through ViewHistory button
    public void viewName(){
        // Get all the database of trips
        tripDatabaseHelper = new TripDatabaseHelper(this);
        final ArrayList<String> name = tripDatabaseHelper.getAllTripName();
        if(!name.isEmpty()){
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,name);
            // Set the Adapter to list all the trip_name
            listView.setAdapter(arrayAdapter);
            // Set the click action to the more details
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    // Pass the trip_name to ViewTripActivity
                    Object data_item = name.get(position);
                    Intent intent = new Intent(TripHistoryActivity.this,ViewTripActivity.class);
                    intent.putExtra("tripName", data_item.toString());
                    startActivity(intent);
                }
            });
        }
    }

    public void onResume() {
        tripDatabaseHelper.onOpen(database);
        super.onResume();
    }

    public void onPause() {
        tripDatabaseHelper.close();
        super.onPause();
    }
}
