package com.add.vpn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.R;
import com.add.vpn.adapters.LogAdapter;
import com.add.vpn.holders.DataViewModel;

import java.util.Objects;

public class LogFragment extends Fragment {
    private RecyclerView logView;
    private LogAdapter logAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DataViewModel model = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        logAdapter = new LogAdapter(model.getLogListLiveData().getValue());
        logView.setAdapter(logAdapter);
        logView.setLayoutManager(new LinearLayoutManager(requireContext()));

        model.getLogListLiveData().observe(getViewLifecycleOwner(), strings -> logAdapter.notifyItemInserted(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_log, container, false);
        logView = inflate.findViewById(R.id.logListView);
        return inflate;
    }
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.log_title);
        }
    }

}