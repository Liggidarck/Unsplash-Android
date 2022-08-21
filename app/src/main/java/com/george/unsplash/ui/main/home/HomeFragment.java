package com.george.unsplash.ui.main.home;

import android.content.Intent;
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
import com.george.unsplash.localdata.topic.TopicData;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.network.viewmodel.TopicDatabaseViewModel;
import com.george.unsplash.ui.adapters.TopicAdapter;
import com.george.unsplash.ui.main.SettingsActivity;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;

    private final TopicAdapter topicAdapter = new TopicAdapter();

    private TopicDatabaseViewModel topicDatabaseViewModel;
    private PhotoViewModel photoViewModel;

    public static final String TAG = HomeFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.topAppBarHome.inflateMenu(R.menu.home_menu);
        binding.topAppBarHome.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.settingsItem)
                startActivity(new Intent(HomeFragment.this.requireActivity(), SettingsActivity.class));
            return false;
        });

        initRecyclerView();

        topicDatabaseViewModel = new ViewModelProvider(this).get(TopicDatabaseViewModel.class);
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        topicDatabaseViewModel
                .getAllTopics()
                .observe(HomeFragment.this.requireActivity(), topicData -> {
                    if(topicData.isEmpty()) {
                        getTopicsFromApi();
                    } else {
                        topicAdapter.addTopics(topicData);
                    }
                });

        topicAdapter.setOnClickItemListener((topic, position) -> startContentFragment(position));

        if (savedInstanceState == null)
            startContentFragment(1);

        return view;
    }

    private void startContentFragment(int position) {
        Log.d(TAG, "onCreateView: start content fragment");

        Fragment fragmentContent = new HomeContentFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);

        fragmentContent.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameHomeRoot, fragmentContent)
                .commit();
    }

    private void initRecyclerView() {
        binding.topicRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        binding.topicRecyclerView.setHasFixedSize(true);
        binding.topicRecyclerView.setAdapter(topicAdapter);
    }

    void getTopicsFromApi() {
        photoViewModel
                .getListTopic()
                .observe(HomeFragment.this.requireActivity(), this::saveTopics);
    }

    void saveTopics(List<Topic> topics) {
        for(Topic topic : topics) {
            String title = topic.getTitle();
            String description = topic.getDescription();
            String slug = topic.getSlug();
            int totalPhotos = topic.getTotalPhotos();
            TopicData topicData = new TopicData(title, description, totalPhotos, slug);
            Log.d(TAG, "saveTopics: " + title);
            topicDatabaseViewModel.insert(topicData);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
