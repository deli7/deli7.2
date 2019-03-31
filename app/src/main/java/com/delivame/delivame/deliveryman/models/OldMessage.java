package com.delivame.delivame.deliveryman.models;

import android.support.annotation.NonNull;

import com.delivame.delivame.deliveryman.utilities.Constants;
import com.google.firebase.database.DataSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class OldMessage implements Comparable<OldMessage>{
    private final String date;
    private final String subject;
    private final String body;
    private final String to;

    public OldMessage(String date, String subject, String body, String to) {
        this.date = date;
        this.subject = subject;
        this.body = body;
        this.to = to;
    }

    public OldMessage(DataSnapshot dataSnapshot) {
        date = dataSnapshot.getKey();
        subject = (String) dataSnapshot.child(Constants.MESSAGE_SUBJECT).getValue();
        body = (String) dataSnapshot.child(Constants.MESSAGE_BODY).getValue();
        to = (String) dataSnapshot.child(Constants.MESSAGE_TO).getValue();

       // MyUtility.logI(Constants.TAG2, "subject: " + subject);
       // MyUtility.logI(Constants.TAG2, "body: " + body);
       // MyUtility.logI(Constants.TAG2, "to: " + to);
       // MyUtility.logI(Constants.TAG2, "date: " + date);

    }

    public String getDate() {
        return date;
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
    public int compareTo(@NonNull OldMessage message) {


        DateFormat df = new SimpleDateFormat("yyy-MM-dd-hh-mm-ss", Locale.ENGLISH);
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse(message.getDate());
            d2 = df.parse(getDate());
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
