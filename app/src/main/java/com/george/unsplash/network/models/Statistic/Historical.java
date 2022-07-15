package com.george.unsplash.network.models.Statistic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Historical {

    @SerializedName("change")
    @Expose
    private int change;

    @SerializedName("values")
    @Expose
    private List<Values> values;

    public int getChange() {
        return change;
    }

    public List<Values> getValues() {
        return values;
    }
}
