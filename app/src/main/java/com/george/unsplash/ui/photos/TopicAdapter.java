package com.george.unsplash.ui.photos;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.george.unsplash.R;
import com.george.unsplash.localdata.topic.TopicData;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    private final List<TopicData> topics = new ArrayList<>();
    onItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopicData topic = topics.get(position);
        holder.titleTopic.setText(topic.getTitle());
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addTopics(List<TopicData> topics) {
        this.topics.addAll(topics);
        notifyDataSetChanged();
    }

    public TopicData getTopicAt(int position) {
        return topics.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTopic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTopic = itemView.findViewById(R.id.titleTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(topics.get(position), position);
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(TopicData topic, int position);
    }

    public void setOnClickItemListener(onItemClickListener listener) {
        this.listener = listener;
    }

}
