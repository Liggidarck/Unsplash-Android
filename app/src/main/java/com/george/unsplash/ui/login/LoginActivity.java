package com.george.unsplash.ui.login;

import static com.george.unsplash.utils.Keys.RESPONSE_URL;
import static com.george.unsplash.utils.Keys.SECRET_KEY;
import static com.george.unsplash.utils.Keys.UNSPLASH_ACCESS_KEY;
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
import static com.george.unsplash.utils.Keys.USER_PROFILE_IMAGE_MEDIUM;
import static com.george.unsplash.utils.Keys.USER_PROFILE_IMAGE_SMALL;
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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.george.unsplash.databinding.ActivityLoginBinding;
import com.george.unsplash.network.api.UnsplashBaseClient;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.user.Links;
import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.models.user.Token;
import com.george.unsplash.network.models.user.common.ProfileImage;
import com.george.unsplash.network.models.user.common.User;
import com.george.unsplash.ui.main.MainActivity;
import com.george.unsplash.utils.Keys;
import com.george.unsplash.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    public static final String TAG = LoginActivity.class.getSimpleName();

    final private String url = Keys.BASE_URL_AUTH + "/authorize?"
            + "client_id=" + UNSPLASH_ACCESS_KEY
            + "&response_type=code&scope=" + Keys.SCOPE
            + "&redirect_uri=" + RESPONSE_URL;

    UnsplashInterface unsplashInterface;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        unsplashInterface = UnsplashBaseClient.getBaseUnsplashClient().create(UnsplashInterface.class);

        binding.codeBtn.setOnClickListener(v -> {
            Uri uri = Uri.parse(url);
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build();
            customTabsIntent.launchUrl(LoginActivity.this, uri);
        });

        binding.loginBtn.setOnClickListener(v -> {
            String code = Objects.requireNonNull(binding.userKeyEditText.getEditText()).getText().toString();
            binding.progressBarLogin.setVisibility(View.VISIBLE);

            if(code.isEmpty()) {
                binding.userKeyEditText.setError("Это поле не может быть пустым");
                binding.progressBarLogin.setVisibility(View.INVISIBLE);
                return;
            }

            if(!utils.isOnline(LoginActivity.this)) {
                Snackbar.make(binding.loginCoordinator, "Проверьте подключение к интернету", Snackbar.LENGTH_SHORT).show();
                binding.progressBarLogin.setVisibility(View.INVISIBLE);
                return;
            }

            getAccessToken(code);
        });
    }

    private void getAccessToken(String code) {
        unsplashInterface.getToken(UNSPLASH_ACCESS_KEY, SECRET_KEY, RESPONSE_URL, code, "authorization_code")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                        if(response.code() == 200) {
                            Token token = response.body();
                            assert token != null;
                            String accessToken = token.getAccess_token();
                            String token_type = token.getToken_type();
                            String scope = token.getScope();
                            Log.i(TAG, "onResponse: token: " + accessToken);

                            editor.putString(USER_TOKEN, accessToken);
                            editor.putString(USER_TOKEN_TYPE, token_type);
                            editor.putString(USER_SCOPE, scope);
                            editor.putString(USER_SCOPE, scope);

                            getMeData(accessToken);
                        } else {
                            utils.showAlertDialog(LoginActivity.this, response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        binding.progressBarLogin.setVisibility(View.INVISIBLE);
                        Snackbar.make(binding.loginCoordinator, "Ошибка! " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }


    void getMeData(String token) {
        UnsplashInterface unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        unsplashInterface.getMeData().enqueue(new Callback<Me>() {
            @Override
            public void onResponse(@NonNull Call<Me> call, @NonNull Response<Me> response) {
                if (response.code() == 200) {
                    Me me = response.body();
                    assert me != null;
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

                    Log.d(TAG, "onResponse: " + email);
                    getPublicData(token, username);

                } else {
                    Utils utils = new Utils();
                    utils.showAlertDialog(LoginActivity.this, response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Me> call, @NonNull Throwable t) {

            }
        });
    }

    private void getPublicData(String token, String username) {
        UnsplashInterface unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        Log.d(TAG, "getPublicData: " + unsplashInterface.getUserData(username).request().url());
        unsplashInterface.getUserData(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.code() == 200) {
                    User user = response.body();
                    assert user != null;
                    ProfileImage profileImage = user.getProfileImage();
                    String small = profileImage.getSmall();
                    String medium = profileImage.getMedium();
                    String large = profileImage.getLarge();

                    editor.putString(USER_PROFILE_IMAGE_SMALL, small);
                    editor.putString(USER_PROFILE_IMAGE_MEDIUM, medium);
                    editor.putString(USER_PROFILE_IMAGE_LARGE, large);

                    editor.apply();

                    binding.progressBarLogin.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    utils.showAlertDialog(LoginActivity.this, response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

            }
        });
    }
}