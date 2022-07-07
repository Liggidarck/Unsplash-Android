package com.george.unsplash.localdata.topic;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "topic_table")
public class TopicData {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String title;
    private final String description;
    private final int totalPhotos;

    public TopicData(String title, String description, int totalPhotos) {
        this.title = title;
        this.description = description;
        this.totalPhotos = totalPhotos;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalPhotos() {
        return totalPhotos;
    }
}
