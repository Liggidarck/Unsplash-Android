package com.george.unsplash.ui.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.george.unsplash.R;
import com.george.unsplash.databinding.HomeFragmentBinding;
import com.george.unsplash.localdata.PreferencesViewModel;
import com.george.unsplash.localdata.topic.TopicData;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.ui.photos.TopicAdapter;
import com.george.unsplash.ui.photos.TopicDatabaseViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    HomeFragmentBinding binding;

    public static final String TAG = "HomeFragment";

    TopicAdapter topicAdapter = new TopicAdapter();
    UnsplashInterface unsplashInterface;

    PreferencesViewModel preferencesViewModel;
    TopicDatabaseViewModel topicDatabaseViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (savedInstanceState == null) {
            Fragment fragmentContent = new HomeContentFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("position", 0);

            fragmentContent.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameHomeRoot, fragmentContent)
                    .commit();
        }

        binding.topicRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        binding.topicRecyclerView.setHasFixedSize(true);
        binding.topicRecyclerView.setAdapter(topicAdapter);

        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);
        topicDatabaseViewModel = new ViewModelProvider(this).get(TopicDatabaseViewModel.class);

        String token = preferencesViewModel.getToken();

        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);

        topicDatabaseViewModel.getAllTopics().observe(HomeFragment.this.requireActivity(), topicData -> topicAdapter.addTopics(topicData));
        topicAdapter.setOnClickItemListener((topic, position) -> {
            Fragment fragmentContent = new HomeContentFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("position", position);

            fragmentContent.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameHomeRoot, fragmentContent)
                    .commit();
        });

        return view;
    }

    void getTopicsFromApi() {
        unsplashInterface
                .getTopics()
                .enqueue(new Callback<List<Topic>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Topic>> call, @NonNull Response<List<Topic>> response) {
                        List<Topic> topics = response.body();
                        assert topics != null;
                        saveTopics(topics);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Topic>> call, @NonNull Throwable t) {

                    }
                });
    }

    void saveTopics(List<Topic> topics) {
        for (int i = 0; i < topics.size(); i++) {
            Topic topic = topics.get(i);
            String title = topic.getTitle();
            String description = topic.getDescription();
            int totalPhotos = topic.getTotal_photos();
            TopicData topicData = new TopicData(title, description, totalPhotos);
            topicDatabaseViewModel.insert(topicData);
        }
    }

}
