package com.george.unsplash.ui.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.databinding.ProfileFragmentBinding;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.network.viewmodel.UserViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;

import java.util.List;

public class UserActivity extends AppCompatActivity {

    private ProfileFragmentBinding binding;

    UserViewModel userViewModel;

    NavController navController;
    Bundle bundle = new Bundle();

    PhotoViewModel photoViewModel;
    PhotosAdapter photosAdapter;
    List<Photo> photoList;

    String username;
    int page = 1;
    public static final String TAG = UserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfileFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);

        binding.topAppBarProfile.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        binding.topAppBarProfile.setNavigationOnClickListener(view -> onBackPressed());

        getUserData();
        initRecyclerView();
        getPhotos();

        binding.likesBtn.setOnClickListener(v -> {
            bundle.putString("username", username);
            navController.navigate(R.id.action_navigation_profile_to_likesFragment, bundle);
        });

        binding.collectionsBtn.setOnClickListener(v -> {
            bundle.putString("username", username);
            bundle.putBoolean("isUser", false);
            navController.navigate(R.id.action_navigation_profile_to_userCollectionsFragment, bundle);
        });
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
        photoList.clear();

        photoViewModel
                .getUserPhotos(username, page)
                .observe(UserActivity.this, photoList -> {
                    this.photoList.removeAll(photoList);
                    photosAdapter.notifyDataSetChanged();
                });

        page++;
    }

    private void getUserData() {
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

}