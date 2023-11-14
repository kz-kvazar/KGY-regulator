package com.add.vpn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.NumberPickerDialog;
import com.add.vpn.R;
import com.add.vpn.adapters.DataAdapter;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.modelService.ModelService;

import java.util.List;

public class ReportFragment extends Fragment {


    private DataAdapter dataAdapter;
    private RecyclerView dataList;
    private RealtimeDatabase realtimeDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        dataList = rootView.findViewById(R.id.dataListView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataAdapter = new DataAdapter(ModelService.dataListLiveData.getValue());
        dataList.setAdapter(dataAdapter);
        dataList.setLayoutManager(new LinearLayoutManager(requireActivity()));

        realtimeDatabase = ModelService.realtimeDatabase.getValue();
        if (realtimeDatabase == null) {
            realtimeDatabase = new RealtimeDatabase(requireActivity());
            ModelService.realtimeDatabase.setValue(realtimeDatabase);
        }
        //realtimeDatabase = new RealtimeDatabase(this.fragmentActivity);
        //realtimeDatabase.connect();

        dataAdapter.setOnItemClickListener(position -> {
            if (position == 4) {
                NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
                numberPickerDialog.setOnNumberSetListener(realtimeDatabase::setMaxPower);
                numberPickerDialog.show(requireActivity().getSupportFragmentManager(), "MaxPower");
            }
        });
        ModelService.dataListLiveData.observe(getViewLifecycleOwner(), strings ->
                dataAdapter.notifyItemRangeChanged(0,20));

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