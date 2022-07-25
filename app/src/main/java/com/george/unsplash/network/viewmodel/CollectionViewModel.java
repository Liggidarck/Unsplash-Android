package com.george.unsplash.network.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.models.collection.CollectionPhotos;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.repository.CollectionRepository;

import java.util.List;

public class CollectionViewModel extends AndroidViewModel {

    AppPreferences appPreferences;
    CollectionRepository repository;

    public CollectionViewModel(@NonNull Application application) {
        super(application);

        appPreferences = new AppPreferences(application);
        String token = appPreferences.getToken();
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
