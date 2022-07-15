package com.george.unsplash.network.models.Statistic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Values {

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("value")
    @Expose
    private Double value;

    public String getDate() {
        return date;
    }

    public Double getValue() {
        return value;
    }
}
