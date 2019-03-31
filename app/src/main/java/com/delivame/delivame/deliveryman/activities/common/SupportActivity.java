package com.delivame.delivame.deliveryman.activities.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.activities.common.Messaging.ListSupportMessagesActivity;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.ContactHelper;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class SupportActivity extends BaseActivity implements InitManager.InitManagerListener {

   @BindView(R.id.buttonUsagePolicy)
   Button buttonUsagePolicy;
   @BindView(R.id.buttonRateApp)
   Button buttonRateApp;
   @BindView(R.id.buttonTermsOfUse)
   Button buttonTermsOfUse;
   @BindView(R.id.btn_telegram)
   ImageView BtnTelegram;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contact_us);
      ButterKnife.bind(this);
      new InitManager().init(SupportActivity.this);
   }


   private void initUI() {

      initUI(getString(R.string.ui_label_support), true);

      BtnTelegram.setOnClickListener(view -> {
         String url = "https://t.me/joinchat/AAAAAE_sUCZTpHnIS2XZxg";
         Intent i = new Intent(Intent.ACTION_VIEW);
         i.setData(Uri.parse(url));
         startActivity(i);
      });

//        ImageView imageViewPhone1 = findViewById(R.id.imageViewPhone1);
//        imageViewPhone1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ContactHelper.makePhoneCall(getApplicationContext(), settings.getPhone1());
//            }
//        });
//        ImageView imageViewPhone2 = findViewById(R.id.imageViewPhone2);
//        imageViewPhone2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ContactHelper.makePhoneCall(getApplicationContext(), settings.getPhone2());
//            }
//        });

//        textViewCompanyAddress.setText(settings.getAddress());
//        textViewCompanyEmail.setText(settings.getEmail());
//        textViewCompanyPhone1.setText(settings.getPhone1());
//        textViewCompanyPhone2.setText(settings.getPhone2());
//
//        ImageView imageViewEmail = findViewById(R.id.imageViewEmail);
//        imageViewEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ContactHelper.sendEmail(getApplicationContext(), currentUser,
//                        settings.getEmail());
//            }
//        });


   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      initUI();

      this.settings = settings;
      this.currentUser = currentUser;
      if (currentUser.isDeliveryMan()) {
         BtnTelegram.setVisibility(View.VISIBLE);
      } else {
         BtnTelegram.setVisibility(View.GONE);

      }
   }


   @OnClick({R.id.buttonUsagePolicy, R.id.buttonRateApp, R.id.buttonChatWithSupport, R.id.buttonTermsOfUse})
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.buttonUsagePolicy:
            logI(TAG, "buttonUsagePolicy");
            WebContentActivity.startMe(getApplicationContext(), settings.getPrivacy_policy_link(), getString(R.string.ui_button_policy));
            break;

         case R.id.buttonRateApp:
            break;

         case R.id.buttonChatWithSupport:
            ListSupportMessagesActivity.startMe(getApplicationContext());
            break;

         case R.id.buttonTermsOfUse:
            logI(TAG, "buttonTermsOfUse");
            WebContentActivity.startMe(getApplicationContext(), settings.getTerms_link(), getString(R.string.ui_terms_of_use));
            break;
      }
   }
}
