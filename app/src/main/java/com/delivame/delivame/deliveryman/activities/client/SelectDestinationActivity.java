package com.delivame.delivame.deliveryman.activities.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.LocationServiceManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.delivame.delivame.deliveryman.utilities.Constants.DEFAULT_ZOOM_LEVEL;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class SelectDestinationActivity extends ClientBaseActivity
        implements OnMapReadyCallback, LocationServiceManager.LocationServiceManagerListener {

   @BindView(R.id.textViewDestinationAddress)
   TextView textViewDestinationAddress;
   @BindView(R.id.imageButtonSearchForPlace)
   ImageButton imageButtonSearchForPlace;
   @BindView(R.id.imageButtonSelectMyPlace)
   Button imageButtonSelectMyPlace;
   @BindView(R.id.buttonSelectCurrentLocation)
   Button buttonSelectCurrentLocation;
   @BindView(R.id.buttonSelectDestination)
   Button buttonSelectDestination;

   private GoogleMap mMap;
   private String placeAddress;
   private LatLng currentLatLng;
   private Store store;
   private Location mCurrentLocation;
   private LocationServiceManager locationServiceManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_select_destination);
      ButterKnife.bind(this);
      getExtras();
      locationServiceManager = new LocationServiceManager(SelectDestinationActivity.this);
      initUI();
   }

   @Override
   protected void onPause() {
      super.onPause();
      if (locationServiceManager != null) {
         locationServiceManager.stopLocationUpdates();
      }
   }

   private void initUI() {
      initUI(getString(R.string.ui_button_select_destination), true);

      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);
   }

   private void getExtras() {
      Intent intent = getIntent();

      if (intent != null) {
         String placeId = intent.getStringExtra(Constants.BUNDLE_PARAMS_STORE_PLACE_ID);
         String placeName = intent.getStringExtra(Constants.BUNDLE_PARAMS_STORE_NAME);
         String vicinity = intent.getStringExtra(Constants.BUNDLE_PARAMS_STORE_VICINITY);

         double lat = intent.getDoubleExtra(Constants.FIREBASE_KEY_ORDER_PICKUP_LAT, 0);
         double lng = intent.getDoubleExtra(Constants.FIREBASE_KEY_ORDER_PICKUP_LNG, 0);
         LatLng latLngPickUpPoint = new LatLng(lat, lng);

         double lat1 = intent.getDoubleExtra(Constants.CURRENT_LOCATION_LAT, 0);
         double lng1 = intent.getDoubleExtra(Constants.CURRENT_LOCATION_LNG, 0);
         currentLatLng = new LatLng(lat1, lng1);

         double distance = intent.getDoubleExtra(Constants.FIREBASE_KEY_ORDER_ESTIMATED_DISTANCE, 0);

         store = new Store();
         store.setPlaceName(placeName);
         store.setVicinity(vicinity);
         store.setDistance(distance);
         store.setLatLng(latLngPickUpPoint);
         store.setPlaceId(placeId);
      }

   }

   public static void startMe(Context context, Store store, LatLng currentLocation) {
      Intent intent = new Intent(context, SelectDestinationActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      intent.putExtra(Constants.BUNDLE_PARAMS_STORE_PLACE_ID, store.getPlaceId());
      intent.putExtra(Constants.BUNDLE_PARAMS_STORE_NAME, store.getPlaceName());
      intent.putExtra(Constants.BUNDLE_PARAMS_STORE_VICINITY, store.getVicinity());
      intent.putExtra(Constants.FIREBASE_KEY_ORDER_PICKUP_LAT, store.getLatLng().latitude);
      intent.putExtra(Constants.FIREBASE_KEY_ORDER_PICKUP_LNG, store.getLatLng().longitude);
      intent.putExtra(Constants.CURRENT_LOCATION_LAT, currentLocation.getLatitude());
      intent.putExtra(Constants.CURRENT_LOCATION_LNG, currentLocation.getLongitude());
      intent.putExtra(Constants.FIREBASE_KEY_ORDER_ESTIMATED_DISTANCE, store.getDistance());

      context.startActivity(intent);
   }

   @Override
   public void onMapReady(GoogleMap googleMap) {
      //------map style start here------
      try {
         // Customise the styling of the base map using a JSON object defined
         // in a raw resource file.
         boolean success = googleMap.setMapStyle(
                 MapStyleOptions.loadRawResourceStyle(
                         this, R.raw.mapstyle));

         if (!success) {
            Log.e(TAG, "Style parsing failed.");
         }
      } catch (Resources.NotFoundException e) {
         Log.e(TAG, "Can't find style. Error: ", e);
      }
      //----------map style end here----------
      mMap = googleMap;
      locationServiceManager.initLocationService();

      mMap.setOnMapClickListener(latLng -> {
         logI(TAG, "Map Clicked...");

         if (!internetConnected) {
            // showSnackBar(getString(R.string.no_internet_connection));
            showInternetStatus();
            return;
         }

         currentLatLng = new LatLng(latLng.latitude, latLng.longitude);

         placeAddress = MyUtility.getAddress(SelectDestinationActivity.this,
                 latLng.latitude, latLng.longitude);
         mMap.addMarker(new MarkerOptions().position(currentLatLng.toGmsLatLng())
                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)));
      });
   }


   @OnClick(R.id.imageButtonSearchForPlace)
   public void onimageButtonSearchForPlaceClick() {
      searchForPlace();
   }

   private void searchForPlace() {
      try {
         Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                 .build(SelectDestinationActivity.this);

         startActivityForResult(intent, Constants.REQ_GOOGLE_PLACES);
         //PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
      } catch (@NonNull GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
         // TODO: Handle the error.
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      switch (requestCode) {

         case Constants.REQ_GOOGLE_PLACES:
            String placeTitle;
            if (resultCode == Activity.RESULT_OK && data != null) {
               Place place = PlaceAutocomplete.getPlace(this, data);
               placeTitle = place.getName().toString();
               placeAddress = place.getAddress().toString();
               double lat = place.getLatLng().latitude;
               double lng = place.getLatLng().longitude;
               currentLatLng = new LatLng(lat, lng);
               textViewDestinationAddress.setText(placeTitle + ", " + placeAddress);

               mMap.addMarker(new MarkerOptions().position(currentLatLng.toGmsLatLng())
                       .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)));
               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng.toGmsLatLng(), DEFAULT_ZOOM_LEVEL));
            }


            break;
         case Constants.REQ_MY_PLACES:
            if (resultCode == Activity.RESULT_OK && data != null) {
               placeTitle = data.getStringExtra(Constants.MY_PLACE_TITLE);
               placeAddress = data.getStringExtra(Constants.MY_PLACE_ADDRESS);
               double lat = data.getDoubleExtra(Constants.MY_PLACE_LAT, 0);
               double lng = data.getDoubleExtra(Constants.MY_PLACE_LNG, 0);
               currentLatLng = new LatLng(lat, lng);

               textViewDestinationAddress.setText(placeTitle + ", " + placeAddress);

               mMap.addMarker(new MarkerOptions().position(currentLatLng.toGmsLatLng())
                       .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)));

               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng.toGmsLatLng(), DEFAULT_ZOOM_LEVEL));

               AddNewOrderActivity.startMe(getApplicationContext(), store,
                       LatLng.fromLocation(mCurrentLocation), currentLatLng, "");
            }

            break;
         default:
            logI(TAG, "switch statement with unhandled case");
            break;
      }

   }

   @OnClick(R.id.imageButtonSelectMyPlace)
   public void onimageButtonSelectMyPlaceClick() {
      Intent intent = new Intent(getApplicationContext(), MyPlacesActivity.class);
      startActivityForResult(intent, Constants.REQ_MY_PLACES);
   }

   @OnClick({R.id.buttonSelectDestination, R.id.buttonSelectCurrentLocation})
   public void onClick(View view) {

      if (mCurrentLocation == null) {
         Toast.makeText(this, getString(R.string.ui_message_error_location_detection), Toast.LENGTH_SHORT).show();
         return;
      }

      switch (view.getId()) {
         case R.id.buttonSelectDestination:
            if (currentLatLng != null) {

               AddNewOrderActivity.startMe(getApplicationContext(), store,
                       LatLng.fromLocation(mCurrentLocation), currentLatLng, "");

            } else {
               showMessage(getString(R.string.ui_message_error_missing_destination_point));
            }
            break;

         case R.id.buttonSelectCurrentLocation:
            currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            AddNewOrderActivity.startMe(getApplicationContext(), store,
                    LatLng.fromLocation(mCurrentLocation), currentLatLng, "");
            break;
      }
   }

   @Override
   public void processLocation(Location location) {
      logI(TAG, "processLocation");
      logI(TAG, "location: " + location);
      mCurrentLocation = location;
      LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
      mMap.addMarker(new MarkerOptions().position(latLng.toGmsLatLng())
              .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)));
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng.toGmsLatLng(), DEFAULT_ZOOM_LEVEL));
      locationServiceManager.stopLocationUpdates();
   }

}
