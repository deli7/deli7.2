package com.delivame.delivame.deliveryman.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.Store;
import com.delivame.delivame.deliveryman.utilities.LocaleHelper;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;


public class StoresRecyclerAdapter extends RecyclerView.Adapter<StoresRecyclerAdapter.MyPlaceViewHolder> {


   private List<Store> storeList;
   private final Context context;
   private final LayoutInflater layoutInflater;
   private final StoreRecyclerAdapterListener listener;


   public StoresRecyclerAdapter(Context context, List<Store> storeList) {
      this.context = context;
      this.storeList = storeList;
      layoutInflater = LayoutInflater.from(context);
      listener = (StoreRecyclerAdapterListener) context;
   }

   @NonNull
   @Override
   public MyPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new MyPlaceViewHolder(layoutInflater.inflate(R.layout.list_item_store, parent, false));
   }


   @Override
   public void onBindViewHolder(@NonNull MyPlaceViewHolder holder, int position) {
      Store store = storeList.get(position);
      holder.setData(store);
   }

   @Override
   public int getItemCount() {
      return storeList.size();
   }


   class MyPlaceViewHolder extends RecyclerView.ViewHolder {

      @BindView(R.id.textViewStorePlaceName)
      TextView textViewStorePlaceName;
      @BindView(R.id.ratingBar)
      RatingBar ratingBar;
      @BindView(R.id.textViewStoreVicinity)
      TextView textViewStoreVicinity;
      @BindView(R.id.imageViewStoreThumbnail)
      ImageView imageViewStoreThumbnail;
      @BindView(R.id.textViewRating)
      TextView textViewRating;
      @BindView(R.id.linearLayoutStore)
      LinearLayout linearLayoutStore;
      @BindView(R.id.txt_open_now)
      TextView txtOpenNow;


      MyPlaceViewHolder(@NonNull View itemView) {
         super(itemView);

         ButterKnife.bind(this, itemView);

         if (!LocaleHelper.isLanguageEnglish(context)) {
            imageViewStoreThumbnail.setImageResource(R.mipmap.ic_arrow_left);
         }

         linearLayoutStore.setOnClickListener(
               v -> listener.makeOrder(storeList.get(getAdapterPosition())));

         imageViewStoreThumbnail.setOnClickListener(
               v -> listener.makeOrder(storeList.get(getAdapterPosition())));
      }


      void setData(@NonNull final Store store) {
         textViewStorePlaceName.setText(store.getPlaceName());
         textViewStoreVicinity.setText(store.getVicinity());
         ratingBar.setRating((float) store.getRating());
         textViewRating.setText(context.getString(R.string.distance_km, store.getDistance())
               + " - " + String.valueOf(store.getRating()));

         txtOpenNow.setVisibility(View.GONE);
         if (store.getOpennow().equalsIgnoreCase("true")) {
            txtOpenNow.setText(context.getString(R.string.open));
            txtOpenNow.setTextColor(Color.parseColor("#FF2EF527"));

         } else {
            txtOpenNow.setText(context.getString(R.string.closed));
            txtOpenNow.setTextColor(Color.parseColor("#FFED1A1A"));
         }
      }

      // Request photos and metadata for the specified place.
      private void getPhotos(String placeId) {
         final GeoDataClient mGeoDataClient;
         mGeoDataClient = Places.getGeoDataClient(context);

         final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
         photoMetadataResponse.addOnCompleteListener(task -> {

            logI(TAG, "task.getResult(): " + task.getResult());

            // Get the list of photos.
            PlacePhotoMetadataResponse photos = task.getResult();
            // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
            PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
            // Get the first photo in the list.
            PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
            // Get the attribution text.
            CharSequence attribution = photoMetadata.getAttributions();
            logI(TAG, "attribution: " + attribution);
            // Get a full-size bitmap for the photo.
            Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
            photoResponse.addOnCompleteListener(task1 -> {
               PlacePhotoResponse photo = task1.getResult();
               Bitmap bitmap = photo.getBitmap();
               Glide.with(context).load(bitmap).into(imageViewStoreThumbnail);
            });
         });
      }

   }

   public void filterList(ArrayList<Store> list) {
      this.storeList = list;
      notifyDataSetChanged();
   }


   public interface StoreRecyclerAdapterListener {

      void showStore(Store store);

      void makeOrder(Store store);
   }
}
