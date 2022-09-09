package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LiveStreamModel {

    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("btnposition")
        @Expose
        private String btnposition;
        @SerializedName("liveurl")
        @Expose
        private String liveurl;

        public String getBtnposition() {
            return btnposition;
        }

        public void setBtnposition(String btnposition) {
            this.btnposition = btnposition;
        }

        public String getLiveurl() {
            return liveurl;
        }

        public void setLiveurl(String liveurl) {
            this.liveurl = liveurl;
        }

    }
}
