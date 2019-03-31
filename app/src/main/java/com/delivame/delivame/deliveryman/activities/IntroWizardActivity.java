package com.delivame.delivame.deliveryman.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.client.ClientHomeActivity;
import com.delivame.delivame.deliveryman.activities.common.Auth.DriverVerificationActivity;
import com.delivame.delivame.deliveryman.activities.common.Auth.SignUpActivity;
import com.delivame.delivame.deliveryman.activities.common.Messaging.ListSupportMessagesActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.DeliveryManHomeActivity;
import com.delivame.delivame.deliveryman.fragments.WizardFragment;
import com.delivame.delivame.deliveryman.models.PromoCode;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.delivame.delivame.deliveryman.utilities.SharedPrefHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.delivame.delivame.deliveryman.utilities.Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;


public class IntroWizardActivity extends BaseActivity
      implements WizardFragment.WizardFragmentListener {

   private ViewPager viewPager;
   private View indicator1;
   private View indicator2;
   private View indicator3;
   private View indicator4;

   private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
   private static final String GOOGLE_PRIVACY_POLICY_URL =
         "https://www.google.com/policies/privacy/";
   private final Settings settings = new Settings();

   public static void startMe(Context context) {
      Intent intent = new Intent(context, IntroWizardActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {

      setContentView(R.layout.activity_intro_wizard);
      coordinatorLayout = findViewById(R.id.coordinatorLayout);

      //reset the database
      if (false) {
         FirebaseDatabase.getInstance().getReference().child("ChatMessages").removeValue();
         FirebaseDatabase.getInstance().getReference().child("Offers").removeValue();
         FirebaseDatabase.getInstance().getReference().child("Orders").removeValue();
         FirebaseDatabase.getInstance().getReference().child("PreviousOrders").removeValue();
         FirebaseDatabase.getInstance().getReference().child("UserOrders").removeValue();
         FirebaseDatabase.getInstance().getReference().child("UserOffers").removeValue();
      }

      if (true) {
         PromoCode promoCode = new PromoCode();
         promoCode.setId("ABC1234");
         promoCode.setDiscountPercent(10);
         promoCode.setPromoStartDate("2018-06-01 00:00:00");
         promoCode.setPromoEndDate("2018-06-28 00:00:00");
         promoCode.setPromoType(0);
         promoCode.setScopeStartDate("2018-06-01 00:00:00");
         promoCode.setScopeEndDate("2018-06-06 00:00:00");
         promoCode.setScopeTripsCount(20);
         promoCode.setScopeTripsValues(100);
         promoCode.setUserRating(4.0);
         promoCode.setValidPromoCode(true);
         DatabaseReference ref =
               FirebaseDatabase.getInstance().getReference().child("PromoCodes").child("ABC1234");
         ref.setValue(promoCode);
      }

      //ChatActivity.startMe(getApplicationContext(), "15", false);
      //finish();

      indicator1 = findViewById(R.id.indicator1);
      indicator2 = findViewById(R.id.indicator2);
      indicator3 = findViewById(R.id.indicator3);
      indicator4 = findViewById(R.id.indicator4);

      if (FirebaseAuth.getInstance().getCurrentUser() != null) {
         indicator1.setVisibility(View.INVISIBLE);
         indicator2.setVisibility(View.INVISIBLE);
         indicator3.setVisibility(View.INVISIBLE);
         indicator4.setVisibility(View.INVISIBLE);
      }

      viewPager = findViewById(R.id.viewPager);
      viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
      viewPager.addOnPageChangeListener(new WizardPageChangeListener());
      updateIndicators(0);

      super.onCreate(savedInstanceState);

      if (LocaleHelper.isLanguageSet(getApplicationContext())) {
//         if (LanguageHelper.isLanguageEnglish(getApplicationContext())) {
//            LanguageHelper.setLocaleEnglish(getApplicationContext());
//         } else {
//            LanguageHelper.setLocaleArabic(getApplicationContext());
//         }

         if (checkPermissions()) {
            continueIntroWizard();
         }
      } else {
         SelectLanguageActivity.startMe(getApplicationContext());
      }
   }

   private boolean checkPermissions() {
      if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
         return true;
      } else {
         requestPermissions();
         return false;
      }
   }

   private void requestPermissions() {
      ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
   }

   @Override
   public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
      logI(TAG, "onRequestPermissionsResult");
      logI(TAG, "requestCode: " + requestCode);

      switch (requestCode) {
         case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

               continueIntroWizard();
               // permission was granted, yay! Do the
               // contacts-related task you need to do.
            }
         }
      }
   }

   private ValueEventListener valueEventListener = new ValueEventListener() {

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

                                 if (dataSnapshot.getValue() == null) {
                                    // Toast.makeText(LoginActivity.this, R.string.error_invalid_registration, Toast.LENGTH_SHORT).show();
                                    //ref.child(Constants.FIREBASE_KEY_USER_LOGGED_IN).setValue(false);
                                    FirebaseAuth.getInstance().signOut();
                                    startLogin2();
                                 } else {
                                    User user = new User(dataSnapshot, MyUtility.getCurrentUserNodeRef(),
                                          MyUtility.getCurrentUserUID(), settings);
                                    handleUserType(user);
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
   };

   private void continueIntroWizard() {
      SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
      boolean first_time = prefs.getBoolean(Constants.PREF_SETTING_FIRST_TIME, true);

      if (first_time) {
         SharedPreferences preferences = getDefaultSharedPreferences(getApplicationContext());
         Editor editor = preferences.edit();
         editor.putBoolean(Constants.PREF_SETTING_FIRST_TIME, false).apply();
         startLogin2();
      } else {
         if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startLogin2();
         } else {
            setSnackBarMessage("Logged In User...", true);
            new CountDownTimer(2000, 1000) {

               @Override
               public void onTick(long millisUntilFinished) {
               }

               @Override
               public void onFinish() {
                  // logI("AppInfo", "[TIMER] TIME FINISHED");
                  MyUtility.getGlobalSettingsNodeRef().addListenerForSingleValueEvent(valueEventListener);
               }
            }.start();
         }
      }
   }

   private void validateAndLoginUser(final IdpResponse response) {
      String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

      final DatabaseReference ref = MyUtility.getUsersNodeRef().child(uId);

      ref.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
               User user = new User(dataSnapshot, ref, MyUtility.getCurrentUserUID(), settings);
               String token = MyUtility.generateToken();

               ref.child(Constants.FIREBASE_KEY_USER_TOKEN).setValue(token);
               SharedPrefHelper.writePrefString(getApplicationContext(),
                     Constants.FIREBASE_KEY_USER_TOKEN, token);
               handleUserType(user);
            } else {
               if (response != null) {
                  //Toast.makeText(LoginActivity.this, R.string.error_invalid_registration, Toast.LENGTH_SHORT).show();
                  startSignedInActivity(response);
               } else {
                  startLogin2();
               }
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
            logI(TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
         }
      });
   }

   @Override
   public void onBackPressed() {
      if (viewPager.getCurrentItem() == 0) {
         // If the user is currently looking at the first step, allow the
         // system to handle the Back button. This calls finish() on this activity and pops the
         // back stack.
         super.onBackPressed();
      } else {
         // Otherwise, select the previous step.
         viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
      }
   }

   @Override
   public void startRideNow() {
      startLogin2();
   }

   private class ViewPagerAdapter extends FragmentPagerAdapter {

      private final int WIZARD_PAGES_COUNT = 1;

      ViewPagerAdapter(FragmentManager fm) {
         super(fm);
      }

      @Override
      public Fragment getItem(int position) {
         WizardFragment f = new WizardFragment();
         f.wizard_page_position = position;
         return f;
      }

      @Override
      public int getCount() {
         return WIZARD_PAGES_COUNT;
      }
   }

   private class WizardPageChangeListener implements ViewPager.OnPageChangeListener {

      @Override
      public void onPageScrollStateChanged(int position) {
      }

      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override
      public void onPageSelected(int position) {
         updateIndicators(position);
      }
   }

   private void updateIndicators(int position) {
      DisplayMetrics metrics = getResources().getDisplayMetrics();
      int resizeValue = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 15, metrics);
      int defaultValue = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
      switch (position) {
         case 0:
            indicator1.getLayoutParams().height = resizeValue;
            indicator1.getLayoutParams().width = resizeValue;
            indicator1.requestLayout();

            indicator2.getLayoutParams().height = defaultValue;
            indicator2.getLayoutParams().width = defaultValue;
            indicator2.requestLayout();

            indicator3.getLayoutParams().height = defaultValue;
            indicator3.getLayoutParams().width = defaultValue;
            indicator3.requestLayout();

            indicator4.getLayoutParams().height = defaultValue;
            indicator4.getLayoutParams().width = defaultValue;
            indicator4.requestLayout();

            break;

         case 1:
            indicator1.getLayoutParams().height = defaultValue;
            indicator1.getLayoutParams().width = defaultValue;
            indicator1.requestLayout();

            indicator2.getLayoutParams().height = resizeValue;
            indicator2.getLayoutParams().width = resizeValue;
            indicator2.requestLayout();

            indicator3.getLayoutParams().height = defaultValue;
            indicator3.getLayoutParams().width = defaultValue;
            indicator3.requestLayout();

            indicator4.getLayoutParams().height = defaultValue;
            indicator4.getLayoutParams().width = defaultValue;
            indicator4.requestLayout();
            break;

         case 2:
            indicator1.getLayoutParams().height = defaultValue;
            indicator1.getLayoutParams().width = defaultValue;
            indicator1.requestLayout();

            indicator2.getLayoutParams().height = defaultValue;
            indicator2.getLayoutParams().width = defaultValue;
            indicator2.requestLayout();

            indicator3.getLayoutParams().height = resizeValue;
            indicator3.getLayoutParams().width = resizeValue;
            indicator3.requestLayout();

            indicator4.getLayoutParams().height = defaultValue;
            indicator4.getLayoutParams().width = defaultValue;
            indicator4.requestLayout();
            break;

         case 3:
            indicator1.getLayoutParams().height = defaultValue;
            indicator1.getLayoutParams().width = defaultValue;
            indicator1.requestLayout();

            indicator2.getLayoutParams().height = defaultValue;
            indicator2.getLayoutParams().width = defaultValue;
            indicator2.requestLayout();

            indicator3.getLayoutParams().height = defaultValue;
            indicator3.getLayoutParams().width = defaultValue;
            indicator3.requestLayout();

            indicator4.getLayoutParams().height = resizeValue;
            indicator4.getLayoutParams().width = resizeValue;
            indicator4.requestLayout();
            break;

         default:
            logI(TAG, "switch statement with unhandled case");
            break;
      }
   }

   private void startLogin2() {
      logI(TAG, "startLogin2");

      startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                  .setLogo(R.drawable.logo1)
                  .setAvailableProviders(getSelectedProviders())
                  .setTheme(R.style.LoginTheme)
                  .setTosUrl(GOOGLE_TOS_URL)
                  .setPrivacyPolicyUrl(getSelectedPrivacyPolicyUrl())
                  .setIsSmartLockEnabled(true, false)
                  .build(),
            Constants.RC_SIGN_UP);
   }

   private ProgressDialog progress;

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == Constants.RC_SIGN_UP) {
         ProgressDialog progress = new ProgressDialog(this);
         progress.setMessage("Checking User...");
         progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         progress.setIndeterminate(true);
         progress.setProgress(0);
         progress.show();
         handleSignInResponse(resultCode, data);
         return;
      }

   }

   private void handleSignInResponse(int resultCode, Intent data) {
      IdpResponse response = IdpResponse.fromResultIntent(data);

      // Successfully signed in
      if (resultCode == RESULT_OK) {
         logI(TAG, "resultCode == RESULT_OK");
         logI(TAG, "response:" + response);
         validateAndLoginUser(response);
      } else {
         if (progress != null) {
            progress.cancel();
         }
         logI(TAG, "resultCode != RESULT_OK");
         // Sign in failed
         if (response == null) {
            // User pressed back button
            showSnackbar(R.string.sign_in_cancelled);
            if (progress != null) {
               progress.cancel();
            }
            return;
         }

         if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
            showSnackbar(R.string.ui_message_error_internet_not_connected);
            return;
         }

         showSnackbar(R.string.unknown_error);
      }
   }

   private void startSignedInActivity(IdpResponse response) {
      startActivity(
            SignUpActivity.createIntent(
                  this, response,
                  new SignUpActivity.SignedInConfig(
                        R.mipmap.ic_splash_logo,
                        AuthUI.getDefaultTheme(),
                        getSelectedProviders(),
                        GOOGLE_TOS_URL,
                        false,
                        false)));
   }

   private List<AuthUI.IdpConfig> getSelectedProviders() {
      List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
      selectedProviders.add(new AuthUI.IdpConfig.PhoneBuilder().build());
      return selectedProviders;
   }

   private String getSelectedPrivacyPolicyUrl() {
      return GOOGLE_PRIVACY_POLICY_URL;
   }

   private void showSnackbar(@StringRes int errorMessageRes) {
      Snackbar.make(coordinatorLayout, errorMessageRes, Snackbar.LENGTH_LONG).show();
   }

   private void handleUserType(@NonNull User user) {
      Intent intent;
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
            if (progress != null) {
               progress.cancel();
            }

            showMessage(getString(R.string.error_user_type_pending_verification_driver));
            break;
         case Constants.USER_TYPE_DRIVER_SUSPENDED:
            if (MyUtility.getCurrentUser() != null) {
               //FirebaseAuth.getInstance().signOut();
            }
            if (progress != null) {
               progress.cancel();
            }

            showMessage(getString(R.string.ui_message_error_user_type_driver_suspended));
            ListSupportMessagesActivity.startMe(getApplicationContext());
            finish();
            break;
         case Constants.USER_TYPE_USER_SUSPENDED:
            if (MyUtility.getCurrentUser() != null) {
               //MyUtility.signOutCurrentUser();
            }
            if (progress != null) {
               progress.cancel();
            }

            showMessage(getString(R.string.ui_message_error_user_type_user_suspended));
            ListSupportMessagesActivity.startMe(getApplicationContext());
            finish();
            break;
         case Constants.USER_TYPE_VERIFIED_DRIVER:
            showMessage(getString(R.string.welcome_driver) + " " + user.getFullName());
            DeliveryManHomeActivity.startMe(getApplicationContext());
            finish();
            break;

         case Constants.USER_TYPE_USER:
            //logI(TAG, "Logged In User");
            //showMessage(getString(R.string.welcome_user) + " " + user.getFullName());
            //            ClientHomeActivity.startMe(getApplicationContext());
            intent = new Intent(getApplicationContext(), ClientHomeActivity.class);
            startActivity(intent);
            finish();
            break;
         default:
            logI(TAG, "switch statement with unhandled case");
            break;
      }
   }
}
