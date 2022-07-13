package com.george.unsplash.ui.main.photos;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.photo.Urls;
import com.george.unsplash.ui.main.home.HomeContentFragment;
import com.george.unsplash.ui.main.photos.FullScreenPhotoActivity;
import com.george.unsplash.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoViewModel extends AndroidViewModel {

    UnsplashInterface unsplashInterface;
    Utils utils = new Utils();

    public static final String TAG = "PhotoViewModel";

    public PhotoViewModel(@NonNull Application application) {
        super(application);
    }

    void init(String token) {
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
    }

    public void getRandomPhoto(ImageView imageView, Context context) {
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

    public void likePhotoBehavior(String token, boolean likedByUser, String photoId, int likes,
                                  Context context, ImageView imageLikes, TextView likesTextView) {
        init(token);
        if (!likedByUser) {
            unsplashInterface.likePhoto(photoId).enqueue(new Callback<Photo>() {
                @Override
                public void onResponse(@NonNull Call<Photo> call, @NonNull Response<Photo> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    if (response.code() == 201) {
                        imageLikes.setImageResource(R.drawable.ic_baseline_favorite_24);
                        int likesPhoto = likes + 1;
                        String likesText = "Likes: " + likesPhoto;
                        likesTextView.setText(likesText);
                    } else {
                        utils.showAlertDialog(context, response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Photo> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                }
            });
        } else {
            unsplashInterface.unlikePhoto(photoId).enqueue(new Callback<Photo>() {
                @Override
                public void onResponse(@NonNull Call<Photo> call, @NonNull Response<Photo> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    if (response.code() == 200) {
                        imageLikes.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        int likesPhoto = likes - 1;
                        String likesText = "Likes: " + likes;
                        likesTextView.setText(likesPhoto);
                    } else {
                        utils.showAlertDialog(context, response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Photo> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                }
            });
        }
    }

    public void showFullScreenImage(Photo photo, Context context) {
        Intent intent = new Intent(context, FullScreenPhotoActivity.class);
        intent.putExtra("photoId", photo.getId());
        intent.putExtra("downloads", photo.getDownloads());
        intent.putExtra("likes", photo.getLikes());
        intent.putExtra("description", photo.getDescription());
        intent.putExtra("fullUrl", photo.getUrls().getFull());
        intent.putExtra("liked_by_user", photo.isLiked_by_user());
        intent.putExtra("htmlLink", photo.getLinks().getHtml());
        intent.putExtra("downloadLink", photo.getLinks().getDownload());

        intent.putExtra("userId", photo.getUser().getId());
        intent.putExtra("userUsername", photo.getUser().getUsername());
        intent.putExtra("userFirstName", photo.getUser().getFirstName());
        intent.putExtra("userLastName", photo.getUser().getLastName());
        intent.putExtra("userProfileImage", photo.getUser().getProfileImage().getLarge());
        context.startActivity(intent);
    }

}
