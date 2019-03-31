package com.delivame.delivame.deliveryman.activities.deliveryman;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.IntroWizardActivity;
import com.delivame.delivame.deliveryman.activities.common.CurrentOrdersActivity;
import com.delivame.delivame.deliveryman.activities.common.Messaging.ViewSupportMessageActivity;
import com.delivame.delivame.deliveryman.activities.common.OrderActivity;
import com.delivame.delivame.deliveryman.activities.common.ProfileActivity;
import com.delivame.delivame.deliveryman.activities.common.SupportActivity;
import com.delivame.delivame.deliveryman.adapters.ScreenBoxesAdapter;
import com.delivame.delivame.deliveryman.googleapis.GetDistanceAndTimeData;
import com.delivame.delivame.deliveryman.models.DeliveryOffer;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.PublicMessage;
import com.delivame.delivame.deliveryman.models.ScreenBox;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;
import com.delivame.delivame.deliveryman.utilities.LocationServiceManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.delivame.delivame.deliveryman.utilities.NotificationsHelper;
import com.delivame.delivame.deliveryman.utilities.SharedPrefHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.delivame.delivame.deliveryman.utilities.Constants.OFFER_STATUS_AWARDED;
import static com.delivame.delivame.deliveryman.utilities.Constants.OFFER_STATUS_CANCELLED;
import static com.delivame.delivame.deliveryman.utilities.Constants.OFFER_STATUS_FINISHED;
import static com.delivame.delivame.deliveryman.utilities.Constants.ORDER_ID;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.saveCurrentLocation;

public class DeliveryManHomeActivity extends
      DeliveryManBaseActivity implements InitManager.InitManagerListener,
      LocationServiceManager.LocationServiceManagerListener,
      GetDistanceAndTimeData.GetDistanceAndTimeDataListener {


   // ---------------------------------------------------------------------------------------
   // UI
   // ---------------------------------------------------------------------------------------
   @BindView(R.id.recyclerView)
   RecyclerView recyclerView;
   @BindView(R.id.imageViewLanguage)
   ImageView imageViewLanguage;

   private String startTime;


   // ---------------------------------------------------------------------------------------
   // Members
   // ---------------------------------------------------------------------------------------
   DeliveryOrder activeOrder;
   private List<ScreenBox> screenBoxes;
   private ScreenBoxesAdapter adapter;
   private LocationServiceManager locationServiceManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_delivery_man_home);
      ButterKnife.bind(this);


      if (LocaleHelper.isLanguageEnglish(getApplicationContext())) {
         imageViewLanguage.setImageResource(R.drawable.aricon);
      } else {
         imageViewLanguage.setImageResource(R.drawable.enicon);
      }

      imageViewLanguage.setOnClickListener(v -> {
         if (LocaleHelper.isLanguageEnglish(getApplicationContext())) {
            LocaleHelper.setLocale(this, "ar");
         } else {
            LocaleHelper.setLocale(this, "en_us");
         }

         IntroWizardActivity.startMe(getApplicationContext());
         finish();
      });
      startTime = DateTimeUtil.getCurrentDateTime();

      listenToPublicMessages();

      new InitManager().init(DeliveryManHomeActivity.this);
   }

   private void listenToPublicMessages() {


      MyUtility.getPublicMessagesRef().addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            PublicMessage publicMessage = dataSnapshot.getValue(PublicMessage.class);
            if (publicMessage.getTo().equals("ALL") || publicMessage.getTo().equals("ALL_DELIVERY_MEN")) {
               if (DateTimeUtil.isDate1AfterDate2(publicMessage.getId(), startTime)) {
                  Bundle bundle = new Bundle();
                  bundle.putBoolean("IS_PUBLIC", true);
                  bundle.putString("MESSAGE_ID", publicMessage.getId());

                  NotificationsHelper.showNotification(getApplicationContext(), publicMessage.getSubject(),
                        publicMessage.getBody(), R.drawable.logo1, ViewSupportMessageActivity.class, bundle);
               }

            }
         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

         }

         @Override
         public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });
   }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (locationServiceManager != null) {
//            locationServiceManager.initLocationService();
//        }
//    }

   @Override
   protected void onResume() {
      super.onResume();
   }

   @Override
   protected void onPause() {
      super.onPause();
      if (locationServiceManager != null) {

         locationServiceManager.stopLocationUpdates();

      }
   }

   private void initUI() {
      initUI(getString(R.string.ui_activity_title_home_page), false);

      //toolbar.setVisibility(View.GONE);
      locationServiceManager = new LocationServiceManager(DeliveryManHomeActivity.this);
      locationServiceManager.initLocationService();

      screenBoxes = prepareScreenBoxes();
      adapter = initGridRecyclerView(recyclerView, screenBoxes);
      locationServiceManager.initLocationService();
   }


   private void monitorOrders() {

      MyUtility.getOrdersNodeRef().addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

            activeOrder = dataSnapshot.getValue(DeliveryOrder.class);
            boolean newOrdersNotifications = SharedPrefHelper.getPrefBoolean(getApplicationContext(),
                  Constants.NEW_ORDERS_NOTIFICATIONS, true);

            if (newOrdersNotifications && activeOrder.isOrderAfterTime(startTime)) {
               if (currentLocation != null) {

                  List<LatLng> sources = new ArrayList<>();
                  sources.add(currentLocation);

                  List<LatLng> destinations = new ArrayList<>();
                  destinations.add(activeOrder.getLatLngPickUpPoint());

                  GetDistanceAndTimeData getDistanceAndTimeData = new GetDistanceAndTimeData(
                        sources, destinations, settings.getGoogleApiKey());

                  Object[] DataTransfer = new Object[3];
                  DataTransfer[0] = DeliveryManHomeActivity.this;
                  DataTransfer[1] = getApplicationContext();

                  getDistanceAndTimeData.execute(DataTransfer);
               }
            }
         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            activeOrder = dataSnapshot.getValue(DeliveryOrder.class);

            MyUtility.getUserOffersNodeRef().child(currentUser.getUID()).child(activeOrder.getId())
                  .addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        DeliveryOffer deliveryOffer = dataSnapshot.getValue(DeliveryOffer.class);

                        if (deliveryOffer != null) {

                           if (activeOrder.isInProgress()) {
                              if (activeOrder.isDeliveryManOrder()) {
                                 activeOrder.showOrderNotification(getApplicationContext(), currentUser);
                              }
                           }

                           if (activeOrder.isCancelled()) {

                              logI(TAG, "Order #" + activeOrder.getId() + " has been cancelled");

                              deliveryOffer.cancelOffer();

                              if (deliveryOffer.getDeliveryManId().equals(MyUtility.getCurrentUserUID())) {
                                 Bundle bundle = new Bundle();
                                 bundle.putString(ORDER_ID, activeOrder.getId());

                                 NotificationsHelper.showNotification(getApplicationContext(),
                                       getString(R.string.ui_notification_your_offer_is_expired),
                                       getString(R.string.ui_notification_your_offer_is_expired) + " - " + getString(R.string.ui_label_order_number) + ": "
                                             + activeOrder.getOrderNumber(),
                                       R.drawable.logo1,
                                       OrderActivity.class, bundle);

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
         public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });
   }

   private void monitorOffers() {
      MyUtility.getUserOffersNodeRef().child(MyUtility.getCurrentUserUID())
            .addChildEventListener(new ChildEventListener() {
               @Override
               public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                  if (dataSnapshot.getValue() == null) return;

                  final DeliveryOffer deliveryOffer = dataSnapshot.getValue(DeliveryOffer.class);

                  MyUtility.getOrdersNodeRef().child(deliveryOffer.getOrderId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                              if (dataSnapshot.getValue() == null) return;

                              DeliveryOrder activeOrder = dataSnapshot.getValue(DeliveryOrder.class);

                              switch (deliveryOffer.getOfferStatus()) {
                                 case OFFER_STATUS_AWARDED:
                                    updateCurrentOrderBox(activeOrder, false);

                                    SharedPrefHelper.writePrefBoolean(getApplicationContext(),
                                          Constants.NEW_ORDERS_NOTIFICATIONS, false);

                                    break;
                                 case OFFER_STATUS_FINISHED:
                                 case OFFER_STATUS_CANCELLED:
                                    SharedPrefHelper.writePrefBoolean(getApplicationContext(),
                                          Constants.NEW_ORDERS_NOTIFICATIONS, true);
                                    break;
                              }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                        });

               }

               @Override
               public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                  if (dataSnapshot.getValue() == null) return;

                  // Toast.makeText(DeliveryManHomeActivity.this, "Offer Changed", Toast.LENGTH_SHORT).show();
                  final DeliveryOffer deliveryOffer = dataSnapshot.getValue(DeliveryOffer.class);

                  MyUtility.getOrdersNodeRef().child(deliveryOffer.getOrderId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              DeliveryOrder activeOrder = dataSnapshot.getValue(DeliveryOrder.class);

                              switch (deliveryOffer.getOfferStatus()) {
                                 case OFFER_STATUS_AWARDED:

                                    updateCurrentOrderBox(activeOrder, true);

                                    listenToOrderChanges(activeOrder.getId());

                                    Bundle bundle = new Bundle();
                                    bundle.putString(ORDER_ID, activeOrder.getId());

                                    NotificationsHelper.showNotification(getApplicationContext(),
                                          "You have been awarded order #" + activeOrder.getId(),
                                          "",
                                          R.drawable.neworder,
                                          OrderActivity.class, bundle);

                                    SharedPrefHelper.writePrefBoolean(getApplicationContext(),
                                          Constants.NEW_ORDERS_NOTIFICATIONS, false);

                                    break;
                                 case OFFER_STATUS_FINISHED:
                                 case OFFER_STATUS_CANCELLED:
                                    SharedPrefHelper.writePrefBoolean(getApplicationContext(),
                                          Constants.NEW_ORDERS_NOTIFICATIONS, true);

                                    updateCurrentOrderBox(activeOrder, false);

                                    break;
                              }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                        });


               }

               @Override
               public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

               }

               @Override
               public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
            });
   }

   private void updateCurrentOrderBox(DeliveryOrder activeOrder, boolean b) {

      if (activeOrder == null) return;

      Bundle bundle = new Bundle();
      bundle.putString(Constants.ORDER_ID, activeOrder.getId());
      ScreenBox box;
      if (b) {
         box = new ScreenBox(getString(R.string.ui_label_view_my_current_orders),
               getString(R.string.ui_label_you_have_in_progress_orders),
               R.drawable.neworder1,
               OrderActivity.class, bundle);
      } else {
         Bundle bundle2 = new Bundle();
         bundle2.putInt(Constants.ORDER_LIST_TYPE, Constants.ORDER_LIST_TYPE_MY_ORDERS);

         box = new ScreenBox(getString(R.string.ui_label_view_my_current_orders),
               getString(R.string.ui_label_you_dont_have_orders),
               R.drawable.neworder1,
               CurrentOrdersActivity.class, bundle);
      }

      screenBoxes.set(1, box);
      adapter.notifyDataSetChanged();
   }


   public static void startMe(Context context) {
      Intent intent = new Intent(context, DeliveryManHomeActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }

   private List<ScreenBox> prepareScreenBoxes() {

      List<ScreenBox> screenBoxList = new ArrayList<>();

      Bundle bundle = new Bundle();
      bundle.putInt(Constants.ORDER_LIST_TYPE, Constants.ORDER_LIST_TYPE_NEW);

      screenBoxList.add(new ScreenBox(getString(R.string.ui_label_current_orders),
            getString(R.string.ui_label_view_current_orders),
            R.drawable.myorders1,
            CurrentOrdersActivity.class, bundle));

      Bundle bundle2 = new Bundle();
      bundle2.putInt(Constants.ORDER_LIST_TYPE, Constants.ORDER_LIST_TYPE_MY_ORDERS);

      screenBoxList.add(new ScreenBox(getString(R.string.ui_label_view_my_current_orders),
            getString(R.string.ui_label_view_my_current_orders),
            R.drawable.neworder1,
            CurrentOrdersActivity.class, bundle2));

      screenBoxList.add(new ScreenBox(getString(R.string.personal_profile),
            getString(R.string.subtitle_view_update_personal_profile),
            R.drawable.myprofile1,
            ProfileActivity.class, null));

      screenBoxList.add(new ScreenBox(getString(R.string.ui_label_support),
            getString(R.string.subtitle_get_support),
            R.drawable.support1,
            SupportActivity.class, null));

      return screenBoxList;
   }


   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      initUI();
   }

   private LatLng currentLocation = null;

   @Override
   public void processLocation(Location location) {

      saveCurrentLocation(getApplicationContext(), location);

      if (currentLocation == null) {
         currentLocation = LatLng.fromLocation(location);
         //monitorOffers();
         monitorOrders();
      } else {
         currentLocation = LatLng.fromLocation(location);
      }
   }

   @Override
   public void setEstimatedDistanceAndTime(double distance, double time) {
      //Toast.makeText(this, "Distance to Order: " + distance, Toast.LENGTH_SHORT).show();
      logI(TAG, "Distance to order: " + distance);

      if (activeOrder.isInProgress() && activeOrder.isDeliveryManOrder()) {
         updateCurrentOrderBox(activeOrder, false);
         SharedPrefHelper.writePrefBoolean(getApplicationContext(),
               Constants.NEW_ORDERS_NOTIFICATIONS, false);

      } else {
         if (activeOrder.isNew()) {
            if (distance < settings.getDistanceRangeToDelivery() / 1000) {
               activeOrder.showOrderNotification(getApplicationContext(), currentUser);
            }
         }
      }


   }

   @Override
   public void setListEstimatedDistanceAndTime(List<Double> distance, List<Double> time) {

   }

   public void ring(View view) {
      //NotificationsHelper.showNotification(getApplicationContext(),"","", R.drawable.neworder,
      // DeliveryManHomeActivity.class, null);
   }
}
