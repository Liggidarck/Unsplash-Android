package com.george.unsplash.ui.main.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.ui.adapters.PhotosAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private SearchFragmentBinding binding;

    private List<Photo> photos;
    private PhotosAdapter photosAdapter;

    private PhotoViewModel photoViewModel;

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

    @SuppressLint("NotifyDataSetChanged")
    void fetchPhotos(String searchQuery, String color, String orientation) {
        binding.progressBarSearch.setVisibility(View.VISIBLE);
        photoViewModel
                .findPhotos(searchQuery, page, color, orientation)
                .observe(SearchFragment.this.requireActivity(), search -> {
                    int totalPhotos = search.getTotal();
                    photos.addAll(search.getResults());
                    photosAdapter.notifyDataSetChanged();

                    String findResultsText = "Images found: " + totalPhotos;
                    binding.pages.setText(findResultsText);
                    binding.progressBarSearch.setVisibility(View.INVISIBLE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
