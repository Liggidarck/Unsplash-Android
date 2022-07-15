package com.george.unsplash.network.models.Statistic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Statistic implements Serializable {

    @SerializedName("downloads")
    @Expose
    private Downloads downloads;

    @SerializedName("views")
    @Expose
    private Views views;

    public Downloads getDownloads() {
        return downloads;
    }

    public Views getViews() {
        return views;
    }
}
