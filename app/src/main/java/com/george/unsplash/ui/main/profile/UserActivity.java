package com.george.unsplash.ui.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.databinding.ProfileFragmentBinding;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.network.viewmodel.UserViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private ProfileFragmentBinding binding;

    private UserViewModel userViewModel;
    private PhotoViewModel photoViewModel;
    private PhotosAdapter photosAdapter;
    private List<Photo> photoList;

    private String username;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UnsplashApp);
        binding = ProfileFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        photoList = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        binding.topAppBarProfile.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        binding.topAppBarProfile.setNavigationOnClickListener(view -> onBackPressed());

        getUserData();
        initRecyclerView();
        getPhotos();

        binding.collectionsBtn.setVisibility(View.INVISIBLE);
    }

    private void initRecyclerView() {
        photosAdapter = new PhotosAdapter(UserActivity.this, photoList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(UserActivity.this, 2);
        binding.profileRecyclerView.setHasFixedSize(true);
        binding.profileRecyclerView.setLayoutManager(gridLayoutManager);
        binding.profileRecyclerView.setAdapter(photosAdapter);

        binding.profileContent.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1)
                                .getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {
                            getPhotos();
                        }
                    }
                });

        photosAdapter.setOnItemClickListener((photo, position) -> photoViewModel
                .showFullScreenImage(photo, UserActivity.this));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getPhotos() {
        photoViewModel
                .getUserPhotos(username, page)
                .observe(UserActivity.this, photoList -> {
                    this.photoList.addAll(photoList);
                    photosAdapter.notifyDataSetChanged();
                });

        page++;
    }

    private void getUserData() {
        userViewModel.getUserData(username).observe(UserActivity.this, user -> {
            String lastName = user.getLastName();
            if (lastName == null)
                lastName = "";
            String fullName = user.getFirstName() + " " + lastName;
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

}