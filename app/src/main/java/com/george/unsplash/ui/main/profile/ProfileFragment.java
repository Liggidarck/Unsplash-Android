package com.george.unsplash.ui.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.databinding.ProfileFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ProfileFragmentBinding binding;

    AppPreferences appPreferences;
    PhotoViewModel photoViewModel;

    PhotosAdapter photosAdapter;
    List<Photo> photoList;

    NavController navController;
    Bundle bundle = new Bundle();

    int page = 1;
    String username;
    public static final String TAG = ProfileFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreferences = new AppPreferences(ProfileFragment.this.requireActivity());
        photoViewModel = new ViewModelProvider(ProfileFragment.this.requireActivity()).get(PhotoViewModel.class);
        photoList = new ArrayList<>();

        navController = Navigation.findNavController(ProfileFragment.this.requireActivity(),
                R.id.nav_host_fragment_activity_main);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getUserData();

        initRecyclerViewPhotos();
        fetchUserPhotos();

        binding.collectionsBtn.setOnClickListener(v -> {
            bundle.putString("username", username);
            bundle.putBoolean("isUser", true);
            navController.navigate(R.id.action_navigation_profile_to_userCollectionsFragment, bundle);
        });

        return root;
    }

    private void initRecyclerViewPhotos() {
        photosAdapter = new PhotosAdapter(ProfileFragment.this.requireActivity(), photoList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ProfileFragment.this.requireActivity(), 2);
        binding.profileRecyclerView.setHasFixedSize(true);
        binding.profileRecyclerView.setLayoutManager(gridLayoutManager);
        binding.profileRecyclerView.setAdapter(photosAdapter);

        binding.profileContent.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1)
                                .getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {
                            fetchUserPhotos();
                        }
                    }
                });

        photosAdapter.setOnItemClickListener((photo, position) -> photoViewModel
                .showFullScreenImage(photo, ProfileFragment.this.requireActivity()));
    }

    @SuppressLint("NotifyDataSetChanged")
    void fetchUserPhotos() {
        photoList.clear();
        photoViewModel
                .getUserPhotos(username, page)
                .observe(ProfileFragment.this.requireActivity(), photos -> {
                    photoList.addAll(photos);
                    photosAdapter.notifyDataSetChanged();
                });

        page++;
    }

    private void getUserData() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
