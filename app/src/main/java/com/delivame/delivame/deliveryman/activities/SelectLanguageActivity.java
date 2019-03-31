package com.delivame.delivame.deliveryman.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectLanguageActivity extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_select_language);
      ButterKnife.bind(this);
   }

   public static void startMe(Context context) {
      Intent intent = new Intent(context, SelectLanguageActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }

   @OnClick({R.id.buttonArabic, R.id.buttonEnglish})
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.buttonArabic:
            LocaleHelper.setLocale(getApplicationContext(), "ar");
            IntroWizardActivity.startMe(getApplicationContext());
            finish();
            break;

         case R.id.buttonEnglish:
            LocaleHelper.setLocale(getApplicationContext(), "en_us");
            IntroWizardActivity.startMe(getApplicationContext());
            finish();
            break;
      }
   }
}
