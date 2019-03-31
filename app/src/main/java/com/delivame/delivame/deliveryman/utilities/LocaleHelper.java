package com.delivame.delivame.deliveryman.utilities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.delivame.delivame.deliveryman.R;

import java.util.Locale;

/**
 * This class is used to change your application locale and persist this change for the next time
 * that your app is going to be used.
 * <p/>
 * You can also change the locale of your application on the fly by using the setLocale method.
 * <p/>
 * Created by gunhansancar on 07/10/15.
 */
public class LocaleHelper {

   private static final String SELECTED_LANGUAGE = "language";

   public static Context onAttach(Context context) {
      String lang = getPersistedData(context, Locale.getDefault().getLanguage());
      return setLocale(context, lang);
   }

   public static boolean isLanguageEnglish(Context context) {
      return getLanguage(context).equals("en_us");
   }

   public static boolean isLanguageSet(Context context) {
      String language = SharedPrefHelper.getPrefString(context, SELECTED_LANGUAGE);
      return !TextUtils.isEmpty(language);
   }

   public static String getLanguage(Context context) {
      return getPersistedData(context, Locale.getDefault().getLanguage());
   }

   public static Context setLocale(Context context, String language) {
      persist(context, language);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         return updateResources(context, language);
      }

      return updateResourcesLegacy(context, language);
   }

   public static void updateMenuLanguage(Context context, Menu menu) {
      MenuItem menuItemLanguage = menu.findItem(R.id.action_change_language);
      if (getLanguage(context).equals("en_us")) {
         menuItemLanguage.setTitle("Ar");
      } else {
         menuItemLanguage.setTitle("En");
      }
      //switchLanguage();
   }

   private static String getPersistedData(Context context, String defaultLanguage) {
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
   }

   private static void persist(Context context, String language) {
      SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
      editor.putString(SELECTED_LANGUAGE, language).apply();
   }

   @TargetApi(Build.VERSION_CODES.N)
   private static Context updateResources(Context context, String language) {
      Locale locale = new Locale(language);
      Locale.setDefault(locale);

      Configuration configuration = context.getResources().getConfiguration();
      configuration.setLocale(locale);
      configuration.setLayoutDirection(locale);

      return context.createConfigurationContext(configuration);
   }

   private static Context updateResourcesLegacy(Context context, String language) {
      Locale locale = new Locale(language);
      Locale.setDefault(locale);

      Resources resources = context.getResources();

      Configuration configuration = resources.getConfiguration();
      configuration.locale = locale;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
         configuration.setLayoutDirection(locale);
      }

      resources.updateConfiguration(configuration, resources.getDisplayMetrics());

      return context;
   }
}