package com.delivame.delivame.deliveryman.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.MyPlace;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyPlacesRecyclerAdapter extends RecyclerView.Adapter<MyPlacesRecyclerAdapter.MyPlaceViewHolder> {

   private final List<MyPlace> myPlaces;
   private final Context context;
   private final LayoutInflater layoutInflater;
   private final User currentUser;
   private final MyPlacesRecyclerAdapterListener mListener;


   public MyPlacesRecyclerAdapter(Context context, List<MyPlace> myPlaces, User currentUser) {
      this.context = context;
      this.myPlaces = myPlaces;
      layoutInflater = LayoutInflater.from(context);
      this.currentUser = currentUser;
      mListener = (MyPlacesRecyclerAdapterListener) context;
   }

   @NonNull
   @Override
   public MyPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      View view = layoutInflater.inflate(R.layout.list_item_myplace, parent, false);

      return new MyPlaceViewHolder(view);
   }


   @Override
   public void onBindViewHolder(@NonNull MyPlaceViewHolder holder, int position) {

      MyPlace currentMyPlace = myPlaces.get(position);
      holder.setData(currentMyPlace);
   }

   @Override
   public int getItemCount() {
      return myPlaces.size();
   }


   public interface MyPlacesRecyclerAdapterListener {
      void finishAct(String title, String address, double lat, double lng);

      void deletePlace(MyPlace place);
   }

   class MyPlaceViewHolder extends RecyclerView.ViewHolder {

      @BindView(R.id.textViewEditPlace)
      TextView textViewEditPlace;
      @BindView(R.id.textViewDeletePlace)
      TextView textViewDeletePlace;
      @BindView(R.id.textViewSelectPlace)
      TextView textViewSelectPlace;
      @BindView(R.id.textViewPlaceTitle)
      TextView textViewPlaceTitle;
      @BindView(R.id.textViewPlaceAddress)
      TextView textViewPlaceAddress;
      @BindView(R.id.textViewPlaceLatLng)
      TextView textViewPlaceLatLng;
      @BindView(R.id.cardViewMessage)
      CardView cardViewMessage;

      MyPlaceViewHolder(@NonNull View itemView) {
         super(itemView);

         ButterKnife.bind(this, itemView);
      }


      void saveMyPlace(final MyPlace point) {
         AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppCompatAlertDialogStyle));
         builder.setTitle(context.getString(R.string.ui_button_save_location_to_my_places));

         // Set up the input
         final EditText input = new EditText(context);
         input.setText(point.getAddress());
         // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
         input.setInputType(InputType.TYPE_CLASS_TEXT);
         builder.setView(input);

         // Set up the buttons
         builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
            String title = input.getText().toString();
            String address = MyUtility.getAddress(context, point.getLat(), point.getLng());

            if (currentUser != null) {
               HashMap<String, Object> map = new HashMap<>();
               map.put(Constants.MY_PLACE_ADDRESS, address);
               map.put(Constants.MY_PLACE_LAT, point.getLat());
               map.put(Constants.MY_PLACE_LNG, point.getLng());
               currentUser.getUserRef().child(Constants.FIREBASE_KEY_MY_PLACES).child(title).setValue(map);
               Toast.makeText(context, "Place has been saved", Toast.LENGTH_SHORT).show();
            }
         });
         builder.setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> dialog.cancel());

         builder.show();
      }

      void setData(@NonNull final MyPlace currentMyPlace) {

         textViewPlaceTitle.setText(currentMyPlace.getTitle());
         textViewPlaceAddress.setText(currentMyPlace.getAddress());
         //textViewPlaceLatLng.setText(currentMyPlace.getLatLng());
         textViewPlaceLatLng.setVisibility(View.GONE);

         textViewEditPlace.setVisibility(View.GONE);

//            textViewEditPlace.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    saveMyPlace(currentMyPlace);
//                }
//            });


         textViewSelectPlace.setOnClickListener(view -> {

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
               switch (which) {
                  case DialogInterface.BUTTON_POSITIVE:

                     mListener.finishAct(currentMyPlace.getTitle(), currentMyPlace.getAddress(),
                           currentMyPlace.getLat(), currentMyPlace.getLng());

                     break;

                  case DialogInterface.BUTTON_NEGATIVE:
                     //No button clicked
                     break;
               }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context,
                  R.style.AppCompatAlertDialogStyle));
            builder.setMessage(context.getString(R.string.ui_dialog_select_place_as_destination))
                  .setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                  .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
         });

         textViewDeletePlace.setOnClickListener(view -> {

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
               switch (which) {
                  case DialogInterface.BUTTON_POSITIVE:
                     //currentUser.getUserRef().child(Constants.FIREBASE_KEY_MY_PLACES).child(currentMyPlace.getTitle()).removeValue();
                     mListener.deletePlace(myPlaces.get(getAdapterPosition()));
                     break;

                  case DialogInterface.BUTTON_NEGATIVE:
                     //No button clicked
                     break;
               }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context,
                  R.style.AppCompatAlertDialogStyle));
            builder.setMessage(context.getString(R.string.ui_button_delete_place))
                  .setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                  .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();

         });
      }
   }
}
