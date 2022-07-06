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

import com.george.unsplash.R;
import com.george.unsplash.databinding.HomeFragmentBinding;
import com.george.unsplash.localdata.PreferencesViewModel;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.ui.photos.TopicAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    HomeFragmentBinding binding;

    public static final String TAG = "HomeFragment";

    TopicAdapter topicAdapter = new TopicAdapter();
    PreferencesViewModel preferencesViewModel;
    UnsplashInterface unsplashInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (savedInstanceState == null) {
            Fragment fragmentContent = new HomeContentFragment();
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
        String token = preferencesViewModel.getToken();

        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);

        unsplashInterface
                .getTopics()
                .enqueue(new Callback<List<Topic>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Topic>> call, @NonNull Response<List<Topic>> response) {
                        ArrayList<Topic> topics = (ArrayList<Topic>) response.body();
                        topicAdapter.addTopics(topics);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Topic>> call, @NonNull Throwable t) {

                    }
                });

        topicAdapter.setOnClickItemListener(topic -> {
            Log.d(TAG, "onCreateView: " + topic.getTitle());
            Log.d(TAG, "onCreateView: " + topic.getDescription());
        });

        return view;
    }
}
