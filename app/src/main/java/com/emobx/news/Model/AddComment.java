package com.emobx.news.Model;

public class AddComment {
    private String error_code, message;
    private CommentModel.Datum data;

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CommentModel.Datum getData() {
        return data;
    }

    public void setData(CommentModel.Datum data) {
        this.data = data;
    }
}
