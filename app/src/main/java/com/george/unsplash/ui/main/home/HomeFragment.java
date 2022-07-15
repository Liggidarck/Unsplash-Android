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
import com.george.unsplash.localdata.topic.TopicData;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.network.viewmodel.PhotoViewModelFuture;
import com.george.unsplash.network.viewmodel.TopicDatabaseViewModel;
import com.george.unsplash.ui.adapters.TopicAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;

    private final TopicAdapter topicAdapter = new TopicAdapter();

    private TopicDatabaseViewModel topicDatabaseViewModel;
    private PhotoViewModelFuture photoViewModelFuture;

    public static final String TAG = HomeFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initRecyclerView();

        topicDatabaseViewModel = new ViewModelProvider(this).get(TopicDatabaseViewModel.class);
        photoViewModelFuture = new ViewModelProvider(this).get(PhotoViewModelFuture.class);

        topicDatabaseViewModel.getAllTopics().observe(HomeFragment.this.requireActivity(), topicData -> {
            if (topicData.isEmpty()) {
                getTopicsFromApi();
            }
            topicAdapter.addTopics(topicData);
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
        photoViewModelFuture
                .getListTopic()
                .observe(HomeFragment.this.requireActivity(), this::saveTopics);
    }

    void saveTopics(List<Topic> topics) {
        for (int i = 0; i < topics.size(); i++) {
            Topic topic = topics.get(i);
            String title = topic.getTitle();
            String description = topic.getDescription();
            String slug = topic.getSlug();
            int totalPhotos = topic.getTotalPhotos();
            TopicData topicData = new TopicData(title, description, totalPhotos, slug);
            topicDatabaseViewModel.insert(topicData);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
