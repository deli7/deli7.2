package com.delivame.delivame.deliveryman.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.delivame.delivame.deliveryman.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.delivame.delivame.deliveryman.utilities.Constants.BUNDLE_PARAMS;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG3;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class NotificationsHelper {

   private final static String channelId = "main_channel";

   public static void showNotification(@NonNull final Context context, String title,
                                       String snippet, int icon, Class<?> clazz, Bundle bundle) {

      Intent intent = new Intent(context, clazz);
      intent.putExtra(BUNDLE_PARAMS, bundle);

      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);

      Uri uri = SharedPrefHelper.getPrefUri(context, Constants.PREF_SETTING_RINGTONE);
      logI(TAG3, "uri: " + uri);
      if (TextUtils.isEmpty(uri.toString())) {
         uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      }

      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
            .setPriority(Notification.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(snippet)
            .setTicker(title)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
            .setStyle(new NotificationCompat.BigTextStyle().bigText(snippet))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.appicon);

      if (uri != null) {
         notificationBuilder.setSound(uri);
      } else {
         notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
      }

      NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

      assert nm != null;
      nm.notify(1, notificationBuilder.build());
   }

   public static void createNotificationChannel(Context context) {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         CharSequence name = context.getString(R.string.main_channel_name);
         String description = context.getString(R.string.main_channel_description);
         int importance = NotificationManager.IMPORTANCE_DEFAULT;
         NotificationChannel channel = new NotificationChannel(channelId, name, importance);
         channel.setDescription(description);
         NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
         notificationManager.createNotificationChannel(channel);
      }
   }
}
