package com.delivame.delivame.deliveryman.old_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.models.Vehicle;
import com.delivame.delivame.deliveryman.models.VehicleCategory;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectVehicleActivity extends BaseActivity implements InitManager.InitManagerListener {

   @BindView(R.id.spinnerVechicleCategories)
   Spinner spinnerVechicleCategories;
   @BindView(R.id.spinnerVehicles)
   Spinner spinnerVehicles;
   @BindView(R.id.textViewBaseFare)
   TextView textViewBaseFare;
   @BindView(R.id.textViewFarePerKm)
   TextView textViewFarePerKm;
   @BindView(R.id.textViewFarePerMin)
   TextView textViewFarePerMin;
   @BindView(R.id.buttonSelect)
   Button buttonSelect;


   private final List<String> vehicles = new ArrayList<>();
   @Nullable
   private VehicleCategory vehicleCategory = null;
   @Nullable
   private Vehicle vehicle = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_select_vehicle);
      ButterKnife.bind(this);

      new InitManager().init(SelectVehicleActivity.this);

   }


   private void updateUI() {

      ArrayAdapter<String> adapter = new ArrayAdapter(SelectVehicleActivity.this,
            android.R.layout.simple_spinner_item, settings.getCategoriesNames());

      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      spinnerVechicleCategories.setAdapter(adapter);

      final ArrayAdapter<String> adapter2 = new ArrayAdapter<>(SelectVehicleActivity.this,
            android.R.layout.simple_spinner_item, vehicles);

      adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

      spinnerVehicles.setAdapter(adapter2);


      spinnerVechicleCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            vehicleCategory = settings.vehicleCategories.get(i);

            vehicles.clear();

            for (int j = 0; j < (vehicleCategory != null ? vehicleCategory.getVehicles().size() : 0); j++) {
               vehicles.add(vehicleCategory.getVehicles().get(j).getVehicleString());
            }

            adapter2.notifyDataSetChanged();

            textViewBaseFare.setText(String.valueOf(vehicleCategory.getBaseFare()) + " "
                  + settings.getCurrency(getApplicationContext()));
            textViewFarePerKm.setText(String.valueOf(vehicleCategory.getFarePerKm()) + " "
                  + settings.getCurrency(getApplicationContext()));
            textViewFarePerMin.setText(String.valueOf(vehicleCategory.getFarePerMin()) + " "
                  + settings.getCurrency(getApplicationContext()));

         }

         @Override
         public void onNothingSelected(AdapterView<?> adapterView) {

         }
      });


      spinnerVehicles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            if (vehicleCategory == null) {
               MyUtility.logI(Constants.TAG, "vehicleCategory = null");
               return;
            }

            vehicle = vehicleCategory.getVehicles().get(i);

            textViewBaseFare.setText(String.valueOf(vehicleCategory.getBaseFare()) + " "
                  + settings.getCurrency(getApplicationContext()));
            textViewFarePerKm.setText(String.valueOf(vehicleCategory.getFarePerKm()) + " "
                  + settings.getCurrency(getApplicationContext()));
            textViewFarePerMin.setText(String.valueOf(vehicleCategory.getFarePerMin()) + " "
                  + settings.getCurrency(getApplicationContext()));
         }

         @Override
         public void onNothingSelected(AdapterView<?> adapterView) {

         }
      });


   }

   private void initUI() {

      initUI(getString(R.string.ui_activity_title_select_vehicle), true);

      buttonSelect.setOnClickListener(view -> {

         int index1 = spinnerVechicleCategories.getSelectedItemPosition();
         int index2 = spinnerVehicles.getSelectedItemPosition();

         MyUtility.logI(Constants.TAG, "index1:" + index1);
         MyUtility.logI(Constants.TAG, "index2:" + index2);

         if (index1 >= 0 && index2 >= 0) {

            vehicleCategory = settings.vehicleCategories.get(index1);
            vehicle = vehicleCategory.getVehicles().get(index2);

            Intent intent = new Intent();
            intent.putExtra(Constants.SETTING_ALLOWED_VEHICLES_MODEL, vehicle != null ?
                  vehicle.getVehicleModelName() : "");
            intent.putExtra(Constants.SETTING_ALLOWED_VEHICLES_YEAR, vehicle.getVehicleModelYear());
            intent.putExtra(Constants.FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_ID, vehicleCategory.getId());
            intent.putExtra(Constants.FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_NAME, vehicleCategory.getCategory());

            setResult(RESULT_OK, intent);

            finish();
         }
      });

      updateUI();
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.currentUser = currentUser;
      this.settings = settings;

      initUI();
   }
}
