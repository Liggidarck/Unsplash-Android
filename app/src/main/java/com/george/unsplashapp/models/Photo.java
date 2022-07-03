package com.george.unsplashapp.models;

import com.george.unsplashapp.models.Urls;
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
    private String description;

    @Expose
    private Urls urls;

    @SerializedName("exif")
    @Expose
    private Exif exif;

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
}
