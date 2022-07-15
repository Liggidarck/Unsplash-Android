package com.george.unsplash.network.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.search.Search;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.network.repository.PhotosRepository;

import java.util.List;

public class PhotoViewModelFuture extends AndroidViewModel {

    PhotosRepository repository;
    AppPreferences appPreferences;

    MutableLiveData<List<Photo>> listPhoto = new MutableLiveData<>();
    MutableLiveData<List<Topic>> listTopic = new MutableLiveData<>();

    public PhotoViewModelFuture(@NonNull Application application) {
        super(application);

        appPreferences = new AppPreferences(application);
        String token = appPreferences.getToken();
        repository = new PhotosRepository(token);
    }

    public MutableLiveData<Photo> unlikePhoto(String id) {
        return repository.unlikePhoto(id);
    }

    public MutableLiveData<Photo> likePhoto(String id) {
        return repository.likePhoto(id);
    }

    public MutableLiveData<Photo> getPhoto(String id) {
        return repository.getPhoto(id);
    }

    public MutableLiveData<List<Photo>> getUserLikePhotos(String username, int page, int perPage) {
        listPhoto = loadUserLikePhotos(username, page, perPage);
        return listPhoto;
    }

    public MutableLiveData<List<Photo>> getUserPhotos(String username, int page, int perPage) {
        listPhoto = loadUserPhotos(username, page, perPage);
        return listPhoto;
    }

    public MutableLiveData<Search> findPhotos(String query, int page, String color, String orientation) {
        return repository.findPhotos(query, page, color, orientation);
    }

    public MutableLiveData<List<Topic>> getListTopic() {
        listTopic = loadListTopic();
        return listTopic;
    }

    public MutableLiveData<Topic> getTopic(String slug) {
        return repository.getTopic(slug);
    }

    public MutableLiveData<List<Photo>> getTopicsPhotos(String slug, int page) {
        listPhoto = loadTopicsPhotos(slug, page);
        return listPhoto;
    }

    private MutableLiveData<List<Photo>> loadUserLikePhotos(String username, int page, int perPage) {
        return repository.getUserLikePhotos(username, page, perPage);
    }

    private MutableLiveData<List<Photo>> loadUserPhotos(String username, int page, int perPage) {
        return repository.getUserPhotos(username, page, perPage);
    }

    private MutableLiveData<List<Topic>> loadListTopic() {
        return repository.getTopics();
    }

    private MutableLiveData<List<Photo>> loadTopicsPhotos(String slug, int page) {
        return repository.getTopicsPhotos(slug, page);
    }


}
