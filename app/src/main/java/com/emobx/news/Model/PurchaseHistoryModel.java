package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PurchaseHistoryModel {

    @SerializedName("productId")
    @Expose
    private Object productId;
    @SerializedName("purchaseToken")
    @Expose
    private Object purchaseToken;
    @SerializedName("purchaseTime")
    @Expose
    private Object purchaseTime;
    @SerializedName("developerPayload")
    @Expose
    private Object developerPayload;

    public Object getProductId() {
        return productId;
    }

    public void setProductId(Object productId) {
        this.productId = productId;
    }

    public Object getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(Object purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public Object getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Object purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public Object getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(Object developerPayload) {
        this.developerPayload = developerPayload;
    }

}
