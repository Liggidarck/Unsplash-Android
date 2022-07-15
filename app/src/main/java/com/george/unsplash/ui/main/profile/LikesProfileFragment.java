package com.george.unsplash.ui.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.george.unsplash.databinding.LikesProfileFragmentBinding;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.network.viewmodel.PhotoViewModelFuture;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LikesProfileFragment extends Fragment {

    private LikesProfileFragmentBinding binding;

    private PhotoViewModel photoViewModel;

    private List<Photo> photos;
    private PhotosAdapter photosAdapter;
    private PhotoViewModelFuture photoViewModelFuture;

    private String username;

    private final Utils utils = new Utils();

    public static final String TAG = LikesProfileFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        assert args != null;
        username = args.getString("username");

        photoViewModel = new ViewModelProvider(this)
                .get(PhotoViewModel.class);

        photoViewModelFuture = new ViewModelProvider(this)
                .get(PhotoViewModelFuture.class);

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

    @SuppressLint("NotifyDataSetChanged")
    private void getLikedPhotos() {
        photoViewModelFuture
                .getUserLikePhotos(username, 1, 10)
                .observe(LikesProfileFragment.this.requireActivity(), photoList -> {
                    photos.addAll(photoList);
                    photosAdapter.notifyDataSetChanged();
                });
    }

    private void initRecyclerView() {
        photosAdapter = new PhotosAdapter(LikesProfileFragment.this.requireActivity(), photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.likesPhotoProfileRecyclerView.setLayoutManager(gridLayoutManager);
        binding.likesPhotoProfileRecyclerView.setHasFixedSize(false);
        binding.likesPhotoProfileRecyclerView.setAdapter(photosAdapter);

        photosAdapter.setOnItemClickListener((photo, position) -> photoViewModel
                .showFullScreenImage(photo, LikesProfileFragment.this.requireActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
