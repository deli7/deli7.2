package com.delivame.delivame.deliveryman.models;

import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;


public class TripPoint {
    private String timeStamp;
    private double bearing;
    private double latitude;
    private double longitude;


    public TripPoint(double lat, double lng, float bearing){
        latitude = lat;
        longitude = lng;
        this.bearing = bearing;
        timeStamp = DateTimeUtil.getCurrentDateTime();
    }


    public TripPoint(DataSnapshot ds){
        if (ds != null && ds.getValue() != null){
            timeStamp = ds.getKey();

            fromHashMap((HashMap<String, Object>) ds.getValue());
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public double getBearing() {
        return bearing;
    }

    public HashMap<String, Object> toHashMap(){
        HashMap<String, Object> map = new HashMap<>();

        map.put(Constants.TRIP_POINT_LATITUDE, latitude);
        map.put(Constants.TRIP_POINT_LONGITUDE, longitude);
        map.put(Constants.TRIP_POINT_BEARING, bearing);

        return map;
    }

    private void fromHashMap(HashMap<String, Object> map){
        if (map == null) return;

        latitude = (double) map.get(Constants.TRIP_POINT_LATITUDE);
        longitude = (double) map.get(Constants.TRIP_POINT_LONGITUDE);
        bearing = ((Long) map.get(Constants.TRIP_POINT_BEARING)).doubleValue();

    }
}
