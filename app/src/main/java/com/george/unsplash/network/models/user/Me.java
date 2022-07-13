package com.george.unsplash.network.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Me {

    public Me(String id, String username, String first_name, String last_name, String twitter_username,
              String portfolio_url, String bio, String location, int total_likes, int total_photos,
              int total_collections, boolean followed_by_user, int downloads, int uploads_remaining,
              String instagram_username, String email, Links links) {
        this.id = id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.twitter_username = twitter_username;
        this.portfolio_url = portfolio_url;
        this.bio = bio;
        this.location = location;
        this.total_likes = total_likes;
        this.total_photos = total_photos;
        this.total_collections = total_collections;
        this.followed_by_user = followed_by_user;
        this.downloads = downloads;
        this.uploads_remaining = uploads_remaining;
        this.instagram_username = instagram_username;
        this.email = email;
        this.links = links;
    }

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

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getTwitterUsername() {
        return twitter_username;
    }

    public String getPortfolioUrl() {
        return portfolio_url;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public int getTotalLikes() {
        return total_likes;
    }

    public int getTotalPhotos() {
        return total_photos;
    }

    public int getTotalCollections() {
        return total_collections;
    }

    public boolean isFollowedByUser() {
        return followed_by_user;
    }

    public int getDownloads() {
        return downloads;
    }

    public int getUploadsRemaining() {
        return uploads_remaining;
    }

    public String getInstagramUsername() {
        return instagram_username;
    }

    public String getEmail() {
        return email;
    }

    public Links getLinks() {
        return links;
    }
}
