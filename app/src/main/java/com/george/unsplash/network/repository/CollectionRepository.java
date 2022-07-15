package com.george.unsplash.network.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.collection.CollectionPhotos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionRepository {

    UnsplashInterface unsplashInterface;

    private final MutableLiveData<List<CollectionPhotos>> listCollection = new MutableLiveData<>();
    private final MutableLiveData<CollectionPhotos> collectionPhotos = new MutableLiveData<>();


    public CollectionRepository(String token) {
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
    }

    public MutableLiveData<CollectionPhotos> createNewCollection(String nameCollection, String descriptionCollection, boolean isPrivate) {
        unsplashInterface.createNewCollection(nameCollection, descriptionCollection, isPrivate).enqueue(new Callback<CollectionPhotos>() {
            @Override
            public void onResponse(@NonNull Call<CollectionPhotos> call, @NonNull Response<CollectionPhotos> response) {
                if(response.code() == 201) {
                    collectionPhotos.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CollectionPhotos> call, @NonNull Throwable t) {
                collectionPhotos.postValue(null);
            }
        });

        return collectionPhotos;
    }

    public MutableLiveData<CollectionPhotos> deleteCollection(String id) {
        unsplashInterface.deleteCollection(id).enqueue(new Callback<CollectionPhotos>() {
            @Override
            public void onResponse(@NonNull Call<CollectionPhotos> call, @NonNull Response<CollectionPhotos> response) {
                if (response.code() == 204) {
                    collectionPhotos.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CollectionPhotos> call, @NonNull Throwable t) {
                collectionPhotos.postValue(null);
            }
        });

        return collectionPhotos;
    }

    public MutableLiveData<CollectionPhotos> updateCollection(String id, String title, String description, boolean isPrivate) {
        unsplashInterface.updateCollection(id, title, description, isPrivate).enqueue(new Callback<CollectionPhotos>() {
            @Override
            public void onResponse(@NonNull Call<CollectionPhotos> call, @NonNull Response<CollectionPhotos> response) {
                if (response.code() == 200) {
                    collectionPhotos.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CollectionPhotos> call, @NonNull Throwable t) {
                collectionPhotos.postValue(null);
            }
        });

        return collectionPhotos;
    }

    public MutableLiveData<List<CollectionPhotos>> getUserCollection(String username, int page, int perPage) {
        unsplashInterface.getUserCollection(username, page, perPage).enqueue(new Callback<List<CollectionPhotos>>() {
            @Override
            public void onResponse(@NonNull Call<List<CollectionPhotos>> call, @NonNull Response<List<CollectionPhotos>> response) {
                if (response.code() == 200) {
                    listCollection.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CollectionPhotos>> call, @NonNull Throwable t) {
                listCollection.postValue(null);
            }
        });

        return listCollection;
    }

}
