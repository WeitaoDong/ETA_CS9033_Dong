package com.nyu.cs9033.eta.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weitao on 4/4/15.
 */
public class TripDatabaseHelper extends SQLiteOpenHelper{
    private static final String TAG="TripDatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "trips.db";

    public static final String TABLE_TRIP = "trips";
    public static final String COLUMN_TRIP_ID = "_id";
    public static final String COLUMN_TRIP_NAME = "name";
    public static final String COLUMN_FRIENDS = "friends";
    public static final String COLUMN_TRIP_DATE = "date";
    public static final String COLUMN_TRIP_DESTINATION = "destination";

    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_LOC_TRIP_ID = " trip_id";
    private static final String COLUMN_LOC_TIMESTAMP = "timestamp";
    private static final String COLUMN_LOC_LAT = "latitude";
    private static final String COLUMN_LOC_LONG = "longitude";
    private static final String COLUMN_LOC_ALT = "altitude";
    private static final String COLUMN_LOC_PROVIDER = "provider";


    public TripDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        // create trip table
        db.execSQL("create table if not exists " + TABLE_TRIP + "("
                        + COLUMN_TRIP_ID + " integer primary key autoincrement, "
                        + COLUMN_TRIP_NAME+ " varchar(50), "
                        + COLUMN_FRIENDS + " varchar(50), "
                        + COLUMN_TRIP_DESTINATION + " varchar(200), "
                        + COLUMN_TRIP_DATE + " integer)");
        // create location table
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION + " ( "
//                + COLUMN_LOC_TRIP_ID + " int references trip(_id), "
//                + COLUMN_LOC_TIMESTAMP + " integer, "
//                + COLUMN_LOC_LAT + " real, "
//                + COLUMN_LOC_LONG + " real, "
//                + COLUMN_LOC_ALT + " real, "
//                + COLUMN_LOC_PROVIDER + " varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

        //create table again
        onCreate(db);
    }

    public long insertTrip(Trip trip){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRIP_NAME,trip.getName());
        cv.put(COLUMN_FRIENDS,trip.ConvertFriendsToString(trip.getFriends()));
        cv.put(COLUMN_TRIP_DESTINATION, trip.getDestination());
        cv.put(COLUMN_TRIP_DATE,trip.getTime());
        Log.e(TAG+"123",trip.ConvertFriendsToString(trip.getFriends())+trip.getName());
        return getWritableDatabase().insert(TABLE_TRIP, null, cv);
    }

    public ArrayList<Trip> getAllTrip() {
        ArrayList<Trip> tripList = new ArrayList<Trip>();
        String selectQuery = "SELECT * FROM" + TABLE_TRIP;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Trip trip = new Trip();
                trip.setName(cursor.getString(1));
                trip.setFriends(cursor.getString(2));
                trip.setDestination(cursor.getString(3));
                trip.setTime(cursor.getString(4));
                tripList.add(trip);
            }
        }
        return tripList;
    }
    public long insertLocation(long tripId, Location location) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOC_TRIP_ID, tripId);
        cv.put(COLUMN_LOC_TIMESTAMP,location.getTime());
        cv.put(COLUMN_LOC_LAT,location.getLatitude());
        cv.put(COLUMN_LOC_LONG,location.getLongitude());
        cv.put(COLUMN_LOC_ALT,location.getAltitude());
        cv.put(COLUMN_LOC_PROVIDER,location.getProvider());
        // return id of new location
        return getWritableDatabase().insert(TABLE_LOCATION,null,cv);
    }



}
