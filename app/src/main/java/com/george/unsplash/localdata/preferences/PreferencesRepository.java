package com.george.unsplash.localdata.preferences;

import static com.george.unsplash.utils.Keys.SCOPE;
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
import static com.george.unsplash.utils.Keys.USER_SCOPE;
import static com.george.unsplash.utils.Keys.USER_SELF;
import static com.george.unsplash.utils.Keys.USER_TOKEN;
import static com.george.unsplash.utils.Keys.USER_TOKEN_TYPE;
import static com.george.unsplash.utils.Keys.USER_TOTAL_COLLECTIONS;
import static com.george.unsplash.utils.Keys.USER_TOTAL_LIKES;
import static com.george.unsplash.utils.Keys.USER_TOTAL_PHOTOS;
import static com.george.unsplash.utils.Keys.USER_TWITTER_USERNAME;
import static com.george.unsplash.utils.Keys.USER_UPLOADS_REMAINING;
import static com.george.unsplash.utils.Keys.USER_USERNAME;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.george.unsplash.network.models.user.Links;
import com.george.unsplash.network.models.user.Me;

public class PreferencesRepository implements PreferencesBehaviour {

    final SharedPreferences sharedPreferences;
    final SharedPreferences.Editor editor;

    public PreferencesRepository(Application application) {
        sharedPreferences = application.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public void saveToken(String token) {
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    @Override
    public void saveTokenType(String tokenType) {
        editor.putString(USER_TOKEN_TYPE, tokenType);
        editor.apply();
    }

    @Override
    public void saveScope(String scope) {
        editor.putString(USER_SCOPE, scope);
        editor.apply();
    }

    @Override
    public void saveMe(Me me) {
        String id = me.getId();
        String username = me.getUsername();
        String firstName = me.getFirstName();
        String lastName = me.getLastName();
        String twitterUsername = me.getTwitterUsername();
        String portfolioUrl = me.getPortfolioUrl();
        String bio = me.getBio();
        String location = me.getLocation();
        int totalLikes = me.getTotalLikes();
        int totalPhotos = me.getTotalPhotos();
        int totalCollections = me.getTotalCollections();
        boolean followedByUser = me.isFollowedByUser();
        int downloads = me.getDownloads();
        int uploadsRemaining = me.getUploadsRemaining();
        String instagramUsername = me.getInstagramUsername();
        String email = me.getEmail();

        Links links = me.getLinks();
        String self = links.getSelf();
        String html = links.getHtml();
        String photos = links.getPhotos();
        String likes = links.getLikes();
        String portfolio = links.getPortfolio();

        editor.putString(USER_ID, id);
        editor.putString(USER_USERNAME, username);
        editor.putString(USER_FIRST_NAME, firstName);
        editor.putString(USER_LAST_NAME, lastName);
        editor.putString(USER_TWITTER_USERNAME, twitterUsername);
        editor.putString(USER_PORTFOLIO_URL, portfolioUrl);
        editor.putString(USER_BIO, bio);
        editor.putString(USER_LOCATION, location);
        editor.putInt(USER_TOTAL_LIKES, totalLikes);
        editor.putInt(USER_TOTAL_PHOTOS, totalPhotos);
        editor.putInt(USER_TOTAL_COLLECTIONS, totalCollections);
        editor.putBoolean(USER_FOLLOWED_BY_USER, followedByUser);
        editor.putInt(USER_DOWNLOADS, downloads);
        editor.putInt(USER_UPLOADS_REMAINING, uploadsRemaining);
        editor.putString(USER_INSTAGRAM_USERNAME, instagramUsername);
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_SELF, self);
        editor.putString(USER_HTML, html);
        editor.putString(USER_PHOTOS, photos);
        editor.putString(USER_LIKES, likes);
        editor.putString(USER_PORTFOLIO, portfolio);

        editor.apply();
    }

    @Override
    public void saveProfileImage(String image) {
        editor.putString(USER_PROFILE_IMAGE_LARGE, image);
        editor.apply();
    }

    @Override
    public String getToken() {
        return sharedPreferences.getString(USER_TOKEN, "");
    }

    @Override
    public String getTokenType() {
        return sharedPreferences.getString(USER_TOKEN_TYPE, "");
    }

    @Override
    public String getScope() {
        return sharedPreferences.getString(SCOPE, "");
    }

    @Override
    public Me getMe() {
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

    @Override
    public String getProfileImage() {
        return sharedPreferences.getString(USER_PROFILE_IMAGE_LARGE, "");
    }

    @Override
    public void logout() {
        editor.putString(USER_ID, null);
        editor.putString(USER_USERNAME, null);
        editor.putString(USER_FIRST_NAME, null);
        editor.putString(USER_LAST_NAME, null);
        editor.putString(USER_TWITTER_USERNAME, null);
        editor.putString(USER_PORTFOLIO_URL, null);
        editor.putString(USER_BIO, null);
        editor.putString(USER_LOCATION, null);
        editor.putInt(USER_TOTAL_LIKES, 0);
        editor.putInt(USER_TOTAL_PHOTOS, 0);
        editor.putInt(USER_TOTAL_COLLECTIONS, 0);
        editor.putBoolean(USER_FOLLOWED_BY_USER, false);
        editor.putInt(USER_DOWNLOADS, 0);
        editor.putInt(USER_UPLOADS_REMAINING, 0);
        editor.putString(USER_INSTAGRAM_USERNAME,null);
        editor.putString(USER_EMAIL, null);
        editor.putString(USER_SELF, null);
        editor.putString(USER_HTML, null);
        editor.putString(USER_PHOTOS, null);
        editor.putString(USER_LIKES, null);
        editor.putString(USER_PORTFOLIO, null);

        editor.putString(USER_TOKEN, null);
        editor.putString(USER_TOKEN_TYPE, null);
        editor.putString(USER_SCOPE, null);

        editor.apply();

    }
}
