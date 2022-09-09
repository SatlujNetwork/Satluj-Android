package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForceUpdateModel {

    @SerializedName("latest_version")
    @Expose
    private String latestVersion;
    @SerializedName("force_update")
    @Expose
    private Integer forceUpdate;
    @SerializedName("device_type")
    @Expose
    private String deviceType;

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public Integer getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Integer forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

}