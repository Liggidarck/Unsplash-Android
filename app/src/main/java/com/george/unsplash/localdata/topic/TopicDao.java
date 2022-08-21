package com.george.unsplash.localdata.topic;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TopicDao {

    @Insert
    void insert(TopicData topicData);

    @Query("DELETE FROM topic_table")
    void clear();

    @Query("SELECT * FROM topic_table")
    LiveData<List<TopicData>> getTopics();

}
