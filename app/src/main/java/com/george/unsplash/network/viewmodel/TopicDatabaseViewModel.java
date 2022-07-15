package com.george.unsplash.network.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.george.unsplash.localdata.topic.TopicData;
import com.george.unsplash.localdata.topic.TopicRepository;

import java.util.List;

public class TopicDatabaseViewModel extends AndroidViewModel {

    final TopicRepository repository;
    final LiveData<List<TopicData>> allTopics;

    public TopicDatabaseViewModel(@NonNull Application application) {
        super(application);
        repository = new TopicRepository(application);
        allTopics = repository.getAllTopics();
    }

    public void insert(TopicData topicData) {
        repository.insert(topicData);
    }

    public void update(TopicData topicData) {
        repository.update(topicData);
    }

    public LiveData<List<TopicData>> getAllTopics() {
        return allTopics;
    }

}