package com.george.unsplash.ui.main.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.george.unsplash.databinding.CollectionsProfileFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.collection.CollectionPhotos;
import com.george.unsplash.ui.adapters.CollectionsAdapter;
import com.george.unsplash.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionsProfileFragment extends Fragment {

    private CollectionsProfileFragmentBinding binding;

    private AppPreferences appPreferences;

    private UnsplashInterface unsplashInterface;
    private List<CollectionPhotos> collectionPhotosList;
    private CollectionsAdapter collectionsAdapter;

    Utils utils = new Utils();

    private String username;

    public static final String TAG = CollectionsProfileFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        assert args != null;
        username = args.getString("username");

        appPreferences = new AppPreferences(CollectionsProfileFragment.this.requireActivity());
        String token = appPreferences.getToken();

        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);

        collectionPhotosList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CollectionsProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initRecyclerView();

        getCollections();

        return root;
    }

    private void getCollections() {
        unsplashInterface.getUserCollection(username, 1, 50).enqueue(new Callback<List<CollectionPhotos>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<CollectionPhotos>> call, @NonNull Response<List<CollectionPhotos>> response) {
                if(response.code() == 200) {
                    assert response.body() != null;
                    collectionPhotosList.addAll(response.body());
                    collectionsAdapter.notifyDataSetChanged();
                } else {
                    utils.showAlertDialog(CollectionsProfileFragment.this.requireActivity(), response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CollectionPhotos>> call, @NonNull Throwable t) {

            }
        });
    }

    void initRecyclerView() {
        collectionsAdapter = new CollectionsAdapter(CollectionsProfileFragment.this.requireActivity(), collectionPhotosList);
        binding.collectionUserProfile.setLayoutManager(new LinearLayoutManager(CollectionsProfileFragment.this.requireActivity()));
        binding.collectionUserProfile.setHasFixedSize(false);
        binding.collectionUserProfile.setAdapter(collectionsAdapter);
    }

}
