package com.george.unsplash.ui.main.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.george.unsplash.R;
import com.george.unsplash.databinding.CollectionsProfileFragmentBinding;
import com.george.unsplash.network.models.collection.CollectionPhotos;
import com.george.unsplash.network.viewmodel.CollectionViewModel;
import com.george.unsplash.ui.adapters.CollectionsAdapter;
import com.george.unsplash.ui.main.collections.CollectionActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionsProfileFragment extends Fragment {

    private CollectionsProfileFragmentBinding binding;

    private List<CollectionPhotos> collectionPhotosList;
    private CollectionsAdapter collectionsAdapter;

    private CollectionViewModel collectionViewModel;

    private String username;
    private boolean isUser;
    public static final String TAG = CollectionsProfileFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        assert args != null;
        username = args.getString("username");
        isUser = args.getBoolean("isUser");

        collectionViewModel = new ViewModelProvider(this)
                .get(CollectionViewModel.class);

        collectionPhotosList = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CollectionsProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initRecyclerView();

        getCollections();

        binding.createCollectionBtn.setVisibility(View.INVISIBLE);
        if (isUser) {
            binding.createCollectionBtn.setVisibility(View.VISIBLE);
            binding.createCollectionBtn.setOnClickListener(v -> showDialogCreateNewCollection());
        }

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCollections() {
        collectionViewModel
                .getCollections(username, 1, 20)
                .observe(CollectionsProfileFragment.this.requireActivity(), collectionPhotos -> {
                    collectionPhotosList.addAll(collectionPhotos);
                    collectionsAdapter.notifyDataSetChanged();
                });
    }

    void initRecyclerView() {
        collectionsAdapter = new CollectionsAdapter(CollectionsProfileFragment.this.requireActivity(), collectionPhotosList);
        binding.collectionUserProfile.setLayoutManager(new LinearLayoutManager(CollectionsProfileFragment.this.requireActivity()));
        binding.collectionUserProfile.setHasFixedSize(false);
        binding.collectionUserProfile.setAdapter(collectionsAdapter);

        collectionsAdapter.setOnItemClickListener(collectionPhotos -> {
            Log.d(TAG, "initRecyclerView: " + collectionPhotos.getId());
            Intent intent = new Intent(CollectionsProfileFragment.this.requireActivity(), CollectionActivity.class);
            intent.putExtra("collectionId", collectionPhotos.getId());
            intent.putExtra("collectionTitle", collectionPhotos.getTitle());
            startActivity(intent);
        });

        if (isUser) {
            collectionsAdapter.setOnLongClickListener(collectionPhotos ->
                    showDialogEditCollection(collectionPhotos.getId(), collectionPhotos.getTitle(),
                            collectionPhotos.getDescription(), collectionPhotos.isPrivateCollection()));
        }
    }

    private void showDialogEditCollection(String id, String title, String description, boolean isPrivate) {
        Dialog dialog = new Dialog(CollectionsProfileFragment.this.requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_edit_collection);

        ImageView closeBtn = dialog.findViewById(R.id.closeBtn);
        TextInputLayout nameCollectionInput = dialog.findViewById(R.id.nameCollectionInputLayout);
        TextInputLayout descriptionCollectionInput = dialog.findViewById(R.id.descriptionCollectionInputLayout);
        CheckBox isPrivateChekBox = dialog.findViewById(R.id.privateCollectionCheckBox);
        Button save = dialog.findViewById(R.id.saveCollectionBtn);
        Button deleteCollectionBtn = dialog.findViewById(R.id.deleteCollectionBtn);
        ProgressBar progressBarCollection = dialog.findViewById(R.id.progressBarCollection);

        Objects.requireNonNull(nameCollectionInput.getEditText()).setText(title);
        Objects.requireNonNull(descriptionCollectionInput.getEditText()).setText(description);
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
                    .observe(CollectionsProfileFragment.this.requireActivity(),
                            collectionPhotos -> dialog.dismiss());

        });

        deleteCollectionBtn.setOnClickListener(v ->
                collectionViewModel
                        .deleteCollection(id)
                        .observe(CollectionsProfileFragment.this.requireActivity(),
                                collectionPhotos -> dialog.dismiss()));

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDialogCreateNewCollection() {
        Dialog dialog = new Dialog(CollectionsProfileFragment.this.requireActivity());
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
            String nameCollection = Objects.requireNonNull(nameCollectionInput.getEditText()).getText().toString();
            String descriptionCollection = Objects.requireNonNull(descriptionCollectionInput.getEditText()).getText().toString();
            boolean isPrivate = isPrivateChekBox.isChecked();

            if (nameCollection.isEmpty()) {
                nameCollectionInput.setError("Empty!");
                return;
            }

            collectionViewModel
                    .createNewCollection(nameCollection, descriptionCollection, isPrivate)
                    .observe(CollectionsProfileFragment.this.requireActivity(), collectionPhotos -> dialog.dismiss());

        });
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
