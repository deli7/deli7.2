package com.delivame.delivame.deliveryman.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.common.OrderActivity;
import com.delivame.delivame.deliveryman.models.User;

public class ContactHelper {
    public static void sendEmail(final Context context, final User user, final String email) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto",email, null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contacting support from User #" + user.getUserNumber());
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.sending_email_to_support)).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();


    }





    public static void makePhoneCall(final Context context, final User user) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        int checkPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    Constants.REQUEST_CALL_PHONE);
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + user.getPhoneNumber()));
                            context.startActivity(callIntent);
                        }


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.call_support_center_now)).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
    }

    public static void makePhoneCall(final Context context, final String phone) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        int checkPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    Constants.REQUEST_CALL_PHONE);
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            context.startActivity(callIntent);
                        }


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppCompatAlertDialogStyle));
        builder.setMessage(context.getString(R.string.call_support_center_now)).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
    }

    public static void makePhoneCall(final Context context, final String phone, final String name) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        int checkPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    Constants.REQUEST_CALL_PHONE);
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            context.startActivity(callIntent);
                        }


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppCompatAlertDialogStyle));
        builder.setMessage(context.getString(R.string.call) + " " + name).setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
    }
}
