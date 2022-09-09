package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentDetailModel {

    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("price_amount_micros")
    @Expose
    private Integer priceAmountMicros;
    @SerializedName("price_currency_code")
    @Expose
    private String priceCurrencyCode;
    @SerializedName("skuDetailsToken")
    @Expose
    private String skuDetailsToken;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getPriceAmountMicros() {
        return priceAmountMicros;
    }

    public void setPriceAmountMicros(Integer priceAmountMicros) {
        this.priceAmountMicros = priceAmountMicros;
    }

    public String getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public void setPriceCurrencyCode(String priceCurrencyCode) {
        this.priceCurrencyCode = priceCurrencyCode;
    }

    public String getSkuDetailsToken() {
        return skuDetailsToken;
    }

    public void setSkuDetailsToken(String skuDetailsToken) {
        this.skuDetailsToken = skuDetailsToken;
    }

}
