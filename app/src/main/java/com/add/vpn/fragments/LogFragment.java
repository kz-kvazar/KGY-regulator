package com.add.vpn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.add.vpn.R;
import com.add.vpn.holders.ContextHolder;

public class LogFragment extends Fragment {
    private ListView logList;
    private ListAdapter adapter;

    public LogFragment() {
        // Required empty public constructor
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = ContextHolder.getLogAdapter();
        logList.setAdapter(adapter);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_log_layout, container, false);
        logList = inflate.findViewById(R.id.logListView);
        return inflate;
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
    }
}