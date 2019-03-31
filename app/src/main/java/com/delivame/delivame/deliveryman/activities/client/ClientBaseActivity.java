package com.delivame.delivame.deliveryman.activities.client;

import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ClientBaseActivity extends BaseActivity {

    private ValueEventListener orderListener = null;

    public void listenToOrderChanges(final String orderId){
        orderListener = MyUtility.getOrdersNodeRef().child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void stopListeningToOrder(String orderId){
        MyUtility.getOrdersNodeRef().child(orderId).removeEventListener(orderListener);
        orderListener = null;
    }
}
