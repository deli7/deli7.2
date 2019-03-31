package com.delivame.delivame.deliveryman.models;

import com.delivame.delivame.deliveryman.utilities.Constants;
import com.google.firebase.database.DataSnapshot;


public class MyPlace{

    private String title;
    private String address;
    private Double lat;
    private Double lng;

    public MyPlace() {
    }

    public MyPlace(String title, String address, Double lat, Double lng) {
        this.title = title;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public MyPlace(DataSnapshot dataSnapshot) {
        title = dataSnapshot.getKey();
        address = (String) dataSnapshot.child(Constants.MY_PLACE_ADDRESS).getValue();
        lat = (Double) dataSnapshot.child(Constants.MY_PLACE_LAT).getValue();
        lng = (Double) dataSnapshot.child(Constants.MY_PLACE_LNG).getValue();
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getLatLng() {
        return lat + "," + lng;
    }
}
