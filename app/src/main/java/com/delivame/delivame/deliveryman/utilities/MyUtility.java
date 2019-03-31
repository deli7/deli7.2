package com.delivame.delivame.deliveryman.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.delivame.delivame.deliveryman.BuildConfig;
import com.delivame.delivame.deliveryman.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MyUtility {

   public static String getDirectionsUrl(@NonNull LatLng origin, @NonNull LatLng dest) {

      logI(Constants.TAG, "API: getDirectionsUrl");

      // Origin of route
      String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
      // Destination of route
      String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
      // Building the parameters to the web service
      String parameters = str_origin + "&" + str_dest + "&sensor=false";//"&key=" + Constants.GOOGLE_DISTANCE_MATRIX_API_KEY;

      // Output format
      String output = "json";
      return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
   }

   public static double getLengthBetweenLatLngs(@NonNull LatLng latLng1, @NonNull LatLng latLng2) {
      Location location1 = new Location("");
      location1.setLatitude(latLng1.latitude);
      location1.setLongitude(latLng1.longitude);

      Location location2 = new Location("");
      location2.setLatitude(latLng2.latitude);
      location2.setLongitude(latLng2.longitude);

      double distance = location1.distanceTo(location2) / 1000;
      return roundDouble2(distance);
   }

   public static double getLengthBetweenLatLngsMeters(@NonNull LatLng latLng1, @NonNull LatLng latLng2) {

      Location location1 = new Location("");
      location1.setLatitude(latLng1.latitude);
      location1.setLongitude(latLng1.longitude);

      Location location2 = new Location("");
      location2.setLatitude(latLng2.latitude);
      location2.setLongitude(latLng2.longitude);

      return (double) location1.distanceTo(location2);
   }

   public static Double roundDouble2(double d) {
      String ds = String.valueOf(d);
      int index;
      if (ds.contains(".")) {
         index = ds.indexOf(".");
         String ds1 = ds.substring(0, index + 2);
         return Double.parseDouble(ds1);
      } else if (ds.contains(",")) {
         index = ds.indexOf(",");
         String ds1 = ds.substring(0, index + 2);
         ds1 = arabicToDecimal(ds1);
         return Double.parseDouble(ds1);
      } else {
         return d;
      }
   }

   public static Integer roundDouble0(Double d) {
      return d.intValue();
   }

   private static String arabicToDecimal(String number) {
      char[] chars = new char[number.length()];
      for (int i = 0; i < number.length(); i++) {
         char ch = number.charAt(i);
         if (ch >= 0x0660 && ch <= 0x0669)
            ch -= 0x0660 - '0';
         else if (ch >= 0x06f0 && ch <= 0x06F9)
            ch -= 0x06f0 - '0';
         chars[i] = ch;
      }
      return new String(chars);
   }


   public static String fixNumber(String number) {
      String fulltext = number;
      fulltext = fulltext.replace("٠", "0")
            .replace("١", "1")
            .replace("٢", "2")
            .replace("٣", "3")
            .replace("٤", "4")
            .replace("٥", "5")
            .replace("٦", "6")
            .replace("٧", "7")
            .replace("٨", "8")
            .replace("٩", "9")
            .replace(",", ".");

      return fulltext;

   }

   public static double getTimeDifferenceInMinutes(String orderTripStartTime, String orderTripEndTime) {

      logI(Constants.TAG, "------ getTimeDifferenceInMinutes ------");
      logI(Constants.TAG, "orderTripStartTime: " + orderTripStartTime);
      logI(Constants.TAG, "orderTripEndTime: " + orderTripEndTime);


      DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      Date date1 = null, date2 = null;
      try {
         date2 = df.parse(orderTripEndTime);

         date1 = df.parse(orderTripStartTime);

      } catch (ParseException e) {
         e.printStackTrace();
      }

      if (date2 == null || date1 == null) {
         logI(Constants.TAG, "WRONG: date1 null or date 2 null");
         logI(Constants.TAG, "--------------------------------------------");
         return 0;
      } else {
         long diff = 0;

         if (date2.getTime() > date1.getTime()) {
            logI(Constants.TAG, "date2 > date1");
            diff = date2.getTime() - date1.getTime();
         } else {
            logI(Constants.TAG, "WRONG: date2 < date1");
         }

         float rounded = Math.round((float) diff / (1000 * 60));
         logI(Constants.TAG, "diff: " + rounded);
         logI(Constants.TAG, "--------------------------------------------");


         return Math.round(rounded);
      }
   }


   public static String getAddress(Context context, double lat, double lng) {
      Geocoder geocoder = new Geocoder(context, Locale.getDefault());
      String add = "";
      try {
         List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
         if (addresses.size() > 0) {
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode();
//            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();
         } else {
            logI(Constants.TAG, "addresses.size(): " + addresses.size());
         }

         // TennisAppActivity.showDialog(add);
      } catch (IOException e) {
         logI(Constants.TAG, "ERROR GETTING ADDRESS:" + e.getMessage());
         // TODO Auto-generated catch block
         e.printStackTrace();
         //Toast.makeText(co, e.getMessage(), Toast.LENGTH_SHORT).show();
      }

      return add;
   }

   public static boolean validateEmailAddress(@NonNull EditText editTextEmailAddress,
                                              @NonNull TextInputLayout textInputLayoutEmailAddress) {
      logI(Constants.TAG, "Validating Email Address");
      if (editTextEmailAddress.getText().toString().isEmpty()) {
         logI(Constants.TAG, "ERROR: Email Address is empty");
         textInputLayoutEmailAddress.setError(Constants.ERROR_MISSING_EMAIL_ADDRESS);
         return false;
      } else {
         logI(Constants.TAG, "Email Address is Valid");
         textInputLayoutEmailAddress.setErrorEnabled(false);
         return true;
      }
   }

   @SuppressLint("ResourceType")
   public static boolean validateLoginPassword(@NonNull EditText editTextPassword, @NonNull TextInputLayout textInputLayoutPassword) {
      logI(Constants.TAG, "Validating Password");
      if (editTextPassword.getText().toString().length() < Constants.MINIMUM_PASSWORD_LENGTH) {
         logI(Constants.TAG, "Error: Password length is less than " + Constants.MINIMUM_PASSWORD_LENGTH);
         textInputLayoutPassword.setError(Constants.ERROR_MINIMUM_PASSWORD_LENGTH);
         return false;
      } else {
         logI(Constants.TAG, "Password is valid");
         textInputLayoutPassword.setErrorEnabled(false);
         return true;
      }
   }

   /**
    * A method to download json data from url
    */
   @NonNull
   public static String downloadUrl(String strUrl) throws IOException {

      logI(Constants.TAG, "Entering downloadUrl");
      String data = "";
      InputStream iStream = null;
      logI(Constants.TAG, "step0");

      HttpURLConnection urlConnection = null;
      try {
         URL url = new URL(strUrl);

         // Creating an http connection to communicate with url
         urlConnection = (HttpURLConnection) url.openConnection();
         // Connecting to url
         urlConnection.connect();
         // Reading data from url
         iStream = urlConnection.getInputStream();

         BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

         StringBuilder sb = new StringBuilder();

         String line;
         while ((line = br.readLine()) != null) {
            sb.append(line);
         }

         data = sb.toString();
         br.close();

      } catch (Exception e) {
         Log.d("Exception", e.toString());
      } finally {
         if (iStream != null) {
            iStream.close();
         }
         if (urlConnection != null) {
            urlConnection.disconnect();
         }
      }
      return data;
   }

   public static String getCurrentUserUID() {
      return FirebaseAuth.getInstance().getCurrentUser().getUid();
   }

   public static double getFirbaseDouble(@NonNull DataSnapshot dataSnapshot) {
      if (String.valueOf(dataSnapshot.getValue()).equals("0")) {
         Long xl = Long.parseLong(String.valueOf(dataSnapshot.getValue()));
         return xl.doubleValue();
      } else {
         try {
            return Double.parseDouble(String.valueOf(dataSnapshot.getValue()));
         } catch (Exception ex) {
            return 0.0;
         }
      }


   }

   public static DatabaseReference getCurrentUserNodeRef() {
      return getUsersNodeRef().child(getCurrentUserUID());
   }

   public static DatabaseReference getPublicMessagesRef() {
      return FirebaseDatabase.getInstance().getReference().child("PublicMessages");
   }

   public static DatabaseReference getAddCreditRequestsRef() {
      return FirebaseDatabase.getInstance().getReference().child("AddCreditRequests");
   }

   public static DatabaseReference getWithdrawCreditRequestsRef() {
      return FirebaseDatabase.getInstance().getReference().child("WithdrawCreditRequests");
   }

   public static DatabaseReference getUsersNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("Users");
   }


   public static DatabaseReference getSavedPlacesNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("SavedPlaces");
   }

   public static DatabaseReference getOffersNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("Offers");
   }

   public static DatabaseReference getPromoCodesNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("PromoCodes");
   }

   public static DatabaseReference getUserOffersNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("UserOffers");
   }

   public static DatabaseReference getOrdersNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("Orders");
   }

   public static DatabaseReference getStoresNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("Stores");
   }


   public static DatabaseReference getUserOrdersNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("UserOrders");
   }

   public static DatabaseReference getUserSupportMessagesNodeRef() {
      return FirebaseDatabase.getInstance().getReference()
            .child("UserSupportMessages").child(getCurrentUserUID());
   }

   public static DatabaseReference getSupportMessagesNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("SupportMessages");
   }

   public static DatabaseReference getCountersNodeRef() {
      return FirebaseDatabase.getInstance().getReference()
            .child("Counters");
   }

   public static int readOrderNumber(@NonNull DataSnapshot dataSnapshot) {
      HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
      if (map != null && map.get(Constants.FIREBASE_KEY_ORDER_NUMBER) != null) {
         Long order_number = (Long) map.get(Constants.FIREBASE_KEY_ORDER_NUMBER);
         getCountersNodeRef().child(Constants.FIREBASE_KEY_ORDER_NUMBER).setValue(order_number + 1);
         return order_number.intValue();
      } else {
         getCountersNodeRef().child(Constants.FIREBASE_KEY_ORDER_NUMBER).setValue(1);
         return 1;
      }
   }

   public static int readOfferNumber(@NonNull DataSnapshot dataSnapshot) {
      HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
      if (map != null && map.get(Constants.FIREBASE_KEY_OFFER_NUMBER) != null) {
         Long order_number = (Long) map.get(Constants.FIREBASE_KEY_OFFER_NUMBER);
         getCountersNodeRef().child(Constants.FIREBASE_KEY_OFFER_NUMBER).setValue(order_number + 1);
         return order_number.intValue();
      } else {
         getCountersNodeRef().child(Constants.FIREBASE_KEY_OFFER_NUMBER).setValue(1);
         return 1;
      }
   }

   public static int readUsersCount(@NonNull DataSnapshot dataSnapshot) {
      HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
      if (map != null) {

         String current_count = String.valueOf(map.get(Constants.FIREBASE_KEY_USERS_COUNT));
         logI(Constants.TAG, "current_count:" + current_count);

         int users_count = Integer.parseInt(current_count);
         logI(Constants.TAG, "users_count:" + users_count);

         getCountersNodeRef().child(Constants.FIREBASE_KEY_USERS_COUNT).setValue(users_count + 1);
         return users_count;
      } else {
         getCountersNodeRef().child(Constants.FIREBASE_KEY_USERS_COUNT).setValue("1");
         return 1;
      }
   }

   @Nullable
   public static FirebaseUser getCurrentUser() {
      return FirebaseAuth.getInstance().getCurrentUser();
   }

   public static DatabaseReference getGlobalSettingsNodeRef() {
      return FirebaseDatabase.getInstance().getReference().child("Settings");
   }

   private static String randomString() {
      Random generator = new Random();
      StringBuilder randomStringBuilder = new StringBuilder();
      char tempChar;
      for (int i = 0; i < 20; i++) {
         tempChar = (char) (generator.nextInt(96) + 32);
         randomStringBuilder.append(tempChar);
      }
      return randomStringBuilder.toString();
   }

   public static void signOutCurrentUser() {

      getCurrentUserNodeRef().child(Constants.FIREBASE_KEY_USER_LOGGED_IN)
            .addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if (dataSnapshot.getValue() != null) {
                     getCurrentUserNodeRef().child(Constants.FIREBASE_KEY_USER_LOGGED_IN).setValue(false);
                  }

                  FirebaseAuth.getInstance().signOut();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
            });
   }

   public static String generateToken() {
      return randomString();
   }

   @NonNull
   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
   public static String getFileName(@NonNull Context context, @NonNull Uri uri) {
      String result = null;
      if (uri.getScheme().equals("userComment")) {
         try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
               result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
         }
      }
      if (result == null) {
         result = uri.getPath();
         int cut = result.lastIndexOf('/');
         if (cut != -1) {
            result = result.substring(cut + 1);
         }
      }
      return result;
   }


   public static void logI(String tag, String text) {
      if (BuildConfig.DEBUG) {
         Log.i(tag, text);
      }
   }

   public static DatabaseReference getPreviousCancelledOrdersRef() {
      return FirebaseDatabase.getInstance().getReference()
            .child("PreviousOrders").child("CancelledOrders");
   }

   public static DatabaseReference getPreviousCompletedOrdersRef() {
      return FirebaseDatabase.getInstance().getReference()
            .child("PreviousOrders").child("CompletedOrders");
   }

   public static DatabaseReference getAllPreviousCompletedOrdersRef() {
      return FirebaseDatabase.getInstance().getReference().child("PreviousOrders")
            .child("CompletedOrders").child("AllOrders");
   }

   public static double calculateTripCostWithoutTax(double tripDistance, double tripTime,
                                                    double tripBaseFare, double tripFarePerKm,
                                                    double tripFarePerMin) {
      double fare = (tripBaseFare + tripTime * tripFarePerMin + tripDistance * tripFarePerKm);
      return roundDouble2(fare);
   }

   public static double calculateTripCostWithTax(double tripDistance, double tripTime,
                                                 double tripBaseFare, double tripFarePerKm,
                                                 double tripFarePerMin, double tripTax) {
      double fare = (tripBaseFare + tripTime * tripFarePerMin + tripDistance * tripFarePerKm)
            * (1 + tripTax / 100);
      return roundDouble2(fare);
   }

   public static double readDoubleValue(Object o) {
      if (o.getClass().equals(Double.class)) {
         return (double) o;
      } else if (o.getClass().equals(Long.class)) {
         return ((Long) o).doubleValue();
      }
      return 0;
   }

   public static void toHomeScreen(Context context) {
      Intent startMain = new Intent(Intent.ACTION_MAIN);
      startMain.addCategory(Intent.CATEGORY_HOME);
      startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(startMain);
   }

   public static void saveCurrentLocation(Context context, Location location) {
      SharedPrefHelper.writePrefFloat(context, "location_lat", (float) location.getLatitude());
      SharedPrefHelper.writePrefFloat(context, "location_lng", (float) location.getLongitude());
   }

   public static com.delivame.delivame.deliveryman.models.LatLng getLastLocation(Context context) {
      float lat = SharedPrefHelper.getPrefFloat(context, "location_lat");
      float lng = SharedPrefHelper.getPrefFloat(context, "location_lng");

      if (SharedPrefHelper.getPrefFloat(context, "location_lat") == 0 ||
            SharedPrefHelper.getPrefFloat(context, "location_lng") == 0) {
         return null;
      }
      return new com.delivame.delivame.deliveryman.models.LatLng(lat, lng);
   }

   public static void handleArrowDirection(Context context, ImageView imageViewMessageDetails) {
      if (LocaleHelper.isLanguageEnglish(context)) {
         imageViewMessageDetails.setImageResource(R.mipmap.ic_arrow_right);
      } else {
         imageViewMessageDetails.setImageResource(R.mipmap.ic_arrow_left);
      }
   }

   public static String pointToString(com.delivame.delivame.deliveryman.models.LatLng latLngPickUpPoint) {
      return "Lat: " + latLngPickUpPoint.latitude + " - Lng: " + latLngPickUpPoint.longitude;
   }
}
