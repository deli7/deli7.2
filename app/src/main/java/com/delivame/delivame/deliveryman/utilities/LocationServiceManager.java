package com.delivame.delivame.deliveryman.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import static com.delivame.delivame.deliveryman.utilities.Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION;
import static com.delivame.delivame.deliveryman.utilities.Constants.REQUEST_CHECK_SETTINGS;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationServiceManager {

   private LocationCallback mLocationCallback;
   private LocationRequest mLocationRequest;
   private boolean mRequestingLocationUpdates = false;
   private Location mCurrentLocation;
   private final LocationServiceManagerListener listener;

   private final Activity context;

   public LocationServiceManager(Activity context) {
      this.context = context;
      listener = (LocationServiceManagerListener) context;
   }

   public void initLocationService() {
      Log.i("AppInfo", "initLocationService");

      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest);

      SettingsClient client = LocationServices.getSettingsClient(context);
      Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

      task.addOnSuccessListener(context, locationSettingsResponse -> startLocationUpdates());

      task.addOnFailureListener(context, e -> {
         if (e instanceof ResolvableApiException) {
            // Location settings are not satisfied, but this can be fixed
            // by showing the user a dialog.
            try {
               // Show the dialog by calling startResolutionForResult(),
               // and check the result in onActivityResult().
               ResolvableApiException resolvable = (ResolvableApiException) e;
               resolvable.startResolutionForResult(context,
                     REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
               // Ignore the error.
            }
         }
      });
   }


   // Trigger new location updates at interval
   private void startLocationUpdates() {

      logI(TAG, "startLocationUpdates");

      // Create the location request to start receiving updates
      mLocationRequest = new LocationRequest();
      mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      long UPDATE_INTERVAL = 10 * 1000;
      mLocationRequest.setInterval(UPDATE_INTERVAL);
      long FASTEST_INTERVAL = 3000;
      mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
      builder.addLocationRequest(mLocationRequest);
      LocationSettingsRequest locationSettingsRequest = builder.build();

      SettingsClient settingsClient = LocationServices.getSettingsClient(context);
      settingsClient.checkLocationSettings(locationSettingsRequest);

      // new Google API SDK v11 uses getFusedLocationProviderClient(this)
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

         checkPermissions();
         return;
      }

      //logI(TAG, "requestLocationUpdates");

      mLocationCallback = new LocationCallback() {
         @Override
         public void onLocationResult(LocationResult locationResult) {
            //logI(TAG, "onLocationResult");
            // do work here
            //onLocationChanged(locationResult.getLastLocation());
            if (mCurrentLocation == null) {
               getLastLocation();
            }
         }

      };

      FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
      mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());


   }


   public void onLocationChanged(Location location) {
      // New location has now been determined
      logI(TAG, "Location Update = " + location);
      //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
      // You can now create a LatLng Object for use with maps
      //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
      mCurrentLocation = location;
      listener.processLocation(mCurrentLocation);
   }

   private void getLastLocation() {
      //logI(TAG3, "getLastLocation");
      // Get last known recent location using new Google Play Services SDK (v11+)
      FusedLocationProviderClient locationClient = getFusedLocationProviderClient(context);

      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
         // TODO: Consider calling
         //    ActivityCompat#requestPermissions
         // here to request the missing permissions, and then overriding
         //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
         //                                          int[] grantResults)
         // to handle the case where the user grants the permission. See the documentation
         // for ActivityCompat#requestPermissions for more orderNumber.
         checkPermissions();
         return;
      }
      locationClient.getLastLocation()
            .addOnSuccessListener(location -> {
               // GPS location can be null if GPS is switched off
               if (location != null) {


                  //logI(TAG3, "Location Changed: " + location);

                  listener.processLocation(location);

               }
            })
            .addOnFailureListener(e -> {
               Log.d("MapDemoActivity", "Error trying to get last GPS location");
               e.printStackTrace();
            });
   }

   private boolean checkPermissions() {
      if (ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
         return true;
      } else {
         requestPermissions();
         return false;
      }
   }

   private void requestPermissions() {
      ActivityCompat.requestPermissions(context,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_ACCESS_FINE_LOCATION);
   }

   public void stopLocationUpdates() {
      logI(TAG, "stopLocationUpdates");
      try {
         if (mLocationCallback != null) {
            logI(TAG, "Stopping location updates");
            FusedLocationProviderClient locationClient = getFusedLocationProviderClient(context);
            locationClient.removeLocationUpdates(mLocationCallback);
         }
      } catch (NullPointerException ex) {
         logI(TAG, "stopLocationUpdates ERROR: " + ex.getMessage());
         ex.printStackTrace();
      }
   }

   public interface LocationServiceManagerListener {
      void processLocation(Location location);
   }
}
