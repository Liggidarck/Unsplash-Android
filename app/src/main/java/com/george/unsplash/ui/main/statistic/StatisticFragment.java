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
import com.george.unsplash.localdata.preferences.user.UserDataViewModel;
import com.george.unsplash.network.models.Statistic.Statistic;
import com.george.unsplash.network.models.Statistic.Values;
import com.george.unsplash.network.viewmodel.StatisticViewModel;
import com.george.unsplash.utils.DialogUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatisticFragment extends Fragment {

    private StatisticFragmentBinding binding;

    private StatisticViewModel statisticViewModel;
    private UserDataViewModel userDataViewModel;

    private final DialogUtils dialogUtils = new DialogUtils();

    private AnyChartView chart;
    private double views = 0.0;
    private double downloads = 0.0;

    public static final String TAG = Statistic.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = StatisticFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);
        statisticViewModel = new ViewModelProvider(this).get(StatisticViewModel.class);
        String username = userDataViewModel.getMe().getUsername();

        chart = binding.viewsChartView;

        getViewsStatistic(username);

        return root;
    }

    private void getViewsStatistic(String username) {
        statisticViewModel
                .getStatistic(username)
                .observe(StatisticFragment.this.requireActivity(), statistic -> {
                    if (statistic == null) {
                        dialogUtils.showAlertDialog(StatisticFragment.this.requireActivity());
                        binding.progressBarStats.setVisibility(View.INVISIBLE);
                        return;
                    }

                    List<DataEntry> viewsData = new ArrayList<>();
                    Cartesian cartesian = initLineChart();

                    List<Values> valuesViews = statistic.getViews().getHistorical().getValues();
                    List<Values> valuesDownloads = statistic.getDownloads().getHistorical().getValues();

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

                    for (Values value : valuesDownloads) {
                        downloads += value.getValue();
                    }

                    int lastMothDownloads = (int) downloads;
                    String textDownloads = "Downloads: " + lastMothDownloads;
                    binding.downloadsTextView.setText(textDownloads);

                    setViewsText();
                    setData(viewsData, cartesian);
                    binding.progressBarStats.setVisibility(View.INVISIBLE);
                });
    }

    private void setViewsText() {
        int lastMothViews = (int) views;
        String textViews = "Total: " + lastMothViews;
        binding.viewsTextView.setText(textViews);
    }

    private void setData(List<DataEntry> viewsData, Cartesian cartesian) {
        Set set = Set.instantiate();
        set.data(viewsData);

        Mapping mappingViews = set.mapAs("{ x: 'x', value: 'value' }");

        Line views = cartesian.line(mappingViews);
        views.name("Views");
        views.hovered().markers().enabled(true);
        views.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        views.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        chart.setChart(cartesian);
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


