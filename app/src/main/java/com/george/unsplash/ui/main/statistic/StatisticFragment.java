package com.george.unsplash.ui.main.statistic;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.george.unsplash.databinding.StatisticFragmentBinding;
import com.george.unsplash.localdata.AppPreferences;
import com.george.unsplash.network.models.Statistic.Statistic;
import com.george.unsplash.network.models.Statistic.Values;
import com.george.unsplash.network.viewmodel.StatisticViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatisticFragment extends Fragment {

    private StatisticFragmentBinding binding;

    private StatisticViewModel statisticViewModel;
    private AppPreferences appPreferences;

    private AnyChartView viewsChart;
    private double views = 0.0;
    private double downloads = 0.0;

    public static final String TAG = Statistic.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = StatisticFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        appPreferences = new AppPreferences(StatisticFragment.this.requireActivity());
        statisticViewModel = new ViewModelProvider(this).get(StatisticViewModel.class);
        String username = appPreferences.getUserData().getUsername();

        viewsChart = binding.viewsChartView;

        getViewsStatistic(username);

        statisticViewModel
                .getStatistic(username)
                .observe(StatisticFragment.this.requireActivity(), statistic -> {
                    List<Values> valuesDownloads = statistic.getDownloads().getHistorical().getValues();
                    for(Values value: valuesDownloads) {
                        downloads += value.getValue();
                    }

                    int lastMothDownloads = (int) downloads;
                    String textDownloads = "Downloads: " + lastMothDownloads;
                    binding.downloadsTextView.setText(textDownloads);
                });

        return root;
    }

    private void getViewsStatistic(String username) {
        statisticViewModel
                .getStatistic(username)
                .observe(StatisticFragment.this.requireActivity(), statistic -> {
                    List<DataEntry> viewsData = new ArrayList<>();
                    Cartesian cartesian = initLineChart();

                    List<Values> valuesViews = statistic.getViews().getHistorical().getValues();
                    LocalDate localDate;

                    for (Values value : valuesViews) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            localDate = LocalDate.parse(value.getDate());
                            String moth = localDate.getMonth().toString();
                            int day = localDate.getDayOfMonth();
                            int year = localDate.getYear();
                            String date = moth + " " + day + ", " + year;
                            views += value.getValue();
                            viewsData.add(new StatisticDataEntry(date, value.getValue()));
                        }
                    }

                    setViewsText();
                    setViews(viewsData, cartesian);
                });
    }

    private void setViewsText() {
        int lastMothViews = (int) views;
        String textViews = "Total: " + lastMothViews;
        binding.viewsTextView.setText(textViews);
    }

    private void setViews(List<DataEntry> viewsData, Cartesian cartesian) {
        Set set = Set.instantiate();
        set.data(viewsData);
        Mapping mapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line line = cartesian.line(mapping);
        line.name("Views");
        line.hovered().markers().enabled(true);
        line.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        line.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        viewsChart.setChart(cartesian);
    }

    @NonNull
    private Cartesian initLineChart() {
        Cartesian cartesian = AnyChart.line();
        cartesian.animation(true);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        return cartesian;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}


