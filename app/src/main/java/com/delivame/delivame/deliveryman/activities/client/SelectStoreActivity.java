package com.delivame.delivame.deliveryman.activities.client;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.adapters.StoresRecyclerAdapter;
import com.delivame.delivame.deliveryman.googleapis.GetDistanceAndTimeData;
import com.delivame.delivame.deliveryman.googleapis.GetNearbyPlacesData;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.LocationServiceManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class SelectStoreActivity extends ClientBaseActivity
      implements GetNearbyPlacesData.GetNearbyDataListener,
      StoresRecyclerAdapter.StoreRecyclerAdapterListener,
      GetDistanceAndTimeData.GetDistanceAndTimeDataListener,

      InitManager.InitManagerListener,
      LocationServiceManager.LocationServiceManagerListener {


   // ---------------------------------------------------------------------------------------
   // UI
   // ---------------------------------------------------------------------------------------
   @BindView(R.id.recyclerView)
   RecyclerView recyclerView;
   @BindView(R.id.editTextSearch)
   EditText editTextSearch;
   @BindView(R.id.progressBarLoading)
   ProgressBar progressBarLoading;


   // ---------------------------------------------------------------------------------------
   // Members
   // ---------------------------------------------------------------------------------------
   private String type = "";
   private final List<Store> storeList = new ArrayList<>();
   private final List<Store> tempList = new ArrayList<>();
   private StoresRecyclerAdapter adapter;
   private Location mCurrentLocation;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_select_store);
      ButterKnife.bind(this);

      getExtras();

      new InitManager().init(SelectStoreActivity.this);


   }

   //    @Override
//    protected void onResume() {
//        super.onResume();
//        if (locationServiceManager != null) {
//            locationServiceManager.initLocationService();
//        }
//    }
//
   @Override
   protected void onPause() {
      super.onPause();
      if (locationServiceManager != null) {

         locationServiceManager.stopLocationUpdates();

      }
   }


   private void getExtras() {
      Intent intent = getIntent();
      Bundle params = intent.getBundleExtra(Constants.BUNDLE_PARAMS);
      if (params != null) {
         type = params.getString(Constants.BUNDLE_PARAMS_BUSINESS_TYPE);
         logI(TAG, "Business Type = " + type);
      }

   }

   public static void startMe(Context context) {
      Intent intent = new Intent(context, SelectStoreActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }

   private LocationServiceManager locationServiceManager;

   private void initUI() {

      initUI(getString(R.string.ui_activity_title_select_store), true);

      initRecyclerView();

      initSearch();

      progressBarLoading.setVisibility(View.VISIBLE);

      logI(TAG, "initUI()");
      locationServiceManager = new LocationServiceManager(this);
      locationServiceManager.initLocationService();

   }


   private void getNearbyPlaces(final Location location, final boolean pageToken) {

      if (type.equalsIgnoreCase("All")) {

         MyUtility.getStoresNodeRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot ds : dataSnapshot.getChildren()) {
                  for (DataSnapshot categoryStore : ds.getChildren()) {

                     Store store = categoryStore.getValue(Store.class);

                     if (isNearbyStore(store)) {

                        if (!isInList(tempList, store.getPlaceName())) {
                           tempList.add(store);
                        }
                     }
                  }
               }

               GetNearbyPlacesData getNearbyPlacesData;
               Object[] DataTransfer;

               //if (storeList.size() == 0) {
               progressBarLoading.setVisibility(View.VISIBLE);
               mCurrentLocation = location;

               DataTransfer = new Object[4];
               DataTransfer[0] = SelectStoreActivity.this;
               DataTransfer[1] = new LatLng(location.getLatitude(), location.getLongitude());
               DataTransfer[2] = type;
               DataTransfer[3] = pageToken;

               getNearbyPlacesData = new GetNearbyPlacesData(getApplicationContext(), settings.getGoogleApiKey());
               getNearbyPlacesData.execute(DataTransfer);
               //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });


      } else {
         MyUtility.getStoresNodeRef().child(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot ds : dataSnapshot.getChildren()) {
                  Store store = ds.getValue(Store.class);

                  if (isNearbyStore(store)) {

                     if (!isInList(tempList, store.getPlaceName())) {
                        tempList.add(store);
                     }
                  }
               }

               GetNearbyPlacesData getNearbyPlacesData;
               Object[] DataTransfer;

               //if (storeList.size() == 0) {
               progressBarLoading.setVisibility(View.VISIBLE);
               mCurrentLocation = location;

               DataTransfer = new Object[4];
               DataTransfer[0] = SelectStoreActivity.this;
               DataTransfer[1] = new LatLng(location.getLatitude(), location.getLongitude());
               DataTransfer[2] = type;
               DataTransfer[3] = pageToken;

               getNearbyPlacesData = new GetNearbyPlacesData(getApplicationContext(), settings.getGoogleApiKey());
               getNearbyPlacesData.execute(DataTransfer);
               //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });

      }


   }

   private boolean isNearbyStore(Store store) {
      if (store == null || store.getLatLng() == null) {
         return false;
      }
      double distance =
            MyUtility.getLengthBetweenLatLngs(
                  new com.google.android.gms.maps.model.LatLng(mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude()),
                  store.getLatLng().toGmsLatLng());

      return distance < 150;
   }


   private boolean isInList(List<Store> storeList, String name) {
      for (int i = 0; i < storeList.size(); i++) {
         if (storeList.get(i).getPlaceName().equals(name)) {
            return true;
         }
      }
      return false;
   }


   @Override
   public void setNearbyStores(List<Store> storeList) {

      for (int i = 0; i < storeList.size(); i++) {
         if (!isInList(this.tempList, storeList.get(i).getPlaceName())) {
            logI(TAG, "## STORE: " + storeList.get(i).getPlaceName());
            this.tempList.add(storeList.get(i));


            MyUtility.getStoresNodeRef().child(type)
                  .child(storeList.get(i).getPlaceName()).setValue(storeList.get(i));
         }
      }

      //filterDistances();


      getDistances();

//        count += 1;
//
//        next_page_token = SharedPrefHelper.getPrefString(getApplicationContext(), "next_page_token");
//        logI(TAG, "???? next_page_token: " + next_page_token);
//
//        if (!TextUtils.isEmpty(next_page_token)) {
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    getNearbyPlaces(mCurrentLocation, true);
//
//
//                }
//            }, 500);
//
//
//        } else {
//            getDistances();
//        }

      // this.storeList = storeList;

      //getDistances();
   }

   private void filterDistances() {

      com.google.android.gms.maps.model.LatLng myLocation = new com.google.android.gms.maps.model.LatLng(
            mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()
      );


      for (int i = 0; i < tempList.size(); i++) {
         Store store = tempList.get(i);

         com.google.android.gms.maps.model.LatLng storeLocation = new com.google.android.gms.maps.model.LatLng(
               store.getLatLng().latitude, store.getLatLng().longitude
         );

         double distance =
               MyUtility.getLengthBetweenLatLngs(myLocation, storeLocation);

         tempList.get(i).setDistance(distance);

         if (distance < settings.getDistanceRangeToDelivery()) {
            storeList.add(tempList.get(i));
         }
      }

      Collections.sort(storeList, (o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));

      logI(TAG, "storeList Size: " + storeList.size());

      //updateRecyclerView(storeList, adapter,getString(R.string.ui_label_no_stores_available));
      progressBarLoading.setVisibility(View.GONE);
      initRecyclerView();
   }

   private void getDistances() {

      GetDistanceAndTimeData getDistanceAndTimeData = new
            GetDistanceAndTimeData(tempList,
            LatLng.fromLocation(mCurrentLocation),
            settings.getGoogleApiKey());

      Object[] DataTransfer = new Object[4];
      DataTransfer[0] = SelectStoreActivity.this;
      DataTransfer[1] = getApplicationContext();


      getDistanceAndTimeData.execute(DataTransfer);

   }

   private void initRecyclerView() {
      adapter = new StoresRecyclerAdapter(SelectStoreActivity.this, storeList);
      RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
      recyclerView.setLayoutManager(mLayoutManager);
      recyclerView.setItemAnimator(new DefaultItemAnimator());
      recyclerView.setAdapter(adapter);


//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (!recyclerView.canScrollVertically(1)) {
//                    //Toast.makeText(SelectStoreActivity.this,"LAst", Toast.LENGTH_LONG).show();
//
//                    getNearbyPlaces(mCurrentLocation, true);
//                }
//
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (dy > 0) {
//                    // Scrolling up
//                } else {
//                    // Scrolling down
//                }
//            }
//        });
   }

   @Override
   public void showStore(Store store) {

   }

   @Override
   public void makeOrder(Store store) {
      SelectDestinationActivity.startMe(getApplicationContext(),
            store,
            LatLng.fromLocation(mCurrentLocation));
   }


   private void initSearch() {

      //adding a TextChangedListener
      //to call a method whenever there is some change on the EditText
      editTextSearch.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         }

         @Override
         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         }

         @Override
         public void afterTextChanged(Editable editable) {
            //after the change calling the method and passing the search input
            filter(editable.toString());
         }
      });
   }

   private void filter(String text) {

      if (storeList == null) return;

      //new array list that will hold the filtered data
      ArrayList<Store> filterdList = new ArrayList<>();

      if (!TextUtils.isEmpty(text)) {
         //looping through existing elements
         for (Store model : storeList) {
            if (model != null && model.getPlaceName() != null) {
               //if the existing elements contains the search input
               if (model.getPlaceName().toLowerCase().contains(text.toLowerCase())) {
                  //adding the element to filtered list
                  filterdList.add(model);
               }
            }
         }
         //calling a method of the adapter class and passing the filtered list
         adapter.filterList(filterdList);
      } else {
         //calling a method of the adapter class and passing the filtered list

         updateRecyclerView(storeList, adapter, getString(R.string.ui_label_no_stores_available));
      }


   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      initUI();
   }

   private boolean isLocationSet = false;

   @Override

   public void processLocation(Location location) {
      logI(TAG, "processLocation");

      if (!isLocationSet) {
         isLocationSet = true;
         mCurrentLocation = location;
         getNearbyPlaces(location, false);
         locationServiceManager.stopLocationUpdates();
      }
   }

   @Override
   public void setEstimatedDistanceAndTime(double distance, double time) {

   }

   @Override
   public void setListEstimatedDistanceAndTime(List<Double> distance, List<Double> time) {


      for (int i = 0; i < tempList.size(); i++) {
         tempList.get(i).setDistance(distance.get(i));
         tempList.get(i).setTime(time.get(i));
         logI(TAG, "STORE: " + tempList.get(i).getPlaceName() + " - distance.get(i): " + distance.get(i));
         double range;
//                if (type.equals("restaurant")) {
//                    range = settings.getDistanceRangeToDelivery() / 1000;
//                } else {
         range = 150;
         //}
         if (distance.get(i) < range) {
            if (!isInList(storeList, tempList.get(i).getPlaceName())) {
               storeList.add(tempList.get(i));
            }

         }


      }

      Collections.sort(storeList, (o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));

      logI(TAG, "storeList Size: " + storeList.size());

      progressBarLoading.setVisibility(View.GONE);

      initRecyclerView();
   }

}



