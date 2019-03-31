package com.delivame.delivame.deliveryman;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

import static com.delivame.delivame.deliveryman.utilities.Constants.REQUEST_CHECK_SETTINGS;

public class GpsLocationReceiver extends BroadcastReceiver {

   Context context;

   @Override
   public void onReceive(Context context, Intent intent) {
      this.context = context;
      if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
//            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
//                    Toast.LENGTH_SHORT).show();
//            Intent pushIntent = new Intent(context, LocalService.class);
//            context.startService(pushIntent);

         showSettingDialog();
      }
   }

   private void showSettingDialog() {
      LocationRequest locationRequest = LocationRequest.create();
      locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
      locationRequest.setInterval(30 * 1000);
      locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);
      builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

      Task<LocationSettingsResponse> result =
            LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());
      result.addOnCompleteListener(task -> {

         try {
            LocationSettingsResponse response = task.getResult(ApiException.class);
            // All location settings are satisfied. The client can initialize location
            // requests here.

         } catch (ApiException exception) {
            switch (exception.getStatusCode()) {
               case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                  // Location settings are not satisfied. But could be fixed by showing the
                  // user a dialog.
                  try {
                     // Cast to a resolvable exception.
                     ResolvableApiException resolvable = (ResolvableApiException) exception;
                     // Show the dialog by calling startResolutionForResult(),
                     // and check the result in onActivityResult().
                     resolvable.startResolutionForResult(
                           (Activity) context,
                           REQUEST_CHECK_SETTINGS);
                  } catch (IntentSender.SendIntentException e) {
                     // Ignore the error.
                  } catch (ClassCastException e) {
                     // Ignore, should be an impossible error.
                  }
                  break;

               case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                  // Location settings are not satisfied. However, we have no way to fix the
                  // settings so we won't show the dialog.
                  break;
            }
         }
      });
   }
}
