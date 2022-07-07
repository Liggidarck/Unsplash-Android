package com.george.unsplash.localdata.topic;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TopicDao {

    @Insert
    void insert(TopicData topicData);

    @Update
    void update(TopicData topicData);

    @Query("SELECT * FROM topic_table")
    LiveData<List<TopicData>> getTopics();

}
