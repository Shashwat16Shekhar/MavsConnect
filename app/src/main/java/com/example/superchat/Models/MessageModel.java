package com.example.superchat.Models;

import android.net.Uri;

public class MessageModel {

    String uId , message, messageId, userName;
    Long timestamp;
    String imageName,imageUrl;

    public MessageModel(String uId, String message, String messageId, String userName, Long timestamp, String imageName, String imageUrl) {
        this.uId = uId;
        this.message = message;
        this.messageId = messageId;
        this.userName = userName;
        this.timestamp = timestamp;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public MessageModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public  MessageModel(){}

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }


    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
