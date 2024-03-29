package com.george.unsplash.network.viewmodel;

import static com.george.unsplash.utils.Keys.USER_PREFERENCES;
import static com.george.unsplash.utils.Keys.USER_TOKEN;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.R;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.search.Search;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.network.repository.PhotosRepository;
import com.george.unsplash.ui.main.photos.FullScreenPhotoActivity;

import java.util.List;

public class PhotoViewModel extends AndroidViewModel {

    PhotosRepository repository;
    SharedPreferences sharedPreferences;

    public PhotoViewModel(@NonNull Application application) {
        super(application);


        sharedPreferences = application.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(USER_TOKEN, "");
        repository = new PhotosRepository(token);
    }

    public void unlikePhoto(String id) {
        repository.unlikePhoto(id);
    }

    public void likePhoto(String id) {
        repository.likePhoto(id);
    }

    public MutableLiveData<Photo> getPhoto(String id) {
        return repository.getPhoto(id);
    }

    public MutableLiveData<List<Photo>> getUserLikePhotos(String username, int page) {
        return loadUserLikePhotos(username, page);
    }

    public MutableLiveData<List<Photo>> getUserPhotos(String username, int page, int perPage) {
        return loadUserPhotos(username, page, perPage);
    }

    public MutableLiveData<Search> findPhotos(String query, int page, String color, String orientation, int perPage) {
        return repository.findPhotos(query, page, color, orientation, perPage);
    }

    public MutableLiveData<List<Topic>> getListTopic() {
        return loadListTopic();
    }

    public MutableLiveData<Topic> getTopic(String slug) {
        return repository.getTopic(slug);
    }

    public MutableLiveData<List<Photo>> getTopicsPhotos(String slug, int page, int perPage) {
        return loadTopicsPhotos(slug, page, perPage);
    }

    private MutableLiveData<List<Photo>> loadUserLikePhotos(String username, int page) {
        return repository.getUserLikePhotos(username, page);
    }

    private MutableLiveData<List<Photo>> loadUserPhotos(String username, int page, int perPage) {
        return repository.getUserPhotos(username, page, perPage);
    }

    private MutableLiveData<List<Topic>> loadListTopic() {
        return repository.getTopics();
    }

    private MutableLiveData<List<Photo>> loadTopicsPhotos(String slug, int page, int perPage) {
        return repository.getTopicsPhotos(slug, page, perPage);
    }

    public void showFullScreenImage(Photo photo, Context context) {
        Intent intent = new Intent(context, FullScreenPhotoActivity.class);
        intent.putExtra("photoId", photo.getId());
        intent.putExtra("downloads", photo.getDownloads());
        intent.putExtra("likes", photo.getLikes());
        intent.putExtra("description", photo.getDescription());
        intent.putExtra("fullUrl", photo.getUrls().getRegular());
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

    public void likePhotoBehavior(boolean likedByUser, String photoId, int likes,
                                  ImageView imageLikes, TextView likesTextView) {
        if (!likedByUser) {
            likePhoto(photoId);
            imageLikes.setImageResource(R.drawable.ic_baseline_favorite_24);
            int likesPhoto = likes + 1;
            String likesText = "Likes: " + likesPhoto;
            likesTextView.setText(likesText);
        } else {
            unlikePhoto(photoId);
            imageLikes.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            int likesPhoto = likes - 1;
            String likesText = "Likes: " + likesPhoto;
            likesTextView.setText(likesText);
        }
    }


}
