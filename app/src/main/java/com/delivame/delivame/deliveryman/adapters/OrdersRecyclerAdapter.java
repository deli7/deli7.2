package com.delivame.delivame.deliveryman.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.googleapis.GetDistanceAndTimeData;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;


public class OrdersRecyclerAdapter extends RecyclerView.Adapter<OrdersRecyclerAdapter.MyPlaceViewHolder> {

   private final List<DeliveryOrder> deliveryOrderList;
   private final Context context;
   private final LayoutInflater layoutInflater;

   private final OrdersRecyclerAdapterListener listener;


   public OrdersRecyclerAdapter(Context context, List<DeliveryOrder> deliveryOrderList) {
      this.context = context;
      this.deliveryOrderList = deliveryOrderList;
      layoutInflater = LayoutInflater.from(context);
      listener = (OrdersRecyclerAdapterListener) context;
   }

   @NonNull
   @Override
   public MyPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view;


      view = layoutInflater.inflate(R.layout.list_item_order, parent, false);

      return new MyPlaceViewHolder(view);
   }


   @Override
   public void onBindViewHolder(@NonNull MyPlaceViewHolder holder, int position) {

      DeliveryOrder store = deliveryOrderList.get(position);
      holder.setData(store);
   }

   @Override
   public int getItemCount() {
      return deliveryOrderList.size();
   }


   class MyPlaceViewHolder extends RecyclerView.ViewHolder
         implements GetDistanceAndTimeData.GetDistanceAndTimeDataListener {

      @BindView(R.id.textViewPickUpAddress)
      TextView textViewPickUpAddress;
      @BindView(R.id.textViewDestinationAddress)
      TextView textViewDestinationAddress;
      @BindView(R.id.textViewOrderInstructions)
      TextView textViewOrderInstructions;
      @BindView(R.id.textViewEstimatedDistance)
      TextView textViewEstimatedDistance;
      @BindView(R.id.textViewEstimatedTime)
      TextView textViewEstimatedTime;
      @BindView(R.id.textViewRequiredTime)
      TextView textViewRequiredTime;
      @BindView(R.id.textViewOrderStatus)
      TextView textViewOrderStatus;
      @BindView(R.id.textViewDeliveryToStoreDistance)
      TextView textViewDeliveryToStoreDistance;
      @BindView(R.id.linearLayoutDistanceToStore)
      LinearLayout linearLayoutDistanceToStore;

      @BindView(R.id.textViewViewOrder)
      TextView textViewViewOrder;

      @BindView(R.id.textViewMakeOffer)
      TextView textViewMakeOffer;

      @BindView(R.id.textViewEditOrder)
      TextView textViewEditOrder;

      @BindView(R.id.textViewCancelOrder)
      TextView textViewCancelOrder;

      @BindView(R.id.textViewFinishOrder)
      TextView textViewFinishOrder;
      @BindView(R.id.textViewOrderNumber)
      TextView textViewOrderNumber;
      @BindView(R.id.linearLayoutOrder)
      LinearLayout linearLayoutOrder;

      MyPlaceViewHolder(@NonNull View itemView) {
         super(itemView);

         ButterKnife.bind(this, itemView);


         textViewViewOrder.setOnClickListener(v -> {
            logI(Constants.TAG, "textViewViewOrder: " + getAdapterPosition());
            listener.viewOrder(deliveryOrderList.get(getAdapterPosition()));
         });

         linearLayoutOrder.setOnClickListener(
               v -> listener.viewOrder(deliveryOrderList.get(getAdapterPosition())));

         textViewMakeOffer.setOnClickListener(
               v -> listener.makeOffer(deliveryOrderList.get(getAdapterPosition())));

         textViewCancelOrder.setOnClickListener(v -> cancelOrder());

         textViewEditOrder.setOnClickListener(
               v -> listener.editDeliveryOrder(deliveryOrderList.get(getAdapterPosition())));

         textViewFinishOrder.setOnClickListener(v -> finishOrder());
      }

      private void cancelOrder() {
         DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
               case DialogInterface.BUTTON_POSITIVE:
                  listener.cancelDeliveryOrder(deliveryOrderList.get(getAdapterPosition()));
                  break;

               case DialogInterface.BUTTON_NEGATIVE:
                  //No button clicked
                  break;
            }
         };


         AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context,
               R.style.AppCompatAlertDialogStyle));
         builder.setMessage(context.getString(R.string.ui_dialog_are_you_sure_delete_order))
               .setPositiveButton(context.getString(R.string.yes), dialogClickListener)
               .setNegativeButton(context.getString(R.string.no), dialogClickListener)
               .show();
      }

      private void finishOrder() {
         listener.finishDeliveryOrder(deliveryOrderList.get(getAdapterPosition()));
      }

      DeliveryOrder deliveryOrder;

      void setData(@NonNull final DeliveryOrder deliveryOrder) {

         this.deliveryOrder = deliveryOrder;
         if (false) {
            textViewPickUpAddress.setText(deliveryOrder.getStoreName() + " - " + deliveryOrder.getPickUpAddress());
            textViewDestinationAddress.setText(deliveryOrder.getDestinationAddress());
            textViewRequiredTime.setText(String.valueOf(deliveryOrder.getRequiredTime()));
            textViewEstimatedDistance.setText(String.valueOf(deliveryOrder.getDistanceToClient()));
            textViewEstimatedTime.setText(String.valueOf(deliveryOrder.getEstimatedTime()));
            textViewOrderInstructions.setText(String.valueOf(deliveryOrder.getInstructions()));
            textViewOrderStatus.setText(deliveryOrder.getOrderStatusString(context));
            textViewOrderNumber.setText(String.valueOf(deliveryOrder.getOrderNumber()));
            textViewRequiredTime.setText(context.getString(R.string.time_hour, deliveryOrder.getRequiredTime()));
         }

         if (deliveryOrder.isNew()) {
            getDistanceToStore();
         } else {
            setOrder(deliveryOrder.getDistanceToStore());
         }

         User currentUser = listener.getCurrentUser();

         textViewViewOrder.setVisibility(View.VISIBLE);
         if (currentUser.isDeliveryMan()) {
            if (deliveryOrder.isNew()) {
               textViewMakeOffer.setVisibility(View.VISIBLE);

               MyUtility.getUserOffersNodeRef()
                     .child(listener.getCurrentUser().getUID())
                     .child(deliveryOrder.getId())
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           if (dataSnapshot.getValue() != null) {
                              //isEditOffer = true;
                              textViewMakeOffer.setVisibility(View.GONE);
                              //textViewMakeOffer.setText(context.getString(R.string.ui_button_edit_offer));
                           }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                     });
            }
         }


         if (false) {
            if (currentUser.isDeliveryMan()) {
               textViewFinishOrder.setVisibility(View.GONE);
               textViewCancelOrder.setVisibility(View.GONE);
               textViewEditOrder.setVisibility(View.GONE);
               if (deliveryOrder.isInProgress()) {
                  textViewMakeOffer.setVisibility(View.GONE);
               } else if (deliveryOrder.isCompleted()) {
                  textViewMakeOffer.setVisibility(View.GONE);
                  textViewViewOrder.setVisibility(View.GONE);
               } else {
                  MyUtility.getUserOffersNodeRef()
                        .child(listener.getCurrentUser().getUID())
                        .child(deliveryOrder.getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if (dataSnapshot.getValue() != null) {
                                 textViewMakeOffer.setText(context.getString(R.string.ui_button_edit_offer));
                              }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                        });

                  // getDistanceToStore();
                  textViewDeliveryToStoreDistance.setText(context.getString(R.string.distance_km,
                        deliveryOrder.getDistanceToStore()));
               }
            } else {
               linearLayoutDistanceToStore.setVisibility(View.GONE);
               textViewMakeOffer.setVisibility(View.GONE);

               //if (!deliveryOrder.isNew()) {
               textViewEditOrder.setVisibility(View.GONE);
               textViewCancelOrder.setVisibility(View.GONE);
               //}


               if (deliveryOrder.isCompleted() || deliveryOrder.isNew()) {
                  textViewFinishOrder.setVisibility(View.GONE);
               }
            }
         }
      }

      private void getDistanceToStore() {
         DeliveryOrder deliveryOrder = deliveryOrderList.get(getAdapterPosition());
         Store store = new Store();
         store.setLatLng(deliveryOrder.getLatLngPickUpPoint());
         List<Store> storeList = new ArrayList<>();
         storeList.add(store);

         GetDistanceAndTimeData getDistanceAndTimeData = new GetDistanceAndTimeData(storeList,
               listener.getCurrentLocation(),
                     listener.getSettings().getGoogleApiKey());
         Object[] DataTransfer = new Object[3];
         DataTransfer[0] = this;
         DataTransfer[1] = context;

         getDistanceAndTimeData.execute(DataTransfer);
      }

      double distanceToStore = 0.0;

      @Override
      public void setEstimatedDistanceAndTime(double distance, double time) {
         //textViewDeliveryToStoreDistance.setText(String.valueOf(distance) + " " + context.getString(R.string.km));
         distanceToStore = distance;
         setOrder(distance);

         deliveryOrder.setDistanceToStore(distanceToStore);
      }

      private void setOrder(Double distance) {
         textViewOrderNumber.setText(String.valueOf(deliveryOrder.getOrderNumber()));

         String order = context.getString(R.string.new_order_from) +
               " " +
               deliveryOrder.getStore().getPlaceName() +
               " (" +
               MyUtility.roundDouble2(deliveryOrder.getDistanceToClient()) +
               " " + context.getString(R.string.km) +
               " + " + MyUtility.roundDouble2(distance) + " " + context.getString(R.string.km) + ")";

         textViewPickUpAddress.setText(order);
      }

      @Override
      public void setListEstimatedDistanceAndTime(List<Double> distance, List<Double> time) {
      }
   }

   public interface OrdersRecyclerAdapterListener {
      void makeOffer(DeliveryOrder deliveryOrder);

      void viewOrder(DeliveryOrder deliveryOrder);

      void cancelDeliveryOrder(DeliveryOrder deliveryOrder);

      void editDeliveryOrder(DeliveryOrder deliveryOrder);

      void finishDeliveryOrder(DeliveryOrder deliveryOrder);

      User getCurrentUser();

      LatLng getCurrentLocation();

      Settings getSettings();
   }
}
