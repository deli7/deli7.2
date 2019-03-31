package com.delivame.delivame.deliveryman.googleapis;

import android.content.Context;
import android.util.Log;

import com.delivame.delivame.deliveryman.utilities.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

/**
 * Created by navneet on 23/7/16.
 */
class PlacesDataParser {
   public List<HashMap<String, String>> parse(Context context, String jsonData) {
      JSONArray jsonArray = null;
      JSONObject jsonObject;
      if (jsonData == null) return null;

      try {
         jsonObject = new JSONObject(jsonData);
         jsonArray = jsonObject.getJSONArray("results");

         logI(TAG, "$$$ jsonObject: " + jsonObject);
         String next_page_token;
         if (jsonObject.getString("next_page_token") != null) {
            next_page_token = jsonObject.getString("next_page_token");
         } else {
            next_page_token = "";
         }


         logI(TAG, "$$$ next_page_token: " + next_page_token);

         SharedPrefHelper.writePrefString(context, "next_page_token", next_page_token);


      } catch (JSONException e) {
         SharedPrefHelper.writePrefString(context, "next_page_token", "");
         Log.d("Places", "parse error");
         e.printStackTrace();
      }
      return getPlaces(jsonArray);
   }

   private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
      int placesCount = jsonArray.length();
      List<HashMap<String, String>> placesList = new ArrayList<>();
      HashMap<String, String> placeMap;
      Log.d("Places", "getPlaces");

      for (int i = 0; i < placesCount; i++) {
         try {
            placeMap = getPlace((JSONObject) jsonArray.get(i));
            placesList.add(placeMap);
            Log.d("Places", "Adding places");

         } catch (JSONException e) {
            Log.d("Places", "Error in Adding places");
            e.printStackTrace();
         }
      }
      return placesList;
   }

   private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
      HashMap<String, String> googlePlaceMap = new HashMap<>();
      String placeName = "-NA-";
      String vicinity = "-NA-";
      String latitude;
      String longitude;
      String reference;
      String distance = "";
      Boolean OpenNow = false;
      logI(TAG, "googlePlaceJson: " + googlePlaceJson.toString());

      Log.d("getPlace", "Entered");

      try {


         if (!googlePlaceJson.isNull("name")) {
            placeName = googlePlaceJson.getString("name");
         }
         if (!googlePlaceJson.isNull("vicinity")) {
            vicinity = googlePlaceJson.getString("vicinity");
         }
         if (!googlePlaceJson.isNull("opening_hours")) {
            OpenNow = googlePlaceJson.getJSONObject("opening_hours").getBoolean("open_now");
         }
         latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
         longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
         reference = googlePlaceJson.getString("reference");

         googlePlaceMap.put("place_name", placeName);
         googlePlaceMap.put("open_now", OpenNow.toString());
         googlePlaceMap.put("vicinity", vicinity);
         googlePlaceMap.put("lat", latitude);
         googlePlaceMap.put("lng", longitude);
         googlePlaceMap.put("reference", reference);
         googlePlaceMap.put("distance", distance);
         googlePlaceMap.put("place_id", googlePlaceJson.getString("place_id"));
         if (googlePlaceJson.has("rating")) {
            if (googlePlaceJson.getString("rating") != null) {
               googlePlaceMap.put("rating", googlePlaceJson.getString("rating"));
            } else {
               googlePlaceMap.put("rating", "0");
            }
         }

         logI(TAG, "place_id: " + googlePlaceJson.getString("place_id"));

//            JSONArray photos = googlePlaceJson.getJSONArray("photos");
//            if (photos.length() > 0) {
//                googlePlaceMap.put("photo", ((JSONObject) photos.get(0)).getString("photo_reference"));
//            }else{
//                googlePlaceMap.put("photo", "");
//            }


         Log.d("getPlace", "Putting Places");
      } catch (JSONException e) {
         Log.d("getPlace", "Error");
         e.printStackTrace();
      }
      return googlePlaceMap;
   }
}
