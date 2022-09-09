package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleNewModel {

    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("data")
    @Expose
    private NewsListModelDatum data;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public NewsListModelDatum getData() {
        return data;
    }

    public void setData(NewsListModelDatum data) {
        this.data = data;
    }

}