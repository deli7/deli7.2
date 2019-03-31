package com.delivame.delivame.deliveryman.activities.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.common.CurrentOrdersActivity;
import com.delivame.delivame.deliveryman.activities.common.Messaging.ViewSupportMessageActivity;
import com.delivame.delivame.deliveryman.activities.common.OrderActivity;
import com.delivame.delivame.deliveryman.activities.common.ProfileActivity;
import com.delivame.delivame.deliveryman.activities.common.SupportActivity;
import com.delivame.delivame.deliveryman.models.DeliveryOffer;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.PublicMessage;
import com.delivame.delivame.deliveryman.models.ScreenBox;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.delivame.delivame.deliveryman.utilities.NotificationsHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.delivame.delivame.deliveryman.utilities.Constants.ORDER_ID;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG3;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class ClientHomeActivity extends ClientBaseActivity implements InitManager.InitManagerListener {

   @BindView(R.id.recyclerView)
   RecyclerView recyclerView;
   @BindView(R.id.imageViewLanguage)
   ImageView imageViewLanguage;
   private String startTime;

   private HashMap<String, CountDownTimer> orderTimers = new HashMap<>();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_client_home);
      ButterKnife.bind(this);

      if (LocaleHelper.isLanguageEnglish(getApplicationContext())) {
         imageViewLanguage.setImageResource(R.drawable.aricon);
      } else {
         imageViewLanguage.setImageResource(R.drawable.enicon);
      }

      imageViewLanguage.setOnClickListener(v -> {
//         if (LanguageHelper.isLanguageEnglish(getApplicationContext())) {
//            LanguageHelper.setLocaleArabic(getApplicationContext());
//         } else {
//            LanguageHelper.setLocaleEnglish(getApplicationContext());
//         }
//
//         IntroWizardActivity.startMe(getApplicationContext());
//         finish();

         if (LocaleHelper.isLanguageEnglish(getApplicationContext())) {
            LocaleHelper.setLocale(this, "ar");
         } else {
            LocaleHelper.setLocale(this, "en_us");
         }

         recreate();
      });

      startTime = DateTimeUtil.getCurrentDateTime();

      listenToPublicMessages();

      new InitManager().init(ClientHomeActivity.this);
   }

   private void listenToPublicMessages() {

      MyUtility.getPublicMessagesRef().addChildEventListener(new ChildEventListener() {

         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            PublicMessage publicMessage = dataSnapshot.getValue(PublicMessage.class);
            if (DateTimeUtil.isDate1AfterDate2(publicMessage.getId(), startTime)) {
               if (publicMessage.getTo().equals("ALL") || publicMessage.getTo().equals("ALL_USERS")) {
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

   private void initUI() {
      initUI(getString(R.string.ui_activity_title_home_page), false);
      List<ScreenBox> screenBoxes = prepareScreenBoxes();
      initGridRecyclerView(recyclerView, screenBoxes);
      monitorOrders();
   }

   @Override
   protected void onResume() {
      super.onResume();
   }

   public static void startMe(Context context) {
      Intent intent = new Intent(context, ClientHomeActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      context.startActivity(intent);
   }

   private List<ScreenBox> prepareScreenBoxes() {

      List<ScreenBox> screenBoxList = new ArrayList<>();
      screenBoxList.add(new ScreenBox(getString(R.string.ui_button_new_order),
            getString(R.string.subtitle_add_new_order),
            R.drawable.neworder1,
            SelectBusinessTypeActivity.class, null));

      Bundle bundle = new Bundle();
      bundle.putInt(Constants.ORDER_LIST_TYPE, Constants.ORDER_LIST_TYPE_MY_ORDERS);

      screenBoxList.add(new ScreenBox(getString(R.string.ui_label_current_orders),
            getString(R.string.ui_label_view_current_orders),
            R.drawable.myorders1,
            CurrentOrdersActivity.class, bundle));

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

   boolean orderHasTimer(String orderId) {
      return (orderTimers.get(orderId) != null);

   }

   void startOrderTimer(final DeliveryOrder deliveryOrder) {
      CountDownTimer countDownTimer = new CountDownTimer(settings.getNewOrderLifetime(), 1000) {
         @Override
         public void onTick(long millisUntilFinished) {
         }

         @Override
         public void onFinish() {
            deliveryOrder.cancelOrder();
            Toast.makeText(ClientHomeActivity.this, getString(R.string.ui_notifcation_order_has_no_activity),
                  Toast.LENGTH_SHORT).show();
         }
      };

      orderTimers.put(deliveryOrder.getId(), countDownTimer);

      countDownTimer.start();
   }

   void stopOrderTimer(DeliveryOrder deliveryOrder) {
      CountDownTimer countDownTimer = orderTimers.get(deliveryOrder.getId());
      countDownTimer.cancel();
   }

   private void monitorOrders() {

      MyUtility.getOrdersNodeRef().addChildEventListener(new ChildEventListener() {

         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            DeliveryOrder deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);
            if (deliveryOrder.isClientOrder()) {
               monitorOrderOffers(deliveryOrder);
               if (deliveryOrder.isNew() && !orderHasTimer(deliveryOrder.getId())) {
                  logI(TAG3, "Starting order timer for order #" + deliveryOrder.getId());
                  startOrderTimer(deliveryOrder);
               }
            }
         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            DeliveryOrder deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);
            if (deliveryOrder.isClientOrder()) {
               deliveryOrder.showOrderNotification(getApplicationContext(), currentUser);
               if (deliveryOrder.isDelivered()) {
                  DeliveryManFeedbackActivity.startMe(getApplicationContext(), deliveryOrder.getId());
               }

               if (deliveryOrder.isAwarded()) {
                  if (orderHasTimer(deliveryOrder.getId())) {
                     logI(TAG3, "Stopping order timer for order #" + deliveryOrder.getId());
                     stopOrderTimer(deliveryOrder);
                  }
               }
            }
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

      MyUtility.getOrdersNodeRef().addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
               int new_orders_count = 0;
               int in_progress_orders_count = 0;
               int delivered_progress_count = 0;

               for (DataSnapshot ds :
                     dataSnapshot.getChildren()) {
                  DeliveryOrder deliveryOrder = ds.getValue(DeliveryOrder.class);
                  if (deliveryOrder.isClientOrder()) {

                     if (deliveryOrder.isDelivered()) {
                        delivered_progress_count += 1;
                     }

                     if (deliveryOrder.isInProgress()) {
                        in_progress_orders_count += 1;
                     }

                     if (deliveryOrder.isNew()) {
                        new_orders_count += 1;
                     }
                  }
               }

               String status = "";
//                    if (new_orders_count > 0){
//                        status += getString(R.string.ui_string_you_have) +
//                                " " +
//                                new_orders_count +
//                                " " +
//                                getString(R.string.ui_string_new_orders) + "\n";
//                    }
//
//                    if (in_progress_orders_count > 0){
//                        status += getString(R.string.ui_string_you_have) +
//                                " " +
//                                in_progress_orders_count +
//                                " " +
//                                getString(R.string.ui_string_in_progress_orders) + "\n";
//                    }

               if (delivered_progress_count > 0) {
                  status += getString(R.string.ui_string_you_have) + " " +
                        delivered_progress_count + " " +
                        getString(R.string.ui_string_delivered_orders_awating_feedback) + "\n";
               }

               if (!TextUtils.isEmpty(status)) {

                  final AlertDialog alertDialog =
                        new AlertDialog.Builder(new ContextThemeWrapper(ClientHomeActivity.this,
                              R.style.AppCompatAlertDialogStyle)).create();
                  alertDialog.setTitle(getString(R.string.ui_dialog_title_orders_status));
                  alertDialog.setMessage(status);
                  alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        (dialog, which) -> alertDialog.dismiss());
                  alertDialog.show();
               }
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
         }
      });
   }

   private void monitorOrderOffers(final DeliveryOrder deliveryOrder) {
      MyUtility.getOffersNodeRef().child(deliveryOrder.getId())
            .addChildEventListener(new ChildEventListener() {

               @Override
               public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                  DeliveryOffer deliveryOffer = dataSnapshot.getValue(DeliveryOffer.class);

                  if (!deliveryOrder.isNew()) return;

                  if (!currentUser.isDeliveryMan() &&
                        deliveryOrder.getClientId().equals(MyUtility.getCurrentUserUID())) {

                     if (DateTimeUtil.isDate1AfterDate2(deliveryOffer.getOfferTime(), startTime)) {

                        Bundle bundle = new Bundle();
                        bundle.putString(ORDER_ID, deliveryOffer.getOrderId());

                        NotificationsHelper.showNotification(getApplicationContext(),
                              getString(R.string.ui_notification_new_offer_added),
                              getString(R.string.ui_notification_new_offer_added)
                                    + " - " + getString(R.string.ui_label_order_number) + ": " + deliveryOrder.getId(),
                              R.drawable.neworder,
                              OrderActivity.class, bundle);
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
}
