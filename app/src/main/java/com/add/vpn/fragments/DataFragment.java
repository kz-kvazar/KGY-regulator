package com.add.vpn.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.AdManager;
import com.add.vpn.NumberPickerDialog;
import com.add.vpn.R;
import com.add.vpn.adapters.DataAdapter;

import com.add.vpn.holders.DataHolder;
import com.add.vpn.model.AlarmSound;
import com.add.vpn.modelService.ModelService;
import com.google.android.gms.ads.MobileAds;

public class DataFragment extends Fragment {

    private RecyclerView dataList;
    private Button btnOnOff;
    private DataAdapter dataAdapter;
    private boolean regulate;
    private AdManager adManager;
    private Button btnSoundOff;
    private FragmentActivity fragmentActivity;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentActivity = requireActivity();

        dataAdapter = new DataAdapter(DataHolder.toLis(fragmentActivity.getApplicationContext()));

        dataAdapter.setOnItemClickListener(position -> {
            if(DataHolder.toLis(fragmentActivity.getApplicationContext()).get(position).contains("Макс")){
                NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
                numberPickerDialog.setOnNumberSetListener(DataHolder::setMaxPower);
                numberPickerDialog.show(fragmentActivity.getSupportFragmentManager(), "MaxPower");
            }
        });

        dataList.setAdapter(dataAdapter);
        dataList.setLayoutManager(new LinearLayoutManager(fragmentActivity));

        ModelService.running.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){
                btnOnOff.setText(R.string.btn_regulateOff);
            }else btnOnOff.setText(R.string.btn_regulateOn);
            regulate = aBoolean;
        });

        ModelService.dataListLiveData.observe(getViewLifecycleOwner(), strings -> dataAdapter.notifyItemRangeChanged(0,10));

        MobileAds.initialize(fragmentActivity, initializationStatus -> {
            adManager = new AdManager(fragmentActivity);
            adManager.loadBannerAd();
            adManager.loadInterstitialAd();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> btnOnOff.setEnabled(true), 3000);
        });

        btnOnOff.setOnClickListener(v -> startRegulate());

        btnSoundOff.setOnClickListener(v -> {
            AlarmSound alarmSound = ModelService.alarmSound;
            if (alarmSound != null) alarmSound.alarmStop();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) fragmentActivity;
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_data, container, false);
        dataList = rootView.findViewById(R.id.dataListView);
        btnOnOff = rootView.findViewById(R.id.on_off);
        btnSoundOff = rootView.findViewById(R.id.soundOff);
        return rootView;
    }

    private void startRegulate() {
        if (regulate) {
            regulate = false;
            serviceIntent("STOP");
            Toast.makeText(fragmentActivity, getString(R.string.regulate_statusOff), Toast.LENGTH_LONG).show();

        } else {
            regulate = true;
            serviceIntent("START");
            Toast.makeText(fragmentActivity, getString(R.string.regulate_statusOn), Toast.LENGTH_LONG).show();
            adManager.showInterstitialAd();
        }
    }

    private void serviceIntent(String action){
        Intent intent = new Intent(requireContext(), ModelService.class);
        intent.setAction(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(requireContext(), intent);
        } else {
            fragmentActivity.startService(intent);
        }
    }

    @Override
    public void onStop() {
        adManager.release();
        ModelService.dataListLiveData.removeObservers(fragmentActivity);
        ModelService.running.removeObservers(fragmentActivity);
        super.onStop();
    }
}