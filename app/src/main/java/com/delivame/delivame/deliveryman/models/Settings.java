package com.delivame.delivame.deliveryman.models;

import android.content.Context;

import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.delivame.delivame.deliveryman.utilities.Constants.SETTING_NEW_ORDER_LIFETIME;

public class Settings {

   private String currencyEn = "";
   private String currencyAr = "";
   private String terms_link = "";
   private String privacy_policy_link = "";
   private String googleApiKey;
   private String email;
   private String phone1;
   private String phone2;
   private String address;
   private String chargeInstructions;

   private Float search_radius;
   private Float zoom_level;

   private double taxPercent = 0;
   private double deliveryMenOffersRange;
   private double distanceRangeToDelivery;
   private long company_percent = 0;

   private Double lowerCreditAllowence;
   private Long newOrderLifetime;
   private Long maxConcurrentOffers;

   public List<VehicleCategory> vehicleCategories = new ArrayList<>();
   private ArrayList<String> categoriesNames = new ArrayList<>();
   private List<BusinessType> businessTypes = new ArrayList<>();

   public void readBasicSettings(DataSnapshot dataSnapshot, Context context) {
      HashMap<String, Object> settingsMap = (HashMap<String, Object>) dataSnapshot.getValue();

      if (settingsMap != null) {

         //MyUtility.logI(Constants.TAG, "GLOBAL SETTINGS");

         try {
            search_radius = Float.parseFloat(String.valueOf(settingsMap.get(Constants.SETTING_SEARCH_RADIUS)));

            //MyUtility.logI(Constants.TAG, "search_radius:" + search_radius);
         } catch (Exception ex) {
            search_radius = Constants.DEFAULT_SEARCH_RADIUS;
         }

         terms_link = String.valueOf(settingsMap.get(Constants.SETTING_TERMS_AND_CONDITIONS_LINK));
         privacy_policy_link = String.valueOf(settingsMap.get(Constants.SETTING_PRIVACY_POLICY));
         currencyEn = String.valueOf(settingsMap.get(Constants.SETTING_CURRENCY_EN));
         currencyAr = String.valueOf(settingsMap.get(Constants.SETTING_CURRENCY_AR));

         //MyUtility.logI(Constants.TAG, "currencyEn:" + currencyEn);

         taxPercent = Double.parseDouble(String.valueOf(settingsMap.get(Constants.SETTING_TAX)));
         //MyUtility.logI(Constants.TAG, "taxPercent:" + taxPercent);

         deliveryMenOffersRange = MyUtility.readDoubleValue(
               settingsMap.get(Constants.SETTING_DELIVERY_MEN_OFFER_RANGE));

         distanceRangeToDelivery = MyUtility.readDoubleValue(
               settingsMap.get(Constants.SETTING_DISTANCE_RANGE_TO_DELIVERY));

         newOrderLifetime = (Long) settingsMap.get(SETTING_NEW_ORDER_LIFETIME);
         maxConcurrentOffers = (Long) settingsMap.get(Constants.SETTING_MAX_CONCURRENT_OFFERS);
         googleApiKey = String.valueOf(settingsMap.get(Constants.SETTING_GOOGLE_API_KEY));
         lowerCreditAllowence = MyUtility.readDoubleValue(
               settingsMap.get(Constants.SETTING_LOWER_CREDIT_ALLOWENCE));

         company_percent = Long.parseLong(String.valueOf(settingsMap.get(Constants.SETTING_COMPANY_PERCENT)));
         //MyUtility.logI(Constants.TAG, "company_percent:" + company_percent);

         phone1 = String.valueOf(settingsMap.get(Constants.SETTING_COMPANY_PHONE1));
         phone2 = String.valueOf(settingsMap.get(Constants.SETTING_COMPANY_PHONE2));
         email = String.valueOf(settingsMap.get(Constants.SETTING_COMPANY_EMAIL));
         address = String.valueOf(settingsMap.get(Constants.SETTING_COMPANY_ADDRESS));
         chargeInstructions = String.valueOf(settingsMap.get(Constants.SETTING_CHARGE_INSTRUCTIONS));
      } else {
         zoom_level = Constants.DEFAULT_ZOOM_LEVEL;
         search_radius = Constants.DEFAULT_SEARCH_RADIUS;
      }
   }

   public String getChargeInstructions() {
      return chargeInstructions;
   }

   public List<BusinessType> getBusinessTypes() {
      return businessTypes;
   }

   public String getGoogleApiKey() {
      return googleApiKey;
   }

   public void readVehicleCategories(DataSnapshot dataSnapshot) {
      for (DataSnapshot ds : dataSnapshot.getChildren()) {
         String category = String.valueOf(ds.child("category").getValue());
         categoriesNames.add(category);
         double baseFare = MyUtility.getFirbaseDouble(ds.child(Constants.SETTING_ALLOWED_VEHICLE_BASE_FARE));
         double farePerKm = MyUtility.getFirbaseDouble(ds.child(Constants.SETTING_ALLOWED_VEHICLE_FARE_PER_KM));
         double farePerMin = MyUtility.getFirbaseDouble(ds.child(Constants.SETTING_ALLOWED_VEHICLE_FARE_PER_MIN));

         String vehicle_category_icon = "";
         if (ds.child(Constants.SETTING_ALLOWED_VEHICLE_CATEGORY_ICON).getValue() != null) {
            vehicle_category_icon = (String) ds.child(Constants.SETTING_ALLOWED_VEHICLE_CATEGORY_ICON).getValue();
         }

         List<Vehicle> vehicles = new ArrayList<>();
         for (DataSnapshot ds1 : ds.child(Constants.SETTING_VEHICLES).getChildren()) {

            //MyUtility.logI(Constants.TAG, "ds1:" + ds1.toString());

            HashMap<String, String> map = (HashMap<String, String>) ds1.getValue();

            String model = String.valueOf(map != null ? map.get(Constants.SETTING_ALLOWED_VEHICLES_MODEL) : "");
            String year = String.valueOf(map != null ? map.get(Constants.SETTING_ALLOWED_VEHICLES_YEAR) : "");

            String vehicle_icon = "";

            if (map.get(Constants.SETTING_ALLOWED_VEHICLE_ICON) != null) {
               vehicle_icon = Constants.SETTING_ALLOWED_VEHICLE_ICON;
            }
            vehicles.add(new Vehicle(model, year, "", vehicle_icon));
         }

         VehicleCategory vehicleCategory = new VehicleCategory(ds.getKey(), category, baseFare,
               farePerMin, farePerKm, vehicles, vehicle_category_icon);
         vehicleCategories.add(vehicleCategory);
      }
   }

   public Long getNewOrderLifetime() {
      return newOrderLifetime;
   }

   public void readBusinessTypes(DataSnapshot dataSnapshot) {
      for (DataSnapshot ds : dataSnapshot.getChildren()) {
         //logI(Constants.TAG, "DS: " + ds.toString());
         BusinessType businessType = ds.getValue(BusinessType.class);
         businessTypes.add(businessType);
      }
   }

   public Long getMaxConcurrentOffers() {
      return maxConcurrentOffers;
   }

   public ArrayList<String> getCategoriesNames() {
      return categoriesNames;
   }

   public double getDistanceRangeToDelivery() {
      return distanceRangeToDelivery;
   }

   public String findVehicleIcon(String vehicleModel, String vehicleYear) {
      for (VehicleCategory vc :
            vehicleCategories) {
         for (Vehicle v :
               vc.getVehicles()) {
            if (v.getVehicleModelYear().equals(vehicleYear) && v.getVehicleModelName().equals(vehicleModel)) {
               return v.getIcon();
            }
         }
      }

      return "";
   }

   public String getCurrencyEn() {
      switch (currencyEn) {
         case "INR":
            return "\u20B9";
         default:
            return currencyEn;
      }
   }

   public String getAddress() {
      return address;
   }

   public String getTerms_link() {
      return terms_link;
   }

   long getCompany_percent() {
      return company_percent;
   }

   double getTaxPercent() {
      return taxPercent;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public double getDeliveryMenOffersRange() {
      return deliveryMenOffersRange;
   }

   public String getPrivacy_policy_link() {
      return privacy_policy_link;
   }

   public double getLowerCreditAllowence() {
      return lowerCreditAllowence;
   }

   public String getCurrency(Context context) {
      if (LocaleHelper.isLanguageEnglish(context)) {
         return currencyEn;
      } else {
         return currencyAr;
      }
   }
}