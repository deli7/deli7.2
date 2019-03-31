package com.delivame.delivame.deliveryman.models;

import android.content.Context;
import android.os.Bundle;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.common.OrderActivity;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.MyUtility;
import com.delivame.delivame.deliveryman.utilities.NotificationsHelper;

import static com.delivame.delivame.deliveryman.utilities.Constants.ORDER_ID;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;
import static com.delivame.delivame.deliveryman.utilities.Constants.TAG2;
import static com.delivame.delivame.deliveryman.utilities.MyUtility.logI;


@SuppressWarnings("unused, WeakerAccess")
public class DeliveryOrder {

   private String id;
   private String orderRequestTime;

   private String startTripTime;
   private String invoiceRecTime;
   private String orderPickUpTime;
   private String orderDeliveryTime;
   private String orderCompletionTime;
   private LatLng latLngPickUpPoint;
   private LatLng latLngDestinationPoint;
   private String pickUpAddress;
   private String storeName;
   private String destinationAddress;
   private String clientId;
   private double requiredTime;
   private int orderStatus;
   private String instructions;
   private double estimatedCost;
   private double estimatedTime;
   private Double distanceToClient;
   private Double distanceToStore;
   private String promoCode;
   private Double promoCodeDiscount;
   private DeliveryOffer acceptedOffer;
   private double actualDistance;
   private double actualTime;
   private int orderNumber;
   private double orderRank;
   private String orderFeedback;
   private String clientName;
   private Double receiptValue;
   private String receiptUrl;
   private String cancellationReason;
   private String vehicleCategory;
   private String phoneNumber = "";
   private String internationalphoneNumber = "";

   private boolean isRestaurant;

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public String getInternationalPhoneNumber() {
      return internationalphoneNumber;
   }

   public void setInternationalPhoneNumber(String internationalPhoneNumber) {
      this.internationalphoneNumber = internationalPhoneNumber;
   }

   public String getVehicleCategory() {
      return vehicleCategory;
   }

   public void setVehicleCategory(String vehicleCategory) {
      this.vehicleCategory = vehicleCategory;
   }

   public String getPromoCode() {
      return promoCode;
   }

   public void setPromoCode(String promoCode) {
      this.promoCode = promoCode;
   }

   public Double getPromoCodeDiscount() {
      return promoCodeDiscount;
   }

   public void setPromoCodeDiscount(Double promoCodeDiscount) {
      this.promoCodeDiscount = promoCodeDiscount;
   }

   public String getCancellationReason() {
      return cancellationReason;
   }

   public void setCancellationReason(String cancellationReason) {
      this.cancellationReason = cancellationReason;
   }

   public void setDistanceToClient(Double distanceToClient) {
      this.distanceToClient = distanceToClient;
   }

   public Double getDistanceToClient() {
      return distanceToClient;
   }

   public Double getDistanceToStore() {
      return distanceToStore;
   }

   public void setDistanceToStore(Double distanceToStore) {
      this.distanceToStore = distanceToStore;
   }

   public String getClientName() {
      return clientName;
   }

   public void setClientName(String clientName) {
      this.clientName = clientName;
   }

   public int getOrderNumber() {
      return orderNumber;
   }

   public void setOrderNumber(int orderNumber) {
      this.orderNumber = orderNumber;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getOrderRequestTime() {
      return orderRequestTime;
   }

   public void setOrderRequestTime(String orderRequestTime) {
      this.orderRequestTime = orderRequestTime;
   }

   public String getOrderPickUpTime() {
      return orderPickUpTime;
   }

   public void setOrderPickUpTime(String orderPickUpTime) {
      this.orderPickUpTime = orderPickUpTime;
   }

   public String getStartTripTime() {
      return startTripTime;
   }

   public void setStartTripTime(String startTripTime) {
      this.startTripTime = startTripTime;
   }

   public String getOrderDeliveryTime() {
      return orderDeliveryTime;
   }

   public void setOrderDeliveryTime(String orderDeliveryTime) {
      this.orderDeliveryTime = orderDeliveryTime;
   }

   public LatLng getLatLngPickUpPoint() {
      return latLngPickUpPoint;
   }

   public void setLatLngPickUpPoint(LatLng latLngPickUpPoint) {
      this.latLngPickUpPoint = latLngPickUpPoint;
   }

   public LatLng getLatLngDestinationPoint() {
      return latLngDestinationPoint;
   }

   public void setLatLngDestinationPoint(LatLng latLngDestinationPoint) {
      this.latLngDestinationPoint = latLngDestinationPoint;
   }

   public String getPickUpAddress() {
      return pickUpAddress;
   }

   public void setPickUpAddress(String pickUpAddress) {
      this.pickUpAddress = pickUpAddress;
   }

   public String getStoreName() {
      return storeName;
   }

   public void setStoreName(String storeName) {
      this.storeName = storeName;
   }

   public String getDestinationAddress() {
      return destinationAddress;
   }

   public void setDestinationAddress(String destinationAddress) {
      this.destinationAddress = destinationAddress;
   }

   public String getClientId() {
      return clientId;
   }

   public void setClientId(String clientId) {
      this.clientId = clientId;
   }

   public int getOrderStatus() {
      return orderStatus;
   }

   public void setOrderStatus(int orderStatus) {
      this.orderStatus = orderStatus;
   }

   public String getInstructions() {
      return instructions;
   }

   public void setInstructions(String instructions) {
      this.instructions = instructions;
   }

   public double getEstimatedCost() {
      return estimatedCost;
   }

   public void setEstimatedCost(double estimatedCost) {
      this.estimatedCost = estimatedCost;
   }


   public String getOrderCompletionTime() {
      return orderCompletionTime;
   }

   public void setOrderCompletionTime(String orderCompletionTime) {
      this.orderCompletionTime = orderCompletionTime;
   }

   public double getEstimatedTime() {
      return estimatedTime;
   }

   public void setEstimatedTime(double estimatedTime) {
      this.estimatedTime = estimatedTime;
   }


   public double getActualDistance() {
      return actualDistance;
   }

   public void setActualDistance(double actualDistance) {
      this.actualDistance = actualDistance;
   }

   public double getActualTime() {
      return actualTime;
   }

   public String getInvoiceRecTime() {
      return invoiceRecTime;
   }

   public void setInvoiceRecTime(String invoiceRecTime) {
      this.invoiceRecTime = invoiceRecTime;
   }

   public void setActualTime(double actualTime) {
      this.actualTime = actualTime;
   }

   public void saveOrder() {
      MyUtility.getOrdersNodeRef().child(String.valueOf(orderNumber)).setValue(this);
      MyUtility.getUserOrdersNodeRef().child(MyUtility.getCurrentUserUID()).child(String
            .valueOf(orderNumber)).setValue(this);
      if (!isNew()) {
         if (getAcceptedOffer() != null) {
            MyUtility.getUserOrdersNodeRef().child(getAcceptedOffer().getDeliveryManId()).
                  child(String.valueOf(orderNumber)).setValue(this);
         }
      }
      MyUtility.getAllPreviousCompletedOrdersRef().child(String.valueOf(orderNumber)).setValue(this);
   }

   public void pickUpOrder() {
      setOrderPickUpTime(DateTimeUtil.getCurrentDateTime());
      orderStatus = Constants.DELIVERY_ORDER_STATUS_ORDER_PICKED_UP;
      saveOrder();
   }

   public void deliverOrder() {
      setOrderDeliveryTime(DateTimeUtil.getCurrentDateTime());
      orderStatus = Constants.DELIVERY_ORDER_STATUS_ORDER_DELIVERED;
      saveOrder();
      //deleteOrder();
   }

   public void finishOrder() {
      setOrderCompletionTime(DateTimeUtil.getCurrentDateTime());
      orderStatus = Constants.DELIVERY_ORDER_STATUS_ORDER_COMPLETED;
      saveOrder();
      if (isInProgress()) {
         MyUtility.getUserOrdersNodeRef().child(getAcceptedOffer().getDeliveryManId()).child(String.valueOf(orderNumber)).setValue(this);
      }
      deleteOrder();
   }

   public void cancelOrder() {
      setOrderCompletionTime(DateTimeUtil.getCurrentDateTime());
      orderStatus = Constants.DELIVERY_ORDER_STATUS_ORDER_CANCELLED;
      saveOrder();
      if (isInProgress()) {
         MyUtility.getUserOrdersNodeRef().child(getAcceptedOffer().getDeliveryManId()).child(String.valueOf(orderNumber)).setValue(this);
      }
      deleteOrder();
   }

   private void deleteOrder() {
      MyUtility.getOrdersNodeRef().child(String.valueOf(orderNumber)).removeValue();
   }

   public void acceptOffer(DeliveryOffer offer) {
      orderStatus = Constants.DELIVERY_ORDER_STATUS_ORDER_ACCEPTED;
      distanceToStore = offer.getDistanceToStore();
      setAcceptedOffer(offer);
      saveOrder();
   }

   public String getOrderStatusString(Context context) {
      switch (orderStatus) {
         case Constants.DELIVERY_ORDER_STATUS_NEW_ORDER:
            return context.getString(R.string.ui_button_new_order);

         case Constants.DELIVERY_ORDER_STATUS_ORDER_ACCEPTED:
            return context.getString(R.string.order_accepted);

         case Constants.DELIVERY_ORDER_STATUS_ORDER_PICKED_UP:
            return context.getString(R.string.order_picked_up);

         case Constants.DELIVERY_ORDER_STATUS_ORDER_DELIVERED:
            return context.getString(R.string.order_delivered);

         case Constants.DELIVERY_ORDER_STATUS_ORDER_COMPLETED:
            return context.getString(R.string.order_completed);

         default:
            return "";

      }
   }

   public double getOrderRank() {
      return orderRank;
   }

   public void setOrderRank(double orderRank) {
      this.orderRank = orderRank;
   }

   public String getOrderFeedback() {
      return orderFeedback;
   }

   public void setOrderFeedback(String orderFeedback) {
      this.orderFeedback = orderFeedback;
   }

   public DeliveryOffer getAcceptedOffer() {
      return acceptedOffer;
   }

   public void setAcceptedOffer(DeliveryOffer acceptedOffer) {
      this.acceptedOffer = acceptedOffer;
   }

   public boolean isAwarded() {
      return (orderStatus == Constants.DELIVERY_ORDER_STATUS_ORDER_ACCEPTED);
   }

   public boolean isPickedUp() {
      return (orderStatus == Constants.DELIVERY_ORDER_STATUS_ORDER_PICKED_UP);
   }

   public boolean isDelivered() {
      return (orderStatus == Constants.DELIVERY_ORDER_STATUS_ORDER_DELIVERED);
   }

   public boolean isCompleted() {
      return (orderStatus == Constants.DELIVERY_ORDER_STATUS_ORDER_COMPLETED);
   }

   public boolean isCancelled() {
      return (orderStatus == Constants.DELIVERY_ORDER_STATUS_ORDER_CANCELLED);
   }

   public boolean isNew() {
      return (orderStatus == Constants.OFFER_STATUS_NEW);
   }


   public boolean isClientOrder() {
      return clientId.equals(MyUtility.getCurrentUserUID());
   }

   public boolean isNotOldOrder() {
      return DateTimeUtil.dateAfterCurrentTime(orderRequestTime);
   }

   public boolean isOrderAfterTime(String time) {
      logI(TAG2, "isOrderAfterTime");
      return DateTimeUtil.isDate1AfterDate2(orderRequestTime, time);
   }

   public boolean isDeliveryManOrder() {
      return isInProgress() && getAcceptedOffer().getDeliveryManId().equals(MyUtility.getCurrentUserUID());
   }


   public void showOrderNotification(Context context, User currentUser) {

      logI(TAG, "------------------------- NOTIFICATION --------------------------");
      logI(TAG, "USER: " + currentUser.getFullName());
      if (currentUser.isDeliveryMan()) {
         logI(TAG, "USER TYPE: DELIVERY MAN");

      } else {
         logI(TAG, "USER TYPE: CLIENT");
      }

      logI(TAG, "ORDER #: " + getOrderNumber());

      Bundle bundle = new Bundle();
      bundle.putString(ORDER_ID, getId());

      switch (getOrderStatus()) {

         case Constants.DELIVERY_ORDER_STATUS_NEW_ORDER:
            logI(TAG, "ORDER STATUS: DELIVERY_ORDER_STATUS_NEW_ORDER");
            if (currentUser.isDeliveryMan()) {
               NotificationsHelper.showNotification(context,
                     context.getString(R.string.ui_notification_new_order_added),
                     context.getString(R.string.ui_notification_you_added_new_order) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                     R.drawable.logo1,
                     OrderActivity.class, bundle);
            }

            break;

         case Constants.DELIVERY_ORDER_STATUS_ORDER_ACCEPTED:
            logI(TAG, "ORDER STATUS: DELIVERY_ORDER_STATUS_ORDER_ACCEPTED");


            if (!currentUser.isDeliveryMan()) {
               logI(TAG, "User is client, showing notification: You accepted offer");
               NotificationsHelper.showNotification(context,
                     context.getString(R.string.ui_notification_order_accepted),
                     context.getString(R.string.ui_notification_you_accepted_offer) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                     R.drawable.logo1,
                     OrderActivity.class, bundle);
            } else {
               logI(TAG, "User is delivery man, checking accepted offer");
               logI(TAG, "Offer was awarded to delivery man: " + getAcceptedOffer().getDeliveryManId());
               logI(TAG, "Current user ID: " + currentUser.getUID());

               if (getAcceptedOffer().getDeliveryManId().equals(currentUser.getUID())) {
                  logI(TAG, "The accepted offer belongs to YOU");
                  logI(TAG, "Showing notification: Your offer accepted");
                  NotificationsHelper.showNotification(context,
                        context.getString(R.string.ui_notification_your_offer_accepted),
                        context.getString(R.string.ui_notification_your_offer_accepted) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                        R.drawable.logo1,
                        OrderActivity.class, bundle);

               } else {
                  logI(TAG, "The accepted offer belongs to another delivery man");
                  logI(TAG, "Showing notification, another offer accepted");
                  NotificationsHelper.showNotification(context,
                        context.getString(R.string.ui_notification_another_offer_accepted),
                        context.getString(R.string.ui_notification_another_offer_accepted) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                        R.drawable.logo1,
                        OrderActivity.class, bundle);
               }
            }

            break;

         case Constants.DELIVERY_ORDER_STATUS_ORDER_PICKED_UP:

            logI(TAG, "ORDER STATUS: DELIVERY_ORDER_STATUS_ORDER_PICKED_UP");
            if (!currentUser.isDeliveryMan()) {
               logI(TAG, "User is client, showing: Your order was picked up");
               NotificationsHelper.showNotification(context,
                     context.getString(R.string.ui_notification_your_order_picked),
                     context.getString(R.string.ui_notification_your_order_picked) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                     R.drawable.logo1,
                     OrderActivity.class, bundle);
            } else {
               logI(TAG, "User is delivery man, showing: You picked up the order");
               NotificationsHelper.showNotification(context,
                     context.getString(R.string.ui_notification_you_picked_up_the_order),
                     context.getString(R.string.ui_notification_you_picked_up_the_order) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                     R.drawable.logo1,
                     OrderActivity.class, bundle);
            }


            break;

         case Constants.DELIVERY_ORDER_STATUS_ORDER_DELIVERED:
            logI(TAG, "ORDER STATUS: DELIVERY_ORDER_STATUS_ORDER_DELIVERED");
            if (!currentUser.isDeliveryMan()) {
               logI(TAG, "User is client, showing your order was delivered");
               NotificationsHelper.showNotification(context,
                     context.getString(R.string.ui_notification_your_order_delivered),
                     context.getString(R.string.ui_notification_your_order_delivered) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                     R.drawable.logo1,
                     OrderActivity.class, bundle);
            } else {
               logI(TAG, "User is delivery man, showing you delivered order");
               NotificationsHelper.showNotification(context,
                     context.getString(R.string.ui_notification_you_delivered_order),
                     context.getString(R.string.ui_notification_you_delivered_order) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                     R.drawable.logo1,
                     OrderActivity.class, bundle);
            }

            break;

         case Constants.DELIVERY_ORDER_STATUS_ORDER_COMPLETED:

            logI(TAG, "ORDER STATUS: DELIVERY_ORDER_STATUS_ORDER_COMPLETED");

            NotificationsHelper.showNotification(context,
                  context.getString(R.string.ui_notification_order_completed),
                  context.getString(R.string.ui_notification_order_completed) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                  R.drawable.logo1,
                  OrderActivity.class, bundle);

            break;

         case Constants.DELIVERY_ORDER_STATUS_ORDER_CANCELLED:

            logI(TAG, "ORDER STATUS: DELIVERY_ORDER_STATUS_ORDER_COMPLETED");

            NotificationsHelper.showNotification(context,
                  context.getString(R.string.ui_notification_order_cancelled),
                  context.getString(R.string.ui_notification_order_cancelled) + " - " + context.getString(R.string.ui_label_order_number) + ": " + getOrderNumber(),
                  R.drawable.logo1,
                  OrderActivity.class, bundle);

            break;
      }
   }


   public boolean isInProgress() {
      return (isAwarded() || isPickedUp() || isDelivered());
   }

   public boolean passed24Hours() {
      return DateTimeUtil.isDateAfter24Hours(DateTimeUtil.getCurrentDateTime(), getOrderCompletionTime());
   }

   public double getRequiredTime() {
      return requiredTime;
   }

   public void setRequiredTime(double requiredTime) {
      this.requiredTime = requiredTime;
   }

   public Store getStore() {
      Store store = new Store();
      store.setLatLng(getLatLngPickUpPoint());
      store.setPlaceName(getStoreName());
      store.setVicinity(getPickUpAddress());
      return store;
   }

   public Double getReceiptValue() {
      return receiptValue;
   }

   public void setReceiptValue(Double receiptValue) {
      this.receiptValue = receiptValue;
   }

   public String getReceiptUrl() {
      return receiptUrl;
   }

   public void setReceiptUrl(String receiptUrl) {
      this.receiptUrl = receiptUrl;
   }

   public boolean isRestaurant() {
      return isRestaurant;
   }

   public void setRestaurant(boolean restaurant) {
      isRestaurant = restaurant;
   }
}
