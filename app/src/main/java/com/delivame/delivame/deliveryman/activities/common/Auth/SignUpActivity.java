package com.delivame.delivame.deliveryman.activities.common.Auth;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.activities.IntroWizardActivity;
import com.delivame.delivame.deliveryman.activities.client.ClientHomeActivity;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends BaseActivity implements InitManager.InitManagerListener {


   @BindView(R.id.editTextFullName)
   EditText editTextFullName;
   @BindView(R.id.editTextEmailAddress)
   EditText editTextEmailAddress;
   @BindView(R.id.imageViewDriverPhoto)
   ImageView imageViewDriverPhoto;
   @BindView(R.id.buttonReceiver)
   Button buttonReceiver;
   @BindView(R.id.buttonDeliveryMan)
   Button buttonDeliveryMan;
   @BindView(R.id.linearLayoutTakePhoto)
   LinearLayout linearLayoutTakePhoto;
   @BindView(R.id.linearLayoutGallery)
   LinearLayout linearLayoutGallery;
   @BindView(R.id.buttonNext)
   Button buttonNext;
   @BindView(R.id.imageButtonTakePhoto)
   ImageButton imageButtonTakePhoto;
   @BindView(R.id.imageButtonBrowseForImage)
   ImageButton imageButtonBrowseForImage;

   private boolean isDeliveryMan = false;
   private int users_count = 0;
   @NonNull
   private String uid = "";

   public static void startMe(Context context) {
      Intent intent = new Intent(context, SignUpActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }


   public static Intent createIntent(
         Context context,
         IdpResponse idpResponse,
         SignedInConfig signedInConfig) {

      Intent startIntent = new Intent();
      if (idpResponse != null) {
         startIntent.putExtra(Constants.EXTRA_IDP_RESPONSE, idpResponse);
      }

      return startIntent.setClass(context, SignUpActivity.class)
            .putExtra(Constants.EXTRA_SIGNED_IN_CONFIG, signedInConfig);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {

      setContentView(R.layout.activity_signup);
      ButterKnife.bind(this);
      super.onCreate(savedInstanceState);

      //new InitManager().init(SignUpActivity.this);
      initUI();
   }


   private void initUI() {

      initUI(getString(R.string.ui_activity_title_register), false);

      FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
      if (currentUser == null) {
         Intent intent = new Intent(getApplicationContext(), IntroWizardActivity.class);
         startActivity(intent);
         finish();
      }
   }


   public void createAccount() {

      if (TextUtils.isEmpty(editTextEmailAddress.getText().toString())) {
         Toast.makeText(this, getString(R.string.ui_message_error_invalid_email_address), Toast.LENGTH_SHORT).show();
         return;
      }

      if (TextUtils.isEmpty(editTextFullName.getText().toString())) {
         Toast.makeText(this, getString(R.string.ui_message_error_invalid_user_name), Toast.LENGTH_SHORT).show();
         return;
      }

      if (!isTypeSelected) {
         Toast.makeText(this, getString(R.string.ui_message_error_user_type_not_selected), Toast.LENGTH_SHORT).show();
         return;
      }


      final ProgressDialog progress = new ProgressDialog(this);
      progress.setMessage(getString(R.string.ui_button_create_user_account));
      progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progress.setIndeterminate(true);
      progress.setProgress(0);
      progress.show();

      FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
      if (currentUser != null) {
         uid = currentUser.getUid();
         createUserData();
      }
   }

   private void createUserData() {

      final String fullName = editTextFullName.getText().toString();
      final String emailAddress = editTextEmailAddress.getText().toString();

      MyUtility.getCountersNodeRef().addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.getValue() != null) {

               final ProgressDialog progress = new ProgressDialog(SignUpActivity.this);
               progress.setMessage("Saving user information...");
               progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               progress.setIndeterminate(true);
               progress.setProgress(0);
               progress.show();

               users_count = MyUtility.readUsersCount(dataSnapshot);

               DatabaseReference ref = MyUtility.getUsersNodeRef().child(uid);
               String token = MyUtility.generateToken();
               ref.child(Constants.FIREBASE_KEY_USER_TOKEN).setValue(token);

               {
                  int userType;
                  if (isDeliveryMan) {
                     userType = Constants.USER_TYPE_UNVERIFIED_DRIVER;
                  } else {
                     userType = Constants.USER_TYPE_USER;
                  }
                  final User user = new User(fullName, emailAddress,
                        FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                        userType, ref, null, null, uid, users_count, 0, 20);

                  // Create a storage reference from our app
                  final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                  // Create a reference to "mountains.jpg"
                  StorageReference mountainsRef = storageRef.child(uid + ".jpg");

                  // Get the data from an ImageView as bytes
                  imageViewDriverPhoto.setDrawingCacheEnabled(true);
                  imageViewDriverPhoto.buildDrawingCache();
                  Bitmap bitmap = imageViewDriverPhoto.getDrawingCache();
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                  byte[] data = baos.toByteArray();

                  UploadTask uploadTask = mountainsRef.putBytes(data);
                  uploadTask.continueWithTask(task -> {
                     if (!task.isSuccessful()) {
                        throw task.getException();
                     }
                     return mountainsRef.getDownloadUrl();
                  }).addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                        user.setPhotoUrl(task.getResult().toString());

                        user.saveUser();

                        if (isDeliveryMan) {
                           DriverVerificationActivity.startMe(getApplicationContext());
                           finish();
                        } else {
                           ClientHomeActivity.startMe(getApplicationContext());
                           finish();
                        }
                     } else {
                        progress.cancel();
                        showMessage("Error: " + task.getException().getMessage());
                     }
                  });

                  //deprecated
//                  uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//
//                     @Override
//                     public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//
//                        user.setPhotoUrl(taskSnapshot.getDownloadUrl().toString());
//
//                        user.saveUser();
//
//                        if (isDeliveryMan) {
//                           DriverVerificationActivity.startMe(getApplicationContext());
//                           finish();
//                        } else {
//                           ClientHomeActivity.startMe(getApplicationContext());
//                           finish();
//                        }
//                     }
//                  }).addOnFailureListener(new OnFailureListener() {
//
//                     @Override
//                     public void onFailure(@NonNull Exception e) {
//                        progress.cancel();
//                        showMessage("Error: " + e.getMessage());
//                     }
//                  });
               }
            } else {
               AlertDialog alertDialog = new AlertDialog.Builder(
                     new ContextThemeWrapper(SignUpActivity.this,
                           R.style.AppCompatAlertDialogStyle)).create();
               alertDialog.setTitle(getString(R.string.ui_dialog_title_error));
               alertDialog.setMessage(getString(R.string.ui_message_error_invalid_configuration));
               alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                     (dialog, which) -> {
                        dialog.dismiss();
                        MyUtility.toHomeScreen(getApplicationContext());
                        finish();
                     });
               alertDialog.show();
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
            MyUtility.logI(Constants.TAG, Constants.FIREBASE_ON_CANCELLED_EVENT);
         }
      });
   }

   public void takePhoto() {
      Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      // TODO Fix no activity available
      if (data == null)
         return;

      switch (requestCode) {
         case Constants.PICKFILE_RESULT_CODE:
            if (resultCode == RESULT_OK) {

               Uri FilePath = data.getData();

               try {
                  imageViewDriverPhoto.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), FilePath));
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
            break;
         case Constants.CAMERA_REQUEST:
            MyUtility.logI(Constants.TAG, "CAMERA_REQUEST");
            if (resultCode == RESULT_OK) {
               Bitmap photo = (Bitmap) data.getExtras().get("data");
               imageViewDriverPhoto.setImageBitmap(photo);
            }
            break;

         default:
            MyUtility.logI(Constants.TAG, "switch statement with unhandled case");
            break;
      }

   }


   public void broweForImage() {
      Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
      fileIntent.setType("image/*");

      try {
         startActivityForResult(fileIntent, Constants.PICKFILE_RESULT_CODE);
      } catch (ActivityNotFoundException e) {
         MyUtility.logI(Constants.TAG, "No activity can handle picking a file. Showing alternatives.");
      }
   }


   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      initUI();
   }

   boolean isTypeSelected = false;

   @OnClick({R.id.linearLayoutTakePhoto,
         R.id.linearLayoutGallery,
         R.id.buttonReceiver,
         R.id.buttonDeliveryMan,
         R.id.buttonNext,
         R.id.imageButtonBrowseForImage,
         R.id.imageButtonTakePhoto})
   public void onClick(View view) {
      int grey_color = ContextCompat.getColor(getApplicationContext(), R.color.lightGreyColor);
      int primary_color = ContextCompat.getColor(getApplicationContext(), R.color.primaryColor);

      switch (view.getId()) {
         case R.id.linearLayoutTakePhoto:
         case R.id.imageButtonTakePhoto:
            takePhoto();
            break;

         case R.id.linearLayoutGallery:
         case R.id.imageButtonBrowseForImage:
            broweForImage();
            break;

         case R.id.buttonReceiver:
            isTypeSelected = true;
            isDeliveryMan = false;
            buttonReceiver.setBackgroundColor(primary_color);
            buttonDeliveryMan.setBackgroundColor(grey_color);
            break;

         case R.id.buttonDeliveryMan:
            isTypeSelected = true;
            buttonReceiver.setBackgroundColor(grey_color);
            buttonDeliveryMan.setBackgroundColor(primary_color);
            isDeliveryMan = true;
            break;

         case R.id.buttonNext:
            createAccount();
            break;
      }
   }


   public static final class SignedInConfig implements Parcelable {
      final int logo;
      final int theme;
      final List<AuthUI.IdpConfig> providerInfo;
      final String tosUrl;
      final boolean isCredentialSelectorEnabled;
      final boolean isHintSelectorEnabled;

      public SignedInConfig(int logo,
                            int theme,
                            List<AuthUI.IdpConfig> providerInfo,
                            String tosUrl,
                            boolean isCredentialSelectorEnabled,
                            boolean isHintSelectorEnabled) {
         this.logo = logo;
         this.theme = theme;
         this.providerInfo = providerInfo;
         this.tosUrl = tosUrl;
         this.isCredentialSelectorEnabled = isCredentialSelectorEnabled;
         this.isHintSelectorEnabled = isHintSelectorEnabled;
      }

      SignedInConfig(Parcel in) {
         logo = in.readInt();
         theme = in.readInt();
         providerInfo = new ArrayList<>();
         in.readList(providerInfo, AuthUI.IdpConfig.class.getClassLoader());
         tosUrl = in.readString();
         isCredentialSelectorEnabled = in.readInt() != 0;
         isHintSelectorEnabled = in.readInt() != 0;
      }

      public static final Creator<SignedInConfig> CREATOR = new Creator<SignedInConfig>() {
         @Override
         public SignedInConfig createFromParcel(Parcel in) {
            return new SignedInConfig(in);
         }

         @Override
         public SignedInConfig[] newArray(int size) {
            return new SignedInConfig[size];
         }
      };

      @Override
      public int describeContents() {
         return 0;
      }

      @Override
      public void writeToParcel(Parcel dest, int flags) {
         dest.writeInt(logo);
         dest.writeInt(theme);
         dest.writeList(providerInfo);
         dest.writeString(tosUrl);
         dest.writeInt(isCredentialSelectorEnabled ? 1 : 0);
         dest.writeInt(isHintSelectorEnabled ? 1 : 0);
      }
   }
}
