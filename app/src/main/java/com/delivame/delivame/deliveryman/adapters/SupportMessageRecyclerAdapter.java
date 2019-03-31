package com.delivame.delivame.deliveryman.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.models.SupportMessage;
import com.delivame.delivame.deliveryman.utilities.MyUtility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SupportMessageRecyclerAdapter extends RecyclerView.Adapter<SupportMessageRecyclerAdapter.MyPlaceViewHolder> {


    private List<SupportMessage> SupportMessageList;
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final SupportMessageRecyclerAdapterListener listener;


    public SupportMessageRecyclerAdapter(Context context, List<SupportMessage> SupportMessageList) {
        this.context = context;
        this.SupportMessageList = SupportMessageList;
        layoutInflater = LayoutInflater.from(context);
        listener = (SupportMessageRecyclerAdapterListener) context;
    }

    @NonNull
    @Override
    public MyPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_item_support_message, parent, false);
        return new MyPlaceViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyPlaceViewHolder holder, int position) {
        SupportMessage SupportMessage = SupportMessageList.get(position);
        holder.setData(SupportMessage);
    }

    @Override
    public int getItemCount() {
        return SupportMessageList.size();
    }


    class MyPlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewFrom)
        TextView textViewFrom;
        @BindView(R.id.textViewMessageDate)
        TextView textViewMessageDate;
        @BindView(R.id.textViewMessageBody)
        TextView textViewMessageBody;
        @BindView(R.id.imageViewMessageDetails)
        ImageView imageViewMessageDetails;
        @BindView(R.id.linearLayoutMessage)
        LinearLayout linearLayoutMessage;

        MyPlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            MyUtility.handleArrowDirection(context, imageViewMessageDetails);
        }


        void setData(@NonNull final SupportMessage supportMessage) {

            textViewFrom.setText(supportMessage.getSenderName());
            textViewMessageDate.setText(supportMessage.getDate());
            if (supportMessage.getMessage().length() > 25) {
                textViewMessageBody.setText(supportMessage.getMessage().substring(0, 25) + "...");
            } else {
                textViewMessageBody.setText(supportMessage.getMessage());
            }

            if (!supportMessage.getRead()) {
                textViewFrom.setTypeface(null, Typeface.BOLD);
                textViewMessageDate.setTypeface(null, Typeface.BOLD);
                textViewMessageBody.setTypeface(null, Typeface.BOLD);
            }

            linearLayoutMessage.setOnClickListener(v -> {
                textViewFrom.setTypeface(null, Typeface.NORMAL);
                textViewMessageDate.setTypeface(null, Typeface.NORMAL);
                textViewMessageBody.setTypeface(null, Typeface.NORMAL);

                listener.showSupportMessage(supportMessage);
            });
        }
    }

    public void filterList(ArrayList<SupportMessage> list) {
        this.SupportMessageList = list;
        notifyDataSetChanged();
    }

    public interface SupportMessageRecyclerAdapterListener {
        void showSupportMessage(SupportMessage SupportMessage);
    }
}
