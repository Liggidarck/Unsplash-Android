package com.george.unsplash.ui.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.george.unsplash.databinding.PhotosProfileFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.ui.main.photos.PhotoViewModel;
import com.george.unsplash.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotosProfileFragment extends Fragment {

    private PhotosProfileFragmentBinding binding;

    private PhotoViewModel photoViewModel;

    private PhotosAdapter photosAdapter;
    private UnsplashInterface unsplashInterface;
    private List<Photo> photos;

    private final Utils utils = new Utils();

    private String username;
    private int page = 1;

    public static final String TAG = PhotosProfileFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoViewModel = new ViewModelProvider(this)
                .get(PhotoViewModel.class);

        AppPreferences appPreferences = new AppPreferences(PhotosProfileFragment.this.requireActivity());
        String token = appPreferences.getToken();

        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);

        photos = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PhotosProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        assert args != null;

        username = args.getString("username");
        Log.d(TAG, "onCreateView: " + username);

        initRecyclerView();

        getUserPhotos();

        return root;
    }

    public void getUserPhotos() {
        unsplashInterface.getUserPhotos(username, page, 50).enqueue(new Callback<List<Photo>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    photos.addAll(response.body());
                    photosAdapter.notifyDataSetChanged();
                } else {
                    utils.showAlertDialog(PhotosProfileFragment.this.requireActivity(), response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });

        page += 1;
    }

    private void initRecyclerView() {
        photosAdapter = new PhotosAdapter(PhotosProfileFragment.this.requireActivity(), photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.photosProfileRecyclerView.setLayoutManager(gridLayoutManager);
        binding.photosProfileRecyclerView.setHasFixedSize(true);
        binding.photosProfileRecyclerView.setAdapter(photosAdapter);

        photosAdapter.setOnItemClickListener((photo, position) -> photoViewModel
                .showFullScreenImage(photo, PhotosProfileFragment.this.requireActivity()));
    }
}
