package com.george.unsplash.ui.main.profile;

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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.george.unsplash.databinding.ProfileFragmentBinding;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.user.Links;
import com.george.unsplash.network.models.user.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    ProfileFragmentBinding binding;

    UnsplashInterface unsplashInterface;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static final String TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = requireActivity().getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String token = sharedPreferences.getString(USER_TOKEN, "");

        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);

        return root;
    }

    void getUserData() {
        unsplashInterface.getUsersData().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User user = response.body();
                assert user != null;
                String id = user.getId();
                String username = user.getUsername();
                String firstName = user.getFirst_name();
                String lastName = user.getLast_name();
                String twitterUsername = user.getTwitter_username();
                String portfolioUrl = user.getPortfolio_url();
                String bio = user.getBio();
                String location = user.getLocation();
                int totalLikes = user.getTotal_likes();
                int totalPhotos = user.getTotal_photos();
                int totalCollections = user.getTotal_collections();
                boolean followedByUser = user.isFollowed_by_user();
                int downloads = user.getDownloads();
                int uploadsRemaining = user.getUploads_remaining();
                String instagramUsername = user.getInstagram_username();
                String email = user.getEmail();

                Links links = user.getLinks();
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
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
