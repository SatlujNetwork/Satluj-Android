package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CommentModel {

    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("has_more")
    @Expose
    private boolean hasMore;
    @SerializedName("data")
    @Expose
    private ArrayList<Datum> data = null;

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }


    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("is_spam")
        @Expose
        private String isSpam;
        @SerializedName("is_flag")
        @Expose
        private String isFlag;
        @SerializedName("news_id")
        @Expose
        private Integer newsId;
        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("created_by")
        @Expose
        private Integer createdBy;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("deleted_at")
        @Expose
        private Object deletedAt;
        @SerializedName("get_user")
        @Expose
        private GetUser getUser;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIsSpam() {
            return isSpam;
        }

        public void setIsSpam(String isSpam) {
            this.isSpam = isSpam;
        }

        public String getIsFlag() {
            return isFlag;
        }

        public void setIsFlag(String isFlag) {
            this.isFlag = isFlag;
        }

        public Integer getNewsId() {
            return newsId;
        }

        public void setNewsId(Integer newsId) {
            this.newsId = newsId;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Object getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Object deletedAt) {
            this.deletedAt = deletedAt;
        }

        public GetUser getGetUser() {
            return getUser;
        }

        public void setGetUser(GetUser getUser) {
            this.getUser = getUser;
        }

    }

    public class GetUser {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("username")
        @Expose
        private Object username;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("email_verified_at")
        @Expose
        private Object emailVerifiedAt;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("phone_code")
        @Expose
        private String phoneCode;
        @SerializedName("country_code")
        @Expose
        private String countryCode;
        @SerializedName("phone")
        @Expose
        private Object phone;
        @SerializedName("phone_verified_at")
        @Expose
        private Object phoneVerifiedAt;
        @SerializedName("device_type")
        @Expose
        private String deviceType;
        @SerializedName("device_token")
        @Expose
        private String deviceToken;
        @SerializedName("pic")
        @Expose
        private String pic;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("is_social")
        @Expose
        private String isSocial;
        @SerializedName("address")
        @Expose
        private Object address;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getUsername() {
            return username;
        }

        public void setUsername(Object username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Object getEmailVerifiedAt() {
            return emailVerifiedAt;
        }

        public void setEmailVerifiedAt(Object emailVerifiedAt) {
            this.emailVerifiedAt = emailVerifiedAt;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneCode() {
            return phoneCode;
        }

        public void setPhoneCode(String phoneCode) {
            this.phoneCode = phoneCode;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public Object getPhone() {
            return phone;
        }

        public void setPhone(Object phone) {
            this.phone = phone;
        }

        public Object getPhoneVerifiedAt() {
            return phoneVerifiedAt;
        }

        public void setPhoneVerifiedAt(Object phoneVerifiedAt) {
            this.phoneVerifiedAt = phoneVerifiedAt;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIsSocial() {
            return isSocial;
        }

        public void setIsSocial(String isSocial) {
            this.isSocial = isSocial;
        }

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
            this.address = address;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }
}
