package com.delivame.delivame.deliveryman.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;


public class InitManager {

   private final Settings settings = new Settings();

   private InitManagerListener listener;
   private User currentUser;

   public void init(final Context context) {

      listener = (InitManagerListener) context;

      MyUtility.getGlobalSettingsNodeRef().addListenerForSingleValueEvent(new ValueEventListener() {

         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            settings.readBasicSettings(dataSnapshot, context);

            MyUtility.getGlobalSettingsNodeRef()
                  .child(Constants.FIREBASE_KEY_ALLOWED_VEHICLES)
                  .addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {

                           invalidConfiguration(context);

                           return;
                        }

                        settings.readVehicleCategories(dataSnapshot);

                        // ---------------------------------------------------------------------------------------
                        // Reading current user information
                        // ---------------------------------------------------------------------------------------
                        MyUtility.getCurrentUserNodeRef()
                              .addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // ---------------------------------------------------------------------------------------
                                    // Check Invalid Snapshot
                                    // ---------------------------------------------------------------------------------------
                                    if (dataSnapshot.getValue() == null) {
                                       invalidConfiguration(context);
                                       return;
                                    }

                                    // ---------------------------------------------------------------------------------------
                                    // Read User information
                                    // ---------------------------------------------------------------------------------------
                                    currentUser = new User(dataSnapshot,
                                          MyUtility.getCurrentUserNodeRef(),
                                          MyUtility.getCurrentUserUID(),
                                          settings);

                                    MyUtility.getGlobalSettingsNodeRef()
                                          .child("business_types")
                                          .addListenerForSingleValueEvent(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                settings.readBusinessTypes(dataSnapshot);
                                                listener.initUI(settings, currentUser);
                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {
                                             }
                                          });
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                              });
                        //initUI();
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {
                        logI(TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
                     }
                  });
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
            logI(TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
         }
      });
   }

   private void invalidConfiguration(final Context context) {
      AlertDialog alertDialog = new AlertDialog.Builder(context).create();
      alertDialog.setTitle(context.getString(R.string.error));
      alertDialog.setMessage(context.getString(R.string.ui_message_error_invalid_configuration));
      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.ok),
            (dialog, which) -> {
               dialog.dismiss();
               MyUtility.toHomeScreen(context);
            });
      alertDialog.show();
   }


   public interface InitManagerListener {
      void initUI(Settings settings, User currentUser);
   }
}
