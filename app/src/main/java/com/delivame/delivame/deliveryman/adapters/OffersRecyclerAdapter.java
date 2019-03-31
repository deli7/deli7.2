package com.delivame.delivame.deliveryman.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.DeliveryOffer;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OffersRecyclerAdapter extends RecyclerView.Adapter<OffersRecyclerAdapter.MyPlaceViewHolder> {

   private final List<DeliveryOffer> deliveryOfferList;
   private final Context context;
   private final LayoutInflater layoutInflater;

   private final OffersRecyclerAdapterListener listener;
   private final boolean showClientInfo;


   public OffersRecyclerAdapter(Context context, List<DeliveryOffer> deliveryOfferList, boolean showClientInfo) {
      this.context = context;
      this.deliveryOfferList = deliveryOfferList;
      layoutInflater = LayoutInflater.from(context);
      listener = (OffersRecyclerAdapterListener) context;
      this.showClientInfo = showClientInfo;
   }

   @NonNull
   @Override
   public MyPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      View view = layoutInflater.inflate(R.layout.list_item_offer, parent, false);

      return new MyPlaceViewHolder(view);
   }


   @Override
   public void onBindViewHolder(@NonNull MyPlaceViewHolder holder, int position) {

      DeliveryOffer store = deliveryOfferList.get(position);
      holder.setData(store);
   }

   @Override
   public int getItemCount() {
      return deliveryOfferList.size();
   }


   class MyPlaceViewHolder extends RecyclerView.ViewHolder {

      @BindView(R.id.textViewDeliveryMan)
      TextView textViewDeliveryMan;
      @BindView(R.id.textViewDeliveryOfferDate)
      TextView textViewDeliveryOfferDate;
      @BindView(R.id.textViewDeliveryOfferValue)
      TextView textViewDeliveryOfferValue;
      @BindView(R.id.textViewDeliveryOfferComment)
      TextView textViewDeliveryOfferComment;
      @BindView(R.id.textViewEditOffer)
      TextView textViewEditOffer;
      @BindView(R.id.textViewDeleteOffer)
      TextView textViewDeleteOffer;
      @BindView(R.id.textViewAcceptOffer)
      TextView textViewAcceptOffer;
      @BindView(R.id.textViewDeliveryOrderNumber)
      TextView textViewDeliveryOrderNumber;
      @BindView(R.id.textViewDeliverOrderClientName)
      TextView textViewDeliverOrderClientName;
      @BindView(R.id.textViewDeliverOrderStatus)
      TextView textViewDeliveryOrderStatus;
      @BindView(R.id.layoutClientInfo)
      LinearLayout layoutClientInfo;
      @BindView(R.id.textViewDeliveryToStoreDistance)
      TextView textViewDeliveryToStoreDistance;
      @BindView(R.id.textViewRating)
      TextView textViewRating;
      @BindView(R.id.ratingBar)
      RatingBar ratingBar;


      DeliveryOrder deliveryOrder;


      MyPlaceViewHolder(@NonNull View itemView) {
         super(itemView);

         ButterKnife.bind(this, itemView);
         initCommandsListeners();
      }


      void setData(@NonNull final DeliveryOffer deliveryOffer) {


         MyUtility.getUsersNodeRef().child(deliveryOffer.getDeliveryManId())
               .addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     User deliveryUser = new User(dataSnapshot,
                           MyUtility.getUsersNodeRef().child(deliveryOffer.getDeliveryManId()),
                           deliveryOffer.getDeliveryManId(), listener.getSettings());

                     textViewDeliveryMan.setText(deliveryOffer.getDeliveryManName());
                     textViewDeliveryOfferDate.setText(deliveryOffer.getOfferTime());
                     textViewDeliveryOfferValue.setText(String.valueOf(deliveryOffer.getOfferValue())
                           + " " + listener.getSettings().getCurrency(context));
                     textViewDeliveryToStoreDistance.setText(context.getString(R.string.distance_km,
                           deliveryOffer.getDistanceToStore()));


                     textViewRating.setText(String.valueOf(deliveryUser.getRank()));
                     ratingBar.setRating((float) deliveryUser.getRank());


                     if (TextUtils.isEmpty(deliveryOffer.getOfferComment())) {
                        textViewDeliveryOfferComment.setText(context.getString(R.string.no_comment));
                     } else {
                        textViewDeliveryOfferComment.setText(deliveryOffer.getOfferComment());
                     }

                     if (!showClientInfo) {
                        layoutClientInfo.setVisibility(View.GONE);
                     }

                     deliveryOrder = listener.getOrder();

                     User currentUser = listener.getCurrentUser();

                     if (currentUser.isDeliveryMan()) {
                        if (deliveryOrder.isNew()) {
                           textViewEditOffer.setVisibility(View.VISIBLE);
                           textViewDeleteOffer.setVisibility(View.VISIBLE);
                        }

                     } else {
                        if (deliveryOrder.isNew()) {
                           textViewAcceptOffer.setVisibility(View.VISIBLE);
                        }
                     }

                     textViewDeliverOrderClientName.setText(deliveryOrder.getClientName());
                     textViewDeliveryOrderNumber.setText(String.valueOf(deliveryOrder.getOrderNumber()));
                     textViewDeliveryOrderStatus.setText(deliveryOrder.getOrderStatusString(context));
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
                  }
               });
      }

      private void initCommandsListeners() {
         textViewDeleteOffer.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
               switch (which) {
                  case DialogInterface.BUTTON_POSITIVE:
                     // OK Button pressed
                     listener.deleteOffer(deliveryOfferList.get(getAdapterPosition()));
                     break;

                  case DialogInterface.BUTTON_NEGATIVE:
                     //No button clicked
                     break;
               }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context,
                  R.style.AppCompatAlertDialogStyle));
            builder.setMessage(context.getString(R.string.ui_dialog_are_you_sure_accept_offer))
                  .setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                  .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
         });

         textViewEditOffer.setOnClickListener(
               v -> listener.editOffer(deliveryOfferList.get(getAdapterPosition())));


         textViewAcceptOffer.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
               switch (which) {
                  case DialogInterface.BUTTON_POSITIVE:
                     // OK Button pressed
                     listener.acceptOffer(deliveryOfferList.get(getAdapterPosition()));
                     break;

                  case DialogInterface.BUTTON_NEGATIVE:
                     //No button clicked
                     break;
               }
            };


            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppCompatAlertDialogStyle));
            builder.setMessage(context.getString(R.string.ui_dialog_are_you_sure_accept_offer))
                  .setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                  .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
         });
      }
   }

   public interface OffersRecyclerAdapterListener {
      Settings getSettings();

      DeliveryOrder getOrder();

      void deleteOffer(DeliveryOffer offer);

      void editOffer(DeliveryOffer offer);

      void acceptOffer(DeliveryOffer offer);

      User getCurrentUser();
   }
}
