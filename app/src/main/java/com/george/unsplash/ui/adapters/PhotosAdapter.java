package com.george.unsplash.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.george.unsplash.R;
import com.george.unsplash.network.models.photo.Photo;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private final List<Photo> photoList;
    private OnPhotoClickedListener listener;
    final Context context;

    public PhotosAdapter(Context context, List<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Photo photo = photoList.get(position);

        Glide.with(context)
                .load(photo.getUrls().getRegular())
                .placeholder(R.color.gray)
                .into(holder.imageViewPhoto);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void clear() {
        photoList.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageViewPhoto;

        public ViewHolder(View view) {
            super(view);
            imageViewPhoto = view.findViewById(R.id.imageViewPhoto);

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
