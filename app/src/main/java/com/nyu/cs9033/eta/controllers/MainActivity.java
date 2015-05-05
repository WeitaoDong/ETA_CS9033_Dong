package com.nyu.cs9033.eta.controllers;


import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
    protected final static String EXTRA_MESSAGE = "com.nyu.cs9033.eta.MESSAGE";

    private TextView textView;
    private Person person;
    Trip trip;
    private ArrayList<Trip> context;
    private ListView listView;
    private ArrayList<String> allFriends;
    private TextView currentName;
    private TextView destination;
    private TextView tripTime;
    private TextView common;
    private Button Update,Arrival;
    private Messenger mService = null;
    boolean isBound = false;

    /** Flag indicating whether we have called bind on the service. */
    final Messenger mMessenger = new Messenger(new IncomingHandler());


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Button create_trip = (Button) findViewById(R.id.create_trip);
        Button view_trip = (Button) findViewById(R.id.view);
        Button view_history = (Button) findViewById(R.id.trip_history);
        Update = (Button) findViewById(R.id.Update);
        Arrival = (Button) findViewById(R.id.Arrival);
        create_trip.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startCreateTripActivity();
            }
        });
        view_trip.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startViewTripActivity();
            }
        });
        view_history.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startTripHistoryActivity();
            }
        });

        startService(new Intent(MainActivity.this, GPS_Location.class));
        onRefresh();
        setCurrentTripInfo(getIntent());
        CheckIfServiceIsRunning();
	}

	/**
	 * This method should start the
	 * Activity responsible for creating
	 * a Trip.
	 */
	public void startCreateTripActivity() {
        Intent intent = new Intent(this, CreateTripActivity.class);
        startActivity(intent);
    }

	/**
	 * This method should start the
	 * Activity responsible for viewing
	 * a Trip.
	 */
	public void startViewTripActivity() {
        Intent intent = new Intent(this, ViewTripActivity.class);
        startActivity(intent);
    }

    public void startTripHistoryActivity() {
        Intent intent = new Intent(this,TripHistoryActivity.class);
        startActivity(intent);
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GPS_Location.MSG_FROM_SERVICE_UPLOAD_LOCATION:
                    // handle request from service
                    common = (TextView) findViewById(R.id.cur_trip);
                    String str = msg.getData().getString("current_location");
                    common.setText(str);
                    break;
                case GPS_Location.MSG_FROM_SERVICE_TRIP_STATUS:
                    common = (TextView) findViewById(R.id.TrackingInfo);
                    String result = msg.getData().getString("trip_status");
                    common.setText(parseTripStatus(result));
                    break;
                default:
                    break;
            }
        }
    }

    private String parseTripStatus(String result) {
        JSONObject jsonobject;
        String convert = "";
        try {
            jsonobject = new JSONObject(result);
            JSONArray distance_left = jsonobject.getJSONArray("distance_left");
            JSONArray time_left = jsonobject.getJSONArray("time_left");
            JSONArray people = jsonobject.getJSONArray("people");
            for (int i = 0; i < people.length(); i++) {
                convert += people.getString(i) + " :\tdistance_left\t"
                        + distance_left.getDouble(i) + "\ttime_left\t"
                        + time_left.getInt(i) + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convert;
    }

    private void setCurrentTripInfo(Intent i) {

        trip = i.getParcelableExtra("CurrentTrip");
        if (trip != null) {
            currentName = (TextView) findViewById(R.id.TripName);
            currentName.append(trip.getName());

            destination = (TextView) findViewById(R.id.Dest);
            destination.append(trip.getDestination());

            tripTime = (TextView) findViewById(R.id.Time);
            tripTime.append(trip.getTime());

            listView = (ListView) findViewById(R.id.AllFriends);
            allFriends = trip.ConvertFriendsToList(trip.getFriends());

            final ArrayAdapter<String> Friends = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allFriends);
            listView.setAdapter(Friends);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = allFriends.size()*200;
            listView.setLayoutParams(params);
            listView.requestLayout();

            Update.setVisibility(View.VISIBLE);
            Arrival.setVisibility(View.VISIBLE);

        }
    }

    private void onRefresh() {

        // show location button click event
        Update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if GPS enabled
                Message msg = Message.obtain(null,
                        GPS_Location.MSG_FROM_ACTIVITY);
                msg.replyTo = mMessenger;
                Bundle b = new Bundle();
                b.putLong("trip_id", trip.getTripID());
                msg.setData(b);
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean CheckIfServiceIsRunning() {
        // If the service is running when the activity starts, we want to automatically bind to it.
        if (GPS_Location.isRunning()) {
            doBindService();
            return true;
        } else return false;
    }
    void doBindService() {
        // Establish a connection with the service. We use an explicit
        // class name because there is no reason to be able to let other
        // applications interact with our component.
        bindService(new Intent(MainActivity.this, GPS_Location.class),
                mConnection, // ServiceConnection object
                Context.BIND_AUTO_CREATE); // Create service if not

        isBound = true;

    }
    /******** Communicate with service ************/
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);

                Message msg = Message.obtain(null,
                        GPS_Location.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
            try {
                Bundle b = new Bundle();
                b.putLong("trip_id", trip.getTripID());
                msg.setData(b);
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }


        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected - process crashed.
            mService = null;
        }
    };

    void doUnbindService() {
        if (isBound) {
            // If we registered with the service, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, GPS_Location.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            isBound = false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
            stopService(new Intent(MainActivity.this, GPS_Location.class));
        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }
}
