package com.delivame.delivame.deliveryman.models;

import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.MyUtility;

public class DeliveryOffer {
   private String deliveryManId;
   private String deliveryManName;
   private String deliveryManPhone;
   private String offerTime;
   private double offerValue;
   private String offerComment;
   private String orderId;
   private String offerNumber;
   private int offerStatus;
   private double distanceToStore;

   public DeliveryOffer() {
      offerStatus = Constants.OFFER_STATUS_NEW;
   }

   public String getDeliveryManPhone() {
      return deliveryManPhone;
   }

   public void setDeliveryManPhone(String deliveryManPhone) {
      this.deliveryManPhone = deliveryManPhone;
   }

   public String getDeliveryManId() {
      return deliveryManId;
   }

   public void setDeliveryManId(String deliveryManId) {
      this.deliveryManId = deliveryManId;
   }

   public String getOfferTime() {
      return offerTime;
   }

   public void setOfferTime(String offerTime) {
      this.offerTime = offerTime;
   }

   public double getOfferValue() {
      return offerValue;
   }

   public void setOfferValue(double offerValue) {
      this.offerValue = offerValue;
   }

   public String getOfferComment() {
      return offerComment;
   }

   public void setOfferComment(String offerComment) {
      this.offerComment = offerComment;
   }

   public String getDeliveryManName() {
      return deliveryManName;
   }

   public void setDeliveryManName(String deliveryManName) {
      this.deliveryManName = deliveryManName;
   }

   public String getOrderId() {
      return orderId;
   }

   public void setOrderId(String orderId) {
      this.orderId = orderId;
   }

   public void saveOffer() {
      MyUtility.getUserOffersNodeRef().child(deliveryManId).child(orderId).setValue(this);
      MyUtility.getOffersNodeRef().child(orderId).child(offerNumber).setValue(this);
   }

   public void awardOffer() {
      offerStatus = Constants.OFFER_STATUS_AWARDED;
      saveOffer();
   }

   public void cancelOffer() {
      offerStatus = Constants.OFFER_STATUS_CANCELLED;
      MyUtility.getUserOffersNodeRef().child(deliveryManId).child(orderId).removeValue();
      MyUtility.getOffersNodeRef().child(orderId).child(offerNumber).removeValue();
   }

   public void finishOffer() {
      offerStatus = Constants.OFFER_STATUS_FINISHED;
      MyUtility.getUserOffersNodeRef().child(deliveryManId).child(orderId).setValue(this);
      MyUtility.getOffersNodeRef().child(orderId).child(offerNumber).removeValue();
      //saveOffer();
   }

   public boolean isNotOldOffer() {
      return DateTimeUtil.dateAfterCurrentTime(offerTime);
   }

   public void deleteOffer() {
      MyUtility.getUserOffersNodeRef().child(deliveryManId).child(orderId).removeValue();
      MyUtility.getOffersNodeRef().child(orderId).child(offerNumber).removeValue();
   }

   public String getOfferNumber() {
      return offerNumber;
   }

   public void setOfferNumber(String offerNumber) {
      this.offerNumber = offerNumber;
   }

   public int getOfferStatus() {
      return offerStatus;
   }

   public void setOfferStatus(int offerStatus) {
      this.offerStatus = offerStatus;
   }

   public boolean isInProgress() {
      return offerStatus == Constants.OFFER_STATUS_AWARDED;
   }

   public Double getDistanceToStore() {
      return distanceToStore;
   }

   public void setDistanceToStore(double distanceToStore) {
      this.distanceToStore = distanceToStore;
   }

   public boolean isNew() {
      return offerStatus == Constants.OFFER_STATUS_NEW;
   }

   public boolean isAwarded() {
      return offerStatus == Constants.OFFER_STATUS_AWARDED;
   }

   public boolean isFinished() {
      return offerStatus == Constants.OFFER_STATUS_FINISHED;
   }

   public boolean isCancelled() {
      return offerStatus == Constants.OFFER_STATUS_CANCELLED;
   }


}
