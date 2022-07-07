package com.george.unsplash.network.models.user.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileImage {

    @SerializedName("small")
    @Expose
    String small;

    @SerializedName("medium")
    @Expose
    String medium;

    @SerializedName("large")
    @Expose
    String large;

    public String getSmall() {
        return small;
    }

    public String getMedium() {
        return medium;
    }

    public String getLarge() {
        return large;
    }
}
