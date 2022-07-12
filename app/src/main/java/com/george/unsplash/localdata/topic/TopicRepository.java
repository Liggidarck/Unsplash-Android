package com.george.unsplash.localdata.topic;

import android.app.Application;

import androidx.lifecycle.LiveData;

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

    public void update(TopicData topicData) {
        service.execute(() -> topicDao.update(topicData));
    }

    public LiveData<List<TopicData>> getAllTopics() {
        return allTopics;
    }

}
