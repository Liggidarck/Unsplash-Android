package com.george.unsplash.ui.main.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.george.unsplash.R;
import com.george.unsplash.databinding.SearchFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.search.Search;
import com.george.unsplash.ui.adapters.PhotosAdapter;
import com.george.unsplash.ui.main.photos.PhotoViewModel;
import com.george.unsplash.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private SearchFragmentBinding binding;

    private UnsplashInterface unsplashInterface;
    private List<Photo> photos;
    private PhotosAdapter photosAdapter;

    private PhotoViewModel photoViewModel;

    private final Utils utils = new Utils();

    public static final String TAG = SearchFragment.class.getSimpleName();
    private int page = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoViewModel = new ViewModelProvider(this)
                .get(PhotoViewModel.class);

        photos = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        AppPreferences appPreferences = new AppPreferences(SearchFragment.this.requireActivity());
        String token = appPreferences.getToken();
        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);

        initFragmentViews();

        binding.searchBtn.setOnClickListener(v -> search());


        binding.searchContent.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1)
                                .getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {
                            startSearch();
                        }
                    }
                });

        return root;
    }

    private void startSearch() {
        String color = binding.colorQueryAutoComplete.getText().toString();
        String orientation = binding.orientationQueryAutoComplete.getText().toString();

        if (color.isEmpty() | color.equals("Any"))
            color = null;

        if (orientation.isEmpty() | orientation.equals("Any"))
            orientation = null;

        fetchPhotos(Objects.requireNonNull(binding.searchQueryTextLayout.getEditText()).getText().toString(),
                color, orientation);
    }

    private void search() {
        String searchQuery = Objects.requireNonNull(binding.searchQueryTextLayout.getEditText()).getText().toString();

        if (searchQuery.isEmpty()) {
            binding.searchQueryTextLayout.setError("This field cannot be empty");
            return;
        }

        startSearch();
    }

    void fetchPhotos(String searchQuery, String color, String orientation) {
        binding.progressBarSearch.setVisibility(View.VISIBLE);
        unsplashInterface
                .findPhotos(searchQuery, page, color, orientation)
                .enqueue(new Callback<Search>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<Search> call,
                                           @NonNull Response<Search> response) {
                        Log.d(TAG, "onResponse: " + response.code());
                        if (response.code() == 200) {
                            Search search = response.body();
                            assert search != null;
                            int totalPhotos = search.getTotal();
                            photos.addAll(search.getResults());
                            photosAdapter.notifyDataSetChanged();

                            String findResultsText = "Images found: " + totalPhotos;
                            binding.pages.setText(findResultsText);
                        } else
                            utils.showAlertDialog(SearchFragment.this.requireActivity(),
                                    response.code());

                    }

                    @Override
                    public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                    }
                });

        page += 1;
    }

    private void initFragmentViews() {
        initRecyclerView();
        initSearchTextLayout();

        String[] colors = getResources().getStringArray(R.array.searchColors);
        ArrayAdapter<String> ColorsAdapter = new ArrayAdapter<>(
                SearchFragment.this.requireActivity(),
                R.layout.dropdown_menu_categories,
                colors
        );

        binding.colorQueryAutoComplete.setAdapter(ColorsAdapter);

        String[] orientations = getResources().getStringArray(R.array.searchOrientations);
        ArrayAdapter<String> orientationsAdapter = new ArrayAdapter<>(
                SearchFragment.this.requireActivity(),
                R.layout.dropdown_menu_categories,
                orientations
        );

        binding.orientationQueryAutoComplete.setAdapter(orientationsAdapter);
    }

    private void initSearchTextLayout() {
        Objects.requireNonNull(binding.searchQueryTextLayout.getEditText())
                .setOnEditorActionListener((textView, actionId, keyEvent) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        search();
                        return true;
                    }
                    return false;
                });

        Objects.requireNonNull(binding.searchQueryTextLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.searchQueryTextLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void afterTextChanged(Editable editable) {
                photos.clear();
                binding.progressBarSearch.setVisibility(View.INVISIBLE);
                binding.pages.setText(null);
                photosAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecyclerView() {
        photosAdapter = new PhotosAdapter(SearchFragment.this.requireActivity(), photos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.searchRecyclerView.setLayoutManager(gridLayoutManager);
        binding.searchRecyclerView.setHasFixedSize(true);
        binding.searchRecyclerView.setAdapter(photosAdapter);

        photosAdapter.setOnItemClickListener((photo, position) -> photoViewModel
                .showFullScreenImage(photo, SearchFragment.this.requireActivity()));
    }

}
