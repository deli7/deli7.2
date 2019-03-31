package com.delivame.delivame.deliveryman.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.squareup.picasso.Picasso;

public class UserInformationFragment extends Fragment {

   @Nullable
   private String fullName = "";
   @Nullable
   private String photoUrl = "";
   @Nullable
   private String orderStatus = "";
   @Nullable
   private String orderNumber = "";
   private int orderStatusInt = 0;
   @Nullable
   private String orderStartDate = "";
   private Button buttonStartStopTrip;
   private OnUserInfoFragInteractionListener mListener;

   public UserInformationFragment() {
      // Required empty public constructor
   }


   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments() != null) {
         fullName = getArguments().getString(Constants.FIREBASE_KEY_USER_FULLNAME);
         photoUrl = getArguments().getString(Constants.FIREBASE_KEY_PHOTO_URL);
         orderStatus = getArguments().getString(Constants.FIREBASE_KEY_ORDER_STATUS);
         orderStatusInt = getArguments().getInt(Constants.FIREBASE_KEY_ORDER_STATUS_INT);
         orderNumber = getArguments().getString(Constants.FIREBASE_KEY_ORDER_NUMBER);
         String phoneNumber = getArguments().getString(Constants.FIREBASE_KEY_USER_PHONE_NUMBER);
         orderStartDate = getArguments().getString(Constants.FIREBASE_KEY_ORDER_USER_REQUEST_TIME);
         if (fullName == null) fullName = "";

      }
   }

   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_user_information, container, false);
      TextView textViewUserName = view.findViewById(R.id.textViewUserName);
      TextView textViewOrderNumber = view.findViewById(R.id.textViewOrderNumber);
      TextView textViewOrderStatus = view.findViewById(R.id.textViewOrderStatus);
      ImageView imageViewUser = view.findViewById(R.id.imageViewUser);

      textViewUserName.setText(getString(R.string.clientdd) + ": " + fullName);
      textViewOrderNumber.setText("Order #: " + orderNumber);
      textViewOrderStatus.setText(getString(R.string.order_status_dd) + ": " + orderStatus);


      BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);

      switch (orderStatusInt) {
         case Constants.ORDER_STATUS_DRIVER_RESPONDED_AND_ON_THE_WAY:

            bottomNavigationView.getMenu().getItem(1).setTitle(getString(R.string.start_trip));
            bottomNavigationView.getMenu().getItem(1).setIcon(android.R.drawable.ic_media_play);

            break;

         case Constants.ORDER_STATUS_TRIP_STARTED:

            bottomNavigationView.getMenu().getItem(1).setTitle(getString(R.string.end_trip));
            bottomNavigationView.getMenu().getItem(1).setIcon(android.R.drawable.ic_media_play);

            bottomNavigationView.getMenu().removeItem(R.id.itemCancelOrder);

            break;
         default:
            MyUtility.logI(Constants.TAG, "switch statement with unhandled case");
            break;

      }


      bottomNavigationView.setOnNavigationItemSelectedListener(
            item -> {
               switch (item.getItemId()) {
                  case R.id.itemStartTrip:

                     switch (orderStatusInt) {
                        case Constants.ORDER_STATUS_DRIVER_RESPONDED_AND_ON_THE_WAY:
                           mListener.requestTripStart();
                           break;

                        case Constants.ORDER_STATUS_TRIP_WAITING_USER_APPROVAL_TO_START:
                           Toast.makeText(getContext(), "Waiting for Client to Accept Ride",
                                 Toast.LENGTH_SHORT).show();
                           break;

                        case Constants.ORDER_STATUS_TRIP_STARTED:
                           mListener.requestTripEnd();
                           break;
                     }

                     break;
                  case R.id.itemCancelOrder:
                     mListener.requestTripCancel(orderStartDate);
                     break;
               }
               return false;
            });


      if (photoUrl != null && !TextUtils.isEmpty(photoUrl)) {
         Picasso.get()
               .load(photoUrl)
               .placeholder(R.mipmap.ic_splash_logo)
               .error(R.mipmap.ic_splash_logo)
               .into(imageViewUser);
      }

      return view;
   }


   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      if (context instanceof OnUserInfoFragInteractionListener) {
         mListener = (OnUserInfoFragInteractionListener) context;
      } else {
         throw new RuntimeException(context.toString()
               + " must implement OnUserInfoFragInteractionListener");
      }
   }

   @Override
   public void onDetach() {
      super.onDetach();
      mListener = null;
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
   }


   interface OnUserInfoFragInteractionListener {

      void requestTripStart();

      void requestTripEnd();

      void requestTripCancel(String orderStartDate);
   }

}
