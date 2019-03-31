package com.delivame.delivame.deliveryman.old_activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

   private final Activity context;

   public CustomInfoWindowAdapter(Activity context) {
      this.context = context;
   }

   @Nullable
   @Override
   public View getInfoWindow(Marker marker) {
      return null;
   }

   @Override
   public View getInfoContents(@NonNull Marker marker) {
      final ViewGroup nullParent = null;

      View view = context.getLayoutInflater().inflate(R.layout.custom_info_window, nullParent);

      TextView tvTitle = view.findViewById(R.id.tv_title);
      TextView tvSubTitle = view.findViewById(R.id.tv_subtitle);


      tvTitle.setText(marker.getTitle());
      tvSubTitle.setText(marker.getSnippet());

      return view;
   }
}
