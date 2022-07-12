package com.george.unsplash.network.models.photo;

import com.george.unsplash.network.models.user.common.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Photo implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @Expose
    private int downloads;

    @Expose
    private int likes;

    @Expose
    private int width;

    @Expose
    private int height;

    @Expose
    private String description;

    @Expose
    private Urls urls;

    @Expose
    private Exif exif;

    @Expose
    private User user;

    public String getId() {
        return id;
    }

    public int getDownloads() {
        return downloads;
    }

    public int getLikes() {
        return likes;
    }

    public String getDescription() {
        return description;
    }

    public Urls getUrls() {
        return urls;
    }

    public Exif getExif() {
        return exif;
    }

    public User getUser() {
        return user;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
