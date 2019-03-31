package com.delivame.delivame.deliveryman.googleapis;

import android.os.AsyncTask;

import com.delivame.delivame.deliveryman.models.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

/**
 * Created by navneet on 23/7/16.
 */
public class GetPlaceDetailsData extends AsyncTask<Object, String, String> {

   private String googlePlacesData;
   private Store store;
   private GetPlaceDetailsDataListener listener;
   private int pos = 0;
   private String Key;

   public GetPlaceDetailsData(String Key) {
      this.Key = Key;
   }

   private String getUrl(String placeid) {
      return ("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeid + "&key=" + Key);
   }


   @Override
   protected String doInBackground(Object... params) {
      try {
         listener = (GetPlaceDetailsDataListener) params[0];
         store = (Store) params[1];
         pos = (int) params[2];

         String url = getUrl(store.getPlaceId());

         DownloadUrl downloadUrl = new DownloadUrl();
         googlePlacesData = downloadUrl.readUrl(url);

      } catch (Exception e) {
         logI(TAG, "ex: " + e.getMessage());
      }
      return googlePlacesData;
   }

   @Override
   protected void onPostExecute(String result) {
      getPlaceDetails(result);
   }

   /**
    * Parsing the Place Details Object object
    */
   private void getPlaceDetails(String jPlaceDetails) {
      try {
         JSONObject jsonObject = new JSONObject(jPlaceDetails);

         String formatted_phone = "";
         String international_phone_number = "";


         if (!jsonObject.isNull("result")) {
            JSONObject resultObject = jsonObject.getJSONObject("result");
            // Extracting Place formatted_phone, if available
            if (!resultObject.isNull("formatted_phone_number")) {
               formatted_phone = resultObject.getString("formatted_phone_number");
            }

            // Extracting rating, if available
            if (!resultObject.isNull("international_phone_number")) {
               international_phone_number = resultObject.getString("international_phone_number");
            }

            // Extracting store types
            if (!resultObject.isNull("types")) {
               JSONArray types = resultObject.getJSONArray("types");

               for (int i = 0; i < types.length(); i++)
                  store.getTypes().add(types.getString(i));
            }
         }

         store.setPhoneNumber(formatted_phone);
         store.setInternationalphoneNumber(international_phone_number);

         listener.updateStoreData(store, pos);
      } catch (JSONException e) {
         e.printStackTrace();
      }
   }

   public interface GetPlaceDetailsDataListener {
      void updateStoreData(Store store, int pos);
   }
}
