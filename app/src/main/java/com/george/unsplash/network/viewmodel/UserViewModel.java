package com.george.unsplash.network.viewmodel;

import static com.george.unsplash.utils.Keys.USER_PREFERENCES;
import static com.george.unsplash.utils.Keys.USER_TOKEN;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.models.user.Token;
import com.george.unsplash.network.models.user.common.User;
import com.george.unsplash.network.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {

    UserRepository repository;
    SharedPreferences sharedPreferences;

    public UserViewModel(@NonNull Application application) {
        super(application);

        sharedPreferences = application.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(USER_TOKEN, "");
        repository = new UserRepository(token);
    }

    public MutableLiveData<Me> getMeData(String token) {
        return repository.getMeData(token);
    }

    public MutableLiveData<User> getUserData(String username) {
        return repository.getUserData(username);
    }

    public MutableLiveData<Token> getToken(String code) {
        return repository.getToken(code);
    }

}
