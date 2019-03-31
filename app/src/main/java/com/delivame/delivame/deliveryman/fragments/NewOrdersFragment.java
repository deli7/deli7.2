package com.delivame.delivame.deliveryman.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.adapters.OrdersRecyclerAdapter;
import com.delivame.delivame.deliveryman.googleapis.GetDistanceAndTimeData;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class NewOrdersFragment extends Fragment implements GetDistanceAndTimeData.GetDistanceAndTimeDataListener {

   @BindView(R.id.recyclerView)
   RecyclerView recyclerView;
   @BindView(R.id.textViewNoCurrentRecords)
   TextView textViewNoCurrentRecords;
   private Unbinder unbinder;

   private NewOrdersFragmentListener mListener;


   public NewOrdersFragment() {
      // Required empty public constructor
   }

   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_new_orders, container, false);
      unbinder = ButterKnife.bind(this, view);

      initVerticalRecyclerView(recyclerView, deliveryOrderList);
      loadOrders();
      return view;
   }

   private OrdersRecyclerAdapter adapter;
   private final List<DeliveryOrder> deliveryOrderList = new ArrayList<>();

   ValueEventListener valueEventListener;

   private void loadOrders() {

      valueEventListener = MyUtility.getOrdersNodeRef()
            .addValueEventListener(new ValueEventListener() {

               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  deliveryOrderList.clear();

                  for (DataSnapshot ds : dataSnapshot.getChildren()) {
                     DeliveryOrder deliveryOrder = ds.getValue(DeliveryOrder.class);
                     if (deliveryOrder.isNew()) {

                        if (deliveryOrder.getVehicleCategory()
                              .equalsIgnoreCase(mListener.getCurrentUser().getVehicleCategory().getId())) {
                           deliveryOrderList.add(deliveryOrder);
                        }
                     }
                  }

                  if (mListener.getCurrentUser() == null) return;

                  if (mListener.getCurrentUser().isDeliveryMan()) {
                     filterStores();
                  } else {
                     ////mListener.updateRecyclerView(deliveryOrderList, adapter, getContext().getString(R.string.ui_label_no_orders_available));
                     updateRecyclerView();
                  }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
               }
            });

   }

   private void updateRecyclerView() {
      if (deliveryOrderList.size() > 0) {
         textViewNoCurrentRecords.setVisibility(View.GONE);
         recyclerView.setVisibility(View.VISIBLE);
         adapter.notifyDataSetChanged();
      } else {
         textViewNoCurrentRecords.setVisibility(View.VISIBLE);
         recyclerView.setVisibility(View.GONE);
      }
   }

   private void initVerticalRecyclerView(RecyclerView recyclerView, List<DeliveryOrder> list) {

      adapter = new OrdersRecyclerAdapter(getContext(), list);

      RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
      recyclerView.setLayoutManager(mLayoutManager);
      recyclerView.setItemAnimator(new DefaultItemAnimator());
      recyclerView.setAdapter(adapter);
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      unbinder.unbind();
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      if (context instanceof NewOrdersFragmentListener) {
         mListener = (NewOrdersFragmentListener) context;
      } else {
         throw new RuntimeException(context.toString()
               + " must implement PreviousOrdersFragmentListener");
      }
   }

   private void filterStores() {
      logI(TAG, "filterStores");
      if (deliveryOrderList.size() == 0) return;

      List<Store> storeList = new ArrayList<>();
      for (int i = 0; i < deliveryOrderList.size(); i++) {
         Store store = new Store();
         store.setLatLng(deliveryOrderList.get(i).getLatLngPickUpPoint());
         storeList.add(store);
      }

      GetDistanceAndTimeData getDistanceAndTimeData = new GetDistanceAndTimeData(storeList,
            mListener.getCurrentLocation(), mListener.getSettings().getGoogleApiKey());
      Object[] DataTransfer = new Object[3];
      DataTransfer[0] = this;
      DataTransfer[1] = getContext();
      getDistanceAndTimeData.execute(DataTransfer);
   }

   @Override
   public void setEstimatedDistanceAndTime(double distance, double time) {
   }

   @Override
   public void setListEstimatedDistanceAndTime(List<Double> distance, List<Double> time) {

      if (mListener == null) return;
      if (deliveryOrderList.size() == 0) return;

      double range = mListener.getSettings().getDistanceRangeToDelivery();

      for (int i = 0; i < deliveryOrderList.size(); i++) {
         deliveryOrderList.get(i).setDistanceToStore(distance.get(i));
      }

      List<DeliveryOrder> tempList = new ArrayList<>();
      for (int i = 0; i < deliveryOrderList.size(); i++) {
         if (distance.get(i) < (range / 1000)) {
            tempList.add(deliveryOrderList.get(i));
         }
      }

      deliveryOrderList.clear();
      deliveryOrderList.addAll(tempList);

      //mListener.updateRecyclerView(deliveryOrderList, adapter, getContext().getString(R.string.ui_label_no_orders_available));
      //adapter.notifyDataSetChanged();
      updateRecyclerView();
   }


   @Override
   public void onDetach() {
      super.onDetach();
      mListener = null;
      if (valueEventListener != null) {
         MyUtility.getOrdersNodeRef().removeEventListener(valueEventListener);
      }
   }

   public interface NewOrdersFragmentListener {
      User getCurrentUser();

      LatLng getCurrentLocation();

      Settings getSettings();

      void updateRecyclerView(List<?> list, RecyclerView.Adapter<?> adapter, String text);
   }
}
