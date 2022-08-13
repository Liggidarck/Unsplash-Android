package com.george.unsplash.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;

import com.george.unsplash.R;
import com.george.unsplash.databinding.SettingsActivityBinding;
import com.george.unsplash.localdata.preferences.user.UserDataViewModel;
import com.george.unsplash.ui.login.LoginActivity;

public class SettingsActivity extends AppCompatActivity {

    SettingsActivityBinding binding;
    UserDataViewModel userDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UnsplashApp);
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        binding.settingsToolbar.setNavigationOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class))
        );

        binding.btnLogout.setOnClickListener(v -> {
            userDataViewModel.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}