package com.delivame.delivame.deliveryman.models;

public class CreditRequest {

   public static final int CREDIT_REQ_TYPE_ADD_CREDIT = 0;
   public static final int CREDIT_REQ_TYPE_WITHDRAW_CREDIT = 1;

   public static final int CREDIT_REQ_STATUS_NEW = 0;
   public static final int CREDIT_REQ_STATUS_PROCESSED = 1;

   private String date;
   private String fullName;
   private String phoneNumber;
   private int requestType;
   private int requestStatus;
   private double credit;

   public CreditRequest() {
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public String getFullName() {
      return fullName;
   }

   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public double getCredit() {
      return credit;
   }

   public void setCredit(double credit) {
      this.credit = credit;
   }


   public int getRequestType() {
      return requestType;
   }

   public void setRequestType(int requestType) {
      this.requestType = requestType;
   }

   public int getRequestStatus() {
      return requestStatus;
   }

   public void setRequestStatus(int requestStatus) {
      this.requestStatus = requestStatus;
   }
}
