package com.george.unsplash.ui.main.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.databinding.ProfileFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.user.common.User;
import com.george.unsplash.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private ProfileFragmentBinding binding;

    private AppPreferences appPreferences;
    private UnsplashInterface unsplashInterface;

    private final Utils utils = new Utils();

    public static final String TAG = UserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfileFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarProfile.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        binding.toolbarProfile.setNavigationOnClickListener(view -> onBackPressed());

        Bundle extras = getIntent().getExtras();

        String username = extras.getString("username");

        appPreferences = new AppPreferences(this);
        String token = appPreferences.getToken();

        //TODO: Добавить отрисовку фотогрфий, лайков и коллекций пользователя
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        unsplashInterface.getUserData(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.code() == 200) {
                    User user = response.body();
                    assert user != null;
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    String bio = user.getBio();
                    String email = user.getEmail();
                    String profileImage = user.getProfileImage().getLarge();

                    if(bio == null)
                        bio = "Download free, beautiful high-quality photos curated by " + user.getFirstName();

                    binding.nameUser.setText(fullName);
                    binding.bioUser.setText(bio);
                    binding.emailUser.setText(email);

                    Glide.with(UserActivity.this)
                            .load(profileImage)
                            .into(binding.profileImage);

                } else {
                    utils.showAlertDialog(UserActivity.this, response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }
}