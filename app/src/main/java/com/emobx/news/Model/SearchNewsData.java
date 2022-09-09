package com.emobx.news.Model;

public class SearchNewsData {
    private String title, date, like;
    private int image;

    public SearchNewsData(String title, String date, String like, int image) {
        this.title = title;
        this.date = date;
        this.like = like;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
