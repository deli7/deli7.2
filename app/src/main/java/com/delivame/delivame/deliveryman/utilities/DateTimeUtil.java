package com.delivame.delivame.deliveryman.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class DateTimeUtil {

    public static boolean isDateBetweenDates(String date, String date1, String date2){
        boolean isDateAfterDate1 = isDate1AfterDate2(date, date1);
        boolean isDateBeforeDate2 = !isDate1AfterDate2(date, date2);

        logI(TAG, "date: " + date);
        logI(TAG, "date1 = " + date1);
        logI(TAG, "date2 = " + date2);
        logI(TAG, "isDateAfterDate1 = " + isDateAfterDate1);
        logI(TAG, "isDateBeforeDate2 = " + isDateBeforeDate2);

        if (isDateAfterDate1 && isDateBeforeDate2){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isDateAfter24Hours(String currentTime, String oldTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
        try {
            Date dFirstTime = sdf.parse(currentTime);

            Date dSecondTime = sdf.parse(oldTime);

            return Math.abs(dFirstTime.getTime() - dSecondTime.getTime()) > MILLIS_PER_DAY;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

    public static String getCurrentDateTime() {
        String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
    public static String getCurrentDateTime1() {
        String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }


    public static boolean dateAfterCurrentTime(String sTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = sdf.parse(sTime);

            Date currentTime = Calendar.getInstance().getTime();

            logI(TAG, "REQ. TIME: " + d.getTime());
            logI(TAG, "CURRENT TIME: " + currentTime.getTime());

            return d.getTime() > currentTime.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean isDate1AfterDate2(String firstTime, String secondTime) {
        logI(Constants.TAG2, "isDate1AfterDate2");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dFirstTime = sdf.parse(firstTime);

            Date dSecondTime = sdf.parse(secondTime);

            logI(Constants.TAG2, "dFirstTime = " + dFirstTime.getTime());
            logI(Constants.TAG2, "dSecondTime = " + dSecondTime.getTime());

            return (dFirstTime.getTime() > dSecondTime.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
