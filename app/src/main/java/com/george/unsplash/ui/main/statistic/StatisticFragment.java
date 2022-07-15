package com.george.unsplash.ui.main.statistic;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.george.unsplash.databinding.StatisticFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.models.Statistic.Statistic;
import com.george.unsplash.network.models.Statistic.Values;
import com.george.unsplash.network.viewmodel.StatisticViewModel;

import java.time.LocalDate;
import java.util.List;

public class StatisticFragment extends Fragment {

    private StatisticFragmentBinding binding;

    StatisticViewModel statisticViewModel;

    AppPreferences appPreferences;

    public static final String TAG = Statistic.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = StatisticFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        appPreferences = new AppPreferences(StatisticFragment.this.requireActivity());
        statisticViewModel = new ViewModelProvider(this).get(StatisticViewModel.class);

        String username = appPreferences.getUserData().getUsername();
        Log.d(TAG, "onCreateView: " + username);

        statisticViewModel.getStatistic(username).observe(StatisticFragment.this.requireActivity(), statistic -> {
            List<Values> valuesViews = statistic.getViews().getHistorical().getValues();
            LocalDate localDate;

            for (Values value : valuesViews) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    localDate = LocalDate.parse(value.getDate());
                    String moth = localDate.getMonth().toString();
                    int day = localDate.getDayOfMonth();
                    int year = localDate.getYear();
                    String date = moth + " " + day + ", " + year;
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}


