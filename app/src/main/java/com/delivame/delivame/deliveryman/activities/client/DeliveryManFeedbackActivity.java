package com.delivame.delivame.deliveryman.activities.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeliveryManFeedbackActivity extends ClientBaseActivity implements InitManager.InitManagerListener {

   @BindView(R.id.textViewDriverName)
   TextView textViewDriverName;
   @BindView(R.id.ratingBar)
   RatingBar ratingBar;
   @BindView(R.id.editTextFeedbackComment)
   EditText editTextFeedbackComment;
   @BindView(R.id.buttonSubmitReview)
   Button buttonSubmitReview;
   @BindView(R.id.buttonSkipReview)
   Button buttonSkipReview;

   private DeliveryOrder deliveryOrder;
   private String orderId;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_delivery_man_feedback);
      ButterKnife.bind(this);
      getExtras();
      new InitManager().init(DeliveryManFeedbackActivity.this);
   }

   private void getExtras() {
      Intent intent = getIntent();
      if (intent != null) {
         orderId = intent.getStringExtra(Constants.ORDER_ID);
      }
   }

   private void loadOrder() {

      // ---------------------------------------------------------------------------------------
      // Reading order data
      // ---------------------------------------------------------------------------------------
      MyUtility.getOrdersNodeRef().child(orderId).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
               deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
         }
      });

   }

   public static void startMe(Context context, String id) {
      Intent intent = new Intent(context, DeliveryManFeedbackActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra(Constants.ORDER_ID, id);
      context.startActivity(intent);
   }


   private void initUI() {
      initUI(getString(R.string.ui_activity_title_order_feedback), false);
      loadOrder();
   }

   @OnClick({R.id.buttonSubmitReview, R.id.buttonSkipReview})
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.buttonSubmitReview:
            if (deliveryOrder == null || deliveryOrder.getAcceptedOffer() == null) {
               ClientHomeActivity.startMe(getApplicationContext());
               finish();
               return;
            }
            MyUtility.getUsersNodeRef().child(deliveryOrder.getAcceptedOffer().getDeliveryManId())
                  .addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User deliveryUser = new User(dataSnapshot,
                              MyUtility.getUsersNodeRef().child(deliveryOrder.getAcceptedOffer()
                                    .getDeliveryManId()),
                              deliveryOrder.getAcceptedOffer().getDeliveryManId(), settings);

                        deliveryOrder.setOrderRank(ratingBar.getRating());
                        deliveryOrder.setOrderFeedback(editTextFeedbackComment.getText().toString());
                        deliveryOrder.getAcceptedOffer().finishOffer();
                        if (!deliveryOrder.isCompleted()) {
                           deliveryOrder.finishOrder();
                        }

                        double rank;
                        if (deliveryUser.getRank() == 0) {
                           rank = ratingBar.getRating();
                        } else {
                           rank = (deliveryUser.getRank() + ratingBar.getRating()) / 2;
                        }

                        deliveryUser.setRank(rank);

                        if (rank == 1) {
                           double lowRankCount = deliveryUser.getLowRankCount();
                           deliveryUser.setLowRankCount(lowRankCount + 1);
                        }

                        deliveryUser.saveUser();
                        ClientHomeActivity.startMe(getApplicationContext());
                        finish();
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {
                        ClientHomeActivity.startMe(getApplicationContext());
                        finish();
                     }
                  });


            break;
         case R.id.buttonSkipReview:
            if (deliveryOrder != null) {
               if (!deliveryOrder.isCompleted()) {
                  deliveryOrder.finishOrder();
               }
            }
            ClientHomeActivity.startMe(getApplicationContext());
            finish();
            break;
      }
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;
      initUI();
   }
}
