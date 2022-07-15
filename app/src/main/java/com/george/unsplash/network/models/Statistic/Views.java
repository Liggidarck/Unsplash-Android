package com.george.unsplash.network.models.Statistic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Views {

    @SerializedName("total")
    @Expose
    private int total;

    @SerializedName("historical")
    @Expose
    private Historical historical;

    public int getTotal() {
        return total;
    }

    public Historical getHistorical() {
        return historical;
    }
}
