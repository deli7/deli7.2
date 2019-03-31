package com.delivame.delivame.deliveryman.googleapis;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;
import com.delivame.delivame.deliveryman.utilities.SharedPrefHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

/**
 * Created by navneet on 23/7/16.
 */
public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private String googlePlacesData;
    private final List<Store> storeList = new ArrayList<>();
    private GetNearbyDataListener listener;
    private final String key;
    private final Context context;


    public GetNearbyPlacesData(Context context, String key) {
        this.key = key;
        this.context = context;
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace, Boolean pageToken) {

        //logI(TAG2, "latitude = " + latitude);
        //logI(TAG2, "longitude = " + longitude);
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(String.valueOf(latitude)).append(",").append(String.valueOf(longitude));
        int PROXIMITY_RADIUS = 1000;
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=").append(nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&fields=address_component,geometry");

        if (LocaleHelper.isLanguageEnglish(context)) {
            googlePlacesUrl.append("&language=en");
        } else {
            googlePlacesUrl.append("&language=ar");
        }

        if (pageToken) {
            String next_page_token = SharedPrefHelper.getPrefString(context, "next_page_token");
            logI(TAG, "next_page_token: " + next_page_token);
            googlePlacesUrl.append("&pagetoken=").append(next_page_token);
        }

        //googlePlacesUrl.append("&keyword=formatted_phone_number");
        googlePlacesUrl.append("&key=").append(key);
        Log.d("AppInfo", "Nearby Places URL: " + googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");

            listener = (GetNearbyDataListener) params[0];
            LatLng latLngCenter = (LatLng) params[1];
            String type = (String) params[2];
            Boolean pageToken = (Boolean) params[3];

            String url = getUrl(latLngCenter.getLatitude(), latLngCenter.getLongitude(), type, pageToken);

            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList;
        PlacesDataParser dataParser = new PlacesDataParser();
        nearbyPlacesList = dataParser.parse(context, result);

        if (nearbyPlacesList != null) {
            ShowNearbyPlaces(nearbyPlacesList);
            Log.d("GooglePlacesReadTask", "onPostExecute Exit");
        }
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {


            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            String place_id = googlePlace.get("place_id");
            //String distance = googlePlace.get("formatted_phone_number");
            double rating = 0;
            if (googlePlace.get("rating") != null) {
                rating = Double.parseDouble(googlePlace.get("rating"));
            }
            LatLng latLng = new LatLng(lat, lng);

            Store store = new Store();
            store.setPlaceName(placeName);
            store.setLatLng(latLng);
            store.setVicinity(vicinity);
            store.setPlaceId(place_id);
            store.setRating(rating);
            store.setOpennow(googlePlace.get("open_now"));
            //store.setDistance(Double.parseDouble(distance));

            storeList.add(store);
        }

        listener.setNearbyStores(storeList);
    }

    public interface GetNearbyDataListener {
        void setNearbyStores(List<Store> storeList);
    }
}
