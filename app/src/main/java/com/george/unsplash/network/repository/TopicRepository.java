package com.george.unsplash.network.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.george.unsplash.localdata.topic.TopicDao;
import com.george.unsplash.localdata.topic.TopicData;
import com.george.unsplash.localdata.topic.TopicDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TopicRepository {

    final TopicDao topicDao;
    final LiveData<List<TopicData>> allTopics;
    final ExecutorService service = Executors.newSingleThreadExecutor();

    public TopicRepository(Application application) {
        TopicDatabase database = TopicDatabase.getInstance(application);
        topicDao = database.topicDao();
        allTopics = topicDao.getTopics();
    }

    public void insert(TopicData topicData) {
        service.execute(() -> topicDao.insert(topicData));
    }

    public void clear() {
        service.execute(topicDao::clear);
    }

    public LiveData<List<TopicData>> getAllTopics() {
        return allTopics;
    }

}
