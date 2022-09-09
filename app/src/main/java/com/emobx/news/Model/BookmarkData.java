package com.emobx.news.Model;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookmarkData {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("has_more")
    @Expose
    private boolean hasMore;
    @SerializedName("message")
    @Expose
    private String message;

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
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


    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("is_bookmark")
        @Expose
        private String isBookmark;
        @SerializedName("news_id")
        @Expose
        private Integer newsId;
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
        @SerializedName("get_news")
        @Expose
        private GetNews getNews;

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

        public String getIsBookmark() {
            return isBookmark;
        }

        public void setIsBookmark(String isBookmark) {
            this.isBookmark = isBookmark;
        }

        public Integer getNewsId() {
            return newsId;
        }

        public void setNewsId(Integer newsId) {
            this.newsId = newsId;
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

        public GetNews getGetNews() {
            return getNews;
        }

        public void setGetNews(GetNews getNews) {
            this.getNews = getNews;
        }

    }

    public class GetNews {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("cat_id")
        @Expose
        private Integer catId;
        @SerializedName("is_featured")
        @Expose
        private String isFeatured;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("content_file")
        @Expose
        private Object contentFile;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("sub_title")
        @Expose
        private String subTitle;
        @SerializedName("cover_image")
        @Expose
        private String coverImage;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("notify_to")
        @Expose
        private Object notifyTo;
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
        @SerializedName("is_like")
        @Expose
        private String isLike;
        @SerializedName("is_bookmark")
        @Expose
        private String isBookmark;
        @SerializedName("likes")
        @Expose
        private Integer likes;
        @SerializedName("category")
        @Expose
        private String category;

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

        public Integer getCatId() {
            return catId;
        }

        public void setCatId(Integer catId) {
            this.catId = catId;
        }

        public String getIsFeatured() {
            return isFeatured;
        }

        public void setIsFeatured(String isFeatured) {
            this.isFeatured = isFeatured;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getContentFile() {
            return contentFile;
        }

        public void setContentFile(Object contentFile) {
            this.contentFile = contentFile;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getCoverImage() {
            if (coverImage == null)
                return "";
            return coverImage;
        }

        public void setCoverImage(String coverImage) {
            this.coverImage = coverImage;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getNotifyTo() {
            return notifyTo;
        }

        public void setNotifyTo(Object notifyTo) {
            this.notifyTo = notifyTo;
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

        public String getIsLike() {
            return isLike;
        }

        public void setIsLike(String isLike) {
            this.isLike = isLike;
        }

        public String getIsBookmark() {
            return isBookmark;
        }

        public void setIsBookmark(String isBookmark) {
            this.isBookmark = isBookmark;
        }

        public Integer getLikes() {
            return likes;
        }

        public void setLikes(Integer likes) {
            this.likes = likes;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

    }

}

