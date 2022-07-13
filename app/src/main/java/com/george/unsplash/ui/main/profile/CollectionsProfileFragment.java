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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.george.unsplash.R;
import com.george.unsplash.databinding.CollectionsProfileFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.collection.CollectionPhotos;
import com.george.unsplash.ui.adapters.CollectionsAdapter;
import com.george.unsplash.ui.main.collections.CollectionActivity;
import com.george.unsplash.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionsProfileFragment extends Fragment {

    private CollectionsProfileFragmentBinding binding;

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

        AppPreferences appPreferences = new AppPreferences(CollectionsProfileFragment.this.requireActivity());
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

        binding.createCollectionFragBtn.setOnClickListener(v -> showDialogCreateNewCollection());
        return root;
    }

    private void getCollections() {
        unsplashInterface.getUserCollection(username, 1, 50).enqueue(new Callback<List<CollectionPhotos>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<CollectionPhotos>> call, @NonNull Response<List<CollectionPhotos>> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    collectionPhotosList.addAll(response.body());
                    collectionsAdapter.notifyDataSetChanged();
                } else {
                    utils.showAlertDialog(CollectionsProfileFragment.this.requireActivity(), response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CollectionPhotos>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
            }
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
            startActivity(intent);
        });

        collectionsAdapter.setOnLongClickListener(collectionPhotos ->
                showDialogEditCollection(collectionPhotos.getId(), collectionPhotos.getTitle(),
                        collectionPhotos.getDescription(), collectionPhotos.isPrivateCollection()));
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

            unsplashInterface.updateCollection(id, updateTitle, updateDescription, updateIsPrivate)
                    .enqueue(new Callback<CollectionPhotos>() {
                        @Override
                        public void onResponse(@NonNull Call<CollectionPhotos> call,
                                               @NonNull Response<CollectionPhotos> response) {
                            if (response.code() == 200) {
                                dialog.dismiss();
                            } else {
                                utils.showAlertDialog(CollectionsProfileFragment.this.requireActivity(),
                                        response.code());
                            }
                            progressBarCollection.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onFailure(@NonNull Call<CollectionPhotos> call,
                                              @NonNull Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                        }
                    });
        });

        deleteCollectionBtn.setOnClickListener(v -> unsplashInterface.deleteCollection(id)
                        .enqueue(new Callback<CollectionPhotos>() {
                            @Override
                            public void onResponse(@NonNull Call<CollectionPhotos> call, @NonNull Response<CollectionPhotos> response) {
                                if (response.code() == 204) {
                                    dialog.dismiss();
                                } else {
                                    utils.showAlertDialog(CollectionsProfileFragment.this.requireActivity(),
                                            response.code());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<CollectionPhotos> call, @NonNull Throwable t) {
                                Log.e(TAG, "onFailure: ", t);
                            }
                        }));

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

            unsplashInterface.createNewCollection(nameCollection, descriptionCollection, isPrivate)
                    .enqueue(new Callback<CollectionPhotos>() {
                        @Override
                        public void onResponse(@NonNull Call<CollectionPhotos> call, @NonNull Response<CollectionPhotos> response) {
                            if (response.code() == 201) {
                                dialog.dismiss();
                            } else {
                                utils.showAlertDialog(CollectionsProfileFragment.this.requireActivity(),
                                        response.code());
                            }
                            progressBarCollection.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onFailure(@NonNull Call<CollectionPhotos> call, @NonNull Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                        }
                    });
        });
        dialog.show();
    }
}