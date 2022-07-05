package com.george.unsplashapp.network.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseHeader implements Interceptor {
    private final String clientId;

    public BaseHeader(String clientId) {
        this.clientId = clientId;
    }

    @NonNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("Authorization", "Client-ID " + clientId)
                .build();
        return chain.proceed(request);
    }
}
