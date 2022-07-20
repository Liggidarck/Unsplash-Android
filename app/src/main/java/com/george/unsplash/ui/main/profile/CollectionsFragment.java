package com.george.unsplash.ui.main.profile;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.george.unsplash.R;
import com.george.unsplash.databinding.UserCollectionFragmentBinding;
import com.george.unsplash.network.models.collection.CollectionPhotos;
import com.george.unsplash.network.viewmodel.CollectionViewModel;
import com.george.unsplash.ui.adapters.CollectionsAdapter;
import com.george.unsplash.ui.main.collections.CollectionActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CollectionsFragment extends Fragment {

    UserCollectionFragmentBinding binding;

    CollectionViewModel collectionViewModel;
    CollectionsAdapter collectionsAdapter;
    List<CollectionPhotos> collectionPhotosList;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    String username;
    boolean isUser;
    int page = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        assert args != null;
        username = args.getString("username");
        isUser = args.getBoolean("isUser", true);

        collectionViewModel = new ViewModelProvider(this)
                .get(CollectionViewModel.class);

        collectionPhotosList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = UserCollectionFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(isUser) {
            binding.addCollectionBtn.setVisibility(View.VISIBLE);
            binding.addCollectionBtn.setOnClickListener(v -> showDialogCreateNewCollection());
        }

        binding.toolbarCollections.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        initRecyclerView();
        getCollections();

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCollections() {
        collectionViewModel
                .getCollections(username, page)
                .observe(CollectionsFragment.this.requireActivity(), collectionPhotos -> {
                    collectionPhotosList.addAll(collectionPhotos);
                    collectionsAdapter.notifyDataSetChanged();
                });
        page++;
    }

    void initRecyclerView() {
        collectionsAdapter = new CollectionsAdapter(CollectionsFragment.this.requireActivity(), collectionPhotosList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CollectionsFragment.this.requireActivity());
        binding.collectionsRecyclerView.setLayoutManager(layoutManager);
        binding.collectionsRecyclerView.setHasFixedSize(false);
        binding.collectionsRecyclerView.setAdapter(collectionsAdapter);

        collectionsAdapter.setOnItemClickListener(collectionPhotos -> {
            Intent intent = new Intent(CollectionsFragment.this.requireActivity(), CollectionActivity.class);
            intent.putExtra("collectionId", collectionPhotos.getId());
            intent.putExtra("collectionTitle", collectionPhotos.getTitle());
            startActivity(intent);
        });

        if(isUser) {
            collectionsAdapter.setOnLongClickListener(collectionPhotos ->
                    showDialogEditCollection(collectionPhotos.getId(), collectionPhotos.getTitle(),
                            collectionPhotos.getDescription(), collectionPhotos.isPrivateCollection()));
        }

        binding.collectionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            getCollections();
                            loading = true;
                        }
                    }
                }

            }
        });
    }


    private void showDialogEditCollection(String id, String title, String description, boolean isPrivate) {
        Dialog dialog = new Dialog(CollectionsFragment.this.requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_edit_collection);

        ImageView closeBtn = dialog.findViewById(R.id.closeBtn);
        TextInputLayout nameCollectionInput = dialog.findViewById(R.id.nameCollectionInputLayout);
        TextInputLayout descriptionCollectionInput = dialog.findViewById(R.id.descriptionCollectionInputLayout);
        CheckBox isPrivateChekBox = dialog.findViewById(R.id.privateCollectionCheckBox);
        Button save = dialog.findViewById(R.id.saveCollectionBtn);
        Button deleteCollectionBtn = dialog.findViewById(R.id.deleteCollectionBtn);
        ProgressBar progressBarCollection = dialog.findViewById(R.id.progressBarCollection);

        requireNonNull(nameCollectionInput.getEditText()).setText(title);
        requireNonNull(descriptionCollectionInput.getEditText()).setText(description);
        isPrivateChekBox.setChecked(isPrivate);

        save.setOnClickListener(v -> {
            progressBarCollection.setVisibility(View.VISIBLE);
            String updateTitle = nameCollectionInput.getEditText().getText().toString();
            String updateDescription = descriptionCollectionInput.getEditText().getText().toString();
            boolean updateIsPrivate = isPrivateChekBox.isChecked();

            if (updateTitle.isEmpty()) {
                nameCollectionInput.setError("Empty!");
                return;
            }

            collectionViewModel
                    .updateCollection(id, updateTitle, updateDescription, updateIsPrivate)
                    .observe(CollectionsFragment.this.requireActivity(),
                            collectionPhotos -> dialog.dismiss());

        });

        deleteCollectionBtn.setOnClickListener(v ->
                collectionViewModel
                        .deleteCollection(id)
                        .observe(CollectionsFragment.this.requireActivity(),
                                collectionPhotos -> dialog.dismiss()));

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDialogCreateNewCollection() {
        Dialog dialog = new Dialog(CollectionsFragment.this.requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_edit_collection);

        ImageView closeBtn = dialog.findViewById(R.id.closeBtn);
        TextInputLayout nameCollectionInput = dialog.findViewById(R.id.nameCollectionInputLayout);
        TextInputLayout descriptionCollectionInput = dialog.findViewById(R.id.descriptionCollectionInputLayout);
        CheckBox isPrivateChekBox = dialog.findViewById(R.id.privateCollectionCheckBox);
        Button save = dialog.findViewById(R.id.saveCollectionBtn);
        Button deleteCollectionBtn = dialog.findViewById(R.id.deleteCollectionBtn);
        ProgressBar progressBarCollection = dialog.findViewById(R.id.progressBarCollection);

        deleteCollectionBtn.setVisibility(View.INVISIBLE);

        closeBtn.setOnClickListener(v -> dialog.dismiss());
        save.setOnClickListener(v -> {
            progressBarCollection.setVisibility(View.VISIBLE);
            String nameCollection = requireNonNull(nameCollectionInput.getEditText()).getText().toString();
            String descriptionCollection = requireNonNull(descriptionCollectionInput.getEditText()).getText().toString();
            boolean isPrivate = isPrivateChekBox.isChecked();

            if (nameCollection.isEmpty()) {
                nameCollectionInput.setError("Empty!");
                return;
            }

            collectionViewModel
                    .createNewCollection(nameCollection, descriptionCollection, isPrivate)
                    .observe(CollectionsFragment.this.requireActivity(), collectionPhotos -> dialog.dismiss());

        });
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
