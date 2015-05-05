package com.nyu.cs9033.eta.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.nyu.cs9033.eta.R;

public class TestLocationService extends Service implements LocationListener {
    private static final String TAG = "TestLocationService";
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_FROM_SERVICE_UPLOAD_LOCATION = 3;
    public static final int MSG_FROM_SERVICE_TRIP_STATUS = 4;
    public static final int MSG_FROM_ACTIVITY = 5;
    private long trip_id ;
    private NotificationManager nm;
    Messenger mClient = null;
    private static boolean ALIVE = false;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    Timer timer = new Timer();
    Timer timer2 = new Timer();
    final String url = "http://cs9033-homework.appspot.com";
    double latitude = 0;
    double longitude = 0;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    int cnt = 0;
    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2;// 1000 * 60 *
    // 2; // 1
    // minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public TestLocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started...");
        ALIVE = true;

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        onTimerTick();
        timer.schedule(new TimerTask() {
            public void run() {
                onTimerTick();
            }
        }, 0, MIN_TIME_BW_UPDATES);
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ALIVE = true;
        showNotification();

        Log.i(TAG, "Service Started.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
        }
        ALIVE = false;
        nm.cancel(R.string.service_started);
        stopUsingGPS();
        Log.i(TAG, "Service Stopped.");
    }

    public static boolean isRunning() {
        return ALIVE;
    }

    private  JSONObject getUploadLocJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("command", "UPDATE_LOCATION");
            jsonObject.accumulate("latitude", latitude);
            jsonObject.accumulate("longitude", longitude);
            jsonObject.accumulate("datetime", new Date().getTime());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "upload location failed");
        }
        return jsonObject;
    }

    private JSONObject getTripStatusJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("command", "TRIP_STATUS");
            jsonObject.accumulate("trip_id", trip_id);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "upload location failed");
        }
        return jsonObject;
    }

    private void timerruntask() throws JSONException {
        String currentDateandTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        getLocation();
//        JSONObject tmp = getUploadLocJson();
//        Log.e(TAG+"Load", tmp.toString());
        String result = POST(this.url, getUploadLocJson());
//        Log.e(TAG,result);
        JSONObject json = new JSONObject(result);
        int status = json.getInt("response_code");
        if (status == 0) {
            String loc = "Latitude: "
                    + String.valueOf(getLatitude())
                    + "\nLongitude: "
                    + String.valueOf(getLongitude() + "\nUpdate Times:\t"
                    + String.valueOf(cnt++) + "\nUpdate Datetime: \t"
                    + currentDateandTime + "\nCurrent trip ID is:\t"
                    + trip_id);
            Message msg = Message
                    .obtain(null, MSG_FROM_SERVICE_UPLOAD_LOCATION);
            Bundle b = new Bundle();
            b.putString("current_location", loc);
            msg.setData(b);
            try {
                mClient.send(msg);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        result = POST(url, getTripStatusJson());
        if (result != null) {
            Message msg = Message.obtain(null, MSG_FROM_SERVICE_TRIP_STATUS);
            Bundle b = new Bundle();
            b.putString("trip_status", result);
            msg.setData(b);
            try {
                mClient.send(msg);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void onTimerTick() {
        Log.i("TimerTick", "Timer doing work.");
        try {
            if (isConnected()) {
                timerruntask();
            } else {
            }
        } catch (Throwable t) {
            Log.e("TimerTick", "Timer Tick Failed.", t);
        }
    }

    class IncomingHandler extends Handler { // Handler of incoming messages from
        // clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClient = msg.replyTo;
                    trip_id = msg.getData().getLong("trip_id");
                    onTimerTick();
                    timer2.schedule(new TimerTask() {
                        public void run() {
                            onTimerTick();
                        }
                    }, 0);

                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClient = null;
                    break;
                case MSG_FROM_ACTIVITY:
                    trip_id = msg.getData().getLong("trip_id");
                    onTimerTick();
                    timer2.schedule(new TimerTask() {
                        public void run() {
                            onTimerTick();
                        }
                    }, 0);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    // This method is used to show your service status on the top corner of
    // screen
    private void showNotification() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // In this sample, we'll use the same text for the ticker and the
        // expanded notification
        CharSequence text = getText(R.string.service_started);
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher,
                text, System.currentTimeMillis());
        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.service_label),
                text, contentIntent);
        // Send the notification.
        // We use a layout id because it is a unique number. We use it later to
        // cancel.
        nm.notify(R.string.service_started, notification);
    }

    /********** location part ******************/
    public Location getLocation() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(TestLocationService.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /*********** This part is used for sending and receiving message to server *******/
    // This method is used to check the status of the network info

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
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

    // This method is used to post command and data to server, and receive
    public String POST(String url, JSONObject jsonObject) {
        InputStream inputStream;
        String result = "";
        String json;
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            // 3. build jsonObject
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
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
            if (e.getLocalizedMessage()!=null)
                Log.d(TAG, e.getLocalizedMessage());
        }
        // 11. return result
        return result;
    }

}
