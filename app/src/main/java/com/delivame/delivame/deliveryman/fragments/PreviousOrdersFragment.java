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
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PreviousOrdersFragment extends Fragment {

   @BindView(R.id.recyclerView)
   RecyclerView recyclerView;
   @BindView(R.id.textViewNoCurrentRecords)
   TextView textViewNoCurrentRecords;
   private Unbinder unbinder;

   private PreviousOrdersFragmentListener mListener;


   public PreviousOrdersFragment() {
      // Required empty public constructor
   }


   public static PreviousOrdersFragment newInstance() {
      return new PreviousOrdersFragment();
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

   }

   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_previous_orders, container, false);
      unbinder = ButterKnife.bind(this, view);

      initVerticalRecyclerView();
      loadOrders();
      return view;
   }

   private OrdersRecyclerAdapter adapter;
   private final List<DeliveryOrder> deliveryOrderList = new ArrayList<>();
   ChildEventListener valueEventListener;

   private void loadOrders() {
      deliveryOrderList.clear();
//      valueEventListener = MyUtility.getUserOrdersNodeRef()
//            .child(MyUtility.getCurrentUserUID())
//            .addChildEventListener(new ChildEventListener() {
//
//               @Override
//               public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
//
//                  if (dataSnapshot.getValue() != null) {
//                     DeliveryOrder deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);
//                     if (deliveryOrder.isCompleted() || deliveryOrder.isDelivered()) {
//
//                        deliveryOrderList.add(0, deliveryOrder);
//                        //mListener.updateRecyclerView(deliveryOrderList, adapter, getContext().getString(R.string.ui_label_no_orders_available));
//                        //adapter.notifyDataSetChanged();
//                        updateRecyclerView();
//                     }
//                  }
//               }
//
//               @Override
//               public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
//               }
//
//               @Override
//               public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//               }
//
//               @Override
//               public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
//               }
//
//               @Override
//               public void onCancelled(@NonNull DatabaseError databaseError) {
//               }
//            });

      String u = MyUtility.getCurrentUserUID();
      MyUtility.getUserOrdersNodeRef()
            .child(MyUtility.getCurrentUserUID())
            .addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if (dataSnapshot.getValue() != null) {

                     for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        DeliveryOrder deliveryOrder = dsp.getValue(DeliveryOrder.class);
                        if (deliveryOrder.isCompleted() || deliveryOrder.isDelivered()) {

                           deliveryOrderList.add(deliveryOrder);
                           //mListener.updateRecyclerView(deliveryOrderList, adapter, getContext().getString(R.string.ui_label_no_orders_available));
                           //adapter.notifyDataSetChanged();
                        }

                     }
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

   private void initVerticalRecyclerView() {

      adapter = new OrdersRecyclerAdapter(getContext(), deliveryOrderList);

      RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
      recyclerView.setLayoutManager(mLayoutManager);
      //recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
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
      if (context instanceof PreviousOrdersFragmentListener) {
         mListener = (PreviousOrdersFragmentListener) context;
      } else {
         throw new RuntimeException(context.toString()
               + " must implement PreviousOrdersFragmentListener");
      }
   }

   @Override
   public void onDetach() {
      super.onDetach();
      mListener = null;
//      if (valueEventListener != null) {
//         MyUtility.getUserOrdersNodeRef().removeEventListener(valueEventListener);
//      }
   }

   public interface PreviousOrdersFragmentListener {
      User getCurrentUser();

      void updateRecyclerView(List<?> list, RecyclerView.Adapter<?> adapter, String text);
   }

}
