package com.george.unsplashapp.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @Expose
    private String username;

    @Expose
    private String first_name;

    @Expose
    private String last_name;

    @Expose
    private String twitter_username;

    @Expose
    private String portfolio_url;

    @Expose
    private String bio;

    @Expose
    private String location;

    @Expose
    private int total_likes;

    @Expose
    private int total_photos;

    @Expose
    private int total_collections;

    @Expose
    private boolean followed_by_user;

    @Expose
    private int downloads;

    @Expose
    private int uploads_remaining;

    @Expose
    private String instagram_username;

    @Expose
    private String email;

    @Expose
    private Links links;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getTwitter_username() {
        return twitter_username;
    }

    public String getPortfolio_url() {
        return portfolio_url;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public int getTotal_likes() {
        return total_likes;
    }

    public int getTotal_photos() {
        return total_photos;
    }

    public int getTotal_collections() {
        return total_collections;
    }

    public boolean isFollowed_by_user() {
        return followed_by_user;
    }

    public int getDownloads() {
        return downloads;
    }

    public int getUploads_remaining() {
        return uploads_remaining;
    }

    public String getInstagram_username() {
        return instagram_username;
    }

    public String getEmail() {
        return email;
    }

    public Links getLinks() {
        return links;
    }
}
