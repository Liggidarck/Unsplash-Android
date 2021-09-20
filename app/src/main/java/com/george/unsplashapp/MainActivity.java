package com.george.unsplashapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainActivity";
    private int page = 1;

    AppBarLayout appBarLayout;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    PhotosAdapter adapter;
    PhotosAdapter.OnPhotoClickedListener photoClickListener;

    UnsplashInterface dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_img_picker);

        appBarLayout = findViewById(R.id.app_bar_layout);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        dataService = UnsplashClient.getUnsplashClient().create(UnsplashInterface.class);

        photoClickListener = (photo, imageView) -> {
            Intent intent = new Intent();
            intent.putExtra("image", photo);
            Log.d(TAG, "image - " + photo);
            setResult(RESULT_OK, intent);
        };

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotosAdapter(new ArrayList<>(), this, photoClickListener);
        recyclerView.setAdapter(adapter);

        loadPhotos();

    }

    private void loadPhotos() {
        progressBar.setVisibility(View.VISIBLE);

        dataService.getPhotos(page,null,"latest")
                .enqueue(new Callback<List<Photo>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {

                        List<Photo> photos = response.body();
                        assert photos != null;
                        Log.d("Photos", "Photos Fetched " + photos.size());
                        //add to adapter
                        page++;
                        adapter.addPhotos(photos);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);

                    }
                });

    }

}