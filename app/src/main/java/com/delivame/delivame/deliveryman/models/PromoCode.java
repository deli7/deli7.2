package com.delivame.delivame.deliveryman.models;

import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;

import java.util.List;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class PromoCode {
    public static final int PROMO_TYPE_GENERAL = 0;
    public static final int PROMO_TYPE_LIMITED_SCOPE_TRIPS_VALUE = 1;
    public static final int PROMO_TYPE_LIMITED_SCOPE_TRIPS_COUNT = 2;
    public static final int PROMO_TYPE_UNLIMITED_SCOPE_TRIPS_VALUE = 3;
    public static final int PROMO_TYPE_UNLIMITED_SCOPE_TRIPS_COUNT = 4;
    public static final int PROMO_TYPE_USER_RATING = 5;

    public static final int PROMO_CODE_VALID = 0;
    public static final int PROMO_CODE_ERROR_PROMO_CODE_NOT_VALID = 1;
    public static final int PROMO_CODE_ERROR_PROMO_CODE_EXPIRED = 2;
    public static final int PROMO_CODE_ERROR_PROMO_CODE_USER_SCOPE_TRIPS_VALUE_NOT_VALID = 3;
    public static final int PROMO_CODE_ERROR_PROMO_CODE_USER_SCOPE_TRIPS_COUNT_NOT_VALID = 4;
    public static final int PROMO_CODE_ERROR_PROMO_CODE_USER_TRIPS_VALUE_NOT_VALID = 5;
    public static final int PROMO_CODE_ERROR_PROMO_CODE_USER_TRIPS_COUNT_NOT_VALID = 6;
    public static final int PROMO_CODE_ERROR_PROMO_CODE_USER_RATING_NOT_VALID = 7;
    public static final int PROMO_CODE_ERROR_PROMO_CODE_INVALID_TYPE = 8;

    String id;
    String promoStartDate;
    String promoEndDate;
    boolean validPromoCode;
    int promoType;
    double discountPercent;
    double scopeTripsValues;
    double scopeTripsCount;
    String scopeStartDate;
    String scopeEndDate;
    double userRating;
    int target;

    public PromoCode() {
    }

    public boolean isValidPromoCode() {
        return validPromoCode;
    }

    public void setValidPromoCode(boolean validPromoCode) {
        this.validPromoCode = validPromoCode;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public int validatePromotCode(User user, List<DeliveryOrder> deliveryOrders) {
        logI(TAG, "validPromoCode: " + validPromoCode);

//        if (!validPromoCode) {
//            return PROMO_CODE_ERROR_PROMO_CODE_NOT_VALID;
//        }

        if (!DateTimeUtil.isDateBetweenDates(DateTimeUtil.getCurrentDateTime(), promoStartDate, promoEndDate)) {
            return PROMO_CODE_ERROR_PROMO_CODE_EXPIRED;
        }

        double sum = 0;
        int count = 0;

        switch (promoType) {
            case PROMO_TYPE_GENERAL:
                break;

            case PROMO_TYPE_LIMITED_SCOPE_TRIPS_VALUE:

                for (int i = 0; i < deliveryOrders.size(); i++) {
                    DeliveryOrder order = deliveryOrders.get(i);

                    if (order.isDelivered() || order.isCompleted()){
                        if (order.getAcceptedOffer() != null){
                            if (DateTimeUtil.isDateBetweenDates(order.getOrderCompletionTime(), scopeStartDate, scopeEndDate)) {
                                sum += order.getAcceptedOffer().getOfferValue();
                            }
                        }
                    }
                }

                if (sum < scopeTripsValues){
                    return PROMO_CODE_ERROR_PROMO_CODE_USER_SCOPE_TRIPS_VALUE_NOT_VALID;
                }


                break;

            case PROMO_TYPE_LIMITED_SCOPE_TRIPS_COUNT:
                for (int i = 0; i < deliveryOrders.size(); i++) {
                    DeliveryOrder order = deliveryOrders.get(i);

                    if (order.isDelivered() || order.isCompleted()){
                        if (order.getAcceptedOffer() != null){
                            if (DateTimeUtil.isDateBetweenDates(order.getOrderCompletionTime(), scopeStartDate, scopeEndDate)) {
                                count += 1;
                            }
                        }
                    }
                }

                if (sum < scopeTripsCount){
                    return PROMO_CODE_ERROR_PROMO_CODE_USER_SCOPE_TRIPS_COUNT_NOT_VALID;
                }

                break;

            case PROMO_TYPE_UNLIMITED_SCOPE_TRIPS_VALUE:
                for (int i = 0; i < deliveryOrders.size(); i++) {
                    DeliveryOrder order = deliveryOrders.get(i);

                    if (order.isDelivered() || order.isCompleted()){
                        if (order.getAcceptedOffer() != null){
                               sum += order.getAcceptedOffer().getOfferValue();
                        }
                    }
                }

                if (sum < scopeTripsValues){
                    return PROMO_CODE_ERROR_PROMO_CODE_USER_TRIPS_VALUE_NOT_VALID;
                }

                break;

            case PROMO_TYPE_UNLIMITED_SCOPE_TRIPS_COUNT:
                for (int i = 0; i < deliveryOrders.size(); i++) {
                    DeliveryOrder order = deliveryOrders.get(i);

                    if (order.isDelivered() || order.isCompleted()){
                        if (order.getAcceptedOffer() != null){
                               count += 1;

                        }
                    }
                }

                if (sum < scopeTripsCount){
                    return PROMO_CODE_ERROR_PROMO_CODE_USER_TRIPS_COUNT_NOT_VALID;
                }
                break;

            case PROMO_TYPE_USER_RATING:
                if (user.getRank() < userRating){
                    return PROMO_CODE_ERROR_PROMO_CODE_USER_RATING_NOT_VALID;
                }
                break;

            default:
                return PROMO_CODE_ERROR_PROMO_CODE_INVALID_TYPE;

        }


        return PROMO_CODE_VALID;
    }


    public void printCodeString(int code){

        switch (code) {
            case PROMO_CODE_VALID:
                logI(TAG, "PROMO_CODE_VALID");
                break;
            case PROMO_CODE_ERROR_PROMO_CODE_NOT_VALID:
                logI(TAG, "PROMO_CODE_ERROR_PROMO_CODE_NOT_VALID");
                break;
            case PROMO_CODE_ERROR_PROMO_CODE_EXPIRED:
                logI(TAG, "PROMO_CODE_ERROR_PROMO_CODE_EXPIRED");
                break;
            case PROMO_CODE_ERROR_PROMO_CODE_USER_SCOPE_TRIPS_VALUE_NOT_VALID:
                logI(TAG, "PROMO_CODE_ERROR_PROMO_CODE_USER_SCOPE_TRIPS_VALUE_NOT_VALID");
                break;
            case PROMO_CODE_ERROR_PROMO_CODE_USER_SCOPE_TRIPS_COUNT_NOT_VALID:
                logI(TAG, "PROMO_CODE_ERROR_PROMO_CODE_USER_SCOPE_TRIPS_COUNT_NOT_VALID");
                break;
            case PROMO_CODE_ERROR_PROMO_CODE_USER_TRIPS_VALUE_NOT_VALID:
                logI(TAG, "PROMO_CODE_ERROR_PROMO_CODE_USER_TRIPS_VALUE_NOT_VALID");
                break;
            case PROMO_CODE_ERROR_PROMO_CODE_USER_TRIPS_COUNT_NOT_VALID:
                logI(TAG, "PROMO_CODE_ERROR_PROMO_CODE_USER_TRIPS_COUNT_NOT_VALID");
                break;
            case PROMO_CODE_ERROR_PROMO_CODE_USER_RATING_NOT_VALID:
                logI(TAG, "PROMO_CODE_ERROR_PROMO_CODE_USER_RATING_NOT_VALID");
                break;
            case PROMO_CODE_ERROR_PROMO_CODE_INVALID_TYPE:
                logI(TAG, "PROMO_CODE_ERROR_PROMO_CODE_INVALID_TYPE");
                break;

        }


    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPromoStartDate() {
        return promoStartDate;
    }

    public void setPromoStartDate(String promoStartDate) {
        this.promoStartDate = promoStartDate;
    }

    public String getPromoEndDate() {
        return promoEndDate;
    }

    public void setPromoEndDate(String promoEndDate) {
        this.promoEndDate = promoEndDate;
    }


    public int getPromoType() {
        return promoType;
    }

    public void setPromoType(int promoType) {
        this.promoType = promoType;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getScopeTripsValues() {
        return scopeTripsValues;
    }

    public void setScopeTripsValues(double scopeTripsValues) {
        this.scopeTripsValues = scopeTripsValues;
    }

    public double getScopeTripsCount() {
        return scopeTripsCount;
    }

    public void setScopeTripsCount(double scopeTripsCount) {
        this.scopeTripsCount = scopeTripsCount;
    }

    public String getScopeStartDate() {
        return scopeStartDate;
    }

    public void setScopeStartDate(String scopeStartDate) {
        this.scopeStartDate = scopeStartDate;
    }

    public String getScopeEndDate() {
        return scopeEndDate;
    }

    public void setScopeEndDate(String scopeEndDate) {
        this.scopeEndDate = scopeEndDate;
    }

    public double getUserRating() {
        return userRating;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
