package com.delivame.delivame.deliveryman.models;

import android.location.Location;

public class LatLng {

   public Double latitude;
   public Double longitude;

   public LatLng() {
   }

   public LatLng(double lat, double lng) {
      latitude = lat;
      longitude = lng;
   }

   public Double getLatitude() {
      return latitude;
   }

   public void setLatitude(Double latitude) {
      this.latitude = latitude;
   }

   public Double getLongitude() {
      return longitude;
   }

   public void setLongitude(Double longitude) {
      this.longitude = longitude;
   }

   public com.google.android.gms.maps.model.LatLng toGmsLatLng() {
      return
            new com.google.android.gms.maps.model.LatLng(latitude,
                  longitude);
   }

   public static LatLng fromLocation(Location mCurrentLocation) {
      return new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
   }
}