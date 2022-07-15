package com.george.unsplash.network.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.models.user.Me;
import com.george.unsplash.network.models.user.common.User;
import com.george.unsplash.network.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {

    UserRepository repository;
    AppPreferences preferences;

    public UserViewModel(@NonNull Application application) {
        super(application);
        preferences = new AppPreferences(application);
        repository = new UserRepository(preferences.getToken());
    }

    public MutableLiveData<Me> getMeData() {
        return repository.getMeData();
    }

    public MutableLiveData<User> getUserData(String username) {
        return repository.getUserData(username);
    }

}
