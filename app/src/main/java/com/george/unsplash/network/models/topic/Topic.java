package com.george.unsplash.network.models.topic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Topic implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("slug")
    @Expose
    private String slug;

    public String getSlug() {
        return slug;
    }

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("total_photos")
    @Expose
    private int total_photos;

    @SerializedName("cover_photo")
    @Expose
    CoverPhoto cover_photo;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalPhotos() {
        return total_photos;
    }

    public CoverPhoto getCoverPhoto() {
        return cover_photo;
    }
}