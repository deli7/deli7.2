package com.delivame.delivame.deliveryman.activities.common.Chat;

class Chat {

   public static final int NOT_AUTO_MESSAGE = -1;

   //if this = -1 means it is not an auto message
   private int autoMessageIndex = NOT_AUTO_MESSAGE;
   private String message;
   private String id;
   private String senderId;
   private boolean isDelivery;
   private String senderName;

   //these used to
   private String value0;
   private String value1;

   public Chat() {
   }

   public Chat(int autoMessageIndex) {
      this.autoMessageIndex = autoMessageIndex;
   }

   public String getSenderName() {
      return senderName;
   }

   public void setSenderName(String senderName) {
      this.senderName = senderName;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getSenderId() {
      return senderId;
   }

   public void setSenderId(String senderId) {
      this.senderId = senderId;
   }

   public boolean isDelivery() {
      return isDelivery;
   }

   public void setDelivery(boolean delivery) {
      isDelivery = delivery;
   }

   public int getAutoMessageIndex() {
      return autoMessageIndex;
   }

   public String getValue0() {
      return value0;
   }

   public void setValue0(String value0) {
      this.value0 = value0;
   }

   public String getValue1() {
      return value1;
   }

   public void setValue1(String value1) {
      this.value1 = value1;
   }
}
