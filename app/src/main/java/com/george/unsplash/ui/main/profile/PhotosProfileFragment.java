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
import com.george.unsplash.network.viewmodel.PhotoViewModelFuture;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotosProfileFragment extends Fragment {

    private PhotosProfileFragmentBinding binding;

    private PhotoViewModel photoViewModel;
    private PhotoViewModelFuture photoViewModelFuture;

    private PhotosAdapter photosAdapter;
    private List<Photo> photos;

    private final Utils utils = new Utils();

    private String username;
    private boolean isUser;
    private int page = 1;

    public static final String TAG = PhotosProfileFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoViewModel = new ViewModelProvider(this)
                .get(PhotoViewModel.class);

        photoViewModelFuture = new ViewModelProvider(this)
                .get(PhotoViewModelFuture.class);

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

        initRecyclerView();

        getUserPhotos();

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getUserPhotos() {
        photoViewModelFuture
                .getUserPhotos(username, page, 15)
                .observe(PhotosProfileFragment.this.requireActivity(), photoList -> {
                    photos.addAll(photoList);
                    photosAdapter.notifyDataSetChanged();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
