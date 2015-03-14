package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Trip implements Parcelable {
	
	// Member fields should exist here, what else do you need for a trip?
	// Please add additional fields
    private UUID mId;
	private String name;
    private List<Person> friends;
    private String start;
    private String destination;
    private Date time;
    private static final String JSON_ID = "id";
    private static final String JSON_DESTINATION = "destination";
    private static final String JSON_PEOPLE = "friends";
    private static final String JSON_DATE = "date";


	public String getName(){
        return name;
    }
    public void setName(String name){this.name = name;}
    public String getStart(){
        return start;
    }
    public void setStart(String start){this.start = start;}
    public List<Person> getFriends(){
        return friends;
    }
    public void setFriends(ArrayList<Person> friends){this.friends = friends;}
    public String getDestination(){
        return destination;
    }
    public void setDestination(String destination){this.destination = destination;}
    public Date getTime(){
        return time;
    }
    public void setTime(Date time){this.time = time;}
	/**
	 * Parcelable creator. Do not modify this function.
	 */
	public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
		public Trip createFromParcel(Parcel p) {
			return new Trip(p);
		}

		public Trip[] newArray(int size) {
			return new Trip[size];
		}
	};
	
	/**
	 * Create a Trip model object from a Parcel. This
	 * function is called via the Parcelable creator.
	 * 
	 * @param p The Parcel used to populate the
	 * Model fields.
	 */
	public Trip(Parcel p) {
        name = p.readString();
        friends = new ArrayList<Person>();
        p.readList(friends, null);
        start = p.readString();
        destination = p.readString();
        time = (Date) p.readSerializable();
		// TODO - fill in here
	}
	
	/**
	 * Create a Trip model object from arguments
	 * 
	 * @param name  Add arbitrary number of arguments to
	 * instantiate Trip class based on member variables.
	 */


	public Trip(String name,List<Person> friends,String start, String destination,String start_time,Date time) {
        this.name = name;
        this.friends = friends;
        this.start = start;
        this.destination = destination;
        this.time = time;
		// TODO - fill in here, please note you must have more arguments here
	}

	/**
	 * Serialize Trip object by using writeToParcel. 
	 * This function is automatically called by the
	 * system when the object is serialized.
	 * 
	 * @param dest Parcel object that gets written on 
	 * serialization. Use functions to write out the
	 * object stored via your member variables. 
	 * 
	 * @param flags Additional flags about how the object 
	 * should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
	 * In our case, you should be just passing 0.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[]{this.name,
                this.start,
                this.destination,});
        dest.writeList(this.friends);
        dest.writeSerializable(this.time);
		// TODO - fill in here 
	}
	
	/**
	 * Feel free to add additional functions as necessary below.
	 */

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID,mId.toString());
        json.put(JSON_DESTINATION,destination);
        json.put(JSON_PEOPLE,new JSONArray(friends));
        json.put(JSON_DATE,time.getTime());
        return json;
    }

	public Trip(){
        friends = new ArrayList<Person>();
        name = null;
        start = null;
        destination = null;
        time = new Date();
    }
	/**
	 * Do not implement
	 */
	@Override
	public int describeContents() {
		// Do not implement!
		return 0;
	}
}
