package com.george.unsplash.ui.adapters;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.bumptech.glide.Glide;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.photo.Urls;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoViewModel extends AndroidViewModel {

    UnsplashInterface unsplashInterface;
    public static final String TAG = "PhotoViewModel";

    public PhotoViewModel(@NonNull Application application) {
        super(application);
    }

    public void getRandomPhoto(String token, ImageView imageView, Context context) {
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        unsplashInterface.getRandomPhoto("landscape").enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(@NonNull Call<Photo> call, @NonNull Response<Photo> response) {
                Photo photo = response.body();
                assert photo != null;
                Urls urls = photo.getUrls();
                String small = urls.getRegular();
                Glide.with(context)
                        .load(small)
                        .into(imageView);
            }

            @Override
            public void onFailure(@NonNull Call<Photo> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

}
