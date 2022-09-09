package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CategoryListModel extends RealmObject {

    @PrimaryKey
    @SerializedName("Id")
    @Expose
    private int id;
    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("data")
    @Expose
    private RealmList<CategoryListDatumModel> data = null;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public RealmList<CategoryListDatumModel> getData() {
        return data;
    }

    public void setData(RealmList<CategoryListDatumModel> data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

