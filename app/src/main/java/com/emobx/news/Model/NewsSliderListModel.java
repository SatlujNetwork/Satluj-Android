package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NewsSliderListModel extends RealmObject {

    @PrimaryKey
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("data")
    @Expose
    private RealmList<NewsSliderListModelDatum> data = null;
    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("message")
    @Expose
    private String message;

    public RealmList<NewsSliderListModelDatum> getData() {
        return data;
    }

    public void setData(RealmList<NewsSliderListModelDatum> data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
