package com.delivame.delivame.deliveryman.activities.common.Auth;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.activities.IntroWizardActivity;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.old_activities.SelectVehicleActivity;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DriverVerificationActivity extends BaseActivity implements InitManager.InitManagerListener {
   private static final int PICKFILE_RESULT_CODE = 2;

   private static final int REQ_CODE_SELECT_VEHICLE = 8000;
   private static final int PICK_DRIVING_LICENSE_RESULT_CODE = 8001;
   private static final int PICK_POLICE_CLEARANCE_CERTIFICATE_RESULT_CODE = 8002;
   private static final int PICK_VEHICLE_PERMIT_RESULT_CODE = 8003;
   private static final int PICK_COMMERCIAL_INSURANCE_RESULT_CODE = 8004;
   private static final int PICK_FITNESS_CERTIFICATE_RESULT_CODE = 8005;
   @BindView(R.id.editTextVehicleNumber)
   EditText editTextVehicleNumber;
   @BindView(R.id.textInputLayoutVehicleNumber)
   TextInputLayout textInputLayoutVehicleNumber;
   @BindView(R.id.textViewVehicleModel)
   TextView textViewVehicleModel;
   @BindView(R.id.buttonSelectModel)
   ImageButton buttonSelectModel;
   @BindView(R.id.textViewDrivingLicense)
   TextView textViewDrivingLicense;
   @BindView(R.id.buttonSelectDrivingLicense)
   ImageButton buttonSelectDrivingLicense;
   @BindView(R.id.textViewPoliceClearanceCertificate)
   TextView textViewPoliceClearanceCertificate;
   @BindView(R.id.buttonPoliceClearanceCertificate)
   ImageButton buttonPoliceClearanceCertificate;
   @BindView(R.id.textViewVehiclePermitCertificate)
   TextView textViewVehiclePermitCertificate;
   @BindView(R.id.buttonVehiclePermitCertificate)
   ImageButton buttonVehiclePermitCertificate;
   @BindView(R.id.textViewCommercialInsurance)
   TextView textViewCommercialInsurance;
   @BindView(R.id.buttonCommercialInsurance)
   ImageButton buttonCommercialInsurance;
   @BindView(R.id.textViewFitnessCertificate)
   TextView textViewFitnessCertificate;
   @BindView(R.id.buttonFitnessCertificate)
   ImageButton buttonFitnessCertificate;

   private String model = "";
   private String year = "";
   private String category_id = "";
   @NonNull
   private String vehicleNumber = "";

   @Nullable
   private Uri uriDrivingLicense = null;
   @Nullable
   private Uri uriPoliceClearanceCertificate = null;
   @Nullable
   private Uri uriCommercialInsurance = null;
   @Nullable
   private Uri uriFitnessCertificate = null;

   @Nullable
   private Uri uriVehiclePermit = null;

   String category_name;

   public static void startMe(Context context) {
      Intent intent = new Intent(context, DriverVerificationActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_driver_verification);
      ButterKnife.bind(this);

      new InitManager().init(DriverVerificationActivity.this);
   }

   private void initUI() {
      initUI(getString(R.string.driver_verification), false);
   }

   private void uploadFile(@Nullable Uri file, @NonNull final String field) {

      if (file == null) {
         MyUtility.logI(Constants.TAG, "field file is NULL");
         return;
      }

      final String uid = MyUtility.getCurrentUserUID();
      String name = uid + "_" + field;

      MyUtility.logI(Constants.TAG, "name:" + name);

      // Create a storage reference from our app
      final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(name + ".pdf");

      //deprecated
//      storageRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//         @Override
//         public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//            MyUtility.logI(Constants.TAG, "Succeeded to upload file: " + field);
//
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
//
//            ref.child(field).setValue(taskSnapshot.getDownloadUrl().toString());
//         }
//      }).addOnFailureListener(new OnFailureListener() {
//         @Override
//         public void onFailure(@NonNull Exception e) {
//            MyUtility.logI(Constants.TAG, "Failed to upload file: " + field);
//         }
//      });

      storageRef.putFile(file).continueWithTask(task -> {
         if (!task.isSuccessful()) {
            throw task.getException();
         }
         return storageRef.getDownloadUrl();
      }).addOnCompleteListener(task -> {
         MyUtility.logI(Constants.TAG, "Succeeded to upload file: " + field);
         DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

         if (task.isSuccessful()) {
            ref.child(field).setValue(task.getResult());
         } else {
            MyUtility.logI(Constants.TAG, "Failed to upload file: " + field);
         }
      });
   }

   private ProgressDialog progress;

   public void submitDocuments(View view) {

      DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
         switch (which) {
            case DialogInterface.BUTTON_POSITIVE:

               progress = new ProgressDialog(DriverVerificationActivity.this);

               MyUtility.logI(Constants.TAG, "Validating Password");
               if (TextUtils.isEmpty(editTextVehicleNumber.getText())) {
                  textInputLayoutVehicleNumber.setError(Constants.ERROR_INVALID_VEHICLE_NUMBER);
                  return;
               } else {
                  textInputLayoutVehicleNumber.setErrorEnabled(false);
               }

               if (TextUtils.isEmpty(model)) {
                  Toast.makeText(DriverVerificationActivity.this, "Invalid Vehicle Model",
                        Toast.LENGTH_SHORT).show();
                  return;
               }

               //final ProgressDialog progress = new ProgressDialog(this);
               progress.setMessage("Saving Driver Information...");
               progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               progress.setIndeterminate(true);
               progress.setProgress(0);
               progress.show();


               vehicleNumber = editTextVehicleNumber.getText().toString();

               if (MyUtility.getCurrentUser() != null) {

                  MyUtility.getCurrentUserNodeRef()
                        .child(Constants.FIREBASE_KEY_USER_VEHICLE_NUMBER).setValue(vehicleNumber);

                  MyUtility.getCurrentUserNodeRef()
                        .child(Constants.FIREBASE_KEY_USER_VEHICLE_MODEL).setValue(model);

                  MyUtility.getCurrentUserNodeRef()
                        .child(Constants.FIREBASE_KEY_USER_VEHICLE_YEAR).setValue(year);

                  MyUtility.getCurrentUserNodeRef()
                        .child(Constants.FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_ID).setValue(category_id);

                  // HACK
                  MyUtility.getCurrentUserNodeRef()
                        .child(Constants.FIREBASE_KEY_USER_TYPE).setValue(Constants.USER_TYPE_PENDING_VERIFICATION_DRIVER);

//                            MyUtility.getCurrentUserNodeRef()
//                                    .child(Constants.FIREBASE_KEY_USER_TYPE).setValue(Constants.USER_TYPE_VERIFIED_DRIVER);
//                            MyUtility.getCurrentUserNodeRef()
//                                    .child(Constants.FIREBASE_KEY_USER_ACCOUNT_POINTS).setValue(2000);

                  uploadFile(uriDrivingLicense, Constants.FIREBASE_KEY_USER_DRIVING_LICENSE);
                  uploadFile(uriPoliceClearanceCertificate, Constants.FIREBASE_KEY_USER_POLICE_CLEARANCE_CERTIFICATE);
                  uploadFile(uriVehiclePermit, Constants.FIREBASE_KEY_USER_VEHICLE_PERMIT);
                  uploadFile(uriCommercialInsurance, Constants.FIREBASE_KEY_USER_COMMERCIAL_INSURANCE);
                  uploadFile(uriFitnessCertificate, Constants.FIREBASE_KEY_USER_FITNESS_CERTIFICATE);

                  Toast.makeText(DriverVerificationActivity.this,
                        "Your documents have been received and being reviewed",
                        Toast.LENGTH_SHORT).show();

//                    if (MyUtility.getCurrentUser() != null) {
//                        FirebaseAuth.getInstance().signOut();
//                    }

                  // taskSnapshot.getMetadata() contains file metadata such as size, userComment-type, and download URL.

                  // Hack
                  startActivity(new Intent(getApplicationContext(), IntroWizardActivity.class));
               }
               break;

            case DialogInterface.BUTTON_NEGATIVE:
               //No button clicked
               break;
         }
      };


      AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppCompatAlertDialogStyle));
      builder.setMessage(getString(R.string.ui_dialog_are_you_sure_submit_documents))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener).show();
   }

   public void broweForImage(View view) {
      Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
      fileintent.setType("image/*");

      try {
         startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
      } catch (ActivityNotFoundException e) {
         Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
      }
   }


   int operation = 0;

   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      // TODO Fix no activity available
      if (data == null)
         return;
      switch (requestCode) {

         case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

               final Uri resultUri = result.getUri();

               switch (operation) {
                  case PICK_DRIVING_LICENSE_RESULT_CODE:
                     buttonSelectDrivingLicense.setImageResource(R.mipmap.ic_remove);
                     uriDrivingLicense = resultUri;

                     assert (uriDrivingLicense) != null;
                     textViewDrivingLicense.setText(MyUtility.getFileName(getApplicationContext(), uriDrivingLicense));
                     break;

                  case PICK_POLICE_CLEARANCE_CERTIFICATE_RESULT_CODE:
                     buttonPoliceClearanceCertificate.setImageResource(R.mipmap.ic_remove);
                     uriPoliceClearanceCertificate = resultUri;

                     assert (uriPoliceClearanceCertificate) != null;
                     textViewPoliceClearanceCertificate.setText(MyUtility
                           .getFileName(getApplicationContext(), uriPoliceClearanceCertificate));
                     break;

                  case PICK_COMMERCIAL_INSURANCE_RESULT_CODE:
                     buttonCommercialInsurance.setImageResource(R.mipmap.ic_remove);
                     uriCommercialInsurance = resultUri;

                     assert (uriCommercialInsurance) != null;
                     textViewCommercialInsurance.setText(MyUtility.getFileName(getApplicationContext(), uriCommercialInsurance));
                     break;

                  case PICK_VEHICLE_PERMIT_RESULT_CODE:
                     buttonVehiclePermitCertificate.setImageResource(R.mipmap.ic_remove);
                     uriVehiclePermit = resultUri;

                     assert (uriVehiclePermit) != null;
                     textViewVehiclePermitCertificate
                           .setText(MyUtility.getFileName(getApplicationContext(), uriVehiclePermit));
                     break;

                  case PICK_FITNESS_CERTIFICATE_RESULT_CODE:
                     buttonFitnessCertificate.setImageResource(R.mipmap.ic_remove);
                     uriFitnessCertificate = resultUri;

                     assert (uriFitnessCertificate) != null;
                     textViewFitnessCertificate.setText(MyUtility.getFileName(getApplicationContext(), uriFitnessCertificate));
                     break;
               }

            }
            break;

         case REQ_CODE_SELECT_VEHICLE:
            if (resultCode == RESULT_OK) {
               buttonSelectModel.setImageResource(R.mipmap.ic_remove);
               model = data.getStringExtra(Constants.SETTING_ALLOWED_VEHICLES_MODEL);
               year = data.getStringExtra(Constants.SETTING_ALLOWED_VEHICLES_YEAR);
               category_id = data.getStringExtra(Constants.FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_ID);
               category_name = data.getStringExtra(Constants.FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_NAME);

               textViewVehicleModel.setText(category_name + " - " + model + " " + year);
            }
            break;
      }
   }

   public void selectVehicleModel(View view) {
      if (TextUtils.isEmpty(category_id)) {
         //showRadioButtonDialog();

         Intent intent = new Intent(getApplicationContext(), SelectVehicleActivity.class);

         startActivityForResult(intent, REQ_CODE_SELECT_VEHICLE);
      } else {
         category_id = "";
         year = "";
         model = "";

         buttonSelectModel.setImageResource(R.mipmap.ic_add);
         textViewVehicleModel.setText(R.string.select_model);
      }
   }

   private static final int CAMERA_REQUEST = 1888;

   public void takePhoto(View view) {
      Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      startActivityForResult(cameraIntent, CAMERA_REQUEST);
   }


   public void loginAnotherAccount(View view) {
      FirebaseAuth.getInstance().signOut();

      Intent intent = new Intent(getApplicationContext(), IntroWizardActivity.class);
      startActivity(intent);
   }

   public void selectDrivingLicense(View view) {
      if (uriDrivingLicense == null) {
         operation = PICK_DRIVING_LICENSE_RESULT_CODE;
//            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
//            fileintent.setType("image/*|application/pdf");
//
//            try {
//                startActivityForResult(fileintent, PICK_DRIVING_LICENSE_RESULT_CODE);
//            } catch (ActivityNotFoundException e) {
//                Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
//            }

         CropImage.activity()
               .setGuidelines(CropImageView.Guidelines.ON)
               .start(DriverVerificationActivity.this);

      } else {
         uriDrivingLicense = null;
         textViewDrivingLicense.setText(R.string.select_file);
         buttonSelectDrivingLicense.setImageResource(R.mipmap.ic_add);
      }
   }

   public void selectPoliceClearanceCertificate(View view) {
      if (uriPoliceClearanceCertificate == null) {

         operation = PICK_POLICE_CLEARANCE_CERTIFICATE_RESULT_CODE;

         CropImage.activity()
               .setGuidelines(CropImageView.Guidelines.ON)
               .start(DriverVerificationActivity.this);

//            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
//            fileintent.setType("image/*|application/pdf");
//
//            try {
//                startActivityForResult(fileintent, PICK_POLICE_CLEARANCE_CERTIFICATE_RESULT_CODE);
//            } catch (ActivityNotFoundException e) {
//                Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
//            }
      } else {
         uriPoliceClearanceCertificate = null;
         textViewPoliceClearanceCertificate.setText(R.string.select_file);
         buttonPoliceClearanceCertificate.setImageResource(R.mipmap.ic_add);
      }
   }

   public void selectVehiclePermit(View view) {
      if (uriVehiclePermit == null) {
         operation = PICK_VEHICLE_PERMIT_RESULT_CODE;

         CropImage.activity()
               .setGuidelines(CropImageView.Guidelines.ON)
               .start(DriverVerificationActivity.this);
//            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
//            fileintent.setType("image/*|application/pdf");
//
//            try {
//                startActivityForResult(fileintent, PICK_VEHICLE_PERMIT_RESULT_CODE);
//            } catch (ActivityNotFoundException e) {
//                Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
//            }
      } else {
         uriVehiclePermit = null;
         textViewVehiclePermitCertificate.setText(R.string.select_file);
         buttonVehiclePermitCertificate.setImageResource(R.mipmap.ic_add);
      }
   }

   public void selectCommercialInsurance(View view) {
      if (uriCommercialInsurance == null) {
         operation = PICK_COMMERCIAL_INSURANCE_RESULT_CODE;

         CropImage.activity()
               .setGuidelines(CropImageView.Guidelines.ON)
               .start(DriverVerificationActivity.this);

//            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
//            fileintent.setType("image/*|application/pdf");
//
//            try {
//                startActivityForResult(fileintent, PICK_COMMERCIAL_INSURANCE_RESULT_CODE);
//            } catch (ActivityNotFoundException e) {
//                Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
//            }
      } else {
         uriCommercialInsurance = null;
         textViewCommercialInsurance.setText(R.string.select_file);
         buttonCommercialInsurance.setImageResource(R.mipmap.ic_add);
      }
   }

   public void selectFitnessCertificate(View view) {
      if (uriFitnessCertificate == null) {

         operation = PICK_FITNESS_CERTIFICATE_RESULT_CODE;

         CropImage.activity()
               .setGuidelines(CropImageView.Guidelines.ON)
               .start(DriverVerificationActivity.this);

//            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
//            fileintent.setType("image/*|application/pdf");
//
//            try {
//                startActivityForResult(fileintent, PICK_FITNESS_CERTIFICATE_RESULT_CODE);
//            } catch (ActivityNotFoundException e) {
//                Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
//            }
      } else {
         uriFitnessCertificate = null;
         textViewFitnessCertificate.setText(R.string.select_file);
         buttonFitnessCertificate.setImageResource(R.mipmap.ic_add);
      }
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.currentUser = currentUser;
      this.settings = settings;
      initUI();
   }
}
