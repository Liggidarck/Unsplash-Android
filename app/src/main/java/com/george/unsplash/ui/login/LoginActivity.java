package com.george.unsplash.ui.login;

import static com.george.unsplash.utils.Keys.RESPONSE_URL;
import static com.george.unsplash.utils.Keys.SECRET_KEY;
import static com.george.unsplash.utils.Keys.UNSPLASH_ACCESS_KEY;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProvider;

import com.george.unsplash.R;
import com.george.unsplash.databinding.ActivityLoginBinding;
import com.george.unsplash.localdata.preferences.PreferencesViewModel;
import com.george.unsplash.network.api.UnsplashBaseClient;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.models.user.Token;
import com.george.unsplash.network.models.user.common.ProfileImage;
import com.george.unsplash.network.models.user.common.User;
import com.george.unsplash.ui.main.MainActivity;
import com.george.unsplash.utils.DialogUtils;
import com.george.unsplash.utils.Keys;
import com.george.unsplash.utils.NetworkUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    public static final String TAG = LoginActivity.class.getSimpleName();

    final private String url = Keys.BASE_URL_AUTH + "/authorize?"
            + "client_id=" + UNSPLASH_ACCESS_KEY
            + "&response_type=code&scope=" + Keys.SCOPE
            + "&redirect_uri=" + RESPONSE_URL;

    private UnsplashInterface unsplashInterface;
    private PreferencesViewModel preferencesViewModel;

    private final NetworkUtils networkUtils = new NetworkUtils();
    private final DialogUtils dialogUtils = new DialogUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UnsplashApp);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);

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

            if (code.isEmpty()) {
                binding.userKeyEditText.setError("Это поле не может быть пустым");
                binding.progressBarLogin.setVisibility(View.INVISIBLE);
                return;
            }

            if (!networkUtils.isOnline(LoginActivity.this)) {
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
                        if (response.code() == 200) {
                            Token token = response.body();
                            assert token != null;
                            String accessToken = token.getAccess_token();
                            String tokenType = token.getToken_type();
                            String scope = token.getScope();
                            Log.i(TAG, "onResponse: token: " + accessToken);

                            preferencesViewModel.saveToken(accessToken);
                            preferencesViewModel.saveTokenType(tokenType);
                            preferencesViewModel.saveScope(scope);

                            getMeData(accessToken);
                        } else {
                            dialogUtils.showAlertDialog(LoginActivity.this);
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
                    String username = me.getUsername();
                    preferencesViewModel.saveMe(me);
                    getPublicData(token, username);
                } else {
                    dialogUtils.showAlertDialog(LoginActivity.this);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Me> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void getPublicData(String token, String username) {
        UnsplashInterface unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        Log.d(TAG, "getPublicData: " + unsplashInterface.getUserData(username).request().url());
        unsplashInterface.getUserData(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    User user = response.body();
                    assert user != null;
                    ProfileImage profileImage = user.getProfileImage();
                    String large = profileImage.getLarge();

                    preferencesViewModel.saveProfileImage(large);

                    binding.progressBarLogin.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    dialogUtils.showAlertDialog(LoginActivity.this);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}