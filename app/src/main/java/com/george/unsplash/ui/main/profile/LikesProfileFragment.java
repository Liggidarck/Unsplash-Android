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
import androidx.recyclerview.widget.GridLayoutManager;

import com.george.unsplash.databinding.LikesProfileFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesProfileFragment extends Fragment {

    private LikesProfileFragmentBinding binding;

    private AppPreferences appPreferences;

    private UnsplashInterface unsplashInterface;
    private List<Photo> photos;
    private PhotosAdapter photosAdapter;

    private String username;

    private final Utils utils = new Utils();

    public static final String TAG = LikesProfileFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        assert args != null;
        username = args.getString("username");

        appPreferences = new AppPreferences(LikesProfileFragment.this.requireActivity());

        photos = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LikesProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initRecyclerView();

        getLikedPhotos();

        return root;
    }

    private void getLikedPhotos() {
        String token = appPreferences.getToken();
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        unsplashInterface.getUserLikePhotos(username, 1, 50).enqueue(new Callback<List<Photo>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                if(response.code() == 200) {
                    assert response.body() != null;
                    photos.addAll(response.body());
                    photosAdapter.notifyDataSetChanged();
                } else {
                    utils.showAlertDialog(LikesProfileFragment.this.requireActivity(), response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    private void initRecyclerView() {
        photosAdapter = new PhotosAdapter(LikesProfileFragment.this.requireActivity(), photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.likesPhotoProfileRecyclerView.setLayoutManager(gridLayoutManager);
        binding.likesPhotoProfileRecyclerView.setHasFixedSize(false);
        binding.likesPhotoProfileRecyclerView.setAdapter(photosAdapter);
    }
}
