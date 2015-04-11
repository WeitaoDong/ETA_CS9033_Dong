package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.TripDatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
//        Button detail1 = (Button)findViewById(R.id.trip_history);
//        detail1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(null,ViewTripActivity.class);
//                intent.putExtra("trip", tripList.get(0));
//                startActivity(intent);
//            }
//        });
    }

    public List<String> GetAllName(){
        tripDatabaseHelper = new TripDatabaseHelper(this);
        res = new ArrayList<String>();
        for(Trip a:tripDatabaseHelper.getAllTrip()){
            res.add(a.getName());
        }
        return res;
    }

    public void viewName(){
        tripDatabaseHelper = new TripDatabaseHelper(this);
        ArrayList<String> name = tripDatabaseHelper.getAllTripName();
        if(!name.isEmpty()){
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,name);
            listView.setAdapter(arrayAdapter);
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
