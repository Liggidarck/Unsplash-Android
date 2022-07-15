package com.george.unsplash.ui.main.collections;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.george.unsplash.databinding.ActivityCollectionBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionActivity extends AppCompatActivity {

    private ActivityCollectionBinding binding;

    private PhotoViewModel photoViewModel;
    private final Utils utils = new Utils();

    private final List<Photo> photos = new ArrayList<>();
    private PhotosAdapter photosAdapter;
    private UnsplashInterface unsplashInterface;

    private String collectionId;
    private int page = 1;

    public static final String TAG = CollectionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppPreferences appPreferences = new AppPreferences(this);
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        String token = appPreferences.getToken();
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);

        Bundle extras = getIntent().getExtras();
        collectionId = extras.getString("collectionId");
        String collectionTitle = extras.getString("collectionTitle");

        binding.toolbarCollection.setTitle(collectionTitle);

        initRecycler();
        getNewPhotos();
    }

    private void getNewPhotos() {
        unsplashInterface.getCollectionPhotos(collectionId, page).enqueue(new Callback<List<Photo>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                if(response.code() == 200) {
                    assert response.body() != null;
                    photos.addAll(response.body());
                    photosAdapter.notifyDataSetChanged();
                } else {
                    utils.showAlertDialog(CollectionActivity.this, response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
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
    }
}