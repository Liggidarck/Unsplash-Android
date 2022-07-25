package com.george.unsplash.ui.main.photos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.george.unsplash.databinding.PhotoInfoBottomSheetBinding;
import com.george.unsplash.network.models.photo.Exif;
import com.george.unsplash.network.viewmodel.PhotoViewModel;
import com.george.unsplash.utils.NetworkUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PhotoInfoBottomSheet extends BottomSheetDialogFragment {

    private PhotoInfoBottomSheetBinding binding;
    PhotoViewModel photoViewModel;

    private final NetworkUtils networkUtils = new NetworkUtils();

    public static final String TAG = PhotoInfoBottomSheet.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PhotoInfoBottomSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        Bundle args = getArguments();
        assert args != null;

        String photoId = args.getString("photoId");

        photoViewModel
                .getPhoto(photoId)
                .observe(PhotoInfoBottomSheet.this.requireActivity(), photo -> {
                    String fullName = photo.getUser().getFirstName() + " " + photo.getUser().getLastName();
                    String username = "@" + photo.getUser().getUsername();
                    String likes = "Likes: " + photo.getLikes();
                    String downloads = "Downloads: " + photo.getDownloads();
                    String resolution = photo.getWidth() + " x " + photo.getHeight();

                    Exif exif = photo.getExif();
                    if (exif.getAperture() != null) {
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

                });

        binding.closeBtn.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
