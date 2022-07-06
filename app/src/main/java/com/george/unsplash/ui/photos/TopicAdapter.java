package com.george.unsplash.ui.photos;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.george.unsplash.R;
import com.george.unsplash.network.models.topic.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    private final List<Topic> topics = new ArrayList<>();
    onItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.titleTopic.setText(topic.getTitle());
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addTopics(List<Topic> topics) {
        this.topics.addAll(topics);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTopic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTopic = itemView.findViewById(R.id.titleTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(topics.get(position));
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(Topic topic);
    }

    public void setOnClickItemListener(onItemClickListener listener) {
        this.listener = listener;
    }

}
