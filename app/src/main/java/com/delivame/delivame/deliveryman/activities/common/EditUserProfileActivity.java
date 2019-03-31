package com.delivame.delivame.deliveryman.activities.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditUserProfileActivity extends BaseActivity implements InitManager.InitManagerListener {

   @BindView(R.id.imageViewDriverPhoto)
   ImageView imageViewDriverPhoto;
   @BindView(R.id.textViewEditPhoto)
   TextView textViewEditPhoto;
   @BindView(R.id.editTextFullName)
   EditText editTextFullName;
   @BindView(R.id.textInputLayoutFullname)
   TextInputLayout textInputLayoutFullname;
   @BindView(R.id.editTextEmailAddress)
   EditText editTextEmailAddress;
   @BindView(R.id.textInputLayoutEmailAddress)
   TextInputLayout textInputLayoutEmailAddress;
   @BindView(R.id.buttonSaveUserInformation)
   Button buttonSaveUserInformation;

   private Boolean isImageChanged = false;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_edit_user_profile);
      ButterKnife.bind(this);

      new InitManager().init(EditUserProfileActivity.this);
   }

   @OnClick({R.id.textViewEditPhoto, R.id.buttonSaveUserInformation})
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.textViewEditPhoto:
            CropImage.activity()
                  .setGuidelines(CropImageView.Guidelines.ON)
                  .start(EditUserProfileActivity.this);
            break;
         case R.id.buttonSaveUserInformation:
            updateUserInformation();
            break;
      }
   }

   public static void startMe(Context context) {
      Intent intent = new Intent(context, EditUserProfileActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (data == null)
         return;

      switch (requestCode) {
         case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
               isImageChanged = true;
               final Uri resultUri = result.getUri();

               try {
                  imageViewDriverPhoto.setImageBitmap(
                        MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri));
               } catch (IOException e) {
                  e.printStackTrace();
               }

               //updateCaption(result);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
               Exception error = result.getError();
            }
            break;

         default:
            MyUtility.logI(Constants.TAG, "switch statement with unhandled case");
            break;
      }
   }


   private void updateUserInformation() {

      final ProgressDialog progress = new ProgressDialog(this);
      progress.setMessage("Saving user information...");
      progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progress.setIndeterminate(true);
      progress.setProgress(0);
      progress.show();

      // ---------------------------------------------------------------------------------------
      // Get the current user
      // ---------------------------------------------------------------------------------------
      final User user = currentUser;

      // ---------------------------------------------------------------------------------------
      // Update Fullname and Email address
      // ---------------------------------------------------------------------------------------
      user.setFullName(textInputLayoutFullname.getEditText().getText().toString());
      user.setEmailAddress(textInputLayoutEmailAddress.getEditText().getText().toString());


      // ---------------------------------------------------------------------------------------
      // Check if user image has changed
      // ---------------------------------------------------------------------------------------
      if (isImageChanged) {
         // Create a storage reference from our app
         final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

         // Create a reference to "mountains.jpg"
         StorageReference mountainsRef = storageRef.child(user.getUID() + ".jpg");

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
               progress.cancel();
               ProfileActivity.startMe(getApplicationContext());
               finish();
            } else {
               progress.cancel();
            }
         });

         //deprecated
//         uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @SuppressWarnings("VisibleForTests")
//            @Override
//            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//               user.setPhotoUrl(taskSnapshot.getDownloadUrl().toString());
//               user.saveUser();
//               progress.cancel();
//               ProfileActivity.startMe(getApplicationContext());
//               finish();
//            }
//
//         }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//               progress.cancel();
//
//            }
//         });
      } else {
         user.saveUser();
         progress.cancel();
         ProfileActivity.startMe(getApplicationContext());
         finish();
      }

   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      initUI();
   }

   private void initUI() {

      initUI(getString(R.string.ui_activity_title_edit_user_information), true);

      textInputLayoutFullname.getEditText().setText(currentUser.getFullName());
      textInputLayoutEmailAddress.getEditText().setText(currentUser.getEmailAddress());
   }
}
