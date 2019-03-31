package com.delivame.delivame.deliveryman.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FirebaseFileUploadHelper {

   private final FirebaseFileUploadHelperListener listener;

   public FirebaseFileUploadHelper(Context context) {
      listener = (FirebaseFileUploadHelperListener) context;
   }

   public void uploadImage(ImageView imageView, String id) {

      // Create a reference to "mountains.jpg"
      StorageReference mountainsRef = FirebaseStorage.getInstance().getReference().child(id + ".jpg");

      // Get the data from an ImageView as bytes
      imageView.setDrawingCacheEnabled(true);
      imageView.buildDrawingCache();
      Bitmap bitmap = imageView.getDrawingCache();
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
            listener.fileUploaded(task.getResult().toString());
         } else {

         }
      });

      //deprecated
//      uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//         @SuppressWarnings("VisibleForTests")
//         @Override
//         public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//            listener.fileUploaded(taskSnapshot.getDownloadUrl().toString());
//         }
//      }).addOnFailureListener(new OnFailureListener() {
//         @Override
//         public void onFailure(@NonNull Exception e) {
//
//         }
//      });
   }

   public void uploadFile(@Nullable Uri file, @NonNull final String filename) {

      if (file == null) {
         MyUtility.logI(Constants.TAG, "field file is NULL");
         return;
      }
      // Create a storage reference from our app
      final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(filename);
      storageRef.putFile(file).continueWithTask(task -> {
         if (!task.isSuccessful()) {
            throw task.getException();
         }
         return storageRef.getDownloadUrl();
      }).addOnCompleteListener(task -> {
         if (task.isSuccessful()) {
            listener.fileUploaded(task.getResult().toString());
         } else {

         }
      });

      //deprecated
//      storageRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//         @Override
//         public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//            listener.fileUploaded(taskSnapshot.getDownloadUrl().toString());
//         }
//      }).addOnFailureListener(new OnFailureListener() {
//         @Override
//         public void onFailure(@NonNull Exception e) {
//
//         }
//      });
   }

   public interface FirebaseFileUploadHelperListener {
      void fileUploaded(String url);
   }
}
