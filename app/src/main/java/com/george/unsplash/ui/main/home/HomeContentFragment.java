package com.george.unsplash.ui.main.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.george.unsplash.databinding.HomeContentFragmentBinding;
import com.george.unsplash.localdata.PreferencesViewModel;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Urls;
import com.george.unsplash.network.models.topic.CoverPhoto;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.ui.photos.PhotoViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeContentFragment extends Fragment {

    HomeContentFragmentBinding binding;

    PhotoViewModel photoViewModel;
    PreferencesViewModel preferencesViewModel;
    UnsplashInterface unsplashInterface;

    public static final String TAG = "HomeContentFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeContentFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        assert args != null;

        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);

        String token = preferencesViewModel.getToken();
        int topicSlug = args.getInt("position");


        return root;
    }

    void getTopicFromApi(String token, String topicSlug) {
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        unsplashInterface.getTopic(topicSlug).enqueue(new Callback<Topic>() {
            @Override
            public void onResponse(@NonNull Call<Topic> call, @NonNull Response<Topic> response) {
                Topic topic = response.body();
                assert topic != null;
                CoverPhoto coverPhoto = topic.getCover_photo();
                Urls urls = coverPhoto.getUrls();
                binding.titleHomeTextView.setText(topic.getTitle());
                binding.descriptionHomeTextView.setText(topic.getDescription());

                Glide.with(HomeContentFragment.this.requireActivity())
                        .load(urls.getRegular())
                        .into(binding.homeMainImage);
            }

            @Override
            public void onFailure(@NonNull Call<Topic> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
