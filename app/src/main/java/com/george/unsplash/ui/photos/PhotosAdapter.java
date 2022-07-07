package com.george.unsplash.ui.photos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.network.models.photo.Photo;
import com.george.unsplash.network.models.user.common.User;

import java.util.ArrayList;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    final List<Photo> photoList = new ArrayList<>();
    private OnPhotoClickedListener listener;
    Context context;

    public PhotosAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Photo photo = photoList.get(position);
        User user = photo.getUser();

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        if(lastName == null)
            lastName = "";
        String username = firstName + " " + lastName;

        holder.profileName.setText(username);

        Glide.with(context)
                .load(photo.getUrls().getRegular())
                .into(holder.imageViewPhoto);

        Glide.with(context)
                .load(user.getProfileImage().getLarge())
                .into(holder.profilePicPhoto);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addPhotos(List<Photo> photos) {
        photoList.addAll(photos);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        photoList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPhoto;
        ImageView profilePicPhoto;
        TextView profileName, textLikePhoto;

        public ViewHolder(View view) {
            super(view);
            imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
            profilePicPhoto = view.findViewById(R.id.profilePicPhoto);
            profileName = view.findViewById(R.id.profileNamePhoto);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.photoClicked(photoList.get(position), position);
                }
            });

        }
    }

    public interface OnPhotoClickedListener {
        void photoClicked(Photo photo, int position);
    }

    public void setOnItemClickListener(OnPhotoClickedListener listener) {
        this.listener = listener;
    }

}
