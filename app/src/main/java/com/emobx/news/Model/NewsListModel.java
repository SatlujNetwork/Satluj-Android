package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NewsListModel extends RealmObject {

    @PrimaryKey
    @SerializedName("Id")
    @Expose
    private Integer id;

    @SerializedName("data")
    @Expose
    private RealmList<NewsListModelDatum> data = null;
    @SerializedName("total_likes")
    @Expose
    private Integer totalLikes;
    @SerializedName("has_more")
    @Expose
    private boolean hasMore;
    @SerializedName("error_code")
    @Expose
    private String errorCode;

    public RealmList<NewsListModelDatum> getData() {
        return data;
    }

    public void setData(RealmList<NewsListModelDatum> data) {
        this.data = data;
    }

    public Integer getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

}
