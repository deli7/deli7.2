package com.delivame.delivame.deliveryman.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.ProgressBar;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.client.ClientHomeActivity;
import com.delivame.delivame.deliveryman.activities.common.Auth.DriverVerificationActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.DeliveryManHomeActivity;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class SplashActivity extends BaseActivity {

   @BindView(R.id.pbBar)
   ProgressBar pbBar;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_splash);
      ButterKnife.bind(this);

      init();
   }

   void init() {
      SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());

      if (prefs.getBoolean(Constants.PREF_SETTING_FIRST_TIME, true)) {
         SharedPreferences preferences = getDefaultSharedPreferences(getApplicationContext());
         SharedPreferences.Editor editor = preferences.edit();
         editor.putBoolean(Constants.PREF_SETTING_FIRST_TIME, false);
         editor.apply();
         editor.commit();

         IntroWizardActivity.startMe(getApplicationContext());
      } else {
         if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            new CountDownTimer(2000, 1000) {
               public void onTick(long millisUntilFinished) {
                  logI("AppInfo", "[TIMER] TIME TICK");
               }

               public void onFinish() {
                  whenTimerFinishes();
               }
            }.start();
         }
      }
   }

   private void whenTimerFinishes() {
      MyUtility.getGlobalSettingsNodeRef()
            .addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  settings.readBasicSettings(dataSnapshot, getApplicationContext());

                  MyUtility.getGlobalSettingsNodeRef()
                        .child(Constants.FIREBASE_KEY_ALLOWED_VEHICLES)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                              if (dataSnapshot.getValue() == null) {
                                 return;
                              }

                              settings.readVehicleCategories(dataSnapshot);

                              //showProgress();
                              MyUtility.getCurrentUserNodeRef()
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                          if (dataSnapshot.getValue() != null) {

                                             User user =
                                                   new User(dataSnapshot, MyUtility.getCurrentUserNodeRef(),
                                                         MyUtility.getCurrentUserUID(), settings);
                                             handleUserType(user);
                                          } else {
                                             // Toast.makeText(LoginActivity.this, R.string.error_invalid_registration, Toast.LENGTH_SHORT).show();
                                             //ref.child(Constants.FIREBASE_KEY_USER_LOGGED_IN).setValue(false);
                                             FirebaseAuth.getInstance().signOut();
                                             //  startLogin2();
                                          }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {
                                          logI(TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
                                       }
                                    });
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {
                              logI(TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
                           }
                        });
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                  logI(TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
               }
            });
   }

   private void handleUserType(@NonNull User user) {

      AlertDialog alertDialog = new AlertDialog.Builder(
            new ContextThemeWrapper(SplashActivity.this, R.style.AppCompatAlertDialogStyle)).create();

      switch (user.getUserType()) {
         case Constants.USER_TYPE_UNVERIFIED_DRIVER:

            showMessage(getString(R.string.error_user_type_unverified_driver));
            DriverVerificationActivity.startMe(getApplicationContext());
            finish();
            break;

         case Constants.USER_TYPE_PENDING_VERIFICATION_DRIVER:

            if (MyUtility.getCurrentUser() != null) {
               FirebaseAuth.getInstance().signOut();
            }

            showMessage(getString(R.string.error_user_type_pending_verification_driver));

            alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
            alertDialog.setMessage(getString(R.string.error_user_type_pending_verification_driver));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                  (dialog, which) -> {
                     MyUtility.toHomeScreen(getApplicationContext());
                     finish();
                  });
            alertDialog.show();

            break;

         case Constants.USER_TYPE_DRIVER_SUSPENDED:

            if (MyUtility.getCurrentUser() != null) {
               FirebaseAuth.getInstance().signOut();
            }

            alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
            alertDialog.setMessage(getString(R.string.ui_message_error_user_type_driver_suspended));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                  (dialog, which) -> {
                     MyUtility.toHomeScreen(getApplicationContext());
                     finish();
                  });
            alertDialog.show();
            break;

         case Constants.USER_TYPE_USER_SUSPENDED:

            if (MyUtility.getCurrentUser() != null) {
               MyUtility.signOutCurrentUser();
            }

            alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
            alertDialog.setMessage(getString(R.string.ui_message_error_user_type_user_suspended));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                  (dialog, which) -> {
                     MyUtility.toHomeScreen(getApplicationContext());
                     finish();
                  });
            alertDialog.show();
            break;

         case Constants.USER_TYPE_VERIFIED_DRIVER:
            DeliveryManHomeActivity.startMe(getApplicationContext());
            finish();
            break;

         case Constants.USER_TYPE_USER:
            ClientHomeActivity.startMe(getApplicationContext());
            finish();
            break;

         default:
            logI(TAG, "switch statement with unhandled case");
            break;
      }
   }
}
