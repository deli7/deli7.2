package com.delivame.delivame.deliveryman.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.ScreenBox;
import com.delivame.delivame.deliveryman.utilities.Constants;

import java.util.List;

/**
 * Created by Rajat Gupta on 18/05/16.
 */
public class ScreenBoxesAdapter extends RecyclerView.Adapter<ScreenBoxesAdapter.MyViewHolder> {

   private final Context mContext;
   private final List<ScreenBox> albumList;


   public class MyViewHolder extends RecyclerView.ViewHolder {
      final TextView title;
      final TextView count;
      final ImageView thumbnail;
      public ImageView overflow;
      Object clazz;
      final CardView card_view;
      Bundle params;
      String iconUrl;

      MyViewHolder(View view) {
         super(view);
         title = view.findViewById(R.id.title);
         count = view.findViewById(R.id.count);
         card_view = view.findViewById(R.id.card_view);
         thumbnail = view.findViewById(R.id.thumbnail);
         //overflow = (ImageView) view.findViewById(R.userName.overflow);


         thumbnail.setOnClickListener(view1 -> {
            Intent intent = new Intent(mContext, (Class<?>) clazz);
            intent.putExtra(Constants.BUNDLE_PARAMS, params);
            mContext.startActivity(intent);
         });
      }
   }


   public ScreenBoxesAdapter(Context mContext, List<ScreenBox> albumList) {
      this.mContext = mContext;
      this.albumList = albumList;

   }

   @NonNull
   @Override
   public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.album_card, parent, false);

      return new MyViewHolder(itemView);
   }

   private ScreenBox album;
   private MyViewHolder holder;

   @Override
   public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
      this.holder = holder;
      album = albumList.get(position);
      holder.title.setText(album.getName());
      holder.count.setText(album.getSubtitle());
      holder.clazz = album.getClazz();
      holder.params = album.getParams();
      holder.iconUrl = album.getIconUrl();

      if (TextUtils.isEmpty(holder.iconUrl)) {
         // loading album cover using Glide library
         Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
      } else {
         Glide.with(mContext).load(album.getIconUrl()).into(holder.thumbnail);
      }
//
//        holder.overflow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPopupMenu(holder.overflow);
////                Toast.makeText(this, "Thank you for trying this app, Find out more...",Toast.LENGTH_SHORT).show();
//            }
//        });
   }

   /**
    * Showing popup menu when tapping on 3 dots
    */
   private void showPopupMenu(View view) {
      // inflate menu
      PopupMenu popup = new PopupMenu(mContext, view);
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.menu_album, popup.getMenu());
      popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
      popup.show();
      Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
   }

   /**
    * Click listener for popup menu items
    */
   class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

      MyMenuItemClickListener() {
      }

      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
         switch (menuItem.getItemId()) {
            case R.id.action_add_favourite:
               Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
               return true;
            case R.id.action_play_next:
               Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
               return true;
            default:
         }
         return false;
      }
   }

   @Override
   public int getItemCount() {
      return albumList.size();
   }


}
