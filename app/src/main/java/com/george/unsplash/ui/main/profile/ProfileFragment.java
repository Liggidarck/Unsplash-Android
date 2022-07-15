package com.george.unsplash.ui.main.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.databinding.ProfileFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.viewmodel.CollectionViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ProfileFragmentBinding binding;
    AppPreferences appPreferences;
    CollectionViewModel collectionViewModel;

    String username;
    Bundle bundle = new Bundle();

    public static final String TAG = ProfileFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        collectionViewModel = new ViewModelProvider(this).get(CollectionViewModel.class);

        getUserData();

        Fragment fragment = new PhotosProfileFragment();
        bundle.putString("username", username);
        fragment.setArguments(bundle);
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileContainer, fragment)
                .commit();

        tabsBehaviour();

        return root;
    }

    private void getUserData() {
        appPreferences = new AppPreferences(ProfileFragment.this.requireActivity());
        Me me = appPreferences.getUserData();

        String fullName = me.getFirstName() + " " + me.getLastName();
        String bio = me.getBio();
        String profileImage = appPreferences.getUserLargeImage();
        username = me.getUsername();

        if (bio.equals(""))
            bio = "Download free, beautiful high-quality photos curated by " + me.getFirstName();

        binding.nameUser.setText(fullName);
        binding.bioUser.setText(bio);
        binding.emailUser.setText(me.getEmail());
        Glide.with(this)
                .load(profileImage)
                .into(binding.profileImage);
    }

    private void tabsBehaviour() {
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
                        bundle.putBoolean("isUser", true);
                        bundle.putString("username", username);
                        selectedFragment.setArguments(bundle);
                        break;
                }

                assert selectedFragment != null;
                requireActivity()
                        .getSupportFragmentManager()
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
