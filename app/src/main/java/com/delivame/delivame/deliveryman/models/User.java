package com.delivame.delivame.deliveryman.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.old_activities.CustomInfoWindowAdapter;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class User {

   private static final String TAG = "AppInfo";

   private double credit;

   private int userNumber = -1;
   private final DatabaseReference userRef;
   private double distanceToUser = 0.0;
   private String photoURL = "";
   private String fullName = "";
   private String emailAddress = "";
   private String phoneNumber = "";
   private String UID;
   private int userType = 0;
   private GeoLocation lastLocation;
   private Marker marker = null;
   private GeoFire geoFire;
   private boolean available = true;
   private LatLng destinationPoint = null;
   private LatLng pickUpPoint = null;
   private String destinationTitle = "";
   private double destinationTime = 0;
   private double destinationDistance = 0;
   private double destinationCost = 0;
   private double rank = 0.0;
   private FareModel fareModel;
   private VehicleCategory vehicleCategory;
   private Vehicle vehicle;
   private Settings settings;
   private float bearing = 0;
   private Marker pickUpMarker;
   private String pickUpTitle = "";
   private Polyline route;
   private double lowRankCount = 0;

   private List<PaymentHistoryRecord> paymentHistoryRecords = new ArrayList<>();

   private boolean distanceCalcWay2 = false;

   public User(@NonNull DataSnapshot dataSnapshot, DatabaseReference ref, String currentUserUID, Settings settings) {
      DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference("GoFire");
      geoFire = new GeoFire(geoFireRef);
      userRef = ref;
      UID = currentUserUID;
      if (settings != null) {
         this.settings = settings;
         this.vehicleCategories = settings.vehicleCategories;
         double accountPoints = MyUtility.getFirbaseDouble(dataSnapshot.child(Constants.FIREBASE_KEY_USER_ACCOUNT_POINTS));
         fareModel = new FareModel(vehicleCategory, settings.getCompany_percent(), accountPoints);
      }

      fromHashMap(dataSnapshot);
   }

   public User(@Nullable String fullName,
               @Nullable String emailAddress,
               @Nullable String phoneNumber,
               int userType,
               DatabaseReference ref,
               VehicleCategory vehicleCategory,
               @Nullable Vehicle vehicle,
               String uid,
               int users_count,
               double account_points,
               double company_percent) {
      this.userType = userType;
      userRef = ref;

      UID = uid;
      this.fullName = fullName;
      this.emailAddress = emailAddress;
      this.phoneNumber = phoneNumber;
      this.userNumber = users_count;
      this.vehicle = vehicle;
      this.vehicleCategory = vehicleCategory;
      fareModel = new FareModel(vehicleCategory, company_percent, account_points);
   }

   public User(@Nullable String fullName,
               String emailAddress,
               @Nullable String phoneNumber,
               int userType,
               DatabaseReference ref,
               String uid) {
      this.userType = userType;
      this.fullName = fullName;
      this.phoneNumber = phoneNumber;
      UID = uid;
      userRef = ref;
   }

   @Nullable
   private Marker getMarker() {
      return marker;
   }

   private void setMarker(@Nullable Marker marker) {
      this.marker = marker;
   }

   public String getUID() {
      return UID;
   }

   public void setUID(String uId) {
      this.UID = uId;
   }

   private GeoLocation getLastLocation() {
      return lastLocation;
   }

   public double getCredit() {
      return credit;
   }

   public void setCredit(double credit) {
      this.credit = credit;
   }

   public LatLng getLastLocationLatLng() {
      return new LatLng(lastLocation.latitude, lastLocation.longitude);
   }

   private void setLastLocation(GeoLocation lastLocation) {
      this.lastLocation = lastLocation;
   }

   private List<VehicleCategory> vehicleCategories;

   private void fromHashMap(@NonNull DataSnapshot dataSnapshot) {

      if (dataSnapshot.getValue() == null) {
         return;
      }

      try {
         UID = dataSnapshot.getKey();
         userType = Integer.parseInt(String.valueOf(dataSnapshot.child(Constants.FIREBASE_KEY_USER_TYPE).getValue()));

         if (dataSnapshot.child(Constants.FIREBASE_KEY_USER_NUMBER).getValue() != null) {
            userNumber = Integer.parseInt(String.valueOf(dataSnapshot.child(Constants.FIREBASE_KEY_USER_NUMBER).getValue()));
         } else {
            MyUtility.getCountersNodeRef().addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  userNumber = MyUtility.readUsersCount(dataSnapshot);

                  userRef.child(Constants.FIREBASE_KEY_USER_NUMBER).setValue(String.valueOf(userNumber));
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                  MyUtility.logI(Constants.TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
               }
            });
         }

         fullName = (String) dataSnapshot.child(Constants.FIREBASE_KEY_USER_FULLNAME).getValue();
         phoneNumber = (String) dataSnapshot.child(Constants.FIREBASE_KEY_USER_PHONE_NUMBER).getValue();
         emailAddress = (String) dataSnapshot.child(Constants.FIREBASE_KEY_USER_EMAIL).getValue();
         photoURL = (String) dataSnapshot.child(Constants.FIREBASE_KEY_USER_PHOTO_URL).getValue();
         String vehicleNumber = (String) dataSnapshot.child(Constants.FIREBASE_KEY_USER_VEHICLE_NUMBER).getValue();
         String vehicleModel = (String) dataSnapshot.child(Constants.FIREBASE_KEY_USER_VEHICLE_MODEL).getValue();
         String vehicleYear = (String) dataSnapshot.child(Constants.FIREBASE_KEY_USER_VEHICLE_YEAR).getValue();
         String icon = settings.findVehicleIcon(vehicleModel, vehicleYear);

         vehicle = new Vehicle(vehicleModel, vehicleYear, vehicleNumber, icon);
         double accountPoints = MyUtility.getFirbaseDouble(dataSnapshot.child(Constants.FIREBASE_KEY_USER_ACCOUNT_POINTS));
         double companyPercent = MyUtility.getFirbaseDouble(dataSnapshot.child(Constants.FIREBASE_KEY_USER_COMPANY_PERCENT));

         String category_id = (String) dataSnapshot.child(Constants.FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_ID).getValue();

         if (!TextUtils.isEmpty(category_id)) {
            for (int i = 0; i < vehicleCategories.size(); i++) {
               if (vehicleCategories.get(i).getId().equals(category_id)) {
                  vehicleCategory = vehicleCategories.get(i);
                  break;
               }
            }
            if (vehicleCategory != null) {
               fareModel = new FareModel(vehicleCategory, companyPercent, accountPoints);
            }
         }


//            MyUtility.logI(Constants.TAG, "Rank: " + dataSnapshot.child(Constants.FIREBASE_KEY_USER_RANK).getValue());
//            MyUtility.logI(Constants.TAG, "UID: " + UID);

         rank = MyUtility.getFirbaseDouble(dataSnapshot.child(Constants.FIREBASE_KEY_USER_RANK));
         if (dataSnapshot.child("low_rank_count").getValue() != null) {
            lowRankCount = MyUtility.getFirbaseDouble(dataSnapshot.child("low_rank_count"));
         } else {
            lowRankCount = 0;
         }

         if (dataSnapshot.child(Constants.FIREBASE_KEY_USER_LOGGED_IN).getValue() != null) {
            userLoggedIn = (Boolean) dataSnapshot.child(Constants.FIREBASE_KEY_USER_LOGGED_IN).getValue();
         } else {
            userLoggedIn = false;
         }

         if (dataSnapshot.child(Constants.FIREBASE_KEY_USER_AVAILABLE).getValue() != null) {
            available = (Boolean) dataSnapshot.child(Constants.FIREBASE_KEY_USER_AVAILABLE).getValue();
         } else {
            available = true;
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }


   }

   public FareModel getFareModel() {
      return fareModel;
   }

   @NonNull
   private HashMap<String, Object> toHashMap() {
      HashMap<String, Object> map = new HashMap<>();
      map.put(Constants.FIREBASE_KEY_USER_TYPE, userType);
      map.put(Constants.FIREBASE_KEY_USER_NUMBER, userNumber);
      map.put(Constants.FIREBASE_KEY_USER_FULLNAME, fullName);
      map.put(Constants.FIREBASE_KEY_USER_EMAIL, emailAddress);
      map.put(Constants.FIREBASE_KEY_USER_PHONE_NUMBER, phoneNumber);
      map.put(Constants.FIREBASE_KEY_USER_RANK, rank);
      map.put(Constants.FIREBASE_KEY_USER_PHOTO_URL, String.valueOf(photoURL));
      map.put(Constants.FIREBASE_KEY_USER_ACCOUNT_POINTS, getFareModel().accountPoints);
      map.put(Constants.FIREBASE_KEY_USER_COMPANY_PERCENT, getFareModel().companyPercent);
      map.put("low_rank_count", lowRankCount);

      if (vehicle != null) {
         map.put(Constants.FIREBASE_KEY_USER_VEHICLE_NUMBER, String.valueOf(vehicle.vehicleNumber));
         map.put(Constants.FIREBASE_KEY_USER_VEHICLE_MODEL, String.valueOf(vehicle.vehicleModelName));
         map.put(Constants.FIREBASE_KEY_USER_VEHICLE_YEAR, String.valueOf(vehicle.vehicleModelYear));
      }
      if (vehicleCategory != null) {
         map.put(Constants.FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_ID, vehicleCategory.getId());
      }
      map.put(Constants.FIREBASE_KEY_USER_AVAILABLE, available);
      map.put(Constants.FIREBASE_KEY_USER_LOGGED_IN, userLoggedIn);


      return map;
   }

   private boolean userLoggedIn = false;


   public void saveUser() {
      userRef.setValue(toHashMap());
   }


   @Nullable
   public String getFullName() {
      return fullName;
   }

   @Nullable
   public String getPhoneNumber() {
      return phoneNumber;
      //return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

   }

   public boolean isDeliveryMan() {
      return userType == Constants.USER_TYPE_VERIFIED_DRIVER;
   }

   @NonNull
   public String getPhotoURL() {
      return photoURL;
   }

   public double getDistanceToUser() {
      return distanceToUser;
   }

   public void setDistanceToUser(double distanceToUser) {
      this.distanceToUser = distanceToUser;
   }

   private LatLng toLatLng() {
      return new LatLng(lastLocation.latitude, lastLocation.longitude);
   }

   private void logLocation() {
      if (getLastLocation() == null) return;

      if (isDeliveryMan()) {
         MyUtility.logI(Constants.TAG, "Driver: " + getFullName()
               + " - Location: (" + getLastLocation().latitude + "," + getLastLocation().longitude + ")");
      } else {
         MyUtility.logI(Constants.TAG, "User: " + getFullName()
               + " - Location: (" + getLastLocation().latitude + "," + getLastLocation().longitude + ")");
      }
   }

   public void saveUserLocation(@NonNull final Location lastLocation) {
      setLastLocation(new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));

      geoFire.setLocation(getUID(),
            new GeoLocation(lastLocation.getLatitude()
                  , lastLocation.getLongitude()), (key, error) -> {
               if (error == null) {
                  geoFire.getDatabaseReference().child(getUID()).child("heading").setValue(lastLocation.getBearing());
               }
            });
   }

   public void singleUserMode(@NonNull GoogleMap map) {
      MyUtility.logI(Constants.TAG, "singleUserMode");
      if (getLastLocation() == null) {
         return;
      }

      map.clear();

      goToLocation(map, getLastLocation().latitude, getLastLocation().longitude, 14);
   }

   public void singleUserMode(@NonNull GoogleMap map, boolean driver) {
      MyUtility.logI(Constants.TAG, "singleUserMode");
      if (getLastLocation() == null) {
         return;
      }

      map.clear();

      goToLocation(map, getLastLocation().latitude, getLastLocation().longitude, 14, driver);
   }

   public void singleUserMode(Context context, @NonNull GoogleMap map) {
      if (getLastLocation() == null) {
         return;
      }

      map.clear();

      goToCurrentLocation(context, map);
   }

   @NonNull
   private String getDestinationString() {
      String s = "";
      s += "Distance: " + destinationDistance + " Km\nETA: " + destinationTime + " min\n";
      s += "Estimate Cost: " + settings.getCurrencyEn() + destinationCost;

      return s;
   }

   public void twoUsersMode(@NonNull GoogleMap map, @Nullable User otherUser) {

      if (otherUser == null) {
         MyUtility.logI(Constants.TAG, "ERROR: Invalid Other User");
         return;
      }

      map.clear();

      if (otherUser.getLastLocation() == null) {
         MyUtility.logI(Constants.TAG, "ERROR: Invalid Other User Location");
         return;
      }

      try {
         logLocation();
         otherUser.logLocation();

         List<Marker> pathMarkers = new ArrayList<>();
         LatLngBounds.Builder builder = new LatLngBounds.Builder();

         // Add the new marker at the new location
         Marker m1 = refreshUserMarker(map);
         Marker m2 = otherUser.refreshUserMarker(map);
         pathMarkers.add(m1);
         pathMarkers.add(m2);

         if (destinationPoint != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(destinationPoint);
            markerOptions.snippet(getDestinationString());
            markerOptions.title(getDestinationTitle());
            Marker m3 = map.addMarker(markerOptions);
            m3.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
            m3.showInfoWindow();
            pathMarkers.add(m3);
         }

         for (Marker marker : pathMarkers) {
            builder.include(marker.getPosition());
         }
         LatLngBounds bounds = builder.build();

         int padding = 100; // offset from edges of the map in pixels
         CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
         map.animateCamera(cu, Constants.MAP_SPEED, null);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private void showDestinationPoint(Context context, @NonNull GoogleMap map) {
      MyUtility.logI(Constants.TAG, "showDestinationPoint");

      if (isDeliveryMan()) {
         MyUtility.logI(Constants.TAG, "showDestinationPoint");

         map.clear();

         //Set Custom InfoWindow Adapter
         CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter((Activity) context);
         map.setInfoWindowAdapter(adapter);

         List<Marker> pathMarkers = new ArrayList<>();
         LatLngBounds.Builder builder = new LatLngBounds.Builder();

         // Add the new marker at the new location
         Marker m1 = refreshUserMarker(map);

         pathMarkers.add(m1);

         if (destinationPoint != null) {
            if (destinationMarker != null) {
               destinationMarker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(destinationPoint);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
            markerOptions.title(getDestinationTitle());
            destinationMarker = map.addMarker(markerOptions);
            destinationMarker.showInfoWindow();
            pathMarkers.add(destinationMarker);
         }

         for (Marker marker : pathMarkers) {
            builder.include(marker.getPosition());
         }
         LatLngBounds bounds = builder.build();

         int padding = Constants.MAP_PADDING; // offset from edges of the map in pixels
         CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
         map.animateCamera(cu, Constants.MAP_SPEED, null);
      } else {
         //Set Custom InfoWindow Adapter
         CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter((Activity) context);
         map.setInfoWindowAdapter(adapter);

         if (destinationPoint != null) {
            MyUtility.logI(Constants.TAG, "destinationPoint != null");
            if (destinationMarker != null) {
               MyUtility.logI(Constants.TAG, "destinationMarker != null");
               destinationMarker.remove();
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(destinationPoint);
            MyUtility.logI(Constants.TAG, "showDestinationPoint: s:");
            markerOptions.snippet("Destination");
            markerOptions.title(getDestinationTitle());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
            destinationMarker = map.addMarker(markerOptions);
            destinationMarker.showInfoWindow();
            goToLocation(map, destinationPoint.latitude, destinationPoint.longitude, map.getCameraPosition().zoom);
         } else {
            MyUtility.logI(Constants.TAG, "destinationPoint == null");
         }
      }


   }

   public void showDestinationPoint(Context context, @NonNull GoogleMap map,
                                    @Nullable LatLng point, String title, String snippet) {
      map.clear();

      //Set Custom InfoWindow Adapter
      CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter((Activity) context);
      map.setInfoWindowAdapter(adapter);

      List<Marker> pathMarkers = new ArrayList<>();
      LatLngBounds.Builder builder = new LatLngBounds.Builder();

      // Add the new marker at the new location
      Marker m1 = refreshUserMarker(map);

      pathMarkers.add(m1);

      if (point != null) {
         MarkerOptions markerOptions = new MarkerOptions();
         markerOptions.position(point);
         markerOptions.snippet(snippet);
         markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
         markerOptions.title(title);
         Marker m3 = map.addMarker(markerOptions);
         m3.showInfoWindow();
         pathMarkers.add(m3);
      }

      for (Marker marker : pathMarkers) {
         builder.include(marker.getPosition());
      }
      LatLngBounds bounds = builder.build();

      int padding = Constants.MAP_PADDING; // offset from edges of the map in pixels
      CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
      map.animateCamera(cu, Constants.MAP_SPEED, null);
   }

   private void showPickUpAndDestinationPoints(Context context,
                                               @NonNull GoogleMap map,
                                               VehicleCategory vehicleCategory, boolean isRoundTrip) {

      MyUtility.logI(Constants.TAG, "showPickUpAndDestinationPoints");
      map.clear();

      List<Marker> pathMarkers = new ArrayList<>();
      LatLngBounds.Builder builder = new LatLngBounds.Builder();

      if (pickUpPoint != null) {
         MarkerOptions markerOptions = new MarkerOptions();
         markerOptions.position(pickUpPoint);
         markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
         markerOptions.title("Pickup from");
         pickUpMarker = map.addMarker(markerOptions);
         pickUpMarker.showInfoWindow();
         pathMarkers.add(pickUpMarker);
      } else {
         MyUtility.logI(Constants.TAG, "showPickUpAndDestinationPoints: pickUpPoint = null");
      }

      if (destinationPoint != null) {
         MarkerOptions markerOptions = new MarkerOptions();
         markerOptions.position(destinationPoint);
         markerOptions.snippet(getDestinationString(context, vehicleCategory));

         markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
         if (isRoundTrip) {
            markerOptions.title("Drop at (Round Trip)");
         } else {
            markerOptions.title("Drop at");
         }
         destinationMarker = map.addMarker(markerOptions);
         destinationMarker.showInfoWindow();
         pathMarkers.add(destinationMarker);
      } else {
         MyUtility.logI(Constants.TAG, "showPickUpAndDestinationPoints: destinationPoint = null");
      }

      for (Marker marker : pathMarkers) {
         builder.include(marker.getPosition());
      }

      if (pathMarkers.size() > 0) {
         LatLngBounds bounds = builder.build();

         int padding = Constants.MAP_PADDING; // offset from edges of the map in pixels

         try {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            map.animateCamera(cu, Constants.MAP_SPEED, null);

            map.animateCamera(CameraUpdateFactory.zoomTo(Constants.DEFAULT_ZOOM_OUT_LEVEL),
                  Constants.MAP_SPEED, null);
         } catch (Exception ex) {
            try {
               CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
               map.animateCamera(cu, Constants.MAP_SPEED, null);
               map.animateCamera(CameraUpdateFactory.zoomTo(Constants.DEFAULT_ZOOM_OUT_LEVEL),
                     Constants.MAP_SPEED, null);
            } catch (Exception ex1) {
               MyUtility.logI(Constants.TAG, "Error: " + ex1.getMessage());
            }

         }
      }
   }

   private void goToLocation(@NonNull GoogleMap map, double lat, double lng, float zoom) {
      LatLng latLng = new LatLng(lat, lng);
      CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
      map.animateCamera(update, Constants.MAP_SPEED, null);

      refreshUserMarker(map);
   }

   private void goToLocation(@NonNull GoogleMap map, double lat, double lng, float zoom, Boolean driver) {
      LatLng latLng = new LatLng(lat, lng);
      CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
      map.animateCamera(update, Constants.MAP_SPEED, null);

      refreshUserMarker(map, driver);
   }

   private void goToCurrentLocation(Context context, @NonNull GoogleMap map) {
      CameraUpdate update = CameraUpdateFactory.newLatLngZoom(getLastLocationLatLng(), map.getCameraPosition().zoom);
      map.animateCamera(update, Constants.MAP_SPEED, null);

      refreshUserMarker(context, map);
   }


   private Marker destinationMarker = null;

   public void addDestinationMarker(Context context, @NonNull GoogleMap map, LatLng point) {

      destinationPoint = point;

      if (destinationMarker != null) {
         destinationMarker.remove();
      }
      CameraUpdate update = CameraUpdateFactory.newLatLngZoom(point, map.getCameraPosition().zoom);
      map.animateCamera(update, Constants.MAP_SPEED, null);

      MarkerOptions options;
      int res;

      res = R.mipmap.ic_location;

      String address = MyUtility.getAddress(context, point.latitude, point.longitude);

      MyUtility.logI(Constants.TAG, "address:" + address);

      options = new MarkerOptions()
            .title("Destination")
            .snippet(address)
            .icon(BitmapDescriptorFactory.fromResource(res))
            .position(point);

      destinationMarker = map.addMarker(options);

      //setMarker(marker);
      destinationMarker.showInfoWindow();
   }

   private Marker refreshUserMarker(@NonNull GoogleMap map) {

      if (getLastLocation() == null) {
         MyUtility.logI(Constants.TAG, "getLastLocation() == null");
         return null;
      }
      if (getMarker() != null) {
         MyUtility.logI(Constants.TAG, "getMarker() == null");
         getMarker().remove();
      }

      MarkerOptions options;
      int res;
      if (isDeliveryMan()) {
         res = R.mipmap.ic_car4;
      } else {
         res = R.mipmap.ic_location;
      }

      options = new MarkerOptions()
            .icon(BitmapDescriptorFactory.fromResource(res))
            .rotation(bearing)
            .position(new LatLng(getLastLocation().latitude, getLastLocation().longitude));

      Marker marker = map.addMarker(options);

      setMarker(marker);

      return marker;
   }

   private Marker refreshUserMarker(@NonNull GoogleMap map, boolean driver) {

      if (getLastLocation() == null) {
         MyUtility.logI(Constants.TAG, "getLastLocation() == null");
         return null;
      }
      if (getMarker() != null) {
         MyUtility.logI(Constants.TAG, "getMarker() == null");
         getMarker().remove();
      }

      MarkerOptions options;
      int res;
      if (driver) {
         res = R.mipmap.ic_car4;
      } else {
         res = R.mipmap.ic_location;
      }

      options = new MarkerOptions()
            .icon(BitmapDescriptorFactory.fromResource(res))
            .rotation(bearing)
            .position(new LatLng(getLastLocation().latitude, getLastLocation().longitude));

      Marker marker = map.addMarker(options);

      setMarker(marker);

      return marker;
   }


   private Marker refreshUserMarker(Context context, @NonNull GoogleMap map) {
      if (getMarker() != null) {
         getMarker().remove();
      }

      MarkerOptions options;
      int res;
      if (isDeliveryMan()) {
         res = R.mipmap.ic_car4;
      } else {
         res = R.mipmap.ic_location;
      }
//        options = new MarkerOptions();
      String address = MyUtility.getAddress(context, getLastLocation().latitude, getLastLocation().longitude);

      MyUtility.logI(Constants.TAG, "address:" + address);

//        options.snippet(address);
//        options.title(getFullName());

      options = new MarkerOptions()
            .title("Pickup from: ")
            .snippet(address)
            .icon(BitmapDescriptorFactory.fromResource(res))
            .position(new LatLng(getLastLocation().latitude, getLastLocation().longitude));

      Marker marker = map.addMarker(options);

      setMarker(marker);
      marker.showInfoWindow();

      return marker;
   }


   public DatabaseReference getUserRef() {
      return userRef;
   }

   public int getUserType() {
      return userType;
   }

   @Nullable
   public LatLng getDestinationPoint() {
      return destinationPoint;
   }

   public void setDestinationPoint(@Nullable LatLng destinationPoint, String s) {
      this.destinationTitle = s;
      this.destinationPoint = destinationPoint;
   }

   public VehicleCategory getVehicleCategory() {
      return vehicleCategory;
   }

   @Nullable
   public Vehicle getVehicle() {
      return vehicle;
   }

   public void setPhotoUrl(String photoUrl) {
      this.photoURL = photoUrl;
   }

   public LatLng getPickUpPoint() {
      return pickUpPoint;
   }

   public void setPickUpPoint(LatLng pickUpPoint, String address) {


      this.pickUpTitle = address;
      this.pickUpPoint = pickUpPoint;
   }

   public void setBearing(float bearing) {
      this.bearing = bearing;
   }


   @NonNull
   public String getVehicleInformation() {
      return vehicleCategory.category + " - " + (vehicle != null ? vehicle.getVehicleModelName() : null) + "," +
            (vehicle != null ? vehicle.getVehicleModelYear() : "") + " - #" + vehicle.vehicleNumber;
   }

   public Marker getPickUpMarker() {
      return pickUpMarker;
   }


//    public void getTripDistanceAndETA(Context context, GoogleMap map, VehicleCategory vehicleCategory, boolean isRoundTrip, VehicleCategoriesFragment vehicleCategoriesFragment) {
//
//        if (getPickUpPoint() == null) {
//            MyUtility.logI(Constants.TAG, "getPickUpPoint() == null ");
//            return;
//        }
//
//        if (getDestinationPoint() == null) {
//            MyUtility.logI(Constants.TAG, "getDestinationPoint() == null ");
//            return;
//        }
//
//        MyUtility.logI(Constants.TAG, "API: USER getDistacnceUrl");
//
//        // Getting URL to the Google Directions API
//        String url1 = MyUtility.getDistacnceUrl(context, getPickUpPoint(), getDestinationPoint(), isRoundTrip);
//
//        MyUtility.logI(Constants.TAG, "url1: " + url1);
//
//
//        TripRouteCalculationTask drawMapRouteTask = new TripRouteCalculationTask(context, map, vehicleCategory, isRoundTrip, vehicleCategoriesFragment);
//
//        // Start downloading json data from Google Directions API
//        drawMapRouteTask.execute(url1);
//    }

   public String getPickUpTitle() {
      return pickUpTitle;
   }

   public void addOrderToCompletedOrders(Order currentOrder) {
      getUserRef().child(Constants.FIREBASE_KEY_COMPLETED_ORDERS)
            .child(currentOrder.getOrderUserRequestTime()).setValue(currentOrder.toHashMap());
   }

   public void addOrderToCancelledOrders(Order currentOrder) {
      getUserRef().child(Constants.FIREBASE_KEY_CANCELLED_ORDERS)
            .child(currentOrder.getOrderUserRequestTime()).setValue(currentOrder.toHashMap());
   }

   public void updateRemainingPoints(double remainingPoints) {
      getUserRef().child(Constants.FIREBASE_KEY_USER_ACCOUNT_POINTS).setValue(remainingPoints);
   }

   public void updateBalance(Order currentOrder) {
      String date = currentOrder.getOrderUserRequestTime();
      getUserRef().child(Constants.FIREBASE_KEY_BALANCE).child(date).child(Constants.FIREBASE_KEY_TRANSACTION_TYPE).setValue("DEBIT");
      getUserRef().child(Constants.FIREBASE_KEY_BALANCE).child(date).child(Constants.FIREBASE_KEY_TRANSACTION_VALUE).setValue(currentOrder.getCompanyEarning());
      getUserRef().child(Constants.FIREBASE_KEY_BALANCE).child(date).child(Constants.FIREBASE_KEY_TRANSACTION_DESCRIPTION).setValue("Order #" + currentOrder.getOrderNumber());
   }

   public void updateCredit(double offerValue) {
      double currentCredit = getFareModel().getAccountPoints();
      getFareModel().setAccountPoints(MyUtility.roundDouble2(currentCredit - offerValue));
      saveUser();

   }


   // Fetches data from url passed
   private class TripRouteCalculationTask extends AsyncTask<String, Void, String> {

      final GoogleMap map;
      final Context context;
      final VehicleCategory vehicleCategory;
      final boolean isRoundTrip;

      TripRouteCalculationTask(Context context, GoogleMap map,
                               VehicleCategory vehicleCategory, boolean isRoundTrip, Object vehicleCategoriesFragment) {
         this.map = map;
         this.context = context;
         this.vehicleCategory = vehicleCategory;
         this.isRoundTrip = isRoundTrip;
         //this.vehicleCategoriesFragment = vehicleCategoriesFragment;
      }

      @Override
      protected void onPreExecute() {
         super.onPreExecute();
         int pathLength = 0;
      }

      @NonNull
      @Override
      protected String doInBackground(String... url) {

         // For storing data from web service
         String data;

         try {

            // Fetching the data from web service
            data = MyUtility.downloadUrl(url[0]);

            Log.d(TAG, "Background Task data: " + data);
         } catch (Exception e) {
            Log.d("Background Task", e.toString());
            return "";

         }
         return data;
      }

      @Override
      protected void onPostExecute(String result) {
         super.onPostExecute(result);

         MyUtility.logI(Constants.TAG, "Response: " + result);


         MyUtility.logI(Constants.TAG, "result is not empty: " + result);

         JSONObject jsonObject;
         try {
            jsonObject = new JSONObject(result);

            //MyUtility.logI(Constants.TAG, "jsonObject: " + jsonObject);

            JSONArray rows;

            rows = jsonObject.getJSONArray("rows");

            if (rows.length() > 0) {

               MyUtility.logI(Constants.TAG, "rows: " + rows);

               JSONObject elements = (JSONObject) rows.get(0);

               MyUtility.logI(Constants.TAG, "elements: " + elements.toString());

               JSONObject distance = elements.getJSONArray("elements").getJSONObject(0).getJSONObject("distance");

               MyUtility.logI("Distance", distance.toString());

               JSONObject duration = elements.getJSONArray("elements").getJSONObject(0).getJSONObject("duration");

               MyUtility.logI(Constants.TAG, "duration: " + duration.toString());

               String regex = "[^.0123456789]";
               //destinationDistance = MyUtility.roundDouble2(Double.parseDouble(distance.getString("text").replaceAll(regex, "")));
               destinationDistance = MyUtility.roundDouble2(Double.parseDouble(distance.getString("value")) / 1000);


               destinationTime = MyUtility.roundDouble2(Double.parseDouble(duration.getString("text").replaceAll(regex, "")));

               if (isRoundTrip) {
                  JSONObject elements1 = (JSONObject) rows.get(1);

                  MyUtility.logI(Constants.TAG, "elements: " + elements.toString());

                  JSONObject distance1 = elements.getJSONArray("elements").getJSONObject(0).getJSONObject("distance");

                  MyUtility.logI("Distance", distance1.toString());

                  JSONObject duration1 = elements.getJSONArray("elements").getJSONObject(0).getJSONObject("duration");

                  MyUtility.logI(Constants.TAG, "duration: " + duration1.toString());

                  destinationDistance += MyUtility.roundDouble2(Double.parseDouble(distance1.getString("text").replaceAll(regex, "")));
                  destinationTime = MyUtility.roundDouble2(Double.parseDouble(duration1.getString("text").replaceAll(regex, "")));

               }
            } else {
               distanceCalcWay2 = true;

               destinationDistance = MyUtility.getLengthBetweenLatLngs(getPickUpPoint(), getDestinationPoint());
               destinationTime = MyUtility.roundDouble2((destinationDistance / 40) * 60);

               if (isRoundTrip) {
                  destinationTitle += "(Round Trip)";
                  destinationDistance = destinationDistance * 2;
                  destinationTime = destinationTime * 2;
               }
            }

            //MyUtility.writeDestinationData(context, destinationDistance, destinationTime, destinationPoint, destinationCost, destinationPoint);

            destinationTitle = MyUtility.getAddress(context, getDestinationPoint().latitude, getDestinationPoint().longitude);

            MyUtility.logI(Constants.TAG, "destinationDistance: " + destinationDistance);
            MyUtility.logI(Constants.TAG, "destinationTime: " + destinationTime);

            showPickUpAndDestinationPoints(context, map, vehicleCategory, isRoundTrip);
            drawRoute(map);
         } catch (JSONException e) {
            e.printStackTrace();
         }
      }
   }

   private void drawRoute(GoogleMap map) {
      String url = MyUtility.getDirectionsUrl(getPickUpPoint(), getDestinationPoint());

      DrawMapRouteTask drawMapRouteTask = new DrawMapRouteTask(map);

      drawMapRouteTask.execute(url);
   }

   public void drawRoute(GoogleMap map, LatLng origin, LatLng destination) {
      String url = MyUtility.getDirectionsUrl(origin, destination);
      DrawMapRouteTask drawMapRouteTask = new DrawMapRouteTask(map);
      drawMapRouteTask.execute(url);
   }

   public void removeRoute() {
      if (route != null) {
         route.remove();
      }
   }


   private String getDestinationString(Context context, VehicleCategory vehicleCategory) {
      if (TextUtils.isEmpty(destinationTitle)) {
         destinationTitle = MyUtility.getAddress(context, getDestinationPoint().latitude, getDestinationPoint().longitude);
      }

      StringBuilder s = new StringBuilder(destinationTitle + "\n" +
            "Est. Distance: " + destinationDistance + " KM\n" +
            "Est. Time: " + destinationTime + " min\n");

      if (vehicleCategory != null) {
         s.append(vehicleCategory.getCostString(destinationDistance, destinationTime,
               settings.getTaxPercent(), settings.getCurrencyEn()));
      }


      return s.toString();
   }

   public void showPoint(Context context, GoogleMap map) {

      if (TextUtils.isEmpty(destinationTitle)) {
         destinationTitle = MyUtility.getAddress(context, getDestinationPoint().latitude, getDestinationPoint().longitude);
      }

      showDestinationPoint(context, map);
   }


   public double getDestinationCost() {
      return destinationCost;
   }

   public void setDestinationCost(double destinationCost) {
      this.destinationCost = destinationCost;
   }

   public double getDestinationTime() {
      return destinationTime;
   }

   public void setDestinationTime(double destinationTime) {
      this.destinationTime = destinationTime;
   }

   public double getDestinationDistance() {
      return destinationDistance;
   }

   public void setDestinationDistance(double destinationDistance) {
      this.destinationDistance = destinationDistance;
   }

   public String getDestinationTitle() {
      return destinationTitle;
   }


   public void setDestinationTitle(String destinationTitle) {
      this.destinationTitle = destinationTitle;
   }


   public double getRank() {
      return rank;
   }


   public boolean isAvailable() {
      return available;
   }

   public void setAvailable(boolean b) {
      available = b;

   }

   public int getUserNumber() {
      return userNumber;
   }

   class DrawMapRouteTask extends AsyncTask<String, Void, String> {

      final GoogleMap map;
      private int pathLength = 0;

      DrawMapRouteTask(GoogleMap map) {
         this.map = map;
      }

      @Override
      protected void onPreExecute() {
         super.onPreExecute();
         pathLength = 0;
      }

      @NonNull
      @Override
      protected String doInBackground(String... url) {

         // For storing data from web service
         String data = "";

         try {
            // Fetching the data from web service
            data = MyUtility.downloadUrl(url[0]);
            Log.d("Background Task data", data);
         } catch (Exception e) {
            Log.d("Background Task", e.toString());
         }
         return data;
      }

      @Override
      protected void onPostExecute(String result) {
         super.onPostExecute(result);

         MapRouteDrawParserTask parserTask = new DrawMapRouteTask.MapRouteDrawParserTask(map);

         // Invokes the thread for parsing the JSON data
         parserTask.execute(result);

      }

      /**
       * A class to parse the Google Places in JSON format
       */
      private class MapRouteDrawParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

         private final boolean calculateDistance = false;
         private final boolean drawPath = false;
         TextView textViewResult;
         @Nullable
         private final GoogleMap map;
         @NonNull
         String resultString = "";

         MapRouteDrawParserTask(@Nullable GoogleMap map) {
            this.map = map;
         }

         // Parsing the data in non-ui thread
         @Nullable
         @Override
         protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
               jObject = new JSONObject(jsonData[0]);
               DataParser parser = new DataParser();

               // Starts parsing data
               routes = parser.parse(jObject);

            } catch (Exception e) {
               Log.d("MapRouteDrawParserTask", e.toString());
               e.printStackTrace();
            }
            return routes;
         }

         // Executes in UI thread, after the parsing process
         @Override
         protected void onPostExecute(@NonNull List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
               points = new ArrayList<>();
               //lineOptions = new PolylineOptions();

               // Fetching i-th route
               List<HashMap<String, String>> path = result.get(i);

               MyUtility.logI("AppInfo", "path.size():" + path.size());

               int count = path.size();

               // Fetching all the points in i-th route
               for (int j = 0; j < count; j++) {
                  HashMap<String, String> point = path.get(j);

                  double lat = Double.parseDouble(point.get("lat"));
                  double lng = Double.parseDouble(point.get("lng"));
                  LatLng position = new LatLng(lat, lng);

                  points.add(position);
               }

               //driverInformationFragment.setDistance("Distance: " + MyUtility.roundDouble2(pathLength / 1000) + " Km");
               lineOptions = new PolylineOptions();
               // Adding all the points in the route to LineOptions
               lineOptions.addAll(points);
               lineOptions.width(7);
               lineOptions.color(Color.BLUE);

               Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }

            // Drawing route in the Google Map for the i-th route
            if (lineOptions != null) {
               route = map.addPolyline(lineOptions);

            } else {
               Log.d("onPostExecute", "without Polylines drawn");
            }
         }
      }
   }

   public static String getTAG() {
      return TAG;
   }

   public void setUserNumber(int userNumber) {
      this.userNumber = userNumber;
   }

   public void setPhotoURL(String photoURL) {
      this.photoURL = photoURL;
   }

   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   public String getEmailAddress() {
      return emailAddress;
   }

   public void setEmailAddress(String emailAddress) {
      this.emailAddress = emailAddress;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public void setUserType(int userType) {
      this.userType = userType;
   }

   public GeoFire getGeoFire() {
      return geoFire;
   }

   public void setGeoFire(GeoFire geoFire) {
      this.geoFire = geoFire;
   }

   public void setDestinationPoint(LatLng destinationPoint) {
      this.destinationPoint = destinationPoint;
   }

   public void setPickUpPoint(LatLng pickUpPoint) {
      this.pickUpPoint = pickUpPoint;
   }

   public void setRank(double rank) {
      this.rank = rank;
   }

   public void setFareModel(FareModel fareModel) {
      this.fareModel = fareModel;
   }

   public void setVehicleCategory(VehicleCategory vehicleCategory) {
      this.vehicleCategory = vehicleCategory;
   }

   public void setVehicle(Vehicle vehicle) {
      this.vehicle = vehicle;
   }

   public Settings getSettings() {
      return settings;
   }

   public void setSettings(Settings settings) {
      this.settings = settings;
   }

   public float getBearing() {
      return bearing;
   }

   public void setPickUpMarker(Marker pickUpMarker) {
      this.pickUpMarker = pickUpMarker;
   }

   public void setPickUpTitle(String pickUpTitle) {
      this.pickUpTitle = pickUpTitle;
   }

   public Polyline getRoute() {
      return route;
   }

   public void setRoute(Polyline route) {
      this.route = route;
   }

   public List<PaymentHistoryRecord> getPaymentHistoryRecords() {
      return paymentHistoryRecords;
   }

   public void setPaymentHistoryRecords(List<PaymentHistoryRecord> paymentHistoryRecords) {
      this.paymentHistoryRecords = paymentHistoryRecords;
   }

   public boolean isDistanceCalcWay2() {
      return distanceCalcWay2;
   }

   public void setDistanceCalcWay2(boolean distanceCalcWay2) {
      this.distanceCalcWay2 = distanceCalcWay2;
   }

   public List<VehicleCategory> getVehicleCategories() {
      return vehicleCategories;
   }

   public void setVehicleCategories(List<VehicleCategory> vehicleCategories) {
      this.vehicleCategories = vehicleCategories;
   }

   public boolean isUserLoggedIn() {
      return userLoggedIn;
   }

   public void setUserLoggedIn(boolean userLoggedIn) {
      this.userLoggedIn = userLoggedIn;
   }

   public Marker getDestinationMarker() {
      return destinationMarker;
   }

   public void setDestinationMarker(Marker destinationMarker) {
      this.destinationMarker = destinationMarker;
   }

   public double getLowRankCount() {
      return lowRankCount;
   }

   public void setLowRankCount(double lowRankCount) {
      this.lowRankCount = lowRankCount;
   }
}
