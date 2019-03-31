package com.delivame.delivame.deliveryman.activities.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.adapters.MyPlacesRecyclerAdapter;
import com.delivame.delivame.deliveryman.models.MyPlace;
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
import butterknife.OnClick;

public class MyPlacesActivity extends ClientBaseActivity implements
        MyPlacesRecyclerAdapter.MyPlacesRecyclerAdapterListener, InitManager.InitManagerListener {

    private MyPlacesRecyclerAdapter adapter;
    private final List<MyPlace> myPlaces = new ArrayList<>();
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplaces);
        ButterKnife.bind(this);
        initUI(getString(R.string.my_places), true);
        new InitManager().init(MyPlacesActivity.this);
    }


    private void loadMyPlaces() {

        MyUtility.getSavedPlacesNodeRef()
                .child(MyUtility.getCurrentUserUID()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {


                    //MyPlace MyPlace = new MyPlace(dataSnapshot);
                    MyPlace myPlace = dataSnapshot.getValue(MyPlace.class);
                    myPlaces.add(myPlace);

                    //updateRecyclerView(myPlaces, adapter, getString(R.string.ui_label_no_places));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                MyPlace myPlace = new MyPlace(dataSnapshot);
                for (MyPlace place :
                        myPlaces) {
                    if (place.getTitle().equals(myPlace.getTitle())) {
                        myPlaces.set(myPlaces.indexOf(place), myPlace);
                        //updateRecyclerView(myPlaces , adapter, getString(R.string.ui_label_no_places));
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                MyPlace myPlace = new MyPlace(dataSnapshot);
                for (MyPlace place :
                        myPlaces) {
                    if (place.getTitle().equals(myPlace.getTitle())) {
                        myPlaces.remove(place);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void setupRecyclerView() {
        myPlaces.clear();

        RecyclerView recyclerViewMyPlaces = findViewById(R.id.recyclerViewMyPlaces);
        adapter = new MyPlacesRecyclerAdapter(this, myPlaces, currentUser);
        recyclerViewMyPlaces.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewMyPlaces.setLayoutManager(linearLayoutManager);
        recyclerViewMyPlaces.setItemAnimator(new DefaultItemAnimator());
       // updateRecyclerView(myPlaces, adapter, getString(R.string.ui_label_no_places));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void finishAct(String title, String address, double lat, double lng) {
        Intent intent = new Intent();
        intent.putExtra(Constants.MY_PLACE_TITLE, title);
        intent.putExtra(Constants.MY_PLACE_ADDRESS, address);
        intent.putExtra(Constants.MY_PLACE_LAT, lat);
        intent.putExtra(Constants.MY_PLACE_LNG, lng);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void deletePlace(MyPlace place) {
        MyUtility.getSavedPlacesNodeRef().child(MyUtility.getCurrentUserUID()).child(place.getTitle()).removeValue();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQ_ADD_NEW_PLACE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String title = data.getStringExtra(Constants.MY_PLACE_TITLE);
                    String address = data.getStringExtra(Constants.MY_PLACE_ADDRESS);
                    double lat = data.getDoubleExtra(Constants.MY_PLACE_LAT, 0);
                    double lng = data.getDoubleExtra(Constants.MY_PLACE_LNG, 0);
                }
                break;
            default:
                MyUtility.logI(Constants.TAG, "switch statement with unhandled case");
                break;
        }
    }

    @OnClick(R.id.fab)
    public void onClick() {
        Intent intent = new Intent(getApplicationContext(), AddNewMyPlaceActivity.class);
        startActivityForResult(intent, Constants.REQ_ADD_NEW_PLACE);
    }

    @Override
    public void initUI(Settings settings, User currentUser) {
        this.currentUser = currentUser;
        setupRecyclerView();
        loadMyPlaces();
    }
}
