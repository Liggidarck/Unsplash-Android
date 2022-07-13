package com.george.unsplash.network.models.collection;

import com.george.unsplash.network.models.topic.CoverPhoto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CollectionPhotos implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @Expose
    private String title;

    @Expose
    private String description;

    @Expose
    private int total_photos;

    @SerializedName("private")
    @Expose
    private boolean private_collection;

    @Expose
    private CoverPhoto cover_photo;

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

    public boolean isPrivateCollection() {
        return private_collection;
    }

    public CoverPhoto getCoverPhoto() {
        return cover_photo;
    }
}
