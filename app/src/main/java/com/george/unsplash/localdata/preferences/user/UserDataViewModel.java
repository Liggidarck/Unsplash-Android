package com.george.unsplash.localdata.preferences.user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.george.unsplash.network.models.user.Me;

public class UserDataViewModel extends AndroidViewModel {

    UserDataRepository repository;

    public UserDataViewModel(@NonNull Application application) {
        super(application);
        repository = new UserDataRepository(application);
    }

    public void saveToken(String token) {
        repository.saveToken(token);
    }

    public void saveScope(String scope) {
        repository.saveScope(scope);
    }

    public void saveMe(Me me) {
        repository.saveMe(me);
    }

    public void saveProfileImage(String image) {
        repository.saveProfileImage(image);
    }

    public String getToken() {
        return repository.getToken();
    }

    public Me getMe() {
        return repository.getMe();
    }

    public void clearMe() {
        repository.clearMe();
    }

    public String getProfileImage() {
        return repository.getProfileImage();
    }

    public void logout() {
        repository.logout();
    }

}
