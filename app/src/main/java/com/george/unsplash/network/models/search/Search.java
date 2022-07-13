package com.george.unsplash.network.models.search;

import com.george.unsplash.network.models.photo.Photo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Search implements Serializable {

    @SerializedName("total")
    @Expose
    private int total;

    @SerializedName("total_pages")
    @Expose
    private int total_pages;

    @SerializedName("results")
    @Expose
    private List<Photo> results;


    public int getTotal() {
        return total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public List<Photo> getResults() {
        return results;
    }
}
