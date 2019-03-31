package com.delivame.delivame.deliveryman.googleapis;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.utilities.MyUtility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

// Fetches data from url passed
public class GetDistanceAndTimeData extends AsyncTask<Object, Void, String> {

   private GetDistanceAndTimeDataListener listener;
   private String url;
   private String googleDistanceMatrixData;
   boolean isRoundTrip;
   private double destinationDistance = 0;
   private double destinationTime = 0;
   LatLng latLngPickUp;
   LatLng latLngDestination;
   private Context context;

   private List<Store> storeList;
   private LatLng location;

   private List<LatLng> sources = null;
   private List<LatLng> destinations = null;
   private final String key;

   public GetDistanceAndTimeData(List<Store> storeList, LatLng location, String key) {
      this.storeList = storeList;
      this.location = location;
      this.key = key;
   }

   public GetDistanceAndTimeData(List<LatLng> sources, List<LatLng> destinations, String key) {
      this.sources = sources;
      this.destinations = destinations;
      this.key = key;
   }

   public String getDistacnceUrl(Context context, @NonNull LatLng origin, @NonNull LatLng dest, boolean isRoundTrip) {

      // Origin of route
      String str_origin = "origins=" + origin.latitude + "," + origin.longitude;

      if (isRoundTrip) {
         str_origin += "|" + dest.latitude + "," + dest.longitude;
      }

      // Destination of route
      String str_dest = "destinations=" + dest.latitude + "," + dest.longitude;
      if (isRoundTrip) {
         str_dest += "|" + origin.latitude + "," + origin.longitude;
      }

      // Building the parameters to the web service
      String parameters = str_origin + "&" + str_dest + "&language=en" +
            "&key=" + key;

      // Output format
      String output = "json";

      // Building the url to the web service
      //String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

      //String url = "https://www.google.com/maps/dir/?api=1&" + output + "?" + parameters;

      return "https://maps.googleapis.com/maps/api/distancematrix/" + output + "?" + parameters;
   }

   private String getDistacnceUrl(Context context, List<Store> storeList, LatLng mCurrentLocation) {


      // Origin of route
      StringBuilder str_origin = new StringBuilder();
      StringBuilder str_dest = new StringBuilder();
      //logI(TAG, "getDistacnceUrl storeList.size(): " + storeList.size());
      for (int i = 0; i < storeList.size(); i++) {
         Store store = storeList.get(i);

         if (i == 0) {
            str_origin = new StringBuilder(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
            str_dest = new StringBuilder(store.getLatLng().latitude + "," + store.getLatLng().longitude);
         } else {
            str_origin.append("|").append(mCurrentLocation.getLatitude()).append(",").append(mCurrentLocation.getLongitude());
            str_dest.append("|").append(store.getLatLng().latitude).append(",").append(store.getLatLng().longitude);
         }

      }

      // Building the parameters to the web service
      String parameters = "origins=" + str_origin + "&" + "destinations=" + str_dest +
            "&key=" + key;

      // Output format
      String output = "json";

      // Building the url to the web service
      //String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

      String url = "https://maps.googleapis.com/maps/api/distancematrix/" + output + "?" + parameters;

      logI(TAG, "getDistacnceUrl URL: " + url);

      return url;
   }

   private String getDistacnceUrl1(Context context, List<LatLng> sources, List<LatLng> destinations) {

      // Origin of route
      StringBuilder str_origin = new StringBuilder();
      StringBuilder str_dest = new StringBuilder();

      for (int i = 0; i < sources.size(); i++) {

         if (i == 0) {
            str_origin = new StringBuilder(sources.get(i).getLatitude() + "," + sources.get(i).getLongitude());
            str_dest = new StringBuilder(destinations.get(i).getLatitude() + "," + destinations.get(i).getLongitude());
         } else {
            str_origin.append("|").append(sources.get(i).getLatitude()).append(",").append(sources.get(i).getLongitude());
            str_dest.append("|").append(destinations.get(i).getLatitude()).append(",").append(destinations.get(i).getLongitude());
         }

//            logI(TAG, "getDistacnceUrl str_origin = " + str_origin);
//            logI(TAG, "getDistacnceUrl str_dest = " + str_dest);
      }

      // Building the parameters to the web service
      String parameters = "origins=" + str_origin + "&" + "destinations=" + str_dest +
            "&key=" + key;

      // Output format
      String output = "json";

      // Building the url to the web service
      //String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

      String url = "https://maps.googleapis.com/maps/api/distancematrix/" + output + "?" + parameters;

      logI(TAG, "getDistacnceUrl URL: " + url);

      return url;
   }


   @Override
   protected void onPreExecute() {
      super.onPreExecute();
      int pathLength = 0;
   }

   @NonNull
   @Override
   protected String doInBackground(Object... params) {

      listener = (GetDistanceAndTimeDataListener) params[0];
      context = (Context) params[1];

      if (sources == null && destinations == null) {
         logI(TAG, "Calculating using stores");
         url = getDistacnceUrl(context, storeList, location);
      } else {
         logI(TAG, "Calculating using sources and destinations");
         url = getDistacnceUrl1(context, sources, destinations);
      }


      try {

         DownloadUrl downloadUrl = new DownloadUrl();
         googleDistanceMatrixData = downloadUrl.readUrl(url);

         Log.d(TAG, "Background Task data: " + googleDistanceMatrixData);
      } catch (Exception e) {
         Log.d("Background Task", e.toString());
         return "";

      }
      return googleDistanceMatrixData;
   }

   @Override
   protected void onPostExecute(String result) {
      super.onPostExecute(result);

      logI(TAG, "Response: " + result);


      logI(TAG, "result is not empty: " + result);

      List<Double> distances = new ArrayList<>();
      List<Double> times = new ArrayList<>();

      JSONObject jsonObject;
      try {
         jsonObject = new JSONObject(result);

         JSONArray rows;

         rows = jsonObject.getJSONArray("rows");

         if (rows.length() > 0) {

            for (int i = 0; i < rows.length(); i++) {
               logI(TAG, "rows: " + rows);

               JSONObject elements = (JSONObject) rows.get(i);

               logI(TAG, "elements: " + elements.toString() + " size: " + elements.length());


               JSONObject distance = elements.getJSONArray("elements").getJSONObject(i).getJSONObject("distance");

               logI("Distance", distance.toString());

               JSONObject duration = elements.getJSONArray("elements").getJSONObject(i).getJSONObject("duration");

               logI(TAG, "duration: " + duration.toString());

               String regex = "[^\\.0123456789]";
               //destinationDistance = MyUtility.roundDouble2(Double.parseDouble(distance.getString("text").replaceAll(regex, "")));
               String value = distance.getString("value");
               value = MyUtility.fixNumber(value);

               destinationDistance = MyUtility.roundDouble2(Double.parseDouble(value) / 1000);

               distances.add(destinationDistance);

               String text = (duration.getString("text").replaceAll(regex, ""));
               text = MyUtility.fixNumber(text);
               destinationTime = MyUtility.roundDouble2(Double.parseDouble(text));

               times.add(destinationTime);

            }

         } else {

            for (int i = 0; i < storeList.size(); i++) {
               destinationDistance =
                     MyUtility.getLengthBetweenLatLngs(
                           new com.google.android.gms.maps.model.LatLng(location.getLatitude(),
                                 location.getLongitude()),
                           storeList.get(i).getLatLng().toGmsLatLng());

               logI(TAG, "destinationDistance = " + destinationDistance);

               destinationTime = MyUtility.roundDouble2((destinationDistance / 40) * 60);

               distances.add(destinationDistance);
               times.add(destinationTime);
            }


         }

         listener.setEstimatedDistanceAndTime(destinationDistance, destinationTime);
         listener.setListEstimatedDistanceAndTime(distances, times);

      } catch (JSONException e) {
         for (int i = 0; i < storeList.size(); i++) {
            destinationDistance =
                  MyUtility.getLengthBetweenLatLngs(
                        new com.google.android.gms.maps.model.LatLng(location.getLatitude(),
                              location.getLongitude()),
                        storeList.get(i).getLatLng().toGmsLatLng());

            logI(TAG, "destinationDistance = " + destinationDistance);

            destinationTime = MyUtility.roundDouble2((destinationDistance / 40) * 60);

            distances.add(destinationDistance);
            times.add(destinationTime);
         }
         listener.setEstimatedDistanceAndTime(destinationDistance, destinationTime);
         listener.setListEstimatedDistanceAndTime(distances, times);

         e.printStackTrace();
      }
   }

   public interface GetDistanceAndTimeDataListener {
      void setEstimatedDistanceAndTime(double distance, double time);

      void setListEstimatedDistanceAndTime(List<Double> distance, List<Double> time);
   }
}