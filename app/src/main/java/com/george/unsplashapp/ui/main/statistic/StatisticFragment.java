package com.george.unsplashapp.ui.main.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.george.unsplashapp.databinding.StatisticFragmentBinding;

public class StatisticFragment extends Fragment {

    StatisticFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = StatisticFragmentBinding.inflate(inflater, container,false);
        View root = binding.getRoot();

        return root;
    }
}
