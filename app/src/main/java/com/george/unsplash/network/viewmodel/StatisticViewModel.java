package com.george.unsplash.network.viewmodel;

import static com.george.unsplash.utils.Keys.USER_PREFERENCES;
import static com.george.unsplash.utils.Keys.USER_TOKEN;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.george.unsplash.network.models.Statistic.Statistic;
import com.george.unsplash.network.repository.StatisticRepository;

public class StatisticViewModel extends AndroidViewModel {

    StatisticRepository statisticRepository;
    SharedPreferences sharedPreferences;

    public StatisticViewModel(@NonNull Application application) {
        super(application);


        sharedPreferences = application.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(USER_TOKEN, "");
        statisticRepository = new StatisticRepository(token);
    }

    public MutableLiveData<Statistic> getStatistic(String username) {
        return loadStatistic(username);
    }

    private MutableLiveData<Statistic> loadStatistic(String username) {
        return statisticRepository.getStatistic(username);
    }
}
