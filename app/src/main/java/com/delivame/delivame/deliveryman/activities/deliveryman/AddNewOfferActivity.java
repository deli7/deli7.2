package com.delivame.delivame.deliveryman.activities.deliveryman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.googleapis.GetDistanceAndTimeData;
import com.delivame.delivame.deliveryman.models.DeliveryOffer;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.models.VehicleCategory;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.LocationServiceManager;
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

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class AddNewOfferActivity extends DeliveryManBaseActivity implements
      OnMapReadyCallback,
      InitManager.InitManagerListener,
      LocationServiceManager.LocationServiceManagerListener,
      GetDistanceAndTimeData.GetDistanceAndTimeDataListener {


   @BindView(R.id.buttonAddOffer)
   Button buttonAddOffer;

   // --Commented out by Inspection (6/6/18, 8:51 AM):Store store = null;
   // --Commented out by Inspection (6/6/18, 8:51 AM):LatLng latLngCurrentLocation = null;
   private LatLng latLngDestinationPoint = null;
   private LatLng latLngPickUpPoint = null;
   // --Commented out by Inspection (6/6/18, 8:51 AM):String pickUpAddress = "";
   // --Commented out by Inspection (6/6/18, 8:51 AM):String destinationAddress = "";
   private double estimatedTime = 0;
   private double orderDistance = 0;
   private double distanceToStore = 0;
   private double estimatedCost = 0;
   private Location mCurrentLocation;

   private boolean isUpdate = false;

   @BindView(R.id.linearLayout2)
   LinearLayout linearLayout2;

   @BindView(R.id.textViewPickUpAddress)
   TextView textViewPickUpAddress;
   @BindView(R.id.textViewDestinationAddress)
   TextView textViewDestinationAddress;
   @BindView(R.id.textViewEstimatedDistance)
   TextView textViewDistanceToClient;
   @BindView(R.id.textViewEstimatedCost)
   TextView textViewEstimatedCost;
   @BindView(R.id.textInputLayoutOfferValue)
   TextInputLayout textInputLayoutOfferValue;
   @BindView(R.id.textInputLayoutInstructions)
   TextInputLayout textInputLayoutOfferComment;
   @BindView(R.id.textViewDeliveryToStoreDistance)
   TextView textViewDistanceToStore;

   private VehicleCategory vehicleCategory;
   private GoogleMap mMap;
   private User currentUser;
   private String orderId;
   private DeliveryOrder deliveryOrder;
   private String offerId = null;

   private DeliveryOffer deliveryOffer;

   // --Commented out by Inspection (6/6/18, 8:51 AM):Double distanceToClient;


   // ---------------------------------------------------------------------------------------
   // UI Binding


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_new_offer);
      ButterKnife.bind(this);
      getExtras();
      new InitManager().init(this);
   }

   public static void startMe(Context context, String orderId, String offerId) {
      Intent intent = new Intent(context, AddNewOfferActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      intent.putExtra(Constants.ORDER_ID, orderId);
      if (!TextUtils.isEmpty(offerId)) {
         intent.putExtra(Constants.OFFER_ID, offerId);
      }

      context.startActivity(intent);
   }

   private void getExtras() {
      Intent intent = getIntent();
      if (intent != null) {
         orderId = intent.getStringExtra(Constants.ORDER_ID);
         offerId = intent.getStringExtra(Constants.OFFER_ID);
         if (offerId != null) {
            isUpdate = true;
         }
      }
   }

   private void initUI() {
      initUI(getString(R.string.ui_label_add_new_offer), true);

      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);

      deliveryOffer = new DeliveryOffer();

      logI(TAG, "isUpdate: " + isUpdate);

      if (!isUpdate) {
         // ---------------------------------------------------------------------------------------
         // PickUp Address
         // ---------------------------------------------------------------------------------------
         textViewPickUpAddress.setText(deliveryOrder.getStoreName());

         // ---------------------------------------------------------------------------------------
         // Destination Address
         // ---------------------------------------------------------------------------------------
         textViewDestinationAddress.setText(deliveryOrder.getDestinationAddress());

         new LocationServiceManager(AddNewOfferActivity.this).initLocationService();


      } else {
         textViewPickUpAddress.setText(deliveryOrder.getStoreName());

         // ---------------------------------------------------------------------------------------
         // Destination Address
         // ---------------------------------------------------------------------------------------
         textViewDestinationAddress.setText(deliveryOrder.getDestinationAddress());

         loadOffer();
      }
   }


   private void getDistanceToStore() {

      MyUtility.getOrdersNodeRef().child(orderId).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
               deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);


               List<LatLng> sources = new ArrayList<>();
               sources.add(LatLng.fromLocation(mCurrentLocation));
               sources.add(deliveryOrder.getLatLngPickUpPoint());

               List<LatLng> destinations = new ArrayList<>();
               destinations.add(deliveryOrder.getLatLngPickUpPoint());
               destinations.add(deliveryOrder.getLatLngDestinationPoint());


               GetDistanceAndTimeData getDistanceAndTimeData =
                     new GetDistanceAndTimeData(sources, destinations, settings.getGoogleApiKey());


               Object[] DataTransfer = new Object[3];
               DataTransfer[0] = AddNewOfferActivity.this;
               DataTransfer[1] = getApplicationContext();

               getDistanceAndTimeData.execute(DataTransfer);
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });


   }

   private void loadOffer() {
      MyUtility.getOffersNodeRef().child(offerId).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            deliveryOffer = dataSnapshot.getValue(DeliveryOffer.class);

            textInputLayoutOfferValue.getEditText().setText(String.valueOf(Math.ceil(deliveryOffer.getOfferValue())));
            textInputLayoutOfferComment.getEditText().setText(deliveryOffer.getOfferComment());
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });
   }

   private void updateEstimatedCost() {
      currentUser.getFareModel().setVehicleCategoryData(vehicleCategory);
      currentUser.getFareModel().calculateFares(orderDistance, estimatedTime);
      estimatedCost = currentUser.getFareModel().getUserTripCost();

      textViewEstimatedCost.setText(estimatedCost + " " + settings.getCurrency(getApplicationContext()));
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

   private void loadOrderAndOffers() {
      // ---------------------------------------------------------------------------------------
      // Reading order data
      // ---------------------------------------------------------------------------------------
      MyUtility.getOrdersNodeRef().child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
               deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);


               checkOrder(deliveryOrder);


               initUI();

            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });
   }

   private void checkOrder(final DeliveryOrder deliveryOrder) {

      MyUtility.getUserOffersNodeRef().child(currentUser.getUID())
            .addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  int activeOrdersCount = 0;
                  for (DataSnapshot ds : dataSnapshot.getChildren()) {
                     DeliveryOffer deliveryOffer = ds.getValue(DeliveryOffer.class);

                     if (deliveryOffer.getOrderId().equals(orderId)) {
                        if (!isUpdate) {
                           errorAlreadyAddedOffer();
                        }
                     }

                     if (deliveryOffer.isInProgress()) {
                        activeOrdersCount += 1;

                        if (activeOrdersCount > settings.getMaxConcurrentOffers()) {
                           errorOrderInProgress();
                        }
                     }
                  }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
            });
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
   }

   private void processDeliveryOrder() {
      latLngPickUpPoint = deliveryOrder.getLatLngPickUpPoint();
      latLngDestinationPoint = deliveryOrder.getLatLngDestinationPoint();

      // ---------------------------------------------------------------------------------------
      // Show PickUp and Destination points on Map
      // ---------------------------------------------------------------------------------------
      showPickUpAndDestination();

      // ---------------------------------------------------------------------------------------
      // Loading spinner cartegories
      // ---------------------------------------------------------------------------------------
      vehicleCategory = currentUser.getVehicleCategory();
      textViewDistanceToClient.setText(String.valueOf(deliveryOrder.getDistanceToClient()));
      textViewEstimatedCost.setText(String.valueOf(deliveryOrder.getEstimatedCost()));
      textViewDistanceToClient.setText(deliveryOrder.getDistanceToClient() + " " + getString(R.string.km));
      //  textViewDistanceToStore.setText(String.valueOf(distanceToStore) + " " + getString(R.string.km));

      getDistanceToStore();

   }

   @Override
   public void setEstimatedDistanceAndTime(double distance, double time) {


      updateEstimatedCost();
   }

   @Override
   public void setListEstimatedDistanceAndTime(List<Double> distance, List<Double> time) {

      distanceToStore = distance.get(0);
      textViewDistanceToStore.setText(distanceToStore + " " + getString(R.string.km));

      orderDistance = deliveryOrder.getDistanceToClient();
//        orderDistance = distance.get(1);
//        textViewDistanceToClient.setText(String.valueOf(orderDistance) + " " + getString(R.string.km));

      estimatedTime = deliveryOrder.getEstimatedTime();

      updateEstimatedCost();
   }

   @OnClick(R.id.buttonAddOffer)
   public void onlinearLayoutAddOfferClick() {

      if (TextUtils.isEmpty(textInputLayoutOfferValue.getEditText().getText().toString())) {
         Toast.makeText(this, getString(R.string.ui_message_error_invalid_offer_value),
               Toast.LENGTH_SHORT).show();
         return;
      }

      double offerValue = Math.ceil(Double.parseDouble(textInputLayoutOfferValue.getEditText().getText().toString()));
      double companyPercent = currentUser.getFareModel().getCompanyPercent();
      double userCredit = currentUser.getFareModel().getAccountPoints();

      estimatedCost = Math.ceil(estimatedCost);

      if (offerValue > (estimatedCost + settings.getDeliveryMenOffersRange()) ||
            offerValue < estimatedCost) {

         String err = getString(R.string.ui_message_error_offer_outside_range);
         err += "\n" + getString(R.string.min_range) + ": " + estimatedCost;
         err += " - " + getString(R.string.max_range) + ": " +
               (estimatedCost + settings.getDeliveryMenOffersRange());
         // Toast.makeText(this, err , Toast.LENGTH_SHORT).show();

         final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(AddNewOfferActivity.
               this, R.style.AppCompatAlertDialogStyle)).create();
         alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
         alertDialog.setMessage(err);
         alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
               (dialog, which) -> alertDialog.dismiss());
         alertDialog.show();

         return;
      }

      DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
         switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
               addOffer();
               break;

            case DialogInterface.BUTTON_NEGATIVE:
               //No button clicked
               break;
         }
      };


      AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
            R.style.AppCompatAlertDialogStyle));
      builder.setMessage(getString(R.string.ui_dialog_are_you_sure_add_new_offer))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener).show();

   }

   private void addOffer() {

      if (checkDeliveryManCredit()) {

         //currentUser.updateCredit(companyEarning);

         if (!isUpdate) {
            MyUtility.getCountersNodeRef().addListenerForSingleValueEvent(new ValueEventListener() {

               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  // Getting new order number
                  final int offerNumber = MyUtility.readOfferNumber(dataSnapshot);
                  String offerTime = DateTimeUtil.getCurrentDateTime();

                  deliveryOffer.setDeliveryManPhone(currentUser.getPhoneNumber());
                  deliveryOffer.setOfferNumber(String.valueOf(offerNumber));
                  deliveryOffer.setOfferValue(Double.parseDouble(textInputLayoutOfferValue.getEditText()
                        .getText().toString()));
                  deliveryOffer.setOfferTime(offerTime);
                  deliveryOffer.setOrderId(orderId);
                  deliveryOffer.setOfferComment(textInputLayoutOfferComment.getEditText()
                        .getText().toString());
                  deliveryOffer.setDeliveryManName(currentUser.getFullName());
                  deliveryOffer.setDeliveryManId(currentUser.getUID());
                  deliveryOffer.setDistanceToStore(distanceToStore);
                  deliveryOffer.saveOffer();

                  // Showing message of new order added
                  showMessage(getString(R.string.ui_message_new_offer_added));

                  // Moving to the main activity
                  DeliveryManHomeActivity.startMe(getApplicationContext());
                  finish();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                  showMessage(getString(R.string.ui_message_error_adding_new_order));
               }
            });
         } else {
            String offerTime = DateTimeUtil.getCurrentDateTime();

            deliveryOffer.setDeliveryManPhone(currentUser.getPhoneNumber());
            deliveryOffer.setOfferValue(Double.parseDouble(textInputLayoutOfferValue.getEditText()
                  .getText().toString()));
            deliveryOffer.setOfferTime(offerTime);
            deliveryOffer.setOrderId(orderId);
            deliveryOffer.setOfferComment(textInputLayoutOfferComment.getEditText().getText().toString());
            deliveryOffer.setDeliveryManName(currentUser.getFullName());
            deliveryOffer.setDeliveryManId(currentUser.getUID());
            deliveryOffer.saveOffer();

            // Showing message of new order added
            showMessage(getString(R.string.ui_message_new_offer_updated));

            // Moving to the main activity
            DeliveryManHomeActivity.startMe(getApplicationContext());
            finish();
         }
      } else {
         final AlertDialog alertDialog = new AlertDialog.Builder(
               new ContextThemeWrapper(AddNewOfferActivity.this, R.style.AppCompatAlertDialogStyle)).create();
         alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
         alertDialog.setMessage(getString(R.string.ui_dialog_insufficient_credit) + ". " +
               getString(R.string.ui_label_current_credit) + ": " + currentUser.getFareModel().getAccountPoints());
         alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
               (dialog, which) -> alertDialog.dismiss());
         alertDialog.show();
      }
   }

   private boolean checkDeliveryManCredit() {
      double offerValue = Double.parseDouble(textInputLayoutOfferValue.getEditText()
            .getText().toString());
      double companyPercent = currentUser.getFareModel().getCompanyPercent();
      double userCredit = currentUser.getFareModel().getAccountPoints();

      double orderCredit = companyPercent * offerValue / 100;
      double remainingCredit = userCredit - orderCredit;
      return remainingCredit > settings.getLowerCreditAllowence();
   }


   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      loadOrderAndOffers();
   }

   private void errorAlreadyAddedOffer() {
      AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(AddNewOfferActivity.this,
            R.style.AppCompatAlertDialogStyle)).create();
      alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
      alertDialog.setMessage(getString(R.string.ui_dialog_aready_added_offer));
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
            (dialog, which) -> finish());
      alertDialog.show();
   }

   private void errorOrderIsAwarded() {
      AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,
            R.style.AppCompatAlertDialogStyle)).create();
      alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
      alertDialog.setMessage(getString(R.string.ui_dialog_order_already_awarded));
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), (dialog, which) -> finish());
      alertDialog.show();
   }

   private void errorOrderInProgress() {
      AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,
            R.style.AppCompatAlertDialogStyle)).create();
      alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
      alertDialog.setMessage(getString(R.string.ui_dialog_you_already_have_other_order));
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
            (dialog, which) -> finish());
      alertDialog.show();
   }

   private boolean isLocationSet = false;

   @Override
   public void processLocation(Location location) {
      mCurrentLocation = location;

      if (!isLocationSet) {
         isLocationSet = true;
         processDeliveryOrder();
      }

   }
}
