package com.delivame.delivame.deliveryman.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.common.EditUserProfileActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.AddCreditActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.WithdrawCreditActivity;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UserProfileFragment extends Fragment {

   @BindView(R.id.textViewRating)
   TextView textViewRating;
   @BindView(R.id.ratingBar)
   RatingBar ratingBar;
   @BindView(R.id.linearLayoutRank)
   LinearLayout linearLayoutRank;
   @BindView(R.id.imageViewDriverPhoto)
   ImageView imageViewDriverPhoto;
   @BindView(R.id.textViewPhoneNumber)
   TextView textViewPhoneNumber;
   @BindView(R.id.coordinatorLayout)
   CoordinatorLayout coordinatorLayout;
   @BindView(R.id.buttonUpdateInformation)
   Button buttonUpdateInformation;
   @BindView(R.id.textViewNumberOfTrips)
   TextView textViewNumberOfTrips;
   @BindView(R.id.textViewTotalEarnings)
   TextView textViewTotalEarnings;
   @BindView(R.id.textViewCredit)
   TextView textViewCredit;
   @BindView(R.id.textViewUsername)
   TextView textViewUsername;
   @BindView(R.id.textViewEmailAddress)
   TextView textViewEmailAddress;
   @BindView(R.id.linearLayoutCredit)
   LinearLayout linearLayoutCredit;
   @BindView(R.id.linearLayoutTotalEarnings)
   LinearLayout linearLayoutTotalEarnings;
   @BindView(R.id.buttonDepositCredit)
   Button buttonDepositCredit;
   @BindView(R.id.buttonWithdrawCredit)
   Button buttonWithdrawCredit;

   private Unbinder unbinder;
   private UserProfileFragmentListener listener;


   public UserProfileFragment() {
      // Required empty public constructor
   }


   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
      unbinder = ButterKnife.bind(this, view);

      User currentUser = listener.getCurrentUser();

      if (!currentUser.isDeliveryMan()) {
         linearLayoutCredit.setVisibility(View.GONE);
         linearLayoutTotalEarnings.setVisibility(View.GONE);
         buttonDepositCredit.setVisibility(View.GONE);
         buttonWithdrawCredit.setVisibility(View.GONE);
      }

      textViewPhoneNumber.setText(currentUser.getPhoneNumber());
      textViewCredit.setText(String.valueOf(MyUtility.roundDouble2(currentUser.getFareModel().getAccountPoints())));
      textViewEmailAddress.setText(currentUser.getEmailAddress());
      textViewUsername.setText(currentUser.getFullName());
      if (!TextUtils.isEmpty(currentUser.getPhotoURL())) {
         Picasso.get()
               .load(currentUser.getPhotoURL())
               .placeholder(R.mipmap.ic_user)
               .error(R.mipmap.ic_user)
               .into(imageViewDriverPhoto);
      }


      MyUtility.getUserOrdersNodeRef().child(MyUtility.getCurrentUserUID())
            .addListenerForSingleValueEvent(new ValueEventListener() {

               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  double totalEarnings = 0;
                  int count = 0;
                  for (DataSnapshot ds :
                        dataSnapshot.getChildren()) {
                     DeliveryOrder deliveryOrder = ds.getValue(DeliveryOrder.class);
                     if (deliveryOrder.isDelivered() || deliveryOrder.isCompleted()) {
                        totalEarnings += deliveryOrder.getAcceptedOffer().getOfferValue();
                        count += 1;
                     }

                  }

                  textViewNumberOfTrips.setText(String.valueOf(count));
                  textViewTotalEarnings.setText(String.valueOf(totalEarnings) + " " + listener.getSettings().getCurrency(getContext()));
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
               }
            });

      if (currentUser.isDeliveryMan()) {
         textViewRating.setText(String.valueOf(MyUtility.roundDouble2(currentUser.getRank())));
         ratingBar.setRating((float) currentUser.getRank());
      } else {
         linearLayoutRank.setVisibility(View.GONE);
      }

      return view;
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      if (context instanceof UserProfileFragmentListener) {
         listener = (UserProfileFragmentListener) context;
      } else {
         throw new RuntimeException(context.toString()
               + " must implement RideHistoryTabFragmentListener");
      }
   }

   @Override
   public void onDetach() {
      super.onDetach();
      listener = null;
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      unbinder.unbind();
   }

   @OnClick(R.id.buttonUpdateInformation)
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.buttonUpdateInformation:
            EditUserProfileActivity.startMe(getContext());
            break;
      }
   }

   @OnClick(R.id.buttonDepositCredit)
   public void onButtonDepositCreditClicked() {
      AddCreditActivity.startMe(getContext());
   }

   @OnClick(R.id.buttonWithdrawCredit)
   public void onButtonWithdrawCreditClicked() {
      WithdrawCreditActivity.startMe(getContext());
   }


   public interface UserProfileFragmentListener {
      User getCurrentUser();

      Settings getSettings();
   }
}
