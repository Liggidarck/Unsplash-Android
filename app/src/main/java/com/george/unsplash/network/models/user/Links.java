package com.george.unsplash.network.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    public Links(String self, String html, String photos, String likes, String portfolio) {
        this.self = self;
        this.html = html;
        this.photos = photos;
        this.likes = likes;
        this.portfolio = portfolio;
    }

    @SerializedName("self")
    @Expose
    private String self;

    @SerializedName("html")
    @Expose
    private String html;


    @SerializedName("photos")
    @Expose
    private String photos;

    @SerializedName("likes")
    @Expose
    private String likes;

    @SerializedName("portfolio")
    @Expose
    private String portfolio;

    public String getSelf() {
        return self;
    }

    public String getHtml() {
        return html;
    }

    public String getPhotos() {
        return photos;
    }

    public String getLikes() {
        return likes;
    }

    public String getPortfolio() {
        return portfolio;
    }
}
