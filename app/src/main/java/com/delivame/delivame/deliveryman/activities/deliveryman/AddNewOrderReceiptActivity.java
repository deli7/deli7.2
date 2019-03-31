package com.delivame.delivame.deliveryman.activities.deliveryman;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.FirebaseFileUploadHelper;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.delivame.delivame.deliveryman.utilities.Constants.ORDER_ID;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class AddNewOrderReceiptActivity extends BaseActivity implements InitManager.InitManagerListener, FirebaseFileUploadHelper.FirebaseFileUploadHelperListener {

   // ---------------------------------------------------------------------------------------
   // UI
   // ---------------------------------------------------------------------------------------
   @BindView(R.id.imageViewBillPhoto)
   ImageView imageViewBillPhoto;
   @BindView(R.id.imageButtonBrowseForImage)
   ImageButton imageButtonBrowseForImage;
   @BindView(R.id.imageButtonTakePhoto)
   ImageButton imageButtonTakePhoto;
   @BindView(R.id.editTextBillValue)
   EditText editTextBillValue;
   @BindView(R.id.textInputLayoutFullname)
   TextInputLayout textInputLayoutReceiptValue;
   @BindView(R.id.buttonSaveBill)
   Button buttonSaveBill;
   @BindView(R.id.progressBar)
   ProgressBar progressBar;

   // ---------------------------------------------------------------------------------------
   // Members
   // ---------------------------------------------------------------------------------------
   private String orderId;
   private DeliveryOrder deliveryOrder;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_new_order_receipt);
      ButterKnife.bind(this);

      getExtras();

      new InitManager().init(AddNewOrderReceiptActivity.this);
   }

   public static void startMe(Context context, String orderId) {
      Intent intent = new Intent(context, AddNewOrderReceiptActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      intent.putExtra(Constants.ORDER_ID, orderId);

      context.startActivity(intent);
   }

   private void getExtras() {
      Intent intent = getIntent();
      if (intent != null) {
         orderId = intent.getStringExtra(ORDER_ID);
      }
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      initUI();
   }

   private void initUI() {
      initUI(getString(R.string.ui_label_add_order_receipt), true);

      progressBar.setVisibility(View.GONE);
      loadOrder();
   }

   private void loadOrder() {
      logI(TAG, "AddReceipt - orderId: " + orderId);
      // ---------------------------------------------------------------------------------------
      // Reading order data
      // ---------------------------------------------------------------------------------------
      MyUtility.getOrdersNodeRef().child(orderId).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
               deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });

   }

   @OnClick({R.id.imageButtonBrowseForImage, R.id.imageButtonTakePhoto, R.id.buttonSaveBill})
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.imageButtonBrowseForImage:
            broweForImage();
            break;
         case R.id.imageButtonTakePhoto:
            takePhoto();
            break;
         case R.id.buttonSaveBill:
            saveBill();
            break;
      }
   }

   Boolean isImageChanged = false;

   private void saveBill() {

      if (!isImageChanged) {
         Toast.makeText(this, getString(R.string.ui_message_error_no_bill_loaded), Toast.LENGTH_SHORT).show();
         return;
      }

      if (TextUtils.isEmpty(textInputLayoutReceiptValue.getEditText().getText().toString())) {
         Toast.makeText(this, getString(R.string.ui_message_error_invalid_bill_value), Toast.LENGTH_SHORT).show();
         return;
      }

      DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
         switch (which) {
            case DialogInterface.BUTTON_POSITIVE:

               progressBar.setVisibility(View.VISIBLE);
               new FirebaseFileUploadHelper(AddNewOrderReceiptActivity.this)
                       .uploadImage(imageViewBillPhoto, "Bill_"
                               + String.valueOf(deliveryOrder.getOrderNumber()));


               break;

            case DialogInterface.BUTTON_NEGATIVE:
               //No button clicked
               break;
         }
      };


      AlertDialog.Builder builder = new AlertDialog.Builder(
              new ContextThemeWrapper(AddNewOrderReceiptActivity.this, R.style.AppCompatAlertDialogStyle));
      builder.setMessage(getString(R.string.ui_dialog_are_you_sure_pick_up_order))
              .setPositiveButton(getString(R.string.yes), dialogClickListener)
              .setNegativeButton(getString(R.string.no), dialogClickListener).show();

   }

   @Override
   public void fileUploaded(String url) {
      deliveryOrder.setReceiptUrl(url);
      deliveryOrder.setReceiptValue(Double.parseDouble(textInputLayoutReceiptValue.getEditText().getText().toString()));
      deliveryOrder.saveOrder();
      Intent i = new Intent();
      i.putExtra("invoice_amount", textInputLayoutReceiptValue.getEditText().getText().toString());
      i.putExtra("image_url", url);
      setResult(RESULT_OK, i);
      finish();
   }


   private void takePhoto() {
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
               isImageChanged = true;
               Uri FilePath = data.getData();

               try {
                  imageViewBillPhoto.setImageBitmap(MediaStore.Images.Media
                          .getBitmap(this.getContentResolver(), FilePath));

               } catch (IOException e) {

                  e.printStackTrace();
               }
            }
            break;
         case Constants.CAMERA_REQUEST:
            logI(TAG, "CAMERA_REQUEST");
            if (resultCode == RESULT_OK) {
               isImageChanged = true;
               Bitmap photo = (Bitmap) data.getExtras().get("data");
               imageViewBillPhoto.setImageBitmap(photo);
            }

            break;

         default:
            logI(TAG, "switch statement with unhandled case");
            break;
      }

   }

   private void broweForImage() {
      Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
      fileIntent.setType("image/*");

      try {
         startActivityForResult(fileIntent, Constants.PICKFILE_RESULT_CODE);
      } catch (ActivityNotFoundException e) {
         logI(TAG, "No activity can handle picking a file. Showing alternatives.");
      }
   }


}
