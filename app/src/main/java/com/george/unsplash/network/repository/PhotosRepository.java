package com.george.unsplash.network.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.search.Search;
import com.george.unsplash.network.models.topic.Topic;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotosRepository {

    UnsplashInterface unsplashInterface;
    private final MutableLiveData<List<Photo>> listPhoto = new MutableLiveData<>();
    private final MutableLiveData<Photo> photo = new MutableLiveData<>();
    private final MutableLiveData<Topic> topic = new MutableLiveData<>();
    private final MutableLiveData<List<Topic>> listTopic = new MutableLiveData<>();
    private final MutableLiveData<Search> search = new MutableLiveData<>();

    public PhotosRepository(String token) {
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
    }

    public void unlikePhoto(String id) {
        unsplashInterface.unlikePhoto(id).enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(@NonNull Call<Photo> call, @NonNull Response<Photo> response) {
                if (response.code() == 200) {
                    photo.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Photo> call, @NonNull Throwable t) {
                photo.postValue(null);
            }
        });

    }

    public void likePhoto(String id) {
        unsplashInterface.likePhoto(id).enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(@NonNull Call<Photo> call, @NonNull Response<Photo> response) {
                photo.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Photo> call, @NonNull Throwable t) {
                photo.postValue(null);
            }
        });
    }

    public MutableLiveData<Photo> getPhoto(String id) {
        unsplashInterface.getPhoto(id).enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(@NonNull Call<Photo> call, @NonNull Response<Photo> response) {
                if (response.code() == 200) {
                    photo.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Photo> call, @NonNull Throwable t) {
                photo.postValue(null);
            }
        });

        return photo;
    }

    public MutableLiveData<List<Photo>> getUserLikePhotos(String username, int page) {
        unsplashInterface.getUserLikePhotos(username, page).enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                if (response.code() == 200) {
                    listPhoto.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                listPhoto.postValue(null);
            }
        });

        return listPhoto;
    }

    public MutableLiveData<List<Photo>> getUserPhotos(String username, int page) {
        unsplashInterface.getUserPhotos(username, page).enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                if (response.code() == 200) {
                    listPhoto.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                listPhoto.postValue(null);
            }
        });

        return listPhoto;
    }

    public MutableLiveData<Search> findPhotos(String query, int page, String color, String orientation) {
        unsplashInterface.findPhotos(query, page, color, orientation).enqueue(new Callback<Search>() {
            @Override
            public void onResponse(@NonNull Call<Search> call, @NonNull Response<Search> response) {
                if (response.code() == 200) {
                    search.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
                search.postValue(null);
            }
        });

        return search;
    }

    public MutableLiveData<List<Topic>> getTopics() {
        unsplashInterface.getTopics().enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(@NonNull Call<List<Topic>> call, @NonNull Response<List<Topic>> response) {
                if (response.code() == 200) {
                    listTopic.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Topic>> call, @NonNull Throwable t) {
                listTopic.postValue(null);
            }
        });

        return listTopic;
    }

    public MutableLiveData<Topic> getTopic(String slug) {
        unsplashInterface.getTopic(slug).enqueue(new Callback<Topic>() {
            @Override
            public void onResponse(@NonNull Call<Topic> call, @NonNull Response<Topic> response) {
                if (response.code() == 200) {
                    topic.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Topic> call, @NonNull Throwable t) {
                topic.postValue(null);
            }
        });

        return topic;
    }

    public MutableLiveData<List<Photo>> getTopicsPhotos(String slug, int page) {
        unsplashInterface.getTopicPhotos(slug, page).enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                if (response.code() == 200) {
                    listPhoto.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                listPhoto.postValue(null);
            }
        });

        return listPhoto;
    }

}
