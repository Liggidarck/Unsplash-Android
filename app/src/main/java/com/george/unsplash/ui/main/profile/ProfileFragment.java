package com.george.unsplash.ui.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.databinding.ProfileFragmentBinding;
import com.george.unsplash.localdata.preferences.app.AppPreferenceViewModel;
import com.george.unsplash.localdata.preferences.user.UserDataViewModel;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.models.user.common.ProfileImage;
import com.george.unsplash.network.models.user.common.User;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.network.viewmodel.UserViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.ui.main.home.HomeContentFragment;
import com.george.unsplash.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ProfileFragmentBinding binding;

    private PhotoViewModel photoViewModel;
    private UserDataViewModel userDataViewModel;
    private AppPreferenceViewModel appPreferenceViewModel;
    private UserViewModel userViewModel;

    private PhotosAdapter photosAdapter;
    private List<Photo> photoList;

    private NavController navController;
    private final Bundle bundle = new Bundle();
    private final DialogUtils dialogUtils = new DialogUtils();

    private int page = 1;
    private String username;
    public static final String TAG = ProfileFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        photoList = new ArrayList<>();
        navController = NavHostFragment.findNavController(this);

        initViewModels();
        getUserData();
        initRecyclerViewPhotos();
        fetchUserPhotos();

        binding.collectionsBtn.setOnClickListener(v -> {
            bundle.putString("username", username);
            bundle.putBoolean("isUser", true);
            navController.navigate(R.id.action_navigation_profile_to_userCollectionsFragment, bundle);
        });

        binding.btnNextPage.setOnClickListener(v -> goToNextPage());

        binding.btnPreviousPage.setOnClickListener(v -> goToPreviousPage());

        binding.swipeRefreshProfile.setOnRefreshListener(() -> {
            binding.swipeRefreshProfile.setRefreshing(true);
            updateUserInfo();
        });

        return root;
    }

    private void initViewModels() {
        userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        appPreferenceViewModel = new ViewModelProvider(this).get(AppPreferenceViewModel.class);
    }

    private void goToPreviousPage() {
        if (page == 1) {
            Toast.makeText(ProfileFragment.this.requireActivity(), "This is first page", Toast.LENGTH_SHORT).show();
            return;
        }

        page--;
        scrollToTop();
        fetchUserPhotos();
    }

    private void goToNextPage() {
        page++;
        scrollToTop();
        fetchUserPhotos();
    }

    private void scrollToTop() {
        binding.profileContent.fullScroll(View.FOCUS_DOWN);
        binding.profileContent.fullScroll(View.FOCUS_UP);
    }

    private void updateUserInfo() {
        userViewModel.getMeData(userDataViewModel.getToken())
                .observe(ProfileFragment.this.requireActivity(), me -> {
                    if(me == null) {
                        dialogUtils.showAlertDialog(ProfileFragment.this.requireActivity());
                        binding.swipeRefreshProfile.setRefreshing(false);
                        return;
                    }
                    userDataViewModel.clearMe();

                    userDataViewModel.saveMe(me);
                    updateProfileImage();
                });
    }

    private void updateProfileImage() {
        userViewModel.getUserData(username)
                .observe(ProfileFragment.this.requireActivity(), user -> {
                    ProfileImage profileImage = user.getProfileImage();
                    String large = profileImage.getLarge();

                    userDataViewModel.saveProfileImage(large);

                    binding.swipeRefreshProfile.setRefreshing(false);
                    requireActivity().recreate();
                });
    }

    private void initRecyclerViewPhotos() {
        photosAdapter = new PhotosAdapter(ProfileFragment.this.requireActivity(), photoList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ProfileFragment.this.requireActivity(),
                appPreferenceViewModel.getGridPhotos());

        binding.profileRecyclerView.setHasFixedSize(true);
        binding.profileRecyclerView.setLayoutManager(gridLayoutManager);
        binding.profileRecyclerView.setAdapter(photosAdapter);

        photosAdapter.setOnItemClickListener((photo, position) -> photoViewModel
                .showFullScreenImage(photo, ProfileFragment.this.requireActivity()));
    }

    @SuppressLint("NotifyDataSetChanged")
    void fetchUserPhotos() {
        photoViewModel
                .getUserPhotos(username, page, appPreferenceViewModel.getPerPage())
                .observe(ProfileFragment.this.requireActivity(), photos -> {
                    binding.swipeRefreshProfile.setRefreshing(true);

                    if(photos == null) {
                        dialogUtils.showAlertDialog(ProfileFragment.this.requireActivity());
                        binding.relativeLayoutNavigationProfile.setVisibility(View.INVISIBLE);
                        binding.swipeRefreshProfile.setRefreshing(false);
                        return;
                    }

                    if (photos.size() == 0) {
                        Toast.makeText(
                                ProfileFragment.this.requireActivity(),
                                "This is final page",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    photoList.clear();
                    photoList.addAll(photos);
                    photosAdapter.notifyDataSetChanged();
                    binding.swipeRefreshProfile.setRefreshing(false);
                });
    }

    private void getUserData() {
        Me me = userDataViewModel.getMe();

        String fullName = me.getFirstName() + " " + me.getLastName();
        String bio = me.getBio();
        String profileImage = userDataViewModel.getProfileImage();
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
