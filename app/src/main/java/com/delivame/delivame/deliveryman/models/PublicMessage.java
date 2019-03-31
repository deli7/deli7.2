package com.delivame.delivame.deliveryman.models;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PublicMessage implements Comparable<PublicMessage>{
    private String id;
    private String subject;
    private String body;
    private String to;

    public PublicMessage() {

    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getTo() {
        return to;
    }




    @Override
    public int compareTo(@NonNull PublicMessage message) {

        DateFormat df = new SimpleDateFormat("yyy-MM-dd-hh-mm-ss", Locale.ENGLISH);
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse(message.getId());
            d2 = df.parse(getId());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d1.getTime() > d2.getTime()) {
            return 1;
        }
        else if (d1.getTime() <  d2.getTime()) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
