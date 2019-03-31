package com.delivame.delivame.deliveryman.activities.common.Chat;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.common.FullScreenViewActivity;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
   private static final int CHAT_END = 1;
   private static final int CHAT_START = 2;

   private String currency;

   private final List<Chat> messages;
   private boolean isRestaurant;

   /**
    * Called when a view has been clicked.
    */
   ChatAdapter(Activity activity, String currency) {
      this.activity = activity;
      this.currency = currency;
      messages = new ArrayList<>();
   }

   public void setRestaurant(boolean restaurant) {
      isRestaurant = restaurant;
   }

   private Activity activity;

   @NonNull
   @Override
   public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      return new ViewHolder(viewType == CHAT_END ? LayoutInflater.from(activity)
            .inflate(R.layout.list_item_chat_end, parent, false) :
            LayoutInflater.from(activity).inflate(R.layout.list_item_chat_start, parent, false));
   }

   @Override
   public int getItemViewType(int position) {
      if (messages.get(position).getSenderId().equals(MyUtility.getCurrentUserUID())) {
         return CHAT_END;
      }
      return CHAT_START;
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      final Chat chat = messages.get(position);

      if (chat.getMessage().startsWith("https://firebasestorage.")) {
         holder.mImageView.setVisibility(View.VISIBLE);
         holder.messageTV.setVisibility(View.GONE);
         Picasso.get()
               .load(chat.getMessage())
               .placeholder(R.mipmap.ic_user)
               .error(R.mipmap.ic_user)
               .into(holder.mImageView);

         holder.mImageView.setOnClickListener(
               v -> FullScreenViewActivity.startMe(activity, chat.getMessage()));
      } else {
         holder.mImageView.setVisibility(View.GONE);


         switch (chat.getAutoMessageIndex()) {
            case (0):
               String senderName = chat.getSenderName();
               String phoneNumber = chat.getValue0();
               if (phoneNumber != null && isRestaurant) {
                  holder.messageTV.setText(activity.getString(R.string.start_trip_restaurant,
                        phoneNumber, senderName));
               } else if (isRestaurant) {
                  holder.messageTV.setText(activity.getString(R.string.start_trip_restaurant_no_phone,
                        senderName));
               } else {
                  holder.messageTV.setText(activity.getString(R.string.start_trip_other, senderName));
               }
               break;
            case (1):
               holder.messageTV.setText(activity.getString(R.string.invoice_received,
                     chat.getValue0(), currency));
               break;
            case (2):
               String val0 = chat.getValue0();
               String val1 = chat.getValue1();
               double total = Double.parseDouble(val0) + Double.parseDouble(val1);
               holder.messageTV.setText(activity.getString(R.string.Order_received_msg)
                     + " " + activity.getString(R.string.total_fees,
                     chat.getValue0(), chat.getValue1(), String.valueOf(total), currency));
               break;
            case (3):
               holder.messageTV.setText(activity.getString(R.string.Arrived_to_client_msg));
               break;
            case (4):
               holder.messageTV.setText(activity.getString(R.string.order_delivered));
               break;
            default:
               holder.messageTV.setText(chat.getSenderName() + ": " + chat.getMessage());
               break;
         }
      }
   }

   @Override
   public int getItemCount() {
      return messages.size();
   }

   void addItem(Chat chat) {
      messages.add(chat);
      notifyItemInserted(messages.size() - 1);
   }

   /**
    * Inner Class for a recycler view
    */
   class ViewHolder extends RecyclerView.ViewHolder {
      final TextView messageTV;
      final ImageView mImageView;

      ViewHolder(View v) {
         super(v);
         messageTV = itemView.findViewById(R.id.tvMessage);
         mImageView = itemView.findViewById(R.id.imageViewChatImage);
      }
   }
}
