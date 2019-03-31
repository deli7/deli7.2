package com.delivame.delivame.deliveryman.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.auth.FirebaseAuth;


public class WizardFragment extends Fragment {

   public int wizard_page_position;

   private WizardFragmentListener mListener;

   public WizardFragment() {
   }

   @Override
   public View onCreateView(@NonNull LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
      int layout_id = R.layout.page1;

      switch (wizard_page_position) {
         case 0:
            layout_id = R.layout.page1;
            break;

         case 1:
            layout_id = R.layout.page2;
            break;

         case 2:
            layout_id = R.layout.page3;
            break;

         case 3:
            layout_id = R.layout.page4;
            break;

         default:
            MyUtility.logI(Constants.TAG, "switch statement with unhandled case");
            break;
      }

      View view = inflater.inflate(layout_id, container, false);

      Button buttonStart = view.findViewById(R.id.start);

      ProgressBar pbBar = view.findViewById(R.id.pbBar);

      if (buttonStart != null) {
         if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            buttonStart.setVisibility(View.INVISIBLE);
            if (pbBar != null) {
               pbBar.setVisibility(View.VISIBLE);
            }
         } else {
            if (pbBar != null) {
               pbBar.setVisibility(View.GONE);
            }
         }

         buttonStart.setOnClickListener(view1 -> mListener.startRideNow());
      }

      return view;
   }

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      if (context instanceof WizardFragmentListener) {
         mListener = (WizardFragmentListener) context;
      } else {
         throw new RuntimeException(context.toString()
               + " must implement OnFragmentInteractionListener");
      }
   }

   @Override
   public void onDetach() {
      super.onDetach();
      mListener = null;
   }

   public interface WizardFragmentListener {
      void startRideNow();
   }

}
