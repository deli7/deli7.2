package com.delivame.delivame.deliveryman.activities.common.Messaging;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.SupportMessage;
import com.delivame.delivame.deliveryman.models.User;
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

public class AskSupportActivity extends BaseActivity implements InitManager.InitManagerListener {

   @BindView(R.id.spinnerSupportMessagesType)
   Spinner spinnerSupportMessagesType;
   @BindView(R.id.spinnerUserOrders)
   Spinner spinnerUserOrders;
   @BindView(R.id.textInputLayoutMessage)
   TextInputLayout textInputLayoutMessage;
   @BindView(R.id.buttonSubmitMessage)
   Button buttonSubmitMessage;

   private final List<DeliveryOrder> deliveryOrderList = new ArrayList<>();
   private final List<String> spinnerArray = new ArrayList<>();
   private ArrayAdapter<String> adapter;
   private DeliveryOrder selectedDeliveryOrder;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_ask_support);
      ButterKnife.bind(this);
      new InitManager().init(AskSupportActivity.this);
   }

   public static void startMe(Context context) {
      Intent intent = new Intent(context, AskSupportActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }


   private void initUI() {
      initUI(getString(R.string.ui_activity_title_send_message_to_support), true);

      adapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, spinnerArray);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinnerUserOrders.setAdapter(adapter);

      spinnerUserOrders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedDeliveryOrder = deliveryOrderList.get(position);
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });

      loadOrders();
   }

   private void loadOrders() {
      deliveryOrderList.clear();

      MyUtility.getUserOrdersNodeRef().child(currentUser.getUID()).addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

            if (dataSnapshot.getValue() != null) {

               DeliveryOrder deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);

               spinnerArray.add(0, "#" + deliveryOrder.getOrderNumber()
                     + " (" + deliveryOrder.getOrderStatusString(getApplicationContext()) + ") - "
                     + deliveryOrder.getOrderRequestTime());

               deliveryOrderList.add(0, deliveryOrder);
               adapter.notifyDataSetChanged();
            }
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

   @Override
   protected void onResume() {
      super.onResume();
      if (adapter != null) {
         adapter.notifyDataSetChanged();
      }
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.currentUser = currentUser;
      this.settings = settings;
      initUI();
   }

   @OnClick(R.id.buttonSubmitMessage)
   public void onClick() {

      DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            switch (which) {
               case DialogInterface.BUTTON_POSITIVE:
                  String message = textInputLayoutMessage.getEditText().getText().toString();
                  if (selectedDeliveryOrder != null) {
                     message += "\n" + getString(R.string.ui_label_order_number) + ":" + selectedDeliveryOrder.getOrderNumber();
                  }
                  message += "\n" + getString(R.string.message_type) + ": " + spinnerSupportMessagesType.getSelectedItem().toString();

                  String id = "";
                  if (selectedDeliveryOrder != null) {
                     id = selectedDeliveryOrder.getId();
                  }

                  SupportMessage supportMessage =
                        new SupportMessage(message,
                              id,
                              spinnerSupportMessagesType.getSelectedItem().toString(), currentUser.getUID(),
                              currentUser.getFullName()
                        );

                  supportMessage.saveMessage();
                  Toast.makeText(AskSupportActivity.this, getString(R.string.ui_notification_message_sent_to_support),
                        Toast.LENGTH_SHORT).show();
                  ListSupportMessagesActivity.startMe(getApplicationContext());
                  finish();
                  break;

               case DialogInterface.BUTTON_NEGATIVE:
                  //No button clicked
                  break;
            }
         }
      };


      AlertDialog.Builder builder =
            new AlertDialog.Builder(new ContextThemeWrapper(AskSupportActivity.this,
                  R.style.AppCompatAlertDialogStyle));
      builder.setMessage(getString(R.string.ui_dialog_confirm_send_support_message))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener).show();
   }
}
