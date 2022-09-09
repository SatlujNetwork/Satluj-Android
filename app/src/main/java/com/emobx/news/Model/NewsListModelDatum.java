package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NewsListModelDatum extends RealmObject implements Serializable {

    @PrimaryKey
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
    @SerializedName("sub_title")
    @Expose
    private String subTitle;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("content_file")
    @Expose
    private String contentFile;
    @SerializedName("is_like")
    @Expose
    private String isLike;
    @SerializedName("is_bookmark")
    @Expose
    private String isBookmark;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("cover_image")
    @Expose
    private String coverImage;
    @SerializedName("cover_image_local")
    @Expose
    private String coverImageLocal;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("comments")
    @Expose
    private Integer comments;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("category_name")
    @Expose
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getCoverImageLocal() {
        return coverImageLocal;
    }

    public void setCoverImageLocal(String coverImageLocal) {
        this.coverImageLocal = coverImageLocal;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

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
        if (type == null)
            return "";
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentFile() {
        return contentFile;
    }

    public void setContentFile(String contentFile) {
        this.contentFile = contentFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
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

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
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

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
