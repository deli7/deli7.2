package com.delivame.delivame.deliveryman.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;

import com.crashlytics.android.Crashlytics;
import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;
import com.delivame.delivame.deliveryman.utilities.NotificationsHelper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainApplication extends Application {

   @Override
   public void onCreate() {
      super.onCreate();

      FirebaseApp.initializeApp(this);
      FirebaseDatabase.getInstance().setPersistenceEnabled(false);

      NotificationsHelper.createNotificationChannel(this);

      // Setup handler for uncaught exceptions.
      Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
   }

   public void handleUncaughtException(Thread thread, Throwable e) {
      e.printStackTrace();

      Crashlytics.log(0, DateTimeUtil.getCurrentDateTime(), Arrays.toString(e.getStackTrace()));
   }

   public void statusCheck() {
      final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

      if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
         buildAlertMessageNoGps();
      }
   }

   private void buildAlertMessageNoGps() {
      final AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes),
                  (dialog, id) -> startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
            .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel());
      final AlertDialog alert = builder.create();
      alert.show();
   }

   @Override
   protected void attachBaseContext(Context base) {
      if (LocaleHelper.isLanguageSet(base))
         super.attachBaseContext(LocaleHelper.onAttach(base));
      else
         super.attachBaseContext(base);
   }
}
