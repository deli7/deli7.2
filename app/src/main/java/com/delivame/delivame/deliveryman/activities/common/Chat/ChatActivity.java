package com.delivame.delivame.deliveryman.activities.common.Chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.activities.common.FullScreenViewActivity;
import com.delivame.delivame.deliveryman.activities.deliveryman.AddNewOrderReceiptActivity;
import com.delivame.delivame.deliveryman.models.DeliveryOrder;
import com.delivame.delivame.deliveryman.models.Settings;
import com.delivame.delivame.deliveryman.models.User;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.FirebaseFileUploadHelper;
import com.delivame.delivame.deliveryman.utilities.InitManager;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.delivame.delivame.deliveryman.utilities.Constants.ORDER_ID;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;

public class ChatActivity extends BaseActivity implements InitManager.InitManagerListener {

   @BindView(R.id.textViewOrderDeliveryCost)
   TextView textViewOrderDeliveryCost;
   @BindView(R.id.textViewOrderReceiptCost)
   TextView textViewOrderReceiptCost;
   @BindView(R.id.imageButtonLoadImage)
   ImageButton imageButtonLoadImage;
   @BindView(R.id.linearLayoutReceiptCost)
   LinearLayout linearLayoutReceiptCost;
   @BindView(R.id.textViewOrderTotalCost)
   TextView textViewOrderTotalCost;
   @BindView(R.id.linearLayoutCosts)
   LinearLayout linearLayoutCosts;
   @BindView(R.id.rvChat)
   RecyclerView rvChat;
   @BindView(R.id.etText)
   EditText etText;
   @BindView(R.id.btSent)
   Button btSent;
   @BindView(R.id.relativeLayout)
   RelativeLayout relativeLayout;
   @BindView(R.id.textViewViewReceipt)
   TextView textViewViewReceipt;

   @BindView(R.id.btn_auto_msg)
   Button BtnAutoMsg;

   private EditText metText;
   private Button mbtSent;
   private DatabaseReference mFirebaseRef;

   private RecyclerView mRecyclerView;
   private ChatAdapter mAdapter;
   private String mId;

   private DeliveryOrder deliveryOrder;
   private String orderId;
   private boolean support = false;

   public static int msgIndex = 0;

   public static List<Integer> chatAutoMsgIndexes = new ArrayList<>();
   List<String> AutoMsgButton = new ArrayList<>();
   String invoiceAmount = "";
   boolean firstTime = true;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_delivery_order_chat);
      msgIndex = 0;
      DefineChatAutoMsgs();
      ButterKnife.bind(this);
      getExtras();
      new InitManager().init(ChatActivity.this);
   }


   private void DefineChatAutoMsgs() {
      AutoMsgButton.add(getString(R.string.start_trip));//DELIVERY_ORDER_STATUS_ORDER_ACCEPTED
      AutoMsgButton.add(getString(R.string.Invoice_received));//DELIVERY_ORDER_STATUS_ORDER_PICKED_UP
      AutoMsgButton.add(getString(R.string.order_received));//DELIVERY_ORDER_STATUS_ORDER_PICKED_UP
      AutoMsgButton.add(getString(R.string.Arrived_to_client));//DELIVERY_ORDER_STATUS_ORDER_DELIVERED
      AutoMsgButton.add(getString(R.string.order_delivered));//DELIVERY_ORDER_STATUS_ORDER_COMPLETED

      chatAutoMsgIndexes.add(R.string.start_trip); //this value is not used
      chatAutoMsgIndexes.add(R.string.invoice_received);
      chatAutoMsgIndexes.add(R.string.Order_received_msg);
      chatAutoMsgIndexes.add(R.string.Arrived_to_client_msg);
      chatAutoMsgIndexes.add(R.string.order_delivered);
   }

   private void getExtras() {
      Intent intent = getIntent();
      if (intent != null) {
         orderId = intent.getStringExtra(Constants.ORDER_ID);
         support = intent.getBooleanExtra(Constants.SUPPORT, false);
      }
   }

   public static void startMe(Context context, String id, boolean support) {
      Intent intent = new Intent(context, ChatActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra(Constants.ORDER_ID, id);
      intent.putExtra(Constants.SUPPORT, support);
      context.startActivity(intent);
   }

   private void loadOrderAndOffers() {
      // ---------------------------------------------------------------------------------------
      // Reading order data
      // ---------------------------------------------------------------------------------------
      MyUtility.getOrdersNodeRef().child(orderId).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.getValue() != null) {
                 /* mChats.clear();
                    mAdapter.notifyDataSetChanged();
*/
               deliveryOrder = dataSnapshot.getValue(DeliveryOrder.class);
               mAdapter.setRestaurant(deliveryOrder.isRestaurant());

               if (deliveryOrder.isNew()) {
                  textViewOrderDeliveryCost.setText("");
                  textViewOrderReceiptCost.setText("");
                  textViewOrderTotalCost.setText("");
               }

               String currency = settings.getCurrency(getApplicationContext());
               if (deliveryOrder.isAwarded()) {
                  double offerValue = deliveryOrder.getAcceptedOffer().getOfferValue();

                  textViewOrderDeliveryCost.setText(String.valueOf(offerValue) + " " + currency);
                  textViewOrderReceiptCost.setText("");
                  textViewOrderTotalCost.setText("");
               } else {

                  double offerValue = deliveryOrder.getAcceptedOffer().getOfferValue();
                  double receiptValue = deliveryOrder.getReceiptValue();
                  double totalValue = offerValue + receiptValue;

                  textViewOrderDeliveryCost.setText(String.valueOf(MyUtility.roundDouble0(offerValue)) + " " + currency);
                  textViewOrderReceiptCost.setText(String.valueOf(MyUtility.roundDouble0(receiptValue)) + " " + currency);
                  textViewOrderTotalCost.setText(String.valueOf(MyUtility.roundDouble0(totalValue)) + " " + currency);
               }

               switch (deliveryOrder.getOrderStatus()) {

                  case Constants.DELIVERY_ORDER_STATUS_ORDER_ACCEPTED:
                     msgIndex = 0;
                     break;

                  case Constants.DELIVERY_ORDER_STATUS_ORDER_PICKED_UP:
                     if (msgIndex == 1) {
                        msgIndex = 2;
                     } else {
                        msgIndex = 3;
                     }
                     break;

                  case Constants.DELIVERY_ORDER_STATUS_ORDER_DELIVERED:
                     msgIndex = 4;
                     break;
               }

               if (msgIndex >= chatAutoMsgIndexes.size()) {//5
                  msgIndex = chatAutoMsgIndexes.size() - 1;
                  BtnAutoMsg.setVisibility(View.GONE);
               }

               BtnAutoMsg.setText(AutoMsgButton.get(msgIndex));
               if (firstTime) {
                  initChat();
                  firstTime = false;
               }

               // if(!IsAlreadyListen) {
                   /*     mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                           //     IsAlreadyListen = true;
                                if (dataSnapshot.getValue() == null) {
                                    String message = getString(R.string.ui_label_activity_title_order_details) + ":\n";
                                    message += deliveryOrder.getInstructions();
                                    mId = DateTimeUtil.getCurrentDateTime();
                                    Chat chat = new Chat();
                                    chat.setDelivery(currentUser.isDeliveryMan());
                                    chat.setMessage(message);
                                    chat.setId(mId);
                                    chat.setSenderName(deliveryOrder.getClientName());
                                    chat.setSenderId(deliveryOrder.getClientId());
                                    mFirebaseRef.child(mId).setValue(chat);
                                }

                                initChat();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });*/
            }

            // }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });
   }

   @Override
   public void initUI(Settings settings, User currentUser) {
      this.settings = settings;
      this.currentUser = currentUser;

      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      if (support) {
         getSupportActionBar().setTitle(getString(R.string.chatting_with) + " " + getString(R.string.ui_label_support));
      } else {
         getSupportActionBar().setTitle(getString(R.string.chatting_with) + " " + currentUser.getFullName());
      }

      metText = findViewById(R.id.etText);
      mbtSent = findViewById(R.id.btSent);
      mRecyclerView = findViewById(R.id.rvChat);

      mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
      //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));
      mAdapter = new ChatAdapter(this, settings.getCurrency(this));
      mRecyclerView.setAdapter(mAdapter);

      FirebaseDatabase database = FirebaseDatabase.getInstance();
      if (support) {
         mFirebaseRef = database.getReference("SupportMessages").child(currentUser.getUID());
      } else {
         mFirebaseRef = database.getReference("ChatMessages").child(orderId);
         loadOrderAndOffers();
      }

      if (currentUser.isDeliveryMan()) {
         BtnAutoMsg.setVisibility(View.VISIBLE);
      }
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, final Intent data) {

      switch (requestCode) {
         case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
            if (data == null)
               return;
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
               final Uri resultUri = result.getUri();

//                    try {
//                        imageViewDriverPhoto.setImageBitmap(
//                                MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

               //updateCaption(result);
               new FirebaseFileUploadHelper(ChatActivity.this).uploadFile(
                     resultUri,
                     "chat_" + DateTimeUtil.getCurrentDateTime()
               );
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
               Exception error = result.getError();
               logI(TAG, "error = " + error);
            }
            break;

         case Constants.REQ_ADD_ORDER_RECEIPT:
            if (resultCode == RESULT_OK) {
               invoiceAmount = data.getStringExtra("invoice_amount");
               String message = getString(R.string.invoice_received, invoiceAmount,
                     settings.getCurrency(getApplicationContext()));
               mId = DateTimeUtil.getCurrentDateTime();

               Chat chat = new Chat(1);
               chat.setDelivery(currentUser.isDeliveryMan());
               chat.setMessage(message);
               chat.setValue0(invoiceAmount);
               chat.setId(mId);
               chat.setSenderName(currentUser.getFullName());
               chat.setSenderId(currentUser.getUID());
               mFirebaseRef.child(mId).setValue(chat);

               new Handler().postDelayed(() -> {
                  String mIdImage = DateTimeUtil.getCurrentDateTime();
                  String ImageUrl = data.getStringExtra("image_url");
                  Chat chatImage = new Chat();
                  chatImage.setDelivery(currentUser.isDeliveryMan());
                  chatImage.setMessage(ImageUrl);
                  chatImage.setId(mIdImage);
                  chatImage.setSenderName(currentUser.getFullName());
                  chatImage.setSenderId(currentUser.getUID());
                  mFirebaseRef.child(mIdImage).setValue(chatImage);
               }, 2000);

               msgIndex++;
               BtnAutoMsg.setText(AutoMsgButton.get(msgIndex));
               deliveryOrder.pickUpOrder();
            }
            break;

         default:
            logI(TAG, "switch statement with unhandled case");
            break;
      }
   }


   private void initChat() {

      BtnAutoMsg.setOnClickListener(view -> {//0,1,2,3,4
         if (msgIndex >= chatAutoMsgIndexes.size()) {//5
            return;
         }

         String message;
         Chat chat = new Chat(msgIndex);
         if (msgIndex == 0) {
            String phoneNumber = deliveryOrder.getInternationalPhoneNumber();
            if (phoneNumber != null && phoneNumber.trim().equals("")) {
               phoneNumber = null;
            }

            boolean isRestaurant = deliveryOrder.isRestaurant();
            String senderName = currentUser.getFullName();
            if (phoneNumber != null && isRestaurant) {
               message = getString(R.string.start_trip_restaurant, phoneNumber, senderName);
               chat.setValue0(phoneNumber);
            } else if (isRestaurant) {
               message = getString(R.string.start_trip_restaurant_no_phone, senderName);
            } else {
               message = getString(R.string.start_trip_other, senderName);
            }
         } else
            message = getString(chatAutoMsgIndexes.get(msgIndex));

         if (!message.isEmpty()) {
            if (msgIndex != 1) {
               mId = DateTimeUtil.getCurrentDateTime();
               chat.setDelivery(currentUser.isDeliveryMan());
               if (msgIndex == 2 && deliveryOrder.getAcceptedOffer() != null) {
                  String offerValue = String.valueOf(deliveryOrder.getAcceptedOffer().getOfferValue());
                  double total = Float.parseFloat(offerValue) + Float.parseFloat(invoiceAmount);
                  message = message + "\n" + getString(R.string.total_fees, offerValue,
                        invoiceAmount, String.valueOf(total), settings.getCurrency(this));
                  chat.setValue0(offerValue);
                  chat.setValue1(invoiceAmount);
               }
               chat.setMessage(message);
               chat.setId(mId);
               chat.setSenderName(currentUser.getFullName());
               chat.setSenderId(currentUser.getUID());
               mFirebaseRef.child(mId).setValue(chat);
            }

            switch (msgIndex) {
               case 0:
                  metText.setText("");
                  msgIndex++;
                  if (msgIndex >= chatAutoMsgIndexes.size()) {
                     return;
                  }
                  BtnAutoMsg.setText(AutoMsgButton.get(msgIndex));
                  break;

               case 1:
                  Intent intent = new Intent(getApplicationContext(), AddNewOrderReceiptActivity.class);
                  intent.putExtra(ORDER_ID, deliveryOrder.getId());
                  startActivityForResult(intent, Constants.REQ_ADD_ORDER_RECEIPT);
                  return;

               case 2:
                  metText.setText("");
                  msgIndex++;
                  if (msgIndex >= chatAutoMsgIndexes.size()) {
                     return;
                  }
                  BtnAutoMsg.setText(AutoMsgButton.get(msgIndex));
                  break;

               case 3:
                  metText.setText("");
                  msgIndex++;
                  if (msgIndex >= chatAutoMsgIndexes.size()) {
                     return;
                  }
                  BtnAutoMsg.setText(AutoMsgButton.get(msgIndex));
                  break;

               case 4:
                  //order Completed
                  deliveryOrder.deliverOrder();
                  deliveryOrder.getAcceptedOffer().finishOffer();
                  deliveryOrder.finishOrder();

                  metText.setText("");
                  msgIndex++;
                  if (msgIndex >= chatAutoMsgIndexes.size()) {
                     BtnAutoMsg.setVisibility(View.GONE);
                     return;
                  }
                  BtnAutoMsg.setText(AutoMsgButton.get(msgIndex));
                  break;
            }
         }
      });

      mbtSent.setOnClickListener(v -> {
         String message = metText.getText().toString();

         if (!message.isEmpty()) {
            mId = DateTimeUtil.getCurrentDateTime();

            Chat chat = new Chat();
            chat.setDelivery(currentUser.isDeliveryMan());
            chat.setMessage(message);
            chat.setId(mId);
            chat.setSenderName(currentUser.getFullName());
            chat.setSenderId(currentUser.getUID());
            mFirebaseRef.child(mId).setValue(chat);
         }

         metText.setText("");
      });
      /*  mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //     IsAlreadyListen = true;
                if (dataSnapshot.getValue() == null) {
                    String message = getString(R.string.ui_label_activity_title_order_details) + ":\n";
                    message += deliveryOrder.getInstructions();
                    mId = DateTimeUtil.getCurrentDateTime();
                    Chat chat = new Chat();
                    chat.setDelivery(currentUser.isDeliveryMan());
                    chat.setMessage(message);
                    chat.setId(mId);
                    chat.setSenderName(deliveryOrder.getClientName());
                    chat.setSenderId(deliveryOrder.getClientId());
                    mFirebaseRef.child(mId).setValue(chat);
                }

                initChat();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

      //  clearChat=true;
      mFirebaseRef.addChildEventListener(new ChildEventListener() {

         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.getValue() != null) {
               Chat model = dataSnapshot.getValue(Chat.class);
               mAdapter.addItem(model);
               mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
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
            Log.d(TAG, databaseError.getMessage());
         }
      });
   }


   @OnClick(R.id.imageButtonLoadImage)
   public void onClick() {

      CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(ChatActivity.this);
   }
/*
    @Override
    public void fileUploaded(String url) {
      *//*  mId = DateTimeUtil.getCurrentDateTime();
        Chat chat = new Chat();
        chat.setDelivery(currentUser.isDeliveryMan());
        chat.setMessage(url);
        chat.setId(mId);
        chat.setSenderName(currentUser.getFullName());
        chat.setSenderId(currentUser.getUID());
        mFirebaseRef.child(mId).setValue(chat);*//*
    }*/

   @OnClick(R.id.textViewViewReceipt)
   public void onViewReceiptClick() {
      FullScreenViewActivity.startMe(getApplicationContext(), deliveryOrder.getReceiptUrl());
   }
}
