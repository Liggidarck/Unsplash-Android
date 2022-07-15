package com.george.unsplash.ui.main.profile;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.databinding.ProfileFragmentBinding;
import com.george.unsplash.network.viewmodel.UserViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    private ProfileFragmentBinding binding;

    UserViewModel userViewModel;

    Bundle bundle = new Bundle();

    String username;
    public static final String TAG = UserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfileFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.topAppBarProfile.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        binding.topAppBarProfile.setNavigationOnClickListener(view -> onBackPressed());

        initTabLayout();

        if (savedInstanceState == null) {
            Fragment fragment = new PhotosProfileFragment();
            bundle.putString("username", username);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profileContainer, fragment)
                    .commit();
        }

        userViewModel.getUserData(username).observe(UserActivity.this, user -> {
            String fullName = user.getFirstName() + " " + user.getLastName();
            String bio = user.getBio();
            String email = user.getEmail();
            String profileImage = user.getProfileImage().getLarge();

            if (bio == null)
                bio = "Download free, beautiful high-quality photos curated by " + user.getFirstName();

            binding.nameUser.setText(fullName);
            binding.bioUser.setText(bio);
            binding.emailUser.setText(email);

            Glide.with(UserActivity.this)
                    .load(profileImage)
                    .into(binding.profileImage);
        });
    }

    private void initTabLayout() {
        binding.profileTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (Objects.requireNonNull(tab.getText()).toString()) {
                    case "Photos":
                        Log.d(TAG, "onTabSelected: photo");
                        selectedFragment = new PhotosProfileFragment();
                        bundle.putString("username", username);
                        selectedFragment.setArguments(bundle);

                        break;
                    case "Likes":
                        Log.d(TAG, "onTabSelected: Likes");
                        selectedFragment = new LikesProfileFragment();
                        bundle.putString("username", username);
                        selectedFragment.setArguments(bundle);

                        break;
                    case "Collections":
                        Log.d(TAG, "onTabSelected: Collections");
                        selectedFragment = new CollectionsProfileFragment();
                        bundle.putString("username", username);
                        bundle.putBoolean("isUser", false);
                        selectedFragment.setArguments(bundle);
                        break;
                }

                assert selectedFragment != null;
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.profileContainer, selectedFragment)
                        .commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}