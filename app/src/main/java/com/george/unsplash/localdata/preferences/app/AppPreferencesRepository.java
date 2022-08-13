package com.george.unsplash.localdata.preferences.app;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class AppPreferencesRepository implements AppPreferencesBehaviour {

    SharedPreferences sharedPreferences;

    public AppPreferencesRepository(Application application) {
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(application);
    }

    @Override
    public int getPerPage() {
        return Integer.parseInt(sharedPreferences.getString("per_page_preference", "10"));
    }

    @Override
    public int getGridPhotos() {
        return Integer.parseInt(sharedPreferences.getString("grid_photos_preference", "2"));
    }
}
