package com.george.unsplash.ui.main.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.george.unsplash.databinding.HomeContentFragmentBinding;
import com.george.unsplash.localdata.topic.TopicData;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.photo.Urls;
import com.george.unsplash.network.models.topic.CoverPhoto;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.network.viewmodel.TopicDatabaseViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.ui.adapters.TopicAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeContentFragment extends Fragment {

    private HomeContentFragmentBinding binding;

    private TopicDatabaseViewModel topicDatabaseViewModel;
    private PhotoViewModel photoViewModel;

    TopicAdapter topicAdapter = new TopicAdapter();
    PhotosAdapter photosAdapter;
    private List<Photo> photos;

    public static final String TAG = HomeContentFragment.class.getSimpleName();
    private int page = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photos = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeContentFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        assert args != null;

        initViewModels();

        int position = args.getInt("position");

        initRecyclerView();

        initHomePage(position);

        return root;
    }

    private void initHomePage(int position) {
        topicDatabaseViewModel
                .getAllTopics()
                .observe(HomeContentFragment.this.requireActivity(), topicData -> {
                    topicAdapter.addTopics(topicData);
                    Log.d(TAG, "initHomePage: " + topicData);
                    if (!topicData.isEmpty()) {
                        TopicData topic = topicAdapter.getTopicAt(position);
                        binding.titleHomeTextView.setText(topic.getTitle());
                        binding.descriptionHomeTextView.setText(topic.getDescription());
                        getMainImage(topic.getSlug());

                        binding.homeContent.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1)
                                                .getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {
                                            fetchPhotos(topic.getSlug());
                                        }
                                    }
                                });

                        binding.swipeRefreshHomeContent.setOnRefreshListener(() -> {
                            photos.clear();
                            fetchPhotos(topic.getSlug());
                            binding.swipeRefreshHomeContent.setRefreshing(false);
                        });
                    }
                });
    }

    void getMainImage(String topicSlug) {
        photoViewModel
                .getTopic(topicSlug)
                .observe(HomeContentFragment.this.requireActivity(), topic -> {
                    Log.d(TAG, "getMainImage: " + topic);
                    CoverPhoto coverPhoto = topic.getCoverPhoto();
                    Urls urls = coverPhoto.getUrls();

                    Glide.with(HomeContentFragment.this.requireActivity())
                            .load(urls.getRegular())
                            .into(binding.homeMainImage);
                });

        fetchPhotos(topicSlug);
    }

    @SuppressLint("NotifyDataSetChanged")
    void fetchPhotos(String topicSlug) {
        photoViewModel
                .getTopicsPhotos(topicSlug, page)
                .observe(HomeContentFragment.this.requireActivity(), photoResponse -> {
                    photos.addAll(photoResponse);
                    photosAdapter.notifyDataSetChanged();
                });
        page++;
    }

    private void initViewModels() {
        photoViewModel = new ViewModelProvider(this)
                .get(PhotoViewModel.class);

        photoViewModel = new ViewModelProvider(this)
                .get(PhotoViewModel.class);

        topicDatabaseViewModel = new ViewModelProvider(this)
                .get(TopicDatabaseViewModel.class);
    }

    private void initRecyclerView() {
        photosAdapter = new PhotosAdapter(HomeContentFragment.this.requireActivity(), photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.homeRecyclerView.setLayoutManager(gridLayoutManager);
        binding.homeRecyclerView.setHasFixedSize(true);
        binding.homeRecyclerView.setAdapter(photosAdapter);

        photosAdapter.setOnItemClickListener((photo, position) -> photoViewModel
                .showFullScreenImage(photo, HomeContentFragment.this.requireActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
