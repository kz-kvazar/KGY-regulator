package com.add.vpn.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.add.vpn.AdManager;
import com.add.vpn.R;
import com.add.vpn.adapters.DataAdapter;
import com.add.vpn.holders.ContextHolder;
import com.add.vpn.model.AlarmSound;
import com.add.vpn.model.Model;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import org.jetbrains.annotations.NotNull;

public class DataFragment extends Fragment {

    private ListView dataList;
    private Button btnOnOff;
    private DataAdapter dataAdapter;
    private Model model;
    private boolean regulate;

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataAdapter = ContextHolder.getDataAdapter();
        dataList.setAdapter(dataAdapter);
        if (savedInstanceState != null) {
            regulate = savedInstanceState.getBoolean("isButtonPressed", false);
            if (regulate) {
                btnOnOff.setText(R.string.btn_regulateOff);
            } else {
                btnOnOff.setText(R.string.btn_regulateOn);
            }
        }
        model = ContextHolder.getModel();
        btnOnOff.setOnClickListener(act -> startRegulate());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_data_layout, container, false);
        dataList = rootView.findViewById(R.id.dataListView);
        btnOnOff = rootView.findViewById(R.id.on_off);

        return rootView;

    }

    public void setAdapter(DataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    private void startRegulate() {
        if (ContextHolder.getModel() != null) {
            regulate = false;
            ContextHolder.getModel().interrupt();
            ContextHolder.setModel(null);
            btnOnOff.setText(R.string.btn_regulateOn);

            AlarmSound alarmSound = ContextHolder.getAlarmSound();
            if (alarmSound != null) alarmSound.alarmStop();

            AlarmSound errorSound = ContextHolder.getErrorSound();
            if (errorSound != null) errorSound.alarmStop();

            Toast.makeText(getContext(), getString(R.string.regulate_statusOff), Toast.LENGTH_LONG).show();

        } else {
            regulate = true;
            model = new Model(ContextHolder.getLogList());
            model.start();
            ContextHolder.setModel(model);
            btnOnOff.setText(R.string.btn_regulateOff);
            Toast.makeText(getContext(), getString(R.string.regulate_statusOn), Toast.LENGTH_LONG).show();

            AdManager.showInterstitialAd();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = ContextHolder.getActivity();
        if (activity != null) {
            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NotNull InitializationStatus initializationStatus) {
                    AdManager.loadBannerAd();
                    AdManager.loadInterstitialAd();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> btnOnOff.setEnabled(true), 3000);
                }
            });
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isButtonPressed", regulate);
    }

    @Override
    public void onStop() {
        if (ContextHolder.getModel() != null) {
            regulate = false;
            ContextHolder.getModel().interrupt();
            ContextHolder.setModel(null);
            btnOnOff.setText(R.string.btn_regulateOn);

            Toast.makeText(getContext(), getString(R.string.regulate_statusOff), Toast.LENGTH_LONG).show();

            AlarmSound alarmSound = ContextHolder.getAlarmSound();
            if (alarmSound != null) alarmSound.alarmStop();

            AlarmSound errorSound = ContextHolder.getErrorSound();
            if (errorSound != null) errorSound.alarmStop();
        }
        super.onStop();
    }
}