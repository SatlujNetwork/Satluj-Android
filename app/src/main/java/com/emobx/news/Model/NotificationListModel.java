package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class NotificationListModel implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<NewsListModelDatum> data = null;
    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("has_more")
    @Expose
    private Boolean hasMore;
    @SerializedName("message")
    @Expose
    private String message;

    public ArrayList<NewsListModelDatum> getData() {
        return data;
    }

    public void setData(ArrayList<NewsListModelDatum> data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}