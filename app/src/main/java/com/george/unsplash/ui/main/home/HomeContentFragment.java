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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.george.unsplash.databinding.HomeContentFragmentBinding;
import com.george.unsplash.localdata.PreferencesViewModel;
import com.george.unsplash.localdata.topic.TopicData;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.photo.Urls;
import com.george.unsplash.network.models.topic.CoverPhoto;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.ui.photos.PhotoViewModel;
import com.george.unsplash.ui.photos.PhotosAdapter;
import com.george.unsplash.ui.photos.TopicAdapter;
import com.george.unsplash.ui.photos.TopicDatabaseViewModel;
import com.george.unsplash.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeContentFragment extends Fragment {

    HomeContentFragmentBinding binding;

    PhotoViewModel photoViewModel;
    PreferencesViewModel preferencesViewModel;
    TopicDatabaseViewModel topicDatabaseViewModel;

    UnsplashInterface unsplashInterface;
    TopicAdapter topicAdapter = new TopicAdapter();
    PhotosAdapter photosAdapter;

    Utils utils = new Utils();

    public static final String TAG = "HomeContentFragment";

    int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeContentFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        assert args != null;

        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);
        topicDatabaseViewModel = new ViewModelProvider(this).get(TopicDatabaseViewModel.class);

        photosAdapter = new PhotosAdapter(HomeContentFragment.this.requireActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeContentFragment.this.requireActivity());
        binding.homeRecyclerView.setLayoutManager(layoutManager);
        binding.homeRecyclerView.setHasFixedSize(true);
        binding.homeRecyclerView.setAdapter(photosAdapter);

        String token = preferencesViewModel.getToken();
        int position = args.getInt("position");

        topicDatabaseViewModel.getAllTopics().observe(HomeContentFragment.this.requireActivity(), topicData -> {
            topicAdapter.addTopics(topicData);

            TopicData topic = topicAdapter.getTopicAt(position);
            binding.titleHomeTextView.setText(topic.getTitle());
            binding.descriptionHomeTextView.setText(topic.getDescription());
            setUpHomePage(token, topic.getSlug());

            binding.loadPhotoBtn.setOnClickListener(v -> {
                photosAdapter.clear();
                getPhotos(topic.getSlug());
            });
        });



        return root;
    }

    void setUpHomePage(String token, String topicSlug) {
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        unsplashInterface.getTopic(topicSlug).enqueue(new Callback<Topic>() {
            @Override
            public void onResponse(@NonNull Call<Topic> call, @NonNull Response<Topic> response) {
                if(response.code() == 200) {
                    Topic topic = response.body();
                    assert topic != null;
                    CoverPhoto coverPhoto = topic.getCoverPhoto();
                    Urls urls = coverPhoto.getUrls();

                    Glide.with(HomeContentFragment.this.requireActivity())
                            .load(urls.getRegular())
                            .into(binding.homeMainImage);
                } else {
                    utils.showAlertDialog(HomeContentFragment.this.requireActivity(), response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Topic> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

        getPhotos(topicSlug);
    }

    void getPhotos(String topicSlug) {
        binding.loadingContent.setVisibility(View.VISIBLE);
        binding.homeContent.setVisibility(View.INVISIBLE);

        unsplashInterface.getTopicPhotos(topicSlug, page).enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if(response.code() == 200) {
                    List<Photo> photos = response.body();
                    photosAdapter.addPhotos(photos);

                    binding.loadingContent.setVisibility(View.INVISIBLE);
                    binding.homeContent.setVisibility(View.VISIBLE);
                } else {
                    utils.showAlertDialog(HomeContentFragment.this.requireActivity(), response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

        page++;
    }

}
