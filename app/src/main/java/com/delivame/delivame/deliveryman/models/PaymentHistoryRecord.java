package com.delivame.delivame.deliveryman.models;

import android.support.annotation.NonNull;

import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


class PaymentHistoryRecord implements Comparable<PaymentHistoryRecord> {
    private String date;
    private String transactionType;
    private Double transactionValue;
    private String transactionDescription;

    public PaymentHistoryRecord(String date, String transactionType, Double transactionValue, String transactionDescription) {
        this.date = date;
        this.transactionType = transactionType;
        this.transactionValue = transactionValue;
        this.transactionDescription = transactionDescription;
    }

    public PaymentHistoryRecord(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
            date = dataSnapshot.getKey();

            transactionType = (String) dataSnapshot.child(Constants.FIREBASE_KEY_TRANSACTION_TYPE).getValue();
            String value1;

            if (dataSnapshot.child(Constants.FIREBASE_KEY_TRANSACTION_VALUE).getValue() != null) {
                value1 = String.valueOf(dataSnapshot.child(Constants.FIREBASE_KEY_TRANSACTION_VALUE).getValue());
                MyUtility.logI(Constants.TAG, "value1: " + value1);
                transactionValue = Double.parseDouble(value1);
            }else{
                transactionValue = 0.0;
            }

            //transactionValue = (Long) dataSnapshot.child(Constants.FIREBASE_KEY_TRANSACTION_VALUE).getValue();
            transactionDescription = (String) dataSnapshot.child(Constants.FIREBASE_KEY_TRANSACTION_DESCRIPTION).getValue();
        }
    }

    private String getDate() {
        return date;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public Double getTransactionValue() {
        return transactionValue;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    @Override
    public int compareTo(@NonNull PaymentHistoryRecord record) {


        DateFormat df = new SimpleDateFormat("yyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse(record.getDate());
            d2 = df.parse(getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d1.getTime() > d2.getTime()) {
            return 1;
        } else if (d1.getTime() < d2.getTime()) {
            return -1;
        } else {
            return 0;
        }
    }
}
