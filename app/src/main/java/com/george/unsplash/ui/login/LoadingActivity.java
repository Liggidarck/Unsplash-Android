package com.george.unsplash.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import com.george.unsplash.databinding.ActivityLoadingBinding;
import com.george.unsplash.localdata.preferences.user.UserDataViewModel;
import com.george.unsplash.ui.main.MainActivity;

public class LoadingActivity extends AppCompatActivity {

    ActivityLoadingBinding binding;
    UserDataViewModel userDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        String token = userDataViewModel.getToken();
        if(!token.equals("")) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();


    }
}