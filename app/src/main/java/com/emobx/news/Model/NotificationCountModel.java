package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationCountModel {

    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("unread_notification_count")
    @Expose
    private Integer unreadNotificationCount;
    @SerializedName("message")
    @Expose
    private String message;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getUnreadNotificationCount() {
        return unreadNotificationCount;
    }

    public void setUnreadNotificationCount(Integer unreadNotificationCount) {
        this.unreadNotificationCount = unreadNotificationCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}