package com.delivame.delivame.deliveryman.activities.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.activities.client.DeliveryManFeedbackActivity;
import com.delivame.delivame.deliveryman.adapters.OrdersRecyclerAdapter;
import com.delivame.delivame.deliveryman.fragments.PreviousOrdersFragment;
import com.delivame.delivame.deliveryman.fragments.UserCommentFragment;
import com.delivame.delivame.deliveryman.fragments.UserSettingsFragment;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.UserComment;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.adapters.TextTabAdapter;
import com.delivame.delivame.deliveryman.fragments.UserProfileFragment;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity
        implements UserProfileFragment.UserProfileFragmentListener,
        OrdersRecyclerAdapter.OrdersRecyclerAdapterListener,
        InitManager.InitManagerListener, UserCommentFragment.OnListFragmentInteractionListener,
        UserSettingsFragment.OnFragmentInteractionListener,
        PreviousOrdersFragment.PreviousOrdersFragmentListener {

   @BindView(R.id.tabs)
   TabLayout tabs;
   @BindView(R.id.viewPager)
   ViewPager viewPager;
   private User currentUser;


   private final List<Fragment> fragmentList = new ArrayList<>();
   private final List<String> titleList = new ArrayList<>();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_client_profile);
      ButterKnife.bind(this);

      new InitManager().init(ProfileActivity.this);
   }

   public static void startMe(Context context) {
      Intent intent = new Intent(context, ProfileActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }


   private void initUI() {

      initUI(getString(R.string.personal_profile), true);

      initFragments();

      TextTabAdapter textTabAdapter = new TextTabAdapter(getSupportFragmentManager(), fragmentList, titleList);
      viewPager.setAdapter(textTabAdapter);

      tabs.setupWithViewPager(viewPager);
   }

   private void initFragments() {

      initFragmentUserProfile(getString(R.string.ui_label_client_profile), Constants.STATISTICS_TYPE_MONTHLY);

      if (currentUser.isDeliveryMan()) {
         UserCommentFragment userCommentFragment = UserCommentFragment.newInstance(1);
         fragmentList.add(userCommentFragment);
         titleList.add(getString(R.string.ui_label_clients_feedback));


      }

      UserSettingsFragment userSettingsFragment = UserSettingsFragment.newInstance();
      fragmentList.add(userSettingsFragment);
      titleList.add(getString(R.string.ui_label_user_settings));

      PreviousOrdersFragment previousOrdersFragment = PreviousOrdersFragment.newInstance();
      fragmentList.add(previousOrdersFragment);
      titleList.add(getString(R.string.ui_label_previous_orders));

   }

   private void initFragmentUserProfile(String title, int type) {
      UserProfileFragment fragment = new UserProfileFragment();
      fragmentList.add(fragment);
      titleList.add(title);
   }


   @Override
   public User getCurrentUser() {
      return currentUser;
   }

   @Override
   public void updateRecyclerView(List<?> list, RecyclerView.Adapter<?> adapter, String text) {

   }

   @Override
   public LatLng getCurrentLocation() {
      return null;
   }

   @Override
   public Settings getSettings() {
      return settings;
   }

   @Override
   public void makeOffer(DeliveryOrder deliveryOrder) {

   }

   @Override
   public void viewOrder(DeliveryOrder deliveryOrder) {
      OrderActivity.startMe(getApplicationContext(), deliveryOrder.getId());
   }

   @Override
   public void cancelDeliveryOrder(DeliveryOrder deliveryOrder) {

   }

   @Override
   public void editDeliveryOrder(DeliveryOrder deliveryOrder) {

   }

   @Override
   public void finishDeliveryOrder(DeliveryOrder deliveryOrder) {
      DeliveryManFeedbackActivity.startMe(getApplicationContext(), deliveryOrder.getId());
   }


   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      initUI();
   }

   @Override
   public void onListFragmentInteraction(UserComment item) {

   }

   @Override
   public void onFragmentInteraction(Uri uri) {

   }
}
