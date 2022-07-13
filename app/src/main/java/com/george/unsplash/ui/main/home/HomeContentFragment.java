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
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.localdata.topic.TopicData;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.photo.Urls;
import com.george.unsplash.network.models.topic.CoverPhoto;
import com.george.unsplash.network.models.topic.Topic;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.ui.adapters.TopicAdapter;
import com.george.unsplash.ui.main.photos.PhotoViewModel;
import com.george.unsplash.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeContentFragment extends Fragment {

    private HomeContentFragmentBinding binding;

    private AppPreferences appPreferences;
    private TopicDatabaseViewModel topicDatabaseViewModel;
    private PhotoViewModel photoViewModel;

    private UnsplashInterface unsplashInterface;
    TopicAdapter topicAdapter = new TopicAdapter();
    PhotosAdapter photosAdapter;
    private List<Photo> photos;

    private final Utils utils = new Utils();

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

        String token = appPreferences.getToken();
        int position = args.getInt("position");

        initRecyclerView();

        initHomePage(token, position);

        return root;
    }

    private void initHomePage(String token, int position) {
        topicDatabaseViewModel
                .getAllTopics()
                .observe(HomeContentFragment.this.requireActivity(), topicData -> {
                    topicAdapter.addTopics(topicData);
                    try {
                        TopicData topic = topicAdapter.getTopicAt(position);
                        binding.titleHomeTextView.setText(topic.getTitle());
                        binding.descriptionHomeTextView.setText(topic.getDescription());
                        getMainImage(token, topic.getSlug());

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
                            fetchPhotos(topic.getSlug());
                            binding.swipeRefreshHomeContent.setRefreshing(false);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    void getMainImage(String token, String topicSlug) {
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        unsplashInterface.getTopic(topicSlug).enqueue(new Callback<Topic>() {
            @Override
            public void onResponse(@NonNull Call<Topic> call, @NonNull Response<Topic> response) {
                if (response.code() == 200) {
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

        fetchPhotos(topicSlug);
    }

    void fetchPhotos(String topicSlug) {
        unsplashInterface.getTopicPhotos(topicSlug, page).enqueue(new Callback<List<Photo>>() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    photos.addAll(response.body());
                    photosAdapter.notifyDataSetChanged();
                } else {
                    utils.showAlertDialog(HomeContentFragment.this.requireActivity(), response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

        page += 1;
    }

    private void initViewModels() {
        photoViewModel = new ViewModelProvider(this)
                .get(PhotoViewModel.class);

        appPreferences = new AppPreferences(HomeContentFragment.this.requireActivity());

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
}
