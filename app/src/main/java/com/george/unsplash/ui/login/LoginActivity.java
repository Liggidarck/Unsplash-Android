package com.george.unsplash.ui.login;

import static com.george.unsplash.utils.Keys.RESPONSE_URL;
import static com.george.unsplash.utils.Keys.SECRET_KEY;
import static com.george.unsplash.utils.Keys.UNSPLASH_ACCESS_KEY;
import static com.george.unsplash.utils.Keys.USER_PREFERENCES;
import static com.george.unsplash.utils.Keys.USER_SCOPE;
import static com.george.unsplash.utils.Keys.USER_TOKEN;
import static com.george.unsplash.utils.Keys.USER_TOKEN_TYPE;

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
import com.george.unsplash.network.models.user.Token;
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

    public static final String TAG = "LoginActivity";

    final private String url = Keys.BASE_URL_AUTH + "/authorize?"
            + "client_id=" + UNSPLASH_ACCESS_KEY
            + "&response_type=code&scope=" + Keys.SCOPE
            + "&redirect_uri=" + RESPONSE_URL;

    UnsplashInterface unsplashInterface;
    SharedPreferences sharedPreferences;

    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
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
        SharedPreferences.Editor editor = sharedPreferences.edit();

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
                            editor.apply();

                            binding.progressBarLogin.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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


}