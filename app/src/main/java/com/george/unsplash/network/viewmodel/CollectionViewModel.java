package com.george.unsplash.network.viewmodel;

import static com.george.unsplash.utils.Keys.USER_PREFERENCES;
import static com.george.unsplash.utils.Keys.USER_TOKEN;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.network.models.collection.CollectionPhotos;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.repository.CollectionRepository;

import java.util.List;

public class CollectionViewModel extends AndroidViewModel {

    SharedPreferences sharedPreferences;
    CollectionRepository repository;

    public CollectionViewModel(@NonNull Application application) {
        super(application);

        sharedPreferences = application.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(USER_TOKEN, "");
        repository = new CollectionRepository(token);
    }

    public MutableLiveData<List<Photo>> getPhotosCollection(String collectionId, int page) {
        return loadPhotos(collectionId, page);
    }

    public MutableLiveData<CollectionPhotos> createNewCollection(String nameCollection, String descriptionCollection, boolean isPrivate) {
        return repository.createNewCollection(nameCollection, descriptionCollection, isPrivate);
    }

    public MutableLiveData<CollectionPhotos> deleteCollection(String id) {
        return repository.deleteCollection(id);
    }

    public MutableLiveData<CollectionPhotos> updateCollection(String id, String title, String description, boolean isPrivate) {
        return repository.updateCollection(id, title, description, isPrivate);
    }

    public MutableLiveData<List<CollectionPhotos>> getCollections(String username, int page) {
        return loadCollections(username, page);
    }

    private MutableLiveData<List<Photo>> loadPhotos(String collectionId, int page) {
        return repository.getPhotosCollection(collectionId, page);
    }

    private MutableLiveData<List<CollectionPhotos>> loadCollections(String username, int page) {
        return repository.getUserCollection(username, page);
    }

}
