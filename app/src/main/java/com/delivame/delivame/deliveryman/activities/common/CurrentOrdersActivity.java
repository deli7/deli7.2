package com.delivame.delivame.deliveryman.activities.common;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.client.AddNewOrderActivity;
import com.delivame.delivame.deliveryman.activities.client.ClientHomeActivity;
import com.delivame.delivame.deliveryman.activities.client.DeliveryManFeedbackActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.AddNewOfferActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.DeliveryManBaseActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.DeliveryManHomeActivity;
import com.delivame.delivame.deliveryman.adapters.OrdersRecyclerAdapter;
import com.delivame.delivame.deliveryman.adapters.TextTabAdapter;
import com.delivame.delivame.deliveryman.fragments.InProgressOrdersFragment;
import com.delivame.delivame.deliveryman.fragments.NewOrdersFragment;
import com.delivame.delivame.deliveryman.fragments.PreviousOrdersFragment;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.LatLng;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.LocationServiceManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.getLastLocation;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.saveCurrentLocation;

public class CurrentOrdersActivity extends DeliveryManBaseActivity implements
      OrdersRecyclerAdapter.OrdersRecyclerAdapterListener,
      LocationServiceManager.LocationServiceManagerListener,
      InitManager.InitManagerListener, NewOrdersFragment.NewOrdersFragmentListener,
      InProgressOrdersFragment.InProgressOrdersFragmentListener,
      PreviousOrdersFragment.PreviousOrdersFragmentListener {


   @BindView(R.id.tabs)
   TabLayout tabs;
   @BindView(R.id.viewPager)
   ViewPager viewPager;

   private Location mCurrentLocation;
   private LatLng mCurrentLocationLatLng;

   private final List<Fragment> fragmentList = new ArrayList<>();
   private final List<String> titleList = new ArrayList<>();
   private LocationServiceManager locationServiceManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_list_current_orders);
      ButterKnife.bind(this);
      new InitManager().init(CurrentOrdersActivity.this);
   }

   @Override
   protected void onPause() {
      super.onPause();
      locationServiceManager.stopLocationUpdates();
   }

   public static void startMe(Context context, int orderListType) {
      Intent intent = new Intent(context, CurrentOrdersActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra(Constants.ORDER_LIST_TYPE, orderListType);
      context.startActivity(intent);
   }

   private void initUI() {
      initUI(getString(R.string.title_current_orders), true);
      locationServiceManager = new LocationServiceManager(CurrentOrdersActivity.this);
      locationServiceManager.initLocationService();

      mCurrentLocationLatLng = getLastLocation(getApplicationContext());
      if (mCurrentLocationLatLng != null) {
         initTabs();
      }
   }

   private void initFragments() {

      initFragmentInProgressOrders(getString(R.string.ui_label_in_progress_orders));
      if (currentUser.isDeliveryMan()) {
         initFragmentNewsOrders(getString(R.string.ui_label_new_orders));
      }
   }

   private void initFragmentNewsOrders(String title) {
      NewOrdersFragment fragment = new NewOrdersFragment();
      fragmentList.add(fragment);
      titleList.add(title);
   }

   private void initFragmentInProgressOrders(String title) {
      InProgressOrdersFragment fragment = new InProgressOrdersFragment();
      fragmentList.add(fragment);
      titleList.add(title);
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      initUI();
   }

   @Override
   public void makeOffer(DeliveryOrder deliveryOrder) {
      AddNewOfferActivity.startMe(getApplicationContext(), deliveryOrder.getId(), "");
   }

   @Override
   public void viewOrder(DeliveryOrder deliveryOrder) {
      logI(TAG, "deliveryOrder.getId(): " + deliveryOrder.getId());
      OrderActivity.startMe(getApplicationContext(), deliveryOrder.getId());
   }

   @Override
   public void cancelDeliveryOrder(final DeliveryOrder deliveryOrder) {

      final String[] singleChoiceItems = getResources().getStringArray(R.array.CancellationReasons);
      int itemSelected = 0;
      new AlertDialog.Builder(new ContextThemeWrapper(CurrentOrdersActivity.this,
            R.style.AppCompatAlertDialogStyle))
            .setTitle(getString(R.string.ui_button_cancel_order))
            .setSingleChoiceItems(singleChoiceItems, itemSelected, (dialogInterface, selectedIndex)
                  -> deliveryOrder.setCancellationReason(singleChoiceItems[selectedIndex]))
            .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
               deliveryOrder.cancelOrder();
               if (currentUser.isDeliveryMan()) {
                  DeliveryManHomeActivity.startMe(getApplicationContext());
               } else {
                  ClientHomeActivity.startMe(getApplicationContext());
               }
            })
            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
            .show();
   }

   @Override
   public void editDeliveryOrder(DeliveryOrder deliveryOrder) {
      Store store = new Store();
      store.setPlaceName(deliveryOrder.getStoreName());
      store.setVicinity(deliveryOrder.getPickUpAddress());
      store.setLatLng(deliveryOrder.getLatLngPickUpPoint());

      AddNewOrderActivity.startMe(getApplicationContext(),
            store,
            LatLng.fromLocation(mCurrentLocation),
            deliveryOrder.getLatLngDestinationPoint(),
            deliveryOrder.getId());
   }

   @Override
   public void finishDeliveryOrder(DeliveryOrder deliveryOrder) {
      DeliveryManFeedbackActivity.startMe(getApplicationContext(), deliveryOrder.getId());
   }

   @Override
   public User getCurrentUser() {
      return currentUser;
   }

   @Override
   public void updateRecyclerView(List<?> list, RecyclerView.Adapter<?> adapter, String text) {
      super.updateRecyclerView(list, adapter, text);
   }

   @Override
   public LatLng getCurrentLocation() {
      return mCurrentLocationLatLng;
   }

   @Override
   public Settings getSettings() {
      return settings;
   }

   @Override
   public void onPointerCaptureChanged(boolean hasCapture) {
   }

   private boolean isLocationSet = false;

   @Override
   public void processLocation(Location location) {

      mCurrentLocation = location;
      mCurrentLocationLatLng = LatLng.fromLocation(location);
      saveCurrentLocation(getApplicationContext(), location);

      if (!isLocationSet) {
         isLocationSet = true;
         //initTabs();
      }
   }

   private void initTabs() {
      initFragments();
      TextTabAdapter textTabAdapter = new TextTabAdapter(getSupportFragmentManager(), fragmentList, titleList);
      viewPager.setAdapter(textTabAdapter);
      tabs.setupWithViewPager(viewPager);
   }
}
