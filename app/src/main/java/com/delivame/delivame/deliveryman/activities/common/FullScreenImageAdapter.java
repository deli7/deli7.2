package com.delivame.delivame.deliveryman.activities.common;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.delivame.delivame.deliveryman.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class FullScreenImageAdapter extends
      PagerAdapter {
   private final Activity _activity;
   private ArrayList<String> _imagePaths;


   private final ArrayList<String> images;

   // constructor
   public FullScreenImageAdapter(Activity activity,
                                 ArrayList<String> images) {
      this._activity = activity;
      this.images = images;
   }

   @Override
   public int getCount() {
      return images.size();
   }

   @Override
   public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
      return view == object;
   }

   @NonNull
   @Override
   public Object instantiateItem(@NonNull ViewGroup container, int position) {
      ImageView imgDisplay;
      Button btnClose;

      LayoutInflater inflater = (LayoutInflater) _activity
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
            false);

      imgDisplay = viewLayout.findViewById(R.id.imgDisplay);
      btnClose = viewLayout.findViewById(R.id.btnClose);

      Picasso.get()
            .load(images.get(position))
            .error(R.drawable.border)         // optional
            .into(imgDisplay);


      //Bitmap bitmap = MyUtils.loadImageFromSdCard(images.get(position));
      //imgDisplay.setImageBitmap(bitmap);

//        imgDisplay.setImageResource(  _activity.getResources().getIdentifier(images.get(position),
//                "drawable",
//                "projects.freelancer.com.electronicstore"));

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;


//        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
//        imgDisplay.setImageBitmap(bitmap);

      // close button click event
      btnClose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            _activity.finish();
         }
      });

      container.addView(viewLayout);

      return viewLayout;
   }

   @Override
   public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
      container.removeView((RelativeLayout) object);
   }
}