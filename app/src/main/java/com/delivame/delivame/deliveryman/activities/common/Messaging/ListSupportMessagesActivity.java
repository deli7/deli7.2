package com.delivame.delivame.deliveryman.activities.common.Messaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.adapters.SupportMessageRecyclerAdapter;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.SupportMessage;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class ListSupportMessagesActivity extends BaseActivity implements InitManager.InitManagerListener,
      SupportMessageRecyclerAdapter.SupportMessageRecyclerAdapterListener {

   @BindView(R.id.editTextSearch)
   EditText editTextSearch;
   @BindView(R.id.recyclerView)
   RecyclerView recyclerView;
   @BindView(R.id.recyclerViewFrame)
   FrameLayout recyclerViewFrame;
   @BindView(R.id.fab)
   FloatingActionButton fab;

   private final List<SupportMessage> supportMessageList = new ArrayList<>();
   private SupportMessageRecyclerAdapter adapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_list_support_messages);
      ButterKnife.bind(this);

      new InitManager().init(ListSupportMessagesActivity.this);
   }

   public static void startMe(Context context) {
      Intent intent = new Intent(context, ListSupportMessagesActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }

   private void initUI() {

      initUI(getString(R.string.ui_activity_title_support_messages), true);

      initSearch();

      initVerticalRecycelerView(recyclerView, supportMessageList);

      loadMessages();

   }

   private void loadMessages() {
      MyUtility.getUserSupportMessagesNodeRef().addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

            SupportMessage supportMessage = dataSnapshot.getValue(SupportMessage.class);
            logI(Constants.TAG, "Message #" + supportMessage.getDate());
            supportMessageList.add(0, supportMessage);

            updateRecyclerView(supportMessageList, adapter, getString(R.string.ui_label_no_support_messages_available));
         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
         }

         @Override
         public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
         }
      });
   }

   private void initSearch() {

      //adding a TextChangedListener
      //to call a method whenever there is some change on the EditText
      editTextSearch.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
         }

         @Override
         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
         }

         @Override
         public void afterTextChanged(Editable editable) {
            //after the change calling the method and passing the search input
            filter(editable.toString());
         }
      });
   }

   private void filter(String text) {

      //new array list that will hold the filtered data
      ArrayList<SupportMessage> filterdList = new ArrayList<>();

      if (!TextUtils.isEmpty(text)) {
         //looping through existing elements
         for (SupportMessage model : supportMessageList) {
            if (model != null && model.getMessage() != null) {
               //if the existing elements contains the search input
               if (model.getMessage().toLowerCase().contains(text.toLowerCase())) {
                  //adding the element to filtered list
                  filterdList.add(model);
               }
            }
         }
         //calling a method of the adapter class and passing the filtered list
         adapter.filterList(filterdList);
      } else {
         //calling a method of the adapter class and passing the filtered list
         updateRecyclerView(supportMessageList, adapter, getString(R.string.ui_label_no_support_messages_available));
      }
   }


   private void initVerticalRecycelerView(RecyclerView recyclerView, List<SupportMessage> list) {

      adapter = new SupportMessageRecyclerAdapter(this, list);

      RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
      recyclerView.setLayoutManager(mLayoutManager);
      //recyclerView.addItemDecoration(new BaseActivity.GridSpacingItemDecoration(1, dpToPx(10), true));
      recyclerView.setItemAnimator(new DefaultItemAnimator());
      recyclerView.setAdapter(adapter);
      updateRecyclerView(list, adapter, getString(R.string.ui_label_no_support_messages_available));
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;
      initUI();
   }

   @Override
   public void showSupportMessage(SupportMessage supportMessage) {
      ViewSupportMessageActivity.startMe(getApplicationContext(), supportMessage.getDate());
   }

   @OnClick(R.id.fab)
   public void onClick() {
      AskSupportActivity.startMe(getApplicationContext());
   }
}
