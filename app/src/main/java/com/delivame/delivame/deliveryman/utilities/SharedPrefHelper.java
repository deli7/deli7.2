package com.delivame.delivame.deliveryman.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;


public class SharedPrefHelper {

   public static void writePrefString(Context context, String key, String value) {
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor editor = preferences.edit();
      editor.putString(key, value);
      editor.apply();
      editor.commit();
   }

   public static String getPrefString(Context context, String key) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      return prefs.getString(key, "");
   }

   public static void writePrefUri(Context context, String key, Uri value) {
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor editor = preferences.edit();
      editor.putString(key, value.toString()).apply();
   }

   @NonNull
   public static Uri getPrefUri(Context context, String key) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      return Uri.parse(prefs.getString(key, ""));
   }

   public static void writePrefBoolean(Context context, String key, Boolean value) {
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor editor = preferences.edit();
      editor.putBoolean(key, value).apply();
   }

   @NonNull
   public static Boolean getPrefBoolean(Context context, String key, Boolean default_return) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      return prefs.getBoolean(key, default_return);
   }

   public static void writePrefFloat(Context context, String key, Float value) {
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor editor = preferences.edit();
      editor.putFloat(key, value).apply();
   }

   @NonNull
   public static Float getPrefFloat(Context context, String key) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      return prefs.getFloat(key, 0);
   }
}
