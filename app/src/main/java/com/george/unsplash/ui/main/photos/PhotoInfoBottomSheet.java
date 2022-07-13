package com.george.unsplash.ui.main.photos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.george.unsplash.databinding.PhotoInfoBottomSheetBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.api.UnsplashInterface;
import com.george.unsplash.network.api.UnsplashTokenClient;
import com.george.unsplash.network.models.photo.Exif;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoInfoBottomSheet extends BottomSheetDialogFragment {

    private PhotoInfoBottomSheetBinding binding;
    private AppPreferences appPreferences;

    private UnsplashInterface unsplashInterface;

    private final Utils utils = new Utils();

    public static final String TAG = PhotoInfoBottomSheet.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PhotoInfoBottomSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        appPreferences = new AppPreferences(PhotoInfoBottomSheet.this.requireActivity());

        Bundle args = getArguments();
        assert args != null;

        String photoId = args.getString("photoId");
        String token = appPreferences.getToken();

        unsplashInterface = UnsplashTokenClient.getUnsplashTokenClient(token).create(UnsplashInterface.class);
        unsplashInterface
                .getPhoto(photoId)
                .enqueue(new Callback<Photo>() {
                    @Override
                    public void onResponse(@NonNull Call<Photo> call, @NonNull Response<Photo> response) {
                        if(response.code() == 200) {
                            Photo photo = response.body();
                            assert photo != null;
                            String fullName = photo.getUser().getFirstName() + " " + photo.getUser().getFirstName();
                            String username = "@" + photo.getUser().getUsername();
                            String likes = "Likes: " + photo.getLikes();
                            String downloads = "Downloads: " + photo.getDownloads();
                            String resolution = photo.getWidth() + " x " + photo.getHeight();

                            Exif exif = photo.getExif();
                            if(exif.getAperture() != null) {
                                String camera = exif.getName();
                                String lens = exif.getFocalLength() + "mm" + " f/" + exif.getAperture();
                                String exposureTime = exif.getExposureTime() + "s";
                                String iso = "ISO " + exif.getIso();

                                binding.cameraTextViewInfo.setText(camera);
                                binding.lensTextViewInfo.setText(lens);
                                binding.exposeTimeTextViewInfo.setText(exposureTime);
                                binding.isoTextViewInfo.setText(iso);
                            } else {
                                binding.cameraTitle.setVisibility(View.INVISIBLE);
                                binding.lensTitle.setVisibility(View.INVISIBLE);
                                binding.cameraTextViewInfo.setVisibility(View.INVISIBLE);
                                binding.lensTextViewInfo.setVisibility(View.INVISIBLE);
                                binding.exposeTimeTextViewInfo.setVisibility(View.INVISIBLE);
                                binding.isoTextViewInfo.setVisibility(View.INVISIBLE);
                            }

                            binding.fullNameTextViewInfo.setText(fullName);
                            binding.usernameTextViewInfo.setText(username);
                            binding.likesTextViewInfo.setText(likes);
                            binding.downloadsTextViewInfo.setText(downloads);
                            binding.resolutionTextViewInfo.setText(resolution);

                            binding.progressBarInformation.setVisibility(View.INVISIBLE);
                        } else {
                            utils.showAlertDialog(PhotoInfoBottomSheet.this.requireActivity(), response.code());
                            binding.progressBarInformation.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Photo> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });

        binding.closeBtn.setOnClickListener(v -> dismiss());

        return view;
    }
}
