package com.george.unsplash.network.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.anychart.chart.common.dataentry.DataEntry;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.models.Statistic.Statistic;
import com.george.unsplash.network.repository.StatisticRepository;

import java.util.ArrayList;
import java.util.List;

public class StatisticViewModel extends AndroidViewModel {

    StatisticRepository statisticRepository;
    AppPreferences appPreferences;

    private MutableLiveData<Statistic> statistic = new MutableLiveData<>();

    public StatisticViewModel(@NonNull Application application) {
        super(application);

        appPreferences = new AppPreferences(application);
        String token = appPreferences.getToken();
        statisticRepository = new StatisticRepository(token);
    }

    public MutableLiveData<Statistic> getStatistic(String username) {
        statistic = loadStatistic(username);
        return statistic;
    }

    private MutableLiveData<Statistic> loadStatistic(String username) {
        return statisticRepository.getStatistic(username);
    }
}
