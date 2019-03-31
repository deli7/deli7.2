package com.delivame.delivame.deliveryman.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NoCurrentRecordsFragment extends Fragment {

   private static final String ARG_PARAM1 = "";

   @BindView(R.id.textViewNoCurrentRecords)
   TextView textViewNoCurrentRecords;
   private Unbinder unbinder;

   // TODO: Rename and change types of parameters
   private String noCurrentViewText;


   public NoCurrentRecordsFragment() {
      // Required empty public constructor
   }

   public static NoCurrentRecordsFragment newInstance(String noCurrentViewText) {
      NoCurrentRecordsFragment fragment = new NoCurrentRecordsFragment();
      Bundle args = new Bundle();
      args.putString(ARG_PARAM1, noCurrentViewText);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (getArguments() != null) {
         noCurrentViewText = getArguments().getString(ARG_PARAM1);
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_no_current_orders, container, false);

      unbinder = ButterKnife.bind(this, view);
      textViewNoCurrentRecords.setText(noCurrentViewText);
      return view;
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      unbinder.unbind();
   }
}
