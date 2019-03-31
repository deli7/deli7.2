package com.delivame.delivame.deliveryman.activities.client;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.common.CurrentOrdersActivity;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.MyPlace;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.LocationServiceManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.github.mikephil.charting.components.MarkerView;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.delivame.delivame.deliveryman.utilities.Constants.DEFAULT_ZOOM_LEVEL;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;

public class AddNewMyPlaceActivity extends ClientBaseActivity implements
      OnMapReadyCallback, LocationServiceManager.LocationServiceManagerListener {

   @BindView(R.id.buttonSavePlace)
   Button buttonSavePlace;

   private GoogleMap mMap;
   private String placeTitle;
   private String placeAddress;
   private LatLng placeLatLng;
   private Store store;
   private LocationServiceManager locationServiceManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_select_my_place);
      ButterKnife.bind(this);
      initUI();
   }

   private void initUI() {
      initUI(getString(R.string.ui_activity_title_select_store), true);
      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);
      initPlaceSearchBar();
      locationServiceManager = new LocationServiceManager(AddNewMyPlaceActivity.this);
      locationServiceManager.initLocationService();
   }

   @Override
   protected void onPause() {
      super.onPause();
      locationServiceManager.stopLocationUpdates();
   }

   @Override
   public void onMapReady(GoogleMap googleMap) {
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
      mMap = googleMap;



      mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
         @Override
         public void onMapClick(@NonNull com.google.android.gms.maps.model.LatLng latLng) {
            MyUtility.logI(Constants.TAG, "Map Clicked...");

            if (!internetConnected) {
               // showSnackBar(getString(R.string.no_internet_connection));
               showInternetStatus();
               return;
            }

            placeLatLng = new LatLng(latLng.latitude, latLng.longitude);

            placeAddress = MyUtility.getAddress(AddNewMyPlaceActivity.this,
                  latLng.latitude, latLng.longitude);
            mMap.addMarker(new MarkerOptions().position(placeLatLng.toGmsLatLng()).
                  title(getString(R.string.ui_label_destination)).snippet(placeAddress)).showInfoWindow();
         }
      });
   }


   private void initPlaceSearchBar() {
      PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
            getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

      autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
         @Override
         public void onPlaceSelected(Place place) {
            // TODO: Get info about the selected place.
            //Log.i(TAG, "Place: " + place.getName());

            placeTitle = place.getName().toString();
            placeAddress = place.getAddress().toString();
            double lat = place.getLatLng().latitude;
            double lng = place.getLatLng().longitude;
            placeLatLng = new LatLng(lat, lng);

            //textViewDestinationAddress.setText(placeTitle + ", " + placeAddress);

            mMap.addMarker(new MarkerOptions().position(placeLatLng.toGmsLatLng()).title(placeTitle)
                  .snippet(placeAddress)).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng.toGmsLatLng(), DEFAULT_ZOOM_LEVEL));
         }

         @Override
         public void onError(Status status) {
            // TODO: Handle the error.
            //Log.i(TAG, "An error occurred: " + status);
         }
      });
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      switch (requestCode) {
         case Constants.REQ_GOOGLE_PLACES:
         case Constants.REQ_MY_PLACES:
            if (resultCode == Activity.RESULT_OK && data != null) {
               placeTitle = data.getStringExtra(Constants.MY_PLACE_TITLE);
               placeAddress = data.getStringExtra(Constants.MY_PLACE_ADDRESS);
               double lat = data.getDoubleExtra(Constants.MY_PLACE_LAT, 0);
               double lng = data.getDoubleExtra(Constants.MY_PLACE_LNG, 0);
               placeLatLng = new LatLng(lat, lng);
               //textViewDestinationAddress.setText(placeTitle + ", " + placeAddress);

               mMap.addMarker(new MarkerOptions().position(placeLatLng.toGmsLatLng()).title(placeTitle)
                     .snippet(placeAddress)).showInfoWindow();
               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng.toGmsLatLng(), DEFAULT_ZOOM_LEVEL));
            }
            break;
         default:
            MyUtility.logI(Constants.TAG, "switch statement with unhandled case");
            break;
      }
   }

   @OnClick(R.id.buttonSavePlace)
   public void onClick() {
      AlertDialog.Builder builder =
            new AlertDialog.Builder(new ContextThemeWrapper(AddNewMyPlaceActivity.this,
                  R.style.AppCompatAlertDialogStyle));
      builder.setTitle(getString(R.string.ui_button_save_location_to_my_places));

      // Set up the input
      final EditText input = new EditText(this);
      // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
      input.setInputType(InputType.TYPE_CLASS_TEXT);
      builder.setView(input);

      // Set up the buttons
      builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            String title = input.getText().toString();
            placeAddress = MyUtility.getAddress(AddNewMyPlaceActivity.this,
                  placeLatLng.latitude, placeLatLng.longitude);

            Intent intent = new Intent();
            intent.putExtra(Constants.MY_PLACE_TITLE, input.getText().toString());
            intent.putExtra(Constants.MY_PLACE_ADDRESS, placeAddress);
            intent.putExtra(Constants.MY_PLACE_LAT, placeLatLng.longitude);
            intent.putExtra(Constants.MY_PLACE_LNG, placeLatLng.longitude);

            MyPlace myPlace = new MyPlace(title, placeAddress, placeLatLng.latitude, placeLatLng.longitude);
            MyUtility.getSavedPlacesNodeRef().child(MyUtility.getCurrentUserUID()).child(title).setValue(myPlace);

            setResult(Constants.REQ_ADD_NEW_PLACE, intent);
            finish();
         }
      });
      builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

      builder.show();
   }

   @Override
   public void processLocation(Location location) {
      //onLocationChanged(location);
      placeLatLng = new LatLng(location.getLatitude(), location.getLongitude());
      mMap.addMarker(new MarkerOptions().position(placeLatLng.toGmsLatLng()).title(""));
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng.toGmsLatLng(), DEFAULT_ZOOM_LEVEL));
   }
}
