package com.add.vpn.fragments;

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
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.AdManager;
import com.add.vpn.NumberPickerDialog;
import com.add.vpn.R;
import com.add.vpn.adapters.DataAdapter;

import com.add.vpn.holders.DataHolder;
import com.add.vpn.holders.DataViewModel;
import com.add.vpn.model.Model;
import com.google.android.gms.ads.MobileAds;

public class DataFragment extends Fragment {

    private RecyclerView dataList;
    private Button btnOnOff;
    private DataAdapter dataAdapter;
    private Model model;
    private boolean regulate;
    private DataViewModel dataViewModel;
    private AdManager adManager;
    private Button btnSoundOff;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        dataAdapter = new DataAdapter(DataHolder.toLis());

        dataAdapter.setOnItemClickListener(position -> {
            if(DataHolder.toLis().get(position).contains("Макс")){
                NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
                numberPickerDialog.setOnNumberSetListener(DataHolder::setMaxPower);
                numberPickerDialog.show(requireActivity().getSupportFragmentManager(), "MaxPower");
            }
        });
        dataList.setAdapter(dataAdapter);
        dataList.setLayoutManager(new LinearLayoutManager(requireContext()));

        dataViewModel.getDataListLiveData().observe(getViewLifecycleOwner(), strings -> dataAdapter.notifyItemRangeChanged(0,10));

        MobileAds.initialize(requireActivity(), initializationStatus -> {
            adManager = new AdManager(getActivity());
            adManager.loadBannerAd();
            adManager.loadInterstitialAd();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> btnOnOff.setEnabled(true), 3000);
        });

        if (savedInstanceState != null) {
            regulate = savedInstanceState.getBoolean("isButtonPressed", false);
            if (regulate) {
                btnOnOff.setText(R.string.btn_regulateOff);
            } else {
                btnOnOff.setText(R.string.btn_regulateOn);
            }
        }
        btnOnOff.setOnClickListener(v -> startRegulate());

        btnSoundOff.setOnClickListener(v -> {
            dataViewModel.stopErrorSound();
            dataViewModel.stopAlarmSound();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_data, container, false);
        dataList = rootView.findViewById(R.id.dataListView);
        btnOnOff = rootView.findViewById(R.id.on_off);
        btnSoundOff = rootView.findViewById(R.id.soundOff);
        return rootView;
    }

    private void startRegulate() {
        if (model != null) {
            regulate = false;
            model.interrupt();
            model = null;
            btnOnOff.setText(R.string.btn_regulateOn);

            dataViewModel.stopErrorSound();
            dataViewModel.stopAlarmSound();

            Toast.makeText(getContext(), getString(R.string.regulate_statusOff), Toast.LENGTH_LONG).show();

        } else {
            regulate = true;
            model = new Model(dataViewModel);
            model.start();
            btnOnOff.setText(R.string.btn_regulateOff);
            Toast.makeText(getContext(), getString(R.string.regulate_statusOn), Toast.LENGTH_LONG).show();
            adManager.showInterstitialAd();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isButtonPressed", regulate);
    }

    @Override
    public void onStop() {
        if (model != null) {

            regulate = false; // instance is saved right, but here is hardcoding!!!
            model.interrupt();
            btnOnOff.setText(R.string.btn_regulateOn);

            Toast.makeText(getContext(), getString(R.string.regulate_statusOff), Toast.LENGTH_LONG).show();

            dataViewModel.stopErrorSound();
            dataViewModel.stopAlarmSound();

        }
        super.onStop();
    }
}