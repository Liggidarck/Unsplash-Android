package com.george.unsplashapp.network.api;

import com.george.unsplashapp.network.models.User;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UnsplashMainInterface {

    @GET("me")
    Call<User> getUsersData();

}
