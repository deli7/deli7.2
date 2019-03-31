package com.delivame.delivame.deliveryman.models;

import android.support.annotation.NonNull;

import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;

import java.util.ArrayList;
import java.util.List;


public class VehicleCategory {
   final String id;
   final double baseFare;
   final double farePerMin;
   final double farePerKm;
   final String category;
   private final String icon;
   private final List<Vehicle> vehicles;
   private final List<User> drivers = new ArrayList<>();
   private double distance = 0;
   private double time = 0;

   public void setDistance(double distance) {
      this.distance = distance;
   }

   public void setTime(double time) {
      this.time = time;
   }

   public VehicleCategory(String id, String category, double baseFare, double farePerMin,
                          double farePerKm, List<Vehicle> vehicles, String icon) {
      this.id = id;
      this.icon = icon;
      this.category = category;
      this.baseFare = baseFare;
      this.farePerMin = farePerMin;
      this.farePerKm = farePerKm;
      this.vehicles = vehicles;
   }

   public double getBaseFare() {
      return baseFare;
   }

   public double getFarePerMin() {
      return farePerMin;
   }

   public double getFarePerKm() {
      return farePerKm;
   }

   public List<Vehicle> getVehicles() {
      return vehicles;
   }

   public String getCategory() {
      return category;
   }

   @NonNull
   public String getCostString(double destinationDistance, double destinationTime, double taxPercent, String currency) {

      MyUtility.logI(Constants.TAG, "getCostString: destinationDistance: " + destinationDistance);
      MyUtility.logI(Constants.TAG, "getCostString: destinationTime: " + destinationTime);

      MyUtility.logI(Constants.TAG, "getCostString: Catgory: " + getCategory());
      MyUtility.logI(Constants.TAG, "getCostString: baseFare: " + baseFare);
      MyUtility.logI(Constants.TAG, "getCostString: farePerMin: " + farePerMin);
      MyUtility.logI(Constants.TAG, "getCostString: farePerKm: " + farePerKm);

      MyUtility.logI(Constants.TAG, "getCostString: taxPercent: " + taxPercent);

      double cost = (baseFare + destinationTime * farePerMin + destinationDistance * farePerKm) * (1 + taxPercent / 100);

      MyUtility.logI(Constants.TAG, "getCostString: cost: " + cost);

      return "Class: " + category + " ~ :" + currency + MyUtility.roundDouble2(cost);
   }

   public double getCostString(double taxPercent) {
      MyUtility.logI(Constants.TAG, "getCostString2: distance: " + distance);
      MyUtility.logI(Constants.TAG, "getCostString2: time: " + time);

      MyUtility.logI(Constants.TAG, "getCostString2: Catgory: " + getCategory());
      MyUtility.logI(Constants.TAG, "getCostString2: baseFare: " + baseFare);
      MyUtility.logI(Constants.TAG, "getCostString2: farePerMin: " + farePerMin);
      MyUtility.logI(Constants.TAG, "getCostString2: farePerKm: " + farePerKm);

      MyUtility.logI(Constants.TAG, "getCostString2: taxPercent: " + taxPercent);

      double cost = (baseFare + time * farePerMin + distance * farePerKm) * (1 + taxPercent / 100);
      MyUtility.logI(Constants.TAG, "getCostString2: cost: " + cost);
      return cost;
   }

   public List<User> getDrivers() {
      return drivers;
   }

   public String getId() {
      return id;
   }

   public String getIcon() {
      return icon;
   }

   public void addDriver(User user) {
      drivers.add(user);
   }
}
