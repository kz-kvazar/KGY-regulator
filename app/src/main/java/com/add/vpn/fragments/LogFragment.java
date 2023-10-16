package com.add.vpn.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.R;
import com.add.vpn.UtilCalculations;
import com.add.vpn.adapters.ChartAdapter;
import com.add.vpn.adapters.LogAdapter;
import com.add.vpn.firebase.FBreportItem;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.modelService.ModelService;
import com.add.vpn.view.ChartView;

import java.util.LinkedList;

public class LogFragment extends Fragment {
    private RecyclerView reportView;
    private LogAdapter logAdapter;

    private ChartView ch4View;
    private ChartView powerView;
    private Spinner timePicker;
    private ChartAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext();
        RealtimeDatabase realtimeDatabase = new RealtimeDatabase(context);
        realtimeDatabase.getReportList(24);
//        timePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                switch (position) {
//                    case 0:
//                        realtimeDatabase.getReportList(24);
//                        break;
//                    case 1:
//                        realtimeDatabase.getReportList(168);
//                        break;
//                    case 2:
//                        realtimeDatabase.getReportList(672);
//                        break;
//                    default:
//                        Toast.makeText(context, "Unexpected value:" + position, Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        ArrayList<ChartView> chartViews = new ArrayList<>();
//        ch4View = new ChartView(context);
//        powerView = new ChartView(context);

        ModelService.reportList.observe(requireActivity(), reportItems -> {
            LinkedList<Float> floats = new LinkedList<>();
            LinkedList<String> time = new LinkedList<>();
            LinkedList<Float> powers = new LinkedList<>();

            for (FBreportItem item : reportItems) {
                if (item == null) return;
                powers.add(Float.valueOf(item.getPowerActive()));
                Float ch4_1 = item.getCH4_1();
                Float ch4_2 = item.getCH4_2();
                floats.add(UtilCalculations.averageConcentration(ch4_1, ch4_2));
                String[] strings = item.getDate().split("-");
                String[] resultTime = strings[1].split(":");
                time.add(resultTime[0]);
            }
            ch4View.setData(floats, time);
            powerView.setData(powers, time);
        });

//        chartViews.add(ch4View);
//        chartViews.add(powerView);
//
//        adapter = new ChartAdapter(chartViews);
//        reportView.setAdapter(adapter);
//        reportView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_log, container, false);
        //logView = inflate.findViewById(R.id.logListView);
        ch4View = inflate.findViewById(R.id.cv_CH41);
        powerView = inflate.findViewById(R.id.cv_power);
        //timePicker = inflate.findViewById(R.id.time_period_picker);
        //reportView = inflate.findViewById(R.id.rv_chart);
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
        //ModelService.logListLiveData.removeObservers(requireActivity());
        super.onStop();
    }
}