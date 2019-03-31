package com.delivame.delivame.deliveryman.models;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Order {

    private String orderNumber = "";
    private String orderUserRequestTime = "";
    private String orderDriverResponseTime = "";
    private String orderTripStartTime = "";
    private String orderTripEndTime = "";
    private int orderStatus = 0;
    private String userKey = "";
    private String driverKey = "";
    private double estimatedTime = 0.0;
    private double estimatedDistance = 0.0;
    private User user;
    private DatabaseReference orderRef;
    private String userFullname = "";
    private String driverFullname = "";

    private double tripRating = 0;
    private int tripStars = 0;
    private String tripFeedback = "";
    private String vehicleCategoryId = "";
    private String vehicleCategoryName = "";
    private double base_fare = 0;
    private double fare_per_km = 0;
    private double fare_per_min = 0;
    private String vehicle_number = "";
    private String vehicle_model = "";
    private String vehicle_year = "";


    private double tripDistance = 0;
    private double tripTime = 0;

    private List<TripPoint> tripPoints = new ArrayList<>();
    private List<TripPoint> driverResponseTripPoints = new ArrayList<>();

    private String destinationTitle = "";
    private double estimatedCost = 0;
    private double driverEarning;
    private double companyEarning;
    private double tripCost = 0;
    private double taxPercent = 0;
    private LatLng pickupPoint;
    private String pickUpTitle = "";
    private boolean isRoundTrip = false;

    public void setTaxPercent(double taxPercent) {
        this.taxPercent = taxPercent;
    }

    @Nullable
    public LatLng getDestinationPoint() {
        return destinationPoint;
    }

    private LatLng destinationPoint = null;


    public void addTripPoint(@NonNull Location location) {
        TripPoint tripPoint = new TripPoint(location.getLatitude(), location.getLongitude(), location.getBearing());

        tripPoints.add(tripPoint);

//        MyUtility.getOrdersNodeRef().child(orderUserRequestTime).child(Constants.FIREBASE_KEY_ORDER_TRIP_POINTS)
//                .child(DateTimeUtil.getCurrentDateTime()).setValue(tripPoint.toHashMap());


    }

    public void addDriverResponseTripPoint(@NonNull Location location) {
        TripPoint tripPoint = new TripPoint(location.getLatitude(), location.getLongitude(), location.getBearing());

        driverResponseTripPoints.add(tripPoint);

//        MyUtility.getOrdersNodeRef().child(orderUserRequestTime).child(Constants.FIREBASE_KEY_ORDER_DRIVER_RESPONSE_TRIP_POINTS)
//                .child(DateTimeUtil.getCurrentDateTime()).setValue(tripPoint.toHashMap());
    }

    public Order() {
        tripPoints = new ArrayList<>();
        orderStatus = Constants.ORDER_STATUS_IDLE;
    }

    public Order(@NonNull DataSnapshot dataSnapshot) {
        try {
            orderStatus = Constants.ORDER_STATUS_IDLE;

            tripPoints = new ArrayList<>();

            fromHashMap(dataSnapshot);

            orderRef = MyUtility.getOrdersNodeRef().child(orderUserRequestTime);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private DatabaseReference getOrderRef() {
        return MyUtility.getOrdersNodeRef().child(orderUserRequestTime);
    }


    public void newOrder(@Nullable User user, String scheduledTime, boolean isRoundTrip) {

        if (user == null) {
            MyUtility.logI(Constants.TAG, "user == null");
            return;
        }
        tripPoints = new ArrayList<>();
        if (TextUtils.isEmpty(scheduledTime)) {
            orderUserRequestTime = DateTimeUtil.getCurrentDateTime();
        } else {
            orderUserRequestTime = scheduledTime;
        }

        if (TextUtils.isEmpty(orderUserRequestTime)) {
            MyUtility.logI(Constants.TAG, "Invalid request time");
            return;
        }

        userKey = user.getUID();
        MyUtility.logI(Constants.TAG, "orderStatus: " + orderStatus);
        if (orderStatus == Constants.ORDER_STATUS_IDLE) {
            orderStatus = Constants.ORDER_STATUS_USER_REQUESTED_TAXI;

            destinationPoint = user.getDestinationPoint();

            destinationTitle = user.getDestinationTitle();

            pickupPoint = user.getPickUpPoint();
            pickUpTitle = user.getPickUpTitle();



            estimatedTime = user.getDestinationTime();
            estimatedDistance = user.getDestinationDistance();

            estimatedCost = MyUtility.calculateTripCostWithTax(estimatedDistance,
                    estimatedTime, getBase_fare(), getFare_per_km(), getFare_per_min(),getTaxPercent());

            MyUtility.logI(Constants.TAG, "estimatedCost:" + estimatedCost);
            MyUtility.logI(Constants.TAG, "estimatedTime:" + estimatedTime);
            MyUtility.logI(Constants.TAG, "estimatedDistance:" + estimatedDistance);

            this.isRoundTrip = isRoundTrip;

            this.user = user;
            userFullname = user.getFullName();

            orderRef = MyUtility.getOrdersNodeRef().child(orderUserRequestTime);

            saveOrder();
        }

    }


    private double getTaxPercent() {
        return taxPercent;
    }

    @Nullable
    private User driver = null;

    public void respond(@NonNull User driver, @NonNull User user, double taxPercent) {
        if (orderStatus == Constants.ORDER_STATUS_USER_REQUESTED_TAXI) {
            orderDriverResponseTime = DateTimeUtil.getCurrentDateTime();
            driverKey = driver.getUID();
            orderStatus = Constants.ORDER_STATUS_DRIVER_RESPONDED_AND_ON_THE_WAY;
            userKey = user.getUID();
            this.user = user;
            this.driver = driver;
            this.taxPercent = taxPercent;
            this.vehicle_number = driver.getVehicle().vehicleNumber;
            this.vehicle_year = driver.getVehicle().vehicleModelYear;
            this.vehicle_model = driver.getVehicle().vehicleModelName;
            this.vehicleCategoryName = driver.getVehicleCategory().category;
            this.vehicleCategoryId = driver.getVehicleCategory().id;
            this.base_fare = driver.getVehicleCategory().baseFare;
            this.fare_per_km = driver.getVehicleCategory().farePerKm;
            this.fare_per_min = driver.getVehicleCategory().farePerMin;

            //logOrder();
            saveOrder();
        }
    }

    public void driverResponded(@NonNull User driver) {
        MyUtility.logI(Constants.TAG, "driverResponded");
        if (orderStatus == Constants.ORDER_STATUS_USER_REQUESTED_TAXI) {
            orderDriverResponseTime = DateTimeUtil.getCurrentDateTime();
            driverKey = driver.getUID();


            orderStatus = Constants.ORDER_STATUS_DRIVER_RESPONDED_AND_ON_THE_WAY;

            this.vehicle_number = driver.getVehicle().vehicleNumber;
            this.vehicle_year = driver.getVehicle().vehicleModelYear;
            this.vehicle_model = driver.getVehicle().vehicleModelName;
            this.vehicleCategoryName = driver.getVehicleCategory().category;
            this.vehicleCategoryId = driver.getVehicleCategory().id;
            this.base_fare = driver.getVehicleCategory().baseFare;
            this.fare_per_km = driver.getVehicleCategory().farePerKm;
            this.fare_per_min = driver.getVehicleCategory().farePerMin;

            saveOrder();
        }else{
            MyUtility.logI(Constants.TAG, "ERROR: Invalid State");
        }
    }

    public void requestTripStart() {
        MyUtility.logI(Constants.TAG1, "requestTripStart");
        MyUtility.logI(Constants.TAG, "orderStatus1: " + orderStatus);

        if (orderStatus == Constants.ORDER_STATUS_DRIVER_RESPONDED_AND_ON_THE_WAY) {
            orderTripStartTime = DateTimeUtil.getCurrentDateTime();
            orderStatus = Constants.ORDER_STATUS_TRIP_WAITING_USER_APPROVAL_TO_START;
            MyUtility.logI(Constants.TAG, "orderStatus2: " + orderStatus);

            saveOrder();
        }
    }

    public void confirmTripStarted() {
        MyUtility.logI(Constants.TAG1, "confirmTripStarted");
        if (orderStatus == Constants.ORDER_STATUS_TRIP_WAITING_USER_APPROVAL_TO_START) {
            orderTripStartTime = DateTimeUtil.getCurrentDateTime();
            orderStatus = Constants.ORDER_STATUS_TRIP_STARTED;

            saveOrder();
        }
    }

    public void requestTripEnd() {
        MyUtility.logI(Constants.TAG, "sendTripEndRequest");
        //if (orderStatus == Constants.ORDER_STATUS_TRIP_STARTED) {
        orderTripEndTime = DateTimeUtil.getCurrentDateTime();
        orderStatus = Constants.ORDER_STATUS_TRIP_END;
        //saveOrder();

        getOrderRef().child(Constants.FIREBASE_KEY_ORDER_TRIP_END_TIME).setValue(orderTripEndTime);
        getOrderRef().child(Constants.FIREBASE_KEY_ORDER_STATUS).setValue(Constants.ORDER_STATUS_TRIP_END);


        //}
    }

    public void requestTripCancel() {
        MyUtility.logI(Constants.TAG, "requestTripCancel");
        // if (orderStatus == Constants.ORDER_STATUS_USER_REQUESTED_TAXI) {
        //orderTripEndTime = DateTimeUtil.getCurrentDateTime();
        orderStatus = Constants.ORDER_STATUS_TRIP_CANCELLED;

        saveOrder();
        // }
    }

    private void fromHashMap(@NonNull DataSnapshot orderDataSnapshot) {

        try {
            MyUtility.logI(Constants.TAG, "fromHashMap");


            MyUtility.logI(Constants.TAG, "orderDataSnapshot.getValue(): " + orderDataSnapshot.getValue());

//        MyUtility.logI(Constants.TAG, "(HashMap<String, String>) orderDataSnapshot.getValue(): " + (HashMap<String, String>) orderDataSnapshot.getValue());
            if (orderDataSnapshot.getValue() == null) {
                return;
            }

            if (!orderDataSnapshot.getValue().getClass().equals(HashMap.class)) {
                MyUtility.logI(Constants.TAG, "String class detected for Order");
                return;
            }

            HashMap<String, Object> map = (HashMap<String, Object>) orderDataSnapshot.getValue();
            if (map != null) {

                if (map.get(Constants.FIREBASE_KEY_ORDER_STATUS) == null) return;

                orderStatus =
                        ((Long) (map.get(Constants.FIREBASE_KEY_ORDER_STATUS))).intValue();

                orderNumber = (String) map.get(Constants.FIREBASE_KEY_ORDER_NUMBER);
                orderUserRequestTime = (String) map.get(Constants.FIREBASE_KEY_ORDER_USER_REQUEST_TIME);
                orderDriverResponseTime = (String) map.get(Constants.FIREBASE_KEY_ORDER_DRIVER_RESPONSE_TIME);
                orderTripStartTime = (String) map.get(Constants.FIREBASE_KEY_ORDER_TRIP_START_TIME);
                orderTripEndTime = (String) map.get(Constants.FIREBASE_KEY_ORDER_TRIP_END_TIME);

                userKey = (String) map.get(Constants.FIREBASE_KEY_ORDER_USER_KEY);
                driverKey = (String) map.get(Constants.FIREBASE_KEY_ORDER_DRIVER_KEY);

                estimatedTime = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_ESTIMATED_TIME));
                estimatedDistance = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_ESTIMATED_DISTANCE));
                estimatedCost = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_ESTIMATED_COST));

                pickUpTitle = (String) map.get(Constants.FIREBASE_KEY_ORDER_PICKUP_TITLE);
                destinationTitle = (String) map.get(Constants.FIREBASE_KEY_ORDER_DESTINATION_TITLE);

                isRoundTrip = map.get(Constants.FIREBASE_KEY_ORDER_IS_ROUND_TRIP) != null && (boolean) map.get(Constants.FIREBASE_KEY_ORDER_IS_ROUND_TRIP);

                Double destinationLat = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_DESTINATION_LAT));
                Double destinationLng = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_DESTINATION_LNG));

                Double pickUpLat = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_PICKUP_LAT));
                Double pickUpLng = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_PICKUP_LNG));

                MyUtility.logI(Constants.TAG, "pickUpLat:" + pickUpLat);
                MyUtility.logI(Constants.TAG, "pickUpLng: " + pickUpLng);
                MyUtility.logI(Constants.TAG, "destinationLng:" + destinationLng);
                MyUtility.logI(Constants.TAG, "destinationLng:" + destinationLng);

                destinationPoint = new LatLng(destinationLat, destinationLng);
                pickupPoint = new LatLng(pickUpLat, pickUpLng);

                MyUtility.logI(Constants.TAG, "pickupPoint: " + pickupPoint);


                if ((map.get(Constants.FIREBASE_KEY_ORDER_USER_FULLNAME) != null)) {
                    userFullname = (String) map.get(Constants.FIREBASE_KEY_ORDER_USER_FULLNAME);
                }

                if ((map.get(Constants.FIREBASE_KEY_ORDER_DRIVER_FULLNAME) != null)) {
                    driverFullname = (String) map.get(Constants.FIREBASE_KEY_ORDER_DRIVER_FULLNAME);
                }

                vehicleCategoryId = (String) map.get(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_ID);
                vehicleCategoryName = (String) map.get(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_NAME);

                taxPercent = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_TAX_PERCENT));

                base_fare = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_BASE_FARE));
                fare_per_km = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_FARE_PER_KM));
                fare_per_min = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_FARE_PER_MIN));
                vehicle_model = (String) map.get(Constants.FIREBASE_KEY_ORDER_VEHICLE_MODEL);
                vehicle_number = (String) map.get(Constants.FIREBASE_KEY_ORDER_VEHICLE_NUMBER);
                vehicle_year = (String) map.get(Constants.FIREBASE_KEY_ORDER_VEHICLE_YEAR);

                tripRating = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_TRIP_RATING));

                tripFeedback = (String) map.get(Constants.FIREBASE_KEY_ORDER_TRIP_FEEDBACK);

                tripDistance = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_TRIP_DISTANCE));
                tripTime = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_TRIP_TIME));
                tripCost = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_TRIP_USER_PAYMENT));
                driverEarning = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_TRIP_DRIVER_EARNING));
                companyEarning = MyUtility.readDoubleValue(map.get(Constants.FIREBASE_KEY_ORDER_TRIP_COMPANY_EARNING));

                tripStars = ((Long) map.get(Constants.FIREBASE_KEY_ORDER_TRIP_STARS)).intValue();
                MyUtility.logI(Constants.TAG, "destinationPoint:" + destinationPoint);
                if (orderDataSnapshot.child(Constants.FIREBASE_KEY_ORDER_TRIP_POINTS).getValue() != null) {
                    tripPoints.clear();
                    for (DataSnapshot ds :
                            orderDataSnapshot.child(Constants.FIREBASE_KEY_ORDER_TRIP_POINTS).getChildren()) {
                        TripPoint tripPoint = new TripPoint(ds);

                        tripPoints.add(tripPoint);
                    }
                }

                if (orderDataSnapshot.child(Constants.FIREBASE_KEY_ORDER_DRIVER_RESPONSE_TRIP_POINTS).getValue() != null) {
                    driverResponseTripPoints.clear();
                    for (DataSnapshot ds :
                            orderDataSnapshot.child(Constants.FIREBASE_KEY_ORDER_DRIVER_RESPONSE_TRIP_POINTS).getChildren()) {
                        TripPoint tripPoint = new TripPoint(ds);

                        driverResponseTripPoints.add(tripPoint);
                    }
                }
                MyUtility.logI(Constants.TAG, "AFTER READ ORDER DATA");
                logOrder();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            MyUtility.logI(Constants.TAG, "ERROR: " + ex.getMessage());
        }
    }


    private void logOrder() {
        MyUtility.logI(Constants.TAG, "----------------------------------------------------");
        MyUtility.logI(Constants.TAG, "ORDER_NUMBER: " + orderNumber);
        MyUtility.logI(Constants.TAG, "USER REQUEST TIME: " + orderUserRequestTime);
        MyUtility.logI(Constants.TAG, "DRIVER RESPONSE TIME: " + orderDriverResponseTime);
        MyUtility.logI(Constants.TAG, "TRIP START TIME: " + orderTripStartTime);
        MyUtility.logI(Constants.TAG, "TRIP END TIME: " + orderTripEndTime);
        MyUtility.logI(Constants.TAG, "ORDER STATUS: " + orderStatus);
        MyUtility.logI(Constants.TAG, "USER KEY: " + userKey);
        MyUtility.logI(Constants.TAG, "DRIVER KEY: " + driverKey);
        if (pickupPoint != null) {
            MyUtility.logI(Constants.TAG, "PICKUP POINT: " + pickupPoint.latitude + "," + pickupPoint.longitude);
            MyUtility.logI(Constants.TAG, "PICKUP TITLE: " + pickUpTitle);
        }

        MyUtility.logI(Constants.TAG, "ROUND TRIP: " + isRoundTrip);


        if (destinationPoint != null) {
            MyUtility.logI(Constants.TAG, "DESTINATION POINT: " + destinationPoint.latitude + "," + destinationPoint.longitude);
        }

        MyUtility.logI(Constants.TAG, "DESTINATION TIME: " + estimatedTime);
        MyUtility.logI(Constants.TAG, "DESTINATION DISTANCE: " + estimatedDistance);
        MyUtility.logI(Constants.TAG, "DESTINATION TITLE: " + destinationTitle);
        MyUtility.logI(Constants.TAG, "DESTINATION COST: " + estimatedCost);
        MyUtility.logI(Constants.TAG, "TRIP DISTANCE: " + String.valueOf(tripDistance));
        MyUtility.logI(Constants.TAG, "TRIP TIME: " + String.valueOf(tripTime));
        MyUtility.logI(Constants.TAG, "TRIP USER PAYMENT: " + String.valueOf(tripCost));
        MyUtility.logI(Constants.TAG, "TRIP DRIVER EARNING: " + String.valueOf(driverEarning));
        MyUtility.logI(Constants.TAG, "TRIP COMPANY EARNING: " + String.valueOf(companyEarning));
        MyUtility.logI(Constants.TAG, "TRIP RATING: " + String.valueOf(tripRating));
        MyUtility.logI(Constants.TAG, "TRIP STARS: " + String.valueOf(tripStars));
        MyUtility.logI(Constants.TAG, "TRIP FEEDBACK: " + String.valueOf(tripFeedback));
        MyUtility.logI(Constants.TAG, "VEHICLE CATEGORY ID: " + vehicleCategoryId);
        MyUtility.logI(Constants.TAG, "VEHICLE CATEGORY BASE FARE: " + base_fare);
        MyUtility.logI(Constants.TAG, "VEHICLE CATEGORY FARE PER KM: " + fare_per_km);
        MyUtility.logI(Constants.TAG, "VEHICLE CATEGORY FARE PER MIN: " + fare_per_min);
        MyUtility.logI(Constants.TAG, "VEHICLE MODEL: " + vehicle_model);
        MyUtility.logI(Constants.TAG, "VEHICLE YEAR: " + vehicle_year);
        MyUtility.logI(Constants.TAG, "VEHICLE NUMBER: " + vehicle_number);

        MyUtility.logI(Constants.TAG, "----------------------------------------------------");
    }

    @NonNull
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.FIREBASE_KEY_ORDER_NUMBER, orderNumber);
        map.put(Constants.FIREBASE_KEY_ORDER_USER_REQUEST_TIME, orderUserRequestTime);
        map.put(Constants.FIREBASE_KEY_ORDER_DRIVER_RESPONSE_TIME, orderDriverResponseTime);
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_START_TIME, orderTripStartTime);
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_END_TIME, orderTripEndTime);
        MyUtility.logI(Constants.TAG, "orderStatus3: " + orderStatus);
        map.put(Constants.FIREBASE_KEY_ORDER_STATUS, orderStatus);
        map.put(Constants.FIREBASE_KEY_ORDER_USER_KEY, userKey);
        map.put(Constants.FIREBASE_KEY_ORDER_USER_FULLNAME, userFullname);
        map.put(Constants.FIREBASE_KEY_ORDER_DRIVER_FULLNAME, driverFullname);
        map.put(Constants.FIREBASE_KEY_ORDER_DRIVER_KEY, driverKey);
        map.put(Constants.FIREBASE_KEY_ORDER_ESTIMATED_TIME, estimatedTime);
        map.put(Constants.FIREBASE_KEY_ORDER_ESTIMATED_DISTANCE, estimatedDistance);
        map.put(Constants.FIREBASE_KEY_ORDER_ESTIMATED_COST, estimatedCost);
        map.put(Constants.FIREBASE_KEY_ORDER_DESTINATION_TITLE, String.valueOf(destinationTitle));
        map.put(Constants.FIREBASE_KEY_ORDER_PICKUP_TITLE, String.valueOf(pickUpTitle));
        map.put(Constants.FIREBASE_KEY_ORDER_IS_ROUND_TRIP, isRoundTrip);

        map.put(Constants.FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_ID, String.valueOf(vehicleCategoryId));
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_DISTANCE, tripDistance);
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_TIME, tripTime);
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_USER_PAYMENT, tripCost);
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_DRIVER_EARNING, driverEarning);
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_COMPANY_EARNING, companyEarning);
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_RATING, tripRating);
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_STARS, tripStars);

        MyUtility.logI(Constants.TAG, "tripFeedback: " + tripFeedback);
        MyUtility.logI(Constants.TAG, "tripRating: " + tripRating);
        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_FEEDBACK, String.valueOf(tripFeedback));

        MyUtility.logI("AppInfo1", "taxPercent:" + taxPercent);
        map.put(Constants.FIREBASE_KEY_ORDER_TAX_PERCENT, taxPercent);

        map.put(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_ID, vehicleCategoryId);
        map.put(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_NAME, vehicleCategoryName);
        map.put(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_BASE_FARE, base_fare);
        map.put(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_FARE_PER_KM, fare_per_km);
        map.put(Constants.FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_FARE_PER_MIN, fare_per_min);
        map.put(Constants.FIREBASE_KEY_ORDER_VEHICLE_MODEL, vehicle_model);
        map.put(Constants.FIREBASE_KEY_ORDER_VEHICLE_YEAR, vehicle_year);
        map.put(Constants.FIREBASE_KEY_ORDER_VEHICLE_NUMBER, vehicle_number);

        if (destinationPoint != null) {
            map.put(Constants.FIREBASE_KEY_ORDER_DESTINATION_LAT, destinationPoint.latitude);
            map.put(Constants.FIREBASE_KEY_ORDER_DESTINATION_LNG, destinationPoint.longitude);
        } else {
            map.put(Constants.FIREBASE_KEY_ORDER_DESTINATION_LAT, String.valueOf(""));
            map.put(Constants.FIREBASE_KEY_ORDER_DESTINATION_LNG, String.valueOf(""));
        }

        if (pickupPoint != null) {
            map.put(Constants.FIREBASE_KEY_ORDER_PICKUP_LAT, pickupPoint.latitude);
            map.put(Constants.FIREBASE_KEY_ORDER_PICKUP_LNG, pickupPoint.longitude);
        } else {
            map.put(Constants.FIREBASE_KEY_ORDER_PICKUP_LAT, String.valueOf(""));
            map.put(Constants.FIREBASE_KEY_ORDER_PICKUP_LNG, String.valueOf(""));
        }

        HashMap<String, HashMap<String, Object>> tripPointsMap = new HashMap<>();
        for (int i = 0; i < tripPoints.size(); i++) {
            tripPointsMap.put(tripPoints.get(i).getTimeStamp(), tripPoints.get(i).toHashMap());
        }

        map.put(Constants.FIREBASE_KEY_ORDER_TRIP_POINTS, tripPointsMap);

        HashMap<String, HashMap<String, Object>> tripPointsMap1 = new HashMap<>();

        for (int i = 0; i < driverResponseTripPoints.size(); i++) {
            tripPointsMap1.put(driverResponseTripPoints.get(i).getTimeStamp(), driverResponseTripPoints.get(i).toHashMap());
        }

        map.put(Constants.FIREBASE_KEY_ORDER_DRIVER_RESPONSE_TRIP_POINTS, tripPointsMap1);

        return map;
    }


    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderUserRequestTime() {
        return orderUserRequestTime;
    }

    public String getOrderDriverResponseTime() {
        return orderDriverResponseTime;
    }

    public String getOrderTripStartTime() {
        return orderTripStartTime;
    }

    public String getOrderTripEndTime() {
        return orderTripEndTime;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    private String getUserUID() {
        return userKey;
    }

    private String getDriverUID() {
        return driverKey;
    }

    private void saveOrder() {

        MyUtility.logI(Constants.TAG, "================ SAVING ORDER ===========");
        logOrder();

        if (getOrderRef() == null) {
            orderRef = MyUtility.getOrdersNodeRef().child(orderUserRequestTime);
        }

        getOrderRef().setValue(toHashMap());


    }

    public void writeOrderPoints() {
        for (int i = 0; i < tripPoints.size(); i++) {
            getOrderRef().child(Constants.FIREBASE_KEY_ORDER_TRIP_POINTS)
                    .child(tripPoints.get(i).getTimeStamp()).setValue(tripPoints.get(i).toHashMap());
        }
    }

    public void writeOrderDriverResponsePoints() {
        for (int i = 0; i < driverResponseTripPoints.size(); i++) {
            getOrderRef().child(Constants.FIREBASE_KEY_ORDER_DRIVER_RESPONSE_TRIP_POINTS)
                    .child(driverResponseTripPoints.get(i).getTimeStamp())
                    .setValue(driverResponseTripPoints.get(i).toHashMap());
        }
    }

    public void finishOrder() {

        if (getOrderRef() == null) return;


        switch (getOrderStatus()) {
            case Constants.ORDER_STATUS_TRIP_CANCELLED:

                if (!TextUtils.isEmpty(getUserUID())) {
                    MyUtility.getUsersNodeRef().child(getUserUID()).child(Constants.FIREBASE_KEY_CANCELLED_ORDERS).child(getOrderUserRequestTime()).setValue(toHashMap());
                }

                if (!TextUtils.isEmpty(getDriverUID())) {
                    MyUtility.getUsersNodeRef().child(getDriverUID()).child(Constants.FIREBASE_KEY_CANCELLED_ORDERS).child(getOrderUserRequestTime()).setValue(toHashMap());
                }
                break;
            default:
                MyUtility.logI(Constants.TAG, "switch statement with unhandled case");
                break;
        }
    }


    @NonNull
    public String getOrderStatusString() {
        switch (getOrderStatus()) {
            case Constants.ORDER_STATUS_IDLE:
                return "Idle";
            case Constants.ORDER_STATUS_USER_REQUESTED_TAXI:
                return "Waiting For Driver";
            case Constants.ORDER_STATUS_DRIVER_RESPONDED_AND_ON_THE_WAY:
                return "Driver on The Way";
            case Constants.ORDER_STATUS_TRIP_STARTED:
                return "Trip Started";
            case Constants.ORDER_STATUS_TRIP_END:
                return "Trip Ended";
        }
        return "";
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public void setUser(@Nullable User user) {
        this.user = user;
    }

    public void setOrderNumber(int order_number) {
        orderNumber = String.valueOf(order_number);
    }


    public double calculateTripDistance() {
        // Fetching all the points in i-th route
        tripDistance = 0;
        for (int j = 0; j < tripPoints.size(); j++) {

            if (j > 0) {

                LatLng latLng1 = new LatLng(tripPoints.get(j - 1).getLatitude(), tripPoints.get(j - 1).getLongitude());
                LatLng latLng2 = new LatLng(tripPoints.get(j).getLatitude(), tripPoints.get(j).getLongitude());

                tripDistance += MyUtility.getLengthBetweenLatLngsMeters(latLng1, latLng2);
            }

            MyUtility.logI(Constants.TAG, "tripDistance: " + tripDistance);
        }

        return MyUtility.roundDouble2(tripDistance / 1000);

    }

    public double calculateTripTime() {
        tripTime = MyUtility.getTimeDifferenceInMinutes(orderTripStartTime, orderTripEndTime);
        return tripTime;
    }

    public double getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(double tripDistance) {
        this.tripDistance = tripDistance;
    }

    public double getTripTime() {
        return tripTime;
    }

    public void setTripTime(double tripTime) {
        this.tripTime = tripTime;
    }

    public String getDestinationTitle() {
        return destinationTitle;
    }

    public void setTripRating(double tripRating) {
        this.tripRating = tripRating;
    }

    public void setTripStars(int tripStars) {
        this.tripStars = tripStars;
    }

    public void setTripFeedback(String tripFeedback) {
        this.tripFeedback = tripFeedback;
    }

    public double getEstimatedTime() {
        return MyUtility.roundDouble2(estimatedTime);
    }

    public double getEstimatedDistance() {
        return MyUtility.roundDouble2(estimatedDistance);
    }

    public void setDriverEarning(double driverEarning) {
        this.driverEarning = driverEarning;
    }

    public void setCompanyEarning(double companyEarning) {
        this.companyEarning = companyEarning;
    }

    public double getTripCost() {
        return tripCost;
    }

    public void setTripCost(double tripCost) {
        this.tripCost = tripCost;
    }

    @Nullable
    public User getDriver() {
        return driver;
    }

    public void setDriver(@Nullable User driver) {
        this.driver = driver;
        this.driverFullname = driver.getFullName();
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }


    @Nullable
    public LatLng getPickUpPoint() {
        return pickupPoint;
    }

    public void setPickUpPoint(@Nullable LatLng pickUpPoint) {
        this.pickupPoint = pickUpPoint;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getDriverKey() {
        return driverKey;
    }

    public double getTripRating() {
        return tripRating;
    }

    public int getTripStars() {
        return tripStars;
    }

    public String getTripFeedback() {
        return tripFeedback;
    }

    public String getVehicleCategoryId() {
        return vehicleCategoryId;
    }

    public String getVehicleCategoryName() {
        return vehicleCategoryName;
    }

    private double getBase_fare() {
        return base_fare;
    }

    private double getFare_per_km() {
        return fare_per_km;
    }

    private double getFare_per_min() {
        return fare_per_min;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public String getVehicle_year() {
        return vehicle_year;
    }

    public List<TripPoint> getTripPoints() {
        return tripPoints;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public double getDriverEarning() {
        return driverEarning;
    }

    public double getCompanyEarning() {
        return companyEarning;
    }

    public LatLng getPickupPoint() {
        return pickupPoint;
    }

    public String getPickUpTitle() {
        return pickUpTitle;
    }

    public boolean isRoundTrip() {
        return isRoundTrip;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public String getDriverFullname() {
        return driverFullname;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setOrderUserRequestTime(String orderUserRequestTime) {
        this.orderUserRequestTime = orderUserRequestTime;
    }

    public void setOrderDriverResponseTime(String orderDriverResponseTime) {
        this.orderDriverResponseTime = orderDriverResponseTime;
    }

    public void setOrderTripStartTime(String orderTripStartTime) {
        this.orderTripStartTime = orderTripStartTime;
    }

    public void setOrderTripEndTime(String orderTripEndTime) {
        this.orderTripEndTime = orderTripEndTime;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setDriverKey(String driverKey) {
        this.driverKey = driverKey;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public void setEstimatedDistance(double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public void setOrderRef(DatabaseReference orderRef) {
        this.orderRef = orderRef;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
    }

    public void setDriverFullname(String driverFullname) {
        this.driverFullname = driverFullname;
    }

    public void setVehicleCategoryId(String vehicleCategoryId) {
        this.vehicleCategoryId = vehicleCategoryId;
    }

    public void setVehicleCategoryName(String vehicleCategoryName) {
        this.vehicleCategoryName = vehicleCategoryName;
    }

    public void setBase_fare(double base_fare) {
        this.base_fare = base_fare;
    }

    public void setFare_per_km(double fare_per_km) {
        this.fare_per_km = fare_per_km;
    }

    public void setFare_per_min(double fare_per_min) {
        this.fare_per_min = fare_per_min;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    public void setVehicle_year(String vehicle_year) {
        this.vehicle_year = vehicle_year;
    }

    public void setTripPoints(List<TripPoint> tripPoints) {
        this.tripPoints = tripPoints;
    }

    public void setDriverResponseTripPoints(List<TripPoint> driverResponseTripPoints) {
        this.driverResponseTripPoints = driverResponseTripPoints;
    }

    public void setDestinationTitle(String destinationTitle) {
        this.destinationTitle = destinationTitle;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public void setPickupPoint(LatLng pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public void setPickUpTitle(String pickUpTitle) {
        this.pickUpTitle = pickUpTitle;
    }

    public void setRoundTrip(boolean roundTrip) {
        isRoundTrip = roundTrip;
    }

    public void setDestinationPoint(LatLng destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public void addToCompletedOrders() {
        MyUtility.getPreviousCompletedOrdersRef().child(getOrderUserRequestTime()).setValue(toHashMap());

    }

    public void removeOrder() {
        MyUtility.getOrdersNodeRef().child(getOrderUserRequestTime()).removeValue();
    }

    public void moveOrderToCancelledOrders() {
        MyUtility.getPreviousCancelledOrdersRef().child(getOrderUserRequestTime()).setValue(toHashMap());
        getOrderRef().removeValue();
    }

    public void setOrderEnded2() {
        MyUtility.getOrdersNodeRef().child(getOrderUserRequestTime()).
                child(Constants.FIREBASE_KEY_ORDER_STATUS).setValue(Constants.ORDER_STATUS_TRIP_END2);
    }

    private double tripCostWithoutTax;
    private double driverPoints;
    private double remainingPoints;
    public void calculateTripValues(double tripDistance,
                                    double tripTime,
                                    double tripBaseFare,
                                    double tripFarePerKm,
                                    double tripFarePerMin,
                                    double tripTax,
                                    double companyPercent,
                                    double driverAccountPoints) {

        MyUtility.logI(Constants.TAG, "----------- calculateTripValues --------------");
        MyUtility.logI(Constants.TAG, "tripDistance: " + tripDistance);
        MyUtility.logI(Constants.TAG, "tripTime:" + tripTime);
        MyUtility.logI(Constants.TAG, "tripBaseFare:" + tripBaseFare);
        MyUtility.logI(Constants.TAG, "tripFarePerKm:" + tripFarePerKm);
        MyUtility.logI(Constants.TAG, "tripFarePerMin:" + tripFarePerMin);
        MyUtility.logI(Constants.TAG, "tripTax:" + tripTax);
        MyUtility.logI(Constants.TAG, "companyPercent:" + companyPercent);
        MyUtility.logI(Constants.TAG, "driverAccountPoints:" + driverAccountPoints);

        this.base_fare = tripBaseFare;
        this.fare_per_km = tripFarePerKm;
        this.fare_per_min = tripFarePerMin;
        this.taxPercent = tripTax;
        this.tripDistance = tripDistance;
        this.tripTime = tripTime;

        tripCostWithoutTax = MyUtility.calculateTripCostWithoutTax(tripDistance, tripTime, tripBaseFare, tripFarePerKm, tripFarePerMin);
        tripCost = MyUtility.calculateTripCostWithTax(tripDistance, tripTime, tripBaseFare, tripFarePerKm, tripFarePerMin, tripTax);
        companyEarning = tripCost * companyPercent / 100;
        driverPoints = companyEarning;
        driverEarning = tripCost - companyEarning;
        remainingPoints = driverAccountPoints - driverPoints;



    }

    public List<TripPoint> getDriverResponseTripPoints() {
        return driverResponseTripPoints;
    }

    public double getTripCostWithoutTax() {
        return MyUtility.roundDouble2(tripCost - tripCost*taxPercent/100);
    }

    public void setTripCostWithoutTax(double tripCostWithoutTax) {
        this.tripCostWithoutTax = tripCostWithoutTax;
    }

    public double getDriverPoints() {
        return driverPoints;
    }

    public void setDriverPoints(double driverPoints) {
        this.driverPoints = driverPoints;
    }

    public double getRemainingPoints() {
        return remainingPoints;
    }

    public void setRemainingPoints(double remainingPoints) {
        this.remainingPoints = remainingPoints;
    }
}
