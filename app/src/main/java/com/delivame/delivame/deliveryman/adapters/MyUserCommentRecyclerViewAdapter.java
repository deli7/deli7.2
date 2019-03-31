package com.delivame.delivame.deliveryman.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.UserComment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyUserCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyUserCommentRecyclerViewAdapter.ViewHolder> {

   private final List<UserComment> mValues;


   public MyUserCommentRecyclerViewAdapter(List<UserComment> items) {
      mValues = items;
   }

   private Context context;

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      context = parent.getContext();
      View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fragment_usercomment, parent, false);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
      //holder.mView = mValues.get(position);
      holder.textViewCommentUserName.setText(mValues.get(position).getUserName());
      if (TextUtils.isEmpty(mValues.get(position).getUserComment())) {
         holder.textViewUserComment.setText(context.getString(R.string.no_comment));
      } else {
         holder.textViewUserComment.setText(mValues.get(position).getUserComment());
      }
      holder.ratingBar.setRating(mValues.get(position).getOrderRating());
   }

   @Override
   public int getItemCount() {
      return mValues.size();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {

      @BindView(R.id.textViewCommentUserName)
      TextView textViewCommentUserName;
      @BindView(R.id.textViewUserComment)
      TextView textViewUserComment;
      @BindView(R.id.textViewRating)
      TextView textViewRating;
      @BindView(R.id.ratingBar)
      RatingBar ratingBar;
      @BindView(R.id.linearLayoutRank)
      LinearLayout linearLayoutRank;

      public ViewHolder(View itemView) {
         super(itemView);

         ButterKnife.bind(this, itemView);
      }

      @Override
      public String toString() {
         return super.toString() + " '" + textViewUserComment.getText() + "'";
      }
   }
}
