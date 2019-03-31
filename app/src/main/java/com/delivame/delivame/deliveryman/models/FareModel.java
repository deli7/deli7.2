package com.delivame.delivame.deliveryman.models;

import android.support.annotation.Nullable;

import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;


public class FareModel {

    private static final String TAG = "AppInfo";
    private double baseFare;
    private double farePerMin;
    private double farePerKm;
    final double companyPercent;
    private double userTripCost;
    private double driverEarning;
    private double companyEarning;
    double accountPoints;
    private double remainingPoints;
    private double tripPoints;
    private double tax = 0;

    public FareModel(@Nullable VehicleCategory vehicleCategory,
                     double companyPercent,
                     double accountPoints
    ) {
        if (vehicleCategory != null) {
            this.baseFare = vehicleCategory.baseFare;
            this.farePerMin = vehicleCategory.farePerMin;
            this.farePerKm = vehicleCategory.farePerKm;
        }
        this.companyPercent = companyPercent;
        this.accountPoints = accountPoints;
    }

    public void setVehicleCategoryData(VehicleCategory vehicleCategory){
        if (vehicleCategory != null) {
            this.baseFare = vehicleCategory.baseFare;
            this.farePerMin = vehicleCategory.farePerMin;
            this.farePerKm = vehicleCategory.farePerKm;
        }
    }



    public void calculateFares(double tripDistance, double tripTime){

        MyUtility.logI(Constants.TAG, "baseFare: " + baseFare);
        MyUtility.logI(Constants.TAG, "farePerKm: " + farePerKm);
        MyUtility.logI(Constants.TAG, "farePerMin: " + farePerMin);
        MyUtility.logI(Constants.TAG, "tax: " + tax);

        userTripCost = MyUtility.roundDouble2(calculateTripFareForUser(tripDistance, tripTime));
        userTripCostWithoutTax = MyUtility.roundDouble2(calculateTripFareForUserWithoutTax(tripDistance, tripTime));
        driverEarning = MyUtility.roundDouble2(calculateTripFareForDriver(tripDistance, tripTime));
        companyEarning = MyUtility.roundDouble2(calculateTripFareForCompany(tripDistance, tripTime));
        tripPoints = companyEarning;
        remainingPoints = accountPoints - tripPoints;

        MyUtility.logI(Constants.TAG, "--------------------------------------------------");
        MyUtility.logI(Constants.TAG, "Trip Cost: "  + userTripCost);
        MyUtility.logI(Constants.TAG, "Driver Earning: "  + driverEarning);
        MyUtility.logI(Constants.TAG, "Company Earning (Points deducted from driver): "  + companyEarning);
        MyUtility.logI(Constants.TAG, "--------------------------------------------------");
    }

    public boolean canDriverDoTheTrip(){

        MyUtility.logI(Constants.TAG, "tripPoints: " + tripPoints);
        MyUtility.logI(Constants.TAG, "accountPoints: " + accountPoints);

        return tripPoints <= accountPoints;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    private double calculateTripFareForUser(double tripDistance, double tripTime){

        double fare =(baseFare + tripTime*farePerMin + tripDistance*farePerKm)*(1 + tax/100);

        MyUtility.logI(Constants.TAG, "calculateTripFareForUser: fare: " + fare);

        return fare;
    }

    private double calculateTripFareForUserWithoutTax(double tripDistance, double tripTime){

        double fare =(baseFare + tripTime*farePerMin + tripDistance*farePerKm);

        MyUtility.logI(Constants.TAG, "calculateTripFareForUser: fare: " + fare);

        return fare;
    }

    private double userTripCostWithoutTax;

    public double getUserTripCostWithoutTax() {
        return userTripCostWithoutTax;
    }

    private double calculateTripFareForDriver(double tripDistance, double tripTime){
        double fare =   calculateTripFareForUser(tripDistance, tripTime) -
                calculateTripFareForCompany(tripDistance, tripTime);

        MyUtility.logI(Constants.TAG, "calculateTripFareForDriver: fare: " + fare);
        return fare;
    }

    private double calculateTripFareForCompany(double tripDistance, double tripTime){

        double points = (baseFare + tripTime*farePerMin + tripDistance*farePerKm)*(companyPercent)/100;

        MyUtility.logI(Constants.TAG, "calculateTripFareForCompany: " + points);

        return points;
    }

    public double getCompanyPercent() {
        return companyPercent;
    }

    public double getUserTripCost() {
        return userTripCost;
    }



    public double getDriverEarning() {
        return MyUtility.roundDouble2(driverEarning);
    }

    public double getCompanyEarning() {
        return companyEarning;
    }

    public double getAccountPoints() {
        return MyUtility.roundDouble2(accountPoints);
    }

    public double getRemainingPoints() {
        return MyUtility.roundDouble2(remainingPoints);
    }

    public double getTripPoints() {
        return MyUtility.roundDouble2(tripPoints);
    }

    public double getNeededPoints(){
        return MyUtility.roundDouble2( tripPoints);
    }

    public void decrementAccountPoints(double points) {

        accountPoints -= points;
    }

    public void setAccountPoints(double accountPoints) {
        this.accountPoints = accountPoints;
    }

    public static String getTAG() {
        return TAG;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getFarePerMin() {
        return farePerMin;
    }

    public void setFarePerMin(double farePerMin) {
        this.farePerMin = farePerMin;
    }

    public double getFarePerKm() {
        return farePerKm;
    }

    public void setFarePerKm(double farePerKm) {
        this.farePerKm = farePerKm;
    }

    public void setUserTripCost(double userTripCost) {
        this.userTripCost = userTripCost;
    }

    public void setDriverEarning(double driverEarning) {
        this.driverEarning = driverEarning;
    }

    public void setCompanyEarning(double companyEarning) {
        this.companyEarning = companyEarning;
    }

    public void setRemainingPoints(double remainingPoints) {
        this.remainingPoints = remainingPoints;
    }

    public void setTripPoints(double tripPoints) {
        this.tripPoints = tripPoints;
    }

    public double getTax() {
        return tax;
    }

    public void setUserTripCostWithoutTax(double userTripCostWithoutTax) {
        this.userTripCostWithoutTax = userTripCostWithoutTax;
    }
}
