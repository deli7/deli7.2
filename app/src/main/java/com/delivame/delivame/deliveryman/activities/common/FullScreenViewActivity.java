package com.delivame.delivame.deliveryman.activities.common;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.delivame.delivame.deliveryman.R;

import java.util.ArrayList;
import java.util.List;

public class FullScreenViewActivity extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_fullscreen_view);

      ViewPager viewPager = findViewById(R.id.pager);

      Intent i = getIntent();
      int position = i.getIntExtra("position", 0);

      ArrayList<String> imagesList = i.getStringArrayListExtra("images");

      FullScreenImageAdapter adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
            imagesList);

      viewPager.setAdapter(adapter);

      // displaying selected number first
      viewPager.setCurrentItem(position);
   }

   public static void startMe(Context context, String url) {

      Intent intent = new Intent(context, FullScreenViewActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      List<String> imagesList = new ArrayList<>();
      imagesList.add(url);
      intent.putStringArrayListExtra("images", (ArrayList<String>) imagesList);
      context.startActivity(intent);
   }
}
