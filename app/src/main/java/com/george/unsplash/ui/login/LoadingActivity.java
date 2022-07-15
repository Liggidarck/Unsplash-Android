package com.george.unsplash.ui.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.george.unsplash.databinding.ActivityLoadingBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.ui.main.MainActivity;

public class LoadingActivity extends AppCompatActivity {

    ActivityLoadingBinding binding;
    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appPreferences = new AppPreferences(this);

        String token = appPreferences.getToken();
        if(!token.equals("")) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();


    }
}