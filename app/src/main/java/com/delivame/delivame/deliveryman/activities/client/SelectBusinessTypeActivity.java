package com.delivame.delivame.deliveryman.activities.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.ScreenBox;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectBusinessTypeActivity extends ClientBaseActivity
      implements InitManager.InitManagerListener {

   @BindView(R.id.recyclerView)
   RecyclerView recyclerView;

   @BindView(R.id.editTextSearch)
   EditText edSearch;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_select_business_type);
      ButterKnife.bind(this);

      new InitManager().init(SelectBusinessTypeActivity.this);
   }

   public static void startMe(Context context) {
      Intent intent = new Intent(context, SelectBusinessTypeActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }


   private void initUI() {
      initUI(getString(R.string.ui_activity_title_select_business), true);
      initGridRecyclerView(recyclerView, prepareScreenBoxes());
      edSearch.setOnClickListener(view -> {
         Bundle params = new Bundle();
         params.putString(Constants.BUNDLE_PARAMS_BUSINESS_TYPE, "All");

         ScreenBox screenBox = new ScreenBox("All", "All", "",
               SelectStoreActivity.class, params);
         Intent intent = new Intent(SelectBusinessTypeActivity.this,
               (Class<?>) screenBox.getClazz());
         intent.putExtra(Constants.BUNDLE_PARAMS, params);
         startActivity(intent);
      });
   }

   private List<ScreenBox> prepareScreenBoxes() {

      List<ScreenBox> screenBoxList = new ArrayList<>();

      String type;
      String title;
      String subtitle;
      String icon;

      for (int i = 0; i < settings.getBusinessTypes().size(); i++) {
         type = settings.getBusinessTypes().get(i).getPlace_name();

         if (LocaleHelper.isLanguageEnglish(getApplicationContext())) {
            title = settings.getBusinessTypes().get(i).getTitle_en();
            subtitle = settings.getBusinessTypes().get(i).getSubtitle_en();
         } else {
            title = settings.getBusinessTypes().get(i).getTitle_ar();
            subtitle = settings.getBusinessTypes().get(i).getSubtitle_ar();
         }

         icon = settings.getBusinessTypes().get(i).getIcon();

         screenBoxList.add(makeBusinessTypeScreenBox(type, title, subtitle, icon));
      }

      return screenBoxList;
   }

   private ScreenBox makeBusinessTypeScreenBox(String type, String title, String subtitle, String icon) {
      Bundle params = new Bundle();
      params.putString(Constants.BUNDLE_PARAMS_BUSINESS_TYPE, type);

      return new ScreenBox(title, subtitle, icon, SelectStoreActivity.class, params);
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;
      initUI();
   }
}
