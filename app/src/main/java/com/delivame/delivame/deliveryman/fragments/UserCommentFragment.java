package com.delivame.delivame.deliveryman.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.adapters.MyUserCommentRecyclerViewAdapter;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.UserComment;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserCommentFragment extends Fragment {

   private static final String ARG_COLUMN_COUNT = "column-count";

   private int mColumnCount = 1;
   final List<UserComment> userComments = new ArrayList<>();


   public UserCommentFragment() {
   }

   public static UserCommentFragment newInstance(int columnCount) {
      UserCommentFragment fragment = new UserCommentFragment();
      Bundle args = new Bundle();
      args.putInt(ARG_COLUMN_COUNT, columnCount);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      if (getArguments() != null) {
         mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
      }
   }

   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_usercomment_list, container, false);

      // Set the adapter
      if (view instanceof RecyclerView) {
         Context context = view.getContext();
         final RecyclerView recyclerView = (RecyclerView) view;
         if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
         } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
         }

         MyUtility.getUserOrdersNodeRef().child(MyUtility.getCurrentUserUID())
               .addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     userComments.clear();
                     for (DataSnapshot ds :
                           dataSnapshot.getChildren()) {
                        DeliveryOrder deliveryOrder = ds.getValue(DeliveryOrder.class);
                        UserComment userComment = new UserComment(deliveryOrder.getClientName(),
                              deliveryOrder.getOrderFeedback(), deliveryOrder.getId(), deliveryOrder.getOrderRank());

                        userComments.add(userComment);
                     }

                     recyclerView.setAdapter(new MyUserCommentRecyclerViewAdapter(userComments));
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
                  }
               });
      }
      return view;
   }

   public interface OnListFragmentInteractionListener {
      // TODO: Update argument type and name
      void onListFragmentInteraction(UserComment item);
   }

   @Override
   public void onDetach() {
      super.onDetach();
   }
}
