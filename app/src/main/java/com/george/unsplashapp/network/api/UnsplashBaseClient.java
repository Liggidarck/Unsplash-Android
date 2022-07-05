package com.george.unsplashapp.network.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UnsplashBaseClient {
    private static Retrofit retrofit = null;

    public static Retrofit getBaseUnsplashClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new BaseHeader(Keys.UNSPLASH_ACCESS_KEY)).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Keys.BASE_URL_UNSPLASH)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
