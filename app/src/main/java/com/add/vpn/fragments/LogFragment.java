package com.add.vpn.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.add.vpn.R;
import com.add.vpn.UtilCalculations;
import com.add.vpn.firebase.FBreportItem;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.modelService.ModelService;
import com.add.vpn.view.ChartView;

import java.util.LinkedList;

public class LogFragment extends Fragment {
    //private RecyclerView reportView;
    // private LogAdapter logAdapter;

    private ChartView ch4View;
    private ChartView powerView;
    private ChartView cleanOil;
    private ChartView resTemp;
    private ChartView ch4VNS;
    private Spinner timePicker;
    private ChartView gasFlow;
    private RealtimeDatabase realtimeDatabase;
    //private RecyclerView logList;
    //private ChartAdapter adapter;
    // private ChartAdapter adapter;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realtimeDatabase.disconnect();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext();

//
//        ArrayList<ChartView> chartViews = new ArrayList<>();
//        ch4View = new ChartView(context);
//        ch4View.setTimeUnit("day");
//        powerView = new ChartView(context);
//
//        chartViews.add(ch4View);
//        chartViews.add(powerView);
//
//        adapter = new ChartAdapter(chartViews);
//        logList.setAdapter(adapter);
//        logList.setLayoutManager(new LinearLayoutManager(context));

        realtimeDatabase = ModelService.realtimeDatabase.getValue();
        if (realtimeDatabase == null) {
            realtimeDatabase = new RealtimeDatabase(context);
            ModelService.realtimeDatabase.setValue(realtimeDatabase);
        }
        //realtimeDatabase.getAvgTemp(300);
        //avgTemp();

        timePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        realtimeDatabase.getReportList(27);
                        report(false);
                        break;
                    case 1:
                        realtimeDatabase.getReportList(168);
                        report(true);
                        break;
                    case 2:
                        realtimeDatabase.getReportList(744);
                        report(true);
                        break;
                    case 3:
                        realtimeDatabase.getReportList(744 * 3);
                        report(true);
                        break;
                    default:
                        Toast.makeText(context, "Unexpected value:" + position, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
//    private void avgTemp(){
//        ModelService.avgTemp.removeObservers(requireActivity());
//        ModelService.avgTemp.observe(requireActivity(),temp ->{
//            LinkedList<String> time = new LinkedList<>();
//            for (int i = 0; i < temp.size(); i++) {
//                if(i == 0){
//                    time.addFirst("0");
//                }else{
//                    time.addFirst(String.valueOf(i));
//                }
//            }
//            avgTemp.setData(temp,time);
//        });
//    }

    private void report(boolean isDayDeport) {
        ModelService.reportList.removeObservers(requireActivity());
        ModelService.reportList.observe(requireActivity(), reportItems -> {
            LinkedList<Float> floats = new LinkedList<>();
            LinkedList<String> time = new LinkedList<>();
            LinkedList<Float> ch4vns = new LinkedList<>();
            LinkedList<Float> powerGenerated = new LinkedList<>();
            LinkedList<Float> res = new LinkedList<>();
            LinkedList<Float> oil = new LinkedList<>();
            LinkedList<Float> gas = new LinkedList<>();

            Float beforeGeneratedStarts = 0f;

            for (int i = 0; i < reportItems.size() - 1; i++) {
                FBreportItem item = reportItems.get(i);
                if (beforeGeneratedStarts < 0 || i == 0){
                    beforeGeneratedStarts = item.getGenerated().floatValue();
                } else {
                    if (item == null) return;
                    powerGenerated.add( (item.getGenerated() - beforeGeneratedStarts));
                    beforeGeneratedStarts = item.getGenerated().floatValue();
                    //powers.add(Float.valueOf(item.getPowerActive()));
                    Float ch4_1 = item.getCH4_1();
                    Float ch4_2 = item.getCH4_2();
                    ch4vns.add(UtilCalculations.averageConcentration(ch4_1, ch4_2));
                    floats.add(item.getCH4_KGY());
                    gas.add(item.getGasFlow());
                    try {
                        //avg.add(Float.valueOf(item.getAvgTemp()));
                        oil.add(Float.valueOf(item.getCleanOil()));
                        res.add(item.getResTemp());
                    } catch (Exception e) {
                        //avg.add(390f);
                        oil.add(71f);
                        res.add(49.6f);
                    }

                    if (!isDayDeport) {
                        String[] strings = item.getDate().split("-");
                        String[] resultTime = strings[1].split(":");
                        time.add(resultTime[0]);
                        ch4View.setTimeUnit(getString(R.string.Hour));
                        powerView.setTimeUnit(getString(R.string.Hour));
                        resTemp.setTimeUnit(getString(R.string.Hour));
                        cleanOil.setTimeUnit(getString(R.string.Hour));
                        gasFlow.setTimeUnit(getString(R.string.Hour));
                        ch4VNS.setTimeUnit(getString(R.string.Hour));
                    } else {
                        String[] strings = item.getDate().split("-");
                        String[] resultTime = strings[0].split("\\.");
                        time.add(resultTime[2]);
                        ch4View.setTimeUnit(getString(R.string.Day));
                        powerView.setTimeUnit(getString(R.string.Day));
                        resTemp.setTimeUnit(getString(R.string.Day));
                        cleanOil.setTimeUnit(getString(R.string.Day));
                        gasFlow.setTimeUnit(getString(R.string.Day));
                        ch4VNS.setTimeUnit(getString(R.string.Day));
                    }
                }

            }
            ch4View.setData(floats, time);
            powerView.setData(powerGenerated, time);
            resTemp.setData(res, time);
            ch4VNS.setData(ch4vns,time);
            cleanOil.setData(oil, time);
            gasFlow.setData(gas, time);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_log, container, false);
        ch4View = inflate.findViewById(R.id.cv_CH41);
        powerView = inflate.findViewById(R.id.cv_power);
        resTemp = inflate.findViewById(R.id.cv_resTemp);
        ch4VNS = inflate.findViewById(R.id.cv_CH4_VNS);
        cleanOil = inflate.findViewById(R.id.cv_cleanOil);
        gasFlow = inflate.findViewById(R.id.cv_gasFlow);

        timePicker = inflate.findViewById(R.id.time_period_picker);
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