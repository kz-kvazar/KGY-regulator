package com.add.vpn.fragments;

import android.os.Bundle;
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
import com.add.vpn.modelService.ModelService;

public class ReportFragment extends Fragment {
    private RecyclerView reportView;
    private ReportAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_report, container, false);
        reportView = inflate.findViewById(R.id.gv_statistics);
        return inflate;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ReportAdapter(ModelService.reportListLiveData.getValue());
        reportView.setAdapter(adapter);

        reportView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ModelService.reportListLiveData.observe(getViewLifecycleOwner(), reportItems -> adapter.notifyItemInserted(0));
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