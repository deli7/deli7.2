package com.delivame.delivame.deliveryman.models;


import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Store {

   private String placeName;
   private String vicinity;
   private LatLng latLng;
   private String photoUrl;
   private String placeId;
   private double rating;
   private double distance = 0;
   private String phoneNumber = "";
   private String internationalphoneNumber = "";
   private String icon;
   private double time;

   private String opennow;

   private List<String> types = new ArrayList<>();

   public String getOpennow() {
      if (opennow == null)
         return "false";
      return opennow;
   }

   public String getInternationalphoneNumber() {
      if (internationalphoneNumber == null)
         return "";
      return internationalphoneNumber;
   }

   public void setInternationalphoneNumber(String internationalphoneNumber) {
      this.internationalphoneNumber = internationalphoneNumber;
   }

   public void setOpennow(String opennow) {
      this.opennow = opennow;
   }

   public Store() {
   }

   public double getTime() {
      return time;
   }

   public void setTime(double time) {
      this.time = time;
   }

   public String getDistanceString() {
      return String.format("%.2f", distance);
   }

   public Double getDistance() {
      return distance;
   }

   public void setDistance(Double distance) {
      this.distance = distance;
   }

   public String getPhotoUrl() {
      return photoUrl;
   }

   public void setPhotoUrl(String photoUrl) {
      this.photoUrl = photoUrl;
   }

   public String getPlaceName() {

      placeName = placeName.replace("#", "")
            .replace("$", "")
            .replace("[", "")
            .replace(".", "")
            .replace("]", "");

      return placeName;
   }

   public void setPlaceName(String placeName) {
      this.placeName = placeName;
   }

   public String getVicinity() {
      return vicinity;
   }

   public void setVicinity(String vicinity) {
      this.vicinity = vicinity;
   }

   public LatLng getLatLng() {
      return latLng;
   }

   public void setLatLng(LatLng latLng) {
      this.latLng = latLng;
   }

   public String getPlaceId() {
      return placeId;
   }

   public void setPlaceId(String placeId) {
      this.placeId = placeId;
   }

   public double getRating() {
      return rating;
   }

   public void setRating(double rating) {
      this.rating = rating;
   }

   public String getPhoneNumber() {
      if (phoneNumber == null)
         return "";
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public String getIcon() {
      return icon;
   }

   public void setIcon(String icon) {
      this.icon = icon;
   }

   public List<String> getTypes() {
      return types;
   }

   public void setTypes(List<String> types) {
      this.types = types;
   }

   public boolean isRestaurant() {
      for (String type : types) {
         if (type.equals("restaurant"))
            return true;
      }
      return false;
   }

}
