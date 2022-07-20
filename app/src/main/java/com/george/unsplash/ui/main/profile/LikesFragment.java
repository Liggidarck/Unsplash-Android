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
import androidx.recyclerview.widget.RecyclerView;

import com.george.unsplash.databinding.LikesProfileFragmentBinding;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;

import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {

    LikesProfileFragmentBinding binding;

    List<Photo> photos;
    PhotosAdapter photosAdapter;
    PhotoViewModel photoViewModel;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    String username;
    int page = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        assert args != null;
        username = args.getString("username");

        photoViewModel = new ViewModelProvider(LikesFragment.this.requireActivity()).get(PhotoViewModel.class);

        photos = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LikesProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.toolbarLikes.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        initRecyclerView();
        getPhotos();

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    void getPhotos() {
        photos.clear();

        photoViewModel
                .getUserLikePhotos(username, page)
                .observe(LikesFragment.this.requireActivity(), photos -> {
                    this.photos.addAll(photos);
                    photosAdapter.notifyDataSetChanged();
                });

        page++;
    }

    void initRecyclerView() {
        photosAdapter = new PhotosAdapter(LikesFragment.this.requireActivity(), photos);
        GridLayoutManager layoutManager = new GridLayoutManager(LikesFragment.this.requireActivity(), 2);
        binding.likesPhotoProfileRecyclerView.setLayoutManager(layoutManager);
        binding.likesPhotoProfileRecyclerView.setHasFixedSize(true);
        binding.likesPhotoProfileRecyclerView.setAdapter(photosAdapter);

        binding.likesPhotoProfileRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            getPhotos();
                            loading = true;
                        }
                    }
                }
            }
        });

        photosAdapter.setOnItemClickListener((photo, position) -> photoViewModel
                .showFullScreenImage(photo, LikesFragment.this.requireActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
