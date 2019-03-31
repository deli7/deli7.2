package com.delivame.delivame.deliveryman.activities.deliveryman;

import android.os.Bundle;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.models.DeliveryOffer;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.delivame.delivame.deliveryman.utilities.NotificationsHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.delivame.delivame.deliveryman.utilities.Constants.ORDER_ID;

public class DeliveryManBaseActivity extends BaseActivity {


    private ValueEventListener orderListener = null;
    private ChildEventListener offersListener = null;


    void listenToOrderChanges(String orderId){
        orderListener = MyUtility.getOrdersNodeRef().child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DeliveryOrder deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);

                if (deliveryOrder == null) return;

                switch (deliveryOrder.getOrderStatus()){
                    case Constants.DELIVERY_ORDER_STATUS_ORDER_ACCEPTED:
                        if (deliveryOrder.getAcceptedOffer().getDeliveryManId().equals(MyUtility.getCurrentUserUID())){
                            Bundle bundle = new Bundle();
                            bundle.putString(ORDER_ID, deliveryOrder.getId());

                            NotificationsHelper.showNotification(getApplicationContext(),
                                    "Order Accepted",
                                    getString(R.string.ui_notification_you_won_order) + " :" + deliveryOrder.getOrderNumber(),
                                    R.drawable.neworder,
                                    DeliveryManHomeActivity.class, bundle);
                        }

                        break;

                    case Constants.DELIVERY_ORDER_STATUS_ORDER_PICKED_UP:
                        if (deliveryOrder.getClientId().equals(MyUtility.getCurrentUserUID())){
                            Bundle bundle = new Bundle();
                            bundle.putString(ORDER_ID, deliveryOrder.getId());

                            NotificationsHelper.showNotification(getApplicationContext(),
                                    "Order Picked Up",
                                    getString(R.string.ui_notification_your_order_picked) + " :" + deliveryOrder.getOrderNumber(),
                                    R.drawable.neworder,
                                    DeliveryManHomeActivity.class, bundle);
                        }

                        break;

                    case Constants.DELIVERY_ORDER_STATUS_ORDER_DELIVERED:
                        if (deliveryOrder.getClientId().equals(MyUtility.getCurrentUserUID())){
                            Bundle bundle = new Bundle();
                            bundle.putString(ORDER_ID, deliveryOrder.getId());
                            NotificationsHelper.showNotification(getApplicationContext(),
                                    "Order Delivered",
                                    getString(R.string.ui_notification_your_order_picked) + " :" + deliveryOrder.getOrderNumber(),
                                    R.drawable.neworder,
                                    DeliveryManHomeActivity.class, bundle);

                            stopListeningToOrder(deliveryOrder.getId());
                        }
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        offersListener = MyUtility.getOffersNodeRef().child(orderId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DeliveryOffer deliveryOffer = dataSnapshot.getValue(DeliveryOffer.class);
                if (deliveryOffer.getOfferStatus() == Constants.OFFER_STATUS_NEW){
                    Bundle bundle = new Bundle();
                    bundle.putString(ORDER_ID, deliveryOffer.getOrderId());

                    NotificationsHelper.showNotification(getApplicationContext(),
                            getString(R.string.new_offer_added),
                            "",
                            R.drawable.neworder,
                            DeliveryManHomeActivity.class, bundle);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                DeliveryOffer deliveryOffer = dataSnapshot.getValue(DeliveryOffer.class);
                if (deliveryOffer.getOfferStatus() == Constants.OFFER_STATUS_NEW){
                    Bundle bundle = new Bundle();
                    bundle.putString(ORDER_ID, deliveryOffer.getOrderId());
                    NotificationsHelper.showNotification(getApplicationContext(),
                            "Offer Changed",
                            "",
                            R.drawable.neworder,
                            DeliveryManHomeActivity.class, bundle);

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DeliveryOffer deliveryOffer = dataSnapshot.getValue(DeliveryOffer.class);
                if (deliveryOffer.getOfferStatus() == Constants.OFFER_STATUS_NEW){
                    Bundle bundle = new Bundle();
                    bundle.putString(ORDER_ID, deliveryOffer.getOrderId());
                    NotificationsHelper.showNotification(getApplicationContext(),
                            "Offer Removed",
                            "",
                            R.drawable.neworder,
                            DeliveryManHomeActivity.class, bundle);

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void stopListeningToOrder(String orderId){
        MyUtility.getOrdersNodeRef().child(orderId).removeEventListener(orderListener);
        MyUtility.getOrdersNodeRef().child(orderId).removeEventListener(offersListener);
        orderListener = null;
        offersListener = null;
    }



}
