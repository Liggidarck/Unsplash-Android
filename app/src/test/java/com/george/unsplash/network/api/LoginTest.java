package com.george.unsplash.network.api;

import static com.george.unsplash.utils.Keys.RESPONSE_URL;
import static com.george.unsplash.utils.Keys.SECRET_KEY;
import static com.george.unsplash.utils.Keys.UNSPLASH_ACCESS_KEY;
import static org.junit.Assert.*;

import com.george.unsplash.network.models.user.Token;
import com.george.unsplash.utils.Keys;

import org.junit.Before;

import java.security.Key;

import retrofit2.Call;

public class LoginTest {

    private UnsplashInterface unsplashInterface;

    @Before
    void setup() throws Exception {
        unsplashInterface = UnsplashBaseClient.getBaseUnsplashClient().create(UnsplashInterface.class);
    }

}