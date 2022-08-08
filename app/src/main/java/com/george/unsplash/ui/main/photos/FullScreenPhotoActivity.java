package com.george.unsplash.ui.main.photos;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.george.unsplash.R;
import com.george.unsplash.databinding.ActivityFullScreenPhotoBinding;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.ui.main.profile.UserActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class FullScreenPhotoActivity extends AppCompatActivity {

    private ActivityFullScreenPhotoBinding binding;

    private DownloadManager.Request downloadRequest;

    String fullUrl, photoId, description, userId, username,
            firstName, lastName, profileImage, fullName, likesCount,
            htmlLink, downloadLink;
    int downloads, likes;
    boolean likedByUser;

    private PhotoViewModel photoViewModel;

    public static final String TAG = FullScreenPhotoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UnsplashApp);
        binding = ActivityFullScreenPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        binding.toolbarPhotoScreen.setNavigationOnClickListener(v -> onBackPressed());

        getImageData();
        loadDataToUi();

        binding.downloadView.setOnClickListener(v -> downloadPhoto());
        binding.informationView.setOnClickListener(v -> {
            PhotoInfoBottomSheet photoInfoBottomSheet = new PhotoInfoBottomSheet();
            Bundle bundle = new Bundle();
            bundle.putString("photoId", photoId);
            photoInfoBottomSheet.setArguments(bundle);
            photoInfoBottomSheet.show(getSupportFragmentManager(), "PhotoInfoBottomSheet");
        });
        binding.likeView.setOnClickListener(v -> photoViewModel
                .likePhotoBehavior(likedByUser, photoId, likes,
                        binding.imageLikes, binding.likesTextView));
        binding.userView.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }

    private void getImageData() {
        Bundle extras = getIntent().getExtras();
        photoId = extras.getString("photoId");
        downloads = extras.getInt("downloads");
        likes = extras.getInt("likes");
        description = extras.getString("description");
        fullUrl = extras.getString("fullUrl");
        likedByUser = extras.getBoolean("liked_by_user");
        htmlLink = extras.getString("htmlLink");
        downloadLink = extras.getString("downloadLink");

        userId = extras.getString("userId");
        username = extras.getString("userUsername");
        firstName = extras.getString("userFirstName");
        lastName = extras.getString("userLastName");
        profileImage = extras.getString("userProfileImage");

        if (lastName == null)
            lastName = "";
        fullName = firstName + " " + lastName;
        likesCount = "Likes: " + likes;

    }

    private void loadDataToUi() {
        Glide.with(this)
                .load(fullUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        binding.progressBarFullImage.setVisibility(View.INVISIBLE);
                        Toast.makeText(FullScreenPhotoActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onLoadFailed: ", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        binding.progressBarFullImage.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .into(binding.mainImage);

        Glide.with(this)
                .load(profileImage)
                .into(binding.userProfileImage);

        binding.fullNameTextView.setText(fullName);
        binding.likesTextView.setText(likesCount);

        if(likedByUser)
            binding.imageLikes.setImageResource(R.drawable.ic_baseline_favorite_24);
    }

    private void downloadPhoto() {
        try {
            downloadRequest = new DownloadManager.Request(Uri.parse(downloadLink));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        String fileName = photoId + ".jpg";
        File dir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath() + "/Unsplash/");
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            Log.d(TAG, "downloadPhoto: directory created " + result);
        }

        Snackbar.make(binding.coordinatorImageActivity, "Image download started", Snackbar.LENGTH_SHORT).show();
        downloadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadRequest.setTitle(fileName);

        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        if (isStoragePermissionGranted()) {
            downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "/Unsplash/" + fileName);
        } else {
            requestPermission();
        }

        downloadRequest.allowScanningByMediaScanner();

        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        final long DM_ID = downloadManager.enqueue(downloadRequest);
        Log.d(TAG, "downloadPhoto: " + DM_ID);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "isStoragePermissionGranted: Permission granted");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadPhoto();
        }
    }
}