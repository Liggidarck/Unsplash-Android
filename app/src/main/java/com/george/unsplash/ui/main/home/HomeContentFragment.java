package com.george.unsplash.ui.main.home;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.george.unsplash.databinding.HomeContentFragmentBinding;
import com.george.unsplash.localdata.PreferencesViewModel;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.ui.photos.PhotoViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeContentFragment extends Fragment {

    HomeContentFragmentBinding binding;

    PhotoViewModel photoViewModel;
    PreferencesViewModel preferencesViewModel;

    public static final String TAG = "HomeContentFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeContentFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);

        String token = preferencesViewModel.getToken();

        photoViewModel.getRandomPhoto(token, binding.homeMainImage, HomeContentFragment.this.requireActivity());

        return root;
    }
}
