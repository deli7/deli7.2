package com.delivame.delivame.deliveryman.models;

import com.delivame.delivame.deliveryman.utilities.DateTimeUtil;
import com.delivame.delivame.deliveryman.utilities.MyUtility;

public class SupportMessage {
    private String date;
    private String message;
    private String orderId;
    private String type;
    private String userId;
    private Boolean isRead;
    private String senderName;

    public SupportMessage() {
    }

    public SupportMessage(String message, String orderId, String type, String userId, String senderName) {
        this.message = message;
        this.orderId = orderId;
        this.type = type;
        this.userId = userId;
        date = DateTimeUtil.getCurrentDateTime();
        isRead = false;
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public void saveMessage(){
        MyUtility.getUserSupportMessagesNodeRef().child(date).setValue(this);
        MyUtility.getSupportMessagesNodeRef().child(date).setValue(this);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
