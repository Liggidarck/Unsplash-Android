package com.george.unsplash.localdata;

import static com.george.unsplash.utils.Keys.USER_BIO;
import static com.george.unsplash.utils.Keys.USER_DOWNLOADS;
import static com.george.unsplash.utils.Keys.USER_EMAIL;
import static com.george.unsplash.utils.Keys.USER_FIRST_NAME;
import static com.george.unsplash.utils.Keys.USER_FOLLOWED_BY_USER;
import static com.george.unsplash.utils.Keys.USER_HTML;
import static com.george.unsplash.utils.Keys.USER_ID;
import static com.george.unsplash.utils.Keys.USER_INSTAGRAM_USERNAME;
import static com.george.unsplash.utils.Keys.USER_LAST_NAME;
import static com.george.unsplash.utils.Keys.USER_LIKES;
import static com.george.unsplash.utils.Keys.USER_LOCATION;
import static com.george.unsplash.utils.Keys.USER_PHOTOS;
import static com.george.unsplash.utils.Keys.USER_PORTFOLIO;
import static com.george.unsplash.utils.Keys.USER_PORTFOLIO_URL;
import static com.george.unsplash.utils.Keys.USER_PREFERENCES;
import static com.george.unsplash.utils.Keys.USER_PROFILE_IMAGE_LARGE;
import static com.george.unsplash.utils.Keys.USER_SELF;
import static com.george.unsplash.utils.Keys.USER_TOKEN;
import static com.george.unsplash.utils.Keys.USER_TOTAL_COLLECTIONS;
import static com.george.unsplash.utils.Keys.USER_TOTAL_LIKES;
import static com.george.unsplash.utils.Keys.USER_TOTAL_PHOTOS;
import static com.george.unsplash.utils.Keys.USER_TWITTER_USERNAME;
import static com.george.unsplash.utils.Keys.USER_UPLOADS_REMAINING;
import static com.george.unsplash.utils.Keys.USER_USERNAME;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.george.unsplash.network.models.user.Links;
import com.george.unsplash.network.models.user.Me;

public class AppPreferences {

    final SharedPreferences sharedPreferences;
    final SharedPreferences.Editor editor;

    public AppPreferences(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getUserLargeImage() {
        return sharedPreferences.getString(USER_PROFILE_IMAGE_LARGE, "");
    }

    public Me getUserData() {
        String userId = sharedPreferences.getString(USER_ID, "");
        String username = sharedPreferences.getString(USER_USERNAME, "");
        String firstname = sharedPreferences.getString(USER_FIRST_NAME, "");
        String lastname = sharedPreferences.getString(USER_LAST_NAME, "");
        String twitterUsername = sharedPreferences.getString(USER_TWITTER_USERNAME, "");
        String portfolioUrl = sharedPreferences.getString(USER_PORTFOLIO_URL, "");
        String userBio = sharedPreferences.getString(USER_BIO, "");
        String userLocation = sharedPreferences.getString(USER_LOCATION, "");
        int totalLikes = sharedPreferences.getInt(USER_TOTAL_LIKES, 0);
        int totalPhotos = sharedPreferences.getInt(USER_TOTAL_PHOTOS, 0);
        int totalCollections = sharedPreferences.getInt(USER_TOTAL_COLLECTIONS, 0);
        boolean userFollowedByUser = sharedPreferences.getBoolean(USER_FOLLOWED_BY_USER, false);
        int downloads = sharedPreferences.getInt(USER_DOWNLOADS, 0);
        int uploads = sharedPreferences.getInt(USER_UPLOADS_REMAINING, 0);
        String instagramUsername = sharedPreferences.getString(USER_INSTAGRAM_USERNAME, "");
        String email = sharedPreferences.getString(USER_EMAIL, "");

        String self = sharedPreferences.getString(USER_SELF, "");
        String html = sharedPreferences.getString(USER_HTML, "");
        String userPhotos = sharedPreferences.getString(USER_PHOTOS, "");
        String userLikes = sharedPreferences.getString(USER_LIKES, "");
        String userPortfolio = sharedPreferences.getString(USER_PORTFOLIO, "");

        Links links = new Links(self, html, userPhotos, userLikes, userPortfolio);

        return new Me(userId, username, firstname, lastname, twitterUsername, portfolioUrl, userBio,
                userLocation, totalLikes, totalPhotos, totalCollections, userFollowedByUser,
                downloads, uploads, instagramUsername, email, links);
    }

    public String getToken() {
        return sharedPreferences.getString(USER_TOKEN, "");
    }

}
