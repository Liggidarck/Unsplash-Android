package com.george.unsplash.network.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.models.user.common.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    UnsplashInterface unsplashInterface;

    public UserRepository(String token) {
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
    }

    public MutableLiveData<Me> getMeData() {
        MutableLiveData<Me> me = new MutableLiveData<>();

        unsplashInterface.getMeData().enqueue(new Callback<Me>() {
            @Override
            public void onResponse(@NonNull Call<Me> call, @NonNull Response<Me> response) {
                if(response.code() == 200) {
                    me.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Me> call, @NonNull Throwable t) {
                me.postValue(null);
            }
        });

        return me;
    }

    public MutableLiveData<User> getUserData(String username) {
        MutableLiveData<User> user = new MutableLiveData<>();

        unsplashInterface.getUserData(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.code() == 200) {
                    user.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                user.postValue(null);
            }
        });

        return user;
    }

}
