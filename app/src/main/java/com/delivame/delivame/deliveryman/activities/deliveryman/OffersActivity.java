package com.delivame.delivame.deliveryman.activities.deliveryman;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.client.ClientBaseActivity;
import com.delivame.delivame.deliveryman.activities.common.Chat.ChatActivity;
import com.delivame.delivame.deliveryman.adapters.OffersRecyclerAdapter;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.models.DeliveryOffer;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OffersActivity extends ClientBaseActivity implements
        OffersRecyclerAdapter.OffersRecyclerAdapterListener,
        InitManager.InitManagerListener{

    private User currentUser;
    private DeliveryOrder deliveryOrder;
    private String orderId;
    private final List<DeliveryOffer> deliveryOfferList = new ArrayList<>();
    private OffersRecyclerAdapter adapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        ButterKnife.bind(this);

        new InitManager().init(OffersActivity.this);
    }

    private void getExtras() {
        Intent intent = getIntent();
        if (intent != null){
            orderId = intent.getStringExtra(Constants.ORDER_ID);

//            MyUtility.getOffersNodeRef().child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.getValue() == null){
//                        for (int i = 0; i < 5; i++) {
//                            DeliveryOffer deliveryOffer = new DeliveryOffer();
//                            deliveryOffer.setDeliveryManId(String.valueOf(i));
//                            deliveryOffer.setDeliveryManName("Ahmed" + String.valueOf(i));
//                            deliveryOffer.setOfferComment("Offer from delivery man: Ahmed" + String.valueOf(i));
//                            deliveryOffer.setOrderId(orderId);
//                            deliveryOffer.setOfferNumber(String.valueOf(i));
//                            deliveryOffer.setOfferTime(getCurrentDateTime());
//                            deliveryOffer.setDeliveryManPhone("12345");
//                            deliveryOffer.setOfferValue(100);
//                            deliveryOffer.saveOffer();
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });


        }
    }

    public static void startMe(Context context) {
        Intent intent = new Intent(context, OffersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void loadMyOffers(){
        // ---------------------------------------------------------------------------------------
        // Reading order data
        // ---------------------------------------------------------------------------------------
        MyUtility.getUserOffersNodeRef().child(MyUtility.getCurrentUserUID()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                DeliveryOffer deliveryOffer = dataSnapshot.getValue(DeliveryOffer.class);
                deliveryOfferList.add(deliveryOffer);
                updateRecyclerView(deliveryOfferList, adapter, getString(R.string.ui_label_no_offers_available));
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

        initUI(getString(R.string.ui_label_list_of_offers), true);

        initVerticalRecycelerView(recyclerView, deliveryOfferList
        );

        loadMyOffers();
    }

    private void initVerticalRecycelerView(RecyclerView recyclerView, List<DeliveryOffer> list){


        adapter = new OffersRecyclerAdapter(this, list, true);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new BaseActivity.GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        updateRecyclerView(deliveryOfferList, adapter, getString(R.string.ui_label_no_offers_available));


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

    }

    @Override
    public void editOffer(DeliveryOffer offer) {

    }

    @Override
    public void acceptOffer(DeliveryOffer offer) {
        deliveryOrder.acceptOffer(offer);
        offer.setOrderId(deliveryOrder.getId());
        offer.awardOffer();
        ChatActivity.startMe(getApplicationContext(), orderId, false);
        finish();
    }



    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void initUI(Settings settings, User currentUser) {
        this.settings = settings;
        this.currentUser = currentUser;

        initUI();
    }
}
