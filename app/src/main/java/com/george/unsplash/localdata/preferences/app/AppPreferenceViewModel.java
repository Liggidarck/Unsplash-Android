package com.george.unsplash.localdata.preferences.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class AppPreferenceViewModel extends AndroidViewModel {

    AppPreferencesRepository repository;

    public AppPreferenceViewModel(@NonNull Application application) {
        super(application);
        repository = new AppPreferencesRepository(application);
    }

    public int getPerPage() {
        return repository.getPerPage();
    }

    public boolean isGrid() {
        return repository.isGrid();
    }

    public int getGridPhotos() {
        return repository.getGridPhotos();
    }

}
