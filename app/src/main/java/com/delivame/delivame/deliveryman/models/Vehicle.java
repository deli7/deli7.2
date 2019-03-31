package com.delivame.delivame.deliveryman.models;

import android.support.annotation.NonNull;
import android.text.TextUtils;

public class Vehicle {
    final String vehicleModelName;
    final String vehicleModelYear;
    final String vehicleNumber;
    private final String icon;


    public Vehicle(String vehicleModelName, String vehicleModelYear, String vehicleNumber, String icon) {
        this.vehicleModelName = vehicleModelName;
        this.vehicleModelYear = vehicleModelYear;
        this.vehicleNumber = vehicleNumber;
        this.icon = icon;
    }



    public String getVehicleModelName() {
        return vehicleModelName;
    }

    public String getVehicleModelYear() {
        return vehicleModelYear;
    }

    @NonNull
    public String getVehicleString(){
        if (!TextUtils.isEmpty(vehicleNumber)) {
            return vehicleModelName + " " + vehicleModelYear + " - " + vehicleNumber;
        }else{
            return vehicleModelName + " " + vehicleModelYear;
        }
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getIcon() {
        return icon;
    }


}
