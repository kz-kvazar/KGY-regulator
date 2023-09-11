package com.add.vpn.fragments;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.R;
import com.add.vpn.adapters.ReportAdapter;
import com.add.vpn.roomDB.DatabaseManager;

import java.util.ArrayList;

public class ReportFragment extends Fragment {
    private RecyclerView reportView;
    private ReportAdapter adapter;
    private Spinner timePicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        reportView = rootView.findViewById(R.id.rv_statistics);
        timePicker = rootView.findViewById(R.id.time_period_picker);
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ReportAdapter(new ArrayList<>());
        reportView.setAdapter(adapter);
        reportView.setLayoutManager(new LinearLayoutManager(requireContext()));
        timePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                removeObservers();
                switch (position){
                   case 0: {
                       DatabaseManager.getInstance(requireContext()).reportDao().getCurrentDayLiveData().observe(getViewLifecycleOwner(), reportItems -> {
                           adapter.setReportItems(reportItems);
                           adapter.notifyDataSetChanged();
                       });
                       break;
                   }
                   case 1: {
                       DatabaseManager.getInstance(requireContext()).reportDao().getCurrentMonthLiveData().observe(getViewLifecycleOwner(), reportItems -> {
                           adapter.setReportItems(reportItems);
                           adapter.notifyDataSetChanged();
                   });
                       break;
                   }
                    case 2: {
                        DatabaseManager.getInstance(requireContext()).reportDao().getCurrentYearLiveData().observe(getViewLifecycleOwner(), reportItems -> {
                            adapter.setReportItems(reportItems);
                            adapter.notifyDataSetChanged();
                        });
                        break;
                    }
                   case 3: {
                       DatabaseManager.getInstance(requireContext()).reportDao().getAllLiveData().observe(getViewLifecycleOwner(), reportItems -> {
                           adapter.setReportItems(reportItems);
                           adapter.notifyDataSetChanged();
                       });
                       break;
                   }
                   default:
                       throw new IllegalStateException("Unexpected value: " + position);
               }
            }

            private void removeObservers() {
                DatabaseManager.getInstance(requireContext()).reportDao().getCurrentDayLiveData().removeObservers(getViewLifecycleOwner());
                DatabaseManager.getInstance(requireContext()).reportDao().getCurrentYearLiveData().removeObservers(getViewLifecycleOwner());
                DatabaseManager.getInstance(requireContext()).reportDao().getCurrentMonthLiveData().removeObservers(getViewLifecycleOwner());
                DatabaseManager.getInstance(requireContext()).reportDao().getAllLiveData().removeObservers(getViewLifecycleOwner());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Выполните действия, если ничего не выбрано
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.report_title);
        }
    }

    @Override
    public void onStop() {
        //ModelService.reportListLiveData.removeObservers(requireActivity());
        super.onStop();
    }
}