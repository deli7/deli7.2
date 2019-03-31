package com.delivame.delivame.deliveryman.activities.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.activities.client.AddNewOrderActivity;
import com.delivame.delivame.deliveryman.activities.client.ClientHomeActivity;
import com.delivame.delivame.deliveryman.activities.client.DeliveryManFeedbackActivity;
import com.delivame.delivame.deliveryman.activities.common.Chat.ChatActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.AddNewOfferActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.AddNewOrderReceiptActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.DeliveryManHomeActivity;
import com.delivame.delivame.deliveryman.adapters.OffersRecyclerAdapter;
import com.delivame.delivame.deliveryman.models.DeliveryOffer;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.PromoCode;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.ContactHelper;
import com.delivame.delivame.deliveryman.utilities.GoogleMapsHelper;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.delivame.delivame.deliveryman.utilities.Constants.ORDER_ID;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;


public class OrderActivity extends BaseActivity implements OnMapReadyCallback,
      InitManager.InitManagerListener,
      OffersRecyclerAdapter.OffersRecyclerAdapterListener,
      LocationServiceManager.LocationServiceManagerListener {


   @BindView(R.id.textViewPickUpAddress)
   TextView textViewPickUpAddress;
   @BindView(R.id.textViewDestinationAddress)
   TextView textViewDestinationAddress;
   @BindView(R.id.textViewOrderInstructions)
   TextView textViewOrderInstructions;
   @BindView(R.id.textViewEstimatedDistance)
   TextView textViewEstimatedDistance;
   @BindView(R.id.textViewEstimatedTime)
   TextView textViewEstimatedTime;
   @BindView(R.id.textViewEstimatedCost)
   TextView textViewEstimatedCost;
   @BindView(R.id.textViewOrderStatus)
   TextView textViewOrderStatus;
   @BindView(R.id.cardView)
   CardView cardView;
   @BindView(R.id.recyclerView)
   RecyclerView recyclerView;
   @BindView(R.id.textViewChatWithDeliveryMan)
   TextView textViewChatWithDeliveryMan;
   @BindView(R.id.textViewCallDeliveryMan)
   TextView textViewCallDeliveryMan;
   @BindView(R.id.textViewMakeOffer)
   TextView textViewMakeOffer;
   @BindView(R.id.textViewEditOrder)
   TextView textViewEditOrder;
   @BindView(R.id.textViewCancelOrder)
   TextView textViewCancelOrder;
   @BindView(R.id.textViewChatWithClient)
   TextView textViewChatWithClient;
   @BindView(R.id.textViewPickUpOrder)
   TextView textViewPickUpOrder;
   @BindView(R.id.textViewNavigateToPickup)
   TextView textViewNavigateToPickup;
   @BindView(R.id.textViewNavigateToDestination)
   TextView textViewNavigateToDestination;
   @BindView(R.id.textViewDeliverOrder)
   TextView textViewDeliverOrder;
   @BindView(R.id.textViewFinishOrder)
   TextView textViewFinishOrder;
   @BindView(R.id.textViewOrderNumber)
   TextView textViewOrderNumber;
   @BindView(R.id.recyclerViewFrame)
   FrameLayout recyclerViewFrame;
   @BindView(R.id.textViewOrderReceiptCost)
   TextView textViewOrderReceiptCost;
   @BindView(R.id.linearLayoutReceiptCost)
   LinearLayout linearLayoutReceiptCost;
   @BindView(R.id.textViewViewReceipt)
   TextView textViewViewReceipt;
   @BindView(R.id.textViewOrderDeliveryCost)
   TextView textViewOrderDeliveryCost;
   @BindView(R.id.linearLayoutCosts)
   LinearLayout linearLayoutCosts;
   private final List<DeliveryOffer> deliveryOfferList = new ArrayList<>();
   private OffersRecyclerAdapter adapter;

   private String orderId;
   private DeliveryOrder deliveryOrder;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_order);
      ButterKnife.bind(this);

      getExtras();

      new InitManager().init(OrderActivity.this);
   }

   private void getExtras() {
      Intent intent = getIntent();
      if (intent != null) {
         orderId = intent.getStringExtra(Constants.ORDER_ID);
      }

      Bundle bundle = intent.getBundleExtra(Constants.BUNDLE_PARAMS);

      if (bundle != null) {
         orderId = bundle.getString(ORDER_ID);
      }

      logI(TAG, "orderId: " + orderId);
   }

   private void initUI() {
      initUI(getString(R.string.ui_label_activity_title_order_details), true);

      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);
      initVerticalRecycelerView(recyclerView, deliveryOfferList);
      loadOrder();
   }

   private void hideAllCommands() {
      textViewChatWithClient.setVisibility(View.GONE);
      textViewMakeOffer.setVisibility(View.GONE);
      textViewPickUpOrder.setVisibility(View.GONE);
      textViewDeliverOrder.setVisibility(View.GONE);
      textViewNavigateToDestination.setVisibility(View.GONE);
      textViewNavigateToPickup.setVisibility(View.GONE);

      textViewCallDeliveryMan.setVisibility(View.GONE);
      textViewChatWithDeliveryMan.setVisibility(View.GONE);
      textViewEditOrder.setVisibility(View.GONE);
      textViewCancelOrder.setVisibility(View.GONE);
      textViewFinishOrder.setVisibility(View.GONE);
      linearLayoutReceiptCost.setVisibility(View.GONE);
      textViewViewReceipt.setVisibility(View.GONE);
      linearLayoutCosts.setVisibility(View.GONE);
   }

   private void showCommands_DeliveryManNewOrder() {
      hideAllCommands();
      textViewMakeOffer.setVisibility(View.VISIBLE);
   }

   private void showCommands_DeliveryManAwardedOrder() {
      logI(TAG, "showCommands_DeliveryManAwardedOrder");
      hideAllCommands();
      textViewChatWithClient.setVisibility(View.VISIBLE);
      textViewPickUpOrder.setVisibility(View.VISIBLE);
      textViewNavigateToPickup.setVisibility(View.VISIBLE);
      linearLayoutCosts.setVisibility(View.VISIBLE);
      textViewFinishOrder.setVisibility(View.VISIBLE);
   }

   private void showCommands_DeliveryManPickedOrder() {
      hideAllCommands();
      textViewChatWithClient.setVisibility(View.VISIBLE);
      textViewDeliverOrder.setVisibility(View.VISIBLE);
      textViewNavigateToDestination.setVisibility(View.VISIBLE);
      linearLayoutReceiptCost.setVisibility(View.VISIBLE);
      textViewViewReceipt.setVisibility(View.VISIBLE);
      linearLayoutCosts.setVisibility(View.VISIBLE);
      textViewFinishOrder.setVisibility(View.VISIBLE);
   }

   private void showCommands_DeliveryManDeliveredOrder() {
      hideAllCommands();
   }

   private void showCommands_ClientNewOrder() {
      hideAllCommands();
      textViewEditOrder.setVisibility(View.VISIBLE);
      textViewCancelOrder.setVisibility(View.VISIBLE);
   }

   private void showCommands_ClientAwardedOrder() {
      hideAllCommands();
      textViewCallDeliveryMan.setVisibility(View.VISIBLE);
      textViewChatWithDeliveryMan.setVisibility(View.VISIBLE);
      linearLayoutCosts.setVisibility(View.VISIBLE);
   }

   private void showCommands_ClientPickedOrder() {
      hideAllCommands();
      textViewCallDeliveryMan.setVisibility(View.VISIBLE);
      textViewChatWithDeliveryMan.setVisibility(View.VISIBLE);
      //textViewFinishOrder.setVisibility(View.VISIBLE);
      linearLayoutReceiptCost.setVisibility(View.VISIBLE);
      textViewViewReceipt.setVisibility(View.VISIBLE);
      linearLayoutCosts.setVisibility(View.VISIBLE);
   }

   private void showCommands_ClientDeliveredOrder() {
      hideAllCommands();
      textViewFinishOrder.setVisibility(View.VISIBLE);
      linearLayoutCosts.setVisibility(View.VISIBLE);
   }


   public static void startMe(Context context, String orderId) {
      Intent intent = new Intent(context, OrderActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra(ORDER_ID, orderId);
      context.startActivity(intent);
   }

   private void initCommands() {
      if (currentUser.isDeliveryMan()) {
         // ---------------------------------------------------------------------------------------
         // Case: Delivery Man
         // --------------------------------------------------------------------------------------
         if (deliveryOrder.isAwarded()) {
            showCommands_DeliveryManAwardedOrder();
         } else if (deliveryOrder.isPickedUp()) {
            showCommands_DeliveryManPickedOrder();
         } else if (deliveryOrder.isDelivered()) {
            showCommands_DeliveryManDeliveredOrder();
         } else if (deliveryOrder.isCompleted()) {
            hideAllCommands();
            if (!deliveryOrder.passed24Hours()) {
               if (currentUser.isDeliveryMan()) {
                  textViewChatWithClient.setVisibility(View.VISIBLE);
               } else {
                  textViewChatWithDeliveryMan.setVisibility(View.VISIBLE);
               }
            }
         } else if (deliveryOrder.isCancelled()) {
            hideAllCommands();
         } else if (deliveryOrder.isNew()) {
            showCommands_DeliveryManNewOrder();
         }

      } else {
         // ---------------------------------------------------------------------------------------
         // Case: Client
         // ---------------------------------------------------------------------------------------
         if (deliveryOrder.isAwarded()) {
            showCommands_ClientAwardedOrder();
         } else if (deliveryOrder.isPickedUp()) {
            showCommands_ClientPickedOrder();
         } else if (deliveryOrder.isDelivered()) {
            showCommands_ClientDeliveredOrder();
         } else if (deliveryOrder.isCompleted()) {
            hideAllCommands();
            if (!deliveryOrder.passed24Hours()) {
               textViewChatWithClient.setVisibility(View.VISIBLE);
            }
         } else if (deliveryOrder.isNew()) {
            showCommands_ClientNewOrder();
         }
      }
   }

   double actualCost = 0;

   private void loadOrder() {

      // ---------------------------------------------------------------------------------------
      // Reading order data
      // ---------------------------------------------------------------------------------------
      MyUtility.getAllPreviousCompletedOrdersRef().child(orderId)
            .addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if (dataSnapshot.getValue() != null) {
                     deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);

                     initCommands();

                     if (deliveryOrder.isNotOldOrder())
                        deliveryOrder.showOrderNotification(getApplicationContext(), currentUser);

                     textViewPickUpAddress.setText(deliveryOrder.getStoreName());
                     textViewDestinationAddress.setText(deliveryOrder.getDestinationAddress());
                     textViewEstimatedCost.setText(String.valueOf(MyUtility.roundDouble0(deliveryOrder.getEstimatedCost())));

                     textViewEstimatedDistance.setText(getString(R.string.distance_km, deliveryOrder.getDistanceToClient()));
                     textViewEstimatedTime.setText(String.valueOf(deliveryOrder.getEstimatedTime()));
                     textViewOrderInstructions.setText(String.valueOf(deliveryOrder.getInstructions()));
                     textViewOrderStatus.setText(deliveryOrder.getOrderStatusString(getApplicationContext()));
                     textViewOrderNumber.setText(String.valueOf(deliveryOrder.getOrderNumber()));

                     if (deliveryOrder.isInProgress() || deliveryOrder.isCompleted()) {

                        double deliveryCost = MyUtility.roundDouble0(deliveryOrder.getAcceptedOffer().getOfferValue());
                        double discount = 0;
                        String s;

                        if (!TextUtils.isEmpty(deliveryOrder.getPromoCode())) {
                           discount = deliveryOrder.getPromoCodeDiscount();

                        }

                        actualCost = deliveryCost - deliveryCost * discount / 100;

                        s = String.valueOf(actualCost) + " " + settings.getCurrency(getApplicationContext());

                        if (!TextUtils.isEmpty(deliveryOrder.getPromoCode())) {
                           s += "    (" + getString(R.string.ui_label_after_discount) + ":   " + deliveryCost + " "
                                 + settings.getCurrency(getApplicationContext()) + ")";

                        }

                        textViewOrderDeliveryCost.setText(s);
                     }

                     if (deliveryOrder.isPickedUp() || deliveryOrder.isDelivered() || deliveryOrder.isCompleted()) {
                        if (deliveryOrder.getReceiptValue() != null) {
                           textViewOrderReceiptCost.setText(
                                 String.valueOf(MyUtility.roundDouble0(deliveryOrder.getReceiptValue()))
                                       + " " + settings.getCurrency(getApplicationContext()));
                        }
                     }

                     loadOffers();

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


   private void loadOffers() {
      deliveryOfferList.clear();

      DatabaseReference ref = null;

      if (!currentUser.isDeliveryMan()) {
         ref = MyUtility.getOffersNodeRef().child(orderId);
      } else {
         if (deliveryOrder.isAwarded() && deliveryOrder.isDeliveryManOrder()) {
            ref = MyUtility.getUserOffersNodeRef()
                  .child(deliveryOrder.getAcceptedOffer().getDeliveryManId());
         }
      }

      if (ref == null) return;

      ref.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot ds :
                  dataSnapshot.getChildren()) {
               DeliveryOffer deliveryOffer = ds.getValue(DeliveryOffer.class);

               if (!deliveryOrder.isNew()) {
                  if (!deliveryOffer.getOrderId().equals(orderId)) {
                     return;
                  }
               }

               if (deliveryOffer.getDeliveryManId().equals(MyUtility.getCurrentUserUID())) {
                  textViewMakeOffer.setVisibility(View.GONE);
               }

               if (!currentUser.isDeliveryMan()) {
                  deliveryOfferList.add(deliveryOffer);
               } else {
                  if (deliveryOffer.getDeliveryManId().equals(MyUtility.getCurrentUserUID())) {
                     deliveryOfferList.add(deliveryOffer);
                  }
               }
            }

            Collections.sort(deliveryOfferList, (o1, o2) ->
                  o1.getDistanceToStore().compareTo(o2.getDistanceToStore()));

            adapter.notifyDataSetChanged();
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
         }
      });
   }

   private void initVerticalRecycelerView(RecyclerView recyclerView, List<DeliveryOffer> list) {

      adapter = new OffersRecyclerAdapter(this, list, false);

      RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
      recyclerView.setLayoutManager(mLayoutManager);
      //recyclerView.addItemDecoration(new BaseActivity.GridSpacingItemDecoration(1, dpToPx(10), true));
      recyclerView.setItemAnimator(new DefaultItemAnimator());
      recyclerView.setAdapter(adapter);
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      initUI();
   }

   @OnClick({R.id.textViewChatWithDeliveryMan,
         R.id.textViewCallDeliveryMan,
         R.id.textViewFinishOrder,
         R.id.textViewViewReceipt,
         R.id.textViewMakeOffer,
         R.id.textViewEditOrder,
         R.id.textViewCancelOrder,
         R.id.textViewChatWithClient,
         R.id.textViewPickUpOrder,
         R.id.textViewDeliverOrder,
         R.id.textViewNavigateToPickup,
         R.id.textViewNavigateToDestination})
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.textViewChatWithDeliveryMan:
            ChatActivity.startMe(getApplicationContext(), deliveryOrder.getId(), false);
            break;

         case R.id.textViewCallDeliveryMan:
            ContactHelper.makePhoneCall(OrderActivity.this,
                  deliveryOrder.getAcceptedOffer().getDeliveryManPhone(),
                  deliveryOrder.getAcceptedOffer().getDeliveryManName());
            break;

         case R.id.textViewMakeOffer:
            AddNewOfferActivity.startMe(getApplicationContext(), deliveryOrder.getId(), "");
            break;

         case R.id.textViewChatWithClient:
            ChatActivity.startMe(getApplicationContext(), deliveryOrder.getId(), false);
            break;

         case R.id.textViewEditOrder:
            editOrder();
            break;

         case R.id.textViewCancelOrder:
            cancelOrder();
            break;

         case R.id.textViewPickUpOrder:
            pickUpOrder();
            break;

         case R.id.textViewViewReceipt:
            viewReceipt();
            break;

         case R.id.textViewDeliverOrder:
            deliverOrder();
            break;

         case R.id.textViewFinishOrder:
            finishOrder();
            break;

         case R.id.textViewNavigateToPickup:
            navigateToPickup();
            break;

         case R.id.textViewNavigateToDestination:
            navigateToDestination();
            break;
      }
   }

   private void viewReceipt() {
      FullScreenViewActivity.startMe(getApplicationContext(), deliveryOrder.getReceiptUrl());
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      if (requestCode == Constants.REQ_ADD_ORDER_RECEIPT) {
         if (resultCode == RESULT_OK) {
            pickOrder2();
         }
      }
   }

   private void cancelOrder() {
//        deliveryOrder.cancelOrder();
//        if (currentUser.isDeliveryMan()) {
//            DeliveryManHomeActivity.startMe(getApplicationContext());
//        } else {
//            ClientHomeActivity.startMe(getApplicationContext());
//        }

      DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            switch (which) {
               case DialogInterface.BUTTON_POSITIVE:

                  deliveryOrder.cancelOrder();
                  ClientHomeActivity.startMe(getApplicationContext());

                  break;

               case DialogInterface.BUTTON_NEGATIVE:
                  //No button clicked
                  break;
            }
         }
      };


      AlertDialog.Builder builder = new AlertDialog
            .Builder(new ContextThemeWrapper(OrderActivity.this, R.style.AppCompatAlertDialogStyle));
      builder.setMessage(getString(R.string.ui_dialog_are_you_sure_delete_order))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener)
            .show();

//
//        final String[] singleChoiceItems = getResources().getStringArray(R.array.CancellationReasons);
//        int itemSelected = 0;
//        new AlertDialog.Builder(new ContextThemeWrapper(OrderActivity.this, R.style.AppCompatAlertDialogStyle))
//                .setTitle(getString(R.string.ui_button_cancel_order))
//                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
//                        deliveryOrder.setCancellationReason(singleChoiceItems[selectedIndex]);
//                    }
//                })
//                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deliveryOrder.cancelOrder();
//                        if (currentUser.isDeliveryMan()) {
//                            DeliveryManHomeActivity.startMe(getApplicationContext());
//                        } else {
//                            ClientHomeActivity.startMe(getApplicationContext());
//                        }
//                    }
//                })
//                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
   }

   private void editOrder() {
      Store store = new Store();
      store.setPlaceName(deliveryOrder.getStoreName());
      store.setVicinity(deliveryOrder.getPickUpAddress());
      store.setLatLng(deliveryOrder.getLatLngPickUpPoint());

      AddNewOrderActivity.startMe(getApplicationContext(),
            store,
            LatLng.fromLocation(mCurrentLocation),
            deliveryOrder.getLatLngDestinationPoint(),
            deliveryOrder.getId());
   }

   int selected_reason = 0;

   private void finishOrder() {
      if (currentUser.isDeliveryMan()) {
         if (deliveryOrder.isDelivered()) {
            DeliveryManHomeActivity.startMe(getApplicationContext());
         } else {
            //Toast.makeText(this, getString(R.string.ui_notification_order_not_delivered), Toast.LENGTH_SHORT).show();


            final String[] singleChoiceItems = getResources().getStringArray(R.array.CancellationReasons);
            int itemSelected = 0;
            new AlertDialog.Builder(new ContextThemeWrapper(OrderActivity.this,
                  R.style.AppCompatAlertDialogStyle))
                  .setTitle(getString(R.string.ui_button_cancel_order))
                  .setSingleChoiceItems(singleChoiceItems, itemSelected,
                        (dialogInterface, selectedIndex) -> selected_reason = selectedIndex)
                  .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                     deliveryOrder.setCancellationReason(singleChoiceItems[selected_reason]);
                     deliveryOrder.finishOrder();
                     DeliveryManHomeActivity.startMe(getApplicationContext());
                  })
                  .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                  .show();
         }
      } else {
         if (deliveryOrder.isDelivered()) {
            DeliveryManFeedbackActivity.startMe(getApplicationContext(), deliveryOrder.getId());
         } else {
            deliveryOrder.finishOrder();
            ClientHomeActivity.startMe(getApplicationContext());
         }
      }
   }

   private void pickUpOrder() {
      Intent intent = new Intent(getApplicationContext(), AddNewOrderReceiptActivity.class);
      intent.putExtra(ORDER_ID, deliveryOrder.getId());
      startActivityForResult(intent, Constants.REQ_ADD_ORDER_RECEIPT);
   }

   private void pickOrder2() {
      deliveryOrder.pickUpOrder();
      showCommands_DeliveryManPickedOrder();
      textViewOrderReceiptCost.setText(String.valueOf(deliveryOrder.getReceiptValue()) + " "
            + settings.getCurrency(getApplicationContext()));
   }

   private void deliverOrder() {
      DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
         switch (which) {
            case DialogInterface.BUTTON_POSITIVE:

               AlertDialog.Builder builder = new AlertDialog
                     .Builder(new ContextThemeWrapper(OrderActivity.this,
                     R.style.AppCompatAlertDialogStyle));
               builder.setTitle(getString(R.string.ui_label_you_want_to_add_promo_code));

               // Set up the input
               final EditText input = new EditText(OrderActivity.this);
               // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
               input.setInputType(InputType.TYPE_CLASS_TEXT);

               builder.setView(input);

               // Set up the buttons
               builder.setPositiveButton(getString(R.string.ok), (dialog1, which1) -> {

                  String promoCode = input.getText().toString();
                  doDeliverOrder(promoCode);

               });

               builder.setNegativeButton(getString(R.string.ui_button_skip), (dialog12, which12) -> {
                  doDeliverOrder("");
                  dialog12.cancel();
               });
               builder.show();
               break;

            case DialogInterface.BUTTON_NEGATIVE:
               //No button clicked
               break;
         }
      };


      AlertDialog.Builder builder = new AlertDialog
            .Builder(new ContextThemeWrapper(OrderActivity.this, R.style.AppCompatAlertDialogStyle));
      builder.setMessage(getString(R.string.ui_dialog_are_you_sure_deliver_order))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener)
            .show();
   }

   private void doDeliverOrder(final String promoCode) {

      if (TextUtils.isEmpty(promoCode)) {

         deliveryOrder.getAcceptedOffer().finishOffer();
         deliveryOrder.deliverOrder();

         double offerValue = deliveryOrder.getAcceptedOffer().getOfferValue();
         //Double.parseDouble(textInputLayoutOfferValue.getEditText().getText().toString());
         double companyPercent = currentUser.getFareModel().getCompanyPercent();
         double companyEarning = MyUtility.roundDouble2(companyPercent * offerValue / 100);

         currentUser.updateCredit(companyEarning);
         showCommands_DeliveryManDeliveredOrder();
         MyUtility.getOffersNodeRef().child(deliveryOrder.getId()).removeValue();
      } else {
         MyUtility.getPromoCodesNodeRef().child(promoCode)
               .addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     logI(TAG, "dataSnapshot: " + dataSnapshot.toString());

                     final PromoCode promoCode1 = dataSnapshot.getValue(PromoCode.class);

                     if (dataSnapshot.getValue() == null) {
                        Toast.makeText(OrderActivity.this, getString(R.string.ui_notifications_invalid_promo_code), Toast.LENGTH_SHORT).show();
                     } else {

                        MyUtility.getUserOrdersNodeRef().child(currentUser.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                 double deliveryCost = MyUtility.roundDouble0(deliveryOrder.getAcceptedOffer().getOfferValue());
                                 double discount = 0;
                                 String s = "";

                                 deliveryOrder.getAcceptedOffer().finishOffer();
                                 deliveryOrder.deliverOrder();

                                 double offerValue = deliveryOrder.getAcceptedOffer().getOfferValue();
                                 //Double.parseDouble(textInputLayoutOfferValue.getEditText().getText().toString());
                                 double companyPercent = currentUser.getFareModel().getCompanyPercent();
                                 double userCredit = currentUser.getFareModel().getAccountPoints();

                                 double companyEarning = MyUtility.roundDouble2(companyPercent * offerValue / 100);

                                 if (!TextUtils.isEmpty(promoCode)) {
                                    discount = Double.parseDouble(promoCode);
                                    double discount_value = MyUtility.roundDouble2(companyEarning * discount / 100);
                                    companyEarning = companyEarning - discount_value;
                                 }

                                 currentUser.updateCredit(companyEarning);
                                 showCommands_DeliveryManDeliveredOrder();
                                 MyUtility.getOffersNodeRef().child(deliveryOrder.getId()).removeValue();
                              } else {
                                 Toast.makeText(OrderActivity.this, getString(R.string.ui_notifications_invalid_promo_code),
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

   @Override
   public Settings getSettings() {
      return settings;
   }

   @Override
   public DeliveryOrder getOrder() {
      return deliveryOrder;
   }

   @Override
   public void deleteOffer(DeliveryOffer offer) {
      offer.deleteOffer();
      DeliveryManHomeActivity.startMe(getApplicationContext());
   }

   @Override
   public void editOffer(DeliveryOffer offer) {
   }

   @Override
   public void acceptOffer(DeliveryOffer offer) {

      //if (checkUserCredit(offer)) {
      offer.awardOffer();
      deliveryOrder.acceptOffer(offer);
      OrderActivity.startMe(getApplicationContext(), deliveryOrder.getId());

//            double offerValue = offer.getOfferValue();
//            double userCredit = currentUser.getFareModel().getAccountPoints();

      //currentUser.updateCredit(offerValue);

//        }else{

//        }
   }

   private boolean checkUserCredit(DeliveryOffer offer) {

      double offerValue = offer.getOfferValue();
      double userCredit = currentUser.getFareModel().getAccountPoints();
      return userCredit >= offerValue;
   }


   private void navigateToPickup() {
      DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            switch (which) {
               case DialogInterface.BUTTON_POSITIVE:

                  GoogleMapsHelper.launchNavigation(getApplicationContext(),
                        deliveryOrder.getLatLngPickUpPoint().latitude,
                        deliveryOrder.getLatLngPickUpPoint().longitude);
                  break;

               case DialogInterface.BUTTON_NEGATIVE:
                  //No button clicked
                  break;
            }
         }
      };


      AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OrderActivity.this,
            R.style.AppCompatAlertDialogStyle));
      builder.setMessage(getString(R.string.ui_dialog_are_you_sure_navigate_to_pickup))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener)
            .show();
   }

   private void navigateToDestination() {
      DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
         switch (which) {
            case DialogInterface.BUTTON_POSITIVE:

               GoogleMapsHelper.launchNavigation(getApplicationContext(),
                     deliveryOrder.getLatLngDestinationPoint().latitude,
                     deliveryOrder.getLatLngDestinationPoint().longitude);

               break;

            case DialogInterface.BUTTON_NEGATIVE:
               //No button clicked
               break;
         }
      };


      AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OrderActivity.this,
            R.style.AppCompatAlertDialogStyle));
      builder.setMessage(getString(R.string.ui_dialog_are_you_sure_navigate_to_destination))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener)
            .show();
   }

   public DeliveryOrder getCurrentOrder() {
      return deliveryOrder;
   }

   @Override
   public User getCurrentUser() {
      return currentUser;
   }

   private GoogleMap mMap;

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

      new LocationServiceManager(OrderActivity.this).initLocationService();
   }

   private void showPickUpAndDestination() {


      mMap.clear();

      List<Marker> pathMarkers = new ArrayList<>();
      LatLngBounds.Builder builder = new LatLngBounds.Builder();

      MarkerOptions options1 = new MarkerOptions()
            .position(deliveryOrder.getLatLngPickUpPoint().toGmsLatLng());

      Marker m1 = mMap.addMarker(options1);

      MarkerOptions options2 = new MarkerOptions()
            .position(deliveryOrder.getLatLngDestinationPoint().toGmsLatLng());

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

   private Location mCurrentLocation;

   @Override
   public void processLocation(Location location) {
      mCurrentLocation = location;
   }
}
