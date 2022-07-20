package com.george.unsplash.ui.main.collections;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.george.unsplash.databinding.ActivityCollectionBinding;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.viewmodel.CollectionViewModel;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    private ActivityCollectionBinding binding;

    private final List<Photo> photos = new ArrayList<>();
    private PhotosAdapter photosAdapter;

    CollectionViewModel collectionViewModel;
    PhotoViewModel photoViewModel;

    private String collectionId;
    private int page = 1;
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    public static final String TAG = CollectionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        collectionViewModel = new ViewModelProvider(this).get(CollectionViewModel.class);
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        Bundle extras = getIntent().getExtras();
        collectionId = extras.getString("collectionId");
        String collectionTitle = extras.getString("collectionTitle");

        binding.toolbarCollection.setTitle(collectionTitle);
        binding.toolbarCollection.setNavigationOnClickListener(v -> onBackPressed());

        initRecycler();
        getNewPhotos();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getNewPhotos() {
        photos.clear();
        collectionViewModel.getPhotosCollection(collectionId, page).observe(CollectionActivity.this, photoList -> {
            photos.addAll(photoList);
            photosAdapter.notifyDataSetChanged();
        });

        page++;
    }

    private void initRecycler() {
        photosAdapter = new PhotosAdapter(this, photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.collectionPhotosRecyclerView.setLayoutManager(gridLayoutManager);
        binding.collectionPhotosRecyclerView.setHasFixedSize(false);
        binding.collectionPhotosRecyclerView.setAdapter(photosAdapter);

        photosAdapter.setOnItemClickListener((photo, position) -> photoViewModel.showFullScreenImage(photo, this));

        binding.collectionPhotosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            getNewPhotos();
                            loading = true;
                        }
                    }
                }

            }
        });
    }
}