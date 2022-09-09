package com.emobx.news.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WebSeriesDetailModel implements Serializable {

    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable {

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
        private String generes;
        @SerializedName("release_date")
        @Expose
        private String releaseDate;
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
        @SerializedName("cast_members")
        @Expose
        private ArrayList<CastMember> castMembers = null;
        @SerializedName("last_watch")
        @Expose
        private LastWatch lastWatch;
        @SerializedName("get_trailer")
        @Expose
        private GetTrailer getTrailer;
        @SerializedName("get_episodes")
        @Expose
        private ArrayList<LastWatch> getEpisodes = null;
        @SerializedName("get_season")
        @Expose
        private ArrayList<GetSeason> getSeason = null;

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

        public LastWatch getLastWatch() {
            return lastWatch;
        }

        public void setLastWatch(LastWatch lastWatch) {
            this.lastWatch = lastWatch;
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

        public String getGeneres() {
            return generes;
        }

        public void setGeneres(String generes) {
            this.generes = generes;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
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

        public ArrayList<CastMember> getCastMembers() {
            return castMembers;
        }

        public void setCastMembers(ArrayList<CastMember> castMembers) {
            this.castMembers = castMembers;
        }

        public GetTrailer getGetTrailer() {
            return getTrailer;
        }

        public void setGetTrailer(GetTrailer getTrailer) {
            this.getTrailer = getTrailer;
        }

        public ArrayList<LastWatch> getGetEpisodes() {
            return getEpisodes;
        }

        public void setGetEpisodes(ArrayList<LastWatch> getEpisodes) {
            this.getEpisodes = getEpisodes;
        }

        public ArrayList<GetSeason> getGetSeason() {
            return getSeason;
        }

        public void setGetSeason(ArrayList<GetSeason> getSeason) {
            this.getSeason = getSeason;
        }

        public class GetTrailer implements Serializable {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("series_id")
            @Expose
            private Integer seriesId;
            @SerializedName("season")
            @Expose
            private Integer season;
            @SerializedName("description")
            @Expose
            private String description;
            @SerializedName("thumb_nail")
            @Expose
            private String thumbNail;
            @SerializedName("video_url")
            @Expose
            private String videoUrl;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("updated_at")
            @Expose
            private String updatedAt;
            @SerializedName("deleted_at")
            @Expose
            private Object deletedAt;

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

            public Integer getSeriesId() {
                return seriesId;
            }

            public void setSeriesId(Integer seriesId) {
                this.seriesId = seriesId;
            }

            public Integer getSeason() {
                return season;
            }

            public void setSeason(Integer season) {
                this.season = season;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
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

        }

        public class GetSeason implements Serializable {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("selected_loc")
            @Expose
            private boolean selected;
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

            public boolean isSelected() {
                return selected;
            }

            public void setSelected(boolean selected) {
                this.selected = selected;
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

        }

//        public class GetEpisode implements Serializable {
//
//            @SerializedName("id")
//            @Expose
//            private Integer id;
//            @SerializedName("series_id")
//            @Expose
//            private Integer seriesId;
//            @SerializedName("season")
//            @Expose
//            private Integer season;
//            @SerializedName("thumb_nail")
//            @Expose
//            private String thumbNail;
//            @SerializedName("video_url")
//            @Expose
//            private String videoUrl;
//            @SerializedName("name")
//            @Expose
//            private String name;
//            @SerializedName("description")
//            @Expose
//            private String description;
//            @SerializedName("part")
//            @Expose
//            private Integer part;
//            @SerializedName("created_at")
//            @Expose
//            private String createdAt;
//            @SerializedName("updated_at")
//            @Expose
//            private String updatedAt;
//            @SerializedName("deleted_at")
//            @Expose
//            private Object deletedAt;
//            @SerializedName("get_user_episode")
//            @Expose
//            private GetUserEpisode getUserEpisode = null;
//
//            public Integer getId() {
//                return id;
//            }
//
//            public void setId(Integer id) {
//                this.id = id;
//            }
//
//            public Integer getSeriesId() {
//                return seriesId;
//            }
//
//            public void setSeriesId(Integer seriesId) {
//                this.seriesId = seriesId;
//            }
//
//            public Integer getSeason() {
//                return season;
//            }
//
//            public void setSeason(Integer season) {
//                this.season = season;
//            }
//
//            public String getThumbNail() {
//                return thumbNail;
//            }
//
//            public void setThumbNail(String thumbNail) {
//                this.thumbNail = thumbNail;
//            }
//
//            public String getVideoUrl() {
//                return videoUrl;
//            }
//
//            public void setVideoUrl(String videoUrl) {
//                this.videoUrl = videoUrl;
//            }
//
//            public String getName() {
//                return name;
//            }
//
//            public void setName(String name) {
//                this.name = name;
//            }
//
//            public String getDescription() {
//                return description;
//            }
//
//            public void setDescription(String description) {
//                this.description = description;
//            }
//
//            public Integer getPart() {
//                return part;
//            }
//
//            public void setPart(Integer part) {
//                this.part = part;
//            }
//
//            public String getCreatedAt() {
//                return createdAt;
//            }
//
//            public void setCreatedAt(String createdAt) {
//                this.createdAt = createdAt;
//            }
//
//            public String getUpdatedAt() {
//                return updatedAt;
//            }
//
//            public void setUpdatedAt(String updatedAt) {
//                this.updatedAt = updatedAt;
//            }
//
//            public Object getDeletedAt() {
//                return deletedAt;
//            }
//
//            public void setDeletedAt(Object deletedAt) {
//                this.deletedAt = deletedAt;
//            }
//
//            public GetUserEpisode getGetUserEpisode() {
//                return getUserEpisode;
//            }
//
//            public void setGetUserEpisode(GetUserEpisode getUserEpisode) {
//                this.getUserEpisode = getUserEpisode;
//            }
//
//            public class GetUserEpisode implements Serializable {
//
//                @SerializedName("id")
//                @Expose
//                private Integer id;
//                @SerializedName("episode_id")
//                @Expose
//                private Integer episodeId;
//                @SerializedName("resume_at")
//                @Expose
//                private String resumeAt;
//                @SerializedName("user_id")
//                @Expose
//                private Integer userId;
//                @SerializedName("created_at")
//                @Expose
//                private String createdAt;
//                @SerializedName("updated_at")
//                @Expose
//                private String updatedAt;
//                @SerializedName("deleted_at")
//                @Expose
//                private Object deletedAt;
//
//                public Integer getId() {
//                    return id;
//                }
//
//                public void setId(Integer id) {
//                    this.id = id;
//                }
//
//                public Integer getEpisodeId() {
//                    return episodeId;
//                }
//
//                public void setEpisodeId(Integer episodeId) {
//                    this.episodeId = episodeId;
//                }
//
//                public String getResumeAt() {
//                    return resumeAt;
//                }
//
//                public void setResumeAt(String resumeAt) {
//                    this.resumeAt = resumeAt;
//                }
//
//                public Integer getUserId() {
//                    return userId;
//                }
//
//                public void setUserId(Integer userId) {
//                    this.userId = userId;
//                }
//
//                public String getCreatedAt() {
//                    return createdAt;
//                }
//
//                public void setCreatedAt(String createdAt) {
//                    this.createdAt = createdAt;
//                }
//
//                public String getUpdatedAt() {
//                    return updatedAt;
//                }
//
//                public void setUpdatedAt(String updatedAt) {
//                    this.updatedAt = updatedAt;
//                }
//
//                public Object getDeletedAt() {
//                    return deletedAt;
//                }
//
//                public void setDeletedAt(Object deletedAt) {
//                    this.deletedAt = deletedAt;
//                }
//
//            }
//        }

        public class CastMember implements Serializable {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("pic")
            @Expose
            private String pic;

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

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

        }

        public class LastWatch implements Serializable {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("series_id")
            @Expose
            private Integer seriesId;
            @SerializedName("season")
            @Expose
            private Integer season;
            @SerializedName("thumb_nail")
            @Expose
            private String thumbNail;
            @SerializedName("video_url")
            @Expose
            private String videoUrl;
            @SerializedName("video_time")
            @Expose
            private Object videoTime;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("description")
            @Expose
            private String description;
            @SerializedName("part")
            @Expose
            private Integer part;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("updated_at")
            @Expose
            private String updatedAt;
            @SerializedName("deleted_at")
            @Expose
            private Object deletedAt;
            @SerializedName("get_user_episode")
            @Expose
            private GetUserEpisode getUserEpisode = null;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getSeriesId() {
                return seriesId;
            }

            public void setSeriesId(Integer seriesId) {
                this.seriesId = seriesId;
            }

            public Integer getSeason() {
                return season;
            }

            public void setSeason(Integer season) {
                this.season = season;
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

            public Object getVideoTime() {
                return videoTime;
            }

            public void setVideoTime(Object videoTime) {
                this.videoTime = videoTime;
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

            public Integer getPart() {
                return part;
            }

            public void setPart(Integer part) {
                this.part = part;
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

            public GetUserEpisode getGetUserEpisode() {
                return getUserEpisode;
            }

            public void setGetUserEpisode(GetUserEpisode getUserEpisode) {
                this.getUserEpisode = getUserEpisode;
            }

            public class GetUserEpisode implements Serializable {

                @SerializedName("id")
                @Expose
                private Integer id;
                @SerializedName("episode_id")
                @Expose
                private Integer episodeId;
                @SerializedName("series_id")
                @Expose
                private Integer seriesId;
                @SerializedName("resume_at")
                @Expose
                private Integer resumeAt;
                @SerializedName("video_total_time")
                @Expose
                private Integer videoTotalTime;
                @SerializedName("user_id")
                @Expose
                private Integer userId;
                @SerializedName("created_at")
                @Expose
                private String createdAt;
                @SerializedName("updated_at")
                @Expose
                private String updatedAt;
                @SerializedName("deleted_at")
                @Expose
                private Object deletedAt;

                public Integer getId() {
                    return id;
                }

                public void setId(Integer id) {
                    this.id = id;
                }

                public Integer getEpisodeId() {
                    return episodeId;
                }

                public void setEpisodeId(Integer episodeId) {
                    this.episodeId = episodeId;
                }

                public Integer getSeriesId() {
                    return seriesId;
                }

                public void setSeriesId(Integer seriesId) {
                    this.seriesId = seriesId;
                }

                public Integer getResumeAt() {
                    return resumeAt;
                }

                public void setResumeAt(Integer resumeAt) {
                    this.resumeAt = resumeAt;
                }

                public Integer getVideoTotalTime() {
                    return videoTotalTime;
                }

                public void setVideoTotalTime(Integer videoTotalTime) {
                    this.videoTotalTime = videoTotalTime;
                }

                public Integer getUserId() {
                    return userId;
                }

                public void setUserId(Integer userId) {
                    this.userId = userId;
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

            }

        }

    }

}

