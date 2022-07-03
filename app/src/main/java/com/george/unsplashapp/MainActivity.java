package com.george.unsplashapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.george.unsplashapp.models.Exif;
import com.george.unsplashapp.models.Photo;
import com.george.unsplashapp.api.UnsplashClient;
import com.george.unsplashapp.api.UnsplashInterface;
import com.george.unsplashapp.models.Urls;
import com.george.unsplashapp.databinding.ActivityMainBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    PhotosAdapter adapter = new PhotosAdapter();
    UnsplashInterface dataService;

    private static final String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataService = UnsplashClient.getUnsplashClient().create(UnsplashInterface.class);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recyclerView.setLayoutManager(layoutManager);

        adapter.setOnItemClickListener((photo, position) -> {
            Log.d(TAG, "photo id: " + photo.getId());
            Log.d(TAG, "photo downloads: " + photo.getDownloads());
            Log.d(TAG, "photo likes: " + photo.getLikes());
            Log.d(TAG, "photo description: " + photo.getDescription());

            Urls urls = photo.getUrls();
            Log.d(TAG, "url: " + urls.getRegular());

            Exif exif = photo.getExif();
            Log.d(TAG, "exif: " + exif);
        });

        loadPhotos();

    }

    private void loadPhotos() {
        binding.progressBar.setVisibility(View.VISIBLE);

        dataService.getPhotos(1,null,"popular")
                .enqueue(new Callback<List<Photo>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {

                        List<Photo> photos = response.body();
                        assert photos != null;
                        Log.d("Photos", "Photos Fetched " + photos.size());

                        adapter.addPhotos(photos);
                        binding.recyclerView.setAdapter(adapter);
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });

    }

}