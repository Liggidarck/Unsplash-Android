package com.george.unsplash.ui.adapters;

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
import com.george.unsplash.network.models.collection.CollectionPhotos;

import java.util.List;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.ViewHolder> {

    private final Context context;
    private final List<CollectionPhotos> collectionPhotosList;
    private onItemClickListener listener;

    public CollectionsAdapter(Context context, List<CollectionPhotos> collectionPhotosList) {
        this.context = context;
        this.collectionPhotosList = collectionPhotosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CollectionPhotos collectionPhotos = collectionPhotosList.get(position);
        String countPhotos = "Photos: " + collectionPhotos.getTotalPhotos();
        boolean isPrivateCollection = collectionPhotos.isPrivateCollection();

        holder.titleCollection.setText(collectionPhotos.getTitle());
        holder.countPhotosCollection.setText(countPhotos);
        holder.privateCollectionImageView.setVisibility(isPrivateCollection ? View.VISIBLE : View.INVISIBLE);

        try {
            String regularUrl = collectionPhotos.getCoverPhoto().getUrls().getRegular();

            if(regularUrl != null) {
                Glide.with(context)
                        .load(regularUrl)
                        .into(holder.coverPhoto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return collectionPhotosList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleCollection;
        TextView countPhotosCollection;
        ImageView privateCollectionImageView;
        ImageView coverPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleCollection = itemView.findViewById(R.id.titleCollection);
            countPhotosCollection = itemView.findViewById(R.id.countPhotosCollection);
            privateCollectionImageView = itemView.findViewById(R.id.privateCollectionImageView);
            coverPhoto = itemView.findViewById(R.id.coverPhoto);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(collectionPhotosList.get(position));
                }
            });

            itemView.setOnLongClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(collectionPhotosList.get(position));
                    return true;
                }
                return false;
            });

        }
    }

    public interface onItemClickListener {
        void onItemClick(CollectionPhotos collectionPhotos);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

}
