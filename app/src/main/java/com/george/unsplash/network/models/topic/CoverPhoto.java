package com.george.unsplash.network.models.topic;

import com.george.unsplash.network.models.photo.Urls;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoverPhoto {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("urls")
    @Expose
    private Urls urls;

    public String getId() {
        return id;
    }

    public Urls getUrls() {
        return urls;
    }
}
