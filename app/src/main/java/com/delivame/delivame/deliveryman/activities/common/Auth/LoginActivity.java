package com.delivame.delivame.deliveryman.activities.common.Auth;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.activities.client.ClientHomeActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.DeliveryManHomeActivity;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.delivame.delivame.deliveryman.utilities.SharedPrefHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class LoginActivity extends BaseActivity implements View.OnClickListener {


   private static final String TAG = LoginActivity.class.getSimpleName();
   private EditText editTextEmail;
   private EditText editTextPassword;
   private TextInputLayout textInputLayoutEmailAddress;
   private TextInputLayout textInputLayoutPassword;
   private final Settings settings = new Settings();

   private ValueEventListener valueEventListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

         if (dataSnapshot.getValue() == null) {
            return;
         }

         settings.readVehicleCategories(dataSnapshot);

         //showProgress();
         MyUtility.getCurrentUserNodeRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if (dataSnapshot.getValue() != null) {

                  User user = new User(dataSnapshot, MyUtility.getCurrentUserNodeRef(),
                        MyUtility.getCurrentUserUID(), settings);
                  handleUserType(user);
               } else {
                  // showMessage(R.string.error_invalid_registration);
                  //ref.child(Constants.FIREBASE_KEY_USER_LOGGED_IN).setValue(false);
                  FirebaseAuth.getInstance().signOut();
                  hideProgress();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               MyUtility.logI(Constants.TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
            }
         });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
         MyUtility.logI(Constants.TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
      }
   };

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      initUI();

      if (MyUtility.getCurrentUser() != null) {

         MyUtility.getGlobalSettingsNodeRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               settings.readBasicSettings(dataSnapshot, getApplicationContext());

               MyUtility.getGlobalSettingsNodeRef()
                     .child(Constants.FIREBASE_KEY_ALLOWED_VEHICLES)
                     .addListenerForSingleValueEvent(valueEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               MyUtility.logI(Constants.TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
            }
         });


      } else {
         hideProgress();
      }
   }

   private void initUI() {
      // Reference to UI Elements
      TextView textViewCreateAccount = findViewById(R.id.textViewCreateAccount);
      editTextEmail = findViewById(R.id.editTextEmailAddress);
      editTextPassword = findViewById(R.id.editTextPassword);
      textInputLayoutEmailAddress = findViewById(R.id.textInputLayoutEmailAddress);
      textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
      Button buttonLogin = findViewById(R.id.buttonLogin);
      // Setting Listeners
      textViewCreateAccount.setOnClickListener(this);
      buttonLogin.setOnClickListener(this);
   }

   private void showProgress() {
      progress = new ProgressDialog(this);
      progress.setMessage("Logging in...");
      progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progress.setIndeterminate(true);
      progress.setProgress(0);
      progress.show();
   }

   private void hideProgress() {
      if (progress != null)
         progress.cancel();
   }

   @Override
   public void onBackPressed() {
      MyUtility.toHomeScreen(getApplicationContext());
   }


   @Override
   public void onClick(@NonNull View view) {
      switch (view.getId()) {
         case R.id.textViewCreateAccount:
            SignUpActivity.startMe(getApplicationContext());
            break;

         case R.id.buttonLogin:
            showProgress();
            validateAndLoginUser();
            break;

         default:
            logI(Constants.TAG, "switch statement with unhandled case");
            break;
      }
   }

   private ProgressDialog progress;

   private void validateAndLoginUser() {
      logI(Constants.TAG, "Validating and Login User");
      if (MyUtility.validateEmailAddress(editTextEmail, textInputLayoutEmailAddress)
            && MyUtility.validateLoginPassword(editTextPassword, textInputLayoutPassword)) {

         final String email = editTextEmail.getText().toString();
         final String password = editTextPassword.getText().toString();

         logI(Constants.TAG, "Username: " + email);
         logI(Constants.TAG, "Password: " + password);

         FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
               .addOnSuccessListener(authResult -> {
                  logI(Constants.TAG, "Successful Login");

                  loginUser(authResult.getUser().getUid());

                  SharedPrefHelper.writePrefString(getApplicationContext(), Constants.LOGIN_EMAIL, email);
                  SharedPrefHelper.writePrefString(getApplicationContext(), Constants.LOGIN_PASSWORD, password);
               })
               .addOnFailureListener(e -> {
                  showMessage("Error: " + e.getMessage());
                  progress.cancel();
               });
      }
   }

   private void loginUser(@NonNull final String uId) {
      final DatabaseReference ref = MyUtility.getUsersNodeRef().child(uId);

      ref.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
               User user = new User(dataSnapshot, ref, MyUtility.getCurrentUserUID(), settings);
               String token = MyUtility.generateToken();
               ref.child(Constants.FIREBASE_KEY_USER_TOKEN).setValue(token);
               SharedPrefHelper.writePrefString(getApplicationContext(), Constants.FIREBASE_KEY_USER_TOKEN, token);
               handleUserType(user);
            } else {
               hideProgress();
               MyUtility.signOutCurrentUser();
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
            logI(Constants.TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
         }
      });
   }

   private void handleUserType(@NonNull User user) {

      switch (user.getUserType()) {
         case Constants.USER_TYPE_UNVERIFIED_DRIVER:
            hideProgress();
            showMessage("Unverified Driver, please proceed to documents verification");
            DriverVerificationActivity.startMe(getApplicationContext());
            finish();
            break;
         case Constants.USER_TYPE_PENDING_VERIFICATION_DRIVER:
            hideProgress();
            showMessage("Your documents are currently being uploaded for review");
            break;
         case Constants.USER_TYPE_DRIVER_SUSPENDED:
            hideProgress();
            if (MyUtility.getCurrentUser() != null) {
               FirebaseAuth.getInstance().signOut();
            }
            showMessage("Your account is suspended");
            break;
         case Constants.USER_TYPE_USER_SUSPENDED:
            hideProgress();
            if (MyUtility.getCurrentUser() != null) {
               MyUtility.signOutCurrentUser();
            }
            showMessage("Your account is suspended");
            break;
         case Constants.USER_TYPE_VERIFIED_DRIVER:
            // hideProgress();
            //  showAppVersion();
            showMessage("Welcome back Driver: " + user.getFullName());
            DeliveryManHomeActivity.startMe(getApplicationContext());
            finish();
            break;
         case Constants.USER_TYPE_USER:
            // hideProgress();
//                showAppVersion();
            showMessage("Welcome back User: " + user.getFullName());
            ClientHomeActivity.startMe(getApplicationContext());
            finish();
            break;
         default:
            logI(Constants.TAG, "switch statement with unhandled case");
            break;
      }
   }
}
