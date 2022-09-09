package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VideoWebSeriesModel {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("video")
        @Expose
        private ArrayList<Video> video = null;
        @SerializedName("webseries")
        @Expose
        private ArrayList<Webseries> webseries = null;
        @SerializedName("has_more_webseries")
        @Expose
        private Boolean hasMoreWebseries;
        @SerializedName("has_more_video")
        @Expose
        private Boolean hasMoreVideo;
        @SerializedName("error_code")
        @Expose
        private String errorCode;
        @SerializedName("message")
        @Expose
        private String message;

        public ArrayList<Video> getVideo() {
            return video;
        }

        public void setVideo(ArrayList<Video> video) {
            this.video = video;
        }

        public ArrayList<Webseries> getWebseries() {
            return webseries;
        }

        public void setWebseries(ArrayList<Webseries> webseries) {
            this.webseries = webseries;
        }

        public Boolean getHasMoreWebseries() {
            return hasMoreWebseries;
        }

        public void setHasMoreWebseries(Boolean hasMoreWebseries) {
            this.hasMoreWebseries = hasMoreWebseries;
        }

        public Boolean getHasMoreVideo() {
            return hasMoreVideo;
        }

        public void setHasMoreVideo(Boolean hasMoreVideo) {
            this.hasMoreVideo = hasMoreVideo;
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

        public class Video {

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
            @SerializedName("is_flash")
            @Expose
            private String isFlash;
            @SerializedName("type")
            @Expose
            private String type;
            @SerializedName("content_file")
            @Expose
            private String contentFile;
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
            @SerializedName("tags")
            @Expose
            private String tags;
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
            @SerializedName("comments")
            @Expose
            private Integer comments;
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

            public String getIsFlash() {
                return isFlash;
            }

            public void setIsFlash(String isFlash) {
                this.isFlash = isFlash;
            }

            public String getType() {
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

            public String getSubTitle() {
                return subTitle;
            }

            public void setSubTitle(String subTitle) {
                this.subTitle = subTitle;
            }

            public String getCoverImage() {
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

            public String getTags() {
                return tags;
            }

            public void setTags(String tags) {
                this.tags = tags;
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

            public Integer getComments() {
                return comments;
            }

            public void setComments(Integer comments) {
                this.comments = comments;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

        }

        public class Webseries {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("title")
            @Expose
            private String title;
            @SerializedName("tag_line")
            @Expose
            private String tagLine;
            @SerializedName("release_year")
            @Expose
            private String releaseYear;
            @SerializedName("no_of_parts")
            @Expose
            private Integer noOfParts;
            @SerializedName("is_hd")
            @Expose
            private String isHd;
            @SerializedName("generes")
            @Expose
            private Object generes;
            @SerializedName("release_date")
            @Expose
            private Object releaseDate;
            @SerializedName("description")
            @Expose
            private String description;
            @SerializedName("cast_id")
            @Expose
            private String castId;
            @SerializedName("creator")
            @Expose
            private String creator;
            @SerializedName("thumb_nail")
            @Expose
            private String thumbNail;
            @SerializedName("video_url")
            @Expose
            private String videoUrl;
            @SerializedName("director")
            @Expose
            private String director;
            @SerializedName("executive_producer")
            @Expose
            private String executiveProducer;
            @SerializedName("music")
            @Expose
            private String music;
            @SerializedName("makeup")
            @Expose
            private String makeup;
            @SerializedName("director_of_photography")
            @Expose
            private String directorOfPhotography;
            @SerializedName("parent_web_series_id")
            @Expose
            private Integer parentWebSeriesId;
            @SerializedName("season")
            @Expose
            private Integer season;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("updated_at")
            @Expose
            private String updatedAt;
            @SerializedName("deleted_at")
            @Expose
            private Object deletedAt;
            @SerializedName("get_episodes_count")
            @Expose
            private Integer getEpisodesCount;
            @SerializedName("get_season_count")
            @Expose
            private Integer getSeasonCount;
            @SerializedName("get_trailer")
            @Expose
            private Object getTrailer;

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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTagLine() {
                return tagLine;
            }

            public void setTagLine(String tagLine) {
                this.tagLine = tagLine;
            }

            public String getReleaseYear() {
                return releaseYear;
            }

            public void setReleaseYear(String releaseYear) {
                this.releaseYear = releaseYear;
            }

            public Integer getNoOfParts() {
                return noOfParts;
            }

            public void setNoOfParts(Integer noOfParts) {
                this.noOfParts = noOfParts;
            }

            public String getIsHd() {
                return isHd;
            }

            public void setIsHd(String isHd) {
                this.isHd = isHd;
            }

            public Object getGeneres() {
                return generes;
            }

            public void setGeneres(Object generes) {
                this.generes = generes;
            }

            public Object getReleaseDate() {
                return releaseDate;
            }

            public void setReleaseDate(Object releaseDate) {
                this.releaseDate = releaseDate;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getCastId() {
                return castId;
            }

            public void setCastId(String castId) {
                this.castId = castId;
            }

            public String getCreator() {
                return creator;
            }

            public void setCreator(String creator) {
                this.creator = creator;
            }

            public String getThumbNail() {
                return thumbNail;
            }

            public void setThumbNail(String thumbNail) {
                this.thumbNail = thumbNail;
            }

            public String getVideoUrl() {
                return videoUrl;
            }

            public void setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
            }

            public String getDirector() {
                return director;
            }

            public void setDirector(String director) {
                this.director = director;
            }

            public String getExecutiveProducer() {
                return executiveProducer;
            }

            public void setExecutiveProducer(String executiveProducer) {
                this.executiveProducer = executiveProducer;
            }

            public String getMusic() {
                return music;
            }

            public void setMusic(String music) {
                this.music = music;
            }

            public String getMakeup() {
                return makeup;
            }

            public void setMakeup(String makeup) {
                this.makeup = makeup;
            }

            public String getDirectorOfPhotography() {
                return directorOfPhotography;
            }

            public void setDirectorOfPhotography(String directorOfPhotography) {
                this.directorOfPhotography = directorOfPhotography;
            }

            public Integer getParentWebSeriesId() {
                return parentWebSeriesId;
            }

            public void setParentWebSeriesId(Integer parentWebSeriesId) {
                this.parentWebSeriesId = parentWebSeriesId;
            }

            public Integer getSeason() {
                return season;
            }

            public void setSeason(Integer season) {
                this.season = season;
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

            public Integer getGetEpisodesCount() {
                return getEpisodesCount;
            }

            public void setGetEpisodesCount(Integer getEpisodesCount) {
                this.getEpisodesCount = getEpisodesCount;
            }

            public Integer getGetSeasonCount() {
                return getSeasonCount;
            }

            public void setGetSeasonCount(Integer getSeasonCount) {
                this.getSeasonCount = getSeasonCount;
            }

            public Object getGetTrailer() {
                return getTrailer;
            }

            public void setGetTrailer(Object getTrailer) {
                this.getTrailer = getTrailer;
            }

        }
    }
}




