package com.add.vpn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.R;
import com.add.vpn.adapters.LogAdapter;
import com.add.vpn.modelService.ModelService;

public class LogFragment extends Fragment {
    private RecyclerView logView;
    private LogAdapter logAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logAdapter = new LogAdapter(ModelService.logListLiveData.getValue());

        logView.setAdapter(logAdapter);
        logView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ModelService.logListLiveData.observe(getViewLifecycleOwner(), strings -> logAdapter.notifyItemInserted(0));
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

    @Override
    public void onStop() {
        ModelService.logListLiveData.removeObservers(requireActivity());
        super.onStop();
    }
}