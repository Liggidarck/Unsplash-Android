package com.george.unsplash.localdata.topic;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "topic_table")
public class TopicData {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String title;
    private final String description;
    private final String slug;
    private final int totalPhotos;

    public TopicData(String title, String description, int totalPhotos, String slug) {
        this.title = title;
        this.description = description;
        this.totalPhotos = totalPhotos;
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
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
