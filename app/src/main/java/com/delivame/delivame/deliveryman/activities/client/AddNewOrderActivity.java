package com.delivame.delivame.deliveryman.activities.client;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.googleapis.GetDistanceAndTimeData;
import com.delivame.delivame.deliveryman.googleapis.GetPlaceDetailsData;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.PromoCode;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.models.VehicleCategory;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.delivame.delivame.deliveryman.utilities.Constants.ORDER_ID;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class AddNewOrderActivity extends ClientBaseActivity implements
        OnMapReadyCallback, GetPlaceDetailsData.GetPlaceDetailsDataListener,
        InitManager.InitManagerListener {

   // ---------------------------------------------------------------------------------------
   // UI
   // ---------------------------------------------------------------------------------------
   @BindView(R.id.linearLayout2)
   LinearLayout linearLayout2;
   @BindView(R.id.textView)
   TextView textView;
   @BindView(R.id.spinnerRequiredDeliveryTime)
   Spinner spinnerRequiredDeliveryTime;

   @BindView(R.id.textViewPickUpAddress)
   TextView textViewPickUpAddress;
   @BindView(R.id.textViewDestinationAddress)
   TextView textViewDestinationAddress;
   @BindView(R.id.textViewEstimatedDistance)
   TextView textViewEstimatedDistance;
   @BindView(R.id.textViewEstimatedTime)
   TextView textViewEstimatedTime;
   @BindView(R.id.textInputLayoutInstructions)
   TextInputLayout textInputLayoutInstructions;

   @BindView(R.id.buttonAddOrder)
   Button buttonAddOrder;
   @BindView(R.id.editTextPromoCode)
   EditText editTextPromoCode;

   @BindView(R.id.spinnerdelivery_type)
   Spinner spDeliveryType;
   @BindView(R.id.textViewDeliveryType_desc)
   TextView txtDeliveryDesc;

   @BindView(R.id.spinnerVechicleCategories)
   Spinner spinnerVehicleCategories;

   @BindView(R.id.img_delivery_type)
   ImageView ImgDeliveryType;
   // ---------------------------------------------------------------------------------------
   // Members
   // ---------------------------------------------------------------------------------------
   private Store mStore = null;
   private LatLng latLngCurrentLocation = null;
   private LatLng latLngDestinationPoint = null;
   private LatLng latLngPickUpPoint = null;
   private String pickUpAddress = "";
   private String destinationAddress = "";
   private double estimatedTime = 0;
   private double estimatedDistance = 0;
   private double estimatedCost = 0;
   private VehicleCategory vehicleCategory;
   private GoogleMap mMap;
   private User currentUser;
   private double reqTime = 1;

   private String orderId = null;
   private boolean isUpdate = false;
   private DeliveryOrder deliveryOrder;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_new_order);
      ButterKnife.bind(this);

      getExtras();

      new InitManager().init(AddNewOrderActivity.this);
   }

   public static void startMe(Context context, Store store, LatLng currentLocation,
                              LatLng destinationLocation, String orderId) {
      Intent intent = new Intent(context, AddNewOrderActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      intent.putExtra(Constants.BUNDLE_PARAMS_STORE_PLACE_ID, store.getPlaceId());
      intent.putExtra(Constants.BUNDLE_PARAMS_STORE_NAME, store.getPlaceName());
      intent.putExtra(Constants.BUNDLE_PARAMS_STORE_VICINITY, store.getVicinity());

      intent.putExtra(Constants.FIREBASE_KEY_ORDER_PICKUP_LAT, store.getLatLng().latitude);
      intent.putExtra(Constants.FIREBASE_KEY_ORDER_PICKUP_LNG, store.getLatLng().longitude);

      intent.putExtra(Constants.CURRENT_LOCATION_LAT, currentLocation.latitude);
      intent.putExtra(Constants.CURRENT_LOCATION_LNG, currentLocation.longitude);

      intent.putExtra(Constants.FIREBASE_KEY_ORDER_DESTINATION_LAT, destinationLocation.latitude);
      intent.putExtra(Constants.FIREBASE_KEY_ORDER_DESTINATION_LNG, destinationLocation.longitude);

      intent.putExtra(Constants.FIREBASE_KEY_ORDER_ESTIMATED_DISTANCE, store.getDistance());

      if (!TextUtils.isEmpty(orderId)) {
         intent.putExtra(Constants.ORDER_ID, orderId);
      }

      context.startActivity(intent);
   }

   private void getExtras() {
      Intent intent = getIntent();
      if (intent != null) {

         orderId = intent.getStringExtra(ORDER_ID);
         isUpdate = orderId != null;

         String placeId = intent.getStringExtra(Constants.BUNDLE_PARAMS_STORE_PLACE_ID);
         String placeName = intent.getStringExtra(Constants.BUNDLE_PARAMS_STORE_NAME);
         String vicinity = intent.getStringExtra(Constants.BUNDLE_PARAMS_STORE_VICINITY);
         double lat = intent.getDoubleExtra(Constants.FIREBASE_KEY_ORDER_PICKUP_LAT, 0);
         double lng = intent.getDoubleExtra(Constants.FIREBASE_KEY_ORDER_PICKUP_LNG, 0);
         latLngPickUpPoint = new LatLng(lat, lng);
         double distance = intent.getDoubleExtra(Constants.FIREBASE_KEY_ORDER_ESTIMATED_DISTANCE, 0);

         mStore = new Store();
         mStore.setPlaceName(placeName);
         mStore.setVicinity(vicinity);
         mStore.setDistance(distance);
         mStore.setLatLng(latLngPickUpPoint);
         mStore.setPlaceId(placeId);

         double currentLocationLat = intent.getDoubleExtra(Constants.CURRENT_LOCATION_LAT, 0);
         double currentLocationLng = intent.getDoubleExtra(Constants.CURRENT_LOCATION_LNG, 0);
         latLngCurrentLocation = new LatLng(currentLocationLat, currentLocationLng);
         double destinationLat = intent.getDoubleExtra(Constants.FIREBASE_KEY_ORDER_DESTINATION_LAT, 0);
         double destinationLng = intent.getDoubleExtra(Constants.FIREBASE_KEY_ORDER_DESTINATION_LNG, 0);

         latLngDestinationPoint = new LatLng(destinationLat, destinationLng);
      }
   }


   private void initUI() {

      if (isUpdate) {
         initUI(getString(R.string.ui_label_edit_order), true);
         buttonAddOrder.setText(R.string.ui_button_edit_order);

      } else {
         initUI(getString(R.string.ui_label_add_new_order), true);
      }

      textViewEstimatedDistance.setText(getString(R.string.distance_km, mStore.getDistance()));
      deliveryOrder = new DeliveryOrder();

      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);

      final int[] reqTimeArray = {1, 2, 4, 8};

      spinnerRequiredDeliveryTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            reqTime = reqTimeArray[position];
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });


      if (!isUpdate) {
         // ---------------------------------------------------------------------------------------
         // PickUp Address
         // ---------------------------------------------------------------------------------------
         pickUpAddress = MyUtility.getAddress(getApplicationContext(),
               mStore.getLatLng().latitude, mStore.getLatLng().longitude);
         textViewPickUpAddress.setText(mStore.getPlaceName());

         // ---------------------------------------------------------------------------------------
         // Destination Address
         // ---------------------------------------------------------------------------------------
         destinationAddress = MyUtility.getAddress(getApplicationContext(),
               latLngDestinationPoint.latitude, latLngDestinationPoint.longitude);
         textViewDestinationAddress.setText(destinationAddress);
      } else {
         loadOrder();
      }

      vehicleCategory = settings.vehicleCategories.get(0);

      ArrayAdapter<String> adapter = new ArrayAdapter(AddNewOrderActivity.this,
            android.R.layout.simple_spinner_item, settings.getCategoriesNames());

      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      spDeliveryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            vehicleCategory = settings.vehicleCategories.get(position);
            Double price = vehicleCategory.getBaseFare() + mStore.getDistance() * vehicleCategory.getFarePerKm();
            String description = getString(R.string.price) + " : " + price + " " + settings.getCurrencyEn();
            if (vehicleCategory.getCategory().equalsIgnoreCase("Bicycle")) {
               ImgDeliveryType.setImageResource(R.drawable.bicycle);
               description = description + "\n " + getString(R.string.bicy_desc);
            }
            if (vehicleCategory.getCategory().equalsIgnoreCase("Scooter")) {
               ImgDeliveryType.setImageResource(R.drawable.scotter);
               description = description + "\n " + getString(R.string.scooter_desc);

            }
            if (vehicleCategory.getCategory().equalsIgnoreCase("Car")) {
               ImgDeliveryType.setImageResource(R.drawable.car);
               description = description + "\n " + getString(R.string.car_desc);

            }
            txtDeliveryDesc.setText(description);
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });

      spDeliveryType.setAdapter(adapter);

      Object[] DataTransfer = new Object[3];
      DataTransfer[0] = AddNewOrderActivity.this;
      DataTransfer[1] = mStore;
      DataTransfer[2] = 0;

      GetPlaceDetailsData getPlaceDetailsData = new GetPlaceDetailsData(settings.getGoogleApiKey());
      getPlaceDetailsData.execute(DataTransfer);
   }

   private void loadOrder() {

      // ---------------------------------------------------------------------------------------
      // Reading order data
      // ---------------------------------------------------------------------------------------
      MyUtility.getOrdersNodeRef().child(orderId).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
               deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);


               textViewPickUpAddress.setText(deliveryOrder.getStoreName());
               textViewDestinationAddress.setText(deliveryOrder.getDestinationAddress());
               //textViewEstimatedCost.setText(String.valueOf(deliveryOrder.getEstimatedCost()));
               textViewEstimatedDistance.setText(String.valueOf(deliveryOrder.getDistanceToClient()));
               textViewEstimatedTime.setText(String.valueOf(deliveryOrder.getEstimatedTime()));
               textInputLayoutInstructions.getEditText().setText(String.valueOf(deliveryOrder.getInstructions()));

               latLngPickUpPoint = deliveryOrder.getLatLngPickUpPoint();
               latLngDestinationPoint = deliveryOrder.getLatLngDestinationPoint();
               // ---------------------------------------------------------------------------------------
               // Show PickUp and Destination points on Map
               // ---------------------------------------------------------------------------------------
               showPickUpAndDestination();
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
         }
      });

   }

   private void updateEstimatedCost() {
      currentUser.getFareModel().setVehicleCategoryData(vehicleCategory);
      currentUser.getFareModel().calculateFares(estimatedDistance, estimatedTime);
      estimatedCost = currentUser.getFareModel().getUserTripCost();

      //textViewEstimatedCost.setText(String.valueOf(estimatedCost) + " " + settings.getCurrencyEn());
   }


   private void showPickUpAndDestination() {

      mMap.clear();

      List<Marker> pathMarkers = new ArrayList<>();
      LatLngBounds.Builder builder = new LatLngBounds.Builder();

      MarkerOptions options1 = new MarkerOptions()
            .position(latLngPickUpPoint.toGmsLatLng());
      Marker m1 = mMap.addMarker(options1);

      MarkerOptions options2 = new MarkerOptions()
            .position(latLngDestinationPoint.toGmsLatLng());
      Marker m2 = mMap.addMarker(options2);

      pathMarkers.add(m1);
      pathMarkers.add(m2);

      for (Marker marker : pathMarkers) {
         builder.include(marker.getPosition());
      }
      LatLngBounds bounds = builder.build();

      int padding = 100; // offset from edges of the map in pixels
      CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
      mMap.animateCamera(cu, Constants.MAP_SPEED, null);
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
      showPickUpAndDestination();
      // processDeliveryOrder();
   }

   private void processDeliveryOrder() {
      // ---------------------------------------------------------------------------------------
      // Show PickUp and Destination points on Map
      // ---------------------------------------------------------------------------------------
      showPickUpAndDestination();


      List<Store> storeList = new ArrayList<>();
      storeList.add(mStore);

      // ---------------------------------------------------------------------------------------
      // Estimated Distance and Time
      // ---------------------------------------------------------------------------------------
      GetDistanceAndTimeData getDistanceAndTimeData = new GetDistanceAndTimeData(storeList,
            latLngCurrentLocation, settings.getGoogleApiKey());


      // ---------------------------------------------------------------------------------------
      // Loading spinner cartegories
      // ---------------------------------------------------------------------------------------
      vehicleCategory = settings.vehicleCategories.get(0);


      ArrayAdapter<String> adapter = new ArrayAdapter(AddNewOrderActivity.this,
            android.R.layout.simple_spinner_item, settings.getCategoriesNames());

      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      spinnerVehicleCategories.setAdapter(adapter);

      spinnerVehicleCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            vehicleCategory = settings.vehicleCategories.get(position);


            updateEstimatedCost();
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {

         }
      });
      Object[] DataTransfer = new Object[2];
      DataTransfer[0] = AddNewOrderActivity.this;
      DataTransfer[1] = getApplicationContext();
      // ---------------------------------------------------------------------------------------
      // Calculating estimated Distance and Time
      // ---------------------------------------------------------------------------------------
      getDistanceAndTimeData.execute(DataTransfer);
   }

   public void setEstimatedDistanceAndTime(double distance, double time) {

      // Estimated Distance
      estimatedDistance = distance;
      //textViewEstimatedDistance.setText(String.valueOf(distance) + " " + getString(R.string.km));

      // Estimated Time
      estimatedTime = time;
      textViewEstimatedTime.setText(getString(R.string.distance_min, time));
   }

   public void setListEstimatedDistanceAndTime(List<Double> distance, List<Double> time) {

   }

   @OnClick(R.id.buttonAddOrder)
   public void onLinearLayoutAddOrderClick() {

      if (TextUtils.isEmpty(textInputLayoutInstructions.getEditText().getText().toString())) {
         Toast.makeText(this, getString(R.string.ui_message_error_invalid_delivery_instructions), Toast.LENGTH_SHORT).show();
         return;
      }

      if (deliveryOrder.getEstimatedTime() > (reqTime * 60)) {
         Toast.makeText(this, getString(R.string.ui_message_error_insufficient_expected_time),
               Toast.LENGTH_SHORT).show();
         return;
      }

      final String promoCode = editTextPromoCode.getText().toString();

      if (TextUtils.isEmpty(promoCode)) {
         if (!isUpdate) {

            MyUtility.getCountersNodeRef().addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  // Getting new order number
                  final int order_number = MyUtility.readOrderNumber(dataSnapshot);
                  saveOrder(order_number, isUpdate);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                  showMessage(getString(R.string.ui_message_error_adding_new_order));
               }
            });
         } else {
            saveOrder(deliveryOrder.getOrderNumber(), isUpdate);
         }
      } else {
         MyUtility.getPromoCodesNodeRef()
               .child(promoCode)
               .addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     logI(TAG, "dataSnapshot: " + dataSnapshot.toString());

                     final PromoCode promoCode1 = dataSnapshot.getValue(PromoCode.class);

                     if (dataSnapshot.getValue() == null) {
                        Toast.makeText(AddNewOrderActivity.this,
                              getString(R.string.ui_notifications_invalid_promo_code),
                              Toast.LENGTH_SHORT).show();
                     } else {

                        MyUtility.getUserOrdersNodeRef()
                              .child(currentUser.getUID())
                              .addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<DeliveryOrder> deliveryOrders = new ArrayList<>();
                                    for (DataSnapshot ds :
                                          dataSnapshot.getChildren()) {
                                       DeliveryOrder order = ds.getValue(DeliveryOrder.class);
                                       if (order.isCompleted() || order.isDelivered()) {
                                          deliveryOrders.add(order);
                                       }
                                    }

                                    logI(TAG, "promoCode1.Id = " + promoCode1.getId());
                                    int ret = promoCode1.validatePromotCode(currentUser, deliveryOrders);

                                    promoCode1.printCodeString(ret);

                                    if (ret == PromoCode.PROMO_CODE_VALID) {
                                       deliveryOrder.setPromoCode(promoCode);
                                       deliveryOrder.setPromoCodeDiscount(promoCode1.getDiscountPercent());

                                       if (!isUpdate) {
                                          MyUtility.getCountersNodeRef()
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                      // Getting new order number
                                                      final int order_number = MyUtility.readOrderNumber(dataSnapshot);
                                                      saveOrder(order_number, isUpdate);
                                                   }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError databaseError) {
                                                      showMessage(getString(R.string.ui_message_error_adding_new_order));
                                                   }
                                                });
                                       } else {
                                          saveOrder(deliveryOrder.getOrderNumber(), isUpdate);
                                       }
                                    } else {
                                       Toast.makeText(AddNewOrderActivity.this,
                                             getString(R.string.ui_notifications_invalid_promo_code),
                                             Toast.LENGTH_SHORT).show();
                                    }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {
                                 }
                              });
                     }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
                  }
               });
      }


   }

   private void saveOrder(int orderNumber, boolean isUpdate) {
      if (!isUpdate) {
         deliveryOrder.setId(String.valueOf(orderNumber));
         deliveryOrder.setOrderNumber(orderNumber);
      }

      String requestTime = DateTimeUtil.getCurrentDateTime();
      deliveryOrder.setOrderRequestTime(requestTime);
      deliveryOrder.setOrderStatus(Constants.DELIVERY_ORDER_STATUS_NEW_ORDER);
      deliveryOrder.setClientId(MyUtility.getCurrentUserUID());
      deliveryOrder.setLatLngPickUpPoint(latLngPickUpPoint);
      deliveryOrder.setPickUpAddress(pickUpAddress);
      deliveryOrder.setLatLngDestinationPoint(latLngDestinationPoint);
      deliveryOrder.setDestinationAddress(destinationAddress);
      deliveryOrder.setInstructions(textInputLayoutInstructions.getEditText().getText().toString());
      deliveryOrder.setEstimatedCost(estimatedCost);
      deliveryOrder.setStoreName(mStore.getPlaceName());
      deliveryOrder.setRestaurant(mStore.isRestaurant());
      deliveryOrder.setDistanceToClient(mStore.getDistance());
      deliveryOrder.setInternationalPhoneNumber(mStore.getInternationalphoneNumber());
      deliveryOrder.setPhoneNumber(mStore.getPhoneNumber());
      deliveryOrder.setEstimatedTime(estimatedTime);
      deliveryOrder.setClientName(currentUser.getFullName());
      deliveryOrder.setVehicleCategory(vehicleCategory.getId());
      // Saving new order
      deliveryOrder.saveOrder();

      if (!isUpdate) {
         // Showing message of new order added
         showMessage(getString(R.string.ui_message_new_order_added));
      } else {
         // Showing message of new order added
         showMessage(getString(R.string.ui_message_new_order_updated));
      }

      // Moving to the main activity
      ClientHomeActivity.startMe(getApplicationContext());
      finish();
   }

   @Override
   public void updateStoreData(Store store, int pos) {
      mStore.setInternationalphoneNumber(store.getInternationalphoneNumber());
      mStore.setPhoneNumber(store.getPhoneNumber());
      mStore.setTypes(store.getTypes());
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;
      initUI();
   }

}
