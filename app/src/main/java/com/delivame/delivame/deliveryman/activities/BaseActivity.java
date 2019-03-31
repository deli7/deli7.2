package com.delivame.delivame.deliveryman.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.common.Messaging.ListSupportMessagesActivity;
import com.delivame.delivame.deliveryman.adapters.ScreenBoxesAdapter;
import com.delivame.delivame.deliveryman.fragments.NoCurrentRecordsFragment;
import com.delivame.delivame.deliveryman.models.ScreenBox;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.BindView;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class BaseActivity extends AppCompatActivity {

   private Snackbar snackbar;

   protected boolean internetConnected = true;
   protected CoordinatorLayout coordinatorLayout;
   private NoCurrentRecordsFragment noCurrentRecordsFragment;

   @BindView(R.id.toolbar)
   protected
   Toolbar toolbar;

   // SETTINGS
   public Settings settings;
   public User currentUser;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
      super.onCreate(savedInstanceState, persistentState);

      coordinatorLayout = findViewById(R.id.coordinatorLayout);
   }

   private void listenToUserCreditChanges() {

      MyUtility.getCurrentUserNodeRef().child(Constants.FIREBASE_KEY_USER_ACCOUNT_POINTS)
            .addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if (dataSnapshot.getValue() == null) {
                     logI(TAG, "ERROR: Account Type changed, but NULL data");
                     return;
                  }
                  Double newAccountPoints = MyUtility.readDoubleValue(dataSnapshot.getValue());
                  if (currentUser != null) {
                     if (newAccountPoints > currentUser.getFareModel().getAccountPoints()) {
                        double diff = newAccountPoints - currentUser.getFareModel().getAccountPoints();
                        currentUser.getFareModel().setAccountPoints(newAccountPoints);
                        Toast.makeText(BaseActivity.this, getString(R.string.ui_message_credit_added)
                              + ": " + String.valueOf(diff) + " " + getString(R.string.points), Toast.LENGTH_SHORT).show();
                     }
                  }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
               }
            });
   }


   protected void initUI(String activityTitle, boolean showBack) {
      if (toolbar != null) {
         setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
         getSupportActionBar().setTitle(activityTitle);
      } else {
         logI(TAG, "activityTitle: " + activityTitle + " - toolbar = null");
      }

      initGoogleAPIClient();

      if (!activityTitle.equals(getString(R.string.driver_verification)) &&
            !activityTitle.equals(getString(R.string.ui_activity_title_select_vehicle)) &&
            !activityTitle.equals(getString(R.string.ui_activity_title_support_messages)) &&
            !activityTitle.equals(getString(R.string.ui_activity_title_send_message_to_support))) {
         listenToUserTypeAndStatus();
      }

      listenToUserCreditChanges();
   }

   private boolean checkGPS() {
      LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      boolean gps_enabled = false;
      boolean network_enabled = false;

      try {
         gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
      } catch (Exception ex) {
         logI(TAG, "ex: " + ex.getMessage());
      }

      try {
         network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
      } catch (Exception ex) {
         logI(TAG, "ex: " + ex.getMessage());
      }

      return gps_enabled || network_enabled;

//            // notify user
//            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
//            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                    // TODO Auto-generated method stub
//                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    context.startActivity(myIntent);
//                    //get gps
//                }
//            });
//            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                    // TODO Auto-generated method stub
//
//                }
//            });
//            dialog.show();
//        }
   }

   private void listenToUserTypeAndStatus() {
      MyUtility.getCurrentUserNodeRef().child(Constants.FIREBASE_KEY_USER_TYPE).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() == null) {
               logI(TAG, "ERROR: Account Type changed, but NULL data");
               return;
            }
            int newType = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));

            logI(TAG, "New Account Type: " + newType);

            if (currentUser != null) {

               if (currentUser.isDeliveryMan()) {
                  if (newType != Constants.USER_TYPE_VERIFIED_DRIVER) {
                     accountNoLongerValid();
                  }
               } else {
                  if (newType != Constants.USER_TYPE_USER) {
                     accountNoLongerValid();
                  }
               }
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
         }
      });

      FirebaseDatabase.getInstance().getReference().addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
         }

         @Override
         public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getKey().equals("Users")) {
               accountNoLongerValid();
            }
         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });
   }

   protected void updateRecyclerView(List<?> list, RecyclerView.Adapter<?> adapter, String text) {

      if (list.size() > 0) {
         removeNoRecordsFragment();
         adapter.notifyDataSetChanged();
      } else {
         loadNoRecordsFragment(text);
      }
   }

   private void accountNoLongerValid() {

      AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(BaseActivity.this,
            R.style.AppCompatAlertDialogStyle)).create();
      alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
      alertDialog.setMessage(getString(R.string.ui_message_error_account_no_longer_valid));
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
            (dialog, which) -> {

//                        MyUtility.signOutCurrentUser();
//                        Intent intent = new Intent(getApplicationContext(), IntroWizardActivity.class);
//                        startActivity(intent);

               ListSupportMessagesActivity.startMe(getApplicationContext());

               dialog.dismiss();
            });
      alertDialog.show();
   }


   private void loadNoRecordsFragment(String text) {

      try {
         noCurrentRecordsFragment = NoCurrentRecordsFragment.newInstance(text);
         FragmentManager fragmentManager = getFragmentManager();
         FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
         fragTransaction.add(R.id.recyclerViewFrame, noCurrentRecordsFragment, "noCurrentRecordsFragment");
         fragTransaction.commit();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private void removeNoRecordsFragment() {

      try {
         if (noCurrentRecordsFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
            fragTransaction.remove(noCurrentRecordsFragment);
            fragTransaction.commit();
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }


   @Override
   protected void onResume() {
      super.onResume();
      registerInternetCheckReceiver();
      registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_PROVIDER_CHANGED));

      if (!checkGPS()) {
         showSettingDialog();
      }

   }

   @Override
   protected void onPause() {
      super.onPause();

      unregisterReceiver(gpsLocationReceiver);
   }

   /**
    * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
    */
   private void registerInternetCheckReceiver() {
      IntentFilter internetFilter = new IntentFilter();
      internetFilter.addAction("android.net.wifi.STATE_CHANGE");
      internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

   }

   void setSnackBarMessage(String status, boolean showBar) {

      String internetStatus = "";
      if (!status.equalsIgnoreCase(getString(R.string.internet_connected_wifi))
            && !status.equalsIgnoreCase(getString(R.string.internet_connected_mobile))) {
         snackbar = Snackbar
               .make(coordinatorLayout, internetStatus, Snackbar.LENGTH_LONG)
               .setAction("X", view -> snackbar.dismiss());
         // Changing message text color
         snackbar.setActionTextColor(Color.WHITE);
         // Changing action button text color
         View sbView = snackbar.getView();
         TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
         textView.setTextColor(Color.WHITE);

         if (!internetConnected) {
            internetConnected = true;
            snackbar.show();
         }
      }
   }


   protected void showInternetStatus() {
      if (!internetConnected) {
         snackbar = Snackbar
               .make(coordinatorLayout, getString(R.string.ui_message_error_no_internet_connectivity), Snackbar.LENGTH_INDEFINITE)
               .setAction("X", view -> snackbar.dismiss());
         // Changing message text color
         snackbar.setActionTextColor(Color.RED);
         // Changing action button text color
         View sbView = snackbar.getView();
         TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
         textView.setTextColor(Color.WHITE);
         snackbar.show();
      } else {
         snackbar = Snackbar
               .make(coordinatorLayout, getString(R.string.ui_message_error_internet_connectivity_back), Snackbar.LENGTH_INDEFINITE)
               .setAction("X", view -> snackbar.dismiss());
         // Changing message text color
         snackbar.setActionTextColor(Color.GREEN);
         // Changing action button text color
         View sbView = snackbar.getView();
         TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
         textView.setTextColor(Color.WHITE);
         snackbar.show();
      }
   }

   protected ScreenBoxesAdapter initGridRecyclerView(RecyclerView recyclerView, List<ScreenBox> list) {

      ScreenBoxesAdapter adapter = new ScreenBoxesAdapter(this, list);

      RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
      recyclerView.setLayoutManager(mLayoutManager);
      //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
      recyclerView.setItemAnimator(new DefaultItemAnimator());
      recyclerView.setAdapter(adapter);
      return adapter;
   }


   protected void showMessage(String message) {
      Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_main, menu);
      LocaleHelper.updateMenuLanguage(getApplicationContext(), menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();

      switch (id) {
         case R.id.action_change_language:
//            if (LanguageHelper.isLanguageEnglish(getApplicationContext())) {
//               LanguageHelper.setLocaleArabic(getApplicationContext());
//            } else {
//               LanguageHelper.setLocaleEnglish(getApplicationContext());
//            }
//
//            IntroWizardActivity.startMe(getApplicationContext());
//            finish();

            if (LocaleHelper.isLanguageEnglish(getApplicationContext())) {
               LocaleHelper.setLocale(this, "ar");
            } else {
               LocaleHelper.setLocale(this, "en_us");
            }

            IntroWizardActivity.startMe(getApplicationContext());
            finish();
            break;

         case R.id.action_logout:

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
               switch (which) {
                  case DialogInterface.BUTTON_POSITIVE:
                     FirebaseAuth.getInstance().signOut();
                     IntroWizardActivity.startMe(getApplicationContext());
                     finish();
                     break;

                  case DialogInterface.BUTTON_NEGATIVE:
                     //No button clicked
                     break;
               }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
                  R.style.AppCompatAlertDialogStyle));
            builder.setMessage(getString(R.string.ui_dialog_are_you_sure_exit))
                  .setPositiveButton(getString(R.string.yes), dialogClickListener)
                  .setNegativeButton(getString(R.string.no), dialogClickListener)
                  .show();
            break;

         case android.R.id.home:
            finish();
            break;
      }

      return super.onOptionsItemSelected(item);
   }

//    public boolean checkPermissions() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            requestPermissions();
//            return false;
//        }
//    }
//
//    public void requestPermissions() {
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                MY_PERMISSIONS_ACCESS_FINE_LOCATION);
//    }

   private static final int REQUEST_CHECK_SETTINGS = 0x1;
   private static final String BROADCAST_PROVIDER_CHANGED = "android.location.PROVIDERS_CHANGED";

   /* Initiate Google API Client  */
   private void initGoogleAPIClient() {
      //Without Google API Client Auto Location Dialog will not work
      GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(BaseActivity.this)
            .addApi(LocationServices.API)
            .build();
      mGoogleApiClient.connect();
   }

   private void showSettingDialog() {
      LocationRequest locationRequest = LocationRequest.create();
      locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      locationRequest.setInterval(30 * 1000);
      locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);
      builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

      Task<LocationSettingsResponse> result =
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
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
                           BaseActivity.this,
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


   @Nullable
   private final BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

      @Override
      public void onReceive(@NonNull Context context, @NonNull Intent intent) {

         //If Action is Location
         if (intent.getAction().matches(BROADCAST_PROVIDER_CHANGED)) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            //Check if GPS is turned ON or OFF
            assert locationManager != null;
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
               Log.e("About GPS", "GPS is Enabled in your device");
               //    updateGPSStatus("GPS is Enabled in your device");
            } else {
               //If GPS turned OFF show Location Dialog
               new Handler().postDelayed(() -> showSettingDialog(), 10);
               // showSettingDialog();
               //  updateGPSStatus("GPS is Disabled in your device");
               Log.e("About GPS", "GPS is Disabled in your device");
            }

         } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo =
                  intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
               internetConnected = true;
               // Wifi is connected
               showInternetStatus();
            }
         } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo =
                  intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                  !networkInfo.isConnected()) {
               internetConnected = false;
               // Wifi is disconnected
               showInternetStatus();
            }
         }
      }
   };

   @Override
   protected void attachBaseContext(Context base) {
      if (LocaleHelper.isLanguageSet(base))
         super.attachBaseContext(LocaleHelper.onAttach(base));
      else
         super.attachBaseContext(base);
   }
}
