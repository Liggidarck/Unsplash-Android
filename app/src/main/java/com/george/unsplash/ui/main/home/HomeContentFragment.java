package com.george.unsplash.ui.main.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.george.unsplash.databinding.HomeContentFragmentBinding;
import com.george.unsplash.localdata.preferences.app.AppPreferenceViewModel;
import com.george.unsplash.localdata.topic.TopicData;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.photo.Urls;
import com.george.unsplash.network.models.topic.CoverPhoto;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.network.viewmodel.TopicDatabaseViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeContentFragment extends Fragment {

    private HomeContentFragmentBinding binding;

    private TopicDatabaseViewModel topicDatabaseViewModel;
    private AppPreferenceViewModel appPreferenceViewModel;
    private PhotoViewModel photoViewModel;

    PhotosAdapter photosAdapter;
    private List<Photo> photos;

    private final DialogUtils dialogUtils = new DialogUtils();

    public static final String TAG = HomeContentFragment.class.getSimpleName();
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeContentFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        assert args != null;
        int position = args.getInt("position");

        photos = new ArrayList<>();

        initViewModels();
        initRecyclerView();
        initHomeView(position);

        return root;
    }

    private void initHomeView(int position) {
        String currentPage = "Page: " + page;
        binding.textViewPage.setText(currentPage);

        topicDatabaseViewModel
                .getAllTopics()
                .observe(HomeContentFragment.this.requireActivity(), topicData -> {
                    if (topicData.isEmpty()) {
                        return;
                    }

                    TopicData topic = topicData.get(position);
                    binding.titleHomeTextView.setText(topic.getTitle());
                    binding.descriptionHomeTextView.setText(topic.getDescription());
                    getMainImage(topic.getSlug());

                    binding.btnNextPage.setOnClickListener(v -> goToNextPage(topic));
                    binding.btnPreviousPage.setOnClickListener(v -> goToPreviousPage(topic));
                    binding.swipeRefreshHomeContent.setOnRefreshListener(() -> refreshPages(topic));
                });
    }

    private void refreshPages(TopicData topic) {
        page = 1;
        scrollToTop();

        photos.clear();
        fetchPhotos(topic.getSlug());
        binding.swipeRefreshHomeContent.setRefreshing(false);
    }

    private void goToPreviousPage(TopicData topic) {
        if (page == 1) {
            Toast.makeText(HomeContentFragment.this.requireActivity(),
                    "This is first page",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        scrollToTop();
        page -= 1;
        fetchPhotos(topic.getSlug());
    }

    private void goToNextPage(TopicData topic) {
        page += 1;
        scrollToTop();
        fetchPhotos(topic.getSlug());
    }

    private void scrollToTop() {
        String currentPage = "Page: " + page;
        binding.textViewPage.setText(currentPage);
        binding.homeContent.fullScroll(View.FOCUS_DOWN);
        binding.homeContent.fullScroll(View.FOCUS_UP);
    }

    void getMainImage(String topicSlug) {
        photoViewModel
                .getTopic(topicSlug)
                .observe(HomeContentFragment.this.requireActivity(), topic -> {
                    Log.d(TAG, "getMainImage: " + topic);
                    if (topic == null) {
                        return;
                    }

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
                .getTopicsPhotos(topicSlug, page, appPreferenceViewModel.getPerPage())
                .observe(HomeContentFragment.this.requireActivity(), photoResponse -> {
                    binding.swipeRefreshHomeContent.setRefreshing(true);
                    if (photoResponse == null) {
                        dialogUtils.showAlertDialog(HomeContentFragment.this.requireActivity());
                        binding.relativeLayoutNavigationBtn.setVisibility(View.INVISIBLE);
                        binding.swipeRefreshHomeContent.setRefreshing(false);
                        return;
                    }

                    photos.clear();
                    photos.addAll(photoResponse);
                    photosAdapter.notifyDataSetChanged();
                    binding.swipeRefreshHomeContent.setRefreshing(false);
                });
    }

    private void initViewModels() {
        photoViewModel = new ViewModelProvider(this)
                .get(PhotoViewModel.class);

        topicDatabaseViewModel = new ViewModelProvider(this)
                .get(TopicDatabaseViewModel.class);

        appPreferenceViewModel = new ViewModelProvider(this)
                .get(AppPreferenceViewModel.class);
    }

    private void initRecyclerView() {
        photosAdapter = new PhotosAdapter(HomeContentFragment.this.requireActivity(), photos);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), appPreferenceViewModel.getGridPhotos());
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
