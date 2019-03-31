package com.delivame.delivame.deliveryman.models;

/**
 * A dummy item representing a piece of userComment.
 */
public class UserComment {
    private String userName;
    private String userComment;
    private String orderNumber;
    private double orderRating;


    public UserComment(String userName, String userComment, String orderNumber, Double rating) {
        this.userName = userName;
        this.userComment = userComment;
        this.orderNumber = orderNumber;
        this.orderRating = rating;
    }

    @Override
    public String toString() {
        return userComment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public float getOrderRating() {
        return (float) orderRating;
    }

    public void setOrderRating(Double orderRating) {
        this.orderRating = orderRating;
    }
}