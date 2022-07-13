package com.george.unsplash.network.models.photo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("html")
    @Expose
    String html;

    @SerializedName("download")
    @Expose
    String download;

    public String getHtml() {
        return html;
    }

    public String getDownload() {
        return download;
    }
}
