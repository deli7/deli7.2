package com.delivame.delivame.deliveryman.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class GoogleMapsHelper {

    public static void launchNavigation(Context context, double mLat, double mLng) {

        String appPackageName = "com.google.android.apps.maps";

        try {
            String location = mLat + "," + mLng;
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

            mapIntent.setPackage(appPackageName);
            mapIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mapIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, context.getString(R.string.ui_message_error_google_maps_not_installed), Toast.LENGTH_SHORT).show();

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://orderNumber?userName=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/orderNumber?userName=" + appPackageName)));
            }
        }
    }
}
