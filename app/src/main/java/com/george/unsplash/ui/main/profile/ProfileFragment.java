package com.george.unsplash.ui.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import com.george.unsplash.localdata.preferences.app.AppPreferenceViewModel;
import com.george.unsplash.localdata.preferences.user.UserDataViewModel;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.models.user.common.ProfileImage;
import com.george.unsplash.network.models.user.common.User;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;
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

    private PhotosAdapter photosAdapter;
    private List<Photo> photoList;

    private final DialogUtils dialogUtils = new DialogUtils();
    private NavController navController;
    private final Bundle bundle = new Bundle();

    private int page = 1;
    private String username;
    public static final String TAG = ProfileFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDataViewModel = new ViewModelProvider(ProfileFragment.this.requireActivity())
                .get(UserDataViewModel.class);

        photoViewModel = new ViewModelProvider(ProfileFragment.this.requireActivity())
                .get(PhotoViewModel.class);

        appPreferenceViewModel = new ViewModelProvider(ProfileFragment.this.requireActivity())
                .get(AppPreferenceViewModel.class);

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

        binding.swipeRefreshProfile.setOnRefreshListener(() -> {
            userDataViewModel.clearMe();

            UnsplashInterface unsplashInterface = UnsplashTokenClient
                    .getUnsplashTokenClient(userDataViewModel.getToken())
                    .create(UnsplashInterface.class);

            unsplashInterface.getMeData().enqueue(new Callback<Me>() {
                @Override
                public void onResponse(@NonNull Call<Me> call, @NonNull Response<Me> response) {
                    if (response.code() == 200) {
                        Me me = response.body();
                        assert me != null;
                        userDataViewModel.saveMe(me);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Me> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });

            unsplashInterface.getUserData(username).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.code() == 200) {
                        User user = response.body();
                        assert user != null;
                        ProfileImage profileImage = user.getProfileImage();
                        String large = profileImage.getLarge();

                        userDataViewModel.saveProfileImage(large);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });

            binding.swipeRefreshProfile.setRefreshing(false);
        });

        return root;
    }

    private void initRecyclerViewPhotos() {
        photosAdapter = new PhotosAdapter(ProfileFragment.this.requireActivity(), photoList);
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(ProfileFragment.this.requireActivity(),
                        appPreferenceViewModel.getGridPhotos());
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
        photoViewModel
                .getUserPhotos(username, page, appPreferenceViewModel.getPerPage())
                .observe(ProfileFragment.this.requireActivity(), photos -> {
                    if (photos == null) {
                        dialogUtils.showAlertDialog(ProfileFragment.this.requireActivity());
                        return;
                    }
                    photoList.addAll(photos);
                    photosAdapter.notifyDataSetChanged();
                });
        page++;
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
